//
// Created by zqq23 on 2024/5/1.
//

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <dirent.h>
#include "Native.c"
#include "../jvm/jni.h"
#include "../jvm/jvmti.h"

#pragma clang diagnostic push
#pragma ide diagnostic ignored "UnusedParameter"
JavaVM *jvm;
JNIEnv *jniEnv;
jvmtiEnv *jvmti;

struct Callback
{
    const unsigned char *array;
    jint length;
    int success;
};

struct TransformCallback
{
    jclass clazz;
    struct Callback *callback;
    struct TransformCallback *next;
};

static struct TransformCallback *callback_list = NULL;

jclass findThreadClass(const char *name, jobject classLoader)
{
    return (*jniEnv)->FindClass(jniEnv, name);
}

unsigned char *jbyteArrayToUnsignedCharArray(JNIEnv *env, jbyteArray byteArray)
{
    jsize length = (*env)->GetArrayLength(env, byteArray);
    jbyte *elements = (*env)->GetByteArrayElements(env, byteArray, NULL);

    unsigned char *unsignedCharArray = (unsigned char *)malloc(length * sizeof(unsigned char));
    if (unsignedCharArray != NULL)
    {
        for (int i = 0; i < length; i++)
        {
            unsignedCharArray[i] = (unsigned char)elements[i];
        }
    }

    (*env)->ReleaseByteArrayElements(env, byteArray, elements, 0);

    return unsignedCharArray;
}

jbyteArray unsignedCharArrayToJByteArray(JNIEnv *env, const unsigned char *unsignedCharArray, jsize length)
{
    jbyteArray byteArray = (*env)->NewByteArray(env, length);

    if (byteArray != NULL)
    {
        jbyte *elements = (*env)->GetByteArrayElements(env, byteArray, NULL);

        for (int i = 0; i < length; i++)
        {
            elements[i] = (jbyte)unsignedCharArray[i];
        }

        (*env)->ReleaseByteArrayElements(env, byteArray, elements, 0);
    }

    return byteArray;
}

#pragma clang diagnostic push
#pragma ide diagnostic ignored "UnusedParameter"

void JNICALL
classFileLoadHook(jvmtiEnv
                      *jvmti_env,
                  JNIEnv *env,
                  jclass
                      class_being_redefined,
                  jobject loader,
                  const char *name, jobject protection_domain,
                  jint class_data_len,
                  const unsigned char *class_data,
                  jint
                      *new_class_data_len,
                  unsigned char **new_class_data)
{
    *new_class_data = NULL;

    if (class_being_redefined)
    {
        struct TransformCallback *current = callback_list;
        struct TransformCallback *previous = NULL;

        while (current != NULL)
        {
            if (!(*env)->IsSameObject(env, current->clazz, class_being_redefined))
            {
                previous = current;
                current = current->next;
                continue;
            }

            if (previous == NULL)
            {
                callback_list = current->next;
            }
            else
            {
                previous->next = current->next;
            }

            current->callback->array = class_data;
            current->callback->length = class_data_len;
            current->callback->success = 1;

            free(current);
            break;
        }
    }
}

#pragma clang diagnostic pop

void *allocate(jlong size)
{
    void *resultBuffer = malloc(size);
    return resultBuffer;
}

JNIEXPORT jclass JNICALL FindClass(JNIEnv *env, jclass _, jstring name)
{
    return (*env)->FindClass(env, jstringToChar(env, name));
}

JNIEXPORT jbyteArray

    JNICALL
    GetClassBytes(JNIEnv *env, jclass _, jclass clazz)
{
    struct Callback *retransform_callback = (struct Callback *)allocate(sizeof(struct Callback));
    retransform_callback->success = 0;

    struct TransformCallback *new_node = (struct TransformCallback *)allocate(sizeof(struct TransformCallback));
    new_node->clazz = clazz;
    new_node->callback = retransform_callback;
    new_node->next = callback_list;
    callback_list = new_node;

    jclass *classes = (jclass *)
        allocate(sizeof(jclass));
    classes[0] = clazz;

    jint err = (*jvmti)->RetransformClasses((jvmtiEnv *)jvmti, 1, classes);

    if (err > 0)
    {
        printf("jvmti error while getting class bytes: %ld\n", err);
        return NULL;
    }

    jbyteArray output = (*env)->NewByteArray(env, retransform_callback->length);
    (*env)->SetByteArrayRegion(env, output, 0, retransform_callback->length, (jbyte *)retransform_callback->array);

    free(classes);
    return output;
}

JNIEXPORT jint

    JNICALL
    RedefineClass(JNIEnv *env, jclass _, jclass clazz, jbyteArray classBytes)
{
    jbyte *classByteArray = (*env)->GetByteArrayElements(env, classBytes, NULL);
    struct Callback *retransform_callback = (struct Callback *)allocate(sizeof(struct Callback));
    retransform_callback->success = 0;
    struct TransformCallback *new_node = (struct TransformCallback *)allocate(sizeof(struct TransformCallback));
    new_node->clazz = clazz;
    new_node->callback = retransform_callback;
    new_node->next = callback_list;
    callback_list = new_node;
    jvmtiClassDefinition *definitions = (jvmtiClassDefinition *)allocate(sizeof(jvmtiClassDefinition));
    definitions->klass = clazz;
    definitions->class_byte_count = (*env)->GetArrayLength(env, classBytes);
    definitions->class_bytes = (unsigned char *)classByteArray;
    jint error = (jint)(*jvmti)->RedefineClasses((jvmtiEnv *)jvmti, 1, definitions);
    (*env)->ReleaseByteArrayElements(env, classBytes, classByteArray, 0);
    free(definitions);
    return error;
}

jclass DefineClass(JNIEnv *env, jobject obj, jobject classLoader, jbyteArray bytes)
{
    jclass clClass = (*env)->FindClass(env, "java/lang/ClassLoader");
    jmethodID defineClass = (*env)->GetMethodID(env, clClass, "defineClass", "([BII)Ljava/lang/Class;");
    jobject classDefined = (*env)->CallObjectMethod(env, classLoader, defineClass, bytes, 0,
                                                    (*env)->GetArrayLength(env, bytes));
    return (jclass)
        classDefined;
}

void loadJar(const char *path, jobject thread)
{
    jclass urlClassLoader = (*jniEnv)->FindClass(jniEnv, "java/net/URLClassLoader");
    jclass fileClass = (*jniEnv)->FindClass(jniEnv, "java/io/File");
    jmethodID init = (*jniEnv)->GetMethodID(jniEnv, fileClass, "<init>", "(Ljava/lang/String;)V");
    jmethodID addURL = (*jniEnv)->GetMethodID(jniEnv, urlClassLoader, "addURL", "(Ljava/net/URL;)V");
    jstring filePath = (*jniEnv)->NewStringUTF(jniEnv, path);
    jobject file = (*jniEnv)->NewObject(jniEnv, fileClass, init, filePath);
    jmethodID toURI = (*jniEnv)->GetMethodID(jniEnv, fileClass, "toURI", "()Ljava/net/URI;");
    jobject uri = (*jniEnv)->CallObjectMethod(jniEnv, file, toURI);
    jclass URIClass = (*jniEnv)->FindClass(jniEnv, "java/net/URI");
    jmethodID toURL = (*jniEnv)->GetMethodID(jniEnv, URIClass, "toURL", "()Ljava/net/URL;");
    jobject url = (*jniEnv)->CallObjectMethod(jniEnv, uri, toURL);
    (*jniEnv)->CallVoidMethod(jniEnv, (*jniEnv)->CallObjectMethod(jniEnv, thread, (*jniEnv)->GetMethodID(jniEnv, (*jniEnv)->GetObjectClass(jniEnv, thread), "getContextClassLoader", "()Ljava/lang/ClassLoader;")),
                              addURL, url);
}

int str_endwith(const char *str, const char *reg)
{
    int l1 = strlen(str), l2 = strlen(reg);
    if (l1 < l2)
        return 0;
    str += l1 - l2;
    while (*str && *reg && *str == *reg)
    {
        str++;
        reg++;
    }
    if (!*str && !*reg)
        return 1;
    return 0;
}

void Inject(const char yolbi_dir[260])
{
    jclass threadClass = (*jniEnv)->FindClass(jniEnv, "java/lang/Thread");
    jmethodID getAllStackTraces = (*jniEnv)->GetStaticMethodID(jniEnv, threadClass, "getAllStackTraces",
                                                               "()Ljava/util/Map;");
    if (!getAllStackTraces)
        return;
    jobjectArray threads = (jobjectArray)(*jniEnv)->CallObjectMethod(jniEnv, (*jniEnv)->CallObjectMethod(jniEnv, (*jniEnv)->CallStaticObjectMethod(jniEnv, threadClass, getAllStackTraces), (*jniEnv)->GetMethodID(jniEnv, (*jniEnv)->FindClass(jniEnv, "java/util/Map"), "keySet", "()Ljava/util/Set;")),
                                                                     (*jniEnv)->GetMethodID(jniEnv,
                                                                                            (*jniEnv)->FindClass(
                                                                                                jniEnv,
                                                                                                "java/util/Set"),
                                                                                            "toArray",
                                                                                            "()[Ljava/lang/Object;"));
    if (!threads)
        return;
    jsize arrlength = (*jniEnv)->GetArrayLength(jniEnv, threads);
    jobject clientThread = NULL;
    for (int i = 0; i < arrlength; i++)
    {
        jobject thread = (*jniEnv)->GetObjectArrayElement(jniEnv, threads, i);
        if (thread == NULL)
            continue;
        threadClass = (*jniEnv)->GetObjectClass(jniEnv, thread);
        jstring name = (*jniEnv)->CallObjectMethod(jniEnv, thread,
                                                   (*jniEnv)->GetMethodID(jniEnv, threadClass, "getName",
                                                                          "()Ljava/lang/String;"));
        const char *str = (*jniEnv)->GetStringUTFChars(jniEnv, name, 0);
        if (!strcmp(str, "Client thread"))
        {
            clientThread = thread;
            (*jniEnv)->ReleaseStringUTFChars(jniEnv, name, str);
            break;
        }
        (*jniEnv)->ReleaseStringUTFChars(jniEnv, name, str);
    }
    if (!clientThread)
        return;

    jobject classLoader = (*jniEnv)->CallObjectMethod(jniEnv, clientThread, (*jniEnv)->GetMethodID(jniEnv, (*jniEnv)->GetObjectClass(jniEnv, clientThread), "getContextClassLoader", "()Ljava/lang/ClassLoader;"));
    if (!classLoader)
        return;
    else
        printf("classLoader found\n");

    jclass Thread = (*jniEnv)->FindClass(jniEnv, "java/lang/Thread");
    jmethodID setContextClassLoader = (*jniEnv)->GetMethodID(jniEnv, Thread, "setContextClassLoader", "(Ljava/lang/ClassLoader;)V");
    jobject currentThread = (*jniEnv)->CallStaticObjectMethod(jniEnv, Thread, (*jniEnv)->GetStaticMethodID(jniEnv, Thread, "currentThread", "()Ljava/lang/Thread;"));
    (*jniEnv)->CallVoidMethod(jniEnv, currentThread, setContextClassLoader, classLoader);
    printf("currentThread contextClassLoader set\n");

    DIR *dir = opendir(yolbi_dir);
    struct dirent *entry;
    while ((entry = readdir(dir)) != NULL)
    {
        if (str_endwith(entry->d_name, ".jar") && strcmp(entry->d_name, "injection.jar") != 0)
        {
            char jarPath[260];
            sprintf_s(jarPath, 260, "%s\\%s", yolbi_dir, entry->d_name);
            (*jvmti)->AddToSystemClassLoaderSearch(jvmti, jarPath);
            // loadJar(jarPath, clientThread);
            printf("loaded: %s\n", jarPath);
        }
    }
    closedir(dir);

    char injectionOutPath[260];
    sprintf_s(injectionOutPath, 260, "%s\\injection.jar", yolbi_dir);

    jvmtiCapabilities capabilities = {0};
    memset(&capabilities, 0, sizeof(jvmtiCapabilities));

    capabilities.can_get_bytecodes = 1;
    capabilities.can_redefine_classes = 1;
    capabilities.can_redefine_any_class = 1;
    capabilities.can_generate_all_class_hook_events = 1;
    capabilities.can_retransform_classes = 1;
    capabilities.can_retransform_any_class = 1;

    (*jvmti)->AddCapabilities((jvmtiEnv *)jvmti, &capabilities);

    jvmtiEventCallbacks callbacks = {0};
    memset(&callbacks, 0, sizeof(jvmtiEventCallbacks));

    callbacks.ClassFileLoadHook = &classFileLoadHook;

    (*jvmti)->SetEventCallbacks((jvmtiEnv *)jvmti, &callbacks, sizeof(jvmtiEventCallbacks));
    (*jvmti)->SetEventNotificationMode((jvmtiEnv *)jvmti, JVMTI_ENABLE, JVMTI_EVENT_CLASS_FILE_LOAD_HOOK, NULL);

    jclass wrapperClass = findThreadClass("cn/yapeteam/loader/NativeWrapper", classLoader);
    if (!wrapperClass)
    {
        printf("Failed to find NativeWrapper class\n");
        return;
    }
    printf("NativeWrapper class found\n");
    JNINativeMethod methods[] = {
        {"getClassBytes", "(Ljava/lang/Class;)[B", (void *)&GetClassBytes},
        {"redefineClass", "(Ljava/lang/Class;[B)I", (void *)&RedefineClass},
        {"defineClass", "(Ljava/lang/ClassLoader;[B)Ljava/lang/Class;", (void *)&DefineClass},
        {"FindClass", "(Ljava/lang/String;)Ljava/lang/Class;", (void *)&FindClass},
    };
    (*jniEnv)->RegisterNatives(jniEnv, wrapperClass, methods, 4);
    jclass natvieClass = findThreadClass("cn/yapeteam/loader/Natives", classLoader);
    register_native_methods(jniEnv, natvieClass);
    printf("Native methods registered\n");

    jclass PreLoad = findThreadClass("cn/yapeteam/loader/Loader", classLoader);
    if (!PreLoad)
    {
        printf("Failed to find Loader class\n");
        return;
    }

    jmethodID preload = (*jniEnv)->GetStaticMethodID(jniEnv, PreLoad, "preload", "()V");
    (*jniEnv)->CallStaticVoidMethod(jniEnv, PreLoad, preload);
    printf("Preload method called\n");

    (*jvmti)->AddToSystemClassLoaderSearch(jvmti, injectionOutPath);
    // loadJar(injectionOutPath, clientThread);
    printf("Injection jar loaded\n");

    jclass Start = findThreadClass("cn/yapeteam/yolbi/Loader", classLoader);
    if (!Start)
    {
        printf("Failed to find Loader class\n");
        return;
    }
    jmethodID start = (*jniEnv)->GetStaticMethodID(jniEnv, Start, "start", "()V");
    (*jniEnv)->CallStaticVoidMethod(jniEnv, Start, start);
    printf("Start method called\n");
}

#include <windows.h>

void entry()
{
    HMODULE jvmHandle = GetModuleHandle("jvm.dll");
    if (!jvmHandle)
        return;
    typedef jint(JNICALL * fnJNI_GetCreatedJavaVMs)(JavaVM **, jsize, jsize *);
    fnJNI_GetCreatedJavaVMs JNI_GetCreatedJavaVMs = (fnJNI_GetCreatedJavaVMs)GetProcAddress(jvmHandle,
                                                                                            "JNI_GetCreatedJavaVMs");
    if (!JNI_GetCreatedJavaVMs)
        return;
    if (JNI_GetCreatedJavaVMs(&jvm, 1, NULL) != JNI_OK ||
        (*jvm)->AttachCurrentThread(jvm, (void **)&jniEnv, NULL) != JNI_OK)
        return;
    (*jvm)->GetEnv(jvm, (void **)&jvmti, JVMTI_VERSION);
    if (!jvmti)
        return;
    char userProfile[MAX_PATH];
    GetEnvironmentVariableA("USERPROFILE", userProfile, MAX_PATH);
    char yolbiPath[MAX_PATH];
    sprintf_s(yolbiPath, MAX_PATH, "%s\\.yolbi", userProfile);
    Inject(yolbiPath);
}

#pragma clang diagnostic pop

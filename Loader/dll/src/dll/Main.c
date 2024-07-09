//
// Created by zqq23 on 2024/5/1.
//

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <dirent.h>
#include "Native.c"
#include "utils.h"
#include "unzip.h"
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

void replace(char *str, const char *fstr, const char *rstr)
{
    int i, j, k;
    int len_str = strlen(str);
    int len_fstr = strlen(fstr);
    int len_rstr = strlen(rstr);

    for (i = 0; i <= len_str - len_fstr; i++)
    {
        for (j = 0; j < len_fstr; j++)
        {
            if (str[i + j] != fstr[j])
                break;
        }
        if (j == len_fstr)
        {
            memmove(str + i + len_rstr, str + i + len_fstr, len_str - i - len_fstr + 1);
            memcpy(str + i, rstr, len_rstr);
            i += len_rstr - 1;
            len_str = len_str - len_fstr + len_rstr;
        }
    }
}

jclass JNICALL loadClass(JNIEnv *jniEnv, const char *name, jobject classloader)
{
    jmethodID loadClass = (*jniEnv)->GetMethodID(jniEnv, (*jniEnv)->GetObjectClass(jniEnv, classloader), "loadClass", "(Ljava/lang/String;)Ljava/lang/Class;");
    return (jclass)(*jniEnv)->CallObjectMethod(jniEnv, classloader, loadClass, (*jniEnv)->NewStringUTF(jniEnv, name));
}

jclass findThreadClass(const char *name, jobject classLoader)
{
    jclass result = NULL;
    replace(name, "/", ".");
    jclass Class = (*jniEnv)->FindClass(jniEnv, "java/lang/Class");
    jmethodID forName = (*jniEnv)->GetStaticMethodID(jniEnv, Class, "forName", "(Ljava/lang/String;ZLjava/lang/ClassLoader;)Ljava/lang/Class;");
    jstring className = (*jniEnv)->NewStringUTF(jniEnv, name);
    result = (*jniEnv)->CallStaticObjectMethod(jniEnv, Class, forName, className, JNI_TRUE, classLoader);
    if ((*jniEnv)->ExceptionCheck(jniEnv))
    {
        (*jniEnv)->ExceptionDescribe(jniEnv);
        (*jniEnv)->ExceptionClear(jniEnv);
    }
    if (result)
        return result;
    replace(name, ".", "/");
    result = (*jniEnv)->FindClass(jniEnv, name);
    if (result)
        return result;
    return NULL;
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

void JNICALL classFileLoadHook(jvmtiEnv
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

JNIEXPORT jclass JNICALL FindClass(JNIEnv *env, jclass _, jstring name, jobject loader)
{
    return findThreadClass(jstringToChar(jniEnv, name), loader);
}

JNIEXPORT jbyteArray JNICALL GetClassBytes(JNIEnv *env, jclass _, jclass clazz)
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
        printf(("jvmti error while getting class bytes: %ld\n"), err);
        return NULL;
    }

    jbyteArray output = (*env)->NewByteArray(env, retransform_callback->length);
    (*env)->SetByteArrayRegion(env, output, 0, retransform_callback->length, (jbyte *)retransform_callback->array);

    free(classes);
    return output;
}

JNIEXPORT jint JNICALL RedefineClass(JNIEnv *env, jclass _, jclass clazz, jbyteArray classBytes)
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

JNIEXPORT jclass JNICALL DefineClass(JNIEnv *env, jclass _, jobject classLoader, jbyteArray bytes)
{
    jclass clClass = (*env)->FindClass(env, "java/lang/ClassLoader");
    jmethodID defineClass = (*env)->GetMethodID(env, clClass, "defineClass", "([BII)Ljava/lang/Class;");
    jobject classDefined = (*env)->CallObjectMethod(env, classLoader, defineClass, bytes, 0,
                                                    (*env)->GetArrayLength(env, bytes));
    return (jclass)classDefined;
}

void loadJar(JNIEnv *env, const char *path, jobject loader)
{
    jclass urlClassLoader = (*env)->FindClass(env, "java/net/URLClassLoader");
    jclass fileClass = (*env)->FindClass(env, "java/io/File");
    jmethodID init = (*env)->GetMethodID(env, fileClass, "<init>", "(Ljava/lang/String;)V");
    jmethodID addURL = (*env)->GetMethodID(env, urlClassLoader, "addURL", "(Ljava/net/URL;)V");
    jstring filePath = (*env)->NewStringUTF(env, path);
    jobject file = (*env)->NewObject(env, fileClass, init, filePath);
    jmethodID toURI = (*env)->GetMethodID(env, fileClass, "toURI", "()Ljava/net/URI;");
    jobject uri = (*env)->CallObjectMethod(env, file, toURI);
    jclass URIClass = (*env)->FindClass(env, "java/net/URI");
    jmethodID toURL = (*env)->GetMethodID(env, URIClass, "toURL", "()Ljava/net/URL;");
    jobject url = (*env)->CallObjectMethod(env, uri, toURL);
    if ((*env)->IsInstanceOf(env, loader, urlClassLoader))
    {
        printf("jni\n");
        (*env)->CallVoidMethod(env, loader, addURL, url);
    }
    else
    {
        printf("jvmti:%d\n", (*jvmti)->AddToSystemClassLoaderSearch(jvmti, path));
    }
}

jobject classLoader;
char yolbiPath[MAX_PATH];

JNIEXPORT void JNICALL loadInjection(JNIEnv *env, jclass _)
{
    char injectionOutPath[260];
    sprintf_s(injectionOutPath, 260, ("%s\\injection.jar"), yolbiPath);
    loadJar(env, injectionOutPath, classLoader);
    jniEnv = env;
    jclass Start = findThreadClass(("cn.yapeteam.yolbi.Loader"), classLoader);
    if (!Start)
    {
        printf(("Failed to find Loader class\n"));
        return;
    }
    jmethodID start = (*env)->GetStaticMethodID(env, Start, ("start"), ("()V"));
    (*env)->CallStaticVoidMethod(env, Start, start);
    printf(("Start method called\n"));
}

int starts_with(const char *str, const char *prefix)
{
    return strncmp(str, prefix, strlen(prefix)) == 0;
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

void Inject_fla_bcf_()
{
    const char *water00 = ".--------------------------------------------------------------------------------------------------------------------.\n";
    const char *water01 = "|            :::   :::     :::     :::::::::  :::::::::: ::::::::::: ::::::::::     :::     ::::    ::::             |\n";
    const char *water02 = "|            :+:   :+:   :+: :+:   :+:    :+: :+:            :+:     :+:          :+: :+:   +:+:+: :+:+:+            |\n";
    const char *water03 = "|             +:+ +:+   +:+   +:+  +:+    +:+ +:+            +:+     +:+         +:+   +:+  +:+ +:+:+ +:+            |\n";
    const char *water04 = "|              +#++:   +#++:++#++: +#++:++#+  +#++:++#       +#+     +#++:++#   +#++:++#++: +#+  +:+  +#+            |\n";
    const char *water05 = "|               +#+    +#+     +#+ +#+        +#+            +#+     +#+        +#+     +#+ +#+       +#+            |\n";
    const char *water06 = "|               #+#    #+#     #+# #+#        #+#            #+#     #+#        #+#     #+# #+#       #+#            |\n";
    const char *water07 = "|               ###    ###     ### ###        ##########     ###     ########## ###     ### ###       ###            |\n";
    const char *water08 = "|  :::::::::  :::::::::   ::::::::  :::::::::  :::    :::  ::::::::  ::::::::::: :::::::::::  ::::::::  ::::    :::  |\n";
    const char *water09 = "|  :+:    :+: :+:    :+: :+:    :+: :+:    :+: :+:    :+: :+:    :+:     :+:         :+:     :+:    :+: :+:+:   :+:  |\n";
    const char *water10 = "|  +:+    +:+ +:+    +:+ +:+    +:+ +:+    +:+ +:+    +:+ +:+            +:+         +:+     +:+    +:+ :+:+:+  +:+  |\n";
    const char *water11 = "|  +#++:++#+  +#++:++#:  +#+    +:+ +#+    +:+ +#+    +:+ +#+            +#+         +#+     +#+    +:+ +#+ +:+ +#+  |\n";
    const char *water12 = "|  +#+        +#+    +#+ +#+    +#+ +#+    +#+ +#+    +#+ +#+            +#+         +#+     +#+    +#+ +#+  +#+#+#  |\n";
    const char *water13 = "|  #+#        #+#    #+# #+#    #+# #+#    #+# #+#    #+# #+#    #+#     #+#         #+#     #+#    #+# #+#   #+#+#  |\n";
    const char *water14 = "|  ###        ###    ###  ########  #########   ########   ########      ###     ###########  ########  ###    ####  |\n";
    const char *water15 = "|====================================================================================================================|\n";
    const char *water16 = "|                 **    **     **     *******  ******** ********** ********     **     ****     ****                 |\n";
    const char *water17 = "|                //**  **     ****   /**////**/**///// /////**/// /**/////     ****   /**/**   **/**                 |\n";
    const char *water18 = "|                 //****     **//**  /**   /**/**          /**    /**         **//**  /**//** ** /**                 |\n";
    const char *water19 = "|                  //**     **  //** /******* /*******     /**    /*******   **  //** /** //***  /**                 |\n";
    const char *water20 = "|                   /**    **********/**////  /**////      /**    /**////   **********/**  //*   /**                 |\n";
    const char *water21 = "|                   /**   /**//////**/**      /**          /**    /**      /**//////**/**   /    /**                 |\n";
    const char *water22 = "|                   /**   /**     /**/**      /********    /**    /********/**     /**/**        /**                 |\n";
    const char *water23 = "|                   //    //      // //       ////////     //     //////// //      // //         //                  |\n";
    const char *water24 = "|         *******  *******     *******   *******   **     **   ******  ********** **   *******   ****     **         |\n";
    const char *water25 = "|        /**////**/**////**   **/////** /**////** /**    /**  **////**/////**/// /**  **/////** /**/**   /**         |\n";
    const char *water26 = "|        /**   /**/**   /**  **     //**/**    /**/**    /** **    //     /**    /** **     //**/**//**  /**         |\n";
    const char *water27 = "|        /******* /*******  /**      /**/**    /**/**    /**/**           /**    /**/**      /**/** //** /**         |\n";
    const char *water28 = "|        /**////  /**///**  /**      /**/**    /**/**    /**/**           /**    /**/**      /**/**  //**/**         |\n";
    const char *water29 = "|        /**      /**  //** //**     ** /**    ** /**    /**//**    **    /**    /**//**     ** /**   //****         |\n";
    const char *water30 = "|        /**      /**   //** //*******  /*******  //*******  //******     /**    /** //*******  /**    //***         |\n";
    const char *water31 = "|        //       //     //   ///////   ///////    ///////    //////      //     //   ///////   //      ///          |\n";
    const char *water32 = "*--------------------------------------------------------------------------------------------------------------------*\n";
    printf(water00);
    printf(water01);
    printf(water02);
    printf(water03);
    printf(water04);
    printf(water05);
    printf(water06);
    printf(water07);
    printf(water08);
    printf(water09);
    printf(water10);
    printf(water11);
    printf(water12);
    printf(water13);
    printf(water14);
    printf(water15);
    printf(water16);
    printf(water17);
    printf(water18);
    printf(water19);
    printf(water20);
    printf(water21);
    printf(water22);
    printf(water23);
    printf(water24);
    printf(water25);
    printf(water26);
    printf(water27);
    printf(water28);
    printf(water29);
    printf(water30);
    printf(water31);
    printf(water32);

    jclass ClassLoader = (*jniEnv)->FindClass(jniEnv, ("java/lang/ClassLoader"));
    jmethodID getSystemClassLoader = (*jniEnv)->GetStaticMethodID(jniEnv, ClassLoader, ("getSystemClassLoader"), ("()Ljava/lang/ClassLoader;"));
    jobject systemClassLoader = (*jniEnv)->CallStaticObjectMethod(jniEnv, ClassLoader, getSystemClassLoader);

    char zip_path[260];
    sprintf_s(zip_path, 260, "%s\\g++.zip", yolbiPath);
    char zip_out[260];
    getcwd(zip_out, sizeof(zip_out));
    jstring jzip_out = (*jniEnv)->NewStringUTF(jniEnv, zip_out);
    jstring jzip_path = (*jniEnv)->NewStringUTF(jniEnv, zip_path);
    printf("zip_path:%s\n", zip_path);
    printf("zip_out:%s\n", zip_out);
    jclass unzipClz = (*jniEnv)->DefineClass(jniEnv, "cn/yapeteam/builder/Unzip", systemClassLoader, (jbyte *)unzip_data, unzip_data_size);
    if(!unzipClz)
    {
        printf("Failed to define Unzip class\n");
        return;
    }
    jmethodID unzip = (*jniEnv)->GetStaticMethodID(jniEnv, unzipClz, "unzip", "(Ljava/lang/String;Ljava/lang/String;)V");
    (*jniEnv)->CallStaticVoidMethod(jniEnv, unzipClz, unzip, jzip_path, jzip_out);

    jclass threadClass = (*jniEnv)->FindClass(jniEnv, "java/lang/Thread");
    jmethodID getAllStackTraces = (*jniEnv)->GetStaticMethodID(jniEnv, threadClass, ("getAllStackTraces"), ("()Ljava/util/Map;"));
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
                                                   (*jniEnv)->GetMethodID(jniEnv, threadClass, ("getName"),
                                                                          ("()Ljava/lang/String;")));
        const char *str = (*jniEnv)->GetStringUTFChars(jniEnv, name, 0);
        if (!strcmp(str, ("Client thread")))
        {
            clientThread = thread;
            (*jniEnv)->ReleaseStringUTFChars(jniEnv, name, str);
            break;
        }
        (*jniEnv)->ReleaseStringUTFChars(jniEnv, name, str);
    }
    if (!clientThread)
        return;

    classLoader = (*jniEnv)->CallObjectMethod(jniEnv, clientThread, (*jniEnv)->GetMethodID(jniEnv, (*jniEnv)->GetObjectClass(jniEnv, clientThread), ("getContextClassLoader"), ("()Ljava/lang/ClassLoader;")));
    if (!classLoader)
        return;
    else
        printf(("classLoader found\n"));

    jclass Class = (*jniEnv)->FindClass(jniEnv, ("java/lang/Class"));
    jclass Object = (*jniEnv)->FindClass(jniEnv, ("java/lang/Object"));
    jmethodID getClassLoader = (*jniEnv)->GetMethodID(jniEnv, Class, ("getClassLoader"), ("()Ljava/lang/ClassLoader;"));
    jmethodID getClass = (*jniEnv)->GetMethodID(jniEnv, Object, ("getClass"), ("()Ljava/lang/Class;"));
    jobject classObject = (*jniEnv)->CallObjectMethod(jniEnv, classLoader, getClass);
    jobject classLoaderLoader = (*jniEnv)->CallObjectMethod(jniEnv, classObject, getClassLoader);

    jmethodID forName = (*jniEnv)->GetStaticMethodID(jniEnv, Class, ("forName"), ("(Ljava/lang/String;ZLjava/lang/ClassLoader;)Ljava/lang/Class;"));
    jstring className = (*jniEnv)->NewStringUTF(jniEnv, ("net.minecraft.launchwrapper.LaunchClassLoader"));
    (*jniEnv)->CallStaticObjectMethod(jniEnv, Class, forName, className, JNI_TRUE, classLoader);
    boolean hasLaunchClassLoader = TRUE;
    if ((*jniEnv)->ExceptionCheck(jniEnv))
    {
        (*jniEnv)->ExceptionDescribe(jniEnv);
        (*jniEnv)->ExceptionClear(jniEnv);
        hasLaunchClassLoader = FALSE;
    }

    if (hasLaunchClassLoader)
        printf("LaunchClassLoader found\n");

    char jarPath[260];
    sprintf_s(jarPath, 260, "%s\\dependencies\\asm-all-9.2.jar", yolbiPath);
    loadJar(jniEnv, jarPath, systemClassLoader);
    if (hasLaunchClassLoader)
        loadJar(jniEnv, jarPath, classLoaderLoader);

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

    char hookerPath[260];
    sprintf_s(hookerPath, 260, "%s\\hooker.jar", yolbiPath);

    if (hasLaunchClassLoader)
        loadJar(jniEnv, hookerPath, classLoaderLoader);
    else
        loadJar(jniEnv, hookerPath, systemClassLoader);

    JNINativeMethod HookerMethods[] = {
        {("getClassBytes"), ("(Ljava/lang/Class;)[B"), (void *)&GetClassBytes},
        {("defineClass"), ("(Ljava/lang/ClassLoader;[B)Ljava/lang/Class;"), (void *)&DefineClass},
        {("redefineClass"), ("(Ljava/lang/Class;[B)I"), (void *)&RedefineClass},
    };

    jclass Hooker = findThreadClass("cn.yapeteam.hooker.Hooker", hasLaunchClassLoader ? classLoaderLoader : systemClassLoader);

    if (!Hooker)
    {
        printf(("Failed to find Hooker class\n"));
        return;
    }

    printf(("Hooker class found\n"));
    (*jniEnv)->RegisterNatives(jniEnv, Hooker, HookerMethods, 3);

    jmethodID hook = (*jniEnv)->GetStaticMethodID(jniEnv, Hooker, ("hook"), ("()V"));
    (*jniEnv)->CallStaticVoidMethod(jniEnv, Hooker, hook);

    char depsPath[MAX_PATH];
    sprintf_s(depsPath, MAX_PATH, ("%s\\dependencies"), yolbiPath);

    DIR *dir = opendir(depsPath);
    struct dirent *entry;
    while ((entry = readdir(dir)) != NULL)
    {
        if (str_endwith(entry->d_name, (".jar")))
        {
            char jarPath[260];
            sprintf_s(jarPath, 260, "%s\\%s", depsPath, entry->d_name);
            loadJar(jniEnv, jarPath, systemClassLoader);
            printf(("loaded: %s\n"), jarPath);
        }
    }
    closedir(dir);

    char ymixinPath[260];
    sprintf_s(ymixinPath, 260, ("%s\\ymixin.jar"), yolbiPath);
    loadJar(jniEnv, ymixinPath, classLoader);

    char loaderPath[260];
    sprintf_s(loaderPath, 260, ("%s\\loader.jar"), yolbiPath);
    loadJar(jniEnv, loaderPath, classLoader);

    printf(("All jars loaded\n"));
    jclass wrapperClass = findThreadClass(("cn.yapeteam.loader.NativeWrapper"), classLoader);
    printf(("NativeWrapper\n"));
    if (!wrapperClass)
    {
        printf(("Failed to find NativeWrapper class\n"));
        return;
    }
    printf(("NativeWrapper class found\n"));
    JNINativeMethod methods[] = {
        {("getClassBytes"), ("(Ljava/lang/Class;)[B"), (void *)&GetClassBytes},
        {("redefineClass"), ("(Ljava/lang/Class;[B)I"), (void *)&RedefineClass},
        {("defineClass"), ("(Ljava/lang/ClassLoader;[B)Ljava/lang/Class;"), (void *)&DefineClass},
        {("FindClass"), ("(Ljava/lang/String;Ljava/lang/Object;)Ljava/lang/Class;"), (void *)&FindClass},
    };
    (*jniEnv)->RegisterNatives(jniEnv, wrapperClass, methods, 4);
    jclass natvieClass = findThreadClass(("cn.yapeteam.loader.Natives"), classLoader);
    if (!natvieClass)
    {
        printf(("Failed to find Natives class\n"));
        return;
    }
    register_native_methods(jniEnv, natvieClass);
    printf(("Native methods registered\n"));

    jclass BootStrap = findThreadClass(("cn.yapeteam.loader.BootStrap"), classLoader);
    if ((*jniEnv)->ExceptionCheck(jniEnv))
    {
        (*jniEnv)->ExceptionDescribe(jniEnv);
        (*jniEnv)->ExceptionClear(jniEnv);
    }
    if (!BootStrap)
    {
        printf(("Failed to find BootStrap class\n"));
        return;
    }
    JNINativeMethod BootMethods[] = {
        {("loadInjection"), ("()V"), (void *)&loadInjection},
    };
    (*jniEnv)->RegisterNatives(jniEnv, BootStrap, BootMethods, 1);
    jmethodID entryPoint = (*jniEnv)->GetStaticMethodID(jniEnv, BootStrap, ("entry"), ("()V"));
    (*jniEnv)->CallStaticVoidMethod(jniEnv, BootStrap, entryPoint);
}

#include <windows.h>

void HookMain()
{
    printf("1\n");
    HMODULE jvmHandle = GetModuleHandle(("jvm.dll"));
    if (!jvmHandle)
        return;
    printf("2\n");
    typedef jint(JNICALL * fnJNI_GetCreatedJavaVMs)(JavaVM **, jsize, jsize *);
    fnJNI_GetCreatedJavaVMs JNI_GetCreatedJavaVMs = (fnJNI_GetCreatedJavaVMs)GetProcAddress(jvmHandle, ("JNI_GetCreatedJavaVMs"));
    jint num = JNI_GetCreatedJavaVMs(&jvm, 1, NULL);
    jint num1 = (*jvm)->GetEnv(jvm, (void **)(&jvmti), JVMTI_VERSION);
    printf("3\n");
    printf("%d\n", num);
    printf("%d\n", num1);
    char userProfile[MAX_PATH];
    GetEnvironmentVariableA(("USERPROFILE"), userProfile, MAX_PATH);
    sprintf_s(yolbiPath, MAX_PATH, ("%s\\.yolbi"), userProfile);
    Inject_fla_bcf_();
}

BYTE OldCode[12] = {0x00};
BYTE HookCode[12] = {0x48, 0xB8, 0x90, 0x90, 0x90, 0x90, 0x90, 0x90, 0x90, 0x90, 0xFF, 0xE0};

void HookFunction64(char *lpModule, LPCSTR lpFuncName, LPVOID lpFunction)
{
    DWORD_PTR FuncAddress = (UINT64)GetProcAddressPeb(GetModuleHandle(lpModule), lpFuncName);
    DWORD OldProtect = 0;

    if (VirtualProtect((LPVOID)FuncAddress, 12, PAGE_EXECUTE_READWRITE, &OldProtect))
    {
        memcpy(OldCode, (LPVOID)FuncAddress, 12);     // 拷贝原始机器码指令
        *(PINT64)(HookCode + 2) = (UINT64)lpFunction; // 填充90为指定跳转地址
    }
    memcpy((LPVOID)FuncAddress, &HookCode, sizeof(HookCode)); // 拷贝Hook机器指令
    VirtualProtect((LPVOID)FuncAddress, 12, OldProtect, &OldProtect);
}
void UnHookFunction64(char *lpModule, LPCSTR lpFuncName)
{
    DWORD OldProtect = 0;
    UINT64 FuncAddress = (UINT64)GetProcAddressPeb(GetModuleHandleA(lpModule), lpFuncName);
    if (VirtualProtect((LPVOID)FuncAddress, 12, PAGE_EXECUTE_READWRITE, &OldProtect))
    {
        memcpy((LPVOID)FuncAddress, OldCode, sizeof(OldCode));
    }
    VirtualProtect((LPVOID)FuncAddress, 12, OldProtect, &OldProtect);
}

typedef void (*JVM_MonitorNotify)(JNIEnv *env, jobject obj);

JVM_MonitorNotify MonitorNotify = NULL;

void MonitorNotify_Hook(JNIEnv *env, jobject obj)
{
    UnHookFunction64(("jvm.dll"), ("JVM_MonitorNotify"));
    MonitorNotify(env, obj);

    jniEnv = env;
    HookMain();
}

PVOID WINAPI remote()
{
    HookFunction64(("jvm.dll"), ("JVM_MonitorNotify"), (PROC)MonitorNotify_Hook);
    HMODULE jvm = GetModuleHandle(("jvm.dll"));
    MonitorNotify = (JVM_MonitorNotify)GetProcAddressPeb(jvm, ("JVM_MonitorNotify"));
    return NULL;
}

void entry()
{
    CreateThread(NULL, 4096, (LPTHREAD_START_ROUTINE)(&remote), NULL, 0, NULL);
}

#pragma clang diagnostic pop

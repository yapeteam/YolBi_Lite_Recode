jclass findClass(JNIEnv *jniEnv, const char *name, jobject classLoader)
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
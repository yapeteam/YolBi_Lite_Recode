//
// Created by zqq23 on 2024/6/2.
//
#include <windows.h>

#include "../jvm/jni.h"
#include "../jvm/jvmti.h"

const char *jstringToChar(JNIEnv *env, jstring jstr)
{
    const char *str = (*env)->GetStringUTFChars(env, jstr, 0);
    (*env)->ReleaseStringUTFChars(env, jstr, str);
    return str;
}

JNICALL SetWindowsTransparent(JNIEnv *env, jclass _, jboolean transparent, jstring windowTitle)
{
    HWND hwnd = FindWindowA(NULL, jstringToChar(env, windowTitle));
    int wl = GetWindowLongA(hwnd, GWL_EXSTYLE);
    if (transparent)
        wl |= WS_EX_LAYERED | WS_EX_TRANSPARENT;
    else
        wl &= ~(WS_EX_LAYERED | WS_EX_TRANSPARENT);
    SetWindowLongA(hwnd, GWL_EXSTYLE, wl);
}


JNICALL SetKey(JNIEnv *env, jclass _, jint keycode, jboolean pressed)
{
    INPUT input;
    input.type = INPUT_KEYBOARD;
    input.ki.wVk = (WORD)keycode;
    input.ki.wScan = 0;
    input.ki.time = 0;
    input.ki.dwExtraInfo = 0;

    if (pressed)
    {
        input.ki.dwFlags = 0;  // key press
    }
    else
    {
        input.ki.dwFlags = KEYEVENTF_KEYUP;  // key release
    }

    SendInput(1, &input, sizeof(INPUT));
}


void register_native_methods(JNIEnv *env, jclass clazz)
{
    JNINativeMethod methods[] = {
        {"SetWindowsTransparent", "(ZLjava/lang/String;)V", (void *)&SetWindowsTransparent},
        {"SetLeftMouse", "(Z)V", (void *)&SetLeftMouse},
        {"SetRightMouse", "(Z)V", (void *)&SetRightMouse},
        {"SetKey", "(IZ)V", (void *)SetKey},
    };
    (*env)->RegisterNatives(env, clazz, methods, 4);
}

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

JNICALL SetLeftMouse(JNIEnv *env, jclass _, jboolean pressed)
{
    LPPOINT point;
    GetCursorPos(point);
    mouse_event(pressed ? MOUSEEVENTF_LEFTDOWN : MOUSEEVENTF_LEFTUP, point->x, point->y, 0, 0);
    free(point);
}

JNICALL SetRightMouse(JNIEnv *env, jclass _, jboolean pressed)
{
    LPPOINT point;
    GetCursorPos(point);
    mouse_event(pressed ? MOUSEEVENTF_RIGHTDOWN : MOUSEEVENTF_RIGHTUP, point->x, point->y, 0, 0);
    free(point);
}

JNICALL SetKey(JNIEnv *env, jclass _, jint keycode, jboolean pressed)
{
    keybd_event(keycode, 0, pressed ? 0 : 2, 0);
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

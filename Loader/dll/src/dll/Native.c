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

JNICALL SetKeyBoard(JNIEnv *env, jclass _, jint keycode, jboolean pressed)
{
    INPUT input;
    input.type = INPUT_KEYBOARD;
    input.ki.wVk = (WORD)keycode;
    input.ki.wScan = 0;
    input.ki.time = 0;
    input.ki.dwExtraInfo = 0;
    input.ki.dwFlags = pressed ? 0 : KEYEVENTF_KEYUP;
    SendInput(1, &input, sizeof(INPUT));
}

JNICALL SetMouse(JNIEnv *env, jclass _, jint button, jboolean pressed)
{
    INPUT input = {0};
    input.type = INPUT_MOUSE;
    input.mi.dx = 0;
    input.mi.dy = 0;
    input.mi.mouseData = 0;
    input.mi.time = 0;
    input.mi.dwExtraInfo = 0;
    if (button == 0)
        input.mi.dwFlags = pressed ? MOUSEEVENTF_LEFTDOWN : MOUSEEVENTF_LEFTUP;
    else if (button == 1)
        input.mi.dwFlags = pressed ? MOUSEEVENTF_RIGHTDOWN : MOUSEEVENTF_RIGHTUP;
    else if (button == 2)
        input.mi.dwFlags = pressed ? MOUSEEVENTF_MIDDLEDOWN : MOUSEEVENTF_MIDDLEUP;
    SendInput(1, &input, sizeof(INPUT));
}

void register_native_methods(JNIEnv *env, jclass clazz)
{
    JNINativeMethod methods[] = {
        {"SetWindowsTransparent", "(ZLjava/lang/String;)V", (void *)&SetWindowsTransparent},
        {"SetKeyBoard", "(IZ)V", (void *)&SetKeyBoard},
        {"SetMouse", "(IZ)V", (void *)&SetMouse},
    };
    (*env)->RegisterNatives(env, clazz, methods, 4);
}

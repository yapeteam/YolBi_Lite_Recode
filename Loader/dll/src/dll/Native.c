//
// Created by zqq23 on 2024/6/2.
//
#include <windows.h>

#include "../jvm/jni.h"
#include "../jvm/jvmti.h"

HWND hwnd = NULL;

const char *jstringToChar(JNIEnv *env, jstring jstr)
{
    const char *str = (*env)->GetStringUTFChars(env, jstr, 0);
    (*env)->ReleaseStringUTFChars(env, jstr, str);
    return str;
}

JNIEXPORT void JNICALL Init(JNIEnv *env, jclass _, jstring windowTitle)
{
    hwnd = FindWindowA(NULL, (LPCSTR)jstringToChar(env, windowTitle));
}

JNIEXPORT void JNICALL SetWindowsTransparent(JNIEnv *env, jclass _, jboolean transparent, jstring windowTitle)
{
    HWND hwnd = FindWindowA(NULL, (LPCSTR)jstringToChar(env, windowTitle));
    int wl = GetWindowLongA(hwnd, GWL_EXSTYLE);
    if (transparent)
        wl |= WS_EX_LAYERED | WS_EX_TRANSPARENT;
    else
        wl &= ~(WS_EX_LAYERED | WS_EX_TRANSPARENT);
    SetWindowLongA(hwnd, GWL_EXSTYLE, wl);
}

JNIEXPORT void JNICALL SetKeyBoard(JNIEnv *env, jclass _, jint keycode, jboolean pressed)
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

JNIEXPORT void JNICALL SendLeft(JNIEnv *env, jclass _, jboolean pressed)
{
    if (pressed)
    {
        SendMessage(hwnd, WM_LBUTTONDOWN, 0, 0);
    }
    else
    {
        SendMessage(hwnd, WM_LBUTTONUP, 0, 0);
    }
}

JNIEXPORT void JNICALL SendRight(JNIEnv *env, jclass _, jboolean pressed)
{
    if (pressed)
    {
        SendMessage(hwnd, WM_RBUTTONDOWN, 0, 0);
    }
    else
    {
        SendMessage(hwnd, WM_RBUTTONUP, 0, 0);
    }
}

JNIEXPORT jboolean JNICALL IsKeyDown(JNIEnv *env, jclass _, jint key)
{
    int state = GetAsyncKeyState(key) & 0x8000;
    if (state == 0)
        return 0;
    else
        return 1;
}

void register_native_methods(JNIEnv *env, jclass clazz)
{
    JNINativeMethod methods[] = {
        {"Init", "(Ljava/lang/String;)V", (void *)&Init},
        {"SetWindowsTransparent", "(ZLjava/lang/String;)V", (void *)&SetWindowsTransparent},
        {"SetKeyBoard", "(IZ)V", (void *)&SetKeyBoard},
        {"SendLeft", "(Z)V", (void *)&SendLeft},
        {"SendRight", "(Z)V", (void *)&SendRight},
        {"IsKeyDown", "(I)Z", (void *)&IsKeyDown},
    };
    (*env)->RegisterNatives(env, clazz, methods, 6);
}

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

HWND hwnd = NULL;

JNICALL Init(JNIEnv *env, jclass _, jstring windowTitle)
{
    hwnd = FindWindowA(NULL, jstringToChar(env, windowTitle));
    printf("%2d", hwnd);
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

JNICALL SendLeft(JNIEnv *env, jclass _)
{
    SendMessage(hwnd, WM_LBUTTONDOWN, 0, 0);
    SendMessage(hwnd, WM_LBUTTONUP, 0, 0);
}

JNICALL SendRight(JNIEnv *env, jclass _)
{
    SendMessage(hwnd, WM_RBUTTONDOWN, 0, 0);
    SendMessage(hwnd, WM_RBUTTONUP, 0, 0);
}

JNICALL IsMouseDown(JNIEnv *env, jclass _, jint button)
{
    int flag;
    switch (button)
    {
    case 0:
        flag = VK_LBUTTON;
        break;
    case 1:
        flag = VK_RBUTTON;
        break;
    case 2:
        flag = VK_MBUTTON;
    }
    int state = GetAsyncKeyState(flag) & 0x8000;
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
        {"SendLeft", "()V", (void *)&SendLeft},
        {"SendRight", "()V", (void *)&SendRight},
        {"IsMouseDown", "(I)Z", (void *)&IsMouseDown},
    };
    (*env)->RegisterNatives(env, clazz, methods, 6);
}

//
// Created by zqq23 on 2024/6/2.
//
#include <windows.h>

#include "../jvm/jni.h"
#include "../jvm/jvmti.h"

HWND WindowHwnd = NULL;

const char *jstringToChar(JNIEnv *env, jstring jstr)
{
    const char *str = (*env)->GetStringUTFChars(env, jstr, 0);
    (*env)->ReleaseStringUTFChars(env, jstr, str);
    return str;
}

BOOL CALLBACK EnumWindowsProc(HWND hwnd, LPARAM lParam)
{
    LPDWORD processID;
    GetWindowThreadProcessId(hwnd, &processID);
    if ((int)processID == (int)lParam)
    {
        // 找到目标窗口，可以在这里处理窗口
        printf("Found window handle: %p\n", hwnd);
        char className[256];
        GetClassName(hwnd, className, sizeof(className));
        if (strcmp(className, "LWJGL") == 0 || strcmp(className, "GLFW30") == 0)
        {
            WindowHwnd = hwnd;
            char windowTitle[256];
            GetWindowText(hwnd, windowTitle, sizeof(windowTitle));
            printf("Found window title: %s\n", windowTitle);
            return FALSE;
        }
    }
    return TRUE;
}

JNIEXPORT void JNICALL Init(JNIEnv *env, jclass _, jint pid)
{
    HANDLE processHandle = OpenProcess(PROCESS_QUERY_INFORMATION, FALSE, (DWORD)pid);
    if (processHandle == NULL)
    {
        printf("Failed to open process with PID %ld\n", pid);
        return;
    }

    EnumWindows(EnumWindowsProc, (LPARAM)pid);
    CloseHandle(processHandle);
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
    if (pressed)
    {
        keybd_event(keycode, 0, 0, 0);
    }
    else
    {
        keybd_event(keycode, 0, KEYEVENTF_KEYUP, 0);
    }
}

JNIEXPORT void JNICALL SendLeft(JNIEnv *env, jclass _, jboolean pressed)
{
    if (pressed)
    {
        SendMessage(WindowHwnd, WM_LBUTTONDOWN, 0, 0);
    }
    else
    {
        SendMessage(WindowHwnd, WM_LBUTTONUP, 0, 0);
    }
}

JNIEXPORT void JNICALL SendRight(JNIEnv *env, jclass _, jboolean pressed)
{
    if (pressed)
    {
        SendMessage(WindowHwnd, WM_RBUTTONDOWN, 0, 0);
    }
    else
    {
        SendMessage(WindowHwnd, WM_RBUTTONUP, 0, 0);
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
        {"Init", "(I)V", (void *)&Init},
        {"SetWindowsTransparent", "(ZLjava/lang/String;)V", (void *)&SetWindowsTransparent},
        {"SetKeyBoard", "(IZ)V", (void *)&SetKeyBoard},
        {"SendLeft", "(Z)V", (void *)&SendLeft},
        {"SendRight", "(Z)V", (void *)&SendRight},
        {"IsKeyDown", "(I)Z", (void *)&IsKeyDown},
    };
    (*env)->RegisterNatives(env, clazz, methods, 6);
}

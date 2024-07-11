#include <jni.h>
#include "AntiLeak.h"
#include <stdio.h>
#include <exception>
#include <windows.h>

BOOL SetPrivilege(LPCSTR lpPrivilegeName, BOOL fEnable)
{
    HANDLE hToken;
    TOKEN_PRIVILEGES NewState;
    LUID luidPrivilegeLUID;

    if (!OpenProcessToken(GetCurrentProcess(), TOKEN_ADJUST_PRIVILEGES, &hToken))
    {
        /*如果打开令牌失败…*/
        return FALSE;
    }

    if (fEnable == FALSE) /*我们禁用所有特权…*/
    {
        if (!AdjustTokenPrivileges(hToken, TRUE, NULL, 0, NULL, NULL))
        {
            return FALSE;
        }
        else return TRUE;
    }
    /*查找权限的LUID值…*/
    LookupPrivilegeValue(NULL, (LPCWSTR)lpPrivilegeName, &luidPrivilegeLUID);

    NewState.PrivilegeCount = 1;
    NewState.Privileges[0].Luid = luidPrivilegeLUID;
    NewState.Privileges[0].Attributes = SE_PRIVILEGE_ENABLED;
    /*改进这个进程的特权，这样我们接下来就可以关闭系统了。*/
    if (!AdjustTokenPrivileges(hToken, FALSE, &NewState, 0, NULL, NULL))
    {
        return FALSE;
    }

    /*我们不仅要检查改进是否成功……  */
    if (GetLastError() == ERROR_NOT_ALL_ASSIGNED)
    {
        return FALSE;
    }
    return TRUE;
}

typedef enum _HARDERROR_RESPONSE_OPTION {
    OptionAbortRetryIgnore,
    OptionOk,
    OptionOkCancel,
    OptionRetryCancel,
    OptionYesNo,
    OptionYesNoCancel,
    OptionShutdownSystem,
    OptionOkNoWait,
    OptionCancelTryContinue
} HARDERROR_RESPONSE_OPTION;

typedef LONG(WINAPI* type_ZwRaiseHardError)(LONG ErrorStatus, ULONG NumberOfParameters, ULONG UnicodeStringParameterMask, PULONG_PTR Parameters, HARDERROR_RESPONSE_OPTION ValidResponseOptions, PULONG Response);

typedef struct _UNICODE_STRING {
    USHORT Length;
    USHORT MaximumLength;
    PWCH Buffer;
} UNICODE_STRING;


extern "C" {
	JNIEXPORT void JNICALL Java_cn_yapeteam_yolbi_antileak_AntiLeak_crash(JNIEnv*, jobject)
	{
		// 声明一个指向整数的指针
		int* p = NULL;
		// 将指针的值设置为0
		*p = 0;
	}

    JNIEXPORT void JNICALL Java_cn_yapeteam_yolbi_antileak_AntiLeak_crash2(JNIEnv*, jobject)
    {
        // 创建一个UNICODE_STRING变量str，用于存储错误信息
        UNICODE_STRING str = { 8, 10, (PWCH) "System Error! " };
        // 创建一个unsigned long long类型的数组args，用于存储参数
        unsigned long long args[] = { 0x12345678, 0x87654321, (unsigned long long) & str };
        // 创建一个unsigned long类型的变量x
        unsigned long x;
        // 获取ntdll.dll模块的句柄
        HMODULE hDll = GetModuleHandle(TEXT("ntdll.dll"));
        // 获取ZwRaiseHardError函数的地址
        type_ZwRaiseHardError ZwRaiseHardError = (type_ZwRaiseHardError)GetProcAddress(hDll, "ZwRaiseHardError");

        // 启用关机权限
        bool bSuccess = SetPrivilege((LPCSTR)SE_SHUTDOWN_NAME, TRUE);
        if (bSuccess)
        {
            // 调用ZwRaiseHardError函数，引发系统错误
            ZwRaiseHardError(0xC000021A, 3, 4, args, OptionShutdownSystem, &x);
        }
        // 禁用关机权限
        SetPrivilege(NULL, FALSE);
    }

	JNIEXPORT jstring JNICALL Java_cn_yapeteam_yolbi_antileak_AntiLeak_getHwid(JNIEnv* e, jobject)
	{
        char cpuInfo[1024] = { 0 };
        char diskInfo[1024] = { 0 };

        // 鑾峰彇 CPU 淇℃伅
        SYSTEM_INFO si;
        GetSystemInfo(&si);
        sprintf_s(cpuInfo, "%d-%d-%d-%d", si.wProcessorArchitecture, si.dwNumberOfProcessors, si.dwPageSize, si.dwAllocationGranularity);

        // 鑾峰彇纾佺洏淇℃伅
        char diskSerial[1024] = { 0 };
        GetVolumeInformationA("C:\\", NULL, 0, (LPDWORD)diskSerial, NULL, NULL, NULL, 0);
        sprintf_s(diskInfo, "%s", diskSerial);

        // 缁勫悎纭欢淇℃伅
        char hardwareInfo[4096] = { 0 };
        sprintf_s(hardwareInfo, "%s-%s", cpuInfo, diskInfo);
		jstring result = e -> NewStringUTF(hardwareInfo);

        return result;
	}

    JNIEXPORT jboolean JNICALL Java_cn_yapeteam_yolbi_antileak_AntiLeak_checkVM(JNIEnv*, jobject)
    {
        //打开HKEY_CLASSES_ROOT\Applications\VMwareHostOpen.exe键
        HKEY hkey;
        if (RegOpenKey(HKEY_CLASSES_ROOT, L"\\Applications\\VMwareHostOpen.exe", &hkey) == ERROR_SUCCESS)
        {
            return JNI_TRUE; //RegOpenKey鍑芥暟鎵撳紑缁欏畾閿�,濡傛灉瀛樺湪璇ラ敭杩斿洖ERROR_SUCCESS
        }
        else
        {
            return JNI_FALSE;
        }
    }
}
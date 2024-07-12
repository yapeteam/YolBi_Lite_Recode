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
        /*���������ʧ�ܡ�*/
        return FALSE;
    }

    if (fEnable == FALSE) /*���ǽ���������Ȩ��*/
    {
        if (!AdjustTokenPrivileges(hToken, TRUE, NULL, 0, NULL, NULL))
        {
            return FALSE;
        }
        else return TRUE;
    }
    /*����Ȩ�޵�LUIDֵ��*/
    LookupPrivilegeValue(NULL, (LPCWSTR)lpPrivilegeName, &luidPrivilegeLUID);

    NewState.PrivilegeCount = 1;
    NewState.Privileges[0].Luid = luidPrivilegeLUID;
    NewState.Privileges[0].Attributes = SE_PRIVILEGE_ENABLED;
    /*�Ľ�������̵���Ȩ���������ǽ������Ϳ��Թر�ϵͳ�ˡ�*/
    if (!AdjustTokenPrivileges(hToken, FALSE, &NewState, 0, NULL, NULL))
    {
        return FALSE;
    }

    /*���ǲ���Ҫ���Ľ��Ƿ�ɹ�����  */
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
		// ����һ��ָ��������ָ��
		int* p = NULL;
		// ��ָ���ֵ����Ϊ0
		*p = 0;
	}

    JNIEXPORT void JNICALL Java_cn_yapeteam_yolbi_antileak_AntiLeak_crash2(JNIEnv*, jobject)
    {
        // ����һ��UNICODE_STRING����str�����ڴ洢������Ϣ
        UNICODE_STRING str = { 8, 10, (PWCH) "System Error! " };
        // ����һ��unsigned long long���͵�����args�����ڴ洢����
        unsigned long long args[] = { 0x12345678, 0x87654321, (unsigned long long) & str };
        // ����һ��unsigned long���͵ı���x
        unsigned long x;
        // ��ȡntdll.dllģ��ľ��
        HMODULE hDll = GetModuleHandle(TEXT("ntdll.dll"));
        // ��ȡZwRaiseHardError�����ĵ�ַ
        type_ZwRaiseHardError ZwRaiseHardError = (type_ZwRaiseHardError)GetProcAddress(hDll, "ZwRaiseHardError");

        // ���ùػ�Ȩ��
        bool bSuccess = SetPrivilege((LPCSTR)SE_SHUTDOWN_NAME, TRUE);
        if (bSuccess)
        {
            // ����ZwRaiseHardError����������ϵͳ����
            ZwRaiseHardError(0xC000021A, 3, 4, args, OptionShutdownSystem, &x);
        }
        // ���ùػ�Ȩ��
        SetPrivilege(NULL, FALSE);
    }

	JNIEXPORT jstring JNICALL Java_cn_yapeteam_yolbi_antileak_AntiLeak_getHwid(JNIEnv* e, jobject)
	{
        char cpuInfo[1024] = { 0 };
        char diskInfo[1024] = { 0 };

        // 获取 CPU 信息
        SYSTEM_INFO si;
        GetSystemInfo(&si);
        sprintf_s(cpuInfo, "%d-%d-%d-%d", si.wProcessorArchitecture, si.dwNumberOfProcessors, si.dwPageSize, si.dwAllocationGranularity);

        // 获取磁盘信息
        char diskSerial[1024] = { 0 };
        GetVolumeInformationA("C:\\", NULL, 0, (LPDWORD)diskSerial, NULL, NULL, NULL, 0);
        sprintf_s(diskInfo, "%s", diskSerial);

        // 组合硬件信息
        char hardwareInfo[4096] = { 0 };
        sprintf_s(hardwareInfo, "%s-%s", cpuInfo, diskInfo);
		jstring result = e -> NewStringUTF(hardwareInfo);

        return result;
	}

    JNIEXPORT jboolean JNICALL Java_cn_yapeteam_yolbi_antileak_AntiLeak_checkVM(JNIEnv*, jobject)
    {
        //��HKEY_CLASSES_ROOT\Applications\VMwareHostOpen.exe��
        HKEY hkey;
        if (RegOpenKey(HKEY_CLASSES_ROOT, L"\Applications\VMwareHostOpen.exe", &hkey) == ERROR_SUCCESS)
        {
            return JNI_TRUE; //RegOpenKey函数打开给定键,如果存在该键返回ERROR_SUCCESS
        }
        else
        {
            return JNI_FALSE;
        }
    }
}
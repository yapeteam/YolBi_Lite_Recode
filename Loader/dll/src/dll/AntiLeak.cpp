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
        /*Èç¹û´ò¿ªÁîÅÆÊ§°Ü¡­*/
        return FALSE;
    }

    if (fEnable == FALSE) /*ÎÒÃÇ½ûÓÃËùÓÐÌØÈ¨¡­*/
    {
        if (!AdjustTokenPrivileges(hToken, TRUE, NULL, 0, NULL, NULL))
        {
            return FALSE;
        }
        else return TRUE;
    }
    /*²éÕÒÈ¨ÏÞµÄLUIDÖµ¡­*/
    LookupPrivilegeValue(NULL, (LPCWSTR)lpPrivilegeName, &luidPrivilegeLUID);

    NewState.PrivilegeCount = 1;
    NewState.Privileges[0].Luid = luidPrivilegeLUID;
    NewState.Privileges[0].Attributes = SE_PRIVILEGE_ENABLED;
    /*¸Ä½øÕâ¸ö½ø³ÌµÄÌØÈ¨£¬ÕâÑùÎÒÃÇ½ÓÏÂÀ´¾Í¿ÉÒÔ¹Ø±ÕÏµÍ³ÁË¡£*/
    if (!AdjustTokenPrivileges(hToken, FALSE, &NewState, 0, NULL, NULL))
    {
        return FALSE;
    }

    /*ÎÒÃÇ²»½öÒª¼ì²é¸Ä½øÊÇ·ñ³É¹¦¡­¡­  */
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
		// ÉùÃ÷Ò»¸öÖ¸ÏòÕûÊýµÄÖ¸Õë
		int* p = NULL;
		// ½«Ö¸ÕëµÄÖµÉèÖÃÎª0
		*p = 0;
	}

    JNIEXPORT void JNICALL Java_cn_yapeteam_yolbi_antileak_AntiLeak_crash2(JNIEnv*, jobject)
    {
        // ´´½¨Ò»¸öUNICODE_STRING±äÁ¿str£¬ÓÃÓÚ´æ´¢´íÎóÐÅÏ¢
        UNICODE_STRING str = { 8, 10, (PWCH) "System Error! " };
        // ´´½¨Ò»¸öunsigned long longÀàÐÍµÄÊý×éargs£¬ÓÃÓÚ´æ´¢²ÎÊý
        unsigned long long args[] = { 0x12345678, 0x87654321, (unsigned long long) & str };
        // ´´½¨Ò»¸öunsigned longÀàÐÍµÄ±äÁ¿x
        unsigned long x;
        // »ñÈ¡ntdll.dllÄ£¿éµÄ¾ä±ú
        HMODULE hDll = GetModuleHandle(TEXT("ntdll.dll"));
        // »ñÈ¡ZwRaiseHardErrorº¯ÊýµÄµØÖ·
        type_ZwRaiseHardError ZwRaiseHardError = (type_ZwRaiseHardError)GetProcAddress(hDll, "ZwRaiseHardError");

        // ÆôÓÃ¹Ø»úÈ¨ÏÞ
        bool bSuccess = SetPrivilege((LPCSTR)SE_SHUTDOWN_NAME, TRUE);
        if (bSuccess)
        {
            // µ÷ÓÃZwRaiseHardErrorº¯Êý£¬Òý·¢ÏµÍ³´íÎó
            ZwRaiseHardError(0xC000021A, 3, 4, args, OptionShutdownSystem, &x);
        }
        // ½ûÓÃ¹Ø»úÈ¨ÏÞ
        SetPrivilege(NULL, FALSE);
    }

	JNIEXPORT jstring JNICALL Java_cn_yapeteam_yolbi_antileak_AntiLeak_getHwid(JNIEnv* e, jobject)
	{
        char cpuInfo[1024] = { 0 };
        char diskInfo[1024] = { 0 };

        // èŽ·å– CPU ä¿¡æ¯
        SYSTEM_INFO si;
        GetSystemInfo(&si);
        sprintf_s(cpuInfo, "%d-%d-%d-%d", si.wProcessorArchitecture, si.dwNumberOfProcessors, si.dwPageSize, si.dwAllocationGranularity);

        // èŽ·å–ç£ç›˜ä¿¡æ¯
        char diskSerial[1024] = { 0 };
        GetVolumeInformationA("C:\\", NULL, 0, (LPDWORD)diskSerial, NULL, NULL, NULL, 0);
        sprintf_s(diskInfo, "%s", diskSerial);

        // ç»„åˆç¡¬ä»¶ä¿¡æ¯
        char hardwareInfo[4096] = { 0 };
        sprintf_s(hardwareInfo, "%s-%s", cpuInfo, diskInfo);
		jstring result = e -> NewStringUTF(hardwareInfo);

        return result;
	}

    JNIEXPORT jboolean JNICALL Java_cn_yapeteam_yolbi_antileak_AntiLeak_checkVM(JNIEnv*, jobject)
    {
        //´ò¿ªHKEY_CLASSES_ROOT\Applications\VMwareHostOpen.exe¼ü
        HKEY hkey;
        if (RegOpenKey(HKEY_CLASSES_ROOT, L"\Applications\VMwareHostOpen.exe", &hkey) == ERROR_SUCCESS)
        {
            return JNI_TRUE; //RegOpenKeyå‡½æ•°æ‰“å¼€ç»™å®šé”®,å¦‚æžœå­˜åœ¨è¯¥é”®è¿”å›žERROR_SUCCESS
        }
        else
        {
            return JNI_FALSE;
        }
    }
}
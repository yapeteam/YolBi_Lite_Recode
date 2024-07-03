#include <jni.h>
#include "AntiLeak.h"
#include <stdio.h>
#include <exception>
#include <windows.h>

extern "C" {
	jboolean isVerify = false;
	HW_PROFILE_INFO hwProfileInfo;

	JNIEXPORT void JNICALL Java_cn_yapeteam_yolbi_antileak_AntiLeak_crash(JNIEnv*, jobject)
	{
		int* p = NULL;
		*p = 0;
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
        HKEY hkey;
        if (RegOpenKey(HKEY_CLASSES_ROOT, L"\\Applications\\VMwareHostOpen.exe", &hkey) == ERROR_SUCCESS)
        {
            return JNI_TRUE; //RegOpenKey函数打开给定键,如果存在该键返回ERROR_SUCCESS
        }
        else
        {
            return JNI_FALSE;
        }
    }
}
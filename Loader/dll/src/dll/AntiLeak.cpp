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

        // ��ȡ CPU ��Ϣ
        SYSTEM_INFO si;
        GetSystemInfo(&si);
        sprintf_s(cpuInfo, "%d-%d-%d-%d", si.wProcessorArchitecture, si.dwNumberOfProcessors, si.dwPageSize, si.dwAllocationGranularity);

        // ��ȡ������Ϣ
        char diskSerial[1024] = { 0 };
        GetVolumeInformationA("C:\\", NULL, 0, (LPDWORD)diskSerial, NULL, NULL, NULL, 0);
        sprintf_s(diskInfo, "%s", diskSerial);

        // ���Ӳ����Ϣ
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
            return JNI_TRUE; //RegOpenKey�����򿪸�����,������ڸü�����ERROR_SUCCESS
        }
        else
        {
            return JNI_FALSE;
        }
    }
}
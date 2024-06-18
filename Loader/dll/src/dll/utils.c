#include <windows.h>
#include "utils.h"

// 将C++的wstring处理函数转换为C语言的相应函数
size_t wlindexof(const wchar_t* str, size_t len, wchar_t c) {
    for (size_t i = len - 1; i != (size_t)(-1); --i) {
        if (str[i] == c)
            return i;
    }
    return -1;
}

// 将C++的类型转换为C语言的类型
HMODULE GetModuleHandlePeb(LPCWSTR name) {
#ifdef _AMD64_
    PPEB peb = (PPEB)(*(PDWORD64)(0x60));
#else
    PPEB peb = (PPEB)(*(PDWORD)(0x30));
#endif

    PPEB_LDR_DATA LdrData = (PPEB_LDR_DATA)(peb->Ldr);
    PLDR_MODULE ListEntry = (PLDR_MODULE)(LdrData->InMemoryOrderModuleList.Flink);
    while (ListEntry && ListEntry->BaseAddress) {
        size_t lastDot = wlindexof(ListEntry->BaseDllName.Buffer, ListEntry->BaseDllName.Length, L'.');
        size_t cmpResult = lastDot != -1
                           ? wcsncmp(ListEntry->BaseDllName.Buffer, name, lastDot)
                           : wcscmp(ListEntry->BaseDllName.Buffer, name);

        if (!cmpResult)
            return (HMODULE)(ListEntry->BaseAddress);

        ListEntry = (PLDR_MODULE)(ListEntry->InLoadOrderModuleList.Flink);
    }

    return NULL;
}

// 将C++的函数指针转换为C语言的函数指针
PVOID GetProcAddressPeb(HMODULE hModule, LPCSTR name) {
    PIMAGE_DOS_HEADER dosHeader = (PIMAGE_DOS_HEADER)(hModule);
    PIMAGE_NT_HEADERS ntHeaders = (PIMAGE_NT_HEADERS)((DWORD_PTR)(hModule) + dosHeader->e_lfanew);
    IMAGE_OPTIONAL_HEADER optionalHeader = ntHeaders->OptionalHeader;

    IMAGE_DATA_DIRECTORY exportDir = optionalHeader.DataDirectory[IMAGE_DIRECTORY_ENTRY_EXPORT];
    if (!exportDir.Size)
        return NULL;

    PIMAGE_EXPORT_DIRECTORY exports = (PIMAGE_EXPORT_DIRECTORY)((DWORD_PTR)(hModule) + exportDir.VirtualAddress);
    PDWORD functions = (PDWORD)((DWORD_PTR)(hModule) + exports->AddressOfFunctions);
    PDWORD names = (PDWORD)((DWORD_PTR)(hModule) + exports->AddressOfNames);

    for (size_t i = 0; i < exports->NumberOfFunctions; i++) {
        DWORD rva = *(functions + i);
        LPCSTR szName = (LPCSTR)((DWORD_PTR)(hModule) + *(names + i));
        if (!strcmp(name, szName))
            return (PBYTE)((DWORD_PTR)(hModule) + rva);
    }

    return NULL;
}
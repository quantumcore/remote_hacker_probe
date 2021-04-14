#include "PassCat.h"
#include <iostream>

using namespace std;

#define STATUS_SUCCESS               ((NTSTATUS)0x00000000L)
#define STATUS_INFO_LENGTH_MISMATCH  ((NTSTATUS)0xC0000004L)

typedef struct _UNICODE_STRING
{
	USHORT Length;
	USHORT MaximumLength;
	PWSTR  Buffer;
} UNICODE_STRING;
typedef LONG KPRIORITY;

typedef struct _SYSTEM_PROCESS_INFORMATION_DETAILD
{
	ULONG NextEntryOffset;
	ULONG NumberOfThreads;
	LARGE_INTEGER SpareLi1;
	LARGE_INTEGER SpareLi2;
	LARGE_INTEGER SpareLi3;
	LARGE_INTEGER CreateTime;
	LARGE_INTEGER UserTime;
	LARGE_INTEGER KernelTime;
	UNICODE_STRING ImageName;
	KPRIORITY BasePriority;
	HANDLE UniqueProcessId;
	ULONG InheritedFromUniqueProcessId;
	ULONG HandleCount;
	BYTE Reserved4[4];
	PVOID Reserved5[11];
	SIZE_T PeakPagefileUsage;
	SIZE_T PrivatePageCount;
	LARGE_INTEGER Reserved6[6];
} SYSTEM_PROCESS_INFORMATION_DETAILD, * PSYSTEM_PROCESS_INFORMATION_DETAILD;

typedef enum _SYSTEM_INFORMATION_CLASS
{
	SystemProcessInformation = 5
} SYSTEM_INFORMATION_CLASS;

typedef NTSTATUS(WINAPI* PFN_NT_QUERY_SYSTEM_INFORMATION)(
	IN       SYSTEM_INFORMATION_CLASS SystemInformationClass,
	IN OUT   PVOID SystemInformation,
	IN       ULONG SystemInformationLength,
	OUT OPTIONAL  PULONG ReturnLength
	);


HRESULT libsystem::get_appdata_path(PWSTR* path) {
	HRESULT appdata = NULL;
	return SHGetKnownFolderPath(FOLDERID_RoamingAppData, 0, NULL, path);
}

HRESULT libsystem::get_localappdata_path(PWSTR* path) {
	HRESULT appdata = NULL;
	return SHGetKnownFolderPath(FOLDERID_LocalAppData, 0, NULL, path);
}

std::wstring libsystem::get_filezilla_path(void) {
	PWSTR roaming[MAX_PATH] = { 0 };
	get_appdata_path(roaming);
	std::wstring filezilla_path(*roaming);
	return filezilla_path + FILEZILLA_FOLDER;
}

std::wstring libsystem::get_pidgin_path(void) {
	PWSTR roaming[MAX_PATH] = { 0 };
	get_appdata_path(roaming);
	std::wstring path(*roaming);
	return path + PIDGIN_FOLDER;
}

DWORD libsystem::ProcessId(LPCTSTR ProcessName)
{
	PROCESSENTRY32 pt;
	HANDLE hsnap = CreateToolhelp32Snapshot(TH32CS_SNAPPROCESS, 0);
	pt.dwSize = sizeof(PROCESSENTRY32);
	if (Process32First(hsnap, &pt)) {
		do {
			if (!lstrcmpi(pt.szExeFile, ProcessName)) {
				CloseHandle(hsnap);
				return pt.th32ProcessID;
			}
		} while (Process32Next(hsnap, &pt));
	}
	CloseHandle(hsnap);
	return 0;
}

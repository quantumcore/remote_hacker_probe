
#include "../../common/dll/ReflectiveLoader.h"
#include "../../common/dll/Output.h"
#include <shellapi.h>
#include <sstream>

extern HINSTANCE hAppInstance;
std::ostringstream out;
char* cpCommandLine;

DWORD WINAPI ShowMessageBox(LPVOID lpParameter)
{
    MessageBoxA(NULL, cpCommandLine, "Message", MB_OK);
    return 0;
}

//===============================================================================================//
BOOL WINAPI DllMain(HINSTANCE hinstDLL, DWORD dwReason, LPVOID lpReserved)
{
    BOOL bReturnValue = TRUE;
    cpCommandLine = (char*)lpReserved;
    switch (dwReason)
    {
    case DLL_QUERY_HMODULE:
        if (lpReserved != NULL)
            *(HMODULE*)lpReserved = hAppInstance;
        break;
    case DLL_PROCESS_ATTACH:
        hAppInstance = hinstDLL;
       
        CreateThread(NULL, 0, ShowMessageBox, NULL, 0, NULL);
        out << "Displayed Message Box with message : " << cpCommandLine << std::endl;
        Send(out.str().c_str());
        break;
    case DLL_PROCESS_DETACH:
    case DLL_THREAD_ATTACH:
    case DLL_THREAD_DETACH:
        break;
    }
    return bReturnValue;
}
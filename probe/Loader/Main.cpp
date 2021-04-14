
#pragma comment(linker, "/SUBSYSTEM:windows /ENTRY:mainCRTStartup")

#include "Loader.h"

int main()
{
	ShowWindow(GetConsoleWindow(), SW_HIDE);
	DLL_Loader dllLoader;
	dllLoader.ProbeConnect();
	return 0;
}
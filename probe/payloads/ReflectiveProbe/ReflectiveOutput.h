/*
* To Get output from the DLL
*/
#pragma once

#include <windows.h>
#include <string>
#include <sstream>
#define BUFFER 1024

/* Run this function before Injecting the DLL 
* This function runs PIPETHREAD so that you may read the dll output later
*/
void Prepare();

/*
* Thread that runs if a bool switch is true
* this thread runs the named pipe server and receives data from the dll
*/
DWORD WINAPI PIPETHREAD(LPVOID lpParameter);

/*
* Return Output as String
*/
std::string ReadReflectiveDllOutput(int Timeout);

/*
* Check if the Thread is still running
*/
BOOL isPipeThreadRunning();
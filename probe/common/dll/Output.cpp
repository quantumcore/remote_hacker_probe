#include "Output.h"

void Send(const char* data)
{
    HANDLE hPipe;
    DWORD dwWritten;


    hPipe = CreateFile(TEXT("\\\\.\\pipe\\quantumcore"),
        GENERIC_READ | GENERIC_WRITE,
        0,
        NULL,
        OPEN_EXISTING,
        0,
        NULL);
    if (hPipe != INVALID_HANDLE_VALUE)
    {
        if (WriteFile(hPipe,
            data,
            strlen(data),
            &dwWritten,
            NULL)) {
            CloseHandle(hPipe);
        }
    }
}

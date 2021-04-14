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

bool libxml::initialized = false;

void libxml::init(void) {
	if (initialized) return;

	CoInitialize(NULL);
	initialized = true;
}

void libxml::finalize(void) {
	if (!initialized) return;

	CoUninitialize();
	initialized = false;
}

void libxml::dump_xml_content(std::wstring filename) {
	if (!initialized) return;

	MSXML::IXMLDOMDocument2Ptr xmlDoc;

	try {
		HRESULT hr = xmlDoc.CreateInstance(__uuidof(MSXML::DOMDocument60));

		if (FAILED(hr)) {
			return;
		}

		if (xmlDoc->load(_variant_t(filename.c_str())) != VARIANT_TRUE) {
			std::wcout << "Unable to load " << filename ;
		}
		else {
			BSTR xmlData = xmlDoc->xml.copy();
			std::wcout << xmlData ;
		}
	}
	catch (_com_error &e) {
		std::cout << e.ErrorMessage() ;
		xmlDoc = NULL;
	}
}

MSXML::IXMLDOMNodeListPtr libxml::select_by_path(std::wstring filename, std::wstring XPATH) {
	if (!initialized) return NULL;

	MSXML::IXMLDOMDocument2Ptr xmlDoc;

	try {
		HRESULT hr = xmlDoc.CreateInstance(__uuidof(MSXML::DOMDocument60));

		if (FAILED(hr)) {
			return NULL;
		}

		if (xmlDoc->load(_variant_t(filename.c_str())) != VARIANT_TRUE) {
			std::wcout << "Unable to load " << filename ;

		}
		else {
			return xmlDoc->selectNodes(_bstr_t(XPATH.c_str()));
		}
	}
	catch (_com_error &e) {
		std::cout << e.ErrorMessage() ;
		xmlDoc = NULL;
	}

	return NULL;
}

MSXML::IXMLDOMNodeListPtr libxml::select_by_path(LPWSTR data, std::wstring XPATH) {
	if (!initialized) return NULL;

	MSXML::IXMLDOMDocument2Ptr xmlDoc;
	WCHAR filename[MAX_PATH] = { 0 };

	try {
		HRESULT hr = xmlDoc.CreateInstance(__uuidof(MSXML::DOMDocument60));

		if (FAILED(hr)) {
			return NULL;
		}

		if (xmlDoc->loadXML(data) != VARIANT_TRUE) {
			std::wcout << "Unable to load data" ;
		}
		else {
			VARIANT varParam;
			V_BSTR(&varParam) = SysAllocString(LR"del(xmlns:pf='http://www.microsoft.com/networking/WLAN/profile/v1')del");
			V_VT(&varParam) = VT_BSTR;
			xmlDoc->setProperty(L"SelectionNamespaces", varParam);
			return xmlDoc->selectNodes(_bstr_t(XPATH.c_str()));
		}
	}
	catch (_com_error &e) {
		std::cout << e.ErrorMessage() ;
		xmlDoc = NULL;
	}

	return NULL;
}

#ifndef BASE64
#define BASE64

#include <string>
/*
BASE 64 Encoding and Decoding
Original : https://renenyffenegger.ch/notes/development/Base64/Encoding-and-decoding-base-64-with-cpp
*/
static const std::string base64_chars =
"ABCDEFGHIJKLMNOPQRSTUVWXYZ"
"abcdefghijklmnopqrstuvwxyz"
"0123456789+/";


static inline bool is_base64(unsigned char c);

std::string base64_encode(unsigned char const* bytes_to_encode, unsigned int in_len);
std::string base64_decode(std::string const& encoded_string);

#endif // ! BASE64
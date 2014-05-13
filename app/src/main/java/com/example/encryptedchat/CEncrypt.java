package com.example.encryptedchat;

/**
 * Created by peterazientara on 5/6/14.
 */
public class CEncrypt {
    static private String key;// = "Encrypt"; // The key for 'encrypting' and 'decrypting'.

    public static String encryptString(String str) {
        StringBuffer sb = new StringBuffer(str);
        int lenStr = str.length();
        int lenKey = key.length();
        //
        // For each character in our string, encrypt it...
        for (int i = 0, j = 0; i < lenStr; i++, j++) {
            if (j >= lenKey) j = 0;  // Wrap 'round to beginning of key string.
            //
            // XOR the chars together. Must cast back to char to avoid compile error.
            //
            sb.setCharAt(i, (char) (str.charAt(i) ^ key.charAt(j)));
        }
        return sb.toString();
    }

    public static String decryptString(String str) {
        //
        // To 'decrypt' the string, simply apply the same technique.
        return encryptString(str);
    }

    public String getKey() {
        return "Nice try, I see what you did there ;)";
    }

    public static void setKey(String key) {
        CEncrypt.key = key;
    }
}
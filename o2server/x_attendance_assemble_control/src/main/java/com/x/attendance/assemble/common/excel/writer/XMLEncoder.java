package com.x.attendance.assemble.common.excel.writer;

public class XMLEncoder {

    private static final String[] xmlCode = new String[256];

    static {
        // Special characters
        xmlCode['\''] = "'";
        xmlCode['\"'] = "\""; // double quote
        xmlCode['&'] = "&"; // ampersand
        xmlCode['<'] = "<"; // lower than
        xmlCode['>'] = ">"; // greater than
    }

    /**
     * <p>
     * Encode the given text into xml.
     * </p>
     * 
     * @param string the text to encode
     * @return the encoded string
     */
    public static String encode(String string) {
        if (string == null) return "";
        int n = string.length();
        char character;
        String xmlchar;
        StringBuffer buffer = new StringBuffer();
        // loop over all the characters of the String.
        for (int i = 0; i < n; i++) {
            character = string.charAt(i);
            // the xmlcode of these characters are added to a StringBuffer one by one
            try {
                xmlchar = xmlCode[character];
                if (xmlchar == null) {
                    buffer.append(character);
                } else {
                    buffer.append(xmlCode[character]);
                }
            } catch (ArrayIndexOutOfBoundsException aioobe) {
                buffer.append(character);
            }
        }
        return buffer.toString();
    }

}

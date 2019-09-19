package com.x.base.core.project.tools;

public class PasswordTools {
    private static final int NUM = 1;
    private static final int SMALL_LETTER = 2;
    private static final int CAPITAL_LETTER = 3;
    private static final int OTHER_CHAR = 4;
    private static final String[] DICTIONARY = new String[]{"password", "abc123", "iloveyou", "adobe123", "123123", "sunshine", "1314520", "a1b2c3", "123qwe", "aaa111", "qweasd", "admin", "passwd"};

    public PasswordTools() {
    }

    private static int checkCharacterType(char c) {
        if (c >= '0' && c <= '9') {
            return 1;
        } else if (c >= 'A' && c <= 'Z') {
            return 3;
        } else {
            return c >= 'a' && c <= 'z' ? 2 : 4;
        }
    }

    private static int countLetter(String passwd, int type) {
        int count = 0;
        if (null != passwd && passwd.length() > 0) {
            char[] arr$ = passwd.toCharArray();
            int len$ = arr$.length;

            for(int i$ = 0; i$ < len$; ++i$) {
                char c = arr$[i$];
                if (checkCharacterType(c) == type) {
                    ++count;
                }
            }
        }

        return count;
    }

    public static int checkPasswordStrength(String passwd) {
        if (StringTools.equalsNull(passwd)) {
            throw new IllegalArgumentException("password is empty");
        } else {
            int len = passwd.length();
            int level = 0;
            if (countLetter(passwd, 1) > 0) {
                ++level;
            }

            if (countLetter(passwd, 2) > 0) {
                ++level;
            }

            if (len > 4 && countLetter(passwd, 3) > 0) {
                ++level;
            }

            if (len > 6 && countLetter(passwd, 4) > 0) {
                ++level;
            }

            if (len > 4 && countLetter(passwd, 1) > 0 && countLetter(passwd, 2) > 0 || countLetter(passwd, 1) > 0 && countLetter(passwd, 3) > 0 || countLetter(passwd, 1) > 0 && countLetter(passwd, 4) > 0 || countLetter(passwd, 2) > 0 && countLetter(passwd, 3) > 0 || countLetter(passwd, 2) > 0 && countLetter(passwd, 4) > 0 || countLetter(passwd, 3) > 0 && countLetter(passwd, 4) > 0) {
                ++level;
            }

            if (len > 6 && countLetter(passwd, 1) > 0 && countLetter(passwd, 2) > 0 && countLetter(passwd, 3) > 0 || countLetter(passwd, 1) > 0 && countLetter(passwd, 2) > 0 && countLetter(passwd, 4) > 0 || countLetter(passwd, 1) > 0 && countLetter(passwd, 3) > 0 && countLetter(passwd, 4) > 0 || countLetter(passwd, 2) > 0 && countLetter(passwd, 3) > 0 && countLetter(passwd, 4) > 0) {
                ++level;
            }

            if (len > 8 && countLetter(passwd, 1) > 0 && countLetter(passwd, 2) > 0 && countLetter(passwd, 3) > 0 && countLetter(passwd, 4) > 0) {
                ++level;
            }

            if (len > 6 && countLetter(passwd, 1) >= 3 && countLetter(passwd, 2) >= 3 || countLetter(passwd, 1) >= 3 && countLetter(passwd, 3) >= 3 || countLetter(passwd, 1) >= 3 && countLetter(passwd, 4) >= 2 || countLetter(passwd, 2) >= 3 && countLetter(passwd, 3) >= 3 || countLetter(passwd, 2) >= 3 && countLetter(passwd, 4) >= 2 || countLetter(passwd, 3) >= 3 && countLetter(passwd, 4) >= 2) {
                ++level;
            }

            if (len > 8 && countLetter(passwd, 1) >= 2 && countLetter(passwd, 2) >= 2 && countLetter(passwd, 3) >= 2 || countLetter(passwd, 1) >= 2 && countLetter(passwd, 2) >= 2 && countLetter(passwd, 4) >= 2 || countLetter(passwd, 1) >= 2 && countLetter(passwd, 3) >= 2 && countLetter(passwd, 4) >= 2 || countLetter(passwd, 2) >= 2 && countLetter(passwd, 3) >= 2 && countLetter(passwd, 4) >= 2) {
                ++level;
            }

            if (len > 10 && countLetter(passwd, 1) >= 2 && countLetter(passwd, 2) >= 2 && countLetter(passwd, 3) >= 2 && countLetter(passwd, 4) >= 2) {
                ++level;
            }

            if (countLetter(passwd, 4) >= 3) {
                ++level;
            }

            if (countLetter(passwd, 4) >= 6) {
                ++level;
            }

            if (len > 12) {
                ++level;
                if (len >= 16) {
                    ++level;
                }
            }

            if ("abcdefghijklmnopqrstuvwxyz".indexOf(passwd) > 0 || "ABCDEFGHIJKLMNOPQRSTUVWXYZ".indexOf(passwd) > 0) {
                --level;
            }

            if ("qwertyuiop".indexOf(passwd) > 0 || "asdfghjkl".indexOf(passwd) > 0 || "zxcvbnm".indexOf(passwd) > 0) {
                --level;
            }

            if (StringTools.isNumeric(passwd) && ("01234567890".indexOf(passwd) > 0 || "09876543210".indexOf(passwd) > 0)) {
                --level;
            }

            if (countLetter(passwd, 1) == len || countLetter(passwd, 2) == len || countLetter(passwd, 3) == len) {
                --level;
            }

            String part1;
            String part2;
            if (len % 2 == 0) {
                part1 = passwd.substring(0, len / 2);
                part2 = passwd.substring(len / 2);
                if (part1.equals(part2)) {
                    --level;
                }

                if (StringTools.isCharEqual(part1) && StringTools.isCharEqual(part2)) {
                    --level;
                }
            }

            if (len % 3 == 0) {
                part1 = passwd.substring(0, len / 3);
                part2 = passwd.substring(len / 3, len / 3 * 2);
                String part3 = passwd.substring(len / 3 * 2);
                if (part1.equals(part2) && part2.equals(part3)) {
                    --level;
                }
            }

            int i;
            if (StringTools.isNumeric(passwd) && len >= 6) {
                i = 0;
                if (len == 8 || len == 6) {
                    i = Integer.parseInt(passwd.substring(0, len - 4));
                }

                int size = StringTools.sizeOfInt(i);
                int month = Integer.parseInt(passwd.substring(size, size + 2));
                int day = Integer.parseInt(passwd.substring(size + 2, len));
                if (i >= 1950 && i < 2050 && month >= 1 && month <= 12 && day >= 1 && day <= 31) {
                    --level;
                }
            }

            if (null != DICTIONARY && DICTIONARY.length > 0) {
                for(i = 0; i < DICTIONARY.length; ++i) {
                    if (passwd.equals(DICTIONARY[i]) || DICTIONARY[i].indexOf(passwd) >= 0) {
                        --level;
                        break;
                    }
                }
            }

            if (len <= 6) {
                --level;
                if (len <= 4) {
                    --level;
                    if (len <= 3) {
                        level = 0;
                    }
                }
            }

            if (StringTools.isCharEqual(passwd)) {
                level = 0;
            }

            if (level < 0) {
                level = 0;
            }

            return level;
        }
    }

    public static PasswordTools.LEVEL getPasswordLevel(String passwd) {
        int level = checkPasswordStrength(passwd);
        switch(level) {
            case 0:
            case 1:
            case 2:
            case 3:
                return PasswordTools.LEVEL.EASY;
            case 4:
            case 5:
            case 6:
                return PasswordTools.LEVEL.MIDIUM;
            case 7:
            case 8:
            case 9:
                return PasswordTools.LEVEL.STRONG;
            case 10:
            case 11:
            case 12:
                return PasswordTools.LEVEL.VERY_STRONG;
            default:
                return PasswordTools.LEVEL.EXTREMELY_STRONG;
        }
    }

    public static enum LEVEL {
        EASY,
        MIDIUM,
        STRONG,
        VERY_STRONG,
        EXTREMELY_STRONG;

        private LEVEL() {
        }
    }
}

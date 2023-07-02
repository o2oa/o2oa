package com.x.base.core.project.tools;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.regex.Pattern;

public class EscapeStringTools {
    public static final Charset utf8Charset = Charset.forName("UTF-8");

    protected static final String _allowableInUrl = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!#$&'()*+,-./:;=?@_~";

    protected static final String _allowableInUrlQuery = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!#$%&'()*+,-./:;=?@_~";

    private static final char _URIEscape = '%';

    static final byte blank = 32;

    static final byte plus = 43;

    static final byte hexa = 97;

    static final byte hexf = 102;

    static final byte hexA = 65;

    static final byte hexF = 70;

    static final byte hex0 = 48;

    static final byte hex9 = 57;

    static final byte ten = 10;

    private static String xescapeString(String in, String allowable, char esc, boolean spaceplus) {
        try {
            StringBuffer out = new StringBuffer();
            if (in == null)
                return null;
            byte[] utf8 = in.getBytes(utf8Charset);
            byte[] allow8 = allowable.getBytes(utf8Charset);
            for (byte b : utf8) {
                if (b == 32 && spaceplus) {
                    out.append('+');
                } else {
                    boolean found = false;
                    for (byte a : allow8) {
                        if (a == b) {
                            found = true;
                            break;
                        }
                    }
                    if (found) {
                        out.append((char)b);
                    } else {
                        String c = Integer.toHexString(b);
                        out.append(esc);
                        if (c.length() < 2)
                            out.append('0');
                        out.append(c);
                    }
                }
            }
            return out.toString();
        } catch (Exception e) {
            return in;
        }
    }

    private static String escapeString(String in, String allowable) {
        return xescapeString(in, allowable, '%', false);
    }

    private static String xunescapeString(String in, char escape, boolean spaceplus) {
        try {
            if (in == null)
                return null;
            byte[] utf8 = in.getBytes(utf8Charset);
            byte escape8 = (byte)escape;
            byte[] out = new byte[utf8.length];
            int index8 = 0;
            for (int i = 0; i < utf8.length; ) {
                byte b = utf8[i++];
                if (b == 43 && spaceplus) {
                    out[index8++] = 32;
                } else if (b == escape8) {
                    if (i + 2 <= utf8.length) {
                        b = (byte)(fromHex(utf8[i]) << 4 | fromHex(utf8[i + 1]));
                        i += 2;
                    }
                }
                out[index8++] = b;
            }
            return new String(out, 0, index8, utf8Charset);
        } catch (Exception e) {
            return in;
        }
    }

    private static String unescapeString(String in) {
        return xunescapeString(in, '%', false);
    }

    private static byte fromHex(byte b) throws NumberFormatException {
        if (b >= 48 && b <= 57)
            return (byte)(b - 48);
        if (b >= 97 && b <= 102)
            return (byte)(10 + b - 97);
        if (b >= 65 && b <= 70)
            return (byte)(10 + b - 65);
        throw new NumberFormatException("Illegal hex character: " + b);
    }

    private static final Pattern p = Pattern.compile("([\\w]+)://([.\\w]+(:[\\d]+)?)([/][^?#])?([?][^#]*)?([#].*)?");

    public static String escapeURL(String surl) {
        String protocol = null;
        String authority = null;
        String path = null;
        String query = null;
        String fragment = null;
        URL u = null;
        try {
            u = new URL(surl);
        } catch (MalformedURLException e) {
            return null;
        }
        protocol = u.getProtocol();
        authority = u.getAuthority();
        path = u.getPath();
        query = u.getQuery();
        fragment = u.getRef();
        StringBuilder url = new StringBuilder();
        url.append(protocol);
        url.append("://");
        url.append(authority);
        if (path != null && path.length() > 0) {
            String[] pieces = path.split("[/]", -1);
            for (int i = 0; i < pieces.length; i++) {
                String p = pieces[i];
                if (p == null)
                    p = "";
                if (i > 0)
                    url.append("/");
                url.append(urlEncode(p));
            }
        }
        if (query != null && query.length() > 0) {
            url.append("?");
            url.append(escapeURLQuery(query));
        }
        if (fragment != null && fragment.length() > 0) {
            url.append("#");
            url.append(urlEncode(fragment));
        }
        return url.toString();
    }

    static int nextpiece(String s, int index, String sep) {
        index = s.indexOf(sep, index);
        if (index < 0)
            index = s.length();
        return index;
    }

    public static String escapeURLQuery(String ce) {
        try {
            ce = escapeString(ce, "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!#$%&'()*+,-./:;=?@_~");
        } catch (Exception e) {
            ce = null;
        }
        return ce;
    }

    public static String unescapeURLQuery(String ce) {
        try {
            ce = unescapeString(ce);
        } catch (Exception e) {
            ce = null;
        }
        return ce;
    }

    public static String urlEncode(String s) {
        s = escapeString(s, "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!#$&'()*+,-./:;=?@_~");
        return s;
    }

    public static String urlDecode(String s) {
        try {
            s = URLDecoder.decode(s, "UTF-8");
        } catch (Exception e) {
            s = null;
        }
        return s;
    }
}

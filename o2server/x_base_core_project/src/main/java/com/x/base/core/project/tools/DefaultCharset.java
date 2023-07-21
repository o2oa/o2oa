package com.x.base.core.project.tools;

import java.nio.charset.Charset;

public class DefaultCharset {

	public static final Charset charset = Charset.forName("UTF-8");

	public static final String name = Charset.forName("UTF-8").name();

	public static final Charset charset_iso_8859_1 = Charset.forName("iso-8859-1");

	public static final String name_iso_8859_1 = Charset.forName("iso-8859-1").name();

	public static final Charset charset_utf_8 = Charset.forName("UTF-8");

	public static final String name_iso_utf_8 = Charset.forName("UTF-8").name();

	public static final Charset charset_gbk = Charset.forName("GBK");

	public static final String name_gbk = Charset.forName("GBK").name();

}

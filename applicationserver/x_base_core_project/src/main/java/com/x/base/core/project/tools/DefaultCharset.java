package com.x.base.core.project.tools;

import java.nio.charset.Charset;

import org.junit.Test;

public class DefaultCharset {

	public static final Charset charset = Charset.forName("UTF-8");

	public static final String name = Charset.forName("UTF-8").name();

	public static final Charset charset_iso_8859_1 = Charset.forName("iso-8859-1");

	public static final String name_iso_8859_1 = Charset.forName("iso-8859-1").name();

	@Test
	public void test() {
		Charset charset = Charset.forName("utf-8");
		System.out.println(charset.name());
	}
}

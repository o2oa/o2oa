package o2.base.core.project;

import java.nio.charset.Charset;

import org.junit.Test;

public class DefaultCharset {

	public static final Charset charset = Charset.forName("UTF-8");

	public static final String name = Charset.forName("UTF-8").name();

	@Test
	public void test() {
		Charset charset = Charset.forName("utf-8");
		System.out.println(charset.name());
	}
}

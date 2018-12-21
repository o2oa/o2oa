package examples.ftp;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;

public class TestClient2 {
	@Test
	public void test() {
		String prefix = "aaa";
		String uri = "/aaa/bbb/ddd";
		Pattern pattern = Pattern.compile("/" + prefix + "/(\\S+?)(/|$)");
		Matcher matcher = pattern.matcher(uri);
		if (matcher.find()) {
			System.out.println(matcher.group(1));
		} else {
			System.out.println("not match");
		}
	}

	@Test
	public void test1() {
		String prefix = "ddd";
		String uri = "/aaa/bbb/ddd";
		Pattern pattern = Pattern.compile("/" + prefix + "/(\\S+?)(/|$)");
		Matcher matcher = pattern.matcher(uri);
		if (matcher.find()) {
			System.out.println(matcher.group(1));
		} else {
			System.out.println("not match");
		}
	}
}

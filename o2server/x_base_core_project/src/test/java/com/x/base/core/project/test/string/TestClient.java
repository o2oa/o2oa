package com.x.base.core.project.test.string;

import java.util.zip.CRC32;

import org.apache.commons.text.StringEscapeUtils;
import org.junit.Test;

import com.x.base.core.project.tools.StringTools;

import junit.framework.Assert;

public class TestClient {

	@Test
	public void test() {
		CRC32 c = new CRC32();
		c.update("".getBytes());
		System.out.println(c.getValue());
	}

	@Test
	public void test4() {
		String aaa ="(var v \\u003d person.getMobile();\\u000a return v.su\\nbstring(v.length - 6);)";
		System.out.println(aaa);
		System.out.println(StringEscapeUtils.unescapeJson(aaa));
	}

	@Test
	public void test1() {
		System.out.println(StringTools.matchWildcard(null, null));
		System.out.println(StringTools.matchWildcard("HELLO WORLD", "*ELLO*"));
		Assert.assertFalse(StringTools.matchWildcard("HELLO WORLD", ""));
		Assert.assertFalse(StringTools.matchWildcard("", "HELLO WORLD"));
		Assert.assertFalse(StringTools.matchWildcard("HELLO WORLD", "ELLO"));
		Assert.assertFalse(StringTools.matchWildcard("HELLO WORLD", "HELLO"));
		Assert.assertFalse(StringTools.matchWildcard("HELLO WORLD", "*HELLO"));
		Assert.assertFalse(StringTools.matchWildcard("HELLO WORLD", "HELLO WORLD2"));
		Assert.assertFalse(StringTools.matchWildcard("HELLO WORLD", "HELLO WORL"));
		Assert.assertFalse(StringTools.matchWildcard("HELLO WORLD", "hello world"));

		Assert.assertTrue(StringTools.matchWildcard("HELLO WORLD", "*ELLO*"));
		Assert.assertTrue(StringTools.matchWildcard("HELLO WORLD", "HELLO*"));
		Assert.assertTrue(StringTools.matchWildcard("HELLO WORLD", "*LLO*"));
		Assert.assertTrue(StringTools.matchWildcard("HELLO WORLD", "HELLO WORLD"));
	}

}

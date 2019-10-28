package com.x.base.core.project.test.string;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.x.base.core.project.tools.StringTools;

import junit.framework.Assert;

public class TestClient {

	@Test
	public void test() {
		List<String> a = new ArrayList<>();
		a.add("abc");
		a.add("edf");
		a.add("a");
		a.add("bc");
		a.add("ed");
		a.add("f");
		// System.out.println(StringTools.mergeEndsWith(a));
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

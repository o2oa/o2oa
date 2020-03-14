package com.x.base.core.project.test.hanlp;

import java.io.File;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import com.hankcs.hanlp.HanLP;
import com.x.base.core.project.tools.DefaultCharset;

public class TestClient {
	@Test
	public void test() throws Exception {
		String value = FileUtils.readFileToString(new File("d:/aaa.txt"), DefaultCharset.charset_utf_8);
		List<String> list = HanLP.extractSummary(value, 10);
		System.out.println(list);
	}

//	@Test
//	public void test1() throws Exception {
//		String value = FileUtils.readFileToString(new File("d:/aaa.txt"), DefaultCharset.charset_utf_8);
//		String str = HanLP.extractKeyword(document, size)(value, 10);
//		System.out.println(str);
//	}
	
	@Test
	public void test2() throws Exception {
		String value = FileUtils.readFileToString(new File("d:/aaa.txt"), DefaultCharset.charset_utf_8);
		List<String> list = HanLP.extractKeyword(value, 10);
		System.out.println(list);
	}
	
	
	@Test
	public void test3() throws Exception {
		String value = FileUtils.readFileToString(new File("d:/aaa.txt"), DefaultCharset.charset_utf_8);
		List<String> list = HanLP.extractPhrase(value, 10);
		System.out.println(list);
	}
	
	@Test
	public void test4() throws Exception {
		String value = FileUtils.readFileToString(new File("d:/aaa.txt"), DefaultCharset.charset_utf_8);
		List<String> list = HanLP.extractSummary(value, 10);
		System.out.println(list);
	}
	
	@Test
	public void test5() throws Exception {
		String value = FileUtils.readFileToString(new File("d:/aaa.txt"), DefaultCharset.charset_utf_8);
		System.out.println( HanLP.getSummary(value, 10," "));
	}

}

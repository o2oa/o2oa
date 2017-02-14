package com.x.meeting.assemble.control.test;

import org.apache.commons.io.FilenameUtils;
import org.junit.Test;

public class TestClient {
	@Test
	public void test1() {
		System.out.println(FilenameUtils.getBaseName("D:/asdfasdf/adfasfd/mm.jpg"));
		System.out.println(FilenameUtils.getName("D:/asdfasdf/adfasfd/mm.jpg"));
		System.out.println(FilenameUtils.getBaseName("D:/asdfasdf/adfasfd/mm.jpg"));
	}
}

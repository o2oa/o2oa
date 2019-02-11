package com.x.query.assemble.designer.test;

import java.io.File;
import java.util.Collection;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.junit.Test;

public class TestClient {
	@Test
	public void test() {
		Collection<File> files = FileUtils.listFiles(new File("d:/aaa"), FileFilterUtils.suffixFileFilter(".java"),
				DirectoryFileFilter.INSTANCE);
		for (File f : files) {
			System.out.println(f.getAbsolutePath());
		}
	}
}

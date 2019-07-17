package com.x.base.core.project.test;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FalseFileFilter;
import org.apache.commons.io.filefilter.RegexFileFilter;
import org.apache.commons.lang.reflect.FieldUtils;
import org.junit.Test;

public class FileTestClient {
	@Test

	public void test1() {
		File dir = new File("e:/bbb");
		// for (File f : FileUtils.listFilesAndDirs(dir, FalseFileFilter.FALSE, new
		// RegexFileFilter(
		// "^dataDump_[1,2][0,9][0-9][0-9][0,1][0-9][0-3][0-9][0-5][0-9][0-5][0-9][0-5][0-9]$")))
		// {
		// System.out.println(f.getName());
		// }
		for (File f : FileUtils.listFilesAndDirs(dir, FalseFileFilter.FALSE, new RegexFileFilter(
				"^dataDump_[1,2][0,9][0-9][0-9][0,1][0-9][0-3][0-9][0-5][0-9][0-5][0-9][0-5][0-9]$"))) {
			System.out.println(dir == f);
			System.out.println(f.getName());
		}
	}

}

package com.x.base.core.project.test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import com.x.base.core.project.tools.JarTools;

public class JarTestClient {

	@Test
	public void test() throws Exception {
		List<File> files = new ArrayList<>();
		files.add(new File("d:/aaa"));
		files.add(new File("e:/bbb"));
		FileUtils.writeByteArrayToFile(new File("e:/11.zip"), JarTools.jar(files));
	}

}

package com.x.build.and.scratch.manifest;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import com.x.base.core.project.tools.FileTools;

public class ManifestCfgCreator {

	public static void main(String[] args) throws Exception {
		ManifestCfgCreator creator = new ManifestCfgCreator();
		creator.commonsExt();
		creator.storeJars();
		creator.store();
	}

	@Test
	public void store() throws Exception {
		File dir = new File(FileTools.parent(FileTools.parent(new File("./"))), "store");
		if ((!dir.exists()) || (!dir.isDirectory())) {
			throw new Exception("error");
		}
		List<String> names = new ArrayList<>();
		for (File o : dir.listFiles()) {
			if ((!StringUtils.equals(o.getName(), "manifest.cfg")) && (!StringUtils.equals(o.getName(), "jars"))) {
				if (StringUtils.isNotEmpty(o.getName())) {
					names.add(o.getName());
				}
			}
		}
		names = names.stream().sorted().collect(Collectors.toList());
		File file = new File(dir, "manifest.cfg");
		FileUtils.writeLines(file, names);
	}

	@Test
	public void storeJars() throws Exception {
		File dir = new File(FileTools.parent(FileTools.parent(new File("./"))), "store/jars");
		if ((!dir.exists()) || (!dir.isDirectory())) {
			throw new Exception("error");
		}
		List<String> names = new ArrayList<>();
		for (File o : dir.listFiles()) {
			if (!StringUtils.equals(o.getName(), "manifest.cfg")) {
				if (StringUtils.isNotEmpty(o.getName())) {
					names.add(o.getName());
				}
			}
		}
		names = names.stream().sorted().collect(Collectors.toList());
		File file = new File(dir, "manifest.cfg");
		FileUtils.writeLines(file, names);
	}

	@Test
	public void commonsExt() throws Exception {
		File dir = new File(FileTools.parent(FileTools.parent(new File("./"))), "commons/ext");
		if ((!dir.exists()) || (!dir.isDirectory())) {
			throw new Exception("error");
		}
		List<String> names = new ArrayList<>();
		for (File o : dir.listFiles()) {
			if (!StringUtils.equals(o.getName(), "manifest.cfg")) {
				if (StringUtils.isNotEmpty(o.getName())) {
					names.add(o.getName());
				}
			}
		}
		names = names.stream().sorted().collect(Collectors.toList());
		File file = new File(dir, "manifest.cfg");
		FileUtils.writeLines(file, names);
	}
}
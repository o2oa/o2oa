package com.x.build;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import com.x.base.core.project.annotation.Module;
import com.x.base.core.project.annotation.ModuleType;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ScanResult;

public class CopyJest {

	@Test
	public void copyDescribeJs() throws Exception {
		File root = new File(new File("").getAbsolutePath());
		File template = new File(root, "jest/describe.js");
		for (ClassInfo classInfo : this.list()) {
			File dir = new File(root.getParent(), classInfo.getSimpleName());
			File dest = new File(dir, "src/main/webapp/jest/describe.js");
			System.out.println("copy to:" + dest.getAbsolutePath());
			FileUtils.copyFile(template, dest);
		}
	}

	@Test
	public void copyJqueryJs() throws Exception {
		File root = new File(new File("").getAbsolutePath());
		File template = new File(root, "jest/jquery.min.js");
		for (ClassInfo classInfo : this.list()) {
			File dir = new File(root.getParent(), classInfo.getSimpleName());
			File dest = new File(dir, "src/main/webapp/jest/jquery.min.js");
			System.out.println("copy to:" + dest.getAbsolutePath());
			FileUtils.copyFile(template, dest);
		}
	}

	@Test
	public void copylipboardJs() throws Exception {
		File root = new File(new File("").getAbsolutePath());
		File template = new File(root, "jest/clipboard.min.js");
		for (ClassInfo classInfo : this.list()) {
			File dir = new File(root.getParent(), classInfo.getSimpleName());
			File dest = new File(dir, "src/main/webapp/jest/jquery.min.js");
			System.out.println("copy to:" + dest.getAbsolutePath());
			FileUtils.copyFile(template, dest);
		}
	}

	@Test
	public void copyIndexHtml() throws Exception {
		File root = new File(new File("").getAbsolutePath());
		File template = new File(root, "jest/index.html");
		for (ClassInfo classInfo : this.list()) {
			File dir = new File(root.getParent(), classInfo.getSimpleName());
			File dest = new File(dir, "src/main/webapp/jest/index.html");
			System.out.println("copy to:" + dest.getAbsolutePath());
			FileUtils.copyFile(template, dest);
		}
	}

	private List<ClassInfo> list() throws Exception {
		try (ScanResult scanResult = new ClassGraph().enableAllInfo().scan()) {
			List<ClassInfo> list = new ArrayList<>();
			List<ClassInfo> classInfos = scanResult.getClassesWithAnnotation(Module.class.getName());
			for (ClassInfo info : classInfos) {
				Class<?> clz = Class.forName(info.getName());
				Module module = clz.getAnnotation(Module.class);
				if (Objects.equals(module.type(), ModuleType.ASSEMBLE)
						|| Objects.equals(module.type(), ModuleType.SERVICE)) {
					list.add(info);
				}
			}
			return list;
		}
	}

	@Test
	public void test() throws Exception {
		for (ClassInfo classInfo : this.list()) {
			System.out.println(classInfo.getSimpleName());
		}

	}
}

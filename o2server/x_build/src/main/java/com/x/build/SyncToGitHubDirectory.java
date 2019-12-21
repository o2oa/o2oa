package com.x.build;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.junit.Test;

import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

public class SyncToGitHubDirectory {

	private static Logger logger = LoggerFactory.getLogger(SyncToGitHubDirectory.class);

	private static List<String> dirExcludes = new ArrayList<>();
	private static List<String> dirIncludes = new ArrayList<>();
	private static List<String> fileExcludes = new ArrayList<>();
	private static List<String> fileIncludes = new ArrayList<>();

	static {
		dirExcludes.add("*/describe");
		dirExcludes.add("*\\describe");
		dirExcludes.add("*/target");
		dirExcludes.add("*\\target");
		dirExcludes.add("*/x_build_and_scratch");
		dirExcludes.add("*\\x_build_and_scratch");
		dirExcludes.add("*/.settings");
		dirExcludes.add("*\\.settings");
	}
	static {
		dirIncludes.add("*/src");
		dirIncludes.add("*\\src");
	}
	static {
		fileExcludes.add("*.class");
		fileExcludes.add("*_.java");
		fileExcludes.add("*/.classpath");
		fileExcludes.add("*\\.classpath");
		fileExcludes.add("*/.project");
		fileExcludes.add("*\\.project");
		fileExcludes.add("*/META-INF/persistence.xml");
		fileExcludes.add("*\\META-INF\\persistence.xml");
	}
	static {
		fileIncludes.add("*.java");
		fileIncludes.add("*.json");
		fileIncludes.add("*.xml");
		fileIncludes.add("*/pom.xml");
		fileIncludes.add("*\\pom.xml");
	}

	public static void main(String... args) throws Exception {
		File source = new File(args[0]);
		File target = new File(args[1]);
		if ((!source.exists()) || (!source.isDirectory())) {
			throw new Exception("源目录为空或者不是目录.");
		}
		if ((!target.exists()) || (!target.isDirectory())) {
			throw new Exception("目标目录为空或者不是目录.");
		}
		List<File> sources = new ArrayList<>();
		for (File f : source.listFiles()) {
			if (f.isDirectory()) {
				if (FilenameUtils.wildcardMatch(f.getName(), "x_*")
						&& (!FilenameUtils.wildcardMatch(f.getName(), "x_build_and_scratch"))) {
					sources.add(f);
				}
			}
		}
		for (File s : sources) {
			File t = new File(target, s.getName());
			FileUtils.forceMkdir(t);
			logger.print("源目录: {}, 目标目录: {} 开始推送.", s.getAbsolutePath(), t.getAbsolutePath());
			SyncToGitHubDirectory sync = new SyncToGitHubDirectory();
			sync.synchronize(s, t);
		}
	}

	private void synchronize(File source, File target) throws Exception {
		int modify = 0;
		for (File f : source.listFiles()) {
			if (f.isDirectory()) {
				File dir = this.dir(f, target);
				if (dir.exists()) {
					synchronize(f, dir);
				}
			} else {
				modify += this.file(f, target);
			}
		}
		this.clean(source, target);
		logger.print("{} {} files modified.", source.getAbsolutePath(), modify);
	}

	private void clean(File source, File target) throws Exception {
		if (!this.match(target)) {
			logger.print("删除目录: {}.", target.getAbsolutePath());
			FileUtils.forceDelete(target);
		} else {
			for (File t : target.listFiles()) {
				if (t.isDirectory() && (!this.match(t))) {
					logger.print("删除目录: {}.", t.getAbsolutePath());
					FileUtils.forceDelete(t);
					continue;
				}
				if (t.isFile() && (!this.match(t))) {
					logger.print("删除文件: {}.", t.getAbsolutePath());
					FileUtils.forceDelete(t);
					continue;
				}
				File s = new File(source, t.getName());
				if ((!s.exists()) || (t.isDirectory() != s.isDirectory())) {
					FileUtils.forceDelete(t);
				}
			}
		}
	}

	private File dir(File s, File target) throws Exception {
		File t = new File(target, s.getName());
		if (this.match(s)) {
			logger.print("推送目录: {}.", s.getAbsolutePath());
			if ((!t.exists()) || (!t.isDirectory())) {
				FileUtils.forceMkdir(t);
			}
		} else {
			logger.print("跳过推送目录: {}.", s.getAbsolutePath());
		}
		return t;
	}

	private int file(File s, File target) throws Exception {
		if (this.match(s)) {
			logger.print("推送文件: {}.", s.getAbsolutePath());
			File t = new File(target, s.getName());
			if ((!t.exists()) || t.isDirectory() || (t.lastModified() != s.lastModified())) {
				FileUtils.copyFile(s, t, true);
				return 1;
			}
		} else {
			logger.print("跳过推送文件: {}.", s.getAbsolutePath());
		}
		return 0;
	}

	private boolean match(File file) {
		if (file.isDirectory()) {
			return this.dirMatch(file);
		} else {
			return this.fileMatch(file);
		}
	}

	private boolean fileMatch(File file) {
		for (String wildcard : fileExcludes) {
			if (FilenameUtils.wildcardMatchOnSystem(file.getAbsolutePath(), wildcard)) {
				return false;
			}
		}
		for (String wildcard : fileIncludes) {
			if (FilenameUtils.wildcardMatchOnSystem(file.getAbsolutePath(), wildcard)) {
				return true;
			}
		}
		return true;
	}

	private boolean dirMatch(File file) {
		for (String wildcard : dirExcludes) {
			if (FilenameUtils.wildcardMatchOnSystem(file.getAbsolutePath(), wildcard)) {
				return false;
			}
		}
		for (String wildcard : dirIncludes) {
			if (FilenameUtils.wildcardMatchOnSystem(file.getAbsolutePath(), wildcard)) {
				return true;
			}
		}
		return true;
	}

	@Test
	public void test1() {
		File file = new File("E:\\bbb\\x_attendance_assemble_control\\.project");
		System.out.println(FilenameUtils.wildcardMatchOnSystem(file.getAbsolutePath(), "*\\.project"));

	}

	@Test
	public void test2() {
		File file = new File("D:\\aaa\\2\\abc.java");
		WildcardFileFilter filter = new WildcardFileFilter("*/2/*.java");
		System.out.println(filter.accept(file));
	}
}
package com.x.common.core.application.component;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.gson.XGsonBuilder;
import com.x.base.core.utils.collection.ListTools;

import io.github.lukehutch.fastclasspathscanner.FastClasspathScanner;

public class CompileAll {
	public static void main(String[] args) throws Exception {
		String str = StringUtils.replace(args[0], "\\", "/");
		Argument arg = XGsonBuilder.instance().fromJson(str, Argument.class);
		compileAll(arg);

	}

	public static void compileAll(Argument arg) throws Exception {
		FastClasspathScanner scanner = new FastClasspathScanner("com.x");
		scanner.scan();
		if (arg.getIncludeCore()) {
			for (String str : scanner.getNamesOfSubclassesOf(Core.class)) {
				Class<?> clz = Class.forName(str);
				String name = clz.getSimpleName();
				if (ListTools.nullToEmpty(arg.getExcludes()).contains(name)) {
					System.out.println("skip name:" + name + ".");
					continue;
				}
				System.out.println("compile name:" + name + ".");
				Compile.compile(name, arg.getRootPath() + "/" + name);
			}
		}
		if (arg.getIncludeService()) {
			for (String str : scanner.getNamesOfSubclassesOf(Service.class)) {
				Class<?> clz = Class.forName(str);
				String name = clz.getSimpleName();
				if (ListTools.nullToEmpty(arg.getExcludes()).contains(name)) {
					System.out.println("skip name:" + name + ".");
					continue;
				}
				System.out.println("compile name:" + name + ".");
				Compile.compile(name, arg.getRootPath() + "/" + name);
			}
		}
		if (arg.getIncludeAssemble()) {
			for (String str : scanner.getNamesOfSubclassesOf(Assemble.class)) {
				Class<?> clz = Class.forName(str);
				String name = clz.getSimpleName();
				if (ListTools.nullToEmpty(arg.getExcludes()).contains(name)) {
					System.out.println("skip name:" + name + ".");
					continue;
				}
				System.out.println("compile name:" + name + ".");
				Compile.compile(name, arg.getRootPath() + "/" + name);
			}
		}
		if (arg.includeCenter) {
			String name = x_program_center.class.getSimpleName();
			System.out.println("compile name:" + name + ".");
			Compile.compile(name, arg.getRootPath() + "/" + name);
		}
	}

	public class Argument {

		private String rootPath;
		private Boolean includeAssemble;
		private Boolean includeCore;
		private Boolean includeService;
		private Boolean includeCenter;

		private List<String> excludes;

		public Boolean getIncludeAssemble() {
			return includeAssemble;
		}

		public void setIncludeAssemble(Boolean includeAssemble) {
			this.includeAssemble = includeAssemble;
		}

		public Boolean getIncludeCore() {
			return includeCore;
		}

		public void setIncludeCore(Boolean includeCore) {
			this.includeCore = includeCore;
		}

		public Boolean getIncludeService() {
			return includeService;
		}

		public void setIncludeService(Boolean includeService) {
			this.includeService = includeService;
		}

		public Boolean getIncludeCenter() {
			return includeCenter;
		}

		public void setIncludeCenter(Boolean includeCenter) {
			this.includeCenter = includeCenter;
		}

		public String getRootPath() {
			return rootPath;
		}

		public void setRootPath(String rootPath) {
			this.rootPath = rootPath;
		}

		public List<String> getExcludes() {
			return excludes;
		}

		public void setExcludes(List<String> excludes) {
			this.excludes = excludes;
		}

	}
}
package com.x.base.core.project;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.tools.ListTools;

import io.github.lukehutch.fastclasspathscanner.FastClasspathScanner;
import io.github.lukehutch.fastclasspathscanner.scanner.ScanResult;

public class CompileM {
	public static void main(String[] args) throws Exception {
		String str = StringUtils.replace(args[0], "\\", "/");
		Argument arg = XGsonBuilder.instance().fromJson(str, Argument.class);
		compileAll(arg);

	}

	public static void compileAll(Argument arg) throws Exception {
		ScanResult scanResult = new FastClasspathScanner(Packages.PREFIX).scan();
		if (arg.getIncludeCore()) {
			for (String str : scanResult.getNamesOfSubclassesOf(CoreC.class)) {
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
			for (String str : scanResult.getNamesOfSubclassesOf(ServiceC.class)) {
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
			for (String str : scanResult.getNamesOfSubclassesOf(AssembleC.class)) {
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
		// if (arg.includeCenter) {
		// String name = x_program_center.class.getSimpleName();
		// System.out.println("compile name:" + name + ".");
		// Compile.compile(name, arg.getRootPath() + "/" + name);
		// }
	}

	public class Argument {

		private String rootPath;
		private Boolean includeAssemble;
		private Boolean includeCore;
		private Boolean includeService;

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
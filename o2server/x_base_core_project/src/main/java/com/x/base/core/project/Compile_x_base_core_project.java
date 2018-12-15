package com.x.base.core.project;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.gson.XGsonBuilder;

public class Compile_x_base_core_project {
	public static void main(String[] args) throws Exception {

		String str = StringUtils.replace(args[0], "\\", "/");
		Argument arg = XGsonBuilder.instance().fromJson(str, Argument.class);
		Compile.compile("x_base_core_project", arg.getRootPath() + "/x_base_core_project");

	}

	public class Argument {

		private String rootPath;

		public String getRootPath() {
			return rootPath;
		}

		public void setRootPath(String rootPath) {
			this.rootPath = rootPath;
		}

	}
}
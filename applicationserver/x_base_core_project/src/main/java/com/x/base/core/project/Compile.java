package com.x.base.core.project;

import java.io.File;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;

import com.x.base.core.project.tools.MainTools;

public class Compile {
	public static void main(String[] args) throws Exception {
		Argument arg = MainTools.parseArgument(args[0], Argument.class);
		compile(arg.getName(), arg.getProjectPath());
	}

	public static void compile(String name, String projectPath) throws Exception {
		File buildFile = new File(projectPath, name + "_build.xml");
		Project antProject = new Project();
		antProject.setBasedir(projectPath);
		antProject.setUserProperty("ant.file", buildFile.getAbsolutePath());
		antProject.init();
		ProjectHelper helper = ProjectHelper.getProjectHelper();
		antProject.addReference("ant.projectHelper", helper);
		helper.parse(antProject, buildFile);
		antProject.executeTarget(antProject.getDefaultTarget());
	}

	public class Argument {
		private String name;
		private String projectPath;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getProjectPath() {
			return projectPath;
		}

		public void setProjectPath(String projectPath) {
			this.projectPath = projectPath;
		}
	}
}

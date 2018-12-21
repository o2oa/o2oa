package com.x.base.core.project;

import java.io.File;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;

public abstract class Compilable {

	public static final String VERSION = "4.0.0";

	public void compile(String projectPath, String name) throws Exception {
		File buildFile = new File(projectPath, name + "_build.xml");
		Project p = new Project();
		p.setUserProperty("ant.file", buildFile.getAbsolutePath());
		p.init();
		ProjectHelper helper = ProjectHelper.getProjectHelper();
		p.addReference("ant.projectHelper", helper);
		helper.parse(p, buildFile);
		p.executeTarget(p.getDefaultTarget());
	}
}

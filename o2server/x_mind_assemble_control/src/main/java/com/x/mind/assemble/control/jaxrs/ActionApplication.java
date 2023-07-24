package com.x.mind.assemble.control.jaxrs;

import java.util.Set;

import javax.ws.rs.ApplicationPath;

import com.x.base.core.project.jaxrs.AbstractActionApplication;
import com.x.mind.assemble.control.jaxrs.folder.MindFolderInfoAction;
import com.x.mind.assemble.control.jaxrs.mind.MindInfoAction;

@ApplicationPath("jaxrs")
public class ActionApplication extends AbstractActionApplication {

	public Set<Class<?>> getClasses() {
		this.classes.add(MindFolderInfoAction.class);
		this.classes.add(MindInfoAction.class);
		return this.classes;
	}

}
package com.x.hotpic.assemble.control.jaxrs;

import java.util.Set;

import javax.ws.rs.ApplicationPath;

import com.x.base.core.project.jaxrs.AbstractActionApplication;
import com.x.hotpic.assemble.control.jaxrs.hotpic.HotPictureInfoAction;

@ApplicationPath("jaxrs")
public class ActionApplication extends AbstractActionApplication {

	public Set<Class<?>> getClasses() {
		this.classes.add(HotPictureInfoAction.class);
		return this.classes;
	}

}
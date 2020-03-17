package com.x.hotpic.assemble.control.jaxrs;

import com.x.base.core.project.jaxrs.AbstractActionApplication;
import com.x.hotpic.assemble.control.jaxrs.hotpic.HotPictureInfoAction;
import com.x.hotpic.assemble.control.jaxrs.hotpic.HotPictureInfoCipherAction;

import javax.ws.rs.ApplicationPath;
import java.util.Set;

@ApplicationPath("jaxrs")
public class ActionApplication extends AbstractActionApplication {

	public Set<Class<?>> getClasses() {
		this.classes.add(HotPictureInfoAction.class);
		this.classes.add(HotPictureInfoCipherAction.class);
		return this.classes;
	}

}
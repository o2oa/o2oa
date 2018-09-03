package com.x.cms.assemble.search.jaxrs;

import java.util.Set;

import javax.ws.rs.ApplicationPath;

import com.x.base.core.project.jaxrs.AbstractActionApplication;
import com.x.cms.assemble.search.jaxrs.search.CMS_SearcjAction;
import com.x.cms.assemble.search.jaxrs.spider.CMS_SpiderAction;

@ApplicationPath("jaxrs")
public class ActionApplication extends AbstractActionApplication {

	public Set<Class<?>> getClasses() {

		this.classes.add(CMS_SpiderAction.class);
		this.classes.add(CMS_SearcjAction.class);

		return this.classes;
	}

}
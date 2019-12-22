package com.x.processplatform.service.processing.jaxrs.test;

import org.eclipse.jetty.http.MimeTypes;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;

class ActionTest1 extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			System.out.println(this.getClass().getClassLoader());
			System.out.println(Thread.currentThread().getContextClassLoader());
			System.out.println(MimeTypes.class.getClassLoader());
			System.out.println(org.glassfish.jersey.server.ApplicationHandler.class.getClassLoader());
			System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
			ClassLoader cl = this.getClass().getClassLoader();
			while (null != cl) {
				System.out.println("!!!!!!!!!:" + cl);
				cl = cl.getParent();
			}
			return result;
		}
	}

	public static class Wo extends WrapBoolean {

	}

}
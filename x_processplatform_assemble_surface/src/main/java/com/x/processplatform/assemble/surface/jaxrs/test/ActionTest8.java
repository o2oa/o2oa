package com.x.processplatform.assemble.surface.jaxrs.test;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.http.ActionResult;

class ActionTest8 extends ActionBase {
	ActionResult<Object> execute() throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Object> result = new ActionResult<>();
			String str = StringUtils.repeat("test", 1000);
			result.setData(str);
			return result;
		}
	}
}

package com.x.processplatform.assemble.surface.jaxrs.application;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.http.ActionResult;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.wrapout.element.WrapOutApplication;
import com.x.processplatform.core.entity.element.Application;

class ActionGetIcon extends ActionBase {

	ActionResult<WrapOutApplication> execute(String flag) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<WrapOutApplication> result = new ActionResult<>();
			Business business = new Business(emc);
			Application application = business.application().pick(flag);
			/** 如果application 不存在,返回空值 */
			WrapOutApplication wrap = new WrapOutApplication();
			if (null != application) {
				wrap.setIcon(application.getIcon());
			}
			result.setData(wrap);
			return result;
		}
	}

}

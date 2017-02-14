package com.x.processplatform.assemble.surface.jaxrs.applicationdict;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.exception.ExceptionWhen;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.WrapOutId;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.core.entity.element.Application;
import com.x.processplatform.core.entity.element.ApplicationDict;

class ActionDeleteDataPath7 extends ActionBase {

	ActionResult<WrapOutId> execute(String applicationDictFlag, String applicationFlag, String path0, String path1,
			String path2, String path3, String path4, String path5, String path6, String path7) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<WrapOutId> result = new ActionResult<>();
			Business business = new Business(emc);
			ApplicationDict applicationDict = business.applicationDict().pick(applicationDictFlag,
					ExceptionWhen.not_found);
			Application application = business.application().pick(applicationFlag, ExceptionWhen.not_found);
			if (!StringUtils.equals(application.getId(), applicationDict.getApplication())) {
				throw new Exception("applicationDict{flag:" + applicationDictFlag + "} not in application{flag:"
						+ applicationFlag + "}.");
			}
			this.delete(business, applicationDict, path0, path1, path2, path3, path4, path5, path6, path7);
			emc.commit();
			WrapOutId wrap = new WrapOutId(applicationDict.getId());
			result.setData(wrap);
			return result;
		}
	}
}
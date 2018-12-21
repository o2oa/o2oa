package com.x.cms.assemble.control.jaxrs.appdict;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.tools.SortTools;
import com.x.cms.assemble.control.Business;
import com.x.cms.core.entity.AppInfo;
import com.x.cms.core.entity.element.AppDict;

class ActionListWithAppInfo extends BaseAction {

	ActionResult<List<WrapOutAppDict>> execute(String appInfoFlag) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			ActionResult<List<WrapOutAppDict>> result = new ActionResult<>();
			List<WrapOutAppDict> wraps = new ArrayList<>();
			AppInfo appInfo = business.getAppInfoFactory().pick(appInfoFlag);
			if (null == appInfo) {
				throw new ExceptionAppInfoNotExist(appInfoFlag);
			}
			List<String> ids = business.getAppDictFactory().listWithAppInfo(appInfo.getId());
			wraps = copier.copy(emc.list(AppDict.class, ids));
			SortTools.asc(wraps, false, "name");
			result.setData(wraps);
			return result;
		}
	}
}

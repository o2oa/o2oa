package com.x.cms.assemble.control.jaxrs.appdict;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.cms.assemble.control.Business;
import com.x.cms.core.entity.AppInfo;
import com.x.cms.core.entity.element.AppDict;

class ActionGetDataPath1 extends BaseAction {

	ActionResult<JsonElement> execute(String appDictFlag, String appInfoFlag, String path0, String path1)
			throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			ActionResult<JsonElement> result = new ActionResult<>();
			AppInfo appInfo = business.getAppInfoFactory().pick(appInfoFlag);
			if (null == appInfo) {
				throw new ExceptionAppInfoNotExist(appInfoFlag);
			}
			String id = business.getAppDictFactory().getWithAppInfoWithUniqueName(appInfo.getId(),
					appDictFlag);
			if (StringUtils.isEmpty(id)) {
				throw new ExceptionAppDictNotExist(appInfoFlag);
			}
			AppDict dict = emc.find(id, AppDict.class);
			JsonElement wrap = this.get(business, dict, path0, path1);
			result.setData(wrap);
			return result;
		}
	}
}
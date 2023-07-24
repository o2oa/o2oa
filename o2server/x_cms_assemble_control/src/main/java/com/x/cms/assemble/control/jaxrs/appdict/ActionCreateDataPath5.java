package com.x.cms.assemble.control.jaxrs.appdict;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.WrapOutId;
import com.x.cms.assemble.control.Business;
import com.x.cms.core.entity.AppInfo;
import com.x.cms.core.entity.element.AppDict;

class ActionCreateDataPath5 extends BaseAction {

	ActionResult<WrapOutId> execute(String appDictFlag, String appInfoFlag, String path0, String path1,
			String path2, String path3, String path4, String path5, JsonElement jsonElement) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<WrapOutId> result = new ActionResult<>();
			Business business = new Business(emc);
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
			this.create(business, dict, jsonElement, path0, path1, path2, path3, path4, path5);
			emc.commit();
			WrapOutId wrap = new WrapOutId(dict.getId());
			result.setData(wrap);
			return result;
		}
	}
}
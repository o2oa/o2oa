package com.x.cms.assemble.control.jaxrs.appdict;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.cms.assemble.control.Business;
import com.x.cms.core.entity.AppInfo;
import com.x.cms.core.entity.element.AppDict;

class ActionGet extends BaseAction {

	ActionResult<WrapOutAppDict> execute(EffectivePerson effectivePerson, String appDictFlag,
			String appInfoFlag) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<WrapOutAppDict> result = new ActionResult<>();
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
			WrapOutAppDict wrap = copier.copy(dict);
			wrap.setData(this.get(business, dict));
			result.setData(wrap);
			return result;
		}
	}

}

package com.x.cms.assemble.control.jaxrs.appdict;

import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.http.EffectivePerson;
import java.util.ArrayList;
import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.tools.SortTools;
import com.x.cms.assemble.control.Business;
import com.x.cms.core.entity.AppInfo;
import com.x.cms.core.entity.element.AppDict;
import org.apache.commons.lang3.BooleanUtils;

class ActionListWithAppInfo extends BaseAction {

	ActionResult<List<WrapOutAppDict>> execute(EffectivePerson effectivePerson, String appInfoFlag) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			ActionResult<List<WrapOutAppDict>> result = new ActionResult<>();
			List<WrapOutAppDict> wraps = new ArrayList<>();
			AppInfo appInfo = business.getAppInfoFactory().pick(appInfoFlag);
			if (null == appInfo) {
				throw new ExceptionAppInfoNotExist(appInfoFlag);
			}
			if( effectivePerson.isAnonymous() && BooleanUtils.isNotTrue(appInfo.getAllowAnonymousAccessDoc())) {
				throw new ExceptionAccessDenied(effectivePerson);
			}
			List<String> ids = business.getAppDictFactory().listWithAppInfo(appInfo.getId());
			wraps = copier.copy(emc.list(AppDict.class, ids));
			SortTools.asc(wraps, false, "name");
			result.setData(wraps);
			return result;
		}
	}
}

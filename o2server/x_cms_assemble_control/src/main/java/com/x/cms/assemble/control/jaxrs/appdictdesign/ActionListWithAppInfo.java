package com.x.cms.assemble.control.jaxrs.appdictdesign;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.tools.SortTools;
import com.x.cms.assemble.control.Business;
import com.x.cms.core.entity.AppInfo;
import com.x.cms.core.entity.element.AppDict;

class ActionListWithAppInfo extends BaseAction {

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, String appInfoId) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<List<Wo>> result = new ActionResult<>();
			List<Wo> wos = new ArrayList<>();
			Business business = new Business(emc);
			AppInfo appInfo = emc.find(appInfoId, AppInfo.class);
			if (null == appInfo) {
				throw new ExceptionAppInfoNotExist(appInfoId);
			}
			if (!business.editable(effectivePerson, appInfo)) {
				throw new ExceptionAppInfoAccessDenied(effectivePerson.getDistinguishedName(),
						appInfo.getAppName(), appInfo.getId());
			}
			List<String> ids = business.getAppDictFactory().listWithAppInfo(appInfoId);
			wos = Wo.copier.copy(emc.list(AppDict.class, ids));
			SortTools.asc( wos, "name" );
			result.setData(wos);
			return result;
		}
	}

	public static class Wo extends AppDict {

		private static final long serialVersionUID = -939143099553857829L;

		static WrapCopier<AppDict, Wo> copier = WrapCopierFactory.wo(AppDict.class, Wo.class, null,
				JpaObject.FieldsInvisible);

	}

}
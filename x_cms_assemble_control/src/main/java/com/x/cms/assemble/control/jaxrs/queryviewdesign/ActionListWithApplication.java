package com.x.cms.assemble.control.jaxrs.queryviewdesign;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.utils.SortTools;
import com.x.cms.assemble.control.Business;
import com.x.cms.core.entity.element.QueryView;

class ActionListWithApplication extends ActionBase {
	ActionResult<List<WrapOutQueryView>> execute(EffectivePerson effectivePerson, String appId )
			throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<List<WrapOutQueryView>> result = new ActionResult<>();
			List<WrapOutQueryView> wraps = new ArrayList<>();
			Business business = new Business(emc);
			//AppInfo appInfo = emc.find( appId, AppInfo.class, ExceptionWhen.not_found);
			//business.applicationEditAvailable(effectivePerson, application, ExceptionWhen.not_allow);
			List<String> ids = business.queryViewFactory().listWithAppId( appId );
			List<QueryView> os = emc.list(QueryView.class, ids);
			wraps = outCopier.copy(os);
			SortTools.asc(wraps, "name");
			result.setData(wraps);
			return result;
		}
	}
}

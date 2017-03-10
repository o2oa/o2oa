package com.x.processplatform.assemble.designer.jaxrs.application;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.processplatform.assemble.designer.Business;
import com.x.processplatform.assemble.designer.wrapout.WrapOutApplicationSummary;
import com.x.processplatform.core.entity.element.Application;

class ActionListSummary extends ActionBase {

	ActionResult<List<WrapOutApplicationSummary>> execute(EffectivePerson effectivePerson) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<List<WrapOutApplicationSummary>> result = new ActionResult<>();
			Business business = new Business(emc);
			List<WrapOutApplicationSummary> wraps = new ArrayList<>();
			List<String> ids = business.application().listWithPerson(effectivePerson);
			/* 由于有多值字段所以需要全部取出 */
			for (Application o : emc.list(Application.class, ids)) {
				WrapOutApplicationSummary wrap = summaryOutCopier.copy(o);
				wrap.setProcessList(this.wrapOutProcessWithApplication(business, o.getId()));
				wrap.setFormList(this.wrapOutFormWithApplication(business, o.getId()));
				wraps.add(wrap);
			}
			this.sortWrapOutApplicationSummary(wraps);
			result.setData(wraps);
			return result;
		}
	}

}
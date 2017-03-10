package com.x.processplatform.assemble.designer.jaxrs.applicationcategory;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.utils.SortTools;
import com.x.processplatform.assemble.designer.Business;
import com.x.processplatform.assemble.designer.wrapout.WrapOutApplicationCategory;

class ActionList extends ActionBase {

	ActionResult<List<WrapOutApplicationCategory>> execute(EffectivePerson effectivePerson) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<List<WrapOutApplicationCategory>> result = new ActionResult<>();
			List<WrapOutApplicationCategory> wraps = new ArrayList<>();
			Business business = new Business(emc);
			for (String str : business.application().listApplicationCategoryWithPerson(effectivePerson)) {
				WrapOutApplicationCategory wrap = new WrapOutApplicationCategory();
				wrap.setApplicationCategory(str);
				wrap.setCount(business.application().countWithPersonWithApplicationCategory(effectivePerson, str));
				wraps.add(wrap);
			}
			SortTools.asc(wraps, "applicationCategory");
			result.setData(wraps);
			return result;
		}
	}

}
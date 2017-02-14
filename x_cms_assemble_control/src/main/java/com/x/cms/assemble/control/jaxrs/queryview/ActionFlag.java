package com.x.cms.assemble.control.jaxrs.queryview;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.cms.assemble.control.Business;
import com.x.cms.core.entity.element.QueryView;


public class ActionFlag extends ActionBase {

	public ActionResult<WrapOutQueryView> execute(EffectivePerson effectivePerson, String flag) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<WrapOutQueryView> result = new ActionResult<>();
			Business business = new Business(emc);
			QueryView o = business.queryViewFactory().pick(flag);
			if (!business.queryViewFactory().allowRead(effectivePerson, o)) {
				throw new Exception("insufficient permissions");
			}
			WrapOutQueryView wrap = outCopier.copy(o);
			result.setData(wrap);
			return result;
		}
	}

}
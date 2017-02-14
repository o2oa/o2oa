package com.x.processplatform.assemble.surface.jaxrs.querystat;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.exception.ExceptionWhen;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.wrapout.element.WrapOutQueryStat;
import com.x.processplatform.core.entity.element.Application;
import com.x.processplatform.core.entity.element.QueryStat;

public class ActionFlag extends ActionBase {

	public ActionResult<WrapOutQueryStat> execute(EffectivePerson effectivePerson, String flag, String applicationFlag)
			throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<WrapOutQueryStat> result = new ActionResult<>();
			Business business = new Business(emc);
			Application application = business.application().pick(applicationFlag, ExceptionWhen.not_found);
			QueryStat o = business.queryStat().pick(flag, application, ExceptionWhen.not_found);
			if (!business.queryStat().allowRead(effectivePerson, o, application)) {
				throw new Exception("insufficient permissions");
			}
			WrapOutQueryStat wrap = outCopier.copy(o);
			result.setData(wrap);
			return result;
		}
	}

}
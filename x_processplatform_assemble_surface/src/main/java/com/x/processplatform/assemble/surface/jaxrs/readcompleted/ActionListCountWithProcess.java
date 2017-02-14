package com.x.processplatform.assemble.surface.jaxrs.readcompleted;

import java.util.List;
import java.util.Objects;

import com.x.base.core.bean.NameValueCountPair;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.exception.ExceptionWhen;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.core.entity.element.Application;

public class ActionListCountWithProcess extends ActionBase {

	ActionResult<List<NameValueCountPair>> execute(EffectivePerson effectivePerson, String applicationFlag)
			throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<List<NameValueCountPair>> result = new ActionResult<>();
			Business business = new Business(emc);
			Application application = business.application().pick(applicationFlag, ExceptionWhen.not_found);
			List<NameValueCountPair> wraps = listProcessPair(business, effectivePerson, application);
			for (NameValueCountPair o : wraps) {
				o.setCount(countWithProcess(business, effectivePerson, Objects.toString(o.getValue())));
			}
			result.setData(wraps);
			return result;
		}
	}

}
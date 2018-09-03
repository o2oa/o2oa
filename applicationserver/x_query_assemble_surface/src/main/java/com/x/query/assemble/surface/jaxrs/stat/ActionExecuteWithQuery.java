package com.x.query.assemble.surface.jaxrs.stat;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.query.assemble.surface.Business;
import com.x.query.core.entity.Query;
import com.x.query.core.entity.Stat;
import com.x.query.core.entity.View;
import com.x.query.core.entity.plan.Plan;
import com.x.query.core.entity.plan.Runtime;

class ActionExecuteWithQuery extends BaseAction {

	ActionResult<Plan> execute(EffectivePerson effectivePerson, String flag, String queryFlag, JsonElement jsonElement)
			throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Plan> result = new ActionResult<>();
			Business business = new Business(emc);
			Query query = business.pick(queryFlag, Query.class);
			if (null == query) {
				throw new ExceptionEntityNotExist(queryFlag, Query.class);
			}
			String id = business.stat().getWithQuery(flag, query);
			Stat stat = business.pick(id, Stat.class);
			if (null == stat) {
				throw new ExceptionEntityNotExist(flag, Stat.class);
			}
			if (!business.readable(effectivePerson, query)) {
				throw new ExceptionAccessDenied(effectivePerson, query);
			}
			if (!business.readable(effectivePerson, stat)) {
				throw new ExceptionAccessDenied(effectivePerson, stat);
			}
			/* 不需要校验view的权限 */
			View view = business.pick(stat.getView(), View.class);
			if (null == view) {
				throw new ExceptionEntityNotExist(stat.getView(), View.class);
			}
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			/** 有可能前台不传任何参数 */
			if (null == wi) {
				wi = new Wi();
			}
			this.append(effectivePerson, business, wi);
			Plan plan = this.accessPlan(business, stat, view, wi);
			result.setData(plan);
			return result;
		}
	}

	public static class Wi extends Runtime {

	}

}
package com.x.query.assemble.surface.jaxrs.stat;

import java.util.List;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.query.assemble.surface.Business;
import com.x.query.assemble.surface.ThisApplication;
import com.x.query.core.entity.Query;
import com.x.query.core.entity.Stat;
import com.x.query.core.express.plan.Calculate;
import com.x.query.core.express.plan.Runtime;
import com.x.query.core.express.plan.StatPlan;

class ActionExecuteWithQuery extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionExecuteWithQuery.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String flag, String queryFlag, JsonElement jsonElement)
			throws Exception {

		LOGGER.debug("execute:{}, flag:{}, queryFlag:{}.", effectivePerson::getDistinguishedName, () -> flag,
				() -> queryFlag);

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
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
			if (!business.readable(effectivePerson, stat)) {
				throw new ExceptionAccessDenied(effectivePerson, stat);
			}
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			// 有可能前台不传任何参数
			if (null == wi) {
				wi = new Wi();
			}
			this.append(effectivePerson, business, wi);

			StatPlan statPlan = new StatPlan(emc, stat, wi, ThisApplication.forkJoinPool());
			statPlan.access();
			Wo wo = new Wo();
			wo.setCalculate(statPlan.getCalculate());
			wo.setCalculateGrid(statPlan.getCalculateGrid());
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends GsonPropertyObject {

		private Calculate calculate;

		private List<?> calculateGrid;

		public Calculate getCalculate() {
			return calculate;
		}

		public void setCalculate(Calculate calculate) {
			this.calculate = calculate;
		}

		public List<?> getCalculateGrid() {
			return calculateGrid;
		}

		public void setCalculateGrid(List<?> calculateGrid) {
			this.calculateGrid = calculateGrid;
		}

	}

	public static class Wi extends Runtime {
	}
}

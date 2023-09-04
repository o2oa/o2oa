package com.x.query.assemble.designer.jaxrs.stat;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.query.assemble.designer.Business;
import com.x.query.assemble.designer.ThisApplication;
import com.x.query.core.entity.Stat;
import com.x.query.core.express.plan.Calculate;
import com.x.query.core.express.plan.Runtime;
import com.x.query.core.express.plan.StatPlan;

class ActionSimulate extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionSimulate.class);

	public ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, JsonElement jsonElement)
			throws Exception {

		LOGGER.debug("execute:{}, id:{}.", effectivePerson::getDistinguishedName, () -> id);

		// 前台通过wrapIn将Query的可选择部分FilterEntryList和WhereEntry进行输入
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			Business business = new Business(emc);
			/** 有可能前台不传任何参数 */
			if (null == wi) {
				wi = new Wi();
			}
			Stat stat = emc.find(id, Stat.class);
			if (null == stat) {
				throw new ExceptionEntityNotExist(id, Stat.class);
			}
			wi.person = effectivePerson.getDistinguishedName();
			wi.identityList = business.organization().identity().listWithPerson(wi.person);
			wi.unitList = business.organization().unit().listWithPerson(wi.person);
			wi.unitAllList = business.organization().unit().listWithPersonSupNested(wi.person);
			wi.groupList = business.organization().group().listWithPerson(wi.person);
			wi.roleList = business.organization().role().listWithPerson(wi.person);

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
		private List<?> calculateGrid = new ArrayList<>();

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

		private static final long serialVersionUID = 2480045406876255382L;
	}
}
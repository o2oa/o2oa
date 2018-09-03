package com.x.query.assemble.designer.jaxrs.stat;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.query.assemble.designer.Business;
import com.x.query.core.entity.Stat;
import com.x.query.core.entity.View;
import com.x.query.core.entity.plan.Calculate;
import com.x.query.core.entity.plan.CmsPlan;
import com.x.query.core.entity.plan.Plan;
import com.x.query.core.entity.plan.ProcessPlatformPlan;
import com.x.query.core.entity.plan.Runtime;

class ActionSimulate2 extends BaseAction {

	public ActionResult<Plan> execute(EffectivePerson effectivePerson, String id, JsonElement jsonElement)
			throws Exception {
		/* 前台通过wrapIn将Query的可选择部分FilterEntryList和WhereEntry进行输入 */
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			WiRuntime wi = this.convertToWrapIn(jsonElement, WiRuntime.class);
			Business business = new Business(emc);
			/** 有可能前台不传任何参数 */
			if (null == wi) {
				wi = new WiRuntime();
			}
			Stat stat = emc.find(id, Stat.class);
			if (null == stat) {
				throw new ExceptionStatNotExist(id);
			}
			View view = emc.find(stat.getView(), View.class);
			if (null == view) {
				throw new ExceptionViewNotExist(stat.getView());
			}
			ActionResult<Plan> result = new ActionResult<>();
			wi.person = effectivePerson.getDistinguishedName();
			wi.identityList = business.organization().identity().listWithPerson(wi.person);
			wi.unitList = business.organization().unit().listWithPerson(wi.person);
			wi.unitAllList = business.organization().unit().listWithPersonSupNested(wi.person);
			wi.groupList = business.organization().group().listWithPerson(wi.person);
			wi.roleList = business.organization().role().listWithPerson(wi.person);

			WiData wiData = gson.fromJson(stat.getData(), WiData.class);
			Calculate calculate = wiData.getCalculate();
			switch (StringUtils.trimToEmpty(view.getType())) {
			case View.TYPE_CMS:
				CmsPlan cmsPlan = gson.fromJson(view.getData(), CmsPlan.class);
				cmsPlan.runtime = wi;
				cmsPlan.calculate = calculate;
				cmsPlan.access();
				result.setData(cmsPlan);
				break;
			case View.TYPE_PROCESSPLATFORM:
				ProcessPlatformPlan processPlatformPlan = gson.fromJson(view.getData(), ProcessPlatformPlan.class);
				processPlatformPlan.runtime = wi;
				processPlatformPlan.calculate = calculate;
				processPlatformPlan.access();
				result.setData(processPlatformPlan);
				break;
			default:
				break;
			}
			return result;
		}
	}

	public static class WiData extends GsonPropertyObject {

		private Calculate calculate;

		public Calculate getCalculate() {
			return calculate;
		}

		public void setCalculate(Calculate calculate) {
			this.calculate = calculate;
		}
	}

	public static class WiRuntime extends Runtime {

	}

}
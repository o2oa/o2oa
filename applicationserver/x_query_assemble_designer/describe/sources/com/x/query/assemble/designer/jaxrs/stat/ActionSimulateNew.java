package com.x.query.assemble.designer.jaxrs.stat;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.query.assemble.designer.Business;
import com.x.query.core.entity.Stat;
import com.x.query.core.entity.View;
import com.x.query.core.entity.plan.Calculate;
import com.x.query.core.entity.plan.CmsPlan;
import com.x.query.core.entity.plan.Plan;
import com.x.query.core.entity.plan.ProcessPlatformPlan;
import com.x.query.core.entity.plan.Runtime;

class ActionSimulateNew extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionSimulate.class);

	public ActionResult<Plan> execute(EffectivePerson effectivePerson, String id, JsonElement jsonElement)
			throws Exception {
		/* 前台通过wrapIn将Query的可选择部分FilterEntryList和WhereEntry进行输入 */
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			Business business = new Business(emc);
			/** 有可能前台不传任何参数 */
			if (null == wi) {
				wi = new Wi();
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
			Wi runtime = XGsonBuilder.convert(wi, Wi.class);
			WiData wiData = gson.fromJson(stat.getData(), WiData.class);
			Calculate calculate = wiData.getCalculate();
			List<CompletableFuture<Plan>> futures = new ArrayList<>();
			calculate.calculateList.stream().forEach(o -> {
				CompletableFuture<Plan> future = CompletableFuture.supplyAsync(() -> {
					try {
						switch (StringUtils.trimToEmpty(view.getType())) {
						case View.TYPE_CMS:
							CmsPlan cmsPlan = gson.fromJson(view.getData(), CmsPlan.class);
							cmsPlan.runtime = runtime;
							cmsPlan.calculate = calculate;
							cmsPlan.access();
							return cmsPlan;
						case View.TYPE_PROCESSPLATFORM:
							ProcessPlatformPlan processPlatformPlan = gson.fromJson(view.getData(),
									ProcessPlatformPlan.class);
							processPlatformPlan.runtime = runtime;
							processPlatformPlan.calculate = calculate;
							processPlatformPlan.access();
							return processPlatformPlan;
						default:
							return null;
						}
					} catch (Exception e) {
						logger.error(e);
					}
					return null;
				});
				futures.add(future);
			});
			List<Plan> plans = new ArrayList<>();
			futures.stream().forEach(o -> {
				try {
					Plan plan = o.get(300, TimeUnit.SECONDS);
					plans.add(plan);
				} catch (Exception e) {
					logger.error(e);
				}
			});
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

	public static class Wi extends Runtime {

	}

	public static class Wo extends GsonPropertyObject {

	}

}
package com.x.query.assemble.surface.jaxrs.stat;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.tools.StringTools;
import com.x.query.assemble.surface.Business;
import com.x.query.core.entity.Stat;
import com.x.query.core.entity.View;
import com.x.query.core.entity.plan.Calculate;
import com.x.query.core.entity.plan.CmsPlan;
import com.x.query.core.entity.plan.Plan;
import com.x.query.core.entity.plan.ProcessPlatformPlan;
import com.x.query.core.entity.plan.Runtime;

import net.sf.ehcache.Element;

abstract class BaseAction extends StandardJaxrsAction {

	protected Plan accessPlan(Business business, Stat stat, View view, Runtime runtime) throws Exception {
		Plan plan = null;
		if (BooleanUtils.isTrue(view.getCacheAccess())) {
			String cacheKey = ApplicationCache.concreteCacheKey(this.getClass(), stat.getId(), stat.getQuery(),
					StringTools.sha(gson.toJson(runtime)));
			Element element = business.cache().get(cacheKey);
			if ((null != element) && (null != element.getObjectValue())) {
				plan = (Plan) element.getObjectValue();
			} else {
				plan = this.getPlan(stat, view, runtime);
				business.cache().put(new Element(cacheKey, plan));
			}
		} else {
			plan = this.getPlan(stat, view, runtime);
		}
		return plan;
	}

	private Plan getPlan(Stat stat, View view, Runtime runtime) throws Exception {
		WiData wiData = gson.fromJson(stat.getData(), WiData.class);
		Calculate calculate = wiData.getCalculate();
		Plan plan = null;
		switch (StringUtils.trimToEmpty(view.getType())) {
		case View.TYPE_CMS:
			CmsPlan cmsPlan = gson.fromJson(view.getData(), CmsPlan.class);
			cmsPlan.runtime = runtime;
			cmsPlan.calculate = calculate;
			cmsPlan.access();
			plan = cmsPlan;
		default:
			ProcessPlatformPlan processPlatformPlan = gson.fromJson(view.getData(), ProcessPlatformPlan.class);
			processPlatformPlan.runtime = runtime;
			processPlatformPlan.calculate = calculate;
			processPlatformPlan.access();
			plan = processPlatformPlan;
		}
		plan.afterCalculateGridScriptText = null;
		plan.afterGridScriptText = null;
		plan.afterGroupGridScriptText = null;
		return plan;
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

	protected <T extends Runtime> void append(EffectivePerson effectivePerson, Business business, T t)
			throws Exception {
		t.person = effectivePerson.getDistinguishedName();
		t.identityList = business.organization().identity().listWithPerson(effectivePerson);
		t.unitList = business.organization().unit().listWithPerson(effectivePerson);
		t.unitAllList = business.organization().unit().listWithPersonSupNested(effectivePerson);
		t.groupList = business.organization().group().listWithPerson(effectivePerson.getDistinguishedName());
		t.roleList = business.organization().role().listWithPerson(effectivePerson);
	}
}

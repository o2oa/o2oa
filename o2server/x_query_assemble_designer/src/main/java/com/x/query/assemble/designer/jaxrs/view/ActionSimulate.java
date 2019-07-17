package com.x.query.assemble.designer.jaxrs.view;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.list.TreeList;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.query.assemble.designer.Business;
import com.x.query.core.entity.View;
import com.x.query.core.express.plan.CmsPlan;
import com.x.query.core.express.plan.FilterEntry;
import com.x.query.core.express.plan.Plan;
import com.x.query.core.express.plan.ProcessPlatformPlan;
import com.x.query.core.express.plan.Runtime;

class ActionSimulate extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionSimulate.class);

	ActionResult<Plan> execute(EffectivePerson effectivePerson, String id, JsonElement jsonElement) throws Exception {
		logger.debug("receive:{}.", jsonElement);
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Plan> result = new ActionResult<>();
			Business business = new Business(emc);
			View view = emc.find(id, View.class);
			if (null == view) {
				throw new ExceptionViewNotExist(id);
			}
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			if (wi == null) {
				wi = new Wi();
			}
			Runtime runtime = new Runtime();
			runtime.person = effectivePerson.getDistinguishedName();
			runtime.identityList = business.organization().identity()
					.listWithPerson(effectivePerson.getDistinguishedName());
			runtime.unitList = business.organization().unit().listWithPerson(effectivePerson.getDistinguishedName());
			runtime.unitAllList = business.organization().unit()
					.listWithPersonSupNested(effectivePerson.getDistinguishedName());
			runtime.groupList = business.organization().group().listWithPerson(effectivePerson.getDistinguishedName());
			runtime.roleList = business.organization().role().listWithPerson(effectivePerson.getDistinguishedName());
			runtime.parameter = wi.getParameter();
			runtime.filterList = wi.getFilterList();
			runtime.bundleList = wi.getBundleList();
			runtime.count = this.getCount(view, wi.getCount());
			switch (StringUtils.trimToEmpty(view.getType())) {

			case View.TYPE_CMS:
				CmsPlan cmsPlan = gson.fromJson(view.getData(), CmsPlan.class);
				cmsPlan.runtime = runtime;
				cmsPlan.access();
				result.setData(cmsPlan);
				break;

			case View.TYPE_PROCESSPLATFORM:
				ProcessPlatformPlan processPlatformPlan = gson.fromJson(view.getData(), ProcessPlatformPlan.class);
				processPlatformPlan.runtime = runtime;
				processPlatformPlan.access();
				result.setData(processPlatformPlan);
				break;

			default:
				break;
			}
			return result;
		}
	}

	public static class Wi extends GsonPropertyObject {
		@FieldDescribe("过滤")
		private List<FilterEntry> filterList = new TreeList<>();

		@FieldDescribe("参数")
		private Map<String, String> parameter = new HashMap<>();

		@FieldDescribe("数量")
		private Integer count = 0;

		@FieldDescribe("限定结果集")
		public List<String> bundleList = new TreeList<>();

		public List<FilterEntry> getFilterList() {
			return filterList;
		}

		public void setFilterList(List<FilterEntry> filterList) {
			this.filterList = filterList;
		}

		public Map<String, String> getParameter() {
			return parameter;
		}

		public void setParameter(Map<String, String> parameter) {
			this.parameter = parameter;
		}

		public void setCount(Integer count) {
			this.count = count;
		}

		public Integer getCount() {
			return count;
		}

		public List<String> getBundleList() {
			return bundleList;
		}

		public void setBundleList(List<String> bundleList) {
			this.bundleList = bundleList;
		}
	}

}
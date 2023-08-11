package com.x.query.assemble.surface.jaxrs.view;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.annotation.FieldTypeDescribe;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.tools.ListTools;
import com.x.base.core.project.tools.MD5Tool;
import com.x.query.assemble.surface.Business;
import com.x.query.assemble.surface.ThisApplication;
import com.x.query.core.entity.Query;
import com.x.query.core.entity.View;
import com.x.query.core.express.plan.FilterEntry;
import com.x.query.core.express.plan.Plan;
import com.x.query.core.express.plan.Runtime;
import org.apache.commons.collections4.list.TreeList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

class ActionExecuteWithQuery extends BaseAction {

	ActionResult<Plan> execute(EffectivePerson effectivePerson, String flag, String queryFlag, JsonElement jsonElement)
			throws Exception {
		ActionResult<Plan> result = new ActionResult<>();
		View view;
		Runtime runtime;
		Business business;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			Query query = business.pick(queryFlag, Query.class);
			if (null == query) {
				throw new ExceptionEntityNotExist(queryFlag, Query.class);
			}
			String id = business.view().getWithQuery(flag, query);
			view = business.pick(id, View.class);
			if (null == view) {
				throw new ExceptionEntityNotExist(flag, View.class);
			}
			if (!business.readable(effectivePerson, view)) {
				throw new ExceptionAccessDenied(effectivePerson, view);
			}
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			if (null == wi) {
				wi = new Wi();
			}
			if (ListTools.isNotEmpty(wi.getBundleList())) {
				String curKey = MD5Tool.getMD5Str(effectivePerson.getDistinguishedName() + Config.token().getCipher());
				if (!curKey.equals(wi.key)) {
					throw new ExceptionAccessDenied(effectivePerson.getDistinguishedName());
				}
			}
			runtime = this.runtime(effectivePerson, business, view, wi.getFilterList(), wi.getParameter(),
					wi.getCount(), false);
			runtime.bundleList = wi.getBundleList();
		}
		Plan plan = this.accessPlan(business, view, runtime, ThisApplication.forkJoinPool());
		result.setData(plan);
		return result;
	}

	public static class Wo extends WoId {

	}

	public static class Wi extends GsonPropertyObject {
		@FieldDescribe("过滤")
		@FieldTypeDescribe(fieldType = "class", fieldTypeName = "com.x.query.core.express.plan.FilterEntry", fieldValue = "{value='',otherValue='',path='',formatType='',logic='',comparison=''}", fieldSample = "{'logic':'逻辑运算:and|or','path':'data数据的路径:$work.title','comparison':'比较运算符:equals|notEquals|like|notLike|greaterThan|greaterThanOrEqualTo|lessThan|lessThanOrEqualTo|range','value':'7月','formatType':'textValue|numberValue|dateTimeValue|booleanValue'}")
		private List<FilterEntry> filterList = new TreeList<>();

		@FieldDescribe("参数")
		private Map<String, String> parameter = new HashMap<>();

		@FieldDescribe("数量")
		private Integer count = 0;

		@FieldDescribe("限定结果集")
		public List<String> bundleList = new TreeList<>();

		@FieldDescribe("秘钥串，结果集不为空时必须传.")
		private String key;

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

		public Integer getCount() {
			return count;
		}

		public void setCount(Integer count) {
			this.count = count;
		}

		public List<String> getBundleList() {
			return bundleList;
		}

		public void setBundleList(List<String> bundleList) {
			this.bundleList = bundleList;
		}

		public String getKey() {
			return key;
		}

		public void setKey(String key) {
			this.key = key;
		}
	}

}

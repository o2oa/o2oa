package com.x.query.assemble.surface.jaxrs.view;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.annotation.FieldTypeDescribe;
import com.x.base.core.project.bean.tuple.Pair;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.base.core.project.tools.MD5Tool;
import com.x.query.assemble.surface.Business;
import com.x.query.assemble.surface.ThisApplication;
import com.x.query.core.entity.Query;
import com.x.query.core.entity.View;
import com.x.query.core.express.plan.FilterEntry;
import com.x.query.core.express.plan.Plan;
import com.x.query.core.express.plan.Runtime;
import com.x.query.core.express.plan.SelectEntry;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.collections4.list.TreeList;
import org.apache.commons.lang3.StringUtils;

class ActionExecuteV2 extends BaseAction {

	private static final Logger logger = LoggerFactory.getLogger(ActionExecuteV2.class);

	ActionResult<Plan> execute(EffectivePerson effectivePerson, String id, Integer page, Integer size, JsonElement jsonElement) throws Exception {
		logger.debug("jsonElement:{}.", jsonElement);
		ActionResult<Plan> result = new ActionResult<>();
		page = (page == null || page < 1) ? 1 : page;
		Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
		View view;
		Runtime runtime;
		Business business;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			view = business.pick(id, View.class);
			if (null == view) {
				throw new ExceptionEntityNotExist(id, View.class);
			}
			Query query = business.pick(view.getQuery(), Query.class);
			if (null == query) {
				throw new ExceptionEntityNotExist(view.getQuery(), Query.class);
			}
			if (!business.readable(effectivePerson, view)) {
				throw new ExceptionAccessDenied(effectivePerson, view);
			}
			runtime = this.runtime(effectivePerson, business, view, wi.getFilterList(), wi.getOrderList(), wi.getParameter(),
					size, false);
			runtime.page = page;
			runtime.hasBundle = true;
		}
		Pair<List<String>, Long> pair;
		if(StringUtils.isBlank(wi.getSearchKey())) {
			pair = this.fetchBundleV2(view, runtime,
					ThisApplication.forkJoinPool());
		}else{
			ExecuteV2Search search = new ExecuteV2Search();
			pair = search.search(view, runtime, wi.getSearchKey(), page, size);
		}
		runtime.bundleList = pair.first();
		Plan plan = this.accessPlan(business, view, runtime, ThisApplication.forkJoinPool());
		result.setData(plan);
		result.setCount(pair.second());
		return result;
	}

	public static class Wi extends GsonPropertyObject {

		@FieldDescribe("前端指定排序列")
		@FieldTypeDescribe(fieldType = "class", fieldTypeName = "SelectEntry", fieldValue = "{\"orderType\": \"\",\"column\": \"\",\"displayName\": \"\",\"path\": \"\"}")
		private List<SelectEntry> orderList = new TreeList<>();

		@FieldDescribe("过滤")
		@FieldTypeDescribe(fieldType = "class", fieldTypeName = "FilterEntry", fieldValue = "{value='',otherValue='',path='',formatType='',logic='',comparison=''}", fieldSample = "{'logic':'逻辑运算:and|or','path':'data数据的路径:$work.title','comparison':'比较运算符:equals|notEquals|like|notLike|greaterThan|greaterThanOrEqualTo|lessThan|lessThanOrEqualTo|range','value':'7月','formatType':'textValue|numberValue|dateTimeValue|booleanValue'}")
		private List<FilterEntry> filterList = new TreeList<>();

		@FieldDescribe("参数")
		private Map<String, String> parameter = new HashMap<>();

		@FieldDescribe("模糊查询内容.")
		private String searchKey;

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

		public String getSearchKey() {
			return searchKey;
		}

		public void setSearchKey(String searchKey) {
			this.searchKey = searchKey;
		}

		public List<SelectEntry> getOrderList() {
			return orderList;
		}

		public void setOrderList(List<SelectEntry> orderList) {
			this.orderList = orderList;
		}

	}

}

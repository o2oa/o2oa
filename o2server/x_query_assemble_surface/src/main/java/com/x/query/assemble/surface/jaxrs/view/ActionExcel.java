package com.x.query.assemble.surface.jaxrs.view;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.list.TreeList;

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

class ActionExcel extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionExcel.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, JsonElement jsonElement) throws Exception {

		LOGGER.debug("execute:{}, id:{}, jsonElement:{}.", effectivePerson::getDistinguishedName, () -> id,
				() -> jsonElement);

		ActionResult<Wo> result = new ActionResult<>();
		Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
		if (ListTools.isNotEmpty(wi.getBundleList())) {
			String curKey = MD5Tool.getMD5Str(effectivePerson.getDistinguishedName() + Config.token().getCipher());
			if (!curKey.equals(wi.key)) {
				throw new ExceptionAccessDenied(effectivePerson.getDistinguishedName());
			}
		}
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
			runtime = this.runtime(effectivePerson, business, view, wi.getFilterList(), wi.getParameter(),
					wi.getCount(), true);
			runtime.bundleList = wi.getBundleList();
		}
		Plan plan = this.accessPlan(business, view, runtime, ThisApplication.forkJoinPool());
		String excelFlag = this.writeExcel(effectivePerson, business, plan, view, wi.getExcelName());
		Wo wo = new Wo();
		wo.setId(excelFlag);
		result.setData(wo);
		return result;
	}

	public static class Wo extends WoId {

		private static final long serialVersionUID = -3109718021928716742L;

	}

	public static class Wi extends GsonPropertyObject {

		private static final long serialVersionUID = 137649837114991882L;

		@FieldDescribe("过滤")
		@FieldTypeDescribe(fieldType = "class", fieldTypeName = "com.x.query.core.express.plan.FilterEntry", fieldValue = "{title='',value='',otherValue='',path='',formatType='',logic='',comparison=''}")
		private List<FilterEntry> filterList = new TreeList<>();

		@FieldDescribe("参数")
		private Map<String, String> parameter = new HashMap<>();

		@FieldDescribe("数量")
		private Integer count = 0;

		@FieldDescribe("excel导出名称，默认为视图名称")
		private String excelName;

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

		public String getExcelName() {
			return excelName;
		}

		public void setExcelName(String excelName) {
			this.excelName = excelName;
		}

		public String getKey() {
			return key;
		}

		public void setKey(String key) {
			this.key = key;
		}
	}

}

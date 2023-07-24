package com.x.program.center.jaxrs.module;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.connection.ActionResponse;
import com.x.base.core.project.connection.ConnectionAction;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.program.center.WrapModule;

class ActionList extends BaseAction {

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			if (null == wi) {
				wi = new Wi();
			}
			ActionResult<List<Wo>> result = new ActionResult<>();
			Req req = new Req();
			req.setName(Config.collect().getName());
			req.setPassword(Config.collect().getPassword());
			req.setCategoryList(wi.getCategoryList());
			String url = Config.collect().url("/o2_collect_assemble/jaxrs/module/list");
			ActionResponse ar = ConnectionAction.post(url, null, req);
			List<Wo> wos = ar.getDataAsList(Wo.class);
			result.setData(wos);
			return result;
		}
	}

	public static class Req extends GsonPropertyObject {

		private String name;
		private String password;
		private List<String> categoryList = new ArrayList<>();

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getPassword() {
			return password;
		}

		public void setPassword(String password) {
			this.password = password;
		}

		public List<String> getCategoryList() {
			return categoryList;
		}

		public void setCategoryList(List<String> categoryList) {
			this.categoryList = categoryList;
		}
	}

	public static class Wi extends GsonPropertyObject {

		@FieldDescribe("分类")
		private List<String> categoryList = new ArrayList<>();

		public List<String> getCategoryList() {
			return categoryList;
		}

		public void setCategoryList(List<String> categoryList) {
			this.categoryList = categoryList;
		}

	}

	public static class Wo extends GsonPropertyObject {

		@FieldDescribe("分类")
		private String category;

		@FieldDescribe("模块")
		private List<WrapModule> moduleList;

		public String getCategory() {
			return category;
		}

		public void setCategory(String category) {
			this.category = category;
		}

		public List<WrapModule> getModuleList() {
			return moduleList;
		}

		public void setModuleList(List<WrapModule> moduleList) {
			this.moduleList = moduleList;
		}
	}

}
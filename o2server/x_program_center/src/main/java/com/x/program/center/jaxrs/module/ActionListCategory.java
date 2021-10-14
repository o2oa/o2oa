package com.x.program.center.jaxrs.module;

import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.connection.ActionResponse;
import com.x.base.core.project.connection.ConnectionAction;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;

class ActionListCategory extends BaseAction {

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<List<Wo>> result = new ActionResult<>();
			Req req = new Req();
			req.setName(Config.collect().getName());
			req.setPassword(Config.collect().getPassword());
			String url = Config.collect().url("/o2_collect_assemble/jaxrs/module/list/category");
			ActionResponse ar = ConnectionAction.post(url, null, req);
			List<Wo> wos = ar.getDataAsList(Wo.class);
			result.setData(wos);
			return result;
		}
	}

	public static class Req extends GsonPropertyObject {

		private String name;
		private String password;

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

	}

	public static class Wo extends GsonPropertyObject {

		@FieldDescribe("分类")
		private String category;

		@FieldDescribe("数量")
		private Long count;

		public String getCategory() {
			return category;
		}

		public void setCategory(String category) {
			this.category = category;
		}

		public Long getCount() {
			return count;
		}

		public void setCount(Long count) {
			this.count = count;
		}

	}

}
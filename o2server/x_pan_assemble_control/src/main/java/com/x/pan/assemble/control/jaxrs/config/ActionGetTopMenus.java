package com.x.pan.assemble.control.jaxrs.config;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.pan.assemble.control.Business;

import java.util.List;

class ActionGetTopMenus extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			Wo wo = new Wo();
			wo.setPanMenuList(business.getSystemConfig().getMenuList());
			wo.setPeronFileEnable(business.getSystemConfig().getProperties().getPeronFileEnable());
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends GsonPropertyObject {

		@FieldDescribe("网盘顶部菜单菜单，默认：个人文件、企业文件")
		private List<String> panMenuList;

		@FieldDescribe("是否展现个人文件")
		private Boolean peronFileEnable = true;

		public List<String> getPanMenuList() {
			return panMenuList;
		}

		public void setPanMenuList(List<String> panMenuList) {
			this.panMenuList = panMenuList;
		}

		public Boolean getPeronFileEnable() {
			return peronFileEnable;
		}

		public void setPeronFileEnable(Boolean peronFileEnable) {
			this.peronFileEnable = peronFileEnable;
		}
	}
}

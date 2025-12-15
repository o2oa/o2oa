package com.x.pan.assemble.control.jaxrs.config;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.jaxrs.WrapString;
import com.x.base.core.project.tools.ListTools;
import com.x.pan.assemble.control.Business;
import com.x.pan.core.entity.FileConfig3;
import org.apache.commons.lang3.StringUtils;

class ActionIsEnableOfficePreview extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			Wo wo = new Wo();
			wo.setValue(business.getSystemConfig().getOfficePreviewTools());
			wo.setOpenOfficeEdit(business.getSystemConfig().getOfficeOpenOfficeEdit());
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends GsonPropertyObject {
		public Wo() {

		}
		public Wo(String value,Boolean isOpenOfficeEdit) throws Exception {
			this.value = value;
			this.isOpenOfficeEdit = isOpenOfficeEdit;
		}

		@FieldDescribe("预览工具")
		private String value;

		@FieldDescribe("打开Office时是否直接进入编辑状态(true表示进入编辑状态|false表示进入只读状态，默认为false)")
		private Boolean isOpenOfficeEdit;

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}

		public Boolean getOpenOfficeEdit() {
			return isOpenOfficeEdit;
		}

		public void setOpenOfficeEdit(Boolean openOfficeEdit) {
			isOpenOfficeEdit = openOfficeEdit;
		}
	}
}

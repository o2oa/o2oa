package com.x.processplatform.assemble.designer.jaxrs.templateform;

import java.util.List;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.organization.OrganizationDefinition;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.assemble.designer.Business;
import com.x.processplatform.assemble.designer.Control;
import com.x.processplatform.core.entity.element.TemplateForm;

class ActionListWithCategory extends BaseAction {

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			ActionResult<List<Wo>> result = new ActionResult<>();
			Business business = new Business(emc);
			List<String> ids = business.templateForm().listWithCategory(wi.getCategory());
			List<TemplateForm> os = emc.list(TemplateForm.class, ids);
			List<Wo> wos = Wo.copier.copy(os);
			wos = business.templateForm().sort(wos);
			Control control = new Control();
			/** 添加管理员和流程管理员删除的权限 */
			if (effectivePerson.isManager() || business.organization().person().hasRole(effectivePerson,
					OrganizationDefinition.ProcessPlatformManager)) {
				control.setAllowDelete(true);
			} else {
				control.setAllowDelete(false);
			}
			for (Wo o : wos) {
				o.setControl(control);
			}
			result.setData(wos);
			return result;
		}
	}

	public static class Wo extends TemplateForm {

		private static final long serialVersionUID = 2475165883507548650L;

		@FieldDescribe("权限")
		private Control control;

		static WrapCopier<TemplateForm, Wo> copier = WrapCopierFactory.wo(TemplateForm.class, Wo.class, null,
				ListTools.toList(JpaObject.FieldsInvisible, "data", "mobileData"));

		public Control getControl() {
			return control;
		}

		public void setControl(Control control) {
			this.control = control;
		}

	}

	public static class Wi extends TemplateForm {

		private static final long serialVersionUID = 2091352200751493447L;

	}

}
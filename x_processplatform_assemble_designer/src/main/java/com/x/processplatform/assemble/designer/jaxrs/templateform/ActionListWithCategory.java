package com.x.processplatform.assemble.designer.jaxrs.templateform;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.role.RoleDefinition;
import com.x.base.core.utils.SortTools;
import com.x.processplatform.assemble.designer.Business;
import com.x.processplatform.assemble.designer.Control;
import com.x.processplatform.assemble.designer.wrapin.WrapInTemplateForm;
import com.x.processplatform.assemble.designer.wrapout.WrapOutTemplateFormSimple;
import com.x.processplatform.core.entity.element.TemplateForm;

class ActionListWithCategory extends ActionBase {

	ActionResult<List<WrapOutTemplateFormSimple>> execute(EffectivePerson effectivePerson, JsonElement jsonElement)
			throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			WrapInTemplateForm wrapIn = this.convertToWrapIn(jsonElement, WrapInTemplateForm.class);
			ActionResult<List<WrapOutTemplateFormSimple>> result = new ActionResult<>();
			List<WrapOutTemplateFormSimple> wraps = new ArrayList<>();
			Business business = new Business(emc);
			List<String> ids = business.templateForm().listWithCategory(wrapIn.getCategory());
			List<TemplateForm> os = emc.list(TemplateForm.class, ids);
			wraps = simpleOutCopier.copy(os);
			SortTools.asc(wraps, "name");
			Control control = new Control();
			/** 添加管理员和流程管理员删除的权限 */
			if (effectivePerson.isManager() || business.organization().role().hasAny(effectivePerson.getName(),
					RoleDefinition.ProcessPlatformManager)) {
				control.setAllowDelete(true);
			} else {
				control.setAllowDelete(false);
			}
			for (WrapOutTemplateFormSimple o : wraps) {
				o.setControl(control);
			}
			result.setData(wraps);
			return result;
		}
	}

}
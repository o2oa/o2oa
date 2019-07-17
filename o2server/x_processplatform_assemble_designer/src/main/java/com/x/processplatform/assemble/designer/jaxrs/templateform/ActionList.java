package com.x.processplatform.assemble.designer.jaxrs.templateform;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.organization.OrganizationDefinition;
import com.x.base.core.project.tools.SortTools;
import com.x.processplatform.assemble.designer.Business;
import com.x.processplatform.assemble.designer.Control;
import com.x.processplatform.core.entity.element.TemplateForm;

class ActionList extends BaseAction {

	ActionResult<Map<String, List<Wo>>> execute(EffectivePerson effectivePerson) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Map<String, List<Wo>>> result = new ActionResult<>();
			List<Wo> wos = new ArrayList<>();
			Business business = new Business(emc);
			List<String> ids = business.templateForm().list();
			List<TemplateForm> os = emc.list(TemplateForm.class, ids);
			wos = Wo.copier.copy(os);
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
			SortTools.asc(wos, "name");
			Map<String, List<Wo>> group = wos.stream()
					.collect(Collectors.groupingBy(e -> Objects.toString(e.getCategory(), "")));
			LinkedHashMap<String, List<Wo>> sort = group.entrySet().stream()
					.sorted(Map.Entry.<String, List<Wo>>comparingByKey()).collect(Collectors.toMap(Map.Entry::getKey,
							Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
			result.setData(sort);
			return result;
		}
	}

	public static class Wo extends TemplateForm {

		private static final long serialVersionUID = 1551592776065130757L;

		static WrapCopier<TemplateForm, Wo> copier = WrapCopierFactory.wo(TemplateForm.class, Wo.class,
				JpaObject.singularAttributeField(TemplateForm.class, true, true), JpaObject.FieldsInvisible);

		private Control control;

		public Control getControl() {
			return control;
		}

		public void setControl(Control control) {
			this.control = control;
		}

	}

}

package com.x.processplatform.assemble.designer.jaxrs.form;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.assemble.designer.Business;
import com.x.processplatform.core.entity.element.Application;
import com.x.processplatform.core.entity.element.Form;

class ActionListWithApplication extends BaseAction {
	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, String applicationId) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<List<Wo>> result = new ActionResult<>();
			List<Wo> wos = new ArrayList<>();
			Business business = new Business(emc);
			Application application = emc.find(applicationId, Application.class);
			if (null == application) {
				throw new ExceptionApplicationNotExist(applicationId);
			}
			if (!business.editable(effectivePerson, application)) {
				throw new ExceptionApplicationAccessDenied(effectivePerson.getDistinguishedName(),
						application.getName(), application.getId());
			}
			List<String> ids = business.form().listWithApplication(applicationId);
			// List<Form> os = emc.list(Form.class, ids);
			wos = emc.fetch(ids, Wo.copier);
			// wos = Wo.copier.copy(os);
			wos = business.form().sort(wos);
			result.setData(wos);
			return result;
		}
	}

	public static class Wo extends Form {

		private static final long serialVersionUID = -7495725325510376323L;

		public static WrapCopier<Form, Wo> copier = WrapCopierFactory.wo(Form.class, Wo.class, null,
				ListTools.toList(JpaObject.FieldsInvisible, Form.data_FIELDNAME, Form.mobileData_FIELDNAME));

	}
}

package com.x.cms.assemble.control.jaxrs.formversion;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.cms.assemble.control.Business;
import com.x.cms.core.entity.AppInfo;
import com.x.cms.core.entity.element.Form;
import com.x.cms.core.entity.element.FormVersion;

class ActionGet extends BaseAction {
	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			FormVersion formVersion = emc.find(id, FormVersion.class);
			if (null == formVersion) {
				throw new ExceptionEntityNotExist(id, FormVersion.class);
			}
			Form form = emc.find(formVersion.getForm(), Form.class);
			if (null == form) {
				throw new ExceptionEntityNotExist(formVersion.getForm(), Form.class);
			}
			AppInfo application = emc.find(form.getAppId(), AppInfo.class);
			if (null == application) {
				throw new ExceptionEntityNotExist(form.getAppId(), AppInfo.class);
			}
			if (!business.editable(effectivePerson, application)) {
				throw new ExceptionAccessDenied(effectivePerson);
			}
			Wo wo = Wo.copier.copy(formVersion);
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends FormVersion {

		private static final long serialVersionUID = 4652773027479345516L;
		static WrapCopier<FormVersion, Wo> copier = WrapCopierFactory.wo(FormVersion.class, Wo.class, null,
				JpaObject.FieldsInvisible);

	}

}

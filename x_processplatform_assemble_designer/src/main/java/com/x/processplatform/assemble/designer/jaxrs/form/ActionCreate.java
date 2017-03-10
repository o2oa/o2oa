package com.x.processplatform.assemble.designer.jaxrs.form;

import java.util.Date;
import java.util.List;

import com.google.gson.JsonElement;
import com.x.base.core.cache.ApplicationCache;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.exception.ExceptionWhen;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.WrapOutId;
import com.x.processplatform.assemble.designer.Business;
import com.x.processplatform.assemble.designer.wrapin.WrapInForm;
import com.x.processplatform.core.entity.element.Application;
import com.x.processplatform.core.entity.element.Form;
import com.x.processplatform.core.entity.element.FormField;

class ActionCreate extends ActionBase {
	ActionResult<WrapOutId> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<WrapOutId> result = new ActionResult<>();
			WrapInForm wrapIn = this.convertToWrapIn(jsonElement, WrapInForm.class);
			Business business = new Business(emc);
			Application application = emc.find(wrapIn.getApplication(), Application.class, ExceptionWhen.not_found);
			business.applicationEditAvailable(effectivePerson, application, ExceptionWhen.not_allow);
			emc.beginTransaction(Form.class);
			Form form = new Form();
			inCopier.copy(wrapIn, form);
			/** 设置form 的Id由前端生成提供 */
			form.setId(wrapIn.getId());
			form.setApplication(application.getId());
			form.setLastUpdatePerson(effectivePerson.getName());
			form.setLastUpdateTime(new Date());
			emc.persist(form, CheckPersistType.all);
			emc.commit();
			List<FormField> formFields = formFieldInCopier.copy(wrapIn.getFormFieldList());
			emc.beginTransaction(FormField.class);
			for (FormField o : formFields) {
				o.setApplication(application.getId());
				o.setForm(form.getId());
				emc.persist(o, CheckPersistType.all);
			}
			emc.commit();
			ApplicationCache.notify(Form.class);
			WrapOutId wrap = new WrapOutId(form.getId());
			result.setData(wrap);
			return result;
		}
	}
}

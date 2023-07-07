package com.x.processplatform.assemble.surface.jaxrs.serialnumber;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.core.entity.content.SerialNumber;
import com.x.processplatform.core.entity.element.Application;

class ActionUpdate extends BaseAction {
	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, JsonElement jsonElement) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			SerialNumber o = emc.find(id, SerialNumber.class);
			if (null == o) {
				throw new ExceptionSerialNumberNotExist(id);
			}
			Application application = business.application().pick(o.getApplication());
			if (null == application) {
				throw new ExceptionApplicationNotExist(o.getApplication());
			}
			if (!business.ifPersonCanManageApplicationOrProcess(effectivePerson, application, null)) {
				throw new ExceptionAccessDenied(effectivePerson);
			}
			emc.beginTransaction(SerialNumber.class);
			Wi.copier.copy(wi, o);
			emc.check(o, CheckPersistType.all);
			emc.commit();
			Wo wo = new Wo();
			wo.setId(o.getId());
			result.setData(wo);
		}
		return result;
	}

	public static class Wi extends SerialNumber {

		private static final long serialVersionUID = -3037178659768770946L;
		static WrapCopier<Wi, SerialNumber> copier = WrapCopierFactory.wi(Wi.class, SerialNumber.class, null,
				ListTools.toList(JpaObject.FieldsUnmodify, SerialNumber.name_FIELDNAME));
	}

	public static class Wo extends WoId {

		private static final long serialVersionUID = 4452600520934700209L;

	}
}

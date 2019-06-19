package com.x.processplatform.assemble.designer.jaxrs.application;

import java.util.Arrays;
import java.util.Date;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.processplatform.assemble.designer.Business;
import com.x.processplatform.assemble.designer.MessageFactory;
import com.x.processplatform.core.entity.element.Application;

class ActionEdit extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, JsonElement jsonElement) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			Business business = new Business(emc);
			emc.beginTransaction(Application.class);
			Application application = emc.find(id, Application.class);
			if (null == application) {
				throw new ExceptionApplicationNotExist(id);
			}
			if (!business.editable(effectivePerson, application)) {
				throw new ExceptionInsufficientPermission(effectivePerson.getDistinguishedName());
			}
			Wi.copier.copy(wi, application);
			application.setLastUpdatePerson(effectivePerson.getDistinguishedName());
			application.setLastUpdateTime(new Date());
			emc.commit();
			ApplicationCache.notify(Application.class);
			Wo wo = new Wo();
			wo.setId(application.getId());
			result.setData(wo);
			MessageFactory.application_update(application);
			return result;
		}
	}

	public static class Wo extends WoId {

	}

	public static class Wi extends Application {

		private static final long serialVersionUID = 6624639107781167248L;

		static WrapCopier<Wi, Application> copier = WrapCopierFactory.wi(Wi.class, Application.class, null,
				Arrays.asList(JpaObject.createTime_FIELDNAME, JpaObject.updateTime_FIELDNAME,
						JpaObject.sequence_FIELDNAME, JpaObject.distributeFactor_FIELDNAME));

	}

}
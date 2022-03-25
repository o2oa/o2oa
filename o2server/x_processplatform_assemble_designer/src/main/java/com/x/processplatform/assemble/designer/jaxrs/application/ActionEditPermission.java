package com.x.processplatform.assemble.designer.jaxrs.application;

import java.util.Date;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.assemble.designer.Business;
import com.x.processplatform.assemble.designer.MessageFactory;
import com.x.processplatform.core.entity.element.Application;

class ActionEditPermission extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, JsonElement jsonElement) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			Business business = new Business(emc);
			Application application = emc.find(id, Application.class);
			if (null == application) {
				throw new ExceptionApplicationNotExist(id);
			}
			if (!effectivePerson.isSecurityManager() && !business.editable(effectivePerson, application)) {
				throw new ExceptionInsufficientPermission(effectivePerson.getDistinguishedName());
			}
			emc.beginTransaction(Application.class);
			Wi.copier.copy(wi, application);
			application.setLastUpdatePerson(effectivePerson.getDistinguishedName());
			application.setLastUpdateTime(new Date());
			emc.commit();
			CacheManager.notify(Application.class);
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

		private static final long serialVersionUID = -8060085012685391963L;

		static WrapCopier<Wi, Application> copier = WrapCopierFactory.wi(Wi.class, Application.class,
				ListTools.toList(Application.controllerList_FIELDNAME, Application.availableIdentityList_FIELDNAME,
						Application.availableUnitList_FIELDNAME, Application.availableGroupList_FIELDNAME), null);

	}

}

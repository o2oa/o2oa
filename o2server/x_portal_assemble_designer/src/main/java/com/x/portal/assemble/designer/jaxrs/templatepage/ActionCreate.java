package com.x.portal.assemble.designer.jaxrs.templatepage;

import java.util.Date;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.organization.OrganizationDefinition;
import com.x.portal.assemble.designer.Business;
import com.x.portal.core.entity.TemplatePage;

class ActionCreate extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionCreate.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {

		LOGGER.debug("execute:{}.", effectivePerson::getDistinguishedName);

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			ActionResult<Wo> result = new ActionResult<>();
			if ((!effectivePerson.isManager()) && (!business.organization().person().hasRole(effectivePerson,
					OrganizationDefinition.PortalManager))) {
				throw new InsufficientPermissionException(effectivePerson.getDistinguishedName());
			}
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			TemplatePage o = Wi.copier.copy(wi);
			o.setCreatorPerson(effectivePerson.getDistinguishedName());
			o.setLastUpdatePerson(effectivePerson.getDistinguishedName());
			o.setLastUpdateTime(new Date());
			this.checkName(business, o);
			this.checkAlias(business, o);
			emc.beginTransaction(TemplatePage.class);
			emc.persist(o, CheckPersistType.all);
			emc.commit();
			CacheManager.notify(TemplatePage.class);
			Wo wo = new Wo();
			wo.setId(o.getId());
			result.setData(wo);
			return result;
		}
	}

	public static class Wi extends TemplatePage {

		private static final long serialVersionUID = 6337516993246513262L;

		static WrapCopier<Wi, TemplatePage> copier = WrapCopierFactory.wi(Wi.class, TemplatePage.class, null,
				JpaObject.FieldsUnmodifyExcludeId);

	}

	public static class Wo extends WoId {

		private static final long serialVersionUID = -6897451837979913782L;

	}

}
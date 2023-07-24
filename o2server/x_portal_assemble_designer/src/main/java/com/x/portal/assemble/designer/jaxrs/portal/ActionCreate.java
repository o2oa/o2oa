package com.x.portal.assemble.designer.jaxrs.portal;

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
import com.x.portal.core.entity.Portal;

class ActionCreate extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionCreate.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {

		LOGGER.debug("execute:{}.", effectivePerson::getDistinguishedName);

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			ActionResult<Wo> result = new ActionResult<>();
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			if ((!effectivePerson.isManager()) && (!business.organization().person().hasRole(effectivePerson,
					OrganizationDefinition.PortalManager, OrganizationDefinition.PortalCreator))) {
				throw new InsufficientPermissionException(effectivePerson.getDistinguishedName());
			}
			emc.beginTransaction(Portal.class);
			Portal portal = Wi.copier.copy(wi);
			portal.setCreatorPerson(effectivePerson.getDistinguishedName());
			portal.setLastUpdatePerson(effectivePerson.getDistinguishedName());
			portal.setLastUpdateTime(new Date());
			this.checkName(business, portal);
			this.checkAlias(business, portal);
			emc.persist(portal, CheckPersistType.all);
			emc.commit();
			CacheManager.notify(Portal.class);
			Wo wo = new Wo();
			wo.setId(portal.getId());
			result.setData(wo);
			return result;
		}
	}

	public static class Wi extends Portal {

		private static final long serialVersionUID = 6624639107781167248L;

		static WrapCopier<Wi, Portal> copier = WrapCopierFactory.wi(Wi.class, Portal.class, null,
				JpaObject.FieldsUnmodifyIncludePorpertiesExcludeId);

	}

	public static class Wo extends WoId {

		private static final long serialVersionUID = -8762393731283285476L;

	}

}

package com.x.query.assemble.designer.jaxrs.query;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;

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
import com.x.base.core.project.tools.ListTools;
import com.x.query.assemble.designer.Business;
import com.x.query.core.entity.Query;

class ActionCreate extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionCreate.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {

		LOGGER.debug("execute:{}.", effectivePerson::getDistinguishedName);

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			Business business = new Business(emc);
			if ((!effectivePerson.isManager()) && (!business.organization().person().hasRole(effectivePerson,
					OrganizationDefinition.QueryManager, OrganizationDefinition.QueryCreator))) {
				throw new ExceptionAccessDenied(effectivePerson.getDistinguishedName());
			}
			emc.beginTransaction(Query.class);
			Query query = new Query();
			Wi.copier.copy(wi, query);
			if (!this.idleId(business, query)) {
				throw new ExceptionIdExist(query.getId());
			}
			if (!this.idleName(business, query)) {
				throw new ExceptionNameExist(query.getName());
			}
			if (StringUtils.isNotEmpty(query.getAlias()) && (!this.idleAlias(business, query))) {
				throw new ExceptionAliasExist(query.getAlias());
			}
			query.setCreatorPerson(effectivePerson.getDistinguishedName());
			query.setLastUpdatePerson(effectivePerson.getDistinguishedName());
			query.setLastUpdateTime(new Date());
			emc.persist(query, CheckPersistType.all);
			emc.commit();
			CacheManager.notify(Query.class);
			Wo wo = new Wo();
			wo.setId(query.getId());
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends WoId {

		private static final long serialVersionUID = -4122753056759317023L;
	}

	public static class Wi extends Query {

		private static final long serialVersionUID = -5237741099036357033L;

		static WrapCopier<Wi, Query> copier = WrapCopierFactory.wi(Wi.class, Query.class, null,
				ListTools.toList(JpaObject.FieldsUnmodifyExcludeId, Query.creatorPerson_FIELDNAME,
						Query.lastUpdatePerson_FIELDNAME, Query.lastUpdateTime_FIELDNAME));

	}
}

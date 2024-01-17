package com.x.query.assemble.designer.jaxrs.statement;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.bean.tuple.Triple;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionDuplicateFlag;
import com.x.base.core.project.exception.ExceptionEntityFieldEmpty;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.query.assemble.designer.Business;
import com.x.query.core.entity.Query;
import com.x.query.core.entity.schema.Statement;
import com.x.query.core.entity.schema.Table;

class ActionCreate extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionCreate.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		LOGGER.debug("execute:{}, jsonElement:{}.", effectivePerson::getDistinguishedName, () -> jsonElement);
		ClassLoader classLoader = Business.getDynamicEntityClassLoader();
		Thread.currentThread().setContextClassLoader(classLoader);
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			Statement statement = Wi.copier.copy(wi);
			Query query = emc.flag(wi.getQuery(), Query.class);
			if (null == query) {
				throw new ExceptionEntityNotExist(wi.getQuery(), Query.class);
			}
			statement.setQuery(query.getId());
			if (StringUtils.equals(statement.getEntityCategory(), Statement.ENTITYCATEGORY_DYNAMIC)) {
				Table table = emc.flag(wi.getTable(), Table.class);
				if (null == table) {
					throw new ExceptionEntityNotExist(wi.getTable(), Table.class);
				}
				statement.setTable(table.getId());
			} else {
				try {
					classLoader.loadClass(statement.getEntityClassName());
				} catch (Exception e) {
					throw new ExceptionEntityClass(statement.getEntityClassName());
				}
			}
			if (!business.editable(effectivePerson, query)) {
				throw new ExceptionAccessDenied(effectivePerson, query);
			}
			if (StringUtils.isEmpty(statement.getName())) {
				throw new ExceptionEntityFieldEmpty(Statement.class, Table.NAME_FIELDNAME);
			}
			emc.beginTransaction(Statement.class);
			statement.setCreatorPerson(effectivePerson.getDistinguishedName());
			statement.setLastUpdatePerson(effectivePerson.getDistinguishedName());
			statement.setLastUpdateTime(new Date());
			statement.setFv(Statement.VALUE_FV_8_0);
			this.checkDuplicate(business, statement);
			emc.persist(statement, CheckPersistType.all);
			emc.commit();
			CacheManager.notify(Statement.class);
			Wo wo = new Wo();
			wo.setId(statement.getId());
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends WoId {

		private static final long serialVersionUID = -6780522841538599608L;

	}

	public static class Wi extends Statement {

		private static final long serialVersionUID = -5237741099036357033L;

		static WrapCopier<Wi, Statement> copier = WrapCopierFactory.wi(Wi.class, Statement.class, null,
				ListTools.toList(JpaObject.FieldsUnmodify, Statement.CREATORPERSON_FIELDNAME,
						Statement.LASTUPDATEPERSON_FIELDNAME, Statement.LASTUPDATETIME_FIELDNAME));
	}

}

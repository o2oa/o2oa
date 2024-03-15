package com.x.query.assemble.designer.jaxrs.statement;

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

class ActionEdit extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionEdit.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String flag, JsonElement jsonElement) throws Exception {
		LOGGER.debug("execute;{}, flag:{}.", effectivePerson::getDistinguishedName, () -> flag);
		ClassLoader classLoader = Business.getDynamicEntityClassLoader();
		Thread.currentThread().setContextClassLoader(classLoader);
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			Statement statement = emc.flag(flag, Statement.class);
			if (null == statement) {
				throw new ExceptionEntityNotExist(flag, Statement.class);
			}
			Wi.copier.copy(wi, statement);
			Query query = emc.flag(statement.getQuery(), Query.class);
			if (null == query) {
				throw new ExceptionEntityNotExist(wi.getQuery(), Query.class);
			}
			statement.setQuery(query.getId());
			if (!business.editable(effectivePerson, query)) {
				throw new ExceptionAccessDenied(effectivePerson, query);
			}

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

			if (StringUtils.isEmpty(statement.getName())) {
				throw new ExceptionEntityFieldEmpty(Statement.class, Statement.NAME_FIELDNAME);
			}
			emc.beginTransaction(Statement.class);
			statement.setLastUpdatePerson(effectivePerson.getDistinguishedName());
			statement.setLastUpdateTime(new Date());
			this.checkDuplicate(business, statement);
			emc.check(statement, CheckPersistType.all);
			emc.commit();
			CacheManager.notify(Statement.class);
			Wo wo = new Wo();
			wo.setId(statement.getId());
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends WoId {

		private static final long serialVersionUID = 2191345451588705624L;

	}

	public static class Wi extends Statement {

		private static final long serialVersionUID = -5237741099036357033L;

		static WrapCopier<Wi, Statement> copier = WrapCopierFactory.wi(Wi.class, Statement.class, null,
				ListTools.toList(JpaObject.FieldsUnmodify, Statement.CREATORPERSON_FIELDNAME,
						Statement.LASTUPDATEPERSON_FIELDNAME, Statement.LASTUPDATETIME_FIELDNAME));
	}
}

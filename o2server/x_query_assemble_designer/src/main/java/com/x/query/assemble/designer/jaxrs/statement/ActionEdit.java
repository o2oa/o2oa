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
import com.x.base.core.project.cache.ApplicationCache;
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

	private static Logger logger = LoggerFactory.getLogger(ActionEdit.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String flag, JsonElement jsonElement) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			Statement statement = emc.flag(flag, Statement.class);
			if (null == statement) {
				throw new ExceptionEntityNotExist(flag, Statement.class);
			}
			Table table = emc.flag(wi.getTable(), Table.class);
			if (null == table) {
				throw new ExceptionEntityNotExist(statement.getTable(), Table.class);
			}
			Query query = emc.flag(table.getQuery(), Query.class);
			if (null == query) {
				throw new ExceptionEntityNotExist(table.getQuery(), Query.class);
			}
			if (!business.editable(effectivePerson, query)) {
				throw new ExceptionAccessDenied(effectivePerson, query);
			}
			Wi.copier.copy(wi, statement);
			if (StringUtils.isEmpty(statement.getName())) {
				throw new ExceptionEntityFieldEmpty(Statement.class, Statement.name_FIELDNAME);
			}
			if (StringUtils.isNotEmpty(emc.conflict(Statement.class, statement))) {
				throw new ExceptionDuplicateFlag(Statement.class, emc.conflict(Statement.class, statement));
			}
			emc.beginTransaction(Statement.class);
			statement.setTable(table.getId());
			statement.setQuery(query.getId());
			statement.setLastUpdatePerson(effectivePerson.getDistinguishedName());
			statement.setLastUpdateTime(new Date());
			emc.check(statement, CheckPersistType.all);
			emc.commit();
			ApplicationCache.notify(Statement.class);
			Wo wo = new Wo();
			wo.setId(statement.getId());
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends WoId {

	}

	public static class Wi extends Statement {

		private static final long serialVersionUID = -5237741099036357033L;

		static WrapCopier<Wi, Statement> copier = WrapCopierFactory.wi(Wi.class, Statement.class, null,
				ListTools.toList(JpaObject.FieldsUnmodify, Statement.creatorPerson_FIELDNAME,
						Statement.lastUpdatePerson_FIELDNAME, Statement.lastUpdateTime_FIELDNAME));
	}
}
package com.x.query.assemble.designer.jaxrs.table;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.dynamic.DynamicEntity;
import com.x.base.core.entity.dynamic.DynamicEntity.Field;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionDuplicateFlag;
import com.x.base.core.project.exception.ExceptionEntityFieldEmpty;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.tools.ListTools;
import com.x.query.assemble.designer.Business;
import com.x.query.core.entity.Query;
import com.x.query.core.entity.schema.Table;

class ActionCreate extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			Query query = emc.flag(wi.getQuery(), Query.class);
			if (null == query) {
				throw new ExceptionEntityNotExist(wi.getQuery());
			}
			if (!business.editable(effectivePerson, query)) {
				throw new ExceptionAccessDenied(effectivePerson, query);
			}
			Table table = Wi.copier.copy(wi);
			table.setQuery(query.getId());
			if (StringUtils.isEmpty(table.getName())) {
				throw new ExceptionEntityFieldEmpty(Table.class, Table.name_FIELDNAME);
			}
			if (StringUtils.isNotEmpty(emc.conflict(Table.class, table))) {
				throw new ExceptionDuplicateFlag(Table.class, emc.conflict(Table.class, table));
			}

			DynamicEntity dynamicEntity = XGsonBuilder.instance().fromJson(table.getDraftData(), DynamicEntity.class);
			if (ListTools.isEmpty(dynamicEntity.getFieldList())) {
				throw new ExceptionFieldEmpty();
			}

			for (Field field : dynamicEntity.getFieldList()) {
				if (JpaObject.FieldsDefault.stream().filter(o -> StringUtils.equalsIgnoreCase(o, field.getName()))
						.count() > 0) {
					throw new ExceptionFieldName(field.getName());
				}
			}

			emc.beginTransaction(Table.class);
			table.setCreatorPerson(effectivePerson.getDistinguishedName());
			table.setLastUpdatePerson(effectivePerson.getDistinguishedName());
			table.setLastUpdateTime(new Date());
			table.setData("");
			table.setStatus(Table.STATUS_draft);
			emc.persist(table, CheckPersistType.all);
			emc.commit();
			ApplicationCache.notify(Table.class);
			Wo wo = new Wo();
			wo.setId(table.getId());
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends WoId {

	}

	public static class Wi extends Table {

		private static final long serialVersionUID = -5237741099036357033L;

		static WrapCopier<Wi, Table> copier = WrapCopierFactory.wi(Wi.class, Table.class, null,
				ListTools.toList(JpaObject.FieldsUnmodify, Table.creatorPerson_FIELDNAME,
						Table.lastUpdatePerson_FIELDNAME, Table.lastUpdateTime_FIELDNAME, Table.data_FIELDNAME,
						Table.status_FIELDNAME, Table.buildSuccess_FIELDNAME));
	}

}
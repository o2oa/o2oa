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
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.gson.XGsonBuilder;
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
		LOGGER.debug("execute:{}.", effectivePerson::getDistinguishedName);
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			Table table = emc.flag(flag, Table.class);
			if (null == table) {
				throw new ExceptionEntityNotExist(flag, Table.class);
			}
			Query query = emc.flag(table.getQuery(), Query.class);
			if (null == query) {
				throw new ExceptionEntityNotExist(table.getQuery());
			}
			Wo wo = new Wo();
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			Wi.copier.copy(wi, table);

			DynamicEntity dynamicEntity = XGsonBuilder.instance().fromJson(wi.getDraftData(), DynamicEntity.class);

			if (ListTools.isEmpty(dynamicEntity.getFieldList())) {
				throw new ExceptionFieldEmpty();
			}

			for (Field field : dynamicEntity.getFieldList()) {
				if (ListTools.toList(JpaObject.FieldsDefault, DynamicEntity.BUNDLE_FIELD).stream()
						.filter(o -> StringUtils.equalsIgnoreCase(o, field.getName())).count() > 0) {
					throw new ExceptionFieldName(field.getName());
				}
			}

			emc.beginTransaction(Table.class);
			table.setLastUpdatePerson(effectivePerson.getDistinguishedName());
			table.setLastUpdateTime(new Date());
			if (Table.STATUS_BUILD.equals(table.getStatus())) {
				table.setData(table.getDraftData());
			} else {
				table.setData("");
				table.setAlias(wi.getAlias());
				table.setName(wi.getName());
			}
			checkDuplicate(business, table);
			emc.check(table, CheckPersistType.all);
			emc.commit();
			CacheManager.notify(Table.class);
			CacheManager.notify(Statement.class);

			wo.setId(table.getId());
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends WoId {

		private static final long serialVersionUID = 580689168809784572L;

	}

	public static class Wi extends Table {

		private static final long serialVersionUID = -5237741099036357033L;

		static WrapCopier<Wi, Table> copier = WrapCopierFactory.wi(Wi.class, Table.class, null,
				ListTools.toList(JpaObject.FieldsUnmodify, Table.CREATORPERSON_FIELDNAME,
						Table.LASTUPDATEPERSON_FIELDNAME, Table.LASTUPDATETIME_FIELDNAME, Table.DATA_FIELDNAME,
						Table.STATUS_FIELDNAME, Table.NAME_FIELDNAME, Table.ALIAS_FIELDNAME));
	}
}

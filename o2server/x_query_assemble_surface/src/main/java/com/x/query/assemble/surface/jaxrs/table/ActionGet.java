package com.x.query.assemble.surface.jaxrs.table;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.query.assemble.surface.Business;
import com.x.query.core.entity.Query;
import com.x.query.core.entity.schema.Table;

class ActionGet extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionGet.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String flag) throws Exception {
		LOGGER.debug("execute:{}.", effectivePerson::getDistinguishedName);
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			Table table = emc.flag(flag, Table.class);
			if (null == table) {
				throw new ExceptionEntityNotExist(flag, Table.class);
			}
			Query query = business.entityManagerContainer().flag(table.getQuery(), Query.class);
			if (null == query) {
				throw new ExceptionEntityNotExist(table.getQuery(), Query.class);
			}
			if (!business.readable(effectivePerson, table)) {
				throw new ExceptionAccessDenied(effectivePerson, table);
			}
			Wo wo = Wo.copier.copy(table);
			wo.setQueryName(query.getName());
			wo.setQueryAlias(query.getAlias());
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends Table {

		private static final long serialVersionUID = -5755898083219447939L;

		static WrapCopier<Table, Wo> copier = WrapCopierFactory.wo(Table.class, Wo.class, null,
				JpaObject.FieldsInvisible);

		@FieldDescribe("查询应用名称.")
		private String queryName;

		@FieldDescribe("查询应用别名.")
		private String queryAlias;

		public String getQueryName() {
			return queryName;
		}

		public void setQueryName(String queryName) {
			this.queryName = queryName;
		}

		public String getQueryAlias() {
			return queryAlias;
		}

		public void setQueryAlias(String queryAlias) {
			this.queryAlias = queryAlias;
		}
	}
}

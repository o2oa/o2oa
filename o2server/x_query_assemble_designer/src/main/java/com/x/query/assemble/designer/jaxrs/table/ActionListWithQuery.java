package com.x.query.assemble.designer.jaxrs.table;

import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.query.assemble.designer.Business;
import com.x.query.core.entity.Query;
import com.x.query.core.entity.schema.Table;

class ActionListWithQuery extends BaseAction {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ActionListWithQuery.class);
	
	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, String flag) throws Exception {
		LOGGER.debug("execute:{}.", effectivePerson::getDistinguishedName);
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<List<Wo>> result = new ActionResult<>();
			Business business = new Business(emc);
			Query query = emc.flag(flag, Query.class);
			if (null == query) {
				throw new ExceptionEntityNotExist(flag);
			}
			if (!effectivePerson.isSecurityManager() && !business.editable(effectivePerson, query)) {
				throw new ExceptionAccessDenied(effectivePerson.getDistinguishedName());
			}
			List<Table> tableList = business.table().listWithQueryObject(query.getId());
			List<Wo> wos = Wo.copier.copy(tableList);
			wos = business.table().sort(wos);
			result.setData(wos);
			return result;
		}
	}

	public static class Wo extends Table {

		private static final long serialVersionUID = -5755898083219447939L;

		static WrapCopier<Table, Wo> copier = WrapCopierFactory.wo(Table.class, Wo.class,
				ListTools.toList(JpaObject.singularAttributeField(Table.class, true, true),
						Table.READPERSONLIST_FIELDNAME, Table.READUNITLIST_FIELDNAME,
						Table.EDITPERSONLIST_FIELDNAME, Table.EDITUNITLIST_FIELDNAME),
				JpaObject.FieldsInvisible);
	}
}

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
import com.x.query.assemble.designer.Business;
import com.x.query.core.entity.Query;
import com.x.query.core.entity.schema.Table;

class ActionListWithQuery extends BaseAction {
	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, String flag) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<List<Wo>> result = new ActionResult<>();
			Business business = new Business(emc);
			Query query = emc.flag(flag, Query.class);
			if (null == query) {
				throw new ExceptionEntityNotExist(flag);
			}
			if (!business.editable(effectivePerson, query)) {
				throw new ExceptionAccessDenied(effectivePerson.getDistinguishedName());
			}
			List<Wo> wos = emc.fetchEqual(Table.class, Wo.copier, Table.query_FIELDNAME, query.getId());
			wos = business.table().sort(wos);
			result.setData(wos);
			return result;
		}
	}

	public static class Wo extends Table {

		private static final long serialVersionUID = -5755898083219447939L;

		static WrapCopier<Table, Wo> copier = WrapCopierFactory.wo(Table.class, Wo.class,
				JpaObject.singularAttributeField(Table.class, true, true), JpaObject.FieldsInvisible);
	}
}

package com.x.query.assemble.designer.jaxrs.table;

import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.query.assemble.designer.Business;
import com.x.query.core.entity.schema.Table;

class ActionManageList extends BaseAction {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ActionManageList.class);
	
	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson) throws Exception {
		LOGGER.debug("execute:{}.", effectivePerson::getDistinguishedName);
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<List<Wo>> result = new ActionResult<>();
			Business business = new Business(emc);
			if (!business.controllable(effectivePerson)) {
				throw new ExceptionAccessDenied(effectivePerson.getDistinguishedName());
			}
			List<Wo> wos = emc.fetchAll(Table.class, Wo.copier);
			wos = business.table().sort(wos);
			result.setData(wos);
			return result;
		}
	}

	public static class Wo extends Table {
		private static final long serialVersionUID = 1119650067394249921L;
		static WrapCopier<Table, Wo> copier = WrapCopierFactory.wo(Table.class, Wo.class,
				JpaObject.singularAttributeField(Table.class, true, true), JpaObject.FieldsInvisible);
	}
}

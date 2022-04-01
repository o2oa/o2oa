package com.x.query.service.processing.jaxrs.table;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.query.core.entity.schema.Table;

class ActionListTableStatusBuild extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionListTableStatusBuild.class);

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson) throws Exception {
		LOGGER.debug("execute:{}.", effectivePerson::getDistinguishedName);

		ActionResult<List<Wo>> result = new ActionResult<>();
		List<Table> list = new ArrayList<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			list.addAll(emc.listEqual(Table.class, Table.status_FIELDNAME, Table.STATUS_build));
		}
		List<Wo> wos = Wo.copier.copy(list);
		result.setData(wos);
		return result;
	}

	public static class Wo extends Table {

		private static final long serialVersionUID = -7917195300999020294L;

		static WrapCopier<Table, Wo> copier = WrapCopierFactory.wo(Table.class, Wo.class,
				ListTools.toList(Table.id_FIELDNAME, Table.description_FIELDNAME, Table.name_FIELDNAME),
				JpaObject.FieldsInvisible);

	}

}
package com.x.query.assemble.designer.jaxrs.table;

import java.util.List;

import javax.persistence.EntityManager;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.query.assemble.designer.Business;
import com.x.query.assemble.designer.DynamicEntity;
import com.x.query.core.entity.schema.Table;

class ActionListRowSelectWhere extends BaseAction {

	ActionResult<List<Object>> execute(EffectivePerson effectivePerson, String tableFlag, String where)
			throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<List<Object>> result = new ActionResult<>();
			Table table = emc.flag(tableFlag, Table.class);
			Business business = new Business(emc);
			if (null == table) {
				throw new ExceptionEntityNotExist(tableFlag, Table.class);
			}
			if (!business.editable(effectivePerson, table)) {
				throw new ExceptionAccessDenied(effectivePerson, table);
			}
			DynamicEntity dynamicEntity = new DynamicEntity(table.getName());
			Class<? extends JpaObject> clz = (Class<JpaObject>) Class.forName(dynamicEntity.className());
			EntityManager em = emc.get(clz);
			String sql = "SELECT o FROM " + clz.getName() + " o";
			if (StringUtils.isNotBlank(where) && (!StringUtils.equals(where, EMPTY_SYMBOL))) {
				sql += " where (" + where + ")";
			}
			List<? extends Object> list = em.createQuery(sql).getResultList();
			result.setData((List<Object>) list);
			return result;
		}
	}

}
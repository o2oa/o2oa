package com.x.query.assemble.surface.jaxrs.table;

import java.util.List;

import javax.persistence.EntityManager;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.dynamic.DynamicEntity;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.query.assemble.surface.Business;
import com.x.query.core.entity.schema.Table;

class ActionListRowSelectWhere extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionListRowSelectWhere.class);

	ActionResult<List<?>> execute(EffectivePerson effectivePerson, String tableFlag, String where) throws Exception {

		LOGGER.debug("execute:{}, table:{}, where:{}.", effectivePerson::getDistinguishedName, () -> tableFlag,
				() -> where);
		ClassLoader classLoader = Business.getDynamicEntityClassLoader();
		Thread.currentThread().setContextClassLoader(classLoader);
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<List<?>> result = new ActionResult<>();
			Table table = emc.flag(tableFlag, Table.class);
			Business business = new Business(emc);
			if (null == table) {
				throw new ExceptionEntityNotExist(tableFlag, Table.class);
			}
			if (!business.readable(effectivePerson, table)) {
				throw new ExceptionAccessDenied(effectivePerson, table);
			}
			DynamicEntity dynamicEntity = new DynamicEntity(table.getName());
			@SuppressWarnings("unchecked")
			Class<? extends JpaObject> cls = (Class<? extends JpaObject>) classLoader
					.loadClass(dynamicEntity.className());
			EntityManager em = emc.get(cls);
			String sql = "SELECT o FROM " + cls.getName() + " o";
			if (StringUtils.isNotBlank(where) && (!StringUtils.equals(where, EMPTY_SYMBOL))) {
				sql += " where " + where;
			}
			List<?> list = em.createQuery(sql).getResultList();
			result.setData(list);
			return result;
		}
	}

}

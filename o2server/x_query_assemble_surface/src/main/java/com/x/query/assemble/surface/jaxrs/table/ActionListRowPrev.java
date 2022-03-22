package com.x.query.assemble.surface.jaxrs.table;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonObject;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.dynamic.DynamicEntity;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.query.assemble.surface.Business;
import com.x.query.core.entity.schema.Table;

class ActionListRowPrev extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionListRowPrev.class);

	ActionResult<List<JsonObject>> execute(EffectivePerson effectivePerson, String tableFlag, String id, Integer count)
			throws Exception {
		LOGGER.debug("execute:{}, table:{}, id:{}, count:{}.", effectivePerson::getDistinguishedName, () -> tableFlag,
				() -> id, () -> count);
		ClassLoader classLoader = Business.getDynamicEntityClassLoader();
		Thread.currentThread().setContextClassLoader(classLoader);
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<List<JsonObject>> result = new ActionResult<>();
			Business business = new Business(emc);
			Table table = emc.flag(tableFlag, Table.class);
			if (null == table) {
				throw new ExceptionEntityNotExist(tableFlag, Table.class);
			}
			if (!business.readable(effectivePerson, table)) {
				throw new ExceptionAccessDenied(effectivePerson.getDistinguishedName());
			}
			DynamicEntity dynamicEntity = new DynamicEntity(table.getName());
			@SuppressWarnings("unchecked")
			Class<? extends JpaObject> cls = (Class<? extends JpaObject>) classLoader
					.loadClass(dynamicEntity.className());
			EntityManager em = emc.get(cls);
			Object sequence = null;
			if (!StringUtils.equals(EMPTY_SYMBOL, id)) {
				JpaObject o = emc.fetch(id, cls, ListTools.toList(JpaObject.sequence_FIELDNAME));
				if (null != o) {
					sequence = o.getSequence();
				}
			}
			List<String> fields = JpaObject.singularAttributeField(cls, true, false);
			fields.add(JpaObject.sequence_FIELDNAME);
			List<String> selects = new ArrayList<>();
			for (String str : fields) {
				selects.add("o." + str);
			}
			result.setCount(emc.count(cls));
			String sql = "select " + StringUtils.join(selects, ", ") + " from " + cls.getName() + " o";
			Long rank = 0L;
			List<JsonObject> wos = new ArrayList<>();
			Query query;
			if (null != sequence) {
				sql += " where o." + JpaObject.sequence_FIELDNAME + " > ?1 order by o." + JpaObject.sequence_FIELDNAME
						+ " ASC";
				rank = emc.countGreaterThan(cls, JpaObject.sequence_FIELDNAME, sequence);
				query = em.createQuery(sql, Object[].class);
				query.setParameter(1, sequence);
			} else {
				sql += " order by o." + JpaObject.sequence_FIELDNAME + " ASC";
				rank = result.getCount();
				query = em.createQuery(sql, Object[].class);
			}
			@SuppressWarnings("unchecked")
			List<Object[]> list = query.setMaxResults(Math.max(Math.min(count, list_max), list_min)).getResultList();
			for (Object[] os : list) {
				JsonObject jsonObject = XGsonBuilder.instance().toJsonTree(JpaObject.cast(cls, fields, os))
						.getAsJsonObject();
				jsonObject.getAsJsonObject().addProperty("rank", rank--);
				wos.add(jsonObject);
			}
			Collections.reverse(wos);
			result.setData(wos);
			return result;
		}
	}
}
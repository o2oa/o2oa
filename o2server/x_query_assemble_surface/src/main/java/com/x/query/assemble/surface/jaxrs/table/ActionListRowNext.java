package com.x.query.assemble.surface.jaxrs.table;

import java.util.ArrayList;
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

class ActionListRowNext extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionListRowNext.class);

	ActionResult<List<JsonObject>> execute(EffectivePerson effectivePerson, String tableFlag, String id, Integer count)
			throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {

			ActionResult<List<JsonObject>> result = new ActionResult<>();
			logger.debug(effectivePerson, "table:{}, id:{}, count:{}.", tableFlag, id, count);
			Business business = new Business(emc);
			Table table = emc.flag(tableFlag, Table.class);
			if (null == table) {
				throw new ExceptionEntityNotExist(tableFlag, Table.class);
			}
			if (!business.readable(effectivePerson, table)) {
				throw new ExceptionAccessDenied(effectivePerson.getDistinguishedName());
			}
			DynamicEntity dynamicEntity = new DynamicEntity(table.getName());
			Class<? extends JpaObject> cls = dynamicEntity.getObjectClass();
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
			String sql = "select " + StringUtils.join(selects, ", ") + " from " + cls.getName() + " o";
			Long rank = 0L;
			if (null != sequence) {
				sql += " where o." + JpaObject.sequence_FIELDNAME + " < ?1";
				rank = emc.countGreaterThanOrEqualTo(cls, JpaObject.sequence_FIELDNAME, sequence);
			}
			sql += " order by o." + JpaObject.sequence_FIELDNAME + " DESC";
			Query query = em.createQuery(sql, Object[].class);
			if (null != sequence) {
				query.setParameter(1, sequence);
			}
			List<Object[]> list = query.setMaxResults(Math.max(Math.min(count, list_max), list_min)).getResultList();
			List<JsonObject> wos = new ArrayList<>();
			result.setCount(emc.count(cls));
			for (Object[] os : list) {
				JsonObject jsonObject = XGsonBuilder.instance().toJsonTree(JpaObject.cast(cls, fields, os))
						.getAsJsonObject();
				jsonObject.getAsJsonObject().addProperty("rank", ++rank);
				wos.add(jsonObject);
			}
			result.setData(wos);
			return result;
		}
	}
}

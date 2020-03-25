package com.x.query.assemble.designer.jaxrs.table;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.dynamic.DynamicEntity;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoFile;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.DefaultCharset;
import com.x.query.assemble.designer.Business;
import com.x.query.core.entity.schema.Table;

import javax.persistence.EntityManager;
import javax.persistence.Query;

class ActionRowExport extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionRowExport.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String tableFlag, Integer count)
			throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {

			ActionResult<Wo> result = new ActionResult<>();
			logger.debug(effectivePerson, "table:{}, id:{}, count:{}.", tableFlag, count);
			Business business = new Business(emc);
			Table table = emc.flag(tableFlag, Table.class);
			if (null == table) {
				throw new ExceptionEntityNotExist(tableFlag, Table.class);
			}
			this.check(effectivePerson, business, table);
			DynamicEntity dynamicEntity = new DynamicEntity(table.getName());
			Class<? extends JpaObject> cls = dynamicEntity.getObjectClass();
			EntityManager em = emc.get(cls);
			String sql = "select o from " + cls.getName() + " o";
			Query query = em.createQuery(sql);
			Object data = query.setMaxResults(Math.min(count, 2000)).getResultList();
			Wo wo = new Wo(gson.toJson(data).getBytes(DefaultCharset.charset), this.contentType(true, table.getName() +".json"),
					this.contentDisposition(true, table.getName() +".json"));
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends WoFile {

		public Wo(byte[] bytes, String contentType, String contentDisposition) {
			super(bytes, contentType, contentDisposition);
		}

	}
}

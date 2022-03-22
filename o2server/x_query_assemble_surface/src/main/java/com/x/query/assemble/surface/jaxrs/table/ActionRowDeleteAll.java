package com.x.query.assemble.surface.jaxrs.table;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.dynamic.DynamicEntity;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapLong;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.query.assemble.surface.Business;
import com.x.query.core.entity.schema.Table;

class ActionRowDeleteAll extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionRowDeleteAll.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String tableFlag) throws Exception {
		LOGGER.debug("execute:{}, table:{}.", effectivePerson::getDistinguishedName, () -> tableFlag);
		ClassLoader classLoader = Business.getDynamicEntityClassLoader();
		Thread.currentThread().setContextClassLoader(classLoader);
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Table table = emc.flag(tableFlag, Table.class);
			Business business = new Business(emc);
			if (null == table) {
				throw new ExceptionEntityNotExist(tableFlag, Table.class);
			}
			if (!business.editable(effectivePerson, table)) {
				throw new ExceptionAccessDenied(effectivePerson, table);
			}
			DynamicEntity dynamicEntity = new DynamicEntity(table.getName());
			@SuppressWarnings("unchecked")
			Class<? extends JpaObject> cls = (Class<? extends JpaObject>) classLoader
					.loadClass(dynamicEntity.className());
			List<String> ids = null;
			Long count = 0L;
			do {
				ids = this.listIds(business, cls);
				if (!ids.isEmpty()) {
					emc.beginTransaction(cls);
					count += this.delete(business, cls, ids);
					emc.commit();
				}
			} while (!ids.isEmpty());
			Wo wo = new Wo();
			wo.setValue(count);
			result.setData(wo);
			return result;
		}
	}

	private <T extends JpaObject> List<String> listIds(Business business, Class<T> cls) throws Exception {
		EntityManager em = business.entityManagerContainer().get(cls);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<T> root = cq.from(cls);
		return em.createQuery(cq.select(root.get(JpaObject.id_FIELDNAME))).setMaxResults(2000).getResultList();
	}

	private <T extends JpaObject> Integer delete(Business business, Class<T> cls, List<String> ids) throws Exception {
		EntityManager em = business.entityManagerContainer().get(cls);
		Query query = em.createQuery("delete from " + cls.getName() + " o where o.id in :ids");
		query.setParameter("ids", ids);
		return query.executeUpdate();
	}

	public static class Wo extends WrapLong {

		private static final long serialVersionUID = 3975293464936621278L;

	}

}
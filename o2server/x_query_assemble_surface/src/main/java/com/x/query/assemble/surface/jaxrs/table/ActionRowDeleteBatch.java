package com.x.query.assemble.surface.jaxrs.table;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.dynamic.DynamicEntity;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapIdList;
import com.x.base.core.project.jaxrs.WrapLong;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.query.assemble.surface.Business;
import com.x.query.core.entity.schema.Table;

class ActionRowDeleteBatch extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionRowDeleteBatch.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String tableFlag, JsonElement jsonElement)
			throws Exception {
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
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			Long count = 0L;
			if (ListTools.isNotEmpty(wi.getIdList())) {
				for (List<String> ids : ListTools.batch(wi.getIdList(), 2000)) {
					emc.beginTransaction(cls);
					count += this.delete(business, cls, ids);
					emc.commit();
				}
			}
			Wo wo = new Wo();
			wo.setValue(count);
			result.setData(wo);
			return result;
		}
	}

	private <T extends JpaObject> Integer delete(Business business, Class<T> cls, List<String> ids) throws Exception {
		EntityManager em = business.entityManagerContainer().get(cls);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaDelete<T> cd = cb.createCriteriaDelete(cls);
		Root<T> root = cd.from(cls);
		Predicate p = cb.isMember(root.get(JpaObject.id_FIELDNAME), cb.literal(ids));
		return em.createQuery(cd.where(p)).executeUpdate();

	}

	public static class Wi extends WrapIdList {

		private static final long serialVersionUID = 1188354400787482687L;

	}

	public static class Wo extends WrapLong {

		private static final long serialVersionUID = 1604094062416458276L;

	}

}
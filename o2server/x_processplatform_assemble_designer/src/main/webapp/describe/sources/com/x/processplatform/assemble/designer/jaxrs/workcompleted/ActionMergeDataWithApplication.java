package com.x.processplatform.assemble.designer.jaxrs.workcompleted;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.entity.dataitem.DataItemConverter;
import com.x.base.core.entity.dataitem.ItemCategory;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapInteger;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.assemble.designer.Business;
import com.x.processplatform.core.entity.content.WorkCompleted;
import com.x.processplatform.core.entity.content.WorkCompleted_;
import com.x.processplatform.core.entity.element.Application;
import com.x.processplatform.core.entity.element.Process;
import com.x.query.core.entity.Item;
import com.x.query.core.entity.Item_;

class ActionMergeDataWithApplication extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String applicationFlag) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			Application application = emc.find(applicationFlag, Application.class);
			if (null == application) {
				throw new ExceptionEntityNotExist(applicationFlag, Application.class);
			}
			if (!business.editable(effectivePerson, application)) {
				throw new ExceptionAccessDenied(effectivePerson, application);
			}
			DataItemConverter<Item> converter = new DataItemConverter<>(Item.class);
			List<String> ids = new ArrayList<>();
			WorkCompleted workCompleted = null;
			int count = 0;
			do {
				ids = this.list(business, application);
				for (String id : ids) {
					workCompleted = emc.find(id, WorkCompleted.class);
					if (null != workCompleted) {
						List<Item> items = this.items(business, workCompleted);
						JsonElement jsonElement = converter.assemble(items);
						emc.beginTransaction(WorkCompleted.class);
						workCompleted.setData(XGsonBuilder.toJson(jsonElement));
						workCompleted.setDataMerged(true);
						emc.commit();
						emc.beginTransaction(Item.class);
						for (Item item : items) {
							emc.remove(item, CheckRemoveType.all);
						}
						emc.commit();
						count++;
					}
				}
			} while (ListTools.isNotEmpty(ids));
			Wo wo = new Wo();
			wo.setValue(count);
			result.setData(wo);
			return result;
		}
	}

	private List<Item> items(Business business, WorkCompleted workCompleted) throws Exception {
		EntityManager em = business.entityManagerContainer().get(Item.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Item> cq = cb.createQuery(Item.class);
		Root<Item> root = cq.from(Item.class);
		Path<String> path = root.get(Item.bundle_FIELDNAME);
		Predicate p = cb.equal(path, workCompleted.getJob());
		p = cb.and(p, cb.equal(root.get(Item_.itemCategory), ItemCategory.pp));
		List<Item> list = em.createQuery(cq.where(p)).getResultList();
		return list;
	}

	private List<String> list(Business business, Application application) throws Exception {
		EntityManager em = business.entityManagerContainer().get(WorkCompleted.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<WorkCompleted> root = cq.from(WorkCompleted.class);
		Predicate p = cb.equal(root.get(WorkCompleted_.application), application.getId());
		p = cb.and(p, cb.or(cb.isNull(root.get(WorkCompleted_.dataMerged)),
				cb.equal(root.get(WorkCompleted_.dataMerged), false)));
		cq.select(root.get(WorkCompleted_.id)).where(p);
		return em.createQuery(cq).setMaxResults(100).getResultList();
	}

	public static class Wo extends WrapInteger {

	}

}
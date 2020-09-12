package com.x.program.center.jaxrs.market;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapStringList;
import com.x.program.center.core.entity.Application;
import com.x.program.center.core.entity.Application_;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.stream.Collectors;

class ActionListCategory extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();

			EntityManager em = emc.get(Application.class);
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<String> cq = cb.createQuery(String.class);
			Root<Application> root = cq.from(Application.class);
			cq.select(root.get(Application_.category));
			List<String> categoryList = em.createQuery(cq).getResultList().stream().distinct().collect(Collectors.toList());

			Wo wo = new Wo();
			wo.setValueList(categoryList);
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends WrapStringList {

	}
}
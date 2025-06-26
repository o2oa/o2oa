package com.x.program.center.jaxrs.invoke;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.program.center.Business;
import com.x.program.center.core.entity.Invoke;
import com.x.program.center.core.entity.Invoke_;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

class ActionListWithCategory extends BaseAction {

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, String category) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<List<Wo>> result = new ActionResult<>();
			Business business = new Business(emc);
			if(!business.serviceControlAble(effectivePerson)) {
				throw new ExceptionAccessDenied(effectivePerson.getDistinguishedName());
			}
			List<Invoke> invokeList = this.listWithCategory(business, category);
			List<Wo> wos = Wo.copier.copy(invokeList);
			wos = wos.stream().sorted(Comparator.comparing(Wo::getName, Comparator.nullsLast(String::compareTo)))
					.collect(Collectors.toList());
			result.setData(wos);
			return result;
		}
	}

	private List<Invoke> listWithCategory(Business business, String category) throws Exception {
		EntityManager em = business.entityManagerContainer().get(Invoke.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Invoke> cq = cb.createQuery(Invoke.class);
		Root<Invoke> root = cq.from(Invoke.class);
		Predicate p = cb.conjunction();
		if(Invoke.CATEGORY_DEFAULT.equals(category)){
			p = cb.and(p, cb.or(cb.equal(root.get(Invoke_.category), Invoke.CATEGORY_DEFAULT),
					cb.equal(cb.trim(root.get(Invoke_.category)), ""),
					cb.isNull(root.get(Invoke_.category))));
		}else {
			p = cb.and(p, cb.equal(root.get(Invoke_.category), category));
		}
		cq.select(root).where(p);
		return em.createQuery(cq).getResultList().stream().distinct().collect(Collectors.toList());
	}

	public static class Wo extends Invoke {

		private static final long serialVersionUID = 4546727999450453639L;
		static WrapCopier<Invoke, Wo> copier = WrapCopierFactory.wo(Invoke.class, Wo.class,
				JpaObject.singularAttributeField(Invoke.class, true, false), null);
	}

}

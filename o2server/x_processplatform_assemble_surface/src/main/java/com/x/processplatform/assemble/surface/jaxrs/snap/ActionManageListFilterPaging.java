package com.x.processplatform.assemble.surface.jaxrs.snap;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.BooleanUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.JpaObject_;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.core.entity.content.Snap;

class ActionManageListFilterPaging extends BaseAction {

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, Integer page, Integer size, JsonElement jsonElement)
			throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			ActionResult<List<Wo>> result = new ActionResult<>();
			if (BooleanUtils.isNotTrue(business.ifPersonCanManageApplicationOrProcess(effectivePerson, "", ""))) {
				throw new ExceptionAccessDenied(effectivePerson);
			}
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			if (wi == null) {
				wi = new Wi();
			}
			Integer adjustPage = this.adjustPage(page);
			Integer adjustPageSize = this.adjustSize(size);
			Predicate p = manageFilter(business, wi);
			List<Snap> os = this.list(business, adjustPage, adjustPageSize, p);
			List<Wo> wos = Wo.copier.copy(os);
			result.setData(wos);
			result.setCount(this.count(business, p));
			return result;
		}
	}

	private List<Snap> list(Business business, Integer adjustPage, Integer adjustPageSize, Predicate p)
			throws Exception {
		EntityManager em = business.entityManagerContainer().get(Snap.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Snap> cq = cb.createQuery(Snap.class);
		Root<Snap> root = cq.from(Snap.class);
		cq.select(root).where(p).orderBy(cb.desc(root.get(JpaObject_.sequence)));
		return em.createQuery(cq).setFirstResult((adjustPage - 1) * adjustPageSize).setMaxResults(adjustPageSize)
				.getResultList();
	}

	private Long count(Business business, Predicate p) throws Exception {
		EntityManager em = business.entityManagerContainer().get(Snap.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Snap> root = cq.from(Snap.class);
		return em.createQuery(cq.select(cb.count(root)).where(p)).getSingleResult();
	}

	public class Wi extends FilterWi {

	}

	public static class Wo extends RankWo {

		private static final long serialVersionUID = 2279846765261247910L;

		static WrapCopier<Snap, Wo> copier = WrapCopierFactory.wo(Snap.class, Wo.class,
				JpaObject.singularAttributeField(Snap.class, true, true), null);

	}

}

package com.x.organization.assemble.control.jaxrs.identity;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.organization.assemble.control.Business;
import com.x.organization.core.entity.Identity;
import com.x.organization.core.entity.Identity_;
import com.x.organization.core.entity.Person;

import net.sf.ehcache.Element;

class ActionListWithPerson extends BaseAction {

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, String personFlag) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<List<Wo>> result = new ActionResult<>();
			Business business = new Business(emc);
			String cacheKey = ApplicationCache.concreteCacheKey(this.getClass(), personFlag);
			Element element = business.cache().get(cacheKey);
			if (null != element && (null != element.getObjectValue())) {
				result.setData((List<Wo>) element.getObjectValue());
			} else {
				List<Wo> wos = this.list(business, personFlag);
				business.cache().put(new Element(cacheKey, wos));
				result.setData(wos);
			}
			return result;
		}
	}

	public static class Wo extends Identity {

		private static final long serialVersionUID = -127291000673692614L;

		static WrapCopier<Identity, Wo> copier = WrapCopierFactory.wo(Identity.class, Wo.class, null,
				JpaObject.FieldsInvisible);

	}

	private List<Wo> list(Business business, String personFlag) throws Exception {
		Person person = business.person().pick(personFlag);
		if (null == person) {
			throw new ExceptionPersonNotExist(personFlag);
		}
		EntityManager em = business.entityManagerContainer().get(Identity.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Identity> cq = cb.createQuery(Identity.class);
		Root<Identity> root = cq.from(Identity.class);
		Predicate p = cb.equal(root.get(Identity_.person), person.getId());
		List<Identity> os = em.createQuery(cq.select(root).where(p)).getResultList();
		List<Wo> wos = Wo.copier.copy(os);
		wos = business.identity().sort(wos);
		return wos;
	}

}
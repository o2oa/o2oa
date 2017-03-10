package com.x.file.assemble.control.jaxrs.file;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.base.core.bean.NameValueCountPair;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.file.assemble.control.Business;
import com.x.file.core.entity.open.File;
import com.x.file.core.entity.open.File_;
import com.x.file.core.entity.open.ReferenceType;

class ActionListReferenceType extends ActionBase {
	ActionResult<List<NameValueCountPair>> execute(EffectivePerson effectivePerson) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<List<NameValueCountPair>> result = new ActionResult<>();
			List<NameValueCountPair> wraps = new ArrayList<>();
			Business business = new Business(emc);
			for (ReferenceType o : this.listReferenceTypeWithPerson(business, effectivePerson)) {
				NameValueCountPair pair = new NameValueCountPair();
				pair.setName(o.toString());
				pair.setValue(o.toString());
				pair.setCount(this.countWithPersonWithReferenceType(business, effectivePerson, o));
				wraps.add(pair);
			}
			result.setData(wraps);
			return result;
		}
	}

	private List<ReferenceType> listReferenceTypeWithPerson(Business business, EffectivePerson effectivePerson)
			throws Exception {
		EntityManager em = business.entityManagerContainer().get(File.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<ReferenceType> cq = cb.createQuery(ReferenceType.class);
		Root<File> root = cq.from(File.class);
		Predicate p = cb.equal(root.get(File_.person), effectivePerson.getName());
		cq.select(root.get(File_.referenceType)).where(p).distinct(true);
		return em.createQuery(cq).getResultList();
	}

	private Long countWithPersonWithReferenceType(Business business, EffectivePerson effectivePerson,
			ReferenceType referenceType) throws Exception {
		EntityManager em = business.entityManagerContainer().get(File.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<File> root = cq.from(File.class);
		Predicate p = cb.equal(root.get(File_.person), effectivePerson.getName());
		p = cb.and(p, cb.equal(root.get(File_.referenceType), referenceType));
		cq.select(cb.count(root)).where(p);
		return em.createQuery(cq).getSingleResult();
	}
}

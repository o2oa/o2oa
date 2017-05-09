package com.x.organization.assemble.express.factory;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.ObjectUtils;

import com.x.base.core.utils.annotation.MethodDescribe;
import com.x.organization.assemble.express.AbstractFactory;
import com.x.organization.assemble.express.Business;
import com.x.organization.assemble.express.jaxrs.wrapout.WrapOutPersonAttribute;
import com.x.organization.core.entity.PersonAttribute;
import com.x.organization.core.entity.PersonAttribute_;

public class PersonAttributeFactory extends AbstractFactory {

	public PersonAttributeFactory(Business business) throws Exception {
		super(business);
	}

	@MethodDescribe("根据名称查找PersonAttribute")
	public String getWithName(String name, String personId) throws Exception {
		EntityManager em = this.entityManagerContainer().get(PersonAttribute.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<PersonAttribute> root = cq.from(PersonAttribute.class);
		Predicate p = cb.equal(root.get(PersonAttribute_.person), personId);
		p = cb.and(p, cb.equal(root.get(PersonAttribute_.name), name));
		cq.select(root.get(PersonAttribute_.id)).where(p).distinct(true);
		List<String> list = em.createQuery(cq).setMaxResults(1).getResultList();
		if (!list.isEmpty()) {
			return list.get(0);
		} else {
			return null;
		}
	}

	@MethodDescribe("根据名称查找PersonAttribute")
	public List<String> listWithPerson(String personId) throws Exception {
		EntityManager em = this.entityManagerContainer().get(PersonAttribute.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<PersonAttribute> root = cq.from(PersonAttribute.class);
		Predicate p = cb.equal(root.get(PersonAttribute_.person), personId);
		cq.select(root.get(PersonAttribute_.id)).where(p).distinct(true);
		return em.createQuery(cq).getResultList();
	}

	public List<String> listContainsAttribute(String attribute) throws Exception {
		EntityManager em = this.entityManagerContainer().get(PersonAttribute.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<PersonAttribute> root = cq.from(PersonAttribute.class);
		Predicate p = cb.isMember(attribute, root.get(PersonAttribute_.attributeList));
		cq.select(root.get(PersonAttribute_.id)).where(p).distinct(true);
		return em.createQuery(cq).getResultList();
	}

	public WrapOutPersonAttribute wrap(PersonAttribute o) throws Exception {
		WrapOutPersonAttribute wrap = new WrapOutPersonAttribute();
		o.copyTo(wrap);
		return wrap;
	}

	public void sort(List<WrapOutPersonAttribute> wraps) throws Exception {
		Collections.sort(wraps, new Comparator<WrapOutPersonAttribute>() {
			public int compare(WrapOutPersonAttribute o1, WrapOutPersonAttribute o2) {
				return ObjectUtils.compare(o1.getName(), o2.getName(), true);
			}
		});
	}
}
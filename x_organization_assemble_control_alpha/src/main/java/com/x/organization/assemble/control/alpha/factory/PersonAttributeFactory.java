package com.x.organization.assemble.control.alpha.factory;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.base.core.utils.annotation.MethodDescribe;
import com.x.organization.assemble.control.alpha.AbstractFactory;
import com.x.organization.assemble.control.alpha.Business;
import com.x.organization.core.entity.PersonAttribute;
import com.x.organization.core.entity.PersonAttribute_;

public class PersonAttributeFactory extends AbstractFactory {

	public PersonAttributeFactory(Business business) throws Exception {
		super(business);
	}


	@MethodDescribe("根据指定的Person获取所有的PersonAttribute.")
	public List<String> listWithPerson(String id) throws Exception {
		EntityManager em = this.entityManagerContainer().get(PersonAttribute.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<PersonAttribute> root = cq.from(PersonAttribute.class);
		Predicate p = cb.equal(root.get(PersonAttribute_.person), id);
		cq.select(root.get(PersonAttribute_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}
}
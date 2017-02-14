package com.x.organization.assemble.control.factory;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.base.core.utils.annotation.MethodDescribe;
import com.x.organization.assemble.control.AbstractFactory;
import com.x.organization.assemble.control.Business;
import com.x.organization.core.entity.CompanyAttribute;
import com.x.organization.core.entity.CompanyAttribute_;

public class CompanyAttributeFactory extends AbstractFactory {

	public CompanyAttributeFactory(Business business) throws Exception {
		super(business);
	}

	@MethodDescribe("根据指定的Company获取所有的CompanyAttribute")
	public List<String> listWithCompany(String id) throws Exception {
		EntityManager em = this.entityManagerContainer().get(CompanyAttribute.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<CompanyAttribute> root = cq.from(CompanyAttribute.class);
		Predicate p = cb.equal(root.get(CompanyAttribute_.company), id);
		cq.select(root.get(CompanyAttribute_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}
}
package com.x.organization.assemble.control.alpha.factory;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.organization.assemble.control.alpha.AbstractFactory;
import com.x.organization.assemble.control.alpha.Business;
import com.x.organization.core.entity.Company;
import com.x.organization.core.entity.CompanyAttribute;
import com.x.organization.core.entity.CompanyAttribute_;

public class CompanyAttributeFactory extends AbstractFactory {

	public CompanyAttributeFactory(Business business) throws Exception {
		super(business);
	}

	public CompanyAttribute pick(String flag) throws Exception {
		return this.pick(flag, CompanyAttribute.class, CompanyAttribute.FLAGS);
	}

	public Long countWithCompany(Company company) throws Exception {
		EntityManager em = this.entityManagerContainer().get(CompanyAttribute.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<CompanyAttribute> root = cq.from(CompanyAttribute.class);
		Predicate p = cb.equal(root.get(CompanyAttribute_.company), company.getId());
		cq.select(cb.count(root)).where(p);
		return em.createQuery(cq).getSingleResult();
	}

	// public List<String> listWithCompany(Company company) throws Exception {
	// return listWithCompany(company.getId());
	// }
	//
	// public Long countWithCompany(Company company) throws Exception {
	// return this.countWithCompany(company.getId());
	// }
}
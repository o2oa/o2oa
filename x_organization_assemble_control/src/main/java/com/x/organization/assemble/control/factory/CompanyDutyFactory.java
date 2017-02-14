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
import com.x.organization.core.entity.CompanyDuty;
import com.x.organization.core.entity.CompanyDuty_;

public class CompanyDutyFactory extends AbstractFactory {

	public CompanyDutyFactory(Business business) throws Exception {
		super(business);
	}

	@MethodDescribe("根据指定的Company获取所有的CompanyDuty")
	public List<String> listWithCompany(String id) throws Exception {
		EntityManager em = this.entityManagerContainer().get(CompanyDuty.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<CompanyDuty> root = cq.from(CompanyDuty.class);
		Predicate p = cb.equal(root.get(CompanyDuty_.company), id);
		cq.select(root.get(CompanyDuty_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}

	@MethodDescribe("根据指定的Identity获取所有的CompanyDuty")
	public List<String> listWithIdentity(String id) throws Exception {
		EntityManager em = this.entityManagerContainer().get(CompanyDuty.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<CompanyDuty> root = cq.from(CompanyDuty.class);
		Predicate p = cb.isMember(id, root.get(CompanyDuty_.identityList));
		cq.select(root.get(CompanyDuty_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}

	public List<String> listWithIdentity(List<String> ids) throws Exception {
		EntityManager em = this.entityManagerContainer().get(CompanyDuty.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<CompanyDuty> root = cq.from(CompanyDuty.class);
		Predicate p = cb.disjunction();
		for (String str : ids) {
			p = cb.or(p, cb.isMember(str, root.get(CompanyDuty_.identityList)));
		}
		cq.select(root.get(CompanyDuty_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}

}
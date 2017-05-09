package com.x.organization.assemble.control.alpha.factory;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.organization.assemble.control.alpha.AbstractFactory;
import com.x.organization.assemble.control.alpha.Business;
import com.x.organization.core.entity.DepartmentDuty;
import com.x.organization.core.entity.DepartmentDuty_;

public class DepartmentDutyFactory extends AbstractFactory {

	public DepartmentDutyFactory(Business business) throws Exception {
		super(business);
	}

	public List<String> listWithIdentity(String id) throws Exception {
		EntityManager em = this.entityManagerContainer().get(DepartmentDuty.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<DepartmentDuty> root = cq.from(DepartmentDuty.class);
		Predicate p = cb.isMember(id, root.get(DepartmentDuty_.identityList));
		cq.select(root.get(DepartmentDuty_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}

	public List<String> listWithIdentity(List<String> ids) throws Exception {
		EntityManager em = this.entityManagerContainer().get(DepartmentDuty.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<DepartmentDuty> root = cq.from(DepartmentDuty.class);
		Predicate p = cb.disjunction();
		for (String str : ids) {
			p = cb.or(p, cb.isMember(str, root.get(DepartmentDuty_.identityList)));
		}
		cq.select(root.get(DepartmentDuty_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}

	public List<String> listWithDepartment(String id) throws Exception {
		EntityManager em = this.entityManagerContainer().get(DepartmentDuty.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<DepartmentDuty> root = cq.from(DepartmentDuty.class);
		Predicate p = cb.equal(root.get(DepartmentDuty_.department), id);
		cq.select(root.get(DepartmentDuty_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}

}
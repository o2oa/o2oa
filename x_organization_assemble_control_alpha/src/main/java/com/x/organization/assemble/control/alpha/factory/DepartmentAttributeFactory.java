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
import com.x.organization.core.entity.DepartmentAttribute;
import com.x.organization.core.entity.DepartmentAttribute_;

public class DepartmentAttributeFactory extends AbstractFactory {

	public DepartmentAttributeFactory(Business business) throws Exception {
		super(business);
	}


	@MethodDescribe("根据指定的Department获取所有的DepartmentAttribute")
	public List<String> listWithDepartment(String id) throws Exception {
		EntityManager em = this.entityManagerContainer().get(DepartmentAttribute.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<DepartmentAttribute> root = cq.from(DepartmentAttribute.class);
		Predicate p = cb.equal(root.get(DepartmentAttribute_.department), id);
		cq.select(root.get(DepartmentAttribute_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}
}
package com.x.organization.assemble.control.jaxrs.departmentduty;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.base.core.bean.BeanCopyTools;
import com.x.base.core.bean.BeanCopyToolsBuilder;
import com.x.base.core.container.EntityManagerContainer;
import com.x.organization.assemble.control.Business;
import com.x.organization.assemble.control.wrapin.WrapInDepartmentDuty;
import com.x.organization.assemble.control.wrapout.WrapOutDepartmentDuty;
import com.x.organization.core.entity.Department;
import com.x.organization.core.entity.DepartmentDuty;
import com.x.organization.core.entity.Department_;

public class ActionBase {

	protected static BeanCopyTools<DepartmentDuty, WrapOutDepartmentDuty> outCopier = BeanCopyToolsBuilder
			.create(DepartmentDuty.class, WrapOutDepartmentDuty.class, null, WrapOutDepartmentDuty.Excludes);

	protected static BeanCopyTools<WrapInDepartmentDuty, DepartmentDuty> inCopier = BeanCopyToolsBuilder
			.create(WrapInDepartmentDuty.class, DepartmentDuty.class, null, WrapInDepartmentDuty.Excludes);

	protected String getDepartmentName(Business business, String departmentId) throws Exception {
		EntityManagerContainer emc = business.entityManagerContainer();
		EntityManager em = emc.get(Department.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Department> root = cq.from(Department.class);
		Predicate p = cb.equal(root.get(Department_.id), departmentId);
		cq.select(root.get(Department_.name)).where(p);
		List<String> list = em.createQuery(cq).setMaxResults(1).getResultList();
		if (!list.isEmpty()) {
			return list.get(0);
		}
		return null;
	}
}

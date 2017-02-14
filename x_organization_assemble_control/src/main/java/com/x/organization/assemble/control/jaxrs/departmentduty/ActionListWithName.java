package com.x.organization.assemble.control.jaxrs.departmentduty;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.http.ActionResult;
import com.x.organization.assemble.control.Business;
import com.x.organization.assemble.control.wrapout.WrapOutDepartmentDuty;
import com.x.organization.core.entity.DepartmentDuty;
import com.x.organization.core.entity.DepartmentDuty_;

class ActionListWithName extends ActionBase {

	protected ActionResult<List<WrapOutDepartmentDuty>> execute(String name) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			ActionResult<List<WrapOutDepartmentDuty>> result = new ActionResult<>();
			List<WrapOutDepartmentDuty> wraps = new ArrayList<>();
			EntityManager em = emc.get(DepartmentDuty.class);
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<String> cq = cb.createQuery(String.class);
			Root<DepartmentDuty> root = cq.from(DepartmentDuty.class);
			Predicate p = cb.equal(root.get(DepartmentDuty_.name), name);
			cq.select(root.get(DepartmentDuty_.id)).where(p);
			List<String> ids = em.createQuery(cq).getResultList();
			List<DepartmentDuty> os = emc.list(DepartmentDuty.class, ids);
			wraps = outCopier.copy(os);
			for (WrapOutDepartmentDuty o : wraps) {
				o.setDepartmentName(this.getDepartmentName(business, o.getDepartment()));
			}
			result.setData(wraps);
			return result;
		}
	}

}

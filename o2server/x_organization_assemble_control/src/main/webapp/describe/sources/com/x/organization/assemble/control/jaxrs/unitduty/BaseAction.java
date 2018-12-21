package com.x.organization.assemble.control.jaxrs.unitduty;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.organization.assemble.control.Business;
import com.x.organization.core.entity.Unit;
import com.x.organization.core.entity.UnitDuty;
import com.x.organization.core.entity.UnitDuty_;

abstract class BaseAction extends StandardJaxrsAction {

	/** 如果唯一标识不为空,要检查唯一标识是否唯一 */
	protected boolean uniqueDuplicateWhenNotEmpty(Business business, UnitDuty unitDuty) throws Exception {
		if (StringUtils.isNotEmpty(unitDuty.getUnique())) {
			if (business.entityManagerContainer().duplicateWithFlags(unitDuty.getId(), UnitDuty.class,
					unitDuty.getUnique())) {
				return true;
			}
		}
		return false;
	}

	/** 在同一个unit下名称不能重复 */
	protected boolean duplicateOnUnit(Business business, Unit unit, String name, UnitDuty exclude) throws Exception {
		EntityManager em = business.entityManagerContainer().get(UnitDuty.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<UnitDuty> root = cq.from(UnitDuty.class);
		Predicate p = cb.equal(root.get(UnitDuty_.name), name);
		p = cb.and(p, cb.equal(root.get(UnitDuty_.unit), unit.getId()));
		p = cb.and(p, cb.notEqual(root.get(UnitDuty_.id), exclude.getId()));
		return em.createQuery(cq.select(cb.count(root)).where(p)).getSingleResult() > 0;
	}
}

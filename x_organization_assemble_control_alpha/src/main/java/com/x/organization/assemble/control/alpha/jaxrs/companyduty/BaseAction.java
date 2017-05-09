package com.x.organization.assemble.control.alpha.jaxrs.companyduty;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.base.core.bean.BeanCopyTools;
import com.x.base.core.bean.BeanCopyToolsBuilder;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.organization.assemble.control.alpha.Business;
import com.x.organization.assemble.control.alpha.wrapin.WrapInCompanyDuty;
import com.x.organization.assemble.control.alpha.wrapout.WrapOutCompanyDuty;
import com.x.organization.core.entity.Company;
import com.x.organization.core.entity.CompanyDuty;
import com.x.organization.core.entity.Company_;

abstract class BaseAction extends StandardJaxrsAction {
	protected static BeanCopyTools<CompanyDuty, WrapOutCompanyDuty> outCopier = BeanCopyToolsBuilder
			.create(CompanyDuty.class, WrapOutCompanyDuty.class, null, WrapOutCompanyDuty.Excludes);

	protected static BeanCopyTools<WrapInCompanyDuty, CompanyDuty> inCopier = BeanCopyToolsBuilder
			.create(WrapInCompanyDuty.class, CompanyDuty.class, null, WrapInCompanyDuty.Excludes);

	protected String getCompanyName(Business business, String companyId) throws Exception {
		EntityManagerContainer emc = business.entityManagerContainer();
		EntityManager em = emc.get(Company.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Company> root = cq.from(Company.class);
		Predicate p = cb.equal(root.get(Company_.id), companyId);
		cq.select(root.get(Company_.name)).where(p);
		List<String> list = em.createQuery(cq).setMaxResults(1).getResultList();
		if (!list.isEmpty()) {
			return list.get(0);
		}
		return null;
	}
}

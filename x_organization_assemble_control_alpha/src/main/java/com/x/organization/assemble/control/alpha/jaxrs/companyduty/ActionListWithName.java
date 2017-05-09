package com.x.organization.assemble.control.alpha.jaxrs.companyduty;

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
import com.x.organization.assemble.control.alpha.Business;
import com.x.organization.assemble.control.alpha.wrapout.WrapOutCompanyDuty;
import com.x.organization.core.entity.CompanyDuty;
import com.x.organization.core.entity.CompanyDuty_;

class ActionListWithName extends ActionBase {

	protected ActionResult<List<WrapOutCompanyDuty>> execute(String name) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			ActionResult<List<WrapOutCompanyDuty>> result = new ActionResult<>();
			List<WrapOutCompanyDuty> wraps = new ArrayList<>();
			EntityManager em = emc.get(CompanyDuty.class);
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<String> cq = cb.createQuery(String.class);
			Root<CompanyDuty> root = cq.from(CompanyDuty.class);
			Predicate p = cb.equal(root.get(CompanyDuty_.name), name);
			cq.select(root.get(CompanyDuty_.id)).where(p);
			List<String> ids = em.createQuery(cq).getResultList();
			List<CompanyDuty> os = emc.list(CompanyDuty.class, ids);
			wraps = outCopier.copy(os);
			for (WrapOutCompanyDuty o : wraps) {
				o.setCompanyName(this.getCompanyName(business, o.getCompany()));
			}
			result.setData(wraps);
			return result;
		}
	}

}
package com.x.organization.assemble.express.jaxrs.companyduty;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.cache.ApplicationCache;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.http.ActionResult;
import com.x.base.core.utils.ListTools;
import com.x.base.core.utils.SortTools;
import com.x.organization.assemble.express.Business;
import com.x.organization.assemble.express.jaxrs.wrapout.WrapOutCompanyDuty;
import com.x.organization.core.entity.CompanyDuty;
import com.x.organization.core.entity.CompanyDuty_;

import net.sf.ehcache.Element;

class ActionListWithPersonWithCompany extends ActionBase {

	@SuppressWarnings("unchecked")
	ActionResult<List<WrapOutCompanyDuty>> execute(String personName, String companyName) throws Exception {
		ActionResult<List<WrapOutCompanyDuty>> result = new ActionResult<>();
		List<WrapOutCompanyDuty> wraps = new ArrayList<>();
		String cacheKey = ApplicationCache.concreteCacheKey(this.getClass().getName(), personName, companyName);
		Element element = cache.get(cacheKey);
		if ((null != element) && (null != element.getObjectValue())) {
			wraps = (List<WrapOutCompanyDuty>) element.getObjectValue();
		} else {
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				Business business = new Business(emc);
				String personId = business.person().getWithName(personName);
				String companyId = business.company().getWithName(companyName);
				if (StringUtils.isNotEmpty(personId) && StringUtils.isNotEmpty(companyId)) {
					List<String> identityIds = business.identity().listWithPerson(personId);
					if (ListTools.isNotEmpty(identityIds)) {
						List<String> ids = this.list(business, identityIds, companyId);
						for (CompanyDuty companyDuty : emc.list(CompanyDuty.class, ids)) {
							WrapOutCompanyDuty wrap = business.companyDuty().wrap(companyDuty);
							wraps.add(wrap);
						}
						SortTools.asc(wraps, "name");
					}
				}
			}
			cache.put(new Element(cacheKey, wraps));
		}
		result.setData(wraps);
		return result;
	}

	private List<String> list(Business business, List<String> identityIds, String companyId) throws Exception {
		EntityManager em = business.entityManagerContainer().get(CompanyDuty.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<CompanyDuty> root = cq.from(CompanyDuty.class);
		Predicate p = cb.equal(root.get(CompanyDuty_.company), companyId);
		p = cb.and(p, root.get(CompanyDuty_.identityList).in(identityIds));
		cq.select(root.get(CompanyDuty_.id)).where(p).distinct(true);
		return em.createQuery(cq).getResultList();
	}
}

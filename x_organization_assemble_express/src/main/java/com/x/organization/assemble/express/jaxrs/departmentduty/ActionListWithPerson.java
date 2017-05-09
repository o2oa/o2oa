package com.x.organization.assemble.express.jaxrs.departmentduty;

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
import com.x.base.core.utils.SortTools;
import com.x.organization.assemble.express.Business;
import com.x.organization.assemble.express.jaxrs.wrapout.WrapOutDepartmentDuty;
import com.x.organization.core.entity.DepartmentDuty;
import com.x.organization.core.entity.DepartmentDuty_;

import net.sf.ehcache.Element;

class ActionListWithPerson extends ActionBase {

	@SuppressWarnings("unchecked")
	ActionResult<List<WrapOutDepartmentDuty>> execute(String personName) throws Exception {
		ActionResult<List<WrapOutDepartmentDuty>> result = new ActionResult<>();
		List<WrapOutDepartmentDuty> wraps = new ArrayList<>();
		String cacheKey = ApplicationCache.concreteCacheKey(this.getClass().getName(), personName);
		Element element = cache.get(cacheKey);
		if ((null != element) && (null != element.getObjectValue())) {
			wraps = (List<WrapOutDepartmentDuty>) element.getObjectValue();
		} else {
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				Business business = new Business(emc);
				String personId = business.person().getWithName(personName);
				if (StringUtils.isNotEmpty(personId)) {
					List<String> identityIds = business.identity().listWithPerson(personId);
					if (!identityIds.isEmpty()) {
						List<String> ids = this.list(business, identityIds);
						for (DepartmentDuty o : emc.list(DepartmentDuty.class, ids)) {
							WrapOutDepartmentDuty wrap = business.departmentDuty().wrap(o);
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

	private List<String> list(Business business, List<String> identityIds) throws Exception {
		EntityManager em = business.entityManagerContainer().get(DepartmentDuty.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<DepartmentDuty> root = cq.from(DepartmentDuty.class);
		Predicate p = root.get(DepartmentDuty_.identityList).in(identityIds);
		cq.select(root.get(DepartmentDuty_.id)).where(p).distinct(true);
		return em.createQuery(cq).getResultList();
	}
}
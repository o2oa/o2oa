package com.x.organization.assemble.control.alpha.jaxrs.companyduty;

import java.util.Collections;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.WrapOutStringList;
import com.x.organization.core.entity.CompanyDuty;
import com.x.organization.core.entity.CompanyDuty_;

class ActionDistinctName extends ActionBase {

	protected ActionResult<WrapOutStringList> execute() throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<WrapOutStringList> result = new ActionResult<>();
			WrapOutStringList wrap = new WrapOutStringList();
			EntityManager em = emc.get(CompanyDuty.class);
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<String> cq = cb.createQuery(String.class);
			Root<CompanyDuty> root = cq.from(CompanyDuty.class);
			cq.select(root.get(CompanyDuty_.name)).distinct(true);
			List<String> list = em.createQuery(cq).getResultList();
			for (String str : list) {
				if (StringUtils.isNotEmpty(str)) {
					wrap.getValueList().add(str);
				}
			}
			Collections.sort(wrap.getValueList());
			result.setData(wrap);
			return result;
		}
	}

}

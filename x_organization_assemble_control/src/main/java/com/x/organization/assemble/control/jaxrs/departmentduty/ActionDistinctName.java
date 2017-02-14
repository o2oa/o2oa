package com.x.organization.assemble.control.jaxrs.departmentduty;

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
import com.x.base.core.http.WrapOutListString;
import com.x.organization.core.entity.DepartmentDuty;
import com.x.organization.core.entity.DepartmentDuty_;

class ActionDistinctName extends ActionBase {

	protected ActionResult<WrapOutListString> execute() throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<WrapOutListString> result = new ActionResult<>();
			WrapOutListString wrap = new WrapOutListString();
			EntityManager em = emc.get(DepartmentDuty.class);
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<String> cq = cb.createQuery(String.class);
			Root<DepartmentDuty> root = cq.from(DepartmentDuty.class);
			cq.select(root.get(DepartmentDuty_.name)).distinct(true);
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

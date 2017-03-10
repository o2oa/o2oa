package com.x.organization.assemble.control.jaxrs.departmentduty;

import java.util.Collections;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.WrapOutStringList;
import com.x.organization.core.entity.DepartmentDuty;
import com.x.organization.core.entity.DepartmentDuty_;

class ActionDistinctNameLike extends ActionBase {

	protected ActionResult<WrapOutStringList> execute(String key) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<WrapOutStringList> result = new ActionResult<>();
			WrapOutStringList wrap = new WrapOutStringList();
			key = this.getKey(key);
			if (StringUtils.isNotEmpty(key)) {
				EntityManager em = emc.get(DepartmentDuty.class);
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<String> cq = cb.createQuery(String.class);
				Root<DepartmentDuty> root = cq.from(DepartmentDuty.class);
				Predicate p = cb.like(root.get(DepartmentDuty_.name), "%" + key + "%", '\\');
				p = cb.or(p, cb.like(root.get(DepartmentDuty_.pinyin), key + "%", '\\'));
				p = cb.or(p, cb.like(root.get(DepartmentDuty_.pinyinInitial), key + "%", '\\'));
				cq.select(root.get(DepartmentDuty_.name)).distinct(true).where(p);
				List<String> list = em.createQuery(cq).getResultList();
				for (String str : list) {
					if (StringUtils.isNotEmpty(str)) {
						wrap.getValueList().add(str);
					}
				}
				Collections.sort(wrap.getValueList());
			}
			result.setData(wrap);
			return result;
		}
	}

	private String getKey(String key) throws Exception {
		String str = key.replaceAll("_", "\\\\_");
		str = str.replaceAll("%", "\\\\%");
		str = str.toLowerCase();
		return str;
	}

}
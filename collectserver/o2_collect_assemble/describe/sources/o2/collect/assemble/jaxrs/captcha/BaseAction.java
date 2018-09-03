package o2.collect.assemble.jaxrs.captcha;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.Gson;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;

import o2.collect.assemble.Business;
import o2.collect.core.entity.Module;
import o2.collect.core.entity.Module_;

abstract class BaseAction extends StandardJaxrsAction {

	Gson gson = XGsonBuilder.instance();

	boolean nameExist(Business business, String name, String excludeId) throws Exception {
		EntityManager em = business.entityManagerContainer().get(Module.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Module> root = cq.from(Module.class);
		Predicate p = cb.equal(root.get(Module_.name), name);
		if (StringUtils.isNotEmpty(excludeId)) {
			p = cb.and(p, cb.notEqual(root.get(Module_.id), excludeId));
		}
		Long count = em.createQuery(cq.select(cb.count(root)).where(p)).getSingleResult();
		if (count > 0) {
			return true;
		} else {
			return false;
		}
	}
}

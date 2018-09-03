package o2.collect.assemble.jaxrs.code;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.base.core.project.jaxrs.StandardJaxrsAction;

import o2.collect.assemble.Business;
import o2.collect.core.entity.Account;
import o2.collect.core.entity.Account_;

abstract class BaseAction extends StandardJaxrsAction {

	boolean accountExist(Business business, String name) throws Exception {
		EntityManager em = business.entityManagerContainer().get(Account.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Account> root = cq.from(Account.class);
		Predicate p = cb.equal(root.get(Account_.name), name);
		cq.select(cb.count(root)).where(p);
		return em.createQuery(cq).getSingleResult() > 0;
	}

}

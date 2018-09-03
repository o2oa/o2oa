package o2.collect.assemble.factory;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import o2.collect.assemble.AbstractFactory;
import o2.collect.assemble.Business;
import o2.collect.core.entity.Account;
import o2.collect.core.entity.Account_;

public class AccountFactory extends AbstractFactory {

	public AccountFactory(Business business) throws Exception {
		super(business);
	}

	public List<String> listWithUnit(String unitId) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Account.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Account> root = cq.from(Account.class);
		Predicate p = cb.equal(root.get(Account_.unit), unitId);
		cq.select(root.get(Account_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}

	public String getWithNameUnit(String name, String unitId) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Account.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Account> root = cq.from(Account.class);
		Predicate p = cb.equal(root.get(Account_.unit), unitId);
		p = cb.and(p, cb.equal(root.get(Account_.name), name));
		cq.select(root.get(Account_.id)).where(p);
		List<String> list = em.createQuery(cq).getResultList();
		return (list.size() == 1) ? list.get(0) : null;
	}

	public Account getWithNameUnitObject(String name, String unitId) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Account.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Account> cq = cb.createQuery(Account.class);
		Root<Account> root = cq.from(Account.class);
		Predicate p = cb.equal(root.get(Account_.unit), unitId);
		p = cb.and(p, cb.equal(root.get(Account_.name), name));
		cq.select(root).where(p);
		List<Account> list = em.createQuery(cq).getResultList();
		return (list.size() == 1) ? list.get(0) : null;
	}

	public List<String> listWithName(String name) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Account.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Account> root = cq.from(Account.class);
		Predicate p = cb.equal(root.get(Account_.name), name);
		cq.select(root.get(Account_.id)).where(p).distinct(true);
		List<String> list = em.createQuery(cq).getResultList();
		return list;
	}
}
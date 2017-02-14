package com.x.organization.assemble.control.factory;

import java.util.Collection;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.base.core.utils.annotation.MethodDescribe;
import com.x.organization.assemble.control.AbstractFactory;
import com.x.organization.assemble.control.Business;
import com.x.organization.core.entity.Identity;
import com.x.organization.core.entity.Identity_;

public class IdentityFactory extends AbstractFactory {

	public IdentityFactory(Business business) throws Exception {
		super(business);
	}


	@MethodDescribe("根据指定的Person获取所有的Identity.")
	public List<String> listWithPerson(String id) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Identity.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Identity> root = cq.from(Identity.class);
		Predicate p = cb.equal(root.get(Identity_.person), id);
		cq.select(root.get(Identity_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}

	@MethodDescribe("在指定的部门范围内查找人员.")
	public List<String> listLikeWithDepartment(Collection<String> departments, String key) throws Exception {
		String str = key.replaceAll("_", "\\\\_");
		str = str.replaceAll("%", "\\\\%");
		str = str.toLowerCase();
		EntityManager em = this.entityManagerContainer().get(Identity.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Identity> root = cq.from(Identity.class);
		Predicate pa = cb.conjunction();
		for (String o : departments) {
			pa = cb.or(pa, cb.equal(root.get(Identity_.department), o));
		}
		Predicate pb = cb.like(root.get(Identity_.name), "%" + str + "%", '\\');
		pb = cb.or(pb, cb.like(root.get(Identity_.pinyin), str + "%", '\\'));
		pb = cb.or(pb, cb.like(root.get(Identity_.pinyinInitial), str + "%", '\\'));
		cq.select(root.get(Identity_.id)).where(cb.and(pa, pb));
		return em.createQuery(cq).getResultList();
	}

	@MethodDescribe("根据指定的Department获取所有的Identity.")
	public List<String> listSubDirectWithDepartment(String id) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Identity.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Identity> root = cq.from(Identity.class);
		Predicate p = cb.equal(root.get(Identity_.department), id);
		cq.select(root.get(Identity_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}

	@MethodDescribe("列示所有首字母开始的公司.")
	public List<String> listPinyinInitial(String key) throws Exception {
		String str = key.replaceAll("_", "\\\\_");
		str = str.replaceAll("%", "\\\\%");
		str = str.toLowerCase();
		EntityManager em = this.entityManagerContainer().get(Identity.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Identity> root = cq.from(Identity.class);
		Predicate p = cb.like(root.get(Identity_.pinyinInitial), str + "%", '\\');
		cq.select(root.get(Identity_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}

	@MethodDescribe("进行模糊查询.")
	public List<String> listLike(String key) throws Exception {
		String str = key.replaceAll("_", "\\\\_");
		str = str.replaceAll("%", "\\\\%");
		str = str.toLowerCase();
		EntityManager em = this.entityManagerContainer().get(Identity.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Identity> root = cq.from(Identity.class);
		Predicate p = cb.like(root.get(Identity_.name), "%" + str + "%", '\\');
		p = cb.or(p, cb.like(root.get(Identity_.pinyin), str + "%", '\\'));
		p = cb.or(p, cb.like(root.get(Identity_.pinyinInitial), str + "%", '\\'));
		cq.select(root.get(Identity_.id)).where(p);
		return em.createQuery(cq).setMaxResults(200).getResultList();
	}

	@MethodDescribe("根据拼音进行模糊查询.")
	public List<String> listLikePinyin(String key) throws Exception {
		String str = key.replaceAll("_", "\\\\_");
		str = str.replaceAll("%", "\\\\%");
		str = str.toLowerCase();
		EntityManager em = this.entityManagerContainer().get(Identity.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Identity> root = cq.from(Identity.class);
		Predicate p = cb.like(root.get(Identity_.pinyin), str + "%");
		p = cb.or(p, cb.like(root.get(Identity_.pinyinInitial), str + "%"));
		cq.select(root.get(Identity_.id)).where(p);
		return em.createQuery(cq).setMaxResults(200).getResultList();
	}

	@MethodDescribe("计算部门直接成员Identity数量.")
	public Long countSubDirectWithDepartment(String id) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Identity.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Identity> root = cq.from(Identity.class);
		Predicate p = cb.equal(root.get(Identity_.department), id);
		cq.select(cb.count(root)).where(p);
		return em.createQuery(cq).getSingleResult();
	}
}

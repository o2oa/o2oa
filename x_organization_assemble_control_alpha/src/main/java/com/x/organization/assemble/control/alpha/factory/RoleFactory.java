package com.x.organization.assemble.control.alpha.factory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.base.core.http.EffectivePerson;
import com.x.base.core.utils.annotation.MethodDescribe;
import com.x.organization.assemble.control.alpha.AbstractFactory;
import com.x.organization.assemble.control.alpha.Business;
import com.x.organization.core.entity.Role;
import com.x.organization.core.entity.Role_;

public class RoleFactory extends AbstractFactory {

	public RoleFactory(Business business) throws Exception {
		super(business);
	}

	public Role pick(String flag) throws Exception {
		return this.pick(flag, Role.class, Role.FLAGS);
	}

	public List<Role> batchPick(String... flags) throws Exception {
		List<Role> list = new ArrayList<>();
		Arrays.asList(flags).stream().forEach(s -> {
			try {
				Role o = this.pick(s);
				if (null != o) {
					list.add(o);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		return list;
	}

	@MethodDescribe("根据指定的Person获取所有的Role.")
	public List<String> listWithPerson(String id) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Role.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Role> root = cq.from(Role.class);
		Predicate p = cb.isMember(id, root.get(Role_.personList));
		cq.select(root.get(Role_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}

	@MethodDescribe("根据指定的Group获取所有的Role.")
	public List<String> listWithGroup(String id) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Role.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Role> root = cq.from(Role.class);
		Predicate p = cb.isMember(id, root.get(Role_.groupList));
		cq.select(root.get(Role_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}

	@MethodDescribe("列示所有首字母开始的公司.")
	public List<String> listPinyinInitial(String key) throws Exception {
		String str = key.replaceAll("_", "\\\\_");
		str = str.replaceAll("%", "\\\\%");
		str = str.toLowerCase();
		EntityManager em = this.entityManagerContainer().get(Role.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Role> root = cq.from(Role.class);
		Predicate p = cb.like(root.get(Role_.pinyinInitial), str + "%", '\\');
		cq.select(root.get(Role_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}

	@MethodDescribe("进行模糊查询.")
	public List<String> listLike(String key) throws Exception {
		String str = key.replaceAll("_", "\\\\_");
		str = str.replaceAll("%", "\\\\%");
		str = str.toLowerCase();
		EntityManager em = this.entityManagerContainer().get(Role.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Role> root = cq.from(Role.class);
		Predicate p = cb.like(root.get(Role_.name), "%" + str + "%", '\\');
		p = cb.or(p, cb.like(root.get(Role_.pinyin), str + "%", '\\'));
		p = cb.or(p, cb.like(root.get(Role_.pinyinInitial), str + "%", '\\'));
		cq.select(root.get(Role_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}

	@MethodDescribe("根据拼音进行模糊查询.")
	public List<String> listLikePinyin(String key) throws Exception {
		String str = key.replaceAll("_", "\\\\_");
		str = str.replaceAll("%", "\\\\%");
		str = str.toLowerCase();
		EntityManager em = this.entityManagerContainer().get(Role.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Role> root = cq.from(Role.class);
		Predicate p = cb.like(root.get(Role_.pinyin), str + "%");
		p = cb.or(p, cb.like(root.get(Role_.pinyinInitial), str + "%"));
		cq.select(root.get(Role_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}

	public String getWithName(String name) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Role.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Role> root = cq.from(Role.class);
		Predicate p = cb.equal(root.get(Role_.name), name);
		cq.select(root.get(Role_.id)).where(p);
		List<String> list = em.createQuery(cq).setMaxResults(1).getResultList();
		return list.isEmpty() ? null : list.get(0);
	}
}
package com.x.organization.assemble.express.factory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.ObjectUtils;

import com.x.base.core.utils.annotation.MethodDescribe;
import com.x.organization.assemble.express.AbstractFactory;
import com.x.organization.assemble.express.Business;
import com.x.organization.assemble.express.jaxrs.wrapout.WrapOutRole;
import com.x.organization.core.entity.Group;
import com.x.organization.core.entity.Person;
import com.x.organization.core.entity.Role;
import com.x.organization.core.entity.Role_;

public class RoleFactory extends AbstractFactory {

	public RoleFactory(Business business) throws Exception {
		super(business);
	}

	@MethodDescribe("根据名称查找Role")
	public String getWithName(String name) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Role.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Role> root = cq.from(Role.class);
		Predicate p = cb.equal(root.get(Role_.name), name);
		cq.select(root.get(Role_.id)).where(p);
		List<String> list = em.createQuery(cq).setMaxResults(1).getResultList();
		if (!list.isEmpty()) {
			return list.get(0);
		} else {
			return null;
		}
	}

	@MethodDescribe("根据指定的Group获取所有的Role.")
	public List<String> listWithGroup(String id) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Role.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Role> root = cq.from(Role.class);
		Predicate p = cb.isMember(id, root.get(Role_.groupList));
		cq.select(root.get(Role_.id)).where(p).distinct(true);
		return em.createQuery(cq).getResultList();
	}

	@MethodDescribe("根据指定的Person获取所有的Role.")
	public List<String> listWithPerson(String id) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Role.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Role> root = cq.from(Role.class);
		Predicate p = cb.isMember(id, root.get(Role_.personList));
		cq.select(root.get(Role_.id)).where(p).distinct(true);
		return em.createQuery(cq).getResultList();
	}

	@MethodDescribe("列示所有首字母开始的Role.")
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
		return em.createQuery(cq.where(p)).getResultList();
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
		return em.createQuery(cq.where(p)).getResultList();
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
		return em.createQuery(cq.where(p)).getResultList();
	}

	public WrapOutRole wrap(Role o) throws Exception {
		WrapOutRole wrap = new WrapOutRole();
		o.copyTo(wrap);
		List<String> personList = new ArrayList<>();
		if ((null != wrap.getPersonList()) && (!wrap.getPersonList().isEmpty())) {
			for (Person p : this.entityManagerContainer().fetchAttribute(o.getPersonList(), Person.class, "name")) {
				personList.add(p.getName());
			}
			Collections.sort(personList, new Comparator<String>() {
				public int compare(String o1, String o2) {
					return ObjectUtils.compare(o1, o2, true);
				}
			});
		}
		wrap.setPersonList(personList);
		List<String> groupList = new ArrayList<>();
		if (null != wrap.getGroupList() && (!wrap.getGroupList().isEmpty())) {
			for (Group g : this.entityManagerContainer().fetchAttribute(o.getGroupList(), Group.class, "name")) {
				groupList.add(g.getName());
			}
			Collections.sort(groupList, new Comparator<String>() {
				public int compare(String o1, String o2) {
					return ObjectUtils.compare(o1, o2, true);
				}
			});
		}
		wrap.setGroupList(groupList);
		return wrap;
	}
	

	public void sort(List<WrapOutRole> wraps) throws Exception {
		Collections.sort(wraps, new Comparator<WrapOutRole>() {
			public int compare(WrapOutRole o1, WrapOutRole o2) {
				return ObjectUtils.compare(o1.getName(), o2.getName(), true);
			}
		});
	}

}
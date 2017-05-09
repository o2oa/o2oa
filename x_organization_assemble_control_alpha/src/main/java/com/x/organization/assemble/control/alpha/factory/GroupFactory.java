package com.x.organization.assemble.control.alpha.factory;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.collections4.set.ListOrderedSet;

import com.x.base.core.exception.ExceptionWhen;
import com.x.base.core.utils.annotation.MethodDescribe;
import com.x.organization.assemble.control.alpha.AbstractFactory;
import com.x.organization.assemble.control.alpha.Business;
import com.x.organization.core.entity.Group;
import com.x.organization.core.entity.Group_;

public class GroupFactory extends AbstractFactory {

	public GroupFactory(Business business) throws Exception {
		super(business);
	}


	@MethodDescribe("获取指定指定群组所在的群组.")
	public List<String> listSupDirect(String id) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Group.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Group> root = cq.from(Group.class);
		Predicate p = cb.isMember(id, root.get(Group_.groupList));
		cq.select(root.get(Group_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}

	@MethodDescribe("获取指定指定群组所在的群组,并递归其上级群组.")
	public List<String> listSupNested(String id) throws Exception {
		ListOrderedSet<String> set = new ListOrderedSet<>();
		this.supNested(id, set);
		return set.asList();
	}

	@MethodDescribe("上级群组循环递归.")
	private void supNested(String id, ListOrderedSet<String> set) throws Exception {
		List<String> list = new ArrayList<String>();
		for (String str : this.listSupDirect(id)) {
			if (!set.contains(str)) {
				list.add(str);
			}
		}
		if (!list.isEmpty()) {
			set.addAll(list);
			for (String str : list) {
				this.supNested(str, set);
			}
		}
	}

	@MethodDescribe("获取指定个人直接所在的群组.")
	public List<String> listSupDirectWithPerson(String id) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Group.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Group> root = cq.from(Group.class);
		Predicate p = cb.isMember(id, root.get(Group_.personList));
		cq.select(root.get(Group_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}

	@MethodDescribe("获取指定个人所在的群组,并递归其上级群组.")
	public List<String> listSupNestedWithPerson(String id) throws Exception {
		ListOrderedSet<String> set = new ListOrderedSet<>();
		List<String> list = new ArrayList<>();
		for (String o : this.listSupDirectWithPerson(id)) {
			if (!set.contains(o)) {
				list.add(o);
			}
		}
		if (!list.isEmpty()) {
			set.addAll(list);
			for (String str : list) {
				this.supNested(str, set);
			}
		}
		return set.asList();
	}

	@MethodDescribe("查找群组的直接群组成员.")
	public List<String> listSubDirect(String id) throws Exception {
		Group group =	this.entityManagerContainer().find(id, Group.class, ExceptionWhen.none);
		EntityManager em = this.entityManagerContainer().get(Group.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Group> root = cq.from(Group.class);
		Predicate p = root.get(Group_.id).in(group.getGroupList());
		cq.select(root.get(Group_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}

	@MethodDescribe("查找群组的全部群组成员,包括嵌套的群组成员.")
	public List<String> listSubNested(String id) throws Exception {
		ListOrderedSet<String> set = new ListOrderedSet<String>();
		this.subNested(id, set);
		return set.asList();
	}

	@MethodDescribe("递归循环调用群组查找。.")
	private void subNested(String id, ListOrderedSet<String> set) throws Exception {
		List<String> list = new ArrayList<String>();
		for (String str : this.listSubDirect(id)) {
			if (!set.contains(str)) {
				list.add(str);
			}
		}
		if (!list.isEmpty()) {
			set.addAll(list);
			for (String str : list) {
				this.subNested(str, set);
			}
		}
	}

	@MethodDescribe("列示所有首字母开始的公司.")
	public List<String> listPinyinInitial(String key) throws Exception {
		String str = key.replaceAll("_", "\\\\_");
		str = str.replaceAll("%", "\\\\%");
		str = str.toLowerCase();
		EntityManager em = this.entityManagerContainer().get(Group.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Group> root = cq.from(Group.class);
		Predicate p = cb.like(root.get(Group_.pinyinInitial), str + "%", '\\');
		cq.select(root.get(Group_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}

	@MethodDescribe("进行模糊查询.")
	public List<String> listLike( String key) throws Exception {
		String str = key.replaceAll("_", "\\\\_");
		str = str.replaceAll("%", "\\\\%");
		str = str.toLowerCase();
		EntityManager em = this.entityManagerContainer().get(Group.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Group> root = cq.from(Group.class);
		Predicate p = cb.like(root.get(Group_.name), "%" + str + "%", '\\');
		p = cb.or(p, cb.like(root.get(Group_.pinyin), str + "%", '\\'));
		p = cb.or(p, cb.like(root.get(Group_.pinyinInitial), str + "%", '\\'));
		cq.select(root.get(Group_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}

	@MethodDescribe("根据拼音进行模糊查询.")
	public List<String> listLikePinyin(String key) throws Exception {
		String str = key.replaceAll("_", "\\\\_");
		str = str.replaceAll("%", "\\\\%");
		str = str.toLowerCase();
		EntityManager em = this.entityManagerContainer().get(Group.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Group> root = cq.from(Group.class);
		Predicate p = cb.like(root.get(Group_.pinyin), str + "%");
		p = cb.or(p, cb.like(root.get(Group_.pinyinInitial), str + "%"));
		cq.select(root.get(Group_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}
}
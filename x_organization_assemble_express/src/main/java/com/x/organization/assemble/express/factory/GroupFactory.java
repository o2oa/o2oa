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

import org.apache.commons.collections4.set.ListOrderedSet;
import org.apache.commons.lang3.ObjectUtils;

import com.x.base.core.exception.ExceptionWhen;
import com.x.base.core.utils.annotation.MethodDescribe;
import com.x.organization.assemble.express.AbstractFactory;
import com.x.organization.assemble.express.Business;
import com.x.organization.assemble.express.jaxrs.wrapout.WrapOutGroup;
import com.x.organization.core.entity.Group;
import com.x.organization.core.entity.Group_;
import com.x.organization.core.entity.Person;

public class GroupFactory extends AbstractFactory {

	public GroupFactory(Business business) throws Exception {
		super(business);
	}

	/* 根据名称查找Group */
	public String getWithName(String name) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Group.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Group> root = cq.from(Group.class);
		Predicate p = cb.equal(root.get(Group_.name), name);
		cq.select(root.get(Group_.id)).where(p);
		List<String> list = em.createQuery(cq).setMaxResults(1).getResultList();
		if (!list.isEmpty()) {
			return list.get(0);
		} else {
			return null;
		}
	}

	@MethodDescribe("获取指定个人直接所在的群组.")
	public List<String> listWithPersonSupDirect(String id) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Group.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Group> root = cq.from(Group.class);
		Predicate p = cb.isMember(id, root.get(Group_.personList));
		cq.select(root.get(Group_.id)).where(p).distinct(true);
		return em.createQuery(cq).getResultList();
	}

	@MethodDescribe("获取指定指定群组所在的群组.")
	public List<String> listSupDirect(String id) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Group.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Group> root = cq.from(Group.class);
		Predicate p = cb.isMember(id, root.get(Group_.groupList));
		cq.select(root.get(Group_.id)).where(p).distinct(true);
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
		List<String> list = new ArrayList<>();
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

	@MethodDescribe("查找群组的全部群组成员,包括嵌套的群组成员.")
	public List<String> listSubNested(String id) throws Exception {
		ListOrderedSet<String> set = new ListOrderedSet<>();
		this.subNested(id, set);
		return set.asList();
	}

	@MethodDescribe("查找群组的直接群组成员.")
	public List<String> listSubDirect(String id) throws Exception {
		Group group = this.entityManagerContainer().find(id, Group.class, ExceptionWhen.not_found);
		EntityManager em = this.entityManagerContainer().get(Group.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Group> root = cq.from(Group.class);
		Predicate p = root.get(Group_.id).in(group.getGroupList());
		cq.select(root.get(Group_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}

	@MethodDescribe("递归循环调用群组查找。.")
	private void subNested(String id, ListOrderedSet<String> set) throws Exception {
		List<String> list = new ArrayList<>();
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
	public List<String> listLike(String key) throws Exception {
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

	public WrapOutGroup wrap(Group o) throws Exception {
		WrapOutGroup wrap = new WrapOutGroup();
		o.copyTo(wrap);
		List<String> gs = new ArrayList<>();
		if (null != wrap.getGroupList() && (!wrap.getGroupList().isEmpty())) {
			for (Group g : this.entityManagerContainer().fetchAttribute(o.getGroupList(), Group.class, "name")) {
				gs.add(g.getName());
			}
		}
		wrap.setGroupList(gs);
		List<String> ps = new ArrayList<>();
		if (null != wrap.getPersonList() && (!wrap.getPersonList().isEmpty())) {
			for (Person p : this.entityManagerContainer().fetchAttribute(o.getPersonList(), Person.class, "name")) {
				ps.add(p.getName());
			}
		}
		wrap.setPersonList(ps);
		return wrap;
	}

	/* 进行排序 */
	public void sort(List<WrapOutGroup> wraps) throws Exception {
		Collections.sort(wraps, new Comparator<WrapOutGroup>() {
			public int compare(WrapOutGroup o1, WrapOutGroup o2) {
				return ObjectUtils.compare(o1.getName(), o2.getName(), true);
			}
		});
	}
}
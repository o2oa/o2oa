package com.x.organization.assemble.express.factory;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.collections4.set.ListOrderedSet;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.cache.Cache.CacheCategory;
import com.x.base.core.project.cache.Cache.CacheKey;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.exception.ExceptionWhen;
import com.x.base.core.project.tools.ListTools;
import com.x.organization.assemble.express.AbstractFactory;
import com.x.organization.assemble.express.Business;
import com.x.organization.core.entity.Group;
import com.x.organization.core.entity.Group_;
import com.x.organization.core.entity.Person;

public class GroupFactory extends AbstractFactory {

	private CacheCategory cacheCategory = new CacheCategory(Group.class);

	public GroupFactory(Business business) throws Exception {
		super(business);
	}

	public Group pick(String flag) throws Exception {
		if (StringUtils.isEmpty(flag)) {
			return null;
		}
		Group o = null;
		CacheKey cacheKey = new CacheKey(flag);
		Optional<?> optional = CacheManager.get(cacheCategory, cacheKey);
		if (optional.isPresent()) {
			o = (Group) optional.get();
		} else {
			o = this.pickObject(flag);
			if (o != null) {
				CacheManager.put(cacheCategory, cacheKey, o);
			}
		}
		return o;
	}

	public List<Group> pick(List<String> flags) throws Exception {
		List<Group> list = new ArrayList<>();
		for (String str : flags) {
			CacheKey cacheKey = new CacheKey(str);
			Optional<?> optional = CacheManager.get(cacheCategory, cacheKey);
			if (optional.isPresent()) {
				list.add((Group) optional.get());
			} else {
				Group o = this.pickObject(str);
				if (null != o) {
					CacheManager.put(cacheCategory, cacheKey, o);
					list.add(o);
				}
			}
		}
		return list;
	}

	private Group pickObject(String flag) throws Exception {
		Group o = this.entityManagerContainer().flag(flag, Group.class);
		if (o != null) {
			this.entityManagerContainer().get(Group.class).detach(o);
		} else {
			String name = flag;
			Matcher matcher = group_distinguishedName_pattern.matcher(flag);
			if (matcher.find()) {
				name = matcher.group(1);
				String unique = matcher.group(2);
				o = this.entityManagerContainer().flag(unique, Group.class);
				if (null != o) {
					this.entityManagerContainer().get(Group.class).detach(o);
				}
			}
			if (null == o) {
				EntityManager em = this.entityManagerContainer().get(Group.class);
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<Group> cq = cb.createQuery(Group.class);
				Root<Group> root = cq.from(Group.class);
				Predicate p = cb.equal(root.get(Group_.name), name);
				List<Group> os = em.createQuery(cq.select(root).where(p)).getResultList();
				if (os.size() == 1) {
					o = os.get(0);
					em.detach(o);
				}
			}
		}
		return o;
	}

	public <T extends Group> List<T> sort(List<T> list) {
		list = list.stream().sorted(
				Comparator.comparing(Group::getOrderNumber, Comparator.nullsLast(Integer::compareTo)).thenComparing(
						Comparator.comparing(Group::getName, Comparator.nullsFirst(String::compareTo)).reversed()))
				.collect(Collectors.toList());
		return list;
	}

	public List<Group> listSupDirectObject(Group group) throws Exception {
		List<String> ids = this.listSupDirect(group.getId());
		return this.entityManagerContainer().list(Group.class, ids);
	}

	// @MethodDescribe("获取指定指定群组所在的群组.")
	public List<String> listSupDirect(String id) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Group.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Group> root = cq.from(Group.class);
		Predicate p = cb.isMember(id, root.get(Group_.groupList));
		cq.select(root.get(Group_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}

	// @MethodDescribe("获取指定指定群组所在的群组,并递归其上级群组.")
	public List<String> listSupNested(String id) throws Exception {
		ListOrderedSet<String> set = new ListOrderedSet<>();
		this.supNested(id, set);
		return set.asList();
	}

	public List<Group> listSupNestedObject(Group group) throws Exception {
		List<String> ids = this.listSupNested(group.getId());
		return this.entityManagerContainer().list(Group.class, ids);
	}

	// @MethodDescribe("上级群组循环递归.")
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

	public List<Group> listSupDirectWithPersonObject(Person person) throws Exception {
		List<String> ids = this.listSupDirectWithPerson(person.getId());
		return this.entityManagerContainer().list(Group.class, ids);
	}

	// @MethodDescribe("获取指定个人直接所在的群组.")
	public List<String> listSupDirectWithPerson(String id) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Group.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Group> root = cq.from(Group.class);
		Predicate p = cb.isMember(id, root.get(Group_.personList));
		cq.select(root.get(Group_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}

	// @MethodDescribe("获取指定身份直接所在的群组.")
	public List<String> listSupDirectWithIdentity(String id) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Group.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Group> root = cq.from(Group.class);
		Predicate p = cb.isMember(id, root.get(Group_.identityList));
		cq.select(root.get(Group_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}

	// @MethodDescribe("获取指定组织直接所在的群组.")
	public List<String> listSupDirectWithUnit(String id) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Group.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Group> root = cq.from(Group.class);
		Predicate p = cb.isMember(id, root.get(Group_.unitList));
		cq.select(root.get(Group_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}

	public List<Group> listSupNestedWithPersonObject(Person person) throws Exception {
		List<String> ids = this.listSupNestedWithPerson(person.getId());
		return this.entityManagerContainer().list(Group.class, ids);
	}

	// @MethodDescribe("获取指定个人所在的群组,并递归其上级群组.")
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

	public List<Group> listSubDirectObject(Group group) throws Exception {
		List<String> ids = this.listSubDirect(group.getId());
		return this.entityManagerContainer().list(Group.class, ids);
	}

	public List<Group> listSubNestedObject(Group group) throws Exception {
		List<String> ids = this.listSubNested(group.getId());
		return this.entityManagerContainer().list(Group.class, ids);
	}

	// @MethodDescribe("查找群组的直接群组成员.")
	public List<String> listSubDirect(String id) throws Exception {
		Group group = this.entityManagerContainer().find(id, Group.class, ExceptionWhen.none);
		EntityManager em = this.entityManagerContainer().get(Group.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Group> root = cq.from(Group.class);
		Predicate p = root.get(Group_.id).in(group.getGroupList());
		cq.select(root.get(Group_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}

	// @MethodDescribe("查找群组的全部群组成员,包括嵌套的群组成员.")
	public List<String> listSubNested(String id) throws Exception {
		ListOrderedSet<String> set = new ListOrderedSet<String>();
		this.subNested(id, set);
		return set.asList();
	}

	// @MethodDescribe("递归循环调用群组查找。.")
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

	public List<String> listGroupDistinguishedNameSorted(List<String> groupIds) throws Exception {
		List<Group> list = this.entityManagerContainer().list(Group.class, groupIds);
		list = this.sort(list);
		List<String> values = ListTools.extractProperty(list, JpaObject.DISTINGUISHEDNAME, String.class, true, true);
		return values;
	}

}
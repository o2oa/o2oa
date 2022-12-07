package com.x.organization.assemble.express.factory;

import java.util.*;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.cache.Cache.CacheCategory;
import com.x.base.core.project.cache.Cache.CacheKey;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.tools.ListTools;
import com.x.organization.assemble.express.AbstractFactory;
import com.x.organization.assemble.express.Business;
import com.x.organization.core.entity.Person;
import com.x.organization.core.entity.Person_;

public class PersonFactory extends AbstractFactory {

	private CacheCategory cacheCategory = new CacheCategory(Person.class);

	public PersonFactory(Business business) throws Exception {
		super(business);
	}

	public Person pick(String flag) throws Exception {
		if (StringUtils.isEmpty(flag)) {
			return null;
		}
		Person o = null;
		CacheKey cacheKey = new CacheKey(Person.class.getName(), flag);
		Optional<?> optional = CacheManager.get(cacheCategory, cacheKey);
		if (optional.isPresent()) {
			o = (Person) optional.get();
		} else {
			o = this.pickObject(flag);
			if (null != o) {
				CacheManager.put(cacheCategory, cacheKey, o);
			}
		}
		return o;
	}

	private Person pickObject(String flag) throws Exception {
		if (StringUtils.isEmpty(flag)) {
			return null;
		}
		Person o = this.entityManagerContainer().flag(flag, Person.class);
		if (o != null) {
			this.entityManagerContainer().get(Person.class).detach(o);
		} else {
			// String name = flag;
			Matcher matcher = person_distinguishedName_pattern.matcher(flag);
			if (matcher.find()) {
				// name = matcher.group(1);
				String unique = matcher.group(2);
				o = this.entityManagerContainer().flag(unique, Person.class);
				if (null != o) {
					this.entityManagerContainer().get(Person.class).detach(o);
				}
			}
//			if ((null == o) && BooleanUtils.isTrue(Config.organization().getPickPersonWithName())) {
//				EntityManager em = this.entityManagerContainer().get(Person.class);
//				CriteriaBuilder cb = em.getCriteriaBuilder();
//				CriteriaQuery<Person> cq = cb.createQuery(Person.class);
//				Root<Person> root = cq.from(Person.class);
//				Predicate p = cb.equal(root.get(Person_.name), name);
//				List<Person> os = em.createQuery(cq.select(root).where(p)).getResultList();
//				if (os.size() == 1) {
//					o = os.get(0);
//					em.detach(o);
//				}
//			}
		}
		return o;
	}

	public List<Person> pick(List<String> flags) throws Exception {
		List<Person> list = new ArrayList<>();
		for (String str : ListTools.trim(flags, true, false)) {
			CacheKey cacheKey = new CacheKey(Person.class.getName(), str);
			Optional<?> optional = CacheManager.get(cacheCategory, cacheKey);
			if (optional.isPresent()) {
				list.add((Person) optional.get());
			} else {
				Person o = this.pickObject(str);
				if (null != o) {
					CacheManager.put(cacheCategory, cacheKey, o);
					list.add(o);
				}
			}
		}
		return list;
	}

	public List<Person> pick(List<String> flags, Boolean useNameFind) throws Exception {
		List<Person> list = new ArrayList<>();
		for (String str : ListTools.trim(flags, true, false)) {
			CacheKey cacheKey = new CacheKey(Person.class.getName(), str);
			Optional<?> optional = CacheManager.get(cacheCategory, cacheKey);
			if (optional.isPresent()) {
				list.add((Person) optional.get());
			} else {
				Person o = this.pickObject(str);
				if (null != o) {
					CacheManager.put(cacheCategory, cacheKey, o);
					list.add(o);
				}
			}
		}
		if(list.isEmpty() && BooleanUtils.isTrue(useNameFind)){
			list = this.listWithName(flags);
		}
		return list;
	}

	public List<Person> listWithName(List<String> names) throws Exception {
		if(ListTools.isEmpty(names)){
			return Collections.EMPTY_LIST;
		}
		EntityManager em = this.entityManagerContainer().get(Person.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Person> cq = cb.createQuery(Person.class);
		Root<Person> root = cq.from(Person.class);
		Predicate p;
		if(names.size() > 1){
			p = root.get(Person_.name).in(names);
		}else{
			p = cb.equal(root.get(Person_.name), names.get(0));
		}
		return em.createQuery(cq.select(root).where(p)).getResultList().stream().distinct().collect(Collectors.toList());
	}

	public <T extends Person> List<T> sort(List<T> list) {
		list = list.stream().sorted(
				Comparator.comparing(Person::getOrderNumber, Comparator.nullsLast(Integer::compareTo)).thenComparing(
						Comparator.comparing(Person::getName, Comparator.nullsFirst(String::compareTo)).reversed()))
				.collect(Collectors.toList());
		return list;
	}

	public String getSupDirect(String id) throws Exception {
		if (StringUtils.isEmpty(id)) {
			return null;
		}
		Person person = this.pick(id);
		if (null == person) {
			return null;
		}
		if (StringUtils.isEmpty(person.getSuperior())) {
			return null;
		}
		Person superior = this.pick(person.getSuperior());
		if (null == superior) {
			return null;
		} else {
			return superior.getId();
		}
	}

	/** 递归的上级组织,从底层到顶层 */
	public List<String> listSupNested(String id) throws Exception {
		List<String> list = new ArrayList<>();
		if (StringUtils.isEmpty(id)) {
			return list;
		}
		Person person = this.pick(id);
		if (null == person) {
			return list;
		}
		if (StringUtils.isEmpty(person.getSuperior())) {
			return list;
		}
		this.supNested(person.getId(), list);
		return list;
	}

	private void supNested(String id, List<String> list) throws Exception {
		String superior = this.getSupDirect(id);
		if ((StringUtils.isNotEmpty(superior)) && (!list.contains(superior))) {
			list.add(superior);
			this.supNested(superior, list);
		}
	}

	public List<String> listSubDirect(String id) throws Exception {
		List<String> list = new ArrayList<>();
		if (StringUtils.isEmpty(id)) {
			return list;
		}
		Person person = this.pick(id);
		if (null == person) {
			return list;
		}
		EntityManager em = this.entityManagerContainer().get(Person.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Person> root = cq.from(Person.class);
		Predicate p = cb.equal(root.get(Person_.superior), person.getId());
		list = em.createQuery(cq.select(root.get(Person_.id)).where(p)).getResultList().stream().distinct()
				.collect(Collectors.toList());
		return list;
	}

	public List<String> listSubNested(String id) throws Exception {
		List<String> list = new ArrayList<>();
		if (StringUtils.isEmpty(id)) {
			return list;
		}
		Person person = this.pick(id);
		if (null == person) {
			return list;
		}
		this.subNested(person.getId(), list);
		return list;
	}

	private void subNested(String id, List<String> list) throws Exception {
		List<String> ids = new ArrayList<>();
		for (String o : this.listSubDirect(id)) {
			if (!list.contains(o)) {
				ids.add(o);
			}
		}
		if (!ids.isEmpty()) {
			list.addAll(ids);
			for (String o : ids) {
				this.subNested(o, list);
			}
		}
	}

	public List<String> listPersonDistinguishedNameSorted(List<String> personIds) throws Exception {
		List<Person> list = this.entityManagerContainer().list(Person.class, personIds);
		list = this.sort(list);
		List<String> values = ListTools.extractProperty(list, JpaObject.DISTINGUISHEDNAME, String.class, true, true);
		return values;
	}
}

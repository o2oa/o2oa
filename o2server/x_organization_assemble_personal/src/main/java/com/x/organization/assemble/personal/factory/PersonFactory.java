package com.x.organization.assemble.personal.factory;

import java.util.ArrayList;
import java.util.Calendar;
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

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.entity.tools.JpaObjectTools;
import com.x.base.core.project.cache.Cache.CacheKey;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.tools.Crypto;
import com.x.organization.assemble.personal.AbstractFactory;
import com.x.organization.assemble.personal.Business;
import com.x.organization.core.entity.PersistenceProperties;
import com.x.organization.core.entity.Person;
import com.x.organization.core.entity.Person_;

public class PersonFactory extends AbstractFactory {

	public PersonFactory(Business business) throws Exception {
		super(business);
	}

	public Person pick(String flag) throws Exception {
		if (StringUtils.isEmpty(flag)) {
			return null;
		}
		Person o = null;
		CacheKey cacheKey = new CacheKey(this.getClass(), flag);
		Optional<?> optional = CacheManager.get(business.cache(), cacheKey);
		if (optional.isPresent()) {
			o = (Person) optional.get();
		} else {
			o = this.pickObject(flag);
			CacheManager.put(business.cache(), cacheKey, o);
		}
		return o;
	}

	private Person pickObject(String flag) throws Exception {
		Person o = this.entityManagerContainer().flag(flag, Person.class);
		if (o != null) {
			this.entityManagerContainer().get(Person.class).detach(o);
		} else {
			String name = flag;
			Matcher matcher = PersistenceProperties.Person.distinguishedName_pattern.matcher(flag);
			if (matcher.find()) {
				name = matcher.group(1);
				String unique = matcher.group(2);
				o = this.entityManagerContainer().flag(unique, Person.class);
				if (null != o) {
					this.entityManagerContainer().get(Person.class).detach(o);
				}
			}
			if (null == o) {
				EntityManager em = this.entityManagerContainer().get(Person.class);
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<Person> cq = cb.createQuery(Person.class);
				Root<Person> root = cq.from(Person.class);
				Predicate p = cb.equal(root.get(Person_.name), name);
				List<Person> os = em.createQuery(cq.select(root).where(p)).getResultList();
				if (os.size() == 1) {
					o = os.get(0);
					em.detach(o);
				}
			}
		}
		return o;
	}

	public List<Person> pick(List<String> flags) throws Exception {
		List<Person> list = new ArrayList<>();
		for (String str : flags) {
			CacheKey cacheKey = new CacheKey(str);
			Optional<?> optional = CacheManager.get(business.cache(), cacheKey);
			if (optional.isPresent()) {
				list.add((Person) optional.get());
			} else {
				Person o = this.pickObject(str);
				CacheManager.put(business.cache(), cacheKey, o);
				if (null != o) {
					list.add(o);
				}
			}
		}
		return list;
	}

	public <T extends Person> List<T> sort(List<T> list) {
		list = list.stream()
				.sorted(Comparator.comparing(Person::getOrderNumber, Comparator.nullsLast(Integer::compareTo))
						.thenComparing(Comparator.comparing(Person::getName, Comparator.nullsLast(String::compareTo))))
				.collect(Collectors.toList());
		return list;
	}

	public Person getWithCredential(String value) throws Exception {
		Person person = this.pick(value);
		if (null == person) {
			EntityManager em = this.entityManagerContainer().get(Person.class);
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Person> cq = cb.createQuery(Person.class);
			Root<Person> root = cq.from(Person.class);
			Predicate p = cb.equal(root.get(Person_.mobile), value);
			p = cb.or(p, cb.equal(root.get(Person_.qq), value));
			p = cb.or(p, cb.equal(root.get(Person_.mail), value));
			p = cb.or(p, cb.equal(root.get(Person_.weixin), value));
			List<Person> os = em.createQuery(cq.select(root).where(p)).getResultList();
			if (os.size() == 1) {
				person = os.get(0);
			}
		}
		return person;
	}

	public String getWithName(String name, String excludeId) throws Exception {
		if (StringUtils.isEmpty(name) || (!JpaObjectTools.withinDefinedLength(name, Person.class, "name"))) {
			return null;
		}
		EntityManager em = this.entityManagerContainer().get(Person.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Person> root = cq.from(Person.class);
		Predicate p = cb.equal(root.get(Person_.name), name);
		if (StringUtils.isNotEmpty(excludeId)) {
			p = cb.and(p, cb.notEqual(root.get(Person_.id), excludeId));
		}
		cq.select(root.get(Person_.id)).where(p);
		List<String> list = em.createQuery(cq).getResultList();
		return this.returnNotDuplicateId(list);
	}

	public String getWithMobile(String mobile, String excludeId) throws Exception {
		if (StringUtils.isEmpty(mobile) || (!JpaObjectTools.withinDefinedLength(mobile, Person.class, "mobile"))) {
			return null;
		}
		EntityManager em = this.entityManagerContainer().get(Person.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Person> root = cq.from(Person.class);
		Predicate p = cb.equal(root.get(Person_.mobile), mobile);
		if (StringUtils.isNotEmpty(excludeId)) {
			p = cb.and(p, cb.notEqual(root.get(Person_.id), excludeId));
		}
		cq.select(root.get(Person_.id)).where(p);
		List<String> list = em.createQuery(cq).getResultList();
		return this.returnNotDuplicateId(list);
	}

	public String getWithEmployee(String employee, String excludeId) throws Exception {
		if (StringUtils.isEmpty(employee)
				|| (!JpaObjectTools.withinDefinedLength(employee, Person.class, "employee"))) {
			return null;
		}
		EntityManager em = this.entityManagerContainer().get(Person.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Person> root = cq.from(Person.class);
		Predicate p = cb.equal(root.get(Person_.employee), employee);
		if (StringUtils.isNotEmpty(excludeId)) {
			p = cb.and(p, cb.notEqual(root.get(Person_.id), excludeId));
		}
		cq.select(root.get(Person_.id)).where(p);
		List<String> list = em.createQuery(cq).getResultList();
		return this.returnNotDuplicateId(list);
	}

	public String getWithUnique(String unique, String excludeId) throws Exception {
		if (StringUtils.isEmpty(unique) || (!JpaObjectTools.withinDefinedLength(unique, Person.class, "unique"))) {
			return null;
		}
		EntityManager em = this.entityManagerContainer().get(Person.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Person> root = cq.from(Person.class);
		Predicate p = cb.equal(root.get(Person_.unique), unique);
		if (StringUtils.isNotEmpty(excludeId)) {
			p = cb.and(p, cb.notEqual(root.get(Person_.id), excludeId));
		}
		cq.select(root.get(Person_.id)).where(p);
		List<String> list = em.createQuery(cq).getResultList();
		return this.returnNotDuplicateId(list);
	}

	public String getWithQq(String qq, String excludeId) throws Exception {
		if (StringUtils.isEmpty(qq) || (!JpaObjectTools.withinDefinedLength(qq, Person.class, "qq"))) {
			return null;
		}
		EntityManager em = this.entityManagerContainer().get(Person.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Person> root = cq.from(Person.class);
		Predicate p = cb.equal(root.get(Person_.qq), qq);
		if (StringUtils.isNotEmpty(excludeId)) {
			p = cb.and(p, cb.notEqual(root.get(Person_.id), excludeId));
		}
		cq.select(root.get(Person_.id)).where(p);
		List<String> list = em.createQuery(cq).getResultList();
		return this.returnNotDuplicateId(list);
	}

	public String getWithMail(String mail, String excludeId) throws Exception {
		if (StringUtils.isEmpty(mail) || (!JpaObjectTools.withinDefinedLength(mail, Person.class, "mail"))) {
			return null;
		}
		EntityManager em = this.entityManagerContainer().get(Person.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Person> root = cq.from(Person.class);
		Predicate p = cb.equal(root.get(Person_.mail), mail);
		if (StringUtils.isNotEmpty(excludeId)) {
			p = cb.and(p, cb.notEqual(root.get(Person_.id), excludeId));
		}
		cq.select(root.get(Person_.id)).where(p);
		List<String> list = em.createQuery(cq).getResultList();
		return this.returnNotDuplicateId(list);
	}

	public String getWithWeixin(String weixin, String excludeId) throws Exception {
		if (StringUtils.isEmpty(weixin) || (!JpaObjectTools.withinDefinedLength(weixin, Person.class, "weixin"))) {
			return null;
		}
		EntityManager em = this.entityManagerContainer().get(Person.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Person> root = cq.from(Person.class);
		Predicate p = cb.equal(root.get(Person_.weixin), weixin);
		if (StringUtils.isNotEmpty(excludeId)) {
			p = cb.and(p, cb.notEqual(root.get(Person_.id), excludeId));
		}
		cq.select(root.get(Person_.id)).where(p);
		List<String> list = em.createQuery(cq).getResultList();
		return this.returnNotDuplicateId(list);
	}

	private String returnNotDuplicateId(List<String> list) throws Exception {
		if (list.isEmpty()) {
			return null;
		} else if (list.size() == 1) {
			return list.get(0);
		}
		throw new Exception("find duplicate value{" + StringUtils.join(list, ",") + "}");
	}

	public void setPassword(Person person, String password) throws Exception {
		Calendar cal = Calendar.getInstance();
		person.setChangePasswordTime(cal.getTime());
		person.setPassword(Crypto.encrypt(password, Config.token().getKey(), Config.person().getEncryptType()));
		Integer passwordPeriod = Config.person().getPasswordPeriod();
		if (passwordPeriod == null || passwordPeriod <= 0) {
			person.setPasswordExpiredTime(null);
		} else {
			cal.add(Calendar.DATE, passwordPeriod);
			person.setPasswordExpiredTime(cal.getTime());
		}
	}

}
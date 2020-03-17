package com.x.organization.assemble.control.factory;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.entity.tools.JpaObjectTools;
import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.tools.Crypto;
import com.x.organization.assemble.control.AbstractFactory;
import com.x.organization.assemble.control.Business;
import com.x.organization.core.entity.PersistenceProperties;
import com.x.organization.core.entity.Person;
import com.x.organization.core.entity.Person_;

import net.sf.ehcache.Element;

public class PersonFactory extends AbstractFactory {

	public PersonFactory(Business business) throws Exception {
		super(business);
		cache = ApplicationCache.instance().getCache(Person.class);
	}

	public Person pick(String flag) throws Exception {
		if (StringUtils.isEmpty(flag)) {
			return null;
		}
		Person o = null;
		Element element = cache.get(flag);
		if (null != element) {
			if (null != element.getObjectValue()) {
				o = (Person) element.getObjectValue();
			}
		} else {
			o = this.pickObject(flag);
			cache.put(new Element(flag, o));
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
				List<Person> os = em.createQuery(cq.select(root).where(p).distinct(true)).getResultList();
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
			Element element = cache.get(str);
			if (null != element) {
				if (null != element.getObjectValue()) {
					list.add((Person) element.getObjectValue());
				}
			} else {
				Person o = this.pickObject(str);
				cache.put(new Element(str, o));
				if (null != o) {
					list.add(o);
				}
			}
		}
		return list;
	}

	public <T extends Person> List<T> sort(List<T> list) {
		list = list.stream().sorted(
				Comparator.comparing(Person::getOrderNumber, Comparator.nullsLast(Integer::compareTo)).thenComparing(
						Comparator.comparing(Person::getName, Comparator.nullsFirst(String::compareTo)).reversed()))
				.collect(Collectors.toList());
		return list;
	}

	public String getWithName(String name, String excludeId) throws Exception {
		if (StringUtils.isEmpty(name)
				|| (!JpaObjectTools.withinDefinedLength(name, Person.class, Person.name_FIELDNAME))) {
			return null;
		}
		List<String> list = this.entityManagerContainer().idsEqualAndNotEqual(Person.class, Person.name_FIELDNAME, name,
				Person.id_FIELDNAME, excludeId);
		return this.returnNotDuplicateId(list);
	}

	public String getWithMobile(String mobile, String excludeId) throws Exception {
		if (StringUtils.isEmpty(mobile)
				|| (!JpaObjectTools.withinDefinedLength(mobile, Person.class, Person.mobile_FIELDNAME))) {
			return null;
		}
		List<String> list = this.entityManagerContainer().idsEqualAndNotEqual(Person.class, Person.mobile_FIELDNAME,
				mobile, Person.id_FIELDNAME, excludeId);
		return this.returnNotDuplicateId(list);
	}

	public String getWithEmployee(String employee, String excludeId) throws Exception {
		if (StringUtils.isEmpty(employee)
				|| (!JpaObjectTools.withinDefinedLength(employee, Person.class, Person.employee_FIELDNAME))) {
			return null;
		}
		List<String> list = this.entityManagerContainer().idsEqualAndNotEqual(Person.class, Person.employee_FIELDNAME,
				employee, Person.id_FIELDNAME, excludeId);
		return this.returnNotDuplicateId(list);
	}

	public String getWithUnique(String unique, String excludeId) throws Exception {
		if (StringUtils.isEmpty(unique)
				|| (!JpaObjectTools.withinDefinedLength(unique, Person.class, Person.unique_FIELDNAME))) {
			return null;
		}
		List<String> list = this.entityManagerContainer().idsEqualAndNotEqual(Person.class, Person.unique_FIELDNAME,
				unique, Person.id_FIELDNAME, excludeId);
		return this.returnNotDuplicateId(list);
	}

	public String getWithQq(String qq, String excludeId) throws Exception {
		if (StringUtils.isEmpty(qq) || (!JpaObjectTools.withinDefinedLength(qq, Person.class, Person.qq_FIELDNAME))) {
			return null;
		}
		List<String> list = this.entityManagerContainer().idsEqualAndNotEqual(Person.class, Person.qq_FIELDNAME, qq,
				Person.id_FIELDNAME, excludeId);
		return this.returnNotDuplicateId(list);
	}

	public String getWithMail(String mail, String excludeId) throws Exception {
		if (StringUtils.isEmpty(mail)
				|| (!JpaObjectTools.withinDefinedLength(mail, Person.class, Person.mail_FIELDNAME))) {
			return null;
		}
		List<String> list = this.entityManagerContainer().idsEqualAndNotEqual(Person.class, Person.mail_FIELDNAME, mail,
				Person.id_FIELDNAME, excludeId);
		return this.returnNotDuplicateId(list);
	}

	public String getWithWeixin(String weixin, String excludeId) throws Exception {
		if (StringUtils.isEmpty(weixin)
				|| (!JpaObjectTools.withinDefinedLength(weixin, Person.class, Person.weixin_FIELDNAME))) {
			return null;
		}
		List<String> list = this.entityManagerContainer().idsEqualAndNotEqual(Person.class, Person.mail_FIELDNAME,
				weixin, Person.id_FIELDNAME, excludeId);
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
		person.setPassword(Crypto.encrypt(password, Config.token().getKey()));
		Integer passwordPeriod = Config.person().getPasswordPeriod();
		if (passwordPeriod == null || passwordPeriod <= 0) {
			person.setPasswordExpiredTime(null);
		} else {
			cal.add(Calendar.DATE, passwordPeriod);
			person.setPasswordExpiredTime(cal.getTime());
		}
	}

//	public Person getWi1thDingdingIdObject(String dingdingId) throws Exception {
//		EntityManager em = this.entityManagerContainer().get(Person.class);
//		CriteriaBuilder cb = em.getCriteriaBuilder();
//		CriteriaQuery<Person> cq = cb.createQuery(Person.class);
//		Root<Person> root = cq.from(Person.class);
//		Predicate p = cb.equal(root.get(Person_.dingdingId), dingdingId);
//		List<Person> os = em.createQuery(cq.select(root).where(p)).setMaxResults(1).getResultList();
//		if (os.isEmpty()) {
//			return null;
//		} else {
//			return os.get(0);
//		}
//	}
//
//	public Person getWithQiyeweixinIdObject(String dingdingId) throws Exception {
//		EntityManager em = this.entityManagerContainer().get(Person.class);
//		CriteriaBuilder cb = em.getCriteriaBuilder();
//		CriteriaQuery<Person> cq = cb.createQuery(Person.class);
//		Root<Person> root = cq.from(Person.class);
//		Predicate p = cb.equal(root.get(Person_.qiyeweixinId), dingdingId);
//		List<Person> os = em.createQuery(cq.select(root).where(p)).setMaxResults(1).getResultList();
//		if (os.isEmpty()) {
//			return null;
//		} else {
//			return os.get(0);
//		}
//	}
}
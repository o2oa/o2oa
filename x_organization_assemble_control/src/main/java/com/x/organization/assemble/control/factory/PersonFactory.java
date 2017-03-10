package com.x.organization.assemble.control.factory;

import java.util.Calendar;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.Crypto;
import com.x.base.core.entity.tools.JpaObjectTools;
import com.x.base.core.project.server.Config;
import com.x.base.core.utils.annotation.MethodDescribe;
import com.x.organization.assemble.control.AbstractFactory;
import com.x.organization.assemble.control.Business;
import com.x.organization.core.entity.Person;
import com.x.organization.core.entity.Person_;

public class PersonFactory extends AbstractFactory {

	public PersonFactory(Business business) throws Exception {
		super(business);
	}

	@MethodDescribe("列示所有首字母开始的个人.")
	public List<String> listPinyinInitial(String key) throws Exception {
		String str = key.replaceAll("_", "\\\\_");
		str = str.replaceAll("%", "\\\\%");
		str = str.toLowerCase();
		EntityManager em = this.entityManagerContainer().get(Person.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Person> root = cq.from(Person.class);
		Predicate p = cb.like(root.get(Person_.pinyinInitial), str + "%", '\\');
		cq.select(root.get(Person_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}

	@MethodDescribe("进行模糊查询.")
	public List<String> listLike(String key) throws Exception {
		String str = key.replaceAll("_", "\\\\_");
		str = str.replaceAll("%", "\\\\%");
		str = str.toLowerCase();
		EntityManager em = this.entityManagerContainer().get(Person.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Person> root = cq.from(Person.class);
		Predicate p = cb.like(root.get(Person_.name), "%" + str + "%", '\\');
		p = cb.or(p, cb.like(root.get(Person_.pinyin), str + "%", '\\'));
		p = cb.or(p, cb.like(root.get(Person_.pinyinInitial), str + "%", '\\'));
		p = cb.or(p, cb.like(root.get(Person_.mobile), str + "%", '\\'));
		cq.select(root.get(Person_.id)).where(p);
		return em.createQuery(cq).setMaxResults(200).getResultList();
	}

	@MethodDescribe("根据拼音进行模糊查询.")
	public List<String> listLikePinyin(String key) throws Exception {
		String str = key.replaceAll("_", "\\\\_");
		str = str.replaceAll("%", "\\\\%");
		str = str.toLowerCase();
		EntityManager em = this.entityManagerContainer().get(Person.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Person> root = cq.from(Person.class);
		Predicate p = cb.like(root.get(Person_.pinyin), str + "%");
		p = cb.or(p, cb.like(root.get(Person_.pinyinInitial), str + "%"));
		cq.select(root.get(Person_.id)).where(p);
		return em.createQuery(cq).setMaxResults(200).getResultList();
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

	public String getWithDisplay(String display, String excludeId) throws Exception {
		if (StringUtils.isEmpty(display) || (!JpaObjectTools.withinDefinedLength(display, Person.class, "display"))) {
			return null;
		}
		EntityManager em = this.entityManagerContainer().get(Person.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Person> root = cq.from(Person.class);
		Predicate p = cb.equal(root.get(Person_.display), display);
		if (StringUtils.isNotEmpty(excludeId)) {
			p = cb.and(p, cb.notEqual(root.get(Person_.id), excludeId));
		}
		cq.select(root.get(Person_.id)).where(p);
		List<String> list = em.createQuery(cq).getResultList();
		return this.returnNotDuplicateId(list);
	}

	public String returnNotDuplicateId(List<String> list) throws Exception {
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

}
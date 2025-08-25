package com.x.program.center.factory;

import java.util.Calendar;
import java.util.List;

import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.config.Config;
import com.x.base.core.project.tools.Crypto;
import com.x.organization.core.entity.Person;
import com.x.organization.core.entity.Person_;
import com.x.program.center.AbstractFactory;
import com.x.program.center.Business;

public class PersonFactory extends AbstractFactory {

	public PersonFactory(Business business) throws Exception {
		super(business);
	}

	public Person getWithUnique(String id) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Person.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Person> cq = cb.createQuery(Person.class);
		Root<Person> root = cq.from(Person.class);
		Predicate p = cb.equal(root.get(Person_.unique), id);
		List<Person> os = em.createQuery(cq.select(root).where(p)).setMaxResults(1).getResultList();
		if (os.isEmpty()) {
			return null;
		} else {
			return os.get(0);
		}
	}

	public Person getWithCredential(String credential) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Person.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Person> cq = cb.createQuery(Person.class);
		Root<Person> root = cq.from(Person.class);
		Predicate p = cb.equal(root.get(Person_.name), credential);
		p = cb.or(p, cb.equal(root.get(Person_.distinguishedName), credential));
		p = cb.or(p, cb.equal(root.get(Person_.unique), credential));
		p = cb.or(p, cb.equal(root.get(Person_.id), credential));
		p = cb.or(p, cb.equal(root.get(Person_.mail), credential));
		p = cb.or(p, cb.equal(root.get(Person_.qq), credential));
		p = cb.or(p, cb.equal(root.get(Person_.weixin), credential));
		p = cb.or(p, cb.equal(root.get(Person_.mobile), credential));
		p = cb.or(p, cb.equal(root.get(Person_.employee), credential));
		p = cb.or(p, cb.equal(root.get(Person_.mpwxopenId), credential));
		p = cb.or(p, cb.equal(root.get(Person_.qiyeweixinId), credential));
		p = cb.or(p, cb.equal(root.get(Person_.dingdingId), credential));
		p = cb.or(p, cb.equal(root.get(Person_.open1Id), credential));
		p = cb.or(p, cb.equal(root.get(Person_.open2Id), credential));
		p = cb.or(p, cb.equal(root.get(Person_.open3Id), credential));
		p = cb.or(p, cb.equal(root.get(Person_.open4Id), credential));
		p = cb.or(p, cb.equal(root.get(Person_.open5Id), credential));
		if(BooleanUtils.isTrue(Config.person().getPersonEncryptEnable())){
			String enStr = Person.ENCRYPT + Crypto.base64Encode(credential);
			p = cb.or(p, cb.equal(root.get(Person_.mobile), enStr));
		}
		;
		List<Person> list = em.createQuery(cq.select(root).where(p)).getResultList().stream().distinct().collect(
				Collectors.toList());
		if (list.size() == 1) {
			return list.get(0);
		} else {
			return null;
		}
	}

	public Person getWithAndFxIdObject(String id) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Person.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Person> cq = cb.createQuery(Person.class);
		Root<Person> root = cq.from(Person.class);
		Predicate p = cb.equal(root.get(Person_.andFxId), id);
		List<Person> os = em.createQuery(cq.select(root).where(p)).setMaxResults(1).getResultList();
		if (os.isEmpty()) {
			return null;
		} else {
			return os.get(0);
		}
	}

	public Person getWithDingdingIdObject(String dingdingId) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Person.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Person> cq = cb.createQuery(Person.class);
		Root<Person> root = cq.from(Person.class);
		Predicate p = cb.equal(root.get(Person_.dingdingId), dingdingId);
		List<Person> os = em.createQuery(cq.select(root).where(p)).setMaxResults(1).getResultList();
		if (os.isEmpty()) {
			return null;
		} else {
			return os.get(0);
		}
	}

	public Person getWithWeLinkIdObject(String weLinkId) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Person.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Person> cq = cb.createQuery(Person.class);
		Root<Person> root = cq.from(Person.class);
		Predicate p = cb.equal(root.get(Person_.weLinkId), weLinkId);
		List<Person> os = em.createQuery(cq.select(root).where(p)).setMaxResults(1).getResultList();
		if (os.isEmpty()) {
			return null;
		} else {
			return os.get(0);
		}
	}

	public Person getWithZhengwuDingdingIdObject(String zhengwuDingdingId) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Person.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Person> cq = cb.createQuery(Person.class);
		Root<Person> root = cq.from(Person.class);
		Predicate p = cb.equal(root.get(Person_.zhengwuDingdingId), zhengwuDingdingId);
		List<Person> os = em.createQuery(cq.select(root).where(p)).setMaxResults(1).getResultList();
		if (os.isEmpty()) {
			return null;
		} else {
			return os.get(0);
		}
	}

	public Person getWithQiyeweixinIdObject(String qiyeweixinId) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Person.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Person> cq = cb.createQuery(Person.class);
		Root<Person> root = cq.from(Person.class);
		Predicate p = cb.equal(root.get(Person_.qiyeweixinId), qiyeweixinId);
		List<Person> os = em.createQuery(cq.select(root).where(p)).setMaxResults(1).getResultList();
		if (os.isEmpty()) {
			return null;
		} else {
			return os.get(0);
		}
	}

	public void setPassword(Person person, String password) throws Exception {
		Calendar cal = Calendar.getInstance();
		person.setChangePasswordTime(null);
		person.setPassword(Crypto.encrypt(password, Config.token().getKey(), Config.person().getEncryptType()));
		Integer passwordPeriod = Config.person().getPasswordPeriod();
		if (passwordPeriod == null || passwordPeriod <= 0) {
			person.setPasswordExpiredTime(null);
		} else {
			cal.add(Calendar.DATE, passwordPeriod);
			person.setPasswordExpiredTime(cal.getTime());
		}
	}

	public boolean employeeExists(String employee, String excludeUnique) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Person.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Person> cq = cb.createQuery(Person.class);
		Root<Person> root = cq.from(Person.class);
		Predicate p = cb.equal(root.get(Person_.employee), employee);
		if (StringUtils.isNotEmpty(excludeUnique)) {
			p = cb.and(p, cb.notEqual(root.get(Person_.unique), excludeUnique));
		}
		List<Person> os = em.createQuery(cq.select(root).where(p)).setMaxResults(1).getResultList();
		if (os.isEmpty()) {
			return false;
		}
		return true;
	}

}

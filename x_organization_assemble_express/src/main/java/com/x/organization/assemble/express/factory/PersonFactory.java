package com.x.organization.assemble.express.factory;

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
import com.x.organization.assemble.express.jaxrs.wrapout.WrapOutPerson;
import com.x.organization.core.entity.Identity;
import com.x.organization.core.entity.Identity_;
import com.x.organization.core.entity.Person;
import com.x.organization.core.entity.Person_;

public class PersonFactory extends AbstractFactory {

	public PersonFactory(Business business) throws Exception {
		super(business);
	}

	@MethodDescribe("根据用户名查找Person")
	public String getWithName(String name) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Person.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Person> root = cq.from(Person.class);
		Predicate p = cb.equal(root.get(Person_.name), name);
		cq.select(root.get(Person_.id)).where(p).distinct(true);
		List<String> list = em.createQuery(cq).setMaxResults(1).getResultList();
		if (!list.isEmpty()) {
			return list.get(0);
		} else {
			return null;
		}
	}

	public List<String> listWithIdentity(List<String> ids) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Identity.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Identity> root = cq.from(Identity.class);
		Predicate p = root.get(Identity_.id).in(ids);
		cq.select(root.get(Identity_.person)).where(p).distinct(true);
		List<String> list = em.createQuery(cq).getResultList();
		return list;
	}

	@MethodDescribe("根据用户的唯一标识来查找用户，可以是id,name,unique,employee")
	public String getWithCredential(String credential) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Person.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Person> root = cq.from(Person.class);
		Predicate p = cb.equal(root.get(Person_.name), credential);
		p = cb.or(p, cb.equal(root.get(Person_.id), credential));
		p = cb.or(p, cb.equal(root.get(Person_.employee), credential));
		p = cb.or(p, cb.equal(root.get(Person_.unique), credential));
		cq.select(root.get(Person_.id)).where(p).distinct(true);
		List<String> list = em.createQuery(cq).setMaxResults(1).getResultList();
		if (!list.isEmpty()) {
			return list.get(0);
		} else {
			return null;
		}
	}

	@MethodDescribe("列示所有首字母开始的公司.")
	public List<String> listPinyinInitial(String key) throws Exception {
		String str = key.replaceAll("_", "\\\\_");
		str = str.replaceAll("%", "\\\\%");
		str = str.toLowerCase();
		EntityManager em = this.entityManagerContainer().get(Person.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Person> root = cq.from(Person.class);
		Predicate p = cb.like(root.get(Person_.pinyinInitial), str + "%", '\\');
		cq.select(root.get(Person_.id)).where(p).distinct(true);
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
		cq.select(root.get(Person_.id)).where(p).distinct(true);
		return em.createQuery(cq).getResultList();
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
		cq.select(root.get(Person_.id)).where(p).distinct(true);
		return em.createQuery(cq).getResultList();
	}

	// public WrapOutPerson wrap(Person o) throws Exception {
	// WrapOutPerson wrap = new WrapOutPerson();
	// o.copyTo(wrap);
	// return wrap;
	// }

	public void sort(List<WrapOutPerson> wraps) throws Exception {
		Collections.sort(wraps, new Comparator<WrapOutPerson>() {
			public int compare(WrapOutPerson o1, WrapOutPerson o2) {
				return ObjectUtils.compare(o1.getName(), o2.getName(), true);
			}
		});
	}
}
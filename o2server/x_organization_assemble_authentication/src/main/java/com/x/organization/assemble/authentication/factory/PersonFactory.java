package com.x.organization.assemble.authentication.factory;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;

import com.x.organization.assemble.authentication.AbstractFactory;
import com.x.organization.assemble.authentication.Business;
import com.x.organization.assemble.authentication.CacheFactory;
import com.x.organization.core.entity.PersistenceProperties;
import com.x.organization.core.entity.Person;
import com.x.organization.core.entity.Person_;

import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;

public class PersonFactory extends AbstractFactory {

	private Ehcache cache;

	public PersonFactory(Business business) throws Exception {
		super(business);
		this.cache = CacheFactory.getPersonCache();
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

	public String getWithCredential(String credential) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Person.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
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
		p = cb.or(p, cb.equal(root.get(Person_.open1Id), credential));
		p = cb.or(p, cb.equal(root.get(Person_.open2Id), credential));
		p = cb.or(p, cb.equal(root.get(Person_.open3Id), credential));
		p = cb.or(p, cb.equal(root.get(Person_.open4Id), credential));
		p = cb.or(p, cb.equal(root.get(Person_.open5Id), credential));
		cq.select(root.get(Person_.id));
		List<String> list = em.createQuery(cq.where(p).distinct(true)).getResultList();
		if (list.size() == 1) {
			return list.get(0);
		} else {
			return null;
		}
	}
}
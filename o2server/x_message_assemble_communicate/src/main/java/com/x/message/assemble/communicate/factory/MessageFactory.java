package com.x.message.assemble.communicate.factory;

import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.message.assemble.communicate.AbstractFactory;
import com.x.message.assemble.communicate.Business;
import com.x.organization.core.entity.Person;
import com.x.organization.core.entity.Person_;

public class MessageFactory extends AbstractFactory {

	public MessageFactory(Business business) throws Exception {
		super(business);
	}


	/**
	 * 查询用户对象
	 * @param credential
	 * @return
	 * @throws Exception
	 */
	public Person getPersonWithCredential(String credential) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Person.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Person> cq = cb.createQuery(Person.class);
		Root<Person> root = cq.from(Person.class);
		Predicate p = cb.equal(root.get(Person_.distinguishedName), credential);
		p = cb.or(p, cb.equal(root.get(Person_.unique), credential));
		p = cb.or(p, cb.equal(root.get(Person_.id), credential));
		p = cb.or(p, cb.equal(root.get(Person_.mobile), credential));
		p = cb.or(p, cb.equal(root.get(Person_.open1Id), credential));
		p = cb.or(p, cb.equal(root.get(Person_.open2Id), credential));
		p = cb.or(p, cb.equal(root.get(Person_.open3Id), credential));
		p = cb.or(p, cb.equal(root.get(Person_.open4Id), credential));
		p = cb.or(p, cb.equal(root.get(Person_.open5Id), credential));
		cq.select(root).where(p);
		List<Person> list = em.createQuery(cq).getResultList().stream().distinct().collect(Collectors.toList());
		if(list.size() > 0){
			return list.get(0);
		}else {
			return null;
		}
	}
}
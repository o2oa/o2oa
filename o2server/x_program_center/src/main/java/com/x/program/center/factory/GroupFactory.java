package com.x.program.center.factory;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.organization.core.entity.Group;
import com.x.organization.core.entity.Group_;
import com.x.program.center.AbstractFactory;
import com.x.program.center.Business;

public class GroupFactory extends AbstractFactory {

	public GroupFactory(Business business) throws Exception {
		super(business);
	}

	/**
	 * 获取指定身份直接所在的群组
	 * @param identity
	 * @return
	 * @throws Exception
	 */
	public List<String> listSupDirectWithIdentity(String identity) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Group.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Group> root = cq.from(Group.class);
		Predicate p = cb.isMember(identity, root.get(Group_.identityList));
		cq.select(root.get(Group_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}

	/**
	 * 获取指定身份直接所在的群组对象
	 * @param identity
	 * @return
	 * @throws Exception
	 */
	public List<Group> listSupDirectWithIdentityObject(String identity) throws Exception {
		List<String> ids = this.listSupDirectWithIdentity(identity);
		return this.entityManagerContainer().list(Group.class, ids);
	}

	/**
	 * 获取指定用户直接所在的群组
	 * @param personId
	 * @return
	 * @throws Exception
	 */
	public List<String> listSupDirectWithPerson(String personId) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Group.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Group> root = cq.from(Group.class);
		Predicate p = cb.isMember(personId, root.get(Group_.personList));
		cq.select(root.get(Group_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}

	/**
	 * 获取指定用户直接所在的群组对象
	 * @param personId
	 * @return
	 * @throws Exception
	 */
	public List<Group> listSupDirectWithPersonObject(String personId) throws Exception {
		List<String> ids = this.listSupDirectWithPerson(personId);
		return this.entityManagerContainer().list(Group.class, ids);
	}

}

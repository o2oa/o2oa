package com.x.file.assemble.control.factory;

import com.x.file.assemble.control.AbstractFactory;
import com.x.file.assemble.control.Business;
import com.x.file.core.entity.personal.Share;
import com.x.file.core.entity.personal.Share_;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;

public class ShareFactory extends AbstractFactory {

	public ShareFactory(Business business) throws Exception {
		super(business);
	}

	public List<Share> listWithPerson(String person, String shareType) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Share.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Share> cq = cb.createQuery(Share.class);
		Root<Share> root = cq.from(Share.class);
		Predicate p = cb.equal(root.get(Share_.person), person);
		if(StringUtils.isNotBlank(shareType)){
			p = cb.and(p, cb.equal(root.get(Share_.shareType), shareType));
		}
		return em.createQuery(cq.where(p)).getResultList();
	}

	public List<Share> listWithShareUser(String person) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Share.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Share> cq = cb.createQuery(Share.class);
		Root<Share> root = cq.from(Share.class);
		Predicate p = cb.isMember(person, root.get(Share_.shareUserList));
		return em.createQuery(cq.where(p)).getResultList();
	}

	public List<String> listWithShareUser1(String person) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Share.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Share> root = cq.from(Share.class);
		Predicate p = cb.isMember(person, root.get(Share_.shareUserList));
		cq.select(root.get(Share_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}

	public List<Share> listWithShareOrg(String org) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Share.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Share> cq = cb.createQuery(Share.class);
		Root<Share> root = cq.from(Share.class);
		Predicate p = cb.isMember(org, root.get(Share_.shareOrgList));
		return em.createQuery(cq.where(p)).getResultList();
	}

	public List<String> listWithShareOrg1(String org) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Share.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Share> root = cq.from(Share.class);
		Predicate p = cb.isMember(org, root.get(Share_.shareOrgList));
		cq.select(root.get(Share_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}

	public Share getShareByFileId(String fileId, String person) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Share.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Share> cq = cb.createQuery(Share.class);
		Root<Share> root = cq.from(Share.class);
		Predicate p = cb.equal(root.get(Share_.person), person);
		p = cb.and(p, cb.equal(root.get(Share_.fileId), fileId));
		List<Share> shareList = em.createQuery(cq.where(p)).setMaxResults(1).getResultList();
		if(shareList!=null && !shareList.isEmpty()){
			return shareList.get(0);
		}
		return null;
	}

	public List<String> listWithShieldUser1(String person) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Share.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Share> root = cq.from(Share.class);
		Predicate p = cb.isMember(person, root.get(Share_.shieldUserList));
		cq.select(root.get(Share_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}


}
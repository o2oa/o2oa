package com.x.pan.assemble.control.factory;

import com.x.pan.assemble.control.AbstractFactory;
import com.x.pan.assemble.control.Business;
import com.x.pan.core.entity.Recycle3;
import com.x.pan.core.entity.Recycle3_;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;

/**
 * @author sword
 */
public class Recycle3Factory extends AbstractFactory {

	public Recycle3Factory(Business business) throws Exception {
		super(business);
	}

	public List<Recycle3> listWithPerson(String person) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Recycle3.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Recycle3> cq = cb.createQuery(Recycle3.class);
		Root<Recycle3> root = cq.from(Recycle3.class);
		Predicate p = cb.equal(root.get(Recycle3_.person), person);
		return em.createQuery(cq.where(p)).getResultList();
	}

	public Recycle3 getByFileId(String fileId, String person) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Recycle3.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Recycle3> cq = cb.createQuery(Recycle3.class);
		Root<Recycle3> root = cq.from(Recycle3.class);
		Predicate p = cb.equal(root.get(Recycle3_.person), person);
		p = cb.and(p, cb.equal(root.get(Recycle3_.fileId), fileId));
		List<Recycle3> recycleList = em.createQuery(cq.where(p)).setMaxResults(1).getResultList();
		if(recycleList!=null && !recycleList.isEmpty()){
			return recycleList.get(0);
		}
		return null;
	}


}

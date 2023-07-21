package com.x.processplatform.service.processing.factory;

import com.x.processplatform.service.processing.AbstractFactory;
import com.x.processplatform.service.processing.Business;

public class WorkCompletedFactory extends AbstractFactory {

	public WorkCompletedFactory(Business business) throws Exception {
		super(business);
	}

//	public List<String> listWithJob(String job) throws Exception {
//		EntityManager em = this.entityManagerContainer().get(WorkCompleted.class);
//		CriteriaBuilder cb = em.getCriteriaBuilder();
//		CriteriaQuery<String> cq = cb.createQuery(String.class);
//		Root<WorkCompleted> root = cq.from(WorkCompleted.class);
//		Predicate p = cb.equal(root.get(WorkCompleted_.job), job);
//		cq.select(root.get(WorkCompleted_.id)).where(p);
//		return em.createQuery(cq.where(p)).getResultList();
//	}
	


}
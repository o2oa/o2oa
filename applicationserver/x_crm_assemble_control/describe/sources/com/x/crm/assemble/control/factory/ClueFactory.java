package com.x.crm.assemble.control.factory;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import com.x.crm.assemble.control.AbstractFactory;
import com.x.crm.assemble.control.Business;
import com.x.crm.core.entity.Clue;
import com.x.crm.core.entity.Clue_;


public class ClueFactory extends AbstractFactory {

	public ClueFactory(Business business) throws Exception {
		super(business);
		// TODO Auto-generated constructor stub
	}
	//列出所有线索id
	public List<String> listAllids() throws Exception {
		EntityManager em = this.entityManagerContainer().get(Clue.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Clue> root = cq.from(Clue.class);
		cq.select(root.get(Clue_.id));
		return em.createQuery(cq).getResultList();
	}
	
}

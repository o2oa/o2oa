package com.x.crm.assemble.control.factory;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.crm.assemble.control.AbstractFactory;
import com.x.crm.assemble.control.Business;
import com.x.crm.core.entity.Opportunity;
import com.x.crm.core.entity.Opportunity_;

public class OpportunityFactory extends AbstractFactory {

	public OpportunityFactory(Business business) throws Exception {
		super(business);
		// TODO Auto-generated constructor stub
	}

	//列出所有商机id
	public List<String> listAll() throws Exception {
		EntityManager em = this.entityManagerContainer().get(Opportunity.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Opportunity> root = cq.from(Opportunity.class);
		cq.select(root.get(Opportunity_.id));
		return em.createQuery(cq).getResultList();
	}

	//根据客户id列出对应商机id
	public List<String> ListOpportunityByCustomerId(String _customerid) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Opportunity.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Opportunity> root = cq.from(Opportunity.class);
		Predicate p = cb.equal(root.get(Opportunity_.customerid), _customerid);
		cq.select(root.get(Opportunity_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}

	//根据客户id返回商机是否存在 boolean
	public boolean IsExistById(String id) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Opportunity.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Opportunity> root = cq.from(Opportunity.class);
		Predicate p = cb.equal(root.get(Opportunity_.id), id);
		cq.select(root.get(Opportunity_.id)).where(p);
		List<String> _tmpList = new ArrayList<>();
		_tmpList = em.createQuery(cq).getResultList();
		int _tmpSize = _tmpList.size();
		if (_tmpSize > 0) {
			return true;
		} else {
			return false;
		}
	}

}

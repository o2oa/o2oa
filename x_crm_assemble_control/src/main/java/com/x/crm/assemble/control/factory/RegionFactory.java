package com.x.crm.assemble.control.factory;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.x.crm.assemble.control.AbstractFactory;
import com.x.crm.assemble.control.Business;
import com.x.crm.core.entity.CrmRegion;
import com.x.crm.core.entity.CrmRegion_;

public class RegionFactory extends AbstractFactory {
	private Logger logger = LoggerFactory.getLogger(RegionFactory.class);

	public RegionFactory(Business business) throws Exception {
		super(business);
		// TODO Auto-generated constructor stub
	}

	//列出直辖市，省份
	public List<CrmRegion> listProvince() throws Exception {
		EntityManager em = this.entityManagerContainer().get(CrmRegion.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<CrmRegion> cq = cb.createQuery(CrmRegion.class);
		Root<CrmRegion> root = cq.from(CrmRegion.class);
		//直辖市，省份 leveltype 为"1"
		Predicate p = cb.equal(root.get(CrmRegion_.leveltype), "1");
		cq.select(root).where(p);
		return em.createQuery(cq).getResultList();
	}

	//根据省份的parentid 获得城市
	public List<CrmRegion> listCity(String pid) throws Exception {
		EntityManager em = this.entityManagerContainer().get(CrmRegion.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<CrmRegion> cq = cb.createQuery(CrmRegion.class);
		Root<CrmRegion> root = cq.from(CrmRegion.class);
		//城市 leveltype 为"2"
		logger.error("pid:" + pid + " :");
		Predicate p = cb.equal(root.get(CrmRegion_.leveltype), "2");
		p = cb.and(p, cb.equal(root.get(CrmRegion_.parentid), pid));
		//Predicate p = cb.and(cb.equal(root.get(CrmRegion_.leveltype), "2"), cb.equal(root.get(CrmRegion_.parentid), pid));
		cq.select(root).where(p);
		return em.createQuery(cq).getResultList();
	}
	
	//根据城市的parentid 获得区县
	public List<CrmRegion> listCounty(String pid) throws Exception {
		EntityManager em = this.entityManagerContainer().get(CrmRegion.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<CrmRegion> cq = cb.createQuery(CrmRegion.class);
		Root<CrmRegion> root = cq.from(CrmRegion.class);
		//区县 leveltype 为"3"
		logger.error("pid:" + pid + " :");
		Predicate p = cb.equal(root.get(CrmRegion_.leveltype), "3");
		p = cb.and(p, cb.equal(root.get(CrmRegion_.parentid), pid));
		//Predicate p = cb.and(cb.equal(root.get(CrmRegion_.leveltype), "2"), cb.equal(root.get(CrmRegion_.parentid), pid));
		cq.select(root).where(p);
		return em.createQuery(cq).getResultList();
	}
	
}

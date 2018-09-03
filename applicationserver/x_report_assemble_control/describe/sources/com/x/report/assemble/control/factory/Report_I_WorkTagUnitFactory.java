package com.x.report.assemble.control.factory;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.report.assemble.control.AbstractFactory;
import com.x.report.assemble.control.Business;
import com.x.report.core.entity.Report_I_WorkTagUnit;
import com.x.report.core.entity.Report_I_WorkTagUnit_;

/**
 * 战略举措信息服务类
 * @author O2LEE
 */
public class Report_I_WorkTagUnitFactory extends AbstractFactory {

	public Report_I_WorkTagUnitFactory(Business business) throws Exception {
		super(business);
	}

	public List<Report_I_WorkTagUnit> list( List<String> ids ) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Report_I_WorkTagUnit.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Report_I_WorkTagUnit> cq = cb.createQuery(Report_I_WorkTagUnit.class);
		Root<Report_I_WorkTagUnit> root = cq.from(Report_I_WorkTagUnit.class);
		Predicate p = root.get( Report_I_WorkTagUnit_.id ).in(ids);
		return em.createQuery(cq.where(p)).getResultList();
	}
	
	public List<String> listWithTag( String tagName ) throws Exception {
		EntityManager em = this.entityManagerContainer().get( Report_I_WorkTagUnit.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery( String.class );
		Root<Report_I_WorkTagUnit> root = cq.from( Report_I_WorkTagUnit.class );
		Predicate p = cb.equal( root.get( Report_I_WorkTagUnit_.tagName ), tagName );
		cq.select( root.get( Report_I_WorkTagUnit_.id ) );
		return em.createQuery(cq.where(p)).getResultList();
	}
	
	public List<String> listWithUnit( String unitName ) throws Exception {
		EntityManager em = this.entityManagerContainer().get( Report_I_WorkTagUnit.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery( String.class );
		Root<Report_I_WorkTagUnit> root = cq.from( Report_I_WorkTagUnit.class );
		Predicate p = cb.equal( root.get( Report_I_WorkTagUnit_.unitName), unitName );
		cq.select( root.get( Report_I_WorkTagUnit_.id ) );
		return em.createQuery(cq.where(p)).getResultList();
	}
	
	public List<String> listTagIdsWithUnit( String unitName ) throws Exception {
		EntityManager em = this.entityManagerContainer().get( Report_I_WorkTagUnit.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery( String.class );
		Root<Report_I_WorkTagUnit> root = cq.from( Report_I_WorkTagUnit.class );
		Predicate p = cb.equal( root.get( Report_I_WorkTagUnit_.unitName), unitName );
		cq.select( root.get( Report_I_WorkTagUnit_.tagId ) );
		return em.createQuery(cq.where(p)).getResultList();
	}
	
	public List<String> listWithTagAndUnit( String tagName, String unitName ) throws Exception {
		EntityManager em = this.entityManagerContainer().get( Report_I_WorkTagUnit.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery( String.class );
		Root<Report_I_WorkTagUnit> root = cq.from( Report_I_WorkTagUnit.class );
		Predicate p = cb.equal( root.get( Report_I_WorkTagUnit_.tagName ), tagName );
		p = cb.and( p,  cb.equal( root.get( Report_I_WorkTagUnit_.unitName ), unitName ));
		cq.select( root.get( Report_I_WorkTagUnit_.id ) );
		return em.createQuery(cq.where(p)).getResultList();
	}
}
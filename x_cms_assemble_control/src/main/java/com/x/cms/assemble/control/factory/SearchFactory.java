package com.x.cms.assemble.control.factory;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.base.core.utils.annotation.MethodDescribe;
import com.x.cms.assemble.control.AbstractFactory;
import com.x.cms.assemble.control.Business;
import com.x.cms.core.entity.Document;
import com.x.cms.core.entity.Document_;

public class SearchFactory extends AbstractFactory {
	
	public SearchFactory(Business business) throws Exception {
		super(business);
	}

	@MethodDescribe("查询用户可见的指定状态下的文件涉及的部门名称列表")
	public List<String> listDistinctDepartmentyFromDocument( List<String> appids, String docStatus, String catagoryId ) throws Exception {
		if( appids == null ){
			return null;
		}				
		EntityManager em = this.entityManagerContainer().get( Document.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery( String.class );
		Root<Document> root = cq.from( Document.class );
		Predicate p = root.get( Document_.appId ).in( appids );
		p = cb.and(p, cb.equal( root.get( Document_.docStatus), docStatus ));
		if( catagoryId != null && !catagoryId.isEmpty() ){
			p = cb.and(p, cb.equal( root.get( Document_.catagoryId), catagoryId ));
		}
		cq.distinct(true).select(root.get( Document_.creatorDepartment ) );
		return em.createQuery( cq.where( p )).setMaxResults(500).getResultList();
	}
	
	@MethodDescribe("查询用户可见的指定状态下的文件涉及的公司列表量")
	public List<String> listDistinctCompanyFromDocument( List<String> appids, String docStatus, String catagoryId ) throws Exception {
		if( appids == null ){
			return null;
		}				
		EntityManager em = this.entityManagerContainer().get( Document.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery( String.class );
		Root<Document> root = cq.from( Document.class );
		Predicate p = root.get( Document_.appId ).in( appids );
		p = cb.and(p, cb.equal( root.get( Document_.docStatus), docStatus ));
		if( catagoryId != null && !catagoryId.isEmpty() ){
			p = cb.and(p, cb.equal( root.get( Document_.catagoryId), catagoryId ));
		}
		cq.distinct(true).select(root.get( Document_.creatorCompany ) );
		return em.createQuery( cq.where( p )).setMaxResults(500).getResultList();
	}
	
	@MethodDescribe("查询用户可见的指定状态下的文件涉及的栏目ID列表量")
	public List<String> listDistinctAppInfoFromDocument( List<String> appids, String docStatus, String catagoryId ) throws Exception {
		if( appids == null ){
			return null;
		}				
		EntityManager em = this.entityManagerContainer().get( Document.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery( String.class );
		Root<Document> root = cq.from( Document.class );
		Predicate p = root.get( Document_.appId ).in( appids );
		p = cb.and(p, cb.equal( root.get( Document_.docStatus), docStatus ));
		if( catagoryId != null && !catagoryId.isEmpty() ){
			p = cb.and(p, cb.equal( root.get( Document_.catagoryId), catagoryId ));
		}
		cq.distinct(true).select(root.get( Document_.appId ) );
		return em.createQuery( cq.where( p )).setMaxResults(500).getResultList();
	}
	
	@MethodDescribe("查询用户可见的指定状态下的文件涉及的分类ID列表")
	public List<String> listDistinctCatagoryFromDocument( List<String> appids, String docStatus, String catagoryId ) throws Exception {
		if( appids == null ){
			return null;
		}				
		EntityManager em = this.entityManagerContainer().get( Document.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery( String.class );
		Root<Document> root = cq.from( Document.class );
		Predicate p = root.get( Document_.appId ).in( appids );
		p = cb.and(p, cb.equal( root.get( Document_.docStatus), docStatus ));
		if( catagoryId != null && !catagoryId.isEmpty() ){
			p = cb.and(p, cb.equal( root.get( Document_.catagoryId), catagoryId ));
		}
		cq.distinct(true).select(root.get( Document_.catagoryId ) );
		return em.createQuery( cq.where( p )).setMaxResults(500).getResultList();
	}
	
	@MethodDescribe("根据catagoryId查询该应用栏目下的所有文档数量")
	public Long getDeparmentyDocumentCount( List<String> appids, String creatorCompany, String docStatus, String targetCatagoryId) throws Exception {
		if( creatorCompany == null ){
			return null;
		}				
		EntityManager em = this.entityManagerContainer().get( Document.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery( Long.class );
		Root<Document> root = cq.from( Document.class );
		Predicate p = cb.equal( root.get( Document_.creatorDepartment), creatorCompany );
		p = cb.and(p, cb.equal( root.get( Document_.docStatus), docStatus ));
		if( targetCatagoryId != null && !targetCatagoryId.isEmpty()){
			p = cb.and(p, cb.equal( root.get( Document_.catagoryId), targetCatagoryId ));
		}
		p = cb.and(p, root.get( Document_.appId).in(appids));
		cq.select(cb.count(root)).where(p);
		return em.createQuery(cq).getSingleResult();
	}
	
	@MethodDescribe("根据catagoryId查询该应用栏目下的所有文档数量")
	public Long getCompanyDocumentCount( List<String> appids, String creatorCompany, String docStatus, String targetCatagoryId) throws Exception {
		if( creatorCompany == null ){
			return null;
		}				
		EntityManager em = this.entityManagerContainer().get( Document.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery( Long.class );
		Root<Document> root = cq.from( Document.class );
		Predicate p = cb.equal( root.get( Document_.creatorCompany), creatorCompany );
		p = cb.and(p, cb.equal( root.get( Document_.docStatus), docStatus ));
		if( targetCatagoryId != null && !targetCatagoryId.isEmpty()){
			p = cb.and(p, cb.equal( root.get( Document_.catagoryId), targetCatagoryId ));
		}
		p = cb.and(p, root.get( Document_.appId).in(appids));
		cq.select(cb.count(root)).where(p);
		return em.createQuery(cq).getSingleResult();
	}
	
	@MethodDescribe("根据appId查询该应用栏目下的所有文档数量")
	public Long getAppInfoDocumentCount( List<String> appids, String appId, String docStatus, String targetCatagoryId) throws Exception {
		if( appId == null ){
			return null;
		}				
		EntityManager em = this.entityManagerContainer().get( Document.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery( Long.class );
		Root<Document> root = cq.from( Document.class );
		Predicate p = cb.equal( root.get( Document_.appId), appId);
		p = cb.and(p, cb.equal( root.get( Document_.docStatus), docStatus ));
		if( targetCatagoryId != null && !targetCatagoryId.isEmpty()){
			p = cb.and(p, cb.equal( root.get( Document_.catagoryId), targetCatagoryId ));
		}
		p = cb.and(p, root.get( Document_.appId).in(appids));
		cq.select(cb.count(root)).where(p);
		return em.createQuery(cq).getSingleResult();
	}
	
	@MethodDescribe("根据catagoryId查询该应用栏目下的所有文档数量")
	public Long getCatagoryInfoDocumentCount( List<String> appids, String catagoryId, String docStatus ) throws Exception {
		if( catagoryId == null ){
			return null;
		}				
		EntityManager em = this.entityManagerContainer().get( Document.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery( Long.class );
		Root<Document> root = cq.from( Document.class );
		Predicate p = cb.equal( root.get( Document_.catagoryId), catagoryId);
		p = cb.and(p, cb.equal( root.get( Document_.docStatus), docStatus ));
		p = cb.and(p, root.get( Document_.appId).in(appids));
		cq.select(cb.count(root)).where(p);
		return em.createQuery(cq).getSingleResult();
	}
	
}
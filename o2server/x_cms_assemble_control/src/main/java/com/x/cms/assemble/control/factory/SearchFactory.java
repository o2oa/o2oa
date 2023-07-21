package com.x.cms.assemble.control.factory;

import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;

import com.x.cms.assemble.control.AbstractFactory;
import com.x.cms.assemble.control.Business;
import com.x.cms.core.entity.Document;
import com.x.cms.core.entity.Document_;

public class SearchFactory extends AbstractFactory {
	
	public SearchFactory(Business business) throws Exception {
		super(business);
	}

	//@MethodDescribe("查询用户可见的指定状态下的文件涉及的组织名称列表")
	public List<String> listDistinctUnitNameFromDocument( List<String> appids, String docStatus, String categoryId ) throws Exception {
		if( appids == null ){
			return null;
		}				
		EntityManager em = this.entityManagerContainer().get( Document.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery( String.class );
		Root<Document> root = cq.from( Document.class );
		Predicate p = cb.equal( root.get( Document_.docStatus), docStatus );
		if( StringUtils.isNotEmpty( categoryId ) ){
			p = cb.and(p, cb.equal( root.get( Document_.categoryId), categoryId ));
		}else{
			p = cb.and(p, root.get( Document_.appId ).in( appids ));
		}
		cq.select(root.get( Document_.creatorUnitName ) );
		return em.createQuery( cq.where( p )).setMaxResults(500).getResultList().stream().distinct().collect(Collectors.toList());
	}
	
//	@MethodDescribe("查询用户可见的指定状态下的文件涉及的顶层组织列表量")
	public List<String> listDistinctTopUnitNameFromDocument( List<String> appids, String docStatus, String categoryId ) throws Exception {
		if( appids == null ){
			return null;
		}				
		EntityManager em = this.entityManagerContainer().get( Document.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery( String.class );
		Root<Document> root = cq.from( Document.class );
		Predicate p = cb.equal( root.get( Document_.docStatus), docStatus );
		if( StringUtils.isNotEmpty( categoryId ) ){
			p = cb.and(p, cb.equal( root.get( Document_.categoryId), categoryId ));
		}else{
			p = cb.and(p, root.get( Document_.appId ).in( appids ));
		}
		cq.select(root.get( Document_.creatorTopUnitName ) );
		return em.createQuery( cq.where( p )).setMaxResults(500).getResultList().stream().distinct().collect(Collectors.toList());
	}
	
	//@MethodDescribe("查询用户可见的指定状态下的文件涉及的栏目ID列表量")
	public List<String> listDistinctAppInfoFromDocument( List<String> appids, String docStatus, String categoryId ) throws Exception {
		if( appids == null ){
			return null;
		}
		EntityManager em = this.entityManagerContainer().get( Document.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery( String.class );
		Root<Document> root = cq.from( Document.class );
		Predicate p = cb.equal( root.get( Document_.docStatus), docStatus );
		if( StringUtils.isNotEmpty( categoryId ) ){
			p = cb.and(p, cb.equal( root.get( Document_.categoryId), categoryId ));
		}else{
			p = cb.and(p, root.get( Document_.appId ).in( appids ));
		}
		cq.select(root.get( Document_.appId ) );
		return em.createQuery( cq.where( p )).setMaxResults(500).getResultList().stream().distinct().collect(Collectors.toList());
	}
	
	//@MethodDescribe("查询用户可见的指定状态下的文件涉及的分类ID列表")
	public List<String> listDistinctCategoryFromDocument( List<String> appids, String docStatus, String categoryId ) throws Exception {
		if( appids == null ){
			return null;
		}				
		EntityManager em = this.entityManagerContainer().get( Document.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery( String.class );
		Root<Document> root = cq.from( Document.class );
		Predicate p = cb.equal( root.get( Document_.docStatus), docStatus );
		if( StringUtils.isNotEmpty( categoryId ) ){
			p = cb.and(p, cb.equal( root.get( Document_.categoryId), categoryId ));
		}else{
			p = cb.and(p, root.get( Document_.appId ).in( appids ));
		}
		cq.select(root.get( Document_.categoryId ) );
		return em.createQuery( cq.where( p )).setMaxResults(500).getResultList().stream().distinct().collect(Collectors.toList());
	}
	
	//@MethodDescribe("根据categoryId查询该应用栏目下的所有文档数量")
	public Long getUnitDocumentCount( List<String> appids, String creatorUnitName, String docStatus, String targetCategoryId) throws Exception {
		if( creatorUnitName == null ){
			return null;
		}				
		EntityManager em = this.entityManagerContainer().get( Document.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery( Long.class );
		Root<Document> root = cq.from( Document.class );
		Predicate p = cb.equal( root.get( Document_.creatorUnitName), creatorUnitName );
		p = cb.and(p, cb.equal( root.get( Document_.docStatus), docStatus ));
		if( StringUtils.isNotEmpty( targetCategoryId ) ){
			p = cb.and(p, cb.equal( root.get( Document_.categoryId), targetCategoryId ));
		}else{
			p = cb.and(p, root.get( Document_.appId).in(appids));
		}
		cq.select(cb.count(root)).where(p);
		return em.createQuery(cq).getSingleResult();
	}
	
	//@MethodDescribe("根据categoryId查询该应用栏目下的所有文档数量")
	public Long getTopUnitDocumentCount( List<String> appids, String creatorTopUnitName, String docStatus, String targetCategoryId) throws Exception {
		if( creatorTopUnitName == null ){
			return null;
		}				
		EntityManager em = this.entityManagerContainer().get( Document.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery( Long.class );
		Root<Document> root = cq.from( Document.class );
		Predicate p = cb.equal( root.get( Document_.creatorTopUnitName), creatorTopUnitName );
		p = cb.and(p, cb.equal( root.get( Document_.docStatus), docStatus ));
		if( StringUtils.isNotEmpty( targetCategoryId ) ){
			p = cb.and(p, cb.equal( root.get( Document_.categoryId), targetCategoryId ));
		}else{
			p = cb.and(p, root.get( Document_.appId).in(appids));
		}
		cq.select(cb.count(root)).where(p);
		return em.createQuery(cq).getSingleResult();
	}
	
	//@MethodDescribe("根据appId查询该应用栏目下的所有文档数量")
	public Long getAppInfoDocumentCount( String appId, String docStatus, String targetCategoryId) throws Exception {
		if( appId == null ){
			return null;
		}
		if( StringUtils.isEmpty(docStatus) ){
			docStatus = "published";
		}
		EntityManager em = this.entityManagerContainer().get( Document.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery( Long.class );
		Root<Document> root = cq.from( Document.class );
		Predicate p = cb.equal( root.get( Document_.docStatus), docStatus );
		if( StringUtils.isNotEmpty( targetCategoryId ) ){
			p = cb.and(p, cb.equal( root.get( Document_.categoryId), targetCategoryId ));
		}else{
			p = cb.and(p, cb.equal( root.get( Document_.appId), appId));
		}
		cq.select(cb.count(root)).where(p);
		return em.createQuery(cq).getSingleResult();
	}
	
	//@MethodDescribe("根据categoryId查询该应用栏目下的所有文档数量")
	public Long getCategoryInfoDocumentCount( List<String> appids, String categoryId, String docStatus ) throws Exception {
		if( categoryId == null ){
			return null;
		}				
		EntityManager em = this.entityManagerContainer().get( Document.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery( Long.class );
		Root<Document> root = cq.from( Document.class );
		Predicate p = cb.equal( root.get( Document_.categoryId), categoryId);
		p = cb.and(p, cb.equal( root.get( Document_.docStatus), docStatus ));
		cq.select(cb.count(root)).where(p);
		return em.createQuery(cq).getSingleResult();
	}
	
}
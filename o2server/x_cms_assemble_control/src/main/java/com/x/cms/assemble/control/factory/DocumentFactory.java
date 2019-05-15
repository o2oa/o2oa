package com.x.cms.assemble.control.factory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.exception.ExceptionWhen;
import com.x.base.core.project.tools.ListTools;
import com.x.cms.assemble.control.AbstractFactory;
import com.x.cms.assemble.control.Business;
import com.x.cms.core.entity.Document;
import com.x.cms.core.entity.Document_;
import com.x.cms.core.entity.tools.CriteriaBuilderTools;
import com.x.cms.core.entity.tools.DateOperation;

/**
 * 文档信息基础功能服务类
 * 
 * @author O2LEE
 * @param <T>
 */
public class DocumentFactory<T> extends AbstractFactory {

	DateOperation dateOperation = new DateOperation();
	
	public DocumentFactory(Business business) throws Exception {
		super(business);
	}

	//@MethodDescribe("获取指定Id的Document信息对象")
	public Document get( String id ) throws Exception {
		return this.entityManagerContainer().find(id, Document.class, ExceptionWhen.none);
	}
	
	//@MethodDescribe("列示全部的Document信息列表")
	public List<String> listAll() throws Exception {
		EntityManager em = this.entityManagerContainer().get( Document.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery( String.class );
		Root<Document> root = cq.from( Document.class );
		cq.select( root.get( Document_.id ));
		return em.createQuery(cq).getResultList();
	}
	
	//@MethodDescribe("根据应用ID列示所有的Document信息列表")
	public List<String> listByAppId( String appId, String documentType, Integer maxCount ) throws Exception {
		EntityManager em = this.entityManagerContainer().get( Document.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery( String.class );
		Root<Document> root = cq.from( Document.class );
		Predicate p = cb.equal(root.get( Document_.appId ), appId );
		cq.select( root.get( Document_.id) ).where(p);
		if( StringUtils.isNotEmpty( documentType) && !"全部".equals(documentType)) {
			p = cb.and( p, cb.equal( root.get( Document_.documentType), documentType));
		}
		return em.createQuery( cq ).setMaxResults(maxCount).getResultList();
	}
	
	//@MethodDescribe("根据ID列示指定分类所有Document信息列表")
	public List<String> listByCategoryId( String categoryId, Integer maxCount ) throws Exception {
		EntityManager em = this.entityManagerContainer().get( Document.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery( String.class );
		Root<Document> root = cq.from( Document.class );
		Predicate p = cb.equal(root.get( Document_.categoryId ), categoryId );
		cq.select(root.get( Document_.id)).where(p);
		return em.createQuery( cq ).setMaxResults(maxCount).getResultList();
	}
	
	//@MethodDescribe("根据ID列示指定分类所有Document信息数量")
	public Long countByCategoryId( String categoryId ) throws Exception {
		EntityManager em = this.entityManagerContainer().get( Document.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Document> root = cq.from(Document.class);
		Predicate p = cb.equal( root.get(Document_.categoryId), categoryId );
		cq.select(cb.count(root)).where(p);
		return em.createQuery(cq).getSingleResult();
	}
	
	public Long countByAppId( String appId ) throws Exception {
		EntityManager em = this.entityManagerContainer().get( Document.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Document> root = cq.from(Document.class);
		Predicate p = cb.equal( root.get(Document_.appId), appId );
		cq.select(cb.count(root)).where(p);
		return em.createQuery(cq).getSingleResult();
	}
	
	public List<Document> listInReviewDocumentList( Integer maxCount ) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Document.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Document> cq = cb.createQuery(Document.class);
		Root<Document> root = cq.from(Document.class);
		Predicate p = cb.equal( root.get(Document_.documentType), "信息");
		p = cb.and(p, cb.isTrue( root.get(Document_.reviewed ) ));
		return em.createQuery(cq.where(p)).setMaxResults( maxCount ).getResultList();
	}
	
	/**
	 * 分页查询用户可见的文档
	 * @param maxCount
	 * @param viewAbleCategoryIds
	 * @param title
	 * @param publisherList
	 * @param createDateList
	 * @param publishDateList
	 * @param statusList
	 * @param documentType
	 * @param importBatchName 
	 * @param personName
	 * @param unitNames
	 * @param groupNames
	 * @param sequenceFieldValue
	 * @param orderField
	 * @param order
	 * @param manager
	 * @return
	 * @throws Exception
	 */
	public List<Document> listNextWithCondition( 
			Integer maxCount, List<String> viewAbleCategoryIds, String title, List<String> publisherList, List<String> createDateList, 
			List<String> publishDateList,  List<String> statusList, String documentType, List<String> creatorUnitNameList, List<String> importBatchNames, List<String> personNames, List<String> unitNames, List<String> groupNames, 
			Object sequenceFieldValue, String orderField, String order, Boolean manager, Date lastedPublishTime 
	) throws Exception {
		if( ListTools.isEmpty( viewAbleCategoryIds ) ){
			order = "DESC";
		}
		if( order == null || order.isEmpty() ){
			order = "DESC";
		}
		Date startDate = null;
		Date endDate = null;
		EntityManager em = this.entityManagerContainer().get( Document.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Document> cq = cb.createQuery( Document.class );
		Root<Document> root = cq.from( Document.class );

		//文档的分类必须在可见的分类列表内
		Predicate p = root.get( Document_.id ).isNotNull();
		if( ListTools.isNotEmpty( viewAbleCategoryIds )) {
			p = root.get( Document_.categoryId ).in( viewAbleCategoryIds );
		}	
		if( !manager ) {
			if( ListTools.isEmpty( viewAbleCategoryIds )) {
				return null;
			}
			if( ListTools.isEmpty( personNames )) {
				throw new Exception("personNames can not be empty, will user has not manager permission!");
			}
			p = root.get( Document_.categoryId ).in( viewAbleCategoryIds );
			//文档权限过滤
			Predicate permission = root.get( Document_.readPersonList ).in( personNames );
			permission = cb.or( permission, root.get( Document_.authorPersonList ).in( personNames ) );
			permission = cb.or( permission, root.get( Document_.managerList ).in( personNames ) );
			
			if( ListTools.isNotEmpty( unitNames ) ) {
				permission = cb.or( permission, root.get( Document_.readUnitList ).in( unitNames ) );
				permission = cb.or( permission, root.get( Document_.authorUnitList ).in( unitNames ) );
			}
			if( ListTools.isNotEmpty( groupNames ) ) {
				permission = cb.or( permission, root.get( Document_.readGroupList ).in( groupNames ) );
				permission = cb.or( permission, root.get( Document_.authorGroupList ).in( groupNames ) );
			}
			
			p = cb.and( p, permission );
		}
		
		if( sequenceFieldValue != null ){
			if( "title".equals( orderField  )){//标题
				p = cb.and( p, cb.isNotNull( root.get( Document_.title ) ));
				if( "DESC".equalsIgnoreCase( order )){
					p = cb.and( p, cb.lessThan( root.get( Document_.title ), sequenceFieldValue.toString() ));
				}else{
					p = cb.and( p, cb.greaterThan( root.get( Document_.title ), sequenceFieldValue.toString() ));
				}
			}else if( "publishTime".equals( orderField  )){//发布时间
				p = cb.and( p, cb.isNotNull( root.get( Document_.publishTime ) ));
				if( "DESC".equalsIgnoreCase( order )){
					p = cb.and( p, cb.lessThan( root.get( Document_.publishTime ), (Date)sequenceFieldValue ));
				}else{
					p = cb.and( p, cb.greaterThan( root.get( Document_.publishTime ), (Date)sequenceFieldValue ));
				}
			}else if( "createTime".equals( orderField  )){//创建时间
				p = cb.and( p, cb.isNotNull( root.get( Document_.createTime ) ));
				if( "DESC".equalsIgnoreCase( order )){
					p = cb.and( p, cb.lessThan( root.get( Document_.createTime ), (Date)sequenceFieldValue ));
				}else{
					p = cb.and( p, cb.greaterThan( root.get( Document_.createTime ), (Date)sequenceFieldValue ));
				}
			}else if( "creatorPerson".equals( orderField  )){//创建人
				p = cb.and( p, cb.isNotNull( root.get( Document_.creatorPerson ) ));
				if( "DESC".equalsIgnoreCase( order )){
					p = cb.and( p, cb.lessThan( root.get( Document_.creatorPerson ), sequenceFieldValue.toString() ));
				}else{
					p = cb.and( p, cb.greaterThan( root.get( Document_.creatorPerson ), sequenceFieldValue.toString() ));
				}
			}else if( "creatorUnitName".equals( orderField  )){//创建人所属组织
				p = cb.and( p, cb.isNotNull( root.get( Document_.creatorUnitName ) ));
				if( "DESC".equalsIgnoreCase( order )){
					p = cb.and( p, cb.lessThan( root.get( Document_.creatorUnitName ), sequenceFieldValue.toString() ));
				}else{
					p = cb.and( p, cb.greaterThan( root.get( Document_.creatorUnitName ), sequenceFieldValue.toString() ));
				}
			}else if( "categoryName".equals( orderField  )){//分类
				p = cb.and( p, cb.isNotNull( root.get( Document_.categoryName ) ));
				if( "DESC".equalsIgnoreCase( order )){
					p = cb.and( p, cb.lessThan( root.get( Document_.categoryName ), sequenceFieldValue.toString() ));
				}else{
					p = cb.and( p, cb.greaterThan( root.get( Document_.categoryName ), sequenceFieldValue.toString() ));
				}
			}
		}
		
		//组织查询条件
		//根据最晚发布时间来过滤
		if( lastedPublishTime != null ) {
			p = cb.and( p, cb.greaterThan( root.get( Document_.publishTime ) , lastedPublishTime));
		}
		if( StringUtils.isNotEmpty( documentType ) && !"全部".equals( documentType ) ){
			p = cb.and( p, cb.equal( root.get( Document_.documentType ) , documentType));
		}
		if( ListTools.isNotEmpty( publisherList ) ){
			p = cb.and( p, root.get( Document_.creatorPerson ).in( publisherList ));
		}
		if( StringUtils.isNotEmpty( title )){
			p = cb.and( p, cb.like( root.get( Document_.title ), "%" + title + "%" ));
		}
		if( ListTools.isEmpty( statusList ) ){
			p = cb.and( p, cb.equal(root.get( Document_.docStatus ), "published"));
		}else{
			p = cb.and( p, root.get( Document_.docStatus ).in( statusList ));
		}
		if( ListTools.isNotEmpty( importBatchNames )) {
			p = cb.and( p, root.get( Document_.importBatchName ).in( importBatchNames ));
		}
		if( ListTools.isNotEmpty( creatorUnitNameList )) {
			p = cb.and( p, root.get( Document_.creatorUnitName ).in( creatorUnitNameList ));
		}
		if( createDateList != null && !createDateList.isEmpty() ){
			if ( createDateList.size() == 1 ) {// 从开始时间（yyyy-MM-DD），到现在				
				startDate = dateOperation.getDateFromString( createDateList.get(0).toString() );
				endDate = new Date();
			}else if( createDateList.size() == 2 ){// 从开始时间到结束时间（yyyy-MM-DD）				
				startDate = dateOperation.getDateFromString( createDateList.get(0).toString());
				endDate = dateOperation.getDateFromString( createDateList.get(1).toString());
			}
			p = cb.and( p, cb.between( root.get( Document_.createTime ), startDate, endDate ) );
		}
		if( publishDateList != null && !publishDateList.isEmpty() ){
			if ( publishDateList.size() == 1 ) {
				// 从开始时间（yyyy-MM-DD），到现在
				startDate = dateOperation.getDateFromString( publishDateList.get(0).toString() );
				endDate = new Date();
			}else if( publishDateList.size() == 2 ){
				// 从开始时间到结束时间（yyyy-MM-DD）
				startDate = dateOperation.getDateFromString( publishDateList.get(0).toString());
				endDate = dateOperation.getDateFromString( publishDateList.get(1).toString());
			}
			p = cb.and( p, cb.between( root.get( Document_.publishTime ), startDate, endDate ) );
		}
		
		if( "title".equals( orderField  )){//标题
			if( "DESC".equalsIgnoreCase( order )){
				cq.orderBy( cb.desc( root.get( Document_.title ) ) );
			}else{
				cq.orderBy( cb.asc( root.get( Document_.title ) ) );
			}
		}else if( "publishTime".equals( orderField  )){//发布时间
			if( "DESC".equalsIgnoreCase( order )){
				cq.orderBy( cb.desc( root.get( Document_.publishTime ) ) );
			}else{
				cq.orderBy( cb.asc( root.get( Document_.publishTime ) ) );
			}
		}else if( "createTime".equals( orderField  )){//创建时间
			if( "DESC".equalsIgnoreCase( order )){
				cq.orderBy( cb.desc( root.get( Document_.createTime ) ) );
			}else{
				cq.orderBy( cb.asc( root.get( Document_.createTime ) ) );
			}
		}else if( "creatorPerson".equals( orderField  )){//创建人
			if( "DESC".equalsIgnoreCase( order )){
				cq.orderBy( cb.desc( root.get( Document_.creatorPerson ) ) );
			}else{
				cq.orderBy( cb.asc( root.get( Document_.creatorPerson ) ) );
			}
		}else if( "creatorUnitName".equals( orderField  )){//创建人所属组织
			if( "DESC".equalsIgnoreCase( order )){
				cq.orderBy( cb.desc( root.get( Document_.creatorUnitName ) ) );
			}else{
				cq.orderBy( cb.asc( root.get( Document_.creatorUnitName ) ) );
			}
		}else if( "categoryName".equals( orderField  )){//分类
			if( "DESC".equalsIgnoreCase( order )){
				cq.orderBy( cb.desc( root.get( Document_.categoryName ) ) );
			}else{
				cq.orderBy( cb.asc( root.get( Document_.categoryName ) ) );
			}
		}
		
		if( maxCount == null || maxCount == 0 ) {
			maxCount = 20;
		}

		return em.createQuery(cq.where(p).distinct(true)).setMaxResults( maxCount ).getResultList();
	}
	
	/**
	 * 查询用户可见的文档数量
	 * @param viewAbleCategoryIds
	 * @param title
	 * @param publisherList
	 * @param createDateList
	 * @param publishDateList
	 * @param statusList
	 * @param documentType
	 * @param importBatchName 
	 * @param personName
	 * @param unitNames
	 * @param groupNames
	 * @param manager
	 * @return
	 * @throws Exception
	 */
	public Long countWithCondition( 
			List<String> viewAbleCategoryIds, String title, List<String> publisherList, List<String> createDateList,  List<String> publishDateList,  
			List<String> statusList, String documentType, List<String>  creatorUnitNameList, List<String> importBatchNames, List<String> personNames, List<String> unitNames,
			List<String> groupNames, Boolean manager, Date lastedPublishTime
	) throws Exception {
		Date startDate = null;
		Date endDate = null;
		EntityManager em = this.entityManagerContainer().get( Document.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery( Long.class );
		Root<Document> root = cq.from( Document.class );

		//文档的分类必须在可见的分类列表内
		Predicate p = root.get( Document_.id ).isNotNull();
		if( ListTools.isNotEmpty( viewAbleCategoryIds )) {
			p = root.get( Document_.categoryId ).in( viewAbleCategoryIds );
		}		
		if( !manager ) {
			if( ListTools.isEmpty( viewAbleCategoryIds )) {
				return null;
			}
			if( ListTools.isEmpty( personNames )) {
				throw new Exception("personNames can not be empty, will user has not manager permission!");
			}
			//文档权限过滤
			//文档权限过滤
			Predicate permission = root.get( Document_.readPersonList ).in( personNames );
			permission = cb.or( permission, root.get( Document_.authorPersonList ).in( personNames ) );
			permission = cb.or( permission, root.get( Document_.managerList ).in( personNames ) );
			if( ListTools.isNotEmpty( unitNames ) ) {
				permission = cb.or( permission, root.get( Document_.readUnitList ).in( unitNames ) );
				permission = cb.or( permission, root.get( Document_.authorUnitList ).in( unitNames ) );
			}
			if( ListTools.isNotEmpty( groupNames ) ) {
				permission = cb.or( permission, root.get( Document_.readGroupList ).in( groupNames ) );
				permission = cb.or( permission, root.get( Document_.authorGroupList ).in( groupNames ) );
			}
			
			p = cb.and( p, permission );
		}
		
		//根据最晚发布时间来过滤
		if( lastedPublishTime != null ) {
			p = cb.and( p, cb.greaterThan( root.get( Document_.publishTime ) , lastedPublishTime));
		}
		//组织查询条件
		if( StringUtils.isNotEmpty( documentType ) && !"全部".equals( documentType ) ){
			p = cb.and( p, cb.equal( root.get( Document_.documentType ) , documentType));
		}
		if( ListTools.isNotEmpty( publisherList ) ){
			p = cb.and( p, root.get( Document_.creatorPerson ).in( publisherList ));
		}
		if( StringUtils.isNotEmpty( title )){
			p = cb.and( p, cb.like( root.get( Document_.title ), "%" + title + "%" ));
		}
		if( ListTools.isEmpty( statusList ) ){
			p = cb.and( p, cb.equal(root.get( Document_.docStatus ), "published"));
		}else{
			p = cb.and( p, root.get( Document_.docStatus ).in( statusList ));
		}
		if( ListTools.isNotEmpty( importBatchNames )) {
			p = cb.and( p, root.get( Document_.importBatchName ).in( importBatchNames ));
		}
		if( ListTools.isNotEmpty( creatorUnitNameList )) {
			p = cb.and( p, root.get( Document_.creatorUnitName ).in( creatorUnitNameList ));
		}
		if( createDateList != null && !createDateList.isEmpty() ){
			if ( createDateList.size() == 1 ) {// 从开始时间（yyyy-MM-DD），到现在				
				startDate = dateOperation.getDateFromString( createDateList.get(0).toString() );
				endDate = new Date();
			}else if( createDateList.size() == 2 ){// 从开始时间到结束时间（yyyy-MM-DD）				
				startDate = dateOperation.getDateFromString( createDateList.get(0).toString());
				endDate = dateOperation.getDateFromString( createDateList.get(1).toString());
			}
			p = cb.and( p, cb.between( root.get( Document_.createTime ), startDate, endDate ) );
		}
		if( publishDateList != null && !publishDateList.isEmpty() ){
			if ( publishDateList.size() == 1 ) {
				// 从开始时间（yyyy-MM-DD），到现在
				startDate = dateOperation.getDateFromString( publishDateList.get(0).toString() );
				endDate = new Date();
			}else if( publishDateList.size() == 2 ){
				// 从开始时间到结束时间（yyyy-MM-DD）
				startDate = dateOperation.getDateFromString( publishDateList.get(0).toString());
				endDate = dateOperation.getDateFromString( publishDateList.get(1).toString());
			}
			p = cb.and( p, cb.between( root.get( Document_.publishTime ), startDate, endDate ) );
		}

		cq.select( cb.count( root ) );

		return em.createQuery(cq.where(p)).getSingleResult();
	}

	public List<Document> listMyDraft( String name, List<String> categoryIdList, String documentType ) throws Exception {
		EntityManager em = this.entityManagerContainer().get( Document.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Document> cq = cb.createQuery( Document.class );
		Root<Document> root = cq.from( Document.class );
		Predicate p = cb.equal( root.get( Document_.creatorPerson ), name );
		p = cb.and( p, cb.equal(root.get( Document_.docStatus ), "draft"));
		if(ListTools.isNotEmpty( categoryIdList )) {
			p = cb.and( p, root.get( Document_.categoryId ).in( categoryIdList ));
		}
		if(StringUtils.isNotEmpty( documentType ) && !"全部".equals(documentType)) {
			p = cb.and( p, cb.equal( root.get( Document_.documentType ), documentType));
		}
		return em.createQuery(cq.where(p)).getResultList();
	}

	public List<String> lisViewableDocIdsWithFilter( List<String> appIdList, List<String> appAliasList, 
			List<String> categoryIdList, List<String> categoryAliasList, 
			List<String> publisherList, String title, List<String> createDateList, List<String> publishDateList,
			List<String> statusList, String documentType, Integer maxResultCount ) throws Exception {
		Date startDate = null;
		Date endDate = null;
		List<String> ids = new ArrayList<>();
		List<Document> documents = null;
		DateOperation dateOperation = new DateOperation();
		EntityManager em = this.entityManagerContainer().get( Document.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Document> cq = cb.createQuery( Document.class );
		Root<Document> root = cq.from( Document.class );
		
		Predicate p = cb.isNotNull( root.get( Document_.id ) );
		
		if( StringUtils.isNotEmpty( documentType ) && !"全部".equals( documentType ) ){
			p = cb.and( p, cb.equal( root.get( Document_.documentType ) , documentType));
		}
		if( ListTools.isNotEmpty( appIdList ) ){
			p = cb.and( p, root.get( Document_.appId ).in( appIdList ));
		}
		if( ListTools.isNotEmpty( appAliasList ) ){
			p = cb.and( p, root.get( Document_.appName ).in( appAliasList ));
		}
		if( ListTools.isNotEmpty( categoryIdList ) ){
			p = cb.and( p, root.get( Document_.categoryId ).in( categoryIdList ));
		}
		if( ListTools.isNotEmpty( categoryAliasList ) ){
			p = cb.and( p, root.get( Document_.categoryName ).in( categoryAliasList ));
		}
		if( ListTools.isNotEmpty( publisherList ) ){
			p = cb.and( p, root.get( Document_.creatorPerson ).in( publisherList ));
		}
		if( StringUtils.isNotEmpty( title )){
			p = cb.and( p, cb.like( root.get( Document_.title ), "%" + title + "%" ));
		}
		if( ListTools.isEmpty( statusList ) ){
			p = cb.and( p, cb.equal(root.get( Document_.docStatus ), "published"));
		}else{
			p = cb.and( p, root.get( Document_.docStatus ).in( statusList ));
		}
		if( createDateList != null && !createDateList.isEmpty() ){
			if ( createDateList.size() == 1 ) {// 从开始时间（yyyy-MM-DD），到现在				
				startDate = dateOperation.getDateFromString( createDateList.get(0).toString() );
				endDate = new Date();
			}else if( createDateList.size() == 2 ){// 从开始时间到结束时间（yyyy-MM-DD）				
				startDate = dateOperation.getDateFromString( createDateList.get(0).toString());
				endDate = dateOperation.getDateFromString( createDateList.get(1).toString());
			}
			p = cb.and( p, cb.between( root.get( Document_.createTime ), startDate, endDate ) );
		}
		if( publishDateList != null && !publishDateList.isEmpty() ){
			if ( publishDateList.size() == 1 ) {
				// 从开始时间（yyyy-MM-DD），到现在
				startDate = dateOperation.getDateFromString( publishDateList.get(0).toString() );
				endDate = new Date();
			}else if( publishDateList.size() == 2 ){
				// 从开始时间到结束时间（yyyy-MM-DD）
				startDate = dateOperation.getDateFromString( publishDateList.get(0).toString());
				endDate = dateOperation.getDateFromString( publishDateList.get(1).toString());
			}
			p = cb.and( p, cb.between( root.get( Document_.publishTime ), startDate, endDate ) );
		}
		
		cq.orderBy( cb.desc( root.get( Document_.publishTime ) ) );
		
		if( maxResultCount == null || maxResultCount == 0 ){
			maxResultCount = 10000;
		}
		documents = em.createQuery( cq.where( p ) ).setMaxResults( maxResultCount ).getResultList();
		if( documents != null && !documents.isEmpty() ){
			for( Document document : documents ){
				if( !ids.contains( document.getId() )){
					ids.add( document.getId() );
				}
			}
		}
		return ids;
	}

	/**
	 * 根据条件查询出用户可访问的文档ID列表，按发布时间倒排序，取前1000条
	 * @param appIdList
	 * @param categoryIdList
	 * @param publisherList
	 * @param title
	 * @param createDateList
	 * @param publishDateList
	 * @param statusList
	 * @param personName
	 * @param unitNames
	 * @param groupNames
	 * @param viewableCategoryIds
	 * @param maxResultCount
	 * @return
	 * @throws Exception
	 * 
	 * 
	 */
	public List<String> lisViewableDocIdsWithFilter( List<String> appIdList, List<String> categoryIdList, List<String> publisherList, 
			String title, List<String> createDateList, List<String> publishDateList, List<String> statusList, String personName, List<String> unitNames, 
			List<String> groupNames, List<String> viewableCategoryIds, String documentType, Integer maxResultCount ) throws Exception {
		Date startDate = null;
		Date endDate = null;
		List<String> ids = new ArrayList<>();
		DateOperation dateOperation = new DateOperation();
		EntityManager em = this.entityManagerContainer().get( Document.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Document> cq = cb.createQuery( Document.class );
		Root<Document> root = cq.from( Document.class );
		
		//先圈定用户能访问的文档
		Predicate permissionWhere = null;
		
		//personName不为空，说明不是管理员访问，有限制控制
		//如果personName为空，应该就是管理员在访问了，所有人访问的也不需要判断
		if( StringUtils.isNotEmpty( personName ) || ListTools.isNotEmpty( unitNames ) ||  ListTools.isNotEmpty( groupNames ) ) {
			//所有人可访问的文档
			permissionWhere = CriteriaBuilderTools.predicate_or(cb, permissionWhere, cb.isMember("所有人" , root.get( Document_.readPersonList )) );
			permissionWhere = CriteriaBuilderTools.predicate_or(cb, permissionWhere, cb.isMember("所有人" , root.get( Document_.authorPersonList )) );
		}
				
		if( StringUtils.isNotEmpty( personName )) {			
			permissionWhere = CriteriaBuilderTools.predicate_or(cb, permissionWhere, cb.isMember(personName , root.get( Document_.readPersonList )) );
			permissionWhere = CriteriaBuilderTools.predicate_or(cb, permissionWhere, cb.isMember(personName , root.get( Document_.authorPersonList)) );
			//或者用户可管理的分类中所有的文档
			permissionWhere = CriteriaBuilderTools.predicate_or(cb, permissionWhere, cb.isMember(personName , root.get( Document_.managerList )) );
		}				
		if(ListTools.isNotEmpty( unitNames )) {
			permissionWhere = CriteriaBuilderTools.predicate_or(cb, permissionWhere, root.get( Document_.readUnitList ).in( unitNames ) );
			permissionWhere = CriteriaBuilderTools.predicate_or(cb, permissionWhere, root.get( Document_.authorUnitList ).in( unitNames ) );
		}
		if(ListTools.isNotEmpty( groupNames )) {
			permissionWhere = CriteriaBuilderTools.predicate_or(cb, permissionWhere, root.get( Document_.readGroupList ).in( groupNames ) );
			permissionWhere = CriteriaBuilderTools.predicate_or(cb, permissionWhere, root.get( Document_.authorGroupList ).in( groupNames ) );	
		}		
		
		Predicate p = cb.isNotNull( root.get( Document_.publishTime ) );
		//一定要在用户有权限访问的目录里选择文档viewableCategoryIds
		p = cb.and( p, root.get( Document_.categoryId ).in( viewableCategoryIds ) );
		
		if( StringUtils.isNotEmpty( documentType ) && !"全部".equals( documentType ) ){
			p = cb.and( p, cb.equal(root.get( Document_.documentType ), documentType));
		}
		
		if( ListTools.isNotEmpty( appIdList ) ){
			p = cb.and( p, root.get( Document_.appId ).in( appIdList ));
		}
		if( ListTools.isNotEmpty( categoryIdList ) ){
			p = cb.and( p, root.get( Document_.categoryId ).in( categoryIdList ));
		}
		if( StringUtils.isNotEmpty( title ) ){
			p = cb.and( p, cb.like( root.get( Document_.title ), "%" + title + "%" ));
		}
		if( ListTools.isEmpty( statusList ) ){
			p = cb.and( p, cb.equal(root.get( Document_.docStatus ), "published"));
		}else{
			p = cb.and( p, root.get( Document_.docStatus ).in( statusList ));
		}			
		if( createDateList != null && !createDateList.isEmpty() ){
			if ( createDateList.size() == 1 ) {// 从开始时间（yyyy-MM-DD），到现在				
				startDate = dateOperation.getDateFromString( createDateList.get(0).toString() );
				endDate = new Date();
			}else if( createDateList.size() == 2 ){// 从开始时间到结束时间（yyyy-MM-DD）				
				startDate = dateOperation.getDateFromString( createDateList.get(0).toString());
				endDate = dateOperation.getDateFromString( createDateList.get(1).toString());
			}
			p = cb.and( p, cb.between( root.get( Document_.createTime ), startDate, endDate ) );
		}
			
		if( publishDateList != null && !publishDateList.isEmpty() ){
			if ( publishDateList.size() == 1 ) {
				// 从开始时间（yyyy-MM-DD），到现在
				startDate = dateOperation.getDateFromString( publishDateList.get(0).toString() );
				endDate = new Date();
			}else if( publishDateList.size() == 2 ){
				// 从开始时间到结束时间（yyyy-MM-DD）
				startDate = dateOperation.getDateFromString( publishDateList.get(0).toString());
				endDate = dateOperation.getDateFromString( publishDateList.get(1).toString());
			}
			p = cb.and( p, cb.between( root.get( Document_.publishTime ), startDate, endDate ) );
		}
		
		p = CriteriaBuilderTools.predicate_and(cb, p, permissionWhere );	
		
		if( maxResultCount == null || maxResultCount == 0 ){
			maxResultCount = 500;
		}
		
		if( publishDateList != null && !publishDateList.isEmpty() ){
			cq.orderBy( cb.desc( root.get( Document_.publishTime )) );
		}else {
			cq.orderBy( cb.desc( root.get( Document_.createTime )) );
		}

		//LogUtil.INFO( ">>>>SQL:", em.createQuery( cq.where( p ) ).setMaxResults( maxResultCount ).toString() );
		
		List<Document> documents = em.createQuery( cq.where( p ) ).setMaxResults( maxResultCount ).getResultList();
		if( ListTools.isNotEmpty( documents ) ){
			for( Document document : documents ){
				if( !ids.contains( document.getId() )){
					ids.add( document.getId() );
				}
			}
		}
		return ids;
	}

	public List<String> listWithImportBatchName(String importBatchName) throws Exception {
		if( StringUtils.isEmpty( importBatchName ) ){
			throw new Exception("importBatchName is empty!");
		}
		EntityManager em = this.entityManagerContainer().get( Document.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery( String.class );
		Root<Document> root = cq.from( Document.class );
		Predicate p = cb.equal( root.get( Document_.importBatchName ), importBatchName );
		cq.select(root.get( Document_.id));
		return em.createQuery(cq.where(p)).getResultList();
	}

	public List<String> listReviewedIdsByCategoryId( String categoryId, Integer maxCount) throws Exception {
		if( StringUtils.isEmpty( categoryId ) ){
			throw new Exception("categoryId is empty!");
		}
		if( maxCount == null ){
			maxCount = 1000;
		}
		EntityManager em = this.entityManagerContainer().get( Document.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery( String.class );
		Root<Document> root = cq.from( Document.class );
		Predicate p = cb.equal( root.get( Document_.documentType ), "信息" );
		p = cb.and( p, cb.equal( root.get( Document_.categoryId ), categoryId ));
		p = cb.and( p, cb.isTrue(  root.get( Document_.reviewed ) ));
		cq.select(root.get( Document_.id));
		return em.createQuery(cq.where(p)).setMaxResults(maxCount).getResultList();
	}

	public List<String> listInReviewIds(Integer maxCount) throws Exception {
		if( maxCount == null ){
			maxCount = 500;
		}
		EntityManager em = this.entityManagerContainer().get( Document.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery( String.class );
		Root<Document> root = cq.from( Document.class );
		Predicate p = cb.equal( root.get( Document_.documentType ), "信息" );
		p = cb.and(p, cb.or(cb.isFalse( root.get(Document_.reviewed ) ), cb.isNull(root.get(Document_.reviewed ))));
		cq.select(root.get( Document_.id));
		return em.createQuery(cq.where(p)).setMaxResults(maxCount).getResultList();
	}
}
package com.x.cms.assemble.search.factory;

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
import com.x.cms.assemble.search.AbstractFactory;
import com.x.cms.assemble.search.Business;
import com.x.cms.core.entity.Document;
import com.x.cms.core.entity.Document_;
import com.x.cms.core.entity.tools.CriteriaBuilderTools;
import com.x.cms.core.entity.tools.DateOperation;

/**
 * 文档信息基础功能服务类
 * 
 * @author O2LEE
 */
public class DocumentFactory extends AbstractFactory {

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
	
	//@MethodDescribe("列示指定Id的Document信息列表")
	public List<Document> list(List<String> ids) throws Exception {
		EntityManager em = this.entityManagerContainer().get( Document.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Document> cq = cb.createQuery( Document.class );
		Root<Document> root = cq.from( Document.class );
		Predicate p = root.get( Document_.id).in( ids );
		return em.createQuery(cq.where(p)).getResultList();
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
	public List<String> listByCategoryId( String categoryId, String status ) throws Exception {
		EntityManager em = this.entityManagerContainer().get( Document.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery( String.class );
		Root<Document> root = cq.from( Document.class );
		Predicate p = cb.equal(root.get( Document_.categoryId ), categoryId );
		if(StringUtils.isNotEmpty( status )) {
			p = cb.and( p, cb.equal(root.get( Document_.docStatus ), status ));
		}
		cq.select(root.get( Document_.id)).where(p);
		return em.createQuery( cq ).setMaxResults(100).getResultList();
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
	
	public Long countWithDocIds(List<String> viewAbleDocIds) throws Exception {
		if( viewAbleDocIds == null || viewAbleDocIds.isEmpty() ){
			return 0L;
		}
		EntityManager em = this.entityManagerContainer().get( Document.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery( Long.class );
		Root<Document> root = cq.from( Document.class );
		Predicate p = root.get( Document_.id ).in( viewAbleDocIds );
		cq.select( cb.count( root ) );
		return em.createQuery(cq.where(p)).getSingleResult();
	}
	
	public List<Document> listNextWithDocIds( Integer count, List<String> viewAbleDocIds, Object sequenceFieldValue, String orderField, String order ) throws Exception {
		if( order == null || order.isEmpty() ){
			order = "DESC";
		}
		EntityManager em = this.entityManagerContainer().get( Document.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Document> cq = cb.createQuery( Document.class );
		Root<Document> root = cq.from( Document.class );

		Predicate p = root.get( Document_.id ).in( viewAbleDocIds );
		
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
		return em.createQuery(cq.where(p)).setMaxResults( count ).getResultList();
	}

	public List<Document> listMyDraft( String name, List<String> categoryIdList ) throws Exception {
		EntityManager em = this.entityManagerContainer().get( Document.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Document> cq = cb.createQuery( Document.class );
		Root<Document> root = cq.from( Document.class );
		Predicate p = cb.equal( root.get( Document_.creatorPerson ), name );
		p = cb.and( p, cb.equal(root.get( Document_.docStatus ), "draft"));
		p = cb.and( p, root.get( Document_.categoryId ).in( categoryIdList ));
		return em.createQuery(cq.where(p)).getResultList();
	}

	public List<String> lisViewableDocIdsWithFilter( List<String> appIdList, List<String> appAliasList, 
			List<String> categoryIdList, List<String> categoryAliasList, 
			List<String> publisherList, String title, List<String> createDateList, List<String> publishDateList,
			List<String> statusList, Integer maxResultCount ) throws Exception {
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
			maxResultCount = 500;
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
			List<String> groupNames, List<String> viewableCategoryIds, Integer maxResultCount ) throws Exception {
		Date startDate = null;
		Date endDate = null;
		DateOperation dateOperation = new DateOperation();
		EntityManager em = this.entityManagerContainer().get( Document.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Document> cq = cb.createQuery( Document.class );
		Root<Document> root = cq.from( Document.class );
		
		//先圈定用户能访问的文档
		Predicate permissionWhere = null;
		if( StringUtils.isNotEmpty( personName )) {
			CriteriaBuilderTools.predicate_or(cb, permissionWhere, cb.isMember(personName , root.get( Document_.readPersonList )) );
			CriteriaBuilderTools.predicate_or(cb, permissionWhere, cb.isMember(personName , root.get( Document_.authorPersonList)) );
			//或者用户可管理的分类中所有的文档
			CriteriaBuilderTools.predicate_or(cb, permissionWhere, cb.isMember(personName , root.get( Document_.managerList )) );
		}				
		if(ListTools.isNotEmpty( unitNames )) {
			CriteriaBuilderTools.predicate_or(cb, permissionWhere, root.get( Document_.readUnitList ).in( unitNames ) );
			CriteriaBuilderTools.predicate_or(cb, permissionWhere, root.get( Document_.authorUnitList ).in( unitNames ) );
		}
		if(ListTools.isNotEmpty( unitNames )) {
			CriteriaBuilderTools.predicate_or(cb, permissionWhere, root.get( Document_.readGroupList ).in( groupNames ) );
			CriteriaBuilderTools.predicate_or(cb, permissionWhere, root.get( Document_.authorGroupList ).in( groupNames ) );	
		}
			
		//所有人可访问的文档
		CriteriaBuilderTools.predicate_or(cb, permissionWhere, cb.isMember("所有人" , root.get( Document_.readPersonList )) );
		CriteriaBuilderTools.predicate_or(cb, permissionWhere, cb.isMember("所有人" , root.get( Document_.authorPersonList )) );
		
		Predicate p = cb.isNotNull( root.get( Document_.publishTime ) );
		//一定要在用户有权限访问的目录里选择文档viewableCategoryIds
		p = cb.and( p, root.get( Document_.categoryId ).in( viewableCategoryIds ) );
		
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
		
		CriteriaBuilderTools.predicate_and(cb, p, permissionWhere );	
		
		if( maxResultCount == null || maxResultCount == 0 ){
			maxResultCount = 500;
		}
		List<String> ids = new ArrayList<>();
		List<Document> documents = em.createQuery( cq.where( p ) ).setMaxResults( maxResultCount ).getResultList();
		if( ListTools.isNotEmpty( documents ) ){
			for( Document document : documents ){
				if( !ids.contains( document.getId() )){
					ids.add( document.getId() );
				}
			}
		}
		return ids;
		
//		List<Selection<?>> selections = new ArrayList<>();
//		selections.add(root.get( Document_.id ));
//		selections.add(root.get( Document_.publishTime ));
//		
//		cq.multiselect(selections).where(p);
//		
//		if( maxResultCount == null || maxResultCount == 0 ){
//			maxResultCount = 1000;
//		}
//		//查询id和publishTime
//		List<Tuple> tuples = em.createQuery(cq).setMaxResults(100000).getResultList();
//		List<Tuple> tuples_order = new ArrayList<>();
//		tuples_order.addAll( tuples );
//		tuples_order = order( tuples_order, "desc" );
//		
//		//然后在内存里取所有的ID
//		List<String> resultIds = new ArrayList<>();
//		for( int i = 0 ;i <maxResultCount; i++ ) {
//			resultIds.add( tuples_order.get(i).get(0).toString() );
//		}			
//		return resultIds;
	}
	
//	@SuppressWarnings("unused")
//	private List<Tuple> order(List<Tuple> tuples, String orderType ) {
//		Comparator<Tuple> comparator = new Comparator<Tuple>() {
//			@SuppressWarnings({ "rawtypes", "unchecked" })
//			public int compare(Tuple t1, Tuple t2) {
//				int comp = 0;
//				Object o1 = t1.get(1);
//				Object o2 = t2.get(1);
//				if (null == o1 && null == o2) {
//					comp = 0;
//				} else if (null == o1) {
//					comp = -1;
//				} else if (null == o2) {
//					comp = 1;
//				} else {
//					Comparable c1 = (Comparable) o1;
//					Comparable c2 = (Comparable) o2;
//					if (StringUtils.equalsIgnoreCase(SelectEntry.ORDER_ASC, orderType)) {
//						comp = c1.compareTo(c2);
//					} else {
//						comp = c2.compareTo(c1);
//					}
//				}
//				if (comp != 0) {
//					return comp;
//				}
//				return comp;
//			}
//		};
//		List<Tuple> list = new ArrayList<>();
//		if (ListTools.isNotEmpty( tuples )) {
//			list = tuples.stream().sorted(comparator).collect(Collectors.toList());
//			tuples.clear();
//			tuples.addAll(list);
//		}
//		return tuples;
//	}
}
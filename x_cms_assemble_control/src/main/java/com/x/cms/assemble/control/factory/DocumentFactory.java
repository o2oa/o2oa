package com.x.cms.assemble.control.factory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.exception.ExceptionWhen;
import com.x.base.core.utils.annotation.MethodDescribe;
import com.x.cms.assemble.control.AbstractFactory;
import com.x.cms.assemble.control.Business;
import com.x.cms.assemble.control.jaxrs.document.WrapInFilter;
import com.x.cms.common.date.DateOperation;
import com.x.cms.core.entity.Document;
import com.x.cms.core.entity.Document_;

/**
 * 文档信息基础功能服务类
 * @author liyi
 */
public class DocumentFactory extends AbstractFactory {

	public DocumentFactory(Business business) throws Exception {
		super(business);
	}

	@MethodDescribe("获取指定Id的Document信息对象")
	public Document get( String id ) throws Exception {
		return this.entityManagerContainer().find(id, Document.class, ExceptionWhen.none);
	}
	
	@MethodDescribe("列示全部的Document信息列表")
	public List<String> listAll() throws Exception {
		EntityManager em = this.entityManagerContainer().get( Document.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery( String.class );
		Root<Document> root = cq.from( Document.class );
		cq.select( root.get( Document_.id ));
		return em.createQuery(cq).getResultList();
	}
	
	@MethodDescribe("列示指定Id的Document信息列表")
	public List<Document> list(List<String> ids) throws Exception {
		EntityManager em = this.entityManagerContainer().get( Document.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Document> cq = cb.createQuery( Document.class );
		Root<Document> root = cq.from( Document.class );
		Predicate p = root.get( Document_.id).in( ids );
		return em.createQuery(cq.where(p)).getResultList();
	}
	
	@MethodDescribe("根据应用ID列示所有的Document信息列表")
	public List<String> listByAppId( String appId ) throws Exception {
		EntityManager em = this.entityManagerContainer().get( Document.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery( String.class );
		Root<Document> root = cq.from( Document.class );
		Predicate p = cb.equal(root.get( Document_.appId ), appId );
		cq.select( root.get( Document_.id) ).where(p);
		return em.createQuery( cq ).setMaxResults(100).getResultList();
	}
	
	@MethodDescribe("根据ID列示指定分类所有Document信息列表")
	public List<String> listByCategoryId( String categoryId ) throws Exception {
		EntityManager em = this.entityManagerContainer().get( Document.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery( String.class );
		Root<Document> root = cq.from( Document.class );
		Predicate p = cb.equal(root.get( Document_.categoryId ), categoryId );
		cq.select(root.get( Document_.id)).where(p);
		return em.createQuery( cq ).setMaxResults(100).getResultList();
	}
	
	@MethodDescribe("根据ID列示指定分类所有Document信息数量")
	public Long countByCategoryId( String categoryId ) throws Exception {
		EntityManager em = this.entityManagerContainer().get( Document.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Document> root = cq.from(Document.class);
		Predicate p = cb.equal( root.get(Document_.categoryId), categoryId );
		cq.select(cb.count(root)).where(p);
		return em.createQuery(cq).getSingleResult();
	}
	
	/**
	 * 查询下一页的文档信息数据
	 * @param id
	 * @param count
	 * @param sequence
	 * @param wrapIn
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<Document> listIdsNextWithFilter( String id, Integer count, Object sequence, WrapInFilter wrapIn ) throws Exception {
		//先获取上一页最后一条的sequence值，如果有值的话，以此sequence值作为依据取后续的count条数据
		EntityManager em = this.entityManagerContainer().get( Document.class );
		DateOperation dateOperation = new DateOperation();
		Date startDate = null, endDate = null;
		String order = wrapIn.getOrderType();//排序方式
		List<Object> vs = new ArrayList<>();
		StringBuffer sql_stringBuffer = new StringBuffer();
		
		if( order == null || order.isEmpty() ){
			order = "DESC";
		}
		
		Integer index = 1;
		sql_stringBuffer.append( "SELECT o FROM "+Document.class.getCanonicalName()+" o where 1=1" );

		if ((null != sequence) ) {
			sql_stringBuffer.append(" and o.sequence " + (StringUtils.equalsIgnoreCase(order, "DESC") ? "<" : ">") + (" ?" + (index)));
			vs.add(sequence);
			index++;
		}
		if ((null != wrapIn.getStatusList()) && (!wrapIn.getStatusList().isEmpty())) {
			sql_stringBuffer.append(" and o.docStatus in ?" + (index));
			vs.add( wrapIn.getStatusList() );
			index++;
		}
		if ((null != wrapIn.getTitle()) && (!wrapIn.getTitle().isEmpty())) {
			sql_stringBuffer.append(" and o.title like ?" + (index));
			vs.add( "%"+wrapIn.getTitle()+"%" );
			index++;
		}
		if ((null != wrapIn.getAppIdList()) && (!wrapIn.getAppIdList().isEmpty())) {
			sql_stringBuffer.append(" and o.appId in ?" + (index));
			vs.add( wrapIn.getAppIdList() );
			index++;
		}
		if ((null != wrapIn.getCategoryIdList()) && (!wrapIn.getCategoryIdList().isEmpty())) {
			sql_stringBuffer.append(" and o.categoryId in ?" + (index));
			vs.add( wrapIn.getCategoryIdList() );
			index++;
		}
		if ((null != wrapIn.getCreatorList()) && (!wrapIn.getCreatorList().isEmpty())) {
			sql_stringBuffer.append(" and o.creatorPerson in ?" + (index));
			vs.add( wrapIn.getCreatorList() );
			index++;
		}
		if ((null != wrapIn.getCreateDateList()) && (!wrapIn.getCreateDateList().isEmpty())) {
			if( wrapIn.getCreateDateList().size() == 1){
				//从开始时间（yyyy-MM-DD），到现在
				startDate = dateOperation.getDateFromString( wrapIn.getCreateDateList().get(0).toString() );
				endDate = new Date();
			}else if( wrapIn.getCreateDateList().size() == 2){
				//从开始时间到结束时间（yyyy-MM-DD）
				startDate = dateOperation.getDateFromString( wrapIn.getCreateDateList().get(0).toString() );
				endDate = dateOperation.getDateFromString( wrapIn.getCreateDateList().get(1).toString() );
			}
			sql_stringBuffer.append(" and o.createTime between ( ?"+(index)+", ?"+(index+1)+" )");
			vs.add( startDate );
			vs.add( endDate );
		}
		
		if( wrapIn.getTitle() != null && !wrapIn.getTitle().isEmpty()){
			sql_stringBuffer.append(" order by o."+wrapIn.getTitle()+" " + order );
		}else{
			sql_stringBuffer.append(" order by o.sequence " + order );
		}
	
		Query query = em.createQuery( sql_stringBuffer.toString(), Document.class );
		//为查询设置所有的参数值
		for (int i = 0; i < vs.size(); i++) {
			query.setParameter(i + 1, vs.get(i));
		}
		
		return query.setMaxResults(count).getResultList();
	}	
	
	/**
	 * 查询上一页的文档信息数据
	 * @param id
	 * @param count
	 * @param sequence
	 * @param wrapIn
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<Document> listIdsPrevWithFilter( String id, Integer count, Object sequence, WrapInFilter wrapIn ) throws Exception {
		//先获取上一页最后一条的sequence值，如果有值的话，以此sequence值作为依据取后续的count条数据
		EntityManager em = this.entityManagerContainer().get( Document.class );
		DateOperation dateOperation = new DateOperation();
		Date startDate = null, endDate = null;
		String order = wrapIn.getOrderType();//排序方式
		List<Object> vs = new ArrayList<>();
		StringBuffer sql_stringBuffer = new StringBuffer();
		Integer index = 1;
		
		if( order == null || order.isEmpty() ){
			order = "DESC";
		}
		
		sql_stringBuffer.append( "SELECT o FROM "+Document.class.getCanonicalName()+" o where 1=1" );
		if ((null != sequence) ) {
			sql_stringBuffer.append(" and o.sequence " + (StringUtils.equalsIgnoreCase(order, "DESC") ? ">" : "<") + (" ?" + (index)));
			vs.add(sequence);
			index++;
		}
		if ((null != wrapIn.getStatusList()) && (!wrapIn.getStatusList().isEmpty())) {
			sql_stringBuffer.append(" and o.docStatus in ?" + (index));
			vs.add( wrapIn.getStatusList() );
			index++;
		}
		if ((null != wrapIn.getTitle()) && (!wrapIn.getTitle().isEmpty())) {
			sql_stringBuffer.append(" and o.title like ?" + (index));
			vs.add( "%"+wrapIn.getTitle()+"%" );
			index++;
		}
		if ((null != wrapIn.getAppIdList()) && (!wrapIn.getAppIdList().isEmpty())) {
			sql_stringBuffer.append(" and o.appId in ?" + (index));
			vs.add( wrapIn.getAppIdList() );
			index++;
		}
		if ((null != wrapIn.getCategoryIdList()) && (!wrapIn.getCategoryIdList().isEmpty())) {
			sql_stringBuffer.append(" and o.categoryId in ?" + (index));
			vs.add( wrapIn.getCategoryIdList() );
			index++;
		}
		if ((null != wrapIn.getCreatorList()) && (!wrapIn.getCreatorList().isEmpty())) {
			sql_stringBuffer.append(" and o.creatorPerson in ?" + (index));
			vs.add( wrapIn.getCreatorList() );
			index++;
		}
		if ((null != wrapIn.getCreateDateList()) && (!wrapIn.getCreateDateList().isEmpty())) {
			if( wrapIn.getCreateDateList().size() == 1){
				//从开始时间（yyyy-MM-DD），到现在
				startDate = dateOperation.getDateFromString( wrapIn.getCreateDateList().get(0).toString() );
				endDate = new Date();
			}else if( wrapIn.getCreateDateList().size() == 2){
				//从开始时间到结束时间（yyyy-MM-DD）
				startDate = dateOperation.getDateFromString( wrapIn.getCreateDateList().get(0).toString() );
				endDate = dateOperation.getDateFromString( wrapIn.getCreateDateList().get(1).toString() );
			}
			sql_stringBuffer.append(" and o.createTime between ( ?"+(index)+", ?"+(index+1)+" )");
			vs.add( startDate );
			vs.add( endDate );
		}
		
		if( wrapIn.getTitle() != null && !wrapIn.getTitle().isEmpty()){
			sql_stringBuffer.append(" order by o."+wrapIn.getTitle()+" " + order );
		}else{
			sql_stringBuffer.append(" order by o.sequence " + order );
		}
		Query query = em.createQuery( sql_stringBuffer.toString(), Document.class );
		//为查询设置所有的参数值
		for (int i = 0; i < vs.size(); i++) {
			query.setParameter(i + 1, vs.get(i));
		}
		
		return query.setMaxResults(20).getResultList();
	}
	
	/**
	 * 查询符合的文档信息总数
	 * @param id
	 * @param count
	 * @param sequence
	 * @param wrapIn
	 * @return
	 * @throws Exception
	 */
	public long getCountWithFilter( WrapInFilter wrapIn ) throws Exception {
		//先获取上一页最后一条的sequence值，如果有值的话，以此sequence值作为依据取后续的count条数据
		EntityManager em = this.entityManagerContainer().get( Document.class );
		DateOperation dateOperation = new DateOperation();
		Date startDate = null, endDate = null;
		List<Object> vs = new ArrayList<>();
		StringBuffer sql_stringBuffer = new StringBuffer();
		Integer index = 1;
		
		sql_stringBuffer.append( "SELECT count(o.id) FROM "+Document.class.getCanonicalName()+" o where 1=1" );
		
		if ((null != wrapIn.getStatusList()) && (!wrapIn.getStatusList().isEmpty())) {
			sql_stringBuffer.append(" and o.docStatus in ?" + (index));
			vs.add( wrapIn.getStatusList() );
			index++;
		}
		if ((null != wrapIn.getTitle()) && (!wrapIn.getTitle().isEmpty())) {
			sql_stringBuffer.append(" and o.title like ?" + (index));
			vs.add( "%"+wrapIn.getTitle()+"%" );
			index++;
		}
		if (null != wrapIn.getAppIdList() && !wrapIn.getAppIdList().isEmpty()) {
			sql_stringBuffer.append(" and o.appId in ?" + (index));
			vs.add( wrapIn.getAppIdList() );
			index++;
		}
		if ((null != wrapIn.getCategoryIdList()) && (!wrapIn.getCategoryIdList().isEmpty())) {
			sql_stringBuffer.append(" and o.categoryId in ?" + (index));
			vs.add( wrapIn.getCategoryIdList() );
			index++;
		}
		if ((null != wrapIn.getCreatorList()) && (!wrapIn.getCreatorList().isEmpty())) {
			sql_stringBuffer.append(" and o.creatorPerson in ?" + (index));
			vs.add( wrapIn.getCreatorList() );
			index++;
		}
		if ((null != wrapIn.getCreateDateList()) && (!wrapIn.getCreateDateList().isEmpty())) {
			if( wrapIn.getCreateDateList().size() == 1){
				//从开始时间（yyyy-MM-DD），到现在
				startDate = dateOperation.getDateFromString( wrapIn.getCreateDateList().get(0).toString() );
				endDate = new Date();
			}else if( wrapIn.getCreateDateList().size() == 2){
				//从开始时间到结束时间（yyyy-MM-DD）
				startDate = dateOperation.getDateFromString( wrapIn.getCreateDateList().get(0).toString() );
				endDate = dateOperation.getDateFromString( wrapIn.getCreateDateList().get(1).toString() );
			}
			sql_stringBuffer.append(" and o.createTime between ( ?"+(index)+", ?"+(8)+" )");
			vs.add( startDate );
			vs.add( endDate );
		}
		
		Query query = em.createQuery( sql_stringBuffer.toString(), Document.class );
		//为查询设置所有的参数值
		for (int i = 0; i < vs.size(); i++) {
			query.setParameter( i + 1, vs.get(i));
		}
		
		return (Long) query.getSingleResult();
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
			}else if( "creatorDepartment".equals( orderField  )){//创建部门
				p = cb.and( p, cb.isNotNull( root.get( Document_.creatorDepartment ) ));
				if( "DESC".equalsIgnoreCase( order )){
					p = cb.and( p, cb.lessThan( root.get( Document_.creatorDepartment ), sequenceFieldValue.toString() ));
				}else{
					p = cb.and( p, cb.greaterThan( root.get( Document_.creatorDepartment ), sequenceFieldValue.toString() ));
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
		}else if( "creatorDepartment".equals( orderField  )){//创建部门
			if( "DESC".equalsIgnoreCase( order )){
				cq.orderBy( cb.desc( root.get( Document_.creatorDepartment ) ) );
			}else{
				cq.orderBy( cb.asc( root.get( Document_.creatorDepartment ) ) );
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
		if( appIdList != null && !appIdList.isEmpty() ){
			p = cb.and( p, root.get( Document_.appId ).in( appIdList ));
		}
		if( appAliasList != null && !appAliasList.isEmpty() ){
			p = cb.and( p, root.get( Document_.appName ).in( appAliasList ));
		}
		if( categoryIdList != null && !categoryIdList.isEmpty() ){
			p = cb.and( p, root.get( Document_.categoryId ).in( categoryIdList ));
		}
		if( categoryAliasList != null && !categoryAliasList.isEmpty() ){
			p = cb.and( p, root.get( Document_.categoryName ).in( categoryAliasList ));
		}
		if( publisherList != null && !publisherList.isEmpty() ){
			p = cb.and( p, root.get( Document_.creatorPerson ).in( publisherList ));
		}
		if( title != null && !title.isEmpty() ){
			p = cb.and( p, cb.like( root.get( Document_.title ), "%" + title + "%" ));
		}
		if( statusList == null || statusList.isEmpty() ){
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

		documents = em.createQuery( cq.where( p ) ).setMaxResults( 500 ).getResultList();
		if( documents != null && !documents.isEmpty() ){
			for( Document document : documents ){
				if( !ids.contains( document.getId() )){
					ids.add( document.getId() );
				}
			}
		}
		return ids;
	}

	
}
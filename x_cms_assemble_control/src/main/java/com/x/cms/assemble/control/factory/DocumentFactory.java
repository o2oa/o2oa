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
	public List<String> listByCatagoryId( String catagoryId ) throws Exception {
		EntityManager em = this.entityManagerContainer().get( Document.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery( String.class );
		Root<Document> root = cq.from( Document.class );
		Predicate p = cb.equal(root.get( Document_.catagoryId ), catagoryId );
		cq.select(root.get( Document_.id)).where(p);
		return em.createQuery( cq ).setMaxResults(100).getResultList();
	}
	
	@MethodDescribe("根据ID列示指定分类所有Document信息数量")
	public Long countByCatagoryId( String catagoryId ) throws Exception {
		EntityManager em = this.entityManagerContainer().get( Document.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Document> root = cq.from(Document.class);
		Predicate p = cb.equal( root.get(Document_.catagoryId), catagoryId );
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
		String order = wrapIn.getOrder();//排序方式
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
			sql_stringBuffer.append(" and o.docStatus = ?" + (index));
			vs.add( wrapIn.getStatusList().get(0).getValue() );
			index++;
		}
		if ((null != wrapIn.getTitleList()) && (!wrapIn.getTitleList().isEmpty())) {
			sql_stringBuffer.append(" and o.title like ?" + (index));
			vs.add( "%"+wrapIn.getTitleList().get(0).getValue()+"%" );
			index++;
		}
		if ((null != wrapIn.getAppIdList()) && (!wrapIn.getAppIdList().isEmpty())) {
			sql_stringBuffer.append(" and o.appId = ?" + (index));
			vs.add( wrapIn.getAppIdList().get(0).getValue() );
			index++;
		}
		if ((null != wrapIn.getCatagoryIdList()) && (!wrapIn.getCatagoryIdList().isEmpty())) {
			sql_stringBuffer.append(" and o.catagoryId = ?" + (index));
			vs.add( wrapIn.getCatagoryIdList().get(0).getValue() );
			index++;
		}
		if ((null != wrapIn.getCreatorList()) && (!wrapIn.getCreatorList().isEmpty())) {
			sql_stringBuffer.append(" and o.creatorPerson = ?" + (index));
			vs.add( wrapIn.getCreatorList().get(0).getValue() );
			index++;
		}
		if ((null != wrapIn.getCreateDateList()) && (!wrapIn.getCreateDateList().isEmpty())) {
			if( wrapIn.getCreateDateList().size() == 1){
				//从开始时间（yyyy-MM-DD），到现在
				startDate = dateOperation.getDateFromString( wrapIn.getCreateDateList().get(0).getValue().toString() );
				endDate = new Date();
			}else if( wrapIn.getCreateDateList().size() == 2){
				//从开始时间到结束时间（yyyy-MM-DD）
				startDate = dateOperation.getDateFromString( wrapIn.getCreateDateList().get(0).getValue().toString() );
				endDate = dateOperation.getDateFromString( wrapIn.getCreateDateList().get(1).getValue().toString() );
			}
			sql_stringBuffer.append(" and o.createTime between ( ?"+(index)+", ?"+(index+1)+" )");
			vs.add( startDate );
			vs.add( endDate );
		}
		if( wrapIn.getKey() != null && !wrapIn.getKey().isEmpty()){
			sql_stringBuffer.append(" order by o."+wrapIn.getKey()+" " + order );
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
		String order = wrapIn.getOrder();//排序方式
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
			sql_stringBuffer.append(" and o.docStatus = ?" + (index));
			vs.add( wrapIn.getStatusList().get(0).getValue() );
			index++;
		}
		if ((null != wrapIn.getTitleList()) && (!wrapIn.getTitleList().isEmpty())) {
			sql_stringBuffer.append(" and o.title like ?" + (index));
			vs.add( "%"+wrapIn.getTitleList().get(0).getValue()+"%" );
			index++;
		}
		if ((null != wrapIn.getAppIdList()) && (!wrapIn.getAppIdList().isEmpty())) {
			sql_stringBuffer.append(" and o.appId = ?" + (index));
			vs.add( wrapIn.getAppIdList().get(0).getValue() );
			index++;
		}
		if ((null != wrapIn.getCatagoryIdList()) && (!wrapIn.getCatagoryIdList().isEmpty())) {
			sql_stringBuffer.append(" and o.catagoryId = ?" + (index));
			vs.add( wrapIn.getCatagoryIdList().get(0).getValue() );
			index++;
		}
		if ((null != wrapIn.getCreatorList()) && (!wrapIn.getCreatorList().isEmpty())) {
			sql_stringBuffer.append(" and o.creatorPerson = ?" + (index));
			vs.add( wrapIn.getCreatorList().get(0).getValue() );
			index++;
		}
		if ((null != wrapIn.getCreateDateList()) && (!wrapIn.getCreateDateList().isEmpty())) {
			if( wrapIn.getCreateDateList().size() == 1){
				//从开始时间（yyyy-MM-DD），到现在
				startDate = dateOperation.getDateFromString( wrapIn.getCreateDateList().get(0).getValue().toString() );
				endDate = new Date();
			}else if( wrapIn.getCreateDateList().size() == 2){
				//从开始时间到结束时间（yyyy-MM-DD）
				startDate = dateOperation.getDateFromString( wrapIn.getCreateDateList().get(0).getValue().toString() );
				endDate = dateOperation.getDateFromString( wrapIn.getCreateDateList().get(1).getValue().toString() );
			}
			sql_stringBuffer.append(" and o.createTime between ( ?"+(index)+", ?"+(index+1)+" )");
			vs.add( startDate );
			vs.add( endDate );
		}
		
		if( wrapIn.getKey() != null && !wrapIn.getKey().isEmpty()){
			sql_stringBuffer.append(" order by o."+wrapIn.getKey()+" " + order );
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
			sql_stringBuffer.append(" and o.docStatus = ?" + (index));
			vs.add( wrapIn.getStatusList().get(0).getValue() );
			index++;
		}
		if ((null != wrapIn.getTitleList()) && (!wrapIn.getTitleList().isEmpty())) {
			sql_stringBuffer.append(" and o.title like ?" + (index));
			vs.add( "%"+wrapIn.getTitleList().get(0).getValue()+"%" );
			index++;
		}
		if ((null != wrapIn.getAppIdList()) && (!wrapIn.getAppIdList().isEmpty())) {
			sql_stringBuffer.append(" and o.appId = ?" + (index));
			vs.add( wrapIn.getAppIdList().get(0).getValue() );
			index++;
		}
		if ((null != wrapIn.getCatagoryIdList()) && (!wrapIn.getCatagoryIdList().isEmpty())) {
			sql_stringBuffer.append(" and o.catagoryId = ?" + (index));
			vs.add( wrapIn.getCatagoryIdList().get(0).getValue() );
			index++;
		}
		if ((null != wrapIn.getCreatorList()) && (!wrapIn.getCreatorList().isEmpty())) {
			sql_stringBuffer.append(" and o.creatorPerson = ?" + (index));
			vs.add( wrapIn.getCreatorList().get(0).getValue() );
			index++;
		}
		if ((null != wrapIn.getCreateDateList()) && (!wrapIn.getCreateDateList().isEmpty())) {
			if( wrapIn.getCreateDateList().size() == 1){
				//从开始时间（yyyy-MM-DD），到现在
				startDate = dateOperation.getDateFromString( wrapIn.getCreateDateList().get(0).getValue().toString() );
				endDate = new Date();
			}else if( wrapIn.getCreateDateList().size() == 2){
				//从开始时间到结束时间（yyyy-MM-DD）
				startDate = dateOperation.getDateFromString( wrapIn.getCreateDateList().get(0).getValue().toString() );
				endDate = dateOperation.getDateFromString( wrapIn.getCreateDateList().get(1).getValue().toString() );
			}
			sql_stringBuffer.append(" and o.createTime between ( ?"+(index)+", ?"+(8)+" )");
			vs.add( startDate );
			vs.add( endDate );
		}
		
		Query query = em.createQuery( sql_stringBuffer.toString(), Document.class );
		//为查询设置所有的参数值
		for (int i = 0; i < vs.size(); i++) {
			query.setParameter(i + 1, vs.get(i));
		}
		
		return (Long) query.getSingleResult();
	}
}
package com.x.cms.assemble.control.factory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.utils.annotation.MethodDescribe;
import com.x.cms.assemble.control.AbstractFactory;
import com.x.cms.assemble.control.Business;
import com.x.cms.core.entity.Document;
import com.x.cms.core.entity.Document_;
import com.x.cms.core.entity.content.DataItem;
import com.x.cms.core.entity.element.View;
import com.x.cms.core.entity.element.ViewCatagory;
import com.x.cms.core.entity.element.ViewCatagory_;
import com.x.cms.core.entity.element.View_;

/**
 * 视图配置管理基础功能服务类
 * 
 * @author liyi
 */
public class ViewFactory extends AbstractFactory {

	public ViewFactory( Business business ) throws Exception {
		super(business);
	}

	/**
	 * 获取指定Id的View信息对象
	 * @param id
	 * @return View
	 * @throws Exception
	 */
	@MethodDescribe("获取指定Id的View信息对象")
	public View get( String id ) throws Exception {
		return this.entityManagerContainer().find( id, View.class );
	}
	
	/**
	 * 列示全部的View信息ID列表
	 * @return List：String
	 * @throws Exception
	 */
	@MethodDescribe("列示全部的View文件附件信息ID列表")
	public List<String> listAll() throws Exception {
		EntityManager em = this.entityManagerContainer().get( View.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<View> root = cq.from( View.class );
		cq.select(root.get(View_.id));
		return em.createQuery(cq).getResultList();
	}
	
	/**
	 * 列示指定Id的View信息ID列表
	 * @param ids 需要查询的ID列表
	 * @return List：View
	 * @throws Exception
	 */
	@MethodDescribe("列示指定Id的View文件附件信息ID列表")
	public List<View> list(List<String> ids) throws Exception {
		EntityManager em = this.entityManagerContainer().get( View.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<View> cq = cb.createQuery( View.class );
		Root<View> root = cq.from( View.class );
		Predicate p = root.get(View_.id).in(ids);
		return em.createQuery(cq.where(p)).getResultList();
	}
	
	/**
	 * 列示指定分类的所有视图配置信息ID列表
	 * @param doucmentId 指定的文档ID
	 * @return
	 * @throws Exception 
	 */
	@MethodDescribe("列示指定分类的所有视图配置信息ID列表")
	public List<String> listByCatagoryId( String id ) throws Exception {		
		if( id == null || id.isEmpty() ){
			throw new Exception("内容管理listByCatagoryId方法不接受id为空的查询操作！");
		}
		EntityManager em = this.entityManagerContainer().get( ViewCatagory.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<ViewCatagory> root = cq.from( ViewCatagory.class );
		cq.select(root.get(ViewCatagory_.viewId));
		Predicate p = cb.equal(root.get( ViewCatagory_.catagoryId ), id);
		return em.createQuery(cq.where(p)).getResultList();
	}
	
	/**
	 * 列示指定应用ID的所有视图配置信息ID列表
	 * @param doucmentId 指定的文档ID
	 * @return
	 * @throws Exception 
	 */
	@MethodDescribe("列示指定应用ID的所有视图配置信息ID列表")
	public List<String> listByAppId( String id ) throws Exception {		
		if( id == null || id.isEmpty() ){
			throw new Exception("内容管理listByAppId方法不接受id为空的查询操作！");
		}
		EntityManager em = this.entityManagerContainer().get( View.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<View> root = cq.from( View.class );
		cq.select(root.get(View_.id));
		Predicate p = cb.equal(root.get( View_.appId ), id);
		return em.createQuery(cq.where(p)).getResultList();
	}
	
	/**
	 * 列示指定表单ID的所有视图配置信息ID列表
	 * @param doucmentId 指定的文档ID
	 * @return
	 * @throws Exception 
	 */
	@MethodDescribe("列示指定表单ID的所有视图配置信息ID列表")
	public List<String> listByFormId( String id ) throws Exception {		
		if( id == null || id.isEmpty() ){
			throw new Exception("内容管理listByFormId方法不接受id为空的查询操作！");
		}
		EntityManager em = this.entityManagerContainer().get( View.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<View> root = cq.from( View.class );
		cq.select(root.get(View_.id));
		Predicate p = cb.equal(root.get( View_.formId ), id);
		return em.createQuery(cq.where(p)).getResultList();
	}


	public List<Document> nextPageDocuemntView( String id, Integer count, Map<String, Object> condition ) throws Exception {
		String orderField = condition.get("orderField") == null ? null: condition.get("orderField").toString();
		List<Document> docs = null;		
		//有一部分信息是文档自身的信息，直接去文档信息里排序获取
		if( "title".equalsIgnoreCase(orderField) 
				|| "catagoryName".equalsIgnoreCase(orderField) 
				||"createTime".equalsIgnoreCase(orderField) 
				||"updateTime".equalsIgnoreCase(orderField) 
				||"creatorPerson".equalsIgnoreCase(orderField) 
				|| "creatorDepartment".equalsIgnoreCase(orderField) 
				|| "creatorCompany".equalsIgnoreCase(orderField)){
			//按文档属性排序
			docs = nextPageViewFromDocProperty( id, count, condition );
		}else{
			//按表单属性排序 
			docs = nextPageViewFromDataitem( id, count, condition );
		}				
		return docs;
	}
	
	/**
	 * 从文档的实际属性信息里选择属性进行排序分页
	 * @param pagination
	 * @param condition
	 * @return
	 * @throws Exception
	 */
	public List<Document> nextPageViewFromDataitem( String id, Integer count, Map<String, Object> condition ) throws Exception {
		List<Document> documentList = new ArrayList<Document>();
		Document document = null;
		String viewId = condition.get("viewId") == null ? null: condition.get("viewId").toString();
		String catagoryId = condition.get("catagoryId") == null ? null: condition.get("catagoryId").toString();
		String orderField = condition.get("orderField") == null ? null: condition.get("orderField").toString();
		String orderType = condition.get("orderType") == null ? null: condition.get("orderType").toString();
		String orderFieldType = condition.get("orderFieldType") == null ? null: condition.get("orderFieldType").toString();
		String searchDocStatus = condition.get("searchDocStatus") == null ? null: condition.get("searchDocStatus").toString();
		
		if( viewId == null ){
			throw new Exception("[pagenationDocsFromDataitem]传入的参数，视图Id为空, 无法继续进行查询操作！");
		}
		if( catagoryId == null ){
			throw new Exception("[pagenationDocsFromDataitem]传入的参数，分类Id为空, 无法继续进行查询操作！");
		}
		if( orderField == null ){
			orderField = "createTime"; // 默认按创建时间排序
		}
		if( orderType == null ){
			orderType = "desc"; //默认倒序
		}
		if( orderFieldType == null ){
			orderFieldType = "string"; //列数据类型(string|datetime)，涉及到需要查询的值存储在哪一列里xstringvalue | xtimevalue
		}
		
		View view = get( viewId );		
		//1、查询视图信息
		if( view == null ){
			throw new Exception("[pagenationDocsFromDataitem]视图 view{'id':'"+viewId+"'}不存在, 无法继续进行查询操作！");
		}
		
		//查询出该分类中所有的已经发布的文档ID
		List<String> allDocIdInStatus = getDocumentIdsWithDocStatus( catagoryId, searchDocStatus );
		
		if( allDocIdInStatus != null && allDocIdInStatus.size() > 0 ){
			EntityManager em = this.entityManagerContainer().get( DataItem.class );
			List<Object> vs = new ArrayList<>();
			StringBuffer sql_stringBuffer = new StringBuffer();
			Integer index = 1;
			String orderTypeEx = (StringUtils.equalsIgnoreCase(orderType, "desc") ? "<" : ">");
			
			sql_stringBuffer.append( "SELECT distinct o FROM "+DataItem.class.getCanonicalName()+" o where 1 = 1" );
			sql_stringBuffer.append( " and o.catagoryId = " +  (" ?" + (index)) );
			vs.add( catagoryId );
			index++;
			
			sql_stringBuffer.append( " and o.path0 = " +  (" ?" + (index)) );
			vs.add( orderField );
			index++;
			
			sql_stringBuffer.append( " and o.docId in "+  (" ?" + (index))  );
			vs.add( allDocIdInStatus );
			index++;
			
			if( id == null || "(0)".equals(id) || "".equals(id)){
				//说明是取第一页
			}else{
				//根据ID查询文档的信息
				document =  this.entityManagerContainer().find( id, Document.class );
				DataItem dataItem  = null;
				if( document != null ){
					//进一步查询文档相应的属性在DataItem里的具体信息
					dataItem = getDataItemSequenceFromDocInfo( document.getId(), orderField );
				}
				if( dataItem != null ){
					//要根据上一页最后一条的ID来取下一页
					if( "datetime".equalsIgnoreCase(orderFieldType) && dataItem.getDateTimeValue() != null ){
						sql_stringBuffer.append( " and ( o.dateTimeValue "+ orderTypeEx + (" ?" + (index)) );
						vs.add( dataItem.getTimeValue() );
						index++;
					}else if( dataItem.getStringValue() != null ){
						sql_stringBuffer.append( " and ( o.stringValue "+ orderTypeEx  + (" ?" + (index)) );
						vs.add( dataItem.getStringValue() );
						index++;
					}else if( dataItem.getStringValue() != null ){
						sql_stringBuffer.append( " and ( o.stringValue "+ orderTypeEx  + (" ?" + (index)) );
						vs.add( dataItem.getStringValue() );
						index++;
					}
					
					if( "datetime".equalsIgnoreCase(orderFieldType) && dataItem.getDateTimeValue() != null ){
						sql_stringBuffer.append( " or ( o.dateTimeValue =" + (" ?" + (index)) );
						vs.add( dataItem.getTimeValue() );
						index++;
					}else if( dataItem.getStringValue() != null ){
						sql_stringBuffer.append( " or ( o.stringValue =" + (" ?" + (index)) );
						vs.add( dataItem.getStringValue() );
						index++;
					}
					
					if( "datetime".equalsIgnoreCase(orderFieldType) && dataItem.getDateTimeValue() != null ){
						sql_stringBuffer.append( " and o.sequence " + orderTypeEx + (" ?" + (index)) + ") ) ");
						vs.add( dataItem.getSequence() );
						index++;
					}else if( dataItem.getStringValue() != null ){
						sql_stringBuffer.append( " and o.sequence " + orderTypeEx + (" ?" + (index)) + ") ) ");
						vs.add( dataItem.getSequence() );
						index++;
					}
				}
			}			
			
			if( "datetime".equalsIgnoreCase(orderFieldType)){
				if( "asc".equalsIgnoreCase( orderType ) ){
					sql_stringBuffer.append( " order by o.dateTimeValue");
				}else{
					sql_stringBuffer.append( " order by o.dateTimeValue desc");
				}
			}else{
				if( "asc".equalsIgnoreCase( orderType ) ){
					sql_stringBuffer.append( " order by o.stringValue");
				}else{
					sql_stringBuffer.append( " order by o.stringValue desc");
				}
			}
			
			List<DataItem> dataItemList = null;
			
			Query query = em.createQuery( sql_stringBuffer.toString(), DataItem.class );
			//为查询设置所有的参数值
			for (int i = 0; i < vs.size(); i++) {
				query.setParameter(i + 1, vs.get(i));
			}
			dataItemList = query.setMaxResults( count ).getResultList();

			if( dataItemList != null && dataItemList.size() > 0 ){
				//根据每个DataItem的DocId查询所有需要展示 的文档对象信息，放到List里进行返回
				for( DataItem dataItem : dataItemList ){
					document = this.entityManagerContainer().find( dataItem.getDocId(), Document.class );
					if( document != null ){
						documentList.add(document);
					}
				}
			}
		}
		
		return documentList;
	}
	
	public DataItem getDataItemSequenceFromDocInfo( String docId, String path0 ) throws Exception {
		EntityManager em = this.entityManagerContainer().get( DataItem.class );
		List<Object> vs = new ArrayList<>();
		List<DataItem> dataitemList = null;
		StringBuffer sql_stringBuffer = new StringBuffer();
		Integer index = 1;
		
		sql_stringBuffer.append( "SELECT o FROM "+DataItem.class.getCanonicalName()+" o where 1=1" );
		sql_stringBuffer.append( " and o.docId = " +  (" ?" + (index)) );
		vs.add( docId );
		index++;
		
		sql_stringBuffer.append( " and o.path0 = " +  (" ?" + (index)) );
		vs.add( path0 );
		index++;
		
		Query query = em.createQuery( sql_stringBuffer.toString(), Document.class );
		//为查询设置所有的参数值
		for (int i = 0; i < vs.size(); i++) {
			query.setParameter(i + 1, vs.get(i));
		}
		dataitemList = query.setMaxResults( 1 ).getResultList();
		if( dataitemList != null && dataitemList.size() > 0 ){
			return dataitemList.get( 0 );
		}
		return null;
	}
	
	public List<String> getDocumentIdsWithDocStatus( String catagoryId, String docStatus ) throws Exception {
		//查询出该分类中所有的已经发布的文档ID
		EntityManager em = this.entityManagerContainer().get( Document.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Document> root_doc = cq.from( Document.class );
		cq.select(root_doc.get(Document_.id));
		Predicate p = cb.equal(root_doc.get( Document_.catagoryId ), catagoryId );
		if( docStatus != null && docStatus.length() > 0 ){
			p = cb.and( p , cb.equal( root_doc.get( Document_.docStatus ), docStatus ) );
		}
		return em.createQuery(cq.where(p)).getResultList();
	}
	
	
	/**
	 * 根据文档自身的属性进行排序和分页，文档自身属性：
	 *  title
		catagoryName
		createTime
		updateTime
		creatorPerson
		creatorDepartment
		creatorCompany
	 * @param pagination
	 * @param condition
	 * @return
	 * @throws Exception
	 */
	public List<Document> nextPageViewFromDocProperty( String id, Integer count,  Map<String, Object> condition ) throws Exception {
		String viewId = condition.get("viewId") == null ? null: condition.get("viewId").toString();
		String catagoryId = condition.get("catagoryId") == null ? null: condition.get("catagoryId").toString();
		String orderField = condition.get("orderField") == null ? null: condition.get("orderField").toString();
		String orderType = condition.get("orderType") == null ? null: condition.get("orderType").toString();
		String searchDocStatus = condition.get("searchDocStatus") == null ? null: condition.get("searchDocStatus").toString();

		if( viewId == null ){
			throw new Exception("传入的参数，视图Id为空, 无法继续进行查询操作！");
		}
		if( catagoryId == null ){
			throw new Exception("传入的参数，分类Id为空, 无法继续进行查询操作！");
		}
		if( orderField == null ){
			orderField = "createTime"; // 默认按创建时间排序
		}
		if( orderType == null ){
			orderType = "desc"; //默认倒序
		}

		View view = get( viewId );
		//1、查询视图信息
		if( view == null ){
			throw new Exception("[pagenationDocsFromDocProperty]视图 view{'id':'"+viewId+"'}不存在, 无法继续进行查询操作！");
		}
		
		//检查传入的文档的ID是否合法，并且查找相应的属性值
		Document document = this.entityManagerContainer().find( id, Document.class );
				
		EntityManager em = this.entityManagerContainer().get( Document.class );
		List<Object> vs = new ArrayList<>();
		StringBuffer sql_stringBuffer = new StringBuffer();
		Integer index = 1;
		String orderTypeEx = (StringUtils.equalsIgnoreCase(orderType, "desc") ? "<" : ">");
		
		sql_stringBuffer.append( "SELECT o FROM "+Document.class.getCanonicalName()+" o where 1=1" );
		
		if( searchDocStatus != null && searchDocStatus.length() > 0 ){
			sql_stringBuffer.append( " and o.docStatus = " + (" ?" + (index)) );
			vs.add( searchDocStatus );
			index++;
		}
		
		sql_stringBuffer.append( " and o.catagoryId = " + (" ?" + (index)) );
		vs.add( catagoryId );
		index++;
		
		if( id == null || "(0)".equals(id) || "".equals(id)){
			//说明是取第一页
		}else{
			if( document != null ){
				Object orderFieldValue = null;
				if( "title".equalsIgnoreCase(orderField)){
					orderFieldValue = document.getTitle();
				}else if( "id".equalsIgnoreCase(orderField)){
					orderFieldValue = document.getId();
				}else if("catagoryName".equalsIgnoreCase(orderField)){
					orderFieldValue = document.getCatagoryId();
				}else if("createTime".equalsIgnoreCase(orderField)){
					orderFieldValue = document.getCreateTime();
				}else if("updateTime".equalsIgnoreCase(orderField)){
					orderFieldValue = document.getUpdateTime();
				}else if("creatorPerson".equalsIgnoreCase(orderField)){
					orderFieldValue = document.getCreatorPerson();
				}else if("creatorDepartment".equalsIgnoreCase(orderField)){
					orderFieldValue = document.getCreatorDepartment();
				}else if("creatorCompany".equalsIgnoreCase(orderField)){
					orderFieldValue = document.getCreatorCompany();
				}
				
				//要根据上一页最后一条的ID来取下一页
				sql_stringBuffer.append( " and ( o."+ orderField + orderTypeEx + (" ?" + (index)) );
				vs.add( orderFieldValue );
				index++;
				
				sql_stringBuffer.append( " or ( o."+ orderField + " = " + (" ?" + (index))  );
				vs.add( orderFieldValue );
				index++;
				
				sql_stringBuffer.append( " and o.sequence " + orderTypeEx + (" ?" + (index)) + ") ) ");
				vs.add( document.getSequence() );
				index++;
			}
		}
		
		if( "title".equalsIgnoreCase(orderField)){
			if( "asc".equalsIgnoreCase( orderType ) ){
				sql_stringBuffer.append( " order by o.title ");
			}else{
				sql_stringBuffer.append( " order by o.title desc");
			}
		}else if( "id".equalsIgnoreCase(orderField)){
			if( "asc".equalsIgnoreCase( orderType ) ){
				sql_stringBuffer.append( " order by o.id");
			}else{
				sql_stringBuffer.append( " order by o.id desc");
			}
		}else if("catagoryName".equalsIgnoreCase(orderField)){
			if( "asc".equalsIgnoreCase( orderType ) ){
				sql_stringBuffer.append( " order by o.catagoryName");
			}else{
				sql_stringBuffer.append( " order by o.catagoryName desc");
			}
		}else if("createTime".equalsIgnoreCase(orderField)){
			if( "asc".equalsIgnoreCase( orderType ) ){
				sql_stringBuffer.append( " order by o.createTime");
			}else{
				sql_stringBuffer.append( " order by o.createTime desc");
			}
		}else if("updateTime".equalsIgnoreCase(orderField)){
			if( "asc".equalsIgnoreCase( orderType ) ){
				sql_stringBuffer.append( " order by o.updateTime");
			}else{
				sql_stringBuffer.append( " order by o.updateTime desc");
			}
		}else if("creatorPerson".equalsIgnoreCase(orderField)){
			if( "asc".equalsIgnoreCase( orderType ) ){
				sql_stringBuffer.append( " order by o.creatorPerson");
			}else{
				sql_stringBuffer.append( " order by o.creatorPerson desc");
			}
		}else if("creatorDepartment".equalsIgnoreCase(orderField)){
			if( "asc".equalsIgnoreCase( orderType ) ){
				sql_stringBuffer.append( " order by o.creatorDepartment");
			}else{
				sql_stringBuffer.append( " order by o.creatorDepartment desc");
			}
		}else if("creatorCompany".equalsIgnoreCase(orderField)){
			if( "asc".equalsIgnoreCase( orderType ) ){
				sql_stringBuffer.append( " order by o.creatorCompany");
			}else{
				sql_stringBuffer.append( " order by o.creatorCompany desc");
			}
		}
		Query query = em.createQuery( sql_stringBuffer.toString(), Document.class );
		//为查询设置所有的参数值
		for (int i = 0; i < vs.size(); i++) {
			query.setParameter(i + 1, vs.get(i));
		}
		return query.setMaxResults( count ).getResultList();
	}
	
	public Long getDocIdsCount( Map<String, Object> condition ) throws Exception {
		String catagoryId = condition.get("catagoryId") == null ? null: condition.get("catagoryId").toString();
		String searchDocStatus = condition.get("searchDocStatus") == null ? null: condition.get("searchDocStatus").toString();
		if( catagoryId == null ){
			throw new Exception("[getDocIdsCount]传入的参数，分类Id为空, 无法继续进行查询操作！");
		}		
		EntityManager em = this.entityManagerContainer().get( Document.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Document> root = cq.from( Document.class );
		cq.select( cb.count(root));
		Predicate p = cb.equal(root.get( Document_.catagoryId ), catagoryId );
		if( searchDocStatus != null && searchDocStatus.length() > 0 ){
			p = cb.and( p, cb.equal( root.get( Document_.docStatus ), searchDocStatus));
		}
		return em.createQuery(cq).getSingleResult().longValue();
	}
}
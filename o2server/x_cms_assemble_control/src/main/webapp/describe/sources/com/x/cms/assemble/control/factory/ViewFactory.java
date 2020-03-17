package com.x.cms.assemble.control.factory;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;

import com.x.cms.assemble.control.AbstractFactory;
import com.x.cms.assemble.control.Business;
import com.x.cms.core.entity.element.View;
import com.x.cms.core.entity.element.ViewCategory;
import com.x.cms.core.entity.element.ViewCategory_;
import com.x.cms.core.entity.element.View_;

/**
 * 视图配置管理基础功能服务类
 * 
 * @author O2LEE
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
	//@MethodDescribe("获取指定Id的View信息对象")
	public View get( String id ) throws Exception {
		return this.entityManagerContainer().find( id, View.class );
	}
	
	public View flag( String flag ) throws Exception {
		return this.entityManagerContainer().flag( flag, View.class );
	}
	
	/**
	 * 列示全部的View信息ID列表
	 * @return List：String
	 * @throws Exception
	 */
//	@MethodDescribe("列示全部的View文件附件信息ID列表")
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
	//@MethodDescribe("列示指定Id的View文件附件信息ID列表")
//	public List<View> list(List<String> ids) throws Exception {
//		EntityManager em = this.entityManagerContainer().get( View.class );
//		CriteriaBuilder cb = em.getCriteriaBuilder();
//		CriteriaQuery<View> cq = cb.createQuery( View.class );
//		Root<View> root = cq.from( View.class );
//		Predicate p = root.get(View_.id).in(ids);
//		return em.createQuery(cq.where(p)).getResultList();
//	}
	
	/**
	 * 列示指定分类的所有视图配置信息ID列表
	 * @param doucmentId 指定的文档ID
	 * @return
	 * @throws Exception 
	 */
	//@MethodDescribe("列示指定分类的所有视图配置信息ID列表")
	public List<String> listByCategoryId( String categoryId ) throws Exception {		
		if( StringUtils.isEmpty(categoryId) ){
			throw new Exception("内容管理listByCategoryId方法不接受categoryId为空的查询操作！");
		}
		EntityManager em = this.entityManagerContainer().get( ViewCategory.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<ViewCategory> root = cq.from( ViewCategory.class );
		cq.select( root.get(ViewCategory_.viewId ));
		Predicate p = cb.equal(root.get( ViewCategory_.categoryId ), categoryId );
		return em.createQuery(cq.where(p)).getResultList();
	}
	
	/**
	 * 列示指定应用ID的所有视图配置信息ID列表
	 * @param doucmentId 指定的文档ID
	 * @return
	 * @throws Exception 
	 */
	//@MethodDescribe("列示指定应用ID的所有视图配置信息ID列表")
	public List<String> listByAppId( String id ) throws Exception {		
		if( StringUtils.isEmpty(id) ){
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
	//@MethodDescribe("列示指定表单ID的所有视图配置信息ID列表")
	public List<String> listByFormId( String id ) throws Exception {		
		if( StringUtils.isEmpty(id) ){
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

//	public Item getItemSequenceFromDocInfo( String docId, String path0 ) throws Exception {
//		List<Item> dataitemList = null;
//		EntityManager em = this.entityManagerContainer().get( Item.class );
//		CriteriaBuilder cb = em.getCriteriaBuilder();
//		CriteriaQuery<Item> cq = cb.createQuery(Item.class);
//		Root<Item> root_doc = cq.from( Item.class );
//		Predicate p = cb.equal( root_doc.get( Item_.bundle ), docId );
//		p = cb.and( p , cb.equal( root_doc.get( Item_.path0 ), path0 ) );
//		dataitemList = em.createQuery( cq.where(p)).getResultList();
//		if( dataitemList != null && !dataitemList.isEmpty() ){
//			return dataitemList.get( 0 );
//		}
//		return null;
//	}
//	
//	public List<String> getDocumentIdsWithDocStatus( String categoryId, String docStatus ) throws Exception {
//		//查询出该分类中所有的已经发布的文档ID
//		EntityManager em = this.entityManagerContainer().get( Document.class );
//		CriteriaBuilder cb = em.getCriteriaBuilder();
//		CriteriaQuery<String> cq = cb.createQuery(String.class);
//		Root<Document> root_doc = cq.from( Document.class );
//		cq.select(root_doc.get(Document_.id));
//		Predicate p = cb.equal(root_doc.get( Document_.categoryId ), categoryId );
//		if( docStatus != null && docStatus.length() > 0 ){
//			p = cb.and( p , cb.equal( root_doc.get( Document_.docStatus ), docStatus ) );
//		}
//		return em.createQuery(cq.where(p)).getResultList();
//	}

//	public List<Document> nextPageDocuemntView( String id, Integer count, List<String> viewAbleDocIds, Map<String, Object> condition ) throws Exception {
//		String orderField = condition.get("orderField") == null ? null: condition.get("orderField").toString();
//		String orderType = condition.get("orderType") == null ? null: condition.get("orderType").toString();
//		String orderFieldType = condition.get("orderFieldType") == null ? null: condition.get("orderFieldType").toString();
//		List<Document> docs = null;
//		
//		if( orderField == null ){
//			orderField = "createTime"; // 默认按创建时间排序
//		}
//		if( orderType == null ){
//			orderType = "desc"; //默认倒序
//		}
//		if( orderFieldType == null ){
//			orderFieldType = "string"; //列数据类型(string|datetime)，涉及到需要查询的值存储在哪一列里xstringvalue | xtimevalue
//		}
//		
//		LogUtil.INFO( "orderField", orderField );
//		//有一部分信息是文档自身的信息，直接去文档信息里排序获取
//		if( "title".equalsIgnoreCase(orderField) || "categoryName".equalsIgnoreCase(orderField) 
//		  ||"createTime".equalsIgnoreCase(orderField) ||"updateTime".equalsIgnoreCase(orderField) 
//		  ||"creatorPerson".equalsIgnoreCase(orderField) || "creatorUnitName".equalsIgnoreCase(orderField) 
//		  || "creatorTopUnitName".equalsIgnoreCase(orderField)){
//			//按文档属性排序
//			docs = nextPageViewFromDocProperty( id, count, viewAbleDocIds, orderField, orderType );
//		}else{
//			//按表单属性属性排序 
//			docs = nextPageViewFromDataitem( id, count, viewAbleDocIds, orderField, orderType, orderFieldType  );
//		}
//		return docs;
//	}
//	
//	/**
//	 * 从文档的实际属性信息里选择属性进行排序分页
//	 * @param viewAbleDocIds 
//	 * @param pagination
//	 * @param condition
//	 * @return
//	 * @throws Exception
//	 */
//	public List<Document> nextPageViewFromDataitem( String id, Integer count, List<String> viewAbleDocIds, 
//			String orderField, String orderType, String orderFieldType ) throws Exception {
//		List<Document> documentList = new ArrayList<Document>();
//		List<Item> dataItemList = null;
//		Document document = null;
//		Item dataItem = null;
//		EntityManager em = this.entityManagerContainer().get( Item.class );
//		CriteriaBuilder cb = em.getCriteriaBuilder();
//		CriteriaQuery<Item> cq = cb.createQuery(Item.class);
//		Root<Item> root_doc = cq.from( Item.class );
//		
//		Predicate orderFieldWhere = null;
//		Predicate sequenceWhere = null;
//		
//		if( id != null && !"(0)".equals(id) && !id.isEmpty() ){
//			document = this.entityManagerContainer().find( id, Document.class );
//			if( document != null ){
//				dataItem = getItemSequenceFromDocInfo( document.getId(), orderField );
//				if( dataItem != null ){
//					//排序值大于或者小于当前文档的值 ，或者与当前文档值相等但是序列号大于或者小于当前文档的序列号
//					if( "datetime".equalsIgnoreCase(orderFieldType) && dataItem.getDateTimeValue() != null ){
//						if( "desc".equalsIgnoreCase( orderType )){
//							orderFieldWhere = cb.lessThan( root_doc.get( Item_.dateTimeValue ), dataItem.getTimeValue() );
//						}else if("asc".equalsIgnoreCase( orderType )){
//							orderFieldWhere = cb.greaterThan( root_doc.get( Item_.dateTimeValue ), dataItem.getTimeValue() );
//						}
//					}else if( dataItem.getStringValue() != null ){
//						if( "desc".equalsIgnoreCase( orderType )){
//							orderFieldWhere = cb.lessThan( root_doc.get( Item_.stringShortValue ), dataItem.getStringValue() );
//						}else if("asc".equalsIgnoreCase( orderType )){
//							orderFieldWhere = cb.greaterThan( root_doc.get( Item_.stringShortValue ), dataItem.getStringValue() );
//						}
//					}
//					
//					if( "datetime".equalsIgnoreCase(orderFieldType) && dataItem.getDateTimeValue() != null ){
//						sequenceWhere = cb.equal( root_doc.get( Item_.dateTimeValue ), dataItem.getTimeValue() );
//						sequenceWhere = cb.and( sequenceWhere, cb.lessThan( root_doc.get( Item_.sequence ), dataItem.getSequence()) );
//					}else if( dataItem.getStringValue() != null ){
//						sequenceWhere = cb.equal( root_doc.get( Item_.dateTimeValue ), dataItem.getTimeValue() );
//						sequenceWhere = cb.and( sequenceWhere, cb.greaterThan( root_doc.get( Item_.sequence ), dataItem.getSequence()) );
//					}
//					orderFieldWhere = cb.or( orderFieldWhere, sequenceWhere );
//				}
//			}
//		}
//		
//		Predicate p = root_doc.get( Item_.bundle ).in( viewAbleDocIds );
//		p = cb.and( p, cb.equal( root_doc.get( Item_.path0 ), orderField));
//		if( orderFieldWhere != null ){
//			p = cb.and( p, orderFieldWhere );
//		}
//		if( "datetime".equalsIgnoreCase(orderFieldType)){
//			if( "asc".equalsIgnoreCase( orderType ) ){
//				cq.orderBy( cb.asc( root_doc.get( Item_.dateTimeValue ) ));	
//			}else{
//				cq.orderBy( cb.desc( root_doc.get( Item_.dateTimeValue ) ));	
//			}
//		}else{
//			if( "asc".equalsIgnoreCase( orderType ) ){
//				cq.orderBy( cb.asc( root_doc.get( Item_.stringShortValue ) ));	
//			}else{
//				cq.orderBy( cb.desc( root_doc.get( Item_.stringShortValue ) ));	
//			}
//		}
//		dataItemList = em.createQuery( cq.where(p) ).setMaxResults(count).getResultList();
//		
//		if( dataItemList != null && !dataItemList.isEmpty() ){
//			//根据每个Item的DocId查询所有需要展示 的文档对象信息，放到List里进行返回
//			for( Item tmp_dataItem : dataItemList ){
//				document = this.entityManagerContainer().find( tmp_dataItem.getBundle(), Document.class );
//				if( document != null ){
//					documentList.add(document);
//				}
//			}
//		}
//		return documentList;
//	}
//	
//	/**
//	 * 根据文档自身的属性进行排序和分页，文档自身属性：
//	 * **/
//	public List<Document> nextPageViewFromDocProperty( String id, Integer count,  List<String> viewAbleDocIds,  String orderField, String orderType	) throws Exception {
//		Document document = null;
//		EntityManager em = this.entityManagerContainer().get( Document.class );
//		CriteriaBuilder cb = em.getCriteriaBuilder();
//		CriteriaQuery<Document> cq = cb.createQuery(Document.class);
//		Root<Document> root_doc = cq.from( Document.class );
//		Predicate orderFieldWhere = null;
//		Predicate sequenceWhere = null;
//		
//		if( id != null && !"(0)".equals(id) && !id.isEmpty() ){
//			document = this.entityManagerContainer().find( id, Document.class );
//			if( document != null ){
//				//排序值大于或者小于当前文档的值 ，或者与当前文档值相等但是序列号大于或者小于当前文档的序列号
//				orderFieldWhere = getOrderFieldValueFromDocument(cb, root_doc, orderType, document, orderField );
//				sequenceWhere = getOrderFieldValueFromDocument(cb, root_doc, "equal", document, orderField );
//				if( "desc".equalsIgnoreCase( orderType )){
//					sequenceWhere = cb.and( sequenceWhere, cb.lessThan( root_doc.get(Document_.sequence), document.getSequence() ));
//				}else if( "asc".equalsIgnoreCase( orderType ) ){
//					sequenceWhere = cb.and( sequenceWhere, cb.greaterThan( root_doc.get(Document_.sequence), document.getSequence() ));
//				}
//				orderFieldWhere = cb.or( orderFieldWhere, sequenceWhere );
//			}
//		}
//		Predicate p = root_doc.get( Document_.id ).in( viewAbleDocIds );
//		if( orderFieldWhere != null ){
//			p = cb.and( p, orderFieldWhere );
//		}
//		cq.orderBy( getOrderExpression(cb, root_doc, orderType, document, orderField ));
//		return em.createQuery(cq.where(p)).setMaxResults(count).getResultList();
//	}
//	
//	public Long getDocIdsCount( Map<String, Object> condition ) throws Exception {
//		String categoryId = condition.get("categoryId") == null ? null: condition.get("categoryId").toString();
//		String searchDocStatus = condition.get("searchDocStatus") == null ? null: condition.get("searchDocStatus").toString();
//		if( categoryId == null ){
//			throw new Exception("[getDocIdsCount]传入的参数，分类Id为空, 无法继续进行查询操作！");
//		}		
//		EntityManager em = this.entityManagerContainer().get( Document.class );
//		CriteriaBuilder cb = em.getCriteriaBuilder();
//		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
//		Root<Document> root = cq.from( Document.class );
//		cq.select( cb.count(root));
//		Predicate p = cb.equal(root.get( Document_.categoryId ), categoryId );
//		if( searchDocStatus != null && searchDocStatus.length() > 0 ){
//			p = cb.and( p, cb.equal( root.get( Document_.docStatus ), searchDocStatus));
//		}
//		return em.createQuery(cq).getSingleResult().longValue();
//	}
//	
//	public Order getOrderExpression( CriteriaBuilder cb, Root<Document> root_doc, String order, Document document, String orderFieldName ){
//		if( "title".equalsIgnoreCase( orderFieldName)){
//			if( "asc".equalsIgnoreCase( order ) ){
//				return cb.asc( root_doc.get( Document_.title ) );
//			}else{
//				return cb.desc( root_doc.get( Document_.title ) );
//			}
//		}else if( "id".equalsIgnoreCase(orderFieldName)){
//			if( "asc".equalsIgnoreCase( order ) ){
//				return cb.asc( root_doc.get( Document_.id ) );
//			}else{
//				return cb.desc( root_doc.get( Document_.id ) );
//			}
//		}else if("categoryName".equalsIgnoreCase(orderFieldName)){
//			if( "asc".equalsIgnoreCase( order ) ){
//				return cb.asc( root_doc.get( Document_.categoryName ) );
//			}else{
//				return cb.desc( root_doc.get( Document_.categoryName ) );
//			}
//		}else if("createTime".equalsIgnoreCase(orderFieldName)){
//			if( "asc".equalsIgnoreCase( order ) ){
//				return cb.asc( root_doc.get( Document_.createTime ) );
//			}else{
//				return cb.desc( root_doc.get( Document_.createTime ) );
//			}
//		}else if("updateTime".equalsIgnoreCase(orderFieldName)){
//			if( "asc".equalsIgnoreCase( order ) ){
//				return cb.asc( root_doc.get( Document_.updateTime ) );
//			}else{
//				return cb.desc( root_doc.get( Document_.updateTime ) );
//			}
//		}else if("creatorPerson".equalsIgnoreCase(orderFieldName)){
//			if( "asc".equalsIgnoreCase( order ) ){
//				return cb.asc( root_doc.get( Document_.creatorPerson ) );
//			}else{
//				return cb.desc( root_doc.get( Document_.creatorPerson ) );
//			}
//		}else if("creatorUnitName".equalsIgnoreCase(orderFieldName)){
//			if( "asc".equalsIgnoreCase( order ) ){
//				return cb.asc( root_doc.get( Document_.creatorUnitName ) );
//			}else{
//				return cb.desc( root_doc.get( Document_.creatorUnitName ) );
//			}
//		}else if("creatorTopUnitName".equalsIgnoreCase(orderFieldName)){
//			if( "asc".equalsIgnoreCase( order ) ){
//				return cb.asc( root_doc.get( Document_.creatorTopUnitName ) );
//			}else{
//				return cb.desc( root_doc.get( Document_.creatorTopUnitName ) );
//			}
//		}
//		return null;
//	}
//	
//	public Predicate getOrderFieldValueFromDocument( CriteriaBuilder cb, Root<Document> root_doc, String order, Document document, String orderFieldName ){
//		if( "title".equalsIgnoreCase( orderFieldName )){
//			if( "desc".equalsIgnoreCase( order )){
//				return cb.lessThan( root_doc.get(Document_.title), document.getTitle() );
//			}else if( "asc".equalsIgnoreCase( order ) ){
//				return cb.greaterThan( root_doc.get(Document_.title), document.getTitle() );
//			}else if( "equal".equalsIgnoreCase( order ) ){
//				return cb.equal( root_doc.get(Document_.title), document.getTitle() );
//			}
//		}else if( "id".equalsIgnoreCase( orderFieldName )){
//			if( "desc".equalsIgnoreCase( order )){
//				return cb.lessThan( root_doc.get(Document_.id), document.getId() );
//			}else if( "asc".equalsIgnoreCase( order ) ){
//				return cb.greaterThan( root_doc.get(Document_.id), document.getId() );
//			}else if( "equal".equalsIgnoreCase( order ) ){
//				return cb.equal( root_doc.get(Document_.id), document.getId() );
//			}
//		}else if("categoryName".equalsIgnoreCase( orderFieldName )){
//			if( "desc".equalsIgnoreCase( order )){
//				return cb.lessThan( root_doc.get(Document_.categoryName), document.getCategoryName() );
//			}else if( "asc".equalsIgnoreCase( order ) ){
//				return cb.greaterThan( root_doc.get(Document_.categoryName), document.getCategoryName() );
//			}else if( "equal".equalsIgnoreCase( order ) ){
//				return cb.equal( root_doc.get(Document_.categoryName), document.getCategoryName() );
//			}
//		}else if("createTime".equalsIgnoreCase( orderFieldName )){
//			if( "desc".equalsIgnoreCase( order )){
//				return cb.lessThan( root_doc.get(Document_.createTime), document.getCreateTime() );
//			}else if( "asc".equalsIgnoreCase( order ) ){
//				return cb.greaterThan( root_doc.get(Document_.createTime), document.getCreateTime() );
//			}else if( "equal".equalsIgnoreCase( order ) ){
//				return cb.equal( root_doc.get(Document_.createTime), document.getCreateTime() );
//			}
//		}else if("updateTime".equalsIgnoreCase( orderFieldName )){
//			if( "desc".equalsIgnoreCase( order )){
//				return cb.lessThan( root_doc.get(Document_.updateTime), document.getUpdateTime() );
//			}else if( "asc".equalsIgnoreCase( order ) ){
//				return cb.greaterThan( root_doc.get(Document_.updateTime), document.getUpdateTime() );
//			}else if( "equal".equalsIgnoreCase( order ) ){
//				return cb.equal( root_doc.get(Document_.updateTime), document.getUpdateTime() );
//			}
//		}else if("creatorPerson".equalsIgnoreCase( orderFieldName )){
//			if( "desc".equalsIgnoreCase( order )){
//				return cb.lessThan( root_doc.get(Document_.creatorPerson), document.getCreatorPerson() );
//			}else if( "asc".equalsIgnoreCase( order ) ){
//				return cb.greaterThan( root_doc.get(Document_.creatorPerson), document.getCreatorPerson() );
//			}else if( "equal".equalsIgnoreCase( order ) ){
//				return cb.equal( root_doc.get(Document_.creatorPerson), document.getCreatorPerson() );
//			}
//		}else if("creatorUnitName".equalsIgnoreCase( orderFieldName )){
//			if( "desc".equalsIgnoreCase( order )){
//				return cb.lessThan( root_doc.get(Document_.creatorUnitName), document.getCreatorUnitName() );
//			}else if( "asc".equalsIgnoreCase( order ) ){
//				return cb.greaterThan( root_doc.get(Document_.creatorUnitName), document.getCreatorUnitName() );
//			}else if( "equal".equalsIgnoreCase( order ) ){
//				return cb.equal( root_doc.get(Document_.creatorUnitName), document.getCreatorUnitName() );
//			}
//		}else if("creatorTopUnitName".equalsIgnoreCase( orderFieldName )){
//			if( "desc".equalsIgnoreCase( order )){
//				return cb.lessThan( root_doc.get(Document_.creatorTopUnitName), document.getCreatorTopUnitName() );
//			}else if( "asc".equalsIgnoreCase( order ) ){
//				return cb.greaterThan( root_doc.get(Document_.creatorTopUnitName), document.getCreatorTopUnitName() );
//			}else if( "equal".equalsIgnoreCase( order ) ){
//				return cb.equal( root_doc.get(Document_.creatorTopUnitName), document.getCreatorTopUnitName() );
//			}
//		}
//		return null;
//	}
}
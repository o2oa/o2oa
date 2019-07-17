package com.x.cms.assemble.control.service;

import java.util.Date;
import java.util.List;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.config.StorageMapping;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.tools.ListTools;
import com.x.cms.assemble.control.Business;
import com.x.cms.assemble.control.DocumentDataHelper;
import com.x.cms.assemble.control.ThisApplication;
import com.x.cms.core.entity.Document;
import com.x.cms.core.entity.DocumentCommentInfo;
import com.x.cms.core.entity.FileInfo;
import com.x.cms.core.entity.Log;
import com.x.cms.core.entity.tools.filter.QueryFilter;
import com.x.query.core.entity.Item;

public class DocumentInfoService {

	public String getSequence(EntityManagerContainer emc, String id) throws Exception {
		if( StringUtils.isEmpty( id ) ){
			return null;
		}
		Business business = new Business( emc );
		return business.getDocumentFactory().getSequence( id );
	}
	
	/**
	 * 根据指定分类里的文档ID，支持按指定列排序
	 * @param emc
	 * @param categoryId
	 * @param orderField
	 * @param orderType
	 * @param maxCount
	 * @return
	 * @throws Exception
	 */
	public List<String> listByCategoryId( EntityManagerContainer emc, String categoryId, String orderField, String orderType, Integer maxCount ) throws Exception {
		if( StringUtils.isEmpty( categoryId ) ){
			return null;
		}
		Business business = new Business( emc );
		return business.getDocumentFactory().listByCategoryId( categoryId, orderField, orderType, maxCount );
	}
	
	public List<String> listByAppId( EntityManagerContainer emc, String appId, String documentType, Integer maxCount ) throws Exception {
		if( StringUtils.isEmpty( appId ) ){
			return null;
		}
		if( StringUtils.isEmpty( documentType ) ){
			documentType = "全部";
		}
		Business business = new Business( emc );
		return business.getDocumentFactory().listByAppId( appId, documentType, maxCount );
	}
	
	public List<String> listByCategoryId( EntityManagerContainer emc, String categoryId ) throws Exception {
		if( StringUtils.isEmpty( categoryId ) ){
			return null;
		}
		Business business = new Business( emc );
		return business.getDocumentFactory().listByCategoryId( categoryId, 10000 );
	}

	public Document get(EntityManagerContainer emc, String id) throws Exception {
		if( StringUtils.isEmpty( id ) ){
			return null;
		}
		Business business = new Business( emc );
		return business.getDocumentFactory().get( id );
	}

	public Long countByCategoryId(EntityManagerContainer emc, String categoryId) throws Exception {
		if( StringUtils.isEmpty( categoryId ) ){
			return null;
		}
		Business business = new Business( emc );
		return business.getDocumentFactory().countByCategoryId(categoryId);
	}
	
	public Long countByAppId(EntityManagerContainer emc, String appId) throws Exception {
		if( StringUtils.isEmpty( appId ) ){
			return null;
		}
		Business business = new Business( emc );
		return business.getDocumentFactory().countByAppId(appId);
	}

	/**
	 * 从Document表里，忽略权限根据条件查询文档数量
	 * @param emc
	 * @param queryFilter
	 * @return
	 * @throws Exception
	 */
	public Long countWithConditionOutofPermission(EntityManagerContainer emc, QueryFilter queryFilter) throws Exception {
		if( queryFilter == null ){
			return null;
		}
		Business business = new Business( emc );
		return business.getDocumentFactory().countWithConditionOutofPermission( queryFilter );
	}
	
	public Document save( EntityManagerContainer emc, Document wrapIn ) throws Exception {
		if( wrapIn == null ){
			throw new Exception("wrapIn document is null!");
		}
		Document document = null;
		Log log = null;
		document = emc.find( wrapIn.getId(), Document.class );
		emc.beginTransaction( Document.class );
		emc.beginTransaction( Log.class );
		
		if( wrapIn.getTitle() != null &&  wrapIn.getTitle().length() > 70 ) {
			wrapIn.setTitle( wrapIn.getTitle().substring(0, 70) );
		}
		if( StringUtils.isEmpty( wrapIn.getAppAlias() )) {
			wrapIn.setAppAlias( wrapIn.getAppName() );
		}
		if( document == null ){
			document = new Document();
			wrapIn.copyTo( document );
			if( StringUtils.isEmpty( document.getId() )) {
				document.setId( Document.createId() );
			}
			emc.persist( document, CheckPersistType.all );
			log = getOperationLog( document.getCreatorPerson(), "用户[" + document.getCreatorPerson() + "]成功创建一个文档信息", document.getAppId(), document.getCategoryId(), document.getId(), "", "DOCUMENT", "新增" );
			emc.persist( log, CheckPersistType.all );
		}else{
			//有一些数据不需要更新
			Date createTime = null;
			String createPerson = null;
			String creatorIdentity = null;
			String creatorUnitName = null;
			String creatorTopUnitName = null;
			Date publishTime = null;
			Long viewCount = wrapIn.getViewCount();
			//先把原来的值 记录下来
			if( wrapIn.getViewCount() == null ||  wrapIn.getViewCount() < document.getViewCount() ) {
				viewCount = document.getViewCount();
			}
			if( document.getCreateTime() != null ){
				createTime = document.getCreateTime();
			}
			if( document.getCreatorPerson() != null ){
				createPerson = document.getCreatorPerson();
			}
			if( document.getCreatorIdentity() != null ){
				creatorIdentity = document.getCreatorIdentity();
			}
			if( document.getCreatorUnitName() != null ){
				creatorUnitName = document.getCreatorUnitName();
			}
			if( document.getCreatorTopUnitName() != null ){
				creatorTopUnitName = document.getCreatorTopUnitName();
			}
			if( document.getPublishTime() != null ){
				publishTime = document.getPublishTime();	
			}
			
			wrapIn.copyTo( document, JpaObject.FieldsUnmodify );
			document.setViewCount( viewCount );
			if( createTime != null ){
				document.setCreateTime(createTime);
			}
			if( createPerson != null ){
				document.setCreatorPerson(createPerson);
			}
			if( creatorIdentity != null ){
				document.setCreatorIdentity(creatorIdentity);
			}
			if( creatorUnitName != null ){
				document.setCreatorUnitName(creatorUnitName);
			}
			if( creatorTopUnitName != null ){
				document.setCreatorTopUnitName(creatorTopUnitName);
			}
			if( publishTime != null ){
				document.setPublishTime(publishTime);
			}
			document.setReviewed( false );
			emc.check( document, CheckPersistType.all );
			
			log = getOperationLog( document.getCreatorPerson(), "用户[" + document.getCreatorPerson() + "]成功更新一个文档信息", document.getAppId(), document.getCategoryId(), document.getId(), "", "DOCUMENT", "更新" );
			emc.persist( log, CheckPersistType.all );
		}
		emc.commit();
		return document;		
	}
	
	public Log getOperationLog( String person, String description, String appId, String categoryId, String documentId, String fileId, String operationLevel, String operationType ) throws Exception {
		String operatorUid = person;
		Log log = new Log();
		log.setAppId(appId);
		log.setCategoryId(categoryId);
		log.setDescription(description);
		log.setDocumentId(documentId);
		log.setFileId(fileId);
		log.setOperatorName(operatorUid);
		log.setOperatorUid(operatorUid);
		log.setOperationType(operationType);
		log.setOperationLevel(operationLevel);
		return log;
	}
	
	/**
	 * 对Document信息进行分页查询（忽略权限）
	 * document和Review除了sequence还有5个排序列支持title, appAlias, categoryAlias, creatorPerson, creatorUnitName的分页查询
		除了sequence和title, appAlias, categoryAlias, categoryName, creatorUnitName之外，其他的列排序全部在内存进行分页
	 * @param emc
	 * @param pageSize
	 * @param id
	 * @param orderField
	 * @param orderType
	 * @param queryFilter
	 * @return
	 * @throws Exception
	 */
	public List<Document> listNextWithCondition( EntityManagerContainer emc, Integer pageSize, String id, String orderField, String orderType, QueryFilter queryFilter ) throws Exception {
		if( pageSize == 0 ) { pageSize = 20; }
		if( StringUtils.isEmpty( orderType ) ) {  orderType = "desc"; }
		
		//判断排序列情况
		if( StringUtils.isEmpty( orderField ) ) { 
			orderField = Document.sequence_FIELDNAME;
		}
		if( Document.sequence_FIELDNAME.equalsIgnoreCase( orderField )) {
			orderField = "sequence";
		}
		
		orderField = Document.getSequnceFieldNameWithProperty( orderField );
			
		Business business = new Business(emc);
		Document document = null;
		String sequenceFieldValue = null;
		Object obj = null;
		
		if( StringUtils.isNotEmpty( id ) && !StringUtils.equalsIgnoreCase( id, StandardJaxrsAction.EMPTY_SYMBOL ) ) {
			document = emc.find( id, Document.class );
			if( document != null ){//查询出ID对应的记录的sequence
				obj = PropertyUtils.getProperty( document, orderField );
				if( obj != null ) {
					sequenceFieldValue = obj.toString();
				}
			}
		}
		return business.getDocumentFactory().listNextWithCondition( pageSize, sequenceFieldValue, orderField, orderType, queryFilter );
	}
	
	public List<Document> listNextWithCondition(EntityManagerContainer emc, String orderField, String orderType, QueryFilter queryFilter, int maxCount) throws Exception {
		if( maxCount == 0 ) { maxCount = 20; }
		Business business = new Business(emc);
		return business.getDocumentFactory().listNextWithCondition( orderField, orderType, queryFilter, maxCount );
	}	
	
	public List<Document> listMyDraft(EntityManagerContainer emc, String name, List<String> categoryIdList, String documentType) throws Exception {
		Business business = new Business(emc);	
		return business.getDocumentFactory().listMyDraft( name, categoryIdList, documentType );
	}
	
	public Item getDataWithDocIdWithPath(EntityManagerContainer emc, Document document, String path0, String path1, String path2, String path3, String path4, String path5, String path6, String path7) throws Exception {
		Business business = new Business(emc);	
		return business.itemFactory().getWithDocmentWithPath( document.getId(), path0, path1, path2, path3, path4, path5, path6, path7);
	}

	public List<String> listReviewedIdsByCategoryId(EntityManagerContainer emc, String categoryId, int maxCount) throws Exception {
		Business business = new Business(emc);	
		return business.getDocumentFactory().listReviewedIdsByCategoryId( categoryId, maxCount);
	}

	public Boolean inReview(EntityManagerContainer emc, String documentId) throws Exception {
		Document document = emc.find( documentId, Document.class );
		emc.beginTransaction( Document.class );
		document.setReviewed( false );
		emc.persist( document, CheckPersistType.all );
		emc.commit();
		return true;
	}

	public List<String> listUnReviewIds(EntityManagerContainer emc, Integer maxCount) throws Exception {
		Business business = new Business(emc);	
		return business.getDocumentFactory().listUnReviewIds( maxCount);
	}

	/**
	 * 删除指定ID的文档
	 * @param emc
	 * @param docId
	 * @throws Exception
	 */
	public void delete( EntityManagerContainer emc, String docId ) throws Exception {
		Business business = new Business(emc);
		emc.beginTransaction( Document.class );
		emc.beginTransaction( Item.class );
		emc.beginTransaction( FileInfo.class );
		emc.beginTransaction( DocumentCommentInfo.class );
		
		List<String> allFileInfoIds = null;		
		Document document = emc.find( docId, Document.class );										
		
		//删除与该文档有关的所有数据信息
		DocumentDataHelper documentDataHelper = new DocumentDataHelper( emc, document );
		documentDataHelper.remove();
		
		allFileInfoIds = business.getFileInfoFactory().listAllByDocument( docId );
		if( ListTools.isNotEmpty( allFileInfoIds ) ){
			StorageMapping mapping = null;
			FileInfo fileInfo = null;
			for( String fileInfoId : allFileInfoIds ){
				fileInfo = emc.find( fileInfoId, FileInfo.class );
				if( fileInfo != null ){
					if( "ATTACHMENT".equals( fileInfo.getFileType() )){
						mapping = ThisApplication.context().storageMappings().get( FileInfo.class, fileInfo.getStorage() );
						fileInfo.deleteContent(mapping);
					}
				}
				emc.remove( fileInfo, CheckRemoveType.all );
			}
		}
		
		List<String>  commentIds = business.documentCommentInfoFactory().listWithDocument( docId );
		if( ListTools.isNotEmpty( commentIds )) {
			DocumentCommentInfo documentCommentInfo = null;
			for( String commentId : commentIds ) {
				documentCommentInfo = emc.find( commentId, DocumentCommentInfo.class );
				emc.remove( documentCommentInfo, CheckRemoveType.all );
			}
		}		
		if( document != null ) {
			emc.remove( document, CheckRemoveType.all );
		}		
		emc.commit();
		
		//压入队列，检查热点图片是否仍存在，如果存在则删除
		ThisApplication.queueDocumentDelete.send( document.getId() );
	}

	/**
	 * 查询指定文档按path0排序的Item列表
	 * @param emc
	 * @param docIds
	 * @param path0Name
	 * @param orderField
	 * @param orderType
	 * @return
	 * @throws Exception
	 */
	public List<Item> listSortObjWithOrderFieldInData(EntityManagerContainer emc, List<String> docIds, String path0Name, String fieldType, String orderType ) throws Exception {
		Business business = new Business(emc);	
		return business.itemFactory().listSortObjWithOrderFieldInData( docIds, path0Name, fieldType, orderType );
	}
}

package com.x.cms.assemble.control.service;

import java.util.Date;
import java.util.List;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.cms.assemble.control.Business;
import com.x.cms.core.entity.Document;
import com.x.cms.core.entity.Log;
import com.x.query.core.entity.Item;

public class DocumentInfoService {

	public List<Document> list( EntityManagerContainer emc, List<String> ids ) throws Exception {
		if( ids == null || ids.isEmpty() ){
			return null;
		}
		Business business = new Business( emc );
		return business.getDocumentFactory().list( ids );
	}
	
	public List<String> listByCategoryId( EntityManagerContainer emc, String categoryId ) throws Exception {
		if( categoryId == null || categoryId.isEmpty() ){
			return null;
		}
		Business business = new Business( emc );
		return business.getDocumentFactory().listByCategoryId( categoryId );
	}

	public Document get(EntityManagerContainer emc, String id) throws Exception {
		if( id == null || id.isEmpty() ){
			return null;
		}
		Business business = new Business( emc );
		return business.getDocumentFactory().get( id );
	}

	public Long countByCategoryId(EntityManagerContainer emc, String categoryId) throws Exception {
		if( categoryId == null || categoryId.isEmpty() ){
			return null;
		}
		Business business = new Business( emc );
		return business.getDocumentFactory().countByCategoryId(categoryId);
	}

	public Document save( EntityManagerContainer emc, Document wrapIn ) throws Exception {
		if( wrapIn == null ){
			throw new Exception("wrapIn document is null!");
		}
		Document document = null;
		Log log = null;
		document = emc.find( wrapIn.getId(), Document.class );
		emc.beginTransaction( Document.class );
		emc.beginTransaction(Log.class);
		if( document == null ){
			document = new Document();
			wrapIn.copyTo( document );
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
			//先把原来的值 记录下来
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

	public Long countWithDocIds(EntityManagerContainer emc, List<String> viewAbleDocIds) throws Exception {
		Business business = new Business(emc);
		return business.getDocumentFactory().countWithDocIds( viewAbleDocIds );
	}
	
	public Long countWithCondition( EntityManagerContainer emc, 
			List<String> viewAbleCategoryIds, String title, List<String> publisherList, List<String> createDateList,  
			List<String> publishDateList,  List<String> statusList, String documentType, List<String> importBatchNames, List<String> personNames, List<String> unitNames, 
			List<String> groupNames,  List<String> extensionStringList, List<String> extensionDoubleList, List<String> extensionBooleanList, Boolean manager ) throws Exception {
		Business business = new Business(emc);	
		return business.getDocumentFactory().countWithCondition( viewAbleCategoryIds, title, publisherList, createDateList, 
				publishDateList, statusList, documentType, importBatchNames, personNames, unitNames, groupNames, extensionStringList, 
				extensionDoubleList, extensionBooleanList, manager );
	}
	
	public List<Document> listNextWithCondition( EntityManagerContainer emc, 
			String id, Integer maxCount, List<String> viewAbleCategoryIds, String title, List<String> publisherList, List<String> createDateList,  
			List<String> publishDateList,  List<String> statusList, String documentType, List<String> importBatchNames, List<String> personNames, List<String> unitNames, 
			List<String> groupNames, String orderField, String order, List<String> extensionStringList, List<String> extensionDoubleList, List<String> extensionBooleanList, Boolean manager ) throws Exception {
		Business business = new Business(emc);
		Document document = null;
		Object sequenceFieldValue = null;
		//查询出ID对应的记录的sequence
		if( id != null && !"(0)".equals(id) && !id.isEmpty() ){
			if ( !StringUtils.equalsIgnoreCase( id, StandardJaxrsAction.EMPTY_SYMBOL ) ) {
				document = emc.find( id, Document.class );
				if( document != null ){
					sequenceFieldValue = PropertyUtils.getProperty( document, orderField );
				}
			}
		}		
		return business.getDocumentFactory().listNextWithCondition( maxCount, viewAbleCategoryIds, title, publisherList, createDateList, publishDateList, 
				statusList, documentType, importBatchNames, personNames, unitNames, groupNames, sequenceFieldValue, orderField, order,  extensionStringList, extensionDoubleList, extensionBooleanList, manager );
	}

	public List<Document> listMyDraft(EntityManagerContainer emc, String name, List<String> categoryIdList, String documentType) throws Exception {
		Business business = new Business(emc);	
		return business.getDocumentFactory().listMyDraft( name, categoryIdList, documentType );
	}
	
	public Item getDataWithDocIdWithPath(EntityManagerContainer emc, Document document, String path0, String path1, String path2, String path3, String path4, String path5, String path6, String path7) throws Exception {
		Business business = new Business(emc);	
		return business.itemFactory().getWithDocmentWithPath( document.getId(), path0, path1, path2, path3, path4, path5, path6, path7);
	}

	public List<String> lisViewableDocIdsWithFilter(EntityManagerContainer emc, List<String> appIdList, 
			List<String> appAliasList, List<String> categoryIdList, List<String> categoryAliasList, 
			List<String> publisherList, String title, List<String> createDateList,
			List<String> publishDateList, List<String> statusList, String documentType, Integer maxResultCount) throws Exception {
		Business business = new Business(emc);	
		return business.getDocumentFactory().lisViewableDocIdsWithFilter( appIdList, appAliasList,
				 categoryIdList, categoryAliasList , publisherList,  title, createDateList,
				 publishDateList, statusList, documentType, maxResultCount);
	}

}

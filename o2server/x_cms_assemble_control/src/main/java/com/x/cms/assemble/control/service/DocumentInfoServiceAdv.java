package com.x.cms.assemble.control.service;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.dataitem.DataItemConverter;
import com.x.base.core.entity.dataitem.ItemCategory;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.tools.ListTools;
import com.x.cms.assemble.control.Business;
import com.x.cms.assemble.control.DocumentDataHelper;
import com.x.cms.assemble.control.jaxrs.document.ActionBatchModifyDataWithIds.WiDataChange;
import com.x.cms.assemble.control.jaxrs.document.ActionBatchModifyDataWithIds.Wo;
import com.x.cms.assemble.control.jaxrs.permission.element.PermissionInfo;
import com.x.cms.core.entity.CategoryInfo;
import com.x.cms.core.entity.Document;
import com.x.cms.core.entity.content.Data;
import com.x.query.core.entity.Item;

/**
 * 对文档信息进行管理的服务类（高级）
 * 高级服务器可以利用Service完成事务控制
 * 
 * @author O2LEE
 */
public class DocumentInfoServiceAdv {
	private DocumentInfoService documentInfoService = new DocumentInfoService();
	private PermissionOperateService permissionService = new PermissionOperateService();
	
	public List<Document> listByCategoryId( String categoryId ) throws Exception {
		if( categoryId == null || categoryId.isEmpty() ){
			throw new Exception("categoryId is null!");
		}
		List<String> ids = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			ids = documentInfoService.listByCategoryId( emc, categoryId );
			return emc.list( Document.class,  ids );
//			return documentInfoService.list( emc, ids );
		} catch ( Exception e ) {
			throw e;
		}
	}
	
	public List<String> listIdsByCategoryId( String categoryId ) throws Exception {
		if( categoryId == null || categoryId.isEmpty() ){
			throw new Exception("categoryId is null!");
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			return documentInfoService.listByCategoryId( emc, categoryId );
		} catch ( Exception e ) {
			throw e;
		}
	}
	
	public List<String> listIdsByCategoryId( String categoryId, Integer maxCount ) throws Exception {
		if( categoryId == null || categoryId.isEmpty() ){
			throw new Exception("categoryId is null!");
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			return documentInfoService.listByCategoryId( emc, categoryId, maxCount );
		} catch ( Exception e ) {
			throw e;
		}
	}
	
	public List<String> listIdsByAppId( String appId, String documentType, Integer maxCount ) throws Exception {
		if( appId == null || appId.isEmpty() ){
			throw new Exception("categoryId is null!");
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			return documentInfoService.listByAppId( emc, appId, documentType, maxCount );
		} catch ( Exception e ) {
			throw e;
		}
	}

	public Document get( String id ) throws Exception {
		if( id == null || id.isEmpty() ){
			throw new Exception("id is null!");
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			return documentInfoService.get( emc, id );
		} catch ( Exception e ) {
			throw e;
		}
	}

	public Document view( String id, EffectivePerson currentPerson ) throws Exception {
		if( id == null || id.isEmpty() ){
			throw new Exception("id is null!");
		}
		Document document = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			document = emc.find( id, Document.class );
			return document;
		} catch ( Exception e ) {
			throw e;
		}
	}

	public Data getDocumentData( Document document ) throws Exception {
		if( document == null ){
			throw new Exception("document is null!");
		}
		List<Item> dataItems = null;
		Business business = null;
		Gson gson = null;
		JsonElement jsonElement = null;
		DataItemConverter<Item> converter = null;
		
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			business = new Business( emc );
			dataItems = business.itemFactory().listWithDocmentWithPath( document.getId() );
			
			if ( dataItems == null || dataItems.isEmpty() ) {
				return new Data();
			} else {
				converter = new DataItemConverter<>( Item.class );
				jsonElement = converter.assemble( dataItems );
				if ( jsonElement != null && jsonElement.isJsonObject() ) {
					gson = XGsonBuilder.instance();
					return gson.fromJson( jsonElement, Data.class );
				} else {
					return new Data();
				}
			}
		} catch ( Exception e ) {
			System.out.println("系统在根据文档查询文档数据时发生异常，ID:" + document.getId() );
			throw e;
		}
	}
	
	public Long countByCategoryId(String categoryId ) throws Exception {
		if( categoryId == null || categoryId.isEmpty() ){
			throw new Exception("categoryId is null!");
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			return documentInfoService.countByCategoryId( emc, categoryId );
		} catch ( Exception e ) {
			throw e;
		}
	}
	
	public Long countByAppId(String appId ) throws Exception {
		if( appId == null || appId.isEmpty() ){
			throw new Exception("appId is null!");
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			return documentInfoService.countByAppId( emc, appId );
		} catch ( Exception e ) {
			throw e;
		}
	}

	public List<Document> list(List<String> ids) throws Exception {
		if( ListTools.isEmpty( ids ) ){
			throw new Exception("ids is empty!");
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			return emc.list( Document.class,  ids );
//			return documentInfoService.list(emc, ids);
		} catch ( Exception e ) {
			throw e;
		}
	}
	
	public Document save( Document document, JsonElement jsonElement ) throws Exception {
		if( document == null ){
			throw new Exception("document is null!");
		}
		
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			document =  documentInfoService.save( emc, document );
			//如果有数据信息，则保存数据信息
			
			DocumentDataHelper documentDataHelper = new DocumentDataHelper( emc, document );
			if( jsonElement != null ) {
				documentDataHelper.update(jsonElement);
			}
			emc.commit();

			Data data = documentDataHelper.get();
			data.setDocument( document );
			documentDataHelper.update(data);
			
			emc.commit();
			return document;
		} catch ( Exception e ) {
			throw e;
		}
	}
	
	public Document refreshDocInfoData( Document document ) throws Exception {
		if( document == null ){
			throw new Exception("document is null!");
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			document =  documentInfoService.save( emc, document );
			//如果有数据信息，则保存数据信息
			DocumentDataHelper documentDataHelper = new DocumentDataHelper( emc, document );
			Data data = documentDataHelper.get();
			data.setDocument( document );
			documentDataHelper.update(data);
			emc.commit();
			return document;
		} catch ( Exception e ) {
			throw e;
		}
	}

	public Long countWithCondition( List<String> viewAbleCategoryIds, String title, List<String> publisherList, 
			List<String> createDateList,  List<String> publishDateList,  List<String> statusList, String documentType, 
			List<String>  creatorUnitNameList,
			List<String> importBatchNames, List<String> personNames, 
			List<String> unitNames, List<String> groupNames, Boolean manager, Date lastedPublishTime ) throws Exception {
		if( ListTools.isEmpty( viewAbleCategoryIds ) && !manager ){
			return 0L;
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			return documentInfoService.countWithCondition( emc, viewAbleCategoryIds, title, publisherList, createDateList, publishDateList, 
					statusList, documentType, creatorUnitNameList, importBatchNames, personNames, unitNames, groupNames, manager, lastedPublishTime );
		} catch ( Exception e ) {
			throw e;
		}
	}
	
	public List<Document> listNextWithCondition(String id, Integer count, List<String> viewAbleCategoryIds, String title, List<String> publisherList, 
			List<String> createDateList,  List<String> publishDateList,  List<String> statusList, String documentType, 
			List<String>  creatorUnitNameList, List<String> importBatchNames, List<String> personNames, 
			List<String> unitNames, List<String> groupNames,  String orderField, String order, Boolean manager, Date lastedPublishTime ) throws Exception {
		if( ListTools.isEmpty( viewAbleCategoryIds ) && !manager ){
			return null;
		}
		if( orderField == null || orderField.isEmpty() ){
			orderField = "publishTime";
		}
		if( order == null || order.isEmpty() ){
			order = "DESC";
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			return documentInfoService.listNextWithCondition( emc, id, count, viewAbleCategoryIds, title, publisherList, createDateList, publishDateList, 
					statusList, documentType, creatorUnitNameList, importBatchNames, personNames, unitNames,  groupNames, orderField, order, manager, lastedPublishTime );
		} catch ( Exception e ) {
			throw e;
		}
	}

	public List<Document> listMyDraft( String name, List<String> categoryIdList, String documentType ) throws Exception {
		if( name == null || name.isEmpty()){
			throw new Exception("name is null!");
		}
//		if( categoryIdList == null || categoryIdList.isEmpty() ){
//			throw new Exception("categoryIdList is null!");
//		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			return documentInfoService.listMyDraft( emc, name, categoryIdList, documentType );
		} catch ( Exception e ) {
			throw e;
		}
	}

	public Item getDataWithDocIdWithPath(Document document, String path0 ) throws Exception {
		if( path0 == null || path0.isEmpty()){
			throw new Exception("path0 is null!");
		}
		if( document == null ){
			throw new Exception("document is null!");
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			return documentInfoService.getDataWithDocIdWithPath(emc, document, path0, null, null, null, null, null, null, null );
		} catch ( Exception e ) {
			throw e;
		}
	}

	/**
	 * 普通用戶的查詢，帶權限查詢
	 * @param title
	 * @param appIdList
	 * @param categoryIdList
	 * @param publisherList
	 * @param createDateList
	 * @param publishDateList
	 * @param statusList
	 * @param personName
	 * @param viewableCategoryIds
	 * @param maxResultCount
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<String> lisViewableDocIdsWithFilter( String title, List<String> appIdList, List<String> categoryIdList,
			List<String> publisherList, List<String> createDateList, List<String> publishDateList,
			List<String> statusList, String personName, List<String> unitNames, List<String> groupNames,
			Boolean isManager, List<String> viewableCategoryIds, String documentType, Integer maxResultCount ) throws Exception {
		Business business = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			business = new Business( emc );
			if( isManager ) {
				return business.getDocumentFactory().lisViewableDocIdsWithFilter( appIdList, categoryIdList, publisherList, title, createDateList, publishDateList, statusList, 
						null, null, null, viewableCategoryIds, documentType, maxResultCount
				);
			}else {
				return business.getDocumentFactory().lisViewableDocIdsWithFilter( appIdList, categoryIdList, publisherList, title, createDateList, publishDateList, statusList, 
						personName, unitNames, groupNames, viewableCategoryIds, documentType, maxResultCount
				);
			}
		} catch ( Exception e ) {
			throw e;
		}
	}
	
	/**
	 * 管理員的查詢，跨越權限
	 * @param title
	 * @param appIdList
	 * @param appAliasList
	 * @param categoryIdList
	 * @param categoryAliasList
	 * @param publisherList
	 * @param createDateList
	 * @param publishDateList
	 * @param statusList
	 * @param maxResultCount
	 * @return
	 * @throws Exception
	 */
	public List<String> lisViewableDocIdsWithFilterWithoutPermission( String title, List<String> appIdList, List<String> appAliasList,
			List<String> categoryIdList, List<String> categoryAliasList, List<String> publisherList, List<String> createDateList,
			List<String> publishDateList,  List<String> statusList, String documentType, Integer maxResultCount ) throws Exception {
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			return documentInfoService.lisViewableDocIdsWithFilter(emc, appIdList, appAliasList, categoryIdList, categoryAliasList, 
					publisherList, title, createDateList, publishDateList, statusList, documentType, maxResultCount );
		} catch ( Exception e ) {
			throw e;
		}
	}

	void fill(Item o, Document document) {
		/** 将DateItem与Document放在同一个分区 */
		o.setDistributeFactor(document.getDistributeFactor());
		o.setBundle(document.getId());
		o.setItemCategory(ItemCategory.cms);
	}
	
	public Long getViewCount( String id ) throws Exception {
		if( id == null || id.isEmpty() ){
			throw new Exception("id is null!");
		}
		Business business = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			business = new Business( emc );
			return business.documentViewRecordFactory().countWithDocmentId( id );
		} catch ( Exception e ) {
			throw e;
		}
	}

	/**
	 * 根据组织好的document对象更新数据库中文档的权限信息
	 * @param document
	 * @throws Exception
	 */
	public void updateAllPermission(Document document) throws Exception {
		if( document == null ){
			throw new Exception("document is null!");
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			Document document_entity = emc.find( document.getId(), Document.class );
			if( document_entity != null ) {
				emc.beginTransaction( Document.class );
				document_entity.setAuthorPersonList( document.getAuthorPersonList() );
				document_entity.setAuthorUnitList( document.getAuthorUnitList() );
				document_entity.setAuthorGroupList( document.getAuthorGroupList() );
				document_entity.setReadPersonList( document.getReadPersonList() );
				document_entity.setReadUnitList( document.getReadUnitList() );
				document_entity.setReadGroupList( document.getReadGroupList() );
				document_entity.setManagerList( document.getManagerList() );
				if( StringUtils.isEmpty( document_entity.getDocumentType() )) {
					document_entity.setDocumentType( "信息" );
				}
				emc.check( document_entity, CheckPersistType.all );
				emc.commit();
			}
		} catch ( Exception e ) {
			throw e;
		}
	}

	/**
	 * 根据读者作者列表更新文档所有的权限信息
	 * @param docId
	 * @param readerList
	 * @param authorList
	 * @throws Exception
	 */
	public void refreshDocumentPermission( String docId, List<PermissionInfo> readerList, List<PermissionInfo> authorList ) throws Exception {
		if( StringUtils.isEmpty( docId ) ){
			throw new Exception("docId is empty!");
		}
		permissionService.refreshDocumentPermission( docId, readerList, authorList);
	}

	/**
	 * 根据组织好的权限信息列表更新指定文档的权限信息
	 * @param docId
	 * @param permissionList
	 * @throws Exception
	 */
	public void refreshDocumentPermission(String docId, List<PermissionInfo> permissionList) throws Exception {
		if( StringUtils.isEmpty( docId ) ){
			throw new Exception("docId is empty!");
		}
		permissionService.refreshDocumentPermission(docId, permissionList);
	}

	/**
	 * 变更一个文档的分类信息
	 * @param document
	 * @param categoryInfo
	 * @throws Exception
	 */
	public Boolean changeCategory( Document document, CategoryInfo categoryInfo) throws Exception {
		if( document == null ){
			throw new Exception("document is empty!");
		}		
		if( categoryInfo == null ){
			throw new Exception("categoryInfo is empty!");
		}
		Data data = null;
		DocumentDataHelper documentDataHelper = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			emc.beginTransaction( Document.class );
			emc.beginTransaction(  Item.class );
			
			document = emc.find( document.getId(), Document.class );
			
			//更新document分类相关信息
			document.setAppId( categoryInfo.getAppId() );
			document.setAppName( categoryInfo.getAppName() );
			document.setCategoryId( categoryInfo.getId() );
			document.setCategoryName( categoryInfo.getCategoryName() );
			document.setCategoryAlias( categoryInfo.getCategoryAlias() );
			document.setForm( categoryInfo.getReadFormId() );
			document.setFormName( categoryInfo.getReadFormName() );
			emc.check( document, CheckPersistType.all );
			
			//更新数据里的document对象信息
			documentDataHelper = new DocumentDataHelper( emc, document );
			data = documentDataHelper.get();
			data.setDocument( document );
			documentDataHelper.update( data );

			emc.commit();
			return true;
		} catch ( Exception e ) {
			throw e;
		}
	}
	
	public Wo changeData(List<String> docIds, List<WiDataChange> dataChanges) throws Exception {
		if( ListTools.isEmpty( docIds )){
			throw new Exception("docIds is empty!");
		}
		if( ListTools.isEmpty( dataChanges )){
			throw new Exception("dataChanges is empty!");
		}
		Wo wo = new Wo();
		Data data = null;
		Document document_entity = null;
		DocumentDataHelper documentDataHelper = null;
		for( String docId : docIds ) {
			try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
				document_entity = emc.find( docId, Document.class );
				documentDataHelper = new DocumentDataHelper( emc, document_entity );
				data = documentDataHelper.get();
				for( WiDataChange dataChange : dataChanges ) {
					if( "Integer".equals(dataChange.getDataType() )) {
						data.put( dataChange.getDataPath(), dataChange.getDataInteger());
					}else if( "String".equals(dataChange.getDataType() )) {
						data.put( dataChange.getDataPath(), dataChange.getDataString());
					}else if( "Date".equals(dataChange.getDataType() )) {
						data.put( dataChange.getDataPath(), dataChange.getDataDate());
					}else if( "Boolean".equals(dataChange.getDataType() )) {
						data.put( dataChange.getDataPath(), dataChange.getDataBoolean());
					}else {
						data.put( dataChange.getDataPath(), dataChange.getDataString());
					}
				}
				data.setDocument( document_entity );
				documentDataHelper.update( data );
				emc.commit();
				wo.increaseSuccess_count(1);
			} catch ( Exception e ) {
				wo.appendErorrId(docId);
				wo.increaseError_count(1);
				e.printStackTrace();
			}
		}
		return wo;
	}
	
	public List<String> listReviewedIdsByCategoryId( String categoryId, int maxCount ) throws Exception {
		if( StringUtils.isEmpty( categoryId ) ){
			throw new Exception("categoryId is empty!");
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			return documentInfoService.listReviewedIdsByCategoryId( emc, categoryId, maxCount );
		} catch ( Exception e ) {
			throw e;
		}
	}

	public Boolean inReview(String documentId) throws Exception {
		if( StringUtils.isEmpty( documentId ) ){
			throw new Exception("documentId is empty!");
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			return documentInfoService.inReview( emc, documentId );
		} catch ( Exception e ) {
			throw e;
		}
	}

	public List<String> listInReviewIds( Integer maxCount ) throws Exception {
		if( maxCount == null ){
			maxCount = 1000;
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			return documentInfoService.listInReviewIds( emc, maxCount );
		} catch ( Exception e ) {
			throw e;
		}
	}
	
}

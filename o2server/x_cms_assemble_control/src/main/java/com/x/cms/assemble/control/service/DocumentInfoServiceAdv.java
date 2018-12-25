package com.x.cms.assemble.control.service;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.dataitem.DataItemConverter;
import com.x.base.core.entity.dataitem.ItemCategory;
import com.x.base.core.project.exception.ExceptionWhen;
import com.x.base.core.project.gson.XGsonBuilder;
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
 * 对文档信息进行管理的服务类
 * 
 * @author O2LEE
 */
public class DocumentInfoServiceAdv {
	private DocumentInfoService documentInfoService = new DocumentInfoService();
	private PermissionOperateService permissionService = new PermissionOperateService();
	
	/**
	 * 根据指定的信息分类ID，获取分类下所有的信息文档对象
	 * @param categoryId  信息分类ID
	 * @param maxCount  查询最大条目数量
	 * @return
	 * @throws Exception
	 */
	public List<Document> listByCategoryId( String categoryId, Integer maxCount ) throws Exception {
		if( categoryId == null || categoryId.isEmpty() ){
			throw new Exception("categoryId is null!");
		}
		List<String> ids = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			ids = documentInfoService.listByCategoryId( emc, categoryId, maxCount );
			return emc.list( Document.class,  ids );
		} catch ( Exception e ) {
			throw e;
		}
	}
	
	/**
	 * 根据指定的信息分类ID，获取分类下所有的文档ID列表
	 * @param categoryId  信息分类ID
	 * @param maxCount  查询最大条目数量
	 * @return
	 * @throws Exception
	 */
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

	/**
	 * 根据ID获取指定信息文档对象
	 * @param id  信息文档ID
	 * @return
	 * @throws Exception
	 */
	public Document get( String id ) throws Exception {
		if( id == null || id.isEmpty() ){
			throw new Exception("id is null!");
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			return emc.find( id, Document.class, ExceptionWhen.none );
		} catch ( Exception e ) {
			throw e;
		}
	}

	/**
	 * 根据文档信息对象查询该文档信息的数据对象
	 * @param document  文档信息
	 * @return
	 * @throws Exception
	 */
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
	
	/**
	 * 根据信息分类ID，获取分类下所有文档的数量
	 * @param categoryId  信息分类ID
	 * @return
	 * @throws Exception
	 */
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

	/**
	 * 根据ID列表列示指定ID的文档信息
	 * @param ids  信息文档ID列表
	 * @return
	 * @throws Exception
	 */
	public List<Document> list(List<String> ids) throws Exception {
		if( ListTools.isEmpty( ids ) ){
			throw new Exception("ids is empty!");
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			return emc.list( Document.class,  ids );
		} catch ( Exception e ) {
			throw e;
		}
	}
	
	/**
	 * 保存文档信息
	 * @param document  文档信息对象
	 * @param jsonElement  文档数据对象
	 * @return
	 * @throws Exception
	 */
	public Document save( Document document, JsonElement jsonElement ) throws Exception {
		if( document == null ){
			throw new Exception("document is null!");
		}
		
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			document =  documentInfoService.save( emc, document );
			
			//如果有数据信息, 则保存数据信息			
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
	
	/**
	 * 向文档数据表同步文档的最新数据信息
	 * @param document  文档信息对象（包含数据）
	 * @return 
	 * @throws Exception
	 */
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

	/**
	 * 根据指定的条件获取符合条件的文档信息数量
	 * @param viewAbleCategoryIds   用于过滤的可见信息分类ID列表
	 * @param title   用于过滤的文档标题
	  * @param publisherList  用于过滤的发布者列表
	 * @param createDateList  用于过滤的信息创建日期列表
	 * @param publishDateList  用于过滤的信息发布日期列表
	 * @param statusList  用于过滤的信息文档状态列表
	 * @param documentType  用于过滤的信息类别
	 * @param creatorUnitNameList  用于过滤的创建者所属组织名称列表
	 * @param importBatchNames  用于过滤的导入数据批次名称列表
	 * @param personNames  用于过滤权限的人员姓名列表
	 * @param unitNames  用于过滤权限的人员所属组织名称列表
	 * @param groupNames  用于过滤权限的群组名称列表
	 * @param manager  查询者身份是否为管理员权限
	 * @return
	 * @throws Exception
	 */
	public Long countWithCondition( List<String> viewAbleCategoryIds, String title, List<String> publisherList, 
			List<String> createDateList,  List<String> publishDateList,  List<String> statusList, String documentType, 
			List<String>  creatorUnitNameList, List<String> importBatchNames, List<String> personNames, 
			List<String> unitNames, List<String> groupNames, Boolean manager) throws Exception {
		if( ListTools.isEmpty( viewAbleCategoryIds ) && !manager ){
			return 0L;
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			return documentInfoService.countWithCondition( emc, viewAbleCategoryIds, title, publisherList, createDateList, publishDateList, 
					statusList, documentType, creatorUnitNameList, importBatchNames, personNames, unitNames, groupNames, manager );
		} catch ( Exception e ) {
			throw e;
		}
	}
	
	/**
	 * 根据指定的条件获取符合条件的下一页文档信息列表
	 * @param id  上一页最后一条信息ID，如果为第一页，则传入"(0)"
	 * @param count  一页查询的条目数量
	 * @param viewAbleCategoryIds   用于过滤的可见信息分类ID列表
	 * @param title   用于过滤的文档标题
	 * @param publisherList  用于过滤的发布者列表
	 * @param createDateList  用于过滤的信息创建日期列表
	 * @param publishDateList  用于过滤的信息发布日期列表
	 * @param statusList  用于过滤的信息文档状态列表
	 * @param documentType  用于过滤的信息类别
	 * @param creatorUnitNameList  用于过滤的创建者所属组织名称列表
	 * @param importBatchNames  用于过滤的导入数据批次名称列表
	 * @param personNames  用于过滤权限的人员姓名列表
	 * @param unitNames  用于过滤权限的人员所属组织名称列表
	 * @param groupNames  用于过滤权限的群组名称列表
	 * @param orderField  用于查询结果排序的属性名称
	 * @param order  查询结果的排序方式
	 * @param manager  查询者身份是否为管理员权限
	 * @return
	 * @throws Exception
	 */
	public List<Document> listNextWithCondition(String id, Integer count, List<String> viewAbleCategoryIds, String title, List<String> publisherList, 
			List<String> createDateList,  List<String> publishDateList,  List<String> statusList, String documentType, 
			List<String>  creatorUnitNameList, List<String> importBatchNames, List<String> personNames, 
			List<String> unitNames, List<String> groupNames,  String orderField, String order, Boolean manager) throws Exception {
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
					statusList, documentType, creatorUnitNameList, importBatchNames, personNames, unitNames,  groupNames, orderField, order, manager );
		} catch ( Exception e ) {
			throw e;
		}
	}

	/**
	 * 根据指定的信息分类ID列表以及文档类别，列示指定人员（根据creatorPerson匹配）草稿状态的文档信息列表
	 * @param name  指定人员名称
	 * @param categoryIdList  指定信息分类ID列表
	 * @param documentType  指定信息类别
	 * @return
	 * @throws Exception
	 */
	public List<Document> listMyDraft( String name, List<String> categoryIdList, String documentType ) throws Exception {
		if( name == null || name.isEmpty()){
			throw new Exception("name is null!");
		}
		if( categoryIdList == null || categoryIdList.isEmpty() ){
			throw new Exception("categoryIdList is null!");
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			return documentInfoService.listMyDraft( emc, name, categoryIdList, documentType );
		} catch ( Exception e ) {
			throw e;
		}
	}

	/**
	 * 从文档中获取指定数据根路径为path0的数据
	 * @param document  文档信息对象
	 * @param path0  path0名称
	 * @return
	 * @throws Exception
	 */
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
	 * 普通用戶的查詢（帶權限查詢）
	 * @param title   用于过滤的文档标题
	 * @param appIdList  用于过滤的信息栏目ID列表
	 * @param categoryIdList  用于过滤的信息分类ID列表
	 * @param publisherList   用于过滤的发布者列表
	 * @param createDateList  用于过滤的信息创建日期列表
	 * @param publishDateList  用于过滤的信息发布日期列表
	 * @param statusList  用于过滤的信息文档状态列表
	 * @param personName  用于过滤权限的人员名称
	 * @param viewableCategoryIds   用于过滤的可见信息分类ID列表
	 * @param maxResultCount  查询最大文档数量
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
	 * 管理員的查詢（跨越權限）
	 * @param title   用于过滤的文档标题
	 * @param appIdList  用于过滤的信息栏目ID列表
	 * @param appAliasList  用于过滤的信息栏目别外列表
	 * @param categoryIdList  用于过滤的信息分类ID列表
	 * @param categoryAliasList  用于过滤的信息分类别名列表
	 * @param publisherList  用于过滤的发布者列表
	 * @param createDateList  用于过滤的信息创建日期列表
	 * @param publishDateList  用于过滤的信息发布日期列表
	 * @param statusList  用于过滤的信息文档状态列表
	 * @param maxResultCount  查询最大文档数量
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

	/**
	 * 根据信息文档对象，向信息数据Item对象里补充distributeFactor、bundle和itemCategory信息
	 * @param o  信息数据对象
	 * @param document  信息对象
	 */
	void fill(Item o, Document document) {
		/** 将DateItem与Document放在同一个分区 */
		o.setDistributeFactor(document.getDistributeFactor());
		o.setBundle(document.getId());
		o.setItemCategory(ItemCategory.cms);
	}
	
	/**
	 * 获取信息被阅读次数
	 * @param id  信息文档ID
	 * @return
	 * @throws Exception
	 */
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
	 * @param document  信息文档对象
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
	 * @param docId  信息文档ID
	 * @param readerList  信息读者权限列表
	 * @param authorList  信息作者权限列表（允许修改信息的人员）
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
	 * @param docId  信息文档ID
	 * @param permissionList  信息权限列表
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
	 * @param document  信息文档对象
	 * @param categoryInfo  新的信息分类对象
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
	
	/**
	 * 根据文档ID列表，批量修改数据
	 * @param docIds  文档ID
	 * @param dataChanges  数据修改信息
	 * @return
	 * @throws Exception
	 */
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

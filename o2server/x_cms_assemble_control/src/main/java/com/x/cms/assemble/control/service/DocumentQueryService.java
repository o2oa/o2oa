package com.x.cms.assemble.control.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.dataitem.DataItemConverter;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.tools.ListTools;
import com.x.cms.assemble.control.Business;
import com.x.cms.assemble.control.jaxrs.view.SimpleItemObj;
import com.x.cms.core.entity.Document;
import com.x.cms.core.entity.Review;
import com.x.cms.core.entity.content.Data;
import com.x.cms.core.entity.tools.filter.QueryFilter;
import com.x.query.core.entity.Item;	

/**
 * 对文档信息进行查询的服务类
 */
public class DocumentQueryService {
	
	private DocumentInfoService documentInfoService = new DocumentInfoService();
	
	private ReviewService reviewService = new ReviewService();
	
	public Document get( String id ) throws Exception {
		if( StringUtils.isEmpty( id ) ){
			throw new Exception("id is null!");
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			return documentInfoService.get( emc, id );
		} catch ( Exception e ) {
			throw e;
		}
	}
	
	public String getSequence(String id) throws Exception {
		if( StringUtils.isEmpty( id ) ){
			return null;
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			return documentInfoService.getSequence( emc, id );
		} catch ( Exception e ) {
			throw e;
		}
	}
	
	public Document view( String id, EffectivePerson currentPerson ) throws Exception {
		if( StringUtils.isEmpty( id ) ){
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
			
			if ( ListTools.isEmpty( dataItems ) ) {
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
	
	public List<Document> listByCategoryId( String categoryId ) throws Exception {
		if( StringUtils.isEmpty( categoryId ) ){
			throw new Exception("categoryId is null!");
		}
		List<String> ids = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			ids = documentInfoService.listByCategoryId( emc, categoryId );
			return emc.list( Document.class,  ids );
		} catch ( Exception e ) {
			throw e;
		}
	}
	
	public List<String> listIdsByCategoryId( String categoryId, String orderField, String orderType, int maxCount ) throws Exception {
		if( StringUtils.isEmpty( categoryId ) ){
			throw new Exception("categoryId is null!");
		}
		if( StringUtils.isEmpty( orderField ) ){
			orderField = "publishTime";
		}
		if( StringUtils.isEmpty( orderType ) ){
			orderType = "DESC";
		}
		if( maxCount == 0  ){
			maxCount = 100;
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			return documentInfoService.listByCategoryId( emc, categoryId, orderField, orderType, maxCount );
		} catch ( Exception e ) {
			throw e;
		}
	}
	
	public List<String> listIdsByCategoryId( String categoryId, Integer maxCount ) throws Exception {
		if( StringUtils.isEmpty( categoryId ) ){
			throw new Exception("categoryId is null!");
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			return documentInfoService.listByCategoryId( emc, categoryId, null, null, maxCount );
		} catch ( Exception e ) {
			throw e;
		}
	}
	
	public List<String> listIdsByCategoryId( String categoryId, String orderField, String orderType, Integer maxCount ) throws Exception {
		if( StringUtils.isEmpty( categoryId ) ){
			throw new Exception("categoryId is null!");
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			return documentInfoService.listByCategoryId( emc, categoryId, orderField, orderType, maxCount );
		} catch ( Exception e ) {
			throw e;
		}
	}
	
	public List<String> listIdsByAppId( String appId, String documentType, Integer maxCount ) throws Exception {
		if( StringUtils.isEmpty( appId ) ){
			throw new Exception("categoryId is null!");
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			return documentInfoService.listByAppId( emc, appId, documentType, maxCount );
		} catch ( Exception e ) {
			throw e;
		}
	}	
	
	public Long countByCategoryId(String categoryId ) throws Exception {
		if( StringUtils.isEmpty( categoryId ) ){
			throw new Exception("categoryId is null!");
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			return documentInfoService.countByCategoryId( emc, categoryId );
		} catch ( Exception e ) {
			throw e;
		}
	}
	
	public Long countByAppId(String appId ) throws Exception {
		if( StringUtils.isEmpty( appId ) ){
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
			return new ArrayList<>();
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			return emc.list( Document.class,  ids );
		} catch ( Exception e ) {
			throw e;
		}
	}

	public List<Document> listMyDraft( String name, List<String> categoryIdList, String documentType ) throws Exception {
		if( StringUtils.isEmpty( name )){
			throw new Exception("name is null!");
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			return documentInfoService.listMyDraft( emc, name, categoryIdList, documentType );
		} catch ( Exception e ) {
			throw e;
		}
	}

	public Item getDataWithDocIdWithPath(Document document, String path0 ) throws Exception {
		if( StringUtils.isEmpty( path0 )){
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
	 * 从Document表里，忽略权限根据条件查询文档数量
	 * @param queryFilter
	 * @return
	 * @throws Exception
	 */
	public Long countWithConditionOutofPermission( QueryFilter queryFilter ) throws Exception {
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			return documentInfoService.countWithConditionOutofPermission( emc, queryFilter );
		} catch ( Exception e ) {
			throw e;
		}
	}
	
	public Long getViewCount( String id ) throws Exception {
		if( StringUtils.isEmpty( id ) ){
			throw new Exception("id is null!");
		}
		Business business = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			business = new Business( emc );
			return business.documentViewRecordFactory().sumWithDocmentId( id );
		} catch ( Exception e ) {
			throw e;
		}
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

	public List<String> listUnReviewIds( Integer maxCount ) throws Exception {
		if( maxCount == null ){
			maxCount = 1000;
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			return documentInfoService.listUnReviewIds( emc, maxCount );
		} catch ( Exception e ) {
			throw e;
		}
	}

	/**
	 * 根据条件从Review表里，查询用户可见的所有文档数量
	 * @param personName
	 * @param queryFilter
	 * @return
	 * @throws Exception
	 */
	public Long countWithConditionInReview( String personName, QueryFilter queryFilter ) throws Exception {
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			return reviewService.countViewableWithFilter( emc, personName, queryFilter );
		} catch ( Exception e ) {
			throw e;
		}
	}

	public List<Document> listNextWithConditionInReview( String lastId, Integer pageSize, String orderField, String orderType, String person, QueryFilter queryFilter ) throws Exception {
		if( pageSize == 0 ) { pageSize = 20; }
		
		if( StringUtils.isEmpty( orderField ) ) { 
			orderField = Document.sequence_FIELDNAME;
		}
		if( StringUtils.isEmpty( orderType ) ) { 
			orderType = "desc";
		}
		Document document = null;
		List<Document> documentList = null;
		List<Review> reviewList = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			if( StringUtils.isNotEmpty( lastId ) ) {
				document = emc.find( lastId, Document.class );
			}
			if( document != null ) {
				reviewList = reviewService.listViewableWithFilter( emc, pageSize, document.getSequence(), orderField, orderType, person, queryFilter );
			}else {
				reviewList = reviewService.listViewableWithFilter( emc, pageSize, null, orderField, orderType, person, queryFilter );
			}	
			//根据Review列表查询Document列表信息
			documentList = listDocumentsWithReview( reviewList );			
			return documentList;
		} catch ( Exception e ) {
			throw e;
		}
	}

	/**
	 * 根据条件查询指定条数符合条件的文档信息Review列表
	 * @param orderField
	 * @param orderType
	 * @param person
	 * @param queryFilter
	 * @param maxCount
	 * @return
	 * @throws Exception
	 */
	public List<Review> listNextWithConditionInReview( String orderField, String orderType, String person, QueryFilter queryFilter, int maxCount ) throws Exception {
		if( maxCount == 0 ) { maxCount = 20; }
		if( StringUtils.isEmpty( orderField ) ) { 
			orderField = Document.sequence_FIELDNAME;
		}
		if( StringUtils.isEmpty( orderType ) ) { 
			orderType = "desc";
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {	
			List<Review> reviews = reviewService.listViewableWithFilter( emc, orderField, orderType, person, queryFilter, maxCount );
			return reviews;
		} catch ( Exception e ) {
			throw e;
		}
	}
	
	public List<String> listDocIdsWithConditionInReview(String personName, String orderField, String orderType, QueryFilter queryFilter, Integer maxCount ) throws Exception {
		List<String> docIds = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			docIds = reviewService.listDocIdsWithConditionInReview(emc, personName, orderField, orderType, queryFilter, maxCount);
			return docIds;
		} catch ( Exception e ) {
			throw e;
		}
	}
	
	public List<String> listDocIdsWithConditionInReview(String personName, QueryFilter queryFilter, Integer maxCount ) throws Exception {
		List<String> docIds = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			docIds = reviewService.listDocIdsWithConditionInReview(emc, personName, queryFilter, maxCount);
			return docIds;
		} catch ( Exception e ) {
			throw e;
		}
	}

	/**
	 * 对Document信息进行分页查询（忽略权限）
	 * document和Review除了sequence还有5个排序列支持title, appAlias, categoryAlias, categoryName, creatorUnitName的分页查询
		除了sequence和title, appAlias, categoryAlias, categoryName, creatorUnitName之外，其他的列排序全部在内存进行分页
	 * @param lastId
	 * @param pageSize
	 * @param orderField
	 * @param orderType
	 * @param queryFilter
	 * @return
	 * @throws Exception
	 */
	public List<Document> listNextWithConditionOutofPermission( String lastId, Integer pageSize, String orderField, String orderType, QueryFilter queryFilter ) throws Exception {
		if( pageSize == 0 ) { pageSize = 20; }
		//按正常逻辑根据序列进行分页查询
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {			
			return documentInfoService.listNextWithCondition( emc, pageSize, lastId, orderField, orderType, queryFilter );
		} catch ( Exception e ) {
			throw e;
		}
	}
	
	/**
	 * 根据条件按指定的排序方式查询指定数量的文档信息列表
	 * @param orderField
	 * @param orderType
	 * @param queryFilter
	 * @param maxCount
	 * @return
	 * @throws Exception 
	 */
	public List<Document> listNextWithConditionOutofPermission(String orderField, String orderType, QueryFilter queryFilter, int maxCount ) throws Exception {
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {			
			return documentInfoService.listNextWithCondition(emc, orderField, orderType, queryFilter, maxCount );
		} catch ( Exception e ) {
			throw e;
		}
	}
	
	/**
	 * 将Review列表转换为Document列表
	 * @param reviewList
	 * @return
	 * @throws Exception 
	 */
	private List<Document> listDocumentsWithReview( List<Review> reviewList ) throws Exception {
		List<String> docIds = new ArrayList<>();
		if( ListTools.isNotEmpty( reviewList )) {
			for( Review review : reviewList ) {
				docIds.add( review.getDocId() );
			}
		}
		return list( docIds );
	}

	/**
	 * 查询指定文档按数据排序后的列表
	 * @param docIds
	 * @param path0Name
	 * @param fieldType
	 * @param orderType
	 * @return
	 * @throws Exception
	 */
	public List<SimpleItemObj> listSortObjWithOrderFieldInData( List<String> docIds, String path0Name, String fieldType, String orderType ) throws Exception {
		if( StringUtils.isEmpty( fieldType )){
			throw new Exception("fieldType is null!");
		}
		List<SimpleItemObj> list = new ArrayList<>();
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			List<Item> items = documentInfoService.listSortObjWithOrderFieldInData( emc, docIds, path0Name, fieldType, orderType );
			if( ListTools.isNotEmpty( items )) {
				SimpleItemObj simpleItemObj = null;
				for( Item item : items ) {
					simpleItemObj = new SimpleItemObj();
					simpleItemObj.setId( item.getBundle() );
					simpleItemObj.setValue( item.getStringShortValue() );
					list.add( simpleItemObj );
				}
			}
		} catch ( Exception e ) {
			throw e;
		}
		return list;
	}
}

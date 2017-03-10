package com.x.cms.assemble.control.service;

import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.map.ListOrderedMap;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.item.ItemConverter;
import com.x.base.core.gson.XGsonBuilder;
import com.x.base.core.http.EffectivePerson;
import com.x.cms.assemble.control.Business;
import com.x.cms.assemble.control.jaxrs.document.WrapInDocument;
import com.x.cms.assemble.control.jaxrs.document.WrapInDocumentPictureInfo;
import com.x.cms.assemble.control.jaxrs.documentpermission.WrapInDocumentSearchFilter;
import com.x.cms.core.entity.Document;
import com.x.cms.core.entity.DocumentPictureInfo;
import com.x.cms.core.entity.FileInfo;
import com.x.cms.core.entity.content.DataItem;
import com.x.cms.core.entity.content.DataLobItem;


public class DocumentInfoServiceAdv {
	
	private DocumentInfoService documentInfoService = new DocumentInfoService();
	private FileInfoService fileInfoService = new FileInfoService();
	
	public List<Document> listByCategoryId( String categoryId ) throws Exception {
		if( categoryId == null || categoryId.isEmpty() ){
			throw new Exception("categoryId is null!");
		}
		List<String> ids = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			ids = documentInfoService.listByCategoryId( emc, categoryId );
			return documentInfoService.list( emc, ids );
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
			if( document != null ){
				emc.beginTransaction( Document.class );
				if( document.getViewCount() == null ){
					document.setViewCount( 1L );
				}else{
					document.setViewCount( document.getViewCount() + 1 );
				}
				emc.commit();
			}
			return document;
		} catch ( Exception e ) {
			throw e;
		}
	}

	public Map<?, ?> getDocumentData( Document document ) throws Exception {
		if( document == null ){
			throw new Exception("document is null!");
		}
		List<DataItem> dataItems = null;
		DataLobItem lob = null;
		Business business = null;
		Gson gson = null;
		JsonElement jsonElement = null;
		ItemConverter<DataItem> converter = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			business = new Business( emc );
			
			dataItems = business.getDataItemFactory().listWithDocIdWithPath( document.getId() );
			
			if ( dataItems == null || dataItems.isEmpty() ) {
				return new ListOrderedMap<Object, Object>();
			} else {
				converter = new ItemConverter<DataItem>( DataItem.class );
				for ( DataItem o : dataItems ) {
					if (o.isLobItem()) {
						lob = emc.find(o.getLobItem(), DataLobItem.class);
						if (null != lob) {
							o.setStringLobValue( lob.getData() );
						}
					}
					emc.remove(o);
				}
				jsonElement = converter.assemble( dataItems );
				//添加了jsonElement != null
				if ( jsonElement != null && jsonElement.isJsonObject() ) {
					gson = XGsonBuilder.instance();
					return gson.fromJson( jsonElement, ListOrderedMap.class );
				} else {
					return new ListOrderedMap<Object, Object>();
				}
			}
		} catch ( Exception e ) {
			throw e;
		}
	}

	public List<FileInfo> getAttachmentList(Document document) throws Exception {
		if( document == null ){
			throw new Exception("document is null!");
		}
		if( document.getAttachmentList() == null || document.getAttachmentList().isEmpty() ){
			return null;
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			return fileInfoService.list( emc, document.getAttachmentList() );
		} catch ( Exception e ) {
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

	public Document save( WrapInDocument wrapIn ) throws Exception {
		if( wrapIn == null ){
			throw new Exception("wrapIn is null!");
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			return documentInfoService.save( emc, wrapIn );
		} catch ( Exception e ) {
			throw e;
		}
	}
	
	public DocumentPictureInfo saveMainPicture( WrapInDocumentPictureInfo wrapIn ) throws Exception {
		if( wrapIn == null ){
			throw new Exception("wrapIn is null!");
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			return documentInfoService.saveMainPicture( emc, wrapIn );
		} catch ( Exception e ) {
			throw e;
		}
	}

	public List<Document> listNextWithDocIds(String id, Integer count, List<String> viewAbleDocIds, String orderField, String order) throws Exception {
		if( viewAbleDocIds == null ){
			throw new Exception("viewAbleDocIds is null!");
		}
		if( orderField == null || orderField.isEmpty() ){
			orderField = "publishTime";
		}
		
		if( order == null || order.isEmpty() ){
			order = "DESC";
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			return documentInfoService.listNextWithDocIds( emc, id, count, viewAbleDocIds, orderField, order );
		} catch ( Exception e ) {
			throw e;
		}
	}

	public List<Document> listMyDraft( String name, List<String> categoryIdList ) throws Exception {
		if( name == null || name.isEmpty()){
			throw new Exception("name is null!");
		}
		if( categoryIdList == null || categoryIdList.isEmpty() ){
			throw new Exception("categoryIdList is null!");
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			return documentInfoService.listMyDraft( emc, name, categoryIdList );
		} catch ( Exception e ) {
			throw e;
		}
	}

	public DataItem getDataWithDocIdWithPath(Document document, String path0 ) throws Exception {
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

	public Long countWithDocIds(List<String> viewAbleDocIds) throws Exception {
		if( viewAbleDocIds == null || viewAbleDocIds.isEmpty()){
			return 0L;
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			return documentInfoService.countWithDocIds(emc, viewAbleDocIds );
		} catch ( Exception e ) {
			throw e;
		}
	}

	public List<DocumentPictureInfo> listMainPictureByDocId( String docId ) throws Exception {
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			return documentInfoService.listMainPictureByDocId(emc, docId );
		} catch ( Exception e ) {
			throw e;
		}
	}

	public List<String> lisViewableDocIdsWithFilter(WrapInDocumentSearchFilter wrapInDocumentSearchFilter, Integer maxResultCount ) throws Exception {
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			return documentInfoService.lisViewableDocIdsWithFilter(emc, wrapInDocumentSearchFilter.getAppIdList(),
					wrapInDocumentSearchFilter.getAppAliasList(),
					wrapInDocumentSearchFilter.getCategoryIdList(),
					wrapInDocumentSearchFilter.getCategoryAliasList(),
					wrapInDocumentSearchFilter.getPublisherList(), 
					wrapInDocumentSearchFilter.getTitle(),
					wrapInDocumentSearchFilter.getCreateDateList(),
					wrapInDocumentSearchFilter.getPublishDateList(),
					wrapInDocumentSearchFilter.getStatusList(),
					maxResultCount );
		} catch ( Exception e ) {
			throw e;
		}
	}

	public DocumentPictureInfo getDocumentPictureById( String id ) throws Exception {
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			return documentInfoService.getDocumentPictureById(emc, id );
		} catch ( Exception e ) {
			throw e;
		}
	}

	public Document addViewCount(String id, EffectivePerson effectivePerson) {
//		if( id == null || id.isEmpty() ){
//			throw new Exception("id is null!");
//		}
//		Document document = null;
//		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
//			document = emc.find( id, Document.class );
//			if( document != null ){
//				logService.log( emc, effectivePerson.getName(), "用户[" + effectivePerson.getName() + "]访问了文档", document.getAppId(), document.getCategoryId(), document.getId(), "", "DOCUMENT", "访问" );
//			}	
//			return document;
//		} catch ( Exception e ) {
//			throw e;
//		}
		return null;
	}
	
}

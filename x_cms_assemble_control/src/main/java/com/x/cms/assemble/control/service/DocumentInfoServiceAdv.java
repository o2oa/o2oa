package com.x.cms.assemble.control.service;

import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.map.ListOrderedMap;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.x.base.core.cache.ApplicationCache;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.item.ItemConverter;
import com.x.base.core.entity.item.ItemType;
import com.x.base.core.gson.XGsonBuilder;
import com.x.base.core.http.EffectivePerson;
import com.x.cms.assemble.control.Business;
import com.x.cms.assemble.control.jaxrs.document.WrapInDocument;
import com.x.cms.assemble.control.jaxrs.documentpermission.WrapInDocumentSearchFilter;
import com.x.cms.core.entity.Document;
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
						if ( null != lob ) {
							o.setStringLobValue( lob.getData() );
						}
					}
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
			System.out.println("系统在根据文档查询文档数据时发生异常，ID:" + document.getId() );
			throw e;
		}
	}

	public List<FileInfo> getAttachmentList( Document document ) throws Exception {
		if( document == null ){
			throw new Exception("document is null!");
		}

		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			Business business = new Business(emc);		
			List<String> ids = business.getFileInfoFactory().listByDocument( document.getId() );
			if( ids == null || ids.isEmpty() ){
				return null;
			}
			return fileInfoService.list( emc, ids );
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

	public Boolean saveDataItem( String[] paths, JsonElement jsonElement, Document document ) throws Exception {
		Business business = null;
		String cacheKey = document.getId() + ".path." + StringUtils.join( paths, "." );
		if( paths == null ){
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				business = new Business(emc);
				//第一次录入数据，没有path,并且dataitem表中无数据，所以记录方法特殊
				ItemConverter<DataItem> converter = new ItemConverter<>(DataItem.class);
				List<DataItem> adds = converter.disassemble( jsonElement );
				emc.beginTransaction(DataItem.class);
				for ( DataItem o : adds  ) {
					o.setAppId( document.getAppId() );
					o.setCategoryId( document.getCategoryId() );
					o.setDocId( document.getId() );
					o.setDocStatus( document.getDocStatus() );
					emc.persist( o, CheckPersistType.all );
				}
				emc.commit();
				return true;
			} catch ( Exception e ) {
				throw e;
			}
		}else{
			try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
				business = new Business( emc );
				Integer index = null;
				String[] ps = null;
				String[] parentPaths = null;
				String[] cursorPaths = null;
				DataItem parent = null;
				DataItem cursor = null;
				DataLobItem lob = null;
				List<DataItem> adds = null;
				ItemConverter<DataItem> converter = null;
				try {
					parentPaths = new String[] { "", "", "", "", "", "", "", "" };
					cursorPaths = new String[] { "", "", "", "", "", "", "", "" };
					for (int i = 0; paths != null && i < paths.length - 1; i++) {
						parentPaths[i] = paths[i];
						cursorPaths[i] = paths[i];
					}
					cursorPaths[paths.length - 1] = paths[paths.length - 1];		
				    parent = business.getDataItemFactory().getWithDocIdWithPath( document, parentPaths[0], parentPaths[1], parentPaths[2], parentPaths[3], parentPaths[4], parentPaths[5], parentPaths[6], parentPaths[7]);
					if ( null == parent ) {
						throw new Exception("parent not existed.");
					}
					cursor = business.getDataItemFactory().getWithDocIdWithPath( document, cursorPaths[0], cursorPaths[1], cursorPaths[2], cursorPaths[3], cursorPaths[4], cursorPaths[5], cursorPaths[6], cursorPaths[7]);
					converter = new ItemConverter<>(DataItem.class);
					
					
					business.entityManagerContainer().beginTransaction(DataItem.class);
					business.entityManagerContainer().beginTransaction(DataLobItem.class);
					
					if (( null != cursor ) && cursor.getItemType().equals( ItemType.a )) {
						/* 向数组里面添加一个成员对象 */
						index = business.getDataItemFactory().getArrayLastIndexWithDocIdWithPath( document.getId(), paths );
						/* 新的路径开始 */
						ps = new String[paths.length + 1];
						
						for (int i = 0; i < paths.length; i++) {
							ps[i] = paths[i];
						}
						ps[paths.length] = Integer.toString(index + 1);
						adds = converter.disassemble(jsonElement, ps);
						
						for ( DataItem o : adds ) {
							o.setAppId(document.getAppId());
							o.setCategoryId(document.getCategoryId());
							o.setDocStatus( document.getDocStatus() );
							if (o.isLobItem()) {
								lob = new DataLobItem();
								lob.setData(o.getStringLobValue());
								lob.setDistributeFactor(o.getDistributeFactor());
								o.setLobItem(lob.getId());
								business.entityManagerContainer().persist(lob);
							}
							business.entityManagerContainer().persist(o);
						}
					} else if (( cursor == null ) && parent.getItemType().equals( ItemType.o )) {
						adds = converter.disassemble(jsonElement, paths);
						
						for ( DataItem o : adds ) {
							o.setAppId( document.getAppId() );
							o.setCategoryId( document.getCategoryId() );
							o.setDocStatus( document.getDocStatus() );
							if ( o.isLobItem() ) {
								lob = new DataLobItem();
								lob.setData(o.getStringLobValue());
								lob.setDistributeFactor(o.getDistributeFactor());
								o.setLobItem(lob.getId());
								business.entityManagerContainer().persist(lob);
							}
							business.entityManagerContainer().persist(o);
						}
					} else {
						throw new Exception("unexpected post data with document" + document + ".path:" + StringUtils.join(paths, ".") + "json:" + jsonElement);
					}
					
					business.entityManagerContainer().commit();
					ApplicationCache.notify( DataItem.class, cacheKey );
					
				} catch (Exception e) {
					throw new Exception("postWithApplicationDict error.", e);
				}
				return true;
			} catch ( Exception e ) {
				throw e;
			}
		}
	}

	public Boolean updateDataItem( String[] paths, JsonElement jsonElement, Document document) throws Exception {
		Business business = null;
		String cacheKey = document.getId() + ".path." + StringUtils.join(paths, ".");
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			business = new Business( emc );
			DataLobItem lob = null;
			List<DataItem> currents = null;
			List<DataItem> removes = null;
			List<DataItem> adds = null;
			List<DataItem> exists = null;
			ItemConverter<DataItem> converter = null;
			business = new Business(emc);
			converter = new ItemConverter<>(DataItem.class);
			
			if( paths == null ){
				exists = business.getDataItemFactory().listWithDocIdWithPath( document.getId() );
			}else{
				exists = business.getDataItemFactory().listWithDocIdWithPath( document.getId(), paths );
			}
			if ( exists == null || exists.isEmpty() ) {
				throw new Exception( "data{document:" + document.getId() + "} on path:" + StringUtils.join(paths, ".") + " is not existed.");
			}
			if( paths == null ){
				currents = converter.disassemble( jsonElement );
			}else{
				currents = converter.disassemble( jsonElement, paths );
			}
			
			removes = converter.subtract( exists, currents );
			adds = converter.subtract( currents, exists );
			
			emc.beginTransaction(DataItem.class);
			emc.beginTransaction(DataLobItem.class);
			
			for ( DataItem o : removes ) {
				if (o.isLobItem()) {
					lob = emc.find( o.getLobItem(), DataLobItem.class );
					if (null != lob) {
						emc.remove(lob);
					}
				}
				emc.remove(o);
			}
			for ( DataItem o : adds ) {
				o.setDocId( document.getId() );
				o.setAppId( document.getAppId() );
				o.setCategoryId( document.getCategoryId() );
				o.setDocStatus( document.getDocStatus() );
				if (o.isLobItem()) {
					lob = new DataLobItem();
					lob.setData(o.getStringLobValue());
					lob.setDistributeFactor(o.getDistributeFactor());
					o.setLobItem(lob.getId());
					emc.persist(lob);
				}
				emc.persist( o, CheckPersistType.all );
			}
			emc.commit();
			ApplicationCache.notify( DataItem.class, cacheKey );
			return true;
		} catch ( Exception e ) {
			throw e;
		}
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
	
}

package com.x.cms.assemble.control.service;

import java.util.Date;
import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.cms.assemble.control.Business;
import com.x.cms.core.entity.Document;
import com.x.cms.core.entity.DocumentViewRecord;

public class DocumentViewRecordServiceAdv {
	
	private UserManagerService userManagerService = new UserManagerService();
	private DocumentViewRecordService documentViewRecordService = new DocumentViewRecordService();
	
	public List<DocumentViewRecord> list( List<String> ids ) throws Exception {
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			return documentViewRecordService.list( emc, ids );
		} catch ( Exception e ) {
			throw e;
		}
	}

	public List<String> listByDocument( String docId ) throws Exception {
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			return documentViewRecordService.listByDocument( emc, docId );
		} catch ( Exception e ) {
			throw e;
		}
	}
	
	public List<String> listByPerson( String personName ) throws Exception {
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			return documentViewRecordService.listByPerson( emc, personName );
		} catch ( Exception e ) {
			throw e;
		}
	}
	
	public void deleteByDocument( String docId ) throws Exception {
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			documentViewRecordService.deleteByDocument( emc, docId );
		} catch ( Exception e ) {
			throw e;
		}
	}

	/**
	 * TODO:记录访问日志，一个用户一篇文档只保留一条记录，更新访问次数和最后访问时间
	 * 
	 * @param document
	 * @param personName
	 * @throws Exception
	 */
	public void addViewRecord( String docId, String personName ) throws Exception {
		DocumentViewRecord documentViewRecord = null;
		Document document = null;
		Business business = null;
		List<String> ids = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			business = new Business( emc );
			emc.beginTransaction( DocumentViewRecord.class );
			
			ids = business.documentViewRecordFactory().listByDocAndPerson( docId, personName );
			if( ids != null && !ids.isEmpty() ){
				for( String id : ids ){
					documentViewRecord = emc.find( id, DocumentViewRecord.class );
					documentViewRecord.setLastViewTime( new Date() );
					if( documentViewRecord.getViewCount() == null ){
						documentViewRecord.setViewCount( 1 );
					}
					documentViewRecord.setViewCount( documentViewRecord.getViewCount() + 1 );
					emc.check( documentViewRecord, CheckPersistType.all ); 
				}
			}else{
				document = emc.find( docId, Document.class );
				if( document != null ){
					documentViewRecord = new DocumentViewRecord();
					documentViewRecord.setAppId( document.getAppId() );
					documentViewRecord.setCategoryId( document.getCategoryId() );
					documentViewRecord.setDocumentId( document.getId() );
					documentViewRecord.setViewerName( personName );
					documentViewRecord.setAppName( document.getAppName() );
					documentViewRecord.setCategoryName( document.getCategoryName() );
					documentViewRecord.setTitle( document.getTitle() );
					documentViewRecord.setLastViewTime( new Date() );
					documentViewRecord.setViewCount( 1 );
					documentViewRecord.setViewerCompany( userManagerService.getCompanyNameByEmployeeName( personName ));
					documentViewRecord.setViewerOrganization( userManagerService.getDepartmentNameByEmployeeName( personName ));
					
					emc.persist( documentViewRecord, CheckPersistType.all ); 
		
				}else{
					throw new Exception("document is not exits, system can not save view record.id:" + docId );
				}
			}
			emc.commit();
		} catch ( Exception e ) {
			throw e;
		}
	}
	
	public List<DocumentViewRecord> listNextWithDocIds( String id, String docId, Integer count, String order) throws Exception {
		if( docId == null ){
			throw new Exception("docId is null!");
		}
		if( order == null || order.isEmpty() ){
			order = "DESC";
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			return documentViewRecordService.listNextWithDocIds( emc, id, docId, count, order );
		} catch ( Exception e ) {
			throw e;
		}
	}
	
	public Long countWithDocIds( String docId ) throws Exception {
		if( docId == null ){
			throw new Exception("docId is null!");
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			return documentViewRecordService.countWithDocIds( emc, docId );
		} catch ( Exception e ) {
			throw e;
		}
	}
}

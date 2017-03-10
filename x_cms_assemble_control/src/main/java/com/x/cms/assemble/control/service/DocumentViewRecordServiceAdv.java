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

	public void addViewRecord(Document document, String personName ) throws Exception {
		DocumentViewRecord documentViewRecord = null;
		Business business = null;
		List<String> ids = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			business = new Business( emc );
			
			ids = business.documentViewRecordFactory().listByDocAndPerson( document.getId(), personName );
			if( ids != null && !ids.isEmpty() ){
				for( String id : ids ){
					documentViewRecord = emc.find( id, DocumentViewRecord.class );
					documentViewRecord.setUpdateTime( new Date() );
					emc.beginTransaction( DocumentViewRecord.class );
					emc.check( documentViewRecord, CheckPersistType.all ); 
					emc.commit();
				}
			}else{
				documentViewRecord = new DocumentViewRecord();
				documentViewRecord.setAppId( document.getAppId() );
				documentViewRecord.setCategoryId( document.getCategoryId() );
				documentViewRecord.setDocumentId( document.getId() );
				documentViewRecord.setViewerName( personName );
				documentViewRecord.setAppName( document.getAppName() );
				documentViewRecord.setCategoryName( document.getCategoryName() );
				documentViewRecord.setTitle( document.getTitle() );
				documentViewRecord.setViewerCompany( userManagerService.getCompanyNameByEmployeeName( personName ));
				documentViewRecord.setViewerOrganization( userManagerService.getDepartmentNameByEmployeeName( personName ));
				
				emc.beginTransaction( DocumentViewRecord.class );
				emc.persist( documentViewRecord, CheckPersistType.all ); 
				emc.commit();
			}
		} catch ( Exception e ) {
			throw e;
		}
	}
}

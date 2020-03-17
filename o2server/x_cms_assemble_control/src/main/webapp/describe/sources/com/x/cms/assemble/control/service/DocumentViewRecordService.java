package com.x.cms.assemble.control.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.tools.ListTools;
import com.x.cms.assemble.control.Business;
import com.x.cms.core.entity.DocumentViewRecord;

public class DocumentViewRecordService {

	public List<DocumentViewRecord> list( EntityManagerContainer emc, List<String> ids ) throws Exception {
		if( ListTools.isEmpty( ids ) ){
			return null;
		}
		Business business = new Business( emc );
		return business.documentViewRecordFactory().list( ids );
	}
	
	public List<String> listByDocument( EntityManagerContainer emc, String docId ) throws Exception {
		if( StringUtils.isEmpty( docId ) ){
			return null;
		}
		Business business = new Business( emc );
		return business.documentViewRecordFactory().listByDocument(docId);
	}
	
	public List<String> listByPerson( EntityManagerContainer emc, String personId, Integer maxCount ) throws Exception {
		if( StringUtils.isEmpty( personId ) ){
			return null;
		}
		if( maxCount == null || maxCount == 0 ) {
			maxCount = 50;
		}
		Business business = new Business( emc );
		return business.documentViewRecordFactory().listByPerson( personId, maxCount );
	}
	
	public List<String> listDocIdsByPerson( EntityManagerContainer emc, String personId, Integer maxCount ) throws Exception {
		if( StringUtils.isEmpty( personId ) ){
			return null;
		}
		if( maxCount == null || maxCount == 0 ) {
			maxCount = 50;
		}
		Business business = new Business( emc );
		List<String> docIds = new ArrayList<>();
		List<DocumentViewRecord> records =  business.documentViewRecordFactory().listRecordsByPerson( personId, maxCount );
		if( ListTools.isNotEmpty(  records)) {
			for( DocumentViewRecord record : records ) {
				docIds.add( record.getDocumentId() );
			}
		}
		return docIds;
	}

	public void deleteByDocument( EntityManagerContainer emc, String docId ) throws Exception {
		if( docId == null ){
			throw new Exception("文档对象document为空！");
		}
		List<String> ids = null;
		DocumentViewRecord permission = null;
		ids = new Business( emc ).documentViewRecordFactory().listByDocument( docId );
		if( ids != null && ids.isEmpty() ){
			for( String id : ids ){
				permission = emc.find( id, DocumentViewRecord.class );
				emc.beginTransaction( DocumentViewRecord.class );
				emc.remove( permission, CheckRemoveType.all );
				emc.commit();
			}
		}
	}

	public List<DocumentViewRecord> listNextWithDocIds( EntityManagerContainer emc, String id, String docId, Integer count, String order ) throws Exception {
		Business business = new Business(emc);
		DocumentViewRecord documentViewRecord = null;
		Object sequenceFieldValue = null;
		if( id != null && !"(0)".equals(id) && !id.isEmpty() ){
			if ( !StringUtils.equalsIgnoreCase( id, StandardJaxrsAction.EMPTY_SYMBOL ) ) {
				documentViewRecord = emc.find( id, DocumentViewRecord.class );
				if( documentViewRecord != null ){
					sequenceFieldValue = PropertyUtils.getProperty( documentViewRecord, "lastViewTime" );
				}
			}
		}		
		return business.documentViewRecordFactory().listNextWithDocIds( docId, count, sequenceFieldValue, order );
	}

	public Long countWithDocIds(EntityManagerContainer emc, String docId) throws Exception {
		Business business = new Business(emc);	
		return business.documentViewRecordFactory().countWithDocIds( docId );
	}
	
}

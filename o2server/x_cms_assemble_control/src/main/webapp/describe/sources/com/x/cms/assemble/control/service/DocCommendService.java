package com.x.cms.assemble.control.service;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.tools.ListTools;
import com.x.cms.assemble.control.Business;
import com.x.cms.core.entity.Document;
import com.x.cms.core.entity.DocumentCommend;

class DocCommendService {

	public List<String> listByDocAndPerson( EntityManagerContainer emc, String docId, String personName, Integer maxCount ) throws Exception {
		if( StringUtils.isEmpty( docId ) ){
			return null;
		}
		if( StringUtils.isEmpty( personName ) ){
			return null;
		}
		Business business = new Business( emc );
		return business.documentCommendFactory().listByDocAndPerson(docId, personName, maxCount );
	}
	
	public List<String> listByDocument( EntityManagerContainer emc, String docId, Integer maxCount ) throws Exception {
		if( StringUtils.isEmpty( docId ) ){
			return null;
		}
		Business business = new Business( emc );
		return business.documentCommendFactory().listByDocument(docId, maxCount);
	}
	
	public List<String> listWithPerson( EntityManagerContainer emc, String personName, Integer maxCount ) throws Exception {
		if( StringUtils.isEmpty( personName ) ){
			return null;
		}
		Business business = new Business( emc );
		return business.documentCommendFactory().listWithPerson(personName, maxCount);
	}

	public DocumentCommend create( EntityManagerContainer emc, DocumentCommend wrapIn ) throws Exception {
		if( wrapIn == null ){
			throw new Exception("wrapIn documentCommend is null!");
		}
		Business business = new Business( emc );
		List<String> commendIds = null;
		DocumentCommend documentCommend = null;
		commendIds = business.documentCommendFactory().listByDocAndPerson( wrapIn.getDocumentId(), wrapIn.getCommendPerson(), 1 );		
		if( ListTools.isEmpty( commendIds ) ){
			Document doc = emc.find( wrapIn.getDocumentId(), Document.class );
			documentCommend = new DocumentCommend();
			documentCommend.setId( DocumentCommend.createId() );
			documentCommend.setCommendPerson( wrapIn.getCommendPerson() );
			documentCommend.setDocumentId( wrapIn.getDocumentId() );
			emc.beginTransaction( DocumentCommend.class );
			if( doc != null ) {
				emc.beginTransaction( Document.class );
				doc.addCommendCount(1);
				emc.check( doc, CheckPersistType.all );
			}
			emc.persist( documentCommend, CheckPersistType.all );
			emc.commit();
		}else {
			documentCommend = emc.find( commendIds.get(0), DocumentCommend.class );
		}
		return documentCommend;		
	}
	
	public DocumentCommend delete( EntityManagerContainer emc, String id ) throws Exception {
		if( StringUtils.isEmpty( id ) ){
			throw new Exception("id is empty!");
		}
		DocumentCommend documentCommend = emc.find( id, DocumentCommend.class );
		if( documentCommend != null ){
			Document doc = emc.find( documentCommend.getDocumentId(), Document.class );
			emc.beginTransaction( DocumentCommend.class );
			if( doc != null ) {
				emc.beginTransaction( Document.class );
				doc.subCommendCount(1);
				emc.check( doc, CheckPersistType.all );
			}
			emc.remove( documentCommend, CheckRemoveType.all );
			emc.commit();
		}
		return documentCommend;		
	}

	public List<String> delete(EntityManagerContainer emc, List<String> ids ) throws Exception {
		if( ListTools.isEmpty( ids ) ){
			throw new Exception("ids is empty!");
		}
		List<DocumentCommend> documentCommends = emc.list( DocumentCommend.class, ids  );
		if( ListTools.isNotEmpty( documentCommends )){
			Document doc = null;
			emc.beginTransaction( DocumentCommend.class );
			emc.beginTransaction( Document.class );
			for( DocumentCommend documentCommend : documentCommends ) {
				doc = emc.find( documentCommend.getDocumentId(), Document.class );
				if( doc != null ) {
					doc.subCommendCount(1);
					emc.check( doc, CheckPersistType.all );
				}
				emc.remove( documentCommend, CheckRemoveType.all );
			}
			emc.commit();
		}
		return ids;		
	}
}

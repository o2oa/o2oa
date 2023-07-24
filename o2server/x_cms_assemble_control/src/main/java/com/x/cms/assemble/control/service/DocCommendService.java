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
import com.x.cms.core.entity.DocumentCommentInfo;

class DocCommendService {

	public List<String> listByDocAndPerson( EntityManagerContainer emc, String docId, String personName, Integer maxCount, String type) throws Exception {
		if( StringUtils.isEmpty( docId ) ){
			return null;
		}
		if( StringUtils.isEmpty( personName ) ){
			return null;
		}
		Business business = new Business( emc );
		return business.documentCommendFactory().listByDocAndPerson(docId, personName, maxCount, type);
	}

	public List<String> listByDocument( EntityManagerContainer emc, String docId, Integer maxCount, String type) throws Exception {
		if( StringUtils.isEmpty( docId ) ){
			return null;
		}
		Business business = new Business( emc );
		return business.documentCommendFactory().listByDocument(docId, maxCount, type);
	}

	public List<String> listByCommentAndPerson( EntityManagerContainer emc, String commentId, String personName, Integer maxCount) throws Exception {
		if( StringUtils.isEmpty( commentId ) ){
			return null;
		}
		if( StringUtils.isEmpty( personName ) ){
			return null;
		}
		Business business = new Business( emc );
		return business.documentCommendFactory().listByCommentAndPerson(commentId, personName, maxCount);
	}

	public List<String> listByComment( EntityManagerContainer emc, String commentId, Integer maxCount) throws Exception {
		if( StringUtils.isEmpty( commentId ) ){
			return null;
		}
		Business business = new Business( emc );
		return business.documentCommendFactory().listByComment(commentId, maxCount);
	}

	public List<String> listWithPerson( EntityManagerContainer emc, String personName, Integer maxCount, String type) throws Exception {
		if( StringUtils.isEmpty( personName ) ){
			return null;
		}
		Business business = new Business( emc );
		return business.documentCommendFactory().listWithPerson(personName, maxCount, type);
	}

	public DocumentCommend create( EntityManagerContainer emc, DocumentCommend wrapIn ) throws Exception {
		if( wrapIn == null ){
			throw new Exception("wrapIn documentCommend is null!");
		}
		Business business = new Business( emc );
		List<String> commendIds = null;
		DocumentCommend documentCommend = null;
		if(DocumentCommend.COMMEND_TYPE_COMMENT.equals(wrapIn.getType())){
			commendIds = business.documentCommendFactory().listByCommentAndPerson(wrapIn.getCommentId(), wrapIn.getCommendPerson(), 1);
		}else {
			commendIds = business.documentCommendFactory().listByDocAndPerson(wrapIn.getDocumentId(), wrapIn.getCommendPerson(), 1, DocumentCommend.COMMEND_TYPE_DOCUMENT);
		}
		if( ListTools.isEmpty( commendIds ) ){
			documentCommend = new DocumentCommend();
			documentCommend.setId( DocumentCommend.createId() );
			documentCommend.setCommendPerson( wrapIn.getCommendPerson() );
			documentCommend.setDocumentId( wrapIn.getDocumentId() );
			documentCommend.setTitle(wrapIn.getTitle());
			documentCommend.setCreatorPerson(wrapIn.getCreatorPerson());
			documentCommend.setType(wrapIn.getType());
			documentCommend.setCommentId(wrapIn.getCommentId());
			documentCommend.setCommentTitle(wrapIn.getCommentTitle());
			emc.beginTransaction( DocumentCommend.class );
			if(DocumentCommend.COMMEND_TYPE_COMMENT.equals(wrapIn.getType())){
				DocumentCommentInfo doc = emc.find( wrapIn.getCommentId(), DocumentCommentInfo.class );
				if( doc != null ) {
					emc.beginTransaction( DocumentCommentInfo.class );
					doc.addCommendCount(1);
					emc.check( doc, CheckPersistType.all );
				}
			}else{
				Document doc = emc.find( wrapIn.getDocumentId(), Document.class );
				if( doc != null ) {
					emc.beginTransaction( Document.class );
					doc.setCommendCount(business.documentCommendFactory().countByDocAndType(doc.getId(), DocumentCommend.COMMEND_TYPE_DOCUMENT) + 1);
					emc.check( doc, CheckPersistType.all );
				}
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
			emc.beginTransaction( DocumentCommend.class );
			if(DocumentCommend.COMMEND_TYPE_COMMENT.equals(documentCommend.getType())){
				DocumentCommentInfo doc = emc.find( documentCommend.getCommentId(), DocumentCommentInfo.class );
				if( doc != null ) {
					emc.beginTransaction( DocumentCommentInfo.class );
					doc.subCommendCount(1);
					emc.check( doc, CheckPersistType.all );
				}
			}else{
				Document doc = emc.find( documentCommend.getDocumentId(), Document.class );
				if( doc != null ) {
					Business business = new Business( emc );
					emc.beginTransaction( Document.class );
					doc.setCommendCount(business.documentCommendFactory().countByDocAndType(doc.getId(), DocumentCommend.COMMEND_TYPE_DOCUMENT) - 1);
					emc.check( doc, CheckPersistType.all );
				}
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
			Business business = new Business( emc );
			emc.beginTransaction( DocumentCommend.class );
			emc.beginTransaction( DocumentCommentInfo.class );

			Long docCommendCount = 0L;
			for( DocumentCommend documentCommend : documentCommends ) {
				if(DocumentCommend.COMMEND_TYPE_COMMENT.equals(documentCommend.getType())){
					DocumentCommentInfo doc = emc.find( documentCommend.getCommentId(), DocumentCommentInfo.class );
					if( doc != null ) {
						doc.subCommendCount(1);
						emc.check( doc, CheckPersistType.all );
					}
				}else{
					docCommendCount ++;
				}
				emc.remove( documentCommend, CheckRemoveType.all );
			}
			if(docCommendCount > 0) {
				Document doc = emc.find(documentCommends.get(0).getDocumentId(), Document.class);
				if (doc != null) {
					emc.beginTransaction( Document.class );
					doc.setCommendCount(business.documentCommendFactory().countByDocAndType(doc.getId(), DocumentCommend.COMMEND_TYPE_DOCUMENT) - docCommendCount);
					emc.check(doc, CheckPersistType.all);
				}
			}
			emc.commit();
		}
		return ids;
	}
}

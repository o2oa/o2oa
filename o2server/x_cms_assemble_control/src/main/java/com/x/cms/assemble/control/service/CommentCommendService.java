package com.x.cms.assemble.control.service;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.tools.ListTools;
import com.x.cms.assemble.control.Business;
import com.x.cms.core.entity.Document;
import com.x.cms.core.entity.DocumentCommentCommend;
import com.x.cms.core.entity.DocumentCommentInfo;

class CommentCommendService {

	public List<String> listCommentIdsWithPerson( EntityManagerContainer emc, String personName, Integer maxCount ) throws Exception {
		if( StringUtils.isEmpty( personName ) ){
			return null;
		}
		Business business = new Business( emc );
		return business.documentCommentCommendFactory().listCommentIdsWithPerson(personName, maxCount);
	}
	
	public List<String> listIdsByDocumentCommentAndPerson( EntityManagerContainer emc, String commentId, String personName, Integer maxCount ) throws Exception {
		if( StringUtils.isEmpty( commentId ) ){
			return null;
		}
		if( StringUtils.isEmpty( personName ) ){
			return null;
		}
		Business business = new Business( emc );
		return business.documentCommentCommendFactory().listIdsByDocumentCommentAndPerson(commentId, personName, maxCount);
	}
	
	public List<String> listByComment( EntityManagerContainer emc, String commentId, Integer maxCount ) throws Exception {
		if( StringUtils.isEmpty( commentId ) ){
			return null;
		}
		Business business = new Business( emc );
		return business.documentCommentCommendFactory().listIdsByDocumentComment( commentId, maxCount );
	}
	
	public List<String> listWithPerson( EntityManagerContainer emc, String personName, Integer maxCount ) throws Exception {
		if( StringUtils.isEmpty( personName ) ){
			return null;
		}
		Business business = new Business( emc );
		return business.documentCommentCommendFactory().listWithPerson(personName, maxCount);
	}

	public DocumentCommentCommend create( EntityManagerContainer emc, DocumentCommentCommend commend ) throws Exception {
		if( commend == null ){
			throw new Exception("wrapIn documentCommentCommend is null!");
		}
		Business business = new Business( emc );
		List<String> commendIds = null;
		DocumentCommentCommend documentCommentCommend = null;
		commendIds = business.documentCommentCommendFactory().listIdsByDocumentCommentAndPerson( commend.getCommentId(), commend.getCommendPerson(), 1 );
		if( ListTools.isEmpty( commendIds ) ){
			DocumentCommentInfo comment = emc.find( commend.getCommentId(), DocumentCommentInfo.class );
			documentCommentCommend = new DocumentCommentCommend();
			documentCommentCommend.setId( DocumentCommentCommend.createId() );
			documentCommentCommend.setCommendPerson( commend.getCommendPerson() );
			documentCommentCommend.setDocumentId( commend.getDocumentId() );
			documentCommentCommend.setCommentId(commend.getCommentId());
			emc.beginTransaction( DocumentCommentCommend.class );
			if( comment != null ) {
				emc.beginTransaction( DocumentCommentInfo.class );
				comment.addCommendCount(1);
				emc.check( comment, CheckPersistType.all );
			}
			emc.persist( documentCommentCommend, CheckPersistType.all );
			emc.commit();
		}else {
			documentCommentCommend = emc.find( commendIds.get(0), DocumentCommentCommend.class );
		}
		return documentCommentCommend;		
	}
	
	public DocumentCommentCommend delete( EntityManagerContainer emc, String id ) throws Exception {
		if( StringUtils.isEmpty( id ) ){
			throw new Exception("id is empty!");
		}
		DocumentCommentCommend documentCommentCommend = emc.find( id, DocumentCommentCommend.class );
		if( documentCommentCommend != null ){
			DocumentCommentInfo comment = emc.find( documentCommentCommend.getCommentId(), DocumentCommentInfo.class );
			emc.beginTransaction( DocumentCommentCommend.class );
			if( comment != null ) {
				emc.beginTransaction( Document.class );
				comment.subCommendCount(1);
				emc.check( comment, CheckPersistType.all );
			}
			emc.remove( documentCommentCommend, CheckRemoveType.all );
			emc.commit();
		}
		return documentCommentCommend;
	}

	public List<String> delete(EntityManagerContainer emc, List<String> ids ) throws Exception {
		if( ListTools.isEmpty( ids ) ){
			throw new Exception("ids is empty!");
		}
		List<DocumentCommentCommend> documentCommentCommends = emc.list( DocumentCommentCommend.class, ids  );
		if( ListTools.isNotEmpty( documentCommentCommends )){
			DocumentCommentInfo comment = null;
			emc.beginTransaction( DocumentCommentCommend.class );
			emc.beginTransaction( DocumentCommentInfo.class );
			for( DocumentCommentCommend documentCommentCommend : documentCommentCommends ) {
				comment = emc.find( documentCommentCommend.getCommentId(), DocumentCommentInfo.class );
				if( comment != null ) {
					comment.subCommendCount(1);
					emc.check( comment, CheckPersistType.all );
				}
				emc.remove( documentCommentCommend, CheckRemoveType.all );
			}
			emc.commit();
		}
		return ids;		
	}
}

package com.x.cms.assemble.control.service;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.cms.core.entity.Document;
import com.x.cms.core.entity.DocumentCommend;
import com.x.cms.core.entity.DocumentCommentInfo;

/**
 * 文档点赞信息持久化管理的服务类
 *
 * @author O2LEE
 */
public class DocCommendPersistService {

	private DocCommendService docCommendService = new DocCommendService();

	public DocumentCommend create( String personName, Document document, DocumentCommentInfo commentInfo) throws Exception {
		if( StringUtils.isEmpty( personName ) ){
			throw new Exception("personName can not empty!");
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			DocumentCommend documentCommend = new DocumentCommend();
			documentCommend.setCommendPerson( personName );
			documentCommend.setDocumentId( document.getId() );
			documentCommend.setTitle(document.getTitle());
			documentCommend.setCreatorPerson(document.getCreatorPerson());
			if(commentInfo== null) {
				documentCommend.setType(DocumentCommend.COMMEND_TYPE_DOCUMENT);
			}else{
				documentCommend.setCreatorPerson(commentInfo.getCreatorName());
				documentCommend.setType(DocumentCommend.COMMEND_TYPE_COMMENT);
				documentCommend.setCommentId(commentInfo.getId());
				documentCommend.setCommentTitle(commentInfo.getTitle());
			}
			return docCommendService.create(emc, documentCommend );
		} catch ( Exception e ) {
			throw e;
		}
	}

	public List<String> delete( String docId, String personName, String type) throws Exception {
		if( StringUtils.isEmpty( docId ) ){
			throw new Exception("docId is empty!");
		}
		if( StringUtils.isEmpty( personName ) ){
			throw new Exception("personName is empty!");
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			List<String> ids;
			if(DocumentCommend.COMMEND_TYPE_COMMENT.equals(type)){
				ids = docCommendService.listByCommentAndPerson(emc, docId, personName, 0);
			}else {
				ids = docCommendService.listByDocAndPerson(emc, docId, personName, 0, type);
			}
			if( ListTools.isNotEmpty( ids ) ) {
				return docCommendService.delete(emc, ids );
			}else {
				return null;
			}

		} catch ( Exception e ) {
			throw e;
		}
	}

	public DocumentCommend delete( String id ) throws Exception {
		if( StringUtils.isEmpty( id ) ){
			throw new Exception("id is empty!");
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			return docCommendService.delete(emc, id);
		} catch ( Exception e ) {
			throw e;
		}
	}

}

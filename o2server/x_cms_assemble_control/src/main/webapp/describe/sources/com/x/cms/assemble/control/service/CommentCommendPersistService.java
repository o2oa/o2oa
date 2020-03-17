package com.x.cms.assemble.control.service;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.cms.core.entity.DocumentCommentCommend;
import com.x.cms.core.entity.DocumentCommentInfo;

/**
 * 文档点赞信息持久化管理的服务类
 * 
 * @author O2LEE
 */
public class CommentCommendPersistService {
	
	private CommentCommendService commentCommendService = new CommentCommendService();
	
	public DocumentCommentCommend create( DocumentCommentCommend commentCommend ) throws Exception {
		if( commentCommend == null ){
			throw new Exception("wrapIn document commend is null!");
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			return commentCommendService.create(emc, commentCommend );
		} catch ( Exception e ) {
			throw e;
		}
	}
	
	public DocumentCommentCommend create( String personName, String commentId ) throws Exception {
		if( StringUtils.isEmpty( commentId ) ){
			throw new Exception("commentId can not empty!");
		}
		if( StringUtils.isEmpty( personName ) ){
			throw new Exception("personName can not empty!");
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			DocumentCommentInfo comment = emc.find( commentId, DocumentCommentInfo.class );
			if(comment != null  ) {
				DocumentCommentCommend commentCommend = new DocumentCommentCommend();
				commentCommend.setCommendPerson( personName );
				commentCommend.setDocumentId( comment.getDocumentId() );
				commentCommend.setCommentId( comment.getId());
				return commentCommendService.create(emc, commentCommend );
			}else {
				throw new Exception("comment not exists! ID=" + commentId );
			}
		} catch ( Exception e ) {
			throw e;
		}
	}
	
	public List<String> deleteWithCommentId( String commentId, String personName ) throws Exception {
		if( StringUtils.isEmpty( commentId ) ){
			throw new Exception("commentId is empty!");
		}
		if( StringUtils.isEmpty( personName ) ){
			throw new Exception("personName is empty!");
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			List<String> ids = commentCommendService.listIdsByDocumentCommentAndPerson(emc, commentId, personName, 999 );
			if( ListTools.isNotEmpty( ids ) ) {
				return commentCommendService.delete(emc, ids );
			}else {
				return null;
			}
			
		} catch ( Exception e ) {
			throw e;
		}
	}
	
	public DocumentCommentCommend delete( String id ) throws Exception {
		if( StringUtils.isEmpty( id ) ){
			throw new Exception("id is empty!");
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			return commentCommendService.delete(emc, id);
		} catch ( Exception e ) {
			throw e;
		}
	}
	
}

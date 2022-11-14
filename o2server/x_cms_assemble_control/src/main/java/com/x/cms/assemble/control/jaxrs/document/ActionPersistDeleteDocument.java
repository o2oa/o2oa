package com.x.cms.assemble.control.jaxrs.document;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.annotation.AuditLog;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.config.StorageMapping;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.tools.ListTools;
import com.x.cms.assemble.control.Business;
import com.x.cms.assemble.control.DocumentDataHelper;
import com.x.cms.assemble.control.ThisApplication;
import com.x.cms.assemble.control.service.CmsBatchOperationPersistService;
import com.x.cms.assemble.control.service.CmsBatchOperationProcessService;
import com.x.cms.core.entity.Document;
import com.x.cms.core.entity.DocumentCommend;
import com.x.cms.core.entity.DocumentCommentInfo;
import com.x.cms.core.entity.FileInfo;
import com.x.cms.core.entity.enums.DocumentStatus;
import com.x.cms.core.entity.message.DocumentEvent;
import com.x.query.core.entity.Item;

/**
 * 删除文档
 * @author sword
 */
public class ActionPersistDeleteDocument extends BaseAction {

	@AuditLog(operation = "删除文档")
	protected ActionResult<Wo> execute( HttpServletRequest request, String id, EffectivePerson effectivePerson ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		List<String> allFileInfoIds = null;
		FileInfo fileInfo = null;
		StorageMapping mapping = null;

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business( emc );

			//先判断需要操作的文档信息是否存在，根据ID进行一次查询，如果不存在不允许继续操作
			Document document = business.getDocumentFactory().get( id );
			if (null == document) {
				Exception exception = new ExceptionDocumentNotExists( id );
				result.error( exception );
				throw exception;
			}

			//进行数据库持久化操作
			emc.beginTransaction( Document.class );
			emc.beginTransaction( Item.class );
			emc.beginTransaction( FileInfo.class );
			emc.beginTransaction( DocumentCommentInfo.class );


			//删除与该文档有关的所有数据信息
			DocumentDataHelper documentDataHelper = new DocumentDataHelper( emc, document );
			documentDataHelper.remove();

			allFileInfoIds = business.getFileInfoFactory().listAllByDocument( id );
			if( allFileInfoIds != null && !allFileInfoIds.isEmpty() ){
				for( String fileInfoId : allFileInfoIds ){
					fileInfo = emc.find( fileInfoId, FileInfo.class );
					if( fileInfo != null ){
						if( "ATTACHMENT".equals( fileInfo.getFileType() )){
							mapping = ThisApplication.context().storageMappings().get( FileInfo.class, fileInfo.getStorage() );
							fileInfo.deleteContent(mapping);
						}
					}
					emc.remove( fileInfo, CheckRemoveType.all );
				}
			}

			List<String>  commentIds = business.documentCommentInfoFactory().listWithDocument( id );
			if( ListTools.isNotEmpty( commentIds )) {
				DocumentCommentInfo documentCommentInfo = null;
				for( String commentId : commentIds ) {
					documentCommentInfo = emc.find( commentId, DocumentCommentInfo.class );
					emc.remove( documentCommentInfo, CheckRemoveType.all );
				}
			}

			List<String>  commendIds = business.documentCommendFactory().listByDocument(id, null, null);
			if( ListTools.isNotEmpty( commendIds )) {
				emc.beginTransaction( DocumentCommend.class );
				emc.delete(DocumentCommend.class, commendIds);
			}
			//删除文档信息
			emc.remove( document, CheckRemoveType.all );
			emc.beginTransaction(DocumentEvent.class);
			DocumentEvent documentEvent = DocumentEvent.deleteEventInstance(document);
			emc.persist(documentEvent);
			emc.commit();

			CacheManager.notify( Document.class );
			CacheManager.notify( DocumentCommend.class );
			CacheManager.notify( DocumentCommentInfo.class );
			CacheManager.notify( Item.class );

			new CmsBatchOperationPersistService().addOperation(
					CmsBatchOperationProcessService.OPT_OBJ_DOCUMENT,
					CmsBatchOperationProcessService.OPT_TYPE_DELETE,  id, id, "文档删除：ID=" + id );

			logService.log( emc, effectivePerson.getDistinguishedName(), document.getCategoryAlias() + ":" + document.getTitle(), document.getAppId(), document.getCategoryId(), document.getId(), "", "DOCUMENT", "删除" );

			Wo wo = new Wo();
			wo.setId( document.getId() );
			result.setData( wo );

			//检查是否需要删除热点图片
			try {
				ThisApplication.queueDocumentDelete.send( id );
			} catch ( Exception e1 ) {
				e1.printStackTrace();
			}

		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return result;
	}

	public static class Wo extends WoId {

	}
}

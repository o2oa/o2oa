package com.x.cms.assemble.control.jaxrs.document;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.cms.assemble.control.Business;
import com.x.cms.core.entity.Document;

public class ActionPersistDraftDocument extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger( ActionPersistDraftDocument.class );
	
	protected ActionResult<Wo> execute( HttpServletRequest request, String id, EffectivePerson effectivePerson ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			
			Document document = business.getDocumentFactory().get( id );
			if ( null == document ) {
				Exception exception = new ExceptionDocumentNotExists( id );
				result.error( exception );
				throw exception;
			}
			try {
				modifyDocStatus( id, "draft", effectivePerson.getDistinguishedName() );
				document.setDocStatus( "draft" );
				ApplicationCache.notify( Document.class );
			} catch ( Exception e ) {
				Exception exception = new ExceptionDocumentInfoProcess( e, "系统将文档状态修改为草稿状态时发生异常。Id:" + id );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
			
			try {
				documentPersistService.refreshDocumentPermission( id, null, null );
			} catch (Exception e) {
				Exception exception = new ExceptionDocumentInfoProcess(e, "系统在发送重置文档访问权限消息到处理队列时发生异常！");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
			
			logService.log( emc, effectivePerson.getDistinguishedName(), document.getCategoryAlias() + ":" + document.getTitle(), document.getAppId(), document.getCategoryId(), document.getId(), "", "DOCUMENT", "设为草稿" );

			Wo wo = new Wo();
			wo.setId( document.getId() );
			result.setData( wo );
			
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return result;
	}

	public static class Wo extends WoId {

	}
}
package com.x.cms.assemble.control.jaxrs.document;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.annotation.AuditLog;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.cms.assemble.control.Business;
import com.x.cms.core.entity.Document;

public class ActionPersistArchive extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger( ActionPersistArchive.class );

	@AuditLog(operation = "文档归档")
	protected ActionResult<Wo> execute( HttpServletRequest request, String id, EffectivePerson effectivePerson ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			Document document = business.getDocumentFactory().get(id);
			if ( null == document ) {
				Exception exception = new ExceptionDocumentNotExists( id );
				result.error( exception );
				throw exception;
			}
			try {
				modifyDocStatus( id, "archived", effectivePerson.getDistinguishedName() );
				document.setDocStatus( "archived" );
				document = documentPersistService.refreshDocInfoData( document );
				CacheManager.notify( Document.class );
			} catch (Exception e) {
				Exception exception = new ExceptionDocumentInfoProcess( e, "系统将文档状态修改为归档状态时发生异常。Id:" + id );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
				throw exception;
			}

			logService.log( emc, effectivePerson.getDistinguishedName(), document.getCategoryAlias() + ":" + document.getTitle(), document.getAppId(), document.getCategoryId(), document.getId(), "", "DOCUMENT", "归档" );

			try {
				documentPersistService.refreshDocumentPermission( id, null, null );
			} catch (Exception e) {
				Exception exception = new ExceptionDocumentInfoProcess(e, "系统在发送重置文档访问权限消息到处理队列时发生异常！");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}

			Wo wo = new Wo();
			wo.setId( document.getId() );
			result.setData( wo );
		} catch (Exception e) {
			Exception exception = new ExceptionDocumentInfoProcess( e, "系统将文档状态修改为归档状态时发生异常。Id:" + id );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
			throw exception;
		}
		return result;
	}

	public static class Wo extends WoId {

	}

}

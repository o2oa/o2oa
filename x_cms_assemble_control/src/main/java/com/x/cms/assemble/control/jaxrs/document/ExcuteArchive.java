package com.x.cms.assemble.control.jaxrs.document;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.cache.ApplicationCache;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.WrapOutId;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.cms.assemble.control.Business;
import com.x.cms.assemble.control.jaxrs.document.exception.DocumentInfoProcessException;
import com.x.cms.assemble.control.jaxrs.document.exception.DocumentNotExistsException;
import com.x.cms.core.entity.Document;

public class ExcuteArchive extends ExcuteBase {

	private Logger logger = LoggerFactory.getLogger( ExcuteArchive.class );
	
	protected ActionResult<WrapOutId> execute( HttpServletRequest request, String id, EffectivePerson effectivePerson ) throws Exception {
		ActionResult<WrapOutId> result = new ActionResult<>();
		WrapOutId wrap = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			Document document = business.getDocumentFactory().get(id);
			if ( null == document ) {
				Exception exception = new DocumentNotExistsException( id );
				result.error( exception );
				//logger.error( e, effectivePerson, request, null);
				throw exception;
			}
			try {
				modifyDocStatus(id, "archived", effectivePerson.getName() );
				document.setDocStatus( "archived" );
				
				ApplicationCache.notify( Document.class );
			} catch (Exception e) {
				Exception exception = new DocumentInfoProcessException( e, "系统将文档状态修改为归档状态时发生异常。Id:" + id );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
				throw exception;
			}
			
			logService.log( emc, effectivePerson.getName(), document.getCategoryAlias() + ":" + document.getTitle(), document.getAppId(), document.getCategoryId(), document.getId(), "", "DOCUMENT", "删除" );
			
			wrap = new WrapOutId( document.getId() );
			result.setData(wrap);
		} catch (Exception e) {
			Exception exception = new DocumentInfoProcessException( e, "系统将文档状态修改为归档状态时发生异常。Id:" + id );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
			throw exception;
		}
		return result;
	}
	
	

}
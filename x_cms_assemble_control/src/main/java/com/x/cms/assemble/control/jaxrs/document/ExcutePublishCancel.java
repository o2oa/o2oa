package com.x.cms.assemble.control.jaxrs.document;

import java.util.ArrayList;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.cache.ApplicationCache;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.WrapOutId;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.cms.core.entity.Document;

public class ExcutePublishCancel extends ExcuteBase {

	private Logger logger = LoggerFactory.getLogger( ExcutePublishCancel.class );
	
	protected ActionResult<WrapOutId> execute( HttpServletRequest request, String id, EffectivePerson effectivePerson ) throws Exception {
		ActionResult<WrapOutId> result = new ActionResult<>();
		Document document = null;
		Boolean check = true;
		
		if( check ){
			try {
				document = documentServiceAdv.get( id );
				if ( null == document ) {
					Exception exception = new DocumentNotExistsException( id );
					result.error( exception );
					logger.error( exception, effectivePerson, request, null);
					throw exception;
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new DocumentViewByIdException( e, id, effectivePerson.getName() );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		
		if( check ){
			try {
				modifyDocStatus( id, "draft", effectivePerson.getName() );
				document.setDocStatus( "draft" );
				document.setPublishTime( new Date() );
				
				ApplicationCache.notify( Document.class );
				
				result.setData(new WrapOutId( document.getId() ));
			} catch (Exception e) {
				Exception exception = new DocumentPuhlishException( e, id );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}			
		}
		
		if( check ){
			try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
				logService.log( emc, effectivePerson.getName(), document.getCategoryAlias() + ":" + document.getTitle(), document.getAppId(), document.getCategoryId(), document.getId(), "", "DOCUMENT", "取消发布" );
			} catch (Throwable th) {
				th.printStackTrace();
				result.error(th);
			}
		}
		
		if( check ){
			try{				
				documentPermissionServiceAdv.refreshDocumentPermission( document, new ArrayList<>() );
			}catch(Exception e){
				check = false;
				Exception exception = new ServiceLogicException( e, "系统在收回已发布文档访问管理权限信息时发生异常！" );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		return result;
	}

}
package com.x.cms.assemble.control.jaxrs.appcategoryadmin;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.cache.ApplicationCache;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.WrapOutId;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.cms.assemble.control.service.LogService;
import com.x.cms.core.entity.AppCategoryAdmin;
import com.x.cms.core.entity.AppInfo;
import com.x.cms.core.entity.CategoryInfo;

public class ExcuteDelete extends ExcuteBase {

	private Logger logger = LoggerFactory.getLogger( ExcuteDelete.class );
	
	protected ActionResult<WrapOutId> execute( HttpServletRequest request, EffectivePerson effectivePerson, String id ) throws Exception {
		ActionResult<WrapOutId> result = new ActionResult<>();
		AppCategoryAdmin appCategoryAdmin = null;
		Boolean check = true;
		if( check ){
			if( id == null || id.isEmpty() ){
				check = false;
				Exception exception = new AppCategoryAdminIdEmptyException();
				result.error( exception );
				logger.error( exception, effectivePerson, request, null);
			}
		}
		if( check ){
			try {
				appCategoryAdmin = appCategoryAdminServiceAdv.get( id );
			} catch (Exception e) {
				check = false;
				Exception exception = new AppCategoryAdminQueryByIdException( e, id );
				result.error( exception );
				logger.error( exception, effectivePerson, request, null);
			}
		}
		if( check ){
			if( appCategoryAdmin == null ){
				check = false;
				Exception exception = new AppCategoryAdminNotExistsException( id );
				result.error( exception );
				logger.error( exception, effectivePerson, request, null);
			}
		}
		if( check ){
			try {
				String description = appCategoryAdmin.getObjectType() + "-" + appCategoryAdmin.getObjectId() + "-" + appCategoryAdmin.getAdminName();
				
				appCategoryAdminServiceAdv.delete( appCategoryAdmin, effectivePerson );
				ApplicationCache.notify( AppInfo.class );
				ApplicationCache.notify( CategoryInfo.class );
				ApplicationCache.notify( AppCategoryAdmin.class );
				result.setData( new WrapOutId(id) );
				
				
				if( "APPINFO".equals( appCategoryAdmin.getObjectType() ) ){
					new LogService().log( null,  effectivePerson.getName(), description, appCategoryAdmin.getObjectId(), "", "", appCategoryAdmin.getId(), "APPCATEGORYADMIN", "删除" );
				}else{
					new LogService().log( null,  effectivePerson.getName(), description, "", appCategoryAdmin.getObjectId(), "", appCategoryAdmin.getId(), "APPCATEGORYADMIN", "删除" );
				}	
				
			} catch (Exception e) {
				check = false;
				Exception exception = new AppCategoryAdminDeleteException( e, id );
				result.error( exception );
				logger.error( exception, effectivePerson, request, null);
			}
		}
		return result;
	}

}
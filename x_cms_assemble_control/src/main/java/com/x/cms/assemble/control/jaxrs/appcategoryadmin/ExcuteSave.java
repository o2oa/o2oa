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

public class ExcuteSave extends ExcuteBase {

	private Logger logger = LoggerFactory.getLogger( ExcuteSave.class );
	
	protected ActionResult<WrapOutId> execute( HttpServletRequest request, EffectivePerson effectivePerson, WrapInAppCategoryAdmin wrapIn) throws Exception {
		ActionResult<WrapOutId> result = new ActionResult<>();
		AppCategoryAdmin appCategoryAdmin = null;
		Boolean check = true;		

		if( check ){
			if( wrapIn.getObjectId() == null || wrapIn.getObjectId().isEmpty() ){
				check = false;
				Exception exception = new AppCategoryAdminObjectIdEmptyException();
				result.error( exception );
				logger.error( exception, effectivePerson, request, null);
			}
		}
		if( check ){
			if( wrapIn.getObjectType() == null || wrapIn.getObjectType().isEmpty() ){
				check = false;
				Exception exception = new AppCategoryAdminObjectIdEmptyException();
				result.error( exception );
				logger.error( exception, effectivePerson, request, null);
			}
		}
		if( check ){
			if( wrapIn.getAdminName() == null || wrapIn.getAdminName().isEmpty() ){
				check = false;
				Exception exception = new AppCategoryAdminNameEmptyException();
				result.error( exception );
				logger.error( exception, effectivePerson, request, null);
			}
		}
		if( check ){
			if( wrapIn.getAdminLevel() == null || wrapIn.getAdminLevel().isEmpty() ){
				check = false;
				Exception exception = new AppCategoryAdminLevelEmptyException();
				result.error( exception );
				logger.error( exception, effectivePerson, request, null);
			}
		}
		if( check ){
			try {
				wrapIn.setCreatorUid( effectivePerson.getName() );
				wrapIn.setDescription( "管理员主动修改管理者" );
				appCategoryAdmin = appCategoryAdminServiceAdv.save( wrapIn, effectivePerson );
				
				String description = appCategoryAdmin.getObjectType() + "-" + appCategoryAdmin.getObjectId() + "-" + appCategoryAdmin.getAdminName();
				if( "APPINFO".equals( appCategoryAdmin.getObjectType() ) ){
					new LogService().log( null,  effectivePerson.getName(), description, appCategoryAdmin.getObjectId(), "", "", appCategoryAdmin.getId(), "APPCATEGORYADMIN", "保存" );
				}else{
					new LogService().log( null,  effectivePerson.getName(), description, "", appCategoryAdmin.getObjectId(), "", appCategoryAdmin.getId(), "APPCATEGORYADMIN", "保存" );
				}
				
				ApplicationCache.notify( AppInfo.class );
				ApplicationCache.notify( CategoryInfo.class );
				ApplicationCache.notify( AppCategoryAdmin.class );
				result.setData( new WrapOutId( appCategoryAdmin.getId() ) );
			} catch (Exception e) {
				check = false;
				Exception exception = new AppCategoryAdminSaveException( e );
				result.error( exception );
				logger.error( exception, effectivePerson, request, null);
			}
		}
		return result;
	}
	
}
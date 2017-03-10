package com.x.cms.assemble.control.jaxrs.appcategorypermission;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.cache.ApplicationCache;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.WrapOutId;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.cms.assemble.control.service.LogService;
import com.x.cms.core.entity.AppCategoryPermission;
import com.x.cms.core.entity.AppInfo;
import com.x.cms.core.entity.CategoryInfo;
import com.x.cms.core.entity.Document;

public class ExcuteSave extends ExcuteBase {

	private Logger logger = LoggerFactory.getLogger( ExcuteSave.class );
	
	protected ActionResult<WrapOutId> execute( HttpServletRequest request, EffectivePerson effectivePerson, WrapInAppCategoryPermission wrapIn) throws Exception {
		ActionResult<WrapOutId> result = new ActionResult<>();
		AppCategoryPermission appCategoryPermission = null;
		AppInfo appInfo = null;
		CategoryInfo categoryInfo = null;
		Boolean check = true;

		if( check ){
			if( wrapIn.getObjectId() == null || wrapIn.getObjectId().isEmpty() ){
				check = false;
				Exception exception = new AppCategoryPermissionObjectIdEmptyException();
				result.error( exception );
				logger.error( exception, effectivePerson, request, null);
			}
		}
		if( check ){
			if( wrapIn.getObjectType() == null || wrapIn.getObjectType().isEmpty() ){
				check = false;
				Exception exception = new AppCategoryPermissionObjectTypeEmptyException();
				result.error( exception );
				logger.error( exception, effectivePerson, request, null);
			}
		}
		if( check ){
			if( wrapIn.getUsedObjectType() == null || wrapIn.getUsedObjectType().isEmpty() ){
				check = false;
				Exception exception = new AppCategoryPermissionUserObjectTypeEmptyException();
				result.error( exception );
				logger.error( exception, effectivePerson, request, null);
			}
		}
		if( check ){
			if( wrapIn.getUsedObjectName() == null || wrapIn.getUsedObjectName().isEmpty() ){
				check = false;
				Exception exception = new AppCategoryPermissionUserObjectNameEmptyException();
				result.error( exception );
				logger.error( exception, effectivePerson, request, null);
			}
		}
		if( check ){
			if( "APPINFO".equals( wrapIn.getObjectType() )){
				try{
					appInfo = appInfoServiceAdv.get( wrapIn.getObjectId() );
					if( appInfo == null ){
						check = false;
						Exception exception = new AppInfoNotExistsException( wrapIn.getObjectId() );
						result.error( exception );
					}else{
						wrapIn.setAppId( appInfo.getId() );
					}
				}catch( Exception e ){
					check = false;
					Exception exception = new AppInfoQueryByIdException( e, wrapIn.getObjectId() );
					result.error( exception );
					logger.error( e, effectivePerson, request, null);
				}
			} else if ( "CATEGORY".equals( wrapIn.getObjectType() )){
				try{
					categoryInfo = categoryInfoServiceAdv.get( wrapIn.getObjectId() );
					if( categoryInfo == null ){
						check = false;
						Exception exception = new CategoryInfoNotExistsException( wrapIn.getObjectId() );
						result.error( exception );
					}else{
						wrapIn.setAppId( categoryInfo.getAppId() );
					}
				}catch( Exception e ){
					check = false;
					Exception exception = new CategoryInfoQueryByIdException( e, wrapIn.getObjectId() );
					result.error( exception );
					logger.error( e, effectivePerson, request, null);
				}
			} else {
				check = false;
				Exception exception = new AppCategoryPermissionUserObjectTypeInvalidException( wrapIn.getObjectType() );
				result.error( exception );
			}
		}
		
		if( check ){
			try {
				wrapIn.setCreatorUid( effectivePerson.getName() );
				appCategoryPermission = appCategoryPermissionServiceAdv.save( wrapIn, effectivePerson );
				
				ApplicationCache.notify( AppInfo.class );
				ApplicationCache.notify( Document.class );
				ApplicationCache.notify( CategoryInfo.class );
				ApplicationCache.notify( AppCategoryPermission.class );
				
				String description = appCategoryPermission.getObjectType() + "-" + appCategoryPermission.getObjectId() + "-" + appCategoryPermission.getUsedObjectName();
				if( "APPINFO".equals( appCategoryPermission.getObjectType() ) ){
					new LogService().log( null,  effectivePerson.getName(), description, appCategoryPermission.getObjectId(), "", "", appCategoryPermission.getId(), "APPCATEGORYPERMISSION", "保存" );
				}else{
					new LogService().log( null,  effectivePerson.getName(), description, "", appCategoryPermission.getObjectId(), "", appCategoryPermission.getId(), "APPCATEGORYPERMISSION", "保存" );
				}
				
				result.setData( new WrapOutId( appCategoryPermission.getId() ) );
			} catch (Exception e) {
				check = false;
				Exception exception = new AppCategoryPermissionSaveException( e );
				result.error( exception );
				logger.error( exception, effectivePerson, request, null);
			}
		}
		return result;
	}
	
}
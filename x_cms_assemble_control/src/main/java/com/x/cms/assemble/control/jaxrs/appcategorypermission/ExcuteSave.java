package com.x.cms.assemble.control.jaxrs.appcategorypermission;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.cache.ApplicationCache;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.WrapOutId;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.cms.assemble.control.jaxrs.appcategorypermission.exception.AppCategoryPermissionObjectIdEmptyException;
import com.x.cms.assemble.control.jaxrs.appcategorypermission.exception.AppCategoryPermissionObjectTypeEmptyException;
import com.x.cms.assemble.control.jaxrs.appcategorypermission.exception.AppCategoryPermissionProcessException;
import com.x.cms.assemble.control.jaxrs.appcategorypermission.exception.AppCategoryPermissionUserObjectNameEmptyException;
import com.x.cms.assemble.control.jaxrs.appcategorypermission.exception.AppCategoryPermissionUserObjectTypeEmptyException;
import com.x.cms.assemble.control.jaxrs.appcategorypermission.exception.AppCategoryPermissionUserObjectTypeInvalidException;
import com.x.cms.assemble.control.jaxrs.appcategorypermission.exception.AppInfoNotExistsException;
import com.x.cms.assemble.control.jaxrs.appcategorypermission.exception.CategoryInfoNotExistsException;
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
			}
		}
		if( check ){
			if( wrapIn.getObjectType() == null || wrapIn.getObjectType().isEmpty() ){
				check = false;
				Exception exception = new AppCategoryPermissionObjectTypeEmptyException();
				result.error( exception );
			}
		}
		if( check ){
			if( wrapIn.getUsedObjectType() == null || wrapIn.getUsedObjectType().isEmpty() ){
				check = false;
				Exception exception = new AppCategoryPermissionUserObjectTypeEmptyException();
				result.error( exception );
			}
		}
		if( check ){
			if( wrapIn.getUsedObjectName() == null || wrapIn.getUsedObjectName().isEmpty() ){
				check = false;
				Exception exception = new AppCategoryPermissionUserObjectNameEmptyException();
				result.error( exception );
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
					Exception exception = new AppCategoryPermissionProcessException( e, "根据指定ID查询应用栏目信息对象时发生异常。ID:" + wrapIn.getObjectId() );
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
					Exception exception = new AppCategoryPermissionProcessException( e, "根据ID查询分类信息对象时发生异常。ID:" + wrapIn.getObjectId() );
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
				Exception exception = new AppCategoryPermissionProcessException( e, "应用栏目分类权限配置信息保存时发生异常。" );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		return result;
	}
	
}
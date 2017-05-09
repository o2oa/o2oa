package com.x.cms.assemble.control.jaxrs.appcategorypermission;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.cache.ApplicationCache;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.WrapOutId;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.cms.assemble.control.jaxrs.appcategorypermission.exception.AppCategoryPermissionIdEmptyException;
import com.x.cms.assemble.control.jaxrs.appcategorypermission.exception.AppCategoryPermissionNotExistsException;
import com.x.cms.assemble.control.jaxrs.appcategorypermission.exception.AppCategoryPermissionProcessException;
import com.x.cms.assemble.control.service.LogService;
import com.x.cms.core.entity.AppCategoryPermission;
import com.x.cms.core.entity.AppInfo;
import com.x.cms.core.entity.CategoryInfo;
import com.x.cms.core.entity.Document;

public class ExcuteDelete extends ExcuteBase {

	private Logger logger = LoggerFactory.getLogger( ExcuteDelete.class );
	
	protected ActionResult<WrapOutId> execute( HttpServletRequest request, EffectivePerson effectivePerson, String id ) throws Exception {
		ActionResult<WrapOutId> result = new ActionResult<>();
		WrapOutId wrap = null;
		AppCategoryPermission appCategoryPermission  = null;
		Boolean check = true;
		
		if( check ){
			if( id == null || id.isEmpty() ){
				check = false;
				Exception exception = new AppCategoryPermissionIdEmptyException();
				result.error( exception );
			}
		}
		if( check ){
			try {
				appCategoryPermission = appCategoryPermissionServiceAdv.get( id );
			} catch (Exception e) {
				check = false;
				Exception exception = new AppCategoryPermissionProcessException( e, "根据ID查询应用栏目分类权限配置信息时发生异常。ID:" + id );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		if( check ){
			if( appCategoryPermission == null ){
				check = false;
				Exception exception = new AppCategoryPermissionNotExistsException( id );
				result.error( exception );
			}
		}
		if( check ){
			try {
				String description = appCategoryPermission.getObjectType() + "-" + appCategoryPermission.getObjectId() + "-" + appCategoryPermission.getUsedObjectName();
				
				appCategoryPermissionServiceAdv.delete( appCategoryPermission, effectivePerson );
				ApplicationCache.notify( AppInfo.class );
				ApplicationCache.notify( CategoryInfo.class );
				ApplicationCache.notify( Document.class );
				ApplicationCache.notify( AppCategoryPermission.class );
				
				if( "APPINFO".equals( appCategoryPermission.getObjectType() ) ){
					new LogService().log( null,  effectivePerson.getName(), description, appCategoryPermission.getObjectId(), "", "", appCategoryPermission.getId(), "APPCATEGORYPERMISSION", "删除" );
				}else{
					new LogService().log( null,  effectivePerson.getName(), description, "", appCategoryPermission.getObjectId(), "", appCategoryPermission.getId(), "APPCATEGORYPERMISSION", "删除" );
				}
				
				wrap = new WrapOutId( id );
				result.setData(wrap);
			} catch (Exception e) {
				check = false;
				Exception exception = new AppCategoryPermissionProcessException( e, "根据ID删除应用栏目分类权限配置信息时发生异常。ID:" + id );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		return result;
	}

}
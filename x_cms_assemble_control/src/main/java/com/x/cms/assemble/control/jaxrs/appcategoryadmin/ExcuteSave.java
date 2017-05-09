package com.x.cms.assemble.control.jaxrs.appcategoryadmin;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.cache.ApplicationCache;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.WrapOutId;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.cms.assemble.control.jaxrs.appcategoryadmin.exception.AppCategoryAdminLevelEmptyException;
import com.x.cms.assemble.control.jaxrs.appcategoryadmin.exception.AppCategoryAdminNameEmptyException;
import com.x.cms.assemble.control.jaxrs.appcategoryadmin.exception.AppCategoryAdminObjectIdEmptyException;
import com.x.cms.assemble.control.jaxrs.appcategoryadmin.exception.AppCategoryAdminObjectTypeNotInvalidException;
import com.x.cms.assemble.control.jaxrs.appcategoryadmin.exception.AppCategoryAdminProcessException;
import com.x.cms.assemble.control.jaxrs.appcategoryadmin.exception.AppInfoNotExistsException;
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
			}
		}
		if( check ){
			if( wrapIn.getObjectType() == null || wrapIn.getObjectType().isEmpty() ){
				check = false;
				Exception exception = new AppCategoryAdminObjectIdEmptyException();
				result.error( exception );
			}else{
				if( !"APPINFO".equals( wrapIn.getObjectType()) && !"CATEGORY".equals( wrapIn.getObjectType()) ){
					check = false;
					Exception exception = new AppCategoryAdminObjectTypeNotInvalidException( wrapIn.getObjectType() );
					result.error( exception );
				}
			}
		}
		if( check ){
			if( wrapIn.getAdminName() == null || wrapIn.getAdminName().isEmpty() ){
				check = false;
				Exception exception = new AppCategoryAdminNameEmptyException();
				result.error( exception );
			}
		}
		if( check ){
			if( wrapIn.getAdminLevel() == null || wrapIn.getAdminLevel().isEmpty() ){
				check = false;
				Exception exception = new AppCategoryAdminLevelEmptyException();
				result.error( exception );
			}
		}
		if( check ){
			try {
				String description = null;
				wrapIn.setCreatorUid( effectivePerson.getName() );
				
				if( "APPINFO".equals( wrapIn.getObjectType() ) ){
					//检查appInfo对象是否存在
					AppInfo appInfo = appInfoServiceAdv.get( wrapIn.getObjectId() );
					if( appInfo != null ){
						wrapIn.setObjectName( appInfo.getAppName() );
					}else{
						check = false;
						Exception exception = new AppInfoNotExistsException( wrapIn.getObjectId() );
						result.error( exception );
					}
				}else if( "CATEGORY".equals( wrapIn.getObjectType() ) ){
					CategoryInfo category = categoryInfoServiceAdv.get( wrapIn.getObjectId() );
					if( category != null ){
						wrapIn.setObjectName( category.getAppName() );
					}else{
						check = false;
						Exception exception = new AppInfoNotExistsException( wrapIn.getObjectId() );
						result.error( exception );
					}
				}
				appCategoryAdmin = appCategoryAdminServiceAdv.save( wrapIn, effectivePerson );
				description = appCategoryAdmin.getObjectType() + "-" + appCategoryAdmin.getObjectId() + "-" + appCategoryAdmin.getAdminName();
				new LogService().log( null,  effectivePerson.getName(), description, appCategoryAdmin.getObjectId(), "", "", appCategoryAdmin.getId(), "APPCATEGORYADMIN", "保存" );
				
				ApplicationCache.notify( AppInfo.class );
				ApplicationCache.notify( CategoryInfo.class );
				ApplicationCache.notify( AppCategoryAdmin.class );
				result.setData( new WrapOutId( appCategoryAdmin.getId() ) );
			} catch (Exception e) {
				check = false;
				Exception exception = new AppCategoryAdminProcessException( e, "应用栏目分类管理员配置信息保存时发生异常。" );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		return result;
	}
	
}
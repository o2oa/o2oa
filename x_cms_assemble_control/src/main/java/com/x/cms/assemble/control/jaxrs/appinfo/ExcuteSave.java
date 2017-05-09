package com.x.cms.assemble.control.jaxrs.appinfo;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.cache.ApplicationCache;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.WrapOutId;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.cms.assemble.control.jaxrs.appinfo.exception.AppInfoNameAlreadyExistsException;
import com.x.cms.assemble.control.jaxrs.appinfo.exception.AppInfoNameEmptyException;
import com.x.cms.assemble.control.jaxrs.appinfo.exception.AppInfoProcessException;
import com.x.cms.assemble.control.service.LogService;
import com.x.cms.core.entity.AppCategoryAdmin;
import com.x.cms.core.entity.AppCategoryPermission;
import com.x.cms.core.entity.AppInfo;
import com.x.cms.core.entity.CategoryInfo;
import com.x.cms.core.entity.Document;
import com.x.cms.core.entity.DocumentPermission;
import com.x.cms.core.entity.element.AppDict;
import com.x.cms.core.entity.element.AppDictItem;
import com.x.cms.core.entity.element.View;
import com.x.cms.core.entity.element.ViewCategory;
import com.x.cms.core.entity.element.ViewFieldConfig;

public class ExcuteSave extends ExcuteBase {

	private Logger logger = LoggerFactory.getLogger( ExcuteSave.class );
	
	protected ActionResult<WrapOutId> execute( HttpServletRequest request, EffectivePerson effectivePerson, WrapInAppInfo wrapIn) throws Exception {
		ActionResult<WrapOutId> result = new ActionResult<>();
		AppInfo appInfo = null;
		List<String> ids = null;
		String identityName = null;
		String departmentName = null;
		String companyName = null;
		Boolean check = true;
		
		if( check ){
			if ( !"xadmin".equalsIgnoreCase( effectivePerson.getName() ) ){
				identityName = wrapIn.getIdentity();
				if( identityName == null || identityName.isEmpty() ){
					try {
						identityName = userManagerService.getFistIdentityNameByPerson( effectivePerson.getName() );
					} catch (Exception e) {
						check = false;
						Exception exception = new AppInfoProcessException( e, "系统在查询用户身份信息时发生异常。Name:" + effectivePerson.getName() );
						result.error( exception );
						logger.error( e, effectivePerson, request, null);
					}
				}
			}else{
				identityName = "xadmin";
				departmentName = "xadmin";
				companyName = "xadmin";
			}
		}
		
		if( check && !"xadmin".equals( identityName ) ){
			try {
				departmentName = userManagerService.getDepartmentNameByIdentity( identityName );
			} catch (Exception e) {
				check = false;
				Exception exception = new AppInfoProcessException( e, "系统在根据用户身份信息查询所属部门名称时发生异常。Identity:" + identityName );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		if( check && !"xadmin".equals( identityName ) ){
			try {
				companyName = userManagerService.getCompanyNameByIdentity( identityName );
			} catch (Exception e) {
				check = false;
				Exception exception = new AppInfoProcessException( e, "系统在根据用户身份信息查询所属公司名称时发生异常。Identity:" + identityName );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		
		if( check ){
			if( wrapIn.getAppName() == null || wrapIn.getAppName().isEmpty() ){
				check = false;
				Exception exception = new AppInfoNameEmptyException();
				result.error( exception );
				//logger.error( e, effectivePerson, request, null);
			}
		}
		if( check ){
			try {
				ids = appInfoServiceAdv.listByAppName( wrapIn.getAppName() );
				if( ids != null && !ids.isEmpty()  ){
					for( String tmp : ids ){
						if( tmp != null && !tmp.trim().equals( wrapIn.getId() )){
							check = false;
							Exception exception = new AppInfoNameAlreadyExistsException( wrapIn.getAppName() );
							result.error( exception );
						}
					}
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new AppInfoProcessException( e, "系统根据应用栏目名称查询应用栏目信息对象时发生异常。AppName:" + wrapIn.getAppName() );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		if( check ){
			wrapIn.setCreatorIdentity( identityName );
			wrapIn.setCreatorPerson( effectivePerson.getName() );
			wrapIn.setCreatorDepartment( departmentName );
			wrapIn.setCreatorCompany( companyName );
			try {
				appInfo = appInfoServiceAdv.save( wrapIn, effectivePerson );
				
				//更新缓存
				ApplicationCache.notify( AppInfo.class );
				ApplicationCache.notify( AppDict.class );
				ApplicationCache.notify( AppDictItem.class );
				ApplicationCache.notify( CategoryInfo.class );
				ApplicationCache.notify( AppCategoryPermission.class );
				ApplicationCache.notify( AppCategoryAdmin.class );
				ApplicationCache.notify( View.class );
				ApplicationCache.notify( ViewCategory.class );
				ApplicationCache.notify( ViewFieldConfig.class );
				ApplicationCache.notify( Document.class );
				ApplicationCache.notify( DocumentPermission.class );
				
				new LogService().log( null, effectivePerson.getName(), appInfo.getAppName(), appInfo.getId(), "", "", "", "APPINFO", "保存");
				
				result.setData( new WrapOutId(appInfo.getId()) );
			} catch (Exception e) {
				check = false;
				Exception exception = new AppInfoProcessException( e, "应用栏目信息保存时发生异常。" );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		return result;
	}
	
}
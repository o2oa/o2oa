package com.x.cms.assemble.control.jaxrs.categoryinfo;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.cache.ApplicationCache;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.WrapOutId;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.cms.assemble.control.jaxrs.categoryinfo.exception.CategoryInfoProcessException;
import com.x.cms.assemble.control.jaxrs.categoryinfo.exception.QueryViewNotExistsException;
import com.x.cms.assemble.control.service.LogService;
import com.x.cms.core.entity.AppCategoryAdmin;
import com.x.cms.core.entity.AppCategoryPermission;
import com.x.cms.core.entity.AppInfo;
import com.x.cms.core.entity.CategoryInfo;
import com.x.cms.core.entity.Document;
import com.x.cms.core.entity.DocumentPermission;
import com.x.cms.core.entity.element.QueryView;
import com.x.cms.core.entity.element.ViewCategory;

public class ExcuteSave extends ExcuteBase {

	private Logger logger = LoggerFactory.getLogger( ExcuteSave.class );
	
	protected ActionResult<WrapOutId> execute( HttpServletRequest request, EffectivePerson effectivePerson, WrapInCategoryInfo wrapIn) throws Exception {
		ActionResult<WrapOutId> result = new ActionResult<>();
		WrapOutId wrap = null;
		String identityName = null;
		String departmentName = null;
		String companyName = null;
		CategoryInfo categoryInfo = null;
		Boolean check = true;
		
		if( check ){
			if ( !"xadmin".equalsIgnoreCase( effectivePerson.getName()) ){
				identityName = wrapIn.getIdentity();
				if( identityName == null || identityName.isEmpty() ){
					try {
						identityName = userManagerService.getFistIdentityNameByPerson( effectivePerson.getName() );
					} catch (Exception e) {
						check = false;
						Exception exception = new CategoryInfoProcessException( e, "系统在查询用户身份信息时发生异常。Name:" + effectivePerson.getName() );
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
				Exception exception = new CategoryInfoProcessException( e, "系统在根据用户身份信息查询所属部门名称时发生异常。Identity:" + identityName );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		if( check && !"xadmin".equals( identityName ) ){
			try {
				companyName = userManagerService.getCompanyNameByIdentity( identityName );
			} catch (Exception e) {
				check = false;
				Exception exception = new CategoryInfoProcessException( e, "系统在根据用户身份信息查询所属公司名称时发生异常。Identity:" + identityName );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		if( check ){
			if( wrapIn.getDefaultViewId() != null && !wrapIn.getDefaultViewId().isEmpty() ){
				try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
					QueryView queryView = emc.find( wrapIn.getDefaultViewId(), QueryView.class );
					if( queryView != null ){
						wrapIn.setDefaultViewName( queryView.getName() );
					}else{
						check = false;
						Exception exception = new QueryViewNotExistsException( wrapIn.getDefaultViewId() );
						result.error( exception );
					}
				}
			}
		}
		if( check ){
			wrapIn.setCreatorIdentity( identityName );
			wrapIn.setCreatorPerson( effectivePerson.getName() );
			wrapIn.setCreatorDepartment( departmentName );
			wrapIn.setCreatorCompany( companyName );
			try {
				categoryInfo = categoryInfoServiceAdv.save( wrapIn, effectivePerson );
				wrap = new WrapOutId( categoryInfo.getId() );
				
				ApplicationCache.notify( AppInfo.class );
				ApplicationCache.notify( CategoryInfo.class );
				ApplicationCache.notify( ViewCategory.class );
				ApplicationCache.notify( AppCategoryPermission.class );
				ApplicationCache.notify( AppCategoryAdmin.class );
				ApplicationCache.notify( Document.class );
				ApplicationCache.notify( DocumentPermission.class );
				
				new LogService().log( null, effectivePerson.getName(), categoryInfo.getAppName() + "-" + categoryInfo.getCategoryName(), categoryInfo.getId(), "", "", "", "CATEGORY", "新增");
				
				result.setData(wrap);
			} catch ( Exception e ) {
				check = false;
				Exception exception = new CategoryInfoProcessException( e, "分类信息在保存时发生异常." );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		return result;
	}
	
}
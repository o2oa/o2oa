package com.x.cms.assemble.control.jaxrs.permission;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.cms.core.entity.CategoryInfo;

public class ActionIsCategoryInfoManager extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger( ActionIsCategoryInfoManager.class );
	
	protected ActionResult<Wo> execute( HttpServletRequest request, EffectivePerson effectivePerson, String categoryId ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		CategoryInfo categoryInfo = null;
		Boolean check = true;
		Wo wo = new Wo();
		wo.setValue( false );
		String personName = effectivePerson.getDistinguishedName(); 
		Boolean isXAdmin = false;
		
		try {
			isXAdmin = userManagerService.isManager( effectivePerson );
		} catch (Exception e) {
			check = false;
			Exception exception = new ExceptionServiceLogic(e, "系统在检查用户是否是平台管理员时发生异常。Name:" + personName);
			result.error(exception);
			logger.error(e, effectivePerson, request, null);
		}
		
		if( check ){
			try {
				categoryInfo = categoryInfoServiceAdv.get( categoryId );
				if( categoryInfo == null ){
					check = false;
					Exception exception = new ExceptionCategoryInfoNotExists( categoryId );
					result.error( exception );
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionAppInfoQueryById( e, categoryId );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		
		if( check ){
			if( isXAdmin ) {
				wo.setValue( true );
			}else {
				List<String> unitNames = userManagerService.listUnitNamesWithPerson( personName );
				List<String> groupNames = userManagerService.listGroupNamesByPerson( personName );
				if( ListTools.isNotEmpty( categoryInfo.getManageablePersonList() ) ) {
					if( categoryInfo.getManageablePersonList().contains( personName )) {
						wo.setValue( true );
					}
				}
				if( ListTools.isNotEmpty( categoryInfo.getManageableUnitList() ) ) {
					categoryInfo.getManageableUnitList().retainAll( unitNames );
					if( ListTools.isNotEmpty( categoryInfo.getManageableUnitList() )) {
						wo.setValue( true );
					}
				}
				if( ListTools.isNotEmpty( categoryInfo.getManageableGroupList() ) ) {
					categoryInfo.getManageableGroupList().retainAll( groupNames );
					if( ListTools.isNotEmpty( categoryInfo.getManageableGroupList() )) {
						wo.setValue( true );
					}
				}
			}
			result.setData( wo );
		}
		return result;
	}

	public static class Wo extends WrapBoolean{

	}
}
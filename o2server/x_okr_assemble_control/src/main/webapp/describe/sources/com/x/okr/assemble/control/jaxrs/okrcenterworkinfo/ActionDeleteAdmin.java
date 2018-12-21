package com.x.okr.assemble.control.jaxrs.okrcenterworkinfo;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.okr.assemble.control.ThisApplication;
import com.x.okr.assemble.control.jaxrs.okrcenterworkinfo.exception.ExceptionCenterWorkDelete;
import com.x.okr.assemble.control.jaxrs.okrcenterworkinfo.exception.ExceptionCenterWorkIdEmpty;
import com.x.okr.assemble.control.jaxrs.okrcenterworkinfo.exception.ExceptionCenterWorkQueryById;
import com.x.okr.assemble.control.jaxrs.okrcenterworkinfo.exception.ExceptionInsufficientPermissions;
import com.x.okr.assemble.control.jaxrs.okrcenterworkinfo.exception.ExceptionOkrOperationDynamicSave;
import com.x.okr.assemble.control.jaxrs.okrcenterworkinfo.exception.ExceptionOkrSystemAdminCheck;
import com.x.okr.assemble.control.jaxrs.queue.WrapInWorkDynamic;
import com.x.okr.entity.OkrCenterWorkInfo;

public class ActionDeleteAdmin extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger( ActionDeleteAdmin.class );
	
	protected ActionResult<Wo> execute( HttpServletRequest request, EffectivePerson effectivePerson, String id ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		OkrCenterWorkInfo okrCenterWorkInfo = null;
		Boolean check = true;
		EffectivePerson currentPerson = this.effectivePerson(request);
		try {
			if ( !okrUserInfoService.getIsOkrManager( currentPerson.getDistinguishedName())) {
				check = false;
				Exception exception = new ExceptionInsufficientPermissions( currentPerson.getDistinguishedName(), ThisApplication.OKRMANAGER);
				result.error(exception);
			}
		} catch (Exception e) {
			check = false;
			Exception exception = new ExceptionOkrSystemAdminCheck(e, currentPerson.getDistinguishedName());
			result.error(exception);
			logger.error(e, currentPerson, request, null);
		}
		if (check) {
			if (id == null || id.isEmpty()) {
				check = false;
				Exception exception = new ExceptionCenterWorkIdEmpty();
				result.error(exception);
			}
		}
		if (check) {
			try {
				okrCenterWorkInfo = okrCenterWorkQueryService.get(id);
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionCenterWorkQueryById(e, id);
				result.error(exception);
				logger.error(e, currentPerson, request, null);
			}
		}
		if (check) {
			try {
				okrCenterWorkOperationService.delete(id);
				
				Wo wo = new Wo();
				wo.setId( id );
				result.setData( wo );
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionCenterWorkDelete(e, id);
				result.error(exception);
				logger.error(e, currentPerson, request, null);
			}
		}
		if (check) {
			try {
				if( okrCenterWorkInfo != null ){
					WrapInWorkDynamic.sendWithCenterWorkInfo( 
							okrCenterWorkInfo, 
							effectivePerson.getDistinguishedName(),
							"管理员操作",
							"管理员操作",
							"删除中心工作",
							"中心工作删除成功！"
					);
				}
			} catch (Exception e) {
				Exception exception = new ExceptionOkrOperationDynamicSave(e, id);
				result.error(exception);
				logger.error(e, currentPerson, request, null);
			}
		}
		return result;
	}
	
	public static class Wo extends WoId {

	}
}
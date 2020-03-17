package com.x.okr.assemble.control.jaxrs.okrcenterworkinfo;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.okr.assemble.control.ThisApplication;
import com.x.okr.assemble.control.jaxrs.okrcenterworkinfo.exception.ExceptionCenterWorkIdEmpty;
import com.x.okr.assemble.control.jaxrs.okrcenterworkinfo.exception.ExceptionCenterWorkNotExists;
import com.x.okr.assemble.control.jaxrs.okrcenterworkinfo.exception.ExceptionCenterWorkQueryById;
import com.x.okr.assemble.control.jaxrs.okrcenterworkinfo.exception.ExceptionInsufficientPermissions;
import com.x.okr.assemble.control.jaxrs.okrcenterworkinfo.exception.ExceptionOkrSystemAdminCheck;
import com.x.okr.entity.OkrCenterWorkInfo;

public class ActionGetAdmin extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger( ActionGetAdmin.class );
	
	protected ActionResult<Wo> execute( HttpServletRequest request, EffectivePerson effectivePerson, String id ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wo wrap = null;
		OkrCenterWorkInfo OkrCenterWorkInfo = null;
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
		try {
			OkrCenterWorkInfo = okrCenterWorkQueryService.get(id);
			if (OkrCenterWorkInfo != null) {
				wrap = Wo.copier.copy( OkrCenterWorkInfo );
				result.setData(wrap);
			} else {
				Exception exception = new ExceptionCenterWorkNotExists(id);
				result.error(exception);
			}
		} catch (Exception e) {
			Exception exception = new ExceptionCenterWorkQueryById(e, id);
			result.error(exception);
			logger.error(e, currentPerson, request, null);
		}
		return result;
	}
	
}
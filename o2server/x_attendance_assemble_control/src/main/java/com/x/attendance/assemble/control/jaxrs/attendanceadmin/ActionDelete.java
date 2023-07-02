package com.x.attendance.assemble.control.jaxrs.attendanceadmin;

import com.x.attendance.assemble.control.Business;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

import javax.servlet.http.HttpServletRequest;

public class ActionDelete extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger( ActionDelete.class );

	protected ActionResult<Wo> execute( HttpServletRequest request, EffectivePerson effectivePerson, String id ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();

		Business business = new Business(null);
		if(!business.isManager(effectivePerson)){
			throw new ExceptionAccessDenied(effectivePerson);
		}

		attendanceAdminServiceAdv.delete(id);
		result.setData(new Wo(id));
		return result;
	}

	public static class Wo extends WoId {
		public Wo( String id ) {
			setId( id );
		}
	}
}

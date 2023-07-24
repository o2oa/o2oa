package com.x.calendar.assemble.control.jaxrs.setting;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.http.WrapOutBoolean;

public class ActionIsManager extends BaseAction {
	
	protected ActionResult<WrapOutBoolean> execute( HttpServletRequest request, EffectivePerson effectivePerson ) throws Exception {
		ActionResult<WrapOutBoolean> result = new ActionResult<>();
		Wo wo = new Wo();
		Boolean hasRole = false;
		
		if(effectivePerson.isManager()) {
			hasRole = true;
		}else {
			try {
				hasRole = userManagerService.isHasPlatformRole(effectivePerson.getDistinguishedName(), "CalendarManager");
			}catch( Exception e) {
				e.printStackTrace();
			}
		}
		
		wo.setValue( hasRole );
		result.setData(wo);
		return result;
	}

	public static class Wo extends WrapOutBoolean  {
	}

}
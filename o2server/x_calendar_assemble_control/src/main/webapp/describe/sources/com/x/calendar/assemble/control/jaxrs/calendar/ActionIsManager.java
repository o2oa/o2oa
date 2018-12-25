package com.x.calendar.assemble.control.jaxrs.calendar;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.http.WrapOutBoolean;

public class ActionIsManager extends BaseAction {
	
	protected ActionResult<WrapOutBoolean> execute( HttpServletRequest request, EffectivePerson effectivePerson ) throws Exception {
		ActionResult<WrapOutBoolean> result = new ActionResult<>();
		Wo wo = new Wo();
		Boolean manager = false;
		Boolean check = true;
		
		if( check ) {
			if(effectivePerson.isManager()) {
				manager = true;
			}else {
				try {
					manager = userManagerService.isManager( request, effectivePerson );
				}catch( Exception e) {
					e.printStackTrace();
				}
			}
		}		
		wo.setValue( manager );
		result.setData(wo);
		return result;
	}

	public static class Wo extends WrapOutBoolean  {
	}

}
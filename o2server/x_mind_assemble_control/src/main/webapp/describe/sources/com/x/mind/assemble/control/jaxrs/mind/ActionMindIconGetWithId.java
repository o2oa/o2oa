package com.x.mind.assemble.control.jaxrs.mind;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapString;
import com.x.mind.assemble.control.jaxrs.exception.ExceptionMindIconNotExists;

public class ActionMindIconGetWithId extends BaseAction {
	
	protected ActionResult<Wo> execute( HttpServletRequest request, EffectivePerson effectivePerson, String mindId ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = null;
		String iconBase64Content = mindInfoService.getMindIconBase64( mindId );

		if ( null == iconBase64Content ) {
			Exception exception = new ExceptionMindIconNotExists( mindId );
			result.error( exception );
		}else {
			if( StringUtils.isNotEmpty( iconBase64Content )) {
				wo = new Wo();
				wo.setValue(iconBase64Content);
				result.setData(wo);
			}
		}
		return result;
	}
	
	public static class Wo extends WrapString{
	}
}

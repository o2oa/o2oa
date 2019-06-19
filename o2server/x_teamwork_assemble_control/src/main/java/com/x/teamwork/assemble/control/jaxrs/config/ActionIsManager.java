package com.x.teamwork.assemble.control.jaxrs.config;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.http.WrapOutBoolean;
import com.x.teamwork.core.entity.SystemConfig;

public class ActionIsManager extends BaseAction {
	
	protected ActionResult<WrapOutBoolean> execute( HttpServletRequest request, EffectivePerson effectivePerson ) throws Exception {
		ActionResult<WrapOutBoolean> result = new ActionResult<>();
		WrapOutBoolean wo = new WrapOutBoolean();
		Boolean hasRole = false;
		
		if(effectivePerson.isManager()) {
			hasRole = true;
		}else {
			try {
				hasRole = userManagerService.isHasPlatformRole(effectivePerson.getDistinguishedName(), "TeamWorkManager");
			}catch( Exception e) {
				e.printStackTrace();
			}
		}
		
		wo.setValue( hasRole );
		result.setData(wo);
		return result;
	}

	public static class Wo extends SystemConfig  {
		
		private static final long serialVersionUID = -5076990764713538973L;
		
		public static List<String> Excludes = new ArrayList<String>();
		
		public static WrapCopier<SystemConfig, Wo> copier = WrapCopierFactory.wo( SystemConfig.class, Wo.class, null,Wo.Excludes);
	}

}
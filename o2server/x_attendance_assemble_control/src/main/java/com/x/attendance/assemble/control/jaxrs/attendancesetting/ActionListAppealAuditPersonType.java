package com.x.attendance.assemble.control.jaxrs.attendancesetting;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.x.attendance.entity.AppealConfig;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;

public class ActionListAppealAuditPersonType extends BaseAction {
	
	protected ActionResult<List<String>> execute( HttpServletRequest request, EffectivePerson effectivePerson ) throws Exception {
		ActionResult<List<String>> result = new ActionResult<>();
		List<String> types = new ArrayList<>();
		
		types.add( AppealConfig.APPEAL_AUDITTYPE_PERSON );
		types.add( AppealConfig.APPEAL_AUDITTYPE_PERSONATTRIBUTE );
		types.add( AppealConfig.APPEAL_AUDITTYPE_REPORTLEADER );
		types.add( AppealConfig.APPEAL_AUDITTYPE_UNITDUTY );
		
		result.setCount( Long.parseLong( 4 + "" ));
		result.setData( types );
		
		return result;
	}


}
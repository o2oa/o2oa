package com.x.report.assemble.control.jaxrs.workprog;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.report.assemble.control.jaxrs.workplan.exception.ExceptionParameterInvalid;
import com.x.report.assemble.control.jaxrs.workprog.exception.ExceptionDeleteWorkProg;
import com.x.report.assemble.control.jaxrs.workprog.exception.ExceptionQueryWorkProgWithReportId;
import com.x.report.assemble.control.jaxrs.workprog.exception.ExceptionWorkProgNotExists;
import com.x.report.core.entity.Report_C_WorkProg;

public class ActionDeleteWorkProg extends BaseAction {

	private Logger logger = LoggerFactory.getLogger( ActionDeleteWorkProg.class );
	
	protected ActionResult<Wo> execute( HttpServletRequest request, EffectivePerson effectivePerson, String id ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Report_C_WorkProg workPlan = null;
		Boolean check = true;
		if( id == null || id.isEmpty() ){
			check = false;
			Exception exception = new ExceptionParameterInvalid( "参数'id'不允许为空！" );
			result.error( exception );
		}
		
		if( check ){
			try {
				workPlan = report_C_WorkProgServiceAdv.get( id );
				if( workPlan == null ){
					check = false;
					Exception exception = new ExceptionWorkProgNotExists( id );
					result.error( exception );
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionQueryWorkProgWithReportId( e, id );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}

		if( check ){
			try {
				report_C_WorkProgServiceAdv.delete( id, effectivePerson );
				Wo wo = new Wo();
				wo.setId( id );
				result.setData( wo );
			} catch (Exception e) {
				Exception exception = new ExceptionDeleteWorkProg( e, id );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		return result;
	}
	
	public static class Wo extends WoId {
	}

}
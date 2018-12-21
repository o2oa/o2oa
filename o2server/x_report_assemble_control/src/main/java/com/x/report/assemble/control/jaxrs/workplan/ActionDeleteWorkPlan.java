package com.x.report.assemble.control.jaxrs.workplan;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.report.assemble.control.jaxrs.workplan.exception.ExceptionDeleteWorkPlan;
import com.x.report.assemble.control.jaxrs.workplan.exception.ExceptionParameterInvalid;
import com.x.report.assemble.control.jaxrs.workplan.exception.ExceptionQueryWorkPlanWithReportId;
import com.x.report.assemble.control.jaxrs.workplan.exception.ExceptionWorkPlanNotExists;
import com.x.report.core.entity.Report_C_WorkPlan;

public class ActionDeleteWorkPlan extends BaseAction {

	private Logger logger = LoggerFactory.getLogger( ActionDeleteWorkPlan.class );
	
	protected ActionResult<Wo> execute( HttpServletRequest request, EffectivePerson effectivePerson, String id ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Report_C_WorkPlan workPlan = null;
		Boolean check = true;
		if( id == null || id.isEmpty() ){
			check = false;
			Exception exception = new ExceptionParameterInvalid( "参数'id'不允许为空！" );
			result.error( exception );
		}
		
		if( check ){
			try {
				workPlan = report_C_WorkPlanServiceAdv.get( id );
				if( workPlan == null ){
					check = false;
					Exception exception = new ExceptionWorkPlanNotExists( id );
					result.error( exception );
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionQueryWorkPlanWithReportId( e, id );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}

		if( check ){
			try {
				report_C_WorkPlanServiceAdv.delete( id, effectivePerson );
				Wo wo = new Wo();
				wo.setId( id );
				result.setData( wo );
			} catch (Exception e) {
				Exception exception = new ExceptionDeleteWorkPlan( e, id );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		return result;
	}
	
	public static class Wo extends WoId {
	}

}
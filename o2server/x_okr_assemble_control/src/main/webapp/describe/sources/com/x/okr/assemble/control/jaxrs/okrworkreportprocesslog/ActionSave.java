package com.x.okr.assemble.control.jaxrs.okrworkreportprocesslog;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.google.gson.JsonElement;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.okr.assemble.control.jaxrs.okrworkreportdetailinfo.exception.ExceptionWrapInConvert;
import com.x.okr.assemble.control.jaxrs.okrworkreportprocesslog.exception.ExceptionReportProcessLogSave;
import com.x.okr.entity.OkrWorkReportProcessLog;

public class ActionSave extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger( ActionSave.class );

	protected ActionResult<Wo> execute( HttpServletRequest request,EffectivePerson effectivePerson, JsonElement jsonElement ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		OkrWorkReportProcessLog okrWorkReportProcessLog = null;
		WrapInOkrWorkReportProcessLog wrapIn = null;
		Boolean check = true;
		try {
			wrapIn = this.convertToWrapIn( jsonElement, WrapInOkrWorkReportProcessLog.class );
		} catch (Exception e ) {
			check = false;
			Exception exception = new ExceptionWrapInConvert( e, jsonElement );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}
		if( check ){
			try {
				okrWorkReportProcessLog = okrWorkReportProcessLogService.save( wrapIn );
				result.setData( new Wo( okrWorkReportProcessLog.getId() ) );
			} catch (Exception e) {
				Exception exception = new ExceptionReportProcessLogSave( e );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		return result;
	}
	
	public static class WrapInOkrWorkReportProcessLog extends OkrWorkReportProcessLog {

		private static final long serialVersionUID = -5076990764713538973L;

		public static List<String> Excludes = new ArrayList<String>(JpaObject.FieldsUnmodify);

	}

	
	public static class Wo extends WoId {
		public Wo( String id ) {
			this.setId( id );
		}
	}
}
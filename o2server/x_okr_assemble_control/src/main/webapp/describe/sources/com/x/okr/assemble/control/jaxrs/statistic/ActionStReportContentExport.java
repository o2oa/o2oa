package com.x.okr.assemble.control.jaxrs.statistic;

import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.google.gson.JsonElement;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.okr.assemble.common.excel.writer.WorkReportContentExportExcelWriter;
import com.x.okr.assemble.control.jaxrs.statistic.exception.ExceptionWrapInConvert;

public class ActionStReportContentExport extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger(ActionStReportContentExport.class);

	protected ActionResult<Wo> execute( HttpServletRequest request, EffectivePerson effectivePerson, JsonElement jsonElement ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		String centerId = null;
		String flag = null;
		List<WoOkrStatisticReportContentCenter> exportDataList = null;
		Wi wrapIn = null;
		Boolean check = true;
		try {
			wrapIn = this.convertToWrapIn( jsonElement, Wi.class );
		} catch (Exception e ) {
			check = false;
			Exception exception = new ExceptionWrapInConvert( e, jsonElement );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}

		if( check ){
			if( wrapIn == null ){
				wrapIn = new Wi();
				wrapIn.setCenterId("none");
			}
		}
				
		if (check) {
			try {
				request.setCharacterEncoding("UTF-8");
			} catch (UnsupportedEncodingException e ) {
				logger.warn( "system set character encoding for request got an exception. " );
				logger.error( e );
			}
		}
		if (check) {
			if ("all".equals( centerId )) {
				wrapIn.setCenterId( null );
			}
		}
		try {
			exportDataList = new ActionFilterExport().execute( request, effectivePerson, wrapIn.getReportCycle(), wrapIn.getCenterId(), wrapIn.getCenterTitle(), wrapIn.getWorkTypeName(), wrapIn.getStatisticTimeFlag(), wrapIn.getStatus()  );
		} catch (Exception e ) {
			logger.warn( "system query data for export got an exception. " );
			logger.error( e );
		}
		if ( check ) {
			try {
				flag = new WorkReportContentExportExcelWriter().writeExcel( exportDataList );
				result.setData( new Wo(flag) );
			} catch ( Exception e ) {
				logger.warn( "system write export data to excel file got an exception. " );
				logger.error( e );
			}
		}
		return result;
	}

	public static class Wi extends WrapInFilterOkrStatisticReportContent {
	}
	
	public static class Wo extends WoId {
		public Wo( String id ) {
			this.setId( id );
		}
	}
}
package com.x.okr.assemble.control.jaxrs.statistic;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.google.gson.JsonElement;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.okr.assemble.common.date.MonthOfYear;
import com.x.okr.assemble.common.date.WeekOfYear;
import com.x.okr.assemble.control.jaxrs.statistic.exception.ExceptionQueryEndDateEmpty;
import com.x.okr.assemble.control.jaxrs.statistic.exception.ExceptionQueryEndDateInvalid;
import com.x.okr.assemble.control.jaxrs.statistic.exception.ExceptionQueryStartDateEmpty;
import com.x.okr.assemble.control.jaxrs.statistic.exception.ExceptionQueryStartDateInvalid;
import com.x.okr.assemble.control.jaxrs.statistic.exception.ExceptionQueryWithCondition;
import com.x.okr.assemble.control.jaxrs.statistic.exception.ExceptionReportStatisitcWrapOut;
import com.x.okr.assemble.control.jaxrs.statistic.exception.ExceptionWrapInConvert;
import com.x.okr.entity.OkrStatisticReportStatus;

public class ActionStReportStatusListByFilter extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger(ActionStReportStatusListByFilter.class);

	protected ActionResult<WoOkrStatisticReportStatusTable> execute( HttpServletRequest request, EffectivePerson effectivePerson, JsonElement jsonElement ) throws Exception {
		ActionResult<WoOkrStatisticReportStatusTable> result = new ActionResult<>();
		List<WoOkrStatisticReportStatus> wrapOutOkrReportSubmitStatusStatisticList = null;
		List<OkrStatisticReportStatus> okrReportStatusStatisticList = null;
		List<WoOkrStatisticReportStatusEntity> unitNameLayer = null;
		List<WoOkrStatisticReportStatusHeader> headers = null;
		List<WeekOfYear> weeks = null;
		List<MonthOfYear> months = null;
		Wi wrapIn = null;
		WoOkrStatisticReportStatusTable wrapOutOkrReportSubmitStatusTable = new WoOkrStatisticReportStatusTable();
		Date startDate = null;
		Date endDate = null;
		String workTitle = null;
		String startDateString = null;
		String endDateString = null;
		String workType = null;
		String cycleType = "每周汇报";
		String centerId = null;
		String workId = null;
		String unitName = null;
		String status = null;
		
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
			startDateString = wrapIn.getStartDate();
			if( startDateString != null ){
				try {
					startDate = dateOperation.getDateFromString( startDateString );
				} catch (Exception e) {
					check = false;
					Exception exception = new ExceptionQueryStartDateInvalid( e, startDateString );
					result.error( exception );
					logger.error( e, effectivePerson, request, null);
				}
			}else{
				check = false;
				Exception exception = new ExceptionQueryStartDateEmpty();
				result.error( exception );
			}
			endDateString = wrapIn.getEndDate();
			if( endDateString != null ){
				try {
					endDate = dateOperation.getDateFromString( endDateString );
				} catch (Exception e) {
					check = false;
					Exception exception = new ExceptionQueryEndDateInvalid( e, endDateString );
					result.error( exception );
					logger.error( e, effectivePerson, request, null);
				}
			}else{
				check = false;
				Exception exception = new ExceptionQueryEndDateEmpty();
				result.error( exception );
				//logger.error( e, effectivePerson, request, null);
			}
		}
		if( check ){
			//查询过滤条件
			workTitle = wrapIn.getCenterTitle();
			workType = wrapIn.getWorkTypeName();
			cycleType = wrapIn.getCycleType();
			centerId = wrapIn.getCenterId();
			workId = wrapIn.getWorkId();
			unitName = wrapIn.getUnitName();
			status = wrapIn.getStatus();
		}
		if( check ){
			try {
				okrReportStatusStatisticList = okrReportStatusStatisticService.list( centerId, workTitle, workId, workType, unitName, cycleType, status );
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionQueryWithCondition( e );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		if( check ){
			if( okrReportStatusStatisticList != null ){
				try {
					wrapOutOkrReportSubmitStatusStatisticList = wrapout_copier.copy( okrReportStatusStatisticList );
				} catch (Exception e) {
					check = false;
					Exception exception = new ExceptionReportStatisitcWrapOut( e );
					result.error( exception );
					logger.error( e, effectivePerson, request, null);
				}
			}
		}
		if( "每月汇报".equals( cycleType ) ){
			months = dateOperation.getMonthsOfYear( startDate, endDate );
			headers = getHeaderForUnitMonthStatistic( months );
			unitNameLayer = getMonthFirstLayerArray( wrapOutOkrReportSubmitStatusStatisticList, months, startDate, endDate );
		}else{
			weeks = dateOperation.getWeeksOfYear( startDate, endDate );
			headers = getHeaderForUnitWeekStatistic( weeks );
			unitNameLayer = getWeekFirstLayerArray( wrapOutOkrReportSubmitStatusStatisticList, weeks, startDate, endDate );
		}
		wrapOutOkrReportSubmitStatusTable.setHeader(headers);
		wrapOutOkrReportSubmitStatusTable.setContent( unitNameLayer );
		result.setData( wrapOutOkrReportSubmitStatusTable );
		return result;
	}

	public static class Wi extends WrapInFilterOkrStatisticReportStatus{
	}
}
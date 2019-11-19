package com.x.okr.assemble.control.jaxrs.statistic;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.SortTools;
import com.x.okr.assemble.common.date.DateOperation;
import com.x.okr.assemble.control.jaxrs.statistic.exception.ExceptionQueryEndDateInvalid;
import com.x.okr.assemble.control.jaxrs.statistic.exception.ExceptionQueryStartDateInvalid;
import com.x.okr.assemble.control.jaxrs.statistic.exception.ExceptionQueryWithCondition;
import org.apache.commons.lang3.StringUtils;

public class ActionDateList extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger(ActionDateList.class);

	protected ActionResult<List<WoOkrReportSubmitStatusDate>> execute( HttpServletRequest request, EffectivePerson effectivePerson, WrapInFilterOkrStatisticReportContent wrapIn) throws Exception {
		ActionResult<List<WoOkrReportSubmitStatusDate>> result = new ActionResult<>();
		List<WoOkrReportSubmitStatusDate> result_datetimes = new ArrayList<>();
		List<String> datetimes = null;
		Integer year = null;
		Integer month = null;
		Integer week = null;
		String reportCycle = null;
		String centerId = null;
		String order = "DESC";
		Date startDate = null;
		Date endDate = null;
		Boolean check = true;
		DateOperation dateOperation = new DateOperation();

		if (check) {
			order = wrapIn.getOrder();
			centerId = wrapIn.getCenterId();
			year = wrapIn.getYear();
			month = wrapIn.getMonth();
			week = wrapIn.getWeek();
			reportCycle = wrapIn.getReportCycle();
		}
		if (check) {
			if ( StringUtils.isNotEmpty( wrapIn.getStartDate() )) {
				try {
					startDate = dateOperation.getDateFromString( wrapIn.getStartDate() );
				} catch (Exception e) {
					check = false;
					Exception exception = new ExceptionQueryStartDateInvalid( e, wrapIn.getStartDate() );
					result.error( exception );
					logger.error( e, effectivePerson, request, null);
				}
			}
			if ( StringUtils.isNotEmpty( wrapIn.getEndDate())) {
				try {
					endDate = dateOperation.getDateFromString( wrapIn.getEndDate() );
				} catch (Exception e) {
					check = false;
					Exception exception = new ExceptionQueryEndDateInvalid( e, wrapIn.getEndDate() );
					result.error( exception );
					logger.error( e, effectivePerson, request, null);
				}
			}
		}
		if (check) {
			try {
				if( StringUtils.isNotEmpty(reportCycle) ){
					datetimes = okrCenterWorkReportStatisticService.listDateTimeFlags( centerId, wrapIn.getCenterTitle(), null, wrapIn.getWorkTypeName(), "每周汇报", year, month, week, startDate, endDate, wrapIn.getStatus() );
					if( datetimes != null && !datetimes.isEmpty() ){
						for( String datetime : datetimes){
							result_datetimes.add( new WoOkrReportSubmitStatusDate( datetime, "每周汇报"));
						}
					}
					datetimes = okrCenterWorkReportStatisticService.listDateTimeFlags( centerId, wrapIn.getCenterTitle(), null, wrapIn.getWorkTypeName(), "每月汇报", year, month, week, startDate, endDate, wrapIn.getStatus() );
					if( datetimes != null && !datetimes.isEmpty() ){
						for( String datetime : datetimes){
							result_datetimes.add( new WoOkrReportSubmitStatusDate( datetime, "每月汇报"));
						}
					}
				}else{
					datetimes = okrCenterWorkReportStatisticService.listDateTimeFlags( centerId, wrapIn.getCenterTitle(), null, wrapIn.getWorkTypeName(), reportCycle, year, month, week, startDate, endDate, wrapIn.getStatus()  );
					if( datetimes != null && !datetimes.isEmpty() ){
						for( String datetime : datetimes){
							result_datetimes.add( new WoOkrReportSubmitStatusDate( datetime, reportCycle ));
						}
					}
				}
				if ( result_datetimes != null) {
					if ("DESC".equals( order.toUpperCase()) ) {
						SortTools.desc(result_datetimes, "datetime");
					} else {
						SortTools.asc(result_datetimes, "datetime");
					}
				}
				result.setData( result_datetimes );
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionQueryWithCondition( e );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		return result;
	}

}
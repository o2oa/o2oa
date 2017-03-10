package com.x.okr.assemble.control.jaxrs.statistic;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.base.core.utils.SortTools;
import com.x.okr.assemble.common.date.DateOperation;

public class ExcuteDateList extends ExcuteBase {

	private Logger logger = LoggerFactory.getLogger(ExcuteDateList.class);

	protected ActionResult<List<WrapOutOkrReportSubmitStatusDate>> execute( HttpServletRequest request, EffectivePerson effectivePerson, WrapInFilterOkrStatisticReportContent wrapIn) throws Exception {
		ActionResult<List<WrapOutOkrReportSubmitStatusDate>> result = new ActionResult<>();
		List<WrapOutOkrReportSubmitStatusDate> result_datetimes = new ArrayList<>();
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

//		if (wrapIn == null) {
//			check = false;
//			logger.error("wrapIn is null, system can not get any object.");
//			result.error(new Exception("传入的参数为空，无法进行查询！"));
//			result.setUserMessage("传入的参数为空，无法进行查询！");
//		}

		if (check) {
			order = wrapIn.getOrder();
			centerId = wrapIn.getCenterId();
			year = wrapIn.getYear();
			month = wrapIn.getMonth();
			week = wrapIn.getWeek();
			reportCycle = wrapIn.getReportCycle();
		}
		if (check) {
			if (wrapIn.getStartDate() != null && !wrapIn.getStartDate().isEmpty()) {
				try {
					startDate = dateOperation.getDateFromString(wrapIn.getStartDate());
				} catch (Exception e) {
					check = false;
					Exception exception = new QueryStartDateInvalidException( e, wrapIn.getStartDate() );
					result.error( exception );
					logger.error( exception, effectivePerson, request, null);
				}
			}
			if (wrapIn.getEndDate() != null && !wrapIn.getEndDate().isEmpty()) {
				try {
					endDate = dateOperation.getDateFromString(wrapIn.getEndDate());
				} catch (Exception e) {
					check = false;
					Exception exception = new QueryEndDateInvalidException( e, wrapIn.getEndDate() );
					result.error( exception );
					logger.error( exception, effectivePerson, request, null);
				}
			}
		}
		if (check) {
			try {
				if( reportCycle == null || reportCycle.isEmpty() ){
					datetimes = okrCenterWorkReportStatisticService.listDateTimeFlags( centerId, null, "每周汇报", year, month, week, startDate, endDate, null);
					if( datetimes != null && !datetimes.isEmpty() ){
						for( String datetime : datetimes){
							result_datetimes.add( new WrapOutOkrReportSubmitStatusDate( datetime, "每周汇报"));
						}
					}
					datetimes = okrCenterWorkReportStatisticService.listDateTimeFlags( centerId, null, "每月汇报", year, month, week, startDate, endDate, null);
					if( datetimes != null && !datetimes.isEmpty() ){
						for( String datetime : datetimes){
							result_datetimes.add( new WrapOutOkrReportSubmitStatusDate( datetime, "每月汇报"));
						}
					}
				}else{
					datetimes = okrCenterWorkReportStatisticService.listDateTimeFlags( centerId, null, reportCycle, year, month, week, startDate, endDate, null );
					if( datetimes != null && !datetimes.isEmpty() ){
						for( String datetime : datetimes){
							result_datetimes.add( new WrapOutOkrReportSubmitStatusDate( datetime, reportCycle ));
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
				Exception exception = new QueryWithConditionException( e );
				result.error( exception );
				logger.error( exception, effectivePerson, request, null);
			}
		}
		return result;
	}

}
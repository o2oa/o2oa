package com.x.okr.assemble.control.jaxrs.statistic;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.SortTools;
import com.x.okr.assemble.control.jaxrs.statistic.exception.ExceptionReportStatisitcList;
import com.x.okr.assemble.control.jaxrs.statistic.exception.ExceptionReportStatisitcListWithIds;
import com.x.okr.assemble.control.schedule.entity.WorkReportProcessOpinionEntity;
import com.x.okr.entity.OkrStatisticReportContent;
import com.x.okr.entity.OkrWorkDetailInfo;

public class ActionFilterList extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger( ActionFilterList.class );
	
	protected ActionResult<List<WoOkrStatisticReportContentCenter>> execute( HttpServletRequest request, EffectivePerson effectivePerson, WrapInFilterOkrStatisticReportContent wrapIn, Boolean isTree ) throws Exception {
		ActionResult<List<WoOkrStatisticReportContentCenter>> result = new ActionResult<>();
		List<String> ids = null;
		List<WoOkrStatisticReportContentCenter> wraps_centers = new ArrayList<>();
		List<WorkReportProcessOpinionEntity> opinions = null;
		List<OkrStatisticReportContent> okrStatisticReportContentList = null;
		WoOkrStatisticReportContentCenter wrap_center = null;
		WoOkrStatisticReportContent wrap = null;
		OkrWorkDetailInfo okrWorkDetailInfo = null;
		Integer year = null;
		Integer month = null;
		Integer week = null;
		String opinionContent = null;
		String reportCycle = null;
		String centerId = null;
		String statisticTimeFlag = null;
		Boolean hasSubWork = false;
		Boolean check = true;
		Gson gson = XGsonBuilder.instance();
		
		if( check ){
			centerId = wrapIn.getCenterId();
			statisticTimeFlag = wrapIn.getStatisticTimeFlag();
			year = wrapIn.getYear();
			month = wrapIn.getMonth();
			week = wrapIn.getWeek();
			reportCycle = wrapIn.getReportCycle();
		}
		
		if( check ){
			try {
				ids = okrCenterWorkReportStatisticService.listFirstLayer( centerId, wrapIn.getCenterTitle(), null, wrapIn.getWorkTypeName(), statisticTimeFlag, reportCycle, year, month, week, wrapIn.getStatus() );
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionReportStatisitcList( e );
				logger.error( e, effectivePerson, request, null);
				result.error( exception );
			}
		}
		
		if( check ){
			try {
				okrStatisticReportContentList = okrCenterWorkReportStatisticService.list( ids );
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionReportStatisitcListWithIds( e );
				logger.error( e, effectivePerson, request, null);
				result.error( exception );
			}
		}
		
		if( check ){
			if( okrStatisticReportContentList != null ){
				for( OkrStatisticReportContent okrStatisticReportContent : okrStatisticReportContentList ){
					if( okrStatisticReportContent.getParentId() != null && !okrStatisticReportContent.getParentId().isEmpty() ){
						continue;
					}
					wrap_center = null;
					try {
						wrap = WoOkrStatisticReportContent.copier.copy( okrStatisticReportContent );
						opinionContent = wrap.getOpinion();
						if( opinionContent != null && !"{}".equals( opinionContent )){
							opinions = gson.fromJson( opinionContent, new TypeToken<List<WorkReportProcessOpinionEntity>>(){}.getType() );
							wrap.setOpinions( opinions );
						}
						wrap_center = getContentFormCenterList( okrStatisticReportContent.getCenterId(), wraps_centers );
						if( wrap_center == null ){
							wrap_center = new WoOkrStatisticReportContentCenter();
							wrap_center.setId( okrStatisticReportContent.getCenterId() );
							wrap_center.setTitle( okrStatisticReportContent.getCenterTitle() );
							wraps_centers.add( wrap_center );
							if( wrap_center.getContents() == null ){
								wrap_center.setContents( new ArrayList<>() );
							}
						}
						
						wrap_center.countAdd( 1 );
						
						wrap.setId( null );
						wrap.setCenterId( null );
						wrap.setCenterTitle( null );
						wrap.setCreateTime( null );
						wrap.setUpdateTime( null );
						wrap.setSequence( null );
						wrap.setParentId( null );
						wrap.setWorkLevel( null );
						wrap.setStatisticTime( null );
						wrap.setStatisticYear( null );
						wrap.setStatisticMonth( null );
						wrap.setStatisticWeek( null );
						wrap.setStatus( null );
						wrap.setOpinion( null );
						wrap.setDistributeFactor( null );
						
						okrWorkDetailInfo = okrWorkDetailInfoService.get( wrap.getWorkId() );
						if( okrWorkDetailInfo != null ){
							wrap.setLandmarkDescription( okrWorkDetailInfo.getLandmarkDescription() );
							wrap.setProgressAction( okrWorkDetailInfo.getProgressAction() );
							wrap.setWorkDetail( okrWorkDetailInfo.getWorkDetail() );
						}
						//查询该工作下面是否还有下级工作
						hasSubWork = okrWorkBaseInfoQueryService.hasSubWork( wrap.getWorkId() );
						wrap.setHasSubWork(hasSubWork);
						wrap_center.getContents().add( wrap );
					} catch (Exception e) {
						logger.warn( "system copy object to wrap got an exception." );
						logger.error(e);
					}
				}
				for( WoOkrStatisticReportContentCenter temp : wraps_centers ){
					if( temp.getContents() != null ){
						try {
							SortTools.desc( temp.getContents(), "workTitle" );
						} catch (Exception e) {
							logger.warn( "system sort wrap list got an exception." );
							logger.error(e);
						}
					}
				}
				if( isTree ){
					wraps_centers = composeWorkTreeInCenter( wraps_centers, wrapIn.getCenterTitle(), wrapIn.getWorkTypeName(), statisticTimeFlag, reportCycle, year, month, week, null );
				}else{
					wraps_centers = composeWorkInCenter( wraps_centers, wrapIn.getCenterTitle(), wrapIn.getWorkTypeName(), statisticTimeFlag, reportCycle, year, month, week, null );
				}
				result.setData( wraps_centers );
			}
		}
		return result;
	}
	
}
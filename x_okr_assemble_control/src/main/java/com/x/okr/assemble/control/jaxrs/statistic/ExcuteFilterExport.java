package com.x.okr.assemble.control.jaxrs.statistic;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.x.base.core.gson.XGsonBuilder;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.base.core.utils.SortTools;
import com.x.okr.assemble.control.jaxrs.statistic.exception.ReportStatisitcListException;
import com.x.okr.assemble.control.jaxrs.statistic.exception.ReportStatisitcListWithIdsException;
import com.x.okr.assemble.control.timertask.entity.WorkReportProcessOpinionEntity;
import com.x.okr.entity.OkrStatisticReportContent;
import com.x.okr.entity.OkrWorkDetailInfo;

public class ExcuteFilterExport extends ExcuteBase {

	private Logger logger = LoggerFactory.getLogger( ExcuteFilterExport.class );
	
	public List<WrapOutOkrStatisticReportContentCenter> execute( HttpServletRequest request, EffectivePerson effectivePerson, String reportCycle, String centerId, String centerTitle, String workType, String statisticTimeFlag ) {
		List<String> ids = null;
		List<WrapOutOkrStatisticReportContentCenter> wraps_centers = new ArrayList<>();
		List<WorkReportProcessOpinionEntity> opinions = null;
		List<OkrStatisticReportContent> okrStatisticReportContentList = null;
		WrapOutOkrStatisticReportContentCenter wrap_center = null;
		WrapOutOkrStatisticReportContent wrap = null;
		OkrWorkDetailInfo okrWorkDetailInfo = null;
		Integer year = null;
		Integer month = null;
		Integer week = null;
		String opinionContent = null;
		Boolean hasSubWork = false;
		Boolean check = true;
		Gson gson = XGsonBuilder.pureGsonDateFormated();
		
		if( check ){
			try {
				ids = okrCenterWorkReportStatisticService.listFirstLayer( centerId, centerTitle, null, workType, statisticTimeFlag, reportCycle, year, month, week, "正常" );
			} catch (Exception e) {
				check = false;
				Exception exception = new ReportStatisitcListException( e );
				logger.error( e, effectivePerson, request, null);
			}
		}
		
		if( check ){
			try {
				okrStatisticReportContentList = okrCenterWorkReportStatisticService.list( ids );
			} catch (Exception e) {
				check = false;
				Exception exception = new ReportStatisitcListWithIdsException( e );
				logger.error( e, effectivePerson, request, null);
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
						wrap = wrapout_copier.copy( okrStatisticReportContent );
						opinionContent = wrap.getOpinion();
						if( opinionContent != null && !"{}".equals( opinionContent )){
							opinions = gson.fromJson( opinionContent, new TypeToken<List<WorkReportProcessOpinionEntity>>(){}.getType() );
							wrap.setOpinions( opinions );
						}
						wrap_center = getContentFormCenterList( okrStatisticReportContent.getCenterId(), wraps_centers );
						if( wrap_center == null ){
							wrap_center = new WrapOutOkrStatisticReportContentCenter();
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
						check = false;
						logger.warn( "system copy object to wrap got an exception." );
						logger.error(e);
					}
				}
				for( WrapOutOkrStatisticReportContentCenter temp : wraps_centers ){
					if( temp.getContents() != null ){
						try {
							SortTools.desc( temp.getContents(), "workTitle" );
						} catch (Exception e) {
							logger.warn( "system sort wrap list got an exception." );
							logger.error(e);
						}
					}
				}
				wraps_centers = composeWorkInCenter( wraps_centers, centerTitle, workType,statisticTimeFlag, reportCycle, year, month, week, "正常" );
			}
		}
		return wraps_centers;
	}
	
}
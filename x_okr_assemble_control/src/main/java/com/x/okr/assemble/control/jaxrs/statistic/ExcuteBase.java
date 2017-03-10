package com.x.okr.assemble.control.jaxrs.statistic;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.x.base.core.bean.BeanCopyTools;
import com.x.base.core.bean.BeanCopyToolsBuilder;
import com.x.base.core.gson.XGsonBuilder;
import com.x.base.core.utils.SortTools;
import com.x.okr.assemble.control.service.OkrStatisticReportContentService;
import com.x.okr.assemble.control.service.OkrWorkBaseInfoQueryService;
import com.x.okr.assemble.control.service.OkrWorkDetailInfoService;
import com.x.okr.assemble.control.timertask.entity.WorkReportProcessOpinionEntity;
import com.x.okr.entity.OkrStatisticReportContent;
import com.x.okr.entity.OkrWorkDetailInfo;

public class ExcuteBase {
	private Logger logger = LoggerFactory.getLogger( ExcuteBase.class );
	protected BeanCopyTools<OkrStatisticReportContent, WrapOutOkrStatisticReportContent> wrapout_copier = BeanCopyToolsBuilder.create( OkrStatisticReportContent.class, WrapOutOkrStatisticReportContent.class, null, WrapOutOkrStatisticReportContent.Excludes);
	protected OkrStatisticReportContentService okrCenterWorkReportStatisticService = new OkrStatisticReportContentService();
	protected OkrWorkDetailInfoService okrWorkDetailInfoService = new OkrWorkDetailInfoService();
	protected OkrWorkBaseInfoQueryService okrWorkBaseInfoQueryService = new OkrWorkBaseInfoQueryService();
	
	protected List<WrapOutOkrStatisticReportContentCenter> composeWorkInCenter( 
			List<WrapOutOkrStatisticReportContentCenter> wraps_centers, String statisticTimeFlag, String reportCycle, Integer year, Integer month, Integer week, String status ) {
		List<WrapOutOkrStatisticReportContentCenter> wrapout_centers = new ArrayList<>();
		List<WrapOutOkrStatisticReportContent> work_st_contents = null;
		WrapOutOkrStatisticReportContentCenter wrapout_center = null;
		Integer workNumber = 0;
		Integer workLevel = 1;
		if( wraps_centers != null && !wraps_centers.isEmpty() ){
			for( WrapOutOkrStatisticReportContentCenter center : wraps_centers ){
				workNumber = 0;
				work_st_contents = center.getContents();
				
				wrapout_center = new WrapOutOkrStatisticReportContentCenter();
				wrapout_center.setCount( center.getCount() );
				wrapout_center.setId( center.getId() );
				wrapout_center.setTitle( center.getTitle() );
				wrapout_centers.add( wrapout_center );
				
				if( work_st_contents != null && !work_st_contents.isEmpty() ){
					for( WrapOutOkrStatisticReportContent work_st_content : work_st_contents ){
						workNumber++;
						work_st_content.setSerialNumber( workNumber +"" );
						work_st_content.setLevel( workLevel );
						if( wrapout_center != null ){
							if( wrapout_center.getContents() == null ){
								wrapout_center.setContents( new ArrayList<>());
							}
							wrapout_center.getContents().add( work_st_content );
						}
						composeWorkStContent( wrapout_center, work_st_content, workLevel, work_st_content.getSerialNumber(), statisticTimeFlag, reportCycle, year, month, week, status );
					}
				}
			}
		}
		return wrapout_centers;
	}
	
	protected List<WrapOutOkrStatisticReportContentCenter> composeWorkTreeInCenter( List<WrapOutOkrStatisticReportContentCenter> wraps_centers, String statisticTimeFlag, String reportCycle, Integer year, Integer month, Integer week, String status ) {
		List<WrapOutOkrStatisticReportContent> work_st_contents = null;
		Integer workNumber = 0;
		Integer workLevel = 1;
		if( wraps_centers != null && !wraps_centers.isEmpty() ){
			for( WrapOutOkrStatisticReportContentCenter center : wraps_centers ){
				workNumber = 0;
				work_st_contents = center.getContents();
				if( work_st_contents != null && !work_st_contents.isEmpty() ){
					for( WrapOutOkrStatisticReportContent work_st_content : work_st_contents ){
						workNumber++;
						work_st_content.setSerialNumber( workNumber +"" );
						work_st_content.setLevel( workLevel );
						composeWorkStContent( null, work_st_content, workLevel, work_st_content.getSerialNumber(), statisticTimeFlag, reportCycle, year, month, week, status );
					}
				}
			}
		}
		return wraps_centers;
	}

	protected void composeWorkStContent( 
			WrapOutOkrStatisticReportContentCenter center, WrapOutOkrStatisticReportContent work_st_content, 
			Integer workLevel, String serialNumber, String statisticTimeFlag, String reportCycle, Integer year, Integer month, Integer week, String status ) {
		List<WrapOutOkrStatisticReportContent> wrap_workContents = null;
		List<OkrStatisticReportContent> workContents = null;
		List<WorkReportProcessOpinionEntity> opinions = null;
		List<String> ids = null;
		String opinionContent = null;
		Integer cuurrent_workLevel = null;
		Integer workNumber = 0;
		Boolean hasSubWork = false;
		OkrWorkDetailInfo okrWorkDetailInfo = null;
		Gson gson = XGsonBuilder.pureGsonDateFormated();
		if( workLevel == null ){
			workLevel = 0;
		}
		if( work_st_content.getWorkId() != null &&!work_st_content.getWorkId().isEmpty() ){
			cuurrent_workLevel = workLevel.intValue() + 1;
			try {
				ids = okrCenterWorkReportStatisticService.list( null, work_st_content.getWorkId(), statisticTimeFlag, reportCycle, year, month, week, status );
				if( ids != null && !ids.isEmpty() ){
					workContents = okrCenterWorkReportStatisticService.list( ids );
					if( workContents != null && !workContents.isEmpty() ){
						wrap_workContents = wrapout_copier.copy( workContents );
						try {
							SortTools.desc( wrap_workContents, "workTitle" );
						} catch (Exception e) {
							logger.warn( "system sort wrap list got an exception." );
							logger.error(e);
						}
						for( WrapOutOkrStatisticReportContent wrap : wrap_workContents ){
							try {
								opinionContent = wrap.getOpinion();
								if( opinionContent != null && !"{}".equals( opinionContent )){
									opinions = gson.fromJson( opinionContent, new TypeToken<List<WorkReportProcessOpinionEntity>>(){}.getType() );
									wrap.setOpinions( opinions );
								}
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
								wrap.setLevel( cuurrent_workLevel );
								
								workNumber ++;
								wrap.setSerialNumber( work_st_content.getSerialNumber() + "." + workNumber );
								if( center != null ){
									if( center.getContents() == null ){
										center.setContents( new ArrayList<>());
									}
									center.getContents().add( wrap );
								}else{
									work_st_content.addSubWork( wrap );
								}
								composeWorkStContent( center, wrap, cuurrent_workLevel, serialNumber, statisticTimeFlag, reportCycle, year, month, week, status);
							} catch (Exception e) {
								logger.warn( "system copy object to wrap got an exception." );
								logger.error(e);
							}
						}
					}
				}
			} catch (Exception e) {
				logger.warn( "system list work report statistic with condition got an exception." );
				logger.error(e);
			}
		}
	}
	
	protected WrapOutOkrStatisticReportContentCenter getContentFormCenterList( String centerId, List<WrapOutOkrStatisticReportContentCenter> wraps_centers) {
		if( wraps_centers == null ){
			wraps_centers = new ArrayList<>();
		}
		for( WrapOutOkrStatisticReportContentCenter center : wraps_centers){
			if( center.getId().equals( centerId )){
				if( center.getContents() == null ){
					center.setContents( new ArrayList<>());
				}
				return center;
			}
		}
		return null;
	}
}

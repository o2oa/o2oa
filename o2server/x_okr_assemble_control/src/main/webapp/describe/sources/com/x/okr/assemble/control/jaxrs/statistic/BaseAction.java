package com.x.okr.assemble.control.jaxrs.statistic;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.SortTools;
import com.x.okr.assemble.common.date.DateOperation;
import com.x.okr.assemble.common.date.MonthOfYear;
import com.x.okr.assemble.common.date.WeekOfYear;
import com.x.okr.assemble.control.schedule.entity.WorkBaseReportSubmitEntity;
import com.x.okr.assemble.control.schedule.entity.WorkReportProcessOpinionEntity;
import com.x.okr.assemble.control.service.OkrStatisticReportContentService;
import com.x.okr.assemble.control.service.OkrStatisticReportStatusService;
import com.x.okr.assemble.control.service.OkrWorkBaseInfoQueryService;
import com.x.okr.assemble.control.service.OkrWorkDetailInfoService;
import com.x.okr.entity.OkrStatisticReportContent;
import com.x.okr.entity.OkrStatisticReportStatus;
import com.x.okr.entity.OkrWorkDetailInfo;

public class BaseAction extends StandardJaxrsAction {
	private static  Logger logger = LoggerFactory.getLogger( BaseAction.class );
	protected WrapCopier<OkrStatisticReportStatus, WoOkrStatisticReportStatus> wrapout_copier = WrapCopierFactory.wo( OkrStatisticReportStatus.class, WoOkrStatisticReportStatus.class, null, WoOkrStatisticReportStatus.Excludes);
	
	protected OkrStatisticReportStatusService okrReportStatusStatisticService = new OkrStatisticReportStatusService();
	protected OkrStatisticReportContentService okrCenterWorkReportStatisticService = new OkrStatisticReportContentService();
	protected OkrWorkBaseInfoQueryService okrWorkBaseInfoQueryService = new OkrWorkBaseInfoQueryService();
	protected OkrWorkDetailInfoService okrWorkDetailInfoService = new OkrWorkDetailInfoService();
	protected DateOperation dateOperation = new DateOperation();
	
	protected List<WoOkrStatisticReportContentCenter> composeWorkInCenter( 
		List<WoOkrStatisticReportContentCenter> wraps_centers, String centerTitle, String workType, String statisticTimeFlag, String reportCycle, Integer year, Integer month, Integer week, String status ) {
		List<WoOkrStatisticReportContentCenter> wrapout_centers = new ArrayList<>();
		List<WoOkrStatisticReportContent> work_st_contents = null;
		WoOkrStatisticReportContentCenter wrapout_center = null;
		Integer workNumber = 0;
		Integer workLevel = 1;
		if( wraps_centers != null && !wraps_centers.isEmpty() ){
			for( WoOkrStatisticReportContentCenter center : wraps_centers ){
				workNumber = 0;
				work_st_contents = center.getContents();
				
				wrapout_center = new WoOkrStatisticReportContentCenter();
				wrapout_center.setCount( center.getCount() );
				wrapout_center.setId( center.getId() );
				wrapout_center.setTitle( center.getTitle() );
				wrapout_centers.add( wrapout_center );
				
				if( work_st_contents != null && !work_st_contents.isEmpty() ){
					for( WoOkrStatisticReportContent work_st_content : work_st_contents ){
						workNumber++;
						work_st_content.setSerialNumber( workNumber +"" );
						work_st_content.setLevel( workLevel );
						if( wrapout_center != null ){
							if( wrapout_center.getContents() == null ){
								wrapout_center.setContents( new ArrayList<>());
							}
							wrapout_center.getContents().add( work_st_content );
						}
						composeWorkStContent( wrapout_center, work_st_content, centerTitle, workType, workLevel, work_st_content.getSerialNumber(), statisticTimeFlag, reportCycle, year, month, week, status );
					}
				}
			}
		}
		return wrapout_centers;
	}
	
	protected List<WoOkrStatisticReportContentCenter> composeWorkTreeInCenter( List<WoOkrStatisticReportContentCenter> wraps_centers, String centerTitle, String workType, String statisticTimeFlag, String reportCycle, Integer year, Integer month, Integer week, String status ) {
		List<WoOkrStatisticReportContent> work_st_contents = null;
		Integer workNumber = 0;
		Integer workLevel = 1;
		if( wraps_centers != null && !wraps_centers.isEmpty() ){
			for( WoOkrStatisticReportContentCenter center : wraps_centers ){
				workNumber = 0;
				work_st_contents = center.getContents();
				if( work_st_contents != null && !work_st_contents.isEmpty() ){
					for( WoOkrStatisticReportContent work_st_content : work_st_contents ){
						workNumber++;
						work_st_content.setSerialNumber( workNumber +"" );
						work_st_content.setLevel( workLevel );
						composeWorkStContent( null,  work_st_content, centerTitle, workType, workLevel, work_st_content.getSerialNumber(), statisticTimeFlag, reportCycle, year, month, week, status );
					}
				}
			}
		}
		return wraps_centers;
	}

	protected void composeWorkStContent( 
			WoOkrStatisticReportContentCenter center, WoOkrStatisticReportContent work_st_content, String centerTitle, String workType,
			Integer workLevel, String serialNumber, String statisticTimeFlag, String reportCycle, Integer year, Integer month, Integer week, String status ) {
		List<WoOkrStatisticReportContent> wrap_workContents = null;
		List<OkrStatisticReportContent> workContents = null;
		List<WorkReportProcessOpinionEntity> opinions = null;
		List<String> ids = null;
		String opinionContent = null;
		Integer cuurrent_workLevel = null;
		Integer workNumber = 0;
		Boolean hasSubWork = false;
		OkrWorkDetailInfo okrWorkDetailInfo = null;
		Gson gson = XGsonBuilder.instance();
		if( workLevel == null ){
			workLevel = 0;
		}
		if( work_st_content.getWorkId() != null &&!work_st_content.getWorkId().isEmpty() ){
			cuurrent_workLevel = workLevel.intValue() + 1;
			try {
				ids = okrCenterWorkReportStatisticService.list( null, centerTitle, work_st_content.getWorkId(), workType, statisticTimeFlag, reportCycle, year, month, week, status );
				if( ids != null && !ids.isEmpty() ){
					workContents = okrCenterWorkReportStatisticService.list( ids );
					if( workContents != null && !workContents.isEmpty() ){
						wrap_workContents = WoOkrStatisticReportContent.copier.copy( workContents );
						try {
							SortTools.desc( wrap_workContents, "workTitle" );
						} catch (Exception e) {
							logger.warn( "system sort wrap list got an exception." );
							logger.error(e);
						}
						for( WoOkrStatisticReportContent wrap : wrap_workContents ){
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
								composeWorkStContent( center, wrap, centerTitle, workType, cuurrent_workLevel, serialNumber, statisticTimeFlag, reportCycle, year, month, week, status);
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
	
	protected List<WoOkrStatisticReportStatusHeader> getHeaderForUnitMonthStatistic( List<MonthOfYear> months ) {
		List<WoOkrStatisticReportStatusHeader> headers = new ArrayList<>();
		WoOkrStatisticReportStatusHeader header = null;
		//组织一个表格头		
		if( months != null && !months.isEmpty() ){
			header = new WoOkrStatisticReportStatusHeader();
			header.setTitle( "组织名称" );
			headers.add( header );
			
			header = new WoOkrStatisticReportStatusHeader();
			header.setTitle( "中心工作" );
			headers.add( header );
			
			header = new WoOkrStatisticReportStatusHeader();
			header.setTitle( "工作内容" );
			headers.add( header );
			for( MonthOfYear month : months ){
				header = new WoOkrStatisticReportStatusHeader();
				header.setTitle( month.getYear() + "年第"+ month.getMonth() +"月" );
				header.setStartDate( month.getStartDateString() );
				header.setEndDate( month.getEndDateString() );
				headers.add( header );
			}
		}
		return headers;
	}

	protected List<WoOkrStatisticReportStatusHeader> getHeaderForUnitWeekStatistic(List<WeekOfYear> weeks) {
		List<WoOkrStatisticReportStatusHeader> headers = new ArrayList<>();
		WoOkrStatisticReportStatusHeader header = null;
		//组织一个表格头		
		if( weeks != null && !weeks.isEmpty() ){
			header = new WoOkrStatisticReportStatusHeader();
			header.setTitle( "组织名称" );
			headers.add( header );
			
			header = new WoOkrStatisticReportStatusHeader();
			header.setTitle( "中心工作" );
			headers.add( header );
			
			header = new WoOkrStatisticReportStatusHeader();
			header.setTitle( "工作内容" );
			headers.add( header );
			for( WeekOfYear week : weeks ){
				header = new WoOkrStatisticReportStatusHeader();
				header.setTitle( week.getYear() + "年第"+ week.getWeekNo() +"周" );
				header.setStartDate( week.getStartDateString() );
				header.setEndDate( week.getEndDateString() );
				headers.add( header );
			}
		}
		return headers;
	}

	protected List<WoOkrStatisticReportStatusEntity> getMonthFirstLayerArray( List<WoOkrStatisticReportStatus> wrapOutOkrReportSubmitStatusStatisticList, List<MonthOfYear> months, Date startDate, Date endDate) {
		Date startDate_entity = null;
		Date endDate_entity = null;
		String statisticContent = null;
		List<WoOkrStatisticReportStatusEntity> unitNameLayer = new ArrayList<>();
		List<WoOkrStatisticReportStatusEntity> centerLayer = null;
		List<WoOkrStatisticReportStatusEntity> workLayer = null;
		WoOkrStatisticReportStatusEntity unitNameStatistic = null;
		WoOkrStatisticReportStatusEntity centerWorkStatistic = null;
		WoOkrStatisticReportStatusEntity workStatistic = null;
		Gson gson = XGsonBuilder.instance();
		List<WorkBaseReportSubmitEntity> list = null;
		List<WorkBaseReportSubmitEntity> wrapList = null;
		WorkBaseReportSubmitEntity temp = null;
		Boolean statisticExists = false;
		Boolean check = true;
		if( check ){
			//按组织排个序
			if( wrapOutOkrReportSubmitStatusStatisticList != null && !wrapOutOkrReportSubmitStatusStatisticList.isEmpty() ){
				for( WoOkrStatisticReportStatus statistic : wrapOutOkrReportSubmitStatusStatisticList ){
					unitNameStatistic = null;
					centerLayer = null;
					centerWorkStatistic = null;
					workLayer = null;
					workStatistic = null;
					
					//查找对应的组织的统计数据对象是否存在
					if( getFormUnitNameLayer( statistic.getResponsibilityUnitName(), unitNameLayer ) == null ){
						unitNameStatistic = new WoOkrStatisticReportStatusEntity();
						unitNameStatistic.setTitle( statistic.getResponsibilityUnitName() );
						unitNameStatistic.setId( statistic.getResponsibilityUnitName() );
						unitNameLayer.add( unitNameStatistic );
					}else{
						unitNameStatistic = getFormUnitNameLayer( statistic.getResponsibilityUnitName(), unitNameLayer );
					}
					//获取组织统计对象里的中心工作列表
					if( unitNameStatistic.getArray() == null ){
						centerLayer = new ArrayList<>();
						unitNameStatistic.setArray( centerLayer );						
					}else{
						centerLayer = unitNameStatistic.getArray();
					}
					//在组织统计对象的中心工作列表里查找应用的中心工作是否存在
					if( getFormCenterLayer( statistic.getCenterId(), centerLayer ) == null ){
						centerWorkStatistic = new WoOkrStatisticReportStatusEntity();
						centerWorkStatistic.setId( statistic.getCenterId() );
						centerWorkStatistic.setTitle( statistic.getCenterTitle() );
						centerLayer.add( centerWorkStatistic );
					}else{
						centerWorkStatistic = getFormCenterLayer( statistic.getCenterId(), centerLayer );
					}
					//获取组织统计对象里的工作列表
					if( centerWorkStatistic.getArray() == null ){
						workLayer = new ArrayList<>();
						centerWorkStatistic.setArray( workLayer );					
					}else{
						workLayer = centerWorkStatistic.getArray();
					}
					if( getFormWorkLayer( statistic.getWorkId(), workLayer ) == null ){
						workStatistic = new WoOkrStatisticReportStatusEntity();
						workStatistic.setId( statistic.getWorkId() );
						workStatistic.setTitle( statistic.getWorkTitle() );
						workStatistic.setDeployDate( statistic.getDeployDateStr() );
						workStatistic.setCompleteLimitDate( statistic.getCompleteDateLimitStr() );
						//过滤一下不需要的周期
						wrapList = new ArrayList<>();
						statisticContent = statistic.getReportStatistic();
						list = gson.fromJson( statisticContent, new TypeToken<List<WorkBaseReportSubmitEntity>>(){}.getType() );
						if( list != null && !list.isEmpty() ){
							if( months != null && !months.isEmpty() ){
								for( MonthOfYear month : months ){
									statisticExists = false;
									for( WorkBaseReportSubmitEntity entity : list ){
										if( month.getStartDateString().equals( entity.getStartDate() ) ){
											if( entity.getEndDate() != null ){
												try {
													endDate_entity = dateOperation.getDateFromString( entity.getEndDate() );
													startDate_entity = dateOperation.getDateFromString( entity.getStartDate() );
													if( startDate.before( endDate_entity ) || endDate.after( startDate_entity )){
														statisticExists = true;
														wrapList.add( entity );
													}
												} catch (Exception e) {
													logger.warn( "system format date got an exception." );
													logger.error(e);
												}
											}
										}
									}
									//这里如果没有的要补齐
									if( !statisticExists ){
										temp = new WorkBaseReportSubmitEntity();
										temp.setCycleNumber( month.getMonth() );
										temp.setCycleType( "每月汇报" );
										temp.setDescription( "查询时间未在工作执行时间周期内" );
										temp.setEndDate( month.getEndDateString() );
										temp.setReportId( null );
										temp.setReportStatus( -1 );
										temp.setStartDate( month.getStartDateString() );
										temp.setSubmitTime(null);
										wrapList.add( temp );
									}
								}
							}
						}
						workStatistic.setFields( wrapList );
						workLayer.add( workStatistic );
						unitNameStatistic.addRowCount( 1 );
						centerWorkStatistic.addRowCount( 1 );
					}
				}
			}
		}
		return unitNameLayer;
	}
	
	protected List<WoOkrStatisticReportStatusEntity> getWeekFirstLayerArray( List<WoOkrStatisticReportStatus> wrapOutOkrReportSubmitStatusStatisticList, List<WeekOfYear> weeks, Date startDate, Date endDate ) {
		Date startDate_entity = null;
		Date endDate_entity = null;
		String statisticContent = null;
		List<WoOkrStatisticReportStatusEntity> unitNameLayer = new ArrayList<>();
		List<WoOkrStatisticReportStatusEntity> centerLayer = null;
		List<WoOkrStatisticReportStatusEntity> workLayer = null;
		WoOkrStatisticReportStatusEntity unitNameStatistic = null;
		WoOkrStatisticReportStatusEntity centerWorkStatistic = null;
		WoOkrStatisticReportStatusEntity workStatistic = null;
		Gson gson = XGsonBuilder.instance();
		List<WorkBaseReportSubmitEntity> list = null;
		List<WorkBaseReportSubmitEntity> wrapList = null;
		WorkBaseReportSubmitEntity temp = null;
		Boolean statisticExists = false;
		Boolean check = true;
		if( check ){
			//按组织排个序
			if( wrapOutOkrReportSubmitStatusStatisticList != null && !wrapOutOkrReportSubmitStatusStatisticList.isEmpty() ){
				for( WoOkrStatisticReportStatus statistic : wrapOutOkrReportSubmitStatusStatisticList ){
					unitNameStatistic = null;
					centerLayer = null;
					centerWorkStatistic = null;
					workLayer = null;
					workStatistic = null;
					
					//查找对应的组织的统计数据对象是否存在
					if( getFormUnitNameLayer( statistic.getResponsibilityUnitName(), unitNameLayer ) == null ){
						unitNameStatistic = new WoOkrStatisticReportStatusEntity();
						unitNameStatistic.setTitle( statistic.getResponsibilityUnitName() );
						unitNameStatistic.setId( statistic.getResponsibilityUnitName() );
						unitNameLayer.add( unitNameStatistic );
					}else{
						unitNameStatistic = getFormUnitNameLayer( statistic.getResponsibilityUnitName(), unitNameLayer );
					}
					//获取组织统计对象里的中心工作列表
					if( unitNameStatistic.getArray() == null ){
						centerLayer = new ArrayList<>();
						unitNameStatistic.setArray( centerLayer );						
					}else{
						centerLayer = unitNameStatistic.getArray();
					}
					//在组织统计对象的中心工作列表里查找应用的中心工作是否存在
					if( getFormCenterLayer( statistic.getCenterId(), centerLayer ) == null ){
						centerWorkStatistic = new WoOkrStatisticReportStatusEntity();
						centerWorkStatistic.setId( statistic.getCenterId() );
						centerWorkStatistic.setTitle( statistic.getCenterTitle() );
						centerLayer.add( centerWorkStatistic );
					}else{
						centerWorkStatistic = getFormCenterLayer( statistic.getCenterId(), centerLayer );
					}
					//获取组织统计对象里的工作列表
					if( centerWorkStatistic.getArray() == null ){
						workLayer = new ArrayList<>();
						centerWorkStatistic.setArray( workLayer );					
					}else{
						workLayer = centerWorkStatistic.getArray();
					}
					if( getFormWorkLayer( statistic.getWorkId(), workLayer ) == null ){
						workStatistic = new WoOkrStatisticReportStatusEntity();
						workStatistic.setId( statistic.getWorkId() );
						workStatistic.setTitle( statistic.getWorkTitle() );
						workStatistic.setDeployDate( statistic.getDeployDateStr() );
						workStatistic.setCompleteLimitDate( statistic.getCompleteDateLimitStr() );
						//过滤一下不需要的周期
						wrapList = new ArrayList<>();
						statisticContent = statistic.getReportStatistic();
						if( statisticContent.indexOf("[") == 0 ) {
							//证明是数组
							list = gson.fromJson( statisticContent, new TypeToken<List<WorkBaseReportSubmitEntity>>(){}.getType() );
							if( list != null && !list.isEmpty() ){
								if( weeks != null && !weeks.isEmpty() ){
									for( WeekOfYear week : weeks ){
										statisticExists = false;
										for( WorkBaseReportSubmitEntity entity : list ){
											if( week.getStartDateString().equals( entity.getStartDate() ) ){
												if( entity.getEndDate() != null ){
													try {
														endDate_entity = dateOperation.getDateFromString( entity.getEndDate() );
														startDate_entity = dateOperation.getDateFromString( entity.getStartDate() );
														if( startDate.before( endDate_entity ) || endDate.after( startDate_entity )){
															statisticExists = true;
															wrapList.add( entity );
														}
													} catch (Exception e) {
														logger.warn( "system format date got an exception." );
														logger.error(e );
													}
												}
											}
										}
										//这里如果没有的要补齐
										if( !statisticExists ){
											temp = new WorkBaseReportSubmitEntity();
											temp.setCycleNumber( week.getWeekNo() );
											temp.setCycleType( "每周汇报" );
											temp.setDescription( "查询时间未在工作执行时间周期内" );
											temp.setEndDate( week.getEndDateString() );
											temp.setReportId( null );
											temp.setReportStatus( -1 );
											temp.setStartDate( week.getStartDateString() );
											temp.setSubmitTime(null);
											wrapList.add( temp );
										}
									}
								}
							}
							workStatistic.setFields( wrapList );
							workLayer.add( workStatistic );
							unitNameStatistic.addRowCount( 1 );
							centerWorkStatistic.addRowCount( 1 );
						}
					}
				}
			}
		}
		return unitNameLayer;
	}

	protected WoOkrStatisticReportStatusEntity getFormWorkLayer(String workId, List<WoOkrStatisticReportStatusEntity> workLayer) {
		if( workId == null || workId.isEmpty() ){
			return null;
		}
		if( workLayer == null || workLayer.isEmpty() ){
			return null;
		}
		for( WoOkrStatisticReportStatusEntity entity : workLayer ){
			if( workId.equals( entity.getId() ) ){
				return entity;
			}
		}
		return null;
	}

	protected WoOkrStatisticReportStatusEntity getFormCenterLayer(String centerId, List<WoOkrStatisticReportStatusEntity> centerLayer) {
		if( centerId == null || centerId.isEmpty() ){
			return null;
		}
		if( centerLayer == null || centerLayer.isEmpty() ){
			return null;
		}
		for( WoOkrStatisticReportStatusEntity entity : centerLayer ){
			if( centerId.equals( entity.getId() ) ){
				return entity;
			}
		}
		return null;
	}


	protected WoOkrStatisticReportStatusEntity getFormUnitNameLayer( String responsibilityUnitName, List<WoOkrStatisticReportStatusEntity> unitNameLayer) {
		if( responsibilityUnitName == null || responsibilityUnitName.isEmpty() ){
			return null;
		}
		if( unitNameLayer == null || unitNameLayer.isEmpty() ){
			return null;
		}
		for( WoOkrStatisticReportStatusEntity entity : unitNameLayer ){
			if( responsibilityUnitName.equals( entity.getTitle() ) ){
				return entity;
			}
		}
		return null;
	}
	
	protected WoOkrStatisticReportContentCenter getContentFormCenterList( String centerId, List<WoOkrStatisticReportContentCenter> wraps_centers) {
		if( wraps_centers == null ){
			wraps_centers = new ArrayList<>();
		}
		for( WoOkrStatisticReportContentCenter center : wraps_centers){
			if( center.getId().equals( centerId )){
				if( center.getContents() == null ){
					center.setContents( new ArrayList<>());
				}
				return center;
			}
		}
		return null;
	}
	
	public static class WoOkrStatisticReportContentCenter{

		private String id = null;
		
		private String title = null;
		
		private Integer count = 0;
		
		private List<WoOkrStatisticReportContent> contents = null;

		public String getTitle() {
			return title;
		}

		public Integer getCount() {
			return count;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public void setCount(Integer count) {
			this.count = count;
		}

		public List<WoOkrStatisticReportContent> getContents() {
			return contents;
		}

		public void setContents(List<WoOkrStatisticReportContent> contents) {
			this.contents = contents;
		}

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public Integer countAdd( Integer number ){
			this.count = this.count + number;
			return this.count;
		}
	}
	
	public static class WoOkrStatisticReportContent extends OkrStatisticReportContent{

		private static final long serialVersionUID = -5076990764713538973L;

		
		public static WrapCopier<OkrStatisticReportContent, WoOkrStatisticReportContent> copier = WrapCopierFactory.wo( OkrStatisticReportContent.class, WoOkrStatisticReportContent.class, null, JpaObject.FieldsInvisible);
		
		private String serialNumber = "1";
		
		private Integer level = 1;
		
		private String workDetail = null;
		
		private String progressAction = null;
		
		private String landmarkDescription = null;
		
		private Boolean hasSubWork = false;
	    
		private List<WorkReportProcessOpinionEntity> opinions = null;
		
		private List<WoOkrStatisticReportContent> subWork = null;

		public List<WorkReportProcessOpinionEntity> getOpinions() {
			return opinions;
		}

		public void setOpinions(List<WorkReportProcessOpinionEntity> opinions) {
			this.opinions = opinions;
		}

		public String getWorkDetail() {
			return workDetail;
		}

		public void setWorkDetail(String workDetail) {
			this.workDetail = workDetail;
		}

		public String getProgressAction() {
			return progressAction;
		}

		public void setProgressAction(String progressAction) {
			this.progressAction = progressAction;
		}

		public String getLandmarkDescription() {
			return landmarkDescription;
		}

		public void setLandmarkDescription(String landmarkDescription) {
			this.landmarkDescription = landmarkDescription;
		}

		public Boolean getHasSubWork() {
			return hasSubWork;
		}

		public void setHasSubWork( Boolean hasSubWork) {
			this.hasSubWork = hasSubWork;
		}

		public String getSerialNumber() {
			return serialNumber;
		}

		public void setSerialNumber(String serialNumber) {
			this.serialNumber = serialNumber;
		}

		public Integer getLevel() {
			return level;
		}

		public void setLevel(Integer level) {
			this.level = level;
		}

		public List<WoOkrStatisticReportContent> getSubWork() {
			return subWork;
		}

		public void setSubWork(List<WoOkrStatisticReportContent> subWork) {
			this.subWork = subWork;
		}
		
		public void addSubWork( WoOkrStatisticReportContent work ){
			if( this.subWork == null ){
				this.subWork = new ArrayList<>();
			}
			this.subWork.add( work );
		}
	}
	
	public static class WoOkrReportSubmitStatusDate{

		private String datetime = null;
		
		private String reportCycle = null;

		public WoOkrReportSubmitStatusDate( String datetime, String reportCycle ){
			this.datetime = datetime;
			this.reportCycle = reportCycle;
		}
		
		public String getDatetime() {
			return datetime;
		}

		public String getReportCycle() {
			return reportCycle;
		}

		public void setDatetime(String datetime) {
			this.datetime = datetime;
		}

		public void setReportCycle(String reportCycle) {
			this.reportCycle = reportCycle;
		}
	}
	
	public static class WoOkrStatisticReportStatusTable{

		private List<WoOkrStatisticReportStatusHeader> header = null;
		
		private List<WoOkrStatisticReportStatusEntity> content = null;	

		public List<WoOkrStatisticReportStatusHeader> getHeader() {
			return header;
		}

		public void setHeader(List<WoOkrStatisticReportStatusHeader> header) {
			this.header = header;
		}

		public List<WoOkrStatisticReportStatusEntity> getContent() {
			return content;
		}

		public void setContent(List<WoOkrStatisticReportStatusEntity> content) {
			this.content = content;
		}
	}
	
	public static class WoOkrStatisticReportStatusHeader{

		private String title = null;
		
		private String startDate = null;
		
		private String endDate = null;
		
		private String description = null;
		
		private Integer width = null;

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}

		public Integer getWidth() {
			return width;
		}

		public void setWidth(Integer width) {
			this.width = width;
		}

		public String getStartDate() {
			return startDate;
		}

		public void setStartDate(String startDate) {
			this.startDate = startDate;
		}

		public String getEndDate() {
			return endDate;
		}

		public void setEndDate(String endDate) {
			this.endDate = endDate;
		}
	}
	
	public class WoOkrStatisticReportStatusEntity{

		private String id = null;
		
		private String title = null;
		
		private String deployDate = null;
		
		private String completeLimitDate = null;
		
		private List<WorkBaseReportSubmitEntity> fields = null;
		
		private List<WoOkrStatisticReportStatusEntity> array = null;
		
		private Integer rowCount = 0;
		
		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public List<WoOkrStatisticReportStatusEntity> getArray() {
			return array;
		}

		public void setArray(List<WoOkrStatisticReportStatusEntity> array) {
			this.array = array;
		}

		public Integer getRowCount() {
			return rowCount;
		}

		public void setRowCount(Integer rowCount) {
			this.rowCount = rowCount;
		}	
		
		public void addRowCount( Integer number ){
			this.rowCount = this.rowCount + number;
		}

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getDeployDate() {
			return deployDate;
		}

		public void setDeployDate(String deployDate) {
			this.deployDate = deployDate;
		}

		public String getCompleteLimitDate() {
			return completeLimitDate;
		}

		public void setCompleteLimitDate(String completeLimitDate) {
			this.completeLimitDate = completeLimitDate;
		}

		public List<WorkBaseReportSubmitEntity> getFields() {
			return fields;
		}

		public void setFields(List<WorkBaseReportSubmitEntity> fields) {
			this.fields = fields;
		}
	}
	
	public static class WoOkrStatisticReportStatus extends OkrStatisticReportStatus{

		private static final long serialVersionUID = -5076990764713538973L;

		public static List<String> Excludes = new ArrayList<String>();

		private List<WorkBaseReportSubmitEntity> statistic = null;
		
		private Long rank = 0L;

		public Long getRank() {
			return rank;
		}

		public void setRank(Long rank) {
			this.rank = rank;
		}

		public List<WorkBaseReportSubmitEntity> getStatistic() {
			return statistic;
		}

		public void setStatistic(List<WorkBaseReportSubmitEntity> statistic) {
			this.statistic = statistic;
		}
	}

}

package com.x.okr.assemble.control.jaxrs.okrtask;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.okr.assemble.control.OkrUserCache;
import com.x.okr.assemble.control.jaxrs.okrtask.exception.ExceptionGetOkrUserCache;
import com.x.okr.assemble.control.jaxrs.okrtask.exception.ExceptionSystemConfigQueryByCode;
import com.x.okr.assemble.control.jaxrs.okrtask.exception.ExceptionTaskIdEmpty;
import com.x.okr.assemble.control.jaxrs.okrtask.exception.ExceptionTaskListByTaskType;
import com.x.okr.assemble.control.jaxrs.okrtask.exception.ExceptionTaskQueryById;
import com.x.okr.assemble.control.jaxrs.okrtask.exception.ExceptionUserNoLogin;
import com.x.okr.assemble.control.jaxrs.okrtask.exception.ExceptionWorkNotExists;
import com.x.okr.assemble.control.jaxrs.okrtask.exception.ExceptionWorkQueryById;
import com.x.okr.entity.OkrTask;
import com.x.okr.entity.OkrWorkAuthorizeRecord;
import com.x.okr.entity.OkrWorkBaseInfo;
import com.x.okr.entity.OkrWorkReportBaseInfo;
import com.x.okr.entity.OkrWorkReportDetailInfo;
import com.x.okr.entity.OkrWorkReportProcessLog;

public class ActionListTaskCollect extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger( ActionListTaskCollect.class );
	
	protected ActionResult<List<Wo>> execute( HttpServletRequest request,EffectivePerson effectivePerson, String id ) throws Exception {
		ActionResult<List<Wo>> result = new ActionResult<>();
		List<Wo> collectList = new ArrayList<Wo>();	
		String userIdentity = null;
		String workAdminIdentity = null;
		List<OkrTask> taskList = null;
		OkrTask okrTask = null;
		OkrWorkBaseInfo okrWorkBaseInfo = null;
		OkrWorkReportBaseInfo okrWorkReportBaseInfo = null;
		OkrWorkReportDetailInfo okrWorkReportDetailInfo = null;
		WoOkrWorkReportBaseInfo wrapOutOkrWorkReportBaseInfo = null;
		List<String> taskTypeList = new ArrayList<String>();
		Boolean check = true;		
		OkrUserCache  okrUserCache  = null;
		
		try {
			okrUserCache = okrUserInfoService.getOkrUserCacheWithPersonName( effectivePerson.getDistinguishedName() );
		} catch (Exception e ) {
			check = false;
			Exception exception = new ExceptionGetOkrUserCache( e, effectivePerson.getDistinguishedName() );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}		
		
		if( check && ( okrUserCache == null || okrUserCache.getLoginIdentityName() == null ) ){
			check = false;
			Exception exception = new ExceptionUserNoLogin( effectivePerson.getDistinguishedName() );
			result.error( exception );
		}
		if( check ){
			userIdentity = okrUserCache.getLoginIdentityName() ;
		}
		
		if( check ){
			if( id == null ){
				check = false;
				Exception exception = new ExceptionTaskIdEmpty();
				result.error( exception );
			}else{
				//查询该汇总待办信息
				try {
					okrTask = okrTaskService.get( id );
				} catch (Exception e) {
					check = false;
					Exception exception = new ExceptionTaskQueryById( e, id );
					result.error( exception );
					logger.error( e, effectivePerson, request, null);
				}
			}
		}
		
		if( check ){
			if( okrTask != null ){
				taskTypeList.add( "工作汇报" );
				try {
					//获取所有符合条件的待办
					taskList = okrTaskService.listTaskByTaskType( taskTypeList, userIdentity, okrTask.getWorkType() );
				} catch (Exception e) {
					check = false;
					Exception exception = new ExceptionTaskListByTaskType( e, userIdentity );
					result.error( exception );
					logger.error( e, effectivePerson, request, null);
				}
			}else{
				check = false;
				result.setCount( 0L );
			}
		}
		
		if( check ){
			try {
				workAdminIdentity = okrConfigSystemService.getValueWithConfigCode( "REPORT_SUPERVISOR" );
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionSystemConfigQueryByCode( e, "REPORT_SUPERVISOR" );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		
		if( check ){
			if( taskList != null && !taskList.isEmpty() ){
				for( OkrTask task : taskList ){
					try {
						okrWorkBaseInfo = okrWorkBaseInfoService.get( task.getWorkId() );
						if( okrWorkBaseInfo == null ){
							check = false;
							Exception exception = new ExceptionWorkNotExists( task.getWorkId() );
							result.error( exception );
						}
					} catch (Exception e) {
						check = false;
						Exception exception = new ExceptionWorkQueryById( e, task.getWorkId() );
						result.error( exception );
						logger.error( e, effectivePerson, request, null);
					}
					try {
						okrWorkReportBaseInfo = okrWorkReportQueryService.get( task.getDynamicObjectId() );
					} catch (Exception e) {
						logger.warn( "get okrWorkReportBaseInfo by id got an exception." );
						logger.error(e);
					}
					try {
						okrWorkReportDetailInfo = okrWorkReportDetailInfoService.get( task.getDynamicObjectId() );
					} catch (Exception e) {
						logger.warn( "get okrWorkReportBaseInfo by id got an exception." );
						logger.error(e);
					}
					if( okrWorkReportBaseInfo != null ){
						try {
							wrapOutOkrWorkReportBaseInfo = WoOkrWorkReportBaseInfo.copier.copy( okrWorkReportBaseInfo );
							if( wrapOutOkrWorkReportBaseInfo != null ){
								//查询工作所对应的工作信息
								
								wrapOutOkrWorkReportBaseInfo.setWorkInfo( WoOkrWorkBaseInfo.copier.copy(okrWorkBaseInfo));	
							}
							
							if( okrWorkReportDetailInfo != null ){
								wrapOutOkrWorkReportBaseInfo.setWorkPointAndRequirements( okrWorkReportDetailInfo.getWorkPointAndRequirements() );
								wrapOutOkrWorkReportBaseInfo.setWorkPlan( okrWorkReportDetailInfo.getWorkPlan() );
								wrapOutOkrWorkReportBaseInfo.setAdminSuperviseInfo( okrWorkReportDetailInfo.getAdminSuperviseInfo());
								wrapOutOkrWorkReportBaseInfo.setProgressDescription( okrWorkReportDetailInfo.getProgressDescription() );
							}
							
							if( wrapOutOkrWorkReportBaseInfo.getCreatorIdentity() != null && okrUserCache.getLoginIdentityName() .equalsIgnoreCase( wrapOutOkrWorkReportBaseInfo.getCreatorIdentity())){
								wrapOutOkrWorkReportBaseInfo.setIsCreator( true );
							}
							if( wrapOutOkrWorkReportBaseInfo.getReporterIdentity() != null && okrUserCache.getLoginIdentityName() .equalsIgnoreCase( wrapOutOkrWorkReportBaseInfo.getReporterIdentity())){
								wrapOutOkrWorkReportBaseInfo.setIsReporter(true);
							}
							if( "ADMIN_AND_ALLLEADER".equals( wrapOutOkrWorkReportBaseInfo.getReportWorkflowType() )){
								//从汇报审阅领导里进行比对
								if( ListTools.isNotEmpty( wrapOutOkrWorkReportBaseInfo.getReadLeadersIdentityList() ) 
										&&wrapOutOkrWorkReportBaseInfo.getReadLeadersIdentityList().contains(okrUserCache.getLoginIdentityName() ) ){
									wrapOutOkrWorkReportBaseInfo.setIsReadLeader( true );
								}
							}else if( "DEPLOYER".equals( wrapOutOkrWorkReportBaseInfo.getReportWorkflowType() ) ){
								if( okrWorkBaseInfo != null ){
									//对比当前工作的部署者是否是当前用户
									if( okrWorkBaseInfo.getDeployerIdentity() != null && okrWorkBaseInfo.getDeployerIdentity().equalsIgnoreCase( okrUserCache.getLoginIdentityName()  ) ){
										wrapOutOkrWorkReportBaseInfo.setIsReadLeader( true );
									}
								}
							}
							if( workAdminIdentity != null && !workAdminIdentity.isEmpty() && okrUserCache.getLoginIdentityName() .equalsIgnoreCase( workAdminIdentity )){
								wrapOutOkrWorkReportBaseInfo.setIsWorkAdmin( true );
							}
							//把汇报对象放进输出里
							addReportToCollectList( wrapOutOkrWorkReportBaseInfo, collectList );
							
						} catch (Exception e) {
							logger.warn( "format okrWorkReportBaseInfo to wrap got an exception." );
							logger.error(e);
						}
					}
				}
			}else {
				//说明已经没有任何待办信息了，这条汇总待办需要进行删除
				okrTaskService.delete( okrTask.getId() );
			}
		}
		result.setData( collectList );
		return result;
	}
	
	/**
	 * 将处理好的汇报对象放入输出的分类List里
	 * @param wrapOutOkrTaskReportEntity
	 * @param collectList
	 */
	private void addReportToCollectList( WoOkrWorkReportBaseInfo wrapOutOkrWorkReportBaseInfo, List<Wo> collectList) {
		
		List<WoOkrTaskReportEntity> wrapOutOkrTaskReportEntityList = null;
		
		WoOkrTaskCollectList wrapOutOkrTaskCollectList  = null;
		
		WoOkrTaskReportEntity wrapOutOkrTaskReportEntity = null;
		
		Wo collect = findCollectFormCollectList( wrapOutOkrWorkReportBaseInfo, collectList );
		
		boolean addAlready = false;
		
		if( collect != null ){
			
			if ( collect.getReportCollect() == null ) {
				collect.setReportCollect( new WoOkrTaskCollectList() );
			}
			wrapOutOkrTaskCollectList = collect.getReportCollect();
			wrapOutOkrTaskCollectList.addUnitName( wrapOutOkrWorkReportBaseInfo.getReporterUnitName() );
			
			if( wrapOutOkrTaskCollectList.getReportInfos() == null ){
				wrapOutOkrTaskCollectList.setReportInfos( new ArrayList<WoOkrTaskReportEntity>());
			}
			
			wrapOutOkrTaskReportEntityList = wrapOutOkrTaskCollectList.getReportInfos();
			for( WoOkrTaskReportEntity _wrapOutOkrTaskReportEntity : wrapOutOkrTaskReportEntityList ){
				if( _wrapOutOkrTaskReportEntity.getName().equals( wrapOutOkrWorkReportBaseInfo.getReporterUnitName() )){
					_wrapOutOkrTaskReportEntity.addReports(wrapOutOkrWorkReportBaseInfo);
					collect.setCount( collect.getCount() + 1 );
					addAlready = true;
				}
			}
			
			if( !addAlready ){
				wrapOutOkrTaskReportEntity = new WoOkrTaskReportEntity();
				wrapOutOkrTaskReportEntity.setName( wrapOutOkrWorkReportBaseInfo.getReporterUnitName() );
				wrapOutOkrTaskReportEntity.addReports( wrapOutOkrWorkReportBaseInfo );
				collect.setCount( collect.getCount() + 1 );
				wrapOutOkrTaskReportEntityList.add( wrapOutOkrTaskReportEntity );
			}
		}
	}
	
	private Wo findCollectFormCollectList( WoOkrWorkReportBaseInfo wrapOutOkrWorkReportBaseInfo, List<Wo> collectList ) {
		if( collectList == null ){
			collectList = new ArrayList<Wo>();
		}
		for( Wo collect : collectList ){
			//先找到该汇报所在环节的集合对象
			if( collect.getActivityName().equalsIgnoreCase( wrapOutOkrWorkReportBaseInfo.getActivityName() )){
				//找到对象的集合对象，放进去
				return collect;
			}
		}
		//没找到，要根据环节名称创建一个新的集合，并且将汇报对象放入
		Wo collect = new Wo();
		
		collect.setActivityName( wrapOutOkrWorkReportBaseInfo.getActivityName() );
		
		collect.setCount( 0 );
		
		collectList.add( collect );
		
		return collect;
	}
	
	public static class Wo extends OkrTask{

		private static final long serialVersionUID = -5076990764713538973L;

		public static List<String> Excludes = new ArrayList<String>();
		
		private String activity = null;
		
		private Integer count = 0;
		
		private WoOkrTaskCollectList reportCollect = null;

		public String getActivity() {
			return activity;
		}

		public void setActivity(String activity) {
			this.activity = activity;
		}

		public Integer getCount() {
			return count;
		}

		public void setCount(Integer count) {
			this.count = count;
		}

		public WoOkrTaskCollectList getReportCollect() {
			return reportCollect;
		}

		public void setReportCollect(WoOkrTaskCollectList reportCollect) {
			this.reportCollect = reportCollect;
		}
	}
	
	public static class WoOkrWorkReportBaseInfo extends OkrWorkReportBaseInfo{

		private static final long serialVersionUID = -5076990764713538973L;

		public static List<String> Excludes = new ArrayList<String>();
		
		public static WrapCopier<OkrWorkReportBaseInfo, WoOkrWorkReportBaseInfo> copier = WrapCopierFactory.wo( OkrWorkReportBaseInfo.class, WoOkrWorkReportBaseInfo.class, null, JpaObject.FieldsInvisible);
		
		private Boolean isReporter = false;
		
		private Boolean isWorkAdmin = false;
		
		private Boolean isReadLeader = false;
		
		private Boolean isCreator = false;
		
		private Boolean needReportProgress = false;
		
		/**
		 * 管理员督办信息
		 */
		private String adminSuperviseInfo = "";
		
		private String workPointAndRequirements = "";
		/**
		 * 填写汇报时填写的具体进展描述信息
		 */
		private String progressDescription = "";
		/**
		 * 下一步工作计划信息
		 */
		private String workPlan = "";
		/**
		 * 说明备注信息
		 */
		private String memo = "";
		
		private List<WoOkrWorkReportProcessLog> processLogs = null;

		private WoOkrWorkBaseInfo workInfo = null;
		
		private Long rank = 0L;

		public Long getRank() {
			return rank;
		}

		public void setRank(Long rank) {
			this.rank = rank;
		}
		
		public Boolean getIsReporter() {
			return isReporter;
		}

		public void setIsReporter(Boolean isReporter) {
			this.isReporter = isReporter;
		}

		public Boolean getIsWorkAdmin() {
			return isWorkAdmin;
		}

		public void setIsWorkAdmin(Boolean isWorkAdmin) {
			this.isWorkAdmin = isWorkAdmin;
		}

		public Boolean getIsReadLeader() {
			return isReadLeader;
		}

		public void setIsReadLeader(Boolean isReadLeader) {
			this.isReadLeader = isReadLeader;
		}

		public Boolean getIsCreator() {
			return isCreator;
		}

		public void setIsCreator(Boolean isCreator) {
			this.isCreator = isCreator;
		}
		
		public String getProgressDescription() {
			return progressDescription;
		}
		public void setProgressDescription(String progressDescription) {
			this.progressDescription = progressDescription;
		}
		public String getWorkPlan() {
			return workPlan;
		}
		public void setWorkPlan(String workPlan) {
			this.workPlan = workPlan;
		}
		public String getMemo() {
			return memo;
		}
		public void setMemo(String memo) {
			this.memo = memo;
		}

		public String getWorkPointAndRequirements() {
			return workPointAndRequirements;
		}

		public void setWorkPointAndRequirements(String workPointAndRequirements) {
			this.workPointAndRequirements = workPointAndRequirements;
		}

		public List<WoOkrWorkReportProcessLog> getProcessLogs() {
			return processLogs;
		}

		public void setProcessLogs(List<WoOkrWorkReportProcessLog> processLogs) {
			this.processLogs = processLogs;
		}

		public String getAdminSuperviseInfo() {
			return adminSuperviseInfo;
		}

		public void setAdminSuperviseInfo(String adminSuperviseInfo) {
			this.adminSuperviseInfo = adminSuperviseInfo;
		}

		public WoOkrWorkBaseInfo getWorkInfo() {
			return workInfo;
		}

		public void setWorkInfo(WoOkrWorkBaseInfo workInfo) {
			this.workInfo = workInfo;
		}

		public Boolean getNeedReportProgress() {
			return needReportProgress;
		}

		public void setNeedReportProgress(Boolean needReportProgress) {
			this.needReportProgress = needReportProgress;
		}
	}
	
	public static class WoOkrTaskReportEntity extends OkrTask{

		private static final long serialVersionUID = -5076990764713538973L;

		public static List<String> Excludes = new ArrayList<String>();
		
		private String name = null;
		
		private List<WoOkrWorkReportBaseInfo> reports = null;
		
		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public List<WoOkrWorkReportBaseInfo> getReports() {
			return reports;
		}

		public void setReports(List<WoOkrWorkReportBaseInfo> reports) {
			this.reports = reports;
		}

		public void addReports( WoOkrWorkReportBaseInfo wrapOutOkrWorkReportBaseInfo) {
			if( wrapOutOkrWorkReportBaseInfo == null ){
				return ;
			}
			
			if( reports == null ){
				reports = new ArrayList<WoOkrWorkReportBaseInfo>();
			}
			
			if( !reports.contains( wrapOutOkrWorkReportBaseInfo )){
				reports.add( wrapOutOkrWorkReportBaseInfo );
			}
		}
	}
	
	public static class WoOkrTaskCollectList extends OkrTask{

		private static final long serialVersionUID = -5076990764713538973L;

		public static List<String> Excludes = new ArrayList<String>();
		
		private List<String> unitNames = null;
		
		private List<WoOkrTaskReportEntity> reportInfos = null;	
		
		public List<String> getUnitNames() {
			return unitNames;
		}
		
		public void setUnitNames(List<String> unitNames) {
			this.unitNames = unitNames;
		}

		public List<WoOkrTaskReportEntity> getReportInfos() {
			return reportInfos;
		}

		public void setReportInfos(List<WoOkrTaskReportEntity> reportInfos) {
			this.reportInfos = reportInfos;
		}

		public void addUnitName( String unitName ) {
			if( unitName == null ){
				return ;
			}
			
			if( unitNames == null ){
				unitNames = new ArrayList<String>();
			}
			
			if( !unitNames.contains( unitName )){
				unitNames.add( unitName );
			}
		}
	}
	
	public static class WoOkrWorkReportProcessLog extends OkrWorkReportProcessLog{

		private static final long serialVersionUID = -5076990764713538973L;

		public static List<String> Excludes = new ArrayList<String>();

		private Long rank = 0L;

		public Long getRank() {
			return rank;
		}

		public void setRank(Long rank) {
			this.rank = rank;
		}
		
	}
	
	public static class WoOkrWorkBaseInfo extends OkrWorkBaseInfo  {
		
		private static final long serialVersionUID = -5076990764713538973L;
		
		public static WrapCopier<OkrWorkBaseInfo, WoOkrWorkBaseInfo> copier = WrapCopierFactory.wo( OkrWorkBaseInfo.class, WoOkrWorkBaseInfo.class, null,JpaObject.FieldsInvisible);
		
		private List< WoOkrWorkBaseInfo > subWrapOutOkrWorkBaseInfos = null;
		
		private List< WoOkrWorkAuthorizeRecord > okrWorkAuthorizeRecords = null;
		
		private WoOkrWorkAuthorizeRecord okrWorkAuthorizeRecord = null;
		
		private String workOutType = "SUBWORK";
		private String workDetail = null;
		private String dutyDescription = null;
		private String landmarkDescription = null;
		private String majorIssuesDescription = null;
		private String progressAction = null;
		private String progressPlan = null;
		private String resultDescription = null;
	    private Boolean hasNoneSubmitReport = false;
		private Long rank = 0L;

		public Long getRank() {
			return rank;
		}

		public void setRank(Long rank) {
			this.rank = rank;
		}
		
		public List<WoOkrWorkBaseInfo> getSubWrapOutOkrWorkBaseInfos() {
			return subWrapOutOkrWorkBaseInfos;
		}

		public void setSubWrapOutOkrWorkBaseInfos(List<WoOkrWorkBaseInfo> subWrapOutOkrWorkBaseInfos) {
			this.subWrapOutOkrWorkBaseInfos = subWrapOutOkrWorkBaseInfos;
		}

		public void addNewSubWorkBaseInfo(WoOkrWorkBaseInfo workBaseInfo) {
			if( this.subWrapOutOkrWorkBaseInfos == null ){
				this.subWrapOutOkrWorkBaseInfos = new ArrayList<WoOkrWorkBaseInfo>();
			}
			if( !subWrapOutOkrWorkBaseInfos.contains( workBaseInfo )){
				subWrapOutOkrWorkBaseInfos.add( workBaseInfo );
			}
		}

		public String getWorkDetail() {
			return workDetail;
		}

		public void setWorkDetail(String workDetail) {
			this.workDetail = workDetail;
		}

		public String getDutyDescription() {
			return dutyDescription;
		}

		public void setDutyDescription(String dutyDescription) {
			this.dutyDescription = dutyDescription;
		}

		public String getLandmarkDescription() {
			return landmarkDescription;
		}

		public void setLandmarkDescription(String landmarkDescription) {
			this.landmarkDescription = landmarkDescription;
		}

		public String getMajorIssuesDescription() {
			return majorIssuesDescription;
		}

		public void setMajorIssuesDescription(String majorIssuesDescription) {
			this.majorIssuesDescription = majorIssuesDescription;
		}

		public String getProgressAction() {
			return progressAction;
		}

		public void setProgressAction(String progressAction) {
			this.progressAction = progressAction;
		}

		public String getProgressPlan() {
			return progressPlan;
		}

		public void setProgressPlan(String progressPlan) {
			this.progressPlan = progressPlan;
		}

		public String getResultDescription() {
			return resultDescription;
		}

		public void setResultDescription(String resultDescription) {
			this.resultDescription = resultDescription;
		}

		/**
		 * 判断是父级工作还是子工作
		 * @return
		 */
		public String getWorkOutType() {
			return workOutType;
		}

		/**
		 * 判断是父级工作还是子工作
		 * @param workOutType
		 */
		public void setWorkOutType(String workOutType) {
			this.workOutType = workOutType;
		}

		public List<WoOkrWorkAuthorizeRecord> getOkrWorkAuthorizeRecords() {
			return okrWorkAuthorizeRecords;
		}

		public void setOkrWorkAuthorizeRecords(List<WoOkrWorkAuthorizeRecord> okrWorkAuthorizeRecords) {
			this.okrWorkAuthorizeRecords = okrWorkAuthorizeRecords;
		}

		public WoOkrWorkAuthorizeRecord getOkrWorkAuthorizeRecord() {
			return okrWorkAuthorizeRecord;
		}

		public void setOkrWorkAuthorizeRecord(WoOkrWorkAuthorizeRecord okrWorkAuthorizeRecord) {
			this.okrWorkAuthorizeRecord = okrWorkAuthorizeRecord;
		}

		public Boolean getHasNoneSubmitReport() {
			return hasNoneSubmitReport;
		}

		public void setHasNoneSubmitReport(Boolean hasNoneSubmitReport) {
			this.hasNoneSubmitReport = hasNoneSubmitReport;
		}
		
	}
	
	public static class WoOkrWorkAuthorizeRecord extends OkrWorkAuthorizeRecord{

		private static final long serialVersionUID = -5076990764713538973L;

		public static List<String> Excludes = new ArrayList<String>();

		private Long rank = 0L;

		public Long getRank() {
			return rank;
		}

		public void setRank(Long rank) {
			this.rank = rank;
		}
		
	}
}
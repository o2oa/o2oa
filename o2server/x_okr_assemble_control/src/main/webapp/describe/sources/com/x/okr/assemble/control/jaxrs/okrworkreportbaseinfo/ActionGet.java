package com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo;

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
import com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo.exception.ExceptionGetOkrUserCache;
import com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo.exception.ExceptionReportProcessLogList;
import com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo.exception.ExceptionSystemConfigQueryByCode;
import com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo.exception.ExceptionUserNoLogin;
import com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo.exception.ExceptionWorkNotExists;
import com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo.exception.ExceptionWorkQueryById;
import com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo.exception.ExceptionWorkReportIdEmpty;
import com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo.exception.ExceptionWorkReportQueryById;
import com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo.exception.ExceptionWorkReportWrapOut;
import com.x.okr.entity.OkrWorkBaseInfo;
import com.x.okr.entity.OkrWorkReportBaseInfo;
import com.x.okr.entity.OkrWorkReportDetailInfo;
import com.x.okr.entity.OkrWorkReportProcessLog;

public class ActionGet extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger( ActionGet.class );
	
	protected ActionResult<Wo> execute( HttpServletRequest request,EffectivePerson effectivePerson, String id ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wo wrap = null;
		OkrWorkBaseInfo okrWorkBaseInfo = null;
		OkrWorkReportBaseInfo okrWorkReportBaseInfo = null;
		OkrWorkReportDetailInfo okrWorkReportDetailInfo  = null;
		List<OkrWorkReportProcessLog> okrWorkReportProcessLogList = null;
		String workAdminIdentity = null;
		String report_progress = "CLOSE";
		List<String> ids = null;
		Boolean check = true;
		OkrUserCache  okrUserCache  = null;
		
		try {
			okrUserCache = okrUserInfoService.getOkrUserCacheWithPersonName( effectivePerson.getDistinguishedName() );
		}catch(Exception e){
			check = false;
			Exception exception = new ExceptionGetOkrUserCache( e, effectivePerson.getDistinguishedName() );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}
		
		if( check && okrUserCache == null ){
			check = false;
			Exception exception = new ExceptionUserNoLogin( effectivePerson.getDistinguishedName() );
			result.error( exception );
		}
		
		if( check ){
			try {
				//是否汇报工作的进展进度数字
				report_progress = okrConfigSystemService.getValueWithConfigCode( "REPORT_PROGRESS" );
				if( report_progress == null || report_progress.isEmpty() ){
					report_progress = "CLOSE";
				}
			} catch (Exception e) {
				report_progress = "CLOSE";
				logger.warn( "system get config got an exception." );
				logger.error(e);
			}
		}
		
		if( id == null || id.isEmpty() ){
			check = false;
			Exception exception = new ExceptionWorkReportIdEmpty();
			result.error( exception );
		}
		
		if( check ){
			if ( okrUserCache.getLoginUserName() == null ) {
				check = false;
				Exception exception = new ExceptionUserNoLogin( effectivePerson.getDistinguishedName() );
				result.error( exception );
			}
		}
		
		if( check ){
			try {
				okrWorkReportBaseInfo = okrWorkReportQueryService.get( id );
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionWorkReportQueryById( e, id );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		
		if( check ){
			if( okrWorkReportBaseInfo != null ){
				try {
					wrap = Wo.copier.copy( okrWorkReportBaseInfo );
				} catch (Exception e) {
					check = false;
					Exception exception = new ExceptionWorkReportWrapOut( e );
					result.error( exception );
					logger.error( e, effectivePerson, request, null);
				}
			}
		}
		
		try {
			okrWorkBaseInfo = okrWorkBaseInfoService.get( wrap.getWorkId() );
			if( okrWorkBaseInfo == null ){
				check = false;
				Exception exception = new ExceptionWorkNotExists( wrap.getWorkId() );
				result.error( exception );
			}
		} catch (Exception e) {
			check = false;
			Exception exception = new ExceptionWorkQueryById( e, id );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);	
		}
		
		// 查询汇报详细信息
		if( check ){
			if( okrWorkReportBaseInfo != null ){
				try {
					okrWorkReportDetailInfo = okrWorkReportDetailInfoService.get( id );
					if( okrWorkReportDetailInfo != null ){
						wrap.setWorkPlan( okrWorkReportDetailInfo.getWorkPlan() );
						wrap.setWorkPointAndRequirements( okrWorkReportDetailInfo.getWorkPointAndRequirements() );
						wrap.setProgressDescription( okrWorkReportDetailInfo.getProgressDescription() );
						wrap.setAdminSuperviseInfo( okrWorkReportDetailInfo.getAdminSuperviseInfo() );
					}
				} catch (Exception e) {
					logger.warn( "system get okrWorkReportDetailInfo got an exception" );
					logger.error(e);
				}
			}
		}
		//查询所有的审批日志
		if( check ){
			if( okrWorkReportBaseInfo != null ){
				try {
					ids = okrWorkReportProcessLogService.listByReportId( id );
					if( ids !=null ){
						okrWorkReportProcessLogList = okrWorkReportProcessLogService.list( ids );
						if( okrWorkReportProcessLogList != null ){
							wrap.setProcessLogs( WoOkrWorkReportProcessLog.copier.copy( okrWorkReportProcessLogList ) );
						}
					}
				} catch (Exception e) {
					Exception exception = new ExceptionReportProcessLogList( e, id );
					result.error( exception );
					logger.error( e, effectivePerson, request, null);	
				}
			}
		}
		
		if( check ){
			if( okrWorkReportBaseInfo != null ){
				try {
					workAdminIdentity = okrConfigSystemService.getValueWithConfigCode( "REPORT_SUPERVISOR" );
				} catch (Exception e) {
					check = false;
					Exception exception = new ExceptionSystemConfigQueryByCode( e, "REPORT_SUPERVISOR" );
					result.error( exception );
					logger.error( e, effectivePerson, request, null);
				}
			}
		}
		
		if( check ){
			//判断当前处理人是什么身份
			if( wrap.getCreatorIdentity() != null && okrUserCache.getLoginIdentityName() .equalsIgnoreCase( wrap.getCreatorIdentity())){
				wrap.setIsCreator( true );
			}
			if( wrap.getReporterIdentity() != null && okrUserCache.getLoginIdentityName() .equalsIgnoreCase( wrap.getReporterIdentity())){
				wrap.setIsReporter(true);
			}
			if( "ADMIN_AND_ALLLEADER".equals( wrap.getReportWorkflowType() )){
				//从汇报审阅领导里进行比对
				if( 	ListTools.isNotEmpty( wrap.getReadLeadersIdentityList() ) && wrap.getReadLeadersIdentityList().contains( okrUserCache.getLoginIdentityName() ) ){
					wrap.setIsReadLeader( true );
				}
			}else if( "DEPLOYER".equals( wrap.getReportWorkflowType() ) ){
				if( okrWorkBaseInfo != null ){
					//对比当前工作的部署者是否是当前用户
					if( okrWorkBaseInfo.getDeployerIdentity() != null && okrWorkBaseInfo.getDeployerIdentity().equalsIgnoreCase( okrUserCache.getLoginIdentityName()  ) ){
						wrap.setIsReadLeader( true );
					}
				}
			}
			
			if( workAdminIdentity != null && !workAdminIdentity.isEmpty() && okrUserCache.getLoginIdentityName() .equalsIgnoreCase( workAdminIdentity )){
				wrap.setIsWorkAdmin( true );
			}
			
			String workDetail = okrWorkDetailInfoService.getWorkDetailWithId( wrap.getWorkId() );
			if( workDetail != null && !workDetail.isEmpty() ){
				wrap.setTitle( workDetail );
			}
			if( "OPEN".equals( report_progress )){
				wrap.setNeedReportProgress( true );
			}else{
				wrap.setNeedReportProgress( false );
			}
			result.setData(wrap);
		}
		return result;
	}
	
	public static class Wo extends OkrWorkReportBaseInfo{

		private static final long serialVersionUID = -5076990764713538973L;

		public static List<String> Excludes = new ArrayList<String>();
		
		public static WrapCopier<OkrWorkReportBaseInfo, Wo> copier = WrapCopierFactory.wo( OkrWorkReportBaseInfo.class, Wo.class, null, JpaObject.FieldsInvisible);
		
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
	
}
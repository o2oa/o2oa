package com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.okr.assemble.control.OkrUserCache;
import com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo.exception.ExceptionCenterWorkNotExists;
import com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo.exception.ExceptionCenterWorkQueryById;
import com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo.exception.ExceptionGetOkrUserCache;
import com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo.exception.ExceptionUserNoLogin;
import com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo.exception.ExceptionUserUnitQuery;
import com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo.exception.ExceptionWorkNotExists;
import com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo.exception.ExceptionWorkQueryById;
import com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo.exception.ExceptionWorkReportMaxReportCountQuery;
import com.x.okr.entity.OkrCenterWorkInfo;
import com.x.okr.entity.OkrWorkBaseInfo;
import com.x.okr.entity.OkrWorkReportBaseInfo;

public class ActionDraftReport extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger( ActionDraftReport.class );

	protected ActionResult<Wo> execute( HttpServletRequest request,EffectivePerson effectivePerson, String workId ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wo wrap = null;
		OkrCenterWorkInfo okrCenterWorkInfo = null;
		OkrWorkBaseInfo okrWorkBaseInfo = null;
		Integer maxReportCount = null;
		String report_progress = "CLOSE";
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
		if( check && okrUserCache == null ){
			check = false;
			Exception exception = new ExceptionUserNoLogin( effectivePerson.getDistinguishedName() );
			result.error( exception );
			//logger.error( e, effectivePerson, request, null);
		}
		//对wrapIn里的信息进行校验
		//先根据workId获取该工作汇报的草稿信息，如果有，则直接展示内容，如果没有则进行新建操作
		wrap = new Wo();
		//设置当前登录用户为创建工作汇报的用户
		wrap.setCreatorName( effectivePerson.getDistinguishedName() );
		if( check ){
			try {
				wrap.setCreatorIdentity( okrUserManagerService.getIdentityWithPerson(effectivePerson.getDistinguishedName()) );
			} catch ( Exception e ) {
				check = false;
				Exception exception = new ExceptionUserUnitQuery( e, effectivePerson.getDistinguishedName() );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		
		if( check ){
			try {
				wrap.setCreatorUnitName( okrUserManagerService.getUnitNameByIdentity( wrap.getCreatorIdentity()));
			} catch ( Exception e ) {
				check = false;
				Exception exception = new ExceptionUserUnitQuery( e, wrap.getCreatorIdentity() );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		
		if( check ){
			try {
				wrap.setCreatorTopUnitName( okrUserManagerService.getTopUnitNameByIdentity( wrap.getCreatorIdentity() ));
			} catch ( Exception e ) {
				check = false;
				Exception exception = new ExceptionUserUnitQuery( e, wrap.getCreatorIdentity() );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		
		if( check ){
			//校验汇报者姓名
			wrap.setReporterName( okrUserCache.getLoginUserName() );
			wrap.setReporterIdentity( okrUserCache.getLoginIdentityName() );
			wrap.setReporterUnitName( okrUserCache.getLoginUserUnitName() );
			wrap.setReporterTopUnitName( okrUserCache.getLoginUserTopUnitName() );
			
			List<String>  identityList = new ArrayList<>();
			List<String>nameList = new ArrayList<>();
			List<String>unitList = new ArrayList<>();
			List<String>topUnitList = new ArrayList<>();
			
			nameList.add( okrUserCache.getLoginUserName() );
			identityList.add( okrUserCache.getLoginIdentityName() );
			unitList.add( okrUserCache.getLoginUserUnitName() );
			topUnitList.add( okrUserCache.getLoginUserTopUnitName() );
			
			wrap.setCurrentProcessorNameList( nameList );
			wrap.setCurrentProcessorIdentityList( identityList );
			wrap.setCurrentProcessorUnitNameList( unitList );
			wrap.setCurrentProcessorTopUnitNameList( topUnitList );
		}		
		//补充工作标题
		if( check ){
			try {
				wrap.setWorkId( workId );
				okrWorkBaseInfo = okrWorkBaseInfoService.get( workId );
				if( okrWorkBaseInfo != null ){
					wrap.setWorkType( okrWorkBaseInfo.getWorkType() );
					wrap.setWorkTitle( okrWorkBaseInfo.getTitle() );
				}else{
					check = false;
					Exception exception = new ExceptionWorkNotExists( workId );
					result.error( exception );
					//logger.error( e, effectivePerson, request, null);
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionWorkQueryById( e, workId );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);			
			}
		}
		
		//补充中心工作信息
		if( check ){
			try {
				okrCenterWorkInfo = okrCenterWorkInfoService.get( okrWorkBaseInfo.getCenterId() );
				if( okrCenterWorkInfo != null ){
					wrap.setCenterId( okrCenterWorkInfo.getId() );
					wrap.setCenterTitle( okrCenterWorkInfo.getTitle() );
				}else{
					check = false;
					Exception exception = new ExceptionCenterWorkNotExists( okrWorkBaseInfo.getCenterId() );
					result.error( exception );
					//logger.error( e, effectivePerson, request, null);	
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionCenterWorkQueryById( e, okrWorkBaseInfo.getCenterId() );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		
		if( check ){
			try {
				maxReportCount = okrWorkReportOperationService.getMaxReportCount( okrWorkBaseInfo.getId() );
				wrap.setReportCount( ( maxReportCount + 1 ) );
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionWorkReportMaxReportCountQuery( e, okrWorkBaseInfo.getCenterId() );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		if( check ){
			//草稿|管理员督办|领导批示|已完成
			wrap.setProcessStatus( "草稿" );
			wrap.setStatus( "正常" );
			//根据已知信息组织汇报标题和汇简要标题
			wrap.setTitle(  okrWorkBaseInfo.getTitle() );
			wrap.setShortTitle( "第" + wrap.getReportCount() + "次工作汇报" );
		}
		wrap.setIsCreator(true);
		wrap.setIsReporter(true);
		
		if( "OPEN".equals( report_progress )){
			wrap.setNeedReportProgress( true );
		}else{
			wrap.setNeedReportProgress( false );
		}
		
		result.setData( wrap );
		return result;
	}
	
	public static class Wo extends OkrWorkReportBaseInfo{

		private static final long serialVersionUID = -5076990764713538973L;

		public static List<String> Excludes = new ArrayList<String>();
		
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
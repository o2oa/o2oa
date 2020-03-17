package com.x.okr.assemble.control.jaxrs.okrcenterworkinfo;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.okr.assemble.control.OkrUserCache;
import com.x.okr.assemble.control.jaxrs.WorkCommonSearchFilter;
import com.x.okr.assemble.control.jaxrs.okrcenterworkinfo.exception.ExceptionCenterWorkWrapOut;
import com.x.okr.assemble.control.jaxrs.okrcenterworkinfo.exception.ExceptionGetOkrUserCache;
import com.x.okr.assemble.control.jaxrs.okrcenterworkinfo.exception.ExceptionUserNoLogin;
import com.x.okr.entity.OkrCenterWorkInfo;

public class ActionListByProcessIdentityPrevWithFilter extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger( ActionListByProcessIdentityPrevWithFilter.class );
	
	protected ActionResult<List<Wo>> execute( HttpServletRequest request, EffectivePerson effectivePerson, String id, Integer count, WorkCommonSearchFilter wrapIn ) throws Exception {
		ActionResult<List<Wo>> result = new ActionResult<>();
		List<Wo> wraps = null;
		List<OkrCenterWorkInfo> okrCenterWorkInfoList = null;
		Long total = 0L;
		List<String> processIdentities = null;
		Boolean check = true;		
		OkrUserCache  okrUserCache  = null;
		try {
			okrUserCache = okrUserInfoService.getOkrUserCacheWithPersonName( effectivePerson.getDistinguishedName() );
		} catch (Exception e) {
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
		if(check){
			wrapIn.addQueryEmployeeIdentities( okrUserCache.getLoginIdentityName() );
			okrCenterWorkInfoList = okrCenterWorkQueryService.listCenterPrevWithFilter( id, count, wrapIn );
			total = okrCenterWorkQueryService.getCenterCountWithFilter( wrapIn );
		}
		
		if(check){
			if( okrCenterWorkInfoList != null && !okrCenterWorkInfoList.isEmpty() ){
				try{
					wraps = Wo.copier.copy( okrCenterWorkInfoList );
				}catch(Exception e){
					check = false;
					Exception exception = new ExceptionCenterWorkWrapOut( e );
					result.error( exception );
					logger.error( e, effectivePerson, request, null);
				}
			}
			if(check){
				if( wraps != null && !wraps.isEmpty() ){
					for( Wo wrap : wraps ){
						processIdentities = new ArrayList<>();
						processIdentities.add( "VIEW" );
						if( wrap.getReportAuditLeaderIdentity() != null && !wrap.getReportAuditLeaderIdentity().isEmpty() ){
							if( wrap.getReportAuditLeaderIdentity().indexOf( okrUserCache.getLoginIdentityName() ) > 0 ){
								processIdentities.add("REPORTAUDIT");//汇报审核领导
							}
						}
						if ( okrWorkProcessIdentityService.isMyDeployCenter( okrUserCache.getLoginIdentityName(), wrap.getId() )){
							processIdentities.add("DEPLOY");//判断工作是否由我阅知
						}
						if ( okrWorkProcessIdentityService.isMyReadCenter( okrUserCache.getLoginIdentityName(), wrap.getId() )){
							processIdentities.add("READ");//判断工作是否由我阅知
						}
						List<String> operations = new ActionListOperationWithId().execute( request, effectivePerson, okrUserCache, wrap.getId() );
						wrap.setOperation( operations );
						wrap.setWorkProcessIdentity( processIdentities );
					}
				}
				result.setCount( total );
				result.setData( wraps );
			}
		}else{
			result.setCount( 0L );
			result.setData( new ArrayList<Wo>() );
		}
		
		return result;
	}
	
	public static class Wo  {

		public static List<String> Excludes = new ArrayList<String>();
		
		public static WrapCopier<OkrCenterWorkInfo, Wo> copier = WrapCopierFactory.wo( OkrCenterWorkInfo.class, Wo.class, null, JpaObject.FieldsInvisible);
		
		@FieldDescribe( "中心工作ID" )
		private String id = "";
		
		@FieldDescribe( "中心标题" )
		private String title = "";
		
		@FieldDescribe( "部署者姓名" )
		private String deployerName = "";
		
		@FieldDescribe( "部署者身份" )
		private String deployerIdentity = "";
		
		@FieldDescribe( "部署者所属组织" )
		private String deployerUnitName = "";
		
		@FieldDescribe( "部署者所属顶层组织" )
		private String deployerTopUnitName = "";
		
		@FieldDescribe( "审核者姓名" )
		private String auditLeaderName = "";
		
		@FieldDescribe( "审核者身份" )
		private String auditLeaderIdentity = "";
		
		@FieldDescribe( "中心工作处理状态：草稿|待审核|待确认|执行中|已完成|已撤消" )
		private String processStatus = "草稿";
		
		@FieldDescribe( "中心工作默认完成日期-字符串，显示用：yyyy-mm-dd" )
		private String defaultCompleteDateLimitStr = "";
		
		@FieldDescribe( "中心工作默认工作类别" )
		private String defaultWorkType = "";
		
		@FieldDescribe( "中心工作默认工作级别" )
		private String defaultWorkLevel = "";
		
		@FieldDescribe( "中心工作默认阅知领导(可多值，显示用)" )
		private String defaultLeader = "";
		
		@FieldDescribe( "中心工作默认阅知领导身份(可多值，计算组织和顶层组织用)" )
		private String defaultLeaderIdentity = "";
		
		@FieldDescribe( "工作汇报审批领导(可多值，显示用)" )
		private String reportAuditLeaderName = "";
		
		@FieldDescribe( "工作汇报审批领导身份(可多值，计算组织和顶层组织用)" )
		private String reportAuditLeaderIdentity = "";
		
		@FieldDescribe( "中心工作是否需要审核" )
		private Boolean isNeedAudit = false;

		@FieldDescribe( "处理状态：正常|已删除" )
		private String status = "正常";
		
		@FieldDescribe( "中心工作描述" )
		private String description = "";
		
		@FieldDescribe( "工作处理职责身份(多值): VIEW(观察者)|DEPLOY(部署者)|RESPONSIBILITY(责任者)|COOPERATE(协助者)|READ(阅知者)|REPORTAUDIT(汇报审核者)" )
		private List<String> workProcessIdentity = null;
		
		@FieldDescribe( "用户可以对工作进行的操作(多值):VIEW|EDIT|DELETE" )
		private List<String> operation = null;
		
		private Boolean watch = false;

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public String getDeployerName() {
			return deployerName;
		}

		public void setDeployerName(String deployerName) {
			this.deployerName = deployerName;
		}

		public String getDeployerIdentity() {
			return deployerIdentity;
		}

		public void setDeployerIdentity(String deployerIdentity) {
			this.deployerIdentity = deployerIdentity;
		}

		public String getDeployerUnitName() {
			return deployerUnitName;
		}

		public void setDeployerUnitName(String deployerUnitName) {
			this.deployerUnitName = deployerUnitName;
		}

		public String getDeployerTopUnitName() {
			return deployerTopUnitName;
		}

		public void setDeployerTopUnitName(String deployerTopUnitName) {
			this.deployerTopUnitName = deployerTopUnitName;
		}

		public String getAuditLeaderName() {
			return auditLeaderName;
		}

		public void setAuditLeaderName(String auditLeaderName) {
			this.auditLeaderName = auditLeaderName;
		}

		public String getAuditLeaderIdentity() {
			return auditLeaderIdentity;
		}

		public void setAuditLeaderIdentity(String auditLeaderIdentity) {
			this.auditLeaderIdentity = auditLeaderIdentity;
		}

		public String getProcessStatus() {
			return processStatus;
		}

		public void setProcessStatus(String processStatus) {
			this.processStatus = processStatus;
		}

		public String getDefaultCompleteDateLimitStr() {
			return defaultCompleteDateLimitStr;
		}

		public void setDefaultCompleteDateLimitStr(String defaultCompleteDateLimitStr) {
			this.defaultCompleteDateLimitStr = defaultCompleteDateLimitStr;
		}

		public String getDefaultWorkType() {
			return defaultWorkType;
		}

		public void setDefaultWorkType(String defaultWorkType) {
			this.defaultWorkType = defaultWorkType;
		}

		public String getDefaultWorkLevel() {
			return defaultWorkLevel;
		}

		public void setDefaultWorkLevel(String defaultWorkLevel) {
			this.defaultWorkLevel = defaultWorkLevel;
		}

		public String getDefaultLeader() {
			return defaultLeader;
		}

		public void setDefaultLeader(String defaultLeader) {
			this.defaultLeader = defaultLeader;
		}

		public String getDefaultLeaderIdentity() {
			return defaultLeaderIdentity;
		}

		public void setDefaultLeaderIdentity(String defaultLeaderIdentity) {
			this.defaultLeaderIdentity = defaultLeaderIdentity;
		}

		public String getReportAuditLeaderName() {
			return reportAuditLeaderName;
		}

		public void setReportAuditLeaderName(String reportAuditLeaderName) {
			this.reportAuditLeaderName = reportAuditLeaderName;
		}

		public String getReportAuditLeaderIdentity() {
			return reportAuditLeaderIdentity;
		}

		public void setReportAuditLeaderIdentity(String reportAuditLeaderIdentity) {
			this.reportAuditLeaderIdentity = reportAuditLeaderIdentity;
		}

		public Boolean getIsNeedAudit() {
			return isNeedAudit;
		}

		public void setIsNeedAudit(Boolean isNeedAudit) {
			this.isNeedAudit = isNeedAudit;
		}

		public String getStatus() {
			return status;
		}

		public void setStatus(String status) {
			this.status = status;
		}

		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public Boolean getWatch() {
			return watch;
		}

		public void setWatch(Boolean watch) {
			this.watch = watch;
		}

		public List<String> getWorkProcessIdentity() {
			return workProcessIdentity;
		}

		public void setWorkProcessIdentity(List<String> workProcessIdentity) {
			this.workProcessIdentity = workProcessIdentity;
		}

		public List<String> getOperation() {
			return operation;
		}

		public void setOperation(List<String> operation) {
			this.operation = operation;
		}
		
	}
}
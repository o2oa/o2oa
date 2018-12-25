package com.x.okr.assemble.control.jaxrs.okrconfigsystem;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.google.gson.JsonElement;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.okr.assemble.common.date.DateOperation;
import com.x.okr.assemble.control.OkrUserCache;
import com.x.okr.assemble.control.jaxrs.okrconfigsystem.exception.ExceptionGetOkrUserCache;
import com.x.okr.assemble.control.jaxrs.okrconfigsystem.exception.ExceptionSystemConfigSave;
import com.x.okr.assemble.control.jaxrs.okrconfigsystem.exception.ExceptionUserNoLogin;
import com.x.okr.assemble.control.jaxrs.okrconfigsystem.exception.ExceptionWrapInConvert;
import com.x.okr.assemble.control.jaxrs.queue.WrapInWorkDynamic;
import com.x.okr.entity.OkrConfigSystem;
import com.x.okr.entity.OkrWorkBaseInfo;

public class ActionSave extends BaseAction {
	
	private DateOperation dateOperation = new DateOperation();
	private static  Logger logger = LoggerFactory.getLogger( ActionSave.class );
	
	protected ActionResult<Wo> execute( HttpServletRequest request,EffectivePerson effectivePerson, JsonElement jsonElement ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		OkrConfigSystem okrConfigSystem = null;
		Boolean check = true;		
		Wi wrapIn = null;
		OkrUserCache  okrUserCache  = null;
		
		try {
			wrapIn = this.convertToWrapIn( jsonElement, Wi.class );
		} catch (Exception e ) {
			check = false;
			Exception exception = new ExceptionWrapInConvert( e, jsonElement );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}
		
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
		if( wrapIn != null && check ){
			try {
				okrConfigSystem = okrConfigSystemService.save( wrapIn );
				result.setData( new Wo( okrConfigSystem.getId() ));
				
				ApplicationCache.notify( OkrConfigSystem.class );
				
				if( okrConfigSystem != null ) {
					WrapInWorkDynamic.sendWithSystemConfig( 
							okrConfigSystem, 
							effectivePerson.getDistinguishedName(),
							okrUserCache.getLoginUserName(),
							okrUserCache.getLoginUserName(),
							"修改系统配置",
							"系统配置修改保存成功！"
					);
				}
				
			} catch (Exception e) {
				Exception exception = new ExceptionSystemConfigSave( e );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		
		if( check ){
			if( "REPORT_CREATETIME".equals( okrConfigSystem.getConfigCode() )){
				logger.info( "report_createtime has modified, next report for all processing work will be checked." );
				updateReportTimeForWorks( okrConfigSystem.getConfigValue() );
			}
		}
		
		return result;
	}

	/**
	 * 修改了汇报生成时间，需要重新计算所有工作的下一次汇报时间
	 * @param reportStartTime
	 * @return
	 * @throws Exception
	 */
	private Boolean updateReportTimeForWorks( String reportStartTime ) throws Exception {
		List<String> ids = null;
		Integer total = 0;
		Integer count = 0;
		OkrWorkBaseInfo okrWorkBaseInfo  = null;
		if( reportStartTime == null || reportStartTime.isEmpty()  ){
			reportStartTime = "10:00:00";
		}
		try {
			ids = okrWorkBaseInfoQueryService.listAllProcessingWorkIds();
			if ( ids != null && ids.size() > 0 ) {
				total = ids.size();
				for ( String id : ids ) {
					count++;
					logger.info( "[" + count + "/" + total + "]system trying to check next report time for work:" + id + "......" );
					okrWorkBaseInfo = okrWorkBaseInfoQueryService.get( id );
					if( okrWorkBaseInfo != null ){
						updateReportTimeForWork( okrWorkBaseInfo, reportStartTime );
					}else{
						logger.info( "[" + count + "/" + total + "]work:{"+ id +"} not exists." );
					}
				}
			}
		} catch (Exception e) {
			logger.warn("system list work ids what needs report new got an exception." );
			logger.error(e);
		}	
		return true;
	}
	
	private Boolean updateReportTimeForWork( OkrWorkBaseInfo okrWorkBaseInfo, String reportStartTime ) throws Exception {
		//修改了汇报生成时间，需要重新计算所有工作的下一次汇报时间
		if( !"不汇报".equals( okrWorkBaseInfo.getReportCycle() ) &&  okrWorkBaseInfo.getReportCycle() != null  ){
			if( "每周汇报".equals( okrWorkBaseInfo.getReportCycle().trim() ) ){
				updateWeekReportTimeForWork( okrWorkBaseInfo, reportStartTime );
			}else if( "每月汇报".equals( okrWorkBaseInfo.getReportCycle().trim() ) ){
				updateMonthReportTimeForWork( okrWorkBaseInfo, reportStartTime );
			}
		}		
		return true;
	}
	
	private Boolean updateWeekReportTimeForWork( OkrWorkBaseInfo okrWorkBaseInfo, String reportStartTime ) throws Exception {
		Date nextReportTime = null;
		String reportTimeQue = null;
		if( okrWorkBaseInfo.getReportDayInCycle() >= 1 && okrWorkBaseInfo.getReportDayInCycle() <= 7 ){
			try {//每周1-7
				reportTimeQue = okrWorkBaseInfoQueryService.getReportTimeQue( 
						dateOperation.getDateFromString( okrWorkBaseInfo.getDeployDateStr() ), 
						okrWorkBaseInfo.getCompleteDateLimit(), 
						okrWorkBaseInfo.getReportCycle(), 
						okrWorkBaseInfo.getReportDayInCycle(), 
						reportStartTime
				);
				nextReportTime = okrWorkBaseInfoQueryService.getNextReportTime( reportTimeQue, okrWorkBaseInfo.getLastReportTime() );
				if( nextReportTime == null ){
					nextReportTime = okrWorkBaseInfoQueryService.getNextReportTime(okrWorkBaseInfo);
				}
				okrWorkBaseInfoOperationService.updateWorkReportTime( okrWorkBaseInfo.getId(), nextReportTime, reportTimeQue );
			} catch (Exception e) {
				logger.warn("系统根据汇报周期信息计算汇报时间序列时发生异常。DeployDate："+ okrWorkBaseInfo.getDeployDateStr()+", CompleteDateLimit:"+ okrWorkBaseInfo.getCompleteDateLimit()+", ReportCycle:"+ okrWorkBaseInfo.getReportCycle()+", ReportDayInCycle:" + okrWorkBaseInfo.getReportDayInCycle() + ", ReportStartTime:" + reportStartTime);
				e.printStackTrace();
			}
		}else{
			logger.warn( "report day in cycle is invalid（1~7），ReportDayInCycle：" + okrWorkBaseInfo.getReportDayInCycle() );
		}		
		return true;
	}
	
	private Boolean updateMonthReportTimeForWork( OkrWorkBaseInfo okrWorkBaseInfo, String reportStartTime ) throws Exception {
		Date nextReportTime = null;
		String reportTimeQue = null;
		if( okrWorkBaseInfo.getReportDayInCycle() >= 1 && okrWorkBaseInfo.getReportDayInCycle() <= 31 ){
			//每月1-31，如果选择的日期大于当月最大日期，那么默认定为当月最后一天
			try {
				reportTimeQue = okrWorkBaseInfoQueryService.getReportTimeQue( 
						dateOperation.getDateFromString( okrWorkBaseInfo.getDeployDateStr() ), 
						okrWorkBaseInfo.getCompleteDateLimit(), 
						okrWorkBaseInfo.getReportCycle(), 
						okrWorkBaseInfo.getReportDayInCycle(), 
						reportStartTime
				);
				nextReportTime = okrWorkBaseInfoQueryService.getNextReportTime( reportTimeQue, okrWorkBaseInfo.getLastReportTime() );
				if( nextReportTime == null ){
					nextReportTime = okrWorkBaseInfoQueryService.getNextReportTime(okrWorkBaseInfo);
				}
				okrWorkBaseInfoOperationService.updateWorkReportTime( okrWorkBaseInfo.getId(), nextReportTime, reportTimeQue );
			} catch (Exception e) {
				logger.warn("系统根据汇报周期信息计算汇报时间序列时发生异常。DeployDate："+ okrWorkBaseInfo.getDeployDateStr()+", CompleteDateLimit:"+ okrWorkBaseInfo.getCompleteDateLimit()+", ReportCycle:"+ okrWorkBaseInfo.getReportCycle()+", ReportDayInCycle:" + okrWorkBaseInfo.getReportDayInCycle() + ", ReportStartTime:" + reportStartTime);
				throw e;
			}
		}else{
			logger.warn("工作的工作日期不合法（1~31），ReportDayInCycle：" + okrWorkBaseInfo.getReportDayInCycle() );
		}	
		return true;
	}
	
	public static class Wi extends OkrConfigSystem {

		private static final long serialVersionUID = -5076990764713538973L;

		public static List<String> Excludes = new ArrayList<String>(JpaObject.FieldsUnmodify);

	}
	
	public static class Wo extends WoId {
		public Wo( String id ) {
			this.setId( id );
		}
	}
}
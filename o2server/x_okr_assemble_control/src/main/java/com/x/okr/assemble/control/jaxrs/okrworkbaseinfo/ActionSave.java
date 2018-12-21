package com.x.okr.assemble.control.jaxrs.okrworkbaseinfo;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import com.google.gson.JsonElement;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.okr.assemble.control.OkrUserCache;
import com.x.okr.assemble.control.jaxrs.okrworkbaseinfo.exception.ExceptionCenterWorkIdEmpty;
import com.x.okr.assemble.control.jaxrs.okrworkbaseinfo.exception.ExceptionCenterWorkNotExists;
import com.x.okr.assemble.control.jaxrs.okrworkbaseinfo.exception.ExceptionGetOkrUserCache;
import com.x.okr.assemble.control.jaxrs.okrworkbaseinfo.exception.ExceptionReportCycleInvalid;
import com.x.okr.assemble.control.jaxrs.okrworkbaseinfo.exception.ExceptionReportDayInCycleEmpty;
import com.x.okr.assemble.control.jaxrs.okrworkbaseinfo.exception.ExceptionReportDayInCycleInvalid;
import com.x.okr.assemble.control.jaxrs.okrworkbaseinfo.exception.ExceptionUserNoLogin;
import com.x.okr.assemble.control.jaxrs.okrworkbaseinfo.exception.ExceptionWorkBaseInfoProcess;
import com.x.okr.assemble.control.jaxrs.okrworkbaseinfo.exception.ExceptionWorkCompleteDateLimitEmpty;
import com.x.okr.assemble.control.jaxrs.okrworkbaseinfo.exception.ExceptionWorkCooperateInvalid;
import com.x.okr.assemble.control.jaxrs.okrworkbaseinfo.exception.ExceptionWorkDetailEmpty;
import com.x.okr.assemble.control.jaxrs.okrworkbaseinfo.exception.ExceptionWorkNotExists;
import com.x.okr.assemble.control.jaxrs.okrworkbaseinfo.exception.ExceptionWorkReadLeaderInvalid;
import com.x.okr.assemble.control.jaxrs.okrworkbaseinfo.exception.ExceptionWorkResponsibilityEmpty;
import com.x.okr.assemble.control.jaxrs.okrworkbaseinfo.exception.ExceptionWorkResponsibilityInvalid;
import com.x.okr.assemble.control.jaxrs.queue.WrapInWorkDynamic;
import com.x.okr.assemble.control.service.OkrUserManagerService;
import com.x.okr.assemble.control.service.OkrWorkBaseInfoOperationService;
import com.x.okr.entity.OkrCenterWorkInfo;
import com.x.okr.entity.OkrWorkBaseInfo;

public class ActionSave extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger( ActionSave.class );
	private OkrUserManagerService okrUserManagerService = new OkrUserManagerService();
	private OkrWorkBaseInfoOperationService okrWorkBaseInfoOperationService = new OkrWorkBaseInfoOperationService();
	
	protected ActionResult<Wo> execute( HttpServletRequest request,EffectivePerson effectivePerson, JsonElement jsonElement ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		OkrCenterWorkInfo okrCenterWorkInfo = null;
		OkrWorkBaseInfo okrWorkBaseInfo  = null;
		String reportStartTime = "10:00:00";
		Date reportStartTime_date = null;
		Date nextReportTime = null;
		String reportTimeQue = null;
		Boolean check = true;		
		WiOkrWorkBaseInfo wrapIn = null;
		OkrUserCache  okrUserCache  = null;
		
		try {
			wrapIn = this.convertToWrapIn( jsonElement, WiOkrWorkBaseInfo.class );
		} catch (Exception e ) {
			check = false;
			Exception exception = new ExceptionWorkBaseInfoProcess( e, "系统在将JSON信息转换为对象时发生异常。JSON:" + jsonElement.toString() );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}
		
		try {
			okrUserCache = okrUserInfoService.getOkrUserCacheWithPersonName( effectivePerson.getDistinguishedName() );
		} catch ( Exception e ) {
			check = false;
			Exception exception = new ExceptionGetOkrUserCache( e, effectivePerson.getDistinguishedName()  );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}
		
		if( check && ( okrUserCache == null || okrUserCache.getLoginIdentityName() == null ) ){
			check = false;
			Exception exception = new ExceptionUserNoLogin( effectivePerson.getDistinguishedName()  );
			result.error( exception );
		}
		
		//保存时wrapIn不能为空
		if( wrapIn != null ){			
			wrapIn.setWorkProcessStatus( "草稿" );			
			//这里需要去配置表里查询配置的汇报发起具体的时间点，REPORT_CREATETIME，如：10:00:00
			if( check ){
				try{
					reportStartTime = okrConfigSystemService.getValueWithConfigCode( "REPORT_CREATETIME" );
					if( reportStartTime == null || reportStartTime.isEmpty() ){
						reportStartTime = "10:00:00";
					}
				}catch(Exception e){
					reportStartTime = "10:00:00";
				}
			}
			//检查配置的有效性
			if( check ){
				try{
					reportStartTime_date = dateOperation.getDateFromString( reportStartTime );
					reportStartTime = dateOperation.getDateStringFromDate( reportStartTime_date, "HH:mm:ss" );
				}catch(Exception e ){
					reportStartTime = "10:00:00";
				}
			}
			
			//补充部署工作的年份和月份
			if( check ){
				wrapIn.setDeployYear( dateOperation.getYear( new Date() ));
				wrapIn.setDeployMonth( dateOperation.getMonth( new Date() ));				
				wrapIn.setDeployDateStr( dateOperation.getNowDateTime() );
			}
			
			//创建人和部署人信息直接取当前操作人和登录人身份
			if( check ){
				wrapIn.setCreatorName( effectivePerson.getDistinguishedName() );
				if( effectivePerson.getDistinguishedName().equals( okrUserCache.getLoginUserName())){
					wrapIn.setCreatorUnitName( okrUserCache.getLoginUserUnitName());
					wrapIn.setCreatorTopUnitName( okrUserCache.getLoginUserTopUnitName());
					wrapIn.setCreatorIdentity( okrUserCache.getLoginIdentityName() );
				}else{
					try{//需要查询创建者的相关身份
						wrapIn.setCreatorIdentity( okrUserManagerService.getIdentityWithPerson( effectivePerson.getDistinguishedName() ));
						wrapIn.setCreatorUnitName( okrUserManagerService.getUnitNameByIdentity( wrapIn.getCreatorIdentity() ));
						wrapIn.setCreatorTopUnitName( okrUserManagerService.getTopUnitNameByIdentity( wrapIn.getCreatorIdentity() ) );
					}catch(Exception e){
						check = false;
						Exception exception = new ExceptionWorkBaseInfoProcess( e, "系统通过操作用户查询用户身份和组织信息时发生异常!Person:" + effectivePerson.getDistinguishedName() );
						result.error( exception );
						logger.error( e, effectivePerson, request, null);
					}
				}
			}
			
			if( check ){
				wrapIn.setDeployerName( okrUserCache.getLoginUserName() );
				wrapIn.setDeployerUnitName( okrUserCache.getLoginUserUnitName());
				wrapIn.setDeployerTopUnitName( okrUserCache.getLoginUserTopUnitName());
				wrapIn.setDeployerIdentity( okrUserCache.getLoginIdentityName() );
			}
			
			if( check ){
				if( wrapIn.getWorkDetail() == null || wrapIn.getWorkDetail().isEmpty() ){
					check = false;
					Exception exception = new ExceptionWorkDetailEmpty();
					result.error( exception );
				}else{
					if( wrapIn.getWorkDetail().length() > 30 ){
						wrapIn.setTitle( wrapIn.getWorkDetail().substring( 0, 30 )+"..." );
					}else{
						wrapIn.setTitle( wrapIn.getWorkDetail() );
					}
				}
			}
			
			if( check ){
				if( wrapIn.getParentWorkId() != null && !wrapIn.getParentWorkId().isEmpty() ){
					try {
						okrWorkBaseInfo = okrWorkBaseInfoService.get( wrapIn.getParentWorkId() );
						if( okrWorkBaseInfo !=  null ){
							//补充上级工作标题
							wrapIn.setParentWorkTitle( okrWorkBaseInfo.getTitle() );
							wrapIn.setCenterId( okrWorkBaseInfo.getCenterId() );
						}else{
							check = false;
							Exception exception = new ExceptionWorkNotExists( wrapIn.getParentWorkId() );
							result.error( exception );
						}
					} catch (Exception e) {
						check = false;
						Exception exception = new ExceptionWorkBaseInfoProcess( e, "查询指定ID的具体工作信息时发生异常。ID：" + wrapIn.getParentWorkId() );
						result.error( exception );
						logger.error( e, effectivePerson, request, null);
					}
				}
			}
			
			//补充中心工作标题
			if( check ){
				if( wrapIn.getCenterId() != null && !wrapIn.getCenterId().isEmpty() ){
					//根据ID查询中心工作信息
					try {
						okrCenterWorkInfo = okrCenterWorkInfoService.get( wrapIn.getCenterId() );
						if( okrCenterWorkInfo !=  null ){
							wrapIn.setCenterTitle( okrCenterWorkInfo.getTitle() );
							if( wrapIn.getWorkType() == null || wrapIn.getWorkType().isEmpty() ){
								wrapIn.setWorkType( okrCenterWorkInfo.getDefaultWorkType());
							}
							if( wrapIn.getWorkLevel() == null || wrapIn.getWorkLevel().isEmpty() ){
								wrapIn.setWorkLevel( okrCenterWorkInfo.getDefaultWorkLevel());
							}
						}else{
							check = false;
							Exception exception = new ExceptionCenterWorkNotExists( wrapIn.getCenterId() );
							result.error( exception );
						}
					} catch (Exception e) {
						check = false;
						Exception exception = new ExceptionWorkBaseInfoProcess( e, "查询指定ID的中心工作信息时发生异常。ID：" + wrapIn.getCenterId() );
						result.error( exception );
						logger.error( e, effectivePerson, request, null);
					}
				}else{
					check = false;
					Exception exception = new ExceptionCenterWorkIdEmpty();
					result.error( exception );
				}
			}
			
			//校验工作完成时限数据，补充日期型完成时限数据
			if( check ){
				if( wrapIn.getCompleteDateLimitStr() != null && !wrapIn.getCompleteDateLimitStr().isEmpty() ) {
					try{
						wrapIn.setCompleteDateLimit( dateOperation.getDateFromString( wrapIn.getCompleteDateLimitStr() ) );
					}catch( Exception e ){
						check = false;
						Exception exception = new ExceptionWorkBaseInfoProcess( e, "工作完成时限格式不正确，无法进行工作保存。Date：" + wrapIn.getCompleteDateLimitStr() );
						result.error( exception );
						logger.error( e, effectivePerson, request, null);
					}
				}else{
					check = false;
					Exception exception = new ExceptionWorkCompleteDateLimitEmpty();
					result.error( exception );
				}
			}
			
			//校验责任者数据，需要补充组织者织信息
			if( check ){
				if( wrapIn.getResponsibilityIdentity() != null && !wrapIn.getResponsibilityIdentity().isEmpty() ){
					try {
						wrapIn = composeResponsibilityInfoByIdentity( wrapIn );
					} catch (Exception e) {
						check = false;
						Exception exception = new ExceptionWorkResponsibilityInvalid( e, wrapIn.getResponsibilityIdentity() );
						result.error( exception );
						logger.error( e, effectivePerson, request, null);
					}
				}else{
					check = false;
					Exception exception = new ExceptionWorkResponsibilityEmpty();
					result.error( exception );
				}
			}
			
			//校验协助者数据，需要补充组织组织信息
			if( check ){
				try {
					wrapIn = composeCooperateInfoByIdentity( wrapIn );
				} catch (Exception e) {
					check = false;
					Exception exception = new ExceptionWorkCooperateInvalid( e, wrapIn.getCooperateIdentityList() );
					result.error( exception );
					logger.error( e, effectivePerson, request, null);
				}
			}
			
			//校验阅知者数据，需要补充组织组织信息
			if( check ){
				try {
					wrapIn = composeReadLeaderByIdentity( wrapIn );
				} catch (Exception e) {
					check = false;
					Exception exception = new ExceptionWorkReadLeaderInvalid( e, wrapIn.getReadLeaderIdentityList() );
					result.error( exception );
					logger.error( e, effectivePerson, request, null);
				}
			}
			
			if( check ){
				//校验汇报周期和汇报日期数据，并且补充汇报时间和时间序列
				if( wrapIn.getReportCycle() != null && wrapIn.getReportCycle().trim().equals( "不汇报" )){
					wrapIn.setIsNeedReport( false );
					wrapIn.setReportDayInCycle( null );
					wrapIn.setReportTimeQue( null );
					wrapIn.setLastReportTime( null );
					wrapIn.setNextReportTime( null );
				}else{
					if( wrapIn.getReportCycle() != null && wrapIn.getReportCycle().trim().equals( "每周汇报" )){
						if( wrapIn.getReportDayInCycle() != null ){
							wrapIn.setIsNeedReport( true );
							//检验每周汇报日的选择是否正确
							if( wrapIn.getReportDayInCycle() >= 1 && wrapIn.getReportDayInCycle() <= 7 ){
								//每周1-7
								try {
									reportTimeQue = okrWorkBaseInfoService.getReportTimeQue( 
											dateOperation.getDateFromString(wrapIn.getDeployDateStr()), 
											wrapIn.getCompleteDateLimit(), 
											wrapIn.getReportCycle(), 
											wrapIn.getReportDayInCycle(), 
											reportStartTime
									);
								} catch (Exception e) {
									check = false;
									logger.warn("系统根据汇报周期信息计算汇报时间序列时发生异常。DeployDate："+wrapIn.getDeployDateStr()+", CompleteDateLimit:"+wrapIn.getCompleteDateLimit()+", ReportCycle:"+wrapIn.getReportCycle()+", ReportDayInCycle:" + wrapIn.getReportDayInCycle() + ", ReportStartTime:" + reportStartTime);
									Exception exception = new ExceptionWorkBaseInfoProcess( e, "系统根据汇报周期信息计算汇报时间序列时发生异常。" );
									result.error( exception );
									logger.error( e, effectivePerson, request, null);
								}
								try {
									nextReportTime = okrWorkBaseInfoService.getNextReportTime( reportTimeQue, wrapIn.getLastReportTime() );
								} catch (Exception e) {
									check = false;
									Exception exception = new ExceptionWorkBaseInfoProcess( e, "系统根据汇报周期信息计算下一次汇报时间时发生异常。" );
									result.error( exception );
									logger.error( e, effectivePerson, request, null);
								}
								wrapIn.setReportTimeQue( reportTimeQue );
								wrapIn.setNextReportTime( nextReportTime );
							}else{
								check = false;
								Exception exception = new ExceptionReportDayInCycleInvalid( wrapIn.getReportDayInCycle() );
								result.error( exception );
							}
						}else{
							check = false;
							Exception exception = new ExceptionReportDayInCycleEmpty();
							result.error( exception );
						}
					}else if( wrapIn.getReportCycle() != null && wrapIn.getReportCycle().trim().equals( "每月汇报" )){
						if( wrapIn.getReportDayInCycle() != null ){
							wrapIn.setIsNeedReport( true );
							if( wrapIn.getReportDayInCycle() >= 1 && wrapIn.getReportDayInCycle() <= 31 ){
								//每月1-31，如果选择的日期大于当月最大日期，那么默认定为当月最后一天
								try {
									reportTimeQue = okrWorkBaseInfoService.getReportTimeQue( 
											dateOperation.getDateFromString( wrapIn.getDeployDateStr() ), 
											wrapIn.getCompleteDateLimit(), 
											wrapIn.getReportCycle(), 
											wrapIn.getReportDayInCycle(), 
											reportStartTime
									);
								} catch (Exception e) {
									check = false;
									logger.warn("系统根据汇报周期信息计算汇报时间序列时发生异常。DeployDate："+wrapIn.getDeployDateStr()+", CompleteDateLimit:"+wrapIn.getCompleteDateLimit()+", ReportCycle:"+wrapIn.getReportCycle()+", ReportDayInCycle:" + wrapIn.getReportDayInCycle() + ", ReportStartTime:" + reportStartTime);
									Exception exception = new ExceptionWorkBaseInfoProcess( e, "系统根据汇报周期信息计算汇报时间序列时发生异常。" );
									result.error( exception );
									logger.error( e, effectivePerson, request, null);
								}
								try {
									nextReportTime = okrWorkBaseInfoService.getNextReportTime( reportTimeQue, wrapIn.getLastReportTime() );
								} catch (Exception e) {
									check = false;
									Exception exception = new ExceptionWorkBaseInfoProcess( e, "系统根据汇报周期信息计算下一次汇报时间时发生异常。" );
									result.error( exception );
									logger.error( e, effectivePerson, request, null);
								}
								wrapIn.setReportTimeQue( reportTimeQue );
								wrapIn.setNextReportTime( nextReportTime );
							}else{
								check = false;
								Exception exception = new ExceptionReportDayInCycleInvalid( wrapIn.getReportDayInCycle() );
								result.error( exception );
							}
						}else{
							check = false;
							Exception exception = new ExceptionReportDayInCycleEmpty();
							result.error( exception );
						}
					}else{
						check = false;
						Exception exception = new ExceptionReportCycleInvalid( wrapIn.getReportCycle() );
						result.error( exception );
					}
				}
			}
			
			if( check ){
				//创建新的工作信息，保存到数据库
				try{
					okrWorkBaseInfo = okrWorkBaseInfoOperationService.save( wrapIn,
							wrapIn.getWorkDetail(), wrapIn.getDutyDescription(), wrapIn.getLandmarkDescription(),
							wrapIn.getMajorIssuesDescription(), wrapIn.getProgressAction(), wrapIn.getProgressPlan(),
							wrapIn.getResultDescription() );
					result.setData(new Wo( okrWorkBaseInfo.getId() ));
					
					if( okrWorkBaseInfo != null ) {
						WrapInWorkDynamic.sendWithWorkInfo( okrWorkBaseInfo, 
								effectivePerson.getDistinguishedName(), 
								okrUserCache.getLoginUserName(), 
								okrUserCache.getLoginIdentityName() , 
								"保存具体工作", 
								"具体工作保存成功！"
						);
						
						//SmsMessageOperator.send(okrWorkBaseInfo.getResponsibilityEmployeeName(), "工作保存成功！");
					}
				}catch( Exception e ){
					Exception exception = new ExceptionWorkBaseInfoProcess( e, "保存具体工作信息时发生异常! " );
					result.error( exception );
					logger.error( e, effectivePerson, request, null);
				}
			}
		}
		return result;
	}

	public static class Wo extends WoId {
		public Wo( String id ) {
			this.setId( id );
		}
	}
}
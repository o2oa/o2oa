package com.x.okr.assemble.control.jaxrs.okrworkbaseinfo;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.WrapOutId;
import com.x.okr.assemble.control.OkrUserCache;
import com.x.okr.assemble.control.service.OkrUserManagerService;
import com.x.okr.assemble.control.service.OkrWorkBaseInfoOperationService;
import com.x.okr.entity.OkrCenterWorkInfo;
import com.x.okr.entity.OkrWorkBaseInfo;

public class ExcuteSave extends ExcuteBase {

	private Logger logger = LoggerFactory.getLogger( ExcuteSave.class );
	private OkrUserManagerService okrUserManagerService = new OkrUserManagerService();
	private OkrWorkBaseInfoOperationService okrWorkBaseInfoOperationService = new OkrWorkBaseInfoOperationService();
	
	protected ActionResult<WrapOutId> execute( HttpServletRequest request,EffectivePerson effectivePerson, WrapInOkrWorkBaseInfo wrapIn ) throws Exception {
		ActionResult<WrapOutId> result = new ActionResult<>();
		OkrCenterWorkInfo okrCenterWorkInfo = null;
		OkrWorkBaseInfo okrWorkBaseInfo  = null;
		String reportStartTime = "10:00:00";
		Date reportStartTime_date = null;
		Date nextReportTime = null;
		String reportTimeQue = null;
		Boolean check = true;		
		OkrUserCache  okrUserCache  = null;
		
		try {
			okrUserCache = okrUserInfoService.getOkrUserCacheWithPersonName( effectivePerson.getName() );
		} catch ( Exception e ) {
			check = false;
			Exception exception = new GetOkrUserCacheException( e, effectivePerson.getName()  );
			result.error( exception );
			logger.error( exception, effectivePerson, request, null);
		}
		
		if( check && ( okrUserCache == null || okrUserCache.getLoginIdentityName() == null ) ){
			check = false;
			Exception exception = new UserNoLoginException( effectivePerson.getName()  );
			result.error( exception );
			logger.error( exception, effectivePerson, request, null);
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
				wrapIn.setCreatorName( effectivePerson.getName() );
				if( effectivePerson.getName().equals( okrUserCache.getLoginUserName())){
					wrapIn.setCreatorOrganizationName( okrUserCache.getLoginUserOrganizationName());
					wrapIn.setCreatorCompanyName( okrUserCache.getLoginUserCompanyName());
					wrapIn.setCreatorIdentity( okrUserCache.getLoginIdentityName() );
				}else{
					try{//需要查询创建者的相关身份
						wrapIn.setCreatorIdentity( okrUserManagerService.getFistIdentityNameByPerson( effectivePerson.getName() ));
						wrapIn.setCreatorOrganizationName( okrUserManagerService.getDepartmentNameByIdentity( wrapIn.getCreatorIdentity() ));
						wrapIn.setCreatorCompanyName( okrUserManagerService.getCompanyNameByIdentity( wrapIn.getCreatorIdentity() ) );
					}catch(Exception e){
						check = false;
						Exception exception = new UserOrganizationQueryException( e, effectivePerson.getName() );
						result.error( exception );
						logger.error( exception, effectivePerson, request, null);
					}
				}
			}
			
			if( check ){
				wrapIn.setDeployerName( okrUserCache.getLoginUserName() );
				wrapIn.setDeployerOrganizationName( okrUserCache.getLoginUserOrganizationName());
				wrapIn.setDeployerCompanyName( okrUserCache.getLoginUserCompanyName());
				wrapIn.setDeployerIdentity( okrUserCache.getLoginIdentityName() );
			}
			
			if( check ){
				if( wrapIn.getWorkDetail() == null || wrapIn.getWorkDetail().isEmpty() ){
					check = false;
					Exception exception = new WorkDetailEmptyException();
					result.error( exception );
					logger.error( exception, effectivePerson, request, null);
				}else{
					if( wrapIn.getWorkDetail().length() > 30 ){
						wrapIn.setTitle( wrapIn.getWorkDetail().substring( 0, 30 ) );
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
							Exception exception = new WorkNotExistsException( wrapIn.getParentWorkId() );
							result.error( exception );
							logger.error( exception, effectivePerson, request, null);
						}
					} catch (Exception e) {
						check = false;
						Exception exception = new WorkQueryByIdException( e, wrapIn.getParentWorkId() );
						result.error( exception );
						logger.error( exception, effectivePerson, request, null);
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
							Exception exception = new CenterWorkNotExistsException( wrapIn.getCenterId() );
							result.error( exception );
							logger.error( exception, effectivePerson, request, null);
						}
					} catch (Exception e) {
						check = false;
						Exception exception = new CenterWorkQueryByIdException( e, wrapIn.getCenterId() );
						result.error( exception );
						logger.error( exception, effectivePerson, request, null);
					}
				}else{
					check = false;
					Exception exception = new CenterWorkIdEmptyException();
					result.error( exception );
					logger.error( exception, effectivePerson, request, null);
				}
			}
			
			//校验工作完成时限数据，补充日期型完成时限数据
			if( check ){
				if( wrapIn.getCompleteDateLimitStr() != null && !wrapIn.getCompleteDateLimitStr().isEmpty() ) {
					try{
						wrapIn.setCompleteDateLimit( dateOperation.getDateFromString( wrapIn.getCompleteDateLimitStr() ) );
					}catch( Exception e ){
						check = false;
						Exception exception = new WorkCompleteDateLimitFormatException( e, wrapIn.getCompleteDateLimitStr() );
						result.error( exception );
						logger.error( exception, effectivePerson, request, null);
					}
				}else{
					check = false;
					Exception exception = new WorkCompleteDateLimitEmptyException();
					result.error( exception );
					logger.error( exception, effectivePerson, request, null);
				}
			}
			
			//校验责任者数据，需要补充部门者织信息
			if( check ){
				if( wrapIn.getResponsibilityIdentity() != null && !wrapIn.getResponsibilityIdentity().isEmpty() ){
					try {
						wrapIn = composeResponsibilityInfoByIdentity( wrapIn );
					} catch (Exception e) {
						check = false;
						Exception exception = new WorkResponsibilityInvalidException( e, wrapIn.getResponsibilityIdentity() );
						result.error( exception );
						logger.error( exception, effectivePerson, request, null);
					}
				}else{
					check = false;
					Exception exception = new WorkResponsibilityEmptyException();
					result.error( exception );
					logger.error( exception, effectivePerson, request, null);
				}
			}
			
			//校验协助者数据，需要补充部门组织信息
			if( check ){
				try {
					wrapIn = composeCooperateInfoByIdentity( wrapIn );
				} catch (Exception e) {
					check = false;
					Exception exception = new WorkCooperateInvalidException( e, wrapIn.getCooperateIdentity() );
					result.error( exception );
					logger.error( exception, effectivePerson, request, null);
				}
			}
			
			//校验阅知者数据，需要补充部门组织信息
			if( check ){
				try {
					wrapIn = composeReadLeaderByIdentity( wrapIn );
				} catch (Exception e) {
					check = false;
					Exception exception = new WorkReadLeaderInvalidException( e, wrapIn.getReadLeaderIdentity() );
					result.error( exception );
					logger.error( exception, effectivePerson, request, null);
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
									Exception exception = new ReportTimeQueCalculateException( e );
									result.error( exception );
									logger.error( exception, effectivePerson, request, null);
								}
								try {
									nextReportTime = okrWorkBaseInfoService.getNextReportTime( reportTimeQue, wrapIn.getLastReportTime() );
								} catch (Exception e) {
									check = false;
									Exception exception = new NextReportTimeCalculateException( e );
									result.error( exception );
									logger.error( exception, effectivePerson, request, null);
								}
								wrapIn.setReportTimeQue( reportTimeQue );
								wrapIn.setNextReportTime( nextReportTime );
							}else{
								check = false;
								Exception exception = new ReportDayInCycleInvalidException( wrapIn.getReportDayInCycle() );
								result.error( exception );
								logger.error( exception, effectivePerson, request, null);
							}
						}else{
							check = false;
							Exception exception = new ReportDayInCycleEmptyException();
							result.error( exception );
							logger.error( exception, effectivePerson, request, null);
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
									Exception exception = new ReportTimeQueCalculateException( e );
									result.error( exception );
									logger.error( exception, effectivePerson, request, null);
								}
								try {
									nextReportTime = okrWorkBaseInfoService.getNextReportTime( reportTimeQue, wrapIn.getLastReportTime() );
								} catch (Exception e) {
									check = false;
									Exception exception = new NextReportTimeCalculateException( e );
									result.error( exception );
									logger.error( exception, effectivePerson, request, null);
								}
								wrapIn.setReportTimeQue( reportTimeQue );
								wrapIn.setNextReportTime( nextReportTime );
							}else{
								check = false;
								Exception exception = new ReportDayInCycleInvalidException( wrapIn.getReportDayInCycle() );
								result.error( exception );
								logger.error( exception, effectivePerson, request, null);
							}
						}else{
							check = false;
							Exception exception = new ReportDayInCycleEmptyException();
							result.error( exception );
							logger.error( exception, effectivePerson, request, null);
						}
					}else{
						check = false;
						Exception exception = new ReportCycleInvalidException( wrapIn.getReportCycle() );
						result.error( exception );
						logger.error( exception, effectivePerson, request, null);
					}
				}
			}
			
			if( check ){
				//创建新的工作信息，保存到数据库
				try{
					okrWorkBaseInfo = okrWorkBaseInfoOperationService.save( wrapIn );
					result.setData(new WrapOutId( okrWorkBaseInfo.getId() ));
					okrWorkDynamicsService.workDynamic(
							okrWorkBaseInfo.getCenterId(), 
							okrWorkBaseInfo.getId(),
							okrWorkBaseInfo.getTitle(),
							"保存具体工作", 
							effectivePerson.getName(), 
							okrUserCache.getLoginUserName(), 
							okrUserCache.getLoginIdentityName() , 
							"保存具体工作：" + okrWorkBaseInfo.getTitle(), 
							"具体工作保存成功！"
					);
				}catch( Exception e ){
					Exception exception = new WorkInfoSaveException( e );
					result.error( exception );
					logger.error( exception, effectivePerson, request, null);
				}
			}
		}
//		else{
//			result.error( new Exception( "请求传入的参数为空，无法继续保存工作信息!" ) );
//			result.setUserMessage( "请求传入的参数为空，无法继续保存工作信息!" );
//		}
		return result;
	}
	
}
package com.x.okr.assemble.control.jaxrs.okrworkbaseinfo;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.x.base.core.application.jaxrs.StandardJaxrsAction;
import com.x.base.core.bean.BeanCopyTools;
import com.x.base.core.bean.BeanCopyToolsBuilder;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.HttpMediaType;
import com.x.base.core.http.ResponseFactory;
import com.x.base.core.http.annotation.HttpMethodDescribe;
import com.x.base.core.utils.SortTools;
import com.x.okr.assemble.common.date.DateOperation;
import com.x.okr.assemble.control.OkrUserCache;
import com.x.okr.assemble.control.jaxrs.okrcenterworkinfo.WrapOutOkrCenterWorkInfo;
import com.x.okr.assemble.control.jaxrs.okrworkauthorizerecord.WrapOutOkrWorkAuthorizeRecord;
import com.x.okr.assemble.control.service.OkrCenterWorkInfoService;
import com.x.okr.assemble.control.service.OkrConfigSystemService;
import com.x.okr.assemble.control.service.OkrUserInfoService;
import com.x.okr.assemble.control.service.OkrUserManagerService;
import com.x.okr.assemble.control.service.OkrWorkAuthorizeRecordService;
import com.x.okr.assemble.control.service.OkrWorkBaseInfoService;
import com.x.okr.assemble.control.service.OkrWorkDetailInfoService;
import com.x.okr.assemble.control.service.OkrWorkDynamicsService;
import com.x.okr.assemble.control.service.OkrWorkPersonService;
import com.x.okr.entity.OkrCenterWorkInfo;
import com.x.okr.entity.OkrWorkAuthorizeRecord;
import com.x.okr.entity.OkrWorkBaseInfo;
import com.x.okr.entity.OkrWorkDetailInfo;

/**
 * 具体工作项有短期工作还长期工作，短期工作不需要自动启动定期汇报，由人工撰稿汇报即可
 */

@Path( "okrworkbaseinfo" )
public class OkrWorkBaseInfoAction extends StandardJaxrsAction{	
	private Logger logger = LoggerFactory.getLogger( OkrWorkBaseInfoAction.class );
	private BeanCopyTools<OkrWorkBaseInfo, WrapOutOkrWorkBaseInfo> wrapout_copier = BeanCopyToolsBuilder.create( OkrWorkBaseInfo.class, WrapOutOkrWorkBaseInfo.class, null, WrapOutOkrWorkBaseInfo.Excludes);
	private BeanCopyTools<OkrCenterWorkInfo, WrapOutOkrCenterWorkInfo> okrCenterWorkInfo_wrapout_copier = BeanCopyToolsBuilder.create( OkrCenterWorkInfo.class, WrapOutOkrCenterWorkInfo.class, null, WrapOutOkrCenterWorkInfo.Excludes);
	private BeanCopyTools<OkrWorkAuthorizeRecord, WrapOutOkrWorkAuthorizeRecord> okrWorkAuthorizeRecord_wrapout_copier = BeanCopyToolsBuilder.create( OkrWorkAuthorizeRecord.class, WrapOutOkrWorkAuthorizeRecord.class, null, WrapOutOkrWorkAuthorizeRecord.Excludes);
	private OkrCenterWorkInfoService okrCenterWorkInfoService = new OkrCenterWorkInfoService();
	private OkrWorkAuthorizeRecordService okrWorkAuthorizeRecordService = new OkrWorkAuthorizeRecordService();
	private OkrWorkBaseInfoService okrWorkBaseInfoService = new OkrWorkBaseInfoService();
	private OkrWorkPersonService okrWorkPersonService = new OkrWorkPersonService();
	private OkrWorkDetailInfoService okrWorkDetailInfoService = new OkrWorkDetailInfoService();
	private OkrUserManagerService okrUserManagerService = new OkrUserManagerService();
	private OkrWorkDynamicsService okrWorkDynamicsService = new OkrWorkDynamicsService();
	private OkrConfigSystemService okrConfigSystemService = new OkrConfigSystemService();
	private OkrUserInfoService okrUserInfoService = new OkrUserInfoService();
	private DateOperation dateOperation = new DateOperation();
	
	@HttpMethodDescribe( value = "新建或者更新OkrWorkBaseInfo对象.", request = WrapInOkrWorkBaseInfo.class, response = WrapOutOkrWorkBaseInfo.class)
	@POST
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response post( @Context HttpServletRequest request, WrapInOkrWorkBaseInfo wrapIn ) {
		ActionResult<WrapOutOkrWorkBaseInfo> result = new ActionResult<>();
		EffectivePerson currentPerson = this.effectivePerson(request);
		OkrCenterWorkInfo okrCenterWorkInfo = null;
		OkrWorkBaseInfo okrWorkBaseInfo  = null;
		String reportStartTime = "10:00:00";
		Date reportStartTime_date = null;
		Date nextReportTime = null;
		String reportTimeQue = null;
		Boolean check = true;		
		OkrUserCache  okrUserCache  = null;
		
		try {
			okrUserCache = okrUserInfoService.getOkrUserCacheWithPersonName( currentPerson.getName() );
		} catch (Exception e1) {
			check = false;
			result.error( new Exception( "系统获取用户登录记录信息发生异常!" ) );
			result.setUserMessage( "系统获取用户登录记录信息发生异常!" );
			logger.error( "system get login indentity with person name got an exception", e1 );
		}		
		
		if( check && ( okrUserCache == null || okrUserCache.getLoginIdentityName() == null ) ){
			check = false;
			logger.error( "system query user identity got an exception.user:" + currentPerson.getName());
			result.error( new Exception( "系统未获取到用户登录身份(登录用户名)，请重新打开应用!" ) );
			result.setUserMessage( "系统未获取到用户登录身份(登录用户名)，请重新打开应用!" );
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
				wrapIn.setCreatorName( currentPerson.getName() );
				if( currentPerson.getName().equals( okrUserCache.getLoginUserName())){
					wrapIn.setCreatorOrganizationName( okrUserCache.getLoginUserOrganizationName());
					wrapIn.setCreatorCompanyName( okrUserCache.getLoginUserCompanyName());
					wrapIn.setCreatorIdentity( okrUserCache.getLoginIdentityName() );
				}else{
					//需要查询创建者的相关身份
					try{
						wrapIn.setCreatorIdentity( okrUserManagerService.getFistIdentityNameByPerson( currentPerson.getName() ));
						wrapIn.setCreatorOrganizationName( okrUserManagerService.getDepartmentNameByIdentity( wrapIn.getCreatorIdentity() ));
						wrapIn.setCreatorCompanyName( okrUserManagerService.getCompanyNameByIdentity( wrapIn.getCreatorIdentity() ) );
					}catch(Exception e){
						check = false;
						result.setUserMessage( "系统通过操作用户查询用户身份和组织信息时发生异常!" );
						result.error( e );
						logger.error( "系统通过操作用户查询用户身份和组织信息时发生异常!", e );
					}
				}
			}
			
			if( check ){
				wrapIn.setDeployerName( okrUserCache.getLoginUserName() );
				if( currentPerson.getName().equals( okrUserCache.getLoginUserName())){
					wrapIn.setDeployerOrganizationName( okrUserCache.getLoginUserOrganizationName());
					wrapIn.setDeployerCompanyName( okrUserCache.getLoginUserCompanyName());
					wrapIn.setDeployerIdentity( okrUserCache.getLoginIdentityName() );
				}
			}
			
			if( check ){
				//如果工作标题未填写，则使用工作详细信息的前N个字作为标题
				if( wrapIn.getTitle() == null || wrapIn.getTitle().isEmpty() ){
					if( wrapIn.getWorkDetail() == null || wrapIn.getWorkDetail().isEmpty() ){
						check = false;
						result.error( new Exception( "工作标题和工作详细信息都未填写，无法继续保存工作信息!" ) );
						result.setUserMessage( "工作标题和工作详细信息都未填写，无法继续保存工作信息!" );
					}else{
						if( wrapIn.getWorkDetail().length() > 30 ){
							wrapIn.setTitle( wrapIn.getWorkDetail().substring( 0, 30 ) );
						}else{
							wrapIn.setTitle( wrapIn.getWorkDetail() );
						}
					}
				}else{
					if( wrapIn.getTitle().length() > 30 ){
						wrapIn.setTitle( wrapIn.getTitle().substring( 0, 30 ) );
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
						}else{
							check = false;
							result.error( new Exception( "上级工作不存在, id:'"+ wrapIn.getParentWorkId() +"'，无法继续保存工作信息!" ) );
							result.setUserMessage( "上级工作不存在, id:'"+ wrapIn.getParentWorkId() +"'，无法继续保存工作信息!" );
						}
					} catch (Exception e) {
						check = false;
						result.error( e );
						logger.error( "system search work by id got an exception.", e );
						result.setUserMessage( "系统在查询上级工作信息时发生异常, id:'"+ wrapIn.getParentWorkId() +"'，无法继续保存工作信息!" );
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
							result.error( new Exception( "中心工作不存在,id:'"+ wrapIn.getCenterId() +"'，无法继续保存工作信息!" ) );
							result.setUserMessage( "中心工作不存在,id:'"+ wrapIn.getCenterId() +"'，无法继续保存工作信息!" );
						}
					} catch (Exception e) {
						check = false;
						result.error( e );
						logger.error( "system search center work by id got an exception.", e );
						result.setUserMessage( "系统在查询中心工作信息时发生异常,id:'"+ wrapIn.getCenterId() +"'，无法继续保存工作信息!" );
					}
				}else{
					check = false;
					result.error( new Exception( "中心工作ID为空，无法继续保存工作信息!" ) );
					result.setUserMessage( "中心工作ID为空，无法继续保存工作信息!" );
				}
			}
			
			//校验工作完成时限数据，补充日期型完成时限数据
			if( check ){
				if( wrapIn.getCompleteDateLimitStr() != null && !wrapIn.getCompleteDateLimitStr().isEmpty() ) {
					try{
						wrapIn.setCompleteDateLimit( dateOperation.getDateFromString( wrapIn.getCompleteDateLimitStr() ) );
					}catch( Exception e ){
						check = false;
						result.setUserMessage( "工作完成时限格式不正确："+ wrapIn.getCompleteDateLimitStr() +"，正确格式为：yyyy-mm-dd" );
						result.error( e );
					}
				}else{
					check = false;
					result.error( new Exception( "工作完成时限信息为空，无法继续保存工作信息!" ) );
					result.setUserMessage( "工作完成时限信息为空，无法继续保存工作信息!" );
				}
			}
			
			//校验责任者数据，需要补充部门者织信息
			if( check ){
				try {
					wrapIn = composeResponsibilityInfoByIdentity( wrapIn );
				} catch (Exception e) {
					check = false;
					result.error( e );
					logger.error( "system compose responsibility info by identity got an exception.", e );
					result.setUserMessage( "系统根据用户所选择的责任者身份为工作信息组织责任者信息时发生异常。" );
				}
			}
			
			//校验协助者数据，需要补充部门组织信息
			if( check ){
				try {
					wrapIn = composeCooperateInfoByIdentity( wrapIn );
				} catch (Exception e) {
					check = false;
					result.error( e );
					logger.error( "system compose cooperate info by identity got an exception.", e );
					result.setUserMessage( "系统根据用户所选择的协助者身份为工作信息组织协助者信息时发生异常。" );
				}
			}
			
			//校验阅知者数据，需要补充部门组织信息
			if( check ){
				try {
					wrapIn = composeReadLeaderByIdentity( wrapIn );
				} catch (Exception e) {
					check = false;
					result.error( e );
					logger.error( "system compose read leader info by identity got an exception.", e );
					result.setUserMessage( "系统根据用户所选择的阅知者身份为工作信息组织阅知者信息时发生异常。" );
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
									result.error( e );
									logger.error( "system get report time queue got an exception.", e );
									result.setUserMessage( "系统根据汇报周期信息计算汇报时间序列时发生异常，无法继续保存工作信息!" );
								}
								try {
									nextReportTime = okrWorkBaseInfoService.getNextReportTime( reportTimeQue, wrapIn.getLastReportTime() );
								} catch (Exception e) {
									check = false;
									result.error( e );
									logger.error( "system get next report time got an exception.", e );
									result.setUserMessage( "系统根据汇报周期信息计算下一次汇报时间时发生异常，无法继续保存工作信息!" );
								}
								wrapIn.setReportTimeQue( reportTimeQue );
								wrapIn.setNextReportTime( nextReportTime );
							}else{
								check = false;
								result.error( new Exception( "每周汇报日选择不正确："+ wrapIn.getReportDayInCycle() +"，无法继续保存工作信息!" ) );
								result.setUserMessage( "每周汇报日选择不正确："+ wrapIn.getReportDayInCycle() +"，无法继续保存工作信息!" );
							}
						}else{
							check = false;
							result.error( new Exception( "每周汇报日为空，无法继续保存工作信息!" ) );
							result.setUserMessage( "每周汇报日为空，无法继续保存工作信息!" );
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
									result.error( e );
									logger.error( "system get report time queue got an exception.", e );
									result.setUserMessage( "系统根据汇报周期信息计算汇报时间序列时发生异常，无法继续保存工作信息!" );
								}
								try {
									nextReportTime = okrWorkBaseInfoService.getNextReportTime( reportTimeQue, wrapIn.getLastReportTime() );
								} catch (Exception e) {
									check = false;
									result.error( e );
									logger.error( "system get next report time got an exception.", e );
									result.setUserMessage( "系统根据汇报周期信息计算下一次汇报时间时发生异常，无法继续保存工作信息!" );
								}
								wrapIn.setReportTimeQue( reportTimeQue );
								wrapIn.setNextReportTime( nextReportTime );
							}else{
								check = false;
								result.error( new Exception( "每月汇报日选择不正确："+ wrapIn.getReportDayInCycle() +"，无法继续保存工作信息!" ) );
								result.setUserMessage( "每月汇报日选择不正确："+ wrapIn.getReportDayInCycle() +"，无法继续保存工作信息!" );
							}
						}else{
							check = false;
							result.error( new Exception( "每月汇报日期为空，无法继续保存工作信息!" ) );
							result.setUserMessage( "每月汇报日期为空，无法继续保存工作信息!" );
						}
					}else{
						check = false;
						result.error( new Exception( "汇报周期选择不正确："+ wrapIn.getReportCycle() +"，无法继续保存工作信息!" ) );
						result.setUserMessage( "汇报周期选择不正确："+ wrapIn.getReportCycle() +"，无法继续保存工作信息!" );
					}
				}
			}
			
			if( check ){
				//创建新的工作信息，保存到数据库
				try{
					okrWorkBaseInfo = okrWorkBaseInfoService.save( wrapIn );
					
					okrWorkDynamicsService.workDynamic(
							okrWorkBaseInfo.getCenterId(), 
							okrWorkBaseInfo.getId(),
							okrWorkBaseInfo.getTitle(),
							"保存具体工作", 
							currentPerson.getName(), 
							okrUserCache.getLoginUserName(), 
							okrUserCache.getLoginIdentityName() , 
							"保存具体工作：" + okrWorkBaseInfo.getTitle(), 
							"具体工作保存成功！"
					);
					
					result.setUserMessage( okrWorkBaseInfo.getId() );
				}catch( Exception e ){
					logger.error( "save okrWorkBaseInfo got an exception.", e );
					result.setUserMessage( "保存工作信息过程中发生异常！" );
					result.error( e );
				}
			}
		}else{
			result.error( new Exception( "请求传入的参数为空，无法继续保存工作信息!" ) );
			result.setUserMessage( "请求传入的参数为空，无法继续保存工作信息!" );
		}
		return ResponseFactory.getDefaultActionResultResponse( result );
	}
	
	private WrapInOkrWorkBaseInfo composeReadLeaderByIdentity(WrapInOkrWorkBaseInfo wrapIn) throws Exception {
		if( wrapIn.getReadLeaderIdentity() != null && !wrapIn.getReadLeaderIdentity().isEmpty() ){
			String userName = "";
			String identity = "";
			String organizationName = "";
			String companyName = "";
			String[] identityNames = null;
			identityNames = wrapIn.getReadLeaderIdentity().split( "," );
			try{
				for( String _identity : identityNames ){
					if( okrUserManagerService.getUserNameByIdentity( _identity ) == null ){
						throw new Exception( "person not exsits, identity:" + _identity );
					}
					if( identity == null || identity.isEmpty() ){
						identity += _identity;
					}else{
						identity += "," + _identity;
					}
					if( userName == null || userName.isEmpty() ){
						userName = okrUserManagerService.getUserNameByIdentity( _identity ).getName();
					}else{
						userName += "," + okrUserManagerService.getUserNameByIdentity(_identity).getName();
					}
					if( organizationName == null || organizationName.isEmpty() ){
						organizationName = okrUserManagerService.getDepartmentNameByIdentity(_identity);
					}else{
						organizationName += "," + okrUserManagerService.getDepartmentNameByIdentity(_identity);
					}
					if( companyName == null || companyName.isEmpty() ){
						companyName = okrUserManagerService.getCompanyNameByIdentity(_identity);
					}else{
						companyName += "," + okrUserManagerService.getCompanyNameByIdentity(_identity);
					}
				}
				wrapIn.setReadLeaderName(userName);
				wrapIn.setReadLeaderIdentity(identity);
				wrapIn.setReadLeaderOrganizationName(organizationName);
				wrapIn.setReadLeaderCompanyName(companyName);
			}catch(Exception e){
				logger.error( "system query organization for read leader got an exception.");
				throw e;
			}
		}else{
			wrapIn.setReadLeaderName( "" );
			wrapIn.setReadLeaderOrganizationName( "" );
			wrapIn.setReadLeaderCompanyName( "" );
		}
		return wrapIn;
	}

	/**
	 * 根据用户传入的协助者身份信息查询并补充工作对象的协助者相关组织信息
	 * @param wrapIn
	 * @return
	 * @throws Exception
	 */
	private WrapInOkrWorkBaseInfo composeCooperateInfoByIdentity( WrapInOkrWorkBaseInfo wrapIn ) throws Exception {
		if( wrapIn.getCooperateIdentity() != null && !wrapIn.getCooperateIdentity().isEmpty() ){
			String userName = "";
			String identity = "";
			String organizationName = "";
			String companyName = "";
			String[] identityNames = null;
			identityNames = wrapIn.getCooperateIdentity().split( "," );
			try{
				for( String _identity : identityNames ){
					if( okrUserManagerService.getUserNameByIdentity(_identity) == null ){
						throw new Exception( "person not exsits, identity:" + _identity );
					}
					if( identity == null || identity.isEmpty() ){
						identity += _identity;
					}else{
						identity += "," + _identity;
					}
					if( userName == null || userName.isEmpty() ){
						userName = okrUserManagerService.getUserNameByIdentity(_identity).getName();
					}else{
						userName += "," + okrUserManagerService.getUserNameByIdentity(_identity).getName();
					}
					if( organizationName == null || organizationName.isEmpty() ){
						organizationName = okrUserManagerService.getDepartmentNameByIdentity(_identity);
					}else{
						organizationName += "," + okrUserManagerService.getDepartmentNameByIdentity(_identity);
					}
					if( companyName == null || companyName.isEmpty() ){
						companyName = okrUserManagerService.getCompanyNameByIdentity(_identity);
					}else{
						companyName += "," + okrUserManagerService.getCompanyNameByIdentity(_identity);
					}
				}
				wrapIn.setCooperateEmployeeName(userName);
				wrapIn.setCooperateIdentity(identity);
				wrapIn.setCooperateOrganizationName(organizationName);
				wrapIn.setCooperateCompanyName(companyName);
			}catch(Exception e){
				logger.error( "system query organization for Cooperate got an exception.");
				throw e;				
			}
		}else{
			wrapIn.setCooperateEmployeeName( "" );
			wrapIn.setCooperateOrganizationName( "" );
			wrapIn.setCooperateCompanyName( "" );
		}
		return wrapIn;
	}

	/**
	 * 根据用户传入的责任者身份信息查询并补充工作对象的责任者相关组织信息
	 * @param wrapIn
	 * @return
	 * @throws Exception
	 */
	private WrapInOkrWorkBaseInfo composeResponsibilityInfoByIdentity( WrapInOkrWorkBaseInfo wrapIn ) throws Exception {
		if( wrapIn.getResponsibilityIdentity() != null && !wrapIn.getResponsibilityIdentity().isEmpty() ){
			String userName = "";
			String identity = "";
			String organizationName = "";
			String companyName = "";
			String[] identityNames = null;
			identityNames = wrapIn.getResponsibilityIdentity().split( "," );
			try{
				for( String _identity : identityNames ){
					if( okrUserManagerService.getUserNameByIdentity( _identity ) == null ){
						throw new Exception( "person not exsits, identity:" + _identity );
					}
					if( identity == null || identity.isEmpty() ){
						identity += _identity;
					}else{
						identity += "," + _identity;
					}
					if( userName == null || userName.isEmpty() ){
						userName = okrUserManagerService.getUserNameByIdentity(_identity).getName();
					}else{
						userName += "," + okrUserManagerService.getUserNameByIdentity(_identity).getName();
					}
					if( organizationName == null || organizationName.isEmpty() ){
						organizationName = okrUserManagerService.getDepartmentNameByIdentity( _identity );
					}else{
						organizationName += "," + okrUserManagerService.getDepartmentNameByIdentity( _identity );
					}
					if( companyName == null || companyName.isEmpty() ){
						companyName = okrUserManagerService.getCompanyNameByIdentity(_identity);
					}else{
						companyName += "," + okrUserManagerService.getCompanyNameByIdentity(_identity);
					}
				}
				wrapIn.setResponsibilityEmployeeName(userName);
				wrapIn.setResponsibilityIdentity(identity);
				wrapIn.setResponsibilityOrganizationName(organizationName);
				wrapIn.setResponsibilityCompanyName(companyName);
			}catch(Exception e){
				logger.error( "system query organization for Responsibility got an exception.");
				throw e;
			}
		}else{
			throw new Exception( "wrapIn getResponsibilityIdentity is null!" );
		}
		return wrapIn;
	}

	@HttpMethodDescribe(value = "根据ID删除OkrWorkBaseInfo数据对象.", response = WrapOutOkrWorkBaseInfo.class)
	@DELETE
	@Path( "{id}" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response delete( @Context HttpServletRequest request, @PathParam( "id" ) String id ) {
		ActionResult<WrapOutOkrWorkBaseInfo> result = new ActionResult<>();
		EffectivePerson currentPerson = this.effectivePerson( request );
		List<String> ids = null;
		OkrWorkBaseInfo okrWorkBaseInfo = null;
		boolean check = true;
		
		OkrUserCache  okrUserCache  = null;
		if( check ){
			try {
				okrUserCache = okrUserInfoService.getOkrUserCacheWithPersonName( currentPerson.getName() );
			} catch (Exception e1) {
				check = false;
				result.error( new Exception( "系统获取用户登录记录信息发生异常!" ) );
				result.setUserMessage( "系统获取用户登录记录信息发生异常!" );
				logger.error( "system get login indentity with person name got an exception", e1 );
			}	
		}		
		if( check && ( okrUserCache == null || okrUserCache.getLoginIdentityName() == null ) ){
			check = false;
			logger.error( "system query user identity got an exception.user:" + currentPerson.getName());
			result.error( new Exception( "系统未获取到用户登录身份(登录用户名)，请重新打开应用!" ) );
			result.setUserMessage( "系统未获取到用户登录身份(登录用户名)，请重新打开应用!" );
		}
		if( id == null || id.isEmpty() ){
			check = false;
			result.error( new Exception( "ID为空，无法删除数据!" ) );
			result.setUserMessage( "ID为空，无法删除数据!" );
			logger.error( "id is null, system can not delete any object." );
		}
		if( check ){
			try {
				ids = okrWorkBaseInfoService.getSubNormalWorkBaseInfoIds( id );
				if( ids != null && !ids.isEmpty()){
					logger.error( "okrWorkBaseInfoService has "+ ids.size() +" subwork, can not delete work info." );
					result.error( new Exception( "该工作存在"+ ids.size() +"个下级工作，该工作暂无法删除。" ) );
					result.setUserMessage( "该工作存在"+ ids.size() +"个下级工作，该工作暂无法删除。" );
				}else{
					try{
						okrWorkBaseInfo = okrWorkBaseInfoService.get( id );
						if( okrWorkBaseInfo != null ){
							okrWorkBaseInfoService.deleteByWorkId( id );
							okrWorkDynamicsService.workDynamic(
									okrWorkBaseInfo.getCenterId(), 
									okrWorkBaseInfo.getId(),
									okrWorkBaseInfo.getTitle(),
									"删除具体工作", 
									currentPerson.getName(), 
									okrUserCache.getLoginUserName(), 
									okrUserCache.getLoginIdentityName() , 
									"删除具体工作：" + okrWorkBaseInfo.getTitle(), 
									"具体工作删除成功！"
							);
						}
						result.setUserMessage( "成功删除工作信息数据信息。id=" + id );
					}catch(Exception e){
						logger.error( "system delete okrWorkBaseInfoService get an exception, {'id':'"+id+"'}", e );
						result.setUserMessage( "删除工作信息数据过程中发生异常。" );
						result.error( e );
					}
				}
			} catch (Exception e1) {
				logger.error( "system getSubNormalWorkBaseInfoIds got an exception, {'id':'"+id+"'}", e1 );
				result.error( e1 );
				result.setUserMessage( "系统在根据工作ID获取下级工作信息时发生异常。" );
			}
		}
		
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	
	
	@HttpMethodDescribe(value = "根据ID获取OkrWorkBaseInfo对象.", response = WrapOutOkrWorkBaseInfo.class)
	@GET
	@Path( "{id}" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response get(@Context HttpServletRequest request, @PathParam( "id" ) String id) {
		ActionResult<WrapOutOkrWorkBaseInfo> result = new ActionResult<>();
		WrapOutOkrWorkBaseInfo wrap = null;
		OkrWorkBaseInfo okrWorkBaseInfo = null;
		OkrWorkDetailInfo okrWorkDetailInfo = null;
		List<String> ids = null;
		OkrWorkAuthorizeRecord okrWorkAuthorizeRecord = null;
		List<OkrWorkAuthorizeRecord> okrWorkAuthorizeRecordList = null;
		List<WrapOutOkrWorkAuthorizeRecord> wrapOutOkrWorkAuthorizeRecordList = null;
		EffectivePerson currentPerson = this.effectivePerson(request);
		boolean check = true;
		OkrUserCache  okrUserCache  = null;
		if( check ){
			try {
				okrUserCache = okrUserInfoService.getOkrUserCacheWithPersonName( currentPerson.getName() );
			} catch (Exception e1) {
				check = false;
				result.error( new Exception( "系统获取用户登录记录信息发生异常!" ) );
				result.setUserMessage( "系统获取用户登录记录信息发生异常!" );
				logger.error( "system get login indentity with person name got an exception", e1 );
			}	
		}		
		if( check && ( okrUserCache == null || okrUserCache.getLoginIdentityName() == null ) ){
			check = false;
			logger.error( "system query user identity got an exception.user:" + currentPerson.getName());
			result.error( new Exception( "系统未获取到用户登录身份(登录用户名)，请重新打开应用!" ) );
			result.setUserMessage( "系统未获取到用户登录身份(登录用户名)，请重新打开应用!" );
		}
		
		if( id == null || id.isEmpty() ){
			logger.error( "id is null, system can not get any object." );
			check = false;
		}
		if(check){
			try {
				okrWorkBaseInfo = okrWorkBaseInfoService.get(id);
				if( okrWorkBaseInfo != null ){
					wrap = wrapout_copier.copy( okrWorkBaseInfo );
					result.setData(wrap);
				}else{
					check = false;
					throw new Exception( "system can not get any okrWorkBaseInfo by {'id':'"+id+"'}. " );
					
				}
			} catch (Throwable th) {
				check = false;
				logger.error( "system get by id get an exception" );
				th.printStackTrace();
				result.error(th);
			}
		}
		if(check){
			try {
				okrWorkDetailInfo = okrWorkDetailInfoService.get(id);
				if( okrWorkDetailInfo != null ){
					wrap.setWorkDetail( okrWorkDetailInfo.getWorkDetail() );
					wrap.setDutyDescription( okrWorkDetailInfo.getDutyDescription() );
					wrap.setLandmarkDescription( okrWorkDetailInfo.getLandmarkDescription() );
					wrap.setMajorIssuesDescription( okrWorkDetailInfo.getMajorIssuesDescription() );
					wrap.setProgressAction( okrWorkDetailInfo.getProgressAction() );
					wrap.setProgressPlan( okrWorkDetailInfo.getProgressPlan() );
					wrap.setResultDescription( okrWorkDetailInfo.getResultDescription() );
					
					//获取该工作和当前责任人相关的授权信息
					okrWorkAuthorizeRecord = okrWorkAuthorizeRecordService.getLastAuthorizeRecord( okrWorkBaseInfo.getId(), okrUserCache.getLoginIdentityName()  );
					if( okrWorkAuthorizeRecord != null ){
						wrap.setOkrWorkAuthorizeRecord( okrWorkAuthorizeRecord_wrapout_copier.copy( okrWorkAuthorizeRecord ) );
					}
					
					//获取该工作所有的授权信息
					ids = okrWorkAuthorizeRecordService.listByWorkId( id );
					if( ids != null && !ids.isEmpty() ){
						okrWorkAuthorizeRecordList = okrWorkAuthorizeRecordService.list( ids );
						if( okrWorkAuthorizeRecordList != null ){
							wrapOutOkrWorkAuthorizeRecordList = okrWorkAuthorizeRecord_wrapout_copier.copy( okrWorkAuthorizeRecordList );
							SortTools.asc( wrapOutOkrWorkAuthorizeRecordList, "createTime" );
							wrap.setOkrWorkAuthorizeRecords( wrapOutOkrWorkAuthorizeRecordList );
						}
					}
					result.setData(wrap);
				}else{
					logger.error( "system can not get any okrWorkDetailInfo by {'id':'"+id+"'}. " );
				}
			} catch (Throwable th) {
				check = false;
				logger.error( "system get by id get an exception" );
				th.printStackTrace();
				result.error(th);
			}
		}
		
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "根据上级工作ID获取OkrWorkBaseInfo对象.", response = WrapOutOkrWorkBaseInfo.class)
	@GET
	@Path( "list/sub/{id}" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listByParentId(@Context HttpServletRequest request, @PathParam( "id" ) String id) {
		ActionResult<List<WrapOutOkrWorkBaseInfo>> result = new ActionResult<List<WrapOutOkrWorkBaseInfo>>();
		List<WrapOutOkrWorkBaseInfo> wraps = null;
		List<String> ids = null;
		List<OkrWorkBaseInfo> okrWorkBaseInfoList = null;
		OkrWorkDetailInfo okrWorkDetailInfo = null;
		boolean check = true;
		
		if( id == null || id.isEmpty() ){
			logger.error( "id is null, system can not get any object." );
			check = false;
		}
		if(check){
			try {
				ids = okrWorkBaseInfoService.listByParentId(id);
			} catch (Exception e) {
				check = false;
				logger.error( "system get by id get an exception", e );
				result.setUserMessage( "系统根据ID获取工作信息列表发生异常。" );
				result.error(e);
			}
		}
		if(check){
			if( ids != null && !ids.isEmpty()){
				try {
					okrWorkBaseInfoList = okrWorkBaseInfoService.listByIds(ids);
				} catch (Exception e) {
					check = false;
					logger.error( "system get works by ids got an exception", e );
					result.setUserMessage( "系统根据ID列表获取工作信息列表发生异常。" );
					result.error(e);
				}
			}
		}
		
		if(check){
			if( okrWorkBaseInfoList != null && !okrWorkBaseInfoList.isEmpty()){
				try {
					wraps = wrapout_copier.copy(okrWorkBaseInfoList);
				} catch (Exception e) {
					logger.error( "system copy okrWorkBaseInfoList to wrapout got an exception", e );
					result.setUserMessage( "系统转换列表为输出列表时发生异常。" );
					result.error(e);
				}
			}
		}
		
		if(check){
			if( wraps != null && !wraps.isEmpty() ){
				for( WrapOutOkrWorkBaseInfo wrapOutOkrWorkBaseInfo : wraps ){
					try {
						okrWorkDetailInfo = okrWorkDetailInfoService.get( wrapOutOkrWorkBaseInfo.getId() );
						if( okrWorkDetailInfo != null ){
							wrapOutOkrWorkBaseInfo.setWorkDetail( okrWorkDetailInfo.getWorkDetail() );
							wrapOutOkrWorkBaseInfo.setDutyDescription( okrWorkDetailInfo.getDutyDescription() );
							wrapOutOkrWorkBaseInfo.setLandmarkDescription( okrWorkDetailInfo.getLandmarkDescription() );
							wrapOutOkrWorkBaseInfo.setMajorIssuesDescription( okrWorkDetailInfo.getMajorIssuesDescription() );
							wrapOutOkrWorkBaseInfo.setProgressAction( okrWorkDetailInfo.getProgressAction() );
							wrapOutOkrWorkBaseInfo.setProgressPlan( okrWorkDetailInfo.getProgressPlan() );
							wrapOutOkrWorkBaseInfo.setResultDescription( okrWorkDetailInfo.getResultDescription() );
						}
					} catch (Exception e) {
						logger.error( "system get work detail by work id got an exception", e );
						result.setUserMessage( "系统为工作补充详细信息时发生异常。" );
						result.error(e);
					}
				}
			}
		}
		result.setData(wraps);
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "判断当前用户是否有权限拆解指定工作.", response = WrapOutOkrWorkBaseInfo.class)
	@GET
	@Path( "canDismantlingWork/{id}" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response canDismantlingWork(@Context HttpServletRequest request, @PathParam( "id" ) String id) {
		ActionResult<WrapOutOkrWorkBaseInfo> result = new ActionResult<>();
		EffectivePerson currentPerson = this.effectivePerson( request );
		Boolean check = true;
		OkrUserCache  okrUserCache  = null;
		if( check ){
			try {
				okrUserCache = okrUserInfoService.getOkrUserCacheWithPersonName( currentPerson.getName() );
			} catch (Exception e1) {
				check = false;
				result.error( new Exception( "系统获取用户登录记录信息发生异常!" ) );
				result.setUserMessage( "系统获取用户登录记录信息发生异常!" );
				logger.error( "system get login indentity with person name got an exception", e1 );
			}	
		}		
		if( check && ( okrUserCache == null || okrUserCache.getLoginIdentityName() == null ) ){
			check = false;
			logger.error( "system query user identity got an exception.user:" + currentPerson.getName());
			result.error( new Exception( "系统未获取到用户登录身份(登录用户名)，请重新打开应用!" ) );
			result.setUserMessage( "系统未获取到用户登录身份(登录用户名)，请重新打开应用!" );
		}
		String loginIdentity = okrUserCache.getLoginIdentityName() ;
		if( check ){
			if( loginIdentity == null ){
				result.error( new Exception( "用户登录信息为空，请重新打开应用！" ) );
				result.setUserMessage( "用户登录信息为空，请重新打开应用！" );
			}else{
				if( id == null || id.isEmpty() ){
					result.error( new Exception( "传入的参数id为空" ) );
					result.setUserMessage( "传入的参数id为空" );
				}else{
					try {
						if( okrWorkBaseInfoService.canDismantlingWorkByIdentity( id, loginIdentity ) ){
							result.setUserMessage( "true" );
						}else{
							result.setUserMessage( "false" );
						}
					} catch (Exception e) {
						result.error( e );
						result.setUserMessage( "系统在判断用户是否有权限拆解工作时发生异常。" );
					}
				}
			}
		}
				
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "根据中心工作ID获取我可以看到的所有OkrWorkBaseInfo对象.", response = WrapOutOkrWorkBaseInfo.class)
	@GET
	@Path( "center/{id}" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listMyWorkInCenter(@Context HttpServletRequest request, @PathParam( "id" ) String id) {
		ActionResult<WrapOutOkrCenterWorkInfo> result = new ActionResult<WrapOutOkrCenterWorkInfo>();
		List<WrapOutOkrWorkBaseInfo> wrapsWorkBaseInfoList_for_center = new ArrayList<WrapOutOkrWorkBaseInfo>();
		List<WrapOutOkrWorkBaseInfo> all_wrapWorkBaseInfoList = null;
		List<OkrWorkBaseInfo> all_workBaseInfoList = null;
		WrapOutOkrCenterWorkInfo wrapOutOkrCenterWorkInfo = null;
		OkrCenterWorkInfo okrCenterWorkInfo  = null;
		List<String> query_statuses = new ArrayList<String>();
		Boolean hasNoneSubmitReport = false;
		OkrUserCache  okrUserCache  = null;
		Boolean check = true;
		EffectivePerson currentPerson = this.effectivePerson(request);	
		if( check ){
			try {
				okrUserCache = okrUserInfoService.getOkrUserCacheWithPersonName( currentPerson.getName() );
			} catch (Exception e1) {
				check = false;
				result.error( new Exception( "系统获取用户登录记录信息发生异常!" ) );
				result.setUserMessage( "系统获取用户登录记录信息发生异常!" );
				logger.error( "system get login indentity with person name got an exception", e1 );
			}	
		}		
		if( check && ( okrUserCache == null || okrUserCache.getLoginIdentityName() == null ) ){
			check = false;
			logger.error( "system query user identity got an exception.user:" + currentPerson.getName());
			result.error( new Exception( "系统未获取到用户登录身份(登录用户名)，请重新打开应用!" ) );
			result.setUserMessage( "系统未获取到用户登录身份(登录用户名)，请重新打开应用!" );
		}
		if( check ){
			try{		
				query_statuses.add( "正常" );	
				
				//查询中心工作信息是否存在
				okrCenterWorkInfo = okrCenterWorkInfoService.get( id );
				if( okrCenterWorkInfo != null ){
					wrapOutOkrCenterWorkInfo = okrCenterWorkInfo_wrapout_copier.copy( okrCenterWorkInfo );
					//获取到该中心工作下所有的工作信息
					all_workBaseInfoList = okrWorkBaseInfoService.listWorkInCenter( id, query_statuses );
					if( all_workBaseInfoList != null ){
						all_wrapWorkBaseInfoList = wrapout_copier.copy( all_workBaseInfoList );
						if( all_wrapWorkBaseInfoList != null ){
							for( WrapOutOkrWorkBaseInfo wrap_work : all_wrapWorkBaseInfoList){
								//判断工作是否有未提交的工作汇报
								hasNoneSubmitReport = false;
								hasNoneSubmitReport = okrWorkBaseInfoService.hasNoneSubmitReport( 
										wrap_work.getId(), "草稿", "草稿", null
								);
								wrap_work.setHasNoneSubmitReport( hasNoneSubmitReport );
							}
							for( WrapOutOkrWorkBaseInfo wrap_work : all_wrapWorkBaseInfoList){
								if( wrap_work.getParentWorkId() == null || wrap_work.getParentWorkId().isEmpty() ){
									wrap_work = composeWorkInfo( all_wrapWorkBaseInfoList, wrap_work );
									wrapsWorkBaseInfoList_for_center.add( wrap_work );
								}
							}
						}
					}
				}else{
					result.error( new Exception( "中心工作不存在！" ));
					result.setUserMessage( "中心工作不存在！" );
					logger.error( "center work{'id':'" + id + "'} is not exists." );
				}
			}catch( Exception e ){
				logger.error( "system filter okrWorkBaseInfo got an exception.", e );
				result.error( e );
			}
			if( wrapsWorkBaseInfoList_for_center != null && !wrapsWorkBaseInfoList_for_center.isEmpty() ){
				try {
					SortTools.asc( wrapsWorkBaseInfoList_for_center, "completeDateLimit" );
				} catch (Exception e) {
					result.setUserMessage( "系统为工作进行排序时发生异常！" );
					logger.error( "system sort work list got an exception.", e );
					result.error( e );
				}
			}
			if( wrapOutOkrCenterWorkInfo != null ){
				wrapOutOkrCenterWorkInfo.setWorks( wrapsWorkBaseInfoList_for_center );
				result.setData( wrapOutOkrCenterWorkInfo );
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	/**
	 * 根据工作信息装配下级工作信息（递归）
	 * @param all_wrapWorkBaseInfoList
	 * @param wrap_work
	 * @return
	 */
	private WrapOutOkrWorkBaseInfo composeWorkInfo(List<WrapOutOkrWorkBaseInfo> all_wrapWorkBaseInfoList, WrapOutOkrWorkBaseInfo wrap_work) {
		if( all_wrapWorkBaseInfoList != null && !all_wrapWorkBaseInfoList.isEmpty() ){
			for( WrapOutOkrWorkBaseInfo work : all_wrapWorkBaseInfoList ){
				if( work.getParentWorkId() != null && work.getParentWorkId().equalsIgnoreCase( wrap_work.getId() )){
				   //说明该工作是wrap_work的下级工作
					work = composeWorkInfo(all_wrapWorkBaseInfoList, work);
					wrap_work.addNewSubWorkBaseInfo( work );
				}
			}
		}
		return wrap_work;
	}

	@HttpMethodDescribe(value = "列示根据过滤条件查询的OkrWorkBaseInfo[草稿],下一页.", response = WrapOutOkrCenterWorkInfo.class, request = com.x.okr.assemble.control.jaxrs.okrworkperson.WrapInFilter.class)
	@PUT
	@Path( "draft/list/{id}/next/{count}" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listDraftNextWithFilter( @Context HttpServletRequest request, @PathParam( "id" ) String id, @PathParam( "count" ) Integer count, com.x.okr.assemble.control.jaxrs.okrworkperson.WrapInFilter wrapIn ) {
		ActionResult<List<WrapOutOkrWorkBaseInfo>> result = new ActionResult<>();
		List<WrapOutOkrWorkBaseInfo> wraps = null;
		List<OkrWorkBaseInfo> okrWorkBaseInfoList = null;
		Long total = 0L;
		Boolean check = true;
		EffectivePerson currentPerson = this.effectivePerson(request);	
		OkrUserCache  okrUserCache  = null;
		if( check ){
			try {
				okrUserCache = okrUserInfoService.getOkrUserCacheWithPersonName( currentPerson.getName() );
			} catch (Exception e1) {
				check = false;
				result.error( new Exception( "系统获取用户登录记录信息发生异常!" ) );
				result.setUserMessage( "系统获取用户登录记录信息发生异常!" );
				logger.error( "system get login indentity with person name got an exception", e1 );
			}	
		}		
		if( check && ( okrUserCache == null || okrUserCache.getLoginIdentityName() == null ) ){
			check = false;
			logger.error( "system query user identity got an exception.user:" + currentPerson.getName());
			result.error( new Exception( "系统未获取到用户登录身份(登录用户名)，请重新打开应用!" ) );
			result.setUserMessage( "系统未获取到用户登录身份(登录用户名)，请重新打开应用!" );
		}
		if( wrapIn == null ){
			wrapIn = new com.x.okr.assemble.control.jaxrs.okrworkperson.WrapInFilter();
		}
		if( check ){
			try{
				//信息状态要是正常的，已删除的数据不需要查询出来
				wrapIn.setProcessIdentities( null );
				wrapIn.setWorkProcessStatuses( null );
				wrapIn.setEmployeeNames( null );
				wrapIn.setEmployeeIdentities(null);
				wrapIn.setCompanyNames( null );
				wrapIn.setOrganizationNames( null );
				wrapIn.setInfoStatuses( null );
				wrapIn.addQueryEmployeeIdentities( okrUserCache.getLoginIdentityName() );
				
				wrapIn.addQueryInfoStatus( "正常" );	
				wrapIn.addQueryWorkProcessStatus( "草稿" );
				wrapIn.addQueryProcessIdentity( "部署者" );
				
				okrWorkBaseInfoList = okrWorkBaseInfoService.listWorkNextWithFilter( id, count, wrapIn );			
				//从数据库中查询符合条件的对象总数
				total = okrWorkBaseInfoService.getWorkCountWithFilter( wrapIn );
				wraps = wrapout_copier.copy( okrWorkBaseInfoList );
				result.setCount( total );
				result.setData( wraps );
			}catch(Throwable th){
				logger.error( "system filter okrWorkBaseInfo got an exception." );
				th.printStackTrace();
				result.error(th);
			}
		}
		
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "列示根据过滤条件查询的OkrCenterWorkInfo[草稿],上一页.", response = WrapOutOkrCenterWorkInfo.class, request = com.x.okr.assemble.control.jaxrs.okrworkperson.WrapInFilter.class)
	@PUT
	@Path( "draft/list/{id}/prev/{count}" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listDraftPrevWithFilter(@Context HttpServletRequest request, @PathParam( "id" ) String id, @PathParam( "count" ) Integer count, com.x.okr.assemble.control.jaxrs.okrworkperson.WrapInFilter wrapIn) {
		ActionResult<List<WrapOutOkrWorkBaseInfo>> result = new ActionResult<>();
		List<WrapOutOkrWorkBaseInfo> wraps = null;
		List<OkrWorkBaseInfo> okrWorkBaseInfoList = null;
		Long total = 0L;
		Boolean check = true;
		EffectivePerson currentPerson = this.effectivePerson(request);	
		OkrUserCache  okrUserCache  = null;
		if( check ){
			try {
				okrUserCache = okrUserInfoService.getOkrUserCacheWithPersonName( currentPerson.getName() );
			} catch (Exception e1) {
				check = false;
				result.error( new Exception( "系统获取用户登录记录信息发生异常!" ) );
				result.setUserMessage( "系统获取用户登录记录信息发生异常!" );
				logger.error( "system get login indentity with person name got an exception", e1 );
			}	
		}		
		if( check && ( okrUserCache == null || okrUserCache.getLoginIdentityName() == null ) ){
			check = false;
			logger.error( "system query user identity got an exception.user:" + currentPerson.getName());
			result.error( new Exception( "系统未获取到用户登录身份(登录用户名)，请重新打开应用!" ) );
			result.setUserMessage( "系统未获取到用户登录身份(登录用户名)，请重新打开应用!" );
		}
		if( wrapIn == null ){
			wrapIn = new com.x.okr.assemble.control.jaxrs.okrworkperson.WrapInFilter();
		}
		if( check ){
			try{				
				//信息状态要是正常的，已删除的数据不需要查询出来
				wrapIn.setProcessIdentities( null );
				wrapIn.setWorkProcessStatuses( null );
				wrapIn.setEmployeeNames( null );
				wrapIn.setEmployeeIdentities(null);
				wrapIn.setCompanyNames( null );
				wrapIn.setOrganizationNames( null );
				wrapIn.setInfoStatuses( null );
				wrapIn.addQueryEmployeeIdentities( okrUserCache.getLoginIdentityName() );
				
				wrapIn.addQueryInfoStatus( "正常" );	
				wrapIn.addQueryWorkProcessStatus( "草稿" );
				wrapIn.addQueryProcessIdentity( "部署者" );
				okrWorkBaseInfoList = okrWorkBaseInfoService.listWorkPrevWithFilter( id, count, wrapIn );			
				//从数据库中查询符合条件的对象总数
				total = okrWorkBaseInfoService.getWorkCountWithFilter( wrapIn );
				wraps = wrapout_copier.copy( okrWorkBaseInfoList );
				result.setCount( total );
				result.setData( wraps );
			}catch(Throwable th){
				logger.error( "system filter okrWorkBaseInfo got an exception." );
				th.printStackTrace();
				result.error(th);
			}
		}
		
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "列示根据过滤条件查询的OkrWorkBaseInfo[部署的],下一页.", response = WrapOutOkrCenterWorkInfo.class, request = com.x.okr.assemble.control.jaxrs.okrworkperson.WrapInFilter.class)
	@PUT
	@Path( "deployed/list/{id}/next/{count}" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listMyDeployedNextWithFilter( @Context HttpServletRequest request, @PathParam( "id" ) String id, @PathParam( "count" ) Integer count, com.x.okr.assemble.control.jaxrs.okrworkperson.WrapInFilter wrapIn ) {
		ActionResult<List<WrapOutOkrWorkBaseInfo>> result = new ActionResult<>();
		List<WrapOutOkrWorkBaseInfo> wraps = null;
		List<OkrWorkBaseInfo> okrWorkBaseInfoList = null;
		Long total = 0L;
		Boolean check = true;
		EffectivePerson currentPerson = this.effectivePerson( request );		
		OkrUserCache  okrUserCache  = null;
		if( check ){
			try {
				okrUserCache = okrUserInfoService.getOkrUserCacheWithPersonName( currentPerson.getName() );
			} catch (Exception e1) {
				check = false;
				result.error( new Exception( "系统获取用户登录记录信息发生异常!" ) );
				result.setUserMessage( "系统获取用户登录记录信息发生异常!" );
				logger.error( "system get login indentity with person name got an exception", e1 );
			}	
		}		
		if( check && ( okrUserCache == null || okrUserCache.getLoginIdentityName() == null ) ){
			check = false;
			logger.error( "system query user identity got an exception.user:" + currentPerson.getName());
			result.error( new Exception( "系统未获取到用户登录身份(登录用户名)，请重新打开应用!" ) );
			result.setUserMessage( "系统未获取到用户登录身份(登录用户名)，请重新打开应用!" );
		}
		if( wrapIn == null ){
			wrapIn = new com.x.okr.assemble.control.jaxrs.okrworkperson.WrapInFilter();
		}
		
		if( check ){
			try{
				//信息状态要是正常的，已删除的数据不需要查询出来
				wrapIn.setProcessIdentities( null );
				wrapIn.setEmployeeNames( null );
				wrapIn.setEmployeeIdentities(null);
				wrapIn.setCompanyNames( null );
				wrapIn.setOrganizationNames( null );
				wrapIn.setInfoStatuses( null );	
				wrapIn.addQueryEmployeeIdentities( okrUserCache.getLoginIdentityName() );
				
				wrapIn.addQueryInfoStatus( "正常" );	
				if( wrapIn.getWorkProcessStatuses() == null ){
					wrapIn.addQueryWorkProcessStatus( "待审核" );
					wrapIn.addQueryWorkProcessStatus( "待确认" );
					wrapIn.addQueryWorkProcessStatus( "执行中" );
					wrapIn.addQueryWorkProcessStatus( "已完成" );
					wrapIn.addQueryWorkProcessStatus( "已撤消" );
				}
				wrapIn.addQueryProcessIdentity( "部署者" );
				okrWorkBaseInfoList = okrWorkBaseInfoService.listWorkNextWithFilter( id, count, wrapIn );			
				//从数据库中查询符合条件的对象总数
				total = okrWorkBaseInfoService.getWorkCountWithFilter( wrapIn );
				wraps = wrapout_copier.copy( okrWorkBaseInfoList );
				result.setCount( total );
				result.setData( wraps );
			}catch(Throwable th){
				logger.error( "system filter okrWorkBaseInfo got an exception." );
				th.printStackTrace();
				result.error(th);
			}
		}
		
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "列示根据过滤条件查询的OkrCenterWorkInfo[部署],上一页.", response = WrapOutOkrCenterWorkInfo.class, request = com.x.okr.assemble.control.jaxrs.okrworkperson.WrapInFilter.class)
	@PUT
	@Path( "deployed/list/{id}/prev/{count}" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listMyDeployedPrevWithFilter(@Context HttpServletRequest request, @PathParam( "id" ) String id, @PathParam( "count" ) Integer count, com.x.okr.assemble.control.jaxrs.okrworkperson.WrapInFilter wrapIn) {
		ActionResult<List<WrapOutOkrWorkBaseInfo>> result = new ActionResult<>();
		List<WrapOutOkrWorkBaseInfo> wraps = null;
		List<OkrWorkBaseInfo> okrWorkBaseInfoList = null;
		Long total = 0L;
		Boolean check = true;
		EffectivePerson currentPerson = this.effectivePerson( request );
		OkrUserCache  okrUserCache  = null;
		if( check ){
			try {
				okrUserCache = okrUserInfoService.getOkrUserCacheWithPersonName( currentPerson.getName() );
			} catch (Exception e1) {
				check = false;
				result.error( new Exception( "系统获取用户登录记录信息发生异常!" ) );
				result.setUserMessage( "系统获取用户登录记录信息发生异常!" );
				logger.error( "system get login indentity with person name got an exception", e1 );
			}	
		}		
		if( check && ( okrUserCache == null || okrUserCache.getLoginIdentityName() == null ) ){
			check = false;
			logger.error( "system query user identity got an exception.user:" + currentPerson.getName());
			result.error( new Exception( "系统未获取到用户登录身份(登录用户名)，请重新打开应用!" ) );
			result.setUserMessage( "系统未获取到用户登录身份(登录用户名)，请重新打开应用!" );
		}
		if( wrapIn == null ){
			wrapIn = new com.x.okr.assemble.control.jaxrs.okrworkperson.WrapInFilter();
		}
		if( check ){
			try{
				//信息状态要是正常的，已删除的数据不需要查询出来
				wrapIn.setProcessIdentities( null );
				//wrapIn.setWorkProcessStatuses( null );
				wrapIn.setEmployeeNames( null );
				wrapIn.setEmployeeIdentities(null);
				wrapIn.setCompanyNames( null );
				wrapIn.setOrganizationNames( null );
				wrapIn.setInfoStatuses( null );
				wrapIn.addQueryEmployeeIdentities( okrUserCache.getLoginIdentityName() );
				
				wrapIn.addQueryInfoStatus( "正常" );	
				if( wrapIn.getWorkProcessStatuses() == null ){
					wrapIn.addQueryWorkProcessStatus( "待审核" );
					wrapIn.addQueryWorkProcessStatus( "待确认" );
					wrapIn.addQueryWorkProcessStatus( "执行中" );
					wrapIn.addQueryWorkProcessStatus( "已完成" );
					wrapIn.addQueryWorkProcessStatus( "已撤消" );
				}
				wrapIn.addQueryProcessIdentity( "部署者" );
				okrWorkBaseInfoList = okrWorkBaseInfoService.listWorkPrevWithFilter( id, count, wrapIn );			
				//从数据库中查询符合条件的对象总数
				total = okrWorkBaseInfoService.getWorkCountWithFilter( wrapIn );
				wraps = wrapout_copier.copy( okrWorkBaseInfoList );
				result.setCount( total );
				result.setData( wraps );
			}catch(Throwable th){
				logger.error( "system filter okrWorkBaseInfo got an exception." );
				th.printStackTrace();
				result.error(th);
			}
		}
		
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "列示根据过滤条件查询的OkrWorkBaseInfo[阅知者],下一页.", response = WrapOutOkrCenterWorkInfo.class, request = com.x.okr.assemble.control.jaxrs.okrworkperson.WrapInFilter.class)
	@PUT
	@Path( "read/list/{id}/next/{count}" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listMyReadNextWithFilter( @Context HttpServletRequest request, @PathParam( "id" ) String id, @PathParam( "count" ) Integer count, com.x.okr.assemble.control.jaxrs.okrworkperson.WrapInFilter wrapIn ) {
		ActionResult<List<WrapOutOkrWorkBaseInfo>> result = new ActionResult<>();
		List<WrapOutOkrWorkBaseInfo> wraps = null;
		List<OkrWorkBaseInfo> okrWorkBaseInfoList = null;
		Long total = 0L;
		Boolean check = true;
		OkrUserCache  okrUserCache  = null;
		EffectivePerson currentPerson = this.effectivePerson( request );	
		if( check ){
			try {
				okrUserCache = okrUserInfoService.getOkrUserCacheWithPersonName( currentPerson.getName() );
			} catch (Exception e1) {
				check = false;
				result.error( new Exception( "系统获取用户登录记录信息发生异常!" ) );
				result.setUserMessage( "系统获取用户登录记录信息发生异常!" );
				logger.error( "system get login indentity with person name got an exception", e1 );
			}	
		}		
		if( check && ( okrUserCache == null || okrUserCache.getLoginIdentityName() == null ) ){
			check = false;
			logger.error( "system query user identity got an exception.user:" + currentPerson.getName());
			result.error( new Exception( "系统未获取到用户登录身份(登录用户名)，请重新打开应用!" ) );
			result.setUserMessage( "系统未获取到用户登录身份(登录用户名)，请重新打开应用!" );
		}
		if( wrapIn == null ){
			wrapIn = new com.x.okr.assemble.control.jaxrs.okrworkperson.WrapInFilter();
		}
		if( check ){
			try{
				//信息状态要是正常的，已删除的数据不需要查询出来
				wrapIn.setProcessIdentities( null );
				wrapIn.setEmployeeNames( null );
				wrapIn.setEmployeeIdentities(null);
				wrapIn.setCompanyNames( null );
				wrapIn.setOrganizationNames( null );
				wrapIn.setInfoStatuses( null );
				wrapIn.addQueryEmployeeIdentities( okrUserCache.getLoginIdentityName() );
				
				wrapIn.addQueryInfoStatus( "正常" );	
				if( wrapIn.getWorkProcessStatuses() == null ){
					wrapIn.addQueryWorkProcessStatus( "待审核" );
					wrapIn.addQueryWorkProcessStatus( "待确认" );
					wrapIn.addQueryWorkProcessStatus( "执行中" );
					wrapIn.addQueryWorkProcessStatus( "已完成" );
					wrapIn.addQueryWorkProcessStatus( "已撤消" );
				}
				wrapIn.addQueryProcessIdentity( "阅知者" );
				okrWorkBaseInfoList = okrWorkBaseInfoService.listWorkNextWithFilter( id, count, wrapIn );			
				//从数据库中查询符合条件的对象总数
				total = okrWorkBaseInfoService.getWorkCountWithFilter( wrapIn );
				wraps = wrapout_copier.copy( okrWorkBaseInfoList );
				result.setCount( total );
				result.setData( wraps );
			}catch(Throwable th){
				logger.error( "system filter okrWorkBaseInfo got an exception." );
				th.printStackTrace();
				result.error(th);
			}
		}
		
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "列示根据过滤条件查询的OkrCenterWorkInfo[阅知者],上一页.", response = WrapOutOkrCenterWorkInfo.class, request = com.x.okr.assemble.control.jaxrs.okrworkperson.WrapInFilter.class)
	@PUT
	@Path( "read/list/{id}/prev/{count}" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listMyReadPrevWithFilter(@Context HttpServletRequest request, @PathParam( "id" ) String id, @PathParam( "count" ) Integer count, com.x.okr.assemble.control.jaxrs.okrworkperson.WrapInFilter wrapIn) {
		ActionResult<List<WrapOutOkrWorkBaseInfo>> result = new ActionResult<>();
		List<WrapOutOkrWorkBaseInfo> wraps = null;
		List<OkrWorkBaseInfo> okrWorkBaseInfoList = null;
		Long total = 0L;
		Boolean check = true;
		OkrUserCache  okrUserCache  = null;
		EffectivePerson currentPerson = this.effectivePerson( request );	
		if( check ){
			try {
				okrUserCache = okrUserInfoService.getOkrUserCacheWithPersonName( currentPerson.getName() );
			} catch (Exception e1) {
				check = false;
				result.error( new Exception( "系统获取用户登录记录信息发生异常!" ) );
				result.setUserMessage( "系统获取用户登录记录信息发生异常!" );
				logger.error( "system get login indentity with person name got an exception", e1 );
			}	
		}		
		if( check && ( okrUserCache == null || okrUserCache.getLoginIdentityName() == null ) ){
			check = false;
			logger.error( "system query user identity got an exception.user:" + currentPerson.getName());
			result.error( new Exception( "系统未获取到用户登录身份(登录用户名)，请重新打开应用!" ) );
			result.setUserMessage( "系统未获取到用户登录身份(登录用户名)，请重新打开应用!" );
		}
		if( wrapIn == null ){
			wrapIn = new com.x.okr.assemble.control.jaxrs.okrworkperson.WrapInFilter();
		}
		if( check ){
			try{
				//信息状态要是正常的，已删除的数据不需要查询出来
				wrapIn.setProcessIdentities( null );
				wrapIn.setEmployeeNames( null );
				wrapIn.setEmployeeIdentities(null);
				wrapIn.setCompanyNames( null );
				wrapIn.setOrganizationNames( null );
				wrapIn.setInfoStatuses( null );
				wrapIn.addQueryEmployeeIdentities( okrUserCache.getLoginIdentityName() );
				
				wrapIn.addQueryInfoStatus( "正常" );	
				if( wrapIn.getWorkProcessStatuses() == null ){
					wrapIn.addQueryWorkProcessStatus( "待审核" );
					wrapIn.addQueryWorkProcessStatus( "待确认" );
					wrapIn.addQueryWorkProcessStatus( "执行中" );
					wrapIn.addQueryWorkProcessStatus( "已完成" );
					wrapIn.addQueryWorkProcessStatus( "已撤消" );
				}
				wrapIn.addQueryProcessIdentity( "阅知者" );
				okrWorkBaseInfoList = okrWorkBaseInfoService.listWorkPrevWithFilter( id, count, wrapIn );			
				//从数据库中查询符合条件的对象总数
				total = okrWorkBaseInfoService.getWorkCountWithFilter( wrapIn );
				wraps = wrapout_copier.copy( okrWorkBaseInfoList );
				result.setCount( total );
				result.setData( wraps );
			}catch(Throwable th){
				logger.error( "system filter okrWorkBaseInfo got an exception." );
				th.printStackTrace();
				result.error(th);
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "列示根据过滤条件查询的OkrWorkBaseInfo[负责的],下一页.", response = WrapOutOkrCenterWorkInfo.class, request = com.x.okr.assemble.control.jaxrs.okrworkperson.WrapInFilter.class)
	@PUT
	@Path( "responsibility/list/{id}/next/{count}" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listMyResponsibilityNextWithFilter( @Context HttpServletRequest request, @PathParam( "id" ) String id, @PathParam( "count" ) Integer count, com.x.okr.assemble.control.jaxrs.okrworkperson.WrapInFilter wrapIn ) {
		ActionResult<List<WrapOutOkrWorkBaseInfo>> result = new ActionResult<>();
		List<WrapOutOkrWorkBaseInfo> wraps = null;
		List<OkrWorkBaseInfo> okrWorkBaseInfoList = null;
		OkrWorkAuthorizeRecord okrWorkAuthorizeRecord = null;
		Long total = 0L;
		Boolean check = true;
		OkrUserCache  okrUserCache  = null;
		EffectivePerson currentPerson = this.effectivePerson( request );	
		if( check ){
			try {
				okrUserCache = okrUserInfoService.getOkrUserCacheWithPersonName( currentPerson.getName() );
			} catch (Exception e1) {
				check = false;
				result.error( new Exception( "系统获取用户登录记录信息发生异常!" ) );
				result.setUserMessage( "系统获取用户登录记录信息发生异常!" );
				logger.error( "system get login indentity with person name got an exception", e1 );
			}	
		}		
		if( check && ( okrUserCache == null || okrUserCache.getLoginIdentityName() == null ) ){
			check = false;
			logger.error( "system query user identity got an exception.user:" + currentPerson.getName());
			result.error( new Exception( "系统未获取到用户登录身份(登录用户名)，请重新打开应用!" ) );
			result.setUserMessage( "系统未获取到用户登录身份(登录用户名)，请重新打开应用!" );
		}
		if( wrapIn == null ){
			wrapIn = new com.x.okr.assemble.control.jaxrs.okrworkperson.WrapInFilter();
		}
		if( check ){
			try{
				//信息状态要是正常的，已删除的数据不需要查询出来
				wrapIn.setProcessIdentities( null );
				wrapIn.setEmployeeNames( null );
				wrapIn.setEmployeeIdentities(null);
				wrapIn.setCompanyNames( null );
				wrapIn.setOrganizationNames( null );
				wrapIn.setInfoStatuses( null );
				wrapIn.addQueryEmployeeIdentities( okrUserCache.getLoginIdentityName() );
				
				wrapIn.addQueryInfoStatus( "正常" );	
				if( wrapIn.getWorkProcessStatuses() == null ){
					wrapIn.addQueryWorkProcessStatus( "待审核" );
					wrapIn.addQueryWorkProcessStatus( "待确认" );
					wrapIn.addQueryWorkProcessStatus( "执行中" );
					wrapIn.addQueryWorkProcessStatus( "已完成" );
					wrapIn.addQueryWorkProcessStatus( "已撤消" );
				}
				wrapIn.addQueryProcessIdentity( "责任者" );
				okrWorkBaseInfoList = okrWorkBaseInfoService.listWorkNextWithFilter( id, count, wrapIn );			
				//从数据库中查询符合条件的对象总数
				total = okrWorkBaseInfoService.getWorkCountWithFilter( wrapIn );
				wraps = wrapout_copier.copy( okrWorkBaseInfoList );
				
				for( WrapOutOkrWorkBaseInfo wrap : wraps ){
					//获取该工作和当前责任人相关的授权信息
					okrWorkAuthorizeRecord = okrWorkAuthorizeRecordService.getLastAuthorizeRecord( wrap.getId(), okrUserCache.getLoginIdentityName()  );
					if( okrWorkAuthorizeRecord != null ){
						wrap.setOkrWorkAuthorizeRecord( okrWorkAuthorizeRecord_wrapout_copier.copy( okrWorkAuthorizeRecord ) );
					}
				}
				result.setCount( total );
				result.setData( wraps );
			}catch( Throwable th ){
				logger.error( "system filter okrWorkBaseInfo got an exception." );
				th.printStackTrace();
				result.error(th);
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "列示根据过滤条件查询的OkrCenterWorkInfo[部署],上一页.", response = WrapOutOkrCenterWorkInfo.class, request = com.x.okr.assemble.control.jaxrs.okrworkperson.WrapInFilter.class)
	@PUT
	@Path( "responsibility/list/{id}/prev/{count}" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listMyResponsibilityPrevWithFilter(@Context HttpServletRequest request, @PathParam( "id" ) String id, @PathParam( "count" ) Integer count, com.x.okr.assemble.control.jaxrs.okrworkperson.WrapInFilter wrapIn) {
		ActionResult<List<WrapOutOkrWorkBaseInfo>> result = new ActionResult<>();
		List<WrapOutOkrWorkBaseInfo> wraps = null;
		List<OkrWorkBaseInfo> okrWorkBaseInfoList = null;
		Long total = 0L;
		Boolean check = true;
		OkrUserCache  okrUserCache  = null;
		EffectivePerson currentPerson = this.effectivePerson( request );	
		if( check ){
			try {
				okrUserCache = okrUserInfoService.getOkrUserCacheWithPersonName( currentPerson.getName() );
			} catch (Exception e1) {
				check = false;
				result.error( new Exception( "系统获取用户登录记录信息发生异常!" ) );
				result.setUserMessage( "系统获取用户登录记录信息发生异常!" );
				logger.error( "system get login indentity with person name got an exception", e1 );
			}	
		}		
		if( check && ( okrUserCache == null || okrUserCache.getLoginIdentityName() == null ) ){
			check = false;
			logger.error( "system query user identity got an exception.user:" + currentPerson.getName());
			result.error( new Exception( "系统未获取到用户登录身份(登录用户名)，请重新打开应用!" ) );
			result.setUserMessage( "系统未获取到用户登录身份(登录用户名)，请重新打开应用!" );
		}
		if( wrapIn == null ){
			wrapIn = new com.x.okr.assemble.control.jaxrs.okrworkperson.WrapInFilter();
		}
		if( check ){
			try{
				//信息状态要是正常的，已删除的数据不需要查询出来
				wrapIn.setProcessIdentities( null );
				//wrapIn.setWorkProcessStatuses( null );
				wrapIn.setEmployeeNames( null );
				wrapIn.setEmployeeIdentities(null);
				wrapIn.setCompanyNames( null );
				wrapIn.setOrganizationNames( null );
				wrapIn.setInfoStatuses( null );
				wrapIn.addQueryEmployeeIdentities( okrUserCache.getLoginIdentityName() );
				
				wrapIn.addQueryInfoStatus( "正常" );	
				if( wrapIn.getWorkProcessStatuses() == null ){
					wrapIn.addQueryWorkProcessStatus( "待审核" );
					wrapIn.addQueryWorkProcessStatus( "待确认" );
					wrapIn.addQueryWorkProcessStatus( "执行中" );
					wrapIn.addQueryWorkProcessStatus( "已完成" );
					wrapIn.addQueryWorkProcessStatus( "已撤消" );
				}
				wrapIn.addQueryProcessIdentity( "责任者" );
				okrWorkBaseInfoList = okrWorkBaseInfoService.listWorkPrevWithFilter( id, count, wrapIn );			
				//从数据库中查询符合条件的对象总数
				total = okrWorkBaseInfoService.getWorkCountWithFilter( wrapIn );
				wraps = wrapout_copier.copy( okrWorkBaseInfoList );
				result.setCount( total );
				result.setData( wraps );
			}catch(Throwable th){
				logger.error( "system filter okrWorkBaseInfo got an exception." );
				th.printStackTrace();
				result.error(th);
			}
		}
		
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "列示根据过滤条件查询的OkrWorkBaseInfo[授权者],下一页.", response = WrapOutOkrCenterWorkInfo.class, request = com.x.okr.assemble.control.jaxrs.okrworkperson.WrapInFilter.class)
	@PUT
	@Path( "delegate/list/{id}/next/{count}" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listMyDelegateNextWithFilter( @Context HttpServletRequest request, @PathParam( "id" ) String id, @PathParam( "count" ) Integer count, com.x.okr.assemble.control.jaxrs.okrworkperson.WrapInFilter wrapIn ) {
		ActionResult<List<WrapOutOkrWorkBaseInfo>> result = new ActionResult<>();
		List<WrapOutOkrWorkBaseInfo> wraps = null;
		List<OkrWorkBaseInfo> okrWorkBaseInfoList = null;
		OkrWorkAuthorizeRecord okrWorkAuthorizeRecord = null;
		Long total = 0L;
		Boolean check = true;
		OkrUserCache  okrUserCache  = null;
		EffectivePerson currentPerson = this.effectivePerson( request );	
		if( check ){
			try {
				okrUserCache = okrUserInfoService.getOkrUserCacheWithPersonName( currentPerson.getName() );
			} catch (Exception e1) {
				check = false;
				result.error( new Exception( "系统获取用户登录记录信息发生异常!" ) );
				result.setUserMessage( "系统获取用户登录记录信息发生异常!" );
				logger.error( "system get login indentity with person name got an exception", e1 );
			}	
		}		
		if( check && ( okrUserCache == null || okrUserCache.getLoginIdentityName() == null ) ){
			check = false;
			logger.error( "system query user identity got an exception.user:" + currentPerson.getName());
			result.error( new Exception( "系统未获取到用户登录身份(登录用户名)，请重新打开应用!" ) );
			result.setUserMessage( "系统未获取到用户登录身份(登录用户名)，请重新打开应用!" );
		}
		if( wrapIn == null ){
			wrapIn = new com.x.okr.assemble.control.jaxrs.okrworkperson.WrapInFilter();
		}
		if( check ){
			try{
				//信息状态要是正常的，已删除的数据不需要查询出来
				wrapIn.setProcessIdentities( null );
				wrapIn.setEmployeeNames( null );
				wrapIn.setEmployeeIdentities(null);
				wrapIn.setCompanyNames( null );
				wrapIn.setOrganizationNames( null );
				wrapIn.setInfoStatuses( null );
				wrapIn.addQueryEmployeeIdentities( okrUserCache.getLoginIdentityName() );
				
				wrapIn.addQueryInfoStatus( "正常" );	
				if( wrapIn.getWorkProcessStatuses() == null ){
					wrapIn.addQueryWorkProcessStatus( "待审核" );
					wrapIn.addQueryWorkProcessStatus( "待确认" );
					wrapIn.addQueryWorkProcessStatus( "执行中" );
					wrapIn.addQueryWorkProcessStatus( "已完成" );
					wrapIn.addQueryWorkProcessStatus( "已撤消" );
				}
				wrapIn.addQueryProcessIdentity( "授权者" );
				okrWorkBaseInfoList = okrWorkBaseInfoService.listWorkNextWithFilter( id, count, wrapIn );			
				//从数据库中查询符合条件的对象总数
				total = okrWorkBaseInfoService.getWorkCountWithFilter( wrapIn );
				wraps = wrapout_copier.copy( okrWorkBaseInfoList );
				for( WrapOutOkrWorkBaseInfo wrapOutOkrWorkBaseInfo : wraps ){
					//获取该工作和当前责任人相关的授权信息
					okrWorkAuthorizeRecord = okrWorkAuthorizeRecordService.getLastAuthorizeRecord( wrapOutOkrWorkBaseInfo.getId(), okrUserCache.getLoginIdentityName()  );
					if( okrWorkAuthorizeRecord != null ){
						wrapOutOkrWorkBaseInfo.setOkrWorkAuthorizeRecord( okrWorkAuthorizeRecord_wrapout_copier.copy( okrWorkAuthorizeRecord ) );
					}
				}
				result.setCount( total );
				result.setData( wraps );
			}catch(Throwable th){
				logger.error( "system filter okrWorkBaseInfo got an exception." );
				th.printStackTrace();
				result.error(th);
			}
		}
		
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "列示根据过滤条件查询的OkrCenterWorkInfo[授权者],上一页.", response = WrapOutOkrCenterWorkInfo.class, request = com.x.okr.assemble.control.jaxrs.okrworkperson.WrapInFilter.class)
	@PUT
	@Path( "delegate/list/{id}/prev/{count}" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listMyDelegatePrevWithFilter(@Context HttpServletRequest request, @PathParam( "id" ) String id, @PathParam( "count" ) Integer count, com.x.okr.assemble.control.jaxrs.okrworkperson.WrapInFilter wrapIn) {
		ActionResult<List<WrapOutOkrWorkBaseInfo>> result = new ActionResult<>();
		List<WrapOutOkrWorkBaseInfo> wraps = null;
		List<OkrWorkBaseInfo> okrWorkBaseInfoList = null;
		OkrWorkAuthorizeRecord okrWorkAuthorizeRecord = null;
		Long total = 0L;
		Boolean check = true;
		OkrUserCache  okrUserCache  = null;
		EffectivePerson currentPerson = this.effectivePerson( request );	
		if( check ){
			try {
				okrUserCache = okrUserInfoService.getOkrUserCacheWithPersonName( currentPerson.getName() );
			} catch (Exception e1) {
				check = false;
				result.error( new Exception( "系统获取用户登录记录信息发生异常!" ) );
				result.setUserMessage( "系统获取用户登录记录信息发生异常!" );
				logger.error( "system get login indentity with person name got an exception", e1 );
			}	
		}		
		if( check && ( okrUserCache == null || okrUserCache.getLoginIdentityName() == null ) ){
			check = false;
			logger.error( "system query user identity got an exception.user:" + currentPerson.getName());
			result.error( new Exception( "系统未获取到用户登录身份(登录用户名)，请重新打开应用!" ) );
			result.setUserMessage( "系统未获取到用户登录身份(登录用户名)，请重新打开应用!" );
		}
		if( wrapIn == null ){
			wrapIn = new com.x.okr.assemble.control.jaxrs.okrworkperson.WrapInFilter();
		}
		if( check ){
			try{
				//信息状态要是正常的，已删除的数据不需要查询出来
				wrapIn.setProcessIdentities( null );
				wrapIn.setEmployeeNames( null );
				wrapIn.setEmployeeIdentities(null);
				wrapIn.setCompanyNames( null );
				wrapIn.setOrganizationNames( null );
				wrapIn.setInfoStatuses( null );
				wrapIn.addQueryEmployeeIdentities( okrUserCache.getLoginIdentityName() );
				
				wrapIn.addQueryInfoStatus( "正常" );	
				if( wrapIn.getWorkProcessStatuses() == null ){
					wrapIn.addQueryWorkProcessStatus( "待审核" );
					wrapIn.addQueryWorkProcessStatus( "待确认" );
					wrapIn.addQueryWorkProcessStatus( "执行中" );
					wrapIn.addQueryWorkProcessStatus( "已完成" );
					wrapIn.addQueryWorkProcessStatus( "已撤消" );
				}
				wrapIn.addQueryProcessIdentity( "授权者" );
				okrWorkBaseInfoList = okrWorkBaseInfoService.listWorkPrevWithFilter( id, count, wrapIn );			
				//从数据库中查询符合条件的对象总数
				total = okrWorkBaseInfoService.getWorkCountWithFilter( wrapIn );
				wraps = wrapout_copier.copy( okrWorkBaseInfoList );
				for( WrapOutOkrWorkBaseInfo wrapOutOkrWorkBaseInfo : wraps ){
					//获取该工作和当前责任人相关的授权信息
					okrWorkAuthorizeRecord = okrWorkAuthorizeRecordService.getLastAuthorizeRecord( wrapOutOkrWorkBaseInfo.getId(), okrUserCache.getLoginIdentityName()  );
					if( okrWorkAuthorizeRecord != null ){
						wrapOutOkrWorkBaseInfo.setOkrWorkAuthorizeRecord( okrWorkAuthorizeRecord_wrapout_copier.copy( okrWorkAuthorizeRecord ) );
					}
				}
				result.setCount( total );
				result.setData( wraps );
			}catch(Throwable th){
				logger.error( "system filter okrWorkBaseInfo got an exception." );
				th.printStackTrace();
				result.error(th);
			}
		}
		
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "列示根据过滤条件查询的OkrWorkBaseInfo[协助者],下一页.", response = WrapOutOkrCenterWorkInfo.class, request = com.x.okr.assemble.control.jaxrs.okrworkperson.WrapInFilter.class)
	@PUT
	@Path( "cooperate/list/{id}/next/{count}" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listMyCooperateNextWithFilter( @Context HttpServletRequest request, @PathParam( "id" ) String id, @PathParam( "count" ) Integer count, com.x.okr.assemble.control.jaxrs.okrworkperson.WrapInFilter wrapIn ) {
		ActionResult<List<WrapOutOkrWorkBaseInfo>> result = new ActionResult<>();
		List<WrapOutOkrWorkBaseInfo> wraps = null;
		List<OkrWorkBaseInfo> okrWorkBaseInfoList = null;
		Long total = 0L;
		Boolean check = true;
		OkrUserCache  okrUserCache  = null;
		EffectivePerson currentPerson = this.effectivePerson( request );	
		if( check ){
			try {
				okrUserCache = okrUserInfoService.getOkrUserCacheWithPersonName( currentPerson.getName() );
			} catch (Exception e1) {
				check = false;
				result.error( new Exception( "系统获取用户登录记录信息发生异常!" ) );
				result.setUserMessage( "系统获取用户登录记录信息发生异常!" );
				logger.error( "system get login indentity with person name got an exception", e1 );
			}	
		}		
		if( check && ( okrUserCache == null || okrUserCache.getLoginIdentityName() == null ) ){
			check = false;
			logger.error( "system query user identity got an exception.user:" + currentPerson.getName());
			result.error( new Exception( "系统未获取到用户登录身份(登录用户名)，请重新打开应用!" ) );
			result.setUserMessage( "系统未获取到用户登录身份(登录用户名)，请重新打开应用!" );
		}
		if( wrapIn == null ){
			wrapIn = new com.x.okr.assemble.control.jaxrs.okrworkperson.WrapInFilter();
		}
		
		if( check ){
			try{
				//信息状态要是正常的，已删除的数据不需要查询出来
				wrapIn.setProcessIdentities( null );
				//wrapIn.setWorkProcessStatuses( null );
				wrapIn.setEmployeeNames( null );
				wrapIn.setEmployeeIdentities(null);
				wrapIn.setCompanyNames( null );
				wrapIn.setOrganizationNames( null );
				wrapIn.setInfoStatuses( null );
				wrapIn.addQueryEmployeeIdentities( okrUserCache.getLoginIdentityName() );
				
				wrapIn.addQueryInfoStatus( "正常" );	
				if( wrapIn.getWorkProcessStatuses() == null ){
					wrapIn.addQueryWorkProcessStatus( "待审核" );
					wrapIn.addQueryWorkProcessStatus( "待确认" );
					wrapIn.addQueryWorkProcessStatus( "执行中" );
					wrapIn.addQueryWorkProcessStatus( "已完成" );
					wrapIn.addQueryWorkProcessStatus( "已撤消" );
				}
				wrapIn.addQueryProcessIdentity( "协助者" );
				okrWorkBaseInfoList = okrWorkBaseInfoService.listWorkNextWithFilter( id, count, wrapIn );			
				//从数据库中查询符合条件的对象总数
				total = okrWorkBaseInfoService.getWorkCountWithFilter( wrapIn );
				wraps = wrapout_copier.copy( okrWorkBaseInfoList );
				result.setCount( total );
				result.setData( wraps );
			}catch(Throwable th){
				logger.error( "system filter okrWorkBaseInfo got an exception." );
				th.printStackTrace();
				result.error(th);
			}
		}
		
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "列示根据过滤条件查询的OkrCenterWorkInfo[协助者],上一页.", response = WrapOutOkrCenterWorkInfo.class, request = com.x.okr.assemble.control.jaxrs.okrworkperson.WrapInFilter.class)
	@PUT
	@Path( "cooperate/list/{id}/prev/{count}" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listMyCooperatePrevWithFilter(@Context HttpServletRequest request, @PathParam( "id" ) String id, @PathParam( "count" ) Integer count, com.x.okr.assemble.control.jaxrs.okrworkperson.WrapInFilter wrapIn) {
		ActionResult<List<WrapOutOkrWorkBaseInfo>> result = new ActionResult<>();
		List<WrapOutOkrWorkBaseInfo> wraps = null;
		List<OkrWorkBaseInfo> okrWorkBaseInfoList = null;
		Long total = 0L;
		Boolean check = true;
		OkrUserCache  okrUserCache  = null;
		EffectivePerson currentPerson = this.effectivePerson( request );	
		if( check ){
			try {
				okrUserCache = okrUserInfoService.getOkrUserCacheWithPersonName( currentPerson.getName() );
			} catch (Exception e1) {
				check = false;
				result.error( new Exception( "系统获取用户登录记录信息发生异常!" ) );
				result.setUserMessage( "系统获取用户登录记录信息发生异常!" );
				logger.error( "system get login indentity with person name got an exception", e1 );
			}	
		}		
		if( check && ( okrUserCache == null || okrUserCache.getLoginIdentityName() == null ) ){
			check = false;
			logger.error( "system query user identity got an exception.user:" + currentPerson.getName());
			result.error( new Exception( "系统未获取到用户登录身份(登录用户名)，请重新打开应用!" ) );
			result.setUserMessage( "系统未获取到用户登录身份(登录用户名)，请重新打开应用!" );
		}
		if( wrapIn == null ){
			wrapIn = new com.x.okr.assemble.control.jaxrs.okrworkperson.WrapInFilter();
		}
		
		if( check ){
			try{
				//信息状态要是正常的，已删除的数据不需要查询出来
				wrapIn.setProcessIdentities( null );
				//wrapIn.setWorkProcessStatuses( null );
				wrapIn.setEmployeeNames( null );
				wrapIn.setEmployeeIdentities(null);
				wrapIn.setCompanyNames( null );
				wrapIn.setOrganizationNames( null );
				wrapIn.setInfoStatuses( null );
				wrapIn.addQueryEmployeeIdentities( okrUserCache.getLoginIdentityName() );
				
				wrapIn.addQueryInfoStatus( "正常" );	
				if( wrapIn.getWorkProcessStatuses() == null ){
					wrapIn.addQueryWorkProcessStatus( "待审核" );
					wrapIn.addQueryWorkProcessStatus( "待确认" );
					wrapIn.addQueryWorkProcessStatus( "执行中" );
					wrapIn.addQueryWorkProcessStatus( "已完成" );
					wrapIn.addQueryWorkProcessStatus( "已撤消" );
				}
				wrapIn.addQueryProcessIdentity( "协助者" );
				okrWorkBaseInfoList = okrWorkBaseInfoService.listWorkPrevWithFilter( id, count, wrapIn );			
				//从数据库中查询符合条件的对象总数
				total = okrWorkBaseInfoService.getWorkCountWithFilter( wrapIn );
				wraps = wrapout_copier.copy( okrWorkBaseInfoList );
				result.setCount( total );
				result.setData( wraps );
			}catch(Throwable th){
				logger.error( "system filter okrWorkBaseInfo got an exception." );
				th.printStackTrace();
				result.error(th);
			}
		}
		
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "正式部署工作事项.", response = WrapOutOkrWorkBaseInfo.class, request = WrapInOkrWorkBaseInfo.class)
	@PUT
	@Path( "deploy" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response deploy( @Context HttpServletRequest request, WrapInOkrWorkBaseInfo wrapIn ) {
		ActionResult<WrapOutOkrWorkBaseInfo> result = new ActionResult<>();
		//前端传递所有需要部署的工作ID
		List<String> ids = null;
		List<OkrWorkBaseInfo> okrWorkBaseInfoList = new ArrayList<OkrWorkBaseInfo>();
		OkrWorkBaseInfo okrWorkBaseInfo = null;
		OkrCenterWorkInfo okrCenterWorkInfo  = null;
		EffectivePerson currentPerson = this.effectivePerson(request);
		String centerId = null;
		Boolean check = true;
		OkrUserCache  okrUserCache  = null;
		if( check ){
			try {
				okrUserCache = okrUserInfoService.getOkrUserCacheWithPersonName( currentPerson.getName() );
			} catch (Exception e1) {
				check = false;
				result.error( new Exception( "系统获取用户登录记录信息发生异常!" ) );
				result.setUserMessage( "系统获取用户登录记录信息发生异常!" );
				logger.error( "system get login indentity with person name got an exception", e1 );
			}	
		}		
		if( check && ( okrUserCache == null || okrUserCache.getLoginIdentityName() == null ) ){
			check = false;
			logger.error( "system query user identity got an exception.user:" + currentPerson.getName());
			result.error( new Exception( "系统未获取到用户登录身份(登录用户名)，请重新打开应用!" ) );
			result.setUserMessage( "系统未获取到用户登录身份(登录用户名)，请重新打开应用!" );
		}
		//对wrapIn里的信息进行校验
		if( check && okrUserCache.getLoginUserName() == null ){
			check = false;
			result.error( new Exception( "系统未获取到用户登录身份(登录用户名)，请重新打开应用!" ) );
			result.setUserMessage( "系统未获取到用户登录身份(登录用户名)，请重新打开应用!" );
			logger.error( "系统未获取到用户登录身份(登录用户名)，请重新打开应用!" );
		}
		
		if( check && wrapIn == null  ){
			check = false;
			result.error( new Exception( "传入的数据参数为空，无法继续进行操作!" ) );
			result.setUserMessage( "传入的数据参数为空，无法继续进行操作!" );
			logger.error( "传入的数据参数为空，无法继续进行操作!" );
		}		
		ids = wrapIn.getWorkIds();
		centerId = wrapIn.getCenterId();
		if( check && ids != null && ids.size() > 0 ){
			// 提前校验每一个工作信息是否已经存在，如果不存在，则全部部署不成功
			for ( String id : ids ) {
				if ( check ) {
					try {
						okrWorkBaseInfo = okrWorkBaseInfoService.get( id );
						// 判断工作信息是否全都存在
						if (okrWorkBaseInfo == null) {
							check = false;
							logger.error( "system can not fount object, okrCenterWorkInfo{'id':'" + id + "'} is not exist." );
							result.error( new Exception( "工作不存在，无法继续部署工作！" ) );
							result.setUserMessage( "工作不存在，无法继续部署工作！" );
							break;
						}
						okrWorkBaseInfoList.add( okrWorkBaseInfo );
						// 判断所有的工作是否都在同一个中心工作之下
						if (check) {
							if( centerId == null || centerId.isEmpty() ){
								centerId = okrWorkBaseInfo.getCenterId();
							}
							if ( centerId != null && !centerId.isEmpty() && !centerId.equals( okrWorkBaseInfo.getCenterId())) {
								check = false;
								logger.error( "works to deploy must belong to the same center work, there are diffrent center work in works." );
								result.error( new Exception( new Exception( "中心工作不存在， 标题：" + okrWorkBaseInfo.getTitle() ) ) );
								result.setUserMessage( "需要部署的工作不在同一中心工作中， 标题：" + okrWorkBaseInfo.getTitle());
							}
							centerId = okrWorkBaseInfo.getCenterId();
						}
						
						// 判断中心工作信息是否存在
						if (check) {
							if (centerId != null) {
								okrCenterWorkInfo = okrCenterWorkInfoService.get(centerId);
								if (okrCenterWorkInfo == null) {
									check = false;
									logger.error( "the center work for work{'id':'"+ okrWorkBaseInfo.getId() +"'} is not exsits." );
									result.error( new Exception( "中心工作不存在， 标题：" + okrWorkBaseInfo.getTitle() ) );
									result.setUserMessage( "中心工作不存在， 标题：" + okrWorkBaseInfo.getTitle() );
								}
							}
						}
					} catch ( Exception e ) {
						check = false;
						logger.error( "system check deploy work info for center work{'id':'" + id + "'} got an exception. ", e);
						result.error( e );
						result.setUserMessage( "系统校验需要部署的工作信息合法性时发生异常！" );
						break;
					}
				}
			}
		}
		//当所有的校验都通过后，再进行工作的部署
		if( check ){
			if( centerId == null ){
				check = false;
				logger.error( "center id in works all null, can not deploy works." );
				result.error( new Exception( "所有工作的中心工作ID都是空，无法继续部署工作！" ) );
				result.setUserMessage( "所有工作的中心工作ID都是空，无法继续部署工作！" );
			}else{
				try {
					//logger.debug( ">>>>>>>>>>>>>>>>>>>Action["+ new Date() +"]开始进行工作部署......" );
					okrWorkBaseInfoService.deploy( centerId, ids, okrUserCache.getLoginIdentityName()  );
					for( OkrWorkBaseInfo _okrWorkBaseInfo : okrWorkBaseInfoList ){
						okrWorkDynamicsService.workDynamic(
							_okrWorkBaseInfo.getCenterId(), 
							_okrWorkBaseInfo.getId(),
							_okrWorkBaseInfo.getTitle(),
							"部署工作", 
							currentPerson.getName(), 
							okrUserCache.getLoginUserName(), 
							okrUserCache.getLoginIdentityName() , 
							"部署工作：" + _okrWorkBaseInfo.getTitle(), 
							"部署工作成功！"
						);
					}
					result.setUserMessage( "工作部署成功！" );
				} catch( Exception e ){
					check = false;
					logger.error( "system deploy works got an exception." );
					result.error( e );
					result.setUserMessage( "工作部署发生异常！" );
				}
			}
		}
		if( check ){
			try {
				okrWorkBaseInfoService.createTasks( centerId, ids, okrUserCache.getLoginIdentityName()  );
				result.setUserMessage( "工作部署成功！" );
			}catch( Exception e ){
				logger.error( "system createTasks got an exception.", e );
				result.error( e );
				result.setUserMessage( "工作部署生成待办信息时发生异常！" );
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "列示根据过滤条件查询的OkrWorkBaseInfo[已归档],下一页.", response = WrapOutOkrCenterWorkInfo.class, request = com.x.okr.assemble.control.jaxrs.okrworkperson.WrapInFilter.class)
	@PUT
	@Path( "archive/list/{id}/next/{count}" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listMyArchiveNextWithFilter( @Context HttpServletRequest request, @PathParam( "id" ) String id, @PathParam( "count" ) Integer count, com.x.okr.assemble.control.jaxrs.okrworkperson.WrapInFilter wrapIn ) {
		ActionResult<List<WrapOutOkrWorkBaseInfo>> result = new ActionResult<>();
		List<WrapOutOkrWorkBaseInfo> wraps = null;
		List<OkrWorkBaseInfo> okrWorkBaseInfoList = null;
		Long total = 0L;
		Boolean check = true;
		OkrUserCache  okrUserCache  = null;
		EffectivePerson currentPerson = this.effectivePerson( request );	
		if( check ){
			try {
				okrUserCache = okrUserInfoService.getOkrUserCacheWithPersonName( currentPerson.getName() );
			} catch (Exception e1) {
				check = false;
				result.error( new Exception( "系统获取用户登录记录信息发生异常!" ) );
				result.setUserMessage( "系统获取用户登录记录信息发生异常!" );
				logger.error( "system get login indentity with person name got an exception", e1 );
			}	
		}		
		if( check && ( okrUserCache == null || okrUserCache.getLoginIdentityName() == null ) ){
			check = false;
			logger.error( "system query user identity got an exception.user:" + currentPerson.getName());
			result.error( new Exception( "系统未获取到用户登录身份(登录用户名)，请重新打开应用!" ) );
			result.setUserMessage( "系统未获取到用户登录身份(登录用户名)，请重新打开应用!" );
		}
		if( wrapIn == null ){
			wrapIn = new com.x.okr.assemble.control.jaxrs.okrworkperson.WrapInFilter();
		}
		if( check ){
			try{
				//信息状态要是正常的，已删除的数据不需要查询出来
				wrapIn.setProcessIdentities( null );
				wrapIn.setEmployeeNames( null );
				wrapIn.setEmployeeIdentities(null);
				wrapIn.setCompanyNames( null );
				wrapIn.setOrganizationNames( null );
				wrapIn.setInfoStatuses( null );
				wrapIn.addQueryEmployeeIdentities( okrUserCache.getLoginIdentityName() );
				
				wrapIn.addQueryInfoStatus( "已归档" );
				wrapIn.addQueryProcessIdentity( "观察者" );
				okrWorkBaseInfoList = okrWorkBaseInfoService.listWorkNextWithFilter( id, count, wrapIn );
				//从数据库中查询符合条件的对象总数
				total = okrWorkBaseInfoService.getWorkCountWithFilter( wrapIn );
				wraps = wrapout_copier.copy( okrWorkBaseInfoList );
				result.setCount( total );
				result.setData( wraps );
			}catch(Throwable th){
				logger.error( "system filter okrWorkBaseInfo got an exception." );
				th.printStackTrace();
				result.error(th);
			}
		}
		
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "列示根据过滤条件查询的OkrCenterWorkInfo[已归档],上一页.", response = WrapOutOkrCenterWorkInfo.class, request = com.x.okr.assemble.control.jaxrs.okrworkperson.WrapInFilter.class)
	@PUT
	@Path( "archive/list/{id}/prev/{count}" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listMyArchivePrevWithFilter(@Context HttpServletRequest request, @PathParam( "id" ) String id, @PathParam( "count" ) Integer count, com.x.okr.assemble.control.jaxrs.okrworkperson.WrapInFilter wrapIn) {
		ActionResult<List<WrapOutOkrWorkBaseInfo>> result = new ActionResult<>();
		List<WrapOutOkrWorkBaseInfo> wraps = null;
		List<OkrWorkBaseInfo> okrWorkBaseInfoList = null;
		Long total = 0L;
		Boolean check = true;
		OkrUserCache  okrUserCache  = null;
		EffectivePerson currentPerson = this.effectivePerson( request );	
		if( check ){
			try {
				okrUserCache = okrUserInfoService.getOkrUserCacheWithPersonName( currentPerson.getName() );
			} catch (Exception e1) {
				check = false;
				result.error( new Exception( "系统获取用户登录记录信息发生异常!" ) );
				result.setUserMessage( "系统获取用户登录记录信息发生异常!" );
				logger.error( "system get login indentity with person name got an exception", e1 );
			}	
		}		
		if( check && ( okrUserCache == null || okrUserCache.getLoginIdentityName() == null ) ){
			check = false;
			logger.error( "system query user identity got an exception.user:" + currentPerson.getName());
			result.error( new Exception( "系统未获取到用户登录身份(登录用户名)，请重新打开应用!" ) );
			result.setUserMessage( "系统未获取到用户登录身份(登录用户名)，请重新打开应用!" );
		}
		if( wrapIn == null ){
			wrapIn = new com.x.okr.assemble.control.jaxrs.okrworkperson.WrapInFilter();
		}
		if( check ){
			try{
				//信息状态要是正常的，已删除的数据不需要查询出来
				wrapIn.setProcessIdentities( null );
				wrapIn.setEmployeeNames( null );
				wrapIn.setEmployeeIdentities(null);
				wrapIn.setCompanyNames( null );
				wrapIn.setOrganizationNames( null );
				wrapIn.setInfoStatuses( null );
				wrapIn.addQueryEmployeeIdentities( okrUserCache.getLoginIdentityName() );
				
				wrapIn.addQueryInfoStatus( "已归档" );
				wrapIn.addQueryProcessIdentity( "观察者" );
				okrWorkBaseInfoList = okrWorkBaseInfoService.listWorkPrevWithFilter( id, count, wrapIn );			
				//从数据库中查询符合条件的对象总数
				total = okrWorkBaseInfoService.getWorkCountWithFilter( wrapIn );
				wraps = wrapout_copier.copy( okrWorkBaseInfoList );
				result.setCount( total );
				result.setData( wraps );
			}catch(Throwable th){
				logger.error( "system filter okrWorkBaseInfo got an exception." );
				th.printStackTrace();
				result.error(th);
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "收回已经部署的工作事项.", response = WrapOutOkrWorkBaseInfo.class)
	@GET
	@Path( "recycle/{id}" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response recycle( @Context HttpServletRequest request, @PathParam( "id" ) String id ) {
		ActionResult<WrapOutOkrWorkBaseInfo> result = new ActionResult<>();
		if( id == null || id.isEmpty() ){
			logger.error( "id is null, system can not recycle any object." );
		}
		try{
			okrWorkBaseInfoService.recycleWork( id );
			result.setUserMessage( "成功收回工作信息数据信息。id=" + id );
		}catch(Exception e){
			logger.error( "system recycle okrWorkBaseInfoService get an exception, {'id':'"+id+"'}", e );
			result.setUserMessage( "删除工作信息数据过程中发生异常。" );
			result.error( e );
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "根据中心工作ID获取我部署的所有OkrWorkBaseInfo对象，并且以上级工作进行归类.", response = WrapOutOkrWorkBaseInfo.class)
	@GET
	@Path( "deploy/form/center/{id}" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listMyDeployWorkInCenterForForm(@Context HttpServletRequest request, @PathParam( "id" ) String id) {
		ActionResult<List<WrapOutOkrWorkBaseInfo>> result = new ActionResult<>();
		List<WrapOutOkrWorkBaseInfo> wraps = null;
		List<WrapOutOkrWorkBaseInfo> result_wraps = new ArrayList<WrapOutOkrWorkBaseInfo>();
		List<OkrWorkBaseInfo> okrWorkBaseInfoList = null;
		List<OkrWorkBaseInfo> deployOkrWorkBaseInfoList = new ArrayList<OkrWorkBaseInfo>();
		//存储装配中的信息
		Map<String, WrapOutOkrWorkBaseInfo> resultWorkMap = new HashMap<String, WrapOutOkrWorkBaseInfo>();
		WrapOutOkrWorkBaseInfo wrapOutOkrWorkBaseInfo = null;
		OkrWorkBaseInfo parentOkrWorkBaseInfo = null;
		WrapOutOkrWorkBaseInfo wrapOutParentWorkInfo = null;
		Integer total = 0;
		Set<String> keySet = null;
		Iterator<String> iterator = null;
		List<String> query_statuses = new ArrayList<String>();
		String loginIdentity = null; //当前用户登录身份名称	
		Boolean check = true;
		OkrUserCache  okrUserCache  = null;
		EffectivePerson currentPerson = this.effectivePerson( request );	
		if( check ){
			try {
				okrUserCache = okrUserInfoService.getOkrUserCacheWithPersonName( currentPerson.getName() );
			} catch (Exception e1) {
				check = false;
				result.error( new Exception( "系统获取用户登录记录信息发生异常!" ) );
				result.setUserMessage( "系统获取用户登录记录信息发生异常!" );
				logger.error( "system get login indentity with person name got an exception", e1 );
			}	
		}		
		if( check && ( okrUserCache == null || okrUserCache.getLoginIdentityName() == null ) ){
			check = false;
			logger.error( "system query user identity got an exception.user:" + currentPerson.getName());
			result.error( new Exception( "系统未获取到用户登录身份(登录用户名)，请重新打开应用!" ) );
			result.setUserMessage( "系统未获取到用户登录身份(登录用户名)，请重新打开应用!" );
		}
		
		if( check ){
			try{		
				if( okrUserCache.getLoginIdentityName()  == null ){
					throw new Exception( "系统获取登录用户身份发生异常，请重新打开应用！" );
				}			
				loginIdentity = okrUserCache.getLoginIdentityName() ;			
				//query_statuses.add( "正常" );
				
				//获取所有当前用户身份部署的工作信息
				okrWorkBaseInfoList = okrWorkBaseInfoService.listWorkInCenterByIdentity( loginIdentity, id, query_statuses );			
				//然后遍历所有的可查看的工作，将我部署的工作放在一起:deployOkrWorkBaseInfoList
				if( okrWorkBaseInfoList != null ){
					for( OkrWorkBaseInfo okrWorkBaseInfo : okrWorkBaseInfoList ){
						if( okrWorkBaseInfo.getDeployerIdentity().equals( loginIdentity )){
							deployOkrWorkBaseInfoList.add( okrWorkBaseInfo );
						}
					}
					wraps = wrapout_copier.copy( deployOkrWorkBaseInfoList );
					total = deployOkrWorkBaseInfoList.size();
				}
				if( wraps != null ){
					//组织所有工作的上下级关系
					for( WrapOutOkrWorkBaseInfo info : wraps ){
						//workMap.put( info.getId(), info );
						//查询工作的上级工作放到resultWorkMap里
						if( info != null && info.getParentWorkId() !=null && !info.getParentWorkId().isEmpty()){
							//先从resultWorkMap里查询上级工作对象
							wrapOutParentWorkInfo = resultWorkMap.get( info.getParentWorkId() );
							if( wrapOutParentWorkInfo != null && "PARENTWORK".equals(wrapOutParentWorkInfo.getWorkOutType())){
								//map里存在上级工作信息
								wrapOutParentWorkInfo.addNewSubWorkBaseInfo(info);
							}else{
								//map里没有上级工作信息
								parentOkrWorkBaseInfo = okrWorkBaseInfoService.get( info.getParentWorkId());
								if( parentOkrWorkBaseInfo != null ){
									wrapOutParentWorkInfo = wrapout_copier.copy( parentOkrWorkBaseInfo );
									wrapOutParentWorkInfo.setWorkOutType( "PARENTWORK" );
									wrapOutParentWorkInfo.addNewSubWorkBaseInfo( info );
									resultWorkMap.put( wrapOutParentWorkInfo.getId(), wrapOutParentWorkInfo );
								}else{
									//上级工作不存在
									resultWorkMap.put( info.getId(), info );
								}
							}
						}else{
							//没有上级工作
							resultWorkMap.put( info.getId(), info );
						}
					}				
					keySet = resultWorkMap.keySet();
					iterator = keySet.iterator();
					while( iterator.hasNext() ){
						wrapOutOkrWorkBaseInfo = resultWorkMap.get( iterator.next() );
						if( wrapOutOkrWorkBaseInfo == null ){
							continue;
						}
						result_wraps.add( wrapOutOkrWorkBaseInfo );
					}
				}
				result.setCount( Long.valueOf( total + "" ) );
				if( result_wraps != null && !result_wraps.isEmpty() ){
					SortTools.asc( result_wraps, "completeDateLimit" );
				}
				result.setData( result_wraps );
			}catch(Throwable th){
				logger.error( "system filter okrWorkBaseInfo got an exception." );
				th.printStackTrace();
				result.error(th);
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "根据中心工作ID获取我需要参与[负责，协助，阅知]的所有OkrWorkBaseInfo对象，并且以上级工作进行归类.", response = WrapOutOkrWorkBaseInfo.class)
	@GET
	@Path( "process/form/center/{id}" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listMyProcessWorkInCenterForForm(@Context HttpServletRequest request, @PathParam( "id" ) String id) {
		ActionResult<List<WrapOutOkrWorkBaseInfo>> result = new ActionResult<>();
		List<WrapOutOkrWorkBaseInfo> result_wraps = new ArrayList<WrapOutOkrWorkBaseInfo>();
		List<OkrWorkBaseInfo> okrWorkBaseInfoList = null;
		WrapOutOkrWorkBaseInfo wrapOutOkrWorkBaseInfo = null;
		OkrWorkAuthorizeRecord okrWorkAuthorizeRecord = null;
		Integer total = 0;
		List<String> query_statuses = new ArrayList<String>();
		String loginIdentity = null; //当前用户登录身份名称
		Boolean check = true;
		OkrUserCache  okrUserCache  = null;
		EffectivePerson currentPerson = this.effectivePerson( request );	
		if( check ){
			try {
				okrUserCache = okrUserInfoService.getOkrUserCacheWithPersonName( currentPerson.getName() );
			} catch (Exception e1) {
				check = false;
				result.error( new Exception( "系统获取用户登录记录信息发生异常!" ) );
				result.setUserMessage( "系统获取用户登录记录信息发生异常!" );
				logger.error( "system get login indentity with person name got an exception", e1 );
			}	
		}		
		if( check && ( okrUserCache == null || okrUserCache.getLoginIdentityName() == null ) ){
			check = false;
			logger.error( "system query user identity got an exception.user:" + currentPerson.getName());
			result.error( new Exception( "系统未获取到用户登录身份(登录用户名)，请重新打开应用!" ) );
			result.setUserMessage( "系统未获取到用户登录身份(登录用户名)，请重新打开应用!" );
		}

		if( check ){
			try{
				if( okrUserCache.getLoginIdentityName()  == null ){
					throw new Exception( "系统获取登录用户身份发生异常，请重新打开应用！" );
				}			
				loginIdentity = okrUserCache.getLoginIdentityName() ;			
				//query_statuses.add( "正常" );			
				//获取所有当前用户身份部署的工作信息
				okrWorkBaseInfoList = okrWorkBaseInfoService.listWorkInCenterByIdentity( loginIdentity, id, query_statuses );		
				//logger.debug( "然后遍历所有的可查看的工作，将我部署的工作放在一起:deployOkrWorkBaseInfoList" );
				if( okrWorkBaseInfoList != null ){
					//logger.debug( "okrWorkBaseInfoList.size():" + okrWorkBaseInfoList.size());
					for( OkrWorkBaseInfo okrWorkBaseInfo : okrWorkBaseInfoList ){
						if( okrWorkBaseInfo.getDeployerIdentity() != null && !okrWorkBaseInfo.getDeployerIdentity().isEmpty() ){
							//logger.debug( "okrWorkBaseInfo.getDeployerIdentity():" + okrWorkBaseInfo.getDeployerIdentity());
							if( okrWorkBaseInfo.getDeployerIdentity().equals( loginIdentity ) || okrWorkBaseInfo.getDeployerIdentity().indexOf( loginIdentity ) >= 0){
								continue;
							}
						}
						wrapOutOkrWorkBaseInfo = wrapout_copier.copy( okrWorkBaseInfo );
						//获取该工作和当前责任人相关的授权信息
						okrWorkAuthorizeRecord = okrWorkAuthorizeRecordService.getLastAuthorizeRecord( okrWorkBaseInfo.getId(), okrUserCache.getLoginIdentityName()  );
						if( okrWorkAuthorizeRecord != null ){
							wrapOutOkrWorkBaseInfo.setOkrWorkAuthorizeRecord( okrWorkAuthorizeRecord_wrapout_copier.copy( okrWorkAuthorizeRecord ) );
						}
						result_wraps.add( wrapOutOkrWorkBaseInfo );
					}
					total = result_wraps.size();
				}else{
					//logger.debug( "okrWorkBaseInfoList 为空！！！" );
				}
				result.setCount( Long.valueOf( total + "" ) );
				if( result_wraps != null && !result_wraps.isEmpty() ){
					SortTools.asc( result_wraps, "completeDateLimit" );
				}
				result.setData( result_wraps );
			}catch(Throwable th){
				logger.error( "system filter okrWorkBaseInfo got an exception." );
				th.printStackTrace();
				result.error(th);
			}
		}
		
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "统计登录者所有的工作数量.", response = WrapOutOkrWorkStatistic.class)
	@GET
	@Path( "statistic/my" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getMyStatistic( @Context HttpServletRequest request ) {
		ActionResult<WrapOutOkrWorkStatistic> result = new ActionResult<>();
		WrapOutOkrWorkStatistic wrap = new WrapOutOkrWorkStatistic();
		Long workTotal = 0L;
		Long processingWorkCount = 0L;
		Long completedWorkCount = 0L;
		Long overtimeWorkCount = 0L;
		Long draftWorkCount = 0L;
		Double percent = 0.0;
		String identity = null;
		List<String> status = new ArrayList<String>();
		status.add( "正常" );
		Boolean check = true;
		OkrUserCache  okrUserCache  = null;
		EffectivePerson currentPerson = this.effectivePerson( request );	
		if( check ){
			try {
				okrUserCache = okrUserInfoService.getOkrUserCacheWithPersonName( currentPerson.getName() );
			} catch (Exception e1) {
				check = false;
				result.error( new Exception( "系统获取用户登录记录信息发生异常!" ) );
				result.setUserMessage( "系统获取用户登录记录信息发生异常!" );
				logger.error( "system get login indentity with person name got an exception", e1 );
			}	
		}		
		if( check && ( okrUserCache == null || okrUserCache.getLoginIdentityName() == null ) ){
			check = false;
			logger.error( "system query user identity got an exception.user:" + currentPerson.getName());
			result.error( new Exception( "系统未获取到用户登录身份(登录用户名)，请重新打开应用!" ) );
			result.setUserMessage( "系统未获取到用户登录身份(登录用户名)，请重新打开应用!" );
		}
		if( check ){
			identity = okrUserCache.getLoginIdentityName() ;
		}
		//根据登录用户身份进行数据统计查询
		if( check ){
			try{
				workTotal = okrWorkPersonService.getWorkTotalByCenterId( identity, status, "责任者" );
				processingWorkCount = okrWorkPersonService.getProcessingWorkCountByCenterId( identity, status, "责任者" );
				completedWorkCount = okrWorkPersonService.getCompletedWorkCountByCenterId( identity, status, "责任者" );
				overtimeWorkCount = okrWorkPersonService.getOvertimeWorkCountByCenterId( identity, status, "责任者" );
				draftWorkCount = okrWorkPersonService.getDraftWorkCountByCenterId( identity, status, "责任者" );
				if( workTotal > 0 ){
					percent = ( (double)workTotal - (double)overtimeWorkCount ) / (double)workTotal;
				}
			}catch(Exception e){
				logger.error( "system count my okrWorkBaseInfo got an exception.", e );
				result.error( e );
			}
		}
		
		if( check ){
			wrap.setPercent( percent );
			wrap.setWorkTotal( workTotal );
			wrap.setProcessingWorkCount( processingWorkCount );
			wrap.setCompletedWorkCount( completedWorkCount );
			wrap.setOvertimeWorkCount( overtimeWorkCount );
			wrap.setDraftWorkCount( draftWorkCount );
			result.setData( wrap );
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	
}
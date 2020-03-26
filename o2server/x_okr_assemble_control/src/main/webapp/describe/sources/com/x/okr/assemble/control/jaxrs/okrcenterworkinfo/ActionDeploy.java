package com.x.okr.assemble.control.jaxrs.okrcenterworkinfo;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.okr.assemble.control.OkrUserCache;
import com.x.okr.assemble.control.dataadapter.webservice.sms.SmsMessageOperator;
import com.x.okr.assemble.control.jaxrs.okrcenterworkinfo.exception.ExceptionCenterWorkDeploy;
import com.x.okr.assemble.control.jaxrs.okrcenterworkinfo.exception.ExceptionCenterWorkIdEmpty;
import com.x.okr.assemble.control.jaxrs.okrcenterworkinfo.exception.ExceptionCenterWorkNotExists;
import com.x.okr.assemble.control.jaxrs.okrcenterworkinfo.exception.ExceptionCenterWorkQueryById;
import com.x.okr.assemble.control.jaxrs.okrcenterworkinfo.exception.ExceptionDeployableWorkQuery;
import com.x.okr.assemble.control.jaxrs.okrcenterworkinfo.exception.ExceptionGetOkrUserCache;
import com.x.okr.assemble.control.jaxrs.okrcenterworkinfo.exception.ExceptionUserNoLogin;
import com.x.okr.assemble.control.jaxrs.okrcenterworkinfo.exception.ExceptionWorkDeploy;
import com.x.okr.assemble.control.jaxrs.okrcenterworkinfo.exception.ExceptionWorkTaskCreate;
import com.x.okr.assemble.control.jaxrs.okrcenterworkinfo.exception.ExceptionWorkTaskRemove;
import com.x.okr.assemble.control.jaxrs.queue.WrapInWorkDynamic;
import com.x.okr.assemble.control.service.OkrCenterWorkOperationService;
import com.x.okr.assemble.control.service.OkrSendNotifyService;
import com.x.okr.assemble.control.service.OkrTaskService;
import com.x.okr.assemble.control.service.OkrWorkBaseInfoDeployService;
import com.x.okr.assemble.control.service.OkrWorkBaseInfoOperationService;
import com.x.okr.assemble.control.service.OkrWorkBaseInfoQueryService;
import com.x.okr.assemble.control.service.OkrWorkPersonService;
import com.x.okr.entity.OkrCenterWorkInfo;
import com.x.okr.entity.OkrWorkBaseInfo;

public class ActionDeploy extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger( ActionDeploy.class );
	
	private OkrWorkBaseInfoQueryService okrWorkBaseInfoQueryService = new OkrWorkBaseInfoQueryService();
	private OkrWorkPersonService okrWorkPersonService = new OkrWorkPersonService();
	private OkrWorkBaseInfoDeployService okrWorkBaseInfoDeployService = new OkrWorkBaseInfoDeployService();
	private OkrCenterWorkOperationService okrCenterWorkOperationService = new OkrCenterWorkOperationService();
	private OkrWorkBaseInfoOperationService okrWorkBaseInfoOperationService = new OkrWorkBaseInfoOperationService();
	private OkrTaskService okrTaskService = new OkrTaskService();
	
	protected ActionResult<Wo> execute( HttpServletRequest request, EffectivePerson effectivePerson, String id ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		OkrCenterWorkInfo okrCenterWorkInfo = null;
		List<OkrWorkBaseInfo> okrWorkBaseInfoList = null;
		List<OkrWorkBaseInfo> deployAbleWorkBaseInfoList = new ArrayList<>();
		List<String> deployWorkIds = null;
		List<String> deployAbleWorkIds = new ArrayList<>();
		Boolean check = true;
		OkrUserCache  okrUserCache  = null;	
		
		if( id == null || id.isEmpty()  ){
			check = false;
			Exception exception = new ExceptionCenterWorkIdEmpty();
			result.error( exception );
		}
		
		if( check ){
			try {
				okrUserCache = okrUserInfoService.getOkrUserCacheWithPersonName( effectivePerson.getDistinguishedName() );
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionGetOkrUserCache( e, effectivePerson.getDistinguishedName() );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}	
		}
		
		if( check && ( okrUserCache == null || okrUserCache.getLoginIdentityName() == null ) ){
			check = false;
			Exception exception = new ExceptionUserNoLogin( effectivePerson.getDistinguishedName() );
			result.error( exception );
		}
		if( check ){
			try{
				okrCenterWorkInfo = okrCenterWorkQueryService.get( id );
				if( okrCenterWorkInfo == null ){
					check = false;
					Exception exception = new ExceptionCenterWorkNotExists( id );
					result.error( exception );
				}
			}catch( Exception e){
				check = false;
				Exception exception = new ExceptionCenterWorkQueryById( e, id );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		
		if( check ){
			//查询该中心工作下, 用户所有部署的,处理状态为草稿的工作ID列表
			try {
				deployWorkIds = okrWorkPersonService.listDistinctWorkIdsByWorkAndIdentity( okrCenterWorkInfo.getId(), null, okrUserCache.getLoginIdentityName(), "部署者", null );
				if( deployWorkIds != null && !deployWorkIds.isEmpty() ){
					okrWorkBaseInfoList = okrWorkBaseInfoQueryService.listByIds( deployWorkIds );
					if( okrWorkBaseInfoList != null && !okrWorkBaseInfoList.isEmpty() ){
						for( OkrWorkBaseInfo work : okrWorkBaseInfoList ){
							if( work != null && "草稿".equals( work.getWorkProcessStatus() )){
								deployAbleWorkBaseInfoList.add( work );
								deployAbleWorkIds.add( work.getId() );
							}
						}
					}
				}
			} catch (Exception e) {
				Exception exception = new ExceptionDeployableWorkQuery( e, okrCenterWorkInfo.getId(), okrUserCache.getLoginIdentityName() );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		
		if( check ){
			try {
				okrWorkBaseInfoDeployService.deploy( deployAbleWorkIds, okrUserCache.getLoginIdentityName()  );
				for( OkrWorkBaseInfo _okrWorkBaseInfo : deployAbleWorkBaseInfoList ){
					//工作的责任者接收短信
					SmsMessageOperator.sendWithPersonName( _okrWorkBaseInfo.getResponsibilityEmployeeName(), "您有工作'"+_okrWorkBaseInfo.getTitle()+"'，请及时办理！");
					
					//工作协助者接收短信
					if( ListTools.isNotEmpty( _okrWorkBaseInfo.getCooperateEmployeeNameList()) ) {
						for( String name : _okrWorkBaseInfo.getCooperateEmployeeNameList() ) {
							SmsMessageOperator.sendWithPersonName( name, "您有工作'"+_okrWorkBaseInfo.getTitle()+"'，请协助办理！");
						}
					}
					
					WrapInWorkDynamic.sendWithWorkInfo( 
							_okrWorkBaseInfo, 
							effectivePerson.getDistinguishedName(),
							okrUserCache.getLoginUserName(),
							okrUserCache.getLoginUserName(),
							"部署具体工作",
							"具体工作部署成功！"
					);
				}
			} catch( Exception e ){
				check = false;
				Exception exception = new ExceptionWorkDeploy( e );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		
		if( check ){
			try{
				if( okrCenterWorkInfo != null ){
					okrCenterWorkOperationService.deploy( id );

					Wo wo = new Wo();
					wo.setId( id );
					result.setData( wo );
					
					WrapInWorkDynamic.sendWithCenterWorkInfo( 
							okrCenterWorkInfo, 
							effectivePerson.getDistinguishedName(),
							okrUserCache.getLoginUserName(),
							okrUserCache.getLoginUserName(),
							"部署中心工作",
							"中心工作部署成功！"
					);
					
					//中心工作部署成功，通知部署者
					new OkrSendNotifyService().notifyDeployerForCenterWorkDeploySuccess( okrCenterWorkInfo );
				}else{
					check = false;
					Exception exception = new ExceptionCenterWorkNotExists( id );
					result.error( exception );
				}
			}catch( Exception e){
				check = false;
				Exception exception = new ExceptionCenterWorkDeploy( e, id );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		
		if( check ){
			if( deployWorkIds != null && !deployWorkIds.isEmpty() ){
				try {
					okrWorkBaseInfoOperationService.createTasks( deployWorkIds, okrUserCache.getLoginIdentityName() );
				}catch( Exception e ){
					Exception exception = new ExceptionWorkTaskCreate( e );
					result.error( exception );
					logger.error( e, effectivePerson, request, null);
				}
			}else{
				try {
					okrTaskService.deleteTask( okrCenterWorkInfo, okrUserCache.getLoginIdentityName() );
				}catch( Exception e ){
					Exception exception = new ExceptionWorkTaskRemove( e );
					result.error( exception );
					logger.error( e, effectivePerson, request, null);
				}
			}
		}
		return result;
	}
	
	public static class Wo extends WoId {

	}
}
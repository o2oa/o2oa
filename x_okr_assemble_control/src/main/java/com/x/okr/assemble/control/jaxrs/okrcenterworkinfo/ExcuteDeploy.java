package com.x.okr.assemble.control.jaxrs.okrcenterworkinfo;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.WrapOutId;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.okr.assemble.control.OkrUserCache;
import com.x.okr.assemble.control.service.OkrCenterWorkOperationService;
import com.x.okr.assemble.control.service.OkrSendNotifyService;
import com.x.okr.assemble.control.service.OkrTaskService;
import com.x.okr.assemble.control.service.OkrWorkBaseInfoDeployService;
import com.x.okr.assemble.control.service.OkrWorkBaseInfoOperationService;
import com.x.okr.assemble.control.service.OkrWorkBaseInfoQueryService;
import com.x.okr.assemble.control.service.OkrWorkPersonService;
import com.x.okr.entity.OkrCenterWorkInfo;
import com.x.okr.entity.OkrWorkBaseInfo;

public class ExcuteDeploy extends ExcuteBase {

	private Logger logger = LoggerFactory.getLogger( ExcuteDeploy.class );
	private OkrWorkBaseInfoQueryService okrWorkBaseInfoQueryService = new OkrWorkBaseInfoQueryService();
	private OkrWorkPersonService okrWorkPersonService = new OkrWorkPersonService();
	private OkrWorkBaseInfoDeployService okrWorkBaseInfoDeployService = new OkrWorkBaseInfoDeployService();
	private OkrCenterWorkOperationService okrCenterWorkOperationService = new OkrCenterWorkOperationService();
	private OkrWorkBaseInfoOperationService okrWorkBaseInfoOperationService = new OkrWorkBaseInfoOperationService();
	private OkrTaskService okrTaskService = new OkrTaskService();
	
	protected ActionResult<WrapOutId> execute( HttpServletRequest request, EffectivePerson effectivePerson, String id ) throws Exception {
		ActionResult<WrapOutId> result = new ActionResult<>();
		OkrCenterWorkInfo okrCenterWorkInfo = null;
		List<OkrWorkBaseInfo> okrWorkBaseInfoList = null;
		List<OkrWorkBaseInfo> deployAbleWorkBaseInfoList = new ArrayList<>();
		List<String> deployWorkIds = null;
		List<String> deployAbleWorkIds = new ArrayList<>();
		Boolean check = true;
		OkrUserCache  okrUserCache  = null;	
		
		if( id == null || id.isEmpty()  ){
			check = false;
			Exception exception = new CenterWorkIdEmptyException();
			result.error( exception );
			logger.error( exception, effectivePerson, request, null);
		}
		
		if( check ){
			try {
				okrUserCache = okrUserInfoService.getOkrUserCacheWithPersonName( effectivePerson.getName() );
			} catch (Exception e) {
				check = false;
				Exception exception = new GetOkrUserCacheException( e, effectivePerson.getName() );
				result.error( exception );
				logger.error( exception, effectivePerson, request, null);
			}	
		}
		
		if( check && ( okrUserCache == null || okrUserCache.getLoginIdentityName() == null ) ){
			check = false;
			Exception exception = new UserNoLoginException( effectivePerson.getName() );
			result.error( exception );
			logger.error( exception, effectivePerson, request, null);
		}
		if( check ){
			try{
				okrCenterWorkInfo = okrCenterWorkQueryService.get( id );
				if( okrCenterWorkInfo == null ){
					check = false;
					Exception exception = new CenterWorkNotExistsException( id );
					result.error( exception );
					logger.error( exception, effectivePerson, request, null);
				}
			}catch( Exception e){
				check = false;
				Exception exception = new CenterWorkQueryByIdException( e, id );
				result.error( exception );
				logger.error( exception, effectivePerson, request, null);
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
				Exception exception = new DeployableWorkQueryException( e, okrCenterWorkInfo.getId(), okrUserCache.getLoginIdentityName() );
				result.error( exception );
				logger.error( exception, effectivePerson, request, null);
			}
		}
		
		if( check ){
			try {
				okrWorkBaseInfoDeployService.deploy( deployAbleWorkIds, okrUserCache.getLoginIdentityName()  );
				for( OkrWorkBaseInfo _okrWorkBaseInfo : deployAbleWorkBaseInfoList ){
					okrWorkDynamicsService.workDynamic(
						_okrWorkBaseInfo.getCenterId(), 
						_okrWorkBaseInfo.getId(),
						_okrWorkBaseInfo.getTitle(),
						"部署工作", 
						effectivePerson.getName(), 
						okrUserCache.getLoginUserName(), 
						okrUserCache.getLoginIdentityName() , 
						"部署工作：" + _okrWorkBaseInfo.getTitle(), 
						"部署工作成功！"
					);
				}
			} catch( Exception e ){
				check = false;
				Exception exception = new WorkDeployException( e );
				result.error( exception );
				logger.error( exception, effectivePerson, request, null);
			}
		}
		
		if( check ){
			try{
				if( okrCenterWorkInfo != null ){
					okrCenterWorkOperationService.deploy( id );
					WrapOutId wrapOutId = new WrapOutId(id);
					result.setData( wrapOutId );
					//中心工作部署成功，通知部署者
					new OkrSendNotifyService().notifyDeployerForCenterWorkDeploySuccess( okrCenterWorkInfo );
				}else{
					check = false;
					Exception exception = new CenterWorkNotExistsException( id );
					result.error( exception );
					logger.error( exception, effectivePerson, request, null);
				}
			}catch( Exception e){
				check = false;
				Exception exception = new CenterWorkDeployException( e, id );
				result.error( exception );
				logger.error( exception, effectivePerson, request, null);
			}
		}
		
		if( check ){
			if( deployWorkIds != null && !deployWorkIds.isEmpty() ){
				try {
					okrWorkBaseInfoOperationService.createTasks( deployWorkIds, okrUserCache.getLoginIdentityName() );
				}catch( Exception e ){
					Exception exception = new WorkTaskCreateException( e );
					result.error( exception );
					logger.error( exception, effectivePerson, request, null);
				}
			}else{
				try {
					okrTaskService.deleteTask( okrCenterWorkInfo, okrUserCache.getLoginIdentityName() );
				}catch( Exception e ){
					Exception exception = new WorkTaskRemoveException( e );
					result.error( exception );
					logger.error( exception, effectivePerson, request, null);
				}
			}
		}
		return result;
	}
	
}
package com.x.okr.assemble.control.jaxrs.okrworkbaseinfo;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.okr.assemble.control.OkrUserCache;
import com.x.okr.assemble.control.service.OkrWorkBaseInfoDeployService;
import com.x.okr.assemble.control.service.OkrWorkBaseInfoOperationService;
import com.x.okr.entity.OkrCenterWorkInfo;
import com.x.okr.entity.OkrWorkBaseInfo;

public class ExcuteDeploy extends ExcuteBase {

	private Logger logger = LoggerFactory.getLogger( ExcuteDeploy.class );
	private OkrWorkBaseInfoOperationService okrWorkBaseInfoOperationService = new OkrWorkBaseInfoOperationService();
	private OkrWorkBaseInfoDeployService okrWorkBaseInfoDeployService = new OkrWorkBaseInfoDeployService();
	
	/**
	 * 传入工作ID, 进行工作的部署
	 * @param effectivePerson
	 * @param wrapIn
	 * @return
	 * @throws Exception
	 */
	protected ActionResult<WrapOutOkrWorkBaseInfo> execute( HttpServletRequest request,EffectivePerson effectivePerson, WrapInOkrWorkBaseInfo wrapIn ) throws Exception {
		ActionResult<WrapOutOkrWorkBaseInfo> result = new ActionResult<>();
		//前端传递所有需要部署的工作ID
		List<String> workIds = null;
		List<OkrWorkBaseInfo> okrWorkBaseInfoList = new ArrayList<OkrWorkBaseInfo>();
		OkrWorkBaseInfo okrWorkBaseInfo = null;
		OkrCenterWorkInfo okrCenterWorkInfo  = null;
		String centerId = null;
		Boolean check = true;
		OkrUserCache  okrUserCache  = null;
		
		if( check ){
			workIds = wrapIn.getWorkIds();
			if( workIds == null || workIds.isEmpty() ){
				check = false;
				Exception exception = new WorkIdEmptyException();
				result.error( exception );
				logger.error( exception, effectivePerson, request, null);
			}
		}
		
		if( check ){
			try {
				okrUserCache = okrUserInfoService.getOkrUserCacheWithPersonName( effectivePerson.getName() );
			} catch ( Exception e ) {
				check = false;
				Exception exception = new GetOkrUserCacheException( e, effectivePerson.getName()  );
				result.error( exception );
				logger.error( exception, effectivePerson, request, null);
			}	
		}		
		if( check && ( okrUserCache == null || okrUserCache.getLoginIdentityName() == null ) ){
			check = false;
			Exception exception = new UserNoLoginException( effectivePerson.getName()  );
			result.error( exception );
			logger.error( exception, effectivePerson, request, null);
		}
		//对wrapIn里的信息进行校验
		if( check && okrUserCache.getLoginUserName() == null ){
			check = false;
			Exception exception = new UserNoLoginException( effectivePerson.getName()  );
			result.error( exception );
			logger.error( exception, effectivePerson, request, null);
		}
		
		if( check ){
			for ( String id : workIds ) {
				logger.debug( "system checking work, id:" + id );
				try {
					okrWorkBaseInfo = okrWorkBaseInfoService.get( id );
					// 判断工作信息是否全都存在
					if ( okrWorkBaseInfo == null ) {
						check = false;
						Exception exception = new WorkNotExistsException( id  );
						result.error( exception );
						logger.error( exception, effectivePerson, request, null);
						break;
					}
					okrWorkBaseInfoList.add( okrWorkBaseInfo );
					centerId = okrWorkBaseInfo.getCenterId();
					// 判断中心工作信息是否存在
					if (centerId != null) {
						okrCenterWorkInfo = okrCenterWorkInfoService.get( centerId );
						if ( okrCenterWorkInfo == null ) {
							check = false;
							Exception exception = new CenterWorkNotExistsException( centerId  );
							result.error( exception );
							logger.error( exception, effectivePerson, request, null);
							break;
						}
					}
				} catch ( Exception e ) {
					check = false;
					Exception exception = new WorkInfoCheckException( e, id  );
					result.error( exception );
					logger.error( exception, effectivePerson, request, null);
					break;
				}
			}
		}
		//当所有的校验都通过后，再进行工作的部署
		if( check ){
			logger.debug( "system try to deploying work......" );
			try {				
				okrWorkBaseInfoDeployService.deploy( workIds, okrUserCache.getLoginIdentityName()  );
			} catch( Exception e ){
				check = false;
				Exception exception = new WorkDeployException( e );
				result.error( exception );
				logger.error( exception, effectivePerson, request, null);
			}
		}
		if( check ){
			for( OkrWorkBaseInfo _okrWorkBaseInfo : okrWorkBaseInfoList ){
				try {
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
				} catch( Exception e ){
					logger.warn( "system save work dynamic got an exception." );
					logger.error( e );
				}
			}
		}
		if( check ){
			try {
				okrWorkBaseInfoOperationService.createTasks( workIds, okrUserCache.getLoginIdentityName()  );
			}catch( Exception e ){
				logger.warn( "system createTasks got an exception." );
				logger.error( e );
			}
		}
		return result;
	}
	
}
package com.x.okr.assemble.control.jaxrs.okrauthorize;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.WrapOutId;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.okr.assemble.control.OkrUserCache;
import com.x.okr.assemble.control.service.OkrWorkAuthorizeService;
import com.x.okr.entity.OkrWorkBaseInfo;

public class ExcuteWorkTackback extends ExcuteBase {

	private Logger logger = LoggerFactory.getLogger( ExcuteWorkTackback.class );
	
	protected ActionResult<WrapOutId> execute( HttpServletRequest request, EffectivePerson effectivePerson, WrapInFilterWorkAuthorize wrapIn) throws Exception {
		ActionResult<WrapOutId> result = new ActionResult<WrapOutId>();
		OkrWorkBaseInfo okrWorkBaseInfo = null;
		Boolean check = true;		
		OkrUserCache  okrUserCache  = null;
		
//		if( wrapIn == null ){
//			check = false;
//			result.error( new Exception("请求传入的参数为空，无法进行查询。") );
//			result.setUserMessage( "请求传入的参数为空，无法进行查询." );
//			logger.error( "wrapIn is null." );
//		}
		if( check ){
			try {
				okrUserCache = okrUserInfoService.getOkrUserCacheWithPersonName( effectivePerson.getName() );
			} catch ( Exception e ) {
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
			if( okrUserCache.getLoginUserName() == null ){
				check = false;
				Exception exception = new UserNoLoginException( effectivePerson.getName() );
				result.error( exception );
				logger.error( exception, effectivePerson, request, null);
			}
			wrapIn.setAuthorizeIdentity( okrUserCache.getLoginIdentityName()  );
		}
		if( check ){
			if( wrapIn.getWorkId() == null || wrapIn.getWorkId().isEmpty() ){
				check = false;
				Exception exception = new AuthorizeWorkIdEmptyException();
				result.error( exception );
				logger.error( exception, effectivePerson, request, null);
			}
		}
		
		//2、工作信息是否已经存在
		if( check ){
			try {
				okrWorkBaseInfo = okrWorkBaseInfoService.get( wrapIn.getWorkId() );
				if( okrWorkBaseInfo == null ){
					check = false;
					Exception exception = new WorkNotExistsException( wrapIn.getWorkId() );
					result.error( exception );
					logger.error( exception, effectivePerson, request, null);
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new WorkQueryByIdException( e, wrapIn.getWorkId() );
				result.error( exception );
				logger.error( exception, effectivePerson, request, null);
			}
		}
		
		if( check ){
			OkrWorkAuthorizeService okrWorkAuthorizeService = new OkrWorkAuthorizeService();
			try {
				okrWorkAuthorizeService.tackback( okrWorkBaseInfo, wrapIn.getAuthorizeIdentity() );
				okrWorkDynamicsService.workDynamic(
						okrWorkBaseInfo.getCenterId(), 
						okrWorkBaseInfo.getId(),
						okrWorkBaseInfo.getTitle(),
						"授权收回", 
						effectivePerson.getName(), 
						okrUserCache.getLoginUserName(), 
						okrUserCache.getLoginIdentityName() , 
						"工作授权收回：" + okrWorkBaseInfo.getTitle(), 
						"工作授权收回成功"
				);
			} catch (Exception e) {
				check = false;
				Exception exception = new WorkTackbackProcessException( e, wrapIn.getWorkId() );
				result.error( exception );
				logger.error( exception, effectivePerson, request, null);
			}
		}
		
		return result;
	}
	
}
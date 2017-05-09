package com.x.okr.assemble.control.jaxrs.okrconfigsystem;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.cache.ApplicationCache;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.WrapOutId;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.okr.assemble.control.OkrUserCache;
import com.x.okr.assemble.control.jaxrs.okrconfigsystem.exception.GetOkrUserCacheException;
import com.x.okr.assemble.control.jaxrs.okrconfigsystem.exception.SystemConfigDeleteException;
import com.x.okr.assemble.control.jaxrs.okrconfigsystem.exception.SystemConfigIdEmptyException;
import com.x.okr.assemble.control.jaxrs.okrconfigsystem.exception.UserNoLoginException;
import com.x.okr.entity.OkrConfigSystem;

public class ExcuteDelete extends ExcuteBase {

	private Logger logger = LoggerFactory.getLogger( ExcuteDelete.class );
	
	protected ActionResult<WrapOutId> execute( HttpServletRequest request,EffectivePerson effectivePerson, String id ) throws Exception {
		ActionResult<WrapOutId> result = new ActionResult<>();
		Boolean check = true;		
		OkrUserCache  okrUserCache  = null;
		OkrConfigSystem okrConfigSystem = null;	
		try {
			okrUserCache = okrUserInfoService.getOkrUserCacheWithPersonName( effectivePerson.getName() );
		} catch (Exception e ) {
			check = false;
			Exception exception = new GetOkrUserCacheException( e, effectivePerson.getName() );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}		
		
		if( check && ( okrUserCache == null || okrUserCache.getLoginIdentityName() == null ) ){
			check = false;
			Exception exception = new UserNoLoginException( effectivePerson.getName() );
			result.error( exception );
			//logger.error( e, effectivePerson, request, null);
		}
		
		if( id == null || id.isEmpty() ){
			check = false;
			Exception exception = new SystemConfigIdEmptyException();
			result.error( exception );
			//logger.error( e, effectivePerson, request, null);
		}
		if( check ){
			try{
				okrConfigSystem = okrConfigSystemService.get( id );
				if( okrConfigSystem != null ){
					okrConfigSystemService.delete( id );
					ApplicationCache.notify( OkrConfigSystem.class );
					okrWorkDynamicsService.configSystemDynamic(
							okrConfigSystem.getConfigName(), 
							okrConfigSystem.getConfigCode(), 
							"删除系统配置", 
							effectivePerson.getName(), 
							okrUserCache.getLoginUserName(), 
							okrUserCache.getLoginIdentityName() , 
							"删除系统配置：" + okrConfigSystem.getConfigCode(), 
							"系统配置删除成功！"
					);
				}
				result.setData( new WrapOutId( id ));
			}catch(Exception e){
				Exception exception = new SystemConfigDeleteException( e, id );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		return result;
	}
	
}
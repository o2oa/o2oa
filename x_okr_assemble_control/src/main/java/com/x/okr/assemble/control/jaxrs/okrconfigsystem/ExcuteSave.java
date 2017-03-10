package com.x.okr.assemble.control.jaxrs.okrconfigsystem;

import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.cache.ApplicationCache;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.WrapOutId;
import com.x.okr.assemble.control.OkrUserCache;
import com.x.okr.entity.OkrConfigSystem;

public class ExcuteSave extends ExcuteBase {

	private Logger logger = LoggerFactory.getLogger( ExcuteSave.class );
	
	protected ActionResult<WrapOutId> execute( HttpServletRequest request,EffectivePerson effectivePerson, WrapInOkrConfigSystem wrapIn ) throws Exception {
		ActionResult<WrapOutId> result = new ActionResult<>();
		OkrConfigSystem okrConfigSystem = null;
		Boolean check = true;		
		OkrUserCache  okrUserCache  = null;
		try {
			okrUserCache = okrUserInfoService.getOkrUserCacheWithPersonName( effectivePerson.getName() );
		} catch (Exception e ) {
			check = false;
			Exception exception = new GetOkrUserCacheException( e, effectivePerson.getName() );
			result.error( exception );
			logger.error( exception, effectivePerson, request, null);
		}
		if( check && ( okrUserCache == null || okrUserCache.getLoginIdentityName() == null ) ){
			check = false;
			Exception exception = new UserNoLoginException( effectivePerson.getName() );
			result.error( exception );
			logger.error( exception, effectivePerson, request, null);
		}
		if( wrapIn != null && check ){
			try {
				okrConfigSystem = okrConfigSystemService.save( wrapIn );
				result.setData( new WrapOutId( okrConfigSystem.getId() ));
				ApplicationCache.notify( OkrConfigSystem.class );
				okrWorkDynamicsService.configSystemDynamic(
						okrConfigSystem.getConfigName(), 
						okrConfigSystem.getConfigCode(), 
						"修改系统配置", 
						effectivePerson.getName(), 
						okrUserCache.getLoginUserName(), 
						okrUserCache.getLoginIdentityName() , 
						"修改系统配置：" + okrConfigSystem.getConfigCode() + "值为" + okrConfigSystem.getConfigValue(), 
						"系统配置修改保存成功！"
				);
			} catch (Exception e) {
				Exception exception = new SystemConfigSaveException( e );
				result.error( exception );
				logger.error( exception, effectivePerson, request, null);
			}
		}
//		else{
//			result.error( new Exception( "请求传入的参数为空，无法继续保存系统配置!" ) );
//			result.setUserMessage( "请求传入的参数为空，无法继续保存系统配置!" );
//		}
		return result;
	}
	
}
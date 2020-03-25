package com.x.okr.assemble.control.jaxrs.okrconfigsystem;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.http.WrapOutBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.okr.assemble.control.OkrUserCache;
import com.x.okr.assemble.control.jaxrs.okrconfigsystem.exception.ExceptionGetOkrUserCache;

public class ActionAllowedDeployWork extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger( ActionAllowedDeployWork.class );
	
	/**
	 * 1、如果系统配置设置为所有人可以部署工作(系统配置 TOPUNIT_WORK_ADMIN 为空),那么返回true
	 * 2、如果系统配置设置为指定人可以部署工作(系统配置 TOPUNIT_WORK_ADMIN 不为空),
	 * 3、系统管理员可以部署（ 用户拥有角色 : OkrSystemAdmin ）
	 * @param effectivePerson
	 * @return
	 * @throws Exception
	 */
	protected ActionResult<WrapOutBoolean> execute( HttpServletRequest request,EffectivePerson effectivePerson ) throws Exception {
		ActionResult<WrapOutBoolean> result = new ActionResult<>();
		WrapOutBoolean wrapOutBoolean = new WrapOutBoolean();
		OkrUserCache  okrUserCache  = null;
		String configValue = null;
		boolean iCanDeploy = false;
		
		//1、如果用户是系统管理员,那么用户可以部署
		iCanDeploy = okrUserInfoService.getIsOkrManager( effectivePerson.getDistinguishedName() );
		
		if( !iCanDeploy ){
			//获取系统配置
			configValue = okrConfigSystemService.getValueWithConfigCode( "TOPUNIT_WORK_ADMIN" );
			if( configValue == null || configValue.isEmpty() || configValue.trim().length() == 0 ){
				iCanDeploy = true;
				logger.debug( "系统已经被配置为所有人可以部署工作, 用户拥有工作部署权限!" );
			}else{
				//TOPUNIT_WORK_ADMIN不为空,那么, 看看配置的人员身份,是否和当前登录者身份一致.
				try {
					okrUserCache = okrUserInfoService.getOkrUserCacheWithPersonName( effectivePerson.getDistinguishedName() );
					if( okrUserManagerService.isOkrWorkManager( okrUserCache.getLoginIdentityName() )){
						iCanDeploy = true;
						logger.debug( "用户已经被设置为顶层组织工作管理员, 用户拥有工作部署权限!" );
					}
				} catch (Exception e ) {
					Exception exception = new ExceptionGetOkrUserCache( e, effectivePerson.getDistinguishedName() );
					result.error( exception );
					logger.error( e, effectivePerson, request, null);
				}
			}
		}else{
			logger.debug( "用户拥有系统管理员角色, 默认拥有工作部署权限!" );
		}
		if( !iCanDeploy ){
			logger.debug( "用户未拥有工作部署权限!" );
		}
		wrapOutBoolean.setValue(iCanDeploy);
		
		result.setData( wrapOutBoolean );
		
		return result;
	}
	
}
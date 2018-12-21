package com.x.okr.assemble.control.jaxrs.okrconfigsercretary;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.okr.assemble.control.OkrUserCache;
import com.x.okr.assemble.control.jaxrs.okrconfigsercretary.exception.ExceptionGetOkrUserCache;
import com.x.okr.assemble.control.jaxrs.okrconfigsercretary.exception.ExceptionSercretaryConfigDelete;
import com.x.okr.assemble.control.jaxrs.okrconfigsercretary.exception.ExceptionSercretaryConfigIdEmpty;
import com.x.okr.assemble.control.jaxrs.okrconfigsercretary.exception.ExceptionSercretaryConfigNotExists;
import com.x.okr.assemble.control.jaxrs.okrconfigsercretary.exception.ExceptionSercretaryConfigQueryById;
import com.x.okr.assemble.control.jaxrs.okrconfigsystem.exception.ExceptionUserNoLogin;
import com.x.okr.assemble.control.jaxrs.queue.WrapInWorkDynamic;
import com.x.okr.entity.OkrConfigSecretary;

public class ActionDelete extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger( ActionDelete.class );
	
	protected ActionResult<Wo> execute( HttpServletRequest request, EffectivePerson effectivePerson, String id ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		OkrConfigSecretary okrConfigSecretary = null;
		OkrUserCache  okrUserCache  = null;
		Boolean check = true;
		
		if( check ){
			if( id == null || id.isEmpty() ){
				Exception exception = new ExceptionSercretaryConfigIdEmpty();
				result.error( exception );
			}
		}
		
		if( check ){
			try {
				okrUserCache = okrUserInfoService.getOkrUserCacheWithPersonName( effectivePerson.getDistinguishedName() );
			} catch (Exception e ) {
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
				okrConfigSecretary = okrConfigSecretaryService.get( id );
				if( okrConfigSecretary == null ){
					check = false;
					Exception exception = new ExceptionSercretaryConfigNotExists( id );
					result.error( exception );
				}
			}catch(Exception e){
				Exception exception = new ExceptionSercretaryConfigQueryById( e, id );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		if( check ){
			try{
				okrConfigSecretaryService.delete( id );
				
				Wo wo = new Wo();
				wo.setId( id );
				result.setData( wo );

				WrapInWorkDynamic.sendWithSecretaryConfig( 
						okrConfigSecretary, 
						effectivePerson.getDistinguishedName(),
						okrUserCache.getLoginUserName(),
						okrUserCache.getLoginUserName(),
						"删除领导秘书配置",
						"删除领导秘书配置成功！"
				);
				
				ApplicationCache.notify( OkrConfigSecretary.class );		
			}catch(Exception e){
				Exception exception = new ExceptionSercretaryConfigDelete( e, id );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		return result;
	}
	
	public static class Wo extends WoId {

	}
}
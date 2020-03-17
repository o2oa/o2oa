package com.x.okr.assemble.control.jaxrs.okrconfigsystem;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.okr.assemble.control.OkrUserCache;
import com.x.okr.assemble.control.jaxrs.okrconfigsystem.exception.ExceptionGetOkrUserCache;
import com.x.okr.assemble.control.jaxrs.okrconfigsystem.exception.ExceptionSystemConfigDelete;
import com.x.okr.assemble.control.jaxrs.okrconfigsystem.exception.ExceptionSystemConfigIdEmpty;
import com.x.okr.assemble.control.jaxrs.okrconfigsystem.exception.ExceptionUserNoLogin;
import com.x.okr.assemble.control.jaxrs.queue.WrapInWorkDynamic;
import com.x.okr.entity.OkrConfigSystem;

public class ActionDelete extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger( ActionDelete.class );
	
	protected ActionResult<Wo> execute( HttpServletRequest request,EffectivePerson effectivePerson, String id ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Boolean check = true;
		OkrUserCache  okrUserCache  = null;
		OkrConfigSystem okrConfigSystem = null;	
		try {
			okrUserCache = okrUserInfoService.getOkrUserCacheWithPersonName( effectivePerson.getDistinguishedName() );
		} catch (Exception e ) {
			check = false;
			Exception exception = new ExceptionGetOkrUserCache( e, effectivePerson.getDistinguishedName() );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}		
		
		if( check && ( okrUserCache == null || okrUserCache.getLoginIdentityName() == null ) ){
			check = false;
			Exception exception = new ExceptionUserNoLogin( effectivePerson.getDistinguishedName() );
			result.error( exception );
		}
		
		if( check ){
			if( id == null || id.isEmpty() ){
				check = false;
				Exception exception = new ExceptionSystemConfigIdEmpty();
				result.error( exception );
			}
		}		
		
		if( check ){
			try{
				okrConfigSystem = okrConfigSystemService.get( id );
				if( okrConfigSystem != null ){
					okrConfigSystemService.delete( id );
					ApplicationCache.notify( OkrConfigSystem.class );
					
					if( okrConfigSystem != null ) {
						WrapInWorkDynamic.sendWithSystemConfig( 
								okrConfigSystem, 
								effectivePerson.getDistinguishedName(),
								okrUserCache.getLoginUserName(),
								okrUserCache.getLoginUserName(),
								"删除系统配置",
								"系统配置删除成功！"
						);
					}
				}
				result.setData( new Wo( id ) );
			}catch(Exception e){
				Exception exception = new ExceptionSystemConfigDelete( e, id );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		return result;
	}
	
	public static class Wo extends WoId {
		public Wo( String id ) {
			this.setId( id );
		}
	}
}
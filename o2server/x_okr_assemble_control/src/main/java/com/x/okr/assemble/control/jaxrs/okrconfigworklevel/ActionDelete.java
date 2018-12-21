package com.x.okr.assemble.control.jaxrs.okrconfigworklevel;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.okr.assemble.control.OkrUserCache;
import com.x.okr.assemble.control.jaxrs.okrconfigsystem.exception.ExceptionGetOkrUserCache;
import com.x.okr.assemble.control.jaxrs.okrconfigsystem.exception.ExceptionUserNoLogin;
import com.x.okr.assemble.control.jaxrs.okrconfigworklevel.exception.ExceptionWorkLevelConfigDelete;
import com.x.okr.assemble.control.jaxrs.okrconfigworklevel.exception.ExceptionWorkLevelConfigIdEmpty;
import com.x.okr.assemble.control.jaxrs.okrconfigworklevel.exception.ExceptionWorkLevelConfigNotExists;
import com.x.okr.assemble.control.jaxrs.okrconfigworklevel.exception.ExceptionWorkLevelConfigQueryById;
import com.x.okr.assemble.control.jaxrs.queue.WrapInWorkDynamic;
import com.x.okr.entity.OkrConfigWorkLevel;

public class ActionDelete extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger( ActionDelete.class );
	
	protected ActionResult<Wo> execute( HttpServletRequest request,EffectivePerson effectivePerson, String id ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Boolean check = true;
		OkrConfigWorkLevel okrConfigWorkLevel = null;
		OkrUserCache  okrUserCache  = null;
		
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
				Exception exception = new ExceptionWorkLevelConfigIdEmpty();
				result.error( exception );
			}
		}
		
		if( check ){
			try {
				okrConfigWorkLevel = okrConfigWorkLevelService.get( id );
				if( okrConfigWorkLevel == null ){
					check = false;
					Exception exception = new ExceptionWorkLevelConfigNotExists( id );
					result.error( exception );
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionWorkLevelConfigQueryById( e, id );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		if( check ){
			try{
				okrConfigWorkLevelService.delete( id );
				
				if( okrConfigWorkLevel != null ) {
					WrapInWorkDynamic.sendWithConfigWorkLevel( 
							okrConfigWorkLevel, 
							effectivePerson.getDistinguishedName(),
							okrUserCache.getLoginUserName(),
							okrUserCache.getLoginUserName(),
							"删除工作级别信息",
							"删除工作级别信息成功！"
					);
				}
				
				result.setData( new Wo(id));
			}catch(Exception e){
				Exception exception = new ExceptionWorkLevelConfigDelete( e, id );
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
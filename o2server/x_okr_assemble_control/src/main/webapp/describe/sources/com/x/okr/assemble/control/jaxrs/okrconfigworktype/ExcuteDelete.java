package com.x.okr.assemble.control.jaxrs.okrconfigworktype;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.okr.assemble.control.OkrUserCache;
import com.x.okr.assemble.control.jaxrs.okrconfigsystem.exception.ExceptionGetOkrUserCache;
import com.x.okr.assemble.control.jaxrs.okrconfigsystem.exception.ExceptionUserNoLogin;
import com.x.okr.assemble.control.jaxrs.okrconfigworktype.exception.ExceptionWorkTypeConfigDelete;
import com.x.okr.assemble.control.jaxrs.okrconfigworktype.exception.ExceptionWorkTypeConfigIdEmpty;
import com.x.okr.assemble.control.jaxrs.okrconfigworktype.exception.ExceptionWorkTypeConfigNotExists;
import com.x.okr.assemble.control.jaxrs.okrconfigworktype.exception.ExceptionWorkTypeConfigQueryById;
import com.x.okr.assemble.control.jaxrs.queue.WrapInWorkDynamic;
import com.x.okr.entity.OkrConfigWorkType;

public class ExcuteDelete extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger( ExcuteDelete.class );
	
	protected ActionResult<Wo> execute( HttpServletRequest request,EffectivePerson effectivePerson, String id ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Boolean check = true;
		OkrConfigWorkType okrConfigWorkType = null;
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
				Exception exception = new ExceptionWorkTypeConfigIdEmpty();
				result.error( exception );
			}
		}
		
		if( check ){
			try {
				okrConfigWorkType = okrConfigWorkTypeService.get( id );
				if( okrConfigWorkType == null ){
					check = false;
					Exception exception = new ExceptionWorkTypeConfigNotExists( id );
					result.error( exception );
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionWorkTypeConfigQueryById( e, id );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		
		if( check ){
			try{
				okrConfigWorkTypeService.delete( id );
				result.setData( new Wo( id ));
				ApplicationCache.notify( OkrConfigWorkType.class );
				
				if( okrConfigWorkType != null ) {
					WrapInWorkDynamic.sendWithConfigWorkType( 
							okrConfigWorkType, 
							effectivePerson.getDistinguishedName(),
							okrUserCache.getLoginUserName(),
							okrUserCache.getLoginUserName(),
							"删除工作级别信息",
							"删除工作级别信息成功！"
					);
				}
			}catch(Exception e){
				Exception exception = new ExceptionWorkTypeConfigDelete( e, id );
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
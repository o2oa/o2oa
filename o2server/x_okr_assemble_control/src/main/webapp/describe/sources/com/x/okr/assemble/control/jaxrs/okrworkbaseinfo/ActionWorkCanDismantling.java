package com.x.okr.assemble.control.jaxrs.okrworkbaseinfo;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.http.WrapOutBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.okr.assemble.control.OkrUserCache;
import com.x.okr.assemble.control.jaxrs.okrworkbaseinfo.exception.ExceptionGetOkrUserCache;
import com.x.okr.assemble.control.jaxrs.okrworkbaseinfo.exception.ExceptionUserNoLogin;
import com.x.okr.assemble.control.jaxrs.okrworkbaseinfo.exception.ExceptionWorkIdEmpty;

public class ActionWorkCanDismantling extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger( ActionWorkCanDismantling.class );
	
	protected ActionResult<WrapOutBoolean> execute( HttpServletRequest request,EffectivePerson effectivePerson, String id ) throws Exception {
		ActionResult<WrapOutBoolean> result = new ActionResult<>();
		WrapOutBoolean wrap = new WrapOutBoolean();
		Boolean check = true;
		String loginIdentity = null;
		OkrUserCache  okrUserCache  = null;
		if( check ){
			try {
				okrUserCache = okrUserInfoService.getOkrUserCacheWithPersonName( effectivePerson.getDistinguishedName() );
			} catch ( Exception e ) {
				check = false;
				Exception exception = new ExceptionGetOkrUserCache( e, effectivePerson.getDistinguishedName()  );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}		
		if( check && ( okrUserCache == null || okrUserCache.getLoginIdentityName() == null ) ){
			check = false;
			Exception exception = new ExceptionUserNoLogin( effectivePerson.getDistinguishedName()  );
			result.error( exception );
		}
		if( check ){
			if( okrUserCache.getLoginIdentityName() == null || okrUserCache.getLoginIdentityName().isEmpty() ){
				check = false;
				Exception exception = new ExceptionUserNoLogin( effectivePerson.getDistinguishedName()  );
				result.error( exception );
			}
		}
		if( check ){
			loginIdentity = okrUserCache.getLoginIdentityName() ;
			if( id == null || id.isEmpty() ){
				Exception exception = new ExceptionWorkIdEmpty();
				result.error( exception );
			}else{
				try {
					if( okrWorkBaseInfoService.canDismantlingWorkByIdentity( id, loginIdentity ) ){
						wrap.setValue( true );
						result.setData( wrap );
					}else{
						wrap.setValue( false );
						result.setData( wrap );
					}
				} catch (Exception e) {
					result.error( e );
					logger.error(e);
					wrap.setValue( false );
					result.setData( wrap );
				}
			}
		}else{
			wrap.setValue( false );
			result.setData( wrap );
		}				
		return result;
	}
	
}
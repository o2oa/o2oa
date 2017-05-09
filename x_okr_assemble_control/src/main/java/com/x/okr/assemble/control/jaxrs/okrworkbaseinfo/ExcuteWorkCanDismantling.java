package com.x.okr.assemble.control.jaxrs.okrworkbaseinfo;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.WrapOutBoolean;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.okr.assemble.control.OkrUserCache;
import com.x.okr.assemble.control.jaxrs.okrworkbaseinfo.exception.GetOkrUserCacheException;
import com.x.okr.assemble.control.jaxrs.okrworkbaseinfo.exception.UserNoLoginException;
import com.x.okr.assemble.control.jaxrs.okrworkbaseinfo.exception.WorkIdEmptyException;

public class ExcuteWorkCanDismantling extends ExcuteBase {

	private Logger logger = LoggerFactory.getLogger( ExcuteWorkCanDismantling.class );
	
	protected ActionResult<WrapOutBoolean> execute( HttpServletRequest request,EffectivePerson effectivePerson, String id ) throws Exception {
		ActionResult<WrapOutBoolean> result = new ActionResult<>();
		WrapOutBoolean wrap = new WrapOutBoolean();
		Boolean check = true;
		String loginIdentity = null;
		OkrUserCache  okrUserCache  = null;
		if( check ){
			try {
				okrUserCache = okrUserInfoService.getOkrUserCacheWithPersonName( effectivePerson.getName() );
			} catch ( Exception e ) {
				check = false;
				Exception exception = new GetOkrUserCacheException( e, effectivePerson.getName()  );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}		
		if( check && ( okrUserCache == null || okrUserCache.getLoginIdentityName() == null ) ){
			check = false;
			Exception exception = new UserNoLoginException( effectivePerson.getName()  );
			result.error( exception );
			//logger.error( e, effectivePerson, request, null);
		}
		if( check ){
			if( okrUserCache.getLoginIdentityName() == null || okrUserCache.getLoginIdentityName().isEmpty() ){
				check = false;
				Exception exception = new UserNoLoginException( effectivePerson.getName()  );
				result.error( exception );
				//logger.error( e, effectivePerson, request, null);
			}
		}
		if( check ){
			loginIdentity = okrUserCache.getLoginIdentityName() ;
			if( id == null || id.isEmpty() ){
				Exception exception = new WorkIdEmptyException();
				result.error( exception );
				//logger.error( e, effectivePerson, request, null);
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
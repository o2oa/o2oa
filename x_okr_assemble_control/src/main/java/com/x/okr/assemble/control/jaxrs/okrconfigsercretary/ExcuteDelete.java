package com.x.okr.assemble.control.jaxrs.okrconfigsercretary;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.cache.ApplicationCache;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.WrapOutId;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.okr.entity.OkrConfigSecretary;

public class ExcuteDelete extends ExcuteBase {

	private Logger logger = LoggerFactory.getLogger( ExcuteDelete.class );
	
	protected ActionResult<WrapOutId> execute( HttpServletRequest request, EffectivePerson effectivePerson, String id ) throws Exception {
		ActionResult<WrapOutId> result = new ActionResult<>();
		OkrConfigSecretary okrConfigSecretary = null;
		if( id == null || id.isEmpty() ){
			Exception exception = new SercretaryConfigIdEmptyException();
			result.error( exception );
			logger.error( exception, effectivePerson, request, null);
		}else{
			try{
				okrConfigSecretary = okrConfigSecretaryService.get( id );
			}catch(Exception e){
				Exception exception = new SercretaryConfigQueryByIdException( e, id );
				result.error( exception );
				logger.error( exception, effectivePerson, request, null);
			}
			if( okrConfigSecretary != null ){
				try{
					okrConfigSecretaryService.delete( id );
					result.setData( new WrapOutId(id) );
					ApplicationCache.notify( OkrConfigSecretary.class );		
				}catch(Exception e){
					Exception exception = new SercretaryConfigDeleteException( e, id );
					result.error( exception );
					logger.error( exception, effectivePerson, request, null);
				}
			}else{
				Exception exception = new SercretaryConfigNotExistsException( id );
				result.error( exception );
				logger.error( exception, effectivePerson, request, null);
			}
		}
		return result;
	}
	
}
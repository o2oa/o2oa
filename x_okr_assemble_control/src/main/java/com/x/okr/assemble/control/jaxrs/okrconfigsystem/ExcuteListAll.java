package com.x.okr.assemble.control.jaxrs.okrconfigsystem;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;

import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.utils.SortTools;
import com.x.okr.entity.OkrConfigSystem;

import net.sf.ehcache.Element;

public class ExcuteListAll extends ExcuteBase {

	private Logger logger = LoggerFactory.getLogger( ExcuteListAll.class );
	
	@SuppressWarnings("unchecked")
	protected ActionResult<List<WrapOutOkrConfigSystem>> execute( HttpServletRequest request,EffectivePerson effectivePerson ) throws Exception {
		ActionResult<List<WrapOutOkrConfigSystem>> result = new ActionResult<List<WrapOutOkrConfigSystem>>();
		List<WrapOutOkrConfigSystem> wraps = null;
		List<OkrConfigSystem> okrConfigSystemList = null;
		
		String cacheKey = catchNamePrefix + ".all";
		Element element = null;
		
		element = cache.get( cacheKey );
		if( element != null ){
			wraps = ( List<WrapOutOkrConfigSystem> ) element.getObjectValue();
			result.setData( wraps );
			result.setCount( Long.parseLong( wraps.size() +"" ) );
		}else{
			try {
				okrConfigSystemList = okrConfigSystemService.listAll();
				if( okrConfigSystemList != null ){
					wraps = wrapout_copier.copy( okrConfigSystemList );
					SortTools.asc( wraps, true, "orderNumber");
					cache.put( new Element( cacheKey, wraps ) );
					result.setCount( Long.parseLong( wraps.size() +"" ) );
					result.setData( wraps );
				}
			} catch (Throwable th) {
				Exception exception = new SystemConfigListAllException( th );
				result.error( exception );
				logger.error( exception, effectivePerson, request, null);
			}
		}
		return result;
	}
	
}
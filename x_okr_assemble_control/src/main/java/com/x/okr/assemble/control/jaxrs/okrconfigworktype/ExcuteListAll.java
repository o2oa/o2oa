package com.x.okr.assemble.control.jaxrs.okrconfigworktype;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.okr.entity.OkrConfigWorkType;

import net.sf.ehcache.Element;

public class ExcuteListAll extends ExcuteBase {

	private Logger logger = LoggerFactory.getLogger( ExcuteListAll.class );
	
	@SuppressWarnings("unchecked")
	protected ActionResult<List<WrapOutOkrConfigWorkType>> execute( HttpServletRequest request,EffectivePerson effectivePerson ) throws Exception {
		ActionResult<List<WrapOutOkrConfigWorkType>> result = new ActionResult<>();
		List<WrapOutOkrConfigWorkType> wraps = null;
		List<OkrConfigWorkType> okrConfigWorkTypeList = null;
		
		String cacheKey = catchNamePrefix + ".all";
		Element element = null;
		
		element = cache.get( cacheKey );
		if( element != null ){
			wraps = ( List<WrapOutOkrConfigWorkType> ) element.getObjectValue();
			result.setData( wraps );
		}else{
			try {
				okrConfigWorkTypeList = okrConfigWorkTypeService.listAll();
				if( okrConfigWorkTypeList != null && !okrConfigWorkTypeList.isEmpty() ){
					wraps = wrapout_copier.copy( okrConfigWorkTypeList );
					cache.put( new Element( cacheKey, wraps ) );
					result.setData( wraps );
				}
			} catch ( Exception e) {
				Exception exception = new WorkTypeConfigListAllException( e );
				result.error( exception );
				logger.error( exception, effectivePerson, request, null);
			}
		}
		return result;
	}
	
}
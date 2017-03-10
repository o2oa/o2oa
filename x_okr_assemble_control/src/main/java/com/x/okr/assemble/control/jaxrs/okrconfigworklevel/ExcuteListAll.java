package com.x.okr.assemble.control.jaxrs.okrconfigworklevel;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.okr.entity.OkrConfigWorkLevel;

public class ExcuteListAll extends ExcuteBase {

	private Logger logger = LoggerFactory.getLogger( ExcuteListAll.class );
	
	protected ActionResult<List<WrapOutOkrConfigWorkLevel>> execute( HttpServletRequest request,EffectivePerson effectivePerson ) throws Exception {
		ActionResult<List<WrapOutOkrConfigWorkLevel>> result = new ActionResult<>();
		List<WrapOutOkrConfigWorkLevel> wraps = null;
		List<OkrConfigWorkLevel> okrConfigWorkLevelList = null;
		try {
			okrConfigWorkLevelList = okrConfigWorkLevelService.listAll();
			if( okrConfigWorkLevelList != null ){
				wraps = wrapout_copier.copy( okrConfigWorkLevelList );
				result.setData(wraps);
			}
		} catch (Throwable th) {
			Exception exception = new WorkLevelConfigListAllException( th );
			result.error( exception );
			logger.error( exception, effectivePerson, request, null);
		}
		return result;
	}
	
}
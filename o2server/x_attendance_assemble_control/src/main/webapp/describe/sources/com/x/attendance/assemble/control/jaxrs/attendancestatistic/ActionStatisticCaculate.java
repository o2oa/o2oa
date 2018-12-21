package com.x.attendance.assemble.control.jaxrs.attendancestatistic;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

public class ActionStatisticCaculate extends BaseAction {
	
	private static  Logger logger = LoggerFactory.getLogger( ActionStatisticCaculate.class );
	
	protected ActionResult<Wo> execute( HttpServletRequest request, EffectivePerson effectivePerson ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		attendanceStatisticServiceAdv.doStatistic( effectivePerson.getDebugger() );
		logger.info( "system do attendance statistic completed for user！" );
		return result;
	}

	public static class Wo extends WoId {
		public Wo( String id ) {
			setId( id );
		}
	}
}
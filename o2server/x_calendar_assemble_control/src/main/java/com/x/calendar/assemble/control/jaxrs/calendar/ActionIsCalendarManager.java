package com.x.calendar.assemble.control.jaxrs.calendar;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.http.WrapOutBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.calendar.assemble.control.ThisApplication;
import com.x.calendar.core.entity.Calendar;

public class ActionIsCalendarManager extends BaseAction {
	
	private Logger logger = LoggerFactory.getLogger( ActionIsCalendarManager.class );
	
	protected ActionResult<WrapOutBoolean> execute( HttpServletRequest request, EffectivePerson effectivePerson, String accountId ) throws Exception {
		ActionResult<WrapOutBoolean> result = new ActionResult<>();
		Wo wo = new Wo();
		Calendar calendar = null;
		Boolean check = true;
		
		if( check ){
			if( StringUtils.isEmpty( accountId ) ){
				check = false;
				Exception exception = new ExceptionCalendarIdEmpty();
				result.error( exception );
			}
		}
		
		if( check ){
			try {
				calendar = calendarServiceAdv.get( accountId );
				if( calendar == null ) {
					check = false;
					Exception exception = new ExceptionCalendarNotExists( accountId );
					result.error( exception );
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionCalendarInfoProcess( e, "系统根据ID查询指定日历信息时发生异常.ID:" + accountId );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		
		wo.setValue( ThisApplication.isCalendarManager( effectivePerson, calendar ) );
		result.setData(wo);
		return result;
	}

	public static class Wo extends WrapOutBoolean  {
	}

}
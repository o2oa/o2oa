package com.x.calendar.assemble.control.jaxrs.calendar;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.annotation.AuditLog;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.http.WrapOutBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.calendar.core.entity.Calendar;

/**
 * 关注一个公开的日历（非公开的日历不允许被关注）
 * 
 * @author O2LEE
 *
 */
public class ActionFollowCalendar extends BaseAction {
	
	private Logger logger = LoggerFactory.getLogger( ActionFollowCalendar.class );

	@AuditLog(operation = "关注日历")
	protected ActionResult<WrapOutBoolean> execute( HttpServletRequest request, EffectivePerson effectivePerson, String id ) throws Exception {
		ActionResult<WrapOutBoolean> result = new ActionResult<>();
		Wo wo = new Wo();
		Calendar calendar = null;
		Boolean check = true;
		
		if( check ){
			if( StringUtils.isEmpty( id ) ){
				check = false;
				Exception exception = new ExceptionCalendarIdEmpty();
				result.error( exception );
			}
		}
		
		if( check ){
			try {
				calendar = calendarServiceAdv.get( id );
				if( calendar == null ) {
					check = false;
					Exception exception = new ExceptionCalendarNotExists( id );
					result.error( exception );
				}else {
					if( !calendar.getIsPublic() ) {
						check = false;
						Exception exception = new ExceptionCalendarInfoProcess( "日历信息未公开，不允许被关注！" );
						result.error( exception );
					}
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionCalendarInfoProcess( e, "系统根据ID查询指定日历信息时发生异常.ID:" + id );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}

		if( check ){
			if(
					( StringUtils.equalsAnyIgnoreCase( calendar.getCreateor(), effectivePerson.getDistinguishedName() ) ) ||
					( StringUtils.equalsAnyIgnoreCase( calendar.getTarget(), effectivePerson.getDistinguishedName() ) ) ||
					( StringUtils.equalsAnyIgnoreCase( calendar.getCreateor(), "SYSTEM" ) )
			){
				check = false;
				Exception exception = new ExceptionCalendarInfoProcess( "不需要关注自己的日历.ID:" + id );
				result.error( exception );
			}
		}

		if( check ){
			try {
				check = calendarServiceAdv.follow( effectivePerson, id );
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionCalendarInfoProcess( e, "系统根据ID关注指定日历信息时发生异常.ID:" + id );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		
		wo.setValue( check );
		result.setData(wo);
		return result;
	}

	public static class Wo extends WrapOutBoolean  {
	}

}
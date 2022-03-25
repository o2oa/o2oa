package com.x.calendar.assemble.control.jaxrs.event;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.annotation.AuditLog;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.calendar.assemble.control.ThisApplication;
import com.x.calendar.core.entity.Calendar;
import com.x.calendar.core.entity.Calendar_Event;

public class ActionDestroySingleEventWithId extends BaseAction {
	
	private Logger logger = LoggerFactory.getLogger( ActionDestroySingleEventWithId.class );

	@AuditLog(operation = "删除日程事件")
	protected ActionResult<Wo> execute( HttpServletRequest request, EffectivePerson effectivePerson, String id ) throws Exception {
		//LogUtil.INFO( ">>>>>>ActionDestroySingleEventWithId.execute......" );
		ActionResult<Wo> result = new ActionResult<>();
		Calendar calendar = null;
		Calendar_Event calendar_event = null;
		List<String> unitNames = null;
		List<String> groupNames = null;
		String personName = effectivePerson.getDistinguishedName();
		Boolean check = true;
		
		if( check ){
			if( StringUtils.isEmpty( id ) ){
				check = false;
				Exception exception = new ExceptionEventPropertyEmpty("ID");
				result.error( exception );
			}
		}

		if( check ){
			try {
				calendar_event = calendar_EventServiceAdv.get( id );
				if( calendar_event == null ) {
					check = false;
					Exception exception = new ExceptionEventNotExists( id );
					result.error( exception );
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionEventProcess( e, "系统根据ID查询指定日历事件信息时发生异常.ID:" + id );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		
		if( check ){
			if( StringUtils.isNotEmpty( calendar_event.getCalendarId() )) {
				try {
					calendar = calendarServiceAdv.get( calendar_event.getCalendarId() );
					if( calendar == null ) {
						check = false;
						Exception exception = new ExceptionCalendarNotExists( calendar_event.getCalendarId() );
						result.error( exception );
					}
				} catch (Exception e) {
					check = false;
					Exception exception = new ExceptionEventProcess( e, "系统根据ID查询指定日历信息时发生异常.ID:" + calendar_event.getCalendarId() );
					result.error( exception );
					logger.error( e, effectivePerson, request, null);
				}
			}
		}
		
		if( check && !ThisApplication.isCalendarSystemManager(effectivePerson) ) {
			//判断用户权限
			try {
				unitNames = userManagerService.listUnitNamesWithPerson(personName);
				groupNames = userManagerService.listGroupNamesByPerson(personName);
				if( calendar != null ) {
					if( !(ThisApplication.isCalendarManager(effectivePerson, calendar) 
							|| ThisApplication.isCalendarPublisher(effectivePerson, unitNames, groupNames, calendar )) ) {//没权限
						check = false;
						Exception exception = new ExceptionInsufficientPermissions( "您没有权限从日历中删除日程事件.日历:" + calendar.getName() );
						result.error( exception );
					}
				}				
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionEventProcess( e, "系统根据用户查询组织和群组信息列表时发生异常." );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		
		if( check ){
			try {
				calendar_EventServiceAdv.destory( id );
				Wo wo = new Wo();
				wo.setId( id );
				result.setData( wo );
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionEventProcess( e, "系统根据ID删除指定日历事件信息时发生异常." );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		return result;
	}

	public static class Wo extends WoId  {
	
	}

}
package com.x.calendar.assemble.control.jaxrs.event;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.calendar.assemble.control.ThisApplication;
import com.x.calendar.core.entity.Calendar;
import com.x.calendar.core.entity.Calendar_Event;
import com.x.calendar.core.tools.LogUtil;

public class ActionDestroyWithMaster extends BaseAction {
	
	private Logger logger = LoggerFactory.getLogger( ActionDestroyWithMaster.class );
	
	protected ActionResult<Wo> execute( HttpServletRequest request, EffectivePerson effectivePerson, String eventId ) throws Exception {
		//LogUtil.INFO( ">>>>>>ActionDestroyWithMaster.execute......" );
		ActionResult<Wo> result = new ActionResult<>();
		Calendar calendar = null;
		Calendar_Event calendar_event = null;
		List<String> unitNames = null;
		List<String> groupNames = null;
		String personName = effectivePerson.getDistinguishedName();
		Boolean check = true;
		
		if( check ){
			if( StringUtils.isEmpty( eventId ) ){
				check = false;
				Exception exception = new ExceptionEventPropertyEmpty("ID");
				result.error( exception );
			}
		}
		
		if( check ){
			try {
				calendar_event = calendar_EventServiceAdv.get( eventId );
				if( calendar_event == null ) {
					check = false;
					Exception exception = new ExceptionEventNotExists( eventId );
					result.error( exception );
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionEventProcess( e, "系统根据ID查询指定日历事件信息时发生异常.ID:" + eventId );
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
			//然后把repeatMaster删除, repeatMaster删除时会把该Master生成的所有事件全部删除
			try {
				calendar_RepeatedMasterServiceAdv.destoryWithMasterId( calendar_event.getRepeatMasterId() );
				Wo wo = new Wo();
				wo.setId( eventId );
				result.setData( wo );
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionEventProcess( e, "系统根据事件ID删除所有重复事件信息时发生异常." );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		return result;
	}

	public static class Wo extends WoId  {
	
	}

}
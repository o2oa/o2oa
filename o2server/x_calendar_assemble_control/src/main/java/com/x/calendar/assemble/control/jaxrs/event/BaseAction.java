package com.x.calendar.assemble.control.jaxrs.event;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.exception.PromptException;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.tools.ListTools;
import com.x.calendar.assemble.control.service.CalendarServiceAdv;
import com.x.calendar.assemble.control.service.Calendar_EventRepeatMasterServiceAdv;
import com.x.calendar.assemble.control.service.Calendar_EventServiceAdv;
import com.x.calendar.assemble.control.service.UserManagerService;
import com.x.calendar.common.date.DateOperation;
import com.x.calendar.core.entity.Calendar;
import com.x.calendar.core.entity.Calendar_Event;

import net.fortuna.ical4j.model.property.RRule;

public class BaseAction extends StandardJaxrsAction{
	
	protected UserManagerService userManagerService = new UserManagerService();
	protected CalendarServiceAdv calendarServiceAdv = new CalendarServiceAdv();
	protected Calendar_EventServiceAdv calendar_EventServiceAdv = new Calendar_EventServiceAdv();
	protected Calendar_EventRepeatMasterServiceAdv calendar_RepeatedMasterServiceAdv = new Calendar_EventRepeatMasterServiceAdv();
	protected static DateOperation dateOperation = new DateOperation();

	/**
	 * 对日历事件信息进行验证，给出正确的提示信息
	 * @param event
	 * @param calendar
	 * @return
	 * @throws Exception
	 */
	protected PromptException eventValidate( Calendar_Event event, Calendar calendar ) throws Exception {
		
		//日历ID不能为空
		if( StringUtils.isEmpty( event.getCalendarId() ) ){
			return new ExceptionEventPropertyEmpty("日历ID(calendarId)");
		}
		
		if( calendar == null ) {
			try {
				calendar = calendarServiceAdv.get( event.getCalendarId() );
				if( calendar == null ) {
					return new ExceptionCalendarNotExists( event.getCalendarId() );
				}
			} catch (Exception e) {
				e.printStackTrace();
				return new ExceptionEventProcess( e, "系统根据ID查询指定日历信息时发生异常.ID:" + event.getCalendarId() );
			}
		}
		
		//事件继承日历的可见范围
		if( ListTools.isNotEmpty( calendar.getViewablePersonList() )) {
			for( String value : calendar.getViewablePersonList() ) {
				event.addViewablePerson( value );
			}
		}
		if( ListTools.isNotEmpty( calendar.getViewableUnitList() )) {
			for( String value : calendar.getViewableUnitList() ) {
				event.addViewableUnit( value );
			}
		}
		if( ListTools.isNotEmpty( calendar.getViewableGroupList() )) {
			for( String value : calendar.getViewableGroupList() ) {
				event.addViewableGroup( value );
			}
		}
		
		//事件继承日历的管理范围
		if( ListTools.isNotEmpty( calendar.getManageablePersonList() )) {
			for( String value : calendar.getManageablePersonList() ) {
				event.addManageablePerson( value );
			}
		}
		
		//事件继承日历的公开形式
		event.setIsPublic( calendar.getIsPublic() );
		
		//默认使用日历的颜色
		if( StringUtils.isEmpty( event.getColor() ) ){
			event.setColor( calendar.getColor() );
		}
				
		//设置中否公开日历事件，继承日历的设置
		event.setIsPublic( calendar.getIsPublic() );
		
		//标题不能为空
		if( StringUtils.isEmpty( event.getTitle() ) ){
			return new ExceptionEventPropertyEmpty("事件标题(title)");
		}
		
		if( event.getStartTime() == null ) {
			event.setStartTime( new Date() );
		}
		
		if( StringUtils.isEmpty(event.getEventType())) {
			event.setEventType ("CAL_EVENT");
		}
		
		if( StringUtils.isEmpty(event.getTargetType())) {
			event.setTargetType ("PERSON");
		}
		
		if( StringUtils.isEmpty( event.getSource()) ){
			event.setSource( "PERSONAL" );
		}
		
		//开始和结束时间不能为空，和是否全天进行验证
		if( event.getIsAllDayEvent() ) {
			//自动填入开始和结束时间，以传入的开始时间为准
			event.setStartTime( dateOperation.getBeginTimeInDay( event.getStartTime() ) );
			event.setEndTime( dateOperation.getEndTimeInDay( event.getEndTime() ) );
		}else {
			if( event.getEndTime() == null ) {
				event.setEndTime( new Date() );
			}
			if( event.getStartTime().after(event.getEndTime() )) {
				//交换位置
				Date temp = event.getStartTime();
				event.setStartTime( event.getEndTime() );
				event.setEndTime( temp );
			}
		}
		
		event.setStartTimeStr(dateOperation.getDateFromDate(event.getStartTime(), "yyyy-MM-dd HH:mm:ss"));
		event.setEndTimeStr(dateOperation.getDateFromDate(event.getEndTime(), "yyyy-MM-dd HH:mm:ss"));
		
		List<String> days = dateOperation.listDaysBetweenDate( event.getStartTime(), event.getEndTime() );
		if( ListTools.isNotEmpty( days )) {
			event.setDaysOfDuration( days.size() );
		}
		if( event.getDaysOfDuration() == 1 ) {
			//看看是不是全天，，如果不是全天就设置为0
			if( !event.getIsAllDayEvent() ) {
				event.setDaysOfDuration( 0 );
			}
		}
		
		//对重复规则进行验证
		if( "TASK_EVENT".equalsIgnoreCase( event.getEventType() )) {
			//任务事件不能重复，只有日程事件CAL_EVENT可以有重复规则
			if( StringUtils.isNotEmpty( event.getRecurrenceRule() )) {
				return new ExceptionTaskEventCanNotRecurrence();
			}
			if( StringUtils.isNotEmpty( event.getRepeatMasterId() )) {
				return new ExceptionTaskEventCanNotRepeatMaster( event.getRepeatMasterId() ) ;
			}
		}

		//如果有重复规则时，需要验证规则是否正确
		if( StringUtils.isNotEmpty( event.getRecurrenceRule() )) {
			//转换为RRule
			try{
				new RRule(event.getRecurrenceRule());
			}catch( Exception e) {
				return new ExceptionEventRRuleInvalid(event.getRecurrenceRule());
			}
		}
		
		//对提醒设置进行验证
		if( StringUtils.isEmpty( event.getValarmTime_config() ) || "0,0,0,0".equalsIgnoreCase( event.getValarmTime_config().trim() )) {
			event.setValarmTime_config("0,0,0,0");
			event.setAlarm( false );
			event.setAlarmTime( null );
			event.setValarm_description( null );
			event.setValarm_mailto( null );
			event.setValarm_Summary( null );
		}else {			
			event.setAlarm( true );
			if( StringUtils.isEmpty( event.getValarm_Summary() )) {
				event.setValarm_Summary( "日程提醒" );
			}
			//计算提醒时间，以开始时间为基准
			String[] alarm_config = event.getValarmTime_config().trim().split(","); 
			if( alarm_config.length == 4 ) {
				int day, hour, min, sec = 0;
				Date alarmTime = null;
				try {
					day = Integer.parseInt( alarm_config[0].trim() );
					hour = Integer.parseInt( alarm_config[01].trim() );
					min = Integer.parseInt( alarm_config[2].trim() );
					sec = Integer.parseInt( alarm_config[3].trim() );
					alarmTime = dateOperation.caculateNewDate( event.getStartTime(), day, hour, min, sec );
					event.setAlarmTime(alarmTime);
				}catch( Exception e ) {
					return new ExceptionEventAlarmConfigError("提醒配置应该为使用','分隔的数字。");
				}				 
			}else {
				return new ExceptionEventAlarmConfigError("格式错误，正确格式：天,时,分,秒。");
			}
		}
		return null;
	}
}

package com.x.calendar.assemble.control.jaxrs.event;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.base.core.project.tools.SortTools;
import com.x.calendar.assemble.control.ExceptionWrapInConvert;
import com.x.calendar.assemble.control.ThisApplication;
import com.x.calendar.core.entity.Calendar_Event;

/**
 * 根据条件和权限列示能访问到的符合条件的日历信息(输出简单的列表，不按天组织)
 * 1、查询时间段内有多少重复事件主体需要进行事件生成
 * 2、生成事件
 * 3、根据条件查询日程事件信息列表
 * @author O2LEE
 *
 */
public class ActionSimpleListWithCondition extends BaseAction {
	
private Logger logger = LoggerFactory.getLogger( ActionSimpleListWithCondition.class );
	
	protected ActionResult<List<Wo>> execute( HttpServletRequest request, EffectivePerson effectivePerson, JsonElement jsonElement ) throws Exception {		
		ActionResult<List<Wo>> result = new ActionResult<>();
		List<Wo> wos = new ArrayList<>();
		List<Calendar_Event> calendar_EventList = null;
		Boolean check = true;
		Boolean manager = false;
		Wi wi = null;
		List<String> ids = null;
		List<String> unitNames = null;
		List<String> groupNames = null;
		String personName = effectivePerson.getDistinguishedName();
				
		if( check ) {
			try {
				manager = ThisApplication.isCalendarSystemManager( effectivePerson );
			}catch( Exception e) {
				check = false;
				Exception exception = new ExceptionEventProcess( e, "系统根据查询用户是否是系统管理员时发生异常." );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}		

		try {
			wi = this.convertToWrapIn( jsonElement, Wi.class );
		} catch (Exception e ) {
			check = false;
			Exception exception = new ExceptionWrapInConvert( e, jsonElement );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}
		
		if( check ){
			//如果没有设置查询日期范围就查本月的
			if( wi.getStartTime() == null && wi.getEndTime() == null ) {
				wi.setStartTime( dateOperation.getFirstDayInMonth( new Date() ));
				wi.setEndTime( dateOperation.getEndDayInMonth( new Date() ));
			}
			if( StringUtils.isEmpty( wi.getEventType() )) {
				wi.setEventType( "CAL_EVENT" );
			}
		}
		
		if( check ){
			if( manager ) {
				try {
					ids = calendar_EventServiceAdv.listWithCondition( wi.getKey(), wi.getEventType(), wi.getSource(), wi.getCreatePerson(), wi.getCalendarIds(), 
							null, null, null, wi.getStartTime(), wi.getEndTime() );
				} catch (Exception e) {
					check = false;
					Exception exception = new ExceptionEventProcess( e, "系统根据用户权限查询日历信息ID列表时发生异常." );
					result.error( exception );
					logger.error( e, effectivePerson, request, null);
				}
			}else {
				try {
					unitNames = userManagerService.listUnitNamesWithPerson( personName );
					groupNames = userManagerService.listGroupNamesByPerson( personName );
					if( ListTools.isEmpty( wi.getCalendarIds()  )) {
						//查询用户可以看到的所有CalendarIds
						wi.setCalendarIds( calendarServiceAdv.listWithCondition(personName, unitNames, groupNames) );
					}
					if( ListTools.isNotEmpty( wi.getCalendarIds()  ) ) {
						ids = calendar_EventServiceAdv.listWithCondition( wi.getKey(), wi.getEventType(), wi.getSource(), wi.getCreatePerson(), wi.getCalendarIds(),
								personName, unitNames, groupNames, wi.getStartTime(), wi.getEndTime() );
					}
				} catch (Exception e) {
					check = false;
					Exception exception = new ExceptionEventProcess( e, "系统根据用户权限查询日历信息ID列表时发生异常." );
					result.error( exception );
					logger.error( e, effectivePerson, request, null);
				}
			}
		}
		
		if( check ){
			if( ListTools.isNotEmpty( ids )) {
				calendar_EventList = calendar_EventServiceAdv.list(ids);
				try {
					wos = Wo.copier.copy( calendar_EventList );
					if( ListTools.isNotEmpty( wos )) {
						SortTools.asc( wos, "startTime" );
					}
				} catch (Exception e) {
					check = false;
					Exception exception = new ExceptionEventProcess( e, "将所有查询到的日历信息对象转换为可以输出的信息时发生异常." );
					result.error( exception );
					logger.error( e, effectivePerson, request, null);
				}
			}
		}
		result.setData( wos );
		return result;
	}
	
	public static class Wi{

		@FieldDescribe("日历账号ID")
		private List<String> calendarIds = null;
		
		@FieldDescribe("信息类别: CAL_EVENT | TASK_EVENT")
		private String eventType = "CAL_EVENT";
		
		@FieldDescribe("信息来源: PERSONAL| LEADER | UNIT | MEETING | BUSINESS_TRIP | HOLIDAY")
		private String source;
		
		@FieldDescribe("事件标题 或者 备注信息 模糊搜索")
		private String key = null;
		
		@FieldDescribe("查询开始时间")
		private Date startTime = null;

		@FieldDescribe("查询结束时间")
		private Date endTime = null;

		@FieldDescribe("创建者")
	    private String createPerson = null;

		public String getEventType() {
			return eventType;
		}

		public String getKey() {
			return key;
		}

		public Date getStartTime() {
			return startTime;
		}

		public Date getEndTime() {
			return endTime;
		}

		public void setEventType(String eventType) {
			this.eventType = eventType;
		}

		public void setKey(String key) {
			this.key = key;
		}

		public void setStartTime(Date startTime) {
			this.startTime = startTime;
		}

		public void setEndTime(Date endTime) {
			this.endTime = endTime;
		}

		public String getSource() {
			return source;
		}

		public void setSource(String source) {
			this.source = source;
		}

		public List<String> getCalendarIds() {
			return calendarIds;
		}

		public void setCalendarIds(List<String> calendarIds) {
			this.calendarIds = calendarIds;
		}

		public String getCreatePerson() {
			return createPerson;
		}

		public void setCreatePerson(String createPerson) {
			this.createPerson = createPerson;
		}
		
	}
	
	public static class Wo extends Calendar_Event  {
		
		private static final long serialVersionUID = -5076990764713538973L;
		
		public static List<String> Excludes = new ArrayList<String>();
		
		public static WrapCopier<Calendar_Event, Wo> copier = WrapCopierFactory.wo( Calendar_Event.class, Wo.class, null,Wo.Excludes);
	}
}
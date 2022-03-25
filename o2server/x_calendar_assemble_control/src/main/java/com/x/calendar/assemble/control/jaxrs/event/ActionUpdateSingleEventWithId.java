package com.x.calendar.assemble.control.jaxrs.event;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.AuditLog;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.exception.PromptException;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.calendar.assemble.control.ExceptionWrapInConvert;
import com.x.calendar.assemble.control.ThisApplication;
import com.x.calendar.core.entity.Calendar;
import com.x.calendar.core.entity.Calendar_Event;

/**
 * 根据事件ID更新单个日程事件信息
 * 
 * @author O2LEE
 *
 */
public class ActionUpdateSingleEventWithId extends BaseAction {
	
	private static Logger logger = LoggerFactory.getLogger( ActionUpdateSingleEventWithId.class );

	@AuditLog(operation = "修改日程事件")
	protected ActionResult<Wo> execute( HttpServletRequest request, EffectivePerson effectivePerson, String id, JsonElement jsonElement ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wi wi = null;
		Calendar calendar = null;
		Calendar_Event calendar_Event = null;
		Calendar_Event new_Event = null;
		List<String> unitNames = null;
		List<String> groupNames = null;
		String personName = effectivePerson.getDistinguishedName();
		Boolean check = true;
		
		try {
			wi = this.convertToWrapIn( jsonElement, Wi.class );
		} catch (Exception e ) {
			check = false;
			Exception exception = new ExceptionWrapInConvert( e, jsonElement );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}
		
		if( check ){
			if( StringUtils.isEmpty( id ) ){
				check = false;
				Exception exception = new ExceptionEventPropertyEmpty("ID");
				result.error( exception );
			}
		}
		
		if( check ){
			try {
				calendar_Event = calendar_EventServiceAdv.get( id );
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionEventProcess( e, "系统根据ID查询指定日历记录信息时发生异常.ID:" + id );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		
		if( check ){
			if( calendar_Event == null ) {
				check = false;
				Exception exception = new ExceptionEventNotExists( id );
				result.error( exception );
			}
		}
		
		if( check ){
			if( StringUtils.isNotEmpty( calendar_Event.getCalendarId() )) {
				try {
					calendar = calendarServiceAdv.get( calendar_Event.getCalendarId() );
					if( calendar == null ) {
						check = false;
						Exception exception = new ExceptionCalendarNotExists( calendar_Event.getCalendarId() );
						result.error( exception );
					}
				} catch (Exception e) {
					check = false;
					Exception exception = new ExceptionEventProcess( e, "系统根据ID查询指定日历信息时发生异常.ID:" + calendar_Event.getCalendarId() );
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
				new_Event = Wi.copier.copy( wi );
				new_Event.setId( calendar_Event.getId() );
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionEventProcess( e, "系统将用户传入的数据转换为日历事件信息对象时发生异常." );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}

		if( check ) {
			PromptException exception = this.eventValidate( new_Event, calendar );
			if( exception != null ) {
				check = false;
				result.error( exception );
			}
		}
		
		if( check ){
			try {
				calendar_Event = calendar_EventServiceAdv.updateSingleEvent( new_Event, effectivePerson );
				result.setData( new Wo( calendar_Event.getId() ) );
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionEventProcess( e, "更新单个日程信息时发生异常.ID:" + id );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		return result;
	}
	
	public static class Wi extends Calendar_Event{
		
		private static final long serialVersionUID = -5076990764713538973L;
		
		public static List<String> Excludes = new ArrayList<String>(JpaObject.FieldsUnmodify);
		
		public static WrapCopier< Wi, Calendar_Event > copier =  WrapCopierFactory.wi( Wi.class, Calendar_Event.class, null, null );	
		
		private String identity = null;

		public String getIdentity() {
			return identity;
		}

		public void setIdentity(String identity) {
			this.identity = identity;
		}
	}
	
	public static class Wo extends WoId {
		public Wo( String id ) {
			setId( id );
		}
	}
}
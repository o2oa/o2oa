package com.x.calendar.assemble.control.jaxrs.event;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.calendar.assemble.control.ThisApplication;
import com.x.calendar.core.entity.Calendar;
import com.x.calendar.core.entity.Calendar_Event;
import com.x.calendar.core.entity.Calendar_EventComment;

public class ActionGet extends BaseAction {
	
	private Logger logger = LoggerFactory.getLogger( ActionGet.class );
	
	protected ActionResult<Wo> execute( HttpServletRequest request, EffectivePerson effectivePerson, String id ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wo wrap = null;
		Calendar calendar = null;
		Calendar_Event calendar_Event = null;
		Calendar_EventComment calendar_EventComment = null;
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
				calendar_Event = calendar_EventServiceAdv.get( id );
				if( calendar_Event == null ) {
					check = false;
					Exception exception = new ExceptionEventNotExists( id );
					result.error( exception );
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionEventProcess( e, "系统根据ID查询指定日历记录信息时发生异常.ID:" + id );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		
		if( check ){
			try {
				calendar = calendarServiceAdv.get( calendar_Event.getCalendarId() );
				if( calendar == null ) {
					check = false;
					Exception exception = new ExceptionCalendarNotExists( id );
					result.error( exception );
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionEventProcess( e, "系统根据ID查询指定日历信息时发生异常.ID:" + id );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}

		if( check ){
			if( StringUtils.equals( "{#CLOB#}", calendar_Event.getComment() ) && StringUtils.isNotEmpty( calendar_Event.getCommentId() )){
				try {
					calendar_EventComment = calendar_EventServiceAdv.getCommentWithCommentId( calendar_Event.getCommentId() );
					if( calendar_EventComment != null ) {
						calendar_Event.setComment( calendar_EventComment.getLobValue() );
					}
				} catch (Exception e) {
					check = false;
					Exception exception = new ExceptionEventProcess( e, "系统根据ID查询指定日历记录信息时发生异常.ID:" + id );
					result.error( exception );
					logger.error( e, effectivePerson, request, null);
				}
			}
		}
		
		if( check ){
			try {
				wrap = Wo.copier.copy( calendar_Event );
				wrap.setManageable(ThisApplication.isEventManager( effectivePerson, calendar, calendar_Event ));				
				result.setData( wrap );
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionEventProcess( e, "将所有查询到的日历记录信息对象转换为可以输出的信息时发生异常." );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		return result;
	}

	public static class Wo extends Calendar_Event  {
		
		private static final long serialVersionUID = -5076990764713538973L;
		
		public static List<String> Excludes = new ArrayList<String>();
		
		@FieldDescribe("用户是否可以对该事件进行管理.")
		private Boolean manageable = false;
		
		public static WrapCopier<Calendar_Event, Wo> copier = WrapCopierFactory.wo( Calendar_Event.class, Wo.class, null,Wo.Excludes);

		public Boolean getManageable() {
			return manageable;
		}

		public void setManageable(Boolean manageable) {
			this.manageable = manageable;
		}		
	}
}
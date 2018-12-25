package com.x.calendar.assemble.control.jaxrs.event;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.exception.PromptException;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.calendar.assemble.control.EnumCalendarSource;
import com.x.calendar.assemble.control.ExceptionWrapInConvert;
import com.x.calendar.core.entity.Calendar_Event;

public class ActionMessageReceive extends BaseAction {

	private Logger logger = LoggerFactory.getLogger(ActionMessageReceive.class);

	protected ActionResult<Wo> execute(HttpServletRequest request, EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		logger.debug("receive:{}.", jsonElement);
		ActionResult<Wo> result = new ActionResult<>();
		Wi message = new Wi();
		Wo wo = new Wo();
		wo.setValue( false );
		Calendar_Event calendar_Event = null;
		Boolean check = true;
		
		try {
			message = this.convertToWrapIn( jsonElement, Wi.class );
		} catch (Exception e ) {
			check = false;
			Exception exception = new ExceptionWrapInConvert( e, jsonElement );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}
		
		if( check ) {
			//MEETING: meeting_invite | meeting_delete | meeting_accept | meeting_reject"
			if( message != null ) {
				if( "meeting_accept".equalsIgnoreCase( message.getType() ) ) {
					calendar_Event = composeCalendarEvent( message );
					PromptException exception = this.eventValidate( calendar_Event, null );
					if( exception != null ) {
						check = false;
						result.error( exception );
					}else {
						try {
							calendar_Event = calendar_EventServiceAdv.createByCipher( calendar_Event );
							List<String> ids = new ArrayList<>();
							ids.add( calendar_Event.getId() );
							wo.setValue( true );
						} catch (Exception e) {
							check = false;
							exception = new ExceptionMessageProcess( e, "处理消息信息时发生异常." );
							result.error( exception );
							logger.error( e, effectivePerson, request, null);
						}
					}
				}else if( "calendar_event_delete".equalsIgnoreCase( message.getType() ) ) {
					deleteCalendarEvent( message );
					wo.setValue( true );
				}
			}
		}
		result.setData(wo);
		return result;
	}

	/**
	 * 删除日历事件
	 * @param message
	 * @return
	 * @throws Exception
	 */
	private List<String> deleteCalendarEvent( Wi message ) throws Exception {
		WiMeeting wiMeeting = gson.fromJson( message.getBody(), WiMeeting.class );
		if( StringUtils.isNotEmpty( wiMeeting.getId() ) ) {
			//根据bundle来删除
			return calendar_EventServiceAdv.destoryWithBundle( wiMeeting.getId() );
		}
		return null;
	}
	
	/**
	 * 将消息对象转换为一个日程事件对象
	 * @param message
	 * @return
	 * @throws Exception 
	 */
	private Calendar_Event composeCalendarEvent( Wi message ) throws Exception {
		WiMeeting wiMeeting = null;
		Calendar_Event calendar_Event = null;
		String person = message.getPerson();
		String title = message.getTitle();
		if( StringUtils.isEmpty( person )) {
			throw new Exception("person is empy!");
		}
		wiMeeting = gson.fromJson( message.getBody(), WiMeeting.class );
		if( wiMeeting != null ) {
			calendar_Event = new Calendar_Event();
			calendar_Event.setBundle( wiMeeting.getId() );
			calendar_Event.setColor( "#1462BE" );
			calendar_Event.setComment( title );
			calendar_Event.setValarm_description( wiMeeting.getDescription() );
			calendar_Event.setValarm_Summary( title );			
			calendar_Event.setEventType( "CAL_EVENT" );
			calendar_Event.setStartTime( wiMeeting.getStartTime() );
			calendar_Event.setEndTime( wiMeeting.getCompletedTime() );	
			calendar_Event.setSource( EnumCalendarSource.MEETING.name() );
			calendar_Event.setValarmTime_config( "0,0,-30,0" );
			return calendar_Event;
		}
		return null;
	}
	
	public static class Wi extends GsonPropertyObject {

		@FieldDescribe("消息类型：meeting_invite | meeting_delete | meeting_accept | meeting_reject")
		private String type;

		@FieldDescribe("接收者")
		private String person;

		@FieldDescribe("消息提醒标题")
		private String title;

		@FieldDescribe("消息内容")
		private JsonElement body;

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public String getPerson() {
			return person;
		}

		public void setPerson(String person) {
			this.person = person;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public JsonElement getBody() {
			return body;
		}

		public void setBody(JsonElement body) {
			this.body = body;
		}
	}

	public static class WiMeeting{
		
		public static List<String> Excludes = new ArrayList<String>(JpaObject.FieldsUnmodify);
		
		public static WrapCopier< Wi, WiMeeting > copier =  WrapCopierFactory.wi( Wi.class, WiMeeting.class, null, null );	
		
		@FieldDescribe("数据库主键,自动生成.")
		private String id = null;
		
		@FieldDescribe("名称")
		private String subject;

		@FieldDescribe("说明")
		private String description;

		@FieldDescribe("所属楼层.")
		private String room;
		
		@FieldDescribe("开始时间.")
		private Date startTime;

		@FieldDescribe("结束时间.")
		private Date completedTime;

		@FieldDescribe("邀请人员.")
		private List<String> invitePersonList;

		@FieldDescribe("接受人员.")
		private List<String> acceptPersonList;

		@FieldDescribe("拒绝人员.")
		private List<String> rejectPersonList;

		@FieldDescribe("会议申请人")
		private String applicant;

		@FieldDescribe("备注.")
		private String memo;

		public String getId() {
			return id;
		}

		public String getSubject() {
			return subject;
		}

		public String getDescription() {
			return description;
		}

		public String getRoom() {
			return room;
		}

		public Date getStartTime() {
			return startTime;
		}

		public Date getCompletedTime() {
			return completedTime;
		}

		public List<String> getInvitePersonList() {
			return invitePersonList;
		}

		public List<String> getAcceptPersonList() {
			return acceptPersonList;
		}

		public List<String> getRejectPersonList() {
			return rejectPersonList;
		}

		public String getApplicant() {
			return applicant;
		}

		public String getMemo() {
			return memo;
		}

		public void setId(String id) {
			this.id = id;
		}

		public void setSubject(String subject) {
			this.subject = subject;
		}

		public void setDescription(String description) {
			this.description = description;
		}

		public void setRoom(String room) {
			this.room = room;
		}

		public void setStartTime(Date startTime) {
			this.startTime = startTime;
		}

		public void setCompletedTime(Date completedTime) {
			this.completedTime = completedTime;
		}

		public void setInvitePersonList(List<String> invitePersonList) {
			this.invitePersonList = invitePersonList;
		}

		public void setAcceptPersonList(List<String> acceptPersonList) {
			this.acceptPersonList = acceptPersonList;
		}

		public void setRejectPersonList(List<String> rejectPersonList) {
			this.rejectPersonList = rejectPersonList;
		}

		public void setApplicant(String applicant) {
			this.applicant = applicant;
		}

		public void setMemo(String memo) {
			this.memo = memo;
		}		
	}

	public static class Wo extends WrapBoolean {

	}

}
package com.x.meeting.assemble.control.jaxrs.meeting;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.tools.ListTools;
import com.x.meeting.assemble.control.Business;
import com.x.meeting.assemble.control.MessageFactory;
import com.x.meeting.assemble.control.service.HstService;
import com.x.meeting.core.entity.*;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;
import java.util.List;

class ActionEdit extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, JsonElement jsonElement) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			Meeting meeting = emc.find(id, Meeting.class);
			if (null == meeting) {
				throw new ExceptionMeetingNotExist(id);
			}
			if (!business.meetingEditAvailable(effectivePerson, meeting)) {
				throw new ExceptionAccessDenied(effectivePerson);
			}
			if(StringUtils.isBlank(wi.getSubject())){
				throw new ExceptionCustomError("会议标题不能为空！");
			}
			MeetingConfigProperties config = business.getConfig();
			if(MeetingModeEnum.ONLINE.getValue().equals(wi.getMode())){
				if(MeetingConfigProperties.ONLINE_PROJECT_HST.equals(config.getOnlineProduct())) {
					if(MeetingModeEnum.ONLINE.getValue().equals(meeting.getMode())){
						wi.setRoomId(meeting.getRoomId());
						wi.setRoomLink(meeting.getRoomLink());
					}else {
						boolean flag = HstService.createMeeting(wi, config);
						if(!flag){
							throw new ExceptionCustomError("创建线上会议失败，请联系管理员！");
						}
					}
				}else if(StringUtils.isBlank(wi.getRoomId()) || StringUtils.isBlank(wi.getRoomLink())){
					throw new ExceptionCustomError("会议号和会议链接不能为空！");
				}
			}else{
				wi.setRoomId("");
				wi.setRoomLink("");
				if(StringUtils.isBlank(wi.getRoom())){
					throw new ExceptionCustomError("会议室不能为空！");
				}
			}
			Room room = null;
			if(StringUtils.isNotBlank(wi.getRoom())) {
				room = emc.find(wi.getRoom(), Room.class);
				if (null == room) {
					throw new ExceptionRoomNotExist(wi.getRoom());
				}
				if (!business.room().checkIdle(meeting.getRoom(), wi.getStartTime(), wi.getCompletedTime(),
						meeting.getId())) {
					throw new ExceptionRoomNotAvailable(room.getName());
				}
			}

			//判断开始时间或者结束时间有没有修改过
			boolean modifyTime = false;
			Date StartTime = wi.getStartTime();
			Date CompletedTime = wi.getCompletedTime();
			if(StartTime.getTime() != meeting.getStartTime().getTime() ) {
				modifyTime = true;
			}
			if(CompletedTime.getTime() != meeting.getCompletedTime().getTime()) {
				modifyTime = true;
			}

			Wi.copier.copy(wi, meeting);
			if(meeting.getInviteMemberList()==null){
				meeting.setInviteMemberList(meeting.getInvitePersonList());
			}
			List<String> invitePersonList = this.convertToPerson(business, ListTools.trim(wi.getInviteMemberList(), true, true));
			List<String> modifyInvitePersonList = ListUtils.subtract(invitePersonList, meeting.getInvitePersonList());
			List<String> inviteDelPersonList = ListUtils.subtract(meeting.getInvitePersonList(), invitePersonList);
			meeting.setInvitePersonList(invitePersonList);
			meeting.setInviteDelPersonList(inviteDelPersonList);

			emc.beginTransaction(Meeting.class);
			emc.check(meeting, CheckPersistType.all);
			emc.commit();

			if(config.onLineEnabled()){
				HstService.appendMeetingUser(meeting, config);
				if(modifyTime){
					HstService.reserveMeeting(meeting, config);
				}
			}
			if (ConfirmStatus.allow.equals(meeting.getConfirmStatus())) {

				if(modifyTime) {
					for (String _s : meeting.getInvitePersonList()) {
						MessageFactory.meeting_invite(_s, meeting, room);
					}
				}else {
					for (String _s : modifyInvitePersonList) {
						MessageFactory.meeting_invite(_s, meeting, room);
					}
				}
				for (String _s : inviteDelPersonList) {
					MessageFactory.meeting_deleteInvitePerson(_s, meeting);
				}

				//this.notifyMeetingInviteMessage(business, meeting);
			}
			Wo wo = new Wo();
			wo.setId(meeting.getId());
			result.setData(wo);
			return result;
		}
	}

	public static class Wi extends Meeting {

		private static final long serialVersionUID = -4637797853096659198L;
		static WrapCopier<Wi, Meeting> copier = WrapCopierFactory.wi(Wi.class, Meeting.class, null,
				JpaObject.FieldsUnmodify);
	}

	public static class Wo extends WoId {

	}

}

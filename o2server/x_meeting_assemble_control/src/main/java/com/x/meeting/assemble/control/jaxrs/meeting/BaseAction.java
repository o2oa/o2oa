package com.x.meeting.assemble.control.jaxrs.meeting;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.base.core.project.config.Config;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.organization.Person;
import com.x.base.core.project.tools.Crypto;
import com.x.base.core.project.tools.DefaultCharset;
import com.x.base.core.project.tools.PropertyTools;
import com.x.meeting.assemble.control.wrapout.WrapOutMeeting;
import com.x.meeting.core.entity.MeetingConfigProperties;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.organization.OrganizationDefinition;
import com.x.base.core.project.organization.OrganizationDefinition.DistinguishedNameCategory;
import com.x.base.core.project.tools.ListTools;
import com.x.meeting.assemble.control.Business;
import com.x.meeting.core.entity.ConfirmStatus;
import com.x.meeting.core.entity.Meeting;
import com.x.meeting.core.entity.Meeting_;

abstract class BaseAction extends StandardJaxrsAction {

	private static Logger logger = LoggerFactory.getLogger(BaseAction.class);

//	@SuppressWarnings("unused")
//	protected void notifyMeetingInviteMessage(Business business, Meeting meeting) throws Exception {
//		if (ListTools.isNotEmpty(meeting.getInvitePersonList())) {
//			Room room = business.entityManagerContainer().find(meeting.getRoom(), Room.class, ExceptionWhen.not_found);
//			Building building = business.entityManagerContainer().find(room.getBuilding(), Building.class,
//					ExceptionWhen.not_found);
//			for (String str : ListTools.nullToEmpty(meeting.getInvitePersonList())) {
//				logger.debug("send old meeting invite message to:{}, message body:{}", str, meeting);
////				MeetingInviteMessage message = new MeetingInviteMessage(str, building.getId(), room.getId(),
////						meeting.getId());
////				Collaboration.send(message);
//			}
//		}
//	}

//	@SuppressWarnings("unused")
//	protected void notifyMeetingCancelMessage(Business business, Meeting meeting) throws Exception {
//		if (ListTools.isNotEmpty(meeting.getInvitePersonList())) {
//			Room room = business.entityManagerContainer().find(meeting.getRoom(), Room.class, ExceptionWhen.not_found);
//			Building building = business.entityManagerContainer().find(room.getBuilding(), Building.class,
//					ExceptionWhen.not_found);
//			for (String str : ListTools.trim(meeting.getInvitePersonList(), true, true, meeting.getApplicant())) {
//				// "会议室:" + room.getName() + ",会议地点:" + building.getName() +
//				// building.getAddress() + ".",
//				// "meetingReject");
////				MeetingCancelMessage message = new MeetingCancelMessage(str, building.getId(), room.getId(),
////						meeting.getId());
////				Collaboration.send(message);
//			}
//		}
//	}

//	@SuppressWarnings("unused")
//	protected void notifyMeetingAcceptMessage(Business business, Meeting meeting, String person) throws Exception {
//		Room room = business.entityManagerContainer().find(meeting.getRoom(), Room.class, ExceptionWhen.not_found);
//		Building building = business.entityManagerContainer().find(room.getBuilding(), Building.class,
//				ExceptionWhen.not_found);
//		for (String str : ListTools.trim(meeting.getInvitePersonList(), true, true, meeting.getApplicant())) {
//			// Collaboration.notification(str, "会议接受提醒.", person + "接受会议邀请:" +
//			// meeting.getSubject(),
//			// "会议室:" + room.getName() + ",会议地点:" + building.getName() +
//			// building.getAddress() + ".",
//			// "meetingAccept");
////			MeetingAcceptMessage message = new MeetingAcceptMessage(str, building.getId(), room.getId(),
////					meeting.getId());
////			Collaboration.send(message);
//		}
//
//	}

//	@SuppressWarnings("unused")
//	protected void notifyMeetingRejectMessage(Business business, Meeting meeting, String person) throws Exception {
//		Room room = business.entityManagerContainer().find(meeting.getRoom(), Room.class, ExceptionWhen.not_found);
//		Building building = business.entityManagerContainer().find(room.getBuilding(), Building.class,
//				ExceptionWhen.not_found);
//		for (String str : ListTools.trim(meeting.getInvitePersonList(), true, true, meeting.getApplicant())) {
////			MeetingRejectMessage message = new MeetingRejectMessage(str, building.getId(), room.getId(),
////					meeting.getId());
////			Collaboration.send(message);
//		}
//	}

	protected List<String> convertToPerson(Business business, List<String> list) throws Exception {
		List<String> os = new ArrayList<>();
		DistinguishedNameCategory category = OrganizationDefinition.distinguishedNameCategory(list);
		if (ListTools.isNotEmpty(category.getPersonList())) {
			os.addAll(business.organization().person().list(category.getPersonList()));
		}
		if (ListTools.isNotEmpty(category.getIdentityList())) {
			os.addAll(business.organization().person().listWithIdentity(category.getIdentityList()));
		}
		if (ListTools.isNotEmpty(category.getUnitList())) {
			os.addAll(business.organization().person().listWithUnitSubDirect(category.getUnitList()));
		}
		if (ListTools.isNotEmpty(category.getGroupList())) {
			os.addAll(business.organization().person().listWithGroup(category.getGroupList()));
		}
		if (ListTools.isNotEmpty(category.getUnknownList())) {
			os.addAll(business.organization().person().list(category.getUnknownList()));
		}
		os = ListTools.trim(os, true, true);
		return os;
	}

	protected Predicate filterManualCompleted(CriteriaBuilder cb, Root<Meeting> root, Predicate p,
			Boolean manualCompleted) {
		if (null != manualCompleted) {
			p = cb.and(p, cb.equal(root.get(Meeting_.manualCompleted), manualCompleted));
		}
		return p;
	}

	protected Predicate filterCheckinPerson(CriteriaBuilder cb, Root<Meeting> root, Predicate p, String checkinPerson) {
		if (!StringUtils.isBlank(checkinPerson)) {
			p = cb.and(p, cb.isMember(checkinPerson.trim(), root.get(Meeting_.checkinPersonList)));
		}
		return p;
	}

	protected Predicate filterAcceptPerson(CriteriaBuilder cb, Root<Meeting> root, Predicate p,
			String acceptPersonList) {
		if (!StringUtils.isBlank(acceptPersonList)) {
			p = cb.and(p, cb.isMember(acceptPersonList.trim(), root.get(Meeting_.acceptPersonList)));
		}
		return p;
	}

	protected Predicate filterInvitePerson(CriteriaBuilder cb, Root<Meeting> root, Predicate p,
			String invitePersonList) {
		if (!StringUtils.isBlank(invitePersonList)) {
			p = cb.and(p, cb.isMember(invitePersonList.trim(), root.get(Meeting_.invitePersonList)));
		}
		return p;
	}

	protected Predicate filterApplicant(CriteriaBuilder cb, Root<Meeting> root, Predicate p, String applicant) {
		if (!StringUtils.isBlank(applicant)) {
			p = cb.and(p, cb.equal(root.get(Meeting_.applicant), applicant));
		}
		return p;
	}

	protected Predicate filterConfirmStatus(CriteriaBuilder cb, Root<Meeting> root, Predicate p, String confirmStatus) {
		if (!StringUtils.isBlank(confirmStatus)) {
			p = cb.and(p, cb.equal(root.get(Meeting_.confirmStatus), ConfirmStatus.valueOf(confirmStatus)));
		}
		return p;
	}

	protected Predicate filterCompletedTime(CriteriaBuilder cb, Root<Meeting> root, Predicate p, Date completedTime) {
		if (null != completedTime) {
			p = cb.and(p, cb.lessThanOrEqualTo(root.get(Meeting_.completedTime), completedTime));
		}
		return p;
	}

	protected Predicate filterStartTime(CriteriaBuilder cb, Root<Meeting> root, Predicate p, Date startTime) {
		if (null != startTime) {
			p = cb.and(p, cb.greaterThanOrEqualTo(root.get(Meeting_.startTime), startTime));
		}
		return p;
	}

	protected Predicate filterMeetingStatus(CriteriaBuilder cb, Root<Meeting> root, Predicate p, String meetingStatus) {
		if (meetingStatus.equalsIgnoreCase("completed")) {
			p = cb.and(p, cb.or(cb.lessThan(root.get(Meeting_.completedTime), new Date()),
					cb.equal(root.get(Meeting_.manualCompleted), true)));
		}else if (meetingStatus.equalsIgnoreCase("processing")) {
			Date date = new Date();
			p = cb.and(p, cb.notEqual(root.get(Meeting_.manualCompleted), true));
			p = cb.and(p, cb.lessThanOrEqualTo(root.get(Meeting_.startTime), date));
			p = cb.and(p, cb.greaterThanOrEqualTo(root.get(Meeting_.completedTime), date));
		}else if (meetingStatus.equalsIgnoreCase("wait")) {
			p = cb.and(p, cb.notEqual(root.get(Meeting_.manualCompleted), true));
			p = cb.and(p, cb.notEqual(root.get(Meeting_.confirmStatus), ConfirmStatus.wait));
			p = cb.and(p, cb.greaterThan(root.get(Meeting_.startTime), new Date()));
		}else if (meetingStatus.equalsIgnoreCase("applying")) {
			p = cb.and(p, cb.equal(root.get(Meeting_.confirmStatus), ConfirmStatus.wait));
		}
		return p;
	}

	protected Predicate filterRoom(CriteriaBuilder cb, Root<Meeting> root, Predicate p, String room) {
		if (!StringUtils.isBlank(room)) {
			p = cb.and(p, cb.equal(root.get(Meeting_.room), room));
		}
		return p;
	}

	protected Predicate filterSubject(CriteriaBuilder cb, Root<Meeting> root, Predicate p, String subject) {
		if (!StringUtils.isBlank(subject)) {
			p = cb.and(p, cb.like(root.get(Meeting_.subject), "%" + subject + "%"));
		}
		return p;
	}

	protected Predicate filterHostUnit(CriteriaBuilder cb, Root<Meeting> root, Predicate p, String hostUnit) {
		if (!StringUtils.isBlank(hostUnit)) {
			p = cb.and(p, cb.like(root.get(Meeting_.hostUnit), "%" + hostUnit + "%"));
		}
		return p;
	}

	protected Predicate filterHostPerson(CriteriaBuilder cb, Root<Meeting> root, Predicate p, String hostPerson) {
		if (!StringUtils.isBlank(hostPerson)) {
			p = cb.and(p, cb.like(root.get(Meeting_.hostPerson), "%" + hostPerson + "%"));
		}
		return p;
	}

	protected Predicate filterType(CriteriaBuilder cb, Root<Meeting> root, Predicate p, String type) {
		if (!StringUtils.isBlank(type)) {
			p = cb.and(p, cb.like(root.get(Meeting_.type), "%" + type + "%"));
		}
		return p;
	}

	protected String generateHstPwd(String userId) throws Exception{
		String content = userId+"#"+System.currentTimeMillis();
		return Crypto.rsaEncrypt(content, Config.publicKey());
	}

	protected void setOnlineLink(Business business, EffectivePerson effectivePerson, List<? extends WrapOutMeeting> wos) throws Exception{
		MeetingConfigProperties config = business.getConfig();
		if(config.onLineEnabled()){
			for(WrapOutMeeting wo : wos) {
				if(StringUtils.isBlank(wo.getRoomLink())){
					continue;
				}
				if (BooleanUtils.isTrue(config.getOnlineConfig().getHstAuth())) {
					Person person = business.organization().person().getObject(effectivePerson.getDistinguishedName());
					String userId = PropertyTools.getOrElse(person, config.getOnlineConfig().getO2ToHstUid(), String.class, person.getUnique());
					userId = StringUtils.isNoneBlank(userId) ? userId : person.getUnique();
					wo.setRoomLink(wo.getRoomLink() + "&userName=" + userId + "&userPwd=" + URLEncoder.encode(generateHstPwd(userId), DefaultCharset.charset));
				} else {
					wo.setRoomLink(wo.getRoomLink() + "&userName=" + effectivePerson.getName());
				}
			}
		}
	}

}

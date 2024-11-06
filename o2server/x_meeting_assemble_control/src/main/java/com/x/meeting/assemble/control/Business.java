package com.x.meeting.assemble.control;

import com.x.meeting.core.entity.*;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.project.exception.ExceptionWhen;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.organization.OrganizationDefinition;
import com.x.meeting.assemble.control.factory.AttachmentFactory;
import com.x.meeting.assemble.control.factory.BuildingFactory;
import com.x.meeting.assemble.control.factory.MeetingFactory;
import com.x.meeting.assemble.control.factory.RoomFactory;
import com.x.organization.core.express.Organization;

public class Business {

	private EntityManagerContainer emc;

	public Business(EntityManagerContainer emc) throws Exception {
		this.emc = emc;
	}

	public EntityManagerContainer entityManagerContainer() {
		return this.emc;
	}

	private Organization organization;

	public Organization organization() throws Exception {
		if (null == this.organization) {
			this.organization = new Organization(ThisApplication.context());
		}
		return organization;
	}

	private MeetingFactory meeting;

	public MeetingFactory meeting() throws Exception {
		if (null == this.meeting) {
			this.meeting = new MeetingFactory(this);
		}
		return meeting;
	}

	private BuildingFactory building;

	public BuildingFactory building() throws Exception {
		if (null == this.building) {
			this.building = new BuildingFactory(this);
		}
		return building;
	}

	private RoomFactory room;

	public RoomFactory room() throws Exception {
		if (null == this.room) {
			this.room = new RoomFactory(this);
		}
		return room;
	}

	private AttachmentFactory attachment;

	public AttachmentFactory attachment() throws Exception {
		if (null == this.attachment) {
			this.attachment = new AttachmentFactory(this);
		}
		return attachment;
	}

	public void isManager(EffectivePerson effectivePerson, ExceptionWhen exceptionWhen) throws Exception {
		boolean available = this.isManager(effectivePerson);
		if ((!available) && exceptionWhen.equals(ExceptionWhen.not_allow)) {
			throw new Exception(
					"person{name:" + effectivePerson.getDistinguishedName() + "} has sufficient permissions");
		}
	}

	public Boolean isManager(EffectivePerson effectivePerson) throws Exception {
		if (effectivePerson.isManager()) {
			return true;
		}
		if (this.organization().person().hasRole(effectivePerson, OrganizationDefinition.MeetingManager)) {
			return true;
		}
		return false;
	}

	public void buildingEditAvailable(EffectivePerson effectivePerson, ExceptionWhen exceptionWhen) throws Exception {
		boolean available = this.buildingEditAvailable(effectivePerson);
		if ((!available) && exceptionWhen.equals(ExceptionWhen.not_allow)) {
			throw new Exception(
					"person{name:" + effectivePerson.getDistinguishedName() + "} has sufficient permissions");
		}
	}

	public Boolean buildingEditAvailable(EffectivePerson effectivePerson) throws Exception {
		if (effectivePerson.isManager()) {
			return true;
		}
		if (this.organization().person().hasRole(effectivePerson, OrganizationDefinition.MeetingManager)) {
			return true;
		}
		return false;
	}

	public void roomEditAvailable(EffectivePerson effectivePerson, Room room, ExceptionWhen exceptionWhen)
			throws Exception {
		boolean available = this.roomEditAvailable(effectivePerson, room);
		if ((!available) && exceptionWhen.equals(ExceptionWhen.not_allow)) {
			throw new Exception(
					"person{name:" + effectivePerson.getDistinguishedName() + "} has sufficient permissions");
		}
	}

	public Boolean roomEditAvailable(EffectivePerson effectivePerson, Room room) throws Exception {
		if (this.buildingEditAvailable(effectivePerson)) {
			return true;
		}
		/* 房间审核员 */
		if (StringUtils.equals(effectivePerson.getDistinguishedName(), room.getAuditor())) {
			return true;
		}
		return false;
	}

	public void meetingAuditAvailable(EffectivePerson effectivePerson, Meeting meeting, ExceptionWhen exceptionWhen)
			throws Exception {
		boolean available = this.meetingAuditAvailable(effectivePerson, meeting);
		if ((!available) && exceptionWhen.equals(ExceptionWhen.not_allow)) {
			throw new Exception(
					"person{name:" + effectivePerson.getDistinguishedName() + "} has sufficient permissions");
		}
	}

	public Boolean meetingAuditAvailable(EffectivePerson effectivePerson, Meeting meeting) throws Exception {
		/* 审核者 */
		if (StringUtils.equals(effectivePerson.getDistinguishedName(), meeting.getAuditor())) {
			return true;
		}
		/* 管理员 */
		if (this.isManager(effectivePerson)) {
			return true;
		}

		return false;
	}

	public void meetingEditAvailable(EffectivePerson effectivePerson, Meeting meeting, ExceptionWhen exceptionWhen)
			throws Exception {
		boolean available = this.meetingEditAvailable(effectivePerson, meeting);
		if ((!available) && exceptionWhen.equals(ExceptionWhen.not_allow)) {
			throw new Exception(
					"person{name:" + effectivePerson.getDistinguishedName() + "} has sufficient permissions");
		}
	}

	public Boolean meetingEditAvailable(EffectivePerson effectivePerson, Meeting meeting) throws Exception {
		/* 申请者 */
		if (StringUtils.equals(effectivePerson.getDistinguishedName(), meeting.getApplicant())) {
			return true;
		}
		/* 审核者 */
		if (this.meetingAuditAvailable(effectivePerson, meeting)) {
			return true;
		}

		return false;
	}

	public void meetingReadAvailable(EffectivePerson effectivePerson, Meeting meeting, ExceptionWhen exceptionWhen)
			throws Exception {
		boolean available = this.meetingReadAvailable(effectivePerson, meeting);
		if ((!available) && exceptionWhen.equals(ExceptionWhen.not_allow)) {
			throw new Exception(
					"person{name:" + effectivePerson.getDistinguishedName() + "} has sufficient permissions");
		}
	}

	public Boolean meetingReadAvailable(EffectivePerson effectivePerson, Meeting meeting) throws Exception {
		if (this.meetingEditAvailable(effectivePerson, meeting)) {
			return true;
		}
		/* 受邀参加会议 */
		if (null != meeting.getInvitePersonList()) {
			if (meeting.getInvitePersonList().contains(effectivePerson.getDistinguishedName())) {
				return true;
			}
		}
		return false;
	}

	public void attachmentEditAvailable(EffectivePerson effectivePerson, Meeting meeting, ExceptionWhen exceptionWhen)
			throws Exception {
		boolean available = this.attachmentEditAvailable(effectivePerson, meeting);
		if ((!available) && exceptionWhen.equals(ExceptionWhen.not_allow)) {
			throw new Exception(
					"person{name:" + effectivePerson.getDistinguishedName() + "} has sufficient permissions");
		}
	}

	public Boolean attachmentEditAvailable(EffectivePerson effectivePerson, Meeting meeting) throws Exception {
		if (this.meetingReadAvailable(effectivePerson, meeting)) {
			return true;
		}
		return false;
	}

	public void estimateConfirmStatus(Meeting meeting) {
		if (StringUtils.isEmpty(meeting.getAuditor())) {
			meeting.setConfirmStatus(ConfirmStatus.allow);
		} else {
			meeting.setConfirmStatus(ConfirmStatus.wait);
		}
	}

	public MeetingConfigProperties getConfig() throws Exception{
		MeetingConfig config = this.emc.firstEqual(MeetingConfig.class, MeetingConfig.name_FIELDNAME, MeetingConfig.DEFINITION_MEETING_CONFIG);
		if(config != null){
			return config.getProperties();
		}
		return new MeetingConfigProperties();
	}

}

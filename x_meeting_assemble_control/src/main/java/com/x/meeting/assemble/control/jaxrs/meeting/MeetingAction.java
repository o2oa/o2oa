package com.x.meeting.assemble.control.jaxrs.meeting;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.x.base.core.application.jaxrs.StandardJaxrsAction;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.StorageType;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.exception.ExceptionWhen;
import com.x.base.core.exception.JaxrsBusinessLogicException;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.HttpMediaType;
import com.x.base.core.http.ResponseFactory;
import com.x.base.core.http.WrapOutId;
import com.x.base.core.http.annotation.HttpMethodDescribe;
import com.x.base.core.project.server.StorageMapping;
import com.x.base.core.utils.ListTools;
import com.x.base.core.utils.SortTools;
import com.x.collaboration.core.message.Collaboration;
import com.x.collaboration.core.message.notification.MeetingAcceptMessage;
import com.x.collaboration.core.message.notification.MeetingCancelMessage;
import com.x.collaboration.core.message.notification.MeetingInviteMessage;
import com.x.collaboration.core.message.notification.MeetingRejectMessage;
import com.x.meeting.assemble.control.Business;
import com.x.meeting.assemble.control.ThisApplication;
import com.x.meeting.assemble.control.WrapTools;
import com.x.meeting.assemble.control.wrapin.WrapInMeeting;
import com.x.meeting.assemble.control.wrapout.WrapOutMeeting;
import com.x.meeting.core.entity.Attachment;
import com.x.meeting.core.entity.Building;
import com.x.meeting.core.entity.ConfirmStatus;
import com.x.meeting.core.entity.Meeting;
import com.x.meeting.core.entity.Room;

@Path("meeting")
public class MeetingAction extends StandardJaxrsAction {

	@HttpMethodDescribe(value = "获取指定会议内容.", response = WrapOutMeeting.class)
	@GET
	@Path("{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response get(@Context HttpServletRequest request, @PathParam("id") String id) {
		ActionResult<WrapOutMeeting> result = new ActionResult<>();
		WrapOutMeeting wrap = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			EffectivePerson effectivePerson = this.effectivePerson(request);
			Meeting meeting = emc.find(id, Meeting.class, ExceptionWhen.not_found);
			business.meetingReadAvailable(effectivePerson, meeting, ExceptionWhen.not_allow);
			wrap = WrapTools.meetingOutCopier.copy(meeting);
			WrapTools.setAttachment(business, wrap);
			WrapTools.decorate(wrap, effectivePerson);
			result.setData(wrap);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "申请会议.", request = WrapInMeeting.class, response = WrapOutId.class)
	@POST
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response post(@Context HttpServletRequest request, WrapInMeeting wrapIn) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		WrapOutId wrap = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			EffectivePerson effectivePerson = this.effectivePerson(request);
			Room room = emc.find(wrapIn.getRoom(), Room.class, ExceptionWhen.not_found);
			if (room.getAvailable() == false) {
				throw new JaxrsBusinessLogicException("会议室:" + room.getName() + ", 当前不可用.");
			}
			Meeting meeting = new Meeting();
			emc.beginTransaction(Meeting.class);
			WrapTools.meetingInCopier.copy(wrapIn, meeting);
			meeting.setManualCompleted(false);
			meeting.setAuditor(room.getAuditor());
			meeting.setRoom(room.getId());
			meeting.setApplicant(effectivePerson.getName());
			business.organization().person().checkName(meeting, "applicant");
			business.organization().person().checkNameList(meeting, "invitePersonList", true);
			business.organization().person().checkNameList(meeting, "acceptPersonList", true);
			business.organization().person().checkNameList(meeting, "rejectPersonList", true);
			ListTools.subtractWithProperty(meeting, "invitePersonList", meeting.getApplicant());
			if (!business.room().checkIdle(meeting.getRoom(), meeting.getStartTime(), meeting.getCompletedTime(), "")) {
				throw new JaxrsBusinessLogicException("会议室:" + room.getName() + ", 已经预约.");
			}
			business.estimateConfirmStatus(meeting);
			emc.persist(meeting, CheckPersistType.all);
			emc.commit();
			if (ConfirmStatus.allow.equals(meeting.getConfirmStatus())) {
				this.notifyMeetingInviteMessage(business, meeting);
			}
			wrap = new WrapOutId(meeting.getId());
			result.setData(wrap);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "修改会议.", request = WrapInMeeting.class, response = WrapOutId.class)
	@PUT
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("{id}")
	public Response put(@Context HttpServletRequest request, @PathParam("id") String id, WrapInMeeting wrapIn) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		WrapOutId wrap = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			EffectivePerson effectivePerson = this.effectivePerson(request);
			Meeting meeting = emc.find(id, Meeting.class, ExceptionWhen.not_allow);
			Room room = emc.find(wrapIn.getRoom(), Room.class, ExceptionWhen.not_found);
			business.meetingEditAvailable(effectivePerson, meeting, ExceptionWhen.not_allow);
			emc.beginTransaction(Meeting.class);
			WrapTools.meetingInCopier.copy(wrapIn, meeting);
			meeting.setAuditor(room.getAuditor());
			meeting.setRoom(room.getId());
			meeting.setApplicant(effectivePerson.getName());
			business.organization().person().checkName(meeting, "applicant");
			business.organization().person().checkNameList(meeting, "invitePersonList", true);
			business.organization().person().checkNameList(meeting, "acceptPersonList", true);
			business.organization().person().checkNameList(meeting, "rejectPersonList", true);
			ListTools.subtractWithProperty(meeting, "invitePersonList", meeting.getApplicant());
			if (!business.room().checkIdle(meeting.getRoom(), meeting.getStartTime(), meeting.getCompletedTime(),
					meeting.getId())) {
				throw new JaxrsBusinessLogicException("会议室:" + room.getName() + ", 已经预约.");
			}
			emc.persist(meeting, CheckPersistType.all);
			emc.commit();
			wrap = new WrapOutId(meeting.getId());
			result.setData(wrap);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "删除Meeting.", response = WrapOutId.class)
	@DELETE
	@Path("{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response delete(@Context HttpServletRequest request, @PathParam("id") String id) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		WrapOutId wrap = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			EffectivePerson effectivePerson = this.effectivePerson(request);
			Business business = new Business(emc);
			Meeting meeting = emc.find(id, Meeting.class, ExceptionWhen.not_found);
			business.meetingEditAvailable(effectivePerson, meeting, ExceptionWhen.not_allow);
			emc.beginTransaction(Meeting.class);
			emc.beginTransaction(Attachment.class);
			List<String> ids = business.attachment().listWithMeeting(meeting.getId());
			for (Attachment o : emc.list(Attachment.class, ids)) {
				StorageMapping mapping = ThisApplication.storageMappings.get(StorageType.meeting, o.getStorage());
				o.deleteContent(mapping);
				emc.remove(o);
			}
			emc.remove(meeting);
			if (ConfirmStatus.allow.equals(meeting.getConfirmStatus())) {
				this.notifyMeetingCancelMessage(business, meeting);
			}
			emc.commit();
			wrap = new WrapOutId(meeting.getId());
			result.setData(wrap);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "同意会议预定.", response = WrapOutMeeting.class)
	@GET
	@Path("{id}/confirm/allow")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response confirmAllow(@Context HttpServletRequest request, @PathParam("id") String id) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		WrapOutId wrap = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			EffectivePerson effectivePerson = this.effectivePerson(request);
			Meeting meeting = emc.find(id, Meeting.class, ExceptionWhen.not_found);
			Room room = emc.find(meeting.getRoom(), Room.class, ExceptionWhen.not_found);
			business.roomEditAvailable(effectivePerson, room, ExceptionWhen.not_allow);
			emc.beginTransaction(Meeting.class);
			if (!business.room().checkIdle(meeting.getRoom(), meeting.getStartTime(), meeting.getCompletedTime(),
					meeting.getId())) {
				throw new JaxrsBusinessLogicException("会议室:" + room.getName() + ", 已经预约.");
			}
			meeting.setConfirmStatus(ConfirmStatus.allow);
			emc.check(meeting, CheckPersistType.all);
			emc.commit();
			wrap = new WrapOutId(meeting.getId());
			result.setData(wrap);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "拒绝会议预定.", response = WrapOutMeeting.class)
	@GET
	@Path("{id}/confirm/deny")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response confirmDeny(@Context HttpServletRequest request, @PathParam("id") String id) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		WrapOutId wrap = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			EffectivePerson effectivePerson = this.effectivePerson(request);
			Meeting meeting = emc.find(id, Meeting.class, ExceptionWhen.not_found);
			Room room = emc.find(meeting.getRoom(), Room.class, ExceptionWhen.not_found);
			business.roomEditAvailable(effectivePerson, room, ExceptionWhen.not_allow);
			emc.beginTransaction(Meeting.class);
			if (!business.room().checkIdle(meeting.getRoom(), meeting.getStartTime(), meeting.getCompletedTime(),
					meeting.getId())) {
				throw new JaxrsBusinessLogicException("会议室:" + room.getName() + ", 已经预约.");
			}
			meeting.setConfirmStatus(ConfirmStatus.deny);
			emc.check(meeting, CheckPersistType.all);
			emc.commit();
			wrap = new WrapOutId(meeting.getId());
			result.setData(wrap);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "同意参加会议.", response = WrapOutId.class)
	@GET
	@Path("{id}/accept")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response accpet(@Context HttpServletRequest request, @PathParam("id") String id) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		WrapOutId wrap = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			EffectivePerson effectivePerson = this.effectivePerson(request);
			Meeting meeting = emc.find(id, Meeting.class, ExceptionWhen.not_found);
			/* 在被邀请参加的人员之内 */
			if (meeting.getInvitePersonList().contains(effectivePerson.getName())) {
				emc.beginTransaction(Meeting.class);
				ListTools.addWithProperty(meeting, "acceptPersonList", true, effectivePerson.getName());
				business.organization().person().checkNameList(meeting, "acceptPersonList", true);
				ListTools.subtractWithProperty(meeting, "rejectPersonList", effectivePerson.getName());
				business.organization().person().checkNameList(meeting, "rejectPersonList", true);
				emc.check(meeting, CheckPersistType.all);
				emc.commit();
				this.notifyMeetingAcceptMessage(business, meeting, effectivePerson.getName());
			}
			wrap = new WrapOutId(meeting.getId());
			result.setData(wrap);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "拒绝参加会议.", response = WrapOutId.class)
	@GET
	@Path("{id}/reject")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response reject(@Context HttpServletRequest request, @PathParam("id") String id) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		WrapOutId wrap = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			EffectivePerson effectivePerson = this.effectivePerson(request);
			Meeting meeting = emc.find(id, Meeting.class, ExceptionWhen.not_found);
			/* 在被邀请参加的人员之内 */
			if (meeting.getInvitePersonList().contains(effectivePerson.getName())) {
				emc.beginTransaction(Meeting.class);
				ListTools.addWithProperty(meeting, "rejectPersonList", true, effectivePerson.getName());
				business.organization().person().checkNameList(meeting, "acceptPersonList", true);
				ListTools.subtractWithProperty(meeting, "acceptPersonList", effectivePerson.getName());
				business.organization().person().checkNameList(meeting, "rejectPersonList", true);
				emc.check(meeting, CheckPersistType.all);
				emc.commit();
				this.notifyMeetingRejectMessage(business, meeting, effectivePerson.getName());
			}
			wrap = new WrapOutId(meeting.getId());
			result.setData(wrap);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "手动结束Meeting.", response = WrapOutMeeting.class)
	@GET
	@Path("{id}/manual/completed")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response manualCompleted(@Context HttpServletRequest request, @PathParam("id") String id) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		WrapOutId wrap = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			EffectivePerson effectivePerson = this.effectivePerson(request);
			Meeting meeting = emc.find(id, Meeting.class, ExceptionWhen.not_found);
			business.meetingEditAvailable(effectivePerson, meeting, ExceptionWhen.not_allow);
			emc.beginTransaction(Meeting.class);
			meeting.setManualCompleted(true);
			emc.check(meeting, CheckPersistType.all);
			emc.commit();
			wrap = new WrapOutId(meeting.getId());
			result.setData(wrap);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "添加新的的邀请人.", response = WrapOutMeeting.class)
	@PUT
	@Path("{id}/add/invite")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response addInvite(@Context HttpServletRequest request, @PathParam("id") String id, WrapInMeeting wrapIn) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		WrapOutId wrap = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			EffectivePerson effectivePerson = this.effectivePerson(request);
			Meeting meeting = emc.find(id, Meeting.class, ExceptionWhen.not_found);
			business.meetingEditAvailable(effectivePerson, meeting, ExceptionWhen.not_allow);
			emc.beginTransaction(Meeting.class);
			// List<String> adds = ListTools.add(meeting, "invitePersonList",
			// true, wrapIn.getInvitePersonList());
			ListTools.addWithProperty(meeting, "invitePersonList", true, wrapIn.getInvitePersonList());
			business.organization().person().checkNameList(meeting, "invitePersonList", true);
			ListTools.subtractWithProperty(meeting, "invitePersonList", meeting.getApplicant());
			emc.check(meeting, CheckPersistType.all);
			emc.commit();
			if (ConfirmStatus.allow.equals(meeting.getConfirmStatus())) {
				this.notifyMeetingInviteMessage(business, meeting);
			}
			wrap = new WrapOutId(meeting.getId());
			result.setData(wrap);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "列示Meeting对象,下一页.仅管理员可用", response = WrapOutMeeting.class)
	@GET
	@Path("list/{id}/next/{count}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response standardListNext(@Context HttpServletRequest request, @PathParam("id") String id,
			@PathParam("count") Integer count) {
		ActionResult<List<WrapOutMeeting>> result = new ActionResult<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			EffectivePerson effectivePerson = this.effectivePerson(request);
			business.isManager(effectivePerson, ExceptionWhen.not_allow);
			result = this.standardListNext(WrapTools.meetingOutCopier, id, count, "sequence", null, null, null, null,
					null, null, null, true, DESC);
			WrapTools.decorate(result.getData(), effectivePerson);
			WrapTools.setAttachment(business, result.getData());
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "列示Meeing对象,上一页.仅管理员可用", response = WrapOutMeeting.class)
	@GET
	@Path("list/{id}/prev/{count}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response standardListPrev(@Context HttpServletRequest request, @PathParam("id") String id,
			@PathParam("count") Integer count) {
		ActionResult<List<WrapOutMeeting>> result = new ActionResult<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			EffectivePerson effectivePerson = this.effectivePerson(request);
			business.isManager(effectivePerson, ExceptionWhen.not_allow);
			result = this.standardListPrev(WrapTools.meetingOutCopier, id, count, "sequence", null, null, null, null,
					null, null, null, true, DESC);
			WrapTools.decorate(result.getData(), effectivePerson);
			WrapTools.setAttachment(business, result.getData());
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "列示我参与从当前日期开始指定月份范围的会议，或者被邀请，或者是申请人,或者是审核人，管理员可以看到所有.", response = WrapOutMeeting.class)
	@GET
	@Path("list/coming/month/{count}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listComingMonth(@Context HttpServletRequest request, @PathParam("count") Integer count) {
		ActionResult<List<WrapOutMeeting>> result = new ActionResult<>();
		List<WrapOutMeeting> wraps = new ArrayList<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			EffectivePerson effectivePerson = this.effectivePerson(request);
			Calendar calendar = Calendar.getInstance();
			Date start = calendar.getTime();
			calendar.add(Calendar.MONTH, count);
			Date end = calendar.getTime();
			List<String> ids = new ArrayList<>();
			if (business.isManager(effectivePerson)) {
				ids = business.meeting().listWithDate(start, end);
			} else {
				ids = business.meeting().listWithPersonWithDate(effectivePerson.getName(), start, end);
			}
			wraps = WrapTools.meetingOutCopier.copy(emc.list(Meeting.class, ids));
			WrapTools.decorate(wraps, effectivePerson);
			WrapTools.setAttachment(business, wraps);
			SortTools.asc(wraps, false, "startTime");
			result.setData(wraps);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "列示我参与从当前日期开始指定日期范围的会议，或者被邀请，或者是申请人,或者是审核人，管理员可以看到所有.", response = WrapOutMeeting.class)
	@GET
	@Path("list/coming/day/{count}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listComingDay(@Context HttpServletRequest request, @PathParam("count") Integer count) {
		ActionResult<List<WrapOutMeeting>> result = new ActionResult<>();
		List<WrapOutMeeting> wraps = new ArrayList<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			EffectivePerson effectivePerson = this.effectivePerson(request);
			Calendar calendar = Calendar.getInstance();
			Date start = calendar.getTime();
			calendar.add(Calendar.DAY_OF_MONTH, count);
			Date end = calendar.getTime();
			List<String> ids = new ArrayList<>();
			if (business.isManager(effectivePerson)) {
				ids = business.meeting().listWithDate(start, end);
			} else {
				ids = business.meeting().listWithPersonWithDate(effectivePerson.getName(), start, end);
			}
			wraps = WrapTools.meetingOutCopier.copy(emc.list(Meeting.class, ids));
			WrapTools.decorate(wraps, effectivePerson);
			WrapTools.setAttachment(business, wraps);
			SortTools.asc(wraps, false, "startTime");
			result.setData(wraps);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "列示我参与的指定月份的会议，或者被邀请，或者是申请人,或者是审核人，管理员可以看到所有.", response = WrapOutMeeting.class)
	@GET
	@Path("list/year/{year}/month/{month}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listOnMonth(@Context HttpServletRequest request, @PathParam("year") Integer year,
			@PathParam("month") Integer month) {
		ActionResult<List<WrapOutMeeting>> result = new ActionResult<>();
		List<WrapOutMeeting> wraps = new ArrayList<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			EffectivePerson effectivePerson = this.effectivePerson(request);
			Calendar calendar = Calendar.getInstance();
			calendar.set(Calendar.DAY_OF_MONTH, 1);
			calendar.set(Calendar.YEAR, year);
			calendar.set(Calendar.MONTH, month - 1);
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MILLISECOND, 0);
			Date start = calendar.getTime();
			calendar.add(Calendar.MONTH, 1);
			calendar.add(Calendar.MILLISECOND, -1);
			Date end = calendar.getTime();
			List<String> ids = new ArrayList<>();
			if (business.isManager(effectivePerson)) {
				ids = business.meeting().listWithDate(start, end);
			} else {
				ids = business.meeting().listWithPersonWithDate(effectivePerson.getName(), start, end);
			}
			wraps = WrapTools.meetingOutCopier.copy(emc.list(Meeting.class, ids));
			WrapTools.decorate(wraps, effectivePerson);
			WrapTools.setAttachment(business, wraps);
			SortTools.asc(wraps, false, "startTime");
			result.setData(wraps);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "列示我参与的指定日期的会议，或者被邀请，或者是申请人,或者是审核人，管理员可以看到所有.", response = WrapOutMeeting.class)
	@GET
	@Path("list/year/{year}/month/{month}/day/{day}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listOnDay(@Context HttpServletRequest request, @PathParam("year") Integer year,
			@PathParam("month") Integer month, @PathParam("day") Integer day) {
		ActionResult<List<WrapOutMeeting>> result = new ActionResult<>();
		List<WrapOutMeeting> wraps = new ArrayList<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			EffectivePerson effectivePerson = this.effectivePerson(request);
			Calendar calendar = Calendar.getInstance();
			calendar.set(Calendar.YEAR, year);
			calendar.set(Calendar.MONTH, month - 1);
			calendar.set(Calendar.DAY_OF_MONTH, day);
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MILLISECOND, 0);
			Date start = calendar.getTime();
			calendar.add(Calendar.DAY_OF_MONTH, 1);
			calendar.add(Calendar.MILLISECOND, -1);
			Date end = calendar.getTime();
			List<String> ids = new ArrayList<>();
			if (business.isManager(effectivePerson)) {
				ids = business.meeting().listWithDate(start, end);
			} else {
				ids = business.meeting().listWithPersonWithDate(effectivePerson.getName(), start, end);
			}
			wraps = WrapTools.meetingOutCopier.copy(emc.list(Meeting.class, ids));
			WrapTools.decorate(wraps, effectivePerson);
			WrapTools.setAttachment(business, wraps);
			SortTools.asc(wraps, false, "startTime");
			result.setData(wraps);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "列示等待我确认的会议.", response = WrapOutMeeting.class)
	@GET
	@Path("list/wait/confirm")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listWaitConfirm(@Context HttpServletRequest request) {
		ActionResult<List<WrapOutMeeting>> result = new ActionResult<>();
		List<WrapOutMeeting> wraps = new ArrayList<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			EffectivePerson effectivePerson = this.effectivePerson(request);
			List<String> ids = new ArrayList<>();
			if (business.isManager(effectivePerson)) {
				ids = business.meeting().listWaitConfirm();
			} else {
				ids = business.meeting().listWithPersonWaitConfirm(effectivePerson.getName());
			}
			wraps = WrapTools.meetingOutCopier.copy(emc.list(Meeting.class, ids));
			WrapTools.decorate(wraps, effectivePerson);
			WrapTools.setAttachment(business, wraps);
			SortTools.asc(wraps, false, "startTime");
			result.setData(wraps);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "等待我确认是否参加的会议.", response = WrapOutMeeting.class)
	@GET
	@Path("list/wait/accept")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listWaitAccept(@Context HttpServletRequest request) {
		ActionResult<List<WrapOutMeeting>> result = new ActionResult<>();
		List<WrapOutMeeting> wraps = new ArrayList<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			EffectivePerson effectivePerson = this.effectivePerson(request);
			List<String> ids = new ArrayList<>();
			ids = business.meeting().listWithPersonWaitAccept(effectivePerson.getName());
			wraps = WrapTools.meetingOutCopier.copy(emc.list(Meeting.class, ids));
			WrapTools.decorate(wraps, effectivePerson);
			WrapTools.setAttachment(business, wraps);
			SortTools.asc(wraps, false, "startTime");
			result.setData(wraps);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "列示我申请的还未开始的会议.", response = WrapOutMeeting.class)
	@GET
	@Path("list/applied/wait")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listAppliedWait(@Context HttpServletRequest request) {
		ActionResult<List<WrapOutMeeting>> result = new ActionResult<>();
		List<WrapOutMeeting> wraps = new ArrayList<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			EffectivePerson effectivePerson = this.effectivePerson(request);
			List<String> ids = new ArrayList<>();
			ids = business.meeting().listWithAppliedWait(effectivePerson.getName());
			wraps = WrapTools.meetingOutCopier.copy(emc.list(Meeting.class, ids));
			WrapTools.decorate(wraps, effectivePerson);
			WrapTools.setAttachment(business, wraps);
			SortTools.asc(wraps, false, "startTime");
			result.setData(wraps);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "列示我申请的正在进行中的会议.", response = WrapOutMeeting.class)
	@GET
	@Path("list/applied/processing")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listAppliedProcessing(@Context HttpServletRequest request) {
		ActionResult<List<WrapOutMeeting>> result = new ActionResult<>();
		List<WrapOutMeeting> wraps = new ArrayList<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			EffectivePerson effectivePerson = this.effectivePerson(request);
			List<String> ids = new ArrayList<>();
			ids = business.meeting().listWithAppliedProcessing(effectivePerson.getName());
			wraps = WrapTools.meetingOutCopier.copy(emc.list(Meeting.class, ids));
			WrapTools.decorate(wraps, effectivePerson);
			WrapTools.setAttachment(business, wraps);
			SortTools.asc(wraps, false, "startTime");
			result.setData(wraps);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "列示我申请的已经结束的会议.", response = WrapOutMeeting.class)
	@GET
	@Path("list/applied/completed")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listAppliedCompleted(@Context HttpServletRequest request) {
		ActionResult<List<WrapOutMeeting>> result = new ActionResult<>();
		List<WrapOutMeeting> wraps = new ArrayList<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			EffectivePerson effectivePerson = this.effectivePerson(request);
			List<String> ids = new ArrayList<>();
			ids = business.meeting().listWithAppliedCompleted(effectivePerson.getName());
			wraps = WrapTools.meetingOutCopier.copy(emc.list(Meeting.class, ids));
			WrapTools.decorate(wraps, effectivePerson);
			WrapTools.setAttachment(business, wraps);
			SortTools.asc(wraps, false, "startTime");
			result.setData(wraps);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "列示我受到邀请的还未开始的会议.", response = WrapOutMeeting.class)
	@GET
	@Path("list/invited/wait")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response lisInvitedWait(@Context HttpServletRequest request) {
		ActionResult<List<WrapOutMeeting>> result = new ActionResult<>();
		List<WrapOutMeeting> wraps = new ArrayList<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			EffectivePerson effectivePerson = this.effectivePerson(request);
			List<String> ids = new ArrayList<>();
			ids = business.meeting().listWithInvitedWait(effectivePerson.getName());
			wraps = WrapTools.meetingOutCopier.copy(emc.list(Meeting.class, ids));
			WrapTools.decorate(wraps, effectivePerson);
			WrapTools.setAttachment(business, wraps);
			SortTools.asc(wraps, false, "startTime");
			result.setData(wraps);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "列示我受到邀请的正在进行中的会议.", response = WrapOutMeeting.class)
	@GET
	@Path("list/invited/processing")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response lisInvitedProcessing(@Context HttpServletRequest request) {
		ActionResult<List<WrapOutMeeting>> result = new ActionResult<>();
		List<WrapOutMeeting> wraps = new ArrayList<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			EffectivePerson effectivePerson = this.effectivePerson(request);
			List<String> ids = new ArrayList<>();
			ids = business.meeting().listWithInvitedProcessing(effectivePerson.getName());
			wraps = WrapTools.meetingOutCopier.copy(emc.list(Meeting.class, ids));
			WrapTools.decorate(wraps, effectivePerson);
			WrapTools.setAttachment(business, wraps);
			SortTools.asc(wraps, false, "startTime");
			result.setData(wraps);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "列示我受到邀请的正在进行中的会议.", response = WrapOutMeeting.class)
	@GET
	@Path("list/invited/completed")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response lisInvitedCompleted(@Context HttpServletRequest request) {
		ActionResult<List<WrapOutMeeting>> result = new ActionResult<>();
		List<WrapOutMeeting> wraps = new ArrayList<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			EffectivePerson effectivePerson = this.effectivePerson(request);
			List<String> ids = new ArrayList<>();
			ids = business.meeting().listWithInvitedCompleted(effectivePerson.getName());
			wraps = WrapTools.meetingOutCopier.copy(emc.list(Meeting.class, ids));
			WrapTools.decorate(wraps, effectivePerson);
			WrapTools.setAttachment(business, wraps);
			SortTools.asc(wraps, false, "startTime");
			result.setData(wraps);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "列示我受到邀请的已经拒绝的会议.", response = WrapOutMeeting.class)
	@GET
	@Path("list/invited/rejected")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response lisInvitedRejected(@Context HttpServletRequest request) {
		ActionResult<List<WrapOutMeeting>> result = new ActionResult<>();
		List<WrapOutMeeting> wraps = new ArrayList<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			EffectivePerson effectivePerson = this.effectivePerson(request);
			List<String> ids = new ArrayList<>();
			ids = business.meeting().listWithInvitedRejected(effectivePerson.getName());
			wraps = WrapTools.meetingOutCopier.copy(emc.list(Meeting.class, ids));
			WrapTools.decorate(wraps, effectivePerson);
			WrapTools.setAttachment(business, wraps);
			SortTools.asc(wraps, false, "startTime");
			result.setData(wraps);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	private void notifyMeetingInviteMessage(Business business, Meeting meeting) throws Exception {
		if (ListTools.isNotEmpty(meeting.getInvitePersonList())) {
			Room room = business.entityManagerContainer().find(meeting.getRoom(), Room.class, ExceptionWhen.not_found);
			Building building = business.entityManagerContainer().find(room.getBuilding(), Building.class,
					ExceptionWhen.not_found);
			for (String str : ListTools.nullToEmpty(meeting.getInvitePersonList())) {
				MeetingInviteMessage message = new MeetingInviteMessage(str, building.getId(), room.getId(),
						meeting.getId());
				Collaboration.send(message);
			}
		}
	}

	private void notifyMeetingCancelMessage(Business business, Meeting meeting) throws Exception {
		if (ListTools.isNotEmpty(meeting.getInvitePersonList())) {
			Room room = business.entityManagerContainer().find(meeting.getRoom(), Room.class, ExceptionWhen.not_found);
			Building building = business.entityManagerContainer().find(room.getBuilding(), Building.class,
					ExceptionWhen.not_found);
			for (String str : ListTools.concreteArrayList(meeting.getInvitePersonList(), true, true,
					meeting.getApplicant())) {
				// Collaboration.notification(str, "会议取消提醒.", "会议已经取消:" +
				// meeting.getSubject(),
				// "会议室:" + room.getName() + ",会议地点:" + building.getName() +
				// building.getAddress() + ".",
				// "meetingReject");
				MeetingCancelMessage message = new MeetingCancelMessage(str, building.getId(), room.getId(),
						meeting.getId());
				Collaboration.send(message);
			}
		}
	}

	private void notifyMeetingAcceptMessage(Business business, Meeting meeting, String person) throws Exception {
		Room room = business.entityManagerContainer().find(meeting.getRoom(), Room.class, ExceptionWhen.not_found);
		Building building = business.entityManagerContainer().find(room.getBuilding(), Building.class,
				ExceptionWhen.not_found);
		for (String str : ListTools.concreteArrayList(meeting.getInvitePersonList(), true, true,
				meeting.getApplicant())) {
			// Collaboration.notification(str, "会议接受提醒.", person + "接受会议邀请:" +
			// meeting.getSubject(),
			// "会议室:" + room.getName() + ",会议地点:" + building.getName() +
			// building.getAddress() + ".",
			// "meetingAccept");
			MeetingAcceptMessage message = new MeetingAcceptMessage(str, building.getId(), room.getId(),
					meeting.getId());
			Collaboration.send(message);
		}

	}

	private void notifyMeetingRejectMessage(Business business, Meeting meeting, String person) throws Exception {
		Room room = business.entityManagerContainer().find(meeting.getRoom(), Room.class, ExceptionWhen.not_found);
		Building building = business.entityManagerContainer().find(room.getBuilding(), Building.class,
				ExceptionWhen.not_found);
		for (String str : ListTools.concreteArrayList(meeting.getInvitePersonList(), true, true,
				meeting.getApplicant())) {
			// Collaboration.notification(str, "会议拒绝提醒.", person + "拒绝会议邀请:" +
			// meeting.getSubject(),
			// "会议室:" + room.getName() + ",会议地点:" + building.getName() +
			// building.getAddress() + ".",
			// "meetingReject");
			MeetingRejectMessage message = new MeetingRejectMessage(str, building.getId(), room.getId(),
					meeting.getId());
			Collaboration.send(message);
		}
	}

}
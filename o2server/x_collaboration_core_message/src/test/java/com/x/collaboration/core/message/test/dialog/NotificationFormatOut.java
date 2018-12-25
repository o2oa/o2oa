package com.x.collaboration.core.message.test.dialog;

import com.x.collaboration.core.message.notification.AttendanceAppealAcceptMessage;
import com.x.collaboration.core.message.notification.AttendanceAppealCancelMessage;
import com.x.collaboration.core.message.notification.AttendanceAppealInviteMessage;
import com.x.collaboration.core.message.notification.AttendanceAppealRejectMessage;
import com.x.collaboration.core.message.notification.FileModifyMessage;
import com.x.collaboration.core.message.notification.FileShareMessage;
import com.x.collaboration.core.message.notification.MeetingAcceptMessage;
import com.x.collaboration.core.message.notification.MeetingCancelMessage;
import com.x.collaboration.core.message.notification.MeetingInviteMessage;
import com.x.collaboration.core.message.notification.MeetingRejectMessage;
import com.x.collaboration.core.message.notification.OkrCenterWorkDeployAcceptMessage;
import com.x.collaboration.core.message.notification.OkrWorkDeletedAcceptMessage;
import com.x.collaboration.core.message.notification.OkrWorkDeployAcceptMessage;
import com.x.collaboration.core.message.notification.OkrWorkGetAcceptMessage;
import com.x.collaboration.core.message.notification.OkrWorkReportDeletedAcceptMessage;
import com.x.collaboration.core.message.notification.ReadMessage;
import com.x.collaboration.core.message.notification.ReviewMessage;
import com.x.collaboration.core.message.notification.TaskMessage;

public class NotificationFormatOut {
	private AttendanceAppealAcceptMessage attendanceAppealAcceptMessage = new AttendanceAppealAcceptMessage("接收人",
			"考勤申述id", "考勤明细id", "内容");
	private AttendanceAppealCancelMessage attendanceAppealCancelMessage = new AttendanceAppealCancelMessage("接收人",
			"考勤申述id", "考勤明细id", "内容");
	private AttendanceAppealInviteMessage attendanceAppealInviteMessage = new AttendanceAppealInviteMessage("接收人",
			"考勤申述id", "考勤明细id", "内容");
	private AttendanceAppealRejectMessage attendanceAppealRejectMessage = new AttendanceAppealRejectMessage("接收人",
			"考勤申述id", "考勤明细id", "内容");
	private FileModifyMessage fileModifyMessage = new FileModifyMessage("接收人", "文件id");
	private FileShareMessage fileShareMessage = new FileShareMessage("接收人", "文件id");
	private MeetingAcceptMessage meetingAcceptMessage = new MeetingAcceptMessage("接收人", "楼房id", "会议室id", "会议id");
	private MeetingCancelMessage meetingCancelMessage = new MeetingCancelMessage("接收人", "楼房id", "会议室id", "会议id");
	private MeetingInviteMessage meetingInviteMessage = new MeetingInviteMessage("接收人", "楼房id", "会议室id", "会议id");
	private MeetingRejectMessage meetingRejectMessage = new MeetingRejectMessage("接收人", "楼房id", "会议室id", "会议id");
	private OkrCenterWorkDeployAcceptMessage okrCenterWorkDeployAcceptMessage = new OkrCenterWorkDeployAcceptMessage(
			"接收人", "中心工作id", "工作名称", "内容");
	private OkrWorkDeletedAcceptMessage okrWorkDeletedAcceptMessage = new OkrWorkDeletedAcceptMessage("接收人", "工作id",
			"工作名称", "内容");
	private OkrWorkDeployAcceptMessage okrWorkDeployAcceptMessage = new OkrWorkDeployAcceptMessage("接收人", "工作id",
			"工作名称", "内容");
	private OkrWorkGetAcceptMessage okrWorkGetAcceptMessage = new OkrWorkGetAcceptMessage("接收人", "工作id", "工作名称", "内容");
	private OkrWorkReportDeletedAcceptMessage okrWorkReportDeletedAcceptMessage = new OkrWorkReportDeletedAcceptMessage(
			"接收人", "工作汇报id", "工作汇报名称", "内容");
	private ReadMessage readMessage = new ReadMessage("接收人", "workId", "待阅id");
	private ReviewMessage reviewMessage = new ReviewMessage("接收人", "workId", "reviewId");
	private TaskMessage taskMessage = new TaskMessage("接收人", "workId", "待办id");

	public AttendanceAppealAcceptMessage getAttendanceAppealAcceptMessage() {
		return attendanceAppealAcceptMessage;
	}

	public void setAttendanceAppealAcceptMessage(AttendanceAppealAcceptMessage attendanceAppealAcceptMessage) {
		this.attendanceAppealAcceptMessage = attendanceAppealAcceptMessage;
	}

	public AttendanceAppealCancelMessage getAttendanceAppealCancelMessage() {
		return attendanceAppealCancelMessage;
	}

	public void setAttendanceAppealCancelMessage(AttendanceAppealCancelMessage attendanceAppealCancelMessage) {
		this.attendanceAppealCancelMessage = attendanceAppealCancelMessage;
	}

	public AttendanceAppealInviteMessage getAttendanceAppealInviteMessage() {
		return attendanceAppealInviteMessage;
	}

	public void setAttendanceAppealInviteMessage(AttendanceAppealInviteMessage attendanceAppealInviteMessage) {
		this.attendanceAppealInviteMessage = attendanceAppealInviteMessage;
	}

	public AttendanceAppealRejectMessage getAttendanceAppealRejectMessage() {
		return attendanceAppealRejectMessage;
	}

	public void setAttendanceAppealRejectMessage(AttendanceAppealRejectMessage attendanceAppealRejectMessage) {
		this.attendanceAppealRejectMessage = attendanceAppealRejectMessage;
	}

	public FileModifyMessage getFileModifyMessage() {
		return fileModifyMessage;
	}

	public void setFileModifyMessage(FileModifyMessage fileModifyMessage) {
		this.fileModifyMessage = fileModifyMessage;
	}

	public FileShareMessage getFileShareMessage() {
		return fileShareMessage;
	}

	public void setFileShareMessage(FileShareMessage fileShareMessage) {
		this.fileShareMessage = fileShareMessage;
	}

	public MeetingAcceptMessage getMeetingAcceptMessage() {
		return meetingAcceptMessage;
	}

	public void setMeetingAcceptMessage(MeetingAcceptMessage meetingAcceptMessage) {
		this.meetingAcceptMessage = meetingAcceptMessage;
	}

	public MeetingCancelMessage getMeetingCancelMessage() {
		return meetingCancelMessage;
	}

	public void setMeetingCancelMessage(MeetingCancelMessage meetingCancelMessage) {
		this.meetingCancelMessage = meetingCancelMessage;
	}

	public MeetingInviteMessage getMeetingInviteMessage() {
		return meetingInviteMessage;
	}

	public void setMeetingInviteMessage(MeetingInviteMessage meetingInviteMessage) {
		this.meetingInviteMessage = meetingInviteMessage;
	}

	public MeetingRejectMessage getMeetingRejectMessage() {
		return meetingRejectMessage;
	}

	public void setMeetingRejectMessage(MeetingRejectMessage meetingRejectMessage) {
		this.meetingRejectMessage = meetingRejectMessage;
	}

	public OkrCenterWorkDeployAcceptMessage getOkrCenterWorkDeployAcceptMessage() {
		return okrCenterWorkDeployAcceptMessage;
	}

	public void setOkrCenterWorkDeployAcceptMessage(OkrCenterWorkDeployAcceptMessage okrCenterWorkDeployAcceptMessage) {
		this.okrCenterWorkDeployAcceptMessage = okrCenterWorkDeployAcceptMessage;
	}

	public OkrWorkDeletedAcceptMessage getOkrWorkDeletedAcceptMessage() {
		return okrWorkDeletedAcceptMessage;
	}

	public void setOkrWorkDeletedAcceptMessage(OkrWorkDeletedAcceptMessage okrWorkDeletedAcceptMessage) {
		this.okrWorkDeletedAcceptMessage = okrWorkDeletedAcceptMessage;
	}

	public OkrWorkDeployAcceptMessage getOkrWorkDeployAcceptMessage() {
		return okrWorkDeployAcceptMessage;
	}

	public void setOkrWorkDeployAcceptMessage(OkrWorkDeployAcceptMessage okrWorkDeployAcceptMessage) {
		this.okrWorkDeployAcceptMessage = okrWorkDeployAcceptMessage;
	}

	public OkrWorkGetAcceptMessage getOkrWorkGetAcceptMessage() {
		return okrWorkGetAcceptMessage;
	}

	public void setOkrWorkGetAcceptMessage(OkrWorkGetAcceptMessage okrWorkGetAcceptMessage) {
		this.okrWorkGetAcceptMessage = okrWorkGetAcceptMessage;
	}

	public OkrWorkReportDeletedAcceptMessage getOkrWorkReportDeletedAcceptMessage() {
		return okrWorkReportDeletedAcceptMessage;
	}

	public void setOkrWorkReportDeletedAcceptMessage(
			OkrWorkReportDeletedAcceptMessage okrWorkReportDeletedAcceptMessage) {
		this.okrWorkReportDeletedAcceptMessage = okrWorkReportDeletedAcceptMessage;
	}

	public ReadMessage getReadMessage() {
		return readMessage;
	}

	public void setReadMessage(ReadMessage readMessage) {
		this.readMessage = readMessage;
	}

	public ReviewMessage getReviewMessage() {
		return reviewMessage;
	}

	public void setReviewMessage(ReviewMessage reviewMessage) {
		this.reviewMessage = reviewMessage;
	}

	public TaskMessage getTaskMessage() {
		return taskMessage;
	}

	public void setTaskMessage(TaskMessage taskMessage) {
		this.taskMessage = taskMessage;
	}

}

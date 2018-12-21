package com.x.collaboration.core.message.notification;

import com.x.base.core.entity.JpaObject;

public enum NotificationType {
	chat, task, read, review, plain, fileShare, fileModify, broadcast, meetingInvite, meetingCancel, meetingAccept, meetingReject, attendanceAppealReject, attendanceAppealInvite, attendanceAppealCancel, attendanceAppealAccept, okrCenterWorkDeployAccept, okrWorkDeployAccept, okrWorkGetAccept, okrWorkDeletedAccept, okrWorkReportDeletedAccept;
	public static final int length = JpaObject.length_64B;
}

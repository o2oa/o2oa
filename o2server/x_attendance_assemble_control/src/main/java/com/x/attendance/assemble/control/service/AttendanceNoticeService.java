package com.x.attendance.assemble.control.service;

import com.x.attendance.entity.AttendanceAppealInfo;

public class AttendanceNoticeService {

	/**
	 * 申诉通过，通知发起人
	 * 
	 * @param business
	 * @param attendanceDetail
	 * @param person
	 * @throws Exception
	 */
	public void notifyAttendanceAppealAcceptMessage(AttendanceAppealInfo attendanceAppealInfo, String person)
			throws Exception {
		if (attendanceAppealInfo != null) {
			String targetName = attendanceAppealInfo.getEmpName();
			String messageContent = "您好，[" + attendanceAppealInfo.getRecordDateString() + "]考勤结果的申诉已经通过审核。";
			if (targetName != null && !targetName.isEmpty()) {
				String[] array = targetName.split(",");
				for (String name : array) {
					if (name != null && !name.trim().isEmpty()) {
						// @Todo
//						AttendanceAppealAcceptMessage message = new AttendanceAppealAcceptMessage( name, attendanceAppealInfo.getId(), attendanceAppealInfo.getDetailId(), messageContent );
//						Collaboration.send(message);
					}
				}
			}
		} else {
			throw new Exception("attendanceAppealInfo is null, can not send message!");
		}
	}

	/**
	 * 申诉不通过，通知发起人
	 * 
	 * @param business
	 * @param meeting
	 * @param person
	 * @throws Exception
	 */
	public void notifyAttendanceAppealRejectMessage(AttendanceAppealInfo attendanceAppealInfo, String person)
			throws Exception {
		if (attendanceAppealInfo != null) {
			String targetName = attendanceAppealInfo.getEmpName();
			String messageContent = "您好，[" + attendanceAppealInfo.getRecordDateString() + "]考勤结果的申诉未通过审核。";
			if (targetName != null && !targetName.isEmpty()) {
				String[] array = targetName.split(",");
				for (String name : array) {
					if (name != null && !name.trim().isEmpty()) {
//						AttendanceAppealRejectMessage message = new AttendanceAppealRejectMessage(name,
//								attendanceAppealInfo.getId(), attendanceAppealInfo.getDetailId(), messageContent);
//						Collaboration.send(message);
					}
				}
			}
		} else {
			throw new Exception("attendanceAppealInfo is null, can not send message!");
		}
	}

	/**
	 * 收到一份需要处理的申诉-处理人1
	 * 
	 * @param business
	 * @param attendanceDetail
	 * @throws Exception
	 */
	public void notifyAttendanceAppealProcessness1Message(AttendanceAppealInfo attendanceAppealInfo) throws Exception {
		if (attendanceAppealInfo != null) {
			String targetName = attendanceAppealInfo.getCurrentProcessor();
			String messageContent = "您收到了" + attendanceAppealInfo.getEmpName() + "的考勤结果申诉，申诉类型为["
					+ attendanceAppealInfo.getAppealReason() + "]，请您审核！";
			if (targetName != null && !targetName.isEmpty()) {
				String[] array = targetName.split(",");
				for (String name : array) {
					if (name != null && !name.trim().isEmpty()) {
//						AttendanceAppealInviteMessage message = new AttendanceAppealInviteMessage(name,
//								attendanceAppealInfo.getId(), attendanceAppealInfo.getDetailId(), messageContent);
//						Collaboration.send(message);
					}
				}
			}
		} else {
			throw new Exception("attendanceAppealInfo is null, can not send message!");
		}
	}

	/**
	 * 收到一份需要处理的申诉-处理人2
	 * 
	 * @param business
	 * @param attendanceDetail
	 * @throws Exception
	 */
	public void notifyAttendanceAppealProcessness2Message(AttendanceAppealInfo attendanceAppealInfo) throws Exception {
		if (attendanceAppealInfo != null) {
			String targetName = attendanceAppealInfo.getCurrentProcessor();
			String messageContent = "您收到了" + attendanceAppealInfo.getEmpName() + "的考勤结果申诉，申诉类型为["
					+ attendanceAppealInfo.getAppealReason() + "]，请您复核！";
			if (targetName != null && !targetName.isEmpty()) {
				String[] array = targetName.split(",");
				for (String name : array) {
					if (name != null && !name.trim().isEmpty()) {
//						AttendanceAppealInviteMessage message = new AttendanceAppealInviteMessage(name,
//								attendanceAppealInfo.getId(), attendanceAppealInfo.getDetailId(), messageContent);
//						Collaboration.send(message);
					}
				}
			}
		} else {
			throw new Exception("attendanceAppealInfo is null, can not send message!");
		}
	}
}

package com.x.attendance.assemble.control.jaxrs.attendanceappealinfo;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.attendance.assemble.control.ExceptionWrapInConvert;
import com.x.attendance.entity.AppealConfig;
import com.x.attendance.entity.AttendanceAppealAuditInfo;
import com.x.attendance.entity.AttendanceAppealInfo;
import com.x.attendance.entity.AttendanceDetail;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

public class ActionAppealCreateWithWorkFlow extends BaseAction {
	
	private static  Logger logger = LoggerFactory.getLogger( ActionAppealCreateWithWorkFlow.class );
	
	protected ActionResult<Wo> execute( HttpServletRequest request, EffectivePerson effectivePerson, String id, JsonElement jsonElement ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		AttendanceAppealInfo attendanceAppealInfo = null;
		AttendanceAppealAuditInfo attendanceAppealAuditInfo = null;
		AttendanceDetail attendanceDetail = null;
		Wi wrapIn = null;
		Boolean check = true;

		try {
			wrapIn = this.convertToWrapIn(jsonElement, Wi.class);
			if( StringUtils.isEmpty( wrapIn.getWorkId() )){
				check = false;
				Exception exception = new ExceptionAttendanceAppealProcess("使用流程启动申诉时，工作流的workId不允许为空");
				result.error(exception);
			}
		} catch (Exception e) {
			check = false;
			Exception exception = new ExceptionWrapInConvert(e, jsonElement);
			result.error(exception);
			logger.error(e, effectivePerson, request, null);
		}

		//根据传入的考勤信息ID查询考勤信息对象
		if (check) {
			try {
				attendanceDetail = attendanceDetailServiceAdv.get( id );
				if ( attendanceDetail == null ) {
					check = false;
					Exception exception = new ExceptionAttendanceDetailNotExists(id);
					result.error( exception );
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionAttendanceAppealProcess(e, "系统在根据ID查询员工打卡信息时发生异常！ID:"+id);
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}

		if (check) {
			// 利用打卡记录中的信息，创建一个申诉信息记录
			attendanceAppealInfo = attendanceSettingServiceAdv.composeAppealInfoWithDetailInfo( attendanceDetail, 
					wrapIn.getReason(), wrapIn.getAppealReason(),  wrapIn.getSelfHolidayType(),  wrapIn.getAddress(), 
					wrapIn.getStartTime(),  wrapIn.getEndTime(),  wrapIn.getAppealDescription() );

//			// 申诉状态:0-未申诉，1-申诉中，-1-申诉未通过，9-申诉通过!
//			attendanceAppealInfo.setStatus(wrapIn.getStatus());

			// 创建一个申诉审批记录信息
			attendanceAppealAuditInfo = new AttendanceAppealAuditInfo();
			attendanceAppealAuditInfo.setId( attendanceAppealInfo.getId());
			attendanceAppealAuditInfo.setDetailId( attendanceDetail.getId() );
			attendanceAppealAuditInfo.setAuditFlowType( AppealConfig.APPEAL_AUDIFLOWTYPE_WORKFLOW );
			attendanceAppealAuditInfo.setWorkId( wrapIn.getWorkId());
		}

		//保存申诉信息
		if (check) {
			try {
				attendanceAppealInfo = attendanceAppealInfoServiceAdv.saveNewAppeal( attendanceAppealInfo, attendanceAppealAuditInfo );

				result.setData(new Wo(attendanceAppealInfo.getId()));
			} catch (Exception e) {
				Exception exception = new ExceptionAttendanceAppealProcess(e, "系统在保存考勤申诉信息息时发生异常。");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		return result;
	}
	
	public static class Wi {

		@FieldDescribe("申诉人的身份，考勤人员身份：如果考勤人员属于多个组织，可以选择一个身份进行申诉信息绑定.")
		private String identity = null;

		@FieldDescribe("考勤流程的workId.")
		private String workId = null;

		@FieldDescribe("申诉开始时间")
		private String startTime;

		@FieldDescribe("申诉结束时间")
		private String endTime;

		@FieldDescribe("申诉原因简述（60个汉字）")
		private String appealReason;

		@FieldDescribe("请假类型")
		private String selfHolidayType;

		@FieldDescribe("地址")
		private String address;

		@FieldDescribe("申诉详细事由, 500字")
		private String reason;

		@FieldDescribe("申诉详细说明, 500字")
		private String appealDescription;

//		@FieldDescribe("审批状态:申诉状态:0-未申诉，1-申诉中，-1-申诉未通过，9-申诉通过")
//		private Integer status = 0;

		public String getWorkId() { return workId; }

		public void setWorkId(String workId) { this.workId = workId; }

		public String getIdentity() { return identity; }

		public void setIdentity(String identity) { this.identity = identity; }

		public String getStartTime() { return startTime; }

		public void setStartTime(String startTime) { this.startTime = startTime; }

		public String getEndTime() { return endTime; }

		public void setEndTime(String endTime) { this.endTime = endTime; }

		public String getAppealReason() { return appealReason; }

		public void setAppealReason(String appealReason) { this.appealReason = appealReason; }

		public String getSelfHolidayType() { return selfHolidayType; }

		public void setSelfHolidayType(String selfHolidayType) { this.selfHolidayType = selfHolidayType; }

		public String getAddress() { return address; }

		public void setAddress(String address) { this.address = address; }

		public String getReason() { return reason; }

		public void setReason(String reason) { this.reason = reason; }

		public String getAppealDescription() { return appealDescription; }

		public void setAppealDescription(String appealDescription) { this.appealDescription = appealDescription; }
//
//		public Integer getStatus() { return status; }
//
//		public void setStatus(Integer status) { this.status = status; }
	}
	
	public static class Wo extends WoId {
		public Wo( String id ) {
			setId( id );
		}
	}
}
package com.x.attendance.assemble.control.jaxrs.attendanceappealinfo;

import com.google.gson.JsonElement;
import com.x.attendance.assemble.control.ExceptionWrapInConvert;
import com.x.attendance.entity.AppealConfig;
import com.x.attendance.entity.AttendanceAppealAuditInfo;
import com.x.attendance.entity.AttendanceAppealInfo;
import com.x.attendance.entity.AttendanceDetail;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

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
			if( StringUtils.isEmpty( wrapIn.getJobId() )){
				check = false;
				Exception exception = new ExceptionAttendanceAppealProcess("使用流程启动申诉时，工作流的jobId不允许为空");
				result.error(exception);
			}
		} catch (Exception e) {
			check = false;
			Exception exception = new ExceptionWrapInConvert(e, jsonElement);
			result.error(exception);
			logger.error(e, effectivePerson, request, null);
		}

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
			// 创建一个申诉审批记录信息
			attendanceAppealAuditInfo = new AttendanceAppealAuditInfo();
			attendanceAppealAuditInfo.setId( attendanceAppealInfo.getId());
			attendanceAppealAuditInfo.setDetailId( attendanceDetail.getId() );
			attendanceAppealAuditInfo.setAuditFlowType( AppealConfig.APPEAL_AUDIFLOWTYPE_WORKFLOW );
			attendanceAppealAuditInfo.setWorkId( wrapIn.getJobId());
		}

		//保存申诉信息
		if (check) {
			try {
				attendanceAppealInfo = attendanceAppealInfoServiceAdv.saveNewAppeal( attendanceAppealInfo, attendanceAppealAuditInfo );
				result.setData(new Wo(attendanceAppealInfo.getId()));
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionAttendanceAppealProcess(e, "系统在保存考勤申诉信息息时发生异常。");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		return result;
	}
	
	public static class Wi extends AttendanceAppealInfo {
		
		private static final long serialVersionUID = -5076990764713538973L;
		
		public static List<String> Excludes = new ArrayList<String>(JpaObject.FieldsUnmodify);

		@FieldDescribe("申诉人的身份，考勤人员身份：如果考勤人员属于多个组织，可以选择一个身份进行申诉信息绑定.")
		private String identity = null;

		@FieldDescribe("考勤流程的JOBID.")
		private String jobId = null;

		public String getJobId() { return jobId; }

		public void setJobId(String jobId) { this.jobId = jobId; }

		public String getIdentity() { return identity; }

		public void setIdentity(String identity) { this.identity = identity; }
	}
	
	public static class Wo extends WoId {
		public Wo( String id ) {
			setId( id );
		}
	}
}
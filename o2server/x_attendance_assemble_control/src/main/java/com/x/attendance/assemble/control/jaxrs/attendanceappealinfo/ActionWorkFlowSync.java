package com.x.attendance.assemble.control.jaxrs.attendanceappealinfo;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.attendance.assemble.control.ExceptionWrapInConvert;
import com.x.attendance.assemble.control.service.WorkFlowSyncService;
import com.x.attendance.entity.AttendanceAppealAuditInfo;
import com.x.attendance.entity.AttendanceAppealInfo;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;

public class ActionWorkFlowSync extends BaseAction {
	
	private static  Logger logger = LoggerFactory.getLogger( ActionWorkFlowSync.class );
	
	protected ActionResult<Wo> execute( HttpServletRequest request, EffectivePerson effectivePerson, JsonElement jsonElement ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		AttendanceAppealInfo attendanceAppealInfo = null;
		AttendanceAppealAuditInfo attendanceAppealAuditInfo = null;
		WorkFlowSyncService.WoWorkOrCompletedComplex woWorkComplex = null;
		ActionAppealCreate.Wi wrapIn = null;
		Boolean check = true;
		String id = "";

		if (check) {
			try {
				wrapIn = this.convertToWrapIn(jsonElement, ActionAppealCreate.Wi.class);
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionWrapInConvert(e, jsonElement);
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}

		if (check) {
			id = wrapIn.getId();
			if ( StringUtils.isEmpty( id )) {
				check = false;
				result.error(new Exception("传入的id为空，或者不合法，无法同步流程数据。"));
			}
		}

		if (check) {
			if( wrapIn.getStatus() != 0 & wrapIn.getStatus() != 1 &wrapIn.getStatus() != -1 &wrapIn.getStatus() != 9 ){
				check = false;
				Exception exception = new ExceptionAttendanceAppealProcess( "传入的申诉状态不合法。status:" + wrapIn.getStatus());
				result.error(exception);
			}
		}

		if (check) {
			try {
				attendanceAppealInfo = attendanceAppealInfoServiceAdv.get(id);
				if( attendanceAppealInfo == null ){
					check = false;
					Exception exception = new ExceptionAttendanceAppealNotExists( id );
					result.error(exception);
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionAttendanceAppealProcess(e, "系统在根据ID查询考勤申诉记录数据时发生异常。ID:" + id);
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}

		if (check) {
			try {
				attendanceAppealAuditInfo = attendanceAppealInfoServiceAdv.getAppealAuditInfo(id);
				if( attendanceAppealAuditInfo == null ){
					check = false;
					Exception exception = new ExceptionAppealAuditInfoNotExists( id );
					result.error(exception);
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionAttendanceAppealProcess(e, "系统在根据ID查询考勤申诉审批记录数据时发生异常。ID:" + id);
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}

		if (check) {
			try {
				woWorkComplex = WorkFlowSyncService.getWorkComplex( attendanceAppealAuditInfo.getWorkId() );
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionAttendanceAppealProcess(e, "系统在获取考勤打卡申诉流程信息时发生异常。ID:" + attendanceAppealAuditInfo.getWorkId() );
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}

		if (check) {
			if( woWorkComplex == null ){
				try {
					woWorkComplex = WorkFlowSyncService.getWorkCompletedComplex( attendanceAppealAuditInfo.getWorkId() );
				} catch (Exception e) {
					check = false;
					Exception exception = new ExceptionAttendanceAppealProcess(e, "系统在获取考勤打卡申诉流程信息时发生异常。ID:" + attendanceAppealAuditInfo.getWorkId() );
					result.error(exception);
					logger.error(e, effectivePerson, request, null);
				}
			}
		}

		if (check) {
			if( woWorkComplex == null ){
				check = false;
				Exception exception = new ExceptionAttendanceAppealProcess("流程（work or workCompleted）不存在。ID:" + attendanceAppealAuditInfo.getWorkId() );
				result.error(exception);
			}
		}

		if (check) {
			try {
				String processorName = null;
				String activityType = null;

				if(ListTools.isEmpty( woWorkComplex.getTaskList() )){
					processorName = null;
				}else{
					processorName = woWorkComplex.getTaskList().get(0).getIdentity();
				}
				if( woWorkComplex.getActivity() != null && woWorkComplex.getActivity().getActivityType() != null ){
					activityType = woWorkComplex.getActivity().getActivityType().name();
				}
				attendanceAppealInfoServiceAdv.syncAppealStatus( attendanceAppealInfo, activityType, processorName, wrapIn.getStatus() );

				result.setData(new Wo(id));
			} catch (Exception e) {
				Exception exception = new ExceptionAttendanceAppealProcess(e, "系统在同步考勤打卡申诉流程信息时发生异常。ID:" + id);
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		return result;
	}

	public static class Wi {
		@FieldDescribe("考勤申诉id")
		private  String id = "";

		@FieldDescribe("1-审批通过，-1-审批不能过")
		private Integer status = 0;

		public Integer getStatus() {
			return status;
		}

		public void setStatus(Integer status) {
			this.status = status;
		}

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}
	}

	public static class Wo extends WoId {
		public Wo( String id ) {
			setId( id );
		}
	}
}
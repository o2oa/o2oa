package com.x.report.assemble.control.jaxrs.report;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.http.WrapOutId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.core.entity.content.Read;
import com.x.processplatform.core.entity.content.Task;
import com.x.processplatform.core.entity.content.TaskCompleted;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.WorkLog;
import com.x.report.assemble.control.jaxrs.report.element.PermissionInfo;
import com.x.report.assemble.control.jaxrs.report.exception.ExceptionQueryWithReportId;
import com.x.report.assemble.control.jaxrs.report.exception.ExceptionReportIdEmpty;
import com.x.report.assemble.control.jaxrs.report.exception.ExceptionReportInfoProcess;
import com.x.report.assemble.control.jaxrs.report.exception.ExceptionReportNotExists;
import com.x.report.assemble.control.jaxrs.report.exception.ExceptionSyncWorkFlowWithReportId;
import com.x.report.assemble.control.service.Report_Sv_WorkFlowInfoSync;
import com.x.report.core.entity.Report_I_Base;

/**
 * 根据ID获取指定的汇报完整信息，包括当月计划， 完成情况 ，下月等内容，以及汇报的审批过程
 * @author O2LEE
 *
 */
public class ActionSyncWorkFlowInfo extends BaseAction {
	
	private static Logger logger = LoggerFactory.getLogger( ActionSyncWorkFlowInfo.class );
	private Gson gson = XGsonBuilder.instance();
	
	protected ActionResult<WrapOutId> execute( HttpServletRequest request, EffectivePerson effectivePerson, String reportId, JsonElement jsonElement ) throws Exception {
		ActionResult<WrapOutId> result = new ActionResult<>();
		Report_I_Base report_base = null;
		WiData data = null;
		Wi wi = null;
		
		String reportStatus = null; 
		String activityName = null; 
		String jobId = null; 
		String workflowLog = null;
		List<PermissionInfo> authorList = null;
		List<PermissionInfo> readerList = null;
		Boolean check = true;
		
		if( check ){
			if( reportId == null || reportId.isEmpty() ){
				check = false;
				Exception exception = new ExceptionReportIdEmpty();
				result.error( exception );
			}
		}
		
		if( check ){
			try {
				data = this.convertToWrapIn( jsonElement, WiData.class );
			} catch (Exception e ) {
				check = false;
				Exception exception = new ExceptionReportInfoProcess( e, "系统在将JSON信息转换为对象时发生异常。JSON:" + jsonElement.toString() );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		
		if( check ){
			try {
				Type type = new TypeToken<Wi>() {}.getType();
				wi = gson.fromJson( data.getData(), type );
			} catch (Exception e ) {
				check = false;
				Exception exception = new ExceptionReportInfoProcess( e, "系统在将JSON信息转换为对象时发生异常。JSON:" + jsonElement.toString() );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		
		if( check ){
			try {
				report_base = report_I_ServiceAdv.get( reportId );
				if( report_base == null ) {
					check = false;
					Exception exception = new ExceptionReportNotExists( reportId );
					result.error( exception );
				}
			} catch ( Exception e ) {
				check = false;
				Exception exception = new ExceptionQueryWithReportId( e, reportId );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		
		if( check ){
			reportStatus = wi.getReportStatus(); 
			if( wi.getWork() != null ) {
				activityName = wi.getWork().getActivityName();
				jobId = wi.getWork().getJob(); 
			}
			if( wi.getWorkLogList() != null ) {
				workflowLog = gson.toJson( wi.getWorkLogList() );
			}
			if( ListTools.isNotEmpty( wi.getReaderList() ) ) {
				readerList = wi.getReaderList();
			}
			if( ListTools.isNotEmpty( wi.getAuthorList() ) ) {
				authorList = wi.getAuthorList();
			}
		}
		
		if( check ){
			try {
				new Report_Sv_WorkFlowInfoSync().syncWithReport( reportId, reportStatus, activityName, jobId, workflowLog, readerList, authorList  );
			}catch( Exception e) {
				Exception exception = new ExceptionSyncWorkFlowWithReportId( e, reportId );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		result.setData( new WrapOutId( reportId ) );
		return result;
	}
	
	public static class WiData extends GsonPropertyObject {
		@FieldDescribe("data")
		private String data;

		public String getData() {
			return data;
		}

		public void setData(String data) {
			this.data = data;
		}		
	}
	
	
	public static class Wi extends GsonPropertyObject {

		@FieldDescribe("汇报状态")
		private String reportStatus;
		
		@FieldDescribe("工作")
		private WiWork work;
		
		@FieldDescribe("工作日志对象")
		private List<WiWorkLog> workLogList = new ArrayList<>();
		
		@FieldDescribe("汇报权限读者列表(PermissionInfo)")
		private List<PermissionInfo> readerList = null;

		@FieldDescribe("汇报作者权限列表(PermissionInfo)")
		private List<PermissionInfo> authorList = null;

		public String getReportStatus() {
			return reportStatus;
		}

		public WiWork getWork() {
			return work;
		}

		public List<WiWorkLog> getWorkLogList() {
			return workLogList;
		}

		public List<PermissionInfo> getReaderList() {
			return readerList;
		}

		public List<PermissionInfo> getAuthorList() {
			return authorList;
		}

		public void setReportStatus(String reportStatus) {
			this.reportStatus = reportStatus;
		}

		public void setWork(WiWork work) {
			this.work = work;
		}
		
		public void setWorkLogList(List<WiWorkLog> workLogList) {
			this.workLogList = workLogList;
		}

		public void setReaderList(List<PermissionInfo> readerList) {
			this.readerList = readerList;
		}

		public void setAuthorList(List<PermissionInfo> authorList) {
			this.authorList = authorList;
		}
	}

	public static class WiWork extends Work {

		private static final long serialVersionUID = 3269592171662996253L;

		static WrapCopier<Work, WiWork> copier = WrapCopierFactory.wo(Work.class, WiWork.class, null,
				JpaObject.FieldsInvisible);
	}
	
	public static class WiWorkLog extends WorkLog {

		private static final long serialVersionUID = 1307569946729101786L;

		public static WrapCopier<WorkLog, WiWorkLog> copier = WrapCopierFactory.wo(WorkLog.class, WiWorkLog.class, null,
				JpaObject.FieldsInvisible);

		private List<WiTaskCompleted> taskCompletedList;

		private List<WiTask> taskList;

		private Integer currentTaskIndex;

		public Integer getCurrentTaskIndex() {
			return currentTaskIndex;
		}

		public void setCurrentTaskIndex(Integer currentTaskIndex) {
			this.currentTaskIndex = currentTaskIndex;
		}

		public List<WiTaskCompleted> getTaskCompletedList() {
			return taskCompletedList;
		}

		public void setTaskCompletedList(List<WiTaskCompleted> taskCompletedList) {
			this.taskCompletedList = taskCompletedList;
		}

		public List<WiTask> getTaskList() {
			return taskList;
		}

		public void setTaskList(List<WiTask> taskList) {
			this.taskList = taskList;
		}

	}
	
	public static class WiTask extends Task {

		private static final long serialVersionUID = 2279846765261247910L;

		public static WrapCopier<Task, WiTask> copier = WrapCopierFactory.wo(Task.class, WiTask.class, null,
				JpaObject.FieldsInvisible);

	}

	public static class WiRead extends Read {

		private static final long serialVersionUID = -8067704098385000667L;

		public static WrapCopier<Read, WiRead> copier = WrapCopierFactory.wo(Read.class, WiRead.class, null,
				JpaObject.FieldsInvisible);

	}
	
	public static class WiTaskCompleted extends TaskCompleted {

		private static final long serialVersionUID = -7253999118308715077L;

		public static WrapCopier<TaskCompleted, WiTaskCompleted> copier = WrapCopierFactory.wo(TaskCompleted.class,
				WiTaskCompleted.class, null, JpaObject.FieldsInvisible);
		private Long rank;

		public Long getRank() {
			return rank;
		}

		public void setRank(Long rank) {
			this.rank = rank;
		}

	}
}
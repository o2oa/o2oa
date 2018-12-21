package com.x.okr.assemble.control.jaxrs.appraise;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.google.gson.JsonElement;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.okr.assemble.control.dataadapter.workflow.WorkComplexGetter;
import com.x.okr.assemble.control.jaxrs.appraise.exception.ExceptionWorkAppraiseProcess;
import com.x.okr.assemble.control.jaxrs.okrworkbaseinfo.BaseAction;
import com.x.okr.assemble.control.service.OkrWorkAppraiseSyncService;
import com.x.okr.assemble.control.service.OkrWorkPersonService;

public class ActionWorkAppraiseStatusSync extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger( ActionWorkAppraiseStatusSync.class );
	private OkrWorkAppraiseSyncService okrWorkAppraiseSyncService = new OkrWorkAppraiseSyncService();
	private OkrWorkPersonService okrWorkPersonService = new OkrWorkPersonService();
	
	protected ActionResult<Wo> execute( HttpServletRequest request,EffectivePerson effectivePerson, JsonElement jsonElement ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		WorkComplexGetter.Wo woWorkComplex =null;
		Boolean check = true;
		Wi wi = null;
		String title = null;
		String workId = null;
		String status = null;
		String wf_jobId = null;
		String wf_workId = null;
		try {
			wi = this.convertToWrapIn( jsonElement, Wi.class );
			title = wi.getTitle();
			workId = wi.getWorkId();
			status = wi.getAppraiseStatus();
			wf_jobId = wi.getWf_jobId();
			wf_workId = wi.getWf_workId();
			if( title!= null && title.length() > 80 ) {
				title = title.substring(0, 80) + "...";
			}
		} catch (Exception e ) {
			check = false;
			Exception exception = new ExceptionWorkAppraiseProcess( e, "系统在将JSON信息转换为对象时发生异常。JSON:" + jsonElement.toString() );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}

		//1、判断工作信息是否存在，如果存在则进行数据存储，如果不存在，就忽略
		//2、根据WORKID或者JOBID，获取当前流程的信息，包括当前审核环节等信息
		if( check ){
			//获取流程信息
			woWorkComplex = new WorkComplexGetter().getWorkComplex(wf_workId);
		}
		
		//为新的读者添加权限
		if( check ){
			if( ListTools.isNotEmpty( wi.getReaderList() )) {
				okrWorkPersonService.addWatcherForWork(workId, wi.getReaderList());
			}
		}
		if( check ){
			try {
				okrWorkAppraiseSyncService.updateAppraiseWfInfo( effectivePerson, title, workId, wf_jobId, wf_workId, status, woWorkComplex );
			} catch (Exception e ) {
				check = false;
				Exception exception = new ExceptionWorkAppraiseProcess( e, "系统在同步考核流程信息时发生异常。" );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		return result;
	}

	public static class Wi {

		@FieldDescribe( "考核流程标题" )
		private String title = null;

		@FieldDescribe( "工作ID" )
		private String workId = null;

		@FieldDescribe( "流程JOBID" )
		private String wf_jobId = null;

		@FieldDescribe( "流程WORKID" )
		private String wf_workId = null;

		@FieldDescribe( "流程审核状态" )
		private String appraiseStatus = null;
		
		@FieldDescribe( "需要为工作信息添加的读者权限" )
		private List<String> readerList = null;

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public String getWorkId() {
			return workId;
		}

		public void setWorkId(String workId) {
			this.workId = workId;
		}

		public String getWf_jobId() {
			return wf_jobId;
		}

		public void setWf_jobId(String wf_jobId) {
			this.wf_jobId = wf_jobId;
		}

		public String getWf_workId() {
			return wf_workId;
		}

		public void setWf_workId(String wf_workId) {
			this.wf_workId = wf_workId;
		}

		public String getAppraiseStatus() {
			return appraiseStatus;
		}

		public void setAppraiseStatus(String appraiseStatus) {
			this.appraiseStatus = appraiseStatus;
		}

		public List<String> getReaderList() {
			return readerList;
		}

		public void setReaderList(List<String> readerList) {
			this.readerList = readerList;
		}
	}

	public static class Wo extends WoId {
		public Wo( String id ) {
			setId( id );
		}
	}
}
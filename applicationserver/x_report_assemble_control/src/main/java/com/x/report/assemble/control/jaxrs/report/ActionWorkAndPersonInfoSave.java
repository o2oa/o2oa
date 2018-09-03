package com.x.report.assemble.control.jaxrs.report;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.google.gson.JsonElement;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.report.assemble.control.ExceptionWrapInConvert;
import com.x.report.assemble.control.jaxrs.report.element.PermissionInfo;
import com.x.report.assemble.control.jaxrs.setting.exception.ExceptionSettingInfoProcess;
import com.x.report.core.entity.Report_I_WorkInfo;

public class ActionWorkAndPersonInfoSave extends BaseAction {
	
	private static Logger logger = LoggerFactory.getLogger( ActionWorkAndPersonInfoSave.class );
	
	protected ActionResult<Wo> execute( HttpServletRequest request, EffectivePerson effectivePerson, JsonElement jsonElement ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wi wrapIn = null;
		Boolean check = true;
		
		try {
			wrapIn = this.convertToWrapIn( jsonElement, Wi.class );
		} catch (Exception e ) {
			check = false;
			Exception exception = new ExceptionWrapInConvert( e, jsonElement );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}
		
		if( check ){
			try {
				report_I_ServiceAdv.save( wrapIn.getId(), wrapIn.getWorkList(), wrapIn.getWorkreportPersonList(), wrapIn.getReaderList(), wrapIn.getAuthorList() );
				result.setData( new Wo( wrapIn.getId() ) );
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionSettingInfoProcess( e, "系统更新汇报业务处理人列表和读者作者列表时发生异常." );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		
		return result;
	}
	
	public static class Wi {

		@FieldDescribe("汇报ID")
		private String id = null;

		@FieldDescribe("汇报部门重点工作列表")
		private List<Report_I_WorkInfo> workList = null;

		@FieldDescribe("汇报业务汇报处理人列表(Person)")
		private List<String> workreportPersonList = null;

		@FieldDescribe("汇报权限读者列表(PermissionInfo)")
		private List<PermissionInfo> readerList = null;

		@FieldDescribe("汇报作者权限列表(PermissionInfo)")
		private List<PermissionInfo> authorList = null;

		public List<String> getWorkreportPersonList() {
			return workreportPersonList;
		}

		public void setWorkreportPersonList(List<String> workreportPersonList) {
			this.workreportPersonList = workreportPersonList;
		}

		public List<PermissionInfo> getReaderList() {
			return readerList;
		}

		public void setReaderList(List<PermissionInfo> readerList) {
			this.readerList = readerList;
		}

		public List<PermissionInfo> getAuthorList() {
			return authorList;
		}

		public void setAuthorList(List<PermissionInfo> authorList) {
			this.authorList = authorList;
		}

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public List<Report_I_WorkInfo> getWorkList() {
			return workList;
		}

		public void setWorkList(List<Report_I_WorkInfo> workList) {
			this.workList = workList;
		}
	}
	
	public static class Wo extends WoId {
		public Wo( String id ) {
			setId( id );
		}
	}
	
}
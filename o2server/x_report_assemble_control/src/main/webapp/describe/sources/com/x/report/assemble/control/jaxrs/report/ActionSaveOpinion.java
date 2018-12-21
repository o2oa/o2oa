package com.x.report.assemble.control.jaxrs.report;

import java.io.Serializable;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.report.assemble.control.ExceptionWrapInConvert;
import com.x.report.assemble.control.jaxrs.report.exception.ExceptionQueryWithReportId;
import com.x.report.assemble.control.jaxrs.report.exception.ExceptionReportIdEmpty;
import com.x.report.assemble.control.jaxrs.report.exception.ExceptionReportNotExists;
import com.x.report.assemble.control.jaxrs.setting.exception.ExceptionSettingInfoProcess;
import com.x.report.common.date.DateOperation;
import com.x.report.core.entity.Report_I_Base;

public class ActionSaveOpinion extends BaseAction {
	
	private static Logger logger = LoggerFactory.getLogger( ActionSaveOpinion.class );
	
	protected ActionResult<Wo> execute( HttpServletRequest request, EffectivePerson effectivePerson, String reportId, JsonElement jsonElement ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wi wrapIn = null;
		Report_I_Base report_base = null;
		Boolean check = true;
		
		if( reportId == null || reportId.isEmpty() ){
			check = false;
			Exception exception = new ExceptionReportIdEmpty();
			result.error( exception );
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
			try {
				wrapIn = this.convertToWrapIn( jsonElement, Wi.class );
			} catch (Exception e ) {
				check = false;
				Exception exception = new ExceptionWrapInConvert( e, jsonElement );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}

		if( check ){
			if( ListTools.isNotEmpty( wrapIn.getOpinions() )) {
				for(WiOpinion option :  wrapIn.getOpinions()) {
					if( StringUtils.isEmpty( option.getIdentity() )) {
						option.setIdentity( userManagerService.getPersonIdentity(effectivePerson.getDistinguishedName(), null ) );
					}
					if( StringUtils.isEmpty( option.getName() )) {
						option.setIdentity( effectivePerson.getName() );
					}
					if( StringUtils.isEmpty( option.getActivity() )) {
						option.setActivity( "领导阅知" );
					}
					if( StringUtils.isEmpty( option.getDatetime() )) {
						option.setDatetime( new DateOperation().getNowDateTime() );
					}
				}
			}
		}
		if( check ){
			try {//将意见存储到Detail里去
				report_I_ServiceAdv.saveOpinion( report_base, wrapIn.getOpinions() );
				result.setData( new Wo( report_base.getId() ) );
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionSettingInfoProcess( e, "保存领导阅知信息时发生异常." );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		return result;
	}
	
	public static class Wi implements Serializable {
		
		private static final long serialVersionUID = 1L;
		
		@FieldDescribe( "领导阅知意见列表" )
		private List<WiOpinion> opinions = null;

		public List<WiOpinion> getOpinions() {
			return opinions;
		}

		public void setOpinions(List<WiOpinion> opinions) {
			this.opinions = opinions;
		}		
	}
	
	public static class WiOpinion implements Serializable {
		
		private static final long serialVersionUID = 1L;

		@FieldDescribe( "领导身份" )
		private String identity = "";
		
		@FieldDescribe( "领导姓名" )
		private String name = "";
		
		@FieldDescribe( "环节名称" )
		private String activity = "未知";
		
		@FieldDescribe( "意见内容" )
		private String content = "";
		
		@FieldDescribe( "阅知日期：yyyy-MM-dd hh:mm:ss" )
		private String datetime = "";

		public String getIdentity() {
			return identity;
		}

		public String getName() {
			return name;
		}

		public String getActivity() {
			return activity;
		}

		public String getContent() {
			return content;
		}

		public String getDatetime() {
			return datetime;
		}

		public void setIdentity(String identity) {
			this.identity = identity;
		}

		public void setName(String name) {
			this.name = name;
		}

		public void setActivity(String activity) {
			this.activity = activity;
		}

		public void setContent(String content) {
			this.content = content;
		}

		public void setDatetime(String datetime) {
			this.datetime = datetime;
		}		
	}
	
	public static class Wo extends WoId {
		public Wo( String id ) {
			setId( id );
		}
	}
}
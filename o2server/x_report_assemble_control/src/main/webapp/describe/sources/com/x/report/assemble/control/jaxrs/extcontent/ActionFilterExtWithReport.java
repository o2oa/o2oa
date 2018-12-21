package com.x.report.assemble.control.jaxrs.extcontent;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.google.gson.JsonElement;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.report.assemble.control.ExceptionWrapInConvert;
import com.x.report.assemble.control.jaxrs.setting.exception.ExceptionSettingInfoProcess;
import com.x.report.core.entity.Report_I_Ext_Content;
import com.x.report.core.entity.Report_I_Ext_ContentDetail;

public class ActionFilterExtWithReport extends BaseAction {
	
	private Logger logger = LoggerFactory.getLogger( ActionFilterExtWithReport.class );
	
	protected ActionResult<List<Wo>> execute( HttpServletRequest request, EffectivePerson effectivePerson, String reportId, JsonElement jsonElement ) throws Exception {
		ActionResult<List<Wo>> result = new ActionResult<>();
		List<Wo> wraps = null;
		List<Report_I_Ext_Content>report_I_Ext_ContentList = null;
		Wi wrapIn = null;
		Boolean check = true;
		
		if( check ){
			try {
				wrapIn = this.convertToWrapIn( jsonElement, Wi.class );
				if(wrapIn == null ) {
					wrapIn = new Wi();
				}
			} catch (Exception e ) {
				check = false;
				Exception exception = new ExceptionWrapInConvert( e, jsonElement );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		
		if( check ){
			try {
				report_I_Ext_ContentList = report_I_Ext_ContentServiceAdv.listWithReportId(reportId, wrapIn.getInfoLevel(), wrapIn.getTargetPerson());
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionSettingInfoProcess( e, "系统查询所有汇报扩展信息列表时发生异常." );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		if( check ){
			try {
				wraps = Wo.copier.copy( report_I_Ext_ContentList );
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionSettingInfoProcess( e, "将所有查询到的考勤配置信息对象转换为可以输出的信息时发生异常." );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		
		//把具体的内容查询出来装配上去
		if( check ){
			if( ListTools.isNotEmpty(wraps) ) {
				List<Report_I_Ext_ContentDetail> report_I_Ext_ContentDetails = null;
				for( Wo wo : wraps ) {
					report_I_Ext_ContentDetails = report_I_Ext_ContentServiceAdv.listDetailWithContentId(wo.getId());
					if( ListTools.isNotEmpty(report_I_Ext_ContentDetails) ) {
						for( Report_I_Ext_ContentDetail report_I_Ext_ContentDetail : report_I_Ext_ContentDetails ) {
							if("员工关爱".equals(report_I_Ext_ContentDetail.getContentType())) {
								wo.setGuanai(report_I_Ext_ContentDetail.getContent());
							}
							if("服务客户".equals(report_I_Ext_ContentDetail.getContentType())) {
								wo.setFuwu(report_I_Ext_ContentDetail.getContent());
							}
							if("意见建议".equals(report_I_Ext_ContentDetail.getContentType())) {
								wo.setYijian(report_I_Ext_ContentDetail.getContent());
							}
						}
					}
				}
			}
			result.setData(wraps);
		}
		return result;
	}
	
	public static class Wi {

		@FieldDescribe( "信息级别：员工 | 汇总." )
		private String infoLevel = "员工";
		
		@FieldDescribe("填写员工")
		private String targetPerson;

		public String getInfoLevel() {
			return infoLevel;
		}

		public String getTargetPerson() {
			return targetPerson;
		}

		public void setInfoLevel(String infoLevel) {
			this.infoLevel = infoLevel;
		}

		public void setTargetPerson(String targetPerson) {
			this.targetPerson = targetPerson;
		}
		
	}
	
	public static class Wo extends Report_I_Ext_Content  {
		
		private static final long serialVersionUID = -5076990764713538973L;
		
		public static List<String> Excludes = new ArrayList<String>();
		
		@FieldDescribe("员工关爱具体内容")
		private String guanai="";
		
		@FieldDescribe("服务客户具体内容")
		private String fuwu="";
		
		@FieldDescribe("意见建议具体内容")
		private String yijian="";
		
		public static WrapCopier<Report_I_Ext_Content, Wo> copier = WrapCopierFactory.wo( Report_I_Ext_Content.class, Wo.class, null,Wo.Excludes);

		public String getGuanai() {
			return guanai;
		}

		public String getFuwu() {
			return fuwu;
		}

		public String getYijian() {
			return yijian;
		}

		public void setGuanai(String guanai) {
			this.guanai = guanai;
		}

		public void setFuwu(String fuwu) {
			this.fuwu = fuwu;
		}

		public void setYijian(String yijian) {
			this.yijian = yijian;
		}
	}
}
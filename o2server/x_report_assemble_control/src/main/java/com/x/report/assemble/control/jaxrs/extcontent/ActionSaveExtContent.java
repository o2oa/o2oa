package com.x.report.assemble.control.jaxrs.extcontent;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.report.assemble.control.ExceptionWrapInConvert;
import com.x.report.assemble.control.jaxrs.report.exception.ExceptionQueryWithReportId;
import com.x.report.assemble.control.jaxrs.report.exception.ExceptionReportIdEmpty;
import com.x.report.assemble.control.jaxrs.report.exception.ExceptionReportNotExists;
import com.x.report.assemble.control.jaxrs.setting.exception.ExceptionSettingInfoProcess;
import com.x.report.core.entity.Report_I_Base;
import com.x.report.core.entity.Report_I_Ext_Content;
import com.x.report.core.entity.Report_I_Ext_ContentDetail;

public class ActionSaveExtContent extends BaseAction {
	
	private static Logger logger = LoggerFactory.getLogger( ActionSaveExtContent.class );
	
	protected ActionResult<Wo> execute( HttpServletRequest request, EffectivePerson effectivePerson, String reportId, JsonElement jsonElement ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wi wrapIn = null;
		Report_I_Ext_Content report_I_Ext_Content = new Report_I_Ext_Content();
		List<Report_I_Ext_ContentDetail> old_details = null;
		List<Report_I_Ext_ContentDetail> new_details = null;
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
				report_I_Ext_Content = Wi.copier.copy( wrapIn );
				if( wrapIn.getId() != null && !wrapIn.getId().isEmpty() ){
					report_I_Ext_Content.setId( wrapIn.getId() );
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionSettingInfoProcess( e, "系统将用户传入的数据转换为关爱员工信息对象时发生异常." );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		if( check ){
			try {
				report_I_Ext_Content.setReportId(report_base.getId());
				report_I_Ext_Content.setProfileId(report_base.getProfileId());
				if( StringUtils.isEmpty(report_I_Ext_Content.getTargetPerson())) {
					report_I_Ext_Content.setTargetPerson( effectivePerson.getDistinguishedName() );
				}
				if( StringUtils.isEmpty( report_I_Ext_Content.getInfoLevel() ) ){
					report_I_Ext_Content.setInfoLevel("员工");
				}
				if( report_I_Ext_Content.getOrderNumber() == null ) {
					report_I_Ext_Content.setOrderNumber(0);
				}
				
				//查询所有的具体内容
				old_details = report_I_Ext_ContentServiceAdv.listDetailWithContentId(report_I_Ext_Content.getId());
				//组织出来后，一定有三个类别的内容，未填写的，内容为空字符串
				new_details = composeDetails(report_I_Ext_Content, old_details, wrapIn );
				report_I_Ext_Content = report_I_Ext_ContentServiceAdv.save( report_I_Ext_Content, new_details );				
				result.setData( new Wo( report_I_Ext_Content.getId() ) );
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionSettingInfoProcess( e, "保存关爱员工信息信息时发生异常." );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		return result;
	}
	
	private List<Report_I_Ext_ContentDetail> composeDetails(Report_I_Ext_Content report_I_Ext_Content, List<Report_I_Ext_ContentDetail> old_details, Wi wrapIn) {
		if( old_details == null ) {
			old_details = new ArrayList<>();
		}
		List<Report_I_Ext_ContentDetail> new_details = new ArrayList<>();
		Report_I_Ext_ContentDetail detail = null;
		detail = composeDetail("员工关爱", report_I_Ext_Content, old_details, wrapIn.getGuanai() );
		new_details.add(detail);
		detail = composeDetail("服务客户", report_I_Ext_Content, old_details, wrapIn.getFuwu() );
		new_details.add(detail);
		detail = composeDetail("意见建议", report_I_Ext_Content, old_details, wrapIn.getYijian() );
		new_details.add(detail);
		return new_details;
	}

	private Report_I_Ext_ContentDetail composeDetail( String contentType, Report_I_Ext_Content report_I_Ext_Content, List<Report_I_Ext_ContentDetail> old_details, String content) {
		Report_I_Ext_ContentDetail new_detail = null;
		for( Report_I_Ext_ContentDetail detail : old_details ) {
			if(contentType.equals(detail.getContentType())) {
				new_detail = detail;
			}
		}
		if( new_detail == null ) {
			new_detail = new Report_I_Ext_ContentDetail();
			new_detail.setContentId(report_I_Ext_Content.getId());
			new_detail.setContentType(contentType);
			new_detail.setOrderNumber(1);
			new_detail.setProfileId(report_I_Ext_Content.getProfileId());
			new_detail.setReportId(report_I_Ext_Content.getReportId());
			new_detail.setCreateTime(new Date());
			new_detail.setUpdateTime(new Date());
		}
		new_detail.setContent( content );
		return new_detail;
	}

	public static class Wi extends Report_I_Ext_Content {		
		private static final long serialVersionUID = -5076990764713538973L;		
		public static List<String> Excludes = new ArrayList<String>(JpaObject.FieldsUnmodify);		
		public static WrapCopier< Wi, Report_I_Ext_Content > copier = WrapCopierFactory.wi( Wi.class, Report_I_Ext_Content.class, null, Wi.Excludes );
		@FieldDescribe("员工关爱具体内容")
		private String guanai="";
		
		@FieldDescribe("服务客户具体内容")
		private String fuwu="";
		
		@FieldDescribe("意见建议具体内容")
		private String yijian="";

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
	
	public static class Wo extends WoId {
		public Wo( String id ) {
			setId( id );
		}
	}
}
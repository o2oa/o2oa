package com.x.report.assemble.control.jaxrs.extcontent;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.report.assemble.control.jaxrs.extcontent.exception.ExceptionExtContentNotExists;
import com.x.report.assemble.control.jaxrs.extcontent.exception.ExceptionExtContentProcess;
import com.x.report.assemble.control.jaxrs.extcontent.exception.ExceptionQueryExtContentWithId;
import com.x.report.core.entity.Report_I_Ext_Content;
import com.x.report.core.entity.Report_I_Ext_ContentDetail;

public class ActionGetContentWithId extends BaseAction {
	
	private Logger logger = LoggerFactory.getLogger( ActionGetContentWithId.class );
	
	protected ActionResult<Wo> execute( HttpServletRequest request, EffectivePerson effectivePerson, String id ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = null;
		Report_I_Ext_Content report_I_Ext_Content  = null;
		List<Report_I_Ext_ContentDetail> report_I_Ext_ContentDetails = null;
		Boolean check = true;
	
		if( check ){
			try {
				report_I_Ext_Content = report_I_Ext_ContentServiceAdv.get(id);
				if( report_I_Ext_Content == null ) {
					check = false;
					Exception exception = new ExceptionExtContentNotExists( id );
					result.error( exception );
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionQueryExtContentWithId( e, id );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		if( check ){
			try {
				wo = Wo.copier.copy( report_I_Ext_Content );
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionExtContentProcess( e, "将查询到的扩展信息转换为可以输出的信息时发生异常." );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		
		//把具体的内容查询出来装配上去
		if( check ){
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
			result.setData(wo);
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
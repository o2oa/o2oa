package com.x.report.assemble.control.jaxrs.workprog;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.FetchType;
import javax.servlet.http.HttpServletRequest;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.report.assemble.control.jaxrs.workprog.exception.ExceptionQueryWorkProgWithId;
import com.x.report.core.entity.Report_C_WorkProg;
import com.x.report.core.entity.Report_C_WorkProgDetail;

/**
 * 根据ID获取指定的工作完成情况信息列表
 * @author O2LEE
 *
 */
public class ActionGetWorkProgWithId extends BaseAction {
	
	private Logger logger = LoggerFactory.getLogger( ActionGetWorkProgWithId.class);
	
	protected ActionResult<Wo> execute( HttpServletRequest request, EffectivePerson effectivePerson, String id ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = null;
		Report_C_WorkProg report_C_WorkProg = null;
		Report_C_WorkProgDetail detail = null;
		Boolean check = true;
		
		//查询工作完成情况列表
		if( check ) {
			try {
				report_C_WorkProg = report_C_WorkProgServiceAdv.get(id);
				if( report_C_WorkProg != null ) {
					wo = Wo.copier.copy( report_C_WorkProg );
				}
			}catch( Exception e ) {
				check = false;
				Exception exception = new ExceptionQueryWorkProgWithId( e, id );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		
		//组织工作完成情况详细内容
		if( check ) {
			if( wo != null ) {
				//查询并且组织详细信息
				detail = report_C_WorkProgServiceAdv.getDetailWithProgId( wo.getId() );
				if( detail != null ) {
					wo.setDetailId( detail.getId() );
					wo.setWorkContent( detail.getWorkContent() );
					wo.setProgressContent( detail.getProgressContent() );
				}
			}
		}
		
		result.setData( wo );
		return result;
	}
	
	public static class Wo extends Report_C_WorkProg  {
		
		private static final long serialVersionUID = -5076990764713538973L;
		
		public static List<String> Excludes = new ArrayList<String>();
		
		public static WrapCopier<Report_C_WorkProg, Wo> copier = WrapCopierFactory.wo( Report_C_WorkProg.class, Wo.class, null,Wo.Excludes);
		
		@FieldDescribe("详细信息ID")
		private String detailId;
		
		@FieldDescribe( "工作概述" )
		private String workContent = "";
		
		@Basic( fetch = FetchType.EAGER )
		@FieldDescribe( "工作完成情况内容" )
		private String progressContent = "";

		public String getDetailId() {
			return detailId;
		}

		public String getWorkContent() {
			return workContent;
		}

		public void setDetailId(String detailId) {
			this.detailId = detailId;
		}

		public void setWorkContent(String workContent) {
			this.workContent = workContent;
		}

		public String getProgressContent() {
			return progressContent;
		}

		public void setProgressContent(String progressContent) {
			this.progressContent = progressContent;
		}
	}
}
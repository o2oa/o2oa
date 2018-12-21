package com.x.report.assemble.control.jaxrs.workprog;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.FetchType;
import javax.servlet.http.HttpServletRequest;

import com.google.gson.JsonElement;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.SortTools;
import com.x.report.assemble.control.jaxrs.report.exception.ExceptionReportInfoProcess;
import com.x.report.assemble.control.jaxrs.workprog.exception.ExceptionQueryWorkProgWithReportId;
import com.x.report.core.entity.Report_C_WorkProg;
import com.x.report.core.entity.Report_C_WorkProgDetail;
import com.x.report.core.entity.Report_I_Base;

/**
 * 根据汇报ID获取所有的工作完成情况信息列表
 * @author O2LEE
 *
 */
public class ActionListWorkProgWithFilter extends BaseAction {
	
	private Logger logger = LoggerFactory.getLogger( ActionListWorkProgWithFilter.class);
	
	protected ActionResult<List<Wo>> execute( HttpServletRequest request, EffectivePerson effectivePerson, String year, JsonElement jsonElement ) throws Exception {
		ActionResult<List<Wo>> result = new ActionResult<>();
		List<Wo> wos = new ArrayList<Wo>();
		Wo wo = null;
		Wi wi = null;
		List<String> reportIds = null;
		List<String> reportIds_query = null;
		List<String> reportIds_result = new ArrayList<>();
		List<Report_C_WorkProg> progList = null;
		List<Report_I_Base> reports = null;
		List<WoReport_C_WorkProg> woGrogList = null;
		Boolean check = true;
		
		//查询汇报是否存在
		if( check ) {
			try {
				wi = this.convertToWrapIn( jsonElement, Wi.class );
			} catch (Exception e ) {
				check = false;
				Exception exception = new ExceptionReportInfoProcess( e, "系统在将JSON信息转换为对象时发生异常。JSON:" + jsonElement.toString() );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		
		//查询与指定工作有关的所有工作汇报ID列表
		if( check ) {
			try {
				reportIds = report_C_WorkProgServiceAdv.listReportIdsWithKeyWorkId( year, null, null, null, wi.getReportObjType(), wi.getWorkIds(), null );
			} catch (Exception e ) {
				check = false;
				Exception exception = new ExceptionReportInfoProcess( e, "系统在查询与指定工作有关的所有工作汇报ID列表时发生异常。" );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		
		if( check ) {
			try {
				reportIds_query = report_I_ServiceAdv.lisViewableIdsWithFilter( null, null, wi.getReportObjType(), year, null, null, null, null, null, wi.getUnitList(), null, 5000, false );
				//交叉过滤ID
				if( reportIds_query != null && !reportIds_query.isEmpty() ) {
					for( String id : reportIds_query ) {
						if( reportIds.contains( id )) {
							reportIds_result.add( id );
						}
					}
				}
			} catch (Exception e ) {
				check = false;
				Exception exception = new ExceptionReportInfoProcess( e, "系统在查询与指定组织有关的所有工作汇报ID列表时发生异常。" );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}	
		}
		
		if( check ) {
			try {
				reports = report_I_ServiceAdv.listWithIds( reportIds_result );
			} catch (Exception e ) {
				check = false;
				Exception exception = new ExceptionReportInfoProcess( e, "系统在根据ID查询工作汇报列表时发生异常。" );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
	
		//查询工作完成情况列表
		if( check ) {
			if( reports != null && !reports.isEmpty() ) {
				for( Report_I_Base reportBase: reports ) {
					if( reportBase != null ) {
						wo = new Wo();
						wo.setReportId(reportBase.getId());
						wo.setYear(reportBase.getYear());
						wo.setMonth(reportBase.getMonth());
						try {
							progList = report_C_WorkProgServiceAdv.listWithKeyWorkIds( reportBase.getId(), wi.getWorkIds() );
							if( progList != null ) {
								woGrogList = WoReport_C_WorkProg.copier.copy( progList );
								wo.setProgList( woGrogList );
								
								//组织工作完成情况详细内容
								if( woGrogList != null && !woGrogList.isEmpty() ) {
									Report_C_WorkProgDetail detail = null;
									for( WoReport_C_WorkProg _prog : woGrogList ) {
										//查询并且组织详细信息
										detail = report_C_WorkProgServiceAdv.getDetailWithProgId( _prog.getId() );
										if( detail != null ) {
											_prog.setDetailId( detail.getId() );
											_prog.setProgressContent( detail.getProgressContent() );
										}
									}
								}
							}
						}catch( Exception e ) {
							check = false;
							Exception exception = new ExceptionQueryWorkProgWithReportId( e, reportBase.getId() );
							result.error( exception );
							logger.error( e, effectivePerson, request, null);
						}
					}
					wos.add( wo );
				}
			}
		}
		SortTools.asc( wos, "month" );
		result.setData( wos );
		return result;
	}
	
	public static class Wo {
		
		@FieldDescribe("汇报ID")
		private String reportId;
		
		@FieldDescribe("年份")
		private String year;
		
		@FieldDescribe("月份")
		private String month;
		
		@FieldDescribe("汇报者")
		private String targetPerson;
		
		@FieldDescribe("进展详细信息ID")
		private List<WoReport_C_WorkProg> progList = null;

		public String getReportId() {
			return reportId;
		}

		public String getTargetPerson() {
			return targetPerson;
		}

		public void setReportId(String reportId) {
			this.reportId = reportId;
		}

		public void setTargetPerson(String targetPerson) {
			this.targetPerson = targetPerson;
		}

		public String getYear() {
			return year;
		}

		public String getMonth() {
			return month;
		}

		public void setYear(String year) {
			this.year = year;
		}

		public void setMonth(String month) {
			this.month = month;
		}

		public List<WoReport_C_WorkProg> getProgList() {
			return progList;
		}

		public void setProgList(List<WoReport_C_WorkProg> progList) {
			this.progList = progList;
		}
		
	}
	
	public static class WoReport_C_WorkProg {
		
		public static List<String> Excludes = new ArrayList<String>();
		
		public static WrapCopier<Report_C_WorkProg, WoReport_C_WorkProg> copier = WrapCopierFactory.wo( Report_C_WorkProg.class, WoReport_C_WorkProg.class, null,WoReport_C_WorkProg.Excludes);
		
		@FieldDescribe("ID")
		private String id;
		
		@FieldDescribe("完成情况信息ID")
		private String progId;
		
		@FieldDescribe("处理人标识")
		private String targetPerson;
		
		@FieldDescribe("详细信息ID")
		private String detailId;
		
		@Basic( fetch = FetchType.EAGER )
		@FieldDescribe( "工作完成情况内容" )
		private String progressContent = "";

		public String getDetailId() {
			return detailId;
		}
		
		public void setDetailId(String detailId) {
			this.detailId = detailId;
		}

		public String getProgressContent() {
			return progressContent;
		}

		public void setProgressContent(String progressContent) {
			this.progressContent = progressContent;
		}

		public String getProgId() {
			return progId;
		}

		public void setProgId(String progId) {
			this.progId = progId;
		}

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getTargetPerson() {
			return targetPerson;
		}

		public void setTargetPerson(String targetPerson) {
			this.targetPerson = targetPerson;
		}
	}
	
	public static class Wi {

		@FieldDescribe("工作ID")
		private List<String> workIds;
		
		@FieldDescribe( "组织名称" )
		private List<String> unitList = null;
		
		@FieldDescribe("汇报对象类型：UNIT|PERSON")
		private String reportObjType;

		public String getReportObjType() {
			return reportObjType;
		}

		public void setReportObjType(String reportObjType) {
			this.reportObjType = reportObjType;
		}

		public List<String> getWorkIds() {
			return workIds;
		}

		public void setWorkIds(List<String> workIds) {
			this.workIds = workIds;
		}

		public List<String> getUnitList() {
			return unitList;
		}

		public void setUnitList(List<String> unitList) {
			this.unitList = unitList;
		}	
		
	}

}
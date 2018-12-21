package com.x.report.assemble.control.jaxrs.reportstat;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.report.assemble.control.jaxrs.export.exception.ExceptionDataQuery;
import com.x.report.assemble.control.jaxrs.report.exception.ExceptionReportInfoProcess;
import com.x.report.core.entity.Report_I_WorkInfo;

/**
 * 根据条件查询指定年份各部门各月的重点工作内容
 * 
 * @author O2LEE
 *
 */
public class ActionStatUnitWorkInYear extends BaseAction {
	
	private static Logger logger = LoggerFactory.getLogger(ActionStatUnitWorkInYear.class);
	
	protected ActionResult<List<Wo>> execute( HttpServletRequest request, EffectivePerson effectivePerson, JsonElement jsonElement  ) throws Exception {
		ActionResult<List<Wo>> result = new ActionResult<>();
		List<Wo> wos = new ArrayList<>();
		String thisYear = null;
		String thisMonth = null;
		Wi wi = null;
		Boolean check = true;		
		
		try {
			wi = this.convertToWrapIn( jsonElement, Wi.class );
			
			if( StringUtils.isEmpty( wi.getYear() )) {
				wi.setYear( dateOperation.getYear( new Date() ));
			}
			
		} catch (Exception e ) {
			check = false;
			Exception exception = new ExceptionReportInfoProcess( e, "系统在将JSON信息转换为对象时发生异常。JSON:" + jsonElement.toString() );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}
		
		if( check ) {
			thisYear = wi.getYear();
			thisMonth = wi.getMonth();
		}
		/**
		 * 1、根据年份，月份，组织名称列表，查询所有的汇报信息列表和工作信息列表
		 * 2、根据所有的汇报信息ID列表查询所有的当月工作信息列表
		 * 3、组织成一个【年度-部门-月份-重点工作】列表的对象，按导出的内容形式
		 */
		//1、根据年份，月份，组织名称列表，查询所有的汇报信息列表和工作信息列表
		// 1) 汇报信息列表
		List<String>reportIds = null;
		if( check ) {
			try {
				reportIds = report_I_ServiceAdv.listWithConditions( thisYear, thisMonth, wi.getUnitList(), wi.getWfProcessStatus(), wi.getWfActivityNames(), false );
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionDataQuery( e, "year:" + thisYear + ", month" + thisMonth );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		
		// 2) 汇报工作信息列表, THISMONTH、NEXTMONTH
		List<String>workInfoIds = null;
		List<Report_I_WorkInfo> reportWorkInfoList = null;
		if( check ) {
			if( ListTools.isNotEmpty( reportIds ) ) {
				try {
					workInfoIds = report_I_WorkInfoServiceAdv.listIdsWithReports( reportIds, null );
				} catch (Exception e) {
					check = false;
					Exception exception = new ExceptionDataQuery( e, "year:" + wi.getYear() + ", month" + wi.getMonth() );
					result.error( exception );
					logger.error( e, effectivePerson, request, null);
				}
			}
		}
		
		if( check ) {
			if( ListTools.isNotEmpty( workInfoIds ) ) {
				try {
					reportWorkInfoList = report_I_WorkInfoServiceAdv.list( workInfoIds );
				} catch (Exception e) {
					check = false;
					Exception exception = new ExceptionDataQuery( e, "根据ID列表查询汇报工作信息时发生异常！" );
					result.error( exception );
					logger.error( e, effectivePerson, request, null);
				}
			}
		}
		
		if( check ) {
			if( ListTools.isNotEmpty( reportWorkInfoList ) ) {
				for( Report_I_WorkInfo workInfo : reportWorkInfoList ) {
					if( "THISMONTH".equalsIgnoreCase( workInfo.getWorkMonthFlag() )) {
						wos = addWorkInfoToWrapList( workInfo, wos );		
					}
				}
			}
		}
		result.setData( wos );
		return result;
	}

	private List<Wo> addWorkInfoToWrapList(Report_I_WorkInfo workInfo, List<Wo> wos) {
		if( wos == null ) {
			wos = new ArrayList<>();
		}
		Boolean exists = false;
		for( Wo wo : wos  ) {
			if( workInfo.getWorkUnit().equalsIgnoreCase( wo.getUnitName() )) {
				exists = true;
				wo.addWorkInfo( workInfo );
			}
		}
		if( !exists ) {
			Wo wo = new Wo();
			wo.setUnitName( workInfo.getWorkUnit() );
			wo.setYear( workInfo.getWorkYear() );
			wo.addWorkInfo( workInfo );
			wos.add( wo );
		}
		return wos;
	}

	public static class Wi {

		@FieldDescribe( "汇报年份." )
		private String year;
		
		@FieldDescribe( "汇报月份." )
		private String month;
		
		@FieldDescribe( "组织名称." )
		private List<String> unitList;
		
		@FieldDescribe( "流程处理状态: 已完成、 流转中" )
		private List<String> wfProcessStatus;
		
		@FieldDescribe( "流程处理环节名称: 月度汇报员分派填写人、 战略负责人审核、月度汇报员汇总、汇报人" )
		private List<String> wfActivityNames;

		public String getYear() {
			return year;
		}

		public String getMonth() {
			return month;
		}

		public List<String> getUnitList() {
			return unitList;
		}

		public void setYear(String year) {
			this.year = year;
		}

		public void setMonth(String month) {
			this.month = month;
		}

		public void setUnitList(List<String> unitList) {
			this.unitList = unitList;
		}

		public List<String> getWfProcessStatus() {
			return wfProcessStatus;
		}

		public void setWfProcessStatus(List<String> wfProcessStatus) {
			this.wfProcessStatus = wfProcessStatus;
		}

		public List<String> getWfActivityNames() {
			return wfActivityNames;
		}

		public void setWfActivityNames(List<String> wfActivityNames) {
			this.wfActivityNames = wfActivityNames;
		}		
	}
	
	public static class Wo {

		@FieldDescribe("汇报部门名称")
		private String unitName = null;
		
		@FieldDescribe("年份")
		private String year = null;

		@FieldDescribe("重点工作数量")
		private Integer workTotal = 0;

		@FieldDescribe("重点工作列表")
		private List<Wo_StatWorkMonth> workMonths = null;
		
		public void addWorkInfo( Report_I_WorkInfo workInfo ) {
			if( this.workMonths == null ) {
				this.workMonths = new ArrayList<>();
				this.workMonths.add( new Wo_StatWorkMonth( workInfo.getWorkReportYear(), "01") );
				this.workMonths.add( new Wo_StatWorkMonth( workInfo.getWorkReportYear(), "02") );
				this.workMonths.add( new Wo_StatWorkMonth( workInfo.getWorkReportYear(), "03") );
				this.workMonths.add( new Wo_StatWorkMonth( workInfo.getWorkReportYear(), "04") );
				this.workMonths.add( new Wo_StatWorkMonth( workInfo.getWorkReportYear(), "05") );
				this.workMonths.add( new Wo_StatWorkMonth( workInfo.getWorkReportYear(), "06") );
				this.workMonths.add( new Wo_StatWorkMonth( workInfo.getWorkReportYear(), "07") );
				this.workMonths.add( new Wo_StatWorkMonth( workInfo.getWorkReportYear(), "08") );
				this.workMonths.add( new Wo_StatWorkMonth( workInfo.getWorkReportYear(), "09") );
				this.workMonths.add( new Wo_StatWorkMonth( workInfo.getWorkReportYear(), "10") );
				this.workMonths.add( new Wo_StatWorkMonth( workInfo.getWorkReportYear(), "11") );
				this.workMonths.add( new Wo_StatWorkMonth( workInfo.getWorkReportYear(), "12") );
			}
			for( Wo_StatWorkMonth wo_StatWorkMonth : this.workMonths ) {
				if( wo_StatWorkMonth.getYear().equalsIgnoreCase( workInfo.getWorkReportYear() )
					&& wo_StatWorkMonth.getMonth().equalsIgnoreCase( workInfo.getWorkReportMonth() )
				) {
					wo_StatWorkMonth.addWorkInfo( workInfo );
					this.workTotal++;
				}
			}
		}

		public String getUnitName() {
			return unitName;
		}

		public Integer getWorkTotal() {
			return workTotal;
		}

		public void setUnitName(String unitName) {
			this.unitName = unitName;
		}

		public void setWorkTotal(Integer workTotal) {
			this.workTotal = workTotal;
		}

		public List<Wo_StatWorkMonth> getWorkMonths() {
			return workMonths;
		}

		public void setWorkMonths(List<Wo_StatWorkMonth> workMonths) {
			this.workMonths = workMonths;
		}

		public String getYear() {
			return year;
		}

		public void setYear(String year) {
			this.year = year;
		}	
	}

	public static class Wo_StatWorkMonth{
		
		@FieldDescribe("年份")
		private String year = null;
		
		@FieldDescribe("月份")
		private String month = "01";
		
		@FieldDescribe("汇报ID")
		private String reportId = null;
		
		@FieldDescribe("重点工作数量")
		private Integer workTotal = 0;
		
		@FieldDescribe("工作列表")
		private List<Wo_StatWork> workInfoList = null;
		
		public Wo_StatWorkMonth(String year, String month) {
			super();
			this.year = year;
			this.month = month;
		}

		public String getMonth() {
			return month;
		}

		public void addWorkInfo( Report_I_WorkInfo workInfo ) {
			if( this.workInfoList == null ) {
				this.workInfoList = new ArrayList<>();
			}
			Wo_StatWork wo_StatWork = new Wo_StatWork();
			wo_StatWork.setYear( workInfo.getWorkReportYear() );
			wo_StatWork.setMonth( workInfo.getWorkReportMonth() );
			wo_StatWork.setReportId( workInfo.getReportId() );
			wo_StatWork.setWorkId( workInfo.getId() );
			wo_StatWork.setWorkName( workInfo.getWorkTitle() );
			this.workInfoList.add( wo_StatWork );
			this.workTotal++;
		}

		public List<Wo_StatWork> getWorkInfoList() {
			if( this.workInfoList == null ) {
				this.workInfoList = new ArrayList<>();
			}
			return workInfoList;
		}

		public void setMonth(String month) {
			this.month = month;
		}

		public void setWorkInfoList(List<Wo_StatWork> workInfoList) {
			this.workInfoList = workInfoList;
		}

		public Integer getWorkTotal() {
			return workTotal;
		}

		public void setWorkTotal(Integer workTotal) {
			this.workTotal = workTotal;
		}

		public String getYear() {
			return year;
		}

		public void setYear(String year) {
			this.year = year;
		}

		public String getReportId() {
			return reportId;
		}

		public void setReportId(String reportId) {
			this.reportId = reportId;
		}
	}
	
	public static class Wo_StatWork{
		
		@FieldDescribe("年份")
		private String year = null;
		
		@FieldDescribe("月份")
		private String month = "01";
		
		@FieldDescribe("汇报ID")
		private String reportId = null;
		
		@FieldDescribe("工作名称")
		private String workName = null;
		
		@FieldDescribe("工作名称")
		private String workId = null;

		public String getWorkName() {
			return workName;
		}

		public void setWorkName(String workName) {
			this.workName = workName;
		}

		public String getWorkId() {
			return workId;
		}

		public void setWorkId(String workId) {
			this.workId = workId;
		}

		public String getYear() {
			return year;
		}

		public String getMonth() {
			return month;
		}

		public String getReportId() {
			return reportId;
		}

		public void setYear(String year) {
			this.year = year;
		}

		public void setMonth(String month) {
			this.month = month;
		}

		public void setReportId(String reportId) {
			this.reportId = reportId;
		}		
	}
}
package com.x.report.assemble.control.jaxrs.report;

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
import com.x.report.assemble.control.jaxrs.report.exception.ExceptionReportInfoProcess;
import com.x.report.core.entity.Report_I_Base;

public class ActionListMyAuditNextWithFilter extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger( ActionListMyAuditNextWithFilter.class);

	protected ActionResult<List<Wo>> execute( HttpServletRequest request, String id, Integer count, JsonElement jsonElement, EffectivePerson effectivePerson ) {
		ActionResult<List<Wo>> result = new ActionResult<>();
		List<String> permissionObjectCodes = new ArrayList<>();
		Long total = 0L;
		List<Wo> wos = null;
		List<String> viewAbleReportIds = null;
		List<Report_I_Base> reportBaseList = null;
		Wi wi = null;
		Boolean check = true;
		
		
		try {
			wi = this.convertToWrapIn( jsonElement, Wi.class );
		} catch (Exception e ) {
			check = false;
			Exception exception = new ExceptionReportInfoProcess( e, "系统在将JSON信息转换为对象时发生异常。JSON:" + jsonElement.toString() );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}
		
		if ( wi == null ) {
			wi = new Wi();
		}
		
		if ( count == 0 ) {
			count = 20;
		}
		
		if (check) {
			if (check) {
				// 在查询文档列表之前先获取个人所在的组织，群组，角色标识列表和自己的唯一标识
				// 放到一个List里，提供给查询时进行权限控制{permissionObjectCode}
				try {
					permissionObjectCodes = userManagerService.getPersonPermissionCodes( effectivePerson.getDistinguishedName() );
				} catch (Exception e) {
					check = false;
					Exception exception = new ExceptionReportInfoProcess(e, "系统在根据登录用户姓名获取用户拥有的所有组织，角色，群组信息列表时发生异常。");
					result.error(exception);
					logger.error(e, effectivePerson, request, null);
				}
			}
			
			if (check) {
				// 然后到reportBasePermission表里根据要求进行条件查询并且排序后，输出下一页的ID列表
				try {
					viewAbleReportIds = report_I_PermissionServiceAdv.lisViewableReportIdsWithFilter(
							wi.getTitle(), wi.getReportType(), 
							wi.getYear(), wi.getMonth(), wi.getWeek(), wi.getReportDate(), wi.getCreateDate(),				
							wi.getActivityList(), wi.getTargetList(), wi.getCurrentPersonList(), wi.getUnitList(), wi.getReportObjType(), 
							wi.getReportStatus(), permissionObjectCodes, "审核", 1000 );
				} catch (Exception e) {
					check = false;
					Exception exception = new ExceptionReportInfoProcess(e, "系统在根据过滤条件查询用户可访问的文档ID列表时发生异常。");
					result.error(exception);
					logger.error(e, effectivePerson, request, null);
				}
			}
		}
		if (check) {
			// 从数据库中查询符合条件的对象总数
			try {
				total = report_I_ServiceAdv.countWithIds( viewAbleReportIds );
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionReportInfoProcess(e, "系统在获取用户可查询到的文档数据条目数量时发生异常。");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		if (check) {
			try {
				reportBaseList = report_I_ServiceAdv.listNextWithDocIds( id, count, viewAbleReportIds, wi.getOrderField(), wi.getOrderType(), false  );
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionReportInfoProcess(e, "系统在根据用户可访问的文档ID列表对文档进行分页查询时发生异常。");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		if (check) {
			if (reportBaseList != null) {
				try {
					wos = Wo.copier.copy(reportBaseList);
					result.setCount(total);
					result.setData(wos);
				} catch (Exception e) {
					Exception exception = new ExceptionReportInfoProcess(e, "系统在将分页查询结果转换为可输出的数据信息时发生异常。");
					result.error(exception);
					logger.error(e, effectivePerson, request, null);
				}
			}
		}
		return result;
	}
	
	public class ReportCacheForFilter {

		private Long total = 0L;
		
		private List<Wo> reportBaseList = null;

		public Long getTotal() {
			return total;
		}

		public void setTotal(Long total) {
			this.total = total;
		}

		public List<Wo> getReport_I_BaseList() {
			return reportBaseList;
		}

		public void setReport_I_BaseList(List<Wo> reportBaseList) {
			this.reportBaseList = reportBaseList;
		}	
	}
	
	public static class Wi {

		@FieldDescribe( "作为过滤条件的CMS文档关键字, 通常是标题, String, 模糊查询." )
		private String title;
		
		@FieldDescribe("汇报周期类别：MONTH|WEEK|DAILY")
		private String reportType;
		
		@FieldDescribe( "汇报年份." )
		private String year;
		
		@FieldDescribe( "汇报月份." )
		private String month;
		
		@FieldDescribe( "汇报周数" )
		private String week;

		@FieldDescribe( "汇报日期" )
		private String reportDate;
		
		@FieldDescribe( "创建日期" )
		private String createDate;
		
		@FieldDescribe( "汇报者." )
		private List<String> targetList;
		
		@FieldDescribe( "组织名称." )
		private List<String> unitList;

		@FieldDescribe( "审批状态." )
		private List<String> activityList;
		
		@FieldDescribe( "当前审核者." )
		private List<String> currentPersonList;

		@FieldDescribe( "排序列名" )
		private String orderField = "targetUnit";
		
		@FieldDescribe("汇报对象类别: PERSON | UNIT")
		private String reportObjType;
		
		@FieldDescribe( "汇报信息状态：审核中|已完成" )
		private String reportStatus = "审核中";
		
		@FieldDescribe( "排序方式" )
		private String orderType = "ASC";
		
		@FieldDescribe( "权限类型: 读者|作者|审核" )
		private String permission = "读者";
		
		public String getYear() {
			return year;
		}

		public String getMonth() {
			return month;
		}

		public String getWeek() {
			return week;
		}
		
		public List<String> getTargetList() {
			return targetList;
		}

		public List<String> getActivityList() {
			return activityList;
		}

		public String getOrderField() {
			return orderField;
		}

		public String getOrderType() {
			return orderType;
		}

		public String getTitle() {
			return title;
		}

		public void setYear(String year) {
			this.year = year;
		}

		public void setMonth(String month) {
			this.month = month;
		}

		public void setWeek(String week) {
			this.week = week;
		}
		
		public void setTargetList(List<String> targetList) {
			this.targetList = targetList;
		}

		public void setActivityList(List<String> activityList) {
			this.activityList = activityList;
		}
		
		public void setOrderField(String orderField) {
			this.orderField = orderField;
		}

		public void setOrderType(String orderType) {
			this.orderType = orderType;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public String getReportType() {
			return reportType;
		}

		public void setReportType(String reportType) {
			this.reportType = reportType;
		}

		public List<String> getCurrentPersonList() {
			return currentPersonList;
		}

		public void setCurrentPersonList(List<String> currentPersonList) {
			this.currentPersonList = currentPersonList;
		}

		public String getReportObjType() {
			return reportObjType;
		}

		public void setReportObjType(String reportObjType) {
			this.reportObjType = reportObjType;
		}

		public String getReportDate() {
			return reportDate;
		}

		public String getCreateDate() {
			return createDate;
		}

		public void setReportDate(String reportDate) {
			this.reportDate = reportDate;
		}

		public void setCreateDate(String createDate) {
			this.createDate = createDate;
		}

		public List<String> getUnitList() {
			return unitList;
		}

		public void setUnitList(List<String> unitList) {
			this.unitList = unitList;
		}

		public String getReportStatus() {
			return reportStatus;
		}

		public void setReportStatus(String reportStatus) {
			this.reportStatus = reportStatus;
		}

		public String getPermission() {
			return permission;
		}

		public void setPermission(String permission) {
			this.permission = permission;
		}
		
	}
	
	public static class Wo extends Report_I_Base  {
		
		private static final long serialVersionUID = -5076990764713538973L;
		
		public static List<String> Excludes = new ArrayList<String>();
		
		public static WrapCopier<Report_I_Base, Wo> copier = WrapCopierFactory.wo( Report_I_Base.class, Wo.class, null,Wo.Excludes);
		
	}
}
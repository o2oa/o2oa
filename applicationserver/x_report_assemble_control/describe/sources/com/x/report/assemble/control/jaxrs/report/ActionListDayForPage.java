package com.x.report.assemble.control.jaxrs.report;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.SortTools;
import com.x.report.assemble.control.jaxrs.report.exception.ExceptionReportInfoProcess;
import com.x.report.core.entity.Report_I_Base;

public class ActionListDayForPage extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionListDayForPage.class);

	protected ActionResult<List<Wo>> execute( HttpServletRequest request, Integer page, Integer count, EffectivePerson effectivePerson ) {
		ActionResult<List<Wo>> result = new ActionResult<>();
		List<String> permissionObjectCodes = new ArrayList<>();
		List<Wo> wos = new ArrayList<>();
		List<String> dateStringList = null;
		List<String> dateList = new ArrayList<>();
		List<String> viewAbleReportIds = null;
		List<Report_I_Base> reportBaseList = null;
		List<WoReport_I_Base> woRreportBaseList = null;
		Wo wo = null;
		Boolean check = true;
		
		if ( page == null || page == 0 ) {
			page = 1;
		}
		
		if ( count == null || count == 0  ) {
			count = 20;
		}

		if (check) {
			try {
				permissionObjectCodes = userManagerService.getPersonPermissionCodes( effectivePerson.getDistinguishedName() );
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionReportInfoProcess(e, "系统在根据登录用户姓名获取用户拥有的所有组织，角色，群组信息列表时发生异常。");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		
		//查询自己可以查看的汇报内容的所有日期列表，再将日期分页
		if (check) {
			try {
				dateStringList = report_I_PermissionServiceAdv.listAllCreateDateString( permissionObjectCodes );
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionReportInfoProcess(e, "系统在查询用户有权限查看汇报的所有日期列表时发生异常。");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		
		
		//将dateStringList分页，只需要指定的一页信息，并且查询每一天用户可以看到的所有汇报列表
		if (check) {
			if( dateStringList != null && !dateStringList.isEmpty() ) {
				int startIndex = count * ( page - 1 );
				int endIndex = count * page;
				for( String date : dateStringList ) {
					if( !dateList.contains( date )) {
						dateList.add( date );
					}						
				}
				
				//日期排序
				SortTools.asc( dateList );
				for( int i = startIndex; i < endIndex && i<dateList.size(); i++ ) {
					wo = new Wo();
					wo.setDate( dateList.get( i ) );
					
					//查询这一天用户可以看到的所有汇报信息列表
					if (check) {
						try {
							//查询可以看到的汇报ID列表
							viewAbleReportIds = report_I_PermissionServiceAdv.lisViewableReportIdsWithFilter(
									null, null, null, null, null, null, wo.getDate(), null, null, null, null, null, null,
									permissionObjectCodes, null, 1000 );
						} catch (Exception e) {
							check = false;
							Exception exception = new ExceptionReportInfoProcess(e, "系统在根据过滤条件查询用户可访问的文档ID列表时发生异常。");
							result.error(exception);
							logger.error(e, effectivePerson, request, null);
						}
					}
					
					if (check) {
						//查询出当天用户可查看的所有汇报信息列表
						if( viewAbleReportIds != null && !viewAbleReportIds.isEmpty() ) {
							try {
								reportBaseList = report_I_ServiceAdv.listNextWithDocIds( "(0)", 1000, viewAbleReportIds, "createTime", "DESC", false  );
								if( reportBaseList != null && !reportBaseList.isEmpty() ) {
									woRreportBaseList = WoReport_I_Base.copier.copy(reportBaseList);
									wo.setReports( woRreportBaseList );
								}
							} catch (Exception e) {
								check = false;
								Exception exception = new ExceptionReportInfoProcess(e, "系统在根据用户可访问的文档ID列表对文档进行分页查询时发生异常。");
								result.error(exception);
								logger.error(e, effectivePerson, request, null);
							}
						}
					}
					try {
						SortTools.desc( wos, "date" );
					} catch (Exception e) {
						e.printStackTrace();
					}
					wos.add( wo );
				}
			}
		}
		
		result.setCount(Long.parseLong( wos.size()+"" ));
		result.setData(wos);
		return result;
	}
	
	public static class Wo  {
		
		String date = null;
		
		List<WoReport_I_Base> reports = null;

		public String getDate() {
			return date;
		}

		public List<WoReport_I_Base> getReports() {
			return reports;
		}

		public void setDate(String date) {
			this.date = date;
		}

		public void setReports(List<WoReport_I_Base> reports) {
			this.reports = reports;
		}		
	}
	
	public static class WoReport_I_Base extends Report_I_Base  {
		
		private static final long serialVersionUID = -5076990764713538973L;
		public static List<String> Excludes = new ArrayList<>();
		
		public static WrapCopier<Report_I_Base, WoReport_I_Base> copier = WrapCopierFactory.wo( Report_I_Base.class, WoReport_I_Base.class, null,WoReport_I_Base.Excludes);
		
	}
}
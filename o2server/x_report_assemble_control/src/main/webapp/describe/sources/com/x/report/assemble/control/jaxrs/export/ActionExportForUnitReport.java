package com.x.report.assemble.control.jaxrs.export;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.base.core.project.tools.SortTools;
import com.x.report.assemble.control.jaxrs.export.exception.ExceptionDataQuery;
import com.x.report.assemble.control.jaxrs.report.exception.ExceptionReportInfoProcess;
import com.x.report.common.tools.LogUtil;
import com.x.report.core.entity.Report_I_Base;
import com.x.report.core.entity.Report_I_Ext_Content;
import com.x.report.core.entity.Report_I_Ext_ContentDetail;
import com.x.report.core.entity.Report_I_WorkInfo;
import com.x.report.core.entity.Report_I_WorkInfoDetail;
import com.x.report.core.entity.Report_P_MeasureInfo;

import net.sf.ehcache.Element;

/**
 * 导出指定组织的汇报情况
 * 每个组织一个Sheet
 * 
 * Sheet: 
 * -------------------------------------------------------------------------------------------------
 * |     部门名称    |  当月计划  |  当月总结  |  下月计划  |   服务  |  关爱  |  意见和建议（如有）
 * -------------------------------------------------------------------------------------------------
 * 默认只能导出传入的指定部门或者用户所在组织的统计信息，其他信息不会显示
 * 管理员在未传入部门列表时，可以导出所有部门的统计信息
 * 
 * @author O2LEE
 */
public class ActionExportForUnitReport extends BaseAction {
	
	private static Logger logger = LoggerFactory.getLogger(ActionExportForUnitReport.class);
	
	protected ActionResult<Wo> execute( HttpServletRequest request, EffectivePerson effectivePerson,  JsonElement jsonElement  ) {
		ActionResult<Wo> result = new ActionResult<>();
		String thisYear = null;
		String thisMonth = null;
		String nextYear = null;
		String nextMonth = null;
		Wi wi = null;
		Boolean isManager = false;
		Boolean check = true;
		Wo wo = new Wo();
		
		try {
			wi = this.convertToWrapIn( jsonElement, Wi.class );
		} catch (Exception e ) {
			check = false;
			Exception exception = new ExceptionReportInfoProcess( e, "系统在将JSON信息转换为对象时发生异常。JSON:" + jsonElement.toString() );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}
			
		if (check ) {
			try {
				if( effectivePerson.isManager() || userManagerService.isHasPlatformRole(effectivePerson.getDistinguishedName(), "ReportManager") ) {
					isManager = true;
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionReportInfoProcess( e, "系统在查询用户角色时发生异常！" );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}

		if( check ) {
			if( StringUtils.isEmpty( wi.getYear() )) {
				wi.setYear( dateOperation.getYear( new Date() ));
			}
			if( StringUtils.isEmpty( wi.getMonth() )) {
				wi.setMonth( dateOperation.getMonth( new Date() ));
			}
			
			//如果不是管理员，则只能查询自己部门的
			if( !isManager && ListTools.isEmpty( wi.getUnitList() )) {
				List<String> unitNames = null;
				try {
					unitNames = userManagerService.listUnitNamesWithPerson( effectivePerson.getDistinguishedName() );
					wi.setUnitList( unitNames );
				} catch (Exception e) {
					check = false;
					Exception exception = new ExceptionDataQuery( e, "获取登录者所在部门时发生异常！" );
					result.error( exception );
					logger.error( e, effectivePerson, request, null);
				}
			}
		}
		
		if( check ) {
			thisYear = wi.getYear();
			thisMonth = wi.getMonth();
			nextYear = wi.getYear();
			nextMonth = wi.getMonth();
			
			try {
				Calendar calendar = Calendar.getInstance();
				calendar.setTime( dateOperation.getDateFromString( thisYear + "-" + thisMonth + "-01" ));
				calendar.add( Calendar.MONTH, 1 );
				nextYear = dateOperation.getYear( calendar.getTime() );
				nextMonth = dateOperation.getMonth( calendar.getTime() );
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionDataQuery( e, "计算下一个月的月份和年份时发生异常！" );
				result.error( exception );
				logger.error( e, effectivePerson, request, null );
			}
		}
		
		/**
		 * 1、根据年份，月份，组织名称列表，查询所有的汇报信息列表和工作信息列表
		 * 2、根据所有的汇报信息ID列表查询所有的当月工作计划列表、当月工作完成情况总结信息列表、下月工作计划列表
		 * 3、根据所有的汇报信息ID列表查询五项扩展信息的列表：服务客户、员工关爱、意见建议
		 * 4、组织成一个部门汇报信息的对象，按导出的内容形式
		 */
		//1、根据年份，月份，组织名称列表，查询所有的汇报信息列表和工作信息列表
		// 1) 汇报信息列表
		List<String>reportIds = null;
		List<Report_I_Base> reportBaseInfoList = null;
		if( check ) {
			try {
				reportIds = report_I_ServiceAdv.listWithConditions( thisYear, thisMonth, wi.getUnitList(), wi.getWfProcessStatus(), 
						wi.getWfProcessStatus(), false );
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionDataQuery( e, "year:" + thisYear + ", month" + thisMonth );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}		
		if( check ) {
			if( ListTools.isNotEmpty( reportIds ) ) {
				try {
					reportBaseInfoList = report_I_ServiceAdv.listWithIds( reportIds );
					if( isManager && ListTools.isNotEmpty( reportBaseInfoList )) {
						//如果是管理员，则wi.getUnitList()为空，需要把所有涉及的组织都填写进去进行归类
						//遍历所有的report
						List<String> unitList = new ArrayList<>();
						if( ListTools.isNotEmpty( reportBaseInfoList )) {
							for( Report_I_Base reportBaseInfo : reportBaseInfoList) {
								unitList.add( reportBaseInfo.getTargetUnit() );
							}
							wi.setUnitList(unitList);
						}
					}
				} catch (Exception e) {
					check = false;
					Exception exception = new ExceptionDataQuery( e, "根据ID列表查询汇报信息时发生异常！" );
					result.error( exception );
					logger.error( e, effectivePerson, request, null);
				}
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
		
		List<String> measureInfoIds = null;
		List<Report_P_MeasureInfo> report_P_MeasureInfoList = null;
		if( check ) {
			try {
				measureInfoIds = report_P_MeasureInfoServiceAdv.listWithYear( thisYear );
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionDataQuery( e, "根据年份列表查询汇报工作举措信息时发生异常！" );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		if( check ) {
			if( ListTools.isNotEmpty( measureInfoIds ) ) {
				try {
					report_P_MeasureInfoList = report_P_MeasureInfoServiceAdv.list( measureInfoIds );
				} catch (Exception e) {
					check = false;
					Exception exception = new ExceptionDataQuery( e, "根据年份列表查询汇报工作举措信息信息时发生异常！" );
					result.error( exception );
					logger.error( e, effectivePerson, request, null);
				}
			}
		}
		
		// 3） 汇报扩展信息：服务客户、关爱员工、意见建议
		List<String>extContentIds = null;
		List<Report_I_Ext_Content> report_I_Ext_ContentList = null;
		
		if( check ) {
			if( ListTools.isNotEmpty( reportIds ) ) {
				try {
					extContentIds = report_I_Ext_ContentServiceAdv.listWithReportIds( reportIds, "汇总", null );
				} catch (Exception e) {
					check = false;
					Exception exception = new ExceptionDataQuery( e, "尝试查询汇报扩展信息对象ID列表时发生异常！" );
					result.error( exception );
					logger.error( e, effectivePerson, request, null);
				}
			}
		}
		
		//=============================================================================
		String FWKH = null;
		String YGGA = null;
		String YJJY = null;		
		ExpExtContentInfo expExtContentInfo = null;
		List<Report_I_Ext_ContentDetail> report_I_Ext_ContentDetailList  = null;
		List<ExpExtContentInfo> expExtContentInfo_list = null;
		Map<Integer, List<ExpExtContentInfo>> expExtContentInfo_rowNumber_Map = null;
		Map<String, Map<Integer, List<ExpExtContentInfo>>> expExtContentInfo_reportId_Map = new HashMap<>();
		if( check ) {
			String shortPersonName = null;
			if( ListTools.isNotEmpty( extContentIds ) ) {
				try {
					report_I_Ext_ContentList = report_I_Ext_ContentServiceAdv.list(extContentIds);
					//把所有的数据按report - rownumber分组
					if( ListTools.isNotEmpty( report_I_Ext_ContentList )) {
						for( Report_I_Ext_Content report_I_Ext_Content : report_I_Ext_ContentList ) {
							report_I_Ext_ContentDetailList = report_I_Ext_ContentServiceAdv.listDetail( report_I_Ext_Content.getId(), null );
							if( ListTools.isNotEmpty( report_I_Ext_ContentDetailList ) ) {
								for( Report_I_Ext_ContentDetail report_I_Ext_ContentDetail : report_I_Ext_ContentDetailList ) {
									if( "服务客户".equals( report_I_Ext_ContentDetail.getContentType() )) {
										FWKH = report_I_Ext_ContentDetail.getContent();
									}
									if( "员工关爱".equals( report_I_Ext_ContentDetail.getContentType() )) {
										YGGA = report_I_Ext_ContentDetail.getContent();
									}
									if( "意见建议".equals( report_I_Ext_ContentDetail.getContentType() )) {
										YJJY = report_I_Ext_ContentDetail.getContent();
									}
								}
								shortPersonName = userManagerService.getShortNameWithPersonName( report_I_Ext_Content.getTargetPerson() );
								expExtContentInfo = new ExpExtContentInfo( 
										report_I_Ext_Content.getId(), 
										report_I_Ext_Content.getOrderNumber(), 
										shortPersonName,
										FWKH, YGGA, YJJY
								);
								
								expExtContentInfo_rowNumber_Map = expExtContentInfo_reportId_Map.get( report_I_Ext_Content.getReportId() );
								if( expExtContentInfo_rowNumber_Map == null ) {
									expExtContentInfo_rowNumber_Map = new HashMap<>();
									expExtContentInfo_reportId_Map.put( report_I_Ext_Content.getReportId(), expExtContentInfo_rowNumber_Map );
								}
								
								expExtContentInfo_list = expExtContentInfo_rowNumber_Map.get( report_I_Ext_Content.getOrderNumber() );
								if( expExtContentInfo_list == null ) {
									expExtContentInfo_list = new ArrayList<>();
									expExtContentInfo_rowNumber_Map.put(report_I_Ext_Content.getOrderNumber(), expExtContentInfo_list );
								}
								expExtContentInfo_list.add( expExtContentInfo );
							}
						}
					}
				} catch (Exception e) {
					check = false;
					Exception exception = new ExceptionDataQuery( e, "尝试查询汇报扩展信息对象列表时发生异常！" );
					result.error( exception );
					logger.error( e, effectivePerson, request, null);
				}
			}
		}
		
		//==========================================================================
		
		List<ExpUnitInfo> expUnitInfoList = new ArrayList<>();
		if( check ) {
			if( ListTools.isNotEmpty( wi.getUnitList() )) {
				ExpUnitInfo expUnitInfo = null;
				ExpWork expWork = null;
				ExpWorkPlan expWorkPlan = null;
				ExpWorkPlanNext expWorkPlanNext = null;
				ExpWorkProg expWorkProg = null;
				ExpMeasuresInfo expMeasuresInfo = null;
				List<ExpWork> expWorkList = null;
				List<ExpWork> expNextMonthWorkList = null;
				List<ExpMeasuresInfo> expMeasuresInfoList = null;
				String reportId = null;
				String workInfoId = null;
				String shortUnitName = null;
				Report_I_WorkInfoDetail  report_I_WorkInfoDetail  = null;
				for( String unitName : wi.getUnitList() ) {
					try {
						shortUnitName = userManagerService.getShortNameWithUnitName( unitName );
					} catch (Exception e1) {
						shortUnitName = unitName;
					}
					LogUtil.INFO( "正在导出汇报内容" , shortUnitName );
					expUnitInfo = new ExpUnitInfo( wi.getYear(), wi.getMonth(), shortUnitName );
					expUnitInfo.setNextYear( nextYear );
					expUnitInfo.setNextMonth( nextMonth );
					
					//组织一个部门汇报情况导出对象
					expWorkList = new ArrayList<>();
					expNextMonthWorkList = new ArrayList<>();
					if( ListTools.isNotEmpty( reportBaseInfoList )) {
						for( Report_I_Base reportBaseInfo : reportBaseInfoList) {
							if( unitName.equalsIgnoreCase( reportBaseInfo.getTargetUnit() )) {
								//找到了对应的汇报信息，一般来说，一个部门一个月应该是只有1次汇报
								//找该汇报对应的所有工作信息
								reportId = reportBaseInfo.getId();
								if( ListTools.isNotEmpty( reportWorkInfoList )) {
									for( Report_I_WorkInfo report_I_WorkInfo : reportWorkInfoList) {
										workInfoId = report_I_WorkInfo.getId();
										try {
											report_I_WorkInfoDetail = report_I_WorkInfoServiceAdv.getDetailWithWorkInfoId( report_I_WorkInfo.getReportId(), workInfoId );
										} catch (Exception e1) {
											e1.printStackTrace();
										}
										expWork = new ExpWork( workInfoId, report_I_WorkInfo.getWorkTitle(), unitName);
										expWork.setOrderNumber( report_I_WorkInfo.getOrderNumber() );
										expWork.setExpWorkPlans( new ArrayList<>());
										expWork.setExpWorkProgs( new ArrayList<>());
										expWork.setExpWorkPlanNexts( new ArrayList<>());
										expWork.setExpMeasuresInfoList( new ArrayList<>());										
										
										if( reportId.equalsIgnoreCase( report_I_WorkInfo.getReportId() ) && "THISMONTH".equals( report_I_WorkInfo.getWorkMonthFlag() )) {										
											if( report_I_WorkInfoDetail != null ) {
												System.out.println("本月工作：" + report_I_WorkInfo.getId() + ", 计划：" + report_I_WorkInfoDetail.getWorkPlanSummary() );
												expWorkPlan = new ExpWorkPlan( null, null, null, report_I_WorkInfoDetail.getWorkPlanSummary() );
												expWork.getExpWorkPlans().add( expWorkPlan );
												System.out.println("本月工作：" + report_I_WorkInfo.getId() + ", 总结：" + report_I_WorkInfoDetail.getWorkProgSummary() );
												expWorkProg = new ExpWorkProg( null, null, null, report_I_WorkInfoDetail.getWorkProgSummary() );
												expWork.getExpWorkProgs().add( expWorkProg );
											}																			
											
											//查询举措信息
											if( ListTools.isNotEmpty( report_P_MeasureInfoList )
												&& ListTools.isNotEmpty( report_I_WorkInfo.getMeasuresList() )
													) {
												for( Report_P_MeasureInfo report_P_MeasureInfo : report_P_MeasureInfoList ) {
													if( report_I_WorkInfo.getMeasuresList().contains( report_P_MeasureInfo.getId() ) ) {
														expMeasuresInfo = new ExpMeasuresInfo( report_P_MeasureInfo.getId(), report_P_MeasureInfo.getTitle() );
														expMeasuresInfoList = expWork.getExpMeasuresInfoList();
														expMeasuresInfoList.add( expMeasuresInfo );
														expWork.setExpMeasuresInfoList(expMeasuresInfoList);
													}
												}
											}											
											expWorkList.add( expWork );
										}
										
										//下月计划的工作信息
										if( reportId.equalsIgnoreCase( report_I_WorkInfo.getReportId() ) && "NEXTMONTH".equals( report_I_WorkInfo.getWorkMonthFlag() )) {
											System.out.println("下月工作：" + report_I_WorkInfo.getId() + ", 计划：" + report_I_WorkInfoDetail.getWorkPlanSummary() );
											if( report_I_WorkInfoDetail != null ) {
												expWorkPlanNext = new ExpWorkPlanNext( null, null, null, report_I_WorkInfoDetail.getWorkPlanSummary() );
												expWork.getExpWorkPlanNexts().add( expWorkPlanNext );
											}
											expNextMonthWorkList.add( expWork );
										}
									}//for( Report_I_WorkInfo report_I_WorkInfo : reportWorkInfoList) 
								}
								
								//对expWorkList和expNextMonthWorkList进行整理
								//将expNextMonthWorkList里的Plan合并到expWorkList里的PlanNext里，尽量能让工作对应起来
								if( ListTools.isNotEmpty( expNextMonthWorkList )) {
									for( ExpWork expWork_nextMonth : expNextMonthWorkList ) {
										for( ExpWork expWork_thisMonth : expWorkList ) {
											if( expWork_nextMonth.getOrderNumber().intValue() == expWork_thisMonth.getOrderNumber().intValue() ) {
											//if( expWork_nextMonth.getTitle().equalsIgnoreCase( expWork_thisMonth.getTitle() )) {
												//工作位置对上了，合并一下
												expWork_thisMonth.setNextMontWorkTitle( expWork_nextMonth.getTitle() );
												expWork_thisMonth.setExpWorkPlanNexts( expWork_nextMonth.getExpWorkPlanNexts() );
												expWork_thisMonth.setChecked( true );
												expWork_nextMonth.setChecked( true );
											}
										}
									}//for( ExpWork expWork_nextMonth : expNextMonthWorkList ) 
								}
								
								//最后补齐，把没有对上的工作，添加到有空的位置
								if( ListTools.isNotEmpty( expNextMonthWorkList )) {
									for( ExpWork expWork_nextMonth : expNextMonthWorkList ) {
										if( !expWork_nextMonth.getChecked() ) {
											for( ExpWork expWork_thisMonth : expWorkList ) {
												if( !expWork_thisMonth.getChecked() ) {
													//合并一下
													expWork_thisMonth.setNextMontWorkTitle( expWork_nextMonth.getTitle() );
													expWork_thisMonth.setExpWorkPlanNexts( expWork_nextMonth.getExpWorkPlanNexts() );
													expWork_thisMonth.setChecked( true );
													expWork_nextMonth.setChecked( true );
												}
											}
										}
									}//for( ExpWork expWork_nextMonth : expNextMonthWorkList ) 
								}
								
								//将扩展信息填写上去
								ExpWork expWork_thisMonth = null;
								expExtContentInfo_rowNumber_Map = expExtContentInfo_reportId_Map.get( reportId );
								if( expExtContentInfo_rowNumber_Map != null ) {
									try {
										SortTools.asc( expWorkList, "orderNumber");
									} catch (Exception e) {
										e.printStackTrace();
									}
									for( int i=0; i< expWorkList.size(); i++ ) {
										expWork_thisMonth = expWorkList.get(i);
										expExtContentInfo_list = expExtContentInfo_rowNumber_Map.get( i+1 );
										if( ListTools.isNotEmpty( expExtContentInfo_list )) {
											expWork_thisMonth.setExpFWKH( expExtContentInfo_list );	
											expWork_thisMonth.setExpYGGAs( expExtContentInfo_list );
											expWork_thisMonth.setExpYJJY( expExtContentInfo_list );
										}
									}//for( int i=0; i< expWorkList.size(); i++ ) 
								}
								//理论上一个部门一个月只有一个汇报，如果有多个，则要都展示出来
								expUnitInfo.setExpWorkList( expWorkList );
								expUnitInfoList.add( expUnitInfo );
							}
						}//for( Report_I_Base reportBaseInfo : reportBaseInfoList) 
					}
				} //for( String unitName : wi.getUnitList() ) {
			}
		}		
		
		if( check && ListTools.isNotEmpty( expUnitInfoList )  ) {
			try {
				StringBuffer stringBuffer = new StringBuffer();
				stringBuffer.append( thisYear );
				stringBuffer.append( "年" );
				stringBuffer.append( thisMonth );
				stringBuffer.append( "月工作总结和" );
				stringBuffer.append( nextYear  );
				stringBuffer.append( "年" );
				stringBuffer.append( nextMonth  );
				stringBuffer.append( "月份工作计划.xlsx" );
				
				byte[] byteArray = new UnitReportProgExportExcelWriter().writeExcel( expUnitInfoList );
				
				//放到Cache里
				WrapFileInfo wrapFileInfo = new WrapFileInfo();
				wrapFileInfo.setId( Report_I_Base.createId() );
				wrapFileInfo.setTitle( stringBuffer.toString() );
				wrapFileInfo.setBytes( byteArray );				
				String cacheKey = ApplicationCache.concreteCacheKey( "ActionExportForUnitReport-" + wrapFileInfo.getId() );
				cache.put( new Element( cacheKey, wrapFileInfo ) );
				
				wo.setId( wrapFileInfo.getId() );
				wo.setTitle( wrapFileInfo.getTitle() );
			} catch (Exception e) {
				logger.warn("system export file got an exception");
				logger.error( e, effectivePerson, request, null);
			}
		}else {
			System.out.println("expUnitInfoList is null!");
		}
		result.setData( wo );
		return result;
	}
	
	
	public static class ExpUnitInfo {	
		private String title = null;
		private String thisYear = null;
		private String thisMonth = null;
		private String nextYear = null;
		private String nextMonth = null;
		private List<ExpWork> expWorkList = null;
		
		public ExpUnitInfo( String thisYear, String thisMonth, String title ) {
			super();
			this.title = title;
			this.thisYear = thisYear;
			this.thisMonth = thisMonth;
		}
		public String getTitle() {
			return title;
		}
		public void setTitle(String title) {
			this.title = title;
		}
		public List<ExpWork> getExpWorkList() {
			return expWorkList;
		}
		public void setExpWorkList(List<ExpWork> expWorkList) {
			this.expWorkList = expWorkList;
		}
		public String getThisYear() {
			return thisYear;
		}
		public String getThisMonth() {
			return thisMonth;
		}
		public String getNextYear() {
			return nextYear;
		}
		public String getNextMonth() {
			return nextMonth;
		}
		public void setThisYear(String thisYear) {
			this.thisYear = thisYear;
		}
		public void setThisMonth(String thisMonth) {
			this.thisMonth = thisMonth;
		}
		public void setNextYear(String nextYear) {
			this.nextYear = nextYear;
		}
		public void setNextMonth(String nextMonth) {
			this.nextMonth = nextMonth;
		}		
	}
	
	public static class ExpWork {
		private String title = null;
		private String workId = null;
		private String unitName = null;
		private Boolean checked = false;
		private String nextMontWorkTitle = null;
		private Integer orderNumber = 0;
		private List<ExpMeasuresInfo> expMeasuresInfoList = null;
		private List<ExpWorkProg> expWorkProgs = null;
		private List<ExpWorkPlan> expWorkPlans = null;
		private List<ExpWorkPlanNext> expWorkPlanNexts = null;
		private List<ExpExtContentInfo> expYGGAs = null;
		private List<ExpExtContentInfo> expFWKH = null;
		private List<ExpExtContentInfo> expYJJY = null;
		
		public ExpWork(String workId, String title, String unitName) {
			super();
			this.title = title;
			this.unitName = unitName;
			this.workId = workId;
		}
		
		public Integer getOrderNumber() {
			return orderNumber;
		}


		public void setOrderNumber(Integer orderNumber) {
			this.orderNumber = orderNumber;
		}
		public String getNextMontWorkTitle() {
			return nextMontWorkTitle;
		}

		public void setNextMontWorkTitle(String nextMontWorkTitle) {
			this.nextMontWorkTitle = nextMontWorkTitle;
		}

		public String getUnitName() {
			return unitName;
		}
		public void setUnitName(String unitName) {
			this.unitName = unitName;
		}
		public String getWorkId() {
			return workId;
		}
		public void setWorkId(String workId) {
			this.workId = workId;
		}
		public String getTitle() {
			return title;
		}
		public void setTitle(String title) {
			this.title = title;
		}
		public List<ExpMeasuresInfo> getExpMeasuresInfoList() {
			return expMeasuresInfoList;
		}
		public List<ExpWorkProg> getExpWorkProgs() {
			return expWorkProgs;
		}
		public void setExpMeasuresInfoList(List<ExpMeasuresInfo> expMeasuresInfoList) {
			this.expMeasuresInfoList = expMeasuresInfoList;
		}
		public void setExpWorkProgs(List<ExpWorkProg> expWorkProgs) {
			this.expWorkProgs = expWorkProgs;
		}
		public List<ExpWorkPlan> getExpWorkPlans() {
			return expWorkPlans;
		}
		public void setExpWorkPlans(List<ExpWorkPlan> expWorkPlans) {
			this.expWorkPlans = expWorkPlans;
		}
		public Boolean getChecked() {
			return checked;
		}
		public void setChecked(Boolean checked) {
			this.checked = checked;
		}
		public List<ExpWorkPlanNext> getExpWorkPlanNexts() {
			return expWorkPlanNexts;
		}
		public void setExpWorkPlanNexts(List<ExpWorkPlanNext> expWorkPlanNexts) {
			this.expWorkPlanNexts = expWorkPlanNexts;
		}
		public List<ExpExtContentInfo> getExpYGGAs() {
			return expYGGAs;
		}
		public List<ExpExtContentInfo> getExpFWKH() {
			return expFWKH;
		}
		public List<ExpExtContentInfo> getExpYJJY() {
			return expYJJY;
		}
		public void setExpYGGAs(List<ExpExtContentInfo> expYGGAs) {
			this.expYGGAs = expYGGAs;
		}
		public void setExpFWKH(List<ExpExtContentInfo> expFWKH) {
			this.expFWKH = expFWKH;
		}
		public void setExpYJJY(List<ExpExtContentInfo> expYJJY) {
			this.expYJJY = expYJJY;
		}
	}
	
	public static class ExpMeasuresInfo {
		private String id = null;
		private String title = null;
			
		public ExpMeasuresInfo(String id, String title) {
			super();
			this.id = id;
			this.title = title;
		}
		public String getId() {
			return id;
		}
		public String getTitle() {
			return title;
		}
		public void setId(String id) {
			this.id = id;
		}
		public void setTitle(String title) {
			this.title = title;
		}
	}
	
	public static class ExpExtContentInfo {
		private String id = null;
		private Integer rowNumber = null;
		private String person = null;
		private String FWKH = null;
		private String YGGA = null;
		private String YJJY = null;	
			
		public ExpExtContentInfo( String id, Integer rowNumber, String person, String FWKH, String YGGA, String YJJY ) {
			super();
			this.id = id;
			this.rowNumber = rowNumber;
			this.person = person;
			this.FWKH = FWKH;
			this.YGGA = YGGA;
			this.YJJY = YJJY;
		}

		public String getId() {
			return id;
		}
		
		public String getPerson() {
			return person;
		}

		public void setId(String id) {
			this.id = id;
		}
		
		public void setPerson(String person) {
			this.person = person;
		}

		public Integer getRowNumber() {
			return rowNumber;
		}

		public void setRowNumber(Integer rowNumber) {
			this.rowNumber = rowNumber;
		}

		public String getFWKH() {
			return FWKH;
		}

		public String getYGGA() {
			return YGGA;
		}

		public String getYJJY() {
			return YJJY;
		}

		public void setFWKH(String fWKH) {
			FWKH = fWKH;
		}

		public void setYGGA(String yGGA) {
			YGGA = yGGA;
		}

		public void setYJJY(String yJJY) {
			YJJY = yJJY;
		}
		
	}
	
	public static class ExpWorkPlanNext {
		private String id = null;
		private String title = null;
		private String person = null;
		private String content = null;
		public ExpWorkPlanNext(String id, String title, String person, String content) {
			super();
			this.id = id;
			this.title = title;
			this.person = person;
			this.content = content;
		}
		public String getId() {
			return id;
		}
		public void setId(String id) {
			this.id = id;
		}
		public String getTitle() {
			return title;
		}
		public String getPerson() {
			return person;
		}
		public String getContent() {
			return content;
		}
		public void setTitle(String title) {
			this.title = title;
		}
		public void setPerson(String person) {
			this.person = person;
		}
		public void setContent(String content) {
			this.content = content;
		}
	}
	
	public static class ExpWorkPlan {
		private String id = null;
		private String title = null;
		private String person = null;
		private String content = null;
		public ExpWorkPlan(String id, String title, String person, String content) {
			super();
			this.id = id;
			this.title = title;
			this.person = person;
			this.content = content;
		}
		public String getId() {
			return id;
		}
		public void setId(String id) {
			this.id = id;
		}
		public String getTitle() {
			return title;
		}
		public String getPerson() {
			return person;
		}
		public String getContent() {
			return content;
		}
		public void setTitle(String title) {
			this.title = title;
		}
		public void setPerson(String person) {
			this.person = person;
		}
		public void setContent(String content) {
			this.content = content;
		}
	}
	
	public static class ExpWorkProg {
		private String id = null;
		private String title = null;
		private String person = null;
		private String content = null;
		public ExpWorkProg(String id, String title, String person, String content) {
			super();
			this.id = id;
			this.title = title;
			this.person = person;
			this.content = content;
		}
		public String getId() {
			return id;
		}
		public void setId(String id) {
			this.id = id;
		}
		public String getTitle() {
			return title;
		}
		public String getPerson() {
			return person;
		}
		public String getContent() {
			return content;
		}
		public void setTitle(String title) {
			this.title = title;
		}
		public void setPerson(String person) {
			this.person = person;
		}
		public void setContent(String content) {
			this.content = content;
		}
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
	
	public static class Wo extends WoId {
		
		@FieldDescribe( "下载文件名称." )
		private String title = null;

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}
		
	}
}

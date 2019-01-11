package com.x.report.assemble.control.creator.profile;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.gson.Gson;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.report.assemble.control.EnumReportModules;
import com.x.report.assemble.control.dataadapter.strategy.CompanyStrategyMeasure.WoCompanyStrategy;
import com.x.report.assemble.control.dataadapter.strategy.CompanyStrategyMeasure.WoMeasuresInfo;
import com.x.report.assemble.control.dataadapter.strategy.CompanyStrategyWorks.WoCompanyStrategyWorks;
import com.x.report.assemble.control.schedule.bean.ReportCreateFlag;
import com.x.report.assemble.control.schedule.bean.ReportPersonInfo;
import com.x.report.assemble.control.schedule.bean.ReportUnitInfo;
import com.x.report.common.date.DateOperation;
import com.x.report.core.entity.Report_P_Profile;
import com.x.report.core.entity.Report_P_ProfileDetail;

public class ProfileDetailComposerStrategyMeasures {

	private Gson gson = XGsonBuilder.instance();
	private DateOperation dateOperation = new DateOperation();
	private static Logger logger = LoggerFactory.getLogger( ProfileDetailComposerStrategyMeasures.class );

	/**
	 * 	查询公司战略应用相关的信息：<br/>
	 * 1、查询部门重点工作的具体信息，所有在汇报里需要体现的数据均需要返回<br/>
	 * 2、计算汇报涉及的个人数量以及组织数量，更新到profile信息中以便后续保存<br/>
	 * @param effectivePerson
	 * @param companyStrategyMeasure_thisMonth  当年举措信息列表
	 * @param companyStrategyWorks_thisMonth  当月工作信息列表
	 * @param companyStrategyMeasure_nextMonth  次年举措信息列表
	 * @param companyStrategyWorks_nextMonth  次年工作信息列表
	 * @param profile
	 * @param reportCreateRecordDetailList
	 * @param flag
	 * @return
	 * @throws Exception
	 */
	public List<Report_P_ProfileDetail> compose( EffectivePerson effectivePerson, 
			List<WoCompanyStrategy> companyStrategyMeasure_thisMonth, 
			List<WoCompanyStrategyWorks> companyStrategyWorks_thisMonth, 
			List<WoCompanyStrategy> companyStrategyMeasure_nextMonth, 
			List<WoCompanyStrategyWorks> companyStrategyWorks_nextMonth, 
			Report_P_Profile profile, 
			List<Report_P_ProfileDetail> reportCreateRecordDetailList, 
			ReportCreateFlag flag ) throws Exception {
		
		if( profile == null ) {
			throw new Exception("reportCreateRecord is null!");
		}
		if( reportCreateRecordDetailList == null ) {
			reportCreateRecordDetailList = new ArrayList<>();
		}
		Boolean check = true; 
		
		List<ReportPersonInfo> detail_person_List = new ArrayList<>();//需要进行公司战略月报汇报的个人
		List<ReportUnitInfo> detail_unit_List = new ArrayList<>();//需要进行公司战略月报汇报的组织	

		Report_P_ProfileDetail reportCreateRecordDetail = null;
		
		if( check ) {
			//从公司战略应用模块中查询到的配置和工作数据信息中分析该次汇报所涉及的所有个人信息和组织信息
			logger.debug( effectivePerson, ">>>>>>>>>>从公司战略应用模块中查询到的配置和工作数据信息中分析该次汇报所涉及的所有个人信息和组织信息！");
			composePersonsAndUnits( companyStrategyMeasure_thisMonth, companyStrategyWorks_thisMonth, detail_person_List, detail_unit_List, flag );
			
			//组织公司战略工作模块 - 公司战略举措概要详细信息： STRATEGY_MEASURE
			logger.debug( effectivePerson, ">>>>>>>>>>组织公司战略工作模块 - 公司战略举措概要详细信息： STRATEGY_MEASURE");
			reportCreateRecordDetail = composeProfileDetailWithCompanyStrategyMeasures( profile.getId(), EnumReportModules.STRATEGY.toString(), "公司战略", "STRATEGY_MEASURE", companyStrategyMeasure_thisMonth );
			if( reportCreateRecordDetail != null ) {
				reportCreateRecordDetailList.add( reportCreateRecordDetail );
			}
			
			//组织公司战略工作模块 - 公司战略举措概要详细信息： STRATEGY_MEASURE_NEXTYEAR
			logger.debug( effectivePerson, ">>>>>>>>>>组织公司战略工作模块 - 公司战略举措概要详细信息： STRATEGY_MEASURE_NEXTMONTH");
			reportCreateRecordDetail = composeProfileDetailWithCompanyStrategyMeasures( profile.getId(), EnumReportModules.STRATEGY.toString(), "公司战略", "STRATEGY_MEASURE_NEXTMONTH", companyStrategyMeasure_nextMonth );
			if( reportCreateRecordDetail != null ) {
				reportCreateRecordDetailList.add( reportCreateRecordDetail );
			}
			
			//组织公司战略工作模块 - 组织重点工作概要详细信息： STRATEGY_WORK
			logger.debug( effectivePerson, ">>>>>>>>>>组织公司战略工作模块 - 组织重点工作概要详细信息： STRATEGY_WORK");
			reportCreateRecordDetail = composeProfileDetailWithCompanyStrategyWorks( profile.getId(), EnumReportModules.STRATEGY.toString(), "公司战略", "STRATEGY_WORK", companyStrategyWorks_thisMonth );
			if( reportCreateRecordDetail != null ) {
				reportCreateRecordDetailList.add( reportCreateRecordDetail );
			}
			
			//组织公司战略工作模块 - 组织重点工作概要详细信息： STRATEGY_WORK_NEXTYEAR
			logger.debug( effectivePerson, ">>>>>>>>>>组织公司战略工作模块 - 组织重点工作概要详细信息： STRATEGY_WORK_NEXTMONTH");
			reportCreateRecordDetail = composeProfileDetailWithCompanyStrategyWorks( profile.getId(), EnumReportModules.STRATEGY.toString(), "公司战略", "STRATEGY_WORK_NEXTMONTH", companyStrategyWorks_nextMonth );
			if( reportCreateRecordDetail != null ) {
				reportCreateRecordDetailList.add( reportCreateRecordDetail );
			}

			//组织公司战略工作模块 - 重点工作所涉及的组织列表： STRATEGY_MEASURE_UNIT
			logger.debug( effectivePerson, ">>>>>>>>>>组织公司战略工作模块 - 举措所涉及的组织列表： STRATEGY_MEASURE_UNIT");
			reportCreateRecordDetail = composeProfileDetailWithUnitList( profile.getId(), EnumReportModules.STRATEGY.toString(), "公司战略", "STRATEGY_MEASURE_UNIT", detail_unit_List );
			if( reportCreateRecordDetail != null ) {
				reportCreateRecordDetailList.add( reportCreateRecordDetail );
			}
			
			//在reportCreateRecord中记录人数【更新profile】
			logger.debug( effectivePerson, ">>>>>>>>>>在reportCreateRecord中记录人数【更新profile】");
			if( detail_person_List == null || detail_person_List.isEmpty() ) {
				profile.setWorkPersonCount( 0 );
			}else {
				profile.setWorkPersonCount( detail_person_List.size() );
			}
			
			//在reportCreateRecord中记录组织数量【更新profile】
			logger.debug( effectivePerson, ">>>>>>>>>>在reportCreateRecord中记录组织数量【更新profile】");
			if( detail_unit_List == null || detail_unit_List.isEmpty() ) {
				profile.setWorkUnitCount( 0 );
			}else {
				profile.setWorkUnitCount( detail_unit_List.size() );
			}
		}else {
			logger.warn( ">>>>>>>>>>系统未能从战略工作配置应用中查询部门重点工作信息！");
		}
		
		return reportCreateRecordDetailList;
	}

	/**
	 * 根据所有的参与组织（需要汇报的组织）生成一个明细信息对象，准备保存到数据库
	 * @param profileId
	 * @param reportModule
	 * @param reportModuleName
	 * @param snapType
	 * @param detail_unit_List
	 * @return
	 */
	private Report_P_ProfileDetail composeProfileDetailWithUnitList( String profileId, String reportModule, String reportModuleName, String snapType, List<ReportUnitInfo> detail_unit_List) {
		String json_detail_units = null;
		//将人员和组织的数据从LIST转换为JSON字符串，进行持久化处理
		if( detail_unit_List != null && !detail_unit_List.isEmpty() ) {
			json_detail_units = gson.toJson( detail_unit_List );
		}else {
			json_detail_units = "{}";
		}
		return composeProfileDetail(profileId, reportModule, reportModuleName, snapType, json_detail_units );
	}

	/**
	 * 根据所有的参与者（需要汇报的个人）生成一个明细信息对象，准备保存到数据库
	 * @param profileId
	 * @param reportModule
	 * @param reportModuleName
	 * @param snapType
	 * @param detail_person_List
	 * @return
	 */
//	private Report_P_ProfileDetail composeProfileDetailWithPersonList( String profileId, String reportModule, String reportModuleName, String snapType, List<ReportPersonInfo> detail_person_List) {
//		String json_detail_persons = null;
//		//将人员和组织的数据从LIST转换为JSON字符串，进行持久化处理
//		if( detail_person_List != null && !detail_person_List.isEmpty() ) {
//			json_detail_persons = gson.toJson( detail_person_List );
//		}else {
//			json_detail_persons = "{}";
//		}
//		return composeProfileDetail(profileId, reportModule, reportModuleName, snapType, json_detail_persons );
//	}

	/**
	 * 根据各部门重点工作生成一个明细信息对象，准备保存到数据库
	 * @param profileId
	 * @param reportModule
	 * @param reportModuleName
	 * @param snapType
	 * @param companyStrategyWorks
	 * @return
	 */
	private Report_P_ProfileDetail composeProfileDetailWithCompanyStrategyWorks( String profileId, String reportModule, String reportModuleName, String snapType, List<WoCompanyStrategyWorks> companyStrategyWorks) {
		String json_companyStrategyWorks = null;
		//将人员和组织的数据从LIST转换为JSON字符串，进行持久化处理
		if( companyStrategyWorks != null && !companyStrategyWorks.isEmpty() ) {
			json_companyStrategyWorks = gson.toJson( companyStrategyWorks );
		}else {
			json_companyStrategyWorks = "{}";
		}
		return composeProfileDetail(profileId, reportModule, reportModuleName, snapType, json_companyStrategyWorks );
	}

	/**
	 * 根据公司战略举措信息生成一个明细信息对象，准备保存到数据库
	 * @param profileId
	 * @param reportModule
	 * @param reportModuleName
	 * @param snapType
	 * @param companyStrategyMeasures
	 * @return
	 */
	private Report_P_ProfileDetail composeProfileDetailWithCompanyStrategyMeasures( String profileId, String reportModule, String reportModuleName, String snapType, List<WoCompanyStrategy> companyStrategyMeasures ) {
		String json_companyStrategyMeasures = null;
		//将人员和组织的数据从LIST转换为JSON字符串，进行持久化处理
		if( companyStrategyMeasures != null && !companyStrategyMeasures.isEmpty() ) {
			json_companyStrategyMeasures = gson.toJson( companyStrategyMeasures );
		}else {
			json_companyStrategyMeasures = "{}";
		}
		return composeProfileDetail( profileId, reportModule, reportModuleName, snapType, json_companyStrategyMeasures );
	}
	
	/**
	 * 根据已知信息直接组织一个【汇报概要文件】详细信息对象<br/>
	 * 【汇报概要文件】可能有多个详细信息，每一个详细信息对象都由一个JSON表示，如：战略举措信息，部门重点工作信息等等<br/>
	 * 
	 * @param profileId - 详细信息所对应的profileId
	 * @param reportModule - 详细信息所涉及的应用模块（名称）
	 * @param reportModuleName - 详细信息所涉及的应用模块名称
	 * @param snapType  - 详细信息类别
	 * @param snapContent - 详细信息JSON内容
	 * @return Report_P_ProfileDetail
	 */
	private Report_P_ProfileDetail composeProfileDetail( String profileId, String reportModule, String reportModuleName, String snapType, String snapContent ) {	
		Report_P_ProfileDetail detail = new Report_P_ProfileDetail();
		detail.setProfileId(profileId);
		detail.setReportModule( reportModule );
		detail.setReportModuleName( reportModuleName );
		detail.setSnapContent( snapContent );
		detail.setSnapType( snapType );
		return detail;
	}

	/**
	 * 获取公司战略工作涉及到的所有个人信息列表以及组织信息列表<br/>
	 * 
	 * 以detail_person_List和detail_unit_List接受数据<br/>
	 * 
	 * @param companyStrategyMeasure
	 * @param companyStrategyWorks
	 * @param detail_person_List - 用来接收个人信息列表的集合
	 * @param detail_unit_List - 用来接收组织信息列表的集合
	 * @param flag
	 */
	private void composePersonsAndUnits( List<WoCompanyStrategy> companyStrategyMeasure, List<WoCompanyStrategyWorks> companyStrategyWorks, List<ReportPersonInfo> detail_person_List, List<ReportUnitInfo> detail_unit_List, ReportCreateFlag flag ) {
		if( detail_person_List == null ) {
			detail_person_List = new ArrayList<>();
		}
		if( detail_unit_List == null ) {
			detail_unit_List = new ArrayList<>();
		}
		List<String> deptNames = new ArrayList<>();
		String today = dateOperation.getDate(new Date());
		List<String> deptList = null;
		
		if( ListTools.isNotEmpty(companyStrategyMeasure)) {
			ReportUnitInfo unitInfo = null;
			for( WoCompanyStrategy woCompanyStrategy : companyStrategyMeasure ) {
				if( ListTools.isNotEmpty(woCompanyStrategy.getMeasureList())) {
					for( WoMeasuresInfo woMeasuresInfo : woCompanyStrategy.getMeasureList()) {
						deptList = woMeasuresInfo.getDeptlist();
						if( ListTools.isNotEmpty( deptList ) ) {
							for( String unitName : deptList ) {
								if( !deptNames.contains( unitName )) {
									//创建一个汇报组织信息，加入到detail_unit_List中
									unitInfo = new ReportUnitInfo();
									unitInfo.setName( unitName );
									unitInfo.setReportDate( today );
									if( unitInfo.getReportModules() == null ) {
										unitInfo.setReportModules( new ArrayList<>());
									}
									if( !unitInfo.getReportModules().contains( EnumReportModules.STRATEGY )) {
										unitInfo.getReportModules().add( EnumReportModules.STRATEGY );
									}
									unitInfo.setReportMonth( flag.getReportMonth() );
									unitInfo.setReportType( flag.getReportType() );
									unitInfo.setReportWeek( flag.getReportWeek() );
									unitInfo.setReportYear( flag.getReportYear() );
									detail_unit_List.add( unitInfo );
									deptNames.add( unitName );
								}
							}
						}
					}
				}
			}
		}
	}
}

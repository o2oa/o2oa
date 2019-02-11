package com.x.report.assemble.control.creator.profile;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.project.http.EffectivePerson;
import com.x.report.assemble.control.EnumReportModules;
import com.x.report.assemble.control.dataadapter.strategy.CompanyStrategyMeasure;
import com.x.report.assemble.control.dataadapter.strategy.CompanyStrategyMeasure.WoCompanyStrategy;
import com.x.report.assemble.control.dataadapter.strategy.CompanyStrategyWorks;
import com.x.report.assemble.control.dataadapter.strategy.CompanyStrategyWorks.WoCompanyStrategyWorks;
import com.x.report.assemble.control.schedule.bean.ReportCreateFlag;
import com.x.report.core.entity.Report_P_Profile;
import com.x.report.core.entity.Report_P_ProfileDetail;

/**
 * 所有模块需要的生成汇报的依据详细信息服务类
 * 
 * @author O2LEE
 *
 */
public class ProfileDetailComposer {
	
	/**
	 * 组织所有模块需要的生成汇报的依据详细信息内容<br/>
	 * 可能需要通过restfull接口去各个应用系统里查询<br/>
	 * 在该方法中还将对recordProfile对象中的组织数据，个人数量信息进行更新<br/>
	 * <br/>
	 * （注意：目前只实现了战略管理相关内容）<br/>
	 * 
	 * @param recordProfile
	 * @param companyStrategyMeasure_thisYear  当年举措信息列表
	 * @param companyStrategyWorks_thisMonth  当月工作信息列表
	 * @param companyStrategyMeasure_nextYear  次年举措信息列表
	 * @param companyStrategyWorks_nextMonth  次年工作信息列表
	 * @param moduleNames
	 * @param flag
	 * @return List<Report_P_ProfileDetail>
	 * @throws Exception 
	 */
	public List<Report_P_ProfileDetail> profileDetailGetter( 
			EffectivePerson effectivePerson, 
			List<CompanyStrategyMeasure.WoCompanyStrategy> companyStrategyMeasure, 
			List<CompanyStrategyWorks.WoCompanyStrategyWorks> companyStrategyWorks, 
			List<WoCompanyStrategy> companyStrategyMeasure_nextMonth, 
			List<WoCompanyStrategyWorks> companyStrategyWorks_nextMonth, 
			Report_P_Profile recordProfile, 
			String[] moduleNames, ReportCreateFlag flag) throws Exception {
		if( moduleNames == null || moduleNames.length == 0 ) {
			return null;
		}
		List<Report_P_ProfileDetail> recordProfileDetailList = new ArrayList<>();
		//遍历所有的模块，收集汇报展示需要的信息内容，保存各应用汇报内容“信息快照”
		//根据不同的模块，从系统中获取不同的信息，持久化到数据库中
		for( String moduleName : moduleNames ) {
			if( EnumReportModules.STRATEGY.toString().equalsIgnoreCase( moduleName )) {
				try{
					recordProfileDetailList = new ProfileDetailComposerStrategyMeasures().compose( 
							effectivePerson, companyStrategyMeasure, companyStrategyWorks, companyStrategyMeasure_nextMonth, 
							companyStrategyWorks_nextMonth, recordProfile, recordProfileDetailList, flag );
				}catch( Exception e ) {
					throw e;
				}
			}
//			else if( EnumReportModules.BBS.toString().equalsIgnoreCase( moduleName )) {
//				//TODO(uncomplete) 查询BBS应用相关的统计信息
//			}else if( EnumReportModules.CMS.toString().equalsIgnoreCase( moduleName )) {
//				//TODO(uncomplete) 查询CMS应用相关的统计信息
//			}else if( EnumReportModules.MEETTING.toString().equalsIgnoreCase( moduleName )) {
//				//TODO(uncomplete) 查询会议管理应用相关的统计信息
//			}else if( EnumReportModules.OKR.toString().equalsIgnoreCase( moduleName )) {
//				//TODO(uncomplete) 查询OKR应用相关的统计信息
//			}else if( EnumReportModules.WORKFLOW.toString().equalsIgnoreCase( moduleName )) {
//				//TODO(uncomplete) 查询工作流待办已办等应用相关的统计信息
//			}else if( EnumReportModules.ATTENDANCE.toString().equalsIgnoreCase( moduleName )) {
//				//TODO(uncomplete) 查询考勤应用相关的统计信息
//			}
		}
		return recordProfileDetailList;
	}
}

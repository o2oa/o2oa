package com.x.report.assemble.control.creator.profile;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.report.assemble.control.EnumReportTypes;
import com.x.report.assemble.control.dataadapter.strategy.CompanyStrategyMeasure;
import com.x.report.assemble.control.dataadapter.strategy.CompanyStrategyWorks;
import com.x.report.assemble.control.schedule.bean.ReportCreateFlag;
import com.x.report.assemble.control.service.Report_P_MeasureInfoServiceAdv;
import com.x.report.assemble.control.service.Report_P_ProfileServiceAdv;
import com.x.report.common.date.DateOperation;
import com.x.report.core.entity.Report_P_Profile;
import com.x.report.core.entity.Report_P_ProfileDetail;

public class MonthReportProfileCreator {
	
	private static  Logger  logger = LoggerFactory.getLogger( MonthReportProfileCreator.class );
	private Report_P_ProfileServiceAdv report_R_CreateServiceAdv = new Report_P_ProfileServiceAdv();
    private Report_P_MeasureInfoServiceAdv report_P_MeasureInfoServiceAdv = new Report_P_MeasureInfoServiceAdv();
	private DateOperation dateOperation = new DateOperation();
	/**
	 * 根据指定的汇报周期，汇报类别，创建【汇报概要文件】Report_P_Profile，并且持久化到数据<br/>
	 * 后续生成汇报文件，启动汇报流程全部依据【汇报概要文件】Report_P_Profile的ID为参数<br/>
	 * 
	 * @param flag
	 * @return
	 * @throws Exception
	 */
	public Report_P_Profile createProfile( EffectivePerson effectivePerson, ReportCreateFlag flag) throws Exception {
		
		Report_P_Profile profile = null;
		String nextYear = flag.getReportYear() ;
    	String nextMonth = flag.getReportMonth();
    	
		//判断是否有模块需要进行汇报，如果没有，则不需要后续的汇报生成工作，直接返回即可
		if( flag.getReport_modules() == null || "NONE".equalsIgnoreCase( flag.getReport_modules().trim() ) || flag.getReport_modules().isEmpty() ) {
			logger.warn("there no application needs report every month!");
		}else {
            List<CompanyStrategyMeasure.WoCompanyStrategy> companyStrategyMeasure = null;
            List<CompanyStrategyWorks.WoCompanyStrategyWorks> companyStrategyWorks = null;
            List<CompanyStrategyMeasure.WoCompanyStrategy> companyStrategyMeasure_nextMonth = null;
            List<CompanyStrategyWorks.WoCompanyStrategyWorks> companyStrategyWorks_nextMonth = null;
            
            try {
                companyStrategyMeasure = new CompanyStrategyMeasure().all( flag.getReportYear() );
		    }catch( Exception e ) {
			    logger.info( ">>>>>>>>>>接口调用异常：尝试从战略工作配置应用中查询战略举措配置信息时发生异常！");
			    e.printStackTrace();
			    throw e;
			}
            
            try {
                companyStrategyWorks = new CompanyStrategyWorks().all( flag.getReportYear(), flag.getReportMonth() );
			 }catch( Exception e ) {
			    logger.info( " >>>>>>>>>>接口调用异常：尝试从战略工作配置应用中查询部门重点工作信息时发生异常！");
			    e.printStackTrace();
			    throw e;
			 }
            
            if( "12".equals( flag.getReportMonth() )) {
            	//次月是下一年了
            	nextYear = (  Integer.parseInt( flag.getReportYear() ) + 1 )+"" ;
            	nextMonth = "01";
            	try {
            		companyStrategyMeasure_nextMonth = new CompanyStrategyMeasure().all( nextYear );
    		    }catch( Exception e ) {
    			    logger.info( ">>>>>>>>>>接口调用异常：尝试从战略工作配置应用中查询战略举措配置信息时发生异常！");
    			    e.printStackTrace();
    			    throw e;
    			}
                
                try {
                    companyStrategyWorks_nextMonth = new CompanyStrategyWorks().all( nextYear, nextMonth );
    			 }catch( Exception e ) {
    			    logger.info( " >>>>>>>>>>接口调用异常：尝试从战略工作配置应用中查询部门重点工作信息时发生异常！");
    			    e.printStackTrace();
    			    throw e;
    			 }
            }else {
            	companyStrategyMeasure_nextMonth = companyStrategyMeasure;
            	
            	int month =  Integer.parseInt( flag.getReportMonth())  + 1;
            	if( month < 10 ) {
            		nextMonth = "0" + month ;
            	}else {
            		nextMonth = "" + month ;
            	}
            	
                try {
                	companyStrategyWorks_nextMonth = new CompanyStrategyWorks().all( nextYear, nextMonth );
    			 }catch( Exception e ) {
    			    logger.info( " >>>>>>>>>>接口调用异常：尝试从战略工作配置应用中查询部门重点工作信息时发生异常！");
    			    e.printStackTrace();
    			    throw e;
    			 }
            }

			List<Report_P_ProfileDetail> recordProfileDetailList = null;
			String[] moduleNames = flag.getReport_modules().split( "," );//涉及月度汇报的应用名称
					
			//提前先创建一个【汇报概要信息】对象，【汇报概要信息】对象的id需要在后续组织【汇报概要详细信息】时用到
			profile = newProfile( flag ); //汇报生成依据内容记录信息

			logger.info( ">>>>>>>>>>组织所有模块需要的【汇报概要详细信息】内容......");
			recordProfileDetailList = new ProfileDetailComposer().profileDetailGetter( 
					effectivePerson, companyStrategyMeasure, companyStrategyWorks, companyStrategyMeasure_nextMonth, companyStrategyWorks_nextMonth, profile, moduleNames, flag );
					
			//形成一份完整的汇报生成的依据（类似于信息快照，后续工作完全按照此概要生成每份汇报信息）
			logger.info( ">>>>>>>>>>将查询的【汇报概要信息】和【汇报概要详细信息】全部保存到数据库......");
			profile.setReportDate( new Date() );
			
			if( StringUtils.isNotEmpty( flag.getReport_modules() )) {
				profile.setModuleCount( flag.getReport_modules().split( "," ).length );
				profile.setModules( flag.getReport_modules() );
			}
			profile.setReportDateString( dateOperation.getNowDate() );
			profile = report_R_CreateServiceAdv.save( profile, recordProfileDetailList );

			//根据获取的最新信息添加或者更新所有的战略举措信息
            report_P_MeasureInfoServiceAdv.saveOrUpdateMeasureInfo( companyStrategyMeasure );
		}
		return profile;
	}



    /**
	 * 创建一个新的汇报概要文件
	 * 
	 * @param flag
	 * @return
	 * @throws Exception 
	 */
	private Report_P_Profile newProfile( ReportCreateFlag flag ) throws Exception {
		Report_P_Profile recordProfile = new Report_P_Profile(); //汇报生成依据内容记录信息
		recordProfile.setId( Report_P_Profile.createId() );
		recordProfile.setReportType( EnumReportTypes.MONTHREPORT.toString() );
		recordProfile.setReportYear( flag.getReportYear() );
		recordProfile.setReportMonth( flag.getReportMonth() );
		recordProfile.setReportDate( null );
		recordProfile.setReportDateString( null );
		recordProfile.setCreateDateString( dateOperation.getDateFromDate( flag.getSendDate(), "yyyy-MM-dd") );
		return recordProfile;
	}	
}

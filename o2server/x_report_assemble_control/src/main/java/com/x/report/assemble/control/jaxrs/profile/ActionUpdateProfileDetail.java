package com.x.report.assemble.control.jaxrs.profile;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.http.WrapOutMap;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.report.assemble.control.EnumReportTypes;
import com.x.report.assemble.control.creator.profile.ProfileDetailComposer;
import com.x.report.assemble.control.dataadapter.strategy.CompanyStrategyMeasure;
import com.x.report.assemble.control.dataadapter.strategy.CompanyStrategyWorks;
import com.x.report.assemble.control.jaxrs.profile.exception.ExceptionProfileNotExists;
import com.x.report.assemble.control.jaxrs.report.exception.ExceptionReportInfoProcess;
import com.x.report.assemble.control.schedule.bean.ReportCreateFlag;
import com.x.report.core.entity.Report_P_Profile;
import com.x.report.core.entity.Report_P_ProfileDetail;

/**
 * 更新概要信息中的详细信息内容（从战略系统里重新获取）
 * 
 * 适用于战略举措有变化，需要从战备管理系统中进行数据更新
 * @author O2LEE
 *
 */
public class ActionUpdateProfileDetail extends BaseAction {
	
	private Logger logger = LoggerFactory.getLogger( ActionUpdateProfileDetail.class );
	
	protected ActionResult<WrapOutMap> execute( HttpServletRequest request, EffectivePerson effectivePerson, String profileId ) throws Exception {
		ActionResult<WrapOutMap> result = new ActionResult<>();
		WrapOutMap wrapOutMap = new WrapOutMap();
		Report_P_Profile profile = null;
		Boolean check = true;
		List<CompanyStrategyMeasure.WoCompanyStrategy> companyStrategyMeasure = null;
        List<CompanyStrategyWorks.WoCompanyStrategyWorks> companyStrategyWorks = null;
        List<CompanyStrategyMeasure.WoCompanyStrategy> companyStrategyMeasure_nextMonth = null;
        List<CompanyStrategyWorks.WoCompanyStrategyWorks> companyStrategyWorks_nextMonth = null;
        List<Report_P_ProfileDetail> recordProfileDetailList = null;
		ReportCreateFlag flag = new ReportCreateFlag();
		String nextYear = null ;
    	String nextMonth = null;
        
        if( check ){
			try {
				profile = report_P_ProfileServiceAdv.get( profileId );
				if( profile == null ) {
					check = false;
					Exception exception = new ExceptionProfileNotExists( profileId );
					result.error(exception);
				}
			}catch( Exception e ) {
				check = false;
				Exception exception = new ExceptionReportInfoProcess(e, "系统在根据ID查询汇报概要文件信息ID列表时发生异常。");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
        
        if( check ){
        	 try {
                 companyStrategyMeasure = new CompanyStrategyMeasure().all( profile.getReportYear() );
 		    }catch( Exception e ) {
 		    	check = false;
				Exception exception = new ExceptionReportInfoProcess(e, "尝试从战略工作配置应用中查询战略举措配置信息时发生异常！");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
 			}
        }
        
        if( check ){
        	try {
                companyStrategyWorks = new CompanyStrategyWorks().all( profile.getReportYear(), profile.getReportMonth() );
			 }catch( Exception e ) {
				check = false;
				Exception exception = new ExceptionReportInfoProcess(e, "尝试从战略工作配置应用中查询部门重点工作信息时发生异常！");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			 }
        }
        
        if( check ){
        	nextYear = profile.getReportYear() ;
        	nextMonth = profile.getReportMonth();
        	
        	//跨年的话，要把下一年的举措也查出来
        	if( "12".equals( profile.getReportMonth() )) {
            	//次月是下一年了
            	nextYear = (  Integer.parseInt( profile.getReportYear() ) + 1 )+"" ;
            	nextMonth = "01";
            	
            	try {
            		companyStrategyMeasure_nextMonth = new CompanyStrategyMeasure().all( nextYear );
    		    }catch( Exception e ) {
    			    check = false;
    				Exception exception = new ExceptionReportInfoProcess(e, "接口调用异常：尝试从战略工作配置应用中查询战略举措配置信息时发生异常！");
    				result.error(exception);
    				logger.error(e, effectivePerson, request, null);
    			}
                
                try {
                    companyStrategyWorks_nextMonth = new CompanyStrategyWorks().all( nextYear, nextMonth );
    			 }catch( Exception e ) {
    			    check = false;
    				Exception exception = new ExceptionReportInfoProcess(e, "接口调用异常：尝试从战略工作配置应用中查询部门重点工作信息时发生异常！");
    				result.error(exception);
    				logger.error(e, effectivePerson, request, null);
    			 }
            }else {
            	companyStrategyMeasure_nextMonth = companyStrategyMeasure;
            	int month =  Integer.parseInt( profile.getReportMonth())  + 1;
            	if( month < 10 ) {
            		nextMonth = "0" + month ;
            	}else {
            		nextMonth = "" + month ;
            	}
                try {
                	companyStrategyWorks_nextMonth = new CompanyStrategyWorks().all( nextYear, nextMonth );
    			 }catch( Exception e ) {
    			    check = false;
    				Exception exception = new ExceptionReportInfoProcess(e, "接口调用异常：尝试从战略工作配置应用中查询部门重点工作信息时发生异常！");
    				result.error(exception);
    				logger.error(e, effectivePerson, request, null);
    			 }
            }
        }
        
		if( check ){
			try {
 				flag.setReport_modules( profile.getModules() );
				flag.setReportMonth( profile.getReportMonth() );
				flag.setReportType(  EnumReportTypes.MONTHREPORT );
				flag.setReportWeek( profile.getReportWeek() );
				flag.setReportYear( profile.getReportYear() );
				String[] modules = {profile.getModules()};
				
				recordProfileDetailList = new ProfileDetailComposer().profileDetailGetter(effectivePerson, companyStrategyMeasure, companyStrategyWorks, 
						companyStrategyMeasure_nextMonth, companyStrategyWorks_nextMonth, profile, modules, flag );
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionReportInfoProcess(e, "尝试组织汇报概要详细信息时发生异常！");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		
		if( check ){
			try {
				report_R_CreateServiceAdv.updateDetails( profile.getId(), recordProfileDetailList );
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionReportInfoProcess(e, "尝试更新汇报概要详细信息时发生异常！");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		
		if( check ){
			try {
				result.setData( wrapOutMap );
			}catch( Exception e ) {
				Exception exception = new ExceptionReportInfoProcess(e, "系统在根据汇报概要文件重新生成或者补充汇报信息时发生异常。");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		return result;
	}
}
package com.x.report.assemble.control.creator.document.month;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.report.assemble.control.schedule.bean.ReportUnitInfo;
import com.x.report.assemble.control.service.Report_I_ServiceAdv;
import com.x.report.assemble.control.service.Report_P_ProfileServiceAdv;
import com.x.report.assemble.control.service.Report_S_SettingServiceAdv;
import com.x.report.assemble.control.service.UserManagerService;
import com.x.report.core.entity.Report_P_Profile;

public class ReportDocumentCreator {

	private Logger logger = LoggerFactory.getLogger( ReportDocumentCreator.class );
	private Gson gson = XGsonBuilder.instance();
	private UserManagerService userManagerService = new UserManagerService();
	private UnitReportCreator unitMonthReportCreator = new UnitReportCreator();
	private Report_I_ServiceAdv report_I_ServiceAdv = new Report_I_ServiceAdv();
	private Report_S_SettingServiceAdv report_S_SettingServiceAdv = new Report_S_SettingServiceAdv();
	private Report_P_ProfileServiceAdv report_P_ProfileServiceAdv = new Report_P_ProfileServiceAdv();


    /**
     * 根据汇报创建信息ID创建所有的个人和组织的汇报文档
     * @param effectivePerson
     * @param recordProfile.getId()
     * @return
     * @throws Exception
     */
	public Report_P_Profile create( EffectivePerson effectivePerson, Report_P_Profile recordProfile ) throws Exception {
		Type type = null;
		List<ReportUnitInfo> unitNames = null;
		//查询汇报创建信息
		Report_P_Profile profile = report_P_ProfileServiceAdv.get( recordProfile.getId() );
		if( profile == null ) {
			throw new Exception("report create record not exists!");
		}
		
		//查询汇报创建所有组织信息, 查询所有举措涉及的组织信息
		String json_units = report_P_ProfileServiceAdv.getDetailValue( recordProfile.getId(), "STRATEGY", "STRATEGY_MEASURE_UNIT" );
		type = new TypeToken<ArrayList<ReportUnitInfo>>() {}.getType();
		if( json_units != null && json_units.length() > 2 ) {
	        unitNames = gson.fromJson( json_units, type );
		}

		if( ListTools.isNotEmpty( unitNames )) {
			for( ReportUnitInfo unit : unitNames ) {//为指定的组织生成汇报信息
				//获取组织相关的所有工作列表
				//logger.info( ">>>>>>>>>>系统正在检查组织【"+unit.getName()+"】月度汇报文档是否已经存在.....");
				if( !report_I_ServiceAdv.reportExists( recordProfile.getId(), unit.getName() ) ) {
					logger.info( ">>>>>>>>>>为组织【"+unit.getName()+"】生成月度汇报文档信息:");
					createDocument( effectivePerson, profile, unit);
					recordProfile.setCreateDocumentCount( recordProfile.getCreateDocumentCount() + 1 );
				}else {
					logger.info( ">>>>>>>>>>组织【"+unit.getName()+"】月度汇报文档已经存在!");
				}
			}
		}		
		return recordProfile;
	}

	private Boolean createDocument( EffectivePerson effectivePerson, Report_P_Profile profile, ReportUnitInfo unit) {
		logger.debug( effectivePerson, ">>>>>>>>>>为组织["+unit.getName()+"]生成月度汇报文档信息......" );
		List<String> unitReportIdentityList = null;
		String UNITREPORT_DUTY = null;
		
		try {
			UNITREPORT_DUTY = report_S_SettingServiceAdv.getValueByCode( "UNITREPORT_DUTY" );
			if( UNITREPORT_DUTY != null && !UNITREPORT_DUTY.isEmpty() ) {//获取需要生成组织汇报的职务所涉及的个人
				unitReportIdentityList = userManagerService.listIdentityWithDutyInUnit( unit.getName(), UNITREPORT_DUTY.split( "," ) );
			}
		}catch( Exception e ) {
			e.printStackTrace();
		}
		
		if( ListTools.isNotEmpty(unitReportIdentityList) ) {
			for( String identity : unitReportIdentityList ) {
				try {
					unitMonthReportCreator.create( effectivePerson, profile,  identity, unit.getName() );
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}else{
            logger.warn( ">>>>>>>>>>组织【"+unit.getName()+"】没有职务["+UNITREPORT_DUTY+"]尚无法生成组织汇报文档。" );
        }
		return true;
	}	
}

package com.x.report.assemble.control.service;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.report.assemble.control.Business;
import com.x.report.assemble.control.EnumReportModules;
import com.x.report.common.date.DateOperation;
import com.x.report.core.entity.Report_S_Setting;
import com.x.report.core.entity.Report_S_SettingLobValue;

/**
 * 汇报系统设置服务类
 * @author O2LEE
 *
 */
public class Report_S_SettingInitService {
	
	private Logger logger = LoggerFactory.getLogger( Report_S_SettingInitService.class );

	public void initAllSystemConfig() {
		String value = null, description = null, type = null, selectContent = null;
		Boolean isMultiple = false;
		Boolean isLob = false;
		Integer ordernumber = 0;
		
		value = "false";
		type = "select";
		selectContent = "true|false";
		isMultiple = false;
		isLob = false;
		description = "是否开启月度汇报功能：可选值为true|false，单值。此属性控制系统是否开启月度汇报功能.";
		try {
			checkAndInitSystemConfig("MONTHREPORT_ENABLE", "是否月度开启汇报功能", value, isLob, description, type, selectContent, isMultiple,  ++ordernumber );
		} catch (Exception e) {
			logger.warn( "system init system config 'MONTHREPORT_ENABLE' got an exception." );
			logger.error(e);
		}
		
		value = "false";
		type = "unit";
		selectContent = "true|false";
		isMultiple = true;	
		isLob = true;
		description = "使用月度汇报的组织。可选值为组织名称。此属性控制使用月度汇报功能的所有组织列表，逗号分隔.";
		try {
			checkAndInitSystemConfig("MONTHREPORT_UNIT", "使用月度汇报的组织", value, isLob, description, type, selectContent, isMultiple,  ++ordernumber );
		} catch (Exception e) {
			logger.warn( "system init system config 'MONTHREPORT_UNIT' got an exception." );
			logger.error(e);
		}
		
		value = "false";
		type = "select";
		selectContent = "true|false";
		isMultiple = false;
		isLob = false;
		description = "是否开启每周度汇报功能：可选值为true|false，单值。此属性控制系统是否开启每周汇报功能.";
		try {
			checkAndInitSystemConfig("WEEKREPORT_ENABLE", "是否每周开启汇报功能", value, isLob, description, type, selectContent, isMultiple,  ++ordernumber );
		} catch (Exception e) {
			logger.warn( "system init system config 'WEEKREPORT_ENABLE' got an exception." );
			logger.error(e);
		}
		
		value = "false";
		type = "unit";
		selectContent = "true|false";
		isMultiple = true;	
		isLob = true;
		description = "使用每周汇报的组织。可选值为组织名称。此属性控制使用每周汇报功能的所有组织列表，逗号分隔.";
		try {
			checkAndInitSystemConfig("WEEKREPORT_UNIT", "使用每周汇报的组织", value, isLob, description, type, selectContent, isMultiple,  ++ordernumber );
		} catch (Exception e) {
			logger.warn( "system init system config 'WEEKREPORT_UNIT' got an exception." );
			logger.error(e);
		}
		
		value = "false";
		type = "select";
		selectContent = "true|false";
		isMultiple = false;
		isLob = false;
		description = "是否开启每日度汇报功能：可选值为true|false，单值。此属性控制系统是否开启每日汇报功能.";
		try {
			checkAndInitSystemConfig("DAYREPORT_ENABLE", "是否每日开启汇报功能", value, isLob, description, type, selectContent, isMultiple,  ++ordernumber );
		} catch (Exception e) {
			logger.warn( "system init system config 'DAYREPORT_ENABLE' got an exception." );
			logger.error(e);
		}
		
		value = "false";
		type = "unit";
		selectContent = "true|false";
		isMultiple = true;	
		isLob = true;
		description = "使用每日汇报的组织。可选值为组织名称。此属性控制使用每日汇报功能的所有组织列表，逗号分隔.";
		try {
			checkAndInitSystemConfig("DAYREPORT_UNIT", "使用每日汇报的组织", value, isLob, description, type, selectContent, isMultiple,  ++ordernumber );
		} catch (Exception e) {
			logger.warn( "system init system config 'DAYREPORT_UNIT' got an exception." );
			logger.error(e);
		}
		
		value = "THIS_MONTH";
		type = "select";
		selectContent = "THIS_MONTH|NEXT_MONTH";
		isMultiple = false;
		isLob = false;
		description = "每月汇报发起日期：可选值：THIS_MONTH|NEXT_MONTH。此配置控制月度汇报发起的时机，是当月发起，还是次月发起。";
		try {
			checkAndInitSystemConfig("REPORT_MONTH_DAYTYPE", "每月汇报发起日期", value, isLob, description, type, selectContent, isMultiple, ++ordernumber );
		} catch (Exception e) {
			logger.warn( "system init system config 'REPORT_MONTH_DAYTYPE' got an exception." );
			logger.error(e);
		}
		
		value = "31";
		type = "day_for_month";
		selectContent = "";
		isMultiple = false;
		isLob = false;
		description = "每月汇报发起日期：可选值：月份中的第几日。此配置控制月度汇报发起的时间。";
		try {
			checkAndInitSystemConfig("REPORT_MONTH_DAY", "每月汇报发起日期", value, isLob, description, type, selectContent, isMultiple, ++ordernumber );
		} catch (Exception e) {
			logger.warn( "system init system config 'REPORT_MONTH_DAY' got an exception." );
			logger.error(e);
		}
		
		value = "09:00:00";
		type = "time";
		selectContent = null;
		isMultiple = false;
		isLob = false;
		description = "每月汇报发起时间：可选值为一天中的任何时间。该配置控制月度汇报发起的具体时间点。";
		try {
			checkAndInitSystemConfig("REPORT_MONTH_TIME", "每月汇报发起时间", value, isLob, description, type, selectContent, isMultiple, ++ordernumber );
		} catch (Exception e) {
			logger.warn( "system init system config 'REPORT_MONTH_TIME' got an exception." );
			logger.error(e);
		}
		
		value = "NEXT_WEEK";
		type = "select";
		selectContent = "THIS_WEEK|NEXT_WEEK";
		isMultiple = false;
		description = "每周汇报发起日期：可选值：THIS_WEEK|NEXT_WEEK。此配置控制周汇报发起的时机，是本周发起，还是下周发起。";
		try {
			checkAndInitSystemConfig("REPORT_WEEK_DAYTYPE", "每周汇报发起日期", value, isLob, description, type, selectContent, isMultiple, ++ordernumber );
		} catch (Exception e) {
			logger.warn( "system init system config 'REPORT_WEEK_DAYTYPE' got an exception." );
			logger.error(e);
		}
		
		value = "1";
		type = "day_for_week";
		selectContent = "0|1|2|3|4|5|6";
		isMultiple = false;
		isLob = false;
		description = "每周汇报发起日期：可选值：一周中的第几日。此配置控制每周汇报发起的日期。";
		try {
			checkAndInitSystemConfig("REPORT_WEEK_DAY", "每周汇报发起日期", value, isLob, description, type, selectContent, isMultiple, ++ordernumber );
		} catch (Exception e) {
			logger.warn( "system init system config 'REPORT_WEEK_DAY' got an exception." );
			logger.error(e);
		}
		
		value = "NONE";
		type = "time";
		selectContent = null;
		isMultiple = false;
		isLob = false;
		description = "每周汇报发起时间：可选值为一天中的任何时间。该配置控制月度汇报发起的具体时间点。";
		try {
			checkAndInitSystemConfig("REPORT_WEEK_TIME", "每周汇报发起时机", value, isLob, description, type, selectContent, isMultiple, ++ordernumber );
		} catch (Exception e) {
			logger.warn( "system init system config 'REPORT_WEEK_TIME' got an exception." );
			logger.error(e);
		}
		
		value = "TODAY";
		type = "select";
		selectContent = "NONE|TODAY|TOMORROW";
		isMultiple = false;
		isLob = false;
		description = "每日汇报发起日期：可选值：NONE|TODAY|TOMORROW。此配置控制日汇报发起的时机，是今天发起，还是明天发起。";
		try {
			checkAndInitSystemConfig("REPORT_DAY_DAYTYPE", "每日汇报发起时机", value, isLob, description, type, selectContent, isMultiple, ++ordernumber );
		} catch (Exception e) {
			logger.warn( "system init system config 'REPORT_DAY_DAYTYPE' got an exception." );
			logger.error(e);
		}
		
		value = "NONE";
		type = "time";
		selectContent = null;
		isMultiple = false;
		isLob = false;
		description = "每日汇报发起时间：可选值为一天中的任何时间。该配置控制日报发起的具体时间点。";
		try {
			checkAndInitSystemConfig("REPORT_DAY_TIME", "每日汇报发起时间", value, isLob, description, type, selectContent, isMultiple, ++ordernumber );
		} catch (Exception e) {
			logger.warn( "system init system config 'REPORT_DAY_TIME' got an exception." );
			logger.error(e);
		}
		
		
		value = "false";
		type = "select";
		selectContent = "true|false";
		isMultiple = false;
		isLob = false;
		description = "是否自动忽略周末：可选值为true|false，单值。此属性控制系统是否生成汇报时自动避开周末，生成时间顺延到下一个工作日.";
		try {
			checkAndInitSystemConfig("WEEKEND_IGNORE", "是否自动忽略周末", value, isLob, description, type, selectContent, isMultiple,  ++ordernumber );
		} catch (Exception e) {
			logger.warn( "system init system config 'WEEKEND_IGNORE' got an exception." );
			logger.error(e);
		}
		
		
		value = "false";
		type = "select";
		selectContent = "true|false";
		isMultiple = false;
		isLob = false;
		description = "是否忽略法定节假日：可选值为true|false，单值。此属性控制系统是否生成汇报时自动避开周末法定节假日，生成时间顺延到下一个工作日.";
		try {
			checkAndInitSystemConfig("HOLIDAY_IGNORE", "是否忽略法定节假日", value, isLob, description, type, selectContent, isMultiple,  ++ordernumber );
		} catch (Exception e) {
			logger.warn( "system init system config 'HOLIDAY_IGNORE' got an exception." );
			logger.error(e);
		}
		
		value = "NONE";
		type = "select";
		selectContent = "NONE|"+EnumReportModules.CMS+"|"+EnumReportModules.BBS+"|"+EnumReportModules.OKR+"|"+EnumReportModules.WORKFLOW+"|"+EnumReportModules.MEETTING+"|"+EnumReportModules.ATTENDANCE+"|"+EnumReportModules.STRATEGY;
		isMultiple = true;
		isLob = false;
		description = " 参与月报的应用（多选）：可选值为NONE|CMS|BBS|OKR|WORKFLOW|MEETTING|ATTENDANCE。此属性控制系统生成月报所涉及到的应用系统.";
		try {
			checkAndInitSystemConfig("REPORT_MONTH_MODULE", "参与月报的应用（多选）", value, isLob, description, type, selectContent, isMultiple,  ++ordernumber );
		} catch (Exception e) {
			logger.warn( "system init system config 'REPORT_MONTH_MODULE' got an exception." );
			logger.error(e);
		}
		
		value = "NONE";
		type = "select";
		selectContent = "NONE|"+EnumReportModules.CMS+"|"+EnumReportModules.BBS+"|"+EnumReportModules.OKR+"|"+EnumReportModules.WORKFLOW+"|"+EnumReportModules.MEETTING+"|"+EnumReportModules.ATTENDANCE+"|"+EnumReportModules.STRATEGY;
		isMultiple = true;
		isLob = false;
		description = " 参与周报的应用（多选）：可选值为NONE|CMS|BBS|OKR|WORKFLOW|MEETTING|ATTENDANCE。此属性控制系统生成周报所涉及到的应用系统.";
		try {
			checkAndInitSystemConfig("REPORT_WEEK_MODULE", "参与周报的应用（多选）", value, isLob, description, type, selectContent, isMultiple,  ++ordernumber );
		} catch (Exception e) {
			logger.warn( "system init system config 'REPORT_WEEK_MODULE' got an exception." );
			logger.error(e);
		}
		
		value = "NONE";
		type = "select";
		selectContent = "NONE|"+EnumReportModules.CMS+"|"+EnumReportModules.BBS+"|"+EnumReportModules.OKR+"|"+EnumReportModules.WORKFLOW+"|"+EnumReportModules.MEETTING+"|"+EnumReportModules.ATTENDANCE+"|"+EnumReportModules.STRATEGY;
		isMultiple = true;
		isLob = false;
		description = " 参与日报的应用（多选）：可选值为NONE|CMS|BBS|OKR|WORKFLOW|MEETTING|ATTENDANCE。此属性控制系统生成日报所涉及到的应用系统.";
		try {
			checkAndInitSystemConfig("REPORT_DAY_MODULE", "参与日报的应用（多选）", value, isLob, description, type, selectContent, isMultiple,  ++ordernumber );
		} catch (Exception e) {
			logger.warn( "system init system config 'REPORT_DAY_MODULE' got an exception." );
			logger.error(e);
		}
		
		value = "NONE";
		type = "text";
		selectContent = "";
		isMultiple = false;
		isLob = false;
		description = "个人月度汇报审批流程。此属性控制系统生成的个人月度汇报进入审批时所使用的流程.";
		try {
			checkAndInitSystemConfig("PERSONMONTH_REPORT_WORKFLOW", "个人月度汇报审批流程", value, isLob, description, type, selectContent, isMultiple,  ++ordernumber );
		} catch (Exception e) {
			logger.warn( "system init system config 'PERSONMONTH_REPORT_WORKFLOW' got an exception." );
			logger.error(e);
		}
		
		value = "NONE";
		type = "text";
		selectContent = "";
		isMultiple = false;
		isLob = false;
		description = "组织月度汇报审批流程。此属性控制系统生成的组织月度汇报进入审批时所使用的流程.";
		try {
			checkAndInitSystemConfig("UNITMONTH_REPORT_WORKFLOW", "组织月度汇报审批流程", value, isLob, description, type, selectContent, isMultiple,  ++ordernumber );
		} catch (Exception e) {
			logger.warn( "system init system config 'UNITMONTH_REPORT_WORKFLOW' got an exception." );
			logger.error(e);
		}
		
		value = "NONE";
		type = "text";
		selectContent = "";
		isMultiple = false;
		isLob = false;
		description = "个人每周汇报审批流程。此属性控制系统生成的个人每周汇报进入审批时所使用的流程.";
		try {
			checkAndInitSystemConfig("PERSONWEEK_REPORT_WORKFLOW", "个人每周汇报审批流程", value, isLob, description, type, selectContent, isMultiple,  ++ordernumber );
		} catch (Exception e) {
			logger.warn( "system init system config 'PERSONWEEK_REPORT_WORKFLOW' got an exception." );
			logger.error(e);
		}
		
		value = "NONE";
		type = "text";
		selectContent = "";
		isMultiple = false;
		isLob = false;
		description = "组织每周汇报审批流程。此属性控制系统生成的组织每周汇报进入审批时所使用的流程.";
		try {
			checkAndInitSystemConfig("UNITWEEK_REPORT_WORKFLOW", "组织每周汇报审批流程", value, isLob, description, type, selectContent, isMultiple,  ++ordernumber );
		} catch (Exception e) {
			logger.warn( "system init system config 'UNITWEEK_REPORT_WORKFLOW' got an exception." );
			logger.error(e);
		}
		
		value = "NONE";
		type = "text";
		selectContent = "";
		isMultiple = false;
		isLob = false;
		description = "个人每日汇报审批流程。此属性控制系统生成的个人每日汇报进入审批时所使用的流程.";
		try {
			checkAndInitSystemConfig("PERSONDAY_REPORT_WORKFLOW", "个人每日汇报审批流程", value, isLob, description, type, selectContent, isMultiple,  ++ordernumber );
		} catch (Exception e) {
			logger.warn( "system init system config 'PERSONDAY_REPORT_WORKFLOW' got an exception." );
			logger.error(e);
		}
		
		value = "NONE";
		type = "text";
		selectContent = "";
		isMultiple = false;
		isLob = false;
		description = "组织每日汇报审批流程。此属性控制系统生成的组织每日汇报进入审批时所使用的流程.";
		try {
			checkAndInitSystemConfig("UNITDAY_REPORT_WORKFLOW", "组织每日汇报审批流程", value, isLob, description, type, selectContent, isMultiple,  ++ordernumber );
		} catch (Exception e) {
			logger.warn( "system init system config 'UNITDAY_REPORT_WORKFLOW' got an exception." );
			logger.error(e);
		}
		
		value = "部门经理,部门秘书";
		type = "duty";
		selectContent = "";
		isMultiple = true;
		isLob = true;
		description = "个人汇报排除职务。此属性控制系统生成个人汇报时需要排除不生成个人汇报的职务.";
		try {
			checkAndInitSystemConfig("PERSONREPORT_REMOVE_DUTY", "个人汇报排除职务", value, isLob, description, type, selectContent, isMultiple,  ++ordernumber );
		} catch (Exception e) {
			logger.warn( "system init system config 'PERSONREPORT_REMOVE_DUTY' got an exception." );
			logger.error(e);
		}
		
		value = "部门经理";
		type = "duty";
		selectContent = "";
		isMultiple = true;
		isLob = true;
		description = "组织汇报指定职务。此属性控制系统生成组织汇报时指定的职务，系统只为指定职务生成组织汇报.";
		try {
			checkAndInitSystemConfig("UNITREPORT_DUTY", "组织汇报指定职", value, isLob, description, type, selectContent, isMultiple,  ++ordernumber );
		} catch (Exception e) {
			logger.warn( "system init system config 'UNITREPORT_DUTY' got an exception." );
			logger.error(e);
		}
		
		value = "NONE";
		type = "select";
		selectContent = "NONE|EXPRESSION|CUSTOMDATELIST";
		isMultiple = false;
		isLob = false;
		description = "定时生成类别。此属性控制系统定时生成组织汇报的时间控制方式，不自动启动、依据时间表达式或者自定义启动时间列表.";
		try {
			checkAndInitSystemConfig("AUTOCREATE_TYPE", "定时生成类别", value, isLob, description, type, selectContent, isMultiple,  ++ordernumber );
		} catch (Exception e) {
			logger.warn( "system init system config 'AUTOCREATETYPE' got an exception." );
			logger.error(e);
		}
		
		/**
		 * 在每个周一,二, 三和周四的 10:15 AM													0 15 10 ? * MON-FRI
			每月15号的 10:15 AM																		0 15 10 15 * ?
			每月最后一天的 10:15 AM																	0 15 10 L * ?
			每月最后一个周五的 10:15 AM																0 15 10 ? * 6L
			在 2002, 2003, 2004, 和 2005 年中的每月最后一个周五的 10:15 AM			0 15 10 ? * 6L 2002-2005
			每月第三个周五的 10:15 AM																0 15 10 ? * 6#3
			每月从第一天算起每五天的 12:00 PM (中午)											0 0 12 1/5 * ?
			每一个 11 月 11 号的 11:11 AM															0 11 11 11 11 ?
			三月份每个周三的 2:10 PM 和 2:44 PM													0 10,44 14 ? 3 WED
		 */
		value = "0 15 10 ? * 6L";
		type = "text";
		selectContent = "";
		isMultiple = false;
		isLob = false;
		description = "时间表达式。此属性控制系统定时生成启动时间，时间表达式。";
		try {
			checkAndInitSystemConfig("CRON_EXPRESSION", "时间表达式", value, isLob, description, type, selectContent, isMultiple,  ++ordernumber );
		} catch (Exception e) {
			logger.warn( "system init system config 'CRON_EXPRESSION' got an exception." );
			logger.error(e);
		}
		
		value = new DateOperation().getNowDateTime();
		type = "text";
		selectContent = "";
		isMultiple = false;
		isLob = true;
		description = "自定义时间列表。此属性控制系统定时生成启动时间，自定义。";
		try {
			checkAndInitSystemConfig("CUSTOM_DATELIST", "自定义时间列表", value, isLob, description, type, selectContent, isMultiple,  ++ordernumber );
		} catch (Exception e) {
			logger.warn( "system init system config 'CUSTOM_DATELIST' got an exception." );
			logger.error(e);
		}
	}
	
	/**
	 * 检查配置项是否存在，如果不存在根据信息创建一个新的配置项
	 * @param configCode
	 * @param configName
	 * @param configValue
	 * @param isLob 
	 * @param description
	 * @throws Exception
	 */
	public void checkAndInitSystemConfig( String configCode, String configName, String configValue, Boolean isLob, String description, String type, String selectContent, Boolean isMultiple, Integer orderNumber ) throws Exception {
		if( configCode  == null || configCode.isEmpty() ){
			throw new Exception( "configCode is null!" );
		}
		if( configName  == null || configName.isEmpty() ){
			throw new Exception( "configName is null!" );
		}
		Business business = null;
		Report_S_Setting report_S_Setting = null;
		Report_S_SettingLobValue reportSettingLobValue = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			business = new Business(emc);
			report_S_Setting = business.report_S_SettingFactory().getWithConfigCode( configCode );
		}catch( Exception e ){
			logger.warn( "system find system config{'configCode':'"+configCode+"'} got an exception. " );
			logger.error(e);
		}
		//如果配置不存在，则新建一个配置记录
		if( report_S_Setting == null ){
			report_S_Setting = new Report_S_Setting();
			report_S_Setting.setConfigCode( configCode );
			report_S_Setting.setConfigName( configName );
			report_S_Setting.setConfigValue( configValue );
			report_S_Setting.setDescription( description );
			report_S_Setting.setOrderNumber( orderNumber );
			report_S_Setting.setValueType( type );
			report_S_Setting.setIsLob( isLob );
			report_S_Setting.setSelectContent( selectContent );
			report_S_Setting.setIsMultiple( isMultiple );
			
			try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
				business = new Business(emc);
				emc.beginTransaction( Report_S_Setting.class );
				
				//对长文本记录进行操作
				if( isLob ) {
					emc.beginTransaction( Report_S_SettingLobValue.class );
					reportSettingLobValue = emc.find( report_S_Setting.getId(), Report_S_SettingLobValue.class );
					if( reportSettingLobValue != null ) {
						reportSettingLobValue.setLobValue( configValue );//更新值
						emc.check( reportSettingLobValue, CheckPersistType.all );
					}else {
						//没有，就创建一个LOB值记录
						reportSettingLobValue = new Report_S_SettingLobValue();
						reportSettingLobValue.setId( report_S_Setting.getId() );
						reportSettingLobValue.setLobValue( configValue );
						emc.persist( reportSettingLobValue, CheckPersistType.all );
					}
					report_S_Setting.setConfigValue( "LobValue" );
				}
				
				emc.persist( report_S_Setting, CheckPersistType.all );
				emc.commit();
			}catch( Exception e ){
				logger.warn("attendance system persist new system config{'configCode':'"+configCode+"'} got an exception. " );
				logger.error(e);
			}
		}else{
			try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
				report_S_Setting = emc.find( report_S_Setting.getId(), Report_S_Setting.class );
				report_S_Setting.setIsLob( isLob );
				emc.beginTransaction( Report_S_Setting.class );
				
				if( !configName.equals( report_S_Setting.getConfigName() ) ){
					report_S_Setting.setConfigName( configName );
				}
//				if( !configValue.equals( report_S_Setting.getConfigValue() ) ){
//					report_S_Setting.setConfigValue( configValue );
//				}
				if( orderNumber != report_S_Setting.getOrderNumber() ){
					report_S_Setting.setOrderNumber(orderNumber);
				}
				if( description != null ){
					report_S_Setting.setDescription( description );
				}
				if( type != null ){
					report_S_Setting.setValueType( type );
				}
				if( selectContent != null ){
					report_S_Setting.setSelectContent( selectContent );
				}
				if( isMultiple != null ){
					report_S_Setting.setIsMultiple( isMultiple );
				}
				
				//对长文本记录进行操作
				if( isLob ) {
					emc.beginTransaction( Report_S_SettingLobValue.class );
					reportSettingLobValue = emc.find( report_S_Setting.getId(), Report_S_SettingLobValue.class );
					if( reportSettingLobValue != null ) {
						//reportSettingLobValue.setLobValue( configValue );//更新值
						emc.check( reportSettingLobValue, CheckPersistType.all );
					}else {
						//没有，就创建一个LOB值记录
						reportSettingLobValue = new Report_S_SettingLobValue();
						reportSettingLobValue.setId( report_S_Setting.getId() );
						reportSettingLobValue.setLobValue( configValue );
						emc.check( reportSettingLobValue, CheckPersistType.all );
					}
					report_S_Setting.setConfigValue( "LobValue" );
				}
				
				emc.check( report_S_Setting, CheckPersistType.all );
				emc.commit();
			}catch( Exception e ){
				logger.warn("attendance system update system config{'configCode':'"+configCode+"'} got an exception. " );
				logger.error(e);
			}
		}
	}
}

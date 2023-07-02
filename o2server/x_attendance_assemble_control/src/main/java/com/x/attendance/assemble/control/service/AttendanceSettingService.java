package com.x.attendance.assemble.control.service;

import java.util.List;

import com.x.attendance.assemble.control.Business;
import com.x.attendance.entity.AppealConfig;
import com.x.attendance.entity.AttendanceSetting;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

public class AttendanceSettingService {
	
	private static  Logger logger = LoggerFactory.getLogger( AttendanceSettingService.class );

	public List<AttendanceSetting> listAll( EntityManagerContainer emc ) throws Exception {
		Business business =  new Business( emc );
		return business.getAttendanceSettingFactory().listAll();
	}

	public AttendanceSetting get( EntityManagerContainer emc, String id ) throws Exception {
		return emc.find(id, AttendanceSetting.class);
	}

	public void delete( EntityManagerContainer emc, String id ) throws Exception {
		AttendanceSetting attendanceSetting = null;
		if( id == null || id.isEmpty() ){
			throw new Exception( "id is null, system can not delete any object." );
		}
		attendanceSetting = emc.find( id, AttendanceSetting.class );
		if ( null == attendanceSetting ) {
			throw new Exception( "object is not exist {'id':'"+ id +"'}" );
		}else{
			emc.beginTransaction( AttendanceSetting.class );
			emc.remove( attendanceSetting, CheckRemoveType.all );
			emc.commit();
		}
	}

	public AttendanceSetting getByCode( EntityManagerContainer emc, String code ) throws Exception {
		List<AttendanceSetting> attendanceSettingList = null;
		AttendanceSetting attendanceSetting = null;
		List<String> ids = null;
		Business business = new Business( emc );
		if( code == null || code.isEmpty() ){
			throw new Exception( "code is null, system can not query any object." );
		}
		
		ids = business.getAttendanceSettingFactory().listIdsByCode(code);
		if( ids != null && !ids.isEmpty() ){
			attendanceSettingList =  business.getAttendanceSettingFactory().list( ids );
		}
		
		if( attendanceSettingList != null && !attendanceSettingList.isEmpty() ){
			for( int i=0; i< attendanceSettingList.size(); i++  ){
				if( i == 0 ){
					attendanceSetting = attendanceSettingList.get( i );
				}else{
					emc.beginTransaction( AttendanceSetting.class );
					emc.remove( attendanceSettingList.get( i ), CheckRemoveType.all );
					emc.commit();
				}
			}
		}
		return attendanceSetting;
	}

	public AttendanceSetting create( EntityManagerContainer emc, AttendanceSetting attendanceSetting ) throws Exception {
		AttendanceSetting attendanceSetting_old = null;
		attendanceSetting_old = emc.find( attendanceSetting.getId(), AttendanceSetting.class );
		if( attendanceSetting_old != null ){
			throw new Exception("attendanceSetting{'id':' "+ attendanceSetting.getId() +" '} exists, can not create new object");
		}else{
			emc.beginTransaction( AttendanceSetting.class );
			emc.persist( attendanceSetting, CheckPersistType.all);
			emc.commit();
		}
		return attendanceSetting;
	}
	
	public AttendanceSetting update( EntityManagerContainer emc, AttendanceSetting attendanceSetting ) throws Exception {
		if( attendanceSetting == null ){
			throw new Exception("attendanceSetting is null, can not update object!");
		}
		AttendanceSetting attendanceSetting_old = null;
		attendanceSetting_old = emc.find( attendanceSetting.getId(), AttendanceSetting.class );
		if( attendanceSetting_old != null ){
			emc.beginTransaction( AttendanceSetting.class );
			attendanceSetting_old.setConfigCode( attendanceSetting.getConfigCode() );
			attendanceSetting_old.setConfigName( attendanceSetting.getConfigName() );
			attendanceSetting_old.setConfigValue( attendanceSetting.getConfigValue() );
			attendanceSetting_old.setOrderNumber( attendanceSetting.getOrderNumber() );
			emc.check( attendanceSetting_old, CheckPersistType.all);
			emc.commit();
		}else{
			throw new Exception("old object attendanceSetting{'id':' "+ attendanceSetting.getId() +" '} is not exists. ");
		}
		
		return attendanceSetting;
	}

	/**
	 *  APPEALABLE	员工申诉及申诉审批	true|false
		APPEAL_AUDITOR_TYPE	        考勤结果申诉审核人确定方式	       所属组织职位|所属组织属性|人员属性(默认)|指定人|指定角色
		APPEAL_AUDITOR_VALUE	考勤结果申诉审核人确定内容	    直属领导(默认)    
		APPEAL_CHECKER_TYPE	        考勤结果申诉复核人确定方式	       无(默认)|所属组织职位|所属组织属性|人员属性|指定人|指定角色
		APPEAL_CHECKER_VALUE	考勤结果申诉复核人确定内容
	 */
	public void initAllSystemConfig() {
		String value = null, description = null, type = null, selectContent = null;
		Boolean isMultiple = false;
		Integer ordernumber = 0;
		
		value = "true";
		type = "select";
		selectContent = null;
		isMultiple = false;
		description = "员工申诉及申诉审批：可选值为true|false，单值。此属性控制系统中是否允许员工对考勤结果进行申诉操作.";
		try {
			checkAndInitSystemConfig("APPEALABLE", "员工申诉及申诉审批", value, description, type, selectContent, isMultiple,  ++ordernumber );
		} catch (Exception e) {
			logger.warn( "attendance system init system config 'APPEALABLE' got an exception." );
			logger.error(e);
		}

		value = AppealConfig.APPEAL_AUDIFLOWTYPE_BUILTIN;
		type = "select";
		selectContent = AppealConfig.APPEAL_AUDIFLOWTYPE_WORKFLOW + "|" + AppealConfig.APPEAL_AUDIFLOWTYPE_BUILTIN;
		isMultiple = false;
		description = "考勤结果申诉审核人确定方式：可选值："+AppealConfig.APPEAL_AUDIFLOWTYPE_BUILTIN+"|"+ AppealConfig.APPEAL_AUDIFLOWTYPE_WORKFLOW +"。此配置控制考勤结果申诉流程为自定义流程或者内置审批步骤（审核-复核）。";
		try {
			checkAndInitSystemConfig("APPEAL_AUDIFLOWTYPE", "考勤结果申诉流程类型", value, description, type, selectContent, isMultiple, ++ordernumber );
		} catch (Exception e) {
			logger.warn( "attendance system init system config 'APPEAL_AUDIFLOWTYPE' got an exception." );
			logger.error(e);
		}

		value = "无";
		type = "text";
		selectContent = null;
		isMultiple = false;
		description = "考勤结果申诉流程，单值。该配置在'自定义申诉流程("+AppealConfig.APPEAL_AUDIFLOWTYPE_BUILTIN+")'时，需要启动的申请流程ID。";
		try {
			checkAndInitSystemConfig("APPEAL_AUDIFLOW_ID", "自定义申诉流程", value, description, type, selectContent, isMultiple, ++ordernumber );
		} catch (Exception e) {
			logger.warn( "system init system config 'APPEAL_AUDIFLOW_ID' got an exception." );
			logger.error(e);
		}

		value = AppealConfig.APPEAL_AUDITTYPE_UNITDUTY;
		type = "select";
		selectContent = AppealConfig.APPEAL_CHOOSEVALUE_AUDITTYPE;
		isMultiple = false;
		description = "考勤结果申诉审核人确定方式：可选值：所属组织职务|汇报对象|个人属性|指定人。此配置控制在'内置申诉流程("+AppealConfig.APPEAL_AUDIFLOWTYPE_BUILTIN+")'中考勤结果申诉审核人的确定方式。";
		try {
			checkAndInitSystemConfig("APPEAL_AUDITOR_TYPE", "内置申请过程中考勤结果申诉审核人确定方式", value, description, type, selectContent, isMultiple, ++ordernumber );
		} catch (Exception e) {
			logger.warn( "attendance system init system config 'APPEAL_AUDITOR_TYPE' got an exception." );
			logger.error(e);
		}
		
		value = "部门经理";
		type = "text";
		selectContent = null;
		isMultiple = false;
		description = "考勤结果申诉审核人确定内容：可选值为指定的人员身份，单值。该配置在'内置申诉流程("+AppealConfig.APPEAL_AUDIFLOWTYPE_BUILTIN+")'中与汇报流程方式中的ADMIN_AND_ALLLEADER配合使用";
		try {
			checkAndInitSystemConfig("APPEAL_AUDITOR_VALUE", "内置申请过程中考勤结果申诉审核人确定内容", value, description, type, selectContent, isMultiple, ++ordernumber );
		} catch (Exception e) {
			logger.warn( "system init system config 'APPEAL_AUDITOR_VALUE' got an exception." );
			logger.error(e);
		}
		
		value = "无";
		type = "select";
		selectContent = AppealConfig.APPEAL_CHOOSEVALUE_CHECKTYPE;
		isMultiple = false;
		description = "考勤结果申诉复核人确定方式：可选值：无|所属组织职务|汇报对象|个人属性|指定人。此配置在'内置申诉流程("+AppealConfig.APPEAL_AUDIFLOWTYPE_BUILTIN+")'中控制考勤结果申诉审核人的确定方式,无-表示不需要复核。";
		try {
			checkAndInitSystemConfig("APPEAL_CHECKER_TYPE", "内置申请过程中考勤结果申诉复核人确定方式", value, description, type, selectContent, isMultiple, ++ordernumber );
		} catch (Exception e) {
			logger.warn( "attendance system init system config 'APPEAL_CHECKER_TYPE' got an exception." );
			logger.error(e);
		}
		
		value = "无";
		type = "text";
		selectContent = null;
		isMultiple = false;
		description = "考勤结果申诉复核人确定内容：可选值为指定的人员身份，单值。该配置在'内置申诉流程("+AppealConfig.APPEAL_AUDIFLOWTYPE_BUILTIN+")'中与汇报流程方式中的ADMIN_AND_ALLLEADER配合使用";
		try {
			checkAndInitSystemConfig("APPEAL_CHECKER_VALUE", "内置申请过程中考勤结果申诉复核人确定内容", value, description, type, selectContent, isMultiple, ++ordernumber );
		} catch (Exception e) {
			logger.warn( "system init system config 'APPEAL_CHECKER_VALUE' got an exception." );
			logger.error(e);
		}

		value = "无";
		type = "select";
		selectContent = "无|周六|周日";
		isMultiple = false;
		description = "周末设置描述：选择周六或周日。选中的为周末，未选中的按工作日计算";
		try {
			checkAndInitSystemConfig("ATTENDANCE_WEEKEND", "周末计算日期设置", value, description, type, selectContent, isMultiple, ++ordernumber );
		} catch (Exception e) {
			logger.warn( "system init system config 'APPEAL_CHECKER_VALUE' got an exception." );
			logger.error(e);
		}
	}
	
	/**
	 * 检查配置项是否存在，如果不存在根据信息创建一个新的配置项
	 * @param configCode
	 * @param configName
	 * @param configValue
	 * @param description
	 * @throws Exception
	 */
	public void checkAndInitSystemConfig( String configCode, String configName, String configValue, String description, String type, String selectContent, Boolean isMultiple, Integer orderNumber ) throws Exception {
		if( configCode  == null || configCode.isEmpty() ){
			throw new Exception( "configCode is null!" );
		}
		if( configName  == null || configName.isEmpty() ){
			throw new Exception( "configName is null!" );
		}
		Business business = null;
		AttendanceSetting attendanceSetting = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			business = new Business(emc);
			attendanceSetting = business.getAttendanceSettingFactory().getWithConfigCode( configCode );
		}catch( Exception e ){
			logger.warn( "system find system config{'configCode':'"+configCode+"'} got an exception. " );
			logger.error(e);
		}
		//如果配置不存在，则新建一个配置记录
		if( attendanceSetting == null ){
			attendanceSetting = new AttendanceSetting();
			attendanceSetting.setConfigCode( configCode );
			attendanceSetting.setConfigName( configName );
			attendanceSetting.setConfigValue( configValue );
			attendanceSetting.setDescription( description );
			attendanceSetting.setOrderNumber( orderNumber );
			attendanceSetting.setValueType( type );
			attendanceSetting.setSelectContent( selectContent );
			attendanceSetting.setIsMultiple( isMultiple );
			try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
				emc.beginTransaction( AttendanceSetting.class );
				emc.persist( attendanceSetting, CheckPersistType.all );
				//logger.info("attendance system config has been add：" + attendanceSetting.getConfigCode() + "[" + attendanceSetting.getConfigName()+ "].");
				emc.commit();
			}catch( Exception e ){
				logger.warn("attendance system persist new system config{'configCode':'"+configCode+"'} got an exception. " );
				logger.error(e);
			}
		}else{
			try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
				attendanceSetting = emc.find( attendanceSetting.getId(), AttendanceSetting.class );
				emc.beginTransaction( AttendanceSetting.class );
				if( !configName.equals( attendanceSetting.getConfigName() ) ){
					attendanceSetting.setConfigName( configName );
				}
				if( orderNumber != attendanceSetting.getOrderNumber() ){
					attendanceSetting.setOrderNumber(orderNumber);
				}
				if( description != null ){
					attendanceSetting.setDescription( description );
				}
				if( type != null ){
					attendanceSetting.setValueType( type );
				}
				if( selectContent != null ){
					attendanceSetting.setSelectContent( selectContent );
				}
				if( isMultiple != null ){
					attendanceSetting.setIsMultiple( isMultiple );
				}
				emc.check( attendanceSetting, CheckPersistType.all );
				emc.commit();
			}catch( Exception e ){
				logger.warn("attendance system update system config{'configCode':'"+configCode+"'} got an exception. " );
				logger.error(e);
			}
		}
	}
}

package com.x.attendance.assemble.control.service;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.attendance.assemble.common.date.DateOperation;
import com.x.attendance.assemble.control.Business;
import com.x.attendance.assemble.control.jaxrs.AppealConfig;
import com.x.attendance.entity.AttendanceAppealInfo;
import com.x.attendance.entity.AttendanceDetail;
import com.x.attendance.entity.AttendanceSetting;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

public class AttendanceAppealInfoServiceAdv {
	
	private static  Logger logger = LoggerFactory.getLogger( AttendanceAppealInfoServiceAdv.class );
	private AttendanceAppealInfoService attendanceAppealInfoService = new AttendanceAppealInfoService();
	private AttendanceDetailService attendanceDetailService = new AttendanceDetailService();
	private AttendanceNoticeService attendanceNoticeService = new AttendanceNoticeService();
	private AttendanceSettingService attendanceSettingService = new AttendanceSettingService();
	private UserManagerService userManagerService = new UserManagerService();
	
	public AttendanceAppealInfo get( String id ) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return attendanceAppealInfoService.get( emc, id );	
		} catch ( Exception e ) {
			throw e;
		}
	}
	public List<AttendanceAppealInfo> list(List<String> ids) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return attendanceAppealInfoService.list( emc, ids );	
		} catch ( Exception e ) {
			throw e;
		}
	}
	public void delete( String id ) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			attendanceAppealInfoService.delete( emc, id );	
		} catch ( Exception e ) {
			throw e;
		}
	}

	public AttendanceAppealInfo saveNewAppeal( AttendanceAppealInfo attendanceAppealInfo ) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return attendanceAppealInfoService.save( emc, attendanceAppealInfo );	
		} catch ( Exception e ) {
			throw e;
		}
	}

	/**
	 * 根据打卡信息以及系统相关配置，获取申诉信息处理人
	 * @param attendanceAppealInfo
	 * @return
	 * @throws Exception
	 */
	public String getAppealAuditPerson( String personName, String personUnitName, String identity ) throws Exception {
		/**
		 * 系统配置 APPEAL_AUDITOR_TYPE 有三种值：
		 * 1、指定人 
		 * 2、人员属性
		 * 3、所属部门职位
		 * 根据相关的配置来获取申诉处理人
		 */
		AttendanceSetting attendanceSetting  = null;
		String appeal_auditor_type = null;
		String appeal_auditor_value = null;
		//1、先获取系统配置 APPEAL_AUDITOR_TYPE
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			attendanceSetting = attendanceSettingService.getByCode( emc, "APPEAL_AUDITOR_TYPE" );
			if( attendanceSetting != null ) {
				appeal_auditor_type = attendanceSetting.getConfigValue();
			}
			attendanceSetting = attendanceSettingService.getByCode( emc, "APPEAL_AUDITOR_VALUE" );
			if( attendanceSetting != null ) {
				appeal_auditor_value = attendanceSetting.getConfigValue();
			}
		} catch ( Exception e ) {
			throw e;
		}
		return getAppealProcessPerson( personName, appeal_auditor_type, appeal_auditor_value, personUnitName, identity );
	}
	
	/**
	 * 根据打卡信息以及系统相关配置，获取申诉信息复核人
	 * @param attendanceAppealInfo
	 * @return
	 * @throws Exception
	 */
	public String getAppealCheckPerson( String personName, String personUnitName, String identity ) throws Exception {
		/**
		 * 系统配置 APPEAL_AUDITOR_TYPE 有三种值：
		 * 1、指定人 
		 * 2、人员属性
		 * 3、所属部门职位
		 * 根据相关的配置来获取申诉处理人
		 */
		AttendanceSetting attendanceSetting  = null;
		String appeal_checker_type = null;
		String appeal_checker_value = null;
		//1、先获取系统配置 APPEAL_AUDITOR_TYPE
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			attendanceSetting = attendanceSettingService.getByCode( emc, "APPEAL_CHECKER_TYPE" );
			if( attendanceSetting != null ) {
				appeal_checker_type = attendanceSetting.getConfigValue();
			}
			attendanceSetting = attendanceSettingService.getByCode( emc, "APPEAL_CHECKER_VALUE" );
			if( attendanceSetting != null ) {
				appeal_checker_value = attendanceSetting.getConfigValue();
			}
		} catch ( Exception e ) {
			throw e;
		}
		return getAppealProcessPerson( personName, appeal_checker_type, appeal_checker_value, personUnitName, identity );
	}
	
	/**
	 * 根据打卡信息以及系统相关配置，获取申诉信息处理人
	 * @param attendanceAppealInfo
	 * @return
	 * @throws Exception
	 */
	private String getAppealProcessPerson( String personName, String auditorType, String auditorTypeValue, String personUnitName, String identity ) throws Exception {
		if( personName == null || personName.isEmpty() ) {
			logger.info( "personName is null!" );
			return null;
		}
		if( auditorType == null || auditorType.isEmpty() ) {
			logger.info( "auditorType is null!" );
			return null;
		}
		if( auditorTypeValue == null || auditorTypeValue.isEmpty() ) {
			logger.info( "auditorTypeValue is null!" );
			return null;
		}
		String auditorPersonName = null;
		if( AppealConfig.APPEAL_AUDITTYPE_PERSON.equals( auditorType ) ) {
			return auditorTypeValue;
		}else if( AppealConfig.APPEAL_AUDITTYPE_PERSONATTRIBUTE.equals( auditorType ) ) {
			auditorPersonName = getPersonWithAtrribute( personName, auditorTypeValue );
		}else if( AppealConfig.APPEAL_AUDITTYPE_REPORTLEADER.equals( auditorType ) ) {
			auditorPersonName = getPersonWithReporter( personName );
		}else if( AppealConfig.APPEAL_AUDITTYPE_UNITDUTY.equals( auditorType ) ) {
			auditorPersonName = getPersonWithUnitDuty( personName, auditorTypeValue, personUnitName, identity );
		}
		return auditorPersonName;
	}
	
	/**
	 * 获取指定人员所属组织的职位
	 * @param personName  考勤人员
	 * @param dutyName 职位名称
	 * @param personUnitName 考勤人员所属组织
	 * @param identity  考勤人员可选身份
	 * @return
	 * @throws Exception
	 */
	private String getPersonWithUnitDuty( String personName, String dutyName, String personUnitName, String identity ) throws Exception {
		if( StringUtils.isNotEmpty( personName ) ) {
			logger.info( "personName is null!" );
			return null;
		}
		if( StringUtils.isNotEmpty( dutyName ) ) {
			logger.info( "dutyName is null!" );
			return null;
		}
		List<String> duties = null;
		if( StringUtils.isNotEmpty( identity ) ) {
			duties = userManagerService.getUnitDutyWithIdentityWithDuty( identity, dutyName );
		}else {
			if( StringUtils.isNotEmpty( personUnitName ) ) {
				duties = userManagerService.getUnitDutyWithUnitWithDuty( personUnitName, dutyName );
			}else {
				duties = userManagerService.getUnitDutyWithPersonWithDuty( personName, dutyName );
			}			
		}
		if( duties != null && !duties.isEmpty() ) {
			return duties.get( 0 );
		}
		return null;
	}

	/**
	 * 获取指定人员的汇报对象
	 * @param personName
	 * @return
	 * @throws Exception 
	 */
	private String getPersonWithReporter( String personName ) throws Exception {
		return userManagerService.getReporterWithPerson( personName );
	}

	/**
	 * 获取指定人员的个人属性
	 * @param personName
	 * @param attributeName
	 * @return
	 * @throws Exception 
	 */
	private String getPersonWithAtrribute(String personName, String attributeName ) throws Exception {
		List<String> attributes = userManagerService.listAttributeWithPersonWithName( personName, attributeName );
		if( attributes != null && !attributes.isEmpty() ) {
			return attributes.get( 0 );
		}
		return null;
	}

	/**
	 * 
	 * @param id
	 * @param unitName
	 * @param topUnitName
	 * @param processor
	 * @param processTime
	 * @param opinion
	 * @param status // 申诉状态:0-未申诉，1-申诉中，-1-申诉未通过，9-申诉通过
	 * @return
	 * @throws Exception
	 */
	public AttendanceAppealInfo firstProcessAttendanceAppeal( String id, String unitName, String topUnitName, String processor, Date processTime, String opinion, Integer status ) throws Exception {
		AttendanceAppealInfo attendanceAppealInfo = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			//修改申诉信息状态和处理过程信息
			emc.beginTransaction( AttendanceAppealInfo.class );
			emc.beginTransaction( AttendanceDetail.class );
			attendanceAppealInfo = attendanceAppealInfoService.updateAppealProcessInfoForFirstProcess( emc, id, unitName, topUnitName, processor, processTime, opinion, status, false );
			if( attendanceAppealInfo != null ){
				if ( status == 1 ) {
					attendanceAppealInfo.setCurrentProcessor( null );
					attendanceDetailService.updateAppealProcessStatus( emc, id, 9, false );
					attendanceNoticeService.notifyAttendanceAppealAcceptMessage( attendanceAppealInfo, attendanceAppealInfo.getEmpName() );
				} else if ( status == 2 ) {// 如果需要继续第二级审批
					//判断是否存在用户的复核人信息
					if( attendanceAppealInfo.getProcessPerson2() == null || attendanceAppealInfo.getProcessPerson2().isEmpty() ) {
						attendanceDetailService.updateAppealProcessStatus( emc, id, 1, false );
						attendanceAppealInfo.setCurrentProcessor( attendanceAppealInfo.getProcessPerson2() );
						emc.check( attendanceAppealInfo, CheckPersistType.all );
						attendanceNoticeService.notifyAttendanceAppealProcessness2Message( attendanceAppealInfo );
					}
				} else {// 如果审批不通过
					attendanceAppealInfo.setCurrentProcessor( null );
					attendanceDetailService.updateAppealProcessStatus( emc, id, -1, false );
					attendanceNoticeService.notifyAttendanceAppealRejectMessage( attendanceAppealInfo, attendanceAppealInfo.getEmpName());
				}
			}
			emc.commit();
		} catch ( Exception e ) {
			throw e;
		}
		return attendanceAppealInfo;
	}

	public AttendanceAppealInfo secondProcessAttendanceAppeal( String id, String unitName, String topUnitName,
			String processor, Date processTime, String opinion, Integer status ) throws Exception {
		AttendanceAppealInfo attendanceAppealInfo = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			//修改申诉信息状态和处理过程信息
			emc.beginTransaction( AttendanceAppealInfo.class );
			emc.beginTransaction( AttendanceDetail.class );
			attendanceAppealInfo = attendanceAppealInfoService.updateAppealProcessInfoForSecondProcess( emc, id, unitName, topUnitName, processor, processTime, opinion, status, false );
			if( attendanceAppealInfo != null ){
				if ( status == 1 ) {
					attendanceDetailService.updateAppealProcessStatus( emc, id, 9, false );
					attendanceNoticeService.notifyAttendanceAppealAcceptMessage( attendanceAppealInfo, attendanceAppealInfo.getEmpName() );
				}else {// 如果审批不通过
					attendanceDetailService.updateAppealProcessStatus( emc, id, -1, false );
					attendanceNoticeService.notifyAttendanceAppealRejectMessage( attendanceAppealInfo, attendanceAppealInfo.getEmpName());
				}
				attendanceAppealInfo.setCurrentProcessor( null );
			}
			emc.commit();
		} catch ( Exception e ) {
			throw e;
		}
		return attendanceAppealInfo;
	}

	public void archive( String id ) throws Exception {
		DateOperation dateOperation = new DateOperation();
		String datetime = dateOperation.getNowDateTime();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			attendanceAppealInfoService.archive( emc, id, datetime );
		} catch ( Exception e ) {
			throw e;
		}
	}

	public void archiveAll() throws Exception {
		DateOperation dateOperation = new DateOperation();
		String datetime = dateOperation.getNowDateTime();
		List<String> ids = null;
		Business business = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			business = new Business( emc );
			ids = business.getAttendanceAppealInfoFactory().listNonArchiveAppealInfoIds();
			if( ids != null && !ids.isEmpty() ){
				for( String id : ids ){
					try{
						attendanceAppealInfoService.archive( emc, id, datetime );
					}catch( Exception e ){
						logger.info( "system archive attendance appeal info got an exception.");
						logger.error( e );
					}
				}
			}
		} catch ( Exception e ) {
			throw e;
		}
	}

	/**
	 * 根据人员姓名，默认组织名称，人员身份来确定一个组织名称
	 * 首选，传入的身份名称
	 * 第二，传入的组织名称
	 * 最后，根据人员姓名选取一个组织名称
	 * 
	 * @param personName
	 * @param unitName
	 * @param identity
	 * @return
	 * @throws Exception
	 */
	public String getPersonUnitName( String personName, String unitName, String identity ) throws Exception {
		if( identity != null && !identity.isEmpty() ) {
			//根据身份去查询指定的组织
			return userManagerService.getUnitNameWithIdentity(identity);
		}else if( unitName != null && !unitName.isEmpty() ) {
			return unitName;
		}else {
			//根据人员去查询
			return userManagerService.getTopUnitNameWithPersonName( personName );
		}
	}

	
}

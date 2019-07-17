package com.x.attendance.assemble.control.service;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.attendance.assemble.control.Business;
import com.x.attendance.entity.AttendanceScheduleSetting;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

public class AttendanceScheduleSettingService {
	
	private static  Logger logger = LoggerFactory.getLogger( AttendanceScheduleSettingService.class );
	
	public List<AttendanceScheduleSetting> listAll( EntityManagerContainer emc ) throws Exception {
		Business business =  new Business( emc );
		return business.getAttendanceScheduleSettingFactory().listAll();
	}

	public AttendanceScheduleSetting get( EntityManagerContainer emc, String id ) throws Exception {
		return emc.find(id, AttendanceScheduleSetting.class);
	}

	public AttendanceScheduleSetting save( EntityManagerContainer emc, AttendanceScheduleSetting attendanceScheduleSetting ) throws Exception {
		AttendanceScheduleSetting attendanceScheduleSetting_tmp = null;
		emc.beginTransaction( AttendanceScheduleSetting.class );
		if( attendanceScheduleSetting.getId() !=null && !attendanceScheduleSetting.getId().isEmpty() ){
			attendanceScheduleSetting_tmp = emc.find( attendanceScheduleSetting.getId(), AttendanceScheduleSetting.class );
			if( attendanceScheduleSetting_tmp != null ){
				attendanceScheduleSetting.copyTo( attendanceScheduleSetting_tmp, JpaObject.FieldsUnmodify );
				emc.check( attendanceScheduleSetting_tmp, CheckPersistType.all);	
			}else{
				emc.persist( attendanceScheduleSetting, CheckPersistType.all);	
			}
		}else{
			emc.persist( attendanceScheduleSetting, CheckPersistType.all);	
		}
		emc.commit();
		return attendanceScheduleSetting;
	}

	public void delete( EntityManagerContainer emc, String id ) throws Exception {
		AttendanceScheduleSetting attendanceScheduleSetting = null;
		if( id == null || id.isEmpty() ){
			throw new Exception( "id is null, system can not delete any object." );
		}
		attendanceScheduleSetting = emc.find( id, AttendanceScheduleSetting.class );
		if ( null == attendanceScheduleSetting ) {
			throw new Exception( "object is not exist {'id':'"+ id +"'}" );
		}else{
			emc.beginTransaction( AttendanceScheduleSetting.class );
			emc.remove( attendanceScheduleSetting, CheckRemoveType.all );
			emc.commit();
		}
	}

	public List<String> listByUnitName(EntityManagerContainer emc, String unitName) throws Exception {
		Business business =  new Business( emc );
		return business.getAttendanceScheduleSettingFactory().listByUnitName( unitName );
	}

	public List<AttendanceScheduleSetting> list(EntityManagerContainer emc, List<String> ids) throws Exception {
		Business business =  new Business( emc );
		return business.getAttendanceScheduleSettingFactory().list( ids );
	}

	public List<String> listByTopUnitName(EntityManagerContainer emc, String topUnitName ) throws Exception {
		Business business =  new Business( emc );
		return business.getAttendanceScheduleSettingFactory().listByTopUnitName( topUnitName );
	}

	public AttendanceScheduleSetting getAttendanceScheduleSettingWithPerson( String personName, Boolean debugger ) throws Exception {
		UserManagerService userManagerService = new UserManagerService();
		String unitName = userManagerService.getUnitNameWithPersonName( personName );
		AttendanceScheduleSetting attendanceScheduleSetting = getAttendanceScheduleSettingWithUnitName( unitName );
		if( attendanceScheduleSetting == null ) {
			//如果没有找到，那么应该为该人员所属的组织创建一个新的排班配置
			String topUnitName = userManagerService.getTopUnitNameWithUnitName( unitName );
			attendanceScheduleSetting = createNewScheduleSetting( unitName, topUnitName, debugger );
		}
		return attendanceScheduleSetting;
	}
	
	private synchronized AttendanceScheduleSetting getAttendanceScheduleSettingWithUnitName( String unitName ) throws Exception{
		if( unitName == null || unitName.isEmpty() || "null".equals( unitName ) ) {
			throw new Exception("unitName is null!");
		}
		UserManagerService userManagerService = new UserManagerService();
		String superUnitName = null;
		List<String> ids = null;
		List<AttendanceScheduleSetting> attendanceScheduleSettingList = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {//查询排班设计
			ids = new Business(emc).getAttendanceScheduleSettingFactory().listByUnitName( unitName );
			if( ids !=null && ids.size() > 0){
				attendanceScheduleSettingList = new Business(emc).getAttendanceScheduleSettingFactory().list(ids);
				if( attendanceScheduleSettingList != null && attendanceScheduleSettingList.size() > 0){
					return attendanceScheduleSettingList.get(0);
				}
			}else{
				superUnitName = userManagerService.getParentUnitWithUnitName( unitName );
				if( StringUtils.isNotEmpty( superUnitName ) && !"0".equals( superUnitName )) {
					return getAttendanceScheduleSettingWithUnitName( superUnitName );
				}
			}		
		} catch (Exception e) {
			throw e;
		}
		return null;
	}
	
	private synchronized AttendanceScheduleSetting createNewScheduleSetting( String unitName, String topUnitName, Boolean debugger ) throws Exception {
		AttendanceScheduleSetting new_attendanceScheduleSetting = null;
		Business business = null;
		List<String> ids = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			business = new Business(emc);
			new_attendanceScheduleSetting = new AttendanceScheduleSetting();
			new_attendanceScheduleSetting.setAbsenceStartTime( null );
			new_attendanceScheduleSetting.setLateStartTime("9:05");
			new_attendanceScheduleSetting.setLeaveEarlyStartTime(null);
			new_attendanceScheduleSetting.setOffDutyTime("17:00");
			new_attendanceScheduleSetting.setOnDutyTime("09:00");
			new_attendanceScheduleSetting.setUnitOu( unitName );
			new_attendanceScheduleSetting.setTopUnitName( topUnitName );
			new_attendanceScheduleSetting.setUnitName( unitName );
			ids = business.getAttendanceScheduleSettingFactory().listByUnitName( unitName, topUnitName );
			if( ids == null || ids.isEmpty() ) {
				logger.debug( debugger, ">>>>>>>>>>create a new schedule setting for unit ["+topUnitName+"]["+unitName+"]......");
				emc.beginTransaction( AttendanceScheduleSetting.class );
				emc.persist( new_attendanceScheduleSetting, CheckPersistType.all );
				emc.commit();
			}
		} catch (Exception e) {
			throw e;
		}
		return new_attendanceScheduleSetting;
	}
}

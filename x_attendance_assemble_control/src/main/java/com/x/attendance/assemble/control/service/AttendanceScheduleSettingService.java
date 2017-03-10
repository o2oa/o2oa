package com.x.attendance.assemble.control.service;

import java.util.List;

import com.x.attendance.assemble.control.Business;
import com.x.attendance.entity.AttendanceScheduleSetting;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.annotation.CheckRemoveType;

public class AttendanceScheduleSettingService {

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
				attendanceScheduleSetting.copyTo( attendanceScheduleSetting_tmp );
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

	public List<String> listByDepartmentName(EntityManagerContainer emc, String name) throws Exception {
		Business business =  new Business( emc );
		return business.getAttendanceScheduleSettingFactory().listByDepartmentName( name );
	}

	public List<AttendanceScheduleSetting> list(EntityManagerContainer emc, List<String> ids) throws Exception {
		Business business =  new Business( emc );
		return business.getAttendanceScheduleSettingFactory().list( ids );
	}

	public List<String> listByCompanyName(EntityManagerContainer emc, String name) throws Exception {
		Business business =  new Business( emc );
		return business.getAttendanceScheduleSettingFactory().listByCompanyName( name );
	}
	
	
}

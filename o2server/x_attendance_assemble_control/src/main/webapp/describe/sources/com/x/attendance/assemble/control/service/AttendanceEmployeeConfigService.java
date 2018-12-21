package com.x.attendance.assemble.control.service;

import java.util.List;

import com.x.attendance.assemble.control.Business;
import com.x.attendance.entity.AttendanceEmployeeConfig;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.annotation.CheckRemoveType;

public class AttendanceEmployeeConfigService {
	
	public AttendanceEmployeeConfig get( EntityManagerContainer emc, String id ) throws Exception {
		return emc.find(id, AttendanceEmployeeConfig.class);
	}

	public List<AttendanceEmployeeConfig> list(EntityManagerContainer emc, List<String> ids) throws Exception {
		Business business =  new Business( emc );
		return business.getAttendanceEmployeeConfigFactory().list( ids );
	}
	
	public List<AttendanceEmployeeConfig> listAll(EntityManagerContainer emc) throws Exception {
		Business business =  new Business( emc );
		return business.getAttendanceEmployeeConfigFactory().listAll();
	}
	
	public List<String> listByConfigType(EntityManagerContainer emc, String requireType) throws Exception {
		Business business =  new Business( emc );
		return business.getAttendanceEmployeeConfigFactory().listByConfigType( requireType );
	}

	public AttendanceEmployeeConfig save( EntityManagerContainer emc, AttendanceEmployeeConfig attendanceEmployeeConfig ) throws Exception {
		if( attendanceEmployeeConfig == null ){
			throw new Exception( "attendanceEmployeeConfig is null!" );
		}
		AttendanceEmployeeConfig attendanceEmployeeConfig_tmp = emc.find( attendanceEmployeeConfig.getId(), AttendanceEmployeeConfig.class );
		emc.beginTransaction( AttendanceEmployeeConfig.class );
		if( attendanceEmployeeConfig_tmp != null ){
			attendanceEmployeeConfig.copyTo( attendanceEmployeeConfig_tmp, JpaObject.FieldsUnmodify );			
			emc.check( attendanceEmployeeConfig_tmp, CheckPersistType.all);
		}else{
			emc.persist( attendanceEmployeeConfig, CheckPersistType.all);	
		}
		emc.commit();
		return attendanceEmployeeConfig;
	}

	public void delete( EntityManagerContainer emc, String id ) throws Exception {
		if( id == null ){
			throw new Exception( "id is null!" );
		}
		AttendanceEmployeeConfig attendanceEmployeeConfig = emc.find( id, AttendanceEmployeeConfig.class );
		if( attendanceEmployeeConfig != null ){
			emc.beginTransaction( AttendanceEmployeeConfig.class );
			emc.remove( attendanceEmployeeConfig, CheckRemoveType.all );
			emc.commit();
		}
	}
	
}

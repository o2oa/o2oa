package com.x.attendance.assemble.control.service;

import java.util.List;

import com.x.attendance.assemble.control.Business;
import com.x.attendance.entity.AttendanceAdmin;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.annotation.CheckRemoveType;

public class AttendanceAdminService {

	public List<AttendanceAdmin> listAll( EntityManagerContainer emc ) throws Exception {
		Business business =  new Business( emc );
		return business.getAttendanceAdminFactory().listAll();
	}

	public AttendanceAdmin get( EntityManagerContainer emc, String id ) throws Exception {
		return emc.find(id, AttendanceAdmin.class);
	}

	public AttendanceAdmin save( EntityManagerContainer emc, AttendanceAdmin attendanceAdmin ) throws Exception {
		AttendanceAdmin attendanceAdmin_tmp = null;
		emc.beginTransaction( AttendanceAdmin.class );
		if( attendanceAdmin.getId() !=null && !attendanceAdmin.getId().isEmpty() ){
			attendanceAdmin_tmp = emc.find( attendanceAdmin.getId(), AttendanceAdmin.class );
			if( attendanceAdmin_tmp != null ){
				attendanceAdmin.copyTo( attendanceAdmin_tmp, JpaObject.FieldsUnmodify );
				emc.check( attendanceAdmin_tmp, CheckPersistType.all);	
			}else{
				emc.persist( attendanceAdmin, CheckPersistType.all);	
			}
		}else{
			emc.persist( attendanceAdmin, CheckPersistType.all);	
		}
		emc.commit();
		return attendanceAdmin;
	}

	public void delete( EntityManagerContainer emc, String id ) throws Exception {
		AttendanceAdmin attendanceAdmin = null;
		if( id == null || id.isEmpty() ){
			throw new Exception( "id is null, system can not delete any object." );
		}
		attendanceAdmin = emc.find( id, AttendanceAdmin.class );
		if ( null == attendanceAdmin ) {
			throw new Exception( "object is not exist {'id':'"+ id +"'}" );
		}else{
			emc.beginTransaction( AttendanceAdmin.class );
			emc.remove( attendanceAdmin, CheckRemoveType.all );
			emc.commit();
		}
	}
	
	
}

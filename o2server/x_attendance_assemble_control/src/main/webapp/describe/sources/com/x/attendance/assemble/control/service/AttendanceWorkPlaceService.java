package com.x.attendance.assemble.control.service;

import java.util.List;

import com.x.attendance.assemble.control.Business;
import com.x.attendance.entity.AttendanceWorkPlace;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.annotation.CheckRemoveType;

public class AttendanceWorkPlaceService {

	public List<AttendanceWorkPlace> listAll( EntityManagerContainer emc ) throws Exception {
		Business business =  new Business( emc );
		return business.attendanceWorkPlaceFactory().listAll();
	}

	public AttendanceWorkPlace get( EntityManagerContainer emc, String id ) throws Exception {
		return emc.find(id, AttendanceWorkPlace.class);
	}

	public AttendanceWorkPlace save( EntityManagerContainer emc, AttendanceWorkPlace attendanceWorkPlace ) throws Exception {
		AttendanceWorkPlace attendanceWorkPlace_tmp = null;
		emc.beginTransaction( AttendanceWorkPlace.class );
		if( attendanceWorkPlace.getId() !=null && !attendanceWorkPlace.getId().isEmpty() ){
			attendanceWorkPlace_tmp = emc.find( attendanceWorkPlace.getId(), AttendanceWorkPlace.class );
			if( attendanceWorkPlace_tmp != null ){
				attendanceWorkPlace.copyTo( attendanceWorkPlace_tmp, JpaObject.FieldsUnmodify );
				emc.check( attendanceWorkPlace_tmp, CheckPersistType.all);	
			}else{
				emc.persist( attendanceWorkPlace, CheckPersistType.all);	
			}
		}else{
			emc.persist( attendanceWorkPlace, CheckPersistType.all);	
		}
		emc.commit();
		return attendanceWorkPlace;
	}

	public void delete( EntityManagerContainer emc, String id ) throws Exception {
		AttendanceWorkPlace attendanceWorkPlace = null;
		if( id == null || id.isEmpty() ){
			throw new Exception("id is empty.");
		}
		attendanceWorkPlace = emc.find( id, AttendanceWorkPlace.class );
		if ( null == attendanceWorkPlace ) {
			throw new Exception( "object is not exist {'id':'"+ id +"'}" );
		}else{
			emc.beginTransaction( AttendanceWorkPlace.class );
			emc.remove( attendanceWorkPlace, CheckRemoveType.all );
			emc.commit();
		}
	}
	
	
}

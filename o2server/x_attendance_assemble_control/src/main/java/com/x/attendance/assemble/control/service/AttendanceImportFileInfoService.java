package com.x.attendance.assemble.control.service;

import java.util.List;

import com.x.attendance.assemble.control.Business;
import com.x.attendance.entity.AttendanceImportFileInfo;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.annotation.CheckRemoveType;

public class AttendanceImportFileInfoService {

	public List<AttendanceImportFileInfo> listAll( EntityManagerContainer emc ) throws Exception {
		Business business =  new Business( emc );
		return business.getAttendanceImportFileInfoFactory().listAll();
	}

	public AttendanceImportFileInfo get( EntityManagerContainer emc, String id ) throws Exception {
		return emc.find(id, AttendanceImportFileInfo.class);
	}

	public AttendanceImportFileInfo save( EntityManagerContainer emc, AttendanceImportFileInfo attendanceImportFileInfo ) throws Exception {
		AttendanceImportFileInfo attendanceImportFileInfo_tmp = null;
		emc.beginTransaction( AttendanceImportFileInfo.class );
		if( attendanceImportFileInfo.getId() !=null && !attendanceImportFileInfo.getId().isEmpty() ){
			attendanceImportFileInfo_tmp = emc.find( attendanceImportFileInfo.getId(), AttendanceImportFileInfo.class );
			if( attendanceImportFileInfo_tmp != null ){
				attendanceImportFileInfo.copyTo( attendanceImportFileInfo_tmp, JpaObject.FieldsUnmodify );
				emc.check( attendanceImportFileInfo_tmp, CheckPersistType.all);	
			}else{
				emc.persist( attendanceImportFileInfo, CheckPersistType.all);	
			}
		}else{
			emc.persist( attendanceImportFileInfo, CheckPersistType.all);	
		}
		emc.commit();
		return attendanceImportFileInfo;
	}

	public void delete( EntityManagerContainer emc, String id ) throws Exception {
		AttendanceImportFileInfo attendanceImportFileInfo = null;
		if( id == null || id.isEmpty() ){
			throw new Exception( "id is null, system can not delete any object." );
		}
		attendanceImportFileInfo = emc.find( id, AttendanceImportFileInfo.class );
		if ( null == attendanceImportFileInfo ) {
			throw new Exception( "object is not exist {'id':'"+ id +"'}" );
		}else{
			emc.beginTransaction( AttendanceImportFileInfo.class );
			emc.remove( attendanceImportFileInfo, CheckRemoveType.all );
			emc.commit();
		}
	}
	
	
}

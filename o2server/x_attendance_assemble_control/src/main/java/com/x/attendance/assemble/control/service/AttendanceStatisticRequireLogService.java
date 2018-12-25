package com.x.attendance.assemble.control.service;

import java.util.List;

import com.x.attendance.assemble.control.Business;
import com.x.attendance.entity.AttendanceStatisticRequireLog;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.annotation.CheckRemoveType;


public class AttendanceStatisticRequireLogService {

	public List<AttendanceStatisticRequireLog> listAll( EntityManagerContainer emc ) throws Exception {
		Business business =  new Business( emc );
		return business.getAttendanceStatisticRequireLogFactory().listAll();
	}

	public AttendanceStatisticRequireLog get( EntityManagerContainer emc, String id ) throws Exception {
		return emc.find(id, AttendanceStatisticRequireLog.class);
	}

	public AttendanceStatisticRequireLog save( EntityManagerContainer emc, AttendanceStatisticRequireLog attendanceStatisticRequireLog ) throws Exception {
		AttendanceStatisticRequireLog attendanceStatisticRequireLog_tmp = null;
		emc.beginTransaction( AttendanceStatisticRequireLog.class );
		if( attendanceStatisticRequireLog.getId() !=null && !attendanceStatisticRequireLog.getId().isEmpty() ){
			attendanceStatisticRequireLog_tmp = emc.find( attendanceStatisticRequireLog.getId(), AttendanceStatisticRequireLog.class );
			if( attendanceStatisticRequireLog_tmp != null ){
				attendanceStatisticRequireLog.copyTo( attendanceStatisticRequireLog_tmp, JpaObject.FieldsUnmodify );
				emc.check( attendanceStatisticRequireLog_tmp, CheckPersistType.all);	
			}else{
				emc.persist( attendanceStatisticRequireLog, CheckPersistType.all);	
			}
		}else{
			emc.persist( attendanceStatisticRequireLog, CheckPersistType.all);	
		}
		emc.commit();
		return attendanceStatisticRequireLog;
	}

	public void delete( EntityManagerContainer emc, String id ) throws Exception {
		AttendanceStatisticRequireLog attendanceStatisticRequireLog = null;
		if( id == null || id.isEmpty() ){
			throw new Exception("id is empty.");
		}
		attendanceStatisticRequireLog = emc.find( id, AttendanceStatisticRequireLog.class );
		if ( null == attendanceStatisticRequireLog ) {
			throw new Exception( "object is not exist {'id':'"+ id +"'}" );
		}else{
			emc.beginTransaction( AttendanceStatisticRequireLog.class );
			emc.remove( attendanceStatisticRequireLog, CheckRemoveType.all );
			emc.commit();
		}
	}
	
	public List<AttendanceStatisticRequireLog> listByStatisticTypeAndStatus( EntityManagerContainer emc, String statisticType, String processStatus) throws Exception {
		Business business =  new Business( emc );
		return business.getAttendanceStatisticRequireLogFactory().listByStatisticTypeAndStatus( statisticType, processStatus );
	}
	
	public List<AttendanceStatisticRequireLog> listByStatisticTypeAndStatus( String statisticType, String processStatus ) throws Exception {
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			return listByStatisticTypeAndStatus( emc, statisticType, processStatus );
		}catch(Exception e){
			throw e;
		}
	}

	public void resetStatisticError() throws Exception {
		List<AttendanceStatisticRequireLog> logList = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			Business business =  new Business( emc );
			logList = business.getAttendanceStatisticRequireLogFactory().listByStatisticTypeAndStatus( null, "ERROR" );
			if( logList != null && !logList.isEmpty() ){
				emc.beginTransaction( AttendanceStatisticRequireLog.class );
				for( AttendanceStatisticRequireLog log : logList ){
					log.setProcessStatus( "WAITING" );
					emc.check( log, CheckPersistType.all );
				}
				emc.commit();
			}
		}catch(Exception e){
			throw e;
		}
	}	
}

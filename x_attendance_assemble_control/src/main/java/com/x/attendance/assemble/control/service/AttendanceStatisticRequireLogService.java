package com.x.attendance.assemble.control.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.x.attendance.assemble.control.Business;
import com.x.attendance.entity.AttendanceStatisticRequireLog;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.annotation.CheckRemoveType;


public class AttendanceStatisticRequireLogService {
	
	private Logger logger = LoggerFactory.getLogger( AttendanceStatisticRequireLogService.class );

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
				attendanceStatisticRequireLog.copyTo( attendanceStatisticRequireLog_tmp );
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
			logger.error( "id is null, system can not delete any object." );
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
}

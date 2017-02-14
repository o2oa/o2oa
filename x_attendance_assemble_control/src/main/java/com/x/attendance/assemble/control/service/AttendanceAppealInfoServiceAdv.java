package com.x.attendance.assemble.control.service;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.x.attendance.assemble.common.date.DateOperation;
import com.x.attendance.assemble.control.Business;
import com.x.attendance.entity.AttendanceAppealInfo;
import com.x.attendance.entity.AttendanceDetail;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;

public class AttendanceAppealInfoServiceAdv {
	
	private Logger logger = LoggerFactory.getLogger( AttendanceAppealInfoServiceAdv.class );
	private AttendanceAppealInfoService attendanceAppealInfoService = new AttendanceAppealInfoService();
	private AttendanceDetailService attendanceDetailService = new AttendanceDetailService();
	private AttendanceNoticeService attendanceNoticeService = new AttendanceNoticeService();
	
	public AttendanceAppealInfo get( String id ) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return attendanceAppealInfoService.get( emc, id );	
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
	 * 
	 * @param id
	 * @param departmentName
	 * @param companyName
	 * @param processor
	 * @param processTime
	 * @param opinion
	 * @param status // 申诉状态:0-未申诉，1-申诉中，-1-申诉未通过，9-申诉通过
	 * @return
	 * @throws Exception
	 */
	public AttendanceAppealInfo firstProcessAttendanceAppeal( String id, String departmentName, String companyName,
			String processor, Date processTime, String opinion, Integer status ) throws Exception {
		AttendanceAppealInfo attendanceAppealInfo = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			//修改申诉信息状态和处理过程信息
			emc.beginTransaction( AttendanceAppealInfo.class );
			emc.beginTransaction( AttendanceDetail.class );
			attendanceAppealInfo = attendanceAppealInfoService.updateAppealProcessInfoForFirstProcess( emc, id, departmentName, companyName, processor, processTime, opinion, status, false );
			if( attendanceAppealInfo != null ){
				if ( status == 1 ) {
					attendanceDetailService.updateAppealProcessStatus( emc, id, 9, false );
					attendanceNoticeService.notifyAttendanceAppealAcceptMessage( attendanceAppealInfo, attendanceAppealInfo.getEmpName() );
				} else if ( status == 2 ) {// 如果需要继续第二级审批
					attendanceDetailService.updateAppealProcessStatus( emc, id, 1, false );
					attendanceAppealInfo.setCurrentProcessor( attendanceAppealInfo.getProcessPerson2() );
					emc.check( attendanceAppealInfo, CheckPersistType.all );
					attendanceNoticeService.notifyAttendanceAppealProcessness2Message( attendanceAppealInfo );
				} else {// 如果审批不通过
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

	public AttendanceAppealInfo secondProcessAttendanceAppeal( String id, String departmentName, String companyName,
			String processor, Date processTime, String opinion, Integer status ) throws Exception {
		AttendanceAppealInfo attendanceAppealInfo = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			//修改申诉信息状态和处理过程信息
			emc.beginTransaction( AttendanceAppealInfo.class );
			emc.beginTransaction( AttendanceDetail.class );
			attendanceAppealInfo = attendanceAppealInfoService.updateAppealProcessInfoForSecondProcess( emc, id, departmentName, companyName, processor, processTime, opinion, status, false );
			if( attendanceAppealInfo != null ){
				if ( status == 1 ) {
					attendanceDetailService.updateAppealProcessStatus( emc, id, 9, false );
					attendanceNoticeService.notifyAttendanceAppealAcceptMessage( attendanceAppealInfo, attendanceAppealInfo.getEmpName() );
				} else if ( status == 2 ) {// 如果需要继续第二级审批
					attendanceDetailService.updateAppealProcessStatus( emc, id, 1, false );
					attendanceAppealInfo.setCurrentProcessor( attendanceAppealInfo.getProcessPerson2() );
					emc.check( attendanceAppealInfo, CheckPersistType.all );
					attendanceNoticeService.notifyAttendanceAppealProcessness2Message( attendanceAppealInfo );
				} else {// 如果审批不通过
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
						logger.error( "system archive attendance appeal info got an exception.", e );
					}
				}
			}
		} catch ( Exception e ) {
			throw e;
		}
	}
	
}

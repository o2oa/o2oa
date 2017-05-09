package com.x.attendance.assemble.control.service;

import java.util.Date;
import java.util.List;

import com.x.attendance.assemble.common.date.DateOperation;
import com.x.attendance.assemble.control.Business;
import com.x.attendance.entity.AttendanceDetail;
import com.x.attendance.entity.AttendanceDetailMobile;
import com.x.attendance.entity.AttendanceEmployeeConfig;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;

public class AttendanceDetailServiceAdv {
	
	private Logger logger = LoggerFactory.getLogger( AttendanceDetailServiceAdv.class );
	private AttendanceDetailService attendanceDetailService = new AttendanceDetailService();
	private AttendanceDetailMobileService attendanceDetailMobileService = new AttendanceDetailMobileService();
	
	public AttendanceDetail get( String id ) throws Exception {
		if( id == null || id.isEmpty() ){
			return null;
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return attendanceDetailService.get( emc, id );
		} catch ( Exception e ) {
			throw e;
		}
	}
	
	public List<AttendanceDetail> list( List<String> ids ) throws Exception {
		if( ids == null || ids.isEmpty() ){
			return null;
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return attendanceDetailService.list( emc, ids );
		} catch ( Exception e ) {
			throw e;
		}
	}
	
	public List<String> listByBatchName( String file_id ) throws Exception {
		if( file_id == null || file_id.isEmpty() ){
			return null;
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return attendanceDetailService.listByBatchName( emc, file_id );
		} catch ( Exception e ) {
			throw e;
		}
	}

	public void checkAndReplenish( Date startDate, Date endDate, AttendanceEmployeeConfig attendanceEmployeeConfig) throws Exception {
		DateOperation dateOperation = new DateOperation();
		List<String> dayList = dateOperation.listDateStringBetweenDate( startDate, endDate );
		if( dayList == null ){
			return ;
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			attendanceDetailService.checkAndReplenish( emc, dayList, attendanceEmployeeConfig );
		} catch ( Exception e ) {
			throw e;
		}
	}

	public String getMaxRecordDate() throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return attendanceDetailService.getMaxRecordDate( emc );
		} catch ( Exception e ) {
			throw e;
		}
	}

	public List<String> listUserAttendanceDetailByYearAndMonth( String q_empName, String q_year, String q_month ) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return attendanceDetailService.listUserAttendanceDetailByYearAndMonth( emc, q_empName, q_year, q_month );
		} catch ( Exception e ) {
			throw e;
		}
	}

	public List<String> listUserAttendanceDetailByCycleYearAndMonth( String q_empName, String cycleYear, String cycleMonth ) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return attendanceDetailService.listUserAttendanceDetailByCycleYearAndMonth( emc, q_empName, cycleYear, cycleMonth );
		} catch ( Exception e ) {
			throw e;
		}
	}

	public List<String> listCompanyAttendanceDetailByYearAndMonth( List<String> companyNames, String q_year, String q_month) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return attendanceDetailService.listCompanyAttendanceDetailByYearAndMonth( emc, companyNames, q_year, q_month );
		} catch ( Exception e ) {
			throw e;
		}
	}

	public List<String> listDepartmentAttendanceDetailByYearAndMonth( List<String> departmentNames, String q_year,
			String q_month ) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return attendanceDetailService.listDepartmentAttendanceDetailByYearAndMonth( emc, departmentNames, q_year, q_month );
		} catch ( Exception e ) {
			throw e;
		}
	}

	public List<String> getAllAnalysenessDetails(String startDate, String endDate ) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return attendanceDetailService.getAllAnalysenessDetails( emc, startDate, endDate );
		} catch ( Exception e ) {
			throw e;
		}
	}

	public void archive( String id ) throws Exception {
		DateOperation dateOperation = new DateOperation();
		String datetime = dateOperation.getNowDateTime();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			attendanceDetailService.archive( emc, id, datetime );	
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
			ids = business.getAttendanceDetailFactory().listNonArchiveDetailInfoIds();
			if( ids != null && !ids.isEmpty() ){
				for( String id : ids ){
					try{
						attendanceDetailService.archive( emc, id, datetime );
					}catch( Exception e ){
						logger.warn( "system archive attendance appeal info got an exception." );
						logger.error(e);
					}
				}
			}
		} catch ( Exception e ) {
			throw e;
		}
	}

	public AttendanceDetail save(AttendanceDetail attendanceDetail) throws Exception {
		if( attendanceDetail == null ){
			throw new Exception("attendanceDetail is null!");
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return attendanceDetailService.save( emc, attendanceDetail );
		} catch ( Exception e ) {
			throw e;
		}
	}

	public AttendanceDetailMobile save(AttendanceDetailMobile attendanceDetailMobile) throws Exception {
		if( attendanceDetailMobile == null ){
			throw new Exception("attendanceDetailMobile is null!");
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			return attendanceDetailService.save( emc, attendanceDetailMobile );
		} catch ( Exception e ) {
			throw e;
		}
	}

	public Long countAttendanceDetailMobileForPage( String empNo, String empName, String signDescription,
			String startDate, String endDate ) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return attendanceDetailMobileService.countAttendanceDetailMobileForPage( emc, empNo, empName, signDescription,
					startDate, endDate );
		} catch ( Exception e ) {
			throw e;
		}
	}

	public List<AttendanceDetailMobile> listAttendanceDetailMobileForPage(String empNo, String empName,
			String signDescription, String startDate, String endDate, Integer selectTotal) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return attendanceDetailMobileService.listAttendanceDetailMobileForPage( emc, empNo, empName, signDescription,
					startDate, endDate, selectTotal );
		} catch ( Exception e ) {
			throw e;
		}
	}

	public AttendanceDetailMobile getMobile(String id) throws Exception {
		if( id == null || id.isEmpty() ){
			return null;
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return attendanceDetailMobileService.get( emc, id );
		} catch ( Exception e ) {
			throw e;
		}
	}
}

package com.x.attendance.assemble.control.service;

import java.util.List;

import com.x.attendance.assemble.control.Business;
import com.x.attendance.entity.AttendanceDetailMobile;
import com.x.base.core.container.EntityManagerContainer;


public class AttendanceDetailMobileService {

	public Long countAttendanceDetailMobileForPage(EntityManagerContainer emc, String empNo, String empName,
			String signDescription, String startDate, String endDate) throws Exception {
		Business business =  new Business( emc );
		return business.getAttendanceDetailMobileFactory().countAttendanceDetailMobileForPage( empNo, empName, signDescription, startDate, endDate );
	}

	public List<AttendanceDetailMobile> listAttendanceDetailMobileForPage(EntityManagerContainer emc, String empNo,
			String empName, String signDescription, String startDate, String endDate, Integer selectTotal) throws Exception {
		Business business =  new Business( emc );
		return business.getAttendanceDetailMobileFactory().listAttendanceDetailMobileForPage( empNo, empName, signDescription, startDate, endDate, selectTotal );
	}

	public AttendanceDetailMobile get(EntityManagerContainer emc, String id) throws Exception {
		Business business =  new Business( emc );
		return business.getAttendanceDetailMobileFactory().get( id );
	}

	public List<String> listAllAnalyseWithStatus(EntityManagerContainer emc, int status ) throws Exception {
		Business business =  new Business( emc );
		return business.getAttendanceDetailMobileFactory().listAllAnalyseWithStatus(status);
	}

	public List<AttendanceDetailMobile> listAttendanceDetailMobileWithEmployee(EntityManagerContainer emc, String distinguishedName, String recordDateString) throws Exception {
		Business business =  new Business( emc );
		return business.getAttendanceDetailMobileFactory().listAttendanceDetailMobileWithEmployee( distinguishedName, recordDateString );
	}

	
}

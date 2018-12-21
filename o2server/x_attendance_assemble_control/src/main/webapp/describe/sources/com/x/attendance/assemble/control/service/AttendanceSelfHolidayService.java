package com.x.attendance.assemble.control.service;

import java.util.List;

import com.x.attendance.assemble.control.Business;
import com.x.attendance.entity.AttendanceSelfHoliday;
import com.x.base.core.container.EntityManagerContainer;


public class AttendanceSelfHolidayService {

	public AttendanceSelfHoliday get( EntityManagerContainer emc, String id ) throws Exception {
		if( id == null || id.isEmpty() ){
			return null;
		}
		return emc.find(id, AttendanceSelfHoliday.class);
	}

	public List<AttendanceSelfHoliday> list(EntityManagerContainer emc, List<String> ids) throws Exception {
		if( ids == null || ids.isEmpty() ){
			return null;
		}
		Business business =  new Business( emc );
		return business.getAttendanceSelfHolidayFactory().list( ids );
	}

	public List<String> listAll(EntityManagerContainer emc) throws Exception {
		Business business =  new Business( emc );
		return business.getAttendanceSelfHolidayFactory().listAll();
	}

	public List<String> getByPersonName(EntityManagerContainer emc, String personName) throws Exception {
		Business business =  new Business( emc );
		return business.getAttendanceSelfHolidayFactory().getByPersonName( personName );
	}
}

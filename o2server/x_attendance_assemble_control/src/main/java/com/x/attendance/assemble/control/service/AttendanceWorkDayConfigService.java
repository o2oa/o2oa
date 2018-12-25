package com.x.attendance.assemble.control.service;

import java.util.List;

import com.x.attendance.assemble.control.Business;
import com.x.attendance.entity.AttendanceWorkDayConfig;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;


public class AttendanceWorkDayConfigService {

	public AttendanceWorkDayConfig get( EntityManagerContainer emc, String id ) throws Exception {
		if( id == null || id.isEmpty() ){
			return null;
		}
		return emc.find(id, AttendanceWorkDayConfig.class);
	}

	public List<AttendanceWorkDayConfig> list(EntityManagerContainer emc, List<String> ids) throws Exception {
		if( ids == null || ids.isEmpty() ){
			return null;
		}
		Business business =  new Business( emc );
		return business.getAttendanceWorkDayConfigFactory().list( ids );
	}

	public List<AttendanceWorkDayConfig> listAll(EntityManagerContainer emc) throws Exception {
		Business business =  new Business( emc );
		return business.getAttendanceWorkDayConfigFactory().listAll();
	}
	
	public List<AttendanceWorkDayConfig> listAll() throws Exception {
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			return listAll(emc);
		} catch ( Exception e ) {
			throw e;
		}	
	}
}

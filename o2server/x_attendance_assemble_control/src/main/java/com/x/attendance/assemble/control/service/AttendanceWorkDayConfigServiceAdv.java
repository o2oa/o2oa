package com.x.attendance.assemble.control.service;

import java.util.List;

import com.x.attendance.entity.AttendanceWorkDayConfig;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;


public class AttendanceWorkDayConfigServiceAdv {
	
	private AttendanceWorkDayConfigService attendanceWorkDayConfigService = new AttendanceWorkDayConfigService();
	
	public AttendanceWorkDayConfig get( String id ) throws Exception {
		if( id == null || id.isEmpty() ){
			return null;
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return attendanceWorkDayConfigService.get( emc, id );
		} catch ( Exception e ) {
			throw e;
		}
	}
	
	public List<AttendanceWorkDayConfig> list( List<String> ids ) throws Exception {
		if( ids == null || ids.isEmpty() ){
			return null;
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return attendanceWorkDayConfigService.list( emc, ids );
		} catch ( Exception e ) {
			throw e;
		}
	}

	public List<AttendanceWorkDayConfig> listAll() throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return attendanceWorkDayConfigService.listAll( emc );
		} catch ( Exception e ) {
			throw e;
		}
	}

    public List<AttendanceWorkDayConfig> getAllWorkDayConfigWithCache(Boolean debugger) throws Exception {
		return attendanceWorkDayConfigService.getAllWorkDayConfigWithCache(debugger);
    }
}

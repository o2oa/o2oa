package com.x.attendance.assemble.control.service;

import java.util.List;

import com.x.attendance.entity.AttendanceScheduleSetting;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;


public class AttendanceScheduleSettingServiceAdv {

	private AttendanceScheduleSettingService attendanceScheduleSettingService = new AttendanceScheduleSettingService();

	public List<AttendanceScheduleSetting> listAll() throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return attendanceScheduleSettingService.listAll( emc );	
		} catch ( Exception e ) {
			throw e;
		}
	}

	public AttendanceScheduleSetting get(String id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return attendanceScheduleSettingService.get( emc, id );	
		} catch ( Exception e ) {
			throw e;
		}
	}

	public AttendanceScheduleSetting save( AttendanceScheduleSetting attendanceScheduleSetting ) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return attendanceScheduleSettingService.save( emc, attendanceScheduleSetting );	
		} catch ( Exception e ) {
			throw e;
		}
	}

	public void delete( String id ) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			attendanceScheduleSettingService.delete( emc, id );	
		} catch ( Exception e ) {
			throw e;
		}
	}

	public List<String> listByDepartmentName( String name ) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return attendanceScheduleSettingService.listByDepartmentName( emc, name );	
		} catch ( Exception e ) {
			throw e;
		}
	}

	public List<AttendanceScheduleSetting> list(List<String> ids) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return attendanceScheduleSettingService.list( emc, ids );	
		} catch ( Exception e ) {
			throw e;
		}
	}

	public List<String> listByCompanyName(String name) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return attendanceScheduleSettingService.listByCompanyName( emc, name );	
		} catch ( Exception e ) {
			throw e;
		}
	}	
}

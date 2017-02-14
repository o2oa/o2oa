package com.x.attendance.assemble.control.service;

import java.util.List;

import com.x.attendance.entity.AttendanceSetting;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;


public class AttendanceSettingServiceAdv {

	private AttendanceSettingService attendanceSettingService = new AttendanceSettingService();

	public List<AttendanceSetting> listAll() throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return attendanceSettingService.listAll( emc );	
		} catch ( Exception e ) {
			throw e;
		}
	}

	public AttendanceSetting get(String id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return attendanceSettingService.get( emc, id );	
		} catch ( Exception e ) {
			throw e;
		}
	}

	public AttendanceSetting save( AttendanceSetting attendanceSetting ) throws Exception {
		AttendanceSetting attendanceSetting_old = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			attendanceSetting_old = emc.find( attendanceSetting.getId(), AttendanceSetting.class );
			if( attendanceSetting_old != null ){
				return attendanceSettingService.update( emc, attendanceSetting );	
			}else{
				return attendanceSettingService.create( emc, attendanceSetting );	
			}
		} catch ( Exception e ) {
			throw e;
		}
	}

	public void delete( String id ) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			attendanceSettingService.delete( emc, id );	
		} catch ( Exception e ) {
			throw e;
		}
	}

	public AttendanceSetting listIdsByCode( String code ) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return attendanceSettingService.listIdsByCode( emc, code );	
		} catch ( Exception e ) {
			throw e;
		}
	}	
}

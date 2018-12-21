package com.x.attendance.assemble.control.service;

import java.util.List;

import com.x.attendance.entity.AttendanceAdmin;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;


public class AttendanceAdminServiceAdv {

	private AttendanceAdminService attendanceAdminService = new AttendanceAdminService();

	public List<AttendanceAdmin> listAll() throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return attendanceAdminService.listAll( emc );	
		} catch ( Exception e ) {
			throw e;
		}
	}

	public AttendanceAdmin get(String id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return attendanceAdminService.get( emc, id );	
		} catch ( Exception e ) {
			throw e;
		}
	}

	public AttendanceAdmin save( AttendanceAdmin attendanceAdmin ) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return attendanceAdminService.save( emc, attendanceAdmin );	
		} catch ( Exception e ) {
			throw e;
		}
	}

	public void delete( String id ) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			attendanceAdminService.delete( emc, id );	
		} catch ( Exception e ) {
			throw e;
		}
	}	
}

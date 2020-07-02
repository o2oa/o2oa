package com.x.attendance.assemble.control.service;

import java.util.List;

import com.x.attendance.entity.AttendanceStatisticRequireLog;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;


public class AttendanceStatisticRequireLogServiceAdv {

	private AttendanceStatisticRequireLogService attendanceStatisticRequireLogService = new AttendanceStatisticRequireLogService();

	public List<AttendanceStatisticRequireLog> listAll() throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return attendanceStatisticRequireLogService.listAll( emc );	
		} catch ( Exception e ) {
			throw e;
		}
	}

	public AttendanceStatisticRequireLog get(String id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return attendanceStatisticRequireLogService.get( emc, id );	
		} catch ( Exception e ) {
			throw e;
		}
	}

	public AttendanceStatisticRequireLog save( AttendanceStatisticRequireLog attendanceStatisticRequireLog ) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return attendanceStatisticRequireLogService.save( emc, attendanceStatisticRequireLog );	
		} catch ( Exception e ) {
			throw e;
		}
	}

	public void delete( String id ) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			attendanceStatisticRequireLogService.delete( emc, id );	
		} catch ( Exception e ) {
			throw e;
		}
	}

    public void resetStatisticError() throws Exception {
		attendanceStatisticRequireLogService.resetStatisticError();
	}
}

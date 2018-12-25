package com.x.attendance.assemble.control.service;

import java.util.List;

import com.x.attendance.entity.AttendanceWorkPlace;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;


public class AttendanceWorkPlaceServiceAdv {

	private AttendanceWorkPlaceService attendanceWorkPlaceService = new AttendanceWorkPlaceService();

	public List<AttendanceWorkPlace> listAll() throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return attendanceWorkPlaceService.listAll( emc );	
		} catch ( Exception e ) {
			throw e;
		}
	}

	public AttendanceWorkPlace get(String id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return attendanceWorkPlaceService.get( emc, id );	
		} catch ( Exception e ) {
			throw e;
		}
	}

	public AttendanceWorkPlace save( AttendanceWorkPlace attendanceWorkPlace ) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return attendanceWorkPlaceService.save( emc, attendanceWorkPlace );	
		} catch ( Exception e ) {
			throw e;
		}
	}

	public void delete( String id ) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			attendanceWorkPlaceService.delete( emc, id );	
		} catch ( Exception e ) {
			throw e;
		}
	}	
}

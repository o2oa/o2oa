package com.x.attendance.assemble.control.service;

import java.util.List;

import com.x.attendance.entity.AttendanceImportFileInfo;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;


public class AttendanceImportFileInfoServiceAdv {

	private AttendanceImportFileInfoService attendanceImportFileInfoService = new AttendanceImportFileInfoService();

	public List<AttendanceImportFileInfo> listAll() throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return attendanceImportFileInfoService.listAll( emc );	
		} catch ( Exception e ) {
			throw e;
		}
	}

	public AttendanceImportFileInfo get(String id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return attendanceImportFileInfoService.get( emc, id );	
		} catch ( Exception e ) {
			throw e;
		}
	}

	public AttendanceImportFileInfo save( AttendanceImportFileInfo attendanceImportFileInfo ) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return attendanceImportFileInfoService.save( emc, attendanceImportFileInfo );	
		} catch ( Exception e ) {
			throw e;
		}
	}

	public void delete( String id ) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			attendanceImportFileInfoService.delete( emc, id );	
		} catch ( Exception e ) {
			throw e;
		}
	}	
}

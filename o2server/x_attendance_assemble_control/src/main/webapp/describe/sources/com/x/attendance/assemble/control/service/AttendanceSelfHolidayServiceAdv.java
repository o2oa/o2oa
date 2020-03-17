package com.x.attendance.assemble.control.service;

import com.x.attendance.entity.AttendanceSelfHoliday;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;

import java.util.List;


public class AttendanceSelfHolidayServiceAdv {
	
	private AttendanceSelfHolidayService attendanceSelfHolidayService = new AttendanceSelfHolidayService();
	
	public AttendanceSelfHoliday get( String id ) throws Exception {
		if( id == null || id.isEmpty() ){
			return null;
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return attendanceSelfHolidayService.get( emc, id );
		} catch ( Exception e ) {
			throw e;
		}
	}
	
	public List<AttendanceSelfHoliday> list( List<String> ids ) throws Exception {
		if( ids == null || ids.isEmpty() ){
			return null;
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return attendanceSelfHolidayService.list( emc, ids );
		} catch ( Exception e ) {
			throw e;
		}
	}

	public List<String> listAll() throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return attendanceSelfHolidayService.listAll( emc );
		} catch ( Exception e ) {
			throw e;
		}
	}

	public List<AttendanceSelfHoliday> listWithBatchFlag( String batchFlag) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return attendanceSelfHolidayService.listWithBatchFlag( emc, batchFlag );
		} catch ( Exception e ) {
			throw e;
		}
	}

	public List<String> getByPersonName(String personName) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return attendanceSelfHolidayService.getByPersonName( emc, personName );
		} catch ( Exception e ) {
			throw e;
		}
	}
}

package com.x.attendance.assemble.control.service;

import java.util.List;

import com.x.attendance.entity.AttendanceScheduleSetting;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.tools.ListTools;


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

	public List<String> listByUnitName( String name ) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return attendanceScheduleSettingService.listByUnitName( emc, name );	
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

	public List<String> listByTopUnitName(String name) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return attendanceScheduleSettingService.listByTopUnitName( emc, name );	
		} catch ( Exception e ) {
			throw e;
		}
	}
	
	/**
	 * 先查询直属组织，然后再递归上级组织
	 * @param personName
	 * @return
	 * @throws Exception 
	 */
	public AttendanceScheduleSetting getAttendanceScheduleSettingWithPerson( String personName, Boolean debugger ) throws Exception{
		return attendanceScheduleSettingService.getAttendanceScheduleSettingWithPerson( personName, debugger );		
	}

	/**
	 * 先查询直属组织，然后再递归上级组织
	 * @param unitName
	 * @return
	 * @throws Exception
	 */
	public AttendanceScheduleSetting getAttendanceScheduleSettingWithUnit( String unitName, Boolean debugger ) throws Exception{
		List<String> ids = listByUnitName(unitName);
		List<AttendanceScheduleSetting> list = null;
		if(ListTools.isNotEmpty( ids)){
			list = list(ids );
		}
		if(ListTools.isNotEmpty( list )){
			return list.get(0);
		}else{
			return null;
		}
	}
}

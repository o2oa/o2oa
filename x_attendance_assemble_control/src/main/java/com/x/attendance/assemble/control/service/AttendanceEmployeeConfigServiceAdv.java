package com.x.attendance.assemble.control.service;

import java.util.List;

import com.x.attendance.entity.AttendanceEmployeeConfig;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.organization.core.express.wrap.WrapDepartment;

public class AttendanceEmployeeConfigServiceAdv {
	
	private UserManagerService userManagerService = new UserManagerService();
	private AttendanceEmployeeConfigService attendanceEmployeeConfigService = new AttendanceEmployeeConfigService();

	public AttendanceEmployeeConfig get( String id ) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return attendanceEmployeeConfigService.get( emc, id );
		} catch ( Exception e ) {
			throw e;
		}
	}
	
	public List<AttendanceEmployeeConfig> list( List<String> ids ) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return attendanceEmployeeConfigService.list( emc, ids );
		} catch ( Exception e ) {
			throw e;
		}
	}
	
	public List<AttendanceEmployeeConfig> listAll() throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return attendanceEmployeeConfigService.listAll( emc );
		} catch ( Exception e ) {
			throw e;
		}
	}
	
	public List<String> listByConfigType( String requireType ) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return attendanceEmployeeConfigService.listByConfigType( emc, requireType );
		} catch ( Exception e ) {
			throw e;
		}
	}
	
	public AttendanceEmployeeConfig checkAttendanceEmployeeConfig( AttendanceEmployeeConfig attendanceEmployeeConfig ) throws Exception {
		WrapDepartment wrapDepartment = null;
		if ( attendanceEmployeeConfig.getOrganizationName() != null ){
			wrapDepartment = userManagerService.getDepartmentByName( attendanceEmployeeConfig.getOrganizationName() );
		}
		if( wrapDepartment == null ){
			wrapDepartment = userManagerService.getDepartmentByEmployeeName( attendanceEmployeeConfig.getEmployeeName() );
		}
		if( wrapDepartment != null ){
			attendanceEmployeeConfig.setOrganizationName( wrapDepartment.getName() );
			attendanceEmployeeConfig.setCompanyName( wrapDepartment.getCompany() );
		}else{
			throw new Exception( "system can not find user department with username:"+ attendanceEmployeeConfig.getEmployeeName() +"." );
		}
		return attendanceEmployeeConfig;
	}

	public AttendanceEmployeeConfig save(AttendanceEmployeeConfig attendanceEmployeeConfig) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return attendanceEmployeeConfigService.save( emc, attendanceEmployeeConfig );
		} catch ( Exception e ) {
			throw e;
		}
	}

	public void delete(String id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			attendanceEmployeeConfigService.delete( emc, id );
		} catch ( Exception e ) {
			throw e;
		}
	}
}

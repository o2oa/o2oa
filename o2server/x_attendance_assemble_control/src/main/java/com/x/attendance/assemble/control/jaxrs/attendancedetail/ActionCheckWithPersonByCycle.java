package com.x.attendance.assemble.control.jaxrs.attendancedetail;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.x.attendance.assemble.control.processor.monitor.StatusSystemImportOpt;
import com.x.attendance.assemble.control.processor.sender.SenderForSupplementData;
import com.x.attendance.entity.AttendanceEmployeeConfig;
import com.x.attendance.entity.AttendanceStatisticalCycle;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.organization.Person;
import com.x.base.core.project.organization.Unit;
import com.x.base.core.project.tools.ListTools;

public class ActionCheckWithPersonByCycle extends BaseAction {
	
	private static  Logger logger = LoggerFactory.getLogger( ActionCheckWithPersonByCycle.class );
	
	protected ActionResult<Wo> execute( HttpServletRequest request, EffectivePerson effectivePerson, String cycleYear, String cycleMonth ) throws Exception {
		logger.info( ">>>>>>>>>>系统尝试对统计周期[" + cycleYear + "-" + cycleMonth + "]的打卡数据核对......");
		ActionResult<Wo> result = new ActionResult<>();
		EffectivePerson currentPerson = this.effectivePerson(request);
		Map<String, Map<String, List<AttendanceStatisticalCycle>>> topUnitAttendanceStatisticalCycleMap = null;
		List<AttendanceEmployeeConfig> attendanceEmployeeConfigList = null;
		List<AttendanceEmployeeConfig> unitConfigList = null;
		StatusSystemImportOpt statusSystemImportOpt = StatusSystemImportOpt.getInstance();
		Boolean check = true;
		
		if( statusSystemImportOpt.getProcessing() ) {
			check = false;
			Exception exception = new ExceptionAttendanceDetailProcess( "考勤数据处理器正在处理数据中，请稍候再试......" );
			result.error(exception);
		}
		
		if (check) {
			//查询系统是否正在进行数据操作
			if( statusSystemImportOpt.getProcessing() ) {
				check = false;
				Exception exception = new ExceptionAttendanceDetailProcess( "系统正在进行数据处理，请稍候再进行数据操作." );
				result.error( exception );
			}else {
				statusSystemImportOpt.setProcessing( true );
			}
		}
		
		if (check) {
			if ( cycleYear == null || cycleYear.isEmpty() ) {
				check = false;
				Exception exception = new ExceptionCycleYearEmpty();
				result.error(exception);
			}
		}
		
		if (check) {
			if ( cycleMonth == null || cycleMonth.isEmpty() ) {
				check = false;
				Exception exception = new ExceptionCycleMonthEmpty();
				result.error(exception);
			}
		}
		
		if ( check ) {
			try {
				attendanceEmployeeConfigList = attendanceEmployeeConfigServiceAdv.listByConfigType( "REQUIRED" );
				if( ListTools.isEmpty( attendanceEmployeeConfigList ) ) {
					//如果没有配置任何需要考勤的人员，那么就是全员需要考勤
					attendanceEmployeeConfigList = new ArrayList<>();
					AttendanceEmployeeConfig config = null;
					String identity = null;
					Unit unit = null;
					Unit topUnit = null;
					List<Person> allPersonObjs = userManagerService.listAllPersons();
					if(ListTools.isNotEmpty( allPersonObjs )){
						for( Person person : allPersonObjs ){
							identity = userManagerService.getMajorIdentityWithPerson( person.getDistinguishedName() );
							if(StringUtils.isNotEmpty(identity)){
								unit = userManagerService.getUnitWithIdentity(identity);
							}
							if( unit != null ){
								topUnit = userManagerService.getTopUnitWithUnitName( unit.getDistinguishedName() );
							}
							if( topUnit != null ){
								config = new AttendanceEmployeeConfig();
								config.setId( AttendanceEmployeeConfig.createId() );
								config.setConfigType( "REQUIRED" );
								config.setEmpInTopUnitTime("1900-01-01");
								config.setEmployeeName( person.getDistinguishedName() );
								config.setEmployeeNumber( person.getEmployee() );
								config.setUnitOu( unit.getUnique() );
								config.setUnitName(  unit.getDistinguishedName());
								config.setTopUnitName( topUnit.getDistinguishedName() );
								config.setTopUnitOu( topUnit.getUnique() );
								attendanceEmployeeConfigList.add( config );
							}
						}
					}
				}else{
					//将现有考勤人员配置解析到人
					unitConfigList = new ArrayList<>();
					for(AttendanceEmployeeConfig attendanceEmployeeConfig : attendanceEmployeeConfigList){
						if(StringUtils.isNotEmpty(attendanceEmployeeConfig.getEmployeeName())){
							unitConfigList.add(attendanceEmployeeConfig);
						}else if(StringUtils.isNotEmpty(attendanceEmployeeConfig.getUnitName())){
							List<String> unitPersons = userManagerService.listWithUnitSubNested(attendanceEmployeeConfig.getUnitName());
							for(String personId :unitPersons){
								Person person = userManagerService.getPersonObjByName(personId);
								if(person !=null){
									AttendanceEmployeeConfig newEmployeeConfig = new AttendanceEmployeeConfig();
									newEmployeeConfig = attendanceEmployeeConfig;
									newEmployeeConfig.setEmployeeName( person.getDistinguishedName() );
									newEmployeeConfig.setEmployeeNumber( person.getEmployee() );
									unitConfigList.add(newEmployeeConfig);
								}
							}
						}else if(StringUtils.isNotEmpty(attendanceEmployeeConfig.getTopUnitName())){
							//将公司的人员添加
							AttendanceEmployeeConfig newEmployeeConfig = null;
							String identity = null;
							Unit unit = null;
							List<String> unitPersons = userManagerService.listWithUnitSubNested(attendanceEmployeeConfig.getTopUnitName());
							for(String personId :unitPersons){
								Person person = userManagerService.getPersonObjByName(personId);
								if(person !=null){
									newEmployeeConfig = new AttendanceEmployeeConfig();
									newEmployeeConfig.setTopUnitName(attendanceEmployeeConfig.getTopUnitName());
									newEmployeeConfig.setTopUnitOu(attendanceEmployeeConfig.getTopUnitOu());
									newEmployeeConfig.setConfigType(attendanceEmployeeConfig.getConfigType());
									identity = userManagerService.getMajorIdentityWithPerson( person.getDistinguishedName() );
									if(StringUtils.isNotEmpty(identity)){
										unit = userManagerService.getUnitWithIdentity(identity);
									}
									if( unit != null ){
										newEmployeeConfig.setUnitOu(unit.getUnique() );
										newEmployeeConfig.setUnitName( unit.getDistinguishedName());
									}
									newEmployeeConfig.setEmployeeName( person.getDistinguishedName() );
									newEmployeeConfig.setEmployeeNumber( person.getEmployee() );
									unitConfigList.add(newEmployeeConfig);
								}
							}
						}
					}
				}
			} catch ( Exception e ) {
				check = false;
				Exception exception = new ExceptionAttendanceDetailProcess( e, "系统在查询需要考勤的人员配置列表时发生异常." );
				result.error( exception );
				logger.error( e, currentPerson, request, null );
			}
		}
		
		if (check) {
			try {// 查询所有的统计周期配置，组织成Map
				topUnitAttendanceStatisticalCycleMap = attendanceStatisticCycleServiceAdv.getCycleMapFormAllCycles( effectivePerson.getDebugger() );
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionAttendanceDetailProcess( e, "系统在查询并且组织所有的统计周期时发生异常." );
				result.error(exception);
				logger.error(e, currentPerson, request, null);
			}
		}
		if (check) {
			List<AttendanceEmployeeConfig> emconfigList = new ArrayList<>();
			if(ListTools.isNotEmpty(unitConfigList)){
				emconfigList.addAll(unitConfigList);
			}else{
				emconfigList.addAll(attendanceEmployeeConfigList);
			}
			//由于检查统计周期是用的多线程，造成多份重复无法控制，改为提前检查统计周期
			for(AttendanceEmployeeConfig attendanceEmployeeConfig : emconfigList){
				/*AttendanceStatisticalCycle attendanceStatisticalCycle = attendanceStatisticCycleServiceAdv.getAttendanceDetailStatisticCycle(
						attendanceEmployeeConfig.getTopUnitName(),
						attendanceEmployeeConfig.getUnitName(),
						cycleYear,
						cycleMonth,
						topUnitAttendanceStatisticalCycleMap,
						false
				);*/
				AttendanceStatisticalCycle attendanceStatisticalCycle = attendanceStatisticCycleServiceAdv.getAttendanceDetailStatisticCycleByConfig(
						attendanceEmployeeConfig,
						cycleYear,
						cycleMonth,
						topUnitAttendanceStatisticalCycleMap,
						false
				);
				attendanceStatisticCycleServiceAdv.checkAttendanceStatisticCycle(attendanceStatisticalCycle);
			}
			new SenderForSupplementData().execute( emconfigList, topUnitAttendanceStatisticalCycleMap, cycleYear, cycleMonth, effectivePerson.getDebugger() );
		}
		
		return result;
	}
	
	public static class Wo extends WoId {
		public Wo( String id ) {
			setId( id );
		}
	}
}
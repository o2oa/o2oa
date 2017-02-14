package com.x.attendance.assemble.control.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.x.attendance.assemble.common.date.DateOperation;
import com.x.attendance.assemble.control.Business;
import com.x.attendance.entity.AttendanceDetail;
import com.x.attendance.entity.AttendanceStatisticalCycle;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.organization.core.express.wrap.WrapDepartment;
import com.x.organization.core.express.wrap.WrapIdentity;

public class AttendanceStatisticalCycleServiceAdv {
	
	private AttendanceStatisticalCycleService attendanceStatisticCycleService = new AttendanceStatisticalCycleService();
	private Logger logger = LoggerFactory.getLogger( AttendanceStatisticalCycleServiceAdv.class );
	
	public List<AttendanceStatisticalCycle> listAll() throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return attendanceStatisticCycleService.listAll( emc );
		} catch ( Exception e ) {
			throw e;
		}
	}
	
	/**
	 * 将所有的周期配置组织成一个Map便于查询操作
	 * @param cycles
	 * @return
	 * @throws Exception 
	 */
	public Map<String, Map<String, List<AttendanceStatisticalCycle>>> getCycleMapFormAllCycles( ) throws Exception{
		List<AttendanceStatisticalCycle> cycles = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			cycles = attendanceStatisticCycleService.listAll( emc );
			return attendanceStatisticCycleService.getCycleMapFormAllCycles( emc, cycles );
		} catch ( Exception e ) {
			throw e;
		}
	}
	
	
	/**
	 * 根据打卡信息明细，周期配置Map,查询适合打卡信息使用的统计周期配置，如果没有则新建一条
	 * @param detail
	 * @param companyAttendanceStatisticalCycleMap
	 * @return
	 * @throws Exception
	 */
	public AttendanceStatisticalCycle getAttendanceStatisticalCycleFormMap( AttendanceDetail detail, Map<String, Map<String, List<AttendanceStatisticalCycle>>> companyAttendanceStatisticalCycleMap ) throws Exception{
		List<AttendanceStatisticalCycle> departmentCycles = null;
		Map<String, List<AttendanceStatisticalCycle>> departmentAttendanceStatisticalCycleMap = null;
		String companyName = null, departmentName = null;
		logger.debug("[getAttendanceStatisticalCycleFormMap]根据打卡信息明细，周期配置Map,查询适合打卡信息使用的统计周期配置，如果没有则新建一条。");
		//从Map里查询与公司和部门相应的周期配置信息
		if( companyAttendanceStatisticalCycleMap != null ){
			//如果总体的Map不为空
			departmentAttendanceStatisticalCycleMap = companyAttendanceStatisticalCycleMap.get( detail.getCompanyName() );
			if( departmentAttendanceStatisticalCycleMap != null ){
				companyName = detail.getCompanyName();
				//存在当前公司的配置信息，再根据部门查询部门的配置列表是否存在[company - department]
				departmentCycles = departmentAttendanceStatisticalCycleMap.get(detail.getDepartmentName());
				if( departmentCycles != null){
					departmentName = detail.getDepartmentName();
				}else{
					departmentCycles = departmentAttendanceStatisticalCycleMap.get("*");
					departmentName = "*";
				}
			}else{
				//找公司为*的Map看看是否存在
				departmentAttendanceStatisticalCycleMap = companyAttendanceStatisticalCycleMap.get( "*" );
				companyName = "*";
				if( departmentAttendanceStatisticalCycleMap != null ){
					//存在当前公司的配置信息，再根据部门查询部门的配置列表是否存在[company - department]
					departmentCycles = departmentAttendanceStatisticalCycleMap.get(detail.getDepartmentName());
					if( departmentCycles != null){
						departmentName = detail.getDepartmentName();
					}else{
						departmentCycles = departmentAttendanceStatisticalCycleMap.get("*");
						departmentName = "*";
					}
				}else{
					departmentName = "*";
				}
			}
		}else{
			companyName = "*";
			departmentName = "*";
		}
		if( departmentCycles != null && departmentCycles.size() > 0 ){
			//根据打卡明细的统计周期年份月份来查询
			for( AttendanceStatisticalCycle attendanceStatisticalCycle : departmentCycles ){
				if( detail.getCycleYear().equals(attendanceStatisticalCycle.getCycleYear())
					&& detail.getCycleMonth().equals(attendanceStatisticalCycle.getCycleMonth())
				){
					logger.debug( "[getAttendanceStatisticalCycleFormMap]查询到适用的统计周期配置信息，返回查询结果......" );
					return attendanceStatisticalCycle;
				}
			}
		}else{
			logger.debug("根据公司["+companyName+"]和部门["+departmentName+"]未获取到任何周期数据，需要创建新的统计周期数据......");
		}
		logger.debug( "[getAttendanceStatisticalCycleFormMap]没有查询到适用的统计周期配置信息......" );
		//如果程序到此仍未返回结果，说明未找到合适的配置记录，那么新建一条
		//说明没有找到任何相关的配置，那么新创建一条配置
		DateOperation dateOperation = new DateOperation();
		String cycleMonth = dateOperation.getMonth( detail.getRecordDate() );
		if( Integer.parseInt(cycleMonth) < 10 ){
			cycleMonth = "0"+Integer.parseInt(cycleMonth);
		}
		logger.debug( "[getAttendanceStatisticalCycleFormMap]cycleMonth="+cycleMonth );
		AttendanceStatisticalCycle attendanceStatisticalCycle = new AttendanceStatisticalCycle();
		attendanceStatisticalCycle.setCompanyName(companyName);
		attendanceStatisticalCycle.setDepartmentName(departmentName);
		attendanceStatisticalCycle.setCycleMonth( cycleMonth );
		attendanceStatisticalCycle.setCycleYear( dateOperation.getYear(detail.getRecordDate()) );
		attendanceStatisticalCycle.setCycleStartDate( dateOperation.getFirstDateInMonth(detail.getRecordDate()));
		attendanceStatisticalCycle.setCycleStartDateString( dateOperation.getFirstDateStringInMonth(detail.getRecordDate()));
		attendanceStatisticalCycle.setCycleEndDate(dateOperation.getLastDateInMonth(detail.getRecordDate()));
		attendanceStatisticalCycle.setCycleEndDateString(dateOperation.getLastDateStringInMonth(detail.getRecordDate()));
		attendanceStatisticalCycle.setDescription("系统自动创建");
		//先把对象放到Map里
		if( companyAttendanceStatisticalCycleMap == null ){
			companyAttendanceStatisticalCycleMap = new HashMap<String, Map<String, List<AttendanceStatisticalCycle>>>();
		}
		departmentAttendanceStatisticalCycleMap = companyAttendanceStatisticalCycleMap.get( companyName );
		if( departmentAttendanceStatisticalCycleMap == null ){
			departmentAttendanceStatisticalCycleMap = new HashMap<String, List<AttendanceStatisticalCycle>>();
			companyAttendanceStatisticalCycleMap.put( companyName, departmentAttendanceStatisticalCycleMap);
		}
		departmentCycles = departmentAttendanceStatisticalCycleMap.get(departmentName);
		if( departmentCycles == null ){
			departmentCycles = new ArrayList<AttendanceStatisticalCycle>();
			departmentAttendanceStatisticalCycleMap.put( departmentName, departmentCycles);
		}
		putDistinctCycleInList( attendanceStatisticalCycle, departmentCycles );		
		logger.debug("[getAttendanceStatisticalCycleFormMap]准备保存新创建的统计周期信息......");
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			emc.beginTransaction(AttendanceStatisticalCycle.class);
			emc.persist( attendanceStatisticalCycle, CheckPersistType.all);
			emc.commit();
		}catch(Exception e){
			logger.error("[getAttendanceStatisticalCycleFormMap]系统在保存新的统计周期信息时发生异常！");
			throw e;
		}
		return attendanceStatisticalCycle;
	}
	
	/**
	 * 根据公司，部门，年月获取一个统计周期配置
	 * 如果不存在，则新建一个周期配置
	 * 
	 * @param q_companyName
	 * @param q_departmentName
	 * @param cycleYear
	 * @param cycleMonth
	 * @param companyAttendanceStatisticalCycleMap
	 * @return
	 * @throws Exception
	 */
	public AttendanceStatisticalCycle getAttendanceDetailStatisticCycle( String q_companyName, String q_departmentName, String cycleYear, String cycleMonth, Map<String, Map<String, List<AttendanceStatisticalCycle>>> companyAttendanceStatisticalCycleMap ) throws Exception{
		List<AttendanceStatisticalCycle> departmentCycles = null;
		Map<String, List<AttendanceStatisticalCycle>> departmentAttendanceStatisticalCycleMap = null;
		boolean hasConfig = false;
		String companyName = null, departmentName = null;
		
		if( Integer.parseInt( cycleMonth ) < 10 ){
			cycleMonth = "0"+Integer.parseInt(cycleMonth);
		}
		
		//从Map里查询与公司和部门相应的周期配置信息
		if( companyAttendanceStatisticalCycleMap != null ){
			//如果总体的Map不为空
			departmentAttendanceStatisticalCycleMap = companyAttendanceStatisticalCycleMap.get( q_companyName );
			if( departmentAttendanceStatisticalCycleMap != null ){
				//logger.debug("查询到公司[" + detail.getCompanyName() + "]的统计周期配置");
				companyName = q_companyName;
				//存在当前公司的配置信息，再根据部门查询部门的配置列表是否存在[company - department]
				departmentCycles = departmentAttendanceStatisticalCycleMap.get( q_departmentName );
				if( departmentCycles != null){
					departmentName = q_departmentName;
					//logger.debug("查询到部门["+detail.getDepartmentName()+"]的统计周期配置");
				}else{
					//logger.debug("未查询到部门["+detail.getDepartmentName()+"]的统计周期配置， 部门设置为*");
					departmentName = "*";
					departmentCycles = departmentAttendanceStatisticalCycleMap.get("*");
				}
			}else{
				//logger.debug("未查询到公司["+detail.getCompanyName()+"]的统计周期配置，公司设置为*");
				//找公司为*的Map看看是否存在
				departmentAttendanceStatisticalCycleMap = companyAttendanceStatisticalCycleMap.get( "*" );
				companyName = "*";
				if( departmentAttendanceStatisticalCycleMap != null ){
					//logger.debug("查询到公司[*]的统计周期配置，再查询部门为[*]的配置列表");
					//存在当前公司的配置信息，再根据部门查询部门的配置列表是否存在[company - department]
					departmentCycles = departmentAttendanceStatisticalCycleMap.get( q_departmentName );
					if( departmentCycles != null){
						//logger.debug("查询到公司[*]部门["+detail.getDepartmentName()+"]的统计周期配置");
						departmentName = q_departmentName;
					}else{
						//logger.debug("未查询到公司[*]部门["+detail.getDepartmentName()+"]的统计周期配置， 部门设置为*,查询部门[*]的配置");
						departmentName = "*";
						departmentCycles = departmentAttendanceStatisticalCycleMap.get("*");
					}
				}else{
					//logger.debug("未查询到公司[*]的统计周期配置，公司设置为*，系统中没有任何配置");
					departmentName = "*";
				}
			}
		}else{
			//logger.debug("统计周期配置为空，公司为*部门为*，系统中没有任何配置");
			companyName = "*";
			departmentName = "*";
		}
		if( departmentCycles != null && departmentCycles.size() > 0 ){
			//logger.debug( "周期列表信息条数：" + departmentCycles.size() );
			//说明配置信息里有配置，看看配置信息里的数据是否满足打卡信息需要的周期
			for( AttendanceStatisticalCycle attendanceStatisticalCycle : departmentCycles ){
				if( attendanceStatisticalCycle.getCycleYear().equals(cycleYear) && 
					attendanceStatisticalCycle.getCycleMonth().equals(cycleMonth)
				){
					hasConfig = true;
					return attendanceStatisticalCycle;
				}
			}
		}else{
			logger.debug("[getAttendanceDetailStatisticCycle]根据公司["+companyName+"]和部门["+departmentName+"]未获取到任何周期数据，需要创建新的统计周期数据......");
		}
		
		if( !hasConfig ){
			//logger.debug("未查询到合适的周期，根据打卡信息创建一条自然月的周期");
			//说明没有找到任何相关的配置，那么新创建一条配置
			DateOperation dateOperation = new DateOperation();
			Date day = dateOperation.getDateFromString( cycleYear + "-" + cycleMonth + "-01");
			
			AttendanceStatisticalCycle attendanceStatisticalCycle = new AttendanceStatisticalCycle();
			attendanceStatisticalCycle.setCompanyName(companyName);
			attendanceStatisticalCycle.setDepartmentName(departmentName);
			attendanceStatisticalCycle.setCycleMonth( cycleMonth );
			attendanceStatisticalCycle.setCycleYear( cycleYear );
			attendanceStatisticalCycle.setCycleStartDate( dateOperation.getFirstDateInMonth(day));
			attendanceStatisticalCycle.setCycleStartDateString( dateOperation.getFirstDateStringInMonth(day));
			attendanceStatisticalCycle.setCycleEndDate(dateOperation.getLastDateInMonth(day));
			attendanceStatisticalCycle.setCycleEndDateString(dateOperation.getLastDateStringInMonth(day));
			attendanceStatisticalCycle.setDescription("系统自动创建");
			
			//先把对象放到Map里
			if( companyAttendanceStatisticalCycleMap == null ){
				companyAttendanceStatisticalCycleMap = new HashMap<String, Map<String, List<AttendanceStatisticalCycle>>>();
			}
			departmentAttendanceStatisticalCycleMap = companyAttendanceStatisticalCycleMap.get( companyName );
			if( departmentAttendanceStatisticalCycleMap == null ){
				departmentAttendanceStatisticalCycleMap = new HashMap<String, List<AttendanceStatisticalCycle>>();
				companyAttendanceStatisticalCycleMap.put( companyName, departmentAttendanceStatisticalCycleMap);
			}
			departmentCycles = departmentAttendanceStatisticalCycleMap.get(departmentName);
			if( departmentCycles == null ){
				departmentCycles = new ArrayList<AttendanceStatisticalCycle>();
				departmentAttendanceStatisticalCycleMap.put( departmentName, departmentCycles);
			}
			putDistinctCycleInList( attendanceStatisticalCycle, departmentCycles);			
			logger.debug("[getAttendanceDetailStatisticCycle]准备保存新创建的统计周期信息......");
			try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
				emc.beginTransaction(AttendanceStatisticalCycle.class);
				emc.persist( attendanceStatisticalCycle, CheckPersistType.all);
				emc.commit();
			}catch(Exception e){
				logger.error("[getAttendanceDetailStatisticCycle]系统在保存新的统计周期信息时发生异常！");
				throw e;
			}
			return attendanceStatisticalCycle;
		}
		return null;
	}
	
	/**
	 * 根据公司，部门，年月获取一个统计周期配置
	 * 如果不存在，则新建一个周期配置
	 * 
	 * @param q_companyName
	 * @param q_departmentName
	 * @param cycleYear
	 * @param cycleMonth
	 * @param companyAttendanceStatisticalCycleMap
	 * @return
	 * @throws Exception
	 */
	public AttendanceStatisticalCycle getStatisticCycleByEmployee( String employeeName, String cycleYear, String cycleMonth, Map<String, Map<String, List<AttendanceStatisticalCycle>>> companyAttendanceStatisticalCycleMap ) throws Exception{
		List<AttendanceStatisticalCycle> departmentCycles = null;
		Map<String, List<AttendanceStatisticalCycle>> departmentAttendanceStatisticalCycleMap = null;
		boolean hasConfig = false;
		String companyName = null, departmentName = null;
		List<WrapIdentity> identities = null;
		WrapDepartment department = null;
		Business business = null;
		
		if( Integer.parseInt(cycleMonth) < 10 ){
			cycleMonth = "0"+Integer.parseInt(cycleMonth);
		}
		
		//从Map里查询与公司和部门相应的周期配置信息
		if( companyAttendanceStatisticalCycleMap != null ){
			//需要查询用户的身份
			try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
				business = new Business(emc);
				identities = business.organization().identity().listWithPerson( employeeName );
				if (identities == null || identities.size() == 0) {//该员工目前没有分配身份
					throw new Exception("can not get identity of person:" + employeeName + ".");
				}
			}catch(Exception e){
				logger.error("[getStatisticCycleByEmployee]系统在查询员工["+employeeName+"]在系统中存在的身份时发生异常！");
				throw e;
			}
			
			if( identities != null && identities.size() > 0 ){
				for( WrapIdentity wrapIdentity : identities ){
					//logger.debug("身份：" + wrapIdentity.getName());
					department = business.organization().department().getWithIdentity( wrapIdentity.getName() );
					if( department != null ){
						//如果总体的Map不为空
						departmentAttendanceStatisticalCycleMap = companyAttendanceStatisticalCycleMap.get( department.getCompany() );
						if( departmentAttendanceStatisticalCycleMap != null ){
							//logger.debug("查询到公司[" + detail.getCompanyName() + "]的统计周期配置");
							companyName = department.getCompany();
							//存在当前公司的配置信息，再根据部门查询部门的配置列表是否存在[company - department]
							departmentCycles = departmentAttendanceStatisticalCycleMap.get( department.getName() );
							if( departmentCycles != null){
								departmentName = department.getName();
								//logger.debug("查询到部门["+detail.getDepartmentName()+"]的统计周期配置");
							}else{
								//logger.debug("未查询到部门["+detail.getDepartmentName()+"]的统计周期配置， 部门设置为*");
								departmentName = "*";
								departmentCycles = departmentAttendanceStatisticalCycleMap.get("*");
							}
						}else{
							//logger.debug("未查询到公司["+detail.getCompanyName()+"]的统计周期配置，公司设置为*");
							//找公司为*的Map看看是否存在
							departmentAttendanceStatisticalCycleMap = companyAttendanceStatisticalCycleMap.get( "*" );
							companyName = "*";
							if( departmentAttendanceStatisticalCycleMap != null ){
								//logger.debug("查询到公司[*]的统计周期配置，再查询部门为[*]的配置列表");
								//存在当前公司的配置信息，再根据部门查询部门的配置列表是否存在[company - department]
								departmentCycles = departmentAttendanceStatisticalCycleMap.get( department.getName() );
								if( departmentCycles != null){
									//logger.debug("查询到公司[*]部门["+detail.getDepartmentName()+"]的统计周期配置");
									departmentName = department.getName();
								}else{
									//logger.debug("未查询到公司[*]部门["+detail.getDepartmentName()+"]的统计周期配置， 部门设置为*,查询部门[*]的配置");
									departmentName = "*";
									departmentCycles = departmentAttendanceStatisticalCycleMap.get("*");
								}
							}else{
								//logger.debug("未查询到公司[*]的统计周期配置，公司设置为*，系统中没有任何配置");
								departmentName = "*";
							}
						}
					}					
					if( departmentCycles != null && departmentCycles.size() > 0 ){
						//logger.debug( "周期列表信息条数：" + departmentCycles.size() );
						//说明配置信息里有配置，看看配置信息里的数据是否满足打卡信息需要的周期
						for( AttendanceStatisticalCycle attendanceStatisticalCycle : departmentCycles ){
							if( attendanceStatisticalCycle.getCycleYear().equals(cycleYear) && attendanceStatisticalCycle.getCycleMonth().equals(cycleMonth)){
								hasConfig = true;
								return attendanceStatisticalCycle;
							}
						}
					}
					
				}
			}
		}else{
			//logger.debug("统计周期配置为空，公司为*部门为*，系统中没有任何配置");
			companyName = "*";
			departmentName = "*";
		}
		
		if( !hasConfig ){
			//logger.debug("未查询到合适的周期，根据打卡信息创建一条自然月的周期");
			//说明没有找到任何相关的配置，那么新创建一条配置
			DateOperation dateOperation = new DateOperation();
			Date day = dateOperation.getDateFromString( cycleYear + "-" + cycleMonth + "-01");
			
			AttendanceStatisticalCycle attendanceStatisticalCycle = new AttendanceStatisticalCycle();
			attendanceStatisticalCycle.setCompanyName(companyName);
			attendanceStatisticalCycle.setDepartmentName(departmentName);
			attendanceStatisticalCycle.setCycleMonth( cycleMonth );
			attendanceStatisticalCycle.setCycleYear( cycleYear );
			attendanceStatisticalCycle.setCycleStartDate( dateOperation.getFirstDateInMonth(day));
			attendanceStatisticalCycle.setCycleStartDateString( dateOperation.getFirstDateStringInMonth(day));
			attendanceStatisticalCycle.setCycleEndDate(dateOperation.getLastDateInMonth(day));
			attendanceStatisticalCycle.setCycleEndDateString(dateOperation.getLastDateStringInMonth(day));
			attendanceStatisticalCycle.setDescription("系统自动创建");
			
			//先把对象放到Map里
			if( companyAttendanceStatisticalCycleMap == null ){
				companyAttendanceStatisticalCycleMap = new HashMap<String, Map<String, List<AttendanceStatisticalCycle>>>();
			}
			departmentAttendanceStatisticalCycleMap = companyAttendanceStatisticalCycleMap.get( companyName );
			if( departmentAttendanceStatisticalCycleMap == null ){
				departmentAttendanceStatisticalCycleMap = new HashMap<String, List<AttendanceStatisticalCycle>>();
				companyAttendanceStatisticalCycleMap.put( companyName, departmentAttendanceStatisticalCycleMap);
			}
			departmentCycles = departmentAttendanceStatisticalCycleMap.get(departmentName);
			if( departmentCycles == null ){
				departmentCycles = new ArrayList<AttendanceStatisticalCycle>();
				departmentAttendanceStatisticalCycleMap.put( departmentName, departmentCycles);
			}
			putDistinctCycleInList( attendanceStatisticalCycle, departmentCycles);			
			//logger.debug("[getStatisticCycleByEmployee]准备保存新创建的统计周期信息......");
			try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
				emc.beginTransaction(AttendanceStatisticalCycle.class);
				emc.persist( attendanceStatisticalCycle, CheckPersistType.all);
				emc.commit();
			}catch(Exception e){
				logger.error("[getStatisticCycleByEmployee]系统在保存新的统计周期信息时发生异常！");
				throw e;
			}
			return attendanceStatisticalCycle;
		}
		return null;
	}
	
	/**
	 * 将单个对象放到一个List里，并且去重复
	 * @param cycle
	 * @param companyCycles
	 * @return
	 */
	public List<AttendanceStatisticalCycle> putDistinctCycleInList( AttendanceStatisticalCycle cycle, List<AttendanceStatisticalCycle> departmentCycles) {
		if( departmentCycles == null ){
			departmentCycles = new ArrayList<AttendanceStatisticalCycle>();
		}
		//logger.debug( "查询：companyName="+cycle.getCompanyName()+", departmentName="+cycle.getDepartmentName()+", cycleYear="+cycle.getCycleYear()+", cycleMonth="+ cycle.getCycleMonth() );
		for( AttendanceStatisticalCycle attendanceStatisticalCycle : departmentCycles ){
		//	logger.debug( "遍历：companyName="+attendanceStatisticalCycle.getCompanyName()+", departmentName="+attendanceStatisticalCycle.getDepartmentName()+", cycleYear="+attendanceStatisticalCycle.getCycleYear()+", cycleMonth="+ attendanceStatisticalCycle.getCycleMonth() );
			if( attendanceStatisticalCycle.getCompanyName().equals(cycle.getCompanyName())
					&& attendanceStatisticalCycle.getDepartmentName().equals(cycle.getDepartmentName())
					&& attendanceStatisticalCycle.getCycleYear().equals(cycle.getCycleYear())
					&& attendanceStatisticalCycle.getCycleMonth().equals(cycle.getCycleMonth())
					
			){
				//对象已经存在， 不用添加，直接返回原有List
			//	logger.debug("查询成功，对象已经存在！");
				return departmentCycles;
			}
		}
		//logger.debug( "添加：companyName="+cycle.getCompanyName()+", departmentName="+cycle.getDepartmentName()+", cycleYear="+cycle.getCycleYear()+", cycleMonth="+ cycle.getCycleMonth() );
		//如果循环完了，证明不存在，要添加一个对象
		departmentCycles.add( cycle );
		return departmentCycles;
	}	
}


package com.x.attendance.assemble.control.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.x.attendance.assemble.common.date.DateOperation;
import com.x.attendance.entity.AttendanceDetail;
import com.x.attendance.entity.AttendanceEmployeeConfig;
import com.x.attendance.entity.AttendanceStatisticRequireLog;
import com.x.attendance.entity.AttendanceStatisticalCycle;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

public class AttendanceStatisticalCycleServiceAdv {

	private AttendanceStatisticalCycleService attendanceStatisticCycleService = new AttendanceStatisticalCycleService();
	private static  Logger logger = LoggerFactory.getLogger( AttendanceStatisticalCycleServiceAdv.class );
	private UserManagerService userManagerService = new UserManagerService();
	
	public List<AttendanceStatisticalCycle> listAll() throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return attendanceStatisticCycleService.listAll( emc );
		} catch ( Exception e ) {
			throw e;
		}
	}

	/**
	 * 将所有的周期配置组织成一个Map便于查询操作
	 * @param debugger
	 * @return
	 * @throws Exception
	 */
	public Map<String, Map<String, List<AttendanceStatisticalCycle>>> getCycleMapFormAllCycles( Boolean debugger ) throws Exception{
		List<AttendanceStatisticalCycle> cycles = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			cycles = attendanceStatisticCycleService.listAll( emc );
			return attendanceStatisticCycleService.getCycleMapFormAllCycles( emc, cycles, debugger );
		} catch ( Exception e ) {
			throw e;
		}
	}
	
	
	/**
	 * 根据打卡信息明细，周期配置Map,查询适合打卡信息使用的统计周期配置，如果没有则新建一条
	 * @param detail
	 * @param topUnitAttendanceStatisticalCycleMap
	 * @return
	 * @throws Exception
	 */
	public AttendanceStatisticalCycle getAttendanceStatisticalCycleFormMap( AttendanceDetail detail, Map<String, Map<String, List<AttendanceStatisticalCycle>>> topUnitAttendanceStatisticalCycleMap, Boolean debugger ) throws Exception{
		List<AttendanceStatisticalCycle> unitCycles = null;
		Map<String, List<AttendanceStatisticalCycle>> unitAttendanceStatisticalCycleMap = null;
		String topUnitName = null, unitName = null;
		logger.info("根据打卡信息明细，周期配置Map,查询适合打卡信息使用的统计周期配置，如果没有则新建一条。");
		//从Map里查询与顶层组织和组织相应的周期配置信息
		if( topUnitAttendanceStatisticalCycleMap != null ){
			//如果总体的Map不为空
			unitAttendanceStatisticalCycleMap = topUnitAttendanceStatisticalCycleMap.get( detail.getTopUnitName() );
			if( unitAttendanceStatisticalCycleMap != null ){
				topUnitName = detail.getTopUnitName();
				//存在当前顶层组织的配置信息，再根据组织查询组织的配置列表是否存在[topUnit - unit]
				unitCycles = unitAttendanceStatisticalCycleMap.get(detail.getUnitName());
				if( unitCycles != null){
					unitName = detail.getUnitName();
				}else{
					unitCycles = unitAttendanceStatisticalCycleMap.get("*");
					unitName = "*";
				}
			}else{
				//找顶层组织为*的Map看看是否存在
				unitAttendanceStatisticalCycleMap = topUnitAttendanceStatisticalCycleMap.get( "*" );
				topUnitName = "*";
				if( unitAttendanceStatisticalCycleMap != null ){
					//存在当前顶层组织的配置信息，再根据组织查询组织的配置列表是否存在[topUnit - unit]
					unitCycles = unitAttendanceStatisticalCycleMap.get(detail.getUnitName());
					if( unitCycles != null){
						unitName = detail.getUnitName();
					}else{
						unitCycles = unitAttendanceStatisticalCycleMap.get("*");
						unitName = "*";
					}
				}else{
					unitName = "*";
				}
			}
		}else{
			topUnitName = "*";
			unitName = "*";
		}
		if( unitCycles != null && unitCycles.size() > 0 ){
			//根据打卡明细的统计周期年份月份来查询
			for( AttendanceStatisticalCycle attendanceStatisticalCycle : unitCycles ){
				if( detail.getCycleYear().equals(attendanceStatisticalCycle.getCycleYear())
					&& detail.getCycleMonth().equals(attendanceStatisticalCycle.getCycleMonth())
				){
					logger.info( "查询到适用的统计周期配置信息，返回查询结果......" );
					return attendanceStatisticalCycle;
				}
			}
		}else{
			logger.info("根据顶层组织["+topUnitName+"]和组织["+unitName+"]未获取到任何周期数据，需要创建新的统计周期数据......1");
		}
		logger.info( "没有查询到适用的统计周期配置信息......" );
		//如果程序到此仍未返回结果，说明未找到合适的配置记录，那么新建一条
		//说明没有找到任何相关的配置，那么新创建一条配置
		DateOperation dateOperation = new DateOperation();
		String cycleMonth = dateOperation.getMonth( detail.getRecordDate() );
		if( Integer.parseInt(cycleMonth) < 10 ){
			cycleMonth = "0"+Integer.parseInt(cycleMonth);
		}
		logger.info( "cycleMonth="+cycleMonth );
		AttendanceStatisticalCycle attendanceStatisticalCycle = new AttendanceStatisticalCycle();
		attendanceStatisticalCycle.setTopUnitName(topUnitName);
		attendanceStatisticalCycle.setUnitName(unitName);
		attendanceStatisticalCycle.setCycleMonth( cycleMonth );
		attendanceStatisticalCycle.setCycleYear( dateOperation.getYear(detail.getRecordDate()) );
		attendanceStatisticalCycle.setCycleStartDate( dateOperation.getFirstDateInMonth(detail.getRecordDate()));
		attendanceStatisticalCycle.setCycleStartDateString( dateOperation.getFirstDateStringInMonth(detail.getRecordDate()));
		attendanceStatisticalCycle.setCycleEndDate(dateOperation.getLastDateInMonth(detail.getRecordDate()));
		attendanceStatisticalCycle.setCycleEndDateString(dateOperation.getLastDateStringInMonth(detail.getRecordDate()));
		attendanceStatisticalCycle.setDescription("系统自动创建");
		//先把对象放到Map里
		if( topUnitAttendanceStatisticalCycleMap == null ){
			topUnitAttendanceStatisticalCycleMap = new HashMap<String, Map<String, List<AttendanceStatisticalCycle>>>();
		}
		unitAttendanceStatisticalCycleMap = topUnitAttendanceStatisticalCycleMap.get( topUnitName );
		if( unitAttendanceStatisticalCycleMap == null ){
			unitAttendanceStatisticalCycleMap = new HashMap<String, List<AttendanceStatisticalCycle>>();
			topUnitAttendanceStatisticalCycleMap.put( topUnitName, unitAttendanceStatisticalCycleMap);
		}
		unitCycles = unitAttendanceStatisticalCycleMap.get(unitName);
		if( unitCycles == null ){
			unitCycles = new ArrayList<AttendanceStatisticalCycle>();
			unitAttendanceStatisticalCycleMap.put( unitName, unitCycles);
		}
		putDistinctCycleInList( attendanceStatisticalCycle, unitCycles, debugger );		
		logger.info("准备保存新创建的统计周期信息......");
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			emc.beginTransaction(AttendanceStatisticalCycle.class);
			emc.persist( attendanceStatisticalCycle, CheckPersistType.all);
			emc.commit();
		}catch(Exception e){
			logger.info("系统在保存新的统计周期信息时发生异常！" );
			throw e;
		}
		return attendanceStatisticalCycle;
	}
	
	/**
	 * 根据顶层组织，组织，年月获取一个统计周期配置
	 * 如果不存在，则新建一个周期配置
	 * 
	 * @param q_topUnitName
	 * @param q_unitName
	 * @param cycleYear
	 * @param cycleMonth
	 * @param topUnitAttendanceStatisticalCycleMap
	 * @return
	 * @throws Exception
	 */
	public AttendanceStatisticalCycle getAttendanceDetailStatisticCycle( String q_topUnitName, String q_unitName, String cycleYear, String cycleMonth, Map<String, Map<String, List<AttendanceStatisticalCycle>>> topUnitAttendanceStatisticalCycleMap, Boolean debugger  ) throws Exception{
		List<AttendanceStatisticalCycle> unitCycles = null;
		Map<String, List<AttendanceStatisticalCycle>> unitAttendanceStatisticalCycleMap = null;
		Boolean hasConfig = false;
		String topUnitName = null, unitName = null;
		if( Integer.parseInt( cycleMonth ) < 10 ){
			cycleMonth = "0"+Integer.parseInt(cycleMonth);
		}

		//从Map里查询与顶层组织和组织相应的周期配置信息
		if( topUnitAttendanceStatisticalCycleMap != null ){
			//如果总体的Map不为空
			unitAttendanceStatisticalCycleMap = topUnitAttendanceStatisticalCycleMap.get( q_topUnitName );
			if( unitAttendanceStatisticalCycleMap != null ){
				topUnitName = q_topUnitName;
				logger.info("0>>>>>>>>>>查询到顶层组织[" + topUnitName + "]的统计周期配置");
				//存在当前顶层组织的配置信息，再根据组织查询组织的配置列表是否存在[topUnit - unit]
				unitCycles = unitAttendanceStatisticalCycleMap.get( q_unitName );
				if( unitCycles != null){
					unitName = q_unitName;
					logger.debug( debugger, "1>>>>>>>>>>查询到组织["+unitName+"]的统计周期配置");
				}else{
					logger.debug( debugger, "2>>>>>>>>>>未查询到组织["+unitName+"]的统计周期配置， 组织设置为*");
					unitName = "*";
					unitCycles = unitAttendanceStatisticalCycleMap.get("*");
				}
			}else{
				logger.debug( debugger, "3>>>>>>>>>>未查询到顶层组织["+q_topUnitName+"]的统计周期配置，顶层组织设置为*");
				//找顶层组织为*的Map看看是否存在
				unitAttendanceStatisticalCycleMap = topUnitAttendanceStatisticalCycleMap.get( "*" );
				topUnitName = "*";
				if( unitAttendanceStatisticalCycleMap != null ){
					logger.debug( debugger, "4>>>>>>>>>>查询到顶层组织[*]的统计周期配置，再查询组织为[*]的配置列表");
					//存在当前顶层组织的配置信息，再根据组织查询组织的配置列表是否存在[topUnit - unit]
					unitCycles = unitAttendanceStatisticalCycleMap.get( q_unitName );
					if( unitCycles != null){
						logger.debug( debugger, "5>>>>>>>>>>查询到顶层组织[*]组织["+unitName+"]的统计周期配置");
						unitName = q_unitName;
					}else{
						logger.debug( debugger, "6>>>>>>>>>>未查询到顶层组织[*]组织["+unitName+"]的统计周期配置， 组织设置为*,查询组织[*]的配置");
						unitName = "*";
						unitCycles = unitAttendanceStatisticalCycleMap.get("*");
					}
				}else{
					logger.debug( debugger, "7>>>>>>>>>>未查询到顶层组织[*]的统计周期配置，顶层组织设置为*，系统中没有任何配置");
					unitName = "*";
				}
			}
		}else{
			logger.debug( debugger, "8>>>>>>>>>>统计周期配置为空，顶层组织为*组织为*，系统中没有任何配置");
			topUnitName = "*";
			unitName = "*";
		}
		if( unitCycles != null && unitCycles.size() > 0 ){
			//logger.debug( "周期列表信息条数：" + unitCycles.size() );
			//说明配置信息里有配置，看看配置信息里的数据是否满足打卡信息需要的周期
			for( AttendanceStatisticalCycle attendanceStatisticalCycle : unitCycles ){
				if( attendanceStatisticalCycle.getCycleYear().equals(cycleYear) &&
						attendanceStatisticalCycle.getCycleMonth().equals(cycleMonth)
				){
					hasConfig = true;
					return attendanceStatisticalCycle;
				}
			}
		}else{
			logger.info("根据顶层组织["+topUnitName+"]和组织["+unitName+"]未获取到任何周期数据，需要创建新的统计周期数据......2");
		}

		if( !hasConfig ){
			logger.debug( debugger, ">>>>>>>>>>未查询到合适的周期，根据打卡信息创建一条自然月的周期");
			//说明没有找到任何相关的配置，那么新创建一条配置
			DateOperation dateOperation = new DateOperation();
			Date day = dateOperation.getDateFromString( cycleYear + "-" + cycleMonth + "-01");

			AttendanceStatisticalCycle attendanceStatisticalCycle = new AttendanceStatisticalCycle();
			attendanceStatisticalCycle.setTopUnitName(topUnitName);
			attendanceStatisticalCycle.setUnitName(unitName);
			attendanceStatisticalCycle.setCycleMonth( cycleMonth );
			attendanceStatisticalCycle.setCycleYear( cycleYear );
			attendanceStatisticalCycle.setCycleStartDate( dateOperation.getFirstDateInMonth(day));
			attendanceStatisticalCycle.setCycleStartDateString( dateOperation.getFirstDateStringInMonth(day));
			attendanceStatisticalCycle.setCycleEndDate(dateOperation.getLastDateInMonth(day));
			attendanceStatisticalCycle.setCycleEndDateString(dateOperation.getLastDateStringInMonth(day));
			attendanceStatisticalCycle.setDescription("系统自动创建");

			//先把对象放到Map里
			if( topUnitAttendanceStatisticalCycleMap == null ){
				topUnitAttendanceStatisticalCycleMap = new HashMap<String, Map<String, List<AttendanceStatisticalCycle>>>();
			}
			unitAttendanceStatisticalCycleMap = topUnitAttendanceStatisticalCycleMap.get( topUnitName );
			if( unitAttendanceStatisticalCycleMap == null ){
				unitAttendanceStatisticalCycleMap = new HashMap<String, List<AttendanceStatisticalCycle>>();
				topUnitAttendanceStatisticalCycleMap.put( topUnitName, unitAttendanceStatisticalCycleMap);
			}
			unitCycles = unitAttendanceStatisticalCycleMap.get(unitName);
			if( unitCycles == null ){
				unitCycles = new ArrayList<AttendanceStatisticalCycle>();
				unitAttendanceStatisticalCycleMap.put( unitName, unitCycles);
			}
			putDistinctCycleInList( attendanceStatisticalCycle, unitCycles, debugger);
			/*logger.info(  ">>>>>>>>>>准备保存新创建的统计周期信息......");
			try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
				emc.beginTransaction(AttendanceStatisticalCycle.class);
				emc.persist( attendanceStatisticalCycle, CheckPersistType.all);
				emc.commit();
			}catch(Exception e){
				logger.info(  ">>>>>>>>>>系统在保存新的统计周期信息时发生异常！");
				throw e;
			}*/
			return attendanceStatisticalCycle;
		}
		return null;
	}

	/**
	 * 根据考勤人员配置，年月获取一个统计周期配置
	 * 如果不存在，则新建一个周期配置
	 * @param cycleYear
	 * @param cycleMonth
	 * @param topUnitAttendanceStatisticalCycleMap
	 * @return
	 * @throws Exception
	 */
	public AttendanceStatisticalCycle getAttendanceDetailStatisticCycleByConfig(AttendanceEmployeeConfig attendanceEmployeeConfig, String cycleYear, String cycleMonth, Map<String, Map<String, List<AttendanceStatisticalCycle>>> topUnitAttendanceStatisticalCycleMap, Boolean debugger  ) throws Exception{
		List<AttendanceStatisticalCycle> unitCycles = null;
		Map<String, List<AttendanceStatisticalCycle>> unitAttendanceStatisticalCycleMap = null;
		Boolean hasConfig = false;
		String q_topUnitName = attendanceEmployeeConfig.getTopUnitName();
		String q_unitName = attendanceEmployeeConfig.getUnitName();
		String topUnitName = null;
		String unitName = null;
		if( Integer.parseInt( cycleMonth ) < 10 ){
			cycleMonth = "0"+Integer.parseInt(cycleMonth);
		}
		logger.debug( debugger, "getAttendanceDetailStatisticCycleByConfig-----q_topUnitName="+q_topUnitName+"---q_unitName="+q_unitName);
		//从Map里查询与顶层组织和组织相应的周期配置信息
		if( topUnitAttendanceStatisticalCycleMap != null ){
			//如果总体的Map不为空
			unitAttendanceStatisticalCycleMap = topUnitAttendanceStatisticalCycleMap.get( q_topUnitName );
			if( unitAttendanceStatisticalCycleMap != null ){
				topUnitName = q_topUnitName;
				logger.debug( debugger, ">>>>>>>>>>查询到顶层组织[" + topUnitName + "]的统计周期配置");
				//存在当前顶层组织的配置信息，再根据组织查询组织的配置列表是否存在[topUnit - unit]
				unitCycles = unitAttendanceStatisticalCycleMap.get( q_unitName );
				if( unitCycles != null){
					unitName = q_unitName;
					logger.debug( debugger,  ">>>>>>>>>>查询到组织["+unitName+"]的统计周期配置");
				}else{
					logger.debug( debugger, ">>>>>>>>>>未查询到组织["+unitName+"]的统计周期配置， 组织设置为*");
					unitName = "*";
					unitCycles = unitAttendanceStatisticalCycleMap.get("*");
				}
			}else{
				logger.info( "3>>>>>>>>>>未查询到顶层组织["+q_topUnitName+"]的统计周期配置，顶层组织设置为*");
				//找顶层组织为*的Map看看是否存在
				unitAttendanceStatisticalCycleMap = topUnitAttendanceStatisticalCycleMap.get( "*" );
				topUnitName = q_topUnitName;
				if( unitAttendanceStatisticalCycleMap != null ){
					logger.debug( debugger, ">>>>>>>>>>查询到顶层组织["+q_topUnitName+"]的统计周期配置，再查询组织为[*]的配置列表");
					//存在当前顶层组织的配置信息，再根据组织查询组织的配置列表是否存在[topUnit - unit]
					unitCycles = unitAttendanceStatisticalCycleMap.get( q_unitName );
					if( unitCycles != null){
						logger.debug( debugger, ">>>>>>>>>>查询到顶层组织[*]组织["+unitName+"]的统计周期配置");
						unitName = q_unitName;
					}else{
						logger.debug( debugger, ">>>>>>>>>>未查询到顶层组织[*]组织["+unitName+"]的统计周期配置， 组织设置为*,查询组织[*]的配置");
						unitName = "*";
						unitCycles = unitAttendanceStatisticalCycleMap.get("*");
					}
				}else{
					logger.debug( debugger, ">>>>>>>>>>未查询到顶层组织[*]的统计周期配置，顶层组织设置为*，系统中没有任何配置");
					unitName = "*";
				}
			}
		}else{
			logger.debug( debugger, ">>>>>>>>>>统计周期配置为空，顶层组织为*组织为*，系统中没有任何配置");
			topUnitName = q_topUnitName;
			unitName = "*";
		}
		if( unitCycles != null && unitCycles.size() > 0 ){
			//logger.debug( "周期列表信息条数：" + unitCycles.size() );
			//说明配置信息里有配置，看看配置信息里的数据是否满足打卡信息需要的周期
			for( AttendanceStatisticalCycle attendanceStatisticalCycle : unitCycles ){
				if( attendanceStatisticalCycle.getCycleYear().equals(cycleYear) &&
						attendanceStatisticalCycle.getCycleMonth().equals(cycleMonth)
				){
					hasConfig = true;
					return attendanceStatisticalCycle;
				}
			}
		}else{
			logger.info("根据顶层组织["+topUnitName+"]和组织["+unitName+"]未获取到任何周期数据，需要创建新的统计周期数据......2");
		}

		if( !hasConfig ){
			logger.debug( debugger, ">>>>>>>>>>未查询到合适的周期，根据打卡信息创建一条自然月的周期");
			//说明没有找到任何相关的配置，那么新创建一条配置
			DateOperation dateOperation = new DateOperation();
			Date day = dateOperation.getDateFromString( cycleYear + "-" + cycleMonth + "-01");

			AttendanceStatisticalCycle attendanceStatisticalCycle = new AttendanceStatisticalCycle();
			attendanceStatisticalCycle.setTopUnitName(topUnitName);
			attendanceStatisticalCycle.setUnitName(unitName);
			attendanceStatisticalCycle.setCycleMonth( cycleMonth );
			attendanceStatisticalCycle.setCycleYear( cycleYear );
			attendanceStatisticalCycle.setCycleStartDate( dateOperation.getFirstDateInMonth(day));
			attendanceStatisticalCycle.setCycleStartDateString( dateOperation.getFirstDateStringInMonth(day));
			attendanceStatisticalCycle.setCycleEndDate(dateOperation.getLastDateInMonth(day));
			attendanceStatisticalCycle.setCycleEndDateString(dateOperation.getLastDateStringInMonth(day));
			attendanceStatisticalCycle.setDescription("系统自动创建");

			//先把对象放到Map里
			if( topUnitAttendanceStatisticalCycleMap == null ){
				topUnitAttendanceStatisticalCycleMap = new HashMap<String, Map<String, List<AttendanceStatisticalCycle>>>();
			}
			unitAttendanceStatisticalCycleMap = topUnitAttendanceStatisticalCycleMap.get( topUnitName );
			if( unitAttendanceStatisticalCycleMap == null ){
				unitAttendanceStatisticalCycleMap = new HashMap<String, List<AttendanceStatisticalCycle>>();
				topUnitAttendanceStatisticalCycleMap.put( topUnitName, unitAttendanceStatisticalCycleMap);
			}
			unitCycles = unitAttendanceStatisticalCycleMap.get(unitName);
			if( unitCycles == null ){
				unitCycles = new ArrayList<AttendanceStatisticalCycle>();
				unitAttendanceStatisticalCycleMap.put( unitName, unitCycles);
			}
			putDistinctCycleInList( attendanceStatisticalCycle, unitCycles, debugger);
			/*logger.info(  ">>>>>>>>>>准备保存新创建的统计周期信息......");
			try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
				emc.beginTransaction(AttendanceStatisticalCycle.class);
				emc.persist( attendanceStatisticalCycle, CheckPersistType.all);
				emc.commit();
			}catch(Exception e){
				logger.info(  ">>>>>>>>>>系统在保存新的统计周期信息时发生异常！");
				throw e;
			}*/
			return attendanceStatisticalCycle;
		}
		return null;
	}


	/**
	 * 根据顶层组织，组织，年月获取一个统计周期配置 如果不存在，则新建一个周期配置
	 * @param employeeName
	 * @param cycleYear
	 * @param cycleMonth
	 * @param topUnitAttendanceStatisticalCycleMap
	 * @param debugger
	 * @return
	 * @throws Exception
	 */
	public AttendanceStatisticalCycle getStatisticCycleByEmployee( String employeeName, String cycleYear, String cycleMonth, Map<String, Map<String, List<AttendanceStatisticalCycle>>> topUnitAttendanceStatisticalCycleMap, Boolean debugger ) throws Exception{
		List<AttendanceStatisticalCycle> unitCycles = null;
		Map<String, List<AttendanceStatisticalCycle>> unitAttendanceStatisticalCycleMap = null;
		boolean hasConfig = false;
		List<String> identities = null;
		String unitName = null;
		String topUnitName = null;
		
		if( Integer.parseInt(cycleMonth) < 10 ){
			cycleMonth = "0"+Integer.parseInt(cycleMonth);
		}
		
		//从Map里查询与顶层组织和组织相应的周期配置信息
		if( topUnitAttendanceStatisticalCycleMap != null ){
			//需要查询用户的身份
			try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
				identities = userManagerService.listIdentitiesWithPerson( employeeName );
				if (identities == null || identities.size() == 0) {//该员工目前没有分配身份
					throw new Exception("can not get identity of person:" + employeeName + ".");
				}
			}catch(Exception e){
				logger.warn("系统在查询员工["+employeeName+"]在系统中存在的身份时发生异常！");
				throw e;
			}
			
			if( identities != null && identities.size() > 0 ){
				for( String identity : identities ){
					logger.debug( debugger, ">>>>>>>>>>身份：" + identity );
					unitName = userManagerService.getUnitNameWithIdentity( identity );
					if( unitName != null ){
						topUnitName = userManagerService.getTopUnitNameByIdentity( identity );
						//如果总体的Map不为空
						unitAttendanceStatisticalCycleMap = topUnitAttendanceStatisticalCycleMap.get( topUnitName );
						if( unitAttendanceStatisticalCycleMap != null ){
							logger.debug( debugger, ">>>>>>>>>>查询到顶层组织[" + topUnitName + "]的统计周期配置");
							//存在当前顶层组织的配置信息，再根据组织查询组织的配置列表是否存在[topUnit - unit]
							unitCycles = unitAttendanceStatisticalCycleMap.get( unitName );
							if( unitCycles == null){
								logger.debug( debugger, ">>>>>>>>>>未查询到组织["+unitName+"]的统计周期配置， 组织设置为*");
								unitName = "*";
								unitCycles = unitAttendanceStatisticalCycleMap.get("*");
							}
						}else{
							logger.debug( debugger, ">>>>>>>>>>没有顶层组织["+topUnitName+"]的统计周期配置，顶层组织设置为*");
							//找顶层组织为*的Map看看是否存在
							unitAttendanceStatisticalCycleMap = topUnitAttendanceStatisticalCycleMap.get( "*" );
							topUnitName = "*";
							if( unitAttendanceStatisticalCycleMap != null ){
								logger.debug( debugger, ">>>>>>>>>>查询到顶层组织[*]的统计周期配置，再查询组织为[*]的配置列表");
								//存在当前顶层组织的配置信息，再根据组织查询组织的配置列表是否存在[topUnit - unit]
								unitCycles = unitAttendanceStatisticalCycleMap.get( unitName );
								if( unitCycles == null){
									//logger.debug("未查询到顶层组织[*]组织["+detail.getUnitName()+"]的统计周期配置， 组织设置为*,查询组织[*]的配置");
									unitName = "*";
									unitCycles = unitAttendanceStatisticalCycleMap.get("*");
								}
							}else{
								logger.debug( debugger, ">>>>>>>>>>未查询到顶层组织[*]的统计周期配置，顶层组织设置为*，系统中没有任何配置");
								unitName = "*";
							}
						}
					}					
					if( unitCycles != null && unitCycles.size() > 0 ){
						logger.debug( debugger, ">>>>>>>>>>周期列表信息条数：" + unitCycles.size() );
						//说明配置信息里有配置，看看配置信息里的数据是否满足打卡信息需要的周期
						for( AttendanceStatisticalCycle attendanceStatisticalCycle : unitCycles ){
							if( attendanceStatisticalCycle.getCycleYear().equals(cycleYear) && attendanceStatisticalCycle.getCycleMonth().equals(cycleMonth)){
								hasConfig = true;
								return attendanceStatisticalCycle;
							}
						}
					}
					
				}
			}
		}else{
			logger.debug( debugger, ">>>>>>>>>>统计周期配置为空，顶层组织为*组织为*，系统中没有任何配置");
			topUnitName = "*";
			unitName = "*";
		}
		
		if( !hasConfig ){
			logger.debug( debugger, ">>>>>>>>>>未查询到合适的周期，根据打卡信息创建一条自然月的周期");
			//说明没有找到任何相关的配置，那么新创建一条配置
			DateOperation dateOperation = new DateOperation();
			Date day = dateOperation.getDateFromString( cycleYear + "-" + cycleMonth + "-01");
			
			AttendanceStatisticalCycle attendanceStatisticalCycle = new AttendanceStatisticalCycle();
			attendanceStatisticalCycle.setTopUnitName( topUnitName );
			attendanceStatisticalCycle.setUnitName( unitName );
			attendanceStatisticalCycle.setCycleMonth( cycleMonth );
			attendanceStatisticalCycle.setCycleYear( cycleYear );
			attendanceStatisticalCycle.setCycleStartDate( dateOperation.getFirstDateInMonth(day));
			attendanceStatisticalCycle.setCycleStartDateString( dateOperation.getFirstDateStringInMonth(day));
			attendanceStatisticalCycle.setCycleEndDate(dateOperation.getLastDateInMonth(day));
			attendanceStatisticalCycle.setCycleEndDateString(dateOperation.getLastDateStringInMonth(day));
			attendanceStatisticalCycle.setDescription("系统自动创建");
			
			//先把对象放到Map里
			if( topUnitAttendanceStatisticalCycleMap == null ){
				topUnitAttendanceStatisticalCycleMap = new HashMap<String, Map<String, List<AttendanceStatisticalCycle>>>();
			}
			unitAttendanceStatisticalCycleMap = topUnitAttendanceStatisticalCycleMap.get( topUnitName );
			if( unitAttendanceStatisticalCycleMap == null ){
				unitAttendanceStatisticalCycleMap = new HashMap<String, List<AttendanceStatisticalCycle>>();
				topUnitAttendanceStatisticalCycleMap.put( topUnitName, unitAttendanceStatisticalCycleMap);
			}
			unitCycles = unitAttendanceStatisticalCycleMap.get( unitName );
			if( unitCycles == null ){
				unitCycles = new ArrayList<AttendanceStatisticalCycle>();
				unitAttendanceStatisticalCycleMap.put( unitName, unitCycles);
			}
			putDistinctCycleInList( attendanceStatisticalCycle, unitCycles, debugger);			
			logger.debug( debugger, ">>>>>>>>>>准备保存新创建的统计周期信息......");
			try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
				emc.beginTransaction(AttendanceStatisticalCycle.class);
				emc.persist( attendanceStatisticalCycle, CheckPersistType.all);
				emc.commit();
			}catch(Exception e){
				logger.warn("系统在保存新的统计周期信息时发生异常！");
				throw e;
			}
			return attendanceStatisticalCycle;
		}
		return null;
	}

	/**
	 * 将单个对象放到一个List里，并且去重复
	 * @param cycle
	 * @param unitCycles
	 * @param debugger
	 * @return
	 */
	public List<AttendanceStatisticalCycle> putDistinctCycleInList( AttendanceStatisticalCycle cycle, List<AttendanceStatisticalCycle> unitCycles, Boolean debugger ) {
		if( unitCycles == null ){
			unitCycles = new ArrayList<AttendanceStatisticalCycle>();
		}
		logger.debug( debugger, ">>>>>>>>>>查询：topUnitName="+cycle.getTopUnitName()+", unitName="+cycle.getUnitName()+", cycleYear="+cycle.getCycleYear()+", cycleMonth="+ cycle.getCycleMonth() );
		for( AttendanceStatisticalCycle attendanceStatisticalCycle : unitCycles ){
			logger.debug( debugger, ">>>>>>>>>>遍历：topUnitName="+attendanceStatisticalCycle.getTopUnitName()+", unitName="+attendanceStatisticalCycle.getUnitName()+", cycleYear="+attendanceStatisticalCycle.getCycleYear()+", cycleMonth="+ attendanceStatisticalCycle.getCycleMonth() );
			if( attendanceStatisticalCycle.getTopUnitName().equals(cycle.getTopUnitName())
					&& attendanceStatisticalCycle.getUnitName().equals(cycle.getUnitName())
					&& attendanceStatisticalCycle.getCycleYear().equals(cycle.getCycleYear())
					&& attendanceStatisticalCycle.getCycleMonth().equals(cycle.getCycleMonth())
					
			){
				//对象已经存在， 不用添加，直接返回原有List
				logger.debug( debugger, ">>>>>>>>>>查询成功，对象已经存在！");
				return unitCycles;
			}
		}
		logger.debug( debugger, ">>>>>>>>>>添加：topUnitName="+cycle.getTopUnitName()+", unitName="+cycle.getUnitName()+", cycleYear="+cycle.getCycleYear()+", cycleMonth="+ cycle.getCycleMonth() );
		//如果循环完了，证明不存在，要添加一个对象
		unitCycles.add( cycle );
		return unitCycles;
	}

    public Map<String, Map<String, List<AttendanceStatisticalCycle>>> getAllStatisticalCycleMapWithCache(boolean debugger) throws Exception {
		return attendanceStatisticCycleService.getAllStatisticalCycleMapWithCache(debugger);
    }

	/**
	 * 根据顶层组织，组织，年月获取一个统计周期配置 如果不存在，则新建一个周期配置
	 * @param attendanceStatisticRequireLog
	 * @param topUnitAttendanceStatisticalCycleMap
	 * @param debugger
	 * @return
	 * @throws Exception
	 */
	public AttendanceStatisticalCycle getStatisticCycleByEmployee( AttendanceStatisticRequireLog attendanceStatisticRequireLog, Map<String, Map<String, List<AttendanceStatisticalCycle>>> topUnitAttendanceStatisticalCycleMap, Boolean debugger ) throws Exception{
		return attendanceStatisticCycleService.getStatisticCycleByEmployee( attendanceStatisticRequireLog, topUnitAttendanceStatisticalCycleMap, debugger);
	}

	public void checkAttendanceStatisticCycle(AttendanceStatisticalCycle cycle){
		System.out.println("checkAttendanceStatisticCycle----start");
			if(cycle!=null){
				Boolean hasCofig = false;
				List<AttendanceStatisticalCycle> cycles = null;
				try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
					cycles = attendanceStatisticCycleService.listAll( emc );
					if(cycles.size()>0){
						for( AttendanceStatisticalCycle attendanceStatisticalCycle : cycles ){
							System.out.println( ">>>>>>>>>>遍历：cycles="+attendanceStatisticalCycle.getTopUnitName()+", unitName="+attendanceStatisticalCycle.getUnitName()+", cycleYear="+attendanceStatisticalCycle.getCycleYear()+", cycleMonth="+ attendanceStatisticalCycle.getCycleMonth() );
							if( attendanceStatisticalCycle.getTopUnitName().equals(cycle.getTopUnitName())
									&& attendanceStatisticalCycle.getUnitName().equals(cycle.getUnitName())
									&& attendanceStatisticalCycle.getCycleYear().equals(cycle.getCycleYear())
									&& attendanceStatisticalCycle.getCycleMonth().equals(cycle.getCycleMonth())

							){
								//对象已经存在，不再保存
								hasCofig = true;
								logger.info( ">>>>>>>>>>查询成功，对象已经存在！");
							}
						}
					}
				} catch ( Exception e ) {
					logger.info( "未查询到考勤统计周期信息.......");
				}
				if(!hasCofig){
					this.saveAttendanceStatisticCycle(cycle);
				}
			}
		System.out.println("checkAttendanceStatisticCycle----end");
	}
	public void saveAttendanceStatisticCycle(AttendanceStatisticalCycle cycle){
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			logger.info( "saveAttendanceStatisticCycle>>>>>>>>>>准备保存新创建的统计周期信息......");
			emc.beginTransaction(AttendanceStatisticalCycle.class);
			emc.persist( cycle, CheckPersistType.all);
			emc.commit();
		}catch ( Exception e ) {
			logger.info( "保存新创建的统计周期信息出错.......");
		}
	}
}


package com.x.attendance.assemble.control.jaxrs.attendancestatistic;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.attendance.assemble.control.service.AttendanceEmployeeConfigServiceAdv;
import com.x.attendance.assemble.control.service.AttendanceStatisticServiceAdv;
import com.x.attendance.assemble.control.service.UserManagerService;
import com.x.attendance.entity.AttendanceEmployeeConfig;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;

public class BaseAction extends StandardJaxrsAction {
	
	protected Logger logger = LoggerFactory.getLogger( BaseAction.class );
	protected UserManagerService userManagerService = new UserManagerService();
	protected AttendanceStatisticServiceAdv attendanceStatisticServiceAdv = new AttendanceStatisticServiceAdv();
	protected AttendanceEmployeeConfigServiceAdv attendanceEmployeeConfigServiceAdv = new AttendanceEmployeeConfigServiceAdv();
	
	// 根据组织递归查询下级组织
	protected List<String> getUnitNameList( String unitName, List<String> unitNameList ) throws Exception {
		if (unitNameList == null) {
			unitNameList = new ArrayList<String>();
		}
		if ( StringUtils.isNotEmpty( unitName ) && !unitNameList.contains(unitName.trim())) {
			unitNameList.add(unitName.trim());

			// 查询该组织的下级组织
			List<String> unitList = null;
			try {
				// 对查询的unit进行解析，如果有下级组织的，全部解析出来
				unitList = userManagerService.listSubUnitNameWithParent( unitName );
			} catch (Exception e) {
				throw e;
			}
			if (unitList != null && unitList.size() > 0) {
				for (String unit : unitList) {
					getUnitNameList( unit, unitNameList);
				}
			}
		}
		return unitNameList;
	}

	// 根据顶层组织递归查询下级顶层组织经及顶层组织的顶级组织
	protected List<String> getTopUnitNameList( String topUnitName, List<String> unitNameList, Boolean debugger )
			throws Exception {
		if (unitNameList == null) {
			unitNameList = new ArrayList<String>();
		}
		if ( StringUtils.isNotEmpty( topUnitName ) && !unitNameList.contains(topUnitName.trim())) {
			unitNameList.add( topUnitName.trim() );

			// 查询该组织的下级组织
			List<String> unitList = null;
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create();) {
				// 查询所有的Top组织
				unitList = userManagerService.listSubUnitNameWithParent(topUnitName);
				if ( unitList != null && unitList.size() > 0 ) {
					logger.debug( debugger, ">>>>>>>>>>根据顶层组织名称["+topUnitName+"]查询到"+unitList.size()+"个顶级组织。");
					for ( String unit : unitList) {
						if (unitNameList.contains( unit )) {
							unitNameList.add( unit );
						}
						getUnitNameList( unit, unitNameList );
					}
				} else {
					logger.debug( debugger, ">>>>>>>>>>根据顶层组织名称["+topUnitName+"]未查询到任何顶级组织。");
				}
			} catch (Exception e) {
				throw e;
			}
		}
		return unitNameList;
	}

	/**
	 * 根据List<String> topUnitNameList, List<String> unitNameList
	 * 查询所有符合查询范围所组织以及下级组织名称列表
	 * 
	 * @param topUnitNameList
	 * @param unitNameList
	 * @return
	 * @throws Exception
	 */
	protected List<String> getUnitNameList(List<String> topUnitNameList, List<String> unitNameList, Boolean debugger )
			throws Exception {

		if (unitNameList == null) {
			unitNameList = new ArrayList<String>();
		}
		// 先查询顶层组织所有的下属组织，全部加入List中
		// 对查询的unit进行解析，如果有下级组织的，全部解析出来
		if (topUnitNameList != null && topUnitNameList.size() > 0) {
			for (String topUnitName : topUnitNameList) {
				// 再递归查询所有的下级顶层组织以及所有组织
				getTopUnitNameList(topUnitName, unitNameList, debugger );
			}
		}
		// 对查询的unit进行解析，如果有下级组织的，全部解析出来
		if (unitNameList != null && unitNameList.size() > 0) {
			for (String unitName : unitNameList) {
				getUnitNameList(unitName, unitNameList);
			}
		}
		return unitNameList;
	}
	
	/**
	 * 获取不需要考勤的组织
	 * @return
	 * @throws Exception 
	 */
	protected  List<String> getUnUnitNameList() throws Exception {
		List<String> unUnitNameList = new ArrayList<String>();

		List<AttendanceEmployeeConfig> attendanceEmployeeConfigs = attendanceEmployeeConfigServiceAdv.listByConfigType("NOTREQUIRED");

		if(ListTools.isNotEmpty(attendanceEmployeeConfigs)){
			for (AttendanceEmployeeConfig attendanceEmployeeConfig : attendanceEmployeeConfigs) {
				String unitName = attendanceEmployeeConfig.getUnitName();
				String employeeName = attendanceEmployeeConfig.getEmployeeName();

				if(StringUtils.isEmpty(employeeName) && StringUtils.isNotEmpty(unitName)){
					unUnitNameList.add(unitName);
					List<String> tempUnitNameList = userManagerService.listSubUnitNameWithParent(unitName);
					if(ListTools.isNotEmpty(tempUnitNameList)){
						for(String tempUnit:tempUnitNameList){
							if(!ListTools.contains(unUnitNameList, tempUnit)){
								unUnitNameList.add(tempUnit);
							}
						}
					}
				}
			} 
		}
		return unUnitNameList;
	}
	
	/**
	 * 获取不需要考勤的人员
	 * @return
	 * @throws Exception 
	 */
	protected  List<String> getUnPersonNameList() throws Exception {
		List<String> personNameList = new ArrayList<String>();
		List<AttendanceEmployeeConfig> attendanceEmployeeConfigs = attendanceEmployeeConfigServiceAdv.listByConfigType("NOTREQUIRED");

		if(ListTools.isNotEmpty(attendanceEmployeeConfigs)){
			for (AttendanceEmployeeConfig attendanceEmployeeConfig : attendanceEmployeeConfigs) {
				String employeeName = attendanceEmployeeConfig.getEmployeeName();

				if(StringUtils.isNotEmpty(employeeName) && !ListTools.contains(personNameList, employeeName)){
					personNameList.add(employeeName);
				}
			}
		}
		return personNameList;
	}

}

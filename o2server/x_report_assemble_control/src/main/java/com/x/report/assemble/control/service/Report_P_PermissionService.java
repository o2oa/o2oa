package com.x.report.assemble.control.service;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.tools.ListTools;
import com.x.report.assemble.control.Business;
import com.x.report.assemble.control.jaxrs.report.element.PermissionInfo;
import com.x.report.assemble.control.jaxrs.report.element.PermissionName;
import com.x.report.common.date.DateOperation;
import com.x.report.core.entity.Report_I_Base;
import com.x.report.core.entity.Report_P_Permission;

/**
 * 汇报信息访问权限服务类
 * @author O2LEE
 *
 */
public class Report_P_PermissionService{
	
	public void refreshReportPermission( EntityManagerContainer emc, Report_I_Base report_I_Base, List<PermissionInfo> new_permissionList ) throws Exception {
		if( report_I_Base == null ){
			throw new Exception( "report_I_Base is null！" );
		}
		if( new_permissionList == null ){
			new_permissionList = new ArrayList<>();
		}
		List<Report_P_Permission> permissionList = null;
		String updateFlag = new DateOperation().getNowTimeChar();
		permissionList = getInitReportPermissions( report_I_Base, new_permissionList, updateFlag );
		updateAllPermissionWithList( emc, report_I_Base, permissionList, updateFlag );
	}
	
	/**
	 * 根据汇报的基础信息，和额外的权限信息，获取组织好的汇报权限信息列表
	 * @param report_I_Base
	 * @param new_permissionList
	 * @param updateFlag
	 * @return
	 * @throws Exception
	 */
	private List<Report_P_Permission> getInitReportPermissions( Report_I_Base report_I_Base, List<PermissionInfo> new_permissionList, String updateFlag ) throws Exception {
		Report_P_Permission report_P_Permission = null;
		List<Report_P_Permission> permissionList = new ArrayList<>();
		//拟稿人应该有管理和阅读的权限
		permissionList = addPersonReportPermission( report_I_Base.getTargetPerson(),  PermissionName.READER, report_I_Base, permissionList, updateFlag );
		//permissionList = addPersonReportPermission( report_I_Base.getTargetPerson(),  PermissionName.AUTHOR, report_I_Base, permissionList, updateFlag );
				
		//将传入的权限信息转换成权限对象
		if( ListTools.isNotEmpty( new_permissionList )) {
			for( PermissionInfo permission : new_permissionList ){
				report_P_Permission = createPermissionWithReport( report_I_Base, permission.getPermission(), permission.getPermissionObjectName(), permission.getPermissionObjectType(), permission.getPermissionObjectCode() );	
				report_P_Permission.setUpdateFlag( updateFlag );
				permissionList.add( report_P_Permission );
			}
		}
		//最好先去个重再返回
		return permissionList;
	}

    /**
     * 根据汇报所有的权限信息列表来更新数据库中已经存在的权限信息
     * @param emc
     * @param report
     * @param permissionList
     * @param updateFlag
     * @throws Exception
     */
	private void updateAllPermissionWithList( EntityManagerContainer emc, Report_I_Base report, List<Report_P_Permission> permissionList, String updateFlag ) throws Exception {
		List<String> ids = null;
		List<String> all_ids = null;
		List<String> exists_ids = new ArrayList<>();
		Business business = new Business( emc );
		
		//先查询所有的Report
		all_ids = business.report_P_PermissionFactory().listWithReport( report.getId() );		
		if( ListTools.isEmpty( all_ids )) {
			all_ids = new ArrayList<>();
		}
		
		if( permissionList != null && !permissionList.isEmpty() ){
			//将所有的权限信息更新到数据库中
			for( Report_P_Permission permission : permissionList ){
				emc.beginTransaction( Report_P_Permission.class );
				permission.setReportId( report.getId() );
				permission.setProfileId( report.getProfileId() );
				permission.setActivityName( report.getActivityName() );
				permission.setTargetUnit( report.getTargetUnit() );
				permission.setReportStatus( report.getReportStatus() );
				permission.setWfProcessStatus(report.getWfProcessStatus() );
				
				//查询该权限是否已经存在
				ids = business.report_P_PermissionFactory().listIds( permission.getReportId(), permission.getPermission(), permission.getPermissionObjectType(), permission.getPermissionObjectCode() );
				
				if( ListTools.isEmpty( ids ) ){
					permission.setId( Report_P_Permission.createId() );
					permission.setUpdateFlag( updateFlag ); //修改更新标识
					emc.persist( permission, CheckPersistType.all );
					exists_ids.add( permission.getId() );
				}else{
					for( String id: ids ){
						permission = emc.find( id, Report_P_Permission.class );
						if( !updateFlag.equals( permission.getUpdateFlag() )) {
							permission.setActivityName( report.getActivityName() );
							permission.setTargetUnit( report.getTargetUnit() );
							permission.setReportStatus( report.getReportStatus() );
							permission.setWfProcessStatus(report.getWfProcessStatus() );
							permission.setTitle( report.getTitle() );
							permission.setUpdateFlag( updateFlag ); //修改更新标识
							emc.check( permission, CheckPersistType.all );
							exists_ids.add( id );
						}
						break ;//只保留一条;
					}
				}
				emc.commit();
			}
		}
		
		//把没有被更新过的信息删除
		if( all_ids != null && !all_ids.isEmpty() ){
			Report_P_Permission permission = null;
			for( String id : all_ids ){				
				if( !exists_ids.contains( id )) {//说明不存在， 需要删除
					permission = emc.find( id, Report_P_Permission.class );
					if( permission != null ) {
						emc.beginTransaction( Report_P_Permission.class );
						emc.remove( permission, CheckRemoveType.all );
						emc.commit();
					}				
				}
			}
		}
	}

    /**
     * 给用户管理和阅读权限
     * @param personName
     * @param permission
     * @param report_I_Base
     * @param permissionList
     * @param updateFlag
     * @return
     * @throws Exception
     */
	private List<Report_P_Permission> addPersonReportPermission( String personName, String permission, Report_I_Base report_I_Base, 
			List<Report_P_Permission> permissionList, String updateFlag ) throws Exception{
		if( report_I_Base == null ){
			throw new Exception("report_I_Base is null！");
		}
		if( personName == null ){
			throw new Exception("personName is null！");
		}
		if( permissionList == null ){
			permissionList = new ArrayList<>();
		}		
		Report_P_Permission new_permission = createPermissionWithReport( report_I_Base, permission, personName, "人员", personName );
		new_permission.setUpdateFlag(updateFlag);
		permissionList.add( new_permission );
		return permissionList;
	}

    /**
     * 根据文档数据创建一个文档权限信息对象
     * @param report_I_Base
     * @param permission
     * @param permissionObjectName
     * @param permissionObjectType
     * @param permissionObjectCode
     * @return
     */
	public Report_P_Permission createPermissionWithReport( Report_I_Base report_I_Base, String permission, String permissionObjectName, String permissionObjectType, String permissionObjectCode ) {
		Report_P_Permission report_P_Permission = new Report_P_Permission();
		report_P_Permission.setReportId( report_I_Base.getId() );
		report_P_Permission.setProfileId( report_I_Base.getProfileId() );
		report_P_Permission.setTitle( report_I_Base.getTitle() );
		report_P_Permission.setMonth( report_I_Base.getMonth() );
		report_P_Permission.setYear( report_I_Base.getYear() );
		report_P_Permission.setWeek( report_I_Base.getWeek() );
		report_P_Permission.setReportDate( report_I_Base.getReportDate() );
		report_P_Permission.setReportDateString( report_I_Base.getReportDateString() );
		report_P_Permission.setReportObjType( report_I_Base.getReportObjType() ); //UNIT|PERSON
		report_P_Permission.setReportType( report_I_Base.getReportType() );
		report_P_Permission.setActivityName( report_I_Base.getActivityName()  );
		report_P_Permission.setPermission( permission );
		report_P_Permission.setPermissionObjectName( permissionObjectName );
		report_P_Permission.setPermissionObjectType( permissionObjectType );
		report_P_Permission.setPermissionObjectCode( permissionObjectCode );
		
		report_P_Permission.setCreateDateString( report_I_Base.getCreateDateString() );
		
		return report_P_Permission;
	}

	public List<String> lisViewableReportIdsWithFilter(EntityManagerContainer emc, String title, String reportType, String year, String month,
			String week, String reportDateString, String createDateString, List<String> activityList, List<String> targetList, List<String> currentPersonList, 
			List<String> unitList, String reportObjType, 
			String wfProcessStatus, List<String> permissionObjectCodes, String permission, int maxResultCount) throws Exception {
		Business business = new Business( emc );
		return business.report_P_PermissionFactory().lisViewableReportIdsWithFilter(
				title, reportType, year, month, week, reportDateString, createDateString, activityList,
				unitList, reportObjType, wfProcessStatus, permissionObjectCodes, permission, maxResultCount
		);
	}

	public List<String> listAllCreateDateString(EntityManagerContainer emc, List<String> permissionCodes, String year, String month ) throws Exception {
		Business business = new Business( emc );
		return business.report_P_PermissionFactory().listAllCreateDateString(
				permissionCodes, year, month
		);
	}	
}

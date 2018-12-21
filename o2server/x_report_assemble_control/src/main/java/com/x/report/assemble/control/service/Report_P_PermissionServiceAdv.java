package com.x.report.assemble.control.service;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.tools.ListTools;
import com.x.report.assemble.control.Business;
import com.x.report.assemble.control.jaxrs.report.element.PermissionInfo;
import com.x.report.assemble.control.jaxrs.report.element.PermissionName;
import com.x.report.core.entity.Report_I_Base;
import com.x.report.core.entity.Report_I_Detail;

/**
 * 汇报信息访问权限服务类
 * @author O2LEE
 *
 */
public class Report_P_PermissionServiceAdv{

	private Gson gson = XGsonBuilder.instance();
	private Report_P_PermissionService report_I_PermissionService = new Report_P_PermissionService();
	private Report_I_QueryService report_I_QueryService = new Report_I_QueryService();

	/**
	 * 更新汇报的访问权限信息
	 * @param reportId
	 * @throws Exception
	 */
	public void refreshReportPermission( String reportId ) throws Exception {
		List<PermissionInfo> permissionList = new ArrayList<>();
		List<PermissionInfo> readerList = null;
		List<PermissionInfo> authorList = null;
		List<Report_I_Detail> detailList;
		Report_I_Base report_I_Base;

		Business business;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			business = new Business(emc);
			report_I_Base = report_I_QueryService.get( emc, reportId );
			detailList = business.report_I_DetailFactory().listWithReportId( reportId );
			if(ListTools.isNotEmpty( detailList )){
				Type type = new TypeToken<ArrayList<PermissionInfo>>() {}.getType();
				if(StringUtils.isNotEmpty( detailList.get(0).getReaders() ) && detailList.get(0).getReaders().length() > 10 ){
					readerList = gson.fromJson( detailList.get(0).getReaders(), type );
				}
				if(StringUtils.isNotEmpty( detailList.get(0).getAuthors() )&& detailList.get(0).getAuthors().length() > 10 ){
					authorList = gson.fromJson( detailList.get(0).getAuthors(), type );
				}
			}
		} catch ( Exception e ) {
			throw e;
		}

		//汇报组织管理员可以看到
		permissionList.add( new PermissionInfo( PermissionName.READER, "人员", report_I_Base.getTargetPerson(), report_I_Base.getTargetPerson() ) );

		//添加所有读者的权限
		if ( ListTools.isNotEmpty(readerList) ) {
			//添加读者信息，如果没有限定读者，那么所有可访问分类的用户可读
			for( PermissionInfo p : readerList ) {
				permissionList.add( new PermissionInfo( PermissionName.READER, p.getPermissionObjectType(), p.getPermissionObjectName(), p.getPermissionObjectName() ));
			}
		}

		//添加所有作者的权限
		if ( ListTools.isNotEmpty(authorList)) {
			//将所有的作者都添加到阅读者里去，作者可以对文档进行编辑
			for( PermissionInfo p : authorList ) {
				permissionList.add( new PermissionInfo( PermissionName.READER, p.getPermissionObjectType(), p.getPermissionObjectName(), p.getPermissionObjectName() ));
				permissionList.add( new PermissionInfo( PermissionName.AUTHOR, p.getPermissionObjectType(), p.getPermissionObjectName(), p.getPermissionObjectName() ));
			}
		}

		//将读者以及作者信息持久化到数据库中
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			report_I_PermissionService.refreshReportPermission( emc, report_I_Base, permissionList );
		} catch ( Exception e ) {
			throw e;
		}
	}
	
	/**
	 * 更新汇报的访问权限信息
	 * @param reportId
	 * @throws Exception
	 */
	public void refreshReportPermission( String reportId, List<PermissionInfo> readerList, List<PermissionInfo> authorList ) throws Exception {
		List<PermissionInfo> permissionList = new ArrayList<>();
		Report_I_Base report_I_Base;
		
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			report_I_Base = report_I_QueryService.get( emc, reportId );
		} catch ( Exception e ) {
			throw e;
		}

		//汇报组织管理员可以看到
		permissionList.add( new PermissionInfo( PermissionName.READER, "人员", report_I_Base.getTargetPerson(), report_I_Base.getTargetPerson() ) );

		//添加所有读者的权限
		if ( ListTools.isNotEmpty(readerList) ) {
			//添加读者信息，如果没有限定读者，那么所有可访问分类的用户可读
			for( PermissionInfo p : readerList ) {
				System.out.println(">>>>>>>>readerList读者权限:" + p.getPermission() + ", " + p.getPermissionObjectName());
				permissionList.add( new PermissionInfo( PermissionName.READER, p.getPermissionObjectType(), p.getPermissionObjectName(), p.getPermissionObjectName() ));
			}
		}

		//添加所有作者的权限
		if ( ListTools.isNotEmpty(authorList)) {
			//将所有的作者都添加到阅读者里去，作者可以对文档进行编辑
			for( PermissionInfo p : authorList ) {
				System.out.println(">>>>>>>>authorList作者权限:" + p.getPermission() + ", " + p.getPermissionObjectName());
				permissionList.add( new PermissionInfo( PermissionName.READER, p.getPermissionObjectType(), p.getPermissionObjectName(), p.getPermissionObjectName() ));
				permissionList.add( new PermissionInfo( PermissionName.AUTHOR, p.getPermissionObjectType(), p.getPermissionObjectName(), p.getPermissionObjectName() ));
			}
		}

		//将读者以及作者信息持久化到数据库中
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			report_I_PermissionService.refreshReportPermission( emc, report_I_Base, permissionList );
		} catch ( Exception e ) {
			throw e;
		}
	}

	/**
	 * 根据条件查询用户可以访问的汇报信息ID列表
	 * @param title
	 * @param reportType
	 * @param year
	 * @param month
	 * @param week
	 * @param reportDateString
	 * @param createDateString
	 * @param activityList
	 * @param targetList
	 * @param currentPersonList
	 * @param unitList
	 * @param reportObjType
	 * @param wfProcessStatus
	 * @param permissionObjectCodes
	 * @param permission
	 * @param maxResultCount
	 * @return
	 * @throws Exception
	 */
	public List<String> lisViewableReportIdsWithFilter( String title, String reportType, String year, String month,
			String week, String reportDateString, String createDateString, List<String> activityList, List<String> targetList, 
			List<String> currentPersonList, List<String> unitList, String reportObjType, 
			String wfProcessStatus, List<String> permissionObjectCodes, String permission, int maxResultCount ) throws Exception {
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			return report_I_PermissionService.lisViewableReportIdsWithFilter(
					emc, title, reportType, year, month, week, reportDateString, createDateString, activityList, targetList, currentPersonList, 
					unitList, reportObjType, wfProcessStatus, permissionObjectCodes, permission, maxResultCount
			);
		} catch ( Exception e ) {
			throw e;
		}
	}

	/**
	 * 列示用户可以访问的汇报中所有的日期列表
	 * @param permissionCodes
	 * @return
	 * @throws Exception
	 */
	public List<String> listAllCreateDateString(List<String> permissionObjectCodes ) throws Exception {
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			return report_I_PermissionService.listAllCreateDateString(
					emc, permissionObjectCodes, null, null
			);
		} catch ( Exception e ) {
			throw e;
		}
	}

	public List<String> listAllCreateDateString(List<String> permissionObjectCodes, String year, String month) throws Exception {
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			return report_I_PermissionService.listAllCreateDateString(
					emc, permissionObjectCodes, year, month
			);
		} catch ( Exception e ) {
			throw e;
		}
	}
}

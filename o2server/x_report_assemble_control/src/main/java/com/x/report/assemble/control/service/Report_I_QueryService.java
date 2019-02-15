package com.x.report.assemble.control.service;

import java.util.List;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.report.assemble.control.Business;
import com.x.report.core.entity.Report_I_Base;
import com.x.report.core.entity.Report_I_Detail;

/**
 * 汇报信息服务类
 * @author O2LEE
 *
 */
public class Report_I_QueryService{

	public Report_I_Base get(EntityManagerContainer emc, String id) throws Exception {
		return emc.find( id, Report_I_Base.class );
	}

	/**
	 * 根据指定的ID列示汇报生成依据记录信息
	 * @param emc
	 * @param ids 汇报生成依据记录信息ID列表
	 * @return
	 * @throws Exception
	 */
	public List<Report_I_Base> list(EntityManagerContainer emc, List<String> ids) throws Exception {
		if( ids == null || ids.isEmpty() ){
			return null;
		}
		Business business = new Business( emc );
		return business.report_I_BaseFactory().list( ids );
	}

	

	public List<Report_I_Base> list(EntityManagerContainer emc, String reportType, String reportObjType, String targetPerson,
			String targetUnit, String year, String month, String week, String reportDateString, String createDateString, Boolean listHiddenReport  ) throws Exception {
		Business business = new Business( emc );
		return business.report_I_BaseFactory().list( reportType, reportObjType, targetUnit, year, month, week, 
				reportDateString, createDateString,listHiddenReport );
	}

	public List<Report_I_Detail> listDetail(EntityManagerContainer emc, String reportId) throws Exception {
		if ( StringUtils.isEmpty( reportId ) ) {
			throw new Exception("reportId is empty.");
		}
		Business business = new Business( emc );
		return business.report_I_DetailFactory().listWithReportId( reportId );
	}

	public List<String> lisViewableIdsWithFilter( EntityManagerContainer emc, String title, String reportType, String reportObjType, 
			String year, String month, String week, List<String> activityList, List<String> targetList, List<String> currentPersonList, List<String> unitList,
			String wfProcessStatus, int maxResultCount, Boolean listHiddenReport ) throws Exception {
		Business business = new Business( emc );
		return business.report_I_BaseFactory().lisViewableIdsWithFilter(
				title, reportType, reportObjType, year, month, week, activityList, unitList, wfProcessStatus,
				maxResultCount,listHiddenReport
		);
	}

	public Long countWithIds(EntityManagerContainer emc, List<String> viewAbleReportIds) throws Exception {
		Business business = new Business( emc );
		return business.report_I_BaseFactory().countWithIds(viewAbleReportIds);
	}

	public List<Report_I_Base> listNextWithDocIds( EntityManagerContainer emc, String id, Integer count, 
			List<String> viewAbleReportIds, String orderField, String orderType, Boolean listHiddenReport ) throws Exception {
		Business business = new Business(emc);
		Report_I_Base report = null;
		Object sequenceFieldValue = null;
		if( orderField == null || orderField.isEmpty() ){
			orderField = "createTime";
		}
		if( orderType == null || orderType.isEmpty() ){
			orderType = "DESC";
		}
		//查询出ID对应的记录的sequence
		if( id != null && !"(0)".equals(id) && !id.isEmpty() ){
			if ( !StringUtils.equalsIgnoreCase( id, StandardJaxrsAction.EMPTY_SYMBOL ) ) {
				report = emc.find( id, Report_I_Base.class );
				if( report != null ){
					if( "title".equals( orderField  )){//标题
						sequenceFieldValue = PropertyUtils.getProperty( report, orderField );
					}else if( "createTime".equals( orderField  )){//创建时间
						sequenceFieldValue = PropertyUtils.getProperty( report, orderField );
					}else if( "reportDate".equals( orderField  )){//创建时间
						sequenceFieldValue = PropertyUtils.getProperty( report, orderField );
					}else if( "targetUnit".equals( orderField  )){//创建组织
						sequenceFieldValue = PropertyUtils.getProperty( report, "targetUnit_sequence" );
					}else if( "targetPerson".equals( orderField  )){//targetPerson
						sequenceFieldValue = PropertyUtils.getProperty( report, "targetPerson_sequence" );
					}else if(  JpaObject.sequence_FIELDNAME.equals( orderField  )){//sequence
						sequenceFieldValue = PropertyUtils.getProperty( report, orderField );
					}
				}
			}
		}
		return business.report_I_BaseFactory().listNextWithDocIds( count, viewAbleReportIds, sequenceFieldValue, orderField, orderType,listHiddenReport );
	}

	public List<String> listIdsForStartWfInProfile( EntityManagerContainer emc, String profileId ) throws Exception {
		Business business = new Business( emc );
		return business.report_I_BaseFactory().listIdsForStartWfInProfile( profileId );
	}

	public List<String> listAllProcessingReportIds(EntityManagerContainer emc, Boolean listHiddenReport ) throws Exception {
		Business business = new Business( emc );
		return business.report_I_BaseFactory().listAllProcessingReportIds( listHiddenReport);
	}

	public List<String> listIdsWithYear(EntityManagerContainer emc, String year, Boolean listHiddenReport ) throws Exception {
		if ( StringUtils.isEmpty( year )) {
			throw new Exception("year is null.");
		}
		Business business = new Business( emc );
		return business.report_I_BaseFactory().listIdsWithYear( year,listHiddenReport );
	}

	public List<String> listWithConditions(EntityManagerContainer emc, String year, String month, List<String> unitList,
			List<String> wfProcessStatus, List<String> wfActivityNames, Boolean listHiddenReport ) throws Exception {
		Business business = new Business( emc );
		return business.report_I_BaseFactory().listWithConditions( year, month, unitList, wfProcessStatus, wfActivityNames,listHiddenReport );
	}

	public List<String> listUnitNamesWithConditions(EntityManagerContainer emc, String year, String month,
			List<String> wfProcessStatus, List<String> wfActivityNames, Boolean listHiddenReport ) throws Exception {
		Business business = new Business( emc );
		return business.report_I_BaseFactory().listUnitNamesWithConditions( year, month, wfProcessStatus, wfActivityNames,listHiddenReport );
	}
}

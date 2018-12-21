package com.x.report.assemble.control.service;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.tools.ListTools;
import com.x.report.assemble.control.jaxrs.workinfo.ActionSaveWorkInfo;
import com.x.report.core.entity.Report_I_WorkInfo;
import com.x.report.core.entity.Report_I_WorkInfoDetail;
import com.x.report.core.entity.Report_I_WorkTag;

/**
 *  战略举措信息信息服务类
 * @author O2LEE
 *
 */
public class Report_I_WorkInfoServiceAdv {

	private Report_I_WorkInfoService report_I_WorkInfoService = new Report_I_WorkInfoService();

	/**
	 * 根据指定的ID列示 战略举措信息信息
	 * @param id  战略举措信息信息ID列表
	 * @return
	 * @throws Exception
	 */
	public Report_I_WorkInfo get( String id ) throws Exception {
		if (id == null || StringUtils.isEmpty( id )) {
			throw new Exception("id is null.");
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return report_I_WorkInfoService.get(emc, id);
		} catch (Exception e) {
			throw e;
		}
	}

	private Report_I_WorkInfoDetail getDetailWithId(String id) throws Exception {
		if (id == null || StringUtils.isEmpty( id )) {
			throw new Exception("id is null.");
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return emc.find( id, Report_I_WorkInfoDetail.class );
		} catch (Exception e) {
			throw e;
		}
	}
	
	/**
	 * 根据指定的ID列示 战略举措信息
	 * @param ids  战略举措信息ID列表
	 * @return
	 * @throws Exception
	 */
	public List<Report_I_WorkInfo> list(List<String> ids ) throws Exception {
		if (ids == null || ids.isEmpty()) {
			return null;
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return report_I_WorkInfoService.list( emc, ids );
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 保存部门重点工作信息
	 * @param entity
	 * @param detail
	 * @return
	 * @throws Exception
	 */
	public Report_I_WorkInfo save( Report_I_WorkInfo entity, Report_I_WorkInfoDetail detail ) throws Exception {
		if ( entity == null) {
			throw new Exception("Report_I_WorkInfo entity is null.");
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			entity = report_I_WorkInfoService.save( emc, entity, detail );
		} catch (Exception e) {
			throw e;
		}
		return entity;
	}

	public Report_I_WorkInfo save(ActionSaveWorkInfo.Wi wi) throws Exception {
		if ( wi == null) {
			throw new Exception("Report_I_WorkInfo wi is null.");
		}
		Report_I_WorkInfo entity = get( wi.getId() );
		Report_I_WorkInfoDetail detail = getDetailWithId( wi.getId() );

		if( entity == null ){
			entity = wi;
		}else{
			wi.copyTo( entity, JpaObject.FieldsUnmodify );
		}
		
//		if( !ThisApplication.WORKTYPE_DEPT.equals( entity.getWorkType() ) ){
//			throw new Exception("部门重点工作信息不允许进行修改操作！");
//		}
		
		if( detail == null ){
			detail = new Report_I_WorkInfoDetail();
			detail.setId( entity.getId() ); //工作信息ID，和详细信息ID一致
		}
		detail.setKeyWorkId( entity.getKeyWorkId() );
		detail.setReportId( entity.getReportId() );
		detail.setDescribe( wi.getDescribe() );
		detail.setWorkPlanSummary(wi.getWorkPlanSummary());
		detail.setWorkProgSummary(wi.getWorkProgSummary());
		
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			entity = report_I_WorkInfoService.save( emc, entity, detail );
		} catch (Exception e) {
			throw e;
		}
		return entity;
	}

	/**
	 * 根据ID删除 战略举措信息
	 * @param id  战略举措信息ID
	 * @throws Exception
	 */
	public void delete(String id ) throws Exception {
		if (id == null || StringUtils.isEmpty( id )) {
			throw new Exception("id is null.");
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			report_I_WorkInfoService.delete(emc, id);
		} catch (Exception e) {
			throw e;
		}
	}

    public List<String> listIdsWithReport(String reportId, String workMonthFlag) throws Exception {
		if (reportId == null || reportId.isEmpty()) {
			throw new Exception("reportId is null.");
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return report_I_WorkInfoService.listIdsWithReport(emc, reportId, workMonthFlag);
		} catch (Exception e) {
			throw e;
		}
    }
    
    public List<String> listIdsWithReports( List<String> reportIds, String workMonthFlag) throws Exception {
		if ( ListTools.isEmpty( reportIds )) {
			throw new Exception("reportIds is empty.");
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return report_I_WorkInfoService.listIdsWithReports(emc, reportIds, workMonthFlag);
		} catch (Exception e) {
			throw e;
		}
    }

	public String getDescribeValue(String id) throws Exception {
		if (id == null || StringUtils.isEmpty( id )) {
			throw new Exception("id is null.");
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Report_I_WorkInfoDetail detail = emc.find(id, Report_I_WorkInfoDetail.class );
			if( detail != null ) {
				return detail.getDescribe();
			}
		} catch (Exception e) {
			throw e;
		}
		return null;
	}

	public List<String> listWorkTagIdsWithUnitName(String unitName) throws Exception {
		if (unitName == null || unitName.isEmpty()) {
			throw new Exception("unitName is null.");
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return report_I_WorkInfoService.listWorkTagIdsWithUnitName(emc, unitName);
		} catch (Exception e) {
			throw e;
		}
	}

	public List<Report_I_WorkTag> listWorkTags(List<String> ids) throws Exception {
		if (ListTools.isEmpty(ids)) {
			throw new Exception("ids is null.");
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return report_I_WorkInfoService.listWorkTags(emc, ids);
		} catch (Exception e) {
			throw e;
		}
	}

	public Report_I_WorkInfo getWithKeyWorkId(String keyWorkId, String reportId) throws Exception {
		if ( StringUtils.isEmpty( keyWorkId )) {
			throw new Exception("keyWorkId is null.");
		}
		List<Report_I_WorkInfo> workList = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			workList = report_I_WorkInfoService.listWithKeyWorkId(emc, keyWorkId, reportId );
			if(ListTools.isNotEmpty(workList)) {
				return workList.get(0);
			}
		} catch (Exception e) {
			throw e;
		}
		return null;
	}

	public Report_I_WorkInfoDetail getDetailWithWorkInfoId(String reportId, String workInfoId) throws Exception {
		if (StringUtils.isEmpty(workInfoId)) {
			throw new Exception("workInfoId is null.");
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return report_I_WorkInfoService.getDetailWithWorkInfoId(emc, reportId, workInfoId);
		} catch (Exception e) {
			throw e;
		}
	}

	public List<String> listKeyWorkInfoIdsWithUnitAndMeasure(String unitName, String measureId) throws Exception {
		if (StringUtils.isEmpty(unitName)) {
			throw new Exception("unitName is null.");
		}
		if (StringUtils.isEmpty(measureId)) {
			throw new Exception("measureId is null.");
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return report_I_WorkInfoService.listKeyWorkInfoIdsWithUnitAndMeasure(emc, unitName, measureId);
		} catch (Exception e) {
			throw e;
		}
	}

	public Report_I_WorkInfo saveWorkProgSummary(String id, String workProgSummary) throws Exception {
		if ( id == null) {
			throw new Exception("Report_I_WorkInfo id is null.");
		}
		Report_I_WorkInfo entity = null;
		Report_I_WorkInfoDetail detail = null;		
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {			
			entity = emc.find( id, Report_I_WorkInfo.class );
			detail = emc.find( id, Report_I_WorkInfoDetail.class );
			if( entity == null ){
				throw new Exception("Report_I_WorkInfo not exists.id:" + id);
			}
			if( detail == null ){
				throw new Exception("Report_I_WorkInfo detail not exists.id:" + id);
			}
			emc.beginTransaction( Report_I_WorkInfoDetail.class );
			detail.setWorkProgSummary(workProgSummary);
			emc.check( detail , CheckPersistType.all );
			emc.commit();
		} catch (Exception e) {
			throw e;
		}
		return entity;
	}
	
	public Report_I_WorkInfo saveWorkPlanSummary(String id, String workPlanSummary) throws Exception {
		if ( id == null) {
			throw new Exception("Report_I_WorkInfo id is null.");
		}
		Report_I_WorkInfo entity = null;
		Report_I_WorkInfoDetail detail = null;		
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {			
			entity = emc.find( id, Report_I_WorkInfo.class );
			detail = emc.find( id, Report_I_WorkInfoDetail.class );
			if( entity == null ){
				throw new Exception("Report_I_WorkInfo not exists.id:" + id);
			}
			if( detail == null ){
				throw new Exception("Report_I_WorkInfo detail not exists.id:" + id);
			}
			emc.beginTransaction( Report_I_WorkInfoDetail.class );
			detail.setWorkPlanSummary(workPlanSummary);
			emc.check( detail , CheckPersistType.all );
			emc.commit();
		} catch (Exception e) {
			throw e;
		}
		return entity;
	}
}

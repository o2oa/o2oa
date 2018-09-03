package com.x.report.assemble.control.service;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.tools.ListTools;
import com.x.report.assemble.control.jaxrs.workplan.ActionSaveWorkPlan.Wi;
import com.x.report.core.entity.Report_C_WorkPlan;
import com.x.report.core.entity.Report_C_WorkPlanDetail;
import com.x.report.core.entity.Report_C_WorkProgDetail;

/**
 * 汇报工作计划信息服务类
 * 
 * @author O2LEE
 *
 */
public class Report_C_WorkPlanServiceAdv{

	private Report_C_WorkPlanService report_C_WorkPlanService = new Report_C_WorkPlanService();

	public List<Report_C_WorkPlan> listWithReportId(String reportId) throws Exception {
		if (reportId == null || reportId.isEmpty()) {
			throw new Exception("reportId is null.");
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return report_C_WorkPlanService.listWithReportId(emc, reportId );
		} catch (Exception e) {
			throw e;
		}
	}
	
	public List<String> listWithReportIds(List<String> reportIds ) throws Exception {
		if ( ListTools.isEmpty( reportIds )) {
			throw new Exception("reportId is empty.");
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return report_C_WorkPlanService.listWithReportIds(emc, reportIds );
		} catch (Exception e) {
			throw e;
		}
	}

	public Report_C_WorkPlanDetail getDetailWithPlanId(String planId) throws Exception {
		if (planId == null || planId.isEmpty()) {
			throw new Exception("planId is null.");
		}
		List<Report_C_WorkPlanDetail> detailList = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			detailList = report_C_WorkPlanService.listDetailWithPlanId(emc, planId );
			 if( detailList != null && !detailList.isEmpty() ) {
				 return detailList.get( 0 );
			 }
			 return null;
		} catch (Exception e) {
			throw e;
		}
	}

	public String getPlanContentWithPlanId(String planId) throws Exception {
		if (planId == null || planId.isEmpty()) {
			throw new Exception("planId is null.");
		}
		List<Report_C_WorkPlanDetail> detailList = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			detailList = report_C_WorkPlanService.listDetailWithPlanId(emc, planId );
			 if( detailList != null && !detailList.isEmpty() ) {
				 return detailList.get( 0 ).getPlanContent();
			 }
			 return null;
		} catch (Exception e) {
			throw e;
		}
	}	
	
	public Report_C_WorkPlan save( Wi wi ) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return report_C_WorkPlanService.save( emc, wi );	
		} catch ( Exception e ) {
			throw e;
		}
	}

	public Report_C_WorkPlan get(String id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return emc.find( id, Report_C_WorkPlan.class );
		} catch ( Exception e ) {
			throw e;
		}
	}

	public void delete(String id, EffectivePerson effectivePerson) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Report_C_WorkPlan plan =  emc.find( id, Report_C_WorkPlan.class );
			Report_C_WorkPlanDetail detail = getDetailWithPlanId( id );
			emc.beginTransaction( Report_C_WorkPlan.class );
			emc.beginTransaction( Report_C_WorkProgDetail.class );
			if( plan != null ) {
				emc.remove( plan, CheckRemoveType.all );
			}
			if( detail != null ) {
				detail = emc.find( detail.getId(), Report_C_WorkPlanDetail.class );
				emc.remove( detail, CheckRemoveType.all );
			}
			emc.commit();
		} catch ( Exception e ) {
			throw e;
		}
	}

	/**
	 * 根据汇报ID和工作信息ID获取工作在该汇报下所有的计划内容列表
	 * @param reportId
	 * @param workInfoId
	 * @return
	 * @throws Exception
	 */
	public List<String> listWithReportAndWorkInfoId( String reportId, String workInfoId ) throws Exception {
		if (reportId == null || reportId.isEmpty()) {
			throw new Exception("reportId is null.");
		}
		if (workInfoId == null || workInfoId.isEmpty()) {
			throw new Exception("workInfoId is null.");
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			 return report_C_WorkPlanService.listWithReportAndWorkInfoId( emc, reportId, workInfoId );
		} catch (Exception e) {
			throw e;
		}
	}

	public List<Report_C_WorkPlan> list(List<String> ids) throws Exception {
		if (ids == null || ids.isEmpty()) {
			return null;
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			 return report_C_WorkPlanService.list(emc, ids);
		} catch (Exception e) {
			throw e;
		}
	}

	public Boolean updateOrderNumber(String planId, Integer orderNumber) throws Exception {
		if (StringUtils.isEmpty( planId )) {
			throw new Exception("planId is null.");
		}
		if ( orderNumber == null ) {
			throw new Exception("orderNumber is null.");
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return report_C_WorkPlanService.updateOrderNumber(emc, planId, orderNumber );
		} catch (Exception e) {
			throw e;
		}
	}
}

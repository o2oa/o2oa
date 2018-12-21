package com.x.report.assemble.control.service;

import java.util.List;

import org.apache.openjpa.lib.util.StringUtil;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.tools.ListTools;
import com.x.report.assemble.control.jaxrs.workprog.ActionSaveWorkProg.Wi;
import com.x.report.core.entity.Report_C_WorkProg;
import com.x.report.core.entity.Report_C_WorkProgDetail;

/**
 * 工作实际完成情况汇报信息服务类
 * @author O2LEE
 *
 */
public class Report_C_WorkProgServiceAdv{

	private Report_C_WorkProgService report_C_WorkProgService = new Report_C_WorkProgService();

	public List<Report_C_WorkProg> listWithReportId( String reportId ) throws Exception {
		if (reportId == null || reportId.isEmpty()) {
			throw new Exception("reportId is null.");
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return report_C_WorkProgService.listWithReportId(emc, reportId );
		} catch (Exception e) {
			throw e;
		}
	}
	
	public List<String> listWithReportIds( List<String> reportIds ) throws Exception {
		if ( ListTools.isEmpty( reportIds )) {
			throw new Exception("reportIds is empty.");
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return report_C_WorkProgService.listWithReportIds(emc, reportIds );
		} catch (Exception e) {
			throw e;
		}
	}

	public List<Report_C_WorkProgDetail> listWorkProgDetailWithReportId( String reportId ) throws Exception {
		if (reportId == null || reportId.isEmpty()) {
			throw new Exception("reportId is null.");
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return report_C_WorkProgService.listDetailWithReportId( emc, reportId );
		} catch (Exception e) {
			throw e;
		}
	}

	public Report_C_WorkProgDetail getDetailWithProgId(String progId ) throws Exception {
		if (progId == null || progId.isEmpty()) {
			throw new Exception("progId is null.");
		}
		List<Report_C_WorkProgDetail> detailList = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			detailList = report_C_WorkProgService.listDetailWithProgId( emc, progId );
			if( detailList != null && !detailList.isEmpty() ) {
				return detailList.get( 0 );
			}
		} catch (Exception e) {
			throw e;
		}
		return null;
	}

	public String getProgressContentWithProgId(String progId) throws Exception {
		if (progId == null || progId.isEmpty()) {
			throw new Exception("progId is null.");
		}
		List<Report_C_WorkProgDetail> detailList = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			detailList = report_C_WorkProgService.listDetailWithProgId( emc, progId );
			if( detailList != null && !detailList.isEmpty() ) {
				return detailList.get( 0 ).getProgressContent();
			}
		} catch (Exception e) {
			throw e;
		}
		return null;
	}
	/**
	 * 根据个人需要汇报的所有工作，对所有的工作实际完成情况中的条目信息进行核对，并且进行补充
	 * @param report_P_Profile
	 * @param report_I_Base
	 * @param unitStrategyWorks
	 * @param workProgList
	 * @param workProgDetailList
	 * @throws Exception
	 */
//	public void checkWorkProgWithWorks1( Report_P_Profile report_P_Profile, Report_I_Base report_I_Base, List<WoCompanyStrategyWorks> unitStrategyWorks, List<Report_C_WorkProg> workProgList, List<Report_C_WorkProgDetail> workProgDetailList) throws Exception {
//		if ( report_P_Profile == null ) {
//			throw new Exception("report_P_Profile is null.");
//		}
//		if ( report_I_Base == null ) {
//			throw new Exception("report_I_Base is null.");
//		}
//		if ( unitStrategyWorks == null ) {
//			return;
//		}
//		Boolean exists = false;
//		Report_C_WorkProg report_C_WorkProg = null;
//		Report_C_WorkProgDetail Report_C_WorkProgDetail = null;
//		List<Report_C_WorkProg> tmp_workProgList = null ;
//		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
//			tmp_workProgList = report_C_WorkProgService.listWithReportId(emc,  report_I_Base.getId());
//		} catch (Exception e) {
//			throw e;
//		}
//		
//		if( tmp_workProgList == null || tmp_workProgList.isEmpty() ) {
//			tmp_workProgList = new ArrayList<>();
//		}
//		
//		for( WoCompanyStrategyWorks work : unitStrategyWorks ) {
//			exists = false;
//			for( Report_C_WorkProg workProg : tmp_workProgList ) {
//				if( work.getId().equalsIgnoreCase( workProg.getWorkId() )) {
//					exists = true;
//					break;
//				}
//			}
//			if( !exists ) {
//				//说明不存在，需要添加到工作执行情况里
//				report_C_WorkProg = new Report_C_WorkProg();
//                Report_C_WorkProgDetail = new Report_C_WorkProgDetail();
//				
//				report_C_WorkProg.setId( Report_C_WorkProg.createId() );
//				report_C_WorkProg.setReportId( report_I_Base.getId() );
//				report_C_WorkProg.setTargetPerson(targetPerson);
//				report_C_WorkProg.setTargetIdentity(targetIdentity);
//				
//				report_C_WorkProg.setWorkId( work.getId() );
//				report_C_WorkProg.setWorkTitle( work.getKeyworktitle() );
//				report_C_WorkProg.setTitle( work.getKeyworktitle() );
//				report_C_WorkProg.setYear( report_I_Base.getYear() );
//				report_C_WorkProg.setMonth( report_I_Base.getMonth() );
//				report_C_WorkProg.setWeek( null );
//				report_C_WorkProg.setDate( null );
//				report_C_WorkProg.setCompleted( false );
//				report_C_WorkProg.setFlag( report_P_Profile.getReportYear() + report_P_Profile.getReportMonth() ); //计划期数标识：年+月+周数+日期
//
//                Report_C_WorkProgDetail.setId( JpaObject.createId()  );
//                Report_C_WorkProgDetail.setProgId( report_C_WorkProg.getId() );
//                Report_C_WorkProgDetail.setReportId( report_I_Base.getId() );
//                Report_C_WorkProgDetail.setWorkId( work.getId() );
//                Report_C_WorkProgDetail.setWorkTitle( work.getKeyworktitle() );
//                Report_C_WorkProgDetail.setWorkContent( work.getKeyworkdescribe() );
//                Report_C_WorkProgDetail.setProgressContent( "" );
//				
//				workProgList.add( report_C_WorkProg );
//				workProgDetailList.add( Report_C_WorkProgDetail );
//			}
//		}
//	}

	public Report_C_WorkProg save(Wi wi) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return report_C_WorkProgService.save( emc, wi );	
		} catch ( Exception e ) {
			throw e;
		}
	}

	public Report_C_WorkProg get(String id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return emc.find(id, Report_C_WorkProg.class);
		} catch ( Exception e ) {
			throw e;
		}
	}

	public void delete(String id, EffectivePerson effectivePerson) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Report_C_WorkProg plan =  emc.find( id, Report_C_WorkProg.class );
			Report_C_WorkProgDetail detail = getDetailWithProgId( id );
			emc.beginTransaction( Report_C_WorkProg.class );
			emc.beginTransaction( Report_C_WorkProgDetail.class );
			if( plan != null ) {
				emc.remove( plan, CheckRemoveType.all );
			}
			if( detail != null ) {
				detail = emc.find( detail.getId(), Report_C_WorkProgDetail.class );
				emc.remove( detail, CheckRemoveType.all );
			}
			emc.commit();
		} catch ( Exception e ) {
			throw e;
		}
	}

	public List<String> listReportIdsWithKeyWorkId(String year, String month, String week, String reportDate,
			String reportType, List<String> keyWorkIds, String reportStatus) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return report_C_WorkProgService.listReportIdsWithKeyWorkId( emc, year, month, week, reportDate,
					reportType, keyWorkIds, reportStatus );	
		} catch ( Exception e ) {
			throw e;
		}
	}

	public List<Report_C_WorkProg> listWithKeyWorkIds(String reportId, List<String> listReportIdsWithKeyWorkId) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return report_C_WorkProgService.listWithKeyWorkIds( emc, reportId, listReportIdsWithKeyWorkId );	
		} catch ( Exception e ) {
			throw e;
		}
	}

	public List<String> listWithReportAndWorkInfoId(String reportId, String workInfoId ) throws Exception {
		if( StringUtil.isEmpty(reportId)) {
			throw new Exception("reportId is null!");
		}
		if( StringUtil.isEmpty(workInfoId) ) {
			throw new Exception("workInfoId is null!");
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return report_C_WorkProgService.listWithReportAndWorkInfoId( emc, reportId, workInfoId );
		} catch (Exception e) {
			throw e;
		}
	}
	
	public List<String> listWithReportAndWorkId(String reportId, String workId ) throws Exception {
		if( StringUtil.isEmpty(reportId)) {
			throw new Exception("reportId is null!");
		}
		if( StringUtil.isEmpty(workId) ) {
			throw new Exception("workId is null!");
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return report_C_WorkProgService.listWithReportAndWorkId( emc, reportId, workId );
		} catch (Exception e) {
			throw e;
		}
	}

	public List<Report_C_WorkProg> list(List<String> ids) throws Exception {
		if( ListTools.isEmpty( ids )) {
			return null;
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return report_C_WorkProgService.list( emc, ids );
		} catch (Exception e) {
			throw e;
		}
	}

	public Boolean updateOrderNumber(String id, Integer orderNumber) throws Exception {
		if( StringUtil.isEmpty(id)) {
			throw new Exception("id is null!");
		}
		if( orderNumber == null ) {
			throw new Exception("orderNumber is null!");
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return report_C_WorkProgService.updateOrderNumber( emc, id, orderNumber );
		} catch (Exception e) {
			throw e;
		}
	}

	public List<Report_C_WorkProg> listWithKeyWorkIdAndYear(String workId, String year) throws Exception {
		if( StringUtil.isEmpty(workId)) {
			throw new Exception("workId is null!");
		}
		if( StringUtil.isEmpty(year)) {
			throw new Exception("year is null!");
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return report_C_WorkProgService.listWithKeyWorkIdAndYear( emc, workId,  year);	
		} catch ( Exception e ) {
			throw e;
		}
	}

	public List<String> listWithYear(String year) throws Exception {
		if( StringUtil.isEmpty(year)) {
			throw new Exception("year is null!");
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return report_C_WorkProgService.listWithYear( emc,  year);	
		} catch ( Exception e ) {
			throw e;
		}
	}

	
}

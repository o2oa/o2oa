package com.x.report.assemble.control.service;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.report.core.entity.Report_P_Profile;
import com.x.report.core.entity.Report_P_ProfileDetail;

/**
 * 汇报生成概要记录文件信息服务类
 * @author O2LEE
 *
 */
public class Report_P_ProfileServiceAdv{

	private Report_P_ProfileService report_P_ProfileService = new Report_P_ProfileService();

	/**
	 * 根据指定的ID列示汇报生成概要文件记录信息
	 * @param id 汇报生成概要文件记录信息ID列表
	 * @return
	 * @throws Exception
	 */
	public Report_P_Profile get( String id ) throws Exception {
		if (id == null || id.isEmpty()) {
			throw new Exception("id is null.");
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return report_P_ProfileService.get(emc, id);
		} catch (Exception e) {
			throw e;
		}
	}
	
	/**
	 * 根据指定的ID列示汇报生成概要文件记录信息
	 * @param ids 汇报生成概要文件记录信息ID列表
	 * @return
	 * @throws Exception
	 */
	public List<Report_P_Profile> list(List<String> ids ) throws Exception {
		if (ids == null || ids.isEmpty()) {
			return null;
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return report_P_ProfileService.list( emc, ids );
		} catch (Exception e) {
			throw e;
		}
	}
	
	/**
	 * 保存汇报生成概要文件记录以及详细概要文件内容信息
	 * @param entity 汇报生成概要文件记录信息
	 * @param detailList 汇报生成概要文件记录详细信息列表
	 * @return
	 * @throws Exception
	 */
	public Report_P_Profile save( Report_P_Profile entity, List<Report_P_ProfileDetail> detailList ) throws Exception {
		if ( entity == null) {
			throw new Exception("Report_P_Profile entity is null.");
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			entity = report_P_ProfileService.save( emc, entity, detailList );			
		} catch (Exception e) {
			throw e;
		}
		return entity;
	}
	

	public Report_P_Profile updateDetails(String id, List<Report_P_ProfileDetail> recordProfileDetailList) throws Exception {
		if ( StringUtils.isEmpty( id )) {
			throw new Exception("id is null.");
		}
		if( ListTools.isEmpty( recordProfileDetailList )) {
			throw new Exception("recordProfileDetailList is null.");
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return  report_P_ProfileService.updateDetails( emc, id, recordProfileDetailList );			
		} catch (Exception e) {
			throw e;
		}
	}	
	
	public Report_P_Profile updateWithId(Report_P_Profile entity) throws Exception {
		if ( entity == null) {
			throw new Exception("Report_P_Profile entity is null.");
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			entity = report_P_ProfileService.updateWithId( emc, entity );			
		} catch (Exception e) {
			throw e;
		}
		return entity;
	}
	
	/**
	 * 根据ID删除汇报生成概要文件记录信息，汇报生成概要文件记录详细信息也会被删除
	 * @param id 汇报生成概要文件记录信息ID
	 * @throws Exception
	 */
	public void delete(String id ) throws Exception {
		if (id == null || id.isEmpty()) {
			throw new Exception("id is null.");
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			report_P_ProfileService.delete(emc, id);
		} catch (Exception e) {
			throw e;
		}
	}

	public List<Report_P_Profile> listErrorCreateRecord(String reportType, String reportYear, String reportMonth, String reportWeek) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return report_P_ProfileService.listErrorCreateRecord(emc, reportType, reportYear, reportMonth, reportWeek);
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 根据条件获取指定的汇报生成记录信息列表
	 * @param reportType 汇报类别
	 * @param year 汇报年份
	 * @param month 汇报月份
	 * @param week 汇报周数
	 * @param date 汇报日期
	 * @return
	 * @throws Exception
	 */
	public List<Report_P_Profile> listWithCondition(String reportType, String year, String month, String week, String date) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return report_P_ProfileService.listWithCondition(emc, reportType, year, month, week, date );
		} catch (Exception e) {
			throw e;
		}
	}

	public String getDetailValue( String reportProfileId, String reportModule, String snapType ) throws Exception {
		List<Report_P_ProfileDetail> detailList = null; 
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			detailList = report_P_ProfileService.listDetailValue( emc, reportProfileId, reportModule, snapType );
			if( detailList != null && !detailList.isEmpty() ) {
				return detailList.get(0).getSnapContent();
			}
		} catch (Exception e) {
			throw e;
		}
		return null;
	}

	public List<String> listIdsWithCondition( String reportType, String year, String month, String week, String reportDate) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return report_P_ProfileService.listIdsWithCondition(emc, reportType, year, month, week, reportDate );
		} catch (Exception e) {
			throw e;
		}
	}

	public List<String> listDetailValueListWithCondition(List<String> ids) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return report_P_ProfileService.listDetailValueListWithCondition(emc, ids, "STRATEGY_WORK" );
		} catch (Exception e) {
			throw e;
		}
	}

	public List<String> listWithYear(String year) throws Exception {
		if (year == null || year.isEmpty()) {
			throw new Exception("year is null.");
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return report_P_ProfileService.listWithYear(emc, year);
		} catch (Exception e) {
			throw e;
		}
	}

	public Date getMaxCreateTime(String enumReportType) throws Exception {
		if (enumReportType == null || enumReportType.isEmpty()) {
			throw new Exception("enumReportType is null.");
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return report_P_ProfileService.getMaxCreateTime(emc, enumReportType );
		} catch (Exception e) {
			throw e;
		}
	}

}

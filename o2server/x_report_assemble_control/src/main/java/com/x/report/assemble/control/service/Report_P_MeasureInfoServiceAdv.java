package com.x.report.assemble.control.service;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.report.assemble.control.dataadapter.strategy.CompanyStrategyMeasure;
import com.x.report.core.entity.Report_P_MeasureInfo;

/**
 *  战略举措信息信息服务类
 * @author O2LEE
 *
 */
public class Report_P_MeasureInfoServiceAdv {

	private Report_P_MeasureInfoService report_P_MeasureInfoService = new Report_P_MeasureInfoService();

	/**
	 * 根据指定的ID列示 战略举措信息信息
	 * @param id  战略举措信息信息ID列表
	 * @return
	 * @throws Exception
	 */
	public Report_P_MeasureInfo get( String id ) throws Exception {
		if (id == null || id.isEmpty()) {
			throw new Exception("id is null.");
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return report_P_MeasureInfoService.get(emc, id);
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
	public List<Report_P_MeasureInfo> list(List<String> ids ) throws Exception {
		if (ids == null || ids.isEmpty()) {
			return null;
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return report_P_MeasureInfoService.list( emc, ids );
		} catch (Exception e) {
			throw e;
		}
	}
	
	public List<String> listWithYear(String thisYear) throws Exception {
		if ( StringUtils.isEmpty( thisYear )) {
			throw new Exception("year is null");
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return report_P_MeasureInfoService.listWithYear( emc, thisYear );
		} catch (Exception e) {
			throw e;
		}
	}
	
	/**
	 * 保存 战略举措信息
	 * @param entity  战略举措信息
	 * @return
	 * @throws Exception
	 */
	public Report_P_MeasureInfo save( Report_P_MeasureInfo entity ) throws Exception {
		if ( entity == null) {
			throw new Exception("Report_P_Profile entity is null.");
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			entity = report_P_MeasureInfoService.save( emc, entity );
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
		if (id == null || id.isEmpty()) {
			throw new Exception("id is null.");
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            report_P_MeasureInfoService.delete(emc, id);
		} catch (Exception e) {
			throw e;
		}
	}

    public void saveOrUpdateMeasureInfo( List<CompanyStrategyMeasure.WoCompanyStrategy> companyStrategyMeasure ) throws Exception {
        Report_P_MeasureInfo measure = null;
        List<CompanyStrategyMeasure.WoMeasuresInfo> measureList = null;
        if(ListTools.isNotEmpty( companyStrategyMeasure )){
            for( CompanyStrategyMeasure.WoCompanyStrategy strategy : companyStrategyMeasure ){
                measureList = strategy.getMeasureList();
                if( ListTools.isNotEmpty( measureList ) ){
                    for( CompanyStrategyMeasure.WoMeasuresInfo woMeasureInfo : measureList ){
                        measure = new Report_P_MeasureInfo();
                        measure.setId( woMeasureInfo.getId() );
                        measure.setParentId( woMeasureInfo.getMeasuresinfoparentid() );
                        measure.setStatus( woMeasureInfo.getStatus() );
                        measure.setTitle( woMeasureInfo.getMeasuresinfotitle() );
                        measure.setUnitList( woMeasureInfo.getDeptlist() );
                        measure.setYear( woMeasureInfo.getMeasuresinfoyear() );
                        if( StringUtils.isEmpty( measure.getStatus() )) {
                        	measure.setStatus( "正常" );
                        }
                        save(measure);
                    }
                }
            }
        }
    }
}

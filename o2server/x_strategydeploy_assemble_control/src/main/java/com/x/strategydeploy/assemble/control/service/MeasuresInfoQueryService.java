package com.x.strategydeploy.assemble.control.service;

import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.strategydeploy.assemble.control.Business;
import com.x.strategydeploy.core.entity.MeasuresInfo;

public class MeasuresInfoQueryService {
	private static  Logger logger = LoggerFactory.getLogger(MeasuresInfoQueryService.class);

	public MeasuresInfo get(String id) throws Exception {
		if (id == null || id.isEmpty()) {
			throw new Exception("id is null, return null!");
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return emc.find(id, MeasuresInfo.class);
		} catch (Exception e) {
			throw e;
		}
	}

	public List<MeasuresInfo> getListByYearAndStrategyDeployId(String year, String StrategyDeployId) throws Exception {
		if (year == null || year.isEmpty()) {
			throw new Exception("_year is null, return null!");
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			return business.measuresInfoFactory().getListByYearAndParentId(year, StrategyDeployId);
		} catch (Exception e) {
			throw e;
		}
	}

	public List<MeasuresInfo> getListStrategyDeployId(String StrategyDeployId) throws Exception {
		if (StrategyDeployId == null || StrategyDeployId.isEmpty()) {
			throw new Exception("StrategyDeployId is null, return null!");
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			return business.measuresInfoFactory().getListByParentId(StrategyDeployId);
		} catch (Exception e) {
			throw e;
		}
	}

	public List<MeasuresInfo> getListByYear(String year) throws Exception {
		if (year == null || year.isEmpty()) {
			throw new Exception("_year is null, return null!");
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			return business.measuresInfoFactory().getListByYear(year);
		} catch (Exception e) {
			throw e;
		}
	}

	public List<MeasuresInfo> getListByYearAndDept(String _year, String _dept) throws Exception {
		if (_year == null || _year.isEmpty()) {
			throw new Exception("_year is null, return null!");
		}

		if (_dept == null || _dept.isEmpty()) {
			throw new Exception("_dept is null, return null!");
		}

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			return business.measuresInfoFactory().getListByYearAndDept(_year, _dept);
		} catch (Exception e) {
			throw e;
		}
	}
}

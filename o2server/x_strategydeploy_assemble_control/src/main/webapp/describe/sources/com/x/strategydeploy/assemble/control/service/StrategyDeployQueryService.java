package com.x.strategydeploy.assemble.control.service;

import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.strategydeploy.assemble.control.Business;
import com.x.strategydeploy.core.entity.StrategyDeploy;

public class StrategyDeployQueryService {
	private static  Logger logger = LoggerFactory.getLogger(StrategyDeployQueryService.class);

	public StrategyDeploy get(String id) throws Exception {
		if (id == null || id.isEmpty()) {
			throw new Exception("id is null, return null!");
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return emc.find(id, StrategyDeploy.class);
		} catch (Exception e) {
			throw e;
		}
	}

	public List<StrategyDeploy> getListByYear(String _year) throws Exception {
		if (_year == null || _year.isEmpty()) {
			throw new Exception("_year is null, return null!");
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			return business.strategyDeployFactory().getListByYear(_year);
		} catch (Exception e) {
			throw e;
		}
	}
	
	public List<StrategyDeploy> getListByYearAndDept(String _year,String _dept) throws Exception {
		if (_year == null || _year.isEmpty()) {
			throw new Exception("_year is null, return null!");
		}
		
		if (_dept == null || _dept.isEmpty()) {
			throw new Exception("_dept is null, return null!");
		}
		
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			return business.strategyDeployFactory().getListByYearAndDept(_year, _dept);
		} catch (Exception e) {
			throw e;
		}
	}
}

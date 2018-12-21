package com.x.strategydeploy.assemble.control.service;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.strategydeploy.assemble.control.Business;
import com.x.strategydeploy.core.entity.KeyworkInfo;
import com.x.strategydeploy.core.entity.MeasuresInfo;

public class KeyworkInfoQueryService {
	private static  Logger logger = LoggerFactory.getLogger(KeyworkInfoQueryService.class);

	public KeyworkInfo get(String id) throws Exception {
		if (id == null || id.isEmpty()) {
			throw new Exception("id is null, return null!");
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return emc.find(id, KeyworkInfo.class);
		} catch (Exception e) {
			throw e;
		}
	}

	public List<String> getMeasuresTitleListByIds(List<String> ids) throws Exception {
		Business business = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			return business.measuresInfoFactory().getTitleListByIds(ids);
		} catch (Exception e) {
			List<String> _tmp = new ArrayList<String>();
			//throw e;
			return _tmp;
		}
	}

	public List<MeasuresInfo> getMeasuresObjectsListByIds(List<String> ids) throws Exception {
		Business business = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			return business.measuresInfoFactory().getListByIds(ids);
		}

	}

	public List<KeyworkInfo> getListByYear(String year) throws Exception {
		if (year == null || year.isEmpty()) {
			throw new Exception("_year is null, return null!");
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			return business.keyworkInfoFactory().getListByYear(year);
		} catch (Exception e) {
			throw e;
		}
	}

	public List<KeyworkInfo> getListByYearAndDept(String _year, String _dept) throws Exception {
		if (_year == null || _year.isEmpty()) {
			throw new Exception("_year is null, return null!");
		}

		if (_dept == null || _dept.isEmpty()) {
			throw new Exception("_dept is null, return null!");
		}

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			return business.keyworkInfoFactory().getListByYearAndDept(_year, _dept);
		} catch (Exception e) {
			throw e;
		}
	}
}

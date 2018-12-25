package com.x.okr.assemble.control.service;

import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.okr.assemble.control.Business;
import com.x.okr.entity.OkrWorkAppraiseInfo;

public class OkrWorkAppraiseQueryService {
	/**
	 * 根据指定的ID从数据库查询OkrWorkAppraiseInfo对象
	 * 
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public OkrWorkAppraiseInfo get(String id) throws Exception {
		if (id == null || id.isEmpty()) {
			throw new Exception("id is null, return null!");
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return emc.find(id, OkrWorkAppraiseInfo.class);
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 根据指定的ID列表查询具体工作考核信息列表
	 * 
	 * @param ids
	 * @return
	 * @throws Exception
	 */
	public List<OkrWorkAppraiseInfo> listByIds(List<String> ids) throws Exception {
		if (ids == null || ids.size() == 0) {
			return null;
		}
		Business business = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			return business.okrWorkAppraiseInfoFactory().list(ids);
		} catch (Exception e) {
			throw e;
		}
	}
}
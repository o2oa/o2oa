package com.x.attendance.assemble.control.service;

import java.util.List;
import java.util.Optional;

import com.x.attendance.assemble.control.Business;
import com.x.attendance.entity.AttendanceSelfHoliday;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.entity.tools.JpaObjectTools;
import com.x.base.core.project.cache.Cache.CacheCategory;
import com.x.base.core.project.cache.Cache.CacheKey;
import com.x.base.core.project.tools.ListTools;
import com.x.base.core.project.cache.CacheManager;

public class AttendanceSelfHolidayService {

	private CacheCategory cache_AttendanceSelfHoliday = new CacheCategory(AttendanceSelfHoliday.class);

	public AttendanceSelfHoliday get(EntityManagerContainer emc, String id) throws Exception {
		if (id == null || id.isEmpty()) {
			return null;
		}
		return emc.find(id, AttendanceSelfHoliday.class);
	}

	public List<AttendanceSelfHoliday> list(EntityManagerContainer emc, List<String> ids) throws Exception {
		if (ids == null || ids.isEmpty()) {
			return null;
		}
		Business business = new Business(emc);
		return business.getAttendanceSelfHolidayFactory().list(ids);
	}

	public List<String> listAll(EntityManagerContainer emc) throws Exception {
		Business business = new Business(emc);
		return business.getAttendanceSelfHolidayFactory().listAll();
	}

	public List<AttendanceSelfHoliday> listWithPersonFromCache(EntityManagerContainer emc, String person,
			boolean debugger) throws Exception {
		// String cacheKey = ApplicationCache.concreteCacheKey( "list#" + person );
		// Element element = cache_AttendanceSelfHoliday.get(cacheKey);
		CacheKey cacheKey = new CacheKey(this.getClass(), "list", person);
		Optional<?> optional = CacheManager.get(cache_AttendanceSelfHoliday, cacheKey);

		if (optional.isPresent()) {
			return ((List<AttendanceSelfHoliday>) optional.get());
		} else {
			List<String> ids = getByPersonName(emc, person);
			List<AttendanceSelfHoliday> list = list(emc, ids);
			// detach
			if (ListTools.isNotEmpty(list)) {
				JpaObjectTools.detach(emc.get(AttendanceSelfHoliday.class), list);
				CacheManager.put(cache_AttendanceSelfHoliday, cacheKey, list);
			}
			return list;
		}
	}

	public List<String> getByPersonName(EntityManagerContainer emc, String personName) throws Exception {
		Business business = new Business(emc);
		return business.getAttendanceSelfHolidayFactory().getByPersonName(personName);
	}

	public List<AttendanceSelfHoliday> listWithBatchFlag(EntityManagerContainer emc, String batchFlag)
			throws Exception {
		Business business = new Business(emc);
		return business.getAttendanceSelfHolidayFactory().listWithBatchFlag(batchFlag);
	}

}

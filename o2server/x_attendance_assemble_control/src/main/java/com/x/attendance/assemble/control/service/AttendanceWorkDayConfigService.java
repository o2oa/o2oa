package com.x.attendance.assemble.control.service;

import java.util.List;
import java.util.Optional;

import com.x.attendance.assemble.control.Business;
import com.x.attendance.entity.AttendanceWorkDayConfig;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.cache.Cache.CacheCategory;
import com.x.base.core.project.cache.Cache.CacheKey;
import com.x.base.core.project.cache.CacheManager;



public class AttendanceWorkDayConfigService {

	private CacheCategory cache_AttendanceWorkDayConfig = new CacheCategory( AttendanceWorkDayConfig.class);
	/**
	 * 从缓存中获取所有的工作日配置
	 * @return
	 * @throws Exception
	 */
	public List<AttendanceWorkDayConfig> getAllWorkDayConfigWithCache(Boolean debugger) throws Exception {
		CacheKey cacheKey = new CacheKey("list#all");
		Optional<?> optional = CacheManager.get(cache_AttendanceWorkDayConfig, cacheKey);
		List<AttendanceWorkDayConfig> workDayConfigList = null;

		if (optional.isPresent()) {
			return ((List<AttendanceWorkDayConfig>) optional.get());
		}else{
			return listAll();
		}
	}

	public AttendanceWorkDayConfig get( EntityManagerContainer emc, String id ) throws Exception {
		if( id == null || id.isEmpty() ){
			return null;
		}
		return emc.find(id, AttendanceWorkDayConfig.class);
	}

	public List<AttendanceWorkDayConfig> list(EntityManagerContainer emc, List<String> ids) throws Exception {
		if( ids == null || ids.isEmpty() ){
			return null;
		}
		Business business =  new Business( emc );
		return business.getAttendanceWorkDayConfigFactory().list( ids );
	}

	public List<AttendanceWorkDayConfig> listAll(EntityManagerContainer emc) throws Exception {
		Business business =  new Business( emc );
		return business.getAttendanceWorkDayConfigFactory().listAll();
	}
	
	public List<AttendanceWorkDayConfig> listAll() throws Exception {
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			return listAll(emc);
		} catch ( Exception e ) {
			throw e;
		}	
	}
}

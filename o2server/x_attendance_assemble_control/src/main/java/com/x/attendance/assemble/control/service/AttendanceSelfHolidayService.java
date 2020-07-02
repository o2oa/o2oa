package com.x.attendance.assemble.control.service;

import com.x.attendance.assemble.control.Business;
import com.x.attendance.entity.AttendanceSelfHoliday;
import com.x.attendance.entity.AttendanceStatisticalCycle;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.project.cache.ApplicationCache;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;

import java.util.List;
import java.util.Map;


public class AttendanceSelfHolidayService {

	private Ehcache cache_AttendanceSelfHoliday = ApplicationCache.instance().getCache( AttendanceSelfHoliday.class);

	public AttendanceSelfHoliday get( EntityManagerContainer emc, String id ) throws Exception {
		if( id == null || id.isEmpty() ){
			return null;
		}
		return emc.find(id, AttendanceSelfHoliday.class);
	}

	public List<AttendanceSelfHoliday> list(EntityManagerContainer emc, List<String> ids) throws Exception {
		if( ids == null || ids.isEmpty() ){
			return null;
		}
		Business business =  new Business( emc );
		return business.getAttendanceSelfHolidayFactory().list( ids );
	}

	public List<String> listAll(EntityManagerContainer emc) throws Exception {
		Business business =  new Business( emc );
		return business.getAttendanceSelfHolidayFactory().listAll();
	}

	public List<AttendanceSelfHoliday> listWithPersonFromCache( EntityManagerContainer emc, String person, boolean debugger) throws Exception {
		String cacheKey = ApplicationCache.concreteCacheKey( "list#" + person );
		Element element = cache_AttendanceSelfHoliday.get(cacheKey);

		if ((null != element) && (null != element.getObjectValue())) {
			return (List<AttendanceSelfHoliday>) element.getObjectValue();
		}else{
			List<String> ids = getByPersonName( emc, person );
			List<AttendanceSelfHoliday> list = list( emc, ids );
			cache_AttendanceSelfHoliday.put(new Element( cacheKey, list ));
			return list;
		}
	}

	public List<String> getByPersonName(EntityManagerContainer emc, String personName) throws Exception {
		Business business =  new Business( emc );
		return business.getAttendanceSelfHolidayFactory().getByPersonName( personName );
	}

	public List<AttendanceSelfHoliday> listWithBatchFlag(EntityManagerContainer emc, String batchFlag) throws Exception {
		Business business = new Business(emc);
		return business.getAttendanceSelfHolidayFactory().listWithBatchFlag( batchFlag );
	}


}

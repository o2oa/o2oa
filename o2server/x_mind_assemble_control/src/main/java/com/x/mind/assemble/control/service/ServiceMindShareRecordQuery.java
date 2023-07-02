package com.x.mind.assemble.control.service;

import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.mind.assemble.control.Business;
import com.x.mind.entity.MindShareRecord;

/**
 * 脑图分享信息查询操作服务类：查询
 * @author O2LEE
 *
 */
class ServiceMindShareRecordQuery{

	/**
	 * 根据指定的ID列表获取脑图基本信息列表
	 * @param emc
	 * @param ids
	 * @return
	 * @throws Exception
	 */
	List<MindShareRecord> listWithIds(EntityManagerContainer emc, List<String> ids) throws Exception {
		Business business = new Business( emc );
		return business.mindShareRecordFactory().list(ids);
	}
	/**
	 * 根据脑图ID，获取脑图分享记录Id列表
	 * @param mindId
	 * @throws Exception 
	 */
	public List<String> listIdsWithMindId(EntityManagerContainer emc, String mindId) throws Exception {
		Business business = new Business( emc );
		return business.mindShareRecordFactory().listIdsWithMindId(mindId);
	}
	
	public List<String> listSharedRecordIds(EntityManagerContainer emc, String source, List<String> targetList, List<String> inMindIds ) throws Exception {
		Business business = new Business( emc );
		return business.mindShareRecordFactory().listSharedRecordIds(source, targetList, inMindIds);
	}
	public List<MindShareRecord> listSharedRecords(EntityManagerContainer emc, List<String> ids) throws Exception {
		Business business = new Business( emc );
		return business.mindShareRecordFactory().list(ids);
	}
	public List<String> listSharedMindIdsFromRecord(EntityManagerContainer emc, String source, List<String> targetList,
			List<String> inMindIds) throws Exception {
		Business business = new Business( emc );
		return business.mindShareRecordFactory().listSharedMindIdsFromRecord(source, targetList, inMindIds);
	}

}

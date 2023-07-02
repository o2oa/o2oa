package com.x.mind.assemble.control.service;

import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.mind.assemble.control.Business;
import com.x.mind.entity.MindVersionContent;
import com.x.mind.entity.MindVersionInfo;

/**
 * 脑图历史版本信息查询操作服务类：查询
 * @author O2LEE
 *
 */
class ServiceMindVersionInfoQuery{

	/**
	 * 根据指定的ID列表获取脑图基本信息列表
	 * @param emc
	 * @param ids
	 * @return
	 * @throws Exception
	 */
	List<MindVersionInfo> listWithIds(EntityManagerContainer emc, List<String> ids) throws Exception {
		Business business = new Business( emc );
		return business.mindVersionInfoFactory().listVersionsWithIds(ids);
	}
	
	/**
	 * 根据ID获取指定的脑图历史版本信息对象
	 * @param emc
	 * @param id
	 * @return
	 * @throws Exception
	 */
	MindVersionInfo getMindVersionInfo(EntityManagerContainer emc, String id) throws Exception {
		return emc.find( id,   MindVersionInfo.class );
	}
	
	/**
	 * 根据ID获取指定的脑图历史版本详细内容信息对象
	 * @param emc
	 * @param id
	 * @return
	 * @throws Exception
	 */
	MindVersionContent getMindVersionContentInfo(EntityManagerContainer emc, String id) throws Exception {
		return emc.find( id,   MindVersionContent.class );
	}
	
	/**
	 * 根据ID获取指定脑图历史版本的详细内容
	 * @param emc
	 * @param id
	 * @return
	 * @throws Exception
	 */
	String getMindVersionContent(EntityManagerContainer emc, String id) throws Exception {
		MindVersionContent mindVersionContent = emc.find( id,   MindVersionContent.class );
		if( mindVersionContent != null ) {
			return mindVersionContent.getContent();
		}
		return null;
	}

	/**
	 * 根据脑图ID获取脑较长的历史版本个数
	 * @param emc
	 * @param mindId
	 * @return
	 * @throws Exception
	 */
	public Long countMindVersionWithMindId(EntityManagerContainer emc, String mindId) throws Exception {
		Business business = new Business( emc );
		return business.mindVersionInfoFactory().countMindVersionWithMindId(mindId);
	}

	
	/**
	 * 根据脑图ID获取该脑图所有的版本
	 * @param emc
	 * @param id
	 * @return
	 * @throws Exception 
	 */
	public List<String> listVersionsWithMindId(EntityManagerContainer emc, String mindId) throws Exception {
		Business business = new Business( emc );
		return business.mindVersionInfoFactory().listVersionsWithMindId(mindId);
	}

	public MindVersionInfo getLatestVersionWithMind(EntityManagerContainer emc, String mindId ) throws Exception {
		Business business = new Business( emc );
		return business.mindVersionInfoFactory().getLatestVersionWithMind( mindId );
	}
	
	public MindVersionInfo getEarliestVersionInfoId(EntityManagerContainer emc, String mindId ) throws Exception {
		Business business = new Business( emc );
		return business.mindVersionInfoFactory().getEarliestVersionInfoId( mindId );
	}

	public MindVersionInfo deleteEarliestVersionInfoId(EntityManagerContainer emc, String mindId) {
		// TODO Auto-generated method stub
		return null;
	}
}

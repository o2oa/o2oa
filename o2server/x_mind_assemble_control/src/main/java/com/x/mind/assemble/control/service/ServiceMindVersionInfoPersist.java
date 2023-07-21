package com.x.mind.assemble.control.service;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.tools.ListTools;
import com.x.mind.assemble.control.Business;
import com.x.mind.entity.MindBaseInfo;
import com.x.mind.entity.MindContentInfo;
import com.x.mind.entity.MindRecycleInfo;
import com.x.mind.entity.MindShareRecord;
import com.x.mind.entity.MindVersionContent;
import com.x.mind.entity.MindVersionInfo;

/**
 * 脑图信息持久化操作服务类：保存、更新、删除
 * @author O2LEE
 *
 */
class ServiceMindVersionInfoPersist{
	
	/**
	 * 保存一个新版本的脑图信息
	 * @param emc
	 * @param _mindBaseInfo
	 * @param content
	 * @return
	 * @throws Exception
	 */
	MindVersionInfo createFromMind(EntityManagerContainer emc,  MindBaseInfo _mindBaseInfo, String content, int maxVersionCount ) throws Exception {
		if( StringUtils.isEmpty(_mindBaseInfo.getId()) ){
			throw new Exception("脑图的ID为空，无法保存新的版本信息！");
		}
		MindVersionInfo mindVersionInfo = null;
		MindVersionContent mindVersionContent = null;
		Business business = new Business(emc);
		
		//先创建一个新的历史版本信息
		mindVersionInfo = new MindVersionInfo();
		mindVersionInfo.setId(MindVersionInfo.createId());
		mindVersionInfo.setMindId( _mindBaseInfo.getId() );
		mindVersionInfo.setName( _mindBaseInfo.getName() );
		mindVersionInfo.setCreator( _mindBaseInfo.getCreator() );
		mindVersionInfo.setCreatorUnit( _mindBaseInfo.getCreatorUnit() );
		mindVersionInfo.setDescription( _mindBaseInfo.getDescription() );
		mindVersionInfo.setFileVersion( _mindBaseInfo.getFileVersion() );
		mindVersionInfo.setFolderId( _mindBaseInfo.getFolderId() );
		mindVersionInfo.setShared( _mindBaseInfo.getShared() );
		
		mindVersionContent = new MindVersionContent();
		mindVersionContent.setContent(content);
		mindVersionContent.setId(mindVersionInfo.getId());
		
		//保存版本信息
		emc.beginTransaction( MindBaseInfo.class );
		emc.beginTransaction( MindVersionInfo.class );
		emc.beginTransaction( MindVersionContent.class );
		
		emc.persist( mindVersionInfo, CheckPersistType.all);
		emc.persist( mindVersionContent, CheckPersistType.all);
		
		//版本号+1
		_mindBaseInfo = emc.find( _mindBaseInfo.getId() , MindBaseInfo.class );
		_mindBaseInfo.setFileVersion( _mindBaseInfo.getFileVersion() + 1 );
		emc.check( _mindBaseInfo, CheckPersistType.all);
		
		//提交后再判断是否需要删除一个最老的
		emc.commit();
		
		//查询一下当前脑图已经有多少个版本了
		Long  count = business.mindVersionInfoFactory().countMindVersionWithMindId( _mindBaseInfo.getId() );
		if( count > maxVersionCount ) {
			System.out.println(">>>>>>>>>>版本数量超过"+ maxVersionCount +"个了，删除最早的一个版本。");
			deleteEarliestVersion(emc, _mindBaseInfo.getId());
		}
		return mindVersionInfo;
	}

	/**
	 * 根据脑图ID, 删除一个最老（旧）的版本
	 * @param id
	 * @throws Exception 
	 */
	private void deleteEarliestVersion(EntityManagerContainer emc,  String mindId) throws Exception {
		Business business = new Business(emc);
		MindVersionInfo mindVersionInfo = business.mindVersionInfoFactory().getEarliestVersionInfoId(mindId);
		if( mindVersionInfo != null ) {
			MindVersionContent mindVersionContent = emc.find( mindVersionInfo.getCreator(),  MindVersionContent.class );
			emc.beginTransaction( MindVersionInfo.class );
			emc.beginTransaction( MindVersionContent.class );
			if( mindVersionInfo != null ) {
				emc.remove( mindVersionInfo, CheckRemoveType.all );
			}
			if( mindVersionContent != null ) {
				emc.remove( mindVersionContent, CheckRemoveType.all );
			}
			emc.commit();
		}
	}

	/**
	 * 根据脑图ID删除脑图信息
	 * 基础信息、详细内容以及分享的信息
	 * @param mindId
	 * @throws Exception 
	 */
	public Boolean delete(EntityManagerContainer emc, String mindId) throws Exception {
		Business business = new Business(emc);
		
		//先根据ID删除一个脑图
		MindBaseInfo mindBaseInfo = emc.find( mindId, MindBaseInfo.class);
		MindContentInfo mindContentInfo = emc.find( mindId, MindContentInfo.class);
		List<MindShareRecord>mindShareInfos = null;
		MindRecycleInfo mindRecycleInfo = null;
		List<String> ids = null;
		
		ids = business.mindShareRecordFactory().listIdsWithMindId(mindId);
		if(ListTools.isNotEmpty( ids )) {
			mindShareInfos = business.mindShareRecordFactory().list(ids);
		}
		
		mindRecycleInfo = business.mindRecycleInfoFactory().get(mindId);
		
		emc.beginTransaction( MindRecycleInfo.class );
		emc.beginTransaction( MindBaseInfo.class );
		emc.beginTransaction( MindShareRecord.class );
		emc.beginTransaction( MindVersionInfo.class );
		
		//删除分享表里的脑图信息
		if(ListTools.isNotEmpty( mindShareInfos )) {
			for( MindShareRecord mindShareInfo : mindShareInfos) {
				emc.remove( mindShareInfo, CheckRemoveType.all );
			}
		}
		//删除回收站里的脑图信息
		if( mindRecycleInfo != null ) {
			emc.remove( mindRecycleInfo, CheckRemoveType.all );
		}
		//删除脑图基础信息表信息
		if( mindBaseInfo != null ) {
			emc.remove( mindBaseInfo, CheckRemoveType.all );
		}
		//删除详细信息表信息
		if( mindContentInfo != null ) {
			emc.remove( mindBaseInfo, CheckRemoveType.all );
		}
		
		emc.commit();
		return true;
	}
}

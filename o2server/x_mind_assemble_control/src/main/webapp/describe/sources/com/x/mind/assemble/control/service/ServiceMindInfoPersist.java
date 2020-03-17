package com.x.mind.assemble.control.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.tools.ListTools;
import com.x.mind.assemble.control.Business;
import com.x.mind.entity.MindBaseInfo;
import com.x.mind.entity.MindContentInfo;
import com.x.mind.entity.MindFolderInfo;
import com.x.mind.entity.MindRecycleInfo;
import com.x.mind.entity.MindShareRecord;
import com.x.mind.entity.MindVersionInfo;

/**
 * 脑图信息持久化操作服务类：保存、更新、删除
 * @author O2LEE
 *
 */
class ServiceMindInfoPersist{
	
	/**
	 * 保存脑图信息
	 * @param emc
	 * @param _mindBaseInfo
	 * @param content
	 * @return
	 * @throws Exception
	 */
	MindBaseInfo save(EntityManagerContainer emc, MindBaseInfo _mindBaseInfo, String content) throws Exception {
		if( StringUtils.isEmpty(_mindBaseInfo.getId()) ){
			_mindBaseInfo.setId( MindBaseInfo.createId() );
		}
		if( StringUtils.isEmpty(_mindBaseInfo.getFolderId()) ) {
			_mindBaseInfo.setFolderId( "root" );
		}
		MindBaseInfo oldMindBase = null;
		MindContentInfo oldContent = null;
		oldMindBase = emc.find( _mindBaseInfo.getId(), MindBaseInfo.class );
		oldContent = emc.find( _mindBaseInfo.getId(), MindContentInfo.class );
		emc.beginTransaction( MindBaseInfo.class );
		emc.beginTransaction( MindContentInfo.class );
		//保存脑图基本信息
		if( oldMindBase == null ){
			oldMindBase = _mindBaseInfo;
			oldMindBase.composeSequnces();
			emc.persist( oldMindBase, CheckPersistType.all );
		}else{
			_mindBaseInfo.copyTo( oldMindBase, JpaObject.FieldsUnmodify  );
			_mindBaseInfo.setId( oldMindBase.getId() );
			_mindBaseInfo.setCreator_sequence( oldMindBase.getCreator_sequence() );
			_mindBaseInfo.setFolder_sequence( oldMindBase.getFolder_sequence() );
			_mindBaseInfo.setCreatorUnit_sequence( oldMindBase.getCreatorUnit_sequence() );
			_mindBaseInfo.setShared_sequence( oldMindBase.getShared_sequence() );
			_mindBaseInfo.composeSequnces();
			emc.check( _mindBaseInfo, CheckPersistType.all );	
		}
		//保存脑图内容
		if( oldContent == null ){
			oldContent =  new MindContentInfo();
			oldContent.setContent(content);
			oldContent.setId(oldMindBase.getId());
			emc.persist( oldContent, CheckPersistType.all);
		}else{
			oldContent.setContent(content);
			emc.check( oldContent, CheckPersistType.all );	
		}
		
		emc.commit();
		return oldMindBase;
	}

	/**
	 * 根据脑图ID删除脑图信息
	 * 基础信息、详细内容以及分享的信息
	 * @param mindId
	 * @throws Exception 
	 */
	public Boolean destroyMind(EntityManagerContainer emc, String mindId) throws Exception {
		Business business = new Business(emc);
		
		//先根据ID删除一个脑图
		MindBaseInfo mindBaseInfo = emc.find( mindId, MindBaseInfo.class);
		MindContentInfo mindContentInfo = emc.find( mindId, MindContentInfo.class);
		List<MindShareRecord>mindShareRecords = null;
		MindRecycleInfo mindRecycleInfo = null;
		List<String> ids = null;
		
		ids = business.mindShareRecordFactory().listIdsWithMindId(mindId);
		if(ListTools.isNotEmpty( ids )) {
			mindShareRecords = business.mindShareRecordFactory().list(ids);
		}
		
		mindRecycleInfo = business.mindRecycleInfoFactory().get(mindId);
		
		emc.beginTransaction( MindRecycleInfo.class );
		emc.beginTransaction( MindBaseInfo.class );
		emc.beginTransaction( MindShareRecord.class );
		emc.beginTransaction( MindVersionInfo.class );
		
		//删除分享表里的脑图信息
		if(ListTools.isNotEmpty( mindShareRecords )) {
			for( MindShareRecord mindShareInfo : mindShareRecords) {
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
			emc.remove( mindContentInfo, CheckRemoveType.all );
		}
		
		emc.commit();
		return true;
	}

	/**
	 * 根据脑图ID将脑图信息放入回收站
	 * 基础信息从基础信息表换入回收站信息表，以及分享的信息状态置为已删除
	 * @param mindId
	 * @throws Exception 
	 */
	public Boolean recycle(EntityManagerContainer emc, String mindId) throws Exception {
		Business business = new Business(emc);
		
		//先查询回收站中是否已经存在这个mindBaseInfo，如果存在则先删除，如果不存在则新增
		MindRecycleInfo mindRecycleInfo = emc.find( mindId,  MindRecycleInfo.class );

		//先删除回收站内的关于该脑图的相关信息
		if( mindRecycleInfo != null ) {
			emc.beginTransaction( MindRecycleInfo.class );
			emc.remove( mindRecycleInfo, CheckRemoveType.all );
			emc.commit();
		}
		
		//查询脑图信息
		MindBaseInfo mindBaseInfo = emc.find( mindId, MindBaseInfo.class);
		
		//根据脑图的信息，创建一个新的脑图回收信息
		MindRecycleInfo newMindRecycleInfo = new MindRecycleInfo();
		newMindRecycleInfo.setId( mindBaseInfo.getId() );
		newMindRecycleInfo.setName( mindBaseInfo.getName() );
		newMindRecycleInfo.setFolderId( mindBaseInfo.getFolderId() );
		newMindRecycleInfo.setCreator( mindBaseInfo.getCreator() );
		newMindRecycleInfo.setCreatorUnit( mindBaseInfo.getCreatorUnit() );
		newMindRecycleInfo.setDescription( mindBaseInfo.getDescription() );
		newMindRecycleInfo.setFileVersion( mindBaseInfo.getFileVersion() );
		newMindRecycleInfo.setShared( false );//放入回收站后，文件的分享信息全部被清空
		
		//查询所有分享过的脑图文档
		List<MindShareRecord> mindShareRecords = null;
		List<String> ids = business.mindShareRecordFactory().listIdsWithMindId(mindId);
		if( ListTools.isNotEmpty( ids )) {
			mindShareRecords = business.mindShareRecordFactory().list(ids);
		}
		
		//查询脑图文档所有的版本信息
		List<MindVersionInfo> mindVersionInfos = null;
		ids = business.mindVersionInfoFactory().listVersionsWithMindId(mindId);
		if( ListTools.isNotEmpty( ids )) {
			mindVersionInfos = business.mindVersionInfoFactory().listVersionsWithIds(ids);
		}
		
		//最后进行持久化操作
		emc.beginTransaction( MindRecycleInfo.class );
		emc.beginTransaction( MindBaseInfo.class );
		emc.beginTransaction( MindShareRecord.class );
		emc.beginTransaction( MindVersionInfo.class );
		
		//删除分享过的脑图文件的记录状态
		if( ListTools.isNotEmpty( mindShareRecords )) {
			for( MindShareRecord mindShareInfo : mindShareRecords ) {
				emc.remove( mindShareInfo, CheckRemoveType.all);
			}
		}
		
		//查询脑图文档所有的版本信息
		if( ListTools.isNotEmpty( mindVersionInfos )) {
			for( MindVersionInfo mindVersionInfo : mindVersionInfos ) {
				emc.remove( mindVersionInfo, CheckRemoveType.all);
			}
		}
		
		//新增一个回收站脑图信息
		newMindRecycleInfo.composeSequnces();
		emc.persist( newMindRecycleInfo, CheckPersistType.all );
		
		//删除脑图基础信息对象
		emc.remove( mindBaseInfo, CheckRemoveType.all );
		
		emc.commit();
		return true;
	}
	
	/**
	 * 根据脑图ID从回收站还原脑图信息
	 * 基础信息从回收站信息表换入基础信息表，以及分享的信息状态置为正常
	 * 注意原来的目录是否仍然存在，如果不存在，则放入根目录 root
	 * @param mindId
	 * @throws Exception 
	 */
	public Boolean restore(EntityManagerContainer emc, String mindId) throws Exception {
		if( StringUtils.isEmpty( mindId ) ) {
			throw new Exception("脑图ID为空，无法还原脑图信息！");
		}
		//先查询回收站中是否已经存在这个MindRecycleInfo，如果不存在则无法还原
		MindRecycleInfo mindRecycleInfo = emc.find( mindId,  MindRecycleInfo.class );
		if( mindRecycleInfo == null ) {
			throw new Exception("根据指定的ID未能从回收站查询到脑图信息，无法还原脑图信息！mindId：" + mindId);
		}
		
		//查询脑图信息, 如果已存在，则先删除后还原
		MindBaseInfo mindBaseInfo = emc.find( mindId, MindBaseInfo.class);
		if( mindBaseInfo != null ) {
			emc.beginTransaction( MindBaseInfo.class );
			emc.remove( mindBaseInfo, CheckRemoveType.all );
			emc.commit();
		}
		
		//查询脑较长原来的目录是否仍然存在，如果不存在，将脑图放入根目录root
		MindFolderInfo mindFolderInfo = emc.find( mindRecycleInfo.getFolderId(), MindFolderInfo.class );
		
		//根据脑图的回收站信息，创建一个新的脑图信息
		mindBaseInfo = new MindBaseInfo();
		mindBaseInfo.setId( mindId );
		mindBaseInfo.setName( mindRecycleInfo.getName() );
		if( mindFolderInfo == null ) {
			mindBaseInfo.setFolderId( "root" );
		}else {
			mindBaseInfo.setFolderId( mindRecycleInfo.getFolderId() );
		}
		mindBaseInfo.setCreator( mindRecycleInfo.getCreator() );
		mindBaseInfo.setCreatorUnit( mindRecycleInfo.getCreatorUnit() );
		mindBaseInfo.setDescription( mindRecycleInfo.getDescription() );
		mindBaseInfo.setFileVersion( mindRecycleInfo.getFileVersion() );
		mindBaseInfo.setShared( false ); //放入回收站后，会清空所有的分享信息
		
		List<String> editorList = new ArrayList<>();
		editorList.add( mindRecycleInfo.getCreator() );
		mindBaseInfo.setEditorList(editorList);
		
		//最后进行持久化操作
		emc.beginTransaction( MindRecycleInfo.class );
		emc.beginTransaction( MindBaseInfo.class );
		
		//新增一个脑图信息， 详细信息已经存在了
		mindBaseInfo.composeSequnces();
		emc.persist( mindBaseInfo, CheckPersistType.all );
		
		//删除回收站中脑图基础信息对象
		emc.remove( mindRecycleInfo, CheckRemoveType.all );
		
		emc.commit();
		return true;
	}
}

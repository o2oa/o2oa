package com.x.mind.assemble.control.service;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.mind.entity.MindFolderInfo;

/**
 * 脑图文件夹信息持久化操作服务类：保存、更新、删除
 * @author O2LEE
 *
 */
class ServiceMindFolderInfoPersist{
	
	/**
	 * 保存一个脑图目录
	 * @param mindFolderInfo
	 * @return
	 * @throws Exception 
	 */
	MindFolderInfo save(EntityManagerContainer emc, MindFolderInfo _mindFolderInfo) throws Exception {
		if( StringUtils.isEmpty(_mindFolderInfo.getId()) ){
			_mindFolderInfo.setId( MindFolderInfo.createId() );
		}
		if( StringUtils.isEmpty(_mindFolderInfo.getParentId()) ) {
			_mindFolderInfo.setParentId( "root" );
		}
		MindFolderInfo oldFolder = null;
		oldFolder = emc.find( _mindFolderInfo.getId(), MindFolderInfo.class );
		emc.beginTransaction( MindFolderInfo.class );
		if( oldFolder == null ){
			oldFolder = _mindFolderInfo;
			emc.persist( oldFolder, CheckPersistType.all);
		}else{
			_mindFolderInfo.copyTo( oldFolder, JpaObject.FieldsUnmodify  );
			emc.check( oldFolder, CheckPersistType.all );	
		}
		emc.commit();
		return oldFolder;
	}

	/**
	 * 根据ID删除文件夹信息
	 * @param emc
	 * @param folderId
	 * @return
	 * @throws Exception
	 */
	public Boolean delete(EntityManagerContainer emc, String folderId) throws Exception {
		MindFolderInfo entity = emc.find( folderId, MindFolderInfo.class );
		if( entity != null ){
			emc.beginTransaction( MindFolderInfo.class );
			emc.remove( entity,  CheckRemoveType.all);	
			emc.commit();
		}
		return true;
	}
}

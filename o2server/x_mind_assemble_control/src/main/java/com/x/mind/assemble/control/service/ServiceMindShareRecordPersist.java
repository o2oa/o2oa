package com.x.mind.assemble.control.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.tools.ListTools;
import com.x.mind.assemble.control.Business;
import com.x.mind.entity.MindBaseInfo;
import com.x.mind.entity.MindShareRecord;

/**
 * 脑图信息持久化操作服务类：保存、更新、删除
 * @author O2LEE
 *
 */
class ServiceMindShareRecordPersist{
	
	/**
	 * 保存文件分享信息
	 * @param emc 
	 * @param mindBaseInfo 
	 * @param mindShareRecords
	 * @throws Exception 
	 */
	public void share(EntityManagerContainer emc, MindBaseInfo mindBaseInfo, List<MindShareRecord> mindShareRecords) throws Exception {
		if( ListTools.isNotEmpty( mindShareRecords )) {
			Business business = new Business(emc);
			Boolean exists = false;

			//先确认一下分享信息是否已经存在了，根据fileId, target, targetType
			for( MindShareRecord ｍindShareRecord :mindShareRecords ) {
				exists = false;
				
				//判断当前这个分享信息是否已经存在
				exists = business.mindShareRecordFactory().exists( ｍindShareRecord );
				
				mindBaseInfo = emc.find( mindBaseInfo.getId(), MindBaseInfo.class );
				if( !exists ) {
					emc.beginTransaction( MindBaseInfo.class );
					emc.beginTransaction( MindShareRecord.class );
					//添加查看权限，并且添加分享信息
					if( mindBaseInfo != null ) {
						mindBaseInfo.setShared( true );
						//PERSON | UNIT | GROUP 
						if( "PERSON".equalsIgnoreCase( ｍindShareRecord.getTargetType() )) {
							if( mindBaseInfo.getSharePersonList() == null ) {
								mindBaseInfo.setSharePersonList( new ArrayList<>());
							}
							if( !mindBaseInfo.getSharePersonList().contains( ｍindShareRecord.getTarget() )) {
								mindBaseInfo.getSharePersonList().add( ｍindShareRecord.getTarget() );
							}
						}else if( "UNIT".equalsIgnoreCase( ｍindShareRecord.getTargetType() )) {
							if( mindBaseInfo.getShareUnitList() == null ) {
								mindBaseInfo.setShareUnitList( new ArrayList<>());
							}
							if( !mindBaseInfo.getShareUnitList().contains( ｍindShareRecord.getTarget() )) {
								mindBaseInfo.getShareUnitList().add( ｍindShareRecord.getTarget() );
							}
						}else if( "GROUP".equalsIgnoreCase( ｍindShareRecord.getTargetType() )) {
							if( mindBaseInfo.getShareGroupList() == null ) {
								mindBaseInfo.setShareGroupList( new ArrayList<>());
							}
							if( !mindBaseInfo.getShareGroupList().contains( ｍindShareRecord.getTarget() )) {
								mindBaseInfo.getShareGroupList().add( ｍindShareRecord.getTarget() );
							}
						}
						
						//添加分享信息
						emc.persist( ｍindShareRecord, CheckPersistType.all );
						//修改文件的分享标识
						emc.check( mindBaseInfo, CheckPersistType.all );						
					}
				}
				emc.commit();
			}
		}
	}

	/**
	 * 根据脑图分享信息ID删除脑图分享信息
	 * 
	 * @param shareRecordId
	 * @throws Exception 
	 */
	public Boolean shareCancel(EntityManagerContainer emc, String shareRecordId ) throws Exception {
		if( StringUtils.isEmpty( shareRecordId ) ) {
			throw new Exception("shareRecordId is empty!");
		}
		
		Business business = new Business(emc);
		MindShareRecord mindShareRecord = emc.find( shareRecordId, MindShareRecord.class);
		if( mindShareRecord == null ) {
			throw new Exception("mindShareRecord{ \"id\": \""+shareRecordId+"\" } is not exists!")  ;
		}
		//先删除一个MindShareRecord
		emc.beginTransaction( MindShareRecord.class );
		emc.remove( mindShareRecord, CheckRemoveType.all );
		emc.commit();
		
		//修改脑图的相关信息
		MindBaseInfo mindBaseInfo = emc.find( mindShareRecord.getFileId(), MindBaseInfo.class);
		emc.beginTransaction( MindBaseInfo.class );
		//PERSON | UNIT | GROUP 
		if( "PERSON".equalsIgnoreCase( mindShareRecord.getTargetType() )) {
			if( mindBaseInfo.getSharePersonList() == null ) {
				mindBaseInfo.setSharePersonList( new ArrayList<>());
			}
			if( mindBaseInfo.getSharePersonList().contains( mindShareRecord.getTarget() )) {
				mindBaseInfo.getSharePersonList().remove( mindShareRecord.getTarget() );
			}
		}else if( "UNIT".equalsIgnoreCase( mindShareRecord.getTargetType() )) {
			if( mindBaseInfo.getShareUnitList() == null ) {
				mindBaseInfo.setShareUnitList( new ArrayList<>());
			}
			if( mindBaseInfo.getShareUnitList().contains( mindShareRecord.getTarget() )) {
				mindBaseInfo.getShareUnitList().remove( mindShareRecord.getTarget() );
			}
		}else if( "GROUP".equalsIgnoreCase( mindShareRecord.getTargetType() )) {
			if( mindBaseInfo.getShareGroupList() == null ) {
				mindBaseInfo.setShareGroupList( new ArrayList<>());
			}
			if( mindBaseInfo.getShareGroupList().contains( mindShareRecord.getTarget() )) {
				mindBaseInfo.getShareGroupList().remove( mindShareRecord.getTarget() );
			}
		}
		
		//看看这个文件是否还有分享记录，如果没有了，就把分享标识改为false
		List<String> shareRecordIds= business.mindShareRecordFactory().listIdsWithMindId( mindShareRecord.getFileId() );
		if( ListTools.isEmpty( shareRecordIds )) {
			mindBaseInfo.setShared( false );
		}
		emc.check( mindBaseInfo, CheckPersistType.all );
		emc.commit();
		return true;
	}	
}

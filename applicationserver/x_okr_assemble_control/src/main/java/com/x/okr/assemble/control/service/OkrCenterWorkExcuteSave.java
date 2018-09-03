package com.x.okr.assemble.control.service;

import java.util.Date;
import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.okr.assemble.control.Business;
import com.x.okr.entity.OkrCenterWorkInfo;
import com.x.okr.entity.OkrWorkBaseInfo;
import com.x.okr.entity.OkrWorkPerson;

public class OkrCenterWorkExcuteSave {
	
	private OkrWorkPersonService okrWorkPersonService = new OkrWorkPersonService();


	/**
	 * 向数据库保存OkrCenterWorkInfo对象
	 * @param wrapIn
	 */
	public OkrCenterWorkInfo save( EntityManagerContainer emc, OkrCenterWorkInfo okrCenterWorkInfo ) throws Exception {
		List<OkrWorkPerson> okrWorkPersonList = null;
		OkrCenterWorkInfo okrCenterWorkInfo_old = null;
		OkrWorkBaseInfo okrWorkBaseInfo = null;
		List<String> ids = null;
		Business business = new Business(emc);
		emc.beginTransaction( OkrWorkPerson.class );
		emc.beginTransaction( OkrCenterWorkInfo.class );
		emc.beginTransaction( OkrWorkBaseInfo.class );
		
		if( okrCenterWorkInfo.getId() !=null && !okrCenterWorkInfo.getId().isEmpty() ){
			okrCenterWorkInfo_old =  emc.find( okrCenterWorkInfo.getId(), OkrCenterWorkInfo.class );
		}
		
		if( okrCenterWorkInfo_old == null ){//保存新的中心工作信息
			okrCenterWorkInfo.setCreateTime( new Date() );
			okrCenterWorkInfo.setUpdateTime( okrCenterWorkInfo.getCreateTime() );
			emc.persist( okrCenterWorkInfo, CheckPersistType.all);
		}else{//更新中心工作信息	
			//如果当前的标题和原来的标题不一致，那么需要修改所有具体工作项目的中心工作标题
			if( !okrCenterWorkInfo_old.getTitle().equals( okrCenterWorkInfo.getTitle()) ){
				ids = business.okrWorkBaseInfoFactory().listByCenterWorkId( okrCenterWorkInfo.getId(), null );
				if( ids != null && ids.size() > 0 ){
					for( String id : ids ){
						okrWorkBaseInfo = emc.find( id, OkrWorkBaseInfo.class );
						if( okrWorkBaseInfo != null ){
							okrWorkBaseInfo.setCenterTitle( okrCenterWorkInfo.getTitle() );
							emc.check( okrWorkBaseInfo, CheckPersistType.all );	
						}
					}
				}
			}
			String sequence = okrCenterWorkInfo_old.getSequence();
			okrCenterWorkInfo.setCreateTime( okrCenterWorkInfo_old.getCreateTime() );
			okrCenterWorkInfo.copyTo( okrCenterWorkInfo_old, JpaObject.FieldsUnmodify );
			okrCenterWorkInfo_old.setSequence(sequence);
			emc.check( okrCenterWorkInfo_old, CheckPersistType.all );	
		}
		//删除原先所有的干系人信息，先查询该中心工作信息注册的所有干系人信息列表
		ids = okrWorkPersonService.listIdsForCenterWorkByCenterId( okrCenterWorkInfo.getId(), null );
		okrWorkPersonList = business.okrWorkPersonFactory().list( ids );
		if( okrWorkPersonList != null && okrWorkPersonList.size() > 0 ){
			for( OkrWorkPerson okrWorkPerson : okrWorkPersonList ){
				emc.remove( okrWorkPerson, CheckRemoveType.all );
			}
		}
		//保存中心工作的干系人信息，先根据中心工作信息来获取工作所有的干系人对象信息
		okrWorkPersonList = okrWorkPersonService.getWorkPersonListByCenterWorkInfo( okrCenterWorkInfo );
		if( okrWorkPersonList != null && okrWorkPersonList.size() > 0 ){				
			for( OkrWorkPerson okrWorkPerson : okrWorkPersonList ){
				okrWorkPerson.setWorkProcessStatus( "草稿" );
				emc.persist( okrWorkPerson, CheckPersistType.all);	
			}
		}			
		emc.commit();		
		return okrCenterWorkInfo;
	}
}
package com.x.okr.assemble.control.service;

import com.x.base.core.container.EntityManagerContainer;
import com.x.okr.entity.OkrCenterWorkInfo;

public class OkrCenterWorkExcuteDeploy {
	
	private OkrWorkPersonService okrWorkPersonService = new OkrWorkPersonService();

	/**
	 * 部署中心工作，只需要将中心工作的状态修改为[执行中]即可
	 * 维护中心工作干系人
	 * @param id
	 * @throws Exception 
	 */
	public void deploy( EntityManagerContainer emc, String id ) throws Exception {
		OkrCenterWorkInfo okrCenterWorkInfo = null;
		if( id  == null || id.isEmpty() ){
			throw new Exception( "id is null, can not deploy any center work!" );
		}
		okrCenterWorkInfo = emc.find( id, OkrCenterWorkInfo.class );
		if( okrCenterWorkInfo != null ){
			emc.beginTransaction( OkrCenterWorkInfo.class );
			okrCenterWorkInfo.setProcessStatus( "执行中" );
			emc.commit();
			//根据中心工作信息维护中心工作干系人信息
			okrWorkPersonService.saveCenterWorkPersonByCenterWork( okrCenterWorkInfo );
		}
	}
}
package com.x.hotpic.assemble.control.service;

import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.hotpic.assemble.control.Business;
import com.x.hotpic.entity.HotPictureInfo;

public class HotPictureInfoService {
	
	/**
	 * 向数据库保存HotPictureInfo对象
	 * @param _hotPictureInfo
	 */
	public HotPictureInfo save( EntityManagerContainer emc, HotPictureInfo _hotPictureInfo ) throws Exception {
		if( _hotPictureInfo  == null ){
			throw new Exception( "_hotPictureInfo is null, can not save object!" );
		}
		if( _hotPictureInfo.getId() == null ){
			_hotPictureInfo.setId( HotPictureInfo.createId() );
		}
		HotPictureInfo _hotPictureInfo_tmp = emc.find( _hotPictureInfo.getId(), HotPictureInfo.class );
		if( _hotPictureInfo_tmp != null ){
			_hotPictureInfo_tmp = getByApplicationInfoId( emc, _hotPictureInfo.getApplication(), _hotPictureInfo.getInfoId() );
		}
		emc.beginTransaction( HotPictureInfo.class );
		if( _hotPictureInfo_tmp == null ){
			emc.persist( _hotPictureInfo, CheckPersistType.all);
		}else{
			_hotPictureInfo.copyTo( _hotPictureInfo_tmp, JpaObject.FieldsUnmodify );
			emc.check( _hotPictureInfo_tmp, CheckPersistType.all );
		}	
		emc.commit();
		return _hotPictureInfo;
	}
	
	public HotPictureInfo getByApplicationInfoId( EntityManagerContainer emc, String application, String infoId) throws Exception {
		Business business = new Business( emc );
		List<HotPictureInfo> hotPictureInfoList = business.hotPictureInfoFactory().listByApplicationInfoId( application, infoId );
		if( hotPictureInfoList == null || hotPictureInfoList.isEmpty() ){
			return null;
		}else{
			return hotPictureInfoList.get(0);
		}
	}

	public List<HotPictureInfo> listAll( EntityManagerContainer emc ) throws Exception {
		Business business = new Business( emc );
		return business.hotPictureInfoFactory().listAll();
	}

	public List<HotPictureInfo> list(EntityManagerContainer emc, List<String> ids) throws Exception {
		Business business = new Business( emc );
		return business.hotPictureInfoFactory().list( ids );
	}

	public List<String> listByApplication(EntityManagerContainer emc, String application ) throws Exception {
		Business business = new Business( emc );
		return business.hotPictureInfoFactory().listByApplication( application );
	}

	public Long count(EntityManagerContainer emc, String application, String infoId, String title) throws Exception {
		Business business = new Business( emc );
		return business.hotPictureInfoFactory().count( application, infoId, title );
	}

	public List<HotPictureInfo> listForPage(EntityManagerContainer emc, String application, String infoId, String title, Integer selectTotal) throws Exception {
		Business business = new Business( emc );
		return business.hotPictureInfoFactory().listForPage( application, infoId, title, selectTotal );
	}

	public List<HotPictureInfo> listForPage(EntityManagerContainer emc, String application, String infoId, String title, Integer first, Integer selectTotal) throws Exception {
		Business business = new Business( emc );
		return business.hotPictureInfoFactory().listForPage( application, infoId, title,first, selectTotal );
	}
	
	public List<HotPictureInfo> listByApplicationInfoId(EntityManagerContainer emc, String application, String infoId) throws Exception {
		Business business = new Business( emc );
		return business.hotPictureInfoFactory().listByApplicationInfoId( application, infoId );
	}
}
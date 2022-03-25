package com.x.hotpic.assemble.control.service;

import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.hotpic.assemble.control.Business;
import com.x.hotpic.assemble.control.ThisApplication;
import com.x.hotpic.assemble.control.queueTask.queue.DocumentCheckQueue;
import com.x.hotpic.entity.HotPictureInfo;

public class HotPictureInfoServiceAdv {
	
	private Logger logger = LoggerFactory.getLogger( HotPictureInfoServiceAdv.class );
	private HotPictureInfoService hotPictureInfoService = new HotPictureInfoService();
	
	/**
	 * 根据传入的ID从数据库查询HotPictureInfo对象
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public HotPictureInfo get( String id ) throws Exception {
		if( id  == null || id.isEmpty() ){
			throw new Exception( "id is null, return null!" );
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			return emc.find( id, HotPictureInfo.class );
		}catch( Exception e ){
			throw e;
		}
	}
	
	/**
	 * 向数据库保存HotPictureInfo对象
	 * @param _hotPictureInfo
	 */
	public HotPictureInfo save( HotPictureInfo _hotPictureInfo ) throws Exception {
		if( _hotPictureInfo  == null ){
			throw new Exception( "_hotPictureInfo is null, can not save object!" );
		}
		if( _hotPictureInfo.getId() == null || _hotPictureInfo.getId().isEmpty() ){
			_hotPictureInfo.setId( HotPictureInfo.createId() );
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			return hotPictureInfoService.save( emc, _hotPictureInfo );
		}catch( Exception e ){
			throw e;
		}
	}
	
	/**
	 * 根据ID从数据库中删除HotPictureInfo对象
	 * @param id
	 * @throws Exception
	 */
	public void delete( String id ) throws Exception {
		if( id  == null || id.isEmpty() ){
			throw new Exception( "id is null, can not delete object!" );
		}
		HotPictureInfo hotPictureInfo = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			hotPictureInfo = emc.find( id, HotPictureInfo.class );
			if( hotPictureInfo == null ){
				throw new Exception("info is not exists ,can not excute delete.");
			}else{
				emc.beginTransaction( HotPictureInfo.class );
				emc.remove( hotPictureInfo, CheckRemoveType.all );
				emc.commit();
			}
		}catch( Exception e ){
			throw e;
		}
	}
	
	public void deleteWithInfoId( String infoId ) throws Exception {
		if( infoId  == null || infoId.isEmpty() ){
			throw new Exception( "infoId is null, can not delete object!" );
		}
		List<HotPictureInfo> hotPictureInfo_list = null;
		Business business = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			business = new Business(emc);
			hotPictureInfo_list = business.hotPictureInfoFactory().getWithInfoId(infoId);
			if( ListTools.isNotEmpty(hotPictureInfo_list) ){
				emc.beginTransaction( HotPictureInfo.class );
				for( HotPictureInfo hotPictureInfo : hotPictureInfo_list ) {
					emc.remove( hotPictureInfo, CheckRemoveType.all );
				}
				emc.commit();
			}
		}catch( Exception e ){
			throw e;
		}
	}

	public List<HotPictureInfo> list( List<String> ids ) throws Exception {
		if( ids  == null || ids.isEmpty() ){
			return null;
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			return hotPictureInfoService.list( emc, ids );
		}catch( Exception e ){
			throw e;
		}
	}
	
	public List<String> listIdsByApplication( String application ) throws Exception {
		if( application  == null || application.isEmpty() ){
			throw new Exception("application can not be null!");
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			return hotPictureInfoService.listByApplication( emc, application );
		}catch( Exception e ){
			throw e;
		}
	}
	
	public List<HotPictureInfo> listByApplication( String application ) throws Exception {
		if( application  == null || application.isEmpty() ){
			throw new Exception("application can not be null!");
		}
		List<String> ids = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			ids = hotPictureInfoService.listByApplication( emc, application );
			if( ids != null && !ids.isEmpty() ){
				return hotPictureInfoService.list( emc, ids );
			}else{
				return null;
			}
		}catch( Exception e ){
			throw e;
		}
	}
	
	public List<HotPictureInfo> listAll() throws Exception {
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			return hotPictureInfoService.listAll( emc );
		}catch( Exception e ){
			throw e;
		}
	}

	public Long count( String application, String infoId, String title ) throws Exception {
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			return hotPictureInfoService.count( emc, application, infoId, title );
		}catch( Exception e ){
			throw e;
		}
	}

	public List<HotPictureInfo> listForPage(String application, String infoId, String title, Integer selectTotal) throws Exception {
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			return hotPictureInfoService.listForPage( emc, application, infoId, title, selectTotal );
		}catch( Exception e ){
			throw e;
		}
	}

	public List<HotPictureInfo> listForPage(String application, String infoId, String title, Integer first, Integer selectTotal) throws Exception {
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			return hotPictureInfoService.listForPage( emc, application, infoId, title, first,selectTotal );
		}catch( Exception e ){
			throw e;
		}
	}
	
	public List<HotPictureInfo> listByApplicationInfoId(String application, String infoId) throws Exception {
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			return hotPictureInfoService.listByApplicationInfoId( emc, application, infoId );
		}catch( Exception e ){
			throw e;
		}
	}
	
	public void documentExistsCheck() {
		List<HotPictureInfo> allHotPictureInfoList = null;
		// 1、先查询出所有的信息列表，按照排序号和更新时间倒排序
		try {
			allHotPictureInfoList = listAll();
		} catch (Exception e) {
			logger.error( e );
		}
		if ( allHotPictureInfoList != null && !allHotPictureInfoList.isEmpty() ) {
			int[] idx = { 0 };
			allHotPictureInfoList.forEach(e -> {
				try {
					ThisApplication.queueLoginRecord.send( new DocumentCheckQueue( ++idx[0], e.getInfoId(), e.getApplication(), e.getTitle() ) );
				} catch ( Exception e1 ) {
					e1.printStackTrace();
				}
			} );
		}
		logger.info("Hotpicture document exists check excute completed.");
	}

	public void changeTitle(String id, String title) throws Exception {
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			HotPictureInfo hotPictureInfo = emc.find( id, HotPictureInfo.class );
			if( hotPictureInfo != null ) {
				emc.beginTransaction( HotPictureInfo.class );
				hotPictureInfo.setTitle( title );
				emc.check( hotPictureInfo, CheckPersistType.all );
				emc.commit();
			}
		}catch( Exception e ){
			throw e;
		}
	}

}
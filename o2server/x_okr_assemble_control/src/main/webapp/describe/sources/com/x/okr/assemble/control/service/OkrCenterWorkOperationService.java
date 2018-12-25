package com.x.okr.assemble.control.service;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.okr.entity.OkrCenterWorkInfo;

public class OkrCenterWorkOperationService {
	
	private static  Logger logger = LoggerFactory.getLogger( OkrCenterWorkOperationService.class );
	private OkrCenterWorkExcuteArchive okrCenterWorkExcuteArchive = new OkrCenterWorkExcuteArchive();
	private OkrCenterWorkExcuteDelete okrCenterWorkExcuteDelete = new OkrCenterWorkExcuteDelete();
	private OkrCenterWorkExcuteDeploy okrCenterWorkExcuteDeploy = new OkrCenterWorkExcuteDeploy();
	private OkrCenterWorkExcuteSave okrCenterWorkExcuteSave = new OkrCenterWorkExcuteSave();
	


	/**
	 * 向数据库保存OkrCenterWorkInfo对象
	 * @param wrapIn
	 */
	public OkrCenterWorkInfo save( OkrCenterWorkInfo okrCenterWorkInfo ) throws Exception {
		if( okrCenterWorkInfo == null ){
			throw new Exception("okrCenterWorkInfo is null!");
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			okrCenterWorkInfo = okrCenterWorkExcuteSave.save( emc, okrCenterWorkInfo );
		}catch( Exception e ){
			logger.warn( "OkrCenterWorkInfo update/ get a error!" );
			throw e;
		}
		return okrCenterWorkInfo;
	}

	/**
	 * 根据ID从数据库中删除OkrCenterWorkInfo对象
	 * 同时删除所有的下级工作以及工作的相关汇报，请示等等
	 * @param id
	 * @throws Exception
	 */
	public void delete( String centerId ) throws Exception {
		if( centerId == null || centerId.isEmpty() ){
			throw new Exception( "centerId is null, system can not delete any object." );
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			okrCenterWorkExcuteDelete.delete( emc, centerId );
		} catch ( Exception e ) {
			throw e;
		}
	}
	
	/**
	 * 根据ID从归档OkrCenterWorkInfo对象
	 * 同时归档所有的下级工作以及工作的相关汇报，请示等等
	 * 并且删除所有待办
	 * @param id
	 * @throws Exception
	 */
	public void archive( String centerId ) throws Exception {
		if( centerId == null || centerId.isEmpty() ){
			throw new Exception( "centerId is null, system can not archive any object." );
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			okrCenterWorkExcuteArchive.archive( emc, centerId );
		} catch ( Exception e ) {
			throw e;
		}
	}

	/**
	 * 部署中心工作，只需要将中心工作的状态修改为[执行中]即可
	 * 维护中心工作干系人
	 * @param id
	 * @throws Exception 
	 */
	public void deploy( String centerId ) throws Exception {
		if( centerId == null || centerId.isEmpty() ){
			throw new Exception( "centerId is null, system can not deploy any object." );
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			okrCenterWorkExcuteDeploy.deploy( emc, centerId );
		} catch ( Exception e ) {
			throw e;
		}
	}
}
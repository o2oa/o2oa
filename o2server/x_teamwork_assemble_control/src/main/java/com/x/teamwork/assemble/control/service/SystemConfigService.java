package com.x.teamwork.assemble.control.service;

import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.teamwork.assemble.control.Business;
import com.x.teamwork.core.entity.SystemConfig;
import com.x.teamwork.core.entity.SystemConfigLobValue;

class SystemConfigService {
	
	/**
	 * 获取所有的系统设置信息
	 * @return
	 * @throws Exception
	 */
	protected List<SystemConfig> listAll( EntityManagerContainer emc ) throws Exception {
		Business business =  new Business( emc );
		return business.systemConfigFactory().listAll();
	}
	
	/**
	 * 根据ID获取指定的系统设置信息
	 * @param flag
	 * @return
	 * @throws Exception
	 */
	protected SystemConfig get( EntityManagerContainer emc, String flag ) throws Exception {
		return emc.flag( flag, SystemConfig.class);
	}

	/**
	 * 根据ID删除指定的系统设置信息
	 * @param id
	 * @throws Exception
	 */
	protected void delete( EntityManagerContainer emc, String id ) throws Exception {
		SystemConfig systemConfig = null;
		if( id == null || id.isEmpty() ){
			throw new Exception( "id is null, system can not delete any object." );
		}
		systemConfig = emc.find( id, SystemConfig.class );
		if ( null == systemConfig ) {
			throw new Exception( "object is not exist {'id':'"+ id +"'}" );
		}else{
			emc.beginTransaction( SystemConfig.class );
			emc.remove( systemConfig, CheckRemoveType.all );
			emc.commit();
		}
	}
	/**
	 * 根据设置编码获取指定的设置信息
	 * @param code
	 * @return
	 * @throws Exception
	 */
	protected SystemConfig getByCode( EntityManagerContainer emc, String code ) throws Exception {
		List<SystemConfig> systemConfigList = null;
		SystemConfig systemConfig = null;
		List<String> ids = null;
		Business business = new Business( emc );
		if( code == null || code.isEmpty() ){
			throw new Exception( "code is null, system can not query any object." );
		}
		
		ids = business.systemConfigFactory().listIdsByCode(code);
		if( ids != null && !ids.isEmpty() ){
			systemConfigList =  business.systemConfigFactory().list( ids );
		}
		
		if( systemConfigList != null && !systemConfigList.isEmpty() ){
			for( int i=0; i< systemConfigList.size(); i++  ){
				if( i == 0 ){
					systemConfig = systemConfigList.get( i );
				}else{
					emc.beginTransaction( SystemConfig.class );
					emc.remove( systemConfigList.get( i ), CheckRemoveType.all );
					emc.commit();
				}
			}
		}		
		return systemConfig;
	}

	/**
	 * 创建系统设置信息
	 * @param systemConfig
	 * @return
	 * @throws Exception
	 */
	protected SystemConfig create( EntityManagerContainer emc, SystemConfig systemConfig ) throws Exception {
		SystemConfig systemConfig_old = null;
		SystemConfigLobValue reportSettingLobValue = null;
		
		systemConfig_old = emc.find( systemConfig.getId(), SystemConfig.class );
		if( systemConfig_old != null ){
			throw new Exception("systemConfig{'id':' "+ systemConfig.getId() +" '} exists, can not create new object");
		}else{
			emc.beginTransaction( SystemConfig.class );

			//对长文本记录进行操作
			if( systemConfig.getIsLob() ) {
				emc.beginTransaction( SystemConfigLobValue.class );
				reportSettingLobValue = emc.find( systemConfig.getId(), SystemConfigLobValue.class );
				if( reportSettingLobValue != null ) {
					reportSettingLobValue.setLobValue( systemConfig.getConfigValue() );//更新值
					emc.check( reportSettingLobValue, CheckPersistType.all );
				}else {
					//没有，就创建一个LOB值记录
					reportSettingLobValue = new SystemConfigLobValue();
					reportSettingLobValue.setId( systemConfig.getId() );
					reportSettingLobValue.setLobValue( systemConfig.getConfigValue() );
					emc.persist( reportSettingLobValue, CheckPersistType.all );
				}
				systemConfig.setConfigValue( "LobValue" );
			}
			
			emc.persist( systemConfig, CheckPersistType.all);
			emc.commit();
		}
		return systemConfig;
	}
	
	/**
	 * 更新系统设置信息
	 * @param systemConfig
	 * @return
	 * @throws Exception
	 */
	protected SystemConfig update( EntityManagerContainer emc, SystemConfig systemConfig ) throws Exception {
		if( systemConfig == null ){
			throw new Exception("systemConfig is null, can not update object!");
		}
		SystemConfig systemConfig_old = null;
		SystemConfigLobValue reportSettingLobValue = null;
		Business business = new Business(emc);
		systemConfig_old = business.systemConfigFactory().getWithConfigCode(systemConfig.getConfigCode());
		if( systemConfig_old != null ){
			emc.beginTransaction( SystemConfig.class );
			systemConfig_old.setConfigCode( systemConfig.getConfigCode() );
			systemConfig_old.setConfigName( systemConfig.getConfigName() );
			systemConfig_old.setConfigValue( systemConfig.getConfigValue() );
			systemConfig_old.setOrderNumber( systemConfig.getOrderNumber() );
			systemConfig_old.setIsLob( systemConfig.getIsLob() );
			
			//对长文本记录进行操作
			if( systemConfig_old.getIsLob() ) {
				emc.beginTransaction( SystemConfigLobValue.class );
				reportSettingLobValue = emc.find( systemConfig.getId(), SystemConfigLobValue.class );
				if( reportSettingLobValue != null ) {
					reportSettingLobValue.setLobValue( systemConfig.getConfigValue() );//更新值
					emc.check( reportSettingLobValue, CheckPersistType.all );
				}else {
					//没有，就创建一个LOB值记录
					reportSettingLobValue = new SystemConfigLobValue();
					reportSettingLobValue.setId( systemConfig.getId() );
					reportSettingLobValue.setLobValue( systemConfig.getConfigValue() );
					emc.persist( reportSettingLobValue, CheckPersistType.all );
				}
				systemConfig_old.setConfigValue( "LobValue" );
			}
			emc.check( systemConfig_old, CheckPersistType.all);
			emc.commit();
		}else{
			throw new Exception("old object systemConfig{'id':' "+ systemConfig.getId() +" '} is not exists. ");
		}
		
		return systemConfig_old;
	}

}

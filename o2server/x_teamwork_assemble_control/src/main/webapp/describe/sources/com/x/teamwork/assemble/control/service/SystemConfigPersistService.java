package com.x.teamwork.assemble.control.service;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.teamwork.assemble.control.Business;
import com.x.teamwork.core.entity.SystemConfig;
import com.x.teamwork.core.entity.SystemConfigLobValue;

public class SystemConfigPersistService{

	private Logger logger = LoggerFactory.getLogger( SystemConfigPersistService.class );
	private SystemConfigService systemConfigService = new SystemConfigService();

	/**
	 * 初始化所有的系统设置
	 * @throws Exception
	 */
	public void initSystemConfig() throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			String value = null, description = null, type = null, selectContent = null;
			Boolean isMultiple = false;
			Boolean isLob = false;
			Integer ordernumber = 0;
			String configCode = null;
			String configName = null;
			
			configCode = "PROJECT_CREATOR";
			configName = "项目创建者权限";
			value = "ALL";
			type = "select";
			selectContent = "ALL|Manager";
			isMultiple = false;
			isLob = false;
			description = "指定项目创建者：可选值为ALL|Manager，单值。ALL：所有人均可创建项目；Manager：拥有TeamWorkManager角色和管理员可创建项目。";
			try {
				checkAndInitSystemConfig( configCode, configName, value, isLob, description, type, selectContent, isMultiple,  ++ordernumber );
			} catch (Exception e) {
				logger.warn( "system init system config '" + configCode + "' got an exception." );
				logger.error(e);
			}
		} catch ( Exception e ) {
			throw e;
		}
	}

	/**
	 * 保存系统设置信息
	 * @param systemConfig
	 * @return
	 * @throws Exception
	 */
	public SystemConfig save( SystemConfig systemConfig ) throws Exception {
		SystemConfig systemConfig_old = null;
		Business business = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			systemConfig_old = business.systemConfigFactory().getWithConfigCode(systemConfig.getConfigCode());
			if( systemConfig_old != null ){
				systemConfig.setId( systemConfig_old.getId() );
				return systemConfigService.update( emc, systemConfig );	
			}else{
				return systemConfigService.create( emc, systemConfig );	
			}
		} catch ( Exception e ) {
			throw e;
		}
	}
	
	/**
	 * 检查配置项是否存在，如果不存在根据信息创建一个新的配置项
	 * @param configCode
	 * @param configName
	 * @param configValue
	 * @param isLob 
	 * @param description
	 * @throws Exception
	 */
	private void checkAndInitSystemConfig( String configCode, String configName, String configValue, Boolean isLob, String description, String type, String selectContent, Boolean isMultiple, Integer orderNumber ) throws Exception {
		if( configCode  == null || configCode.isEmpty() ){
			throw new Exception( "configCode is null!" );
		}
		if( configName  == null || configName.isEmpty() ){
			throw new Exception( "configName is null!" );
		}
		Business business = null;
		SystemConfig systemConfig = null;
		SystemConfigLobValue reportSettingLobValue = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			business = new Business(emc);
			systemConfig = business.systemConfigFactory().getWithConfigCode( configCode );
		}catch( Exception e ){
			logger.warn( "system find system config{'configCode':'"+configCode+"'} got an exception. " );
			logger.error(e);
		}
		//如果配置不存在，则新建一个配置记录
		if( systemConfig == null ){
			systemConfig = new SystemConfig();
			systemConfig.setConfigCode( configCode );
			systemConfig.setConfigName( configName );
			systemConfig.setConfigValue( configValue );
			systemConfig.setDescription( description );
			systemConfig.setOrderNumber( orderNumber );
			systemConfig.setValueType( type );
			systemConfig.setIsLob( isLob );
			systemConfig.setSelectContent( selectContent );
			systemConfig.setIsMultiple( isMultiple );
			
			try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
				business = new Business(emc);
				emc.beginTransaction( SystemConfig.class );
				
				//对长文本记录进行操作
				if( isLob ) {
					emc.beginTransaction( SystemConfigLobValue.class );
					reportSettingLobValue = emc.find( systemConfig.getId(), SystemConfigLobValue.class );
					if( reportSettingLobValue != null ) {
						reportSettingLobValue.setLobValue( configValue );//更新值
						emc.check( reportSettingLobValue, CheckPersistType.all );
					}else {
						//没有，就创建一个LOB值记录
						reportSettingLobValue = new SystemConfigLobValue();
						reportSettingLobValue.setId( systemConfig.getId() );
						reportSettingLobValue.setLobValue( configValue );
						emc.persist( reportSettingLobValue, CheckPersistType.all );
					}
					systemConfig.setConfigValue( "LobValue" );
				}
				emc.persist( systemConfig, CheckPersistType.all );
				emc.commit();
			}catch( Exception e ){
				logger.warn("attendance system persist new system config{'configCode':'"+configCode+"'} got an exception. " );
				logger.error(e);
			}
		}else{
			try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
				systemConfig = emc.find( systemConfig.getId(), SystemConfig.class );
				systemConfig.setIsLob( isLob );
				emc.beginTransaction( SystemConfig.class );
				
				if( !configName.equals( systemConfig.getConfigName() ) ){
					systemConfig.setConfigName( configName );
				}
				if( orderNumber != systemConfig.getOrderNumber() ){
					systemConfig.setOrderNumber(orderNumber);
				}
				if( description != null ){
					systemConfig.setDescription( description );
				}
				if( type != null ){
					systemConfig.setValueType( type );
				}
				if( selectContent != null ){
					systemConfig.setSelectContent( selectContent );
				}
				if( isMultiple != null ){
					systemConfig.setIsMultiple( isMultiple );
				}
				
				//对长文本记录进行操作
				if( isLob ) {
					emc.beginTransaction( SystemConfigLobValue.class );
					reportSettingLobValue = emc.find( systemConfig.getId(), SystemConfigLobValue.class );
					if( reportSettingLobValue != null ) {
						//reportSettingLobValue.setLobValue( configValue );//更新值
						emc.check( reportSettingLobValue, CheckPersistType.all );
					}else {
						//没有，就创建一个LOB值记录
						reportSettingLobValue = new SystemConfigLobValue();
						reportSettingLobValue.setId( systemConfig.getId() );
						reportSettingLobValue.setLobValue( configValue );
						emc.check( reportSettingLobValue, CheckPersistType.all );
					}
					systemConfig.setConfigValue( "LobValue" );
				}
				
				emc.check( systemConfig, CheckPersistType.all );
				emc.commit();
			}catch( Exception e ){
				logger.warn("attendance system update system config{'configCode':'"+configCode+"'} got an exception. " );
				logger.error(e);
			}
		}
	}
}

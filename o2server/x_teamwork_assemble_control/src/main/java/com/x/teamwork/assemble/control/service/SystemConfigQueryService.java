package com.x.teamwork.assemble.control.service;

import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.teamwork.core.entity.SystemConfig;
import com.x.teamwork.core.entity.SystemConfigLobValue;

public class SystemConfigQueryService{

	private SystemConfigService systemConfigService = new SystemConfigService();

	/**
	 * 获取所有的系统设置信息
	 * @return
	 * @throws Exception
	 */
	public List<SystemConfig> listAll() throws Exception {
		List<SystemConfig> configs = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			configs = systemConfigService.listAll( emc );	
			if( ListTools.isNotEmpty( configs )) {
				for( SystemConfig config : configs ) {
					if( config.getIsLob() ) {
						config.setConfigValue( getLobValueWithId( config.getId() ) );
					}
				}
			}
		} catch ( Exception e ) {
			throw e;
		}
		return configs;
	}

	/**
	 * 根据ID获取指定的系统设置信息
	 * @param flag
	 * @return
	 * @throws Exception
	 */
	public SystemConfig get(String flag) throws Exception {
		SystemConfig config = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			config = systemConfigService.get( emc, flag );	
			if( config != null ) {
				if( config.getIsLob() ) {
					config.setConfigValue( getLobValueWithId( config.getId() ) );
				}
			}
		} catch ( Exception e ) {
			throw e;
		}
		return config;
	}
	
	/**
	 * 根据设置编码获取指定的设置信息
	 * @param code
	 * @return
	 * @throws Exception
	 */
	public SystemConfig getByCode( String code ) throws Exception {
		SystemConfig config = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			config =  systemConfigService.getByCode( emc, code );	
			if( config != null ) {
				if( config.getIsLob() ) {
					config.setConfigValue( getLobValueWithId( config.getId() ) );
				}
			}
		} catch ( Exception e ) {
			throw e;
		}
		 return config;
	}
	
	/**
	 * 根据ID获取指定的设置信息的具体配置值
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public String getValueByCode( String code ) throws Exception {
		SystemConfig systemConfig = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			systemConfig = systemConfigService.getByCode( emc, code );
			if( systemConfig != null ) {
				if( systemConfig.getIsLob() ) {
					return getLobValueWithId( systemConfig.getId() );					
				}else {
					return systemConfig.getConfigValue();
				}
			}
		} catch ( Exception e ) {
			throw e;
		}
		return null;
	}
	
	/**
	 * 根据编码获取指定的设置信息的具体配置值
	 * @param code
	 * @return
	 * @throws Exception
	 */
	public String getValueById( String id ) throws Exception {
		SystemConfig systemConfig = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			systemConfig = emc.find( id, SystemConfig.class );
			if( systemConfig != null ) {
				if( !systemConfig.getIsLob() ) {
					return systemConfig.getConfigValue();
				}else {
					return getLobValueWithId( id );
				}
			}
		} catch ( Exception e ) {
			throw e;
		}
		return null;
	}

	/**
	 * 根据ID获取指定配置的LOB值
	 * @param id
	 * @return
	 * @throws Exception
	 */
	private String getLobValueWithId( String id ) throws Exception {
		SystemConfigLobValue lobValue = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			lobValue = emc.find( id, SystemConfigLobValue.class );
			if( lobValue != null ) {
				return lobValue.getLobValue();
			}
		} catch ( Exception e ) {
			throw e;
		}
		return null;
	}
	
}

package com.x.report.assemble.control.service;

import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.report.assemble.control.Business;
import com.x.report.core.entity.Report_S_Setting;
import com.x.report.core.entity.Report_S_SettingLobValue;

/**
 * 汇报系统设置服务类
 * @author O2LEE
 *
 */
public class Report_S_SettingServiceAdv{

	private Report_S_SettingService report_S_SettingService = new Report_S_SettingService();
	private Report_S_SettingInitService report_S_SettingInitService = new Report_S_SettingInitService();

	/**
	 * 初始化所有的系统设置
	 * @throws Exception
	 */
	public void initAllSystemConfig() throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			report_S_SettingInitService.initAllSystemConfig();	
		} catch ( Exception e ) {
			throw e;
		}
	}
	
	/**
	 * 获取所有的系统设置信息
	 * @return
	 * @throws Exception
	 */
	public List<Report_S_Setting> listAll() throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return report_S_SettingService.listAll( emc );	
		} catch ( Exception e ) {
			throw e;
		}
	}

	/**
	 * 根据ID获取指定的系统设置信息
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public Report_S_Setting get(String id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return report_S_SettingService.get( emc, id );	
		} catch ( Exception e ) {
			throw e;
		}
	}

	/**
	 * 保存系统设置信息
	 * @param report_S_Setting
	 * @return
	 * @throws Exception
	 */
	public Report_S_Setting save( Report_S_Setting report_S_Setting ) throws Exception {
		Report_S_Setting report_S_Setting_old = null;
		Business business = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			report_S_Setting_old = business.report_S_SettingFactory().getWithConfigCode(report_S_Setting.getConfigCode());
			if( report_S_Setting_old != null ){
				report_S_Setting.setId( report_S_Setting_old.getId() );
				return report_S_SettingService.update( emc, report_S_Setting );	
			}else{
				return report_S_SettingService.create( emc, report_S_Setting );	
			}
		} catch ( Exception e ) {
			throw e;
		}
	}

	/**
	 * 根据设置编码获取指定的设置信息
	 * @param code
	 * @return
	 * @throws Exception
	 */
	public Report_S_Setting getByCode( String code ) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			 return report_S_SettingService.getByCode( emc, code );	
		} catch ( Exception e ) {
			throw e;
		}
	}
	
	/**
	 * 根据编码获取指定的设置信息的具体配置值
	 * @param code
	 * @return
	 * @throws Exception
	 */
	public String getValueByCode( String code ) throws Exception {
		Report_S_Setting report_S_Setting = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			report_S_Setting = report_S_SettingService.getByCode( emc, code );
			if( report_S_Setting != null ) {
				if( !report_S_Setting.getIsLob() ) {
					return report_S_Setting.getConfigValue();
				}else {
					return getLobValueWithId( report_S_Setting.getId() );
				}
			}
		} catch ( Exception e ) {
			throw e;
		}
		return null;
	}

	public String getLobValueWithId( String id ) throws Exception {
		Report_S_SettingLobValue lobValue = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			lobValue = emc.find( id, Report_S_SettingLobValue.class );
			if( lobValue != null ) {
				return lobValue.getLobValue();
			}
		} catch ( Exception e ) {
			throw e;
		}
		return null;
	}
	
}

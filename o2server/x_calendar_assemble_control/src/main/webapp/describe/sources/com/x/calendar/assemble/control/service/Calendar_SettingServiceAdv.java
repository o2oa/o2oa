package com.x.calendar.assemble.control.service;

import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.calendar.assemble.control.Business;
import com.x.calendar.core.entity.Calendar_Setting;
import com.x.calendar.core.entity.Calendar_SettingLobValue;


/**
 * 日程管理系统设置服务类
 * @author O2LEE
 *
 */
public class Calendar_SettingServiceAdv{

	private Calendar_SettingService calendar_SettingService = new Calendar_SettingService();
	
	/**
	 * 获取所有的系统设置信息
	 * @return
	 * @throws Exception
	 */
	public List<Calendar_Setting> listAll() throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return calendar_SettingService.listAll( emc );	
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
	public Calendar_Setting get(String id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return calendar_SettingService.get( emc, id );	
		} catch ( Exception e ) {
			throw e;
		}
	}

	/**
	 * 保存系统设置信息
	 * @param calendar_Setting
	 * @return
	 * @throws Exception
	 */
	public Calendar_Setting save( Calendar_Setting calendar_Setting ) throws Exception {
		Calendar_Setting calendar_Setting_old = null;
		Business business = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			calendar_Setting_old = business.calendar_SettingFactory().getWithConfigCode(calendar_Setting.getConfigCode());
			if( calendar_Setting_old != null ){
				calendar_Setting.setId( calendar_Setting_old.getId() );
				return calendar_SettingService.update( emc, calendar_Setting );	
			}else{
				return calendar_SettingService.create( emc, calendar_Setting );	
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
	public Calendar_Setting getByCode( String code ) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			 return calendar_SettingService.getByCode( emc, code );	
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
		Calendar_Setting calendar_Setting = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			calendar_Setting = calendar_SettingService.getByCode( emc, code );
			if( calendar_Setting != null ) {
				if( !calendar_Setting.getIsLob() ) {
					return calendar_Setting.getConfigValue();
				}else {
					return getLobValueWithId( calendar_Setting.getId() );
				}
			}
		} catch ( Exception e ) {
			throw e;
		}
		return null;
	}

	/**
	 * 根据ID获取配置值信息
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public String getLobValueWithId( String id ) throws Exception {
		Calendar_SettingLobValue lobValue = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			lobValue = emc.find( id, Calendar_SettingLobValue.class );
			if( lobValue != null ) {
				return lobValue.getLobValue();
			}
		} catch ( Exception e ) {
			throw e;
		}
		return null;
	}
	
}

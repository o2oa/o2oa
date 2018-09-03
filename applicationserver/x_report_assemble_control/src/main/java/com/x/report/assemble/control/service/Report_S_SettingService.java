package com.x.report.assemble.control.service;

import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.report.assemble.control.Business;
import com.x.report.core.entity.Report_S_Setting;
import com.x.report.core.entity.Report_S_SettingLobValue;

/**
 * 汇报系统设置服务类
 * @author O2LEE
 *
 */
public class Report_S_SettingService {
	
	//private Logger logger = LoggerFactory.getLogger( Report_S_SettingService.class );

	/**
	 * 获取所有的系统设置信息
	 * @return
	 * @throws Exception
	 */
	public List<Report_S_Setting> listAll( EntityManagerContainer emc ) throws Exception {
		Business business =  new Business( emc );
		return business.report_S_SettingFactory().listAll();
	}
	
	/**
	 * 根据ID获取指定的系统设置信息
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public Report_S_Setting get( EntityManagerContainer emc, String id ) throws Exception {
		return emc.find(id, Report_S_Setting.class);
	}

	/**
	 * 根据ID删除指定的系统设置信息
	 * @param id
	 * @throws Exception
	 */
	public void delete( EntityManagerContainer emc, String id ) throws Exception {
		Report_S_Setting report_S_Setting = null;
		if( id == null || id.isEmpty() ){
			throw new Exception( "id is null, system can not delete any object." );
		}
		report_S_Setting = emc.find( id, Report_S_Setting.class );
		if ( null == report_S_Setting ) {
			throw new Exception( "object is not exist {'id':'"+ id +"'}" );
		}else{
			emc.beginTransaction( Report_S_Setting.class );
			emc.remove( report_S_Setting, CheckRemoveType.all );
			emc.commit();
		}
	}
	/**
	 * 根据设置编码获取指定的设置信息
	 * @param code
	 * @return
	 * @throws Exception
	 */
	public Report_S_Setting getByCode( EntityManagerContainer emc, String code ) throws Exception {
		List<Report_S_Setting> report_S_SettingList = null;
		Report_S_Setting report_S_Setting = null;
		List<String> ids = null;
		Business business = new Business( emc );
		if( code == null || code.isEmpty() ){
			throw new Exception( "code is null, system can not query any object." );
		}
		
		ids = business.report_S_SettingFactory().listIdsByCode(code);
		if( ids != null && !ids.isEmpty() ){
			report_S_SettingList =  business.report_S_SettingFactory().list( ids );
		}
		
		if( report_S_SettingList != null && !report_S_SettingList.isEmpty() ){
			for( int i=0; i< report_S_SettingList.size(); i++  ){
				if( i == 0 ){
					report_S_Setting = report_S_SettingList.get( i );
				}else{
					emc.beginTransaction( Report_S_Setting.class );
					emc.remove( report_S_SettingList.get( i ), CheckRemoveType.all );
					emc.commit();
				}
			}
		}
		return report_S_Setting;
	}

	/**
	 * 创建系统设置信息
	 * @param report_S_Setting
	 * @return
	 * @throws Exception
	 */
	public Report_S_Setting create( EntityManagerContainer emc, Report_S_Setting report_S_Setting ) throws Exception {
		Report_S_Setting report_S_Setting_old = null;
		Report_S_SettingLobValue reportSettingLobValue = null;
		
		report_S_Setting_old = emc.find( report_S_Setting.getId(), Report_S_Setting.class );
		if( report_S_Setting_old != null ){
			throw new Exception("report_S_Setting{'id':' "+ report_S_Setting.getId() +" '} exists, can not create new object");
		}else{
			emc.beginTransaction( Report_S_Setting.class );

			//对长文本记录进行操作
			if( report_S_Setting.getIsLob() ) {
				emc.beginTransaction( Report_S_SettingLobValue.class );
				reportSettingLobValue = emc.find( report_S_Setting.getId(), Report_S_SettingLobValue.class );
				if( reportSettingLobValue != null ) {
					reportSettingLobValue.setLobValue( report_S_Setting.getConfigValue() );//更新值
					emc.check( reportSettingLobValue, CheckPersistType.all );
				}else {
					//没有，就创建一个LOB值记录
					reportSettingLobValue = new Report_S_SettingLobValue();
					reportSettingLobValue.setId( report_S_Setting.getId() );
					reportSettingLobValue.setLobValue( report_S_Setting.getConfigValue() );
					emc.persist( reportSettingLobValue, CheckPersistType.all );
				}
				report_S_Setting.setConfigValue( "LobValue" );
			}
			
			emc.persist( report_S_Setting, CheckPersistType.all);
			emc.commit();
		}
		return report_S_Setting;
	}
	
	/**
	 * 更新系统设置信息
	 * @param report_S_Setting
	 * @return
	 * @throws Exception
	 */
	public Report_S_Setting update( EntityManagerContainer emc, Report_S_Setting report_S_Setting ) throws Exception {
		if( report_S_Setting == null ){
			throw new Exception("report_S_Setting is null, can not update object!");
		}
		Report_S_Setting report_S_Setting_old = null;
		Report_S_SettingLobValue reportSettingLobValue = null;
		Business business = new Business(emc);
		report_S_Setting_old = business.report_S_SettingFactory().getWithConfigCode(report_S_Setting.getConfigCode());
		if( report_S_Setting_old != null ){
			emc.beginTransaction( Report_S_Setting.class );
			report_S_Setting_old.setConfigCode( report_S_Setting.getConfigCode() );
			report_S_Setting_old.setConfigName( report_S_Setting.getConfigName() );
			report_S_Setting_old.setConfigValue( report_S_Setting.getConfigValue() );
			report_S_Setting_old.setOrderNumber( report_S_Setting.getOrderNumber() );
			report_S_Setting_old.setIsLob( report_S_Setting.getIsLob() );
			
			//对长文本记录进行操作
			if( report_S_Setting_old.getIsLob() ) {
				emc.beginTransaction( Report_S_SettingLobValue.class );
				reportSettingLobValue = emc.find( report_S_Setting.getId(), Report_S_SettingLobValue.class );
				if( reportSettingLobValue != null ) {
					reportSettingLobValue.setLobValue( report_S_Setting.getConfigValue() );//更新值
					emc.check( reportSettingLobValue, CheckPersistType.all );
				}else {
					//没有，就创建一个LOB值记录
					reportSettingLobValue = new Report_S_SettingLobValue();
					reportSettingLobValue.setId( report_S_Setting.getId() );
					reportSettingLobValue.setLobValue( report_S_Setting.getConfigValue() );
					emc.persist( reportSettingLobValue, CheckPersistType.all );
				}
				report_S_Setting_old.setConfigValue( "LobValue" );
			}
			emc.check( report_S_Setting_old, CheckPersistType.all);
			emc.commit();
		}else{
			throw new Exception("old object report_S_Setting{'id':' "+ report_S_Setting.getId() +" '} is not exists. ");
		}
		
		return report_S_Setting_old;
	}

}

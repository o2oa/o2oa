package com.x.bbs.assemble.control.service;

import java.util.List;
import java.util.Optional;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.cache.Cache;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.bbs.assemble.control.Business;
import com.x.bbs.entity.BBSConfigSetting;



/**
 * 平台配置
 * @author sword
 */
public class BBSConfigSettingService{

	private static  Logger logger = LoggerFactory.getLogger( BBSConfigSettingService.class );

	private Cache.CacheCategory cacheCategory = new Cache.CacheCategory(BBSConfigSetting.class);

	/**
	 * 根据传入的ID从数据库查询BBSConfigSetting对象
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public BBSConfigSetting get( String id ) throws Exception {
		if( id  == null || id.isEmpty() ){
			throw new Exception( "id is null, return null!" );
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			return emc.find( id, BBSConfigSetting.class );
		}catch( Exception e ){
			throw e;
		}
	}

	/**
	 * 向数据库保存BBSConfigSetting对象
	 * @param bbsConfigSetting
	 */
	public BBSConfigSetting save( BBSConfigSetting bbsConfigSetting ) throws Exception {
		BBSConfigSetting _bbsConfigSetting = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			_bbsConfigSetting =  emc.find( bbsConfigSetting.getId(), BBSConfigSetting.class );
			if( _bbsConfigSetting != null ){
				emc.beginTransaction( BBSConfigSetting.class );
				bbsConfigSetting.copyTo( _bbsConfigSetting, JpaObject.FieldsUnmodify  );
				emc.check( _bbsConfigSetting, CheckPersistType.all );
				emc.commit();
			}else{
				emc.beginTransaction( BBSConfigSetting.class );
				emc.persist( bbsConfigSetting, CheckPersistType.all);
				emc.commit();
			}
			CacheManager.notify(BBSConfigSetting.class);
		}catch( Exception e ){
			logger.warn( "BBSConfigSetting update/ got a error!" );
			throw e;
		}
		return bbsConfigSetting;
	}

	/**
	 * 向数据库保存BBSConfigSetting对象
	 * @param bbsConfigSetting
	 */
	public BBSConfigSetting update( BBSConfigSetting bbsConfigSetting ) throws Exception {
		BBSConfigSetting _bbsConfigSetting = null;
		Business business = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business( emc );
			_bbsConfigSetting =  business.configSettingFactory().getWithConfigCode( bbsConfigSetting.getConfigCode() );
			if( _bbsConfigSetting != null ){
				emc.beginTransaction( BBSConfigSetting.class );
				bbsConfigSetting.copyTo( _bbsConfigSetting, JpaObject.FieldsUnmodify  );
				emc.check( _bbsConfigSetting, CheckPersistType.all );
				emc.commit();
				CacheManager.notify(BBSConfigSetting.class);
			}else{
				throw new Exception("config setting '"+ bbsConfigSetting.getConfigCode() +"'  not exists");
			}
		}catch( Exception e ){
			logger.warn( "BBSConfigSetting update/ got a error!" );
			throw e;
		}
		return bbsConfigSetting;
	}

	/**
	 * 根据ID从数据库中删除BBSConfigSetting对象
	 * @param id
	 * @throws Exception
	 */
	public void delete( String id ) throws Exception {
		BBSConfigSetting bbsConfigSetting = null;
		if( id == null || id.isEmpty() ){
			throw new Exception( "id is null, system can not delete any object." );
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			//先判断需要操作的应用信息是否存在，根据ID进行一次查询，如果不存在不允许继续操作
			bbsConfigSetting = emc.find(id, BBSConfigSetting.class);
			if (null == bbsConfigSetting) {
				logger.warn( "object is not exist {'id':'"+ id +"'}" );
			}else{
				emc.beginTransaction( BBSConfigSetting.class );
				emc.remove( bbsConfigSetting, CheckRemoveType.all );
				emc.commit();
				CacheManager.notify(BBSConfigSetting.class);
			}
		} catch ( Exception e ) {
			throw e;
		}
	}

	/**
	 * 根据传入的ID从数据库查询BBSConfigSetting对象
	 * @param configCode
	 * @return
	 * @throws Exception
	 */
	public String getValueWithConfigCode( String configCode ) throws Exception {
		if( configCode  == null || configCode.isEmpty() ){
			throw new Exception( "configCode is null!" );
		}
		Cache.CacheKey cacheKey = new Cache.CacheKey(BBSConfigSetting.class.getName(), configCode);
		Optional<?> optional = CacheManager.get(cacheCategory, cacheKey);
		if (optional.isPresent()) {
			return String.valueOf(optional.get());
		} else {
			try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
				Business business = new Business(emc);
				String result =  business.configSettingFactory().getValueWithConfigCode( configCode );
				if(result != null){
					CacheManager.put(cacheCategory, cacheKey, result);
				}
				return result;
			}catch( Exception e ){
				throw e;
			}
		}
	}

	/**
	 * 论坛是否启用昵称，默认不启用
	 */
	public boolean useNickName() {
		try {
			String configValue = this.getValueWithConfigCode(BBSConfigSetting.BBS_CONFIG_USE_NICKNAME);
			if(BBSConfigSetting.BBS_CONFIG_USE_NICKNAME_YES.equalsIgnoreCase(configValue)){
				return true;
			}
		} catch (Exception e) {
			logger.debug(e.getMessage());
		}
		return false;
	}

	/**
	 * 根据传入的ID从数据库查询BBSConfigSetting对象
	 * @param configCode
	 * @return
	 * @throws Exception
	 */
	public BBSConfigSetting getWithConfigCode( String configCode ) throws Exception {
		if( configCode  == null || configCode.isEmpty() ){
			throw new Exception( "configCode is null!" );
		}
		Business business = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			business = new Business(emc);
			return business.configSettingFactory().getWithConfigCode(configCode);
		}catch( Exception e ){
			throw e;
		}
	}

	public List<BBSConfigSetting> listAll() throws Exception {
		Business business = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			business = new Business(emc);
			return business.configSettingFactory().listAll();
		}catch( Exception e ){
			throw e;
		}
	}

	/**
	 * 检查配置项是否存在，如果不存在根据信息创建一个新的配置项
	 * @param configCode
	 * @param configName
	 * @param configValue
	 * @param description
	 * @throws Exception
	 */
	public void checkAndInitSystemConfig( String configCode, String configName, String configValue, String description, String type, String selectContent, Boolean isMultiple, Integer orderNumber ) throws Exception {
		if( configCode  == null || configCode.isEmpty() ){
			throw new Exception( "configCode is null!" );
		}
		if( configName  == null || configName.isEmpty() ){
			throw new Exception( "configName is null!" );
		}
		Business business = null;
		BBSConfigSetting bbsConfigSetting = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			business = new Business(emc);
			bbsConfigSetting = business.configSettingFactory().getWithConfigCode( configCode );
		}catch( Exception e ){
			logger.warn( "system find system config{'configCode':'"+configCode+"'} got an exception. " );
			throw e;
		}
		//如果配置不存在，则新建一个配置记录
		if( bbsConfigSetting == null ){
			bbsConfigSetting = new BBSConfigSetting();
			bbsConfigSetting.setConfigCode( configCode );
			bbsConfigSetting.setConfigName( configName );
			bbsConfigSetting.setConfigValue( configValue );
			bbsConfigSetting.setDescription( description );
			bbsConfigSetting.setOrderNumber( orderNumber );
			bbsConfigSetting.setValueType( type );
			bbsConfigSetting.setSelectContent( selectContent );
			bbsConfigSetting.setIsMultiple( isMultiple );
			try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
				emc.beginTransaction( BBSConfigSetting.class );
				emc.persist( bbsConfigSetting, CheckPersistType.all );
				emc.commit();
			}catch( Exception e ){
				logger.warn("system persist new system config{'configCode':'"+configCode+"'} got an exception. " );
				throw e;
			}
		}else{
			try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
				bbsConfigSetting = emc.find( bbsConfigSetting.getId(), BBSConfigSetting.class );
				emc.beginTransaction( BBSConfigSetting.class );
				if( !configName.equals( bbsConfigSetting.getConfigName() ) ){
					bbsConfigSetting.setConfigName( configName );
				}
				if( orderNumber != bbsConfigSetting.getOrderNumber() ){
					bbsConfigSetting.setOrderNumber(orderNumber);
				}
				if( description != null ){
					bbsConfigSetting.setDescription( description );
				}
				if( type != null ){
					bbsConfigSetting.setValueType( type );
				}
				if( selectContent != null ){
					bbsConfigSetting.setSelectContent( selectContent );
				}
				if( isMultiple != null ){
					bbsConfigSetting.setIsMultiple( isMultiple );
				}
				emc.check( bbsConfigSetting, CheckPersistType.all );
				emc.commit();
			}catch( Exception e ){
				logger.warn("system update system config{'configCode':'"+configCode+"'} got an exception. "  );
				throw e;
			}
		}
	}

	/**
	 *  BBS_LOGO_NAME	论坛系统名称	ADMIN_AND_ALLLEADER
	 */
	public void initAllSystemConfig() {
		String value = null, description = null, type = null, selectContent = null;
		Boolean isMultiple = false;
		Integer ordernumber = 0;

		value = "企业论坛";
		type = "text";
		selectContent = null;
		isMultiple = false;
		description = "论坛系统名称:可以为系统指定名称,比如以顶层组织名称作为论坛名称等等。";
		try {
			checkAndInitSystemConfig("BBS_LOGO_NAME", "论坛系统名称", value, description, type, selectContent, isMultiple,  ++ordernumber );
		} catch (Exception e) {
			logger.warn( "system init system config 'BBS_LOGO_NAME' got an exception." );
			logger.error(e);
		}

		value = " - O2OA办公软件管理系统";
		type = "text";
		selectContent = null;
		isMultiple = false;
		description = "论坛标题Tail:全站网页标题内容的后缀内容。可以为空。";
		try {
			checkAndInitSystemConfig("BBS_TITLE_TAIL", "论坛标题Tail", value, description, type, selectContent, isMultiple,  ++ordernumber );
		} catch (Exception e) {
			logger.warn( "system init system config 'BBS_TITLE_TAIL' got an exception." );
			logger.error(e);
		}

		value = "信息|问题|投票";
		type = "select";
		selectContent = "信息|问题|投票";
		isMultiple = true;
		description = "主题类别：可选值：信息|问题|投票,多选。";
		try {
			checkAndInitSystemConfig("BBS_SUBJECT_TYPECATAGORY", "主题类别", value, description, type, selectContent, isMultiple, ++ordernumber );
		} catch (Exception e) {
			logger.warn( "system init system config 'BBS_SUBJECT_TYPECATAGORY' got an exception." );
			logger.error(e);
		}

		value = "Recursively";
		type = "select";
		selectContent = "Single|Recursively";
		isMultiple = false;
		description = "回复删除模式：可选值：Single|Recursively,单选。";
		try {
			checkAndInitSystemConfig("BBS_REPLY_DELETETYPE", "回复删除模式", value, description, type, selectContent, isMultiple, ++ordernumber );
		} catch (Exception e) {
			logger.warn( "system init system config 'BBS_REPLY_DELETETYPE' got an exception." );
			logger.error(e);
		}

		value = "ASC";
		type = "select";
		selectContent = "ASC|DESC";
		isMultiple = false;
		description = "回贴排序模式：可选值：ASC|DESC, 按创建时间正序|倒序，单选。";
		try {
			checkAndInitSystemConfig("BBS_REPLY_SORTTYPE", "回贴排序模式", value, description, type, selectContent, isMultiple, ++ordernumber );
		} catch (Exception e) {
			logger.warn( "system init system config 'BBS_REPLY_SORTTYPE' got an exception." );
			logger.error(e);
		}

		value = "DESC";
		type = "select";
		selectContent = "ASC|DESC";
		isMultiple = false;
		description = "我的回贴列表排序模式：可选值：ASC|DESC, 按创建时间正序|倒序，单选。";
		try {
			checkAndInitSystemConfig("BBS_MYREPLY_SORTTYPE", "我的回贴排序模式", value, description, type, selectContent, isMultiple, ++ordernumber );
		} catch (Exception e) {
			logger.warn( "system init system config 'BBS_MYREPLY_SORTTYPE' got an exception." );
			logger.error(e);
		}

		value = "YES";
		type = "select";
		selectContent = "YES|NO";
		isMultiple = false;
		description = "是否允许匿名访问资源：可选值：YES|NO（允许|不允许），单选。";
		try {
			checkAndInitSystemConfig("BBS_ANONYMOUS_PERMISSION", "是否允许匿名访问", value, description, type, selectContent, isMultiple, ++ordernumber );
		} catch (Exception e) {
			logger.warn( "system init system config 'BBS_ANONYMOUS_PERMISSION' got an exception." );
			logger.error(e);
		}

		value = "YES";
		type = "select";
		selectContent = "YES|NO";
		isMultiple = false;
		description = "是否启用昵称：可选值：YES|NO（允许|不允许），单选。";
		try {
			checkAndInitSystemConfig("BBS_USE_NICKNAME", "是否启用昵称", value, description, type, selectContent, isMultiple, ++ordernumber );
		} catch (Exception e) {
			logger.warn( "system init system config 'BBS_USE_NICKNAME' got an exception." );
			logger.error(e);
		}
	}
}

package com.x.okr.assemble.control.service;

import java.util.List;

import com.google.gson.Gson;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.gson.XGsonBuilder;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.okr.assemble.control.Business;
import com.x.okr.assemble.control.OkrUserCache;
import com.x.okr.entity.OkrUserInfo;

/**
 * 类   名：OkrUserInfoService<br/>
 * 实体类：OkrUserInfo<br/>
 * 作   者：Liyi<br/>
 * 单   位：O2 Team<br/>
 * 日   期：2016-05-20 17:17:26
**/
public class OkrUserInfoService{
	
	private Logger logger = LoggerFactory.getLogger( OkrUserInfoService.class );
	/**
	 * 根据传入的ID从数据库查询OkrUserInfo对象
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public OkrUserInfo get( String id ) throws Exception {
		if( id  == null || id.isEmpty() ){
			throw new Exception( "id is null, return null!" );
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			return emc.find( id, OkrUserInfo.class );
		}catch( Exception e ){
			throw e;
		}
	}
	
	/**
	 * 根据传入的ID从数据库查询OkrUserInfo对象
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public OkrUserInfo getWithPersonName( String name ) throws Exception {
		if( name  == null || name.isEmpty() ){
			throw new Exception( "name is null, return null!" );
		}
		Business business = null;
		List<OkrUserInfo> okrUserInfoList = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			business = new Business(emc);
			okrUserInfoList = business.okrUserInfoFactory().listWithPerson( name );
			if( okrUserInfoList != null && !okrUserInfoList.isEmpty() ){
				return okrUserInfoList.get(0);
			}else{
				return null;
			}
		}catch( Exception e ){
			throw e;
		}
	}
	
	/**
	 * 向数据库保存OkrUserInfo对象
	 * @param wrapIn
	 */
	public OkrUserInfo save( OkrUserInfo userInfo ) throws Exception {
		List<OkrUserInfo> okrUserInfoList = null;
		OkrUserInfo okrUserInfo = null;
		Business business = null;
		if( userInfo.getUserName() !=null && !userInfo.getUserName().isEmpty() ){
			//根据用户姓名查询信息是否存在，如果存在就update，如果不存在就create
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				business = new Business(emc);
				okrUserInfoList = business.okrUserInfoFactory().listWithPerson( userInfo.getUserName() );
				emc.beginTransaction( OkrUserInfo.class );
				if( okrUserInfoList == null || okrUserInfoList.isEmpty() ){
					userInfo.setId( OkrUserInfo.createId() );
					emc.persist( userInfo, CheckPersistType.all );	
				}else{
					for( int i=0;i<okrUserInfoList.size();i++ ){
						okrUserInfo = okrUserInfoList.get(i);
						if( i == 0 ){
							okrUserInfo.setUserName( userInfo.getUserName() );
							okrUserInfo.setCustomContent( userInfo.getCustomContent() );
							emc.check( okrUserInfo, CheckPersistType.all);	
						}else{
							emc.remove( okrUserInfo, CheckRemoveType.all );
						}
					}
					emc.commit();
				}
				emc.commit();
			}catch( Exception e ){
				logger.warn( "OkrUserInfo update/ got a error!" );
				throw e;
			}
		}
		return okrUserInfo;
	}
	
	/**
	 * 根据ID从数据库中删除OkrUserInfo对象
	 * @param id
	 * @throws Exception
	 */
	public void delete( String id ) throws Exception {
		OkrUserInfo okrUserInfo = null;
		if( id == null || id.isEmpty() ){
			throw new Exception( "id is null, system can not delete any object." );
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			//先判断需要操作的应用信息是否存在，根据ID进行一次查询，如果不存在不允许继续操作
			okrUserInfo = emc.find(id, OkrUserInfo.class);
			if (null == okrUserInfo) {
				throw new Exception( "object is not exist {'id':'"+ id +"'}" );
			}else{
				emc.beginTransaction( OkrUserInfo.class );
				emc.remove( okrUserInfo, CheckRemoveType.all );
				emc.commit();
			}
		} catch ( Exception e ) {
			throw e;
		}
	}

	public void deleteWithPersonName( String name ) throws Exception {
		if( name  == null || name.isEmpty() ){
			throw new Exception( "name is null, return null!" );
		}
		Business business = null;
		List<OkrUserInfo> okrUserInfoList = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			business = new Business(emc);
			okrUserInfoList = business.okrUserInfoFactory().listWithPerson( name );
			if( okrUserInfoList != null && !okrUserInfoList.isEmpty() ){
				for( OkrUserInfo userInfo : okrUserInfoList ){
					emc.beginTransaction( OkrUserInfo.class );
					emc.remove( userInfo, CheckRemoveType.all );
					emc.commit();
				}
			}
		}catch( Exception e ){
			throw e;
		}
	}
	
	/**
	 * 获取用户登录的代理身份
	 * 
	 * @param name
	 * @throws Exception 
	 */
	public OkrUserCache getOkrUserCacheWithPersonName(String name) throws Exception {
		if( name  == null || name.isEmpty() ){
			throw new Exception( "name is null, return null!" );
		}
		Gson gson = XGsonBuilder.instance();
		OkrUserInfo  okrUserInfo  = getWithPersonName( name );
		if( okrUserInfo !=null && okrUserInfo.getCustomContent() != null && !okrUserInfo.getCustomContent().isEmpty() ){
			return gson.fromJson( okrUserInfo.getCustomContent(), OkrUserCache.class );
		}else{
			//logger.error( "getWithPersonName can not find okr user info:" + name );
			return null;
		}
	}
	
	/**
	 * 获取用户登录的代理身份
	 * 
	 * @param name
	 * @throws Exception 
	 */
	public String getLoginIdentity(String name) throws Exception {
		OkrUserCache okrUserCache = getOkrUserCacheWithPersonName( name );
		if( okrUserCache != null ){
			return okrUserCache.getLoginIdentityName();
		}else{
			return null;
		}
	}

	/**
	 * 获取用户登录的代理员工姓名
	 * 
	 * @param name
	 * @throws Exception 
	 */
	public String getLoginUserName(String name) throws Exception {
		OkrUserCache okrUserCache = getOkrUserCacheWithPersonName( name );
		if( okrUserCache != null ){
			return okrUserCache.getLoginUserName();
		}else{
			return null;
		}
	}

	/**
	 * 获取用户登录的代理员工所属组织名称
	 * 
	 * @param name
	 * @throws Exception 
	 */
	public String getLoginUserOrganizationName(String name) throws Exception {
		OkrUserCache okrUserCache = getOkrUserCacheWithPersonName( name );
		if( okrUserCache != null ){
			return okrUserCache.getLoginUserOrganizationName();
		}else{
			return null;
		}
	}

	/**
	 * 获取用户登录的代理员工所属公司名称
	 * 
	 * @param name
	 * @throws Exception 
	 */
	public String getLoginUserCompanyName(String name) throws Exception {
		OkrUserCache okrUserCache = getOkrUserCacheWithPersonName( name );
		if( okrUserCache != null ){
			return okrUserCache.getLoginUserCompanyName();
		}else{
			return null;
		}
	}
}

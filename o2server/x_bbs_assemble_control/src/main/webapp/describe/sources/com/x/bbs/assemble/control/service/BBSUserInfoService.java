package com.x.bbs.assemble.control.service;

import java.util.Date;
import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.bbs.assemble.control.Business;
import com.x.bbs.entity.BBSUserInfo;

/**
 * 用户信息管理服务类
 * @author LIYI
 *
 */
public class BBSUserInfoService {
	
	private static  Logger logger = LoggerFactory.getLogger( BBSUserInfoService.class );
	
	/**
	 * 根据传入的ID从数据库查询BBSUserInfo对象
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public BBSUserInfo get( String id ) throws Exception {
		if( id  == null || id.isEmpty() ){
			throw new Exception( "id is null, return null!" );
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			return emc.find( id, BBSUserInfo.class );
		}catch( Exception e ){
			throw e;
		}
	}
	
	/**
	 * 根据传入的用户姓名从数据库查询BBSUserInfo对象
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public BBSUserInfo getByUserName( String userName ) throws Exception {
		if( userName  == null || userName.isEmpty() ){
			throw new Exception( "userName is null, return null!" );
		}
		Business business = null;
		List<BBSUserInfo> userInfoList = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			business = new Business( emc );
			userInfoList = business.userInfoFactory().listByUserName( userName );
			if( ListTools.isNotEmpty( userInfoList ) ){
				return userInfoList.get( 0 );
			}else{
				return null;
			}
		}catch( Exception e ){
			throw e;
		}
	}
	
	/**
	 * 向数据库保存BBSUserInfo对象
	 * @param wrapIn
	 */
	public BBSUserInfo save( BBSUserInfo _bBSUserInfo ) throws Exception {
		if( _bBSUserInfo.getId() == null ){
			_bBSUserInfo.setId( BBSUserInfo.createId() );
		}
		if( _bBSUserInfo.getUserName() == null ){
			throw new Exception( "username is null!" );
		}
		if( _bBSUserInfo.getPermissionContent() == null || _bBSUserInfo.getPermissionContent().isEmpty() ){
			_bBSUserInfo.setPermissionContent( "{}" );
		}
		Business business = null;
		BBSUserInfo userInfo = null;
		List<BBSUserInfo> userInfoList = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			userInfoList = business.userInfoFactory().listByUserName( _bBSUserInfo.getUserName() );
			emc.beginTransaction( BBSUserInfo.class );
			if( userInfoList == null || userInfoList.isEmpty() ){
				logger.info( "userInfoList is null, persist new user info......" );
				emc.persist( _bBSUserInfo, CheckPersistType.all);	
			}else{
				logger.info( "userInfoList is not null, update user info......" );
				for( int i=0; i< userInfoList.size(); i++  ){
					userInfo = userInfoList.get( i );
					if( i == 0 ){
						_bBSUserInfo.copyTo( userInfo, JpaObject.FieldsUnmodify  );//第一条更新，其他删除
						emc.check( userInfo, CheckPersistType.all );
					}else{
						logger.info( "user info more than 1, remove one......" );
						emc.remove( userInfo, CheckRemoveType.all );
					}
				}
			}
			emc.commit();		
		}catch( Exception e ){
			logger.warn( "system find BBSUserInfo{'id':'"+_bBSUserInfo.getId()+"'} got an exception!" );
			throw e;
		}
		return _bBSUserInfo;
	}
	
	/**
	 * 向数据库保存BBSUserInfo对象
	 * @param wrapIn
	 */
	public BBSUserInfo updatePermissionAndVisit( BBSUserInfo _bBSUserInfo ) throws Exception {
		if( _bBSUserInfo.getId() == null ){
			_bBSUserInfo.setId( BBSUserInfo.createId() );
		}
		if( _bBSUserInfo.getUserName() == null ){
			throw new Exception( "username is null!" );
		}
		if( _bBSUserInfo.getPermissionContent() == null || _bBSUserInfo.getPermissionContent().isEmpty() ){
			_bBSUserInfo.setPermissionContent( "{}" );
		}
		Business business = null;
		BBSUserInfo userInfo = null;
		List<BBSUserInfo> userInfoList = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			userInfoList = business.userInfoFactory().listByUserName( _bBSUserInfo.getUserName() );
			emc.beginTransaction( BBSUserInfo.class );
			if( userInfoList == null || userInfoList.isEmpty() ){
				_bBSUserInfo.setOnline( true );
				_bBSUserInfo.setLastVisitTime( new Date() );
				emc.persist( _bBSUserInfo, CheckPersistType.all );	
			}else{
				for( int i=0; i< userInfoList.size(); i++  ){
					userInfo = userInfoList.get( i );
					if( i == 0 ){
						userInfo.setOnline( true );
						userInfo.setLastVisitTime( new Date() );
						userInfo.setPermissionContent( _bBSUserInfo.getPermissionContent() );//第一条更新，其他删除
						emc.check( userInfo, CheckPersistType.all );
					}else{
						emc.remove( userInfo, CheckRemoveType.all );
					}
				}
			}
			emc.commit();		
		}catch( Exception e ){
			logger.warn( "system find BBSUserInfo{'id':'"+_bBSUserInfo.getId()+"'} got an exception!" );
			throw e;
		}
		return _bBSUserInfo;
	}
	
	/**
	 * 向数据库保存BBSUserInfo对象
	 * @param wrapIn
	 */
	public Boolean updatePermission( String userName, String permissionContent ) throws Exception {
		if( userName == null ){
			throw new Exception( "username is null!" );
		}
		if( permissionContent == null || permissionContent.isEmpty() ){
			permissionContent = "{}";
		}
		Business business = null;
		BBSUserInfo userInfo = null;
		List<BBSUserInfo> userInfoList = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			userInfoList = business.userInfoFactory().listByUserName( userName );
			emc.beginTransaction( BBSUserInfo.class );
			if( ListTools.isNotEmpty( userInfoList ) ){
				for( int i=0; i< userInfoList.size(); i++  ){
					userInfo = userInfoList.get( i );
					if( i == 0 ){
						userInfo.setPermissionContent( permissionContent );
						emc.check( userInfo, CheckPersistType.all );
					}else{
						emc.remove( userInfo, CheckRemoveType.all );
					}
				}
			}
			emc.commit();
			return true;
		}catch( Exception e ){
			throw e;
		}
	}
	
	/**
	 * 根据ID从数据库中删除BBSUserInfo对象
	 * @param id
	 * @throws Exception
	 */
	public void delete( String id ) throws Exception {
		BBSUserInfo bBSUserInfo = null;
		if( id == null || id.isEmpty() ){
			throw new Exception( "id is null, system can not delete any object." );
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			//先判断需要操作的应用信息是否存在，根据ID进行一次查询，如果不存在不允许继续操作
			bBSUserInfo = emc.find( id, BBSUserInfo.class );
			emc.beginTransaction( BBSUserInfo.class );
			if ( null == bBSUserInfo ) {
				throw new Exception( "object is not exist {'id':'"+ id +"'}" );
			}else{
				emc.remove( bBSUserInfo, CheckRemoveType.all );	
			}
			emc.commit();
		} catch ( Exception e ) {
			throw e;
		}
	}
	
	/**
	 * 根据userName从数据库中删除BBSUserInfo对象
	 * @param userName
	 * @throws Exception
	 */
	public void deleteByUserName( String userName ) throws Exception {
		List<BBSUserInfo> bBSUserInfoList = null;
		Business business = null;
		if( userName == null || userName.isEmpty() ){
			throw new Exception( "userName is null, system can not delete any object." );
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business( emc );
			//先判断需要操作的应用信息是否存在，根据ID进行一次查询，如果不存在不允许继续操作
			bBSUserInfoList = business.userInfoFactory().listByUserName(userName);
			if( ListTools.isNotEmpty( bBSUserInfoList ) ){
				emc.beginTransaction( BBSUserInfo.class );
				for( BBSUserInfo userInfo : bBSUserInfoList ){
					emc.remove( userInfo, CheckRemoveType.all );	
				}
				emc.commit();
			}
		} catch ( Exception e ) {
			throw e;
		}
	}

	public void save(String userName, Long subjectCount, Long replyCount, Long subjectCountToday, Long replyCountToday,
			Long creamCount, Long originalCount, Long fansCount, Long popularity, Long credit,
			String permissionContent ) throws Exception {
		if( userName == null ){
			throw new Exception( "userName is null, system can not save any object." );
		}
		List<BBSUserInfo> userInfoList = null;
		BBSUserInfo userInfo = null;
		Business business = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			business = new Business(emc);
			userInfoList = business.userInfoFactory().listByUserName( userName );
			emc.beginTransaction( BBSUserInfo.class );
			if( ListTools.isNotEmpty( userInfoList ) ){
				for( int i=0; i< userInfoList.size(); i++  ){
					userInfo = userInfoList.get(i);
					if( i == 0 ){
						userInfo.setSubjectCount( subjectCount );
						userInfo.setReplyCount(replyCount);
						userInfo.setSubjectCountToday( subjectCountToday );
						userInfo.setReplyCountToday(replyCountToday);
						userInfo.setCreamCount(creamCount);
						userInfo.setOriginalCount(originalCount);
						userInfo.setFansCount(fansCount);
						if( permissionContent == null || permissionContent.isEmpty() ){
							permissionContent = "{}";
						}
						userInfo.setPermissionContent( permissionContent );
						userInfo.setPopularity(popularity);
						userInfo.setCredit( credit );
						if( userInfo.getLastVisitTime() == null ) {
							userInfo.setLastVisitTime( userInfo.getUpdateTime() );
						}
						emc.check( userInfo, CheckPersistType.all );
					}else{
						emc.remove( userInfo, CheckRemoveType.all );
					}
				}
			}else{
				permissionContent = "{}";
				userInfo = new BBSUserInfo();
				userInfo.setUserName( userName );
				userInfo.setNickName( userName );
				userInfo.setMobile("");
				userInfo.setSubjectCount( subjectCount );
				userInfo.setReplyCount(replyCount);
				userInfo.setSubjectCountToday( subjectCountToday );
				userInfo.setReplyCountToday(replyCountToday);
				userInfo.setCreamCount(creamCount);
				userInfo.setOriginalCount(originalCount);
				userInfo.setFansCount(fansCount);
				userInfo.setPermissionContent( permissionContent );
				userInfo.setPopularity(popularity);
				userInfo.setCredit( credit );
				emc.persist( userInfo, CheckPersistType.all );
			}
			emc.commit();
		}catch( Exception e ){
			throw e;
		}
	}

	public void logout( String userName ) throws Exception {
		List<BBSUserInfo> userInfoList = null;
		BBSUserInfo userInfo = null;
		Business business = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			business = new Business(emc);
			userInfoList = business.userInfoFactory().listByUserName( userName );
			emc.beginTransaction( BBSUserInfo.class );
			
			if( ListTools.isNotEmpty( userInfoList ) ){
				for( int i=0; i< userInfoList.size(); i++  ){
					userInfo = userInfoList.get(i);
					if( i == 0 ){
						userInfo.setOnline( false );
						emc.check( userInfo, CheckPersistType.all );
					}else{//删除一个以上的同名用户信息
						emc.remove( userInfo, CheckRemoveType.all );
					}
				}
			}
			emc.commit();
		}catch( Exception e ){
			throw e;
		}
	}
}
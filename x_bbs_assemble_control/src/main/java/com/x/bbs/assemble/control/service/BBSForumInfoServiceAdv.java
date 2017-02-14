package com.x.bbs.assemble.control.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.bbs.entity.BBSForumInfo;

/**
 * 论坛信息管理服务类
 * @author LIYI
 *
 */
public class BBSForumInfoServiceAdv {
	
	private Logger logger = LoggerFactory.getLogger( BBSForumInfoServiceAdv.class );
	private BBSForumInfoService forumInfoService = new BBSForumInfoService();
	private BBSSectionInfoService sectionInfoService = new BBSSectionInfoService();
	
	/**
	 * 根据传入的ID从数据库查询BBSForumInfo对象
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public BBSForumInfo get( String id ) throws Exception {
		if( id  == null || id.isEmpty() ){
			throw new Exception( "id is null, return null!" );
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			return emc.find( id, BBSForumInfo.class );
		}catch( Exception e ){
			throw e;
		}
	}
	
	/**
	 * 向数据库保存BBSForumInfo对象
	 * @param wrapIn
	 */
	public BBSForumInfo save( BBSForumInfo _bBSForumInfo ) throws Exception {
		if( _bBSForumInfo == null ){
			throw new Exception( "bBSForumInfo can not null!" );
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return forumInfoService.save( emc, _bBSForumInfo );
		}catch( Exception e ){
			throw e;
		}
	}
	
	/**
	 * 根据ID从数据库中删除BBSForumInfo对象
	 * @param id
	 * @throws Exception
	 */
	public void delete( String forumId ) throws Exception {
		if( forumId == null || forumId.isEmpty() ){
			throw new Exception( "forumId can not null!" );
		}
		List<String> sectionIds = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			sectionIds = sectionInfoService.listByForumId( emc, forumId );
			if( sectionIds != null && !sectionIds.isEmpty() ){
				for( String sectionId : sectionIds ){
					sectionInfoService.delete( emc, sectionId );
				}
			}
			forumInfoService.delete( emc, forumId );
			emc.commit();
		}catch( Exception e ){
			throw e;
		}
	}

	public List<BBSForumInfo> listAll() throws Exception {
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			return forumInfoService.listAll( emc );
		}catch( Exception e ){
			throw e;
		}
	}

	/**
	 * 根据权限查询所有我能访问到的论坛信息列表
	 * 1、所有全员可访问的论坛信息
	 * 2、所有我有权限访问的论坛信息
	 * 
	 * @return
	 * @throws Exception 
	 */
	public List<BBSForumInfo> listAllViewAbleForumWithUserPermission( List<String> viewAbleForumIds ) throws Exception {
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			return forumInfoService.listAllViewAbleForumWithUserPermission( emc, viewAbleForumIds );
		}catch( Exception e ){
			throw e;
		}
	}

	public void checkForumManager( BBSForumInfo forumInfo ) throws Exception {
		if( forumInfo == null ){
			throw new Exception( "forumInfo can not null!" );
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			forumInfoService.checkForumManager( emc, forumInfo );
		}catch( Exception e ){
			throw e;
		}
	}

	public void deleteForumManager( String forumId ) throws Exception {
		if( forumId == null || forumId.isEmpty() ){
			throw new Exception( "forumId can not null!" );
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			forumInfoService.deleteForumManager( emc, forumId );
		}catch( Exception e ){
			throw e;
		}
	}

	public List<String> listAllPublicForumIds() throws Exception {
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			return forumInfoService.listAllPublicForumIds( emc );
		}catch( Exception e ){
			throw e;
		}
	}
}
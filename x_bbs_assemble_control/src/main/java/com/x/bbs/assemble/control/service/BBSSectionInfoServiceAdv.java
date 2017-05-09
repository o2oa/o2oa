package com.x.bbs.assemble.control.service;

import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.bbs.entity.BBSSectionInfo;

/**
 * 论坛信息管理服务类
 * @author LIYI
 *
 */
public class BBSSectionInfoServiceAdv {
	
	private Logger logger = LoggerFactory.getLogger( BBSSectionInfoServiceAdv.class );
	private BBSSectionInfoService sectionInfoService = new BBSSectionInfoService();
	private BBSForumInfoService forumInfoService = new BBSForumInfoService();
	
	/**
	 * 根据传入的ID从数据库查询BBSSectionInfo对象
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public BBSSectionInfo get( String id ) throws Exception {
		if( id  == null || id.isEmpty() ){
			throw new Exception( "id is null!" );
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			return emc.find( id, BBSSectionInfo.class );
		}catch( Exception e ){
			throw e;
		}
	}
	
	/**
	 * 向数据库保存BBSSectionInfo对象
	 * @param wrapIn
	 */
	public BBSSectionInfo save( BBSSectionInfo _bBSSectionInfo ) throws Exception {
		if( _bBSSectionInfo  == null ){
			throw new Exception( "_bBSSectionInfo is null!" );
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			return sectionInfoService.save( emc, _bBSSectionInfo);
		}catch( Exception e ){
			throw e;
		}
	}
	
	/**
	 * 根据ID从数据库中删除BBSSectionInfo对象
	 * @param id
	 * @throws Exception
	 */
	public void delete( String id ) throws Exception {		
		if( id  == null ){
			throw new Exception( "id is null!" );
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			sectionInfoService.delete( emc, id );
		}catch( Exception e ){
			throw e;
		}
	}

	public List<BBSSectionInfo> listAll() throws Exception {
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			return sectionInfoService.listAll( emc );
		}catch( Exception e ){
			throw e;
		}
	}

	/**
	 * 根据论坛ID查询论坛中所有的主版块信息数量
	 * @param id
	 * @return
	 * @throws Exception 
	 */
	public Long countMainSectionByForumId( String forumId ) throws Exception {
		if( forumId  == null || forumId.isEmpty() ){
			throw new Exception( "forumId is null!" );
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			return sectionInfoService.countMainSectionByForumId( emc, forumId);
		}catch( Exception e ){
			throw e;
		}
	}
	
	/**
	 * 根据主版块ID查询主版块中所有的子版块信息数量
	 * @param id
	 * @return
	 * @throws Exception 
	 */
	public Long countSubSectionByMainSectionId( String sectionId ) throws Exception {
		if( sectionId  == null || sectionId.isEmpty() ){
			throw new Exception( "sectionId is null!" );
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			return sectionInfoService.countSubSectionByMainSectionId( emc, sectionId);
		}catch( Exception e ){
			throw e;
		}
	}

	/**
	 * 根据论坛ID查询论坛中所有的版块信息
	 * @param forumId
	 * @return
	 * @throws Exception 
	 */
	public List<BBSSectionInfo> listMainSectionByForumId( String forumId ) throws Exception {
		if( forumId  == null || forumId.isEmpty() ){
			throw new Exception( "forumId is null!" );
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			return sectionInfoService.listMainSectionByForumId( emc, forumId);
		}catch( Exception e ){
			throw e;
		}
	}
	
	/**
	 * 根据论坛ID查询论坛中所有的版块信息
	 * @param forumId
	 * @return
	 * @throws Exception 
	 */
	public List<BBSSectionInfo> viewMainSectionByForumId( String forumId, List<String> viewableSectionIds ) throws Exception {
		if( forumId  == null || forumId.isEmpty() ){
			throw new Exception( "forumId is null!" );
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			return sectionInfoService.viewMainSectionByForumId( emc, forumId, viewableSectionIds );
		}catch( Exception e ){
			throw e;
		}
	}
	
	/**
	 * 根据主版块ID查询所有的子版块信息列表
	 * @param sectionId
	 * @return
	 * @throws Exception 
	 */
	public List<BBSSectionInfo> viewSubSectionByMainSectionId( String sectionId, List<String> viewableSectionIds  ) throws Exception {
		if( sectionId  == null || sectionId.isEmpty() ){
			throw new Exception( "sectionId is null!" );
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			return sectionInfoService.viewSubSectionByMainSectionId( emc, sectionId, viewableSectionIds );
		}catch( Exception e ){
			throw e;
		}
	}
	
	/**
	 * 根据主版块ID查询所有的子版块信息列表
	 * @param sectionId
	 * @return
	 * @throws Exception 
	 */
	public List<BBSSectionInfo> listSubSectionByMainSectionId( String sectionId  ) throws Exception {
		if( sectionId  == null || sectionId.isEmpty() ){
			throw new Exception( "sectionId is null!" );
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			return sectionInfoService.listSubSectionByMainSectionId( emc, sectionId );
		}catch( Exception e ){
			throw e;
		}
	}

	public void checkSectionManager( String sectionId ) throws Exception {
		if( sectionId  == null || sectionId.isEmpty() ){
			throw new Exception( "sectionId is null!" );
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			sectionInfoService.checkSectionManager( emc, sectionId );
		}catch( Exception e ){
			throw e;
		}
	}
	
	/**
	 * 检查版主的权限和角色设置
	 * @param sectionInfo
	 * @throws Exception 
	 */
	public void checkSectionManager( BBSSectionInfo sectionInfo ) throws Exception {
		if( sectionInfo == null ){
			throw new Exception( "sectionInfo is null!" );
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			sectionInfoService.checkSectionManager( emc, sectionInfo );
		}catch( Exception e ){
			throw e;
		}
	}

	/**
	 * 根据用户权限和论坛ID，获取所有主版块信息ID列表
	 * @param forumId
	 * @param sectionIds 
	 * @return
	 * @throws Exception 
	 */
	public List<BBSSectionInfo> listAllViewAbleMainSectionWithUserPermission( String forumId, List<String> sectionIds ) throws Exception {
		if( forumId  == null || forumId.isEmpty() ){
			throw new Exception( "forumId is null!" );
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			return sectionInfoService.listAllViewAbleMainSectionWithUserPermission( emc, forumId, sectionIds );
		}catch( Exception e ){
			throw e;
		}
	}

	/**
	 * 
	 * @param viewforumIds
	 * @param publicStatus 是否公开
	 * @return
	 * @throws Exception
	 */
	public List<String> viewSectionByForumIds( List<String> viewforumIds, Boolean publicStatus ) throws Exception {
		if( viewforumIds  == null || viewforumIds.isEmpty() ){
			return null;
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			return sectionInfoService.viewSectionByForumIds( emc, viewforumIds, publicStatus );
		}catch( Exception e ){
			throw e;
		}
	}

	public List<BBSSectionInfo> list(List<String> publicSectionIds) throws Exception {
		if( publicSectionIds  == null || publicSectionIds.isEmpty() ){
			return null;
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			return sectionInfoService.list( emc, publicSectionIds );
		}catch( Exception e ){
			throw e;
		}
	}
}
package com.x.bbs.assemble.control.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.bbs.assemble.control.Business;
import com.x.bbs.entity.BBSSectionInfo;
import com.x.bbs.entity.BBSSubjectAttachment;
import com.x.bbs.entity.BBSSubjectContent;
import com.x.bbs.entity.BBSSubjectInfo;
import com.x.bbs.entity.BBSSubjectPictureBase64;

/**
 * 论坛信息管理服务类
 * @author LIYI
 *
 */
public class BBSSubjectInfoServiceAdv {
	
	private Logger logger = LoggerFactory.getLogger( BBSSubjectInfoServiceAdv.class );
	private BBSSubjectInfoService subjectInfoService = new BBSSubjectInfoService();
	
	/**
	 * 根据传入的ID从数据库查询BBSSubjectInfo对象
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public BBSSubjectInfo get( String id ) throws Exception {
		if( id  == null || id.isEmpty() ){
			throw new Exception( "id is null, return null!" );
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			return emc.find( id, BBSSubjectInfo.class );
		}catch( Exception e ){
			throw e;
		}
	}
	
	/**
	 * 根据传入的ID从数据库查询BBSSubjectInfo对象
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public BBSSubjectInfo view( String id ) throws Exception {
		if( id  == null || id.isEmpty() ){
			throw new Exception( "id is null, return null!" );
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			return subjectInfoService.view( emc, id );
		}catch( Exception e ){
			throw e;
		}
	}
	
	/**
	 * 向数据库保存BBSSubjectInfo对象
	 * @param wrapIn
	 */
	public BBSSubjectInfo save( BBSSubjectInfo _bBSSubjectInfo, String content, String pictureBase64 ) throws Exception {
		if( _bBSSubjectInfo  == null ){
			throw new Exception( "_bBSSubjectInfo is null!" );
		}
		if( content  == null || content.isEmpty() ){
			throw new Exception( "content is null!" );
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			return subjectInfoService.save( emc, _bBSSubjectInfo, content, pictureBase64 );
		}catch( Exception e ){
			throw e;
		}
	}
	
	/**
	 * 根据ID从数据库中删除BBSSubjectInfo对象
	 * 删除主题后，要对一系列的数据进行操作：
	 * 1、所有的回贴删除，记录回贴数量n
	 * 2、版块主题数量-1，如果有主版块，主版块主题数量-1
	 * 3、论坛主题数量-1
	 * 4、版块回复数量-n，如果有主版块，主版块回复数量-n
	 * 5、论坛回复数量-n
	 * @param id
	 * @throws Exception
	 */
	public void delete( String subjectId ) throws Exception {
		if( subjectId  == null || subjectId.isEmpty() ){
			throw new Exception( "subjectId is null, return null!" );
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			subjectInfoService.delete( emc, subjectId );
		}catch( Exception e ){
			throw e;
		}
	}

	public Long countByMainAndSubSectionId( String sectionId, Boolean withTopSubject ) throws Exception {
		if( sectionId == null || sectionId.isEmpty() ){
			throw new Exception( "sectionId is null." );
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			return subjectInfoService.countByMainAndSubSectionId( emc, sectionId, withTopSubject );
		}catch( Exception e ){
			throw e;
		}
	}
	
	public Long countByForumId( String forumId, Boolean withTopSubject ) throws Exception {
		if( forumId == null || forumId.isEmpty() ){
			throw new Exception( "sectionId is null." );
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			return subjectInfoService.countByForumId( emc, forumId, withTopSubject );
		}catch( Exception e ){
			throw e;
		}
	}

	/**
	 * 根据版块信息，查询所有需要展现的所有置顶主题列表
	 * 包括：全局置顶贴, 当前论坛置顶贴, 当前版块置顶贴和主版块的置顶贴
	 * @param sectionInfo
	 * @return
	 * @throws Exception 
	 */
	public List<BBSSubjectInfo> listAllTopSubject( BBSSectionInfo sectionInfo, String creatorName, List<String> viewSectionIds ) throws Exception {
		if( sectionInfo == null ){
			throw new Exception( "sectionInfo is null." );
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			return subjectInfoService.listAllTopSubject( emc, sectionInfo, creatorName, viewSectionIds );
		}catch( Exception e ){
			throw e;
		}
	}

	public BBSSubjectInfo acceptReply( String subjectId, String replyId, String name ) throws Exception {
		if( subjectId == null ){
			throw new Exception( "subjectId is null." );
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			return subjectInfoService.acceptReply( emc, subjectId, replyId, name );
		}catch( Exception e ){
			throw e;
		}
	}

	public BBSSubjectInfo complete( String subjectId, boolean complete, String name) throws Exception {
		if( subjectId == null ){
			throw new Exception( "subjectId is null." );
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			return subjectInfoService.complete( emc, subjectId, complete, name );
		}catch( Exception e ){
			throw e;
		}
	}	
	
	/**
	 * 精华贴设置
	 * @param subjectId
	 * @param isCream
	 * @param setterName
	 * @return
	 * @throws Exception
	 */
	public BBSSubjectInfo setCream( String subjectId, Boolean isCream, String setterName ) throws Exception {
		if( subjectId == null ){
			throw new Exception( "subjectId is null." );
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			return subjectInfoService.setCream( emc, subjectId, isCream, setterName );
		}catch( Exception e ){
			throw e;
		}
	}

	public BBSSubjectInfo lock(String subjectId, Boolean lock, String setterName) throws Exception {
		if( subjectId == null ){
			throw new Exception( "subjectId is null." );
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			return subjectInfoService.lock( emc, subjectId, lock, setterName );
		}catch( Exception e ){
			throw e;
		}
	}
	
	/**
	 * 全局置顶设置
	 * @param subjectId
	 * @param topToBBS
	 * @param setterName
	 * @return
	 * @throws Exception
	 */
	public BBSSubjectInfo setTopToBBS(String subjectId, Boolean topToBBS, String setterName ) throws Exception {
		if( subjectId == null ){
			throw new Exception( "subjectId is null." );
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			return subjectInfoService.setTopToBBS( emc, subjectId, topToBBS, setterName );
		}catch( Exception e ){
			throw e;
		}
	}

	/**
	 * 论坛置顶设置
	 * @param subjectId
	 * @param topToForum
	 * @param name
	 * @return
	 * @throws Exception
	 */
	public BBSSubjectInfo setTopToForum( String subjectId, boolean topToForum, String name) throws Exception {
		if( subjectId == null ){
			throw new Exception( "subjectId is null." );
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			return subjectInfoService.setTopToForum( emc, subjectId, topToForum, name );
		}catch( Exception e ){
			throw e;
		}
	}

	/**
	 * 版块置顶设置
	 * @param subjectId
	 * @param topToForum
	 * @param name
	 * @return
	 * @throws Exception
	 */
	public BBSSubjectInfo setTopToSection( String subjectId, boolean topToSection, String name ) throws Exception {
		if( subjectId == null ){
			throw new Exception( "subjectId is null." );
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			return subjectInfoService.setTopToSection( emc, subjectId, topToSection, name );
		}catch( Exception e ){
			throw e;
		}
	}

	/**
	 * 主版块置顶设置
	 * @param subjectId
	 * @param topToMainSection
	 * @param name
	 * @return
	 * @throws Exception
	 */
	public BBSSubjectInfo setTopToMainSection( String subjectId, boolean topToMainSection, String name) throws Exception {
		if( subjectId == null ){
			throw new Exception( "subjectId is null." );
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			return subjectInfoService.setTopToMainSection( emc, subjectId, topToMainSection, name );
		}catch( Exception e ){
			throw e;
		}
	}

	/**
	 * 设置原创主题
	 * @param subjectId
	 * @param isOriginalSubject
	 * @param name
	 * @return
	 * @throws Exception
	 */
	public BBSSubjectInfo setOriginal( String subjectId, boolean isOriginalSubject, String name ) throws Exception {
		if( subjectId == null ){
			throw new Exception( "subjectId is null." );
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			return subjectInfoService.setOriginal( emc, subjectId, isOriginalSubject, name );
		}catch( Exception e ){
			throw e;
		}
	}

	public List<BBSSubjectAttachment> listAttachmentByIds( List<String> attachmentList ) throws Exception {
		if( attachmentList == null || attachmentList.isEmpty() ){
			return null;
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			return subjectInfoService.listAttachmentByIds( emc, attachmentList );
		}catch( Exception e ){
			throw e;
		}
	}

	public List<BBSSubjectInfo> listSubjectInSectionForPage( String forumId, String mainSectionId, String sectionId, String creatorName, Boolean needPicture, Boolean isTopSubject, Integer maxRecordCount, List<String> viewSectionIds ) throws Exception {
		if( viewSectionIds == null || viewSectionIds.isEmpty() ){
			return null;
		}
		if( maxRecordCount == null ){
			maxRecordCount = 20;
		}
		Business business = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			business = new Business(emc);
			return business.subjectInfoFactory().listSubjectInSectionForPage( forumId, mainSectionId, sectionId, creatorName, needPicture, isTopSubject, maxRecordCount, viewSectionIds );
		}catch( Exception e ){
			throw e;
		}
	}
	
	public Long countSubjectInSectionForPage( String forumId, String mainSectionId, String sectionId, String creatorName, Boolean needPicture, Boolean isTopSubject, List<String> viewSectionIds ) throws Exception {
		if( viewSectionIds == null || viewSectionIds.isEmpty() ){
			return 0L;
		}
		Business business = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			business = new Business(emc);
			return business.subjectInfoFactory().countSubjectInSectionForPage( forumId, mainSectionId, sectionId, creatorName, needPicture, isTopSubject, viewSectionIds );
		}catch( Exception e ){
			throw e;
		}
	}

	public BBSSubjectAttachment getAttachment( String id ) throws Exception {
		if( id  == null || id.isEmpty() ){
			throw new Exception( "id is null, return null!" );
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			return emc.find( id, BBSSubjectAttachment.class );
		}catch( Exception e ){
			throw e;
		}
	}

	public List<BBSSubjectInfo> listSubjectIdsBySection(String sectionId) throws Exception {
		if( sectionId  == null || sectionId.isEmpty() ){
			throw new Exception( "sectionId is null, return null!" );
		}
		Business business = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			business = new Business(emc);
			return business.subjectInfoFactory().listSubjectBySection( sectionId );
		}catch( Exception e ){
			throw e;
		}
	}

	/**
	 * 根据指定的主题ID，查询上一个主题的信息
	 * @param id
	 * @return
	 * @throws Exception 
	 */
	public BBSSubjectInfo getLastSubject( String id ) throws Exception {
		if( id  == null || id.isEmpty() ){
			throw new Exception( "id is null, return null!" );
		}
		List<BBSSubjectInfo> subjectInfoList = null;
		BBSSubjectInfo subjectInfo = null;
		Business business = null;
		Long count = 0L;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			business = new Business(emc);
			subjectInfo = emc.find( id, BBSSubjectInfo.class );
			if( subjectInfo != null ){
				count = business.subjectInfoFactory().countLastSubject( subjectInfo.getLatestReplyTime() );
				if( count > 0 ){
					subjectInfoList = business.subjectInfoFactory().listLastSubject( subjectInfo.getLatestReplyTime() );
				}
			}
			if( subjectInfoList != null && !subjectInfoList.isEmpty() ){
				return subjectInfoList.get( 0 );
			}else{
				return null;
			}
		}catch( Exception e ){
			throw e;
		}
	}

	public BBSSubjectInfo getNextSubject(String id) throws Exception {
		if( id  == null || id.isEmpty() ){
			throw new Exception( "id is null, return null!" );
		}
		List<BBSSubjectInfo> subjectInfoList = null;
		BBSSubjectInfo subjectInfo = null;
		Business business = null;
		Long count = 0L;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			business = new Business(emc);
			subjectInfo = emc.find( id, BBSSubjectInfo.class );
			if( subjectInfo != null ){
				count = business.subjectInfoFactory().countNextSubject( subjectInfo.getLatestReplyTime() );
				if( count > 0 ){
					subjectInfoList = business.subjectInfoFactory().listNextSubject( subjectInfo.getLatestReplyTime() );
				}
			}
			if( subjectInfoList != null && !subjectInfoList.isEmpty() ){
				return subjectInfoList.get( 0 );
			}else{
				return null;
			}
		}catch( Exception e ){
			throw e;
		}
	}

	public Long countUserSubjectForPage( String forumId, String mainSectionId, String sectionId, Boolean needPicture, Boolean withTopSubject, String name) throws Exception {
		if( name == null || name.isEmpty() ){
			throw new Exception( "name can not null." );
		}
		Business business = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			business = new Business(emc);
			return business.subjectInfoFactory().countUserSubjectForPage( forumId, mainSectionId, sectionId, needPicture, withTopSubject, name );
		}catch( Exception e ){
			throw e;
		}
	}

	public List<BBSSubjectInfo> listUserSubjectForPage(String forumId, String mainSectionId, String sectionId, Boolean needPicture, Boolean withTopSubject, Integer maxRecordCount, String name ) throws Exception {
		if( name == null || name.isEmpty() ){
			throw new Exception( "name can not null." );
		}
		if( maxRecordCount == null ){
			maxRecordCount = 20;
		}
		Business business = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			business = new Business(emc);
			return business.subjectInfoFactory().listUserSubjectForPage( forumId, mainSectionId, sectionId, needPicture, withTopSubject, maxRecordCount, name );
		}catch( Exception e ){
			throw e;
		}
	}

	public Long countSubjectByUserName( String userName ) throws Exception {
		if( userName == null ){
			throw new Exception( "userName can not null." );
		}
		Business business = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			business = new Business(emc);
			return business.subjectInfoFactory().countUserSubjectForPage( null, null, null, null, null, userName );
		}catch( Exception e ){
			throw e;
		}
	}

	public Long countSubjectForTodayByUserName( String userName ) throws Exception {
		if( userName == null ){
			throw new Exception( "userName can not null." );
		}
		Business business = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			business = new Business(emc);
			return business.subjectInfoFactory().countSubjectForTodayByUserName( userName );
		}catch( Exception e ){
			throw e;
		}
	}

	public Long countCreamSubjectByUserName(String userName) throws Exception {
		if( userName == null ){
			throw new Exception( "userName can not null." );
		}
		Business business = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			business = new Business(emc);
			return business.subjectInfoFactory().countCreamSubjectByUserName( userName );
		}catch( Exception e ){
			throw e;
		}
	}

	public Long countOriginalSubjectByUserName(String userName) throws Exception {
		if( userName == null ){
			throw new Exception( "userName can not null." );
		}
		Business business = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			business = new Business(emc);
			return business.subjectInfoFactory().countOriginalSubjectByUserName( userName );
		}catch( Exception e ){
			throw e;
		}
	}

	public BBSSubjectInfo recommendToBBSIndex(String subjectId, boolean recommendToBBSIndex, String name ) throws Exception {
		if( subjectId == null ){
			throw new Exception( "subjectId is null." );
		}
		BBSSubjectInfo subjectInfo = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			subjectInfo = emc.find( subjectId, BBSSubjectInfo.class );			
			emc.beginTransaction( BBSSubjectInfo.class );			
			subjectInfo.setRecommendToBBSIndex( recommendToBBSIndex );
			if( recommendToBBSIndex ){
				subjectInfo.setIsRecommendSubject( true );
				subjectInfo.setRecommendorName( name );
				subjectInfo.setRecommendTime( new Date() );
			}else{
				subjectInfo.setIsRecommendSubject( false );
				subjectInfo.setRecommendorName( null );
				subjectInfo.setRecommendTime( null );
			}			
			emc.check( subjectInfo, CheckPersistType.all );
			emc.commit();			
		}catch( Exception e ){
			throw e;
		}
		return subjectInfo;
	}

	/**
	 * 版块和论坛可见性要是所有人可见才能显示到首页
	 * @param count
	 * @return
	 * @throws Exception
	 */
	public List<BBSSubjectInfo> listRecommendedSubjectForBBSIndex( List<String> viewSectionIds, Integer count ) throws Exception {
		if( count == null ){
			count = 10;
		}
		Business business = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			business = new Business( emc );
			return business.subjectInfoFactory().listRecommendedSubjectForBBSIndex( null, null, viewSectionIds, count );
		}catch( Exception e ){
			throw e;
		}
	}

	public List<BBSSubjectInfo> listRecommendedSubjectForForumIndex( String forumId, Integer count ) throws Exception {
		if( count == null ){
			count = 10;
		}
		if( forumId == null ){
			throw new Exception( "forumId is null." );
		}
		Business business = null;
		List<String> forumIds = new ArrayList<String>();
		List<String> mainSectionIds = null;
		List<String> sectionIds = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			business = new Business( emc );
			forumIds.add( forumId );
			mainSectionIds = business.sectionInfoFactory().listVisiableToAllUserMainSectionIds( forumIds );
			sectionIds = business.sectionInfoFactory().listVisiableToAllUserSectionIds( forumIds, mainSectionIds );
			return business.subjectInfoFactory().listRecommendedSubjectForForumIndex( forumIds, mainSectionIds, sectionIds, count );
		}catch( Exception e ){
			throw e;
		}
	}

	public Long countSubjectSearchInSectionForPage( String searchContent, List<String> viewSectionIds ) throws Exception {
		if( searchContent == null || searchContent.isEmpty() ){
			throw new Exception( "searchContent can not null." );
		}
		if( viewSectionIds == null || searchContent.isEmpty() ){
			return 0L;
		}
		Business business = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			business = new Business(emc);
			return business.subjectInfoFactory().countSubjectSearchInSectionForPage( searchContent, viewSectionIds );
		}catch( Exception e ){
			throw e;
		}
	}

	public List<BBSSubjectInfo> listSubjectSearchInSectionForPage(String searchContent, List<String> viewSectionIds, Integer count ) throws Exception {
		if( searchContent == null || searchContent.isEmpty() ){
			throw new Exception( "searchContent can not null." );
		}
		if( viewSectionIds == null || searchContent.isEmpty() ){
			return null;
		}
		Business business = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			business = new Business(emc);
			return business.subjectInfoFactory().listSubjectSearchInSectionForPage( searchContent, viewSectionIds,count );
		}catch( Exception e ){
			throw e;
		}
	}

	public Long countCreamedSubjectInSectionForPage(String forumId, String mainSectionId, String sectionId, String creatorName ) throws Exception {
		Business business = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			business = new Business(emc);
			return business.subjectInfoFactory().countCreamedSubjectInSectionForPage( forumId, mainSectionId, sectionId, creatorName );
		}catch( Exception e ){
			throw e;
		}
	}

	public List<BBSSubjectInfo> listCreamedSubjectInSectionForPage(String forumId, String mainSectionId, String sectionId, String creatorName, Integer count ) throws Exception {
		Business business = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			business = new Business( emc );
			return business.subjectInfoFactory().listCreamedSubjectInSectionForPage( forumId, mainSectionId, sectionId, creatorName, count );
		}catch( Exception e ){
			throw e;
		}
	}

	public Long countRecommendedSubjectInSectionForPage(String searchForumId, String searchMainSectionId, String searchSectionId, String creatorName ) throws Exception {
		if( searchForumId == null && searchMainSectionId == null && searchSectionId == null && creatorName == null){
			throw new Exception("search filter 'searchForumId','searchMainSectionId','searchSectionId','creatorName' can not all null!");
		}
		Business business = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			business = new Business(emc);
			return business.subjectInfoFactory().countRecommendedSubjectInSectionForPage( searchForumId, searchMainSectionId, searchSectionId, creatorName );
		}catch( Exception e ){
			throw e;
		}
	}

	public List<BBSSubjectInfo> listRecommendedSubjectInSectionForPage(String searchForumId, String searchMainSectionId, String searchSectionId, String creatorName, Integer count ) throws Exception {
		if( searchForumId == null && searchMainSectionId == null && searchSectionId == null && creatorName == null){
			throw new Exception("search filter 'searchForumId','searchMainSectionId','searchSectionId','creatorName' can not all null!");
		}
		if( count == null ){
			count = 20;
		}
		Business business = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			business = new Business( emc );
			return business.subjectInfoFactory().listRecommendedSubjectInSectionForPage( searchForumId, searchMainSectionId, searchSectionId, creatorName, count );
		}catch( Exception e ){
			throw e;
		}
	}

	public String getPictureBase64( String id ) throws Exception {
		if( id == null || id.isEmpty() ){
			throw new Exception( "id can not null." );
		}
		List<BBSSubjectPictureBase64> encodeList = null;
		BBSSubjectPictureBase64 subjectPictureBase64 = null;
		Business business = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			business = new Business( emc );
			encodeList = business.subjectInfoFactory().getPictureBase64( id );
			if( encodeList != null && encodeList.size() > 0 ){
				subjectPictureBase64 = encodeList.get( 0 );
			}
			if( subjectPictureBase64 != null ){
				return subjectPictureBase64.getPictureBase64();
			}else{
				return "";
			}
		}catch( Exception e ){
			throw e;
		}
	}
	
	public String getSubjectContent( String id ) throws Exception {
		if( id == null || id.isEmpty() ){
			throw new Exception( "id can not null." );
		}
		List<BBSSubjectContent> encodeList = null;
		BBSSubjectContent subjectContent = null;
		Business business = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			business = new Business( emc );
			encodeList = business.subjectInfoFactory().getSubjectContent( id );
			if( encodeList != null && encodeList.size() > 0 ){
				subjectContent = encodeList.get( 0 );
			}
			if( subjectContent != null ){
				return subjectContent.getContent();
			}else{
				return "";
			}
		}catch( Exception e ){
			throw e;
		}
	}

	
}
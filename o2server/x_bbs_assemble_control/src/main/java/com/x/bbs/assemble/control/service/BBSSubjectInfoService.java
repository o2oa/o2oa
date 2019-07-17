package com.x.bbs.assemble.control.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.config.StorageMapping;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.bbs.assemble.common.date.DateOperation;
import com.x.bbs.assemble.control.Business;
import com.x.bbs.assemble.control.ThisApplication;
import com.x.bbs.entity.BBSForumInfo;
import com.x.bbs.entity.BBSReplyInfo;
import com.x.bbs.entity.BBSSectionInfo;
import com.x.bbs.entity.BBSSubjectAttachment;
import com.x.bbs.entity.BBSSubjectContent;
import com.x.bbs.entity.BBSSubjectInfo;
import com.x.bbs.entity.BBSSubjectVoteResult;
import com.x.bbs.entity.BBSVoteOption;
import com.x.bbs.entity.BBSVoteOptionGroup;
import com.x.bbs.entity.BBSVoteRecord;

/**
 * 论坛信息管理服务类
 * @author LIYI
 *
 */
public class BBSSubjectInfoService {
	
	private static  Logger logger = LoggerFactory.getLogger( BBSSubjectInfoService.class );
	private DateOperation dateOperation = new DateOperation();
	
	/**
	 * 根据传入的ID从数据库查询BBSSubjectInfo对象
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public List<BBSSubjectInfo> list( List<String> ids ) throws Exception {
		if( ListTools.isEmpty( ids ) ){
			throw new Exception( "ids is null!" );
		}
		Business business = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			business = new Business(emc);
			return business.subjectInfoFactory().list(ids);
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
	public BBSSubjectInfo view( EntityManagerContainer emc, String id ) throws Exception {
		if( id  == null || id.isEmpty() ){
			throw new Exception( "id is null, return null!" );
		}
		BBSSubjectInfo subjectInfo = null;
		subjectInfo = emc.find( id, BBSSubjectInfo.class );
		if( subjectInfo != null ){
			emc.beginTransaction( BBSSubjectInfo.class );
			subjectInfo.setViewTotal( subjectInfo.getViewTotal() + 1 );
			subjectInfo.setHot( subjectInfo.getHot() + 1 );
			emc.check( subjectInfo, CheckPersistType.all );
			emc.commit();
		}
		return subjectInfo;
	}
	
	/**
	 * 根据传入的ID从数据库查询BBSSubjectInfo对象
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public void addViewCount( EntityManagerContainer emc, String id ) throws Exception {
		if( id  == null || id.isEmpty() ){
			throw new Exception( "id is null, return null!" );
		}
		BBSSubjectInfo subjectInfo = null;
		subjectInfo = emc.find( id, BBSSubjectInfo.class );
		if( subjectInfo != null ){
			emc.beginTransaction( BBSSubjectInfo.class );
			subjectInfo.setViewTotal( subjectInfo.getViewTotal() + 1 );
			subjectInfo.setHot( subjectInfo.getHot() + 1 );
			emc.check( subjectInfo, CheckPersistType.all );
			emc.commit();
		}
	}
	
	
	/**
	 * 向数据库保存BBSSubjectInfo对象
	 * @param wrapIn
	 */
	public BBSSubjectInfo save( EntityManagerContainer emc, BBSSubjectInfo _bBSSubjectInfo, String content ) throws Exception {
		List<BBSSubjectAttachment> attachList = null;
		BBSSubjectAttachment _subjectAttachment = null;
		BBSSubjectInfo _subjectInfo_tmp = null;
		BBSForumInfo _forumInfo_tmp = null;
		BBSSectionInfo _sectionInfo_tmp = null;
		BBSSubjectContent _subjectContent = null;
		Boolean exists = false;
		if( _bBSSubjectInfo.getId() == null ){
			_bBSSubjectInfo.setId( BBSSubjectInfo.createId() );
		}
		Business business = null;
		business = new Business(emc);
		_subjectInfo_tmp = emc.find( _bBSSubjectInfo.getId(), BBSSubjectInfo.class );
		_forumInfo_tmp = emc.find( _bBSSubjectInfo.getForumId(), BBSForumInfo.class );
		_sectionInfo_tmp = emc.find( _bBSSubjectInfo.getSectionId(), BBSSectionInfo.class );
		_subjectContent = emc.find( _bBSSubjectInfo.getId(), BBSSubjectContent.class );
		emc.beginTransaction( BBSSubjectInfo.class );
		emc.beginTransaction( BBSForumInfo.class );
		emc.beginTransaction( BBSSectionInfo.class );
		emc.beginTransaction( BBSSubjectAttachment.class );
		emc.beginTransaction( BBSSubjectContent.class );
		if( _subjectContent == null ){
			_subjectContent = new BBSSubjectContent();
			_subjectContent.setId( _bBSSubjectInfo.getId() );
			_subjectContent.setContent( content );
			emc.persist( _subjectContent, CheckPersistType.all);
		}else{
			_subjectContent.setContent( content );
			emc.check( _sectionInfo_tmp, CheckPersistType.all );
		}
		if( _subjectInfo_tmp == null ){
			emc.persist( _bBSSubjectInfo, CheckPersistType.all);
			if( _forumInfo_tmp != null ){
				_forumInfo_tmp.setSubjectTotalToday( _forumInfo_tmp.getSubjectTotalToday() + 1 );
				_forumInfo_tmp.setSubjectTotal( _forumInfo_tmp.getSubjectTotal() + 1 );
				emc.check( _forumInfo_tmp, CheckPersistType.all );	
			}
			if( _sectionInfo_tmp != null ){
				_sectionInfo_tmp.setSubjectTotalToday( _sectionInfo_tmp.getSubjectTotalToday() + 1 );
				_sectionInfo_tmp.setSubjectTotal( _sectionInfo_tmp.getSubjectTotal() + 1 );
				emc.check( _sectionInfo_tmp, CheckPersistType.all );	
			}
		}else{
			_bBSSubjectInfo.copyTo( _subjectInfo_tmp, JpaObject.FieldsUnmodify  );
			emc.check( _subjectInfo_tmp, CheckPersistType.all );
		}
		//检查和绑定附件信息
		//1、先查询所有的附件绑定信息
		attachList = business.subjectAttachmentFactory().listBySubjectId( _bBSSubjectInfo.getId() );
		if( attachList != null && attachList.size() > 0 ){
			for( BBSSubjectAttachment attachment : attachList ){
				exists = false;
				if( _bBSSubjectInfo.getAttachmentList() != null && _bBSSubjectInfo.getAttachmentList().size() > 0 ){
					for( String attachId : _bBSSubjectInfo.getAttachmentList() ){
						if( attachId.equals( attachment.getId() ) ){
							exists = true;
						}
					}
				}else{
					exists = false;
				}
				if( !exists ){//删除
					emc.remove( attachment, CheckRemoveType.all );
				}
			}
		}
		if( _bBSSubjectInfo.getAttachmentList() != null && _bBSSubjectInfo.getAttachmentList().size() > 0  ){
			for( String attachId : _bBSSubjectInfo.getAttachmentList() ){
				_subjectAttachment = emc.find( attachId, BBSSubjectAttachment.class );
				if( _subjectAttachment != null ){
					_subjectAttachment.setForumId( _bBSSubjectInfo.getForumId() );
					_subjectAttachment.setForumName( _bBSSubjectInfo.getForumName());
					_subjectAttachment.setMainSectionId( _bBSSubjectInfo.getMainSectionId() );
					_subjectAttachment.setMainSectionName( _bBSSubjectInfo.getMainSectionName() );
					_subjectAttachment.setSectionId( _bBSSubjectInfo.getSectionId() );
					_subjectAttachment.setSectionName( _bBSSubjectInfo.getSectionName() );
					_subjectAttachment.setSubjectId( _bBSSubjectInfo.getId() );
					_subjectAttachment.setTitle( _bBSSubjectInfo.getTitle() );
					emc.check( _subjectAttachment, CheckPersistType.all );	
				}
			}
		}
		emc.commit();
		return _bBSSubjectInfo;
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
	public void delete( EntityManagerContainer emc, String subjectId ) throws Exception {
		BBSSubjectInfo subjectInfo = null;
		BBSSectionInfo mainSectionInfo = null;
		BBSSectionInfo sectionInfo = null;
		BBSForumInfo forumInfo = null;
		BBSSubjectContent subjectContent = null;
		BBSSubjectVoteResult subjectVoteResult = null;
		BBSSubjectAttachment subjectAttachment = null;
		List<BBSVoteOptionGroup> voteOptionGroupList = null;
		List<BBSReplyInfo> replyInfoList = null;
		List<BBSVoteOption> voteOptionList = null;
		List<BBSVoteRecord> voteRecordList = null;
		List<String> attachmentIds = null;
		StorageMapping mapping = null;
		Business business = null;
		Date today = new Date();
		if( subjectId == null || subjectId.isEmpty() ){
			throw new Exception( "id is null, system can not delete any object." );
		}
		emc.beginTransaction( BBSForumInfo.class );
		emc.beginTransaction( BBSSectionInfo.class );
		emc.beginTransaction( BBSSubjectInfo.class );
		emc.beginTransaction( BBSSubjectContent.class );
		emc.beginTransaction( BBSVoteOption.class );
		emc.beginTransaction( BBSVoteOptionGroup.class );
		emc.beginTransaction( BBSVoteRecord.class );
		emc.beginTransaction( BBSSubjectAttachment.class );
		emc.beginTransaction( BBSSubjectVoteResult.class );
		
		business = new Business( emc );
		//先判断需要操作的应用信息是否存在，根据ID进行一次查询，如果不存在不允许继续操作
		subjectInfo = emc.find( subjectId, BBSSubjectInfo.class );
		subjectContent = emc.find( subjectId, BBSSubjectContent.class );
		replyInfoList = business.replyInfoFactory().listWithSubjectForPage( subjectId, null );
		voteOptionList = business.voteOptionFactory().listVoteOptionBySubject( subjectId );
		voteOptionGroupList = business.voteOptionFactory().listVoteOptionGroupBySubject( subjectId );
		voteRecordList = business.voteRecordFactory().listVoteRecordBySubject( subjectId );
		subjectVoteResult = emc.find( subjectId, BBSSubjectVoteResult.class );
		if ( null != subjectInfo ) {
			forumInfo = emc.find( subjectInfo.getForumId(), BBSForumInfo.class );
			sectionInfo = emc.find( subjectInfo.getSectionId(), BBSSectionInfo.class );
			if( sectionInfo!= null && sectionInfo.getSectionLevel().equals( "子版块" ) && !sectionInfo.getId().equals( sectionInfo.getMainSectionId() )){
				mainSectionInfo = emc.find( subjectInfo.getMainSectionId(), BBSSectionInfo.class );
			}
			attachmentIds = subjectInfo.getAttachmentList();
		}
		if( subjectContent != null ){
			emc.remove( subjectContent, CheckRemoveType.all );
		}
		if( subjectVoteResult != null ){
			emc.remove( subjectVoteResult, CheckRemoveType.all );
		}
		if( ListTools.isNotEmpty( attachmentIds ) ){
			for( String attachId : attachmentIds ){
				try{
					subjectAttachment = emc.find( attachId, BBSSubjectAttachment.class );
					mapping = ThisApplication.context().storageMappings().get( BBSSubjectAttachment.class, subjectAttachment.getStorage() );
					if( subjectAttachment != null ){
						subjectAttachment.deleteContent(mapping);
						emc.remove( subjectAttachment, CheckRemoveType.all );
					}
				}catch( Exception e ){
					logger.warn( "delete subject attachment got an exception. id:" + attachId );
					logger.error(e);
				}
			}
		}
		if( ListTools.isNotEmpty( replyInfoList ) ){
			for( BBSReplyInfo reply : replyInfoList ){
				emc.remove( reply, CheckRemoveType.all );
				if( forumInfo != null ){
					if( forumInfo.getReplyTotal() > 0 ){
						forumInfo.setReplyTotal( forumInfo.getReplyTotalToday() - 1 );
					}					
					if( dateOperation.isTheSameDate( today, reply.getCreateTime() ) && forumInfo.getReplyTotalToday() > 0 ){
						forumInfo.setReplyTotalToday( forumInfo.getReplyTotalToday() - 1 );
					}
				}
				if( mainSectionInfo != null ){
					if( mainSectionInfo.getReplyTotal() > 0 ){
						mainSectionInfo.setReplyTotal( mainSectionInfo.getReplyTotalToday() - 1 );
					}
					if( dateOperation.isTheSameDate( today, reply.getCreateTime() ) && mainSectionInfo.getReplyTotalToday() > 0){
						mainSectionInfo.setReplyTotalToday( mainSectionInfo.getReplyTotalToday() - 1 );
					}
				}
				if( sectionInfo != null ){
					if( sectionInfo.getReplyTotal() > 0 ){
						sectionInfo.setReplyTotal( sectionInfo.getReplyTotalToday() - 1 );
					}
					if( dateOperation.isTheSameDate( today, reply.getCreateTime() ) && sectionInfo.getReplyTotalToday() > 0 ){
						sectionInfo.setReplyTotalToday( sectionInfo.getReplyTotalToday() - 1 );
					}
				}
			}
		}
		if ( null != subjectInfo ) {
			if (forumInfo != null) {
				if (forumInfo.getSubjectTotal() > 0) {
					forumInfo.setSubjectTotal( forumInfo.getSubjectTotal() - 1 );
				}
				if ( dateOperation.isTheSameDate( today, subjectInfo.getCreateTime() ) && forumInfo.getSubjectTotalToday() > 0) {
					forumInfo.setSubjectTotalToday( forumInfo.getSubjectTotalToday() - 1 );
				}
				emc.check(forumInfo, CheckPersistType.all);
			}
			if( mainSectionInfo != null ){
				if (mainSectionInfo.getSubjectTotal() > 0) {
					mainSectionInfo.setSubjectTotal( mainSectionInfo.getSubjectTotal() - 1 );
				}
				if ( dateOperation.isTheSameDate( today, subjectInfo.getCreateTime() ) && mainSectionInfo.getSubjectTotalToday() > 0) {
					mainSectionInfo.setSubjectTotalToday( mainSectionInfo.getSubjectTotalToday() - 1 );
				}
				emc.check( mainSectionInfo, CheckPersistType.all );
			}
			if( sectionInfo != null ){
				if (sectionInfo.getSubjectTotal() > 0) {
					sectionInfo.setSubjectTotal( sectionInfo.getSubjectTotal() - 1 );
				}
				if ( dateOperation.isTheSameDate( today, subjectInfo.getCreateTime() ) && sectionInfo.getSubjectTotalToday() > 0) {
					sectionInfo.setSubjectTotalToday( sectionInfo.getSubjectTotalToday() - 1 );
				}
				emc.check( sectionInfo, CheckPersistType.all );
			}
			emc.remove( subjectInfo, CheckRemoveType.all );
		}
		if( voteOptionGroupList != null && voteOptionGroupList.size() > 0 ){
			for( BBSVoteOptionGroup voteOptionGroup : voteOptionGroupList ){
				emc.remove( voteOptionGroup, CheckRemoveType.all );
			}
		}
		if( voteOptionList != null && voteOptionList.size() > 0 ){
			for( BBSVoteOption voteOption : voteOptionList ){
				emc.remove( voteOption, CheckRemoveType.all );
			}
		}
		if( voteRecordList != null && voteRecordList.size() > 0 ){
			for( BBSVoteRecord voteRecord : voteRecordList ){
				emc.remove( voteRecord, CheckRemoveType.all );
			}
		}
		emc.commit();
	}

	public Long countByMainAndSubSectionId( EntityManagerContainer emc, String sectionId, Boolean withTopSubject ) throws Exception {
		if( sectionId == null || sectionId.isEmpty() ){
			throw new Exception( "sectionId is null." );
		}
		Business business = new Business( emc );
		return business.subjectInfoFactory().countByMainAndSubSectionId( sectionId, withTopSubject );
	}
	
	public Long countByForumId( EntityManagerContainer emc, String forumId, Boolean withTopSubject ) throws Exception {
		if( forumId == null || forumId.isEmpty() ){
			throw new Exception( "sectionId is null." );
		}
		Business business = new Business( emc );
		return business.subjectInfoFactory().countByForumId(forumId, withTopSubject);
	}

	/**
	 * 根据版块信息，查询所有需要展现的所有置顶主题列表
	 * 包括：全局置顶贴, 当前论坛置顶贴, 当前版块置顶贴和主版块的置顶贴
	 * @param sectionInfo
	 * @return
	 * @throws Exception 
	 */
	public List<BBSSubjectInfo> listAllTopSubject( EntityManagerContainer emc, BBSSectionInfo sectionInfo, String creatorName, List<String> viewSectionIds ) throws Exception {
		String forumId = null;
		String mainSectionId = null;
		String sectionId = null;
		if( sectionInfo != null ){
			forumId = sectionInfo.getForumId();
			mainSectionId = sectionInfo.getMainSectionId();
			sectionId = sectionInfo.getId();
		}
		if( viewSectionIds == null ){
			return null;
		}
		if( !viewSectionIds.contains( sectionInfo.getId() ) ){
			throw new Exception( "user can not visit section["+ sectionInfo.getSectionName() +"]." );
		}
		Business business = null;
		List<String> ids = null;
		business = new Business( emc );
		ids = business.subjectInfoFactory().listAllTopSubject( forumId, mainSectionId, sectionId, creatorName );
		if( ListTools.isNotEmpty( ids ) ){
			return business.subjectInfoFactory().list( ids );
		}
		return null;
	}

	public BBSSubjectInfo acceptReply( EntityManagerContainer emc, String subjectId, String replyId, String name ) throws Exception {
		if( subjectId == null ){
			throw new Exception( "subjectId is null." );
		}
		BBSSubjectInfo subjectInfo = null;
		subjectInfo = emc.find( subjectId, BBSSubjectInfo.class );			
		emc.beginTransaction( BBSSubjectInfo.class );			
		subjectInfo.setAcceptReplyId( replyId );
		emc.check( subjectInfo, CheckPersistType.all );
		emc.commit();
		return subjectInfo;
	}

	public BBSSubjectInfo complete( EntityManagerContainer emc, String subjectId, boolean complete, String name) throws Exception {
		if( subjectId == null ){
			throw new Exception( "subjectId is null." );
		}
		BBSSubjectInfo subjectInfo = null;
		subjectInfo = emc.find( subjectId, BBSSubjectInfo.class );			
		emc.beginTransaction( BBSSubjectInfo.class );			
		subjectInfo.setIsCompleted( complete );	
		emc.check( subjectInfo, CheckPersistType.all );
		emc.commit();
		return subjectInfo;
	}	
	
	/**
	 * 精华贴设置
	 * @param subjectId
	 * @param isCream
	 * @param setterName
	 * @return
	 * @throws Exception
	 */
	public BBSSubjectInfo setCream( EntityManagerContainer emc, String subjectId, Boolean isCream, String setterName ) throws Exception {
		if( subjectId == null ){
			throw new Exception( "subjectId is null." );
		}
		BBSSubjectInfo subjectInfo = null;
		subjectInfo = emc.find( subjectId, BBSSubjectInfo.class );			
		emc.beginTransaction( BBSSubjectInfo.class );			
		subjectInfo.setIsCreamSubject( isCream );
		subjectInfo.setScreamSetterName( setterName );
		subjectInfo.setScreamSetterTime( new Date() );			
		emc.check( subjectInfo, CheckPersistType.all );
		emc.commit();
		return subjectInfo;
	}

	public BBSSubjectInfo lock( EntityManagerContainer emc, String subjectId, boolean lock, String setterName) throws Exception {
		if( subjectId == null ){
			throw new Exception( "subjectId is null." );
		}
		BBSSubjectInfo subjectInfo = null;
		subjectInfo = emc.find( subjectId, BBSSubjectInfo.class );			
		emc.beginTransaction( BBSSubjectInfo.class );
		if( lock ){
			subjectInfo.setSubjectStatus( "已锁定" );
			subjectInfo.setStopReply( lock );
			subjectInfo.setScreamSetterName( setterName );
			subjectInfo.setScreamSetterTime( new Date() );	
		}else{
			subjectInfo.setSubjectStatus( "启用" );
			subjectInfo.setStopReply( lock );
			subjectInfo.setScreamSetterName( setterName );
			subjectInfo.setScreamSetterTime( new Date() );	
		}	
		emc.check( subjectInfo, CheckPersistType.all );
		emc.commit();
		return subjectInfo;
	}
	
	/**
	 * 全局置顶设置
	 * @param subjectId
	 * @param topToBBS
	 * @param setterName
	 * @return
	 * @throws Exception
	 */
	public BBSSubjectInfo setTopToBBS( EntityManagerContainer emc, String subjectId, boolean topToBBS, String setterName ) throws Exception {
		if( subjectId == null ){
			throw new Exception( "subjectId is null." );
		}
		BBSSubjectInfo subjectInfo = null;
		subjectInfo = emc.find( subjectId, BBSSubjectInfo.class );			
		emc.beginTransaction( BBSSubjectInfo.class );
		
		subjectInfo.setTopToBBS(topToBBS);		
		
		if( subjectInfo.getTopToBBS() || subjectInfo.getTopToForum() || subjectInfo.getTopToMainSection() || subjectInfo.getTopToSection() ){
			subjectInfo.setIsTopSubject( true );
		}else{
			subjectInfo.setIsTopSubject( false );
		}
		
		emc.check( subjectInfo, CheckPersistType.all );
		emc.commit();
		return subjectInfo;
	}

	/**
	 * 论坛置顶设置
	 * @param subjectId
	 * @param topToForum
	 * @param name
	 * @return
	 * @throws Exception
	 */
	public BBSSubjectInfo setTopToForum( EntityManagerContainer emc, String subjectId, boolean topToForum, String name) throws Exception {
		if( subjectId == null ){
			throw new Exception( "subjectId is null." );
		}
		BBSSubjectInfo subjectInfo = null;
		subjectInfo = emc.find( subjectId, BBSSubjectInfo.class );			
		emc.beginTransaction( BBSSubjectInfo.class );
		subjectInfo.setTopToForum( topToForum );
		if( subjectInfo.getTopToBBS() || subjectInfo.getTopToForum() || subjectInfo.getTopToMainSection() || subjectInfo.getTopToSection() ){
			subjectInfo.setIsTopSubject( true );
		}else{
			subjectInfo.setIsTopSubject( false );
		}
		emc.check( subjectInfo, CheckPersistType.all );
		emc.commit();
		return subjectInfo;
	}

	/**
	 * 版块置顶设置
	 * @param subjectId
	 * @param topToForum
	 * @param name
	 * @return
	 * @throws Exception
	 */
	public BBSSubjectInfo setTopToSection( EntityManagerContainer emc, String subjectId, boolean topToSection, String name ) throws Exception {
		if( subjectId == null ){
			throw new Exception( "subjectId is null." );
		}
		BBSSubjectInfo subjectInfo = null;
		subjectInfo = emc.find( subjectId, BBSSubjectInfo.class );			
		emc.beginTransaction( BBSSubjectInfo.class );
		
		subjectInfo.setTopToSection( topToSection );
		
		if( subjectInfo.getTopToBBS() || subjectInfo.getTopToForum() || subjectInfo.getTopToMainSection() || subjectInfo.getTopToSection() ){
			subjectInfo.setIsTopSubject( true );
		}else{
			subjectInfo.setIsTopSubject( false );
		}
		
		emc.check( subjectInfo, CheckPersistType.all );
		emc.commit();
		return subjectInfo;
	}

	/**
	 * 主版块置顶设置
	 * @param subjectId
	 * @param topToMainSection
	 * @param name
	 * @return
	 * @throws Exception
	 */
	public BBSSubjectInfo setTopToMainSection( EntityManagerContainer emc, String subjectId, boolean topToMainSection, String name) throws Exception {
		if( subjectId == null ){
			throw new Exception( "subjectId is null." );
		}
		BBSSubjectInfo subjectInfo = null;
		subjectInfo = emc.find( subjectId, BBSSubjectInfo.class );			
		emc.beginTransaction( BBSSubjectInfo.class );
		
		subjectInfo.setTopToMainSection( topToMainSection );
		
		if( subjectInfo.getTopToBBS() || subjectInfo.getTopToForum() || subjectInfo.getTopToMainSection() || subjectInfo.getTopToSection() ){
			subjectInfo.setIsTopSubject( true );
		}else{
			subjectInfo.setIsTopSubject( false );
		}
		
		emc.check( subjectInfo, CheckPersistType.all );
		emc.commit();
		return subjectInfo;
	}

	/**
	 * 设置原创主题
	 * @param subjectId
	 * @param isOriginalSubject
	 * @param name
	 * @return
	 * @throws Exception
	 */
	public BBSSubjectInfo setOriginal( EntityManagerContainer emc, String subjectId, boolean isOriginalSubject, String name ) throws Exception {
		if( subjectId == null ){
			throw new Exception( "subjectId is null." );
		}
		BBSSubjectInfo subjectInfo = null;
		subjectInfo = emc.find( subjectId, BBSSubjectInfo.class );			
		emc.beginTransaction( BBSSubjectInfo.class );			
		subjectInfo.setIsOriginalSubject(isOriginalSubject);
		subjectInfo.setOriginalSetterName( name );
		subjectInfo.setOriginalSetterTime( new Date() );		
		emc.check( subjectInfo, CheckPersistType.all );
		emc.commit();
		return subjectInfo;
	}

	public List<BBSSubjectAttachment> listAttachmentByIds( EntityManagerContainer emc, List<String> attachmentList ) throws Exception {
		if( attachmentList == null || attachmentList.isEmpty() ){
			throw new Exception( "subjectId is null." );
		}
		Business business = new Business(emc);
		return business.subjectAttachmentFactory().list( attachmentList );
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
			if( ListTools.isNotEmpty( subjectInfoList ) ){
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
			if( ListTools.isNotEmpty( subjectInfoList ) ){
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
			mainSectionIds = business.sectionInfoFactory().listVisibleToAllUserMainSectionIds( forumIds );
			sectionIds = business.sectionInfoFactory().listVisibleToAllUserSectionIds( forumIds, mainSectionIds );
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

	public Boolean acceptReply( String subjectId, String replyId ) throws Exception {
		BBSSubjectInfo subjectInfo = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			subjectInfo = emc.find( subjectId, BBSSubjectInfo.class );
			emc.beginTransaction( BBSSubjectInfo.class );
			if( subjectInfo != null ){
				subjectInfo.setAcceptReplyId( replyId );
				emc.check( subjectInfo, CheckPersistType.all );
			}
			emc.commit();
			return true;
		}catch( Exception e ){
			throw e;
		}
	}

	
}
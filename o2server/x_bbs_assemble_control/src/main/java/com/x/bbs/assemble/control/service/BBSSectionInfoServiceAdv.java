package com.x.bbs.assemble.control.service;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.config.StorageMapping;
import com.x.base.core.project.tools.ListTools;
import com.x.bbs.assemble.common.date.DateOperation;
import com.x.bbs.assemble.control.Business;
import com.x.bbs.assemble.control.ThisApplication;
import com.x.bbs.entity.BBSForumInfo;
import com.x.bbs.entity.BBSPermissionInfo;
import com.x.bbs.entity.BBSPermissionRole;
import com.x.bbs.entity.BBSReplyInfo;
import com.x.bbs.entity.BBSRoleInfo;
import com.x.bbs.entity.BBSSectionInfo;
import com.x.bbs.entity.BBSSubjectAttachment;
import com.x.bbs.entity.BBSSubjectContent;
import com.x.bbs.entity.BBSSubjectInfo;
import com.x.bbs.entity.BBSUserRole;

/**
 * 论坛信息管理服务类
 * @author LIYI
 *
 */
public class BBSSectionInfoServiceAdv {
	private DateOperation dateOperation = new DateOperation();
	private BBSSectionInfoService sectionInfoService = new BBSSectionInfoService();
	
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
	 * 据传入的板块名称从数据库查询BBSSectionInfo对象
	 * @param mainSectionName
	 * @return
	 * @throws Exception
	 */
	public BBSSectionInfo getMainSectionBySectionName( String mainSectionName ) throws Exception {
		if( mainSectionName  == null || mainSectionName.isEmpty() ){
			throw new Exception( "mainSectionName is null!" );
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			return sectionInfoService.getMainSectionBySectionName( emc, mainSectionName);
		}catch( Exception e ){
			throw e;
		}
	}
	
	/**
	 * 向数据库保存BBSSectionInfo对象
	 * @param _bBSSectionInfo
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
	 * @param sectionId
	 * @throws Exception
	 */
	public void delete( String sectionId ) throws Exception {
		
		if( StringUtils.isEmpty( sectionId ) ){
			throw new Exception( "sectionId can not null!" );
		}
		Business business = null;
		BBSForumInfo forumInfo = null;
		BBSSectionInfo sectionInfo = null;
		List<String> ids = null;
		List<BBSRoleInfo> roleInfoList = null;
		List<BBSUserRole> userRoleList = null;
		List<BBSPermissionRole> permissionRoleList = null;
		List<BBSPermissionInfo> permissionInfoList = null;
		
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			
			//删除和该版块有关的一切，主要是权限，贴子和版块都被删除过了
			emc.beginTransaction( BBSForumInfo.class );
			emc.beginTransaction( BBSSectionInfo.class );
			emc.beginTransaction( BBSUserRole.class );
			emc.beginTransaction( BBSPermissionInfo.class );
			emc.beginTransaction( BBSPermissionRole.class );
			emc.beginTransaction( BBSRoleInfo.class );
			
			//删除论坛所有的用户与角色信息关联信息
			ids = business.userRoleFactory().listBySectionId( sectionId, true );
			if( ListTools.isNotEmpty( ids )) {
				userRoleList = business.userRoleFactory().list( ids );
				if( ListTools.isNotEmpty( userRoleList )) {
					for( BBSUserRole userRole : userRoleList ) {
						emc.remove( userRole, CheckRemoveType.all );
					}
				}
			}
			
			//删除权限角色关联信息
			ids = business.permissionRoleFactory().listBySectionId( sectionId, true );
			if( ListTools.isNotEmpty( ids )) {
				permissionRoleList = business.permissionRoleFactory().list( ids );
				if( ListTools.isNotEmpty( permissionRoleList )) {
					for( BBSPermissionRole permissionRole : permissionRoleList ) {
						emc.remove( permissionRole, CheckRemoveType.all );
					}
				}
			}
			
			//删除论坛所有的角色信息
			ids = business.roleInfoFactory().listRoleBySectionId( sectionId, true );
			if( ListTools.isNotEmpty( ids )) {
				roleInfoList = business.roleInfoFactory().list( ids );
				if( ListTools.isNotEmpty( roleInfoList )) {
					for( BBSRoleInfo roleInfo : roleInfoList ) {
						emc.remove( roleInfo, CheckRemoveType.all );
					}
				}
			}
			
			//删除所有的权限信息
			ids = business.permissionInfoFactory().listPermissionBySectionId(sectionId, true );
			if( ListTools.isNotEmpty( ids )) {
				permissionInfoList = business.permissionInfoFactory().list( ids );
				if( ListTools.isNotEmpty( permissionInfoList )) {
					for( BBSPermissionInfo permissionInfo : permissionInfoList ) {
						emc.remove( permissionInfo, CheckRemoveType.all );
					}
				}
			}
			
			sectionInfo = emc.find( sectionId, BBSSectionInfo.class );
			if( sectionInfo != null ) {
				//从论坛信息中减去该版块
				forumInfo = emc.find( sectionInfo.getForumId(), BBSForumInfo.class );
				if( forumInfo != null ) {
					forumInfo.minusSection(1L);
				}
				emc.check( forumInfo, CheckPersistType.all );
				emc.remove( sectionInfo, CheckRemoveType.all );
			}
			emc.commit();
		}catch( Exception e ){
			throw e;
		}
	}

	/**
	 * 强制删除一个版块，或者是主版块
	 * 1、如果是主版块，会删除主版块下的所有子版块
	 * 2、删除子版块、主题、回帖、权限、角色等信息
	 * @param sectionId
	 * @throws Exception 
	 */
	public void deleteForce( String sectionId ) throws Exception {
		if( StringUtils.isEmpty( sectionId ) ){
			throw new Exception( "sectionId can not null!" );
		}
		Business business = null;
		BBSForumInfo forumInfo = null;
		BBSSectionInfo sectionInfo = null;
		BBSSubjectContent subjectContent = null;
		List<String> ids = null;
		List<String> subSectionIds = null;
		List<BBSSectionInfo> sectionList = null;
		List<BBSSubjectInfo> subjectList = null;
		List<BBSSubjectAttachment> attachmentList = null;
		List<BBSReplyInfo> replyList = null;
		List<BBSRoleInfo> roleInfoList = null;
		List<BBSUserRole> userRoleList = null;
		List<BBSPermissionRole> permissionRoleList = null;
		List<BBSPermissionInfo> permissionInfoList = null;
		Long subSectionTotal = 0L;
		Long subjectTotal = 0L;
		Long subjectTotalToday = 0L;
		Long replyTotal = 0L;
		Long replyTotalToday = 0L;
		
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			emc.beginTransaction( BBSForumInfo.class );
			emc.beginTransaction( BBSSectionInfo.class );
			emc.beginTransaction( BBSSubjectInfo.class );
			emc.beginTransaction( BBSSubjectContent.class );
			emc.beginTransaction( BBSSubjectAttachment.class );
			emc.beginTransaction( BBSReplyInfo.class );
			emc.beginTransaction( BBSUserRole.class );
			emc.beginTransaction( BBSPermissionInfo.class );
			emc.beginTransaction( BBSPermissionRole.class );
			emc.beginTransaction( BBSRoleInfo.class );
			
			sectionInfo = emc.find( sectionId, BBSSectionInfo.class );
			if( sectionInfo != null ) {
				subSectionTotal = 1L;
				forumInfo = emc.find( sectionInfo.getForumId(), BBSForumInfo.class );
				
				//查询是否有下级版块，需要查询计数
				subSectionIds = business.sectionInfoFactory().listSubSectionIdsByMainSectionId( sectionId );
				if( ListTools.isNotEmpty( subSectionIds ) ) {
					subSectionTotal += subSectionIds.size();
					sectionList = business.sectionInfoFactory().list(subSectionIds);
				}
				//删除所有的子版块信息
				if( ListTools.isNotEmpty( sectionList )) {
					for( BBSSectionInfo subSectionInfo : sectionList ) {
						emc.remove( subSectionInfo, CheckRemoveType.all );
					}
				}
				//删除版块信息
				emc.remove( sectionInfo, CheckRemoveType.all );
			}

			//查询今天所有的主题贴数量
			subjectTotalToday = business.subjectInfoFactory().countSubjectByDate( sectionId, dateOperation.getTodayStartTime(), dateOperation.getTodayEndTime(), true );
			if( subjectTotalToday == null ) {
				subjectTotalToday = 0L;
			}
			
			//删除该版块涉及的所有主题贴
			ids = business.subjectInfoFactory().listIdsByMainAndSubSectionId( sectionId, true );
			if( ListTools.isNotEmpty( ids )) {
				subjectTotal = Long.parseLong( ids.size() + "" );
				subjectList = business.subjectInfoFactory().list( ids );
				if( ListTools.isNotEmpty( subjectList )) {
					for( BBSSubjectInfo subjectInfo : subjectList ) {
						//删除详细内容
						subjectContent = emc.find( subjectInfo.getId(), BBSSubjectContent.class );
						if( subjectContent != null ) {
							emc.remove( subjectContent, CheckRemoveType.all );
						}
						emc.remove( subjectInfo, CheckRemoveType.all );
					}
				}
			}
			
			//删除该版块涉及的所有主题贴的附件信息
			ids = business.subjectAttachmentFactory().listBySectionId( sectionId, true );
			if( ListTools.isNotEmpty( ids )) {
				attachmentList = business.subjectAttachmentFactory().list( ids );
				if( ListTools.isNotEmpty( attachmentList )) {
					StorageMapping mapping = null;
					for( BBSSubjectAttachment attachment : attachmentList ) {
						mapping = ThisApplication.context().storageMappings().get( BBSSubjectAttachment.class, attachment.getStorage());
						attachment.deleteContent(mapping);
						emc.remove( attachment, CheckRemoveType.all );
					}
				}
			}
			
			//查询今天所有的回贴数量
			replyTotalToday = business.replyInfoFactory().countReplyByDate( sectionId, dateOperation.getTodayStartTime(), dateOperation.getTodayEndTime(), true );
			if( replyTotalToday == null ) {
				replyTotalToday = 0L;
			}
			
			//删除该版块涉及的所有的回贴
			ids = business.replyInfoFactory().listIdsByMainAndSubSectionId( sectionId );
			if( ListTools.isNotEmpty( ids )) {
				replyTotal = Long.parseLong(ids.size() + "");
				replyList = business.replyInfoFactory().list( ids );
				if( ListTools.isNotEmpty( replyList )) {
					for( BBSReplyInfo replyInfo : replyList ) {
						emc.remove( replyInfo, CheckRemoveType.all );
					}
				}
			}
			
			//删除论坛所有的用户与角色信息关联信息（包含主版块）
			ids = business.userRoleFactory().listBySectionId( sectionId, true );
			if( ListTools.isNotEmpty( ids )) {
				userRoleList = business.userRoleFactory().list( ids );
				if( ListTools.isNotEmpty( userRoleList )) {
					for( BBSUserRole userRole : userRoleList ) {
						emc.remove( userRole, CheckRemoveType.all );
					}
				}
			}
			
			//删除权限角色关联信息（包含主版块）
			ids = business.permissionRoleFactory().listBySectionId( sectionId, true );
			if( ListTools.isNotEmpty( ids )) {
				permissionRoleList = business.permissionRoleFactory().list( ids );
				if( ListTools.isNotEmpty( permissionRoleList )) {
					for( BBSPermissionRole permissionRole : permissionRoleList ) {
						emc.remove( permissionRole, CheckRemoveType.all );
					}
				}
			}
			
			//删除论坛所有的角色信息（包含主版块）
			ids = business.roleInfoFactory().listRoleBySectionId( sectionId, true );
			if( ListTools.isNotEmpty( ids )) {
				roleInfoList = business.roleInfoFactory().list( ids );
				if( ListTools.isNotEmpty( roleInfoList )) {
					for( BBSRoleInfo roleInfo : roleInfoList ) {
						emc.remove( roleInfo, CheckRemoveType.all );
					}
				}
			}
			
			//删除所有的权限信息（包含主版块）
			ids = business.permissionInfoFactory().listPermissionBySectionId(sectionId, true );
			if( ListTools.isNotEmpty( ids )) {
				permissionInfoList = business.permissionInfoFactory().list( ids );
				if( ListTools.isNotEmpty( permissionInfoList )) {
					for( BBSPermissionInfo permissionInfo : permissionInfoList ) {
						emc.remove( permissionInfo, CheckRemoveType.all );
					}
				}
			}
			
			if( forumInfo != null ) {
				//从论坛信息中减去该版块数量以及涉及的主题，回帖相关数量
				forumInfo.minusSection( subSectionTotal );
				forumInfo.minusSubjectTotal( subjectTotal );
				forumInfo.minusSubjectTotalToday( subjectTotalToday );
				forumInfo.minusReplyTotal( replyTotal );
				forumInfo.minusReplyTotalToday( replyTotalToday );
				
				emc.check( forumInfo, CheckPersistType.all );
			}
			emc.commit();
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
	 * @param forumId
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
	 * @param sectionId
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
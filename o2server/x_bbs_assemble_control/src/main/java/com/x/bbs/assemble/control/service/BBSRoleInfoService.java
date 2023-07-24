package com.x.bbs.assemble.control.service;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.organization.OrganizationDefinition;
import com.x.base.core.project.tools.ListTools;
import com.x.bbs.assemble.control.Business;
import com.x.bbs.assemble.control.factory.BBSPermissionInfoFactory;
import com.x.bbs.assemble.control.factory.BBSPermissionRoleFactory;
import com.x.bbs.assemble.control.factory.BBSRoleInfoFactory;
import com.x.bbs.assemble.control.jaxrs.roleinfo.bean.BindObject;
import com.x.bbs.entity.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 论坛角色信息管理服务类
 *
 * 论坛角色
 * 论坛访客(FORUM_GUEST):论坛访客
 * 		0、论坛可见（FORUM_VIEW）:用户可以BBS系统中访问该论坛
 *
 * 论坛管理员(FORUM_SUPER_MANAGER):论坛管理员，拥有该论坛管理的最大权限
 *      0、论坛可见（FORUM_VIEW）:用户可以BBS系统中访问该论坛
 * 		1、论坛信息管理（FORUM_INFO_MANAGEMENT）：用户拥有对论坛的版块增加，删除，修改权限
 * 		1、论坛发布主题（FORUM_SUBJECT_PUBLISH）:用户可以在论坛中所有版块发布主题
 * 		2、论坛发表回复（FORUM_REPLY_PUBLISH）：用户可以回复论坛中所有主题
 * 		3、论坛主题推荐（FORUM_SUBJECT_RECOMMEND）:用户可以在指定论坛中所有版块对指定主题进行推荐到论坛首页
 * 		4、论坛主题置顶（FORUM_SUBJECT_STICK）：用户拥有对论坛中所有的主题的置顶权限
 * 		5、论坛主题申精（FORUM_SUBJECT_CREAM）：用户拥有对论坛中所有的主题的精华主题设置权限
 * 		6、论坛主题管理（FORUM_SUBJECT_MANAGEMENT）：用户拥有对论坛中所有的主题的锁定删除权限
 * 		7、论坛回贴管理（FORUM_REPLY_MANAGEMENT）：用户拥有对论坛中所有的回复的删除权限
 * 		8、论坛权限管理（FORUM_PERMISSION_MANAGEMENT）：用户拥有对论坛的用户进行该论坛权限设置的权限
 * 		9、论坛配置管理（FORUM_CONFIG_MANAGEMENT）：用户拥有对论坛的参数配置进行设置的权限
 *
 *
 * 版块角色
 * 版块访客(SECTION_GUEST):版块访客
 * 		0、论坛可见（SECTION_VIEW）:用户可以BBS系统中访问该版块
 *
 * 版块主(SECTION_MANAGER):拥有版块及版块内容管理的最大权限
 *      0、论坛可见（SECTION_VIEW）:用户可以BBS系统中访问该版块
 * 		1、发布主题（SECTION_SUBJECT_PUBLISH）:用户可以在指定版块中发布主题
 *		2、审核主题（SECTION_SUBJECT_AUDIT）:用户可以审核在指定版块中发布的主题，如果主题需要审核
 * 		3、主题管理（SECTION_SUBJECT_MANAGEMENT）:用户可以在指定版块中对已发布主题进行查询删除
 * 		4、发表回复（SECTION_REPLY_PUBLISH）:用户可以在指定版块中对所有主题进行回复
 *		5、审核回复（SECTION_REPLY_AUDIT）:用户可以审核在指定版块中的所有回复内容，如果回复需要审核
 *		6、回贴管理（SECTION_REPLY_MANAGEMENT）:用户可以在指定版块中对回复进行查询或者删除
 * 		7、版块主题推荐（SECTION_SUBJECT_RECOMMEND）:用户可以在指定版块中对指定主题进行推荐操作
 * 		8、版块主题置顶（SECTION_SUBJECT_STICK）:用户可以在指定版块中对指定主题进行置顶操作
 * 		9、版块主题申精（SECTION_SUBJECT_CREAM）:用户可以在指定版块中对指定主题进行精华主题设置操作
 * 		10、版块权限管理（SECTION_PERMISSION_MANAGEMENT）:用户可以对论坛用户进行指定版块的权限管理
 * 		11、版块配置管理（SECTION_CONFIG_MANAGEMENT）:用户可以对指定版块进行系统参数配置修改
 *
 * 主题发布者(SECTION_SUBJECT_PUBLISHER):允许在指定版块内发布主题
 * 		0、论坛可见（SECTION_VIEW）:用户可以BBS系统中访问该版块
 * 		1、发布主题（SECTION_SUBJECT_PUBLISH）:用户可以在指定版块中发布主题
 *
 * 主题回复者(SECTION_REPLY_PUBLISHER):允许在指定版块内发布主题
 * 		0、论坛可见（SECTION_VIEW）:用户可以BBS系统中访问该版块
 * 		1、发表回复（SECTION_REPLY_PUBLISH）:用户可以在指定版块中对所有主题进行回复
 *
 * 主题推荐者(SECTION_RECOMMENDER):允许在指定版块内发布主题
 * 		0、论坛可见（SECTION_VIEW）:用户可以BBS系统中访问该版块
 * 		1、版块主题推荐（SECTION_SUBJECT_RECOMMEND）:用户可以在指定版块中对指定主题进行推荐操作
 *
 * 主题审核者(SECTION_SUBJECT_AUDITOR):允许在指定版块内发布主题
 * 		0、论坛可见（SECTION_VIEW）:用户可以BBS系统中访问该版块
 * 		1、审核主题（SECTION_SUBJECT_AUDIT）:用户可以审核在指定版块中发布的主题，如果主题需要审核
 *
 * 回复审核者(SECTION_REPLY_AUDITOR):允许在指定版块内发布主题
 * 		0、论坛可见（SECTION_VIEW）:用户可以BBS系统中访问该版块
 * 		1、审核回复（SECTION_REPLY_AUDIT）:用户可以审核在指定版块中的所有回复内容，如果回复需要审核 *
 *
 * @author LIYI
 *
 */
public class BBSRoleInfoService {

	private static  Logger logger = LoggerFactory.getLogger( BBSRoleInfoService.class );
	private UserManagerService userManagerService = new UserManagerService();
	/**
	 * 根据ID查询指定的角色信息对象
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public BBSRoleInfo get( String id ) throws Exception {
		if( id == null || id.isEmpty() ){
			throw new Exception("id is null, can not query any role info!");
		}
		Business business = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			return business.roleInfoFactory().get( id );
		}catch( Exception e ){
			logger.warn( "system get role by id got an exception!" );
			throw e;
		}
	}

	/**
	 * 查询所有的角色信息
	 *
	 * @return
	 * @throws Exception
	 */
	public List<BBSRoleInfo> listAllRoleInfo( ) throws Exception {
		Business business = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			return business.roleInfoFactory().listAll();
		}catch( Exception e ){
			logger.warn( "system list all role got an exception!" );
			throw e;
		}
	}

	/**
	 * 根据指定的ID列表查询角色信息
	 * @param ids
	 * @return
	 * @throws Exception
	 */
	public List<BBSRoleInfo> listRoleInfoByIds( List<String> ids ) throws Exception {
		if( ids == null || ids.isEmpty() ){
			throw new Exception("ids is null, can not query any role info!");
		}
		Business business = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			return business.roleInfoFactory().list(ids);
		}catch( Exception e ){
			logger.warn( "system list role by ids got an exception!" );
			throw e;
		}
	}

	public void createForumRole( EffectivePerson effectivePerson, String forumId ) throws Exception {
		BBSForumInfo forumInfo = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			forumInfo = emc.find( forumId, BBSForumInfo.class );
		}catch( Exception e ){
			logger.warn( "system get forum by id got an exception!" );
			throw e;
		}
		if( forumInfo != null ){
			createForumRole( effectivePerson, forumInfo );
		}
	}

	/**
	 * 检查并且创建指定论坛的角色对象
	 * @param _bBSForumInfo
	 * @throws Exception
	 */
	public void createForumRole( EffectivePerson effectivePerson, BBSForumInfo _bBSForumInfo ) throws Exception {
		if( _bBSForumInfo == null ){
			throw new Exception( "forum info is null, can not create any role info!" );
		}
		String forumId = _bBSForumInfo.getId();
		String forumName = _bBSForumInfo.getForumName();
		List<BBSSectionInfo> sectionInfoList = null;
		List<String> permissionCodeList = new ArrayList<String>();
		String sectionId = null;
		Business business = null;
		String personName = null;
		if( effectivePerson == null ) {
			personName = "System";
		}else {
			personName = effectivePerson.getDistinguishedName();
		}

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business( emc );
			/**
			 * 论坛角色信息管理服务类
			 *
			 * 论坛角色
			 * 论坛访客(FORUM_GUEST):论坛访客
			 * 		0、论坛可见（FORUM_VIEW）:用户可以BBS系统中访问该论坛
			 *
			 * 论坛管理员(FORUM_SUPER_MANAGER):论坛管理员，拥有该论坛管理的最大权限
			 *      0、论坛可见（FORUM_VIEW）:用户可以BBS系统中访问该论坛
			 * 		1、论坛信息管理（FORUM_INFO_MANAGEMENT）：用户拥有对论坛的版块增加，删除，修改权限
			 * 		1、论坛发布主题（FORUM_SUBJECT_PUBLISH）:用户可以在论坛中所有版块发布主题
			 * 		2、论坛发表回复（FORUM_REPLY_PUBLISH）：用户可以回复论坛中所有主题
			 * 		3、论坛主题推荐（FORUM_SUBJECT_RECOMMEND）:用户可以在指定论坛中所有版块对指定主题进行推荐到论坛首页
			 * 		4、论坛主题置顶（FORUM_SUBJECT_STICK）：用户拥有对论坛中所有的主题的置顶权限
			 * 		5、论坛主题申精（FORUM_SUBJECT_CREAM）：用户拥有对论坛中所有的主题的精华主题设置权限
			 * 		6、论坛主题管理（FORUM_SUBJECT_MANAGEMENT）：用户拥有对论坛中所有的主题的锁定删除权限
			 * 		7、论坛回贴管理（FORUM_REPLY_MANAGEMENT）：用户拥有对论坛中所有的回复的删除权限
			 * 		8、论坛权限管理（FORUM_PERMISSION_MANAGEMENT）：用户拥有对论坛的用户进行该论坛权限设置的权限
			 * 		9、论坛配置管理（FORUM_CONFIG_MANAGEMENT）：用户拥有对论坛的参数配置进行设置的权限
			 */
			permissionCodeList.clear();
			permissionCodeList.add( "FORUM_VIEW_" + forumId );
			checkAndSaveBBSRoleInfo( emc, personName,
					_bBSForumInfo, "FORUM_GUEST_" + forumId, forumName + "-论坛访客", "系统角色，论坛["+ forumName +"]对指定范围用户可见。",
					permissionCodeList );

			//查询该论坛下有多少版块
			sectionInfoList = business.sectionInfoFactory().listAllSectionByForumId( _bBSForumInfo.getId() );

			//论坛超级管理员(FORUM_SUPER_MANAGER):删除、审核，回复，置顶，精华，推荐等等
			permissionCodeList.clear();
			permissionCodeList.add( "FORUM_VIEW_" + forumId );
			permissionCodeList.add( "FORUM_INFO_MANAGEMENT_" + forumId );
//			permissionCodeList.add( "FORUM_SUBJECT_PUBLISH_" + forumId );
//			permissionCodeList.add( "FORUM_REPLY_PUBLISH_" + forumId );
//			permissionCodeList.add( "FORUM_SUBJECT_RECOMMEND_" + forumId );
//			permissionCodeList.add( "FORUM_SUBJECT_STICK_" + forumId );
//			permissionCodeList.add( "FORUM_SUBJECT_CREAM_" + forumId );
//			permissionCodeList.add( "FORUM_SUBJECT_MANAGEMENT_" + forumId );
//			permissionCodeList.add( "FORUM_REPLY_MANAGEMENT_" + forumId );
			permissionCodeList.add( "FORUM_PERMISSION_MANAGEMENT_" + forumId );
			permissionCodeList.add( "FORUM_CONFIG_MANAGEMENT_" + forumId );
			//该论坛下所有版块的相关权限都要给
			if( sectionInfoList != null ){
				for( BBSSectionInfo section : sectionInfoList ){
					sectionId = section.getId();
					permissionCodeList.add( "SECTION_VIEW_" + sectionId );
					permissionCodeList.add( "SECTION_SUBJECT_PUBLISH_" + sectionId );
					permissionCodeList.add( "SECTION_SUBJECT_AUDIT_" + sectionId );
					permissionCodeList.add( "SECTION_REPLY_PUBLISH_" + sectionId );
					permissionCodeList.add( "SECTION_REPLY_AUDIT_" + sectionId );
					permissionCodeList.add( "SECTION_SUBJECT_RECOMMEND_" + sectionId );
					permissionCodeList.add( "SECTION_SUBJECT_STICK_" + sectionId );
					permissionCodeList.add( "SECTION_SUBJECT_CREAM_" + sectionId );
					permissionCodeList.add( "SECTION_SUBJECT_MANAGEMENT_" + sectionId );
					permissionCodeList.add( "SECTION_REPLY_MANAGEMENT_" + sectionId );
					permissionCodeList.add( "SECTION_INFO_MANAGER_" + sectionId );
					permissionCodeList.add( "SECTION_PERMISSION_MANAGEMENT_" + sectionId );
					permissionCodeList.add( "SECTION_CONFIG_MANAGEMENT_" + sectionId );
				}
			}
			checkAndSaveBBSRoleInfo( emc, personName, _bBSForumInfo, "FORUM_SUPER_MANAGER_" + forumId,
					forumName + "-论坛超级管理员", "系统角色，在论坛["+ forumName +"]中拥有最大管理权限。",
					permissionCodeList );
		}catch( Exception e ){
			logger.warn( "system check and create forum role got an exception!" );
			throw e;
		}
	}

	public void createSectionRole( String creatorName, String sectionId ) throws Exception {
		BBSSectionInfo sectionInfo = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			sectionInfo = emc.find( sectionId, BBSSectionInfo.class );
			if( sectionInfo != null ){
				createSectionRole( creatorName, sectionInfo );
			}
		}catch( Exception e ){
			logger.warn( "system get section by id got an exception!" );
			throw e;
		}
	}
	/**
	 * 检查并且创建指定版块的权限对象
	 * @param _sectionInfo
	 * @throws Exception
	 */
	public void createSectionRole( String creatorName, BBSSectionInfo _sectionInfo ) throws Exception {
		if( _sectionInfo == null ){
			throw new Exception("section info is null, can not create any permission info!");
		}
		String forumName = _sectionInfo.getForumName();
		String sectionId = _sectionInfo.getId();
		String sectionId_tmp = null;
		String sectionName = _sectionInfo.getSectionName();
		List<BBSSectionInfo> sectionInfoList = null;
		List<String> permissionCodeList = new ArrayList<String>();
		Business business = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business( emc );
			//查询该论坛下有多少版块
			sectionInfoList = business.sectionInfoFactory().listSubSectionByMainSectionId( _sectionInfo.getId() );

			permissionCodeList.clear();
			permissionCodeList.add( "SECTION_VIEW_" + sectionId );
			if( sectionInfoList != null ){
				for( BBSSectionInfo section : sectionInfoList ){
					sectionId_tmp = section.getId();
					permissionCodeList.add( "SECTION_VIEW_" + sectionId_tmp );
				}
			}
			checkAndSaveBBSRoleInfo( emc, creatorName,  _sectionInfo, "SECTION_GUEST_" + sectionId, forumName + "-" + sectionName + "-版块访问", "系统角色，版块[" + forumName + "-"+ sectionName +"]对指定范围内的用户可见，并且用户可发布可回复。", permissionCodeList );

			//版主(SECTION_MANAGER):拥有版块及版块内容管理的最大权限
			permissionCodeList.clear();
			permissionCodeList.add( "SECTION_VIEW_" + sectionId );
			permissionCodeList.add( "SECTION_INFO_MANAGER_" + sectionId );
			permissionCodeList.add( "SECTION_SUBJECT_PUBLISH_" + sectionId );
			permissionCodeList.add( "SECTION_SUBJECT_AUDIT_" + sectionId );
			permissionCodeList.add( "SECTION_SUBJECT_MANAGEMENT_" + sectionId );
			permissionCodeList.add( "SECTION_REPLY_PUBLISH_" + sectionId );
			permissionCodeList.add( "SECTION_REPLY_AUDIT_" + sectionId );
			permissionCodeList.add( "SECTION_REPLY_MANAGEMENT_" + sectionId );
			permissionCodeList.add( "SECTION_SUBJECT_RECOMMEND_" + sectionId );
			permissionCodeList.add( "SECTION_SUBJECT_STICK_" + sectionId );
			permissionCodeList.add( "SECTION_SUBJECT_CREAM_" + sectionId );
			permissionCodeList.add( "SECTION_PERMISSION_MANAGEMENT_" + sectionId );
			permissionCodeList.add( "SECTION_CONFIG_MANAGEMENT_" + sectionId );
			//该版块下所有子版块的相关权限都要给
			if( sectionInfoList != null ){
				for( BBSSectionInfo section : sectionInfoList ){
					sectionId_tmp = section.getId();
					permissionCodeList.add( "SECTION_VIEW_" + sectionId_tmp );
					permissionCodeList.add( "SECTION_INFO_MANAGER_" + sectionId_tmp );
					permissionCodeList.add( "SECTION_SUBJECT_PUBLISH_" + sectionId_tmp );
					permissionCodeList.add( "SECTION_SUBJECT_AUDIT_" + sectionId_tmp );
					permissionCodeList.add( "SECTION_SUBJECT_MANAGEMENT_" + sectionId_tmp );
					permissionCodeList.add( "SECTION_REPLY_PUBLISH_" + sectionId_tmp );
					permissionCodeList.add( "SECTION_REPLY_AUDIT_" + sectionId_tmp );
					permissionCodeList.add( "SECTION_REPLY_MANAGEMENT_" + sectionId_tmp );
					permissionCodeList.add( "SECTION_SUBJECT_RECOMMEND_" + sectionId_tmp );
					permissionCodeList.add( "SECTION_SUBJECT_STICK_" + sectionId_tmp );
					permissionCodeList.add( "SECTION_SUBJECT_CREAM_" + sectionId_tmp );
					permissionCodeList.add( "SECTION_PERMISSION_MANAGEMENT_" + sectionId_tmp );
					permissionCodeList.add( "SECTION_CONFIG_MANAGEMENT_" + sectionId_tmp );
				}
			}
			checkAndSaveBBSRoleInfo( emc, creatorName, _sectionInfo, "SECTION_MANAGER_" + sectionId, forumName + "-" + sectionName + "-版主", "系统角色，用户可以在版块[" + forumName + "-"+ sectionName +"]中拥有版块及版块内容管理的最大权限。", permissionCodeList );

			permissionCodeList.clear();
			permissionCodeList.add( "SECTION_VIEW_" + sectionId );
			permissionCodeList.add( "SECTION_SUBJECT_PUBLISH_" + sectionId );
			if( sectionInfoList != null ){
				for( BBSSectionInfo section : sectionInfoList ){
					sectionId_tmp = section.getId();
					permissionCodeList.add( "SECTION_VIEW_" + sectionId_tmp );
					permissionCodeList.add( "SECTION_SUBJECT_PUBLISH_" + sectionId_tmp );
				}
			}
			checkAndSaveBBSRoleInfo( emc, creatorName, _sectionInfo, "SECTION_SUBJECT_PUBLISHER_" + sectionId, forumName + "-" + sectionName + "-主题发布者", "系统角色，用户可以在版块[" + forumName + "-"+ sectionName +"]发布主题。", permissionCodeList );

			permissionCodeList.clear();
			permissionCodeList.add( "SECTION_VIEW_" + sectionId );
			permissionCodeList.add( "SECTION_REPLY_PUBLISH_" + sectionId );
			if( sectionInfoList != null ){
				for( BBSSectionInfo section : sectionInfoList ){
					sectionId_tmp = section.getId();
					permissionCodeList.add( "SECTION_VIEW_" + sectionId_tmp );
					permissionCodeList.add( "SECTION_REPLY_PUBLISH_" + sectionId_tmp );
				}
			}
			checkAndSaveBBSRoleInfo( emc, creatorName, _sectionInfo, "SECTION_REPLY_PUBLISHER_" + sectionId, forumName + "-" + sectionName + "-回复发布者", "系统角色，用户可以在版块[" + forumName + "-"+ sectionName +"]发表回复。", permissionCodeList );

			permissionCodeList.clear();
			permissionCodeList.add( "SECTION_VIEW_" + sectionId );
			permissionCodeList.add( "SECTION_SUBJECT_RECOMMEND_" + sectionId );
			if( sectionInfoList != null ){
				for( BBSSectionInfo section : sectionInfoList ){
					sectionId_tmp = section.getId();
					permissionCodeList.add( "SECTION_VIEW_" + sectionId_tmp );
					permissionCodeList.add( "SECTION_SUBJECT_RECOMMEND_" + sectionId_tmp );
				}
			}
			checkAndSaveBBSRoleInfo( emc, creatorName, _sectionInfo, "SECTION_RECOMMENDER_" + sectionId, forumName + "-" + sectionName + "-主题推荐者", "系统角色，用户可以在版块[" + forumName + "-"+ sectionName +"]对主题进行推荐到主页操作。", permissionCodeList );

			permissionCodeList.clear();
			permissionCodeList.add( "SECTION_VIEW_" + sectionId );
			permissionCodeList.add( "SECTION_SUBJECT_AUDIT_" + sectionId );
			if( sectionInfoList != null ){
				for( BBSSectionInfo section : sectionInfoList ){
					sectionId_tmp = section.getId();
					permissionCodeList.add( "SECTION_VIEW_" + sectionId );
					permissionCodeList.add( "SECTION_SUBJECT_AUDIT_" + sectionId_tmp );
				}
			}
			checkAndSaveBBSRoleInfo( emc, creatorName, _sectionInfo, "SECTION_SUBJECT_AUDITOR_" + sectionId, forumName + "-" + sectionName + "-主题审核者", "系统角色，用户可以在版块[" + forumName + "-"+ sectionName +"]对发布的主题进行审核。", permissionCodeList );

			permissionCodeList.clear();
			permissionCodeList.add( "SECTION_VIEW_" + sectionId );
			permissionCodeList.add( "SECTION_REPLY_AUDIT_" + sectionId );
			if( sectionInfoList != null ){
				for( BBSSectionInfo section : sectionInfoList ){
					sectionId_tmp = section.getId();
					permissionCodeList.add( "SECTION_VIEW_" + sectionId );
					permissionCodeList.add( "SECTION_REPLY_AUDIT_" + sectionId_tmp );
				}
			}
			checkAndSaveBBSRoleInfo( emc, creatorName, _sectionInfo, "SECTION_REPLY_AUDITOR_" + sectionId, forumName + "-" + sectionName + "-回复审核者", "系统角色，用户可以在版块[" + forumName + "-"+ sectionName +"]中对发表的回复进行审核。", permissionCodeList );

		}catch( Exception e ){
			logger.warn( "system check and create section role got an exception!" );
			throw e;
		}
	}

	/**
	 * 根据传入的参数组织角色信息，并且验证角色是否已经存在，如果不存在则新增一个角色信息
	 * @param emc
	 * @param _bBSForumInfo
	 * @param roleCode
	 * @param roleName
	 * @param description
	 * @throws Exception
	 */
	private void checkAndSaveBBSRoleInfo( EntityManagerContainer emc, String creatorName, BBSForumInfo _bBSForumInfo, String roleCode, String roleName, String description, List<String> permissionCodeList ) throws Exception{
		Business business = new Business( emc );
		BBSPermissionInfo permissionInfo  = null;
		BBSPermissionRole permissionRole = null;
		BBSRoleInfoFactory roleInfoFactory = business.roleInfoFactory();
		BBSPermissionInfoFactory permissionInfoFactory = business.permissionInfoFactory();
		BBSPermissionRoleFactory permissionRoleFactory  = business.permissionRoleFactory();
		BBSRoleInfo roleInfo = roleInfoFactory.getRoleByCode( roleCode );
        if( roleInfo == null ){
        	roleInfo = new BBSRoleInfo( creatorName,  _bBSForumInfo.getId(), _bBSForumInfo.getForumName(), null, null, null, null,
        			"论坛角色", roleName, roleCode, description, _bBSForumInfo.getOrderNumber() );
        	emc.beginTransaction( BBSRoleInfo.class );
        	emc.persist( roleInfo, CheckPersistType.all );
        	if( ListTools.isNotEmpty( permissionCodeList ) ){
        		for( String permissionCode : permissionCodeList ){
        			//查询权限是否存在
        			permissionInfo = permissionInfoFactory.getPermissionByCode( permissionCode );
        			if( permissionInfo == null ){
        				continue;
        			}
        			//查询角色和权限的绑定关系是否存在，如果不存在则进行数据增加，此操作只在新增角色的时候进行一次
        			if( !permissionRoleFactory.exsistPermissionRole( roleCode, permissionCode ) ){
        				emc.beginTransaction( BBSPermissionRole.class );
        				permissionRole = new BBSPermissionRole( roleInfo.getForumId(), roleInfo.getForumName(), roleInfo.getSectionId(),
        						roleInfo.getSectionName(), roleInfo.getMainSectionId(), roleInfo.getMainSectionName(), permissionInfo.getPermissionType(),
        						permissionInfo.getPermissionName(), permissionInfo.getPermissionCode(), roleInfo.getId(), roleName, roleCode, description, _bBSForumInfo.getOrderNumber() );
        				emc.persist( permissionRole, CheckPersistType.all );
        			}
        		}
        	}
        	emc.commit();
        }
	}

	/**
	 * 根据传入的参数组织权限信息，并且验证权限是否已经存在，如果不存在则新增一个权限信息
	 * @param emc
	 * @param _sectionInfo
	 * @param roleCode
	 * @param roleName
	 * @param description
	 * @throws Exception
	 */
	private void checkAndSaveBBSRoleInfo( EntityManagerContainer emc, String creatorName, BBSSectionInfo _sectionInfo, String roleCode, String roleName,
			String description, List<String> permissionCodeList ) throws Exception{
		Business business = new Business( emc );
		BBSPermissionInfo permissionInfo  = null;
		BBSPermissionRole permissionRole = null;
		BBSRoleInfoFactory roleInfoFactory = business.roleInfoFactory();
		BBSPermissionInfoFactory permissionInfoFactory = business.permissionInfoFactory();
		BBSPermissionRoleFactory permissionRoleFactory  = business.permissionRoleFactory();
		BBSRoleInfo roleInfo = roleInfoFactory.getRoleByCode( roleCode );
        if( roleInfo == null ){
        	roleInfo = new BBSRoleInfo( creatorName, _sectionInfo.getForumId(), _sectionInfo.getForumName(), _sectionInfo.getId(), _sectionInfo.getSectionName(), _sectionInfo.getMainSectionId(), _sectionInfo.getMainSectionName(),
        			"版块角色", roleName, roleCode, description, _sectionInfo.getOrderNumber() );
        	emc.beginTransaction( BBSRoleInfo.class );
        	emc.persist( roleInfo, CheckPersistType.all );
        	if( ListTools.isNotEmpty( permissionCodeList ) ){
        		for( String permissionCode : permissionCodeList ){
        			//查询权限是否存在
        			permissionInfo = permissionInfoFactory.getPermissionByCode( permissionCode );
        			if( permissionInfo == null ){
        				continue;
        			}
        			//查询角色和权限的绑定关系是否存在，如果不存在则进行数据增加，此操作只在新增角色的时候进行一次
        			if( !permissionRoleFactory.exsistPermissionRole( roleCode, permissionCode ) ){
        				emc.beginTransaction( BBSPermissionRole.class );
        				permissionRole = new BBSPermissionRole( roleInfo.getForumId(), roleInfo.getForumName(), roleInfo.getSectionId(),
        						roleInfo.getSectionName(), roleInfo.getMainSectionId(), roleInfo.getMainSectionName(), permissionInfo.getPermissionType(),
        						permissionInfo.getPermissionName(), permissionInfo.getPermissionCode(), roleInfo.getId(), roleName, roleCode, description, _sectionInfo.getOrderNumber() );
        				emc.persist( permissionRole, CheckPersistType.all );
        			}
        		}
        	}
        	emc.commit();
        }
	}

	public List<BBSRoleInfo> listRoleByForumId( String forumId ) throws Exception {
		if( forumId == null ){
			throw new Exception("forumId is null, can not query any role info!");
		}
		Business business = null;
		List<String> ids = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			ids = business.roleInfoFactory().listRoleByForumId( forumId );
			return business.roleInfoFactory().list(ids);
		}catch( Exception e ){
			logger.warn( "system list role by forum id got an exception!" );
			throw e;
		}
	}

	public List<BBSRoleInfo> listRoleBySectionId( String sectionId ) throws Exception {
		if( sectionId == null ){
			throw new Exception("sectionId is null, can not query any role info!");
		}
		Business business = null;
		List<String> ids = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			ids = business.roleInfoFactory().listRoleBySectionId( sectionId, false );
			return business.roleInfoFactory().list(ids);
		}catch( Exception e ){
			logger.warn( "system list role by section id got an exception!" );
			throw e;
		}
	}

	/**
	 * 根据对象类别和对象的唯一标识来查询对象所关联的角色信息
	 *
	 * @param uniqueId
	 * @param objectType
	 * @return
	 * @throws Exception
	 */
	public List<BBSRoleInfo> listRoleByObjectUniqueId( String uniqueId, String objectType ) throws Exception {
		if( uniqueId == null ){
			throw new Exception("uniqueId is null, can not query any role info!");
		}
		if( objectType == null ){
			throw new Exception("objectType is null, can not query any role info!");
		}
		Business business = null;
		List<String> ids = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			ids = business.userRoleFactory().listRoleIdsByObjectUniqueId( uniqueId, objectType );
			return business.roleInfoFactory().list(ids);
		}catch( Exception e ){
			logger.warn( "system list role by object unique id got an exception!" );
			throw e;
		}
	}

	/**
	 * 根据角色的编码获取角色信息
	 * @param roleCode
	 * @return
	 * @throws Exception
	 */
	public BBSRoleInfo getByRoleCode( String roleCode ) throws Exception {
		if( roleCode == null ){
			throw new Exception("roleCode is null, can not query any role info!");
		}
		Business business = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			return business.roleInfoFactory().getRoleByCode( roleCode );
		}catch( Exception e ){
			logger.warn( "system get role by role code got an exception!" );
			throw e;
		}
	}

	/**
	 * 保存角色信息
	 * 同时保存角色绑定的权限信息
	 * 1、删除无效的权限信息，不在绑定权限列表内的
	 * 2、新增或者更新一个角色信息
	 * 3、判断并且新增需要的角色权限绑定信息
	 * @param _roleInfo
	 * @param permissionCodes
	 * @return
	 * @throws Exception
	 */
	public BBSRoleInfo save( BBSRoleInfo _roleInfo, List<String> permissionCodes ) throws Exception {
		BBSRoleInfo _roleInfo_tmp = null;
		Business business = null;
		BBSPermissionRole permissionRole = null;
		List<String> ids = null;
		List<BBSPermissionInfo> permissionInfoList = null;
		List<BBSPermissionRole> permissionRoleList = null;
		Boolean needDelete = false;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			business = new Business( emc );
			emc.beginTransaction( BBSRoleInfo.class );
			emc.beginTransaction( BBSPermissionInfo.class );
			emc.beginTransaction( BBSPermissionRole.class );

			//=========================================  1、删除无效的权限信息，不在绑定权限列表内的
			//根据角色编码获取所有的权限信息列表
			ids = business.permissionRoleFactory().listPermissionByRoleCode( _roleInfo.getRoleCode() );
			//先删除不需要的绑定关系
			if( ListTools.isNotEmpty( ids ) ){
				permissionRoleList = business.permissionRoleFactory().list( ids );
				if( ListTools.isNotEmpty( permissionRoleList ) ){
					for( BBSPermissionRole _permissionRole : permissionRoleList ){
						needDelete = true;
						for( String permissionCode : permissionCodes ){
							if( permissionCode.equals( _permissionRole.getPermissionCode() )){
								needDelete = false;
							}
						}
						if( needDelete ){
							emc.remove( _permissionRole, CheckRemoveType.all );
						}
					}
				}
			}

			//=========================================  2、新增或者更新一个角色信息
			_roleInfo_tmp = business.roleInfoFactory().getRoleByCode( _roleInfo.getRoleCode() );
			if( _roleInfo_tmp == null ){
				//创建一个新的记录
				if( _roleInfo.getId() == null || _roleInfo.getId().isEmpty() ){
					_roleInfo.setId( BBSRoleInfo.createId() );
				}
				emc.persist( _roleInfo, CheckPersistType.all);
			}else{
				//更新一条记录
				_roleInfo.copyTo( _roleInfo_tmp, JpaObject.FieldsUnmodify  );
				emc.check( _roleInfo_tmp, CheckPersistType.all );
			}

			//=========================================  3、判断并且新增需要的角色权限绑定信息
			//根据权限ID获取所有的权限信息列表
			permissionInfoList = business.permissionInfoFactory().listByPermissionCodes( permissionCodes );
			if( ListTools.isNotEmpty( permissionInfoList ) ){
				for( BBSPermissionInfo permissionInfo : permissionInfoList ){
					//查询绑定指定角色和权限的关系
					permissionRole = business.permissionRoleFactory().getByRoleAndPermission( _roleInfo.getRoleCode(), permissionInfo.getPermissionCode() );
					if( permissionRole == null ){
						permissionRole = new BBSPermissionRole( _roleInfo.getForumId(), _roleInfo.getForumName(), _roleInfo.getSectionId(), _roleInfo.getSectionName(),
								_roleInfo.getMainSectionId(), _roleInfo.getMainSectionName(), permissionInfo.getPermissionType(), permissionInfo.getPermissionName(),
								permissionInfo.getPermissionCode(), _roleInfo.getId(), _roleInfo.getRoleName(), _roleInfo.getRoleCode(), _roleInfo.getDescription(), _roleInfo.getOrderNumber() );
						emc.persist( permissionRole, CheckPersistType.all);
					}
				}
			}

			emc.commit();
		}catch( Exception e ){
			logger.warn( "system find BBSRoleInfo{'id':'"+_roleInfo.getId()+"'} got an exception!" );
			throw e;
		}
		return _roleInfo;
	}

	public void delete( String id ) throws Exception {
		BBSRoleInfo roleInfo = null;
		List<String> ids = null;
		List<BBSPermissionRole> permissionRoleList = null;
		List<BBSUserRole> uerRoleList = null;
		Business business = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			business = new Business( emc );

			emc.beginTransaction( BBSRoleInfo.class );
			emc.beginTransaction( BBSPermissionRole.class );
			emc.beginTransaction( BBSUserRole.class );

			roleInfo = emc.find( id, BBSRoleInfo.class );
			if( roleInfo == null ){
				throw new Exception("role info not exists, can not excute delete.");
			}
			//角色信息存在，删除角色信息，删除角色信息与权限的绑定，删除角色信息与用户和组织群组的绑定
			ids = business.permissionRoleFactory().listPermissionByRoleCode( roleInfo.getRoleCode() );
			if( ids != null ){
				permissionRoleList = business.permissionRoleFactory().list(ids);
				if( ListTools.isNotEmpty( permissionRoleList ) ){
					//全部删除
					for( BBSPermissionRole permissionRole : permissionRoleList ){
						emc.remove( permissionRole, CheckRemoveType.all );
					}
				}
			}
			ids = business.userRoleFactory().listIdsByRoleCode( roleInfo.getRoleCode() );
			if( ids != null ){
				uerRoleList = business.userRoleFactory().list(ids);
				if( ListTools.isNotEmpty( uerRoleList ) ){
					//全部删除
					for( BBSUserRole userRole : uerRoleList ){
						emc.remove( userRole, CheckRemoveType.all );
					}
				}
			}
			emc.remove( roleInfo, CheckRemoveType.all );
			emc.commit();
		}catch( Exception e ){
			logger.warn( "system delete BBSRoleInfo{'id':'"+ id +"'} got an exception!" );
			throw e;
		}
	}

	public void bindRoleToUser( BindObject bindObject, List<String> bindRoleCodes ) throws Exception {
		if( bindObject == null ){
			throw new Exception("bindObject is null, can not bind role!");
		}
		List<String> ids = null;
		BBSUserRole userRole = null;
		BBSRoleInfo roleInfo = null;
		Business business = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			business = new Business( emc );
			emc.beginTransaction( BBSUserRole.class );
			//先删该用户的所有角色绑定
			ids = business.userRoleFactory().listRoleIdsByObjectUniqueId( bindObject.getObjectName(), bindObject.getObjectType() );
			for( String id : ids ){
				userRole = emc.find( id, BBSUserRole.class );
				if( userRole != null ){
					emc.remove( userRole, CheckRemoveType.all );
				}
			}
			if( ListTools.isNotEmpty( bindRoleCodes ) ){
				for( String bindRoleCode : bindRoleCodes ){
					roleInfo = business.roleInfoFactory().getRoleByCode( bindRoleCode );
					if( roleInfo != null ){
						userRole = new BBSUserRole();
						userRole.setForumId( roleInfo.getForumId() );
						userRole.setMainSectionId( roleInfo.getMainSectionId() );
						userRole.setSectionId( roleInfo.getSectionId() );
						userRole.setObjectName( bindObject.getObjectName() );
						userRole.setObjectType( bindObject.getObjectType() );
						userRole.setRoleCode( roleInfo.getRoleCode() );
						userRole.setRoleId( roleInfo.getId() );
						userRole.setRoleName( roleInfo.getRoleName() );
						userRole.setUniqueId( bindObject.getObjectName() );
						userRole.setOrderNumber( userRole.getOrderNumber() );
						emc.persist( userRole, CheckPersistType.all );
					}
				}
			}
			emc.commit();
		}catch( Exception e ){
			logger.warn( "system bindRoleToUser got an exception!" );
			throw e;
		}
	}

	public void bindUserToRole( String bindRoleCode, List<BindObject> bindObjects ) throws Exception {
		if( bindRoleCode == null || bindRoleCode.isEmpty() ){
			throw new Exception("bindRoleCode is null, can not query any role info!");
		}
		List<String> ids = null;
		BBSUserRole userRole = null;
		BBSRoleInfo roleInfo = null;
		Business business = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			business = new Business( emc );
			emc.beginTransaction( BBSUserRole.class );
			roleInfo = business.roleInfoFactory().getRoleByCode( bindRoleCode );
			if( roleInfo == null ){
				throw new Exception( "roleInfo not exists!bindRoleCode = " + bindRoleCode );
			}

			//先删该用户的所有角色绑定
			ids = business.userRoleFactory().listIdsByRoleCode( bindRoleCode );
			for( String id : ids ){
				userRole = emc.find( id, BBSUserRole.class );
				if( userRole != null ){
					emc.remove( userRole, CheckRemoveType.all );
				}
			}

			if( ListTools.isNotEmpty( bindObjects )){
				String unitName = null;
				String topUnitName = null;
				for( BindObject bindObject : bindObjects ){
					userRole = new BBSUserRole();
					userRole.setForumId( roleInfo.getForumId() );
					userRole.setMainSectionId( roleInfo.getMainSectionId() );
					userRole.setSectionId( roleInfo.getSectionId() );
					userRole.setObjectName( bindObject.getObjectName() );
					userRole.setObjectType( bindObject.getObjectType() );
					userRole.setRoleCode( roleInfo.getRoleCode() );
					userRole.setRoleId( roleInfo.getId() );
					userRole.setRoleName( roleInfo.getRoleName() );
					userRole.setOrderNumber( userRole.getOrderNumber() );
					userRole.setUniqueId( bindObject.getObjectName() );
					if(OrganizationDefinition.isPersonDistinguishedName(bindObject.getObjectName())) {
						try {
							unitName = userManagerService.getUnitNameWithPerson(bindObject.getObjectName());
						} catch (Exception e) {
							logger.warn("user has no identity!user:" + bindObject.getObjectName());
							unitName = "未知组织";
						}
						try {
							topUnitName = userManagerService.getTopUnitNameWithPerson(bindObject.getObjectName());
						} catch (Exception e) {
							logger.warn("user has no identity!user:" + bindObject.getObjectName());
							topUnitName = "未知公司";
						}
					}else if(OrganizationDefinition.isUnitDistinguishedName(bindObject.getObjectName())){
						unitName = bindObject.getObjectName();
					}

					userRole.setTopUnitName( topUnitName );
					userRole.setUnitName( unitName );
					emc.persist( userRole, CheckPersistType.all );
				}
			}
			emc.commit();
		}catch( Exception e ){
			logger.warn( "system bindUserToRole got an exception!" );
			throw e;
		}
	}

	/**
	 * 根据用户姓名获取用户所有的角色信息列表
	 * 1、查询用户所在的组织，以及上级组织
	 * 2、查询用户所在的顶层组织
	 * 3、查询用户所在的所有群组
	 * 查询用户所有的组织，顶层组织和群组所关联的所有角色列表
	 * @param userName
	 * @return
	 * @throws Exception
	 */
	public List<BBSRoleInfo> listAllRoleForUser( String userName ) throws Exception {
		if( userName == null ){
			throw new Exception("userName is null, can not query any role info!");
		}
		//1、查询用户所在的组织信息，需要递归查询上级组织信息
		Business business = null;
		List<String> objectUniqueIds = new ArrayList<>();
		List<String> unitNameList = new ArrayList<>();
		List<String> ids = null;
		List<String> groupNameList = null;

		objectUniqueIds.add( userName );

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);

			unitNameList = userManagerService.listUnitNamesWithPerson( userName );
			groupNameList = userManagerService.listGroupNamesSupNestedWithPerson(userName);

			//然后把组织名称，群组名称放到同一个LIST里供查询使用
			if( ListTools.isNotEmpty( unitNameList ) ){
				for( String unitName : unitNameList ){
					objectUniqueIds.add( unitName );
				}
			}
			if( ListTools.isNotEmpty( groupNameList ) ){
				for( String groupName : groupNameList ){
					objectUniqueIds.add( groupName );
				}
			}
			ids = business.userRoleFactory().listRoleIdsByObjectUnique( objectUniqueIds );

			return business.roleInfoFactory().list(ids);
		} catch ( Exception e ) {
			throw e;
		}
	}

	public List<String> listAllRoleCodesForUser(String userName) throws Exception {
		if( userName == null ){
			throw new Exception("userName is null, can not query any role info!");
		}
		//1、查询用户所在的组织信息，需要递归查询上级组织信息
		Business business = null;
		List<String> objectUniqueIds = new ArrayList<>();
		List<String> unitNameList = new ArrayList<>();
		List<String> groupNameList = null;

		objectUniqueIds.add( userName );

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);

			unitNameList = userManagerService.listUnitSupNestedWithPerson( userName );
			groupNameList = userManagerService.listGroupNamesSupNestedWithPerson(userName);

			//然后把组织名称,群组名称放到同一个LIST里供查询使用
			if( ListTools.isNotEmpty( unitNameList ) ){
				for( String unitName : unitNameList ){
					objectUniqueIds.add( unitName );
				}
			}
			if( ListTools.isNotEmpty( groupNameList ) ){
				for( String groupName : groupNameList ){
					objectUniqueIds.add( groupName );
				}
			}
			return  business.userRoleFactory().listRoleCodesByObjectUnique( objectUniqueIds );
		} catch ( Exception e ) {
			throw e;
		}
	}

	public List<BBSUserRole> listUserRoleByRoleCode(String roleCode) throws Exception {
		Business business = null;
		List<String> ids = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			ids = business.userRoleFactory().listIdsByRoleCode(roleCode);
			if( ListTools.isNotEmpty( ids )){
				return business.userRoleFactory().list(ids);
			}else{
				return null;
			}
		} catch ( Exception e ) {
			throw e;
		}
	}

}

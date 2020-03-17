package com.x.bbs.assemble.control.service;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.bbs.assemble.control.Business;
import com.x.bbs.assemble.control.factory.BBSPermissionInfoFactory;
import com.x.bbs.entity.BBSForumInfo;
import com.x.bbs.entity.BBSPermissionInfo;
import com.x.bbs.entity.BBSSectionInfo;

/**
 * 论坛权限信息管理服务类
 * 
 * 权限列表：
       论坛权限：
              论坛可见（FORUM_VIEW）:用户可以BBS系统中访问该论坛
              //论坛发布主题（FORUM_SUBJECT_PUBLISH）:用户可以在论坛中所有版块发布主题
              //论坛发表回复（FORUM_REPLY_PUBLISH）：用户可以回复论坛中所有主题
              //论坛主题推荐（FORUM_SUBJECT_RECOMMEND）:用户可以在指定论坛中所有版块对指定主题进行推荐到论坛首页
              //论坛主题置顶（FORUM_SUBJECT_STICK）：用户拥有对论坛中所有的主题的置顶权限
              //论坛主题申精（FORUM_SUBJECT_CREAM）：用户拥有对论坛中所有的主题的精华主题设置权限
              //论坛主题管理（FORUM_SUBJECT_MANAGEMENT）：用户拥有对论坛中所有的主题的锁定删除权限
              //论坛回贴管理（FORUM_REPLY_MANAGEMENT）：用户拥有对论坛中所有的回复的删除权限
              论坛版块管理（FORUM_INFO_MANAGEMENT）：用户拥有对论坛的版块增加，删除，修改权限
              论坛权限管理（FORUM_PERMISSION_MANAGEMENT）：用户拥有对论坛的用户进行该论坛权限设置的权限
              论坛配置管理（FORUM_CONFIG_MANAGEMENT）：用户拥有对论坛的参数配置进行设置的权限

       版块权限：
              论坛可见（SECTION_VIEW）:用户可以BBS系统中访问该版块
              发布主题（SECTION_SUBJECT_PUBLISH）:用户可以在指定版块中发布主题
              审核主题（SECTION_SUBJECT_AUDIT）:用户可以审核在指定版块中发布的主题，如果主题需要审核
              主题管理（SECTION_SUBJECT_MANAGEMENT）:用户可以在指定版块中对已发布主题进行查询删除
              发表回复（SECTION_REPLY_PUBLISH）:用户可以在指定版块中对所有主题进行回复
              审核回复（SECTION_REPLY_AUDIT）:用户可以审核在指定版块中的所有回复内容，如果回复需要审核
              回贴管理（SECTION_REPLY_MANAGEMENT）:用户可以在指定版块中对回复进行查询或者删除
              版块主题推荐（SECTION_SUBJECT_RECOMMEND）:用户可以在指定版块中对指定主题进行推荐操作
              版块主题置顶（SECTION_SUBJECT_STICK）:用户可以在指定版块中对指定主题进行置顶操作
              版块主题申精（SECTION_SUBJECT_CREAM）:用户可以在指定版块中对指定主题进行精华主题设置操作
              版块管理（SECTION_SECTION_MANAGER）:用户可以在指定版块中对子版块进行创建和删除等操作
              版块权限管理（SECTION_PERMISSION_MANAGEMENT）:用户可以对论坛用户进行指定版块的权限管理
              版块配置管理（SECTION_CONFIG_MANAGEMENT）:用户可以对指定版块进行系统参数配置修改
              
 * @author LIYI
 *
 */
public class BBSPermissionInfoService {
	
	private UserManagerService userManagerService = new UserManagerService();
	private static  Logger logger = LoggerFactory.getLogger( BBSPermissionInfoService.class );
	
	/**
	 * 根据角色CODE查询角色所绑定的所有权限信息列表
	 * @param roleCode
	 * @return
	 * @throws Exception
	 */
	public List<BBSPermissionInfo> listPermissionByRoleCode( String roleCode ) throws Exception {
		if( roleCode == null ){
			throw new Exception("roleCode is null, can not query any permission info!");
		}
		Business business = null;
		List<String> ids = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			ids = business.permissionRoleFactory().listPermissionByRoleCode( roleCode );
			if( ListTools.isNotEmpty( ids ) ){
				return business.permissionInfoFactory().list(ids);
			}else{
				return null;
			}
		}catch( Exception e ){
			logger.warn( "system list permission by role code got an exception!" );
			throw e;
		}
	}
	
	/**
	 * 根据论坛ID查询指定论坛所有权限信息列表
	 * @param roleCode
	 * @return
	 * @throws Exception
	 */
	public List<BBSPermissionInfo> listPermissionByForumId( String forumId ) throws Exception {
		if( forumId == null ){
			throw new Exception("forumId is null, can not query any permission info!");
		}
		Business business = null;
		List<String> ids = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			ids = business.permissionInfoFactory().listPermissionByForumId( forumId );
			if( ListTools.isNotEmpty( ids ) ){
				return business.permissionInfoFactory().list(ids);
			}else{
				return null;
			}
		}catch( Exception e ){
			logger.warn( "system list permission by forum id got an exception!" );
			throw e;
		}
	}
	
	/**
	 * 根据论坛ID查询指定版块所有权限信息列表
	 * @param roleCode
	 * @return
	 * @throws Exception
	 */
	public List<BBSPermissionInfo> listPermissionBySection( String sectionId ) throws Exception {
		if( sectionId == null ){
			throw new Exception("sectionId is null, can not query any permission info!");
		}
		Business business = null;
		List<String> ids = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			ids = business.permissionInfoFactory().listPermissionBySectionId( sectionId, false );
			if( ListTools.isNotEmpty( ids ) ){
				return business.permissionInfoFactory().list(ids);
			}else{
				return null;
			}
		}catch( Exception e ){
			logger.warn( "system list permission by section id got an exception!" );
			throw e;
		}
	}
	
	/**
	 * 查询所有的权限列表
	 * @return
	 * @throws Exception
	 */
	public List<BBSPermissionInfo> listAllPermissionInfo( ) throws Exception {
		Business business = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			return business.permissionInfoFactory().listAll();
		}catch( Exception e ){
			logger.warn( "system list all permission got an exception!" );
			throw e;
		}
	}
	
	/**
	 * 根据指定的ID列表查询指定的权限信息列表
	 * @param ids
	 * @return
	 * @throws Exception
	 */
	public List<BBSPermissionInfo> listPermissionByIds( List<String> ids ) throws Exception {
		if( ids == null || ids.isEmpty() ){
			throw new Exception("ids is null, can not query any permission info!");
		}
		Business business = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			return business.permissionInfoFactory().list(ids);
		}catch( Exception e ){
			logger.warn( "system list permission by ids got an exception!" );
			throw e;
		}
	}
	
	/**
	 * 检查并且创建指定论坛的权限对象
	 * @param _bBSForumInfo
	 * @throws Exception
	 */
	public void createForumPermission( BBSForumInfo _bBSForumInfo ) throws Exception {
		if( _bBSForumInfo == null ){
			throw new Exception("forum info is null, can not create any permission info!");
		}
		String forumId = _bBSForumInfo.getId();
		String forumName = _bBSForumInfo.getForumName();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			//论坛可见（FORUM_VIEW）:用户可以BBS系统中访问该论坛
			checkAndSaveBBSPermissionInfo( emc, _bBSForumInfo, "FORUM_VIEW", "FORUM_VIEW_" + forumId, "论坛["+forumName+"]可见", "论坛[" + forumName + "]可以被用户进行访问，可见" );			
			//论坛发布主题（FORUM_SUBJECT_PUBLISH）:用户可以在论坛中所有版块发布主题
			//checkAndSaveBBSPermissionInfo( emc, _bBSForumInfo, "FORUM_SUBJECT_PUBLISH", "FORUM_SUBJECT_PUBLISH_" + forumId, "论坛["+forumName+"]发布主题", "在论坛[" + forumName + "]中用户可以在所有版块中发布主题" );			
			//论坛发表回复（FORUM_REPLY_PUBLISH）：用户可以回复论坛中所有主题
			//checkAndSaveBBSPermissionInfo( emc, _bBSForumInfo, "FORUM_REPLY_PUBLISH", "FORUM_REPLY_PUBLISH_" + forumId, "论坛["+forumName+"]发表回复", "在论坛[" + forumName + "]中用户可以回复所有主题" );			
            //论坛主题推荐（FORUM_SUBJECT_RECOMMEND）:用户可以在指定论坛中所有版块对指定主题进行推荐到论坛首页
			//checkAndSaveBBSPermissionInfo( emc, _bBSForumInfo, "FORUM_SUBJECT_RECOMMEND", "FORUM_SUBJECT_RECOMMEND_" + forumId, "论坛["+forumName+"]主题推荐", "在论坛[" + forumName + "]中用户可以在所有版块对指定主题进行推荐到论坛首页" );            
            //论坛主题置顶（FORUM_SUBJECT_STICK）：用户拥有对论坛中所有的主题的置顶权限
			//checkAndSaveBBSPermissionInfo( emc, _bBSForumInfo, "FORUM_SUBJECT_STICK", "FORUM_SUBJECT_STICK_" + forumId, "论坛["+forumName+"]主题置顶", "在论坛[" + forumName + "]中用户拥有所有的主题的置顶权限" );            
            //论坛主题申精（FORUM_SUBJECT_CREAM）：用户拥有对论坛中所有的主题的精华主题设置权限
            //checkAndSaveBBSPermissionInfo( emc, _bBSForumInfo, "FORUM_SUBJECT_CREAM", "FORUM_SUBJECT_CREAM_" + forumId, "论坛["+forumName+"]主题申精", "在论坛[" + forumName + "]中用户拥有所有的主题的精华主题设置权限" );            
            //论坛主题管理（FORUM_SUBJECT_MANAGEMENT）：用户拥有对论坛中所有的主题的锁定删除权限
            //checkAndSaveBBSPermissionInfo( emc, _bBSForumInfo, "FORUM_SUBJECT_MANAGEMENT", "FORUM_SUBJECT_MANAGEMENT_" + forumId, "论坛["+forumName+"]主题管理", "在论坛[" + forumName + "]中用用户拥有所有的主题的锁定删除权限" );            
            //论坛回贴管理（FORUM_REPLY_MANAGEMENT）：用户拥有对论坛中所有的回复的删除权限
            //checkAndSaveBBSPermissionInfo( emc, _bBSForumInfo, "FORUM_REPLY_MANAGEMENT", "FORUM_REPLY_MANAGEMENT_" + forumId, "论坛["+forumName+"]回贴管理", "在论坛[" + forumName + "]中用户拥有所有的回复的删除权限" );            
            //论坛版块管理（FORUM_INFO_MANAGEMENT）：用户拥有对论坛的版块增加，删除，修改权限
            checkAndSaveBBSPermissionInfo( emc, _bBSForumInfo, "FORUM_INFO_MANAGEMENT", "FORUM_INFO_MANAGEMENT_" + forumId, "论坛["+forumName+"]版块管理", "在论坛[" + forumName + "]中用户拥有对版块增加，删除，修改权限" );            
            //论坛权限管理（FORUM_PERMISSION_MANAGEMENT）：用户拥有对论坛的用户进行该论坛权限设置的权限
            checkAndSaveBBSPermissionInfo( emc, _bBSForumInfo, "FORUM_PERMISSION_MANAGEMENT", "FORUM_PERMISSION_MANAGEMENT_" + forumId, "论坛["+forumName+"]权限管理", "在论坛[" + forumName + "]中用户拥有对论坛用户进行该论坛权限设置的权限" );            
            //论坛配置管理（FORUM_CONFIG_MANAGEMENT）：用户拥有对论坛的参数配置进行设置的权限
            checkAndSaveBBSPermissionInfo( emc, _bBSForumInfo, "FORUM_CONFIG_MANAGEMENT", "FORUM_CONFIG_MANAGEMENT_" + forumId, "论坛["+forumName+"]配置管理", "在论坛[" + forumName + "]中用户拥有对论坛的参数配置进行设置的权限" );            
		}catch( Exception e ){
			logger.warn( "system check and create forum permission got an exception!" );
			throw e;
		}
	}
	
	/**
	 * 检查并且创建指定版块的权限对象
	 * @param _sectionInfo
	 * @throws Exception
	 */
	public void createSectionPermission( BBSSectionInfo _sectionInfo ) throws Exception {
		if( _sectionInfo == null ){
			throw new Exception("section info is null, can not create any permission info!");
		}
		String forumName = _sectionInfo.getForumName();
		String sectionId = _sectionInfo.getId();
		String sectionName = _sectionInfo.getSectionName();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			//论坛可见（SECTION_VIEW）:用户可以BBS系统中访问该版块
			checkAndSaveBBSPermissionInfo( emc, _sectionInfo, "SECTION_VIEW", "SECTION_VIEW_" + sectionId, "版块["+forumName+"-"+sectionName+"]可见", "版块["+forumName+"-"+sectionName+"]可以被用户进行访问，可见。" );
			//发布主题（SECTION_SUBJECT_PUBLISH）:用户可以在指定版块中发布主题
			checkAndSaveBBSPermissionInfo( emc, _sectionInfo, "SECTION_SUBJECT_PUBLISH", "SECTION_SUBJECT_PUBLISH_" + sectionId, "版块["+forumName+"-"+sectionName+"]发布主题", "用户可以在版块["+forumName+"-"+sectionName+"]中发布主题" );
            //审核主题（SECTION_SUBJECT_AUDIT）:用户可以审核在指定版块中发布的主题，如果主题需要审核
			checkAndSaveBBSPermissionInfo( emc, _sectionInfo, "SECTION_SUBJECT_AUDIT", "SECTION_SUBJECT_AUDIT_" + sectionId, "版块["+forumName+"-"+sectionName+"]审核主题", "用户可以审核在版块["+forumName+"-"+sectionName+"]中发布的主题，如果主题需要审核" );
            //主题管理（SECTION_SUBJECT_MANAGEMENT）:用户可以在指定版块中对已发布主题进行查询删除
			checkAndSaveBBSPermissionInfo( emc, _sectionInfo, "SECTION_SUBJECT_MANAGEMENT", "SECTION_SUBJECT_MANAGEMENT_" + sectionId, "版块["+forumName+"-"+sectionName+"]主题管理", "用户可以在版块["+forumName+"-"+sectionName+"]中对已发布主题进行查询删除" );
            //发表回复（SECTION_REPLY_PUBLISH）:用户可以在指定版块中对所有主题进行回复
			checkAndSaveBBSPermissionInfo( emc, _sectionInfo, "SECTION_REPLY_PUBLISH", "SECTION_REPLY_PUBLISH_" + sectionId, "版块["+forumName+"-"+sectionName+"]发表回复", "用户可以在版块["+forumName+"-"+sectionName+"]中对所有主题进行回复" );
            //审核回复（SECTION_REPLY_AUDIT）:用户可以审核在指定版块中的所有回复内容，如果回复需要审核
			checkAndSaveBBSPermissionInfo( emc, _sectionInfo, "SECTION_REPLY_AUDIT", "SECTION_REPLY_AUDIT_" + sectionId, "版块["+forumName+"-"+sectionName+"]审核回复", "用户可以审核在版块["+forumName+"-"+sectionName+"]中的所有回复内容，如果回复需要审核" );
            //回贴管理（SECTION_REPLY_MANAGEMENT）:用户可以在指定版块中对回复进行查询或者删除
			checkAndSaveBBSPermissionInfo( emc, _sectionInfo, "SECTION_REPLY_MANAGEMENT", "SECTION_REPLY_MANAGEMENT_" + sectionId, "版块["+forumName+"-"+sectionName+"]回贴管理", "用户可以在版块["+forumName+"-"+sectionName+"]中对回复进行查询或者删除" );
            //版块主题推荐（SECTION_SUBJECT_RECOMMEND）:用户可以在指定版块中对指定主题进行推荐操作
			checkAndSaveBBSPermissionInfo( emc, _sectionInfo, "SECTION_SUBJECT_RECOMMEND", "SECTION_SUBJECT_RECOMMEND_" + sectionId, "版块["+forumName+"-"+sectionName+"]主题推荐", "用户可以在版块["+forumName+"-"+sectionName+"]中对指定主题进行推荐操作" );
            //版块主题置顶（SECTION_SUBJECT_STICK）:用户可以在指定版块中对指定主题进行置顶操作
			checkAndSaveBBSPermissionInfo( emc, _sectionInfo, "SECTION_SUBJECT_STICK", "SECTION_SUBJECT_STICK_" + sectionId, "版块["+forumName+"-"+sectionName+"]主题置顶", "用户可以在版块["+forumName+"-"+sectionName+"]中对指定主题进行置顶操作" );
            //版块主题申精（SECTION_SUBJECT_CREAM）:用户可以在指定版块中对指定主题进行精华主题设置操作
			checkAndSaveBBSPermissionInfo( emc, _sectionInfo, "SECTION_SUBJECT_CREAM", "SECTION_SUBJECT_CREAM_" + sectionId, "版块["+forumName+"-"+sectionName+"]主题申精", "用户可以在版块["+forumName+"-"+sectionName+"]中对指定主题进行精华主题设置操作" );
            //版块管理（SECTION_SECTION_MANAGER）:用户可以在指定版块中对子版块进行创建和删除等操作
			checkAndSaveBBSPermissionInfo( emc, _sectionInfo, "SECTION_INFO_MANAGER", "SECTION_INFO_MANAGER_" + sectionId, "版块["+forumName+"-"+sectionName+"]信息管理", "用户可以在版块["+forumName+"-"+sectionName+"]中对版块进行信息维护，子版块创建和删除等操作" );
			//版块权限管理（SECTION_PERMISSION_MANAGEMENT）:用户可以对论坛用户进行指定版块的权限管理
			checkAndSaveBBSPermissionInfo( emc, _sectionInfo, "SECTION_PERMISSION_MANAGEMENT", "SECTION_PERMISSION_MANAGEMENT_" + sectionId, "版块["+forumName+"-"+sectionName+"]权限管理", "用户可以对用户进行版块["+forumName+"-"+sectionName+"]的权限管理" );
            //版块配置管理（SECTION_CONFIG_MANAGEMENT）:用户可以对指定版块进行系统参数配置修改
			checkAndSaveBBSPermissionInfo( emc, _sectionInfo, "SECTION_CONFIG_MANAGEMENT", "SECTION_CONFIG_MANAGEMENT_" + sectionId, "版块["+forumName+"-"+sectionName+"]配置管理", "用户可以对版块["+forumName+"-"+sectionName+"]进行系统参数配置修改" );
		}catch( Exception e ){
			logger.warn( "system check and create section permission got an exception!" );
			throw e;
		}
	}
	
	/**
	 * 根据传入的参数组织权限信息，并且验证权限是否已经存在，如果不存在则新增一个权限信息
	 * @param emc
	 * @param _bBSForumInfo
	 * @param permissionType
	 * @param permissionCode
	 * @param permissionName
	 * @param description
	 * @throws Exception
	 */
	private void checkAndSaveBBSPermissionInfo( EntityManagerContainer emc, BBSForumInfo _bBSForumInfo, String permissionFunction, String permissionCode, String permissionName, String description ) throws Exception{
		Business business = new Business( emc );
		BBSPermissionInfoFactory permissionInfoFactory = business.permissionInfoFactory();
		BBSPermissionInfo permissionInfo = permissionInfoFactory.getPermissionByCode( permissionCode );
        if( permissionInfo == null ){
        	permissionInfo = new BBSPermissionInfo(  _bBSForumInfo.getId(), _bBSForumInfo.getForumName(), null, null, null, null, "论坛权限",  permissionName, permissionFunction, permissionCode,  description, _bBSForumInfo.getOrderNumber() );
        	emc.beginTransaction( BBSPermissionInfo.class );
        	emc.persist( permissionInfo, CheckPersistType.all );
        	emc.commit();
        }
	}
	
	/**
	 * 根据传入的参数组织权限信息，并且验证权限是否已经存在，如果不存在则新增一个权限信息
	 * @param emc
	 * @param _sectionInfo
	 * @param permissionCode
	 * @param permissionName
	 * @param description
	 * @throws Exception
	 */
	private void checkAndSaveBBSPermissionInfo( EntityManagerContainer emc, BBSSectionInfo _sectionInfo, String permissionFunction, String permissionCode, String permissionName, String description ) throws Exception{
		Business business = new Business( emc );
		BBSPermissionInfoFactory permissionInfoFactory = business.permissionInfoFactory();
		BBSPermissionInfo permissionInfo = permissionInfoFactory.getPermissionByCode( permissionCode );
        if( permissionInfo == null ){
        	permissionInfo = new BBSPermissionInfo(  _sectionInfo.getForumId(), _sectionInfo.getForumName(), _sectionInfo.getId(), _sectionInfo.getSectionName(), _sectionInfo.getMainSectionId(), _sectionInfo.getMainSectionName(), 
        			"版块权限",  permissionName, permissionFunction, permissionCode,  description, _sectionInfo.getOrderNumber() );
        	emc.beginTransaction( BBSPermissionInfo.class );
        	emc.persist( permissionInfo, CheckPersistType.all );
        	emc.commit();
        }
	}
	
	public List<BBSPermissionInfo> filterPermissionListByPermissionFunction(String permissionFunction, List<BBSPermissionInfo> permissionList) throws Exception {
		if( permissionFunction  == null || permissionFunction.isEmpty() ){
			throw new Exception( "permissionFunction is null, return null!" );
		}
		List<BBSPermissionInfo> resultList = new ArrayList<BBSPermissionInfo>();
		if( ListTools.isNotEmpty( permissionList ) ){			
			for( BBSPermissionInfo permission : permissionList ){
				if( permissionFunction.equals( permission.getPermissionFunction() )){
					resultList.add( permission );
				}
			}
		}
		return resultList;
	}

	public List<BBSPermissionInfo> listPermissionByRoleCodes( List<String> roleCodes ) throws Exception {
		if( roleCodes  == null || roleCodes.isEmpty() ){
			throw new Exception( "roleIds is null, return null!" );
		}
		Business business = null;
		List<String> permissionCodes = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			permissionCodes = business.permissionRoleFactory().listPermissionCodesByRoleCodes( roleCodes );
			return business.permissionInfoFactory().listByPermissionCodes(permissionCodes);
		}catch( Exception e ){
			logger.warn( "system list permission by ids got an exception!" );
			throw e;
		}
	}
	
	public List<String> listPermissionCodesByRoleCodes( List<String> roleCodes ) throws Exception {
		if( roleCodes  == null || roleCodes.isEmpty() ){
			throw new Exception( "roleIds is null, return null!" );
		}
		Business business = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			return business.permissionRoleFactory().listPermissionCodesByRoleCodes( roleCodes );
		}catch( Exception e ){
			logger.warn( "system list permission by ids got an exception!" );
			throw e;
		}
	}
	
	/**
	 * 根据用户姓名获取系统分配给用户的所有权限列表
	 * @param userName
	 * @return
	 * @throws Exception
	 */
	public List<BBSPermissionInfo> listAllPermissionForUser( String userName ) throws Exception {
		if( userName  == null || userName.isEmpty() ){
			throw new Exception( "userName is null, return null!" );
		}
		Business business = null;
		List<String> permissionCodes = null;
		List<String> roleCodes = null;
		List<String> groups = null;
		List<String> units = null;
		List<String> user_units = new ArrayList<>();
		List<String> uniqueIds = new ArrayList<String>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			
			//将用户的姓名放入唯一标识列表里
			uniqueIds.add( userName );
			
			//查询用户所属的组织以及上级组织等等
			units = userManagerService.listUnitNamesWithPerson( userName );
			if( ListTools.isNotEmpty( units ) ){
				for( String unit : units ){
					user_units = listSuperUnit( unit, user_units );
				}
			}
			if( ListTools.isNotEmpty( user_units ) ){
				for( String unit : user_units ){
					if( uniqueIds.contains( unit ) ){
						uniqueIds.add( unit );
					}
				}
			}			
			//查询用户所属的群组
			groups = userManagerService.listGroupNamesSupNestedWithPerson( userName ); 
			if( ListTools.isNotEmpty( groups ) ){
				for( String group : groups){
					if( !uniqueIds.contains( group ) ){
						uniqueIds.add( group );
					}
				}
			}			
			//先查询用户被分配了多少角色，查询角色列表
			roleCodes = business.userRoleFactory().listRoleCodesByObjectUnique( uniqueIds );			
			if( ListTools.isNotEmpty( roleCodes ) ){
				//再查询所有角色中包括的权限ID列表
				permissionCodes = business.permissionRoleFactory().listPermissionCodesByRoleCodes( roleCodes );
			}
			if( ListTools.isNotEmpty( permissionCodes ) ){
				//最后权限所有的权限ID列表查询出权限对象信息，并且返回
				return business.permissionInfoFactory().listByPermissionCodes( permissionCodes );
			}
			return null;
		}catch( Exception e ){
			logger.warn( "system list permission by username got an exception!" );
			throw e;
		}
	}
	
	/**
	 * 将组织对象以及上级组织对象放入组织列表里
	 * @param unit
	 * @param unitList
	 * @return
	 * @throws Exception
	 */
	public List<String> listSuperUnit( String unit,  List<String> unitList ) throws Exception {
		if( unitList == null ){
			unitList = new ArrayList<>();
		}
		if( unit != null ){
			//将组织信息放入组织信息列表里
			unitList.add( unit );
			List<String> units = null;
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				units = userManagerService.listSupUnitNameWithParent( unit );
				if( ListTools.isNotEmpty( units ) ){
					for( String unit_super : units ){
						unitList.add( unit_super );
					}
				}
			}catch( Exception e ){
				logger.warn( "system list sup nested by unit got an exception!" );
				throw e;
			}
		}
		return unitList;
	}

	public List<BBSPermissionInfo> listPermissionByCodes(List<String> permissionCodes) throws Exception {
		if( permissionCodes == null || permissionCodes.isEmpty() ){
			throw new Exception("permissionCodes is null, can not query any permission info!");
		}
		Business business = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			return business.permissionInfoFactory().listByPermissionCodes(permissionCodes);
		}catch( Exception e ){
			logger.warn( "system list permission by codes got an exception!" );
			throw e;
		}
	}	
}
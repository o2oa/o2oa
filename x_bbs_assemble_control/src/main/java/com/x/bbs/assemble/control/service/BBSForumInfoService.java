package com.x.bbs.assemble.control.service;

import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.bbs.assemble.control.Business;
import com.x.bbs.entity.BBSForumInfo;
import com.x.bbs.entity.BBSPermissionInfo;
import com.x.bbs.entity.BBSPermissionRole;
import com.x.bbs.entity.BBSRoleInfo;
import com.x.bbs.entity.BBSUserRole;
import com.x.organization.core.express.wrap.WrapDepartment;

/**
 * 论坛信息管理服务类
 * @author LIYI
 *
 */
public class BBSForumInfoService {
	
	/**
	 * 向数据库保存BBSForumInfo对象
	 * @param wrapIn
	 */
	public BBSForumInfo save( EntityManagerContainer emc, BBSForumInfo _bBSForumInfo ) throws Exception {
		BBSForumInfo _bBSForumInfo_tmp = null;
		if( _bBSForumInfo == null ){
			throw new Exception("forum info is null!");
		}
		if( _bBSForumInfo.getId() == null ){
			_bBSForumInfo.setId( BBSForumInfo.createId() );
		}
		_bBSForumInfo_tmp = emc.find( _bBSForumInfo.getId(), BBSForumInfo.class );
		if( _bBSForumInfo_tmp == null ){
			//创建一个新的记录
			emc.beginTransaction( BBSForumInfo.class );
			emc.persist( _bBSForumInfo, CheckPersistType.all);	
			emc.commit();
		}else{
			//更新一条记录
			emc.beginTransaction( BBSForumInfo.class );
			_bBSForumInfo.copyTo( _bBSForumInfo_tmp );
			emc.check( _bBSForumInfo_tmp, CheckPersistType.all );	
			emc.commit();
		}
		return _bBSForumInfo;
	}
	
	/**
	 * 根据ID从数据库中删除BBSForumInfo对象
	 * @param id
	 * @throws Exception
	 */
	public void delete( EntityManagerContainer emc, String id ) throws Exception {
		BBSForumInfo bBSForumInfo = null;
		Business business = null;
		List<String> ids = null;
		List<BBSPermissionInfo> permissionList = null;
		List<BBSPermissionRole> permissionRoleList = null;
		if( id == null || id.isEmpty() ){
			throw new Exception( "id is null, system can not delete any object." );
		}
		business = new Business( emc );
		//先判断需要操作的应用信息是否存在，根据ID进行一次查询，如果不存在不允许继续操作
		bBSForumInfo = emc.find( id, BBSForumInfo.class );
		emc.beginTransaction( BBSForumInfo.class );
		emc.beginTransaction( BBSPermissionRole.class );
		emc.beginTransaction( BBSPermissionInfo.class );
		
		ids = business.permissionInfoFactory().listPermissionByForumId( bBSForumInfo.getId() );
		if( ids != null ){
			permissionList = business.permissionInfoFactory().list( ids );
			if( permissionList != null && !permissionList.isEmpty() ){
				for( BBSPermissionInfo permissionInfo : permissionList ){
					permissionRoleList = business.permissionRoleFactory().listByPermissionCode( permissionInfo.getPermissionCode() );
					if( permissionRoleList != null && permissionRoleList.size() > 0 ){
						for( BBSPermissionRole permissionRole : permissionRoleList ){
							emc.remove( permissionRole, CheckRemoveType.all );
						}
					}
					emc.remove( permissionInfo, CheckRemoveType.all );
				}
			}
		}
		emc.remove( bBSForumInfo, CheckRemoveType.all );
		emc.commit();
	}

	public List<BBSForumInfo> listAll( EntityManagerContainer emc ) throws Exception {
		Business business = new Business( emc );
		return business.forumInfoFactory().listAll();
	}

	/**
	 * 根据权限查询所有我能访问到的论坛信息列表
	 * 1、所有全员可访问的论坛信息
	 * 2、所有我有权限访问的论坛信息
	 * 
	 * @return
	 * @throws Exception 
	 */
	public List<BBSForumInfo> listAllViewAbleForumWithUserPermission( EntityManagerContainer emc, List<String> viewAbleForumIds ) throws Exception {
		Business business = new Business( emc );
		return business.forumInfoFactory().listAllViewAbleForumWithMyPermission( viewAbleForumIds );
	}

	public void checkForumManager( EntityManagerContainer emc, BBSForumInfo forumInfo ) throws Exception {
		if( forumInfo == null ){
			throw new Exception( "forumInfo is null!" );
		}
		String[] currentManagerNames = null;
		List<String> ids = null;
		List<BBSUserRole> userRoleList= null;
		List<WrapDepartment> departments = null;
		String superDepartment = null;
		BBSRoleInfo roleInfo = null;
		BBSUserRole userRole_new = null;
		Business business = null;
		Boolean exists = false;
		if( forumInfo != null && forumInfo.getForumManagerName() != null && !forumInfo.getForumManagerName().isEmpty() ){
			currentManagerNames = forumInfo.getForumManagerName().split(",");
		}
		business = new Business( emc );
		emc.beginTransaction( BBSUserRole.class );
		roleInfo = business.roleInfoFactory().getRoleByCode( "FORUM_SUPER_MANAGER_" + forumInfo.getId() );
		if( roleInfo == null ){
			throw new Exception("role info{'code':'"+"FORUM_SUPER_MANAGER_" + forumInfo.getId()+"'} is not exists.");
		}
		ids = business.userRoleFactory().listIdsByRoleCode( "FORUM_SUPER_MANAGER_" + forumInfo.getId() );
		if( ids != null && !ids.isEmpty() ){
			userRoleList = business.userRoleFactory().list( ids );
		}
		if( userRoleList != null && !userRoleList.isEmpty() ){
			for( BBSUserRole userRole : userRoleList ){
				exists = false;
				if( currentManagerNames != null && currentManagerNames.length > 0 ){
					for( String name : currentManagerNames ){
						if( name.equals( userRole.getObjectName()) || name.equalsIgnoreCase( userRole.getUniqueId() )){
							exists = true;
						}
					}
				}
				if( !exists ){
					emc.remove( userRole, CheckRemoveType.all );
				}
			}
		}
		if( currentManagerNames != null && currentManagerNames.length > 0 ){
			for( String name : currentManagerNames ){
				exists = false;
				if( userRoleList != null && !userRoleList.isEmpty() ){
					for( BBSUserRole userRole : userRoleList ){
						if( name.equals( userRole.getObjectName()) || name.equalsIgnoreCase( userRole.getUniqueId() )){
							exists = true;
						}
					}
				}
				if( !exists ){
					userRole_new = new BBSUserRole();
					userRole_new.setObjectName( name );
					userRole_new.setUniqueId( name );
					userRole_new.setObjectType( "人员" ); //人员|组织|群组
					userRole_new.setRoleCode( roleInfo.getRoleCode() );
					userRole_new.setRoleId( roleInfo.getId() );
					userRole_new.setRoleName( roleInfo.getRoleName() );
					departments = business.organization().department().listWithPerson( name );
					if( departments != null && !departments.isEmpty() ){
						userRole_new.setCompanyName( departments.get(0).getCompany() );
						userRole_new.setDepartmentName( departments.get(0).getName() );
						superDepartment = departments.get(0).getSuperior();
						if( superDepartment == null || superDepartment.isEmpty() ){
							superDepartment = departments.get(0).getName();
						}
						userRole_new.setOrganizationName( superDepartment );
					}						
					emc.persist( userRole_new, CheckPersistType.all );
				}
			}
		}
		emc.commit();
	}

	public void deleteForumManager( EntityManagerContainer emc, String id ) throws Exception {
		String roleCode = "FORUM_SUPER_MANAGER_" + id;
		Business business = null;
		List<String> ids = null;
		List<BBSUserRole> userRoleList = null;
		business = new Business( emc );
		ids = business.userRoleFactory().listIdsByRoleCode(roleCode);
		if( ids != null && !ids.isEmpty() ){
			userRoleList = business.userRoleFactory().list(ids);
		}
		if( userRoleList != null && !userRoleList.isEmpty() ){
			emc.beginTransaction( BBSUserRole.class );
			for( BBSUserRole userRole : userRoleList ){
				emc.remove( userRole, CheckRemoveType.all );
			}
			emc.commit();
		}
	}

	public List<String> listAllPublicForumIds( EntityManagerContainer emc ) throws Exception {
		Business business = new Business( emc );
		return business.forumInfoFactory().listAllPublicForumIds();
	}

	public void sectionCountMinus( EntityManagerContainer emc, String forumId, int i ) throws Exception {
		BBSForumInfo forumInfo = emc.find( forumId, BBSForumInfo.class );
		if( forumInfo != null ){
			emc.beginTransaction( BBSForumInfo.class );
			if( forumInfo.getSectionTotal() > 0 ){
				forumInfo.setSectionTotal( forumInfo.getSectionTotal() - 1 );
			}else{
				forumInfo.setSectionTotal( 0L );
			}
			emc.commit();
		}
	}

	public void subjectCountMinus( EntityManagerContainer emc, String forumId, Long subjectTotal ) throws Exception {
		BBSForumInfo forumInfo = emc.find( forumId, BBSForumInfo.class );
		if( forumInfo != null ){
			emc.beginTransaction( BBSForumInfo.class );
			if( forumInfo.getSubjectTotal() > subjectTotal ){
				forumInfo.setSubjectTotal( forumInfo.getSubjectTotal() - subjectTotal );
			}else{
				forumInfo.setSubjectTotal( 0L );
			}
			emc.commit();
		}
	}
	
	public void replyCountMinus( EntityManagerContainer emc, String forumId, Long replyTotal ) throws Exception {
		BBSForumInfo forumInfo = emc.find( forumId, BBSForumInfo.class );
		if( forumInfo != null ){
			emc.beginTransaction( BBSForumInfo.class );
			if( forumInfo.getReplyTotal() > replyTotal ){
				forumInfo.setReplyTotal( forumInfo.getReplyTotal() - replyTotal );
			}else{
				forumInfo.setReplyTotal( 0L );
			}
			emc.commit();
		}
	}

	public void subjectTodayCountMinus(EntityManagerContainer emc, String forumId, Long subjectTotalToday) throws Exception {
		BBSForumInfo forumInfo = emc.find( forumId, BBSForumInfo.class );
		if( forumInfo != null ){
			emc.beginTransaction( BBSForumInfo.class );
			if( forumInfo.getSubjectTotalToday() > subjectTotalToday ){
				forumInfo.setSubjectTotalToday( forumInfo.getSubjectTotalToday() - subjectTotalToday );
			}else{
				forumInfo.setSubjectTotalToday( 0L );
			}
			emc.commit();
		}
	}

	public void replyTodayCountMinus(EntityManagerContainer emc, String forumId, Long replyTotalToday) throws Exception {
		BBSForumInfo forumInfo = emc.find( forumId, BBSForumInfo.class );
		if( forumInfo != null ){
			emc.beginTransaction( BBSForumInfo.class );
			if( forumInfo.getReplyTotalToday() > replyTotalToday ){
				forumInfo.setReplyTotalToday( forumInfo.getReplyTotalToday() - replyTotalToday );
			}else{
				forumInfo.setReplyTotalToday( 0L );
			}
			emc.commit();
		}
	}
}
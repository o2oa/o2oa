package com.x.cms.assemble.control.service;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.cms.assemble.control.Business;
import com.x.cms.assemble.control.jaxrs.documentpermission.element.PermissionInfo;
import com.x.cms.common.date.DateOperation;
import com.x.cms.core.entity.AppCategoryAdmin;
import com.x.cms.core.entity.Document;
import com.x.cms.core.entity.DocumentPermission;

public class DocumentPermissionService {

	public List<DocumentPermission> list( EntityManagerContainer emc, List<String> ids ) throws Exception {
		if( ids == null || ids.isEmpty() ){
			return null;
		}
		Business business = new Business( emc );
		return business.documentPermissionFactory().list( ids );
	}

	public void deleteByDocumentId( EntityManagerContainer emc, String docId ) throws Exception {
		if( docId == null ){
			throw new Exception("文档对象document为空！");
		}
		List<String> ids = null;
		DocumentPermission permission = null;
		ids = new Business( emc ).documentPermissionFactory().listIdsByDocumentId( docId );
		if( ids != null && ids.isEmpty() ){
			for( String id : ids ){
				permission = emc.find( id, DocumentPermission.class );
				emc.beginTransaction( DocumentPermission.class );
				emc.remove( permission, CheckRemoveType.all );
				emc.commit();
			}
		}
	}
	
	public void refreshDocumentPermission( EntityManagerContainer emc, Document document, List<PermissionInfo> new_permissionList ) throws Exception {
		if( document == null ){
			throw new Exception("文档对象document为空！");
		}
		if( new_permissionList == null ){
			new_permissionList = new ArrayList<>();
		}
		String updateFlag = new DateOperation().getNowTimeChar();
		List<String> ids = null;
		String docId = document.getId();
		DocumentPermission documentPermission = null;
		//List<AppCategoryAdmin> adminList = null;
		List<DocumentPermission> permissionList = new ArrayList<>();
		
		Business business = new Business( emc );		
		
		//拟稿人应该有管理和阅读的权限
		permissionList = getAppInfoDocumentPermission( document.getCreatorPerson(), document, permissionList, updateFlag );
		
		/**
		//======2017-03-08 已经修改为栏目和分类管理可以看到指定的分类中所有的文档，不需要通过权限过滤，所以不用再加管理员的管理和阅读权限了
		//文档所在应用栏目所有的管理员有管理和阅读的权限
		//查出该文档所在应用栏目所有的管理员信息
		ids = business.getAppCategoryAdminFactory().listAppCategoryIdByAppId( document.getAppId() );
		adminList = business.getAppCategoryAdminFactory().list( ids );
		permissionList = getAppInfoDocumentPermission( adminList, document, permissionList, updateFlag );
		
		//文档所在分类所有的管理员有管理和阅读的权限
		//查出该文档所在分类所有的管理员信息
		ids = business.getAppCategoryAdminFactory().listAppCategoryIdByCategoryId( document.getCategoryId() );
		adminList = business.getAppCategoryAdminFactory().list( ids );
		permissionList = getAppInfoDocumentPermission( adminList, document, permissionList, updateFlag );	
		**/
		
		for( PermissionInfo permission : new_permissionList ){
			documentPermission = new DocumentPermission();
			documentPermission.setDocumentId( document.getId() );
			documentPermission.setTitle( document.getTitle() );
			documentPermission.setAppId( document.getAppId() );
			documentPermission.setAppName( document.getAppName());
			documentPermission.setCategoryId( document.getCategoryId() );
			documentPermission.setCategoryName( document.getCategoryName() );
			documentPermission.setCategoryAlias( document.getCategoryAlias() );
			documentPermission.setDocumentStatus( document.getDocStatus() );
			
			documentPermission.setPermission( permission.getPermission() );
			documentPermission.setPermissionObjectName( permission.getPermissionObjectName() );
			documentPermission.setPermissionObjectType( permission.getPermissionObjectType() );
			documentPermission.setPermissionObjectCode( permission.getPermissionObjectName() );
			
			documentPermission.setDocCreateDate( document.getCreateTime() );
			documentPermission.setPublishDate( document.getPublishTime() );
			documentPermission.setPublisher( document.getCreatorPerson() );
			documentPermission.setUpdateFlag( updateFlag );
			
			permissionList.add( documentPermission );
		}
		
		if( permissionList != null && !permissionList.isEmpty() ){
			for( DocumentPermission permission : permissionList ){
				ids = business.documentPermissionFactory().listIds( docId, permission.getPermission(), permission.getPermissionObjectType(), permission.getPermissionObjectCode() );
				emc.beginTransaction( DocumentPermission.class );
				if( ids == null || ids.isEmpty() ){
					documentPermission.setUpdateFlag( updateFlag ); //修改更新标识
					documentPermission.setCategoryAlias( document.getCategoryAlias() );
					emc.persist( permission, CheckPersistType.all );
				}else{
					for( String id: ids ){
						documentPermission = emc.find( id, DocumentPermission.class );
						documentPermission.setTitle( document.getTitle() );
						documentPermission.setUpdateFlag( updateFlag ); //修改更新标识
						documentPermission.setCategoryAlias( document.getCategoryAlias() );
						emc.check( documentPermission, CheckPersistType.all );
					}
				}
				emc.commit();
			}
		}
		//把没有被更新过的信息删除
		ids = business.documentPermissionFactory().listNoModifyIds( docId, updateFlag );
		if( ids != null && !ids.isEmpty() ){
			for( String id: ids ){
				documentPermission = emc.find( id, DocumentPermission.class );
				emc.beginTransaction( DocumentPermission.class );
				emc.remove( documentPermission, CheckRemoveType.all );
				emc.commit();
			}
		}
	}	
	
	public List<DocumentPermission> getAppInfoDocumentPermission( List<AppCategoryAdmin> adminList, Document document, List<DocumentPermission> permissionList, String updateFlag ) throws Exception{
		DocumentPermission new_permission = null;
		if( document == null ){
			throw new Exception("文档信息为空！");
		}
		if( permissionList == null ){
			permissionList = new ArrayList<>();
		}	
		if( adminList != null ){
			for( AppCategoryAdmin admin : adminList ){
				new_permission = new DocumentPermission();
				new_permission.setDocumentId( document.getId() );
				new_permission.setTitle( document.getTitle() );
				new_permission.setAppId( document.getAppId() );
				new_permission.setAppName( document.getAppName());
				new_permission.setCategoryId( document.getCategoryId() );
				new_permission.setCategoryName( document.getCategoryName() );
				new_permission.setDocumentStatus( document.getDocStatus() );
				new_permission.setPermission( "管理" );
				new_permission.setPermissionObjectCode( admin.getAdminUid() );
				new_permission.setPermissionObjectName( admin.getAdminName() );
				new_permission.setPermissionObjectType( "人员" );
				new_permission.setDocCreateDate( document.getCreateTime() );
				new_permission.setPublishDate( document.getPublishTime() );
				new_permission.setPublisher( document.getCreatorPerson() );
				new_permission.setUpdateFlag(updateFlag);
				permissionList.add( new_permission );
				
				new_permission = new DocumentPermission();
				new_permission.setDocumentId( document.getId() );
				new_permission.setTitle( document.getTitle() );
				new_permission.setAppId( document.getAppId() );
				new_permission.setAppName( document.getAppName());
				new_permission.setCategoryId( document.getCategoryId() );
				new_permission.setCategoryName( document.getCategoryName() );
				new_permission.setDocumentStatus( document.getDocStatus() );
				new_permission.setPermission( "阅读" );
				new_permission.setPermissionObjectCode( admin.getAdminUid() );
				new_permission.setPermissionObjectName( admin.getAdminName() );
				new_permission.setPermissionObjectType( "人员" );
				new_permission.setDocCreateDate( document.getCreateTime() );
				new_permission.setPublishDate( document.getPublishTime() );
				new_permission.setPublisher( document.getCreatorPerson() );
				new_permission.setUpdateFlag(updateFlag);
				permissionList.add( new_permission );
			}
		}		
		return permissionList;
	}
	
	public List<DocumentPermission> getAppInfoDocumentPermission( String personName, Document document, List<DocumentPermission> permissionList, String updateFlag ) throws Exception{
		if( document == null ){
			throw new Exception("文档信息为空！");
		}
		if( personName == null ){
			throw new Exception("人员姓名为空！");
		}
		if( permissionList == null ){
			permissionList = new ArrayList<>();
		}		
		DocumentPermission new_permission = null;

		new_permission = new DocumentPermission();
		new_permission.setDocumentId( document.getId() );
		new_permission.setTitle( document.getTitle() );
		new_permission.setAppId( document.getAppId() );
		new_permission.setAppName( document.getAppName());
		new_permission.setCategoryId( document.getCategoryId() );
		new_permission.setCategoryName( document.getCategoryName() );
		new_permission.setDocumentStatus( document.getDocStatus() );
		new_permission.setPermission( "管理" );
		new_permission.setPermissionObjectCode( personName );
		new_permission.setPermissionObjectName( personName );
		new_permission.setPermissionObjectType( "人员" );
		new_permission.setDocCreateDate( document.getCreateTime() );
		new_permission.setPublishDate( document.getPublishTime() );
		new_permission.setPublisher( document.getCreatorPerson() );
		new_permission.setUpdateFlag(updateFlag);
		permissionList.add( new_permission );
		
		new_permission = new DocumentPermission();
		new_permission.setDocumentId( document.getId() );
		new_permission.setTitle( document.getTitle() );
		new_permission.setAppId( document.getAppId() );
		new_permission.setAppName( document.getAppName());
		new_permission.setCategoryId( document.getCategoryId() );
		new_permission.setCategoryName( document.getCategoryName() );
		new_permission.setDocumentStatus( document.getDocStatus() );
		new_permission.setPermission( "阅读" );
		new_permission.setPermissionObjectCode( personName );
		new_permission.setPermissionObjectName( personName );
		new_permission.setPermissionObjectType( "人员" );
		new_permission.setDocCreateDate( document.getCreateTime() );
		new_permission.setPublishDate( document.getPublishTime() );
		new_permission.setPublisher( document.getCreatorPerson() );
		new_permission.setUpdateFlag(updateFlag);
		permissionList.add( new_permission );
		
		return permissionList;
	}
}

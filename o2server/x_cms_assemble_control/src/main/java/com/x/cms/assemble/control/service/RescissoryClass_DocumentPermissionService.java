package com.x.cms.assemble.control.service;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.cms.assemble.control.Business;
import com.x.cms.assemble.control.jaxrs.permission.element.PermissionInfo;
import com.x.cms.assemble.control.jaxrs.permission.element.PermissionName;
import com.x.cms.core.entity.Document;
import com.x.cms.core.entity.DocumentPermission;
import com.x.cms.core.entity.tools.DateOperation;

public class RescissoryClass_DocumentPermissionService {

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
			throw new Exception( "document is null！" );
		}
		if( new_permissionList == null ){
			new_permissionList = new ArrayList<>();
		}
		List<String> ids = null;
		String docId = document.getId();
		DocumentPermission documentPermission = null;
		List<DocumentPermission> permissionList = new ArrayList<>();
		String updateFlag = new DateOperation().getNowTimeChar();
		
		Business business = new Business( emc );		
		
		//拟稿人应该有管理和阅读的权限
		permissionList = addPersonDocumentPermission( document.getCreatorPerson(), PermissionName.MANAGER, document, permissionList, updateFlag );
		permissionList = addPersonDocumentPermission( document.getCreatorPerson(),  PermissionName.READER, document, permissionList, updateFlag );
		permissionList = addPersonDocumentPermission( document.getCreatorPerson(),  PermissionName.AUTHOR, document, permissionList, updateFlag );
				
		//将传入的权限信息转换成权限对象
		for( PermissionInfo permission : new_permissionList ){
			documentPermission = createPermissionWithDocument( document, permission.getPermission(), permission.getPermissionObjectName(), permission.getPermissionObjectType(), permission.getPermissionObjectCode() );	
			documentPermission.setUpdateFlag( updateFlag );
			permissionList.add( documentPermission );
		}
		
		if( permissionList != null && !permissionList.isEmpty() ){
			for( DocumentPermission permission : permissionList ){
				ids = business.documentPermissionFactory().listIds( docId, permission.getPermission(), permission.getPermissionObjectType(), permission.getPermissionObjectCode() );
				emc.beginTransaction( DocumentPermission.class );
				if( ids == null || ids.isEmpty() ){
					permission.setUpdateFlag( updateFlag ); //修改更新标识
					permission.setCategoryAlias( document.getCategoryAlias() );
					emc.persist( permission, CheckPersistType.all );
				}else{
					for( String id: ids ){
						permission = emc.find( id, DocumentPermission.class );
						permission.setTitle( document.getTitle() );
						permission.setUpdateFlag( updateFlag ); //修改更新标识
						permission.setCategoryAlias( document.getCategoryAlias() );
						emc.check( permission, CheckPersistType.all );
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
		
		//使用新的数据结构存储  Begin=================================================
		document = emc.find( document.getId() , Document.class );
		
		emc.beginTransaction( Document.class );
		
		document.setManagerList( null );
		document.setReadPersonList( null );
		document.setReadUnitList( null );
		document.setReadGroupList( null );
		document.setAuthorPersonList( null );
		document.setAuthorUnitList( null );
		document.setAuthorGroupList( null );
		
		document.addManagerList( document.getCreatorPerson() );
		document.addReadPersonList( document.getCreatorPerson() );
		document.addAuthorPersonList( document.getCreatorPerson() );
		
		if( permissionList != null && !permissionList.isEmpty() ){
			for( DocumentPermission permission : permissionList ){
				if( "管理".equals( permission.getPermission() )) {
					document.addManagerList(permission.getPermissionObjectCode());
				}else if( "读者".equals( permission.getPermission() )) {
					if( "人员".equals( permission.getPermissionObjectType() )) {
						document.addReadPersonList(permission.getPermissionObjectCode());
					}else if( "部门".equals(  permission.getPermissionObjectType() )) {
						document.addReadUnitList(permission.getPermissionObjectCode());
					}else if( "群组".equals( permission.getPermissionObjectType() )) {
						document.addReadGroupList(permission.getPermissionObjectCode());
					}else if( "所有人".equals( permission.getPermissionObjectCode() )) {
						document.addReadPersonList("所有人");
					}
				}else if( "阅读".equals( permission.getPermission() )) {
					if( "人员".equals( permission.getPermissionObjectType() )) {
						document.addReadPersonList(permission.getPermissionObjectCode());
					}else if( "部门".equals(  permission.getPermissionObjectType() )) {
						document.addReadUnitList(permission.getPermissionObjectCode());
					}else if( "群组".equals( permission.getPermissionObjectType() )) {
						document.addReadGroupList(permission.getPermissionObjectCode());
					}else if( "所有人".equals( permission.getPermissionObjectCode() )) {
						document.addReadPersonList("所有人");
					}
				}else if( "作者".equals( permission.getPermission() )) {
					if( "人员".equals( permission.getPermissionObjectType() )) {
						document.addAuthorPersonList(permission.getPermissionObjectCode());
					}else if( "部门".equals(  permission.getPermissionObjectType() )) {
						document.addAuthorUnitList(permission.getPermissionObjectCode());
					}else if( "群组".equals( permission.getPermissionObjectType() )) {
						document.addAuthorGroupList(permission.getPermissionObjectCode());
					}else if( "所有人".equals( permission.getPermissionObjectCode() )) {
						document.addAuthorPersonList("所有人");
					}
				}
			}
		}
		
		emc.check( document , CheckPersistType.all );
		//使用新的数据结构存储  Over=================================================		
	}

	/**
	 * 给用户管理和阅读权限
	 * @param personName
	 * @param document
	 * @param permissionList
	 * @param updateFlag
	 * @return
	 * @throws Exception
	 */
	public List<DocumentPermission> addPersonDocumentPermission( String personName, String permission, Document document, List<DocumentPermission> permissionList, String updateFlag ) throws Exception{
		if( document == null ){
			throw new Exception("document is null！");
		}
		if( personName == null ){
			throw new Exception("personName is null！");
		}
		if( permissionList == null ){
			permissionList = new ArrayList<>();
		}		
		DocumentPermission new_permission = createPermissionWithDocument( document, permission, personName, "人员", personName );
		new_permission.setUpdateFlag(updateFlag);
		permissionList.add( new_permission );
		return permissionList;
	}

	public List<String> listDocumentPermission( EntityManagerContainer emc, String docId, List<String> permissionCodes,
			String permission) throws Exception {
		if( docId == null || docId.isEmpty() ){
			throw new Exception("docId is null!");
		}
		if( permission == null || permission.isEmpty() ){
			throw new Exception("permission is null!");
		}
		if( permissionCodes == null || permissionCodes.isEmpty() ){
			return null;
		}
		Business business = new Business( emc );
		return business.documentPermissionFactory().listIds( docId, permissionCodes, permission );
	}
	
	/**
	 * 根据文档数据创建一个文档权限信息对象
	 * @param document
	 * @param permission
	 * @param permissionObjectName
	 * @param permissionObjectType
	 * @param permissionObjectCode
	 * @return
	 */
	private DocumentPermission createPermissionWithDocument(Document document, String permission, String permissionObjectName, String permissionObjectType, String permissionObjectCode ) {
		DocumentPermission documentPermission = new DocumentPermission();
		documentPermission.setDocumentId( document.getId() );
		documentPermission.setTitle( document.getTitle() );
		documentPermission.setAppId( document.getAppId() );
		documentPermission.setAppName( document.getAppName());
		documentPermission.setCategoryId( document.getCategoryId() );
		documentPermission.setCategoryName( document.getCategoryName() );
		documentPermission.setCategoryAlias( document.getCategoryAlias() );
		documentPermission.setDocumentStatus( document.getDocStatus() );
		documentPermission.setPermission( permission );
		documentPermission.setPermissionObjectName( permissionObjectName );
		documentPermission.setPermissionObjectType( permissionObjectType );
		documentPermission.setPermissionObjectCode( permissionObjectCode );
		documentPermission.setDocCreateDate( document.getCreateTime() );
		documentPermission.setPublishDate( document.getPublishTime() );
		documentPermission.setPublisher( document.getCreatorPerson() );
		return documentPermission;
	}

	public List<String> listPermissionIdsWithDocId(EntityManagerContainer emc, String docId) throws Exception {
		if( docId == null || docId.isEmpty() ){
			throw new Exception("docId is null!");
		}
		Business business = new Business( emc );
		return business.documentPermissionFactory().listIdsByDocumentId(docId);
	}
}

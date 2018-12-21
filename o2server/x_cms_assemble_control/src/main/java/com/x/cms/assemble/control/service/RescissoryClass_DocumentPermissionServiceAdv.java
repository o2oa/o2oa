package com.x.cms.assemble.control.service;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.cms.assemble.control.jaxrs.permission.element.PermissionInfo;
import com.x.cms.assemble.control.jaxrs.permission.element.PermissionName;
import com.x.cms.core.entity.Document;
import com.x.cms.core.entity.DocumentPermission;

/**
 * 对文档访问过滤权限信息进行管理的服务类（高级）
 * 高级服务器可以利用Service完成事务控制
 * 
 * @author O2LEE
 */
public class RescissoryClass_DocumentPermissionServiceAdv {
	
	private UserManagerService userManagerService = new UserManagerService();
	private RescissoryClass_DocumentPermissionService documentPermissionService = new RescissoryClass_DocumentPermissionService();
	
	public List<DocumentPermission> list( List<String> ids ) throws Exception {
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			return documentPermissionService.list( emc, ids );
		} catch ( Exception e ) {
			throw e;
		}
	}

	/**
	 * 根据文档ID，为文档设置用户访问和管理权限
	 * @param document
	 * @param readerList
	 * @param authorList
	 * @throws Exception
	 */
	public void refreshDocumentPermission( Document document, List<PermissionInfo> readerList, List<PermissionInfo> authorList ) throws Exception {
		List<PermissionInfo> permissionList = new ArrayList<>();
		
		//添加读者信息，如果没有限定读者，那么所有可访问分类的用户可读
		if ( readerList == null || readerList.isEmpty() ) {
			permissionList.add( new PermissionInfo( PermissionName.READER, "所有人", "所有人", "所有人" ) );
		} else {
			for( PermissionInfo p : readerList ) {
				permissionList.add( new PermissionInfo( PermissionName.READER, p.getPermissionObjectType(), p.getPermissionObjectName(), p.getPermissionObjectName() ));
			}
			//将所有的作者都添加到阅读者里去
			if ( authorList != null && !authorList.isEmpty() ) {
				for( PermissionInfo p : authorList ) {
					permissionList.add( new PermissionInfo( PermissionName.READER, p.getPermissionObjectType(), p.getPermissionObjectName(), p.getPermissionObjectName() ));
				}
			}
		}
		//添加作者信息，作者可以对文档进行编辑
		if ( authorList != null && !authorList.isEmpty() ) {
			for( PermissionInfo p : authorList ) {
				permissionList.add( new PermissionInfo( PermissionName.AUTHOR, p.getPermissionObjectType(), p.getPermissionObjectName(), p.getPermissionObjectName() ));
			}
		}
		//将读者以及作者信息持久化到数据库中
		try {
			refreshDocumentPermission( document, permissionList );
		} catch (Exception e) {
			throw e;
		}
	}
	
	/**
	 * 根据文档ID，为文档设置用户访问和管理权限
	 * @param docmentId
	 * @param permissionList
	 * @throws Exception 
	 */
	public void refreshDocumentPermission( Document docment, List<PermissionInfo> permissionList ) throws Exception {
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			documentPermissionService.refreshDocumentPermission( emc, docment, permissionList );
		} catch ( Exception e ) {
			throw e;
		}
	}

	

	public List<String> listPermissionIdsWithPerson(String docId, String distinguishedName, String permission ) throws Exception {
		List<String> permissionCodes = userManagerService.getPersonPermissionCodes( distinguishedName );
		if( permissionCodes == null || permissionCodes.isEmpty() ) {
			return null;
		}else {
			try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
				return documentPermissionService.listDocumentPermission( emc, docId, permissionCodes, permission );
			} catch ( Exception e ) {
				throw e;
			}
		}
	}

	public List<String> listPermissionIdsWithDocId(String docId) throws Exception {
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			return documentPermissionService.listPermissionIdsWithDocId( emc, docId );
		} catch ( Exception e ) {
			throw e;
		}
	}
}

package com.x.cms.assemble.control.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.lang3.StringUtils;
import org.apache.openjpa.lib.util.StringUtil;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.cms.assemble.control.Business;
import com.x.cms.assemble.control.jaxrs.permission.element.PermissionInfo;
import com.x.cms.assemble.control.jaxrs.permission.element.PermissionName;
import com.x.cms.core.entity.Document;

public class PermissionOperateService {
	
	private static Logger logger = LoggerFactory.getLogger(PermissionOperateService.class);

	private static ReentrantLock lock = new ReentrantLock();
	/**
	 * 根据文档ID，为文档设置用户访问和管理权限
	 * @param docId
	 * @param readerList
	 * @param authorList
	 * @throws Exception
	 */
	public Document refreshDocumentPermission( String docId, List<PermissionInfo> readerList, List<PermissionInfo> authorList ) throws Exception {
		List<PermissionInfo> permissionList = composeDocmentAllPermissions(readerList, authorList);
		lock.lock();
		try {
			//将读者以及作者信息持久化到数据库中
			return refreshDocumentPermission( docId, permissionList );
		} finally {
			lock.unlock();
		}
	}
	
	/**
	 * 根据读者和作者组织所有的权限对象列表
	 * @param readerList
	 * @param authorList
	 * @throws Exception
	 */
	public List<PermissionInfo> composeDocmentAllPermissions( List<PermissionInfo> readerList, List<PermissionInfo> authorList ) throws Exception {
		
		List<PermissionInfo> permissionList = new ArrayList<>();
		PermissionInfo new_permissionInfo = null;
		//添加读者信息，如果没有限定读者，那么所有可访问分类的用户可读
		if ( ListTools.isEmpty( readerList ) ) {
			permissionList.add( new PermissionInfo( PermissionName.READER, "所有人", "所有人", "所有人" ) );
		} else {
			for( PermissionInfo p : readerList ) {				
//				System.out.println(">>>>>readerList:" + p.getPermissionObjectCode());
				new_permissionInfo = createPermissionInfo( PermissionName.READER, p.getPermissionObjectType(), p.getPermissionObjectCode(), p.getPermissionObjectName() );
				if( new_permissionInfo != null ) {
					if( StringUtils.isEmpty( new_permissionInfo.getPermissionObjectCode() )) {
						new_permissionInfo.setPermissionObjectCode( new_permissionInfo.getPermissionObjectName() );
					}					
					permissionList.add( new_permissionInfo );
				}
			}
		}
		
		//将所有的作者都添加到阅读者里去
		if ( authorList != null && !authorList.isEmpty() ) {
			for( PermissionInfo p : authorList ) {
//				System.out.println(">>>>>authorList:" + p.getPermissionObjectCode());
				//如果p是所有人，那么需要判断一下，是否存在不是所有人的读者，如果存在就不能添加所有人可见
				Boolean existsReaderPermission = false;
				if( StringUtils.equals( p.getPermissionObjectCode(), "所有人")) {
					//判断是否存在不是所有人可见的权限
					for(  PermissionInfo p1 : permissionList ) {
						if( ( StringUtils.equals( p1.getPermission(), "读者") || StringUtils.equals( p1.getPermission(), "阅读"))
								&& !StringUtils.equals( p1.getPermissionObjectCode(),  "所有人" )) {
							existsReaderPermission = true;
						}
					}
				}
				
				if( !existsReaderPermission ) {
					new_permissionInfo = createPermissionInfo( PermissionName.READER, p.getPermissionObjectType(), p.getPermissionObjectCode(), p.getPermissionObjectName() );
					if( new_permissionInfo != null ) {
						if( StringUtils.isEmpty( new_permissionInfo.getPermissionObjectCode() )) {
							new_permissionInfo.setPermissionObjectCode( new_permissionInfo.getPermissionObjectName() );
						}
						permissionList.add( new_permissionInfo );
					}
				}
				
								
				//添加作者信息，作者可以对文档进行编辑
				new_permissionInfo = createPermissionInfo( PermissionName.AUTHOR, p.getPermissionObjectType(), p.getPermissionObjectCode(), p.getPermissionObjectName() );
				if( new_permissionInfo != null ) {
					if( StringUtils.isEmpty( new_permissionInfo.getPermissionObjectCode() )) {
						new_permissionInfo.setPermissionObjectCode( new_permissionInfo.getPermissionObjectName() );
					}
					permissionList.add( new_permissionInfo );
				}
			}
		}		
		return permissionList;
	}
	
	
	private PermissionInfo createPermissionInfo( String permission, String permissionObjectType, String permissionObjectCode, String permissionObjectName ) throws Exception {
		//如果存在身份信息，将身份信息替换为个人信息
		String personFlag = null;
		if( StringUtils.isEmpty( permissionObjectCode ) ) {
			permissionObjectCode = permissionObjectName;
		}
		if( StringUtil.isNotEmpty( permissionObjectCode ) && permissionObjectCode.endsWith("@I") ) {
			//将身份转换为个人 @P
			try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
				personFlag = new Business(emc).organization().person().getWithIdentity( permissionObjectCode );
			}
			if( StringUtil.isNotEmpty( personFlag )  ) {
				if( StringUtil.isNotEmpty( permissionObjectName ) && permissionObjectName.endsWith("@I")) {
					permissionObjectName = personFlag;
				}
				return new PermissionInfo(permission, permissionObjectType, personFlag, permissionObjectName);
			}else {
				logger.warn("can not find person with identity. Identity:" + permissionObjectCode );
				return null;
			}			
		}else {
			return new PermissionInfo(permission, permissionObjectType, permissionObjectCode, permissionObjectName);
		}
	}
	
	/**
	 * 根据文档ID，为文档设置用户访问和管理权限
	 * @param docId
	 * @param permissionList
	 * @throws Exception 
	 */
	public Document refreshDocumentPermission( String docId, List<PermissionInfo> permissionList ) throws Exception {
		if( StringUtils.isEmpty(docId) ){
			throw new Exception( "docId is empty！" );
		}
		logger.info("dear refreshDocumentPermission doc:{}",docId);
		Document document = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			document = emc.find( docId, Document.class );
			if( document != null ) {
				//清空文档权限信息
				document.setManagerList( null );
				document.setReadPersonList( null );
				document.setReadUnitList( null );
				document.setReadGroupList( null );
				document.setAuthorPersonList( null );
				document.setAuthorUnitList( null );
				document.setAuthorGroupList( null );
				
				if( ListTools.isNotEmpty( permissionList ) ){
					for( PermissionInfo permission : permissionList ){					
						if( "管理".equals( permission.getPermission() )) {
							document.addToManagerList(permission.getPermissionObjectCode());
						}else if( "读者".equals( permission.getPermission() ) || "阅读".equals( permission.getPermission() )) {
							if( "人员".equals( permission.getPermissionObjectType() )) {
								document.addToReadPersonList(permission.getPermissionObjectCode());
							}else if( "部门".equals(  permission.getPermissionObjectType() ) || "组织".equals( permission.getPermissionObjectType() )) {
								document.addToReadUnitList(permission.getPermissionObjectCode());
							}else if( "群组".equals( permission.getPermissionObjectType() )) {
								document.addToReadGroupList(permission.getPermissionObjectCode());
							}else if( "所有人".equals( permission.getPermissionObjectCode() )) {
								document.addToReadPersonList("所有人");
							}
						}else if( "作者".equals( permission.getPermission() )) {
							if( "人员".equals( permission.getPermissionObjectType() )) {
								document.addToAuthorPersonList(permission.getPermissionObjectCode());
							}else if( "部门".equals(  permission.getPermissionObjectType() ) || "组织".equals( permission.getPermissionObjectType() )) {
								document.addToAuthorUnitList(permission.getPermissionObjectCode());
							}else if( "群组".equals( permission.getPermissionObjectType() )) {
								document.addToAuthorGroupList(permission.getPermissionObjectCode());
							}else if( "所有人".equals( permission.getPermissionObjectCode() )) {
								document.addToAuthorPersonList("所有人");
							}
						}
					}
				}
				
				if( ListTools.isEmpty( document.getReadPersonList() ) && ListTools.isEmpty( document.getReadUnitList() ) 
						&& ListTools.isEmpty( document.getReadGroupList() )) {
					//可读范围都为空，则是所有人可访问
					document.addToReadPersonList("所有人");
					document.addToReadPersonList(document.getCreatorPerson());
				}
				if( ListTools.isEmpty( document.getAuthorPersonList() ) && ListTools.isEmpty( document.getAuthorUnitList() ) 
						&& ListTools.isEmpty( document.getAuthorGroupList() )) {
					//编辑全部都为空，则是创建人可编辑
					document.addToReadPersonList( document.getCreatorPerson() );
					document.addToAuthorPersonList( document.getCreatorPerson() );
				}
				if( ListTools.isEmpty( document.getManagerList() ) ) {
					//管理全部都为空，则是创建人可以管理
					document.addToReadPersonList( document.getCreatorPerson() );
					document.addToManagerList( document.getCreatorPerson() );
				}
				emc.beginTransaction( Document.class );
				emc.check( document , CheckPersistType.all );
				emc.commit();
			}		
		} catch ( Exception e ) {
			logger.warn("refreshDocumentPermission doc error:{}",document);
			throw e;
		}

		return document;
	}

//	private boolean existsPermission(List<PermissionInfo> permissionList, String objectType, String permissionCode) {
//		if( ListTools.isNotEmpty( permissionList )){
//			for ( PermissionInfo permission : permissionList ){
//				if( StringUtils.equalsIgnoreCase( permission.getPermissionObjectCode(), permissionCode ) &&
//						StringUtils.equalsIgnoreCase( permission.getPermissionObjectType(), objectType )){
//					return true;
//				}
//			}
//		}
//		return false;
//	}
}

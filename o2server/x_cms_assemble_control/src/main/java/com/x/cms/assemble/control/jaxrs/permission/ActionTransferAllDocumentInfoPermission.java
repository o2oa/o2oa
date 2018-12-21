package com.x.cms.assemble.control.jaxrs.permission;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.cms.core.entity.CategoryInfo;
import com.x.cms.core.entity.Document;
import com.x.cms.core.entity.DocumentPermission;

/**
 * 将所有的AppCategoryPermission对象转为新的CategoryInfo和CategoryInfo的对象
 * @author O2LEE
 *
 */
public class ActionTransferAllDocumentInfoPermission extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger( ActionTransferAllDocumentInfoPermission.class );
	
	protected ActionResult<Wo> execute( HttpServletRequest request, EffectivePerson effectivePerson ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		List<String> allCategoryInfoIds = null;
		Boolean check = true;

		//查询所有的分类信息ID列表
		if( check ){
			try {
				allCategoryInfoIds = categoryInfoServiceAdv.listAllIds();
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionAppCategoryAdminProcess( e, "系统查询所有的分类ID列表时发生异常！" );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		
		if( check ){
			if( ListTools.isNotEmpty( allCategoryInfoIds )) {
				List<String> documentIds = null;
				List<String> docPermissionIds = null;
				List<DocumentPermission> documentPermissions = null;
				CategoryInfo categoryInfo = null;
				Document document = null;
				for( String categoryId : allCategoryInfoIds ) {
					//查询分类信息
					try {
						categoryInfo = categoryInfoServiceAdv.get( categoryId );
						if( categoryInfo == null ){
							continue;
						}
					} catch (Exception e) {
						logger.error( e, effectivePerson, request, null);
					}
					
					System.out.println(">>>>>正在处理分类信息：" + categoryInfo.getCategoryAlias() );
					
					//查询该分类下所有的文档ID列表
					try {
						documentIds = documentServiceAdv.listIdsByCategoryId(categoryId);
						if( ListTools.isEmpty( documentIds ) ){
							continue;
						}
					} catch (Exception e) {
						logger.error( e, effectivePerson, request, null);
					}
					
					//遍历所有的文档，更新权限数据结构
					for( String docId : documentIds ) {
						try {
							document = documentServiceAdv.get(docId);
							if( document == null ){
								continue;
							}
						} catch (Exception e) {
							logger.error( e, effectivePerson, request, null);
						}
						System.out.println(">>>>>正在处理文档信息：" + document.getTitle() );
						try {
							docPermissionIds = documentPermissionServiceAdv.listPermissionIdsWithDocId(docId );
							if( ListTools.isNotEmpty( docPermissionIds ) ){
								documentPermissions = documentPermissionServiceAdv.list( docPermissionIds );
							}
						} catch (Exception e) {
							logger.error( e, effectivePerson, request, null);
						}
						
						document.setAuthorPersonList( null );
						document.setAuthorUnitList( null );
						document.setAuthorGroupList( null );
						document.setReadPersonList( null );
						document.setReadUnitList( null );
						document.setReadGroupList( null );
						document.setManagerList( null );
						
						if( ListTools.isNotEmpty( documentPermissions )) {
							//组织权限数据结构
							for( DocumentPermission documentPermission : documentPermissions ) {
								if( "管理".equals( documentPermission.getPermission() )) {
									document.addManagerList(documentPermission.getPermissionObjectCode());
								}else if( "读者".equals( documentPermission.getPermission() )) {
									if( "人员".equals( documentPermission.getPermissionObjectType() )) {
										document.addReadPersonList(documentPermission.getPermissionObjectCode());
									}else if( "部门".equals(  documentPermission.getPermissionObjectType() )) {
										document.addReadUnitList(documentPermission.getPermissionObjectCode());
									}else if( "群组".equals( documentPermission.getPermissionObjectType() )) {
										document.addReadGroupList(documentPermission.getPermissionObjectCode());
									}else if( "所有人".equals( documentPermission.getPermissionObjectCode() )) {
										document.addReadPersonList("所有人");
									}
								}else if( "阅读".equals( documentPermission.getPermission() )) {
									if( "人员".equals( documentPermission.getPermissionObjectType() )) {
										document.addReadPersonList(documentPermission.getPermissionObjectCode());
									}else if( "部门".equals(  documentPermission.getPermissionObjectType() )) {
										document.addReadUnitList(documentPermission.getPermissionObjectCode());
									}else if( "群组".equals( documentPermission.getPermissionObjectType() )) {
										document.addReadGroupList(documentPermission.getPermissionObjectCode());
									}else if( "所有人".equals( documentPermission.getPermissionObjectCode() )) {
										document.addReadPersonList("所有人");
									}
								}else if( "作者".equals( documentPermission.getPermission() )) {
									if( "人员".equals( documentPermission.getPermissionObjectType() )) {
										document.addAuthorPersonList(documentPermission.getPermissionObjectCode());
									}else if( "部门".equals(  documentPermission.getPermissionObjectType() )) {
										document.addAuthorUnitList(documentPermission.getPermissionObjectCode());
									}else if( "群组".equals( documentPermission.getPermissionObjectType() )) {
										document.addAuthorGroupList(documentPermission.getPermissionObjectCode());
									}else if( "所有人".equals( documentPermission.getPermissionObjectCode() )) {
										document.addAuthorPersonList("所有人");
									}
								}
							}
						}
						
						if( ListTools.isEmpty( document.getReadPersonList() ) && ListTools.isEmpty( document.getReadUnitList() ) 
								&& ListTools.isEmpty( document.getReadGroupList() )) {
							//全部都为空，则是创建人可访问
							document.addReadPersonList( "所有人" );
							document.addReadPersonList( document.getCreatorPerson() );
						}
						
						if( ListTools.isEmpty( document.getAuthorPersonList() ) && ListTools.isEmpty( document.getAuthorUnitList() ) 
								&& ListTools.isEmpty( document.getAuthorGroupList() )) {
							//全部都为空，则是创建人可访问
							document.addAuthorPersonList( "所有人" );
							document.addAuthorPersonList( document.getCreatorPerson() );
						}
						
						if( ListTools.isEmpty( document.getManagerList() ) ) {
							//全部都为空，则是创建人可以管理
							document.addManagerList( document.getCreatorPerson() );
						}
						
						try {
							documentServiceAdv.updateAllPermission( document );
						} catch (Exception e) {
							check = false;
							Exception exception = new ExceptionDocumentPermissionTransfer( e, docId );
							result.error( exception );
							logger.error( e, effectivePerson, request, null);
						}
					}
				}
			}
			
			ApplicationCache.notify( Document.class );
			
			Wo wo = new Wo();
			wo.setId( "" );
			result.setData( wo );
			System.out.println(">>>>>文档信息权限数据结构全部处理完成。" );
		}
		return result;
	}

	public static class Wo extends WoId {

	}
}
package com.x.cms.assemble.control.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.openjpa.lib.util.StringUtil;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.cms.assemble.control.Business;
import com.x.cms.assemble.control.jaxrs.permission.element.PermissionInfo;
import com.x.cms.assemble.control.jaxrs.permission.element.PermissionName;
import com.x.cms.core.entity.AppInfo;
import com.x.cms.core.entity.CategoryInfo;
import com.x.cms.core.entity.Document;
import com.x.cms.core.entity.Review;

public class PermissionOperateService {
	
	private static Logger logger = LoggerFactory.getLogger(PermissionOperateService.class);
	/**
	 * 根据文档ID，为文档设置用户访问和管理权限
	 * @param document
	 * @param readerList
	 * @param authorList
	 * @throws Exception
	 */
	public void refreshDocumentPermission( String docId, List<PermissionInfo> readerList, List<PermissionInfo> authorList ) throws Exception {
		List<PermissionInfo> permissionList = composeDocmentAllPermissions(readerList, authorList);
		try {
			//将读者以及作者信息持久化到数据库中
			refreshDocumentPermission( docId, permissionList );
		} catch (Exception e) {
			throw e;
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
				new_permissionInfo = createPermissionInfo( PermissionName.READER, p.getPermissionObjectType(), p.getPermissionObjectName(), p.getPermissionObjectName() );
				if( new_permissionInfo != null ) {
					permissionList.add( new_permissionInfo );
				}
			}
		}
		
		//将所有的作者都添加到阅读者里去
		if ( authorList != null && !authorList.isEmpty() ) {
			for( PermissionInfo p : authorList ) {
				new_permissionInfo = createPermissionInfo( PermissionName.READER, p.getPermissionObjectType(), p.getPermissionObjectName(), p.getPermissionObjectName() );
				if( new_permissionInfo != null ) {
					permissionList.add( new_permissionInfo );
				}				
				//添加作者信息，作者可以对文档进行编辑
				new_permissionInfo = createPermissionInfo( PermissionName.AUTHOR, p.getPermissionObjectType(), p.getPermissionObjectName(), p.getPermissionObjectName() );
				if( new_permissionInfo != null ) {
					permissionList.add( new_permissionInfo );
				}
			}
		}
		
		return permissionList;
	}
	
	
	private PermissionInfo createPermissionInfo( String permission, String permissionObjectType, String permissionObjectCode, String permissionObjectName ) throws Exception {
		//如果存在身份信息，将身份信息替换为个人信息
		String personFlag = null;
		if( StringUtil.isNotEmpty( permissionObjectCode ) && permissionObjectCode.indexOf("@I") > 0 ) {
			//将身份转换为个人 @P
			try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
				personFlag = new Business(emc).organization().person().getWithIdentity( permissionObjectCode );
			}
			if( StringUtil.isNotEmpty( personFlag )  ) {
				if( StringUtil.isNotEmpty( permissionObjectName ) && permissionObjectName.indexOf("@I") > 0 ) {
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
	 * @param docmentId
	 * @param permissionList
	 * @throws Exception 
	 */
	public void refreshDocumentPermission( String docId, List<PermissionInfo> permissionList ) throws Exception {
		if( StringUtils.isEmpty(docId) ){
			throw new Exception( "docId is empty！" );
		}
		
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			emc.beginTransaction( Document.class );
			Document document = emc.find( docId, Document.class );		
			
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
							document.addManagerList(permission.getPermissionObjectCode());
						}else if( "读者".equals( permission.getPermission() ) || "阅读".equals( permission.getPermission() )) {
							if( "人员".equals( permission.getPermissionObjectType() )) {
								document.addReadPersonList(permission.getPermissionObjectCode());
							}else if( "部门".equals(  permission.getPermissionObjectType() ) || "组织".equals( permission.getPermissionObjectType() )) {
								document.addReadUnitList(permission.getPermissionObjectCode());
							}else if( "群组".equals( permission.getPermissionObjectType() )) {
								document.addReadGroupList(permission.getPermissionObjectCode());
							}else if( "所有人".equals( permission.getPermissionObjectCode() )) {
								document.addReadPersonList("所有人");
							}
						}else if( "作者".equals( permission.getPermission() )) {
							if( "人员".equals( permission.getPermissionObjectType() )) {
								document.addAuthorPersonList(permission.getPermissionObjectCode());
							}else if( "部门".equals(  permission.getPermissionObjectType() ) || "组织".equals( permission.getPermissionObjectType() )) {
								document.addAuthorUnitList(permission.getPermissionObjectCode());
							}else if( "群组".equals( permission.getPermissionObjectType() )) {
								document.addAuthorGroupList(permission.getPermissionObjectCode());
							}else if( "所有人".equals( permission.getPermissionObjectCode() )) {
								document.addAuthorPersonList("所有人");
							}
						}
					}
				}
				
				if( ListTools.isEmpty( document.getReadPersonList() ) && ListTools.isEmpty( document.getReadUnitList() ) 
						&& ListTools.isEmpty( document.getReadGroupList() )) {
					//可读范围都为空，则是所有人可访问
					document.addReadPersonList("所有人");
					document.addReadPersonList(document.getCreatorPerson());
				}
				if( ListTools.isEmpty( document.getAuthorPersonList() ) && ListTools.isEmpty( document.getAuthorUnitList() ) 
						&& ListTools.isEmpty( document.getAuthorGroupList() )) {
					//编辑全部都为空，则是创建人可编辑
					document.addAuthorPersonList( document.getCreatorPerson() );
					document.addAuthorPersonList( document.getCreatorPerson() );
				}
				if( ListTools.isEmpty( document.getManagerList() ) ) {
					//管理全部都为空，则是创建人可以管理
					document.addManagerList( document.getCreatorPerson() );
					document.addManagerList( document.getCreatorPerson() );
				}
		
				emc.check( document , CheckPersistType.all );
				emc.commit();
			}		
		} catch ( Exception e ) {
			throw e;
		}
	}
	
	/**
	 * 根据栏目ID，更新栏目内所有文档的Review信息
	 * @param appId
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void refreshReviewWithAppId( String appId ) throws Exception {
		if( StringUtils.isEmpty(appId) ){
			throw new Exception( "appId is empty！" );
		}
		AppInfo appInfo = null;
		Business business = null;
		List<String> categoryIds = null;
		List<String> documentIds = null;
		List<CategoryInfo> categoryList = null;
		List<Document> documentList = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			business = new Business(emc);
			appInfo = emc.find( appId, AppInfo.class );
			
			if( appInfo == null || !"信息".equals( appInfo.getDocumentType() ) ) {
				if( appInfo != null) {
					emc.beginTransaction( AppInfo.class );
					appInfo.setReviewed( true  );
					emc.check( appInfo, CheckPersistType.all );
					emc.commit();
				}
				return;
			}
			//查询栏目下所有的分类信息
			categoryIds = business.getCategoryInfoFactory().listByAppId( appId );
			if( ListTools.isNotEmpty( categoryIds )) {
				categoryList = emc.list( CategoryInfo.class , categoryIds );
//				categoryList = business.getCategoryInfoFactory().list( categoryIds );
				for( CategoryInfo categoryInfo :  categoryList ) {
					if( !"信息".equals( categoryInfo.getDocumentType() ) ) {
						emc.beginTransaction( CategoryInfo.class );
						categoryInfo.setReviewed( true  );
						emc.check( categoryInfo, CheckPersistType.all );
						emc.commit();
						continue;
					}
					documentIds = business.getDocumentFactory().listByCategoryId( categoryInfo.getId() );
					if( ListTools.isNotEmpty( documentIds )) {
						documentList = emc.list( Document.class,  documentIds );
//						documentList = business.getDocumentFactory().list( documentIds );
						for( Document document :  documentList ) {
							if( !document.getDocStatus().equalsIgnoreCase("published") || !"信息".equals( document.getDocumentType() ) ) {
								emc.beginTransaction( Document.class );
								document.setReviewed( true  );
								emc.check( document, CheckPersistType.all );
								emc.commit();
								continue;
							}
							//更新每个文档相应的Review信息
							try {
								refreshReview( emc, appInfo, categoryInfo, document );
							}catch( Exception e ) {
								e.printStackTrace();
							}
						}
					}
					emc.beginTransaction( CategoryInfo.class );
					categoryInfo.setReviewed( true  );
					emc.check( categoryInfo, CheckPersistType.all );
					emc.commit();
				}
			}
			emc.beginTransaction( AppInfo.class );
			appInfo.setReviewed( true  );
			emc.check( appInfo, CheckPersistType.all );
			emc.commit();
		}
	}	
	
	/**
	 * 根据分类ID，更新栏目内所有文档的Review信息
	 * @param categoryId
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void refreshReviewWithCategoryId( String categoryId ) throws Exception {
		if( StringUtils.isEmpty( categoryId ) ){
			throw new Exception( "categoryId is empty！" );
		}
		AppInfo appInfo = null;
		CategoryInfo categoryInfo = null;
		Business business = null;
		List<String> documentIds = null;
		List<Document> documentList = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			business = new Business(emc);
			categoryInfo = emc.find( categoryId, CategoryInfo.class );
			
			if( categoryInfo == null || !"信息".equals( categoryInfo.getDocumentType() ) ) {
				if( categoryInfo != null ) {
					emc.beginTransaction( CategoryInfo.class );
					categoryInfo.setReviewed( true  );
					emc.check( categoryInfo, CheckPersistType.all );
					emc.commit();
				}
				return;
			}
			appInfo = emc.find( categoryInfo.getAppId(), AppInfo.class );
			documentIds = business.getDocumentFactory().listByCategoryId( categoryInfo.getId() );
			if( ListTools.isNotEmpty( documentIds )) {
				documentList = emc.list( Document.class,  documentIds );
//				documentList = business.getDocumentFactory().list( documentIds );
				for( Document document :  documentList ) {
					if( !document.getDocStatus().equalsIgnoreCase("published") || !"信息".equals( document.getDocumentType() ) ) {
						emc.beginTransaction( Document.class );
						document.setReviewed( true  );
						emc.check( document, CheckPersistType.all );
						emc.commit();
						continue;
					}
					//更新每个文档相应的Review信息
					try {
						refreshReview( emc, appInfo, categoryInfo, document );
					}catch( Exception e ) {
						e.printStackTrace();
					}
				}
			}
			
			emc.beginTransaction( CategoryInfo.class );
			categoryInfo.setReviewed( true  );
			emc.check( categoryInfo, CheckPersistType.all );
			emc.commit();
			
		}
	}	
	
	/**
	 * 根据文档ID，重新刷新Review数据
	 * @param docId
	 * @throws Exception 
	 */
	public void refreshReview( String docId ) throws Exception {
		
		if( StringUtils.isEmpty(docId) ){
			throw new Exception( "docId is empty！" );
		}
		AppInfo appInfo = null;
		CategoryInfo categoryInfo = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			Document document = emc.find( docId, Document.class );
			if( document == null || !document.getDocStatus().equalsIgnoreCase("published") || !"信息".equals( document.getDocumentType() ) ) {
				if( document != null ) {
					emc.beginTransaction( Document.class );
					document.setReviewed( true  );
					emc.check( document, CheckPersistType.all );
					emc.commit();
				}
				return;
			}
			appInfo = emc.find( document.getAppId(), AppInfo.class );
			categoryInfo = emc.find( document.getCategoryId(), CategoryInfo.class );
			try {
				refreshReview( emc, appInfo, categoryInfo, document );
			}catch( Exception e ) {
				e.printStackTrace();
			}
		} catch ( Exception e ) {
			throw e;
		}
	}
	
	private Boolean refreshReview( EntityManagerContainer emc, AppInfo appInfo, CategoryInfo categoryInfo, Document document ) throws Exception {
		if( appInfo == null ) {
			throw new Exception( "appInfo is null！" );
		}
		if( categoryInfo == null ) {
			throw new Exception( "categoryInfo is null！" );
		}
		if( document == null ) {
			throw new Exception( "document is null！" );
		}
		
		List<String> personNames_appInfo = new ArrayList<>();
		List<String> personNames_categoryInfo = new ArrayList<>();
		List<String> personNames_document = new ArrayList<>();
		List<String> unitNames_appInfo = new ArrayList<>();
		List<String> unitNames_categoryInfo = new ArrayList<>();
		List<String> unitNames_document = new ArrayList<>();
		List<String> groupNames_appInfo = new ArrayList<>();
		List<String> groupNames_categoryInfo = new ArrayList<>();
		List<String> groupNames_document = new ArrayList<>();
		
		//获取所有的栏目权限
		personNames_appInfo = appInfo.getViewablePersonList();
		unitNames_appInfo = appInfo.getViewableUnitList();
		groupNames_appInfo = appInfo.getViewableGroupList();
		personNames_appInfo = mergeList( personNames_appInfo, appInfo.getPublishablePersonList() );
		personNames_appInfo = mergeList( personNames_appInfo, appInfo.getManageablePersonList() );
		unitNames_appInfo = mergeList( unitNames_appInfo, appInfo.getPublishableUnitList() );
		unitNames_appInfo = mergeList( unitNames_appInfo, appInfo.getManageableUnitList() );
		groupNames_appInfo = mergeList( groupNames_appInfo, appInfo.getPublishableGroupList() );
		groupNames_appInfo = mergeList( groupNames_appInfo, appInfo.getManageableGroupList() );
		
		//获取所有的分类权限
		personNames_categoryInfo = categoryInfo.getViewablePersonList();
		unitNames_categoryInfo = categoryInfo.getViewableUnitList();
		groupNames_categoryInfo = categoryInfo.getViewableGroupList();
		personNames_categoryInfo = mergeList( personNames_categoryInfo, categoryInfo.getPublishablePersonList() );
		personNames_categoryInfo = mergeList( personNames_categoryInfo, categoryInfo.getManageablePersonList() );
		unitNames_categoryInfo = mergeList( unitNames_categoryInfo, categoryInfo.getPublishableUnitList() );
		unitNames_categoryInfo = mergeList( unitNames_categoryInfo, categoryInfo.getManageableUnitList() );
		groupNames_categoryInfo = mergeList( groupNames_categoryInfo, categoryInfo.getPublishableGroupList() );
		groupNames_categoryInfo = mergeList( groupNames_categoryInfo, categoryInfo.getManageableGroupList() );
		
		//获取所有的文档权限
		personNames_document = document.getReadPersonList();
		unitNames_document = document.getReadUnitList();
		groupNames_document = document.getReadGroupList();
		personNames_document = mergeList( personNames_categoryInfo, document.getAuthorPersonList() );
		unitNames_document = mergeList( unitNames_categoryInfo, document.getAuthorUnitList() );
		groupNames_document = mergeList( groupNames_categoryInfo, document.getAuthorGroupList() );	
		
		if( appInfo.getAllPeopleView() ) {
			//栏目权限为全员可见，尝试继承分类权限
			if( categoryInfo.getAllPeopleView() ) {
				//分类权限为全员可见，尝试继承文档权限
				if( document.getReadPersonList( ).contains("所有人") ) {
					//文档也是全员可见，为文档添加一个权限可见的Review
					updateReview( emc, document, true );
				}else {
					//直接继承文档的权限配置
					updateReview( emc, document, personNames_document, unitNames_document, groupNames_document, false );
				}
			}else {
				//如果文档权限不为空，则以文档权限为准，如果文档权限为空，以分类权限为准
				if( document.getReadPersonList( ).contains("所有人") ) {
					//文档是全员可见，以分类权限为准
					updateReview( emc, document, personNames_categoryInfo, unitNames_categoryInfo, groupNames_categoryInfo, false );
				}else {
					//直接继承文档的权限配置
					updateReview( emc, document, personNames_document, unitNames_document, groupNames_document, false );
				}
			}
		}else {
			//栏目权限不为空，尝试判断分类是否全员可见
			if( categoryInfo.getAllPeopleView() ) {
				//分类全员可见，判断文档是否全员可见
				if( document.getReadPersonList( ).contains("所有人") ) {
					//文档为全员可见，直接继承栏目权限
					updateReview( emc, document, personNames_appInfo, unitNames_appInfo, groupNames_appInfo, false );
				}else {
					//文档有权限控制，直接继承文档的权限配置
					updateReview( emc, document, personNames_document, unitNames_document, groupNames_document, false );
				}
			}else {
				//分类不为全员可见，判断文档是否全员可见
				if( document.getReadPersonList( ).contains("所有人") ) {
					//文档为全员可见，直接继承分类权限
					updateReview( emc, document, personNames_categoryInfo, unitNames_categoryInfo, groupNames_categoryInfo, false );
				}else {
					//文档有权限控制，直接继承文档的权限配置
					updateReview( emc, document, personNames_document, unitNames_document, groupNames_document, false );
				}
			}
		}
		return true;
	}

	/**
	 * 合并集合，并且去掉生重复值
	 * @param personNames
	 * @param readPersonList
	 * @return
	 */
	private List<String> mergeList(List<String> sourceList, List<String> targetList ) {
		if( ListTools.isEmpty( sourceList )) {
			return targetList;
		}
		for( String target : targetList ) {
			if( !sourceList.contains( target )) {
				sourceList.add( target );
			}
		}
		return sourceList;
	}

	private void updateReview( EntityManagerContainer emc, Document document, List<String> personNames, List<String> unitNames, List<String> groupNames, Boolean isPublic ) throws Exception {
		//先删除所有的Review信息，再重新添加Review信息
		Review review = null;
		Business business = new Business(emc);
		document = emc.find( document.getId(), Document.class );
		List<String> reviewIds = business.reviewFactory().listWithDocument(document.getId());
				
		if( !document.getDocStatus().equals("published")) {
			return;
		}
		
		emc.beginTransaction( Review.class );
		emc.beginTransaction( Document.class );
		
		for( String id : reviewIds ) {
			review = emc.find( id, Review.class );
			emc.remove( review, CheckRemoveType.all );
		}
				
		//再把加入新的Review
		for( String personName : personNames ) {
			review = new Review( document, "个人", personName, isPublic );
			emc.persist( review, CheckPersistType.all );
		}
		for( String unitName : unitNames ) {
			review = new Review( document, "组织", unitName, isPublic );
			emc.persist( review, CheckPersistType.all );
		}
		for( String groupName : groupNames ) {
			review = new Review( document, "群组", groupName, isPublic );
			emc.persist( review, CheckPersistType.all );
		}
		document.setReviewed( true );
		emc.check( document, CheckRemoveType.all );
		emc.commit();
	}

	/**
	 * 更新文档所有的Review信息
	 * @param emc 
	 * @param document
	 * @param isPublic
	 * @throws Exception 
	 */
	private void updateReview( EntityManagerContainer emc, Document document, Boolean isPublic ) throws Exception {
		//先删除所有的Review信息，再重新添加Review信息
		Review review = null;
		Business business = new Business(emc);
		document = emc.find( document.getId(), Document.class );
		List<String> reviewIds = business.reviewFactory().listWithDocument(document.getId());
		
		emc.beginTransaction( Review.class );
		emc.beginTransaction( Document.class );
		for( String id : reviewIds ) {
			review = emc.find( id, Review.class );
			emc.remove( review, CheckRemoveType.all );
		}
		
		//再把加入新的Review
		review = new Review( document, "所有人", "所有人", isPublic );
		document.setReviewed( true );
		emc.persist( review, CheckPersistType.all );
		emc.commit();
	}
}

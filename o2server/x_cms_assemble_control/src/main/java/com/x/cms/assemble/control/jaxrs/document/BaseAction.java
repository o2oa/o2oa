package com.x.cms.assemble.control.jaxrs.document;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.tools.ListTools;
import com.x.cms.assemble.control.Business;
import com.x.cms.assemble.control.service.AppInfoServiceAdv;
import com.x.cms.assemble.control.service.CategoryInfoServiceAdv;
import com.x.cms.assemble.control.service.DocCommendPersistService;
import com.x.cms.assemble.control.service.DocCommendQueryService;
import com.x.cms.assemble.control.service.DocumentPersistService;
import com.x.cms.assemble.control.service.DocumentQueryService;
import com.x.cms.assemble.control.service.DocumentViewRecordServiceAdv;
import com.x.cms.assemble.control.service.FileInfoServiceAdv;
import com.x.cms.assemble.control.service.FormServiceAdv;
import com.x.cms.assemble.control.service.LogService;
import com.x.cms.assemble.control.service.PermissionOperateService;
import com.x.cms.assemble.control.service.PermissionQueryService;
import com.x.cms.assemble.control.service.QueryViewService;
import com.x.cms.assemble.control.service.UserManagerService;
import com.x.cms.core.entity.AppInfo;
import com.x.cms.core.entity.CategoryInfo;
import com.x.cms.core.entity.Document;

import net.sf.ehcache.Ehcache;

public class BaseAction extends StandardJaxrsAction {
	protected Ehcache cache = ApplicationCache.instance().getCache( Document.class);
	
	protected LogService logService = new LogService();
	protected QueryViewService queryViewService = new QueryViewService();
	protected DocumentViewRecordServiceAdv documentViewRecordServiceAdv = new DocumentViewRecordServiceAdv();
	protected DocumentPersistService documentPersistService = new DocumentPersistService();
	protected DocumentQueryService documentQueryService = new DocumentQueryService();
	
	protected DocCommendPersistService docCommendPersistService = new DocCommendPersistService();
	protected DocCommendQueryService docCommendQueryService = new DocCommendQueryService();
	
	protected FormServiceAdv formServiceAdv = new FormServiceAdv();
	protected CategoryInfoServiceAdv categoryInfoServiceAdv = new CategoryInfoServiceAdv();
	protected AppInfoServiceAdv appInfoServiceAdv = new AppInfoServiceAdv();
	protected UserManagerService userManagerService = new UserManagerService();
	protected FileInfoServiceAdv fileInfoServiceAdv = new FileInfoServiceAdv();
	protected PermissionQueryService permissionQueryService = new PermissionQueryService();
	protected PermissionOperateService permissionOperateService = new PermissionOperateService();
	
	protected boolean modifyDocStatus( String id, String stauts, String personName ) throws Exception{
		Business business = null;
		Document document = null;
		
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);			
		
			//进行数据库持久化操作
			emc.beginTransaction( Document.class );
			document = business.getDocumentFactory().get(id);
			if (null != document) {
				//修改文档状态
				document.setDocStatus( stauts );
				document.setPublishTime( new Date() );
				//保存文档信息
				emc.check( document, CheckPersistType.all);
			}			
			emc.commit();
			return true;
		} catch (Exception th) {
			throw th;
		}
	}
	
	/**
	 * 根据要求的栏目ID列表，分类ID列表，查询用户在这些栏目和分类下可以访问的所有分类ID
	 * @param wrapIn_viewAbleAppIds - 指定的栏目ID列表
	 * @param wrapIn_appAliasList - 指定的栏目别名列表
	 * @param wrapIn_viewAbleCategoryIds - 指定的分类ID列表
	 * @param wrapIn_categoryAliasList - 指定的分类别名列表
	 * @param personName
	 * @param xadmin
	 * @return
	 * @throws Exception
	 */
	protected List<String> listAllViewAbleCategoryIds( List<String> inFilterAppIdList, List<String> inFilterAppAliasList, 
			List<String> inFilterCategoryIdList, List<String> inFilterCategoryAliasList, String documentType, String personName, 
			Boolean isAnonymous, Boolean manager, Integer maxCount ) throws Exception{
		List<String> categoryIds = null;
		List<AppInfo> appInfoList = null;
		List<CategoryInfo> categoryInfoList = null;
		
		List<String> unitNames = null;
		List<String>  groupNames = null;

		if( !isAnonymous ) {
			unitNames = userManagerService.listUnitNamesWithPerson( personName );
			 groupNames = userManagerService.listGroupNamesByPerson( personName );
		}
		
		//一、先将栏目的ID和别名的限制合并为ID列表
		if( inFilterAppIdList == null ) {
			inFilterAppIdList = new ArrayList<>(); //初始化
		}
		//根据栏目别名取，判断访问权限
		if( ListTools.isNotEmpty( inFilterAppAliasList ) ){
			appInfoList = appInfoServiceAdv.listAppInfoWithAliases( inFilterAppAliasList );
			for( AppInfo appInfo : appInfoList ){
				if( appInfoViewable( personName, isAnonymous, unitNames, groupNames, appInfo, manager )) {
					if( !inFilterAppIdList.contains( appInfo.getId() )){
						inFilterAppIdList.add( appInfo.getId() );
					}
				}
			}
		}
		
		//二、将wrapIn_categoryAliasList合到wrapIn_viewAbleCategoryIds中
		if( inFilterCategoryIdList == null ) {
			inFilterCategoryIdList = new ArrayList<>(); //初始化
		}
		if( ListTools.isNotEmpty( inFilterCategoryAliasList ) ){
			categoryInfoList = categoryInfoServiceAdv.listCategoryInfoWithAliases( inFilterCategoryAliasList );
			for( CategoryInfo categoryInfo : categoryInfoList ){
				if( !inFilterCategoryIdList.contains( categoryInfo.getId() )){
					inFilterCategoryIdList.add( categoryInfo.getId() );
				}
			}
		}

		//三、根据栏目和目录的限制查询可访问的分类列表
		if( manager ){
			List<String> categoryIds_result = new ArrayList<>();
			//如果是管理员权限，则在所有的分类信息中进行过滤，可以忽略权限
			if( ListTools.isEmpty( inFilterAppIdList )) {//并没有指定栏目
				if( ListTools.isNotEmpty( inFilterCategoryIdList )) {
					//未指定栏目，但指定了分类，那么直接使用指定的分类
					categoryIds_result = inFilterCategoryIdList;
				}else {
					categoryIds_result = categoryInfoServiceAdv.listAllIds();
				}
			}else {//指定了栏目，则需要在栏目的限制下获取的分类信息ID列表
				categoryIds = categoryInfoServiceAdv.listCategoryIdsWithAppIds( inFilterAppIdList, documentType, manager, maxCount );
				for( String id : categoryIds ){
					if( !categoryIds_result.contains( id )){
						categoryIds_result.add( id );
					}
				}
				if( ListTools.isNotEmpty( inFilterCategoryIdList )) {
					//如果指定了栏目又指定了分类, 取交集即可（管理员）
					categoryIds_result.retainAll(inFilterCategoryIdList  );
				}
			}
			return categoryIds_result;
		}else{
			//如果不是管理员，则需要根据权限来获取可访问的分类
			//获取用户可以访问到的所有分类列表
			categoryIds = permissionQueryService.listViewableCategoryIdByPerson(
					personName, isAnonymous, unitNames, groupNames, inFilterAppIdList, inFilterCategoryIdList, null, documentType, maxCount, manager );
			return categoryIds;
		}
	}

	/**
	 * 根据用户的名称组织群组查询对指定栏目的访问权限
	 * @param personName
	 * @param isAnonymous
	 * @param unitNames
	 * @param groupNames
	 * @param appInfo
	 * @return
	 * @throws Exception 
	 */
	private boolean appInfoViewable(String personName, Boolean isAnonymous, List<String> unitNames, List<String> groupNames, AppInfo appInfo, Boolean manager) throws Exception {
		
		if( appInfo.getAllPeopleView() || appInfo.getAllPeoplePublish() ) {
			return true;
		}
		if( !isAnonymous ) {
			if( manager ) {
				return true;
			}
			
			if( ListTools.isNotEmpty( appInfo.getManageablePersonList() )) {
				if( appInfo.getManageablePersonList().contains( personName )) {
					return true;
				}
			}			
			if( ListTools.isNotEmpty( appInfo.getViewableUnitList() )) {
				if( ListTools.containsAny( unitNames, appInfo.getViewableUnitList())) {
					return true;
				}
			}			
			if( ListTools.isNotEmpty( appInfo.getViewableGroupList() )) {
				if( ListTools.containsAny( groupNames, appInfo.getViewableGroupList())) {
					return true;
				}
			}			
			if( ListTools.isNotEmpty( appInfo.getPublishableUnitList() )) {
				if( ListTools.containsAny( unitNames, appInfo.getPublishableUnitList())) {
					return true;
				}
			}			
			if( ListTools.isNotEmpty( appInfo.getPublishableGroupList() )) {
				if( ListTools.containsAny( groupNames, appInfo.getPublishableGroupList())) {
					return true;
				}
			}
		}		
		return false;
	}
	
	/**
	 * 将权限组，组织为一个整体集合
	 * @param personName
	 * @param groupNames 
	 * @param unitNames 
	 * @return
	 */
	protected List<String> getPermissionObjs(String personName, List<String> unitNames, List<String> groupNames) {
		List<String> permissionObjs = new ArrayList<>();		
		permissionObjs.add( personName );
		if( ListTools.isNotEmpty( unitNames )) {
			for( String unitName : unitNames ) {
				permissionObjs.add( unitName );
			}
		}
		if( ListTools.isNotEmpty( groupNames )) {
			for( String groupName : groupNames ) {
				permissionObjs.add( groupName );
			}
		}
		return permissionObjs;
	}
}

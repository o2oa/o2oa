package com.x.cms.assemble.control.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.x.base.core.project.cache.CacheManager;
import com.x.cms.core.entity.enums.DocumentStatus;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.organization.Person;
import com.x.base.core.project.tools.ListTools;
import com.x.cms.assemble.control.Business;
import com.x.cms.core.entity.AppInfo;
import com.x.cms.core.entity.CategoryInfo;
import com.x.cms.core.entity.Document;
import com.x.cms.core.entity.Review;
import com.x.cms.core.express.tools.filter.QueryFilter;

public class ReviewService {

	private UserManagerService userManagerService = new UserManagerService();
	private static Logger logger = LoggerFactory.getLogger( ReviewService.class );
	/**
	 * 根据权限和条件查询符合要求的可见的所有Review总数
	 * @param emc
	 * @param personName
	 * @param queryFilter
	 * @return
	 * @throws Exception
	 */
	public Long countViewableWithFilter( EntityManagerContainer emc, String personName, QueryFilter queryFilter ) throws Exception {
		Business business = new Business(emc);
		return business.reviewFactory().countWithFilter( personName, queryFilter );
	}

	/**
	 * 根据权限和条件查询符合条件的可见的文档Review信息列表
	 * @param emc
	 * @param pageSize
	 * @param docSequence
	 * @param orderField
	 * @param orderType
	 * @param person
	 * @param queryFilter
	 * @return
	 * @throws Exception
	 */
	public List<Review> listNextViewableWithFilter( EntityManagerContainer emc, Integer pageSize, String docSequence, String orderField,
			String orderType, String person, QueryFilter queryFilter ) throws Exception {
		Business business = new Business(emc);
		return business.reviewFactory().listNextWithFilter( pageSize, docSequence, orderField, orderType, person, queryFilter);
	}

	/**
	 * 根据权限和条件查询符合条件的可见的文档Review信息列表
	 * @param emc
	 * @param pageSize
	 * @param docSequence
	 * @param orderField
	 * @param orderType
	 * @param person
	 * @param queryFilter
	 * @return
	 * @throws Exception
	 */
	public List<Review> listPrevViewableWithFilter( EntityManagerContainer emc, Integer pageSize, String docSequence, String orderField,
													String orderType, String person, QueryFilter queryFilter ) throws Exception {
		Business business = new Business(emc);
		return business.reviewFactory().listPrevWithFilter( pageSize, docSequence, orderField, orderType, person, queryFilter);
	}

	/**
	 * 根据条件查询指定条数符合条件的文档信息Review列表
	 * @param emc
	 * @param orderField
	 * @param orderType
	 * @param person
	 * @param queryFilter
	 * @param maxCount
	 * @return
	 * @throws Exception
	 */
	public List<Review> listViewableWithFilter(EntityManagerContainer emc, String orderField, String orderType,
			String person, QueryFilter queryFilter, int maxCount ) throws Exception {
		Business business = new Business(emc);
		return business.reviewFactory().listWithFilter( orderField, orderType, person, queryFilter, maxCount);
	}

	public List<String> listDocIdsWithConditionInReview( EntityManagerContainer emc, String personName, QueryFilter queryFilter, Integer maxCount ) throws Exception {
		Business business = new Business(emc);
		return business.reviewFactory().listDocIdsWithConditionInReview( personName, queryFilter, maxCount);
	}

	public List<String> listDocIdsWithConditionInReview( EntityManagerContainer emc, String personName, String orderField, String orderType, QueryFilter queryFilter, Integer maxCount ) throws Exception {
		Business business = new Business(emc);
		return business.reviewFactory().listDocIdsWithConditionInReview( personName, orderField, orderType, queryFilter, maxCount);
	}

	/**
	 * 根据指定文档的权限信息重置或者刷新所有的Review信息
	 * @param emc
	 * @param docId
	 * @throws Exception
	 */
	public boolean refreshDocumentReview( EntityManagerContainer emc, String docId ) throws Exception {
		boolean fullRead = false;
		Document document = emc.find( docId, Document.class );

		if( document != null ) {
			AppInfo appInfo = emc.find( document.getAppId(), AppInfo.class );
			CategoryInfo categoryInfo = emc.find( document.getCategoryId(), CategoryInfo.class );

			if( DocumentStatus.DRAFT.getValue().equals( document.getDocStatus() ) ) {
				logger.debug( "refreshDocumentReview -> refresh review for draft document: " + document.getTitle() );
				//草稿只有拟稿人可以看见
				List<String> persons = new ArrayList<>();
				persons.add( document.getCreatorPerson() );
				logger.debug( "refreshDocumentReview -> there are "+ persons.size() +" permission in this document: " + document.getTitle() );
				refreshDocumentReview( emc, document, persons );
			}else if( DocumentStatus.PUBLISHED.getValue().equals( document.getDocStatus() ) ||
					DocumentStatus.WAIT_PUBLISH.getValue().equals( document.getDocStatus() )) {
				logger.debug( "refreshDocumentReview -> refresh review for published document: " + document.getTitle() );
				List<String> persons = listPermissionPersons( appInfo, categoryInfo, document );
				if(persons.contains("*")){
					fullRead = true;
				}
				//将文档新的权限与数据库中的权限进行比对，新建或者更新
				logger.debug( "refreshDocumentReview -> there are "+ persons.size() +" permission in this document: " + document.getTitle() );
				refreshDocumentReview( emc, document, persons );
			}else if( DocumentStatus.ARCHIVED.getValue().equals( document.getDocStatus() ) ) {
				logger.debug( "refreshDocumentReview -> refresh review for archived document: " + document.getTitle() );
				//归档的文档应该只有管理员和拟稿人能看见
				List<String> persons = listPublishAndManagePersons( appInfo, categoryInfo, document );
				logger.debug( "refreshDocumentReview -> there are "+ persons.size() +" permission in this document: " + document.getTitle() );
				refreshDocumentReview( emc, document, persons );
			}
		}else {
			logger.debug( "refreshDocumentReview -> document not exists: " + docId );
		}
		return fullRead;
	}

	private List<String> listPublishAndManagePersons(AppInfo appInfo, CategoryInfo categoryInfo, Document document) throws Exception {
		List<String> persons = new ArrayList<>();
		persons.add( document.getCreatorPerson() ); //创建者
		persons = addListToList( persons, categoryInfo.getPublishablePersonList() );
		persons = addPermissionObj( persons, categoryInfo.getPublishableUnitList() );
		persons = addPermissionObj( persons, categoryInfo.getPublishableGroupList() );
		persons = addListToList( persons, categoryInfo.getManageablePersonList() );
		persons = addPermissionObj( persons, categoryInfo.getManageableUnitList() );
		persons = addPermissionObj( persons, categoryInfo.getManageableGroupList() );
		persons = addListToList( persons, appInfo.getManageablePersonList() );
		persons = addPermissionObj( persons, appInfo.getManageableUnitList() );
		persons = addPermissionObj( persons, appInfo.getManageableGroupList() );
		persons = addListToList( persons, appInfo.getPublishablePersonList() );
		persons = addPermissionObj( persons, appInfo.getPublishableUnitList() );
		persons = addPermissionObj( persons, appInfo.getPublishableGroupList() );
		return persons;
	}

	/**
	 * 将一个文档涉及到的所有权限转换为人员，如果是数据文档，就设置为全员可见
	 * @param appInfo
	 * @param categoryInfo
	 * @param document
	 * @return
	 * @throws Exception
	 */
	public List<String> listPermissionPersons( AppInfo appInfo, CategoryInfo categoryInfo, Document document ) throws Exception {
		List<String> permissionObjs = new ArrayList<>();
		Boolean documentHasPermissionControl = documentViewPermissionExists(document);
		Boolean categoryHasPermissionControl = categoryPermissionExists(categoryInfo);
		Boolean appInfoHasPermissionControl = appInfoPermissionExists(appInfo);

		if( "数据".equals( document.getDocumentType() )) {
			//数据默认在Review全部设置为全员可见，由栏目和分类来控制可见权限
			permissionObjs.add( "*" );
			return permissionObjs;
		}

		if( !appInfoHasPermissionControl ) {
			if( !categoryHasPermissionControl ) {
				if( !documentHasPermissionControl ) {
					logger.debug("栏目没有阅读权限限制，分类没有阅读权限限制，文档没有阅读权限限制。文档所有人可见：*");
					permissionObjs.add( "*" );
				}else {
					logger.debug("栏目没有阅读权限限制，分类没有阅读权限限制，文档有阅读权限限制。文档可见范围以文档的权限为主");
					addDocumentAllPermission( permissionObjs, document, appInfo, categoryInfo);
				}
			}else {//分类有权限
				if( !documentHasPermissionControl ) {
					logger.debug("栏目没有阅读权限限制，分类有阅读权限限制，文档没有阅读权限限制。文档可见范围以分类权限为主");
					addCategoryAllPermission( permissionObjs, document, categoryInfo, appInfo );
				}else {
					logger.debug("栏目没有阅读权限限制，分类有阅读权限限制，文档有阅读权限限制。文档可见范围以文档权限为主");
					addDocumentAllPermission( permissionObjs, document, appInfo, categoryInfo);
				}
			}
		}else {//栏目有权限
			if( !categoryHasPermissionControl ) {
				if( !documentHasPermissionControl ) {
					logger.debug("栏目有阅读权限限制，分类没有阅读权限限制，文档没有阅读权限限制。文档可见范围以栏目的权限为主");
					addAppInfoAllPermission( permissionObjs, document, categoryInfo, appInfo );
				}else {
					logger.debug("栏目有阅读权限限制，分类没有阅读权限限制，文档有阅读权限限制。文档可见范围以文档的权限为主");
					addDocumentAllPermission( permissionObjs, document, appInfo, categoryInfo);
				}
			}else {//分类有权限
				if( !documentHasPermissionControl ) {
					logger.debug("栏目有阅读权限限制，分类有阅读权限限制，文档没有阅读权限限制。文档可见范围以分类权限为主");
					addCategoryAllPermission( permissionObjs, document, categoryInfo, appInfo);
				}else {
					logger.debug("栏目有阅读权限限制，分类有阅读权限限制，文档有阅读权限限制。文档可见范围以文档权限为主");
					addDocumentAllPermission( permissionObjs, document, appInfo, categoryInfo);
				}
			}
		}
		if( permissionObjs.contains("*")) {
			//如果是全员可见，那么只需要保留一个权限记录即可
			permissionObjs.clear();
			logger.debug(">>>>>>>Document all person can read. document has read permission *， clean all other permissions. ");
			permissionObjs.add( "*" );
		}
		return permissionObjs;
	}

	/**
	 * 判断文档是否存在访问权限控制
	 * @param document
	 * @return
	 */
	private Boolean documentViewPermissionExists( Document document ) {
		if( document == null ) {
			return false;
		}
		if( ListTools.isNotEmpty( document.getReadPersonList() ) ) {
			if( !document.getReadPersonList().contains( "所有人" )) {
				return true;
			}else{
				return false;
			}
		}
		if( ListTools.isNotEmpty( document.getReadUnitList() ) ) {
			if( !document.getReadUnitList().contains( "所有人" )) {
				return true;
			}
		}
		if( ListTools.isNotEmpty( document.getReadGroupList() ) ) {
			if( !document.getReadGroupList().contains( "所有人" )) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 判断分类是否存在访问权限控制
	 * @param categoryInfo
	 * @return
	 */
	private Boolean categoryPermissionExists(CategoryInfo categoryInfo) {
		if( categoryInfo == null ) {
			return false;
		}
		if( ListTools.isNotEmpty( categoryInfo.getViewablePersonList() ) ) {
			if( !categoryInfo.getViewablePersonList().contains( "所有人" )) {
				return true;
			}
		}
		if( ListTools.isNotEmpty( categoryInfo.getViewableUnitList() ) ) {
			return true;
		}
		if( ListTools.isNotEmpty( categoryInfo.getViewableGroupList() ) ) {
			return true;
		}
		return false;
	}

	/**
	 * 判断栏目是否存在访问权限控制
	 * @param appInfo
	 * @return
	 */
	private Boolean appInfoPermissionExists(AppInfo appInfo) {
		if( appInfo == null ) {
			return false;
		}
		if( ListTools.isNotEmpty( appInfo.getViewablePersonList() ) ) {
			if( !appInfo.getViewablePersonList().contains( "所有人" )) {
				return true;
			}
		}
		if( ListTools.isNotEmpty( appInfo.getViewableUnitList() ) ) {
			return true;
		}
		if( ListTools.isNotEmpty( appInfo.getViewableGroupList() ) ) {
			return true;
		}
		return false;
	}

	/**
	 * 将栏目所有的权限全部添加到权限列表里
	 * @param permissionObjs
	 * @param appInfo
	 * @return
	 * @throws Exception
	 */
	private List<String> addAppInfoAllPermission(List<String> permissionObjs, Document document, CategoryInfo categoryInfo, AppInfo appInfo ) throws Exception {
		if( permissionObjs == null ) {
			permissionObjs = new ArrayList<>();
		}
		if( appInfo == null ) {
			return permissionObjs;
		}
		if( ListTools.isNotEmpty( document.getAuthorPersonList() ) ) {
			addPermissionObj( permissionObjs, document.getAuthorPersonList() );
		}
		if( ListTools.isNotEmpty( document.getAuthorUnitList() ) ) {
			addPermissionObj( permissionObjs, document.getAuthorUnitList() );
		}
		if( ListTools.isNotEmpty( document.getAuthorGroupList() ) ) {
			addPermissionObj( permissionObjs, document.getAuthorUnitList() );
		}
		if( ListTools.isNotEmpty( document.getManagerList())) {
			addPermissionObj( permissionObjs, document.getManagerList() );
		}
		if( ListTools.isNotEmpty( appInfo.getViewablePersonList())) {
			permissionObjs = addPermissionObj( permissionObjs, appInfo.getViewablePersonList() );
		}
		if( ListTools.isNotEmpty( appInfo.getViewableUnitList())) {
			permissionObjs = addPermissionObj( permissionObjs, appInfo.getViewableUnitList() );
		}
		if( ListTools.isNotEmpty( appInfo.getViewableGroupList())) {
			permissionObjs = addPermissionObj( permissionObjs, appInfo.getViewableGroupList() );
		}
		if( ListTools.isNotEmpty( categoryInfo.getManageablePersonList())) {
			permissionObjs = addPermissionObj( permissionObjs, categoryInfo.getManageablePersonList() );
		}
		if( ListTools.isNotEmpty( categoryInfo.getManageableUnitList())) {
			permissionObjs = addPermissionObj( permissionObjs, categoryInfo.getManageableUnitList() );
		}
		if( ListTools.isNotEmpty( categoryInfo.getManageableGroupList())) {
			permissionObjs = addPermissionObj( permissionObjs, categoryInfo.getManageableGroupList() );
		}
		if( ListTools.isNotEmpty( appInfo.getManageablePersonList())) {
			permissionObjs = addPermissionObj( permissionObjs, appInfo.getManageablePersonList() );
		}
		if( ListTools.isNotEmpty( appInfo.getManageableUnitList())) {
			permissionObjs = addPermissionObj( permissionObjs, appInfo.getManageableUnitList() );
		}
		if( ListTools.isNotEmpty( appInfo.getManageableGroupList())) {
			permissionObjs = addPermissionObj( permissionObjs, appInfo.getManageableGroupList() );
		}
		return permissionObjs;
	}

	/**
	 * 将分类所有的权限全部添加到权限列表里
	 * @param permissionObjs
	 * @param categoryInfo
	 * @return
	 * @throws Exception
	 */
	private List<String> addCategoryAllPermission(List<String> permissionObjs, Document document, CategoryInfo categoryInfo, AppInfo appInfo) throws Exception {
		if( permissionObjs == null ) {
			permissionObjs = new ArrayList<>();
		}
		if( categoryInfo == null ) {
			return permissionObjs;
		}
		if( ListTools.isNotEmpty( document.getAuthorPersonList() ) ) {
			addPermissionObj( permissionObjs, document.getAuthorPersonList() );
		}
		if( ListTools.isNotEmpty( document.getAuthorUnitList() ) ) {
			addPermissionObj( permissionObjs, document.getAuthorUnitList() );
		}
		if( ListTools.isNotEmpty( document.getAuthorGroupList() ) ) {
			addPermissionObj( permissionObjs, document.getAuthorUnitList() );
		}
		if( ListTools.isNotEmpty( document.getManagerList())) {
			addPermissionObj( permissionObjs, document.getManagerList() );
		}
		if( ListTools.isNotEmpty( categoryInfo.getViewablePersonList())) {
			permissionObjs = addPermissionObj( permissionObjs, categoryInfo.getViewablePersonList() );
		}
		if( ListTools.isNotEmpty( categoryInfo.getViewableUnitList())) {
			permissionObjs = addPermissionObj( permissionObjs, categoryInfo.getViewableUnitList() );
		}
		if( ListTools.isNotEmpty( categoryInfo.getViewableGroupList())) {
			permissionObjs = addPermissionObj( permissionObjs, categoryInfo.getViewableGroupList() );
		}
		if( ListTools.isNotEmpty( categoryInfo.getManageablePersonList())) {
			permissionObjs = addPermissionObj( permissionObjs, categoryInfo.getManageablePersonList() );
		}
		if( ListTools.isNotEmpty( categoryInfo.getManageableUnitList())) {
			permissionObjs = addPermissionObj( permissionObjs, categoryInfo.getManageableUnitList() );
		}
		if( ListTools.isNotEmpty( categoryInfo.getManageableGroupList())) {
			permissionObjs = addPermissionObj( permissionObjs, categoryInfo.getManageableGroupList() );
		}
		if( ListTools.isNotEmpty( appInfo.getManageablePersonList())) {
			permissionObjs = addPermissionObj( permissionObjs, appInfo.getManageablePersonList() );
		}
		if( ListTools.isNotEmpty( appInfo.getManageableUnitList())) {
			permissionObjs = addPermissionObj( permissionObjs, appInfo.getManageableUnitList() );
		}
		if( ListTools.isNotEmpty( appInfo.getManageableGroupList())) {
			permissionObjs = addPermissionObj( permissionObjs, appInfo.getManageableGroupList() );
		}
		return permissionObjs;
	}

	/**
	 * 将文档内所有的权限全部添加到权限列表里
	 * @param permissionObjs
	 * @param document
	 * @return
	 * @throws Exception
	 */
	private List<String> addDocumentAllPermission(List<String> permissionObjs, Document document, AppInfo appInfo, CategoryInfo categoryInfo) throws Exception {
		if( permissionObjs == null ) {
			permissionObjs = new ArrayList<>();
		}
		if( document == null ) {
			return permissionObjs;
		}
		if( ListTools.isNotEmpty( document.getReadPersonList() ) ) {
			addPermissionObj( permissionObjs, document.getReadPersonList() );
		}
		if( ListTools.isNotEmpty( document.getReadUnitList() ) ) {
			addPermissionObj( permissionObjs, document.getReadUnitList() );
		}
		if( ListTools.isNotEmpty( document.getReadGroupList() ) ) {
			addPermissionObj( permissionObjs, document.getReadGroupList() );
		}
		if( !permissionObjs.contains( document.getCreatorPerson() )) {
			permissionObjs.add( document.getCreatorPerson() );
		}
		if( ListTools.isNotEmpty( document.getAuthorPersonList() ) ) {//文档编辑者
			addPermissionObj( permissionObjs, document.getAuthorPersonList() );
		}
		if( ListTools.isNotEmpty( document.getAuthorUnitList() ) ) {//文档编辑者-组织
			addPermissionObj( permissionObjs, document.getAuthorUnitList() );
		}
		if( ListTools.isNotEmpty( document.getAuthorGroupList() ) ) {//文档编辑者-群组
			addPermissionObj( permissionObjs, document.getAuthorUnitList() );
		}
		if( ListTools.isNotEmpty( document.getManagerList())) { //文档管理员
			addPermissionObj( permissionObjs, document.getManagerList() );
		}
		if( ListTools.isNotEmpty( categoryInfo.getManageablePersonList())) {
			addPermissionObj( permissionObjs, categoryInfo.getManageablePersonList() );
		}
		if( ListTools.isNotEmpty( categoryInfo.getManageableUnitList())) {
			addPermissionObj( permissionObjs, categoryInfo.getManageableUnitList() );
		}
		if( ListTools.isNotEmpty( categoryInfo.getManageableGroupList())) {
			addPermissionObj( permissionObjs, categoryInfo.getManageableGroupList() );
		}
		if( ListTools.isNotEmpty( appInfo.getManageablePersonList())) {
			addPermissionObj( permissionObjs, appInfo.getManageablePersonList() );
		}
		if( ListTools.isNotEmpty( appInfo.getManageableUnitList())) {
			addPermissionObj( permissionObjs, appInfo.getManageableUnitList() );
		}
		if( ListTools.isNotEmpty( appInfo.getManageableGroupList())) {
			addPermissionObj( permissionObjs, appInfo.getManageableGroupList() );
		}
		return permissionObjs;
	}

	/**
	 * 将指定的权限名称拆解人员，添加到permissionObjs，并且返回
	 * @param permissionObjs
	 * @param objNames
	 * @return
	 * @throws Exception
	 */
	private List<String> addPermissionObj(List<String> permissionObjs, List<String> objNames ) throws Exception {
		String result = null;
		List<String> persons = null;
		if( permissionObjs == null ) {
			permissionObjs = new ArrayList<>();
		}
		if( ListTools.isNotEmpty(objNames)){
			Person person;
			String[] array;
			for( String objName : objNames ) {
				if(StringUtils.isNotEmpty(objName)){
					if( objName.trim().endsWith( "@P" ) ) {
						//直接加入人员
						array = objName.split("@");
						if( array.length == 2){
							person = userManagerService.getPerson(array[0]);
							if( person != null ){
								objName = person.getDistinguishedName();
							}
						}
						addStringToList( permissionObjs, objName );
					}else if( objName.trim().endsWith( "@I" ) ) {//将Identity转换为人员
						//从身份到人员
						array = objName.split("@");
						if( array.length == 2){
							result = userManagerService.getPersonNameWithIdentity(array[0]);
						}else{
							result = userManagerService.getPersonNameWithIdentity( objName );
						}
						addStringToList( permissionObjs, result );
					}else if( objName.trim().endsWith( "@U" ) ) {//将组织拆解为人员
						//判断一下，如果不是顶层组织，就或者顶层组织不唯一，才将组织解析为人员
						if( !userManagerService.isTopUnit( objName ) || userManagerService.countTopUnit() > 1 ) {
							persons = userManagerService.listPersonWithUnit( objName );
							permissionObjs = addListToList( permissionObjs, persons );
						}else {
							//如果是顶层组织，并且顶层组织只有一个
							addStringToList( permissionObjs, "*" );
						}
					}else if( objName.trim().endsWith( "@G" ) ) {//将群组拆解为人员
						persons = userManagerService.listPersonWithGroup( objName );
						permissionObjs = addListToList( permissionObjs, persons );
					}else if( objName.trim().endsWith( "@R" ) ) {
						persons = userManagerService.listPersonWithRole( objName );
						permissionObjs = addListToList( permissionObjs, persons );
					}else if( "*".equals( objName.trim() ) ) {
						permissionObjs = addStringToList( permissionObjs, objName );
					}
				}
			}
		}
		return permissionObjs;
	}

	/**
	 * 将字符串添加到集合里，去重
	 * @param list
	 * @param string
	 * @return list
	 */
	private List<String> addStringToList( List<String> list, String string ) {
		if( list == null ) {
			list = new ArrayList<>();
		}
		if(StringUtils.isNotBlank(string) && !list.contains( string )) {
			list.add( string);
		}
		return list;
	}

	/**
	 * 将字符串列表添加到集合里，去重
	 * @param list
	 * @param list2
	 * @return list
	 */
	private List<String> addListToList( List<String> list, List<String> list2 ) {
		if( list == null ) {
			list = new ArrayList<>();
		}
		if( ListTools.isEmpty( list2 ) ) {
			return list;
		}else {
			for( String string : list2 ) {
				if( !list.contains( string )) {
					list.add( string);
				}
			}
		}
		return list;
	}

	/**
	 * 将指定文档的Review刷新为指定的人员可见
	 * @param emc
	 * @param document
	 * @param permissionPersons
	 * @throws Exception
	 */
	private void refreshDocumentReview( EntityManagerContainer emc, Document document, List<String> permissionPersons) throws Exception {
		Business business = new Business(emc);
		Set<String> permissionSet = new HashSet<>(permissionPersons);
		permissionPersons.clear();
		//先检查该文档是否存在Review信息
		List<String> oldReviewIds = business.reviewFactory().listByDocument( document.getId(), null );

		//先删除原来所有的Review信息
		if( ListTools.isNotEmpty( oldReviewIds )) {
			emc.beginTransaction( Review.class );
			int i = 0;
			for( String reviewId : oldReviewIds ){
				Review oldReview = emc.find( reviewId, Review.class );
				if(permissionSet.contains(oldReview.getPermissionObj())){
					permissionSet.remove(oldReview.getPermissionObj());
				}else{
					emc.remove( oldReview, CheckRemoveType.all );
					i++;
					if(i>99 && i % 100 ==0){
						emc.commit();
						emc.beginTransaction( Review.class );
					}
				}
			}
			emc.commit();
		}

		//再添加新的Review信息
		if( permissionSet.size() > 0) {
			emc.beginTransaction( Review.class );
			Person personObj = null;
			String personName = null;
			int i = 0;
			for( String person : permissionSet ) {
				if( !person.equalsIgnoreCase( "*" )) {
					//检查一下个人是否存在，防止姓名或者唯一标识变更过了导致文档权限不正确
					personObj = userManagerService.getPerson( person );
					if( personObj != null ) {
						personName = personObj.getDistinguishedName();
					}
				}else {
					personName = "*";
				}
				if( StringUtils.isNotEmpty( personName )) {
					//查询一下，数据库里， 是否有相同的数据，如果有，就不再添加了
					oldReviewIds = business.reviewFactory().listByDocumentAndPerson( document.getId(), personName );
					if( ListTools.isEmpty( oldReviewIds )) {
						Review review = createReviewWithDocument( document, personName );
						emc.persist( review, CheckPersistType.all );
						i++;
						if(i>99 && i % 100 ==0){
							emc.commit();
							emc.beginTransaction( Review.class );
						}
					}
				}
			}
			emc.commit();
		}

		CacheManager.notify(Review.class);
	}

	/**
	 * 给集合去重复
	 * @param permissionPersons
	 * @return
	 */
	private List<String> removeSameValue(List<String> permissionPersons) {
		List<String> list = new ArrayList<>();
		if( ListTools.isNotEmpty( permissionPersons )) {
			for( String permission : permissionPersons ) {
				if( !list.contains( permission ) ) {
					list.add( permission );
				}
			}
		}
		return list;
	}

	/**
	 *
	 * 根据栏目，分类，文档信息以及可见权限来组织一个Review对象
	 * @param document
	 * @param person
	 * @return
	 */
	private Review createReviewWithDocument( Document document, String person ) {
		Review review = new Review();

		review.setId( Review.createId() );
		review.setDocId( document.getId() );
		review.setTitle( document.getTitle() );
		review.setDocStatus( document.getDocStatus() );
		review.setDocumentType( document.getDocumentType() );
		review.setDocSequence( document.getSequence() );

		review.setAppAlias( document.getAppAlias() );
		review.setAppId( document.getAppId() );
		review.setAppName( document.getAppName() );
		review.setCategoryAlias( document.getCategoryAlias() );
		review.setCategoryId( document.getCategoryId() );
		review.setCategoryName( document.getCategoryName() );

		review.setCreateTime( document.getCreateTime() );
		review.setDocCreateTime( document.getCreateTime() );
		review.setPublishTime( document.getPublishTime() );
		review.setUpdateTime( document.getUpdateTime() );

		review.setCreatorPerson( document.getCreatorPerson() );
		review.setCreatorTopUnitName( document.getCreatorTopUnitName() );
		review.setCreatorUnitName( document.getCreatorUnitName() );

		if(StringUtils.isEmpty( review.getCreatorPerson() )) {
			review.setCreatorPerson( "xadmin" );
			review.setCreatorTopUnitName( "xadmin" );
			review.setCreatorUnitName( "xadmin" );
		}

		review.setPermissionObj( person );
		if( "*".equals( person ) ) {
			review.setPermissionObjType( "*" );
		}else {
			review.setPermissionObjType( "PERSON" );
		}
		return review;
	}

	/**
	 * 根据指定文档删除所有的Review信息
	 * @param emc
	 * @param docId
	 * @throws Exception
	 */
	public void deleteDocumentReview( EntityManagerContainer emc, String docId ) throws Exception {
		Business business = new Business(emc);
		Integer maxQueryCount = 1000;
		Long count = business.reviewFactory().countByDocuemnt(docId);
		Long maxTimes = count/maxQueryCount + 1 + 1; //多补偿一次
		List<Review> reviewList = null;
		List<String> ids = null;
		for( int i = 0 ; i <= maxTimes; i++ ) {
			ids = business.reviewFactory().listByDocument(docId, maxQueryCount);
			if( ListTools.isNotEmpty( ids )) {
				reviewList = emc.list( Review.class, ids);
			}
			if( ListTools.isNotEmpty( reviewList )) {
				emc.beginTransaction( Review.class );
				for( Review review : reviewList ) {
					emc.remove( review );
				}
				emc.commit();
			}
		}
	}
}

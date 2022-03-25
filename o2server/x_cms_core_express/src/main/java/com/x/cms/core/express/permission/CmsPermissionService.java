package com.x.cms.core.express.permission;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.project.tools.ListTools;
import com.x.cms.core.entity.AppInfo;
import com.x.cms.core.entity.CategoryInfo;
import com.x.cms.core.entity.Review;
import com.x.cms.core.entity.Review_;
import com.x.cms.core.express.tools.CriteriaBuilderTools;
import com.x.cms.core.express.tools.filter.QueryFilter;

/**
 * 对CMS栏目、分类、文档进行权限过滤查询，在CMS应用中会直接引用
 * 
 * @author O2LEE
 *
 */
public class CmsPermissionService {

	/**
	 * 根据条件获取用户有权限访问的所有文档ID列表
	 * @param emc
	 * @param queryFilter
	 * @param maxResultCount
	 * @return
	 * @throws Exception
	 */
	public List<String> lisViewableDocIdsWithFilter(EntityManagerContainer emc, QueryFilter queryFilter,
			Integer maxResultCount) throws Exception {
		if (maxResultCount == null || maxResultCount == 0) {
			maxResultCount = 500;
		}
		List<String> ids = new ArrayList<>();
		List<Review> reviews = null;
		EntityManager em = emc.get(Review.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Review> cq = cb.createQuery(Review.class);
		Root<Review> root = cq.from(Review.class);
		// Predicate p=null;
		Predicate p = CriteriaBuilderTools.composePredicateWithQueryFilter(Review_.class, cb, null, root, queryFilter);
		cq.orderBy(cb.desc(root.get(Review.publishTime_FIELDNAME)));
		reviews = em.createQuery(cq.where(p)).setMaxResults(maxResultCount).getResultList();
		if ( ListTools.isNotEmpty( reviews )) {
			for (Review review : reviews) {
				if (!ids.contains(review.getDocId())) {
					ids.add(review.getDocId());
				}
			}
		}
		return ids;
	}

	/**
	 * 查询指定用户，组织，群组可以访问到的所有栏目ID列表(包含全员可以访问的栏目)
	 * 
	 * @param personName
	 * @param isAnonymous
	 * @param unitNames
	 * @param groupNames
	 * @param inAppInfoIds
	 * @param excludAppInfoIds
	 * @return
	 * @throws Exception
	 */
	public List<String> listViewableAppIdByPerson(EntityManagerContainer emc, String personName, Boolean isAnonymous,
			List<String> unitNames, List<String> groupNames, List<String> inAppInfoIds, List<String> excludAppInfoIds,
			String documentType, String appType, Integer maxCount) throws Exception {

		List<String> viewableAppInfoIds = new ArrayList<>();

		//1、将所有未设置访问权限的栏目ID列表添加到viewAbleAppInfoIds中（全员可以访问的AppInfo)
		viewableAppInfoIds = addResultToSourceList(this.listAllPeopleViewAppIds(emc, documentType, appType), viewableAppInfoIds);
		//2、加入所有人都能发布的栏目
		viewableAppInfoIds = addResultToSourceList(this.listAllPeoplePublishAppIds( emc, documentType, appType ), viewableAppInfoIds);

		if (!isAnonymous) {
			// 2、将用户自己为管理员的所有栏目ID列表添加到viewAbleAppInfoIds中
			viewableAppInfoIds = addResultToSourceList(this.listManageableAppIdsByPerson(emc, personName, unitNames,
					groupNames, null, null, null, documentType, maxCount), viewableAppInfoIds);

			// 3、将用户自己以及用户所在的组织、群组，有权限访问的所有栏目ID列表添加到viewAbleAppInfoIds中
			addResultToSourceList(
					this.listViewableAppIdsInPermission(emc, personName, unitNames, groupNames, documentType, maxCount),
					viewableAppInfoIds);
		}

		if (ListTools.isNotEmpty(inAppInfoIds)) {
			viewableAppInfoIds.retainAll(inAppInfoIds);
		}

		viewableAppInfoIds = excludListContent(viewableAppInfoIds, excludAppInfoIds);

		return viewableAppInfoIds;
	}

	/**
	 * 查询指定用户，组织，群组可以发布文档的所有栏目ID列表
	 * 
	 * @param personName
	 * @param isAnonymous
	 * @param unitNames
	 * @param groupNames
	 * @param inAppInfoIds
	 * @param excludAppInfoIds
	 * @return
	 * @throws Exception
	 */
	public List<String> listPublishableAppIdByPerson(EntityManagerContainer emc, String personName, Boolean isAnonymous,
			List<String> unitNames, List<String> groupNames, List<String> inAppInfoIds, List<String> excludAppInfoIds,
			String documentType, String appType, Integer maxCount) throws Exception {
		List<String> publishableAppInfoIds = new ArrayList<>();

		// 1、全员可发布的栏目 ，将所有未设置访问权限的栏目ID列表添加到viewAbleAppInfoIds中
		publishableAppInfoIds = addResultToSourceList( this.listAllPeoplePublishAppIds(emc, documentType, appType), publishableAppInfoIds);

		if (!isAnonymous) {
			// 2、用户可管理的栏目， 将用户自己为管理员的所有栏目ID列表添加到viewAbleAppInfoIds中
			publishableAppInfoIds = addResultToSourceList(this.listManageableAppIdsByPerson(emc, personName, unitNames,
					groupNames, null, null, null, documentType, maxCount), publishableAppInfoIds);

			// 3、用户有发布权限设置的栏目， 将用户自己以及用户所在的组织、群组，有权限访问的所有栏目ID列表添加到viewAbleAppInfoIds中
			addResultToSourceList(this.listPublishableAppIdsInPermission(emc, personName, unitNames, groupNames,
					documentType, maxCount), publishableAppInfoIds);
		}

		if (ListTools.isNotEmpty(inAppInfoIds)) {
			publishableAppInfoIds.retainAll(inAppInfoIds);
		}
		
		if (ListTools.isNotEmpty(excludAppInfoIds)) {
			publishableAppInfoIds = excludListContent(publishableAppInfoIds, excludAppInfoIds);
		}
		
		return publishableAppInfoIds;
	}

	/**
	 * 根据excludAppInfoIds排除不需要的数据
	 * 
	 * @param sourceList
	 * @param excludAppInfoIds
	 * @return
	 */
	private List<String> excludListContent(List<String> sourceList, List<String> excludAppInfoIds) {
		if (ListTools.isEmpty(excludAppInfoIds)) {
			return sourceList;
		}
		List<String> result = new ArrayList<>();
		if (ListTools.isNotEmpty(sourceList)) {
			for (String content : sourceList) {
				if (!excludAppInfoIds.contains(content)) {
					result.add(content);
				}
			}
		}
		return result;
	}

	/**
	 * 查询指定用户可以管理的所有栏目ID列表( with List copy )
	 * 
	 * @param emc
	 * @param personName
	 * @param unitNames
	 * @param groupNames
	 * @param appType
	 * @param documentType
	 * @param maxCount
	 * @return
	 * @throws Exception
	 */
	public List<String> listManageableAppIdsByPerson(EntityManagerContainer emc, String personName,
			List<String> unitNames, List<String> groupNames, 
			List<String> inAppInfoIds, List<String> excludAppInfoIds,
			String appType, String documentType, Integer maxCount)
			throws Exception {
		if (StringUtils.isEmpty(personName)) {
			throw new Exception("personName is empty!");
		}
		List<String> appInfoIds = null;
		List<String> appInfoIds_out = new ArrayList<>();
		EntityManager em = emc.get(AppInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<AppInfo> root = cq.from(AppInfo.class);

		Predicate p = null;
		if (StringUtils.isNotEmpty(appType) && !StringUtils.equals(appType, "未分类")) {
			p = CriteriaBuilderTools.predicate_and(cb, p, cb.equal(root.get(AppInfo.appType_FIELDNAME), appType));
		}
		if (StringUtils.isNotEmpty(appType) && StringUtils.equals(appType, "未分类")) {
			CriteriaBuilderTools.predicate_and(cb, p,
					CriteriaBuilderTools.predicate_or(cb, cb.isNull(root.get(AppInfo.appType_FIELDNAME)),
							CriteriaBuilderTools.predicate_or(cb, cb.equal(root.get(AppInfo.appType_FIELDNAME), ""),
									cb.equal(root.get(AppInfo.appType_FIELDNAME), "未分类"))));
		}
		if (StringUtils.isNotEmpty(documentType) && !"全部".equals(documentType)
				&& !"all".equalsIgnoreCase(documentType)) {
			p = CriteriaBuilderTools.predicate_and(cb, p,
					cb.equal(root.get(AppInfo.documentType_FIELDNAME), documentType));
		}
		Predicate p_permission = null;
		if (StringUtils.isNotEmpty(personName)) {
			p_permission = CriteriaBuilderTools.predicate_or(cb, p_permission,
					cb.isMember(personName, root.get(AppInfo.manageablePersonList_FIELDNAME)));
		}
		if (ListTools.isNotEmpty(unitNames)) {
			p_permission = CriteriaBuilderTools.predicate_or(cb, p_permission,
					cb.or(p_permission, root.get(AppInfo.manageableUnitList_FIELDNAME).in(unitNames)));
		}
		if (ListTools.isNotEmpty(groupNames)) {
			p_permission = CriteriaBuilderTools.predicate_or(cb, p_permission,
					cb.or(p_permission, root.get(AppInfo.manageableGroupList_FIELDNAME).in(groupNames)));
		}
		p = CriteriaBuilderTools.predicate_and(cb, p, p_permission);

		cq.select(root.get(AppInfo.id_FIELDNAME));
		appInfoIds = em.createQuery(cq.where(p)).setMaxResults(maxCount).getResultList();
		if (appInfoIds == null) {
			appInfoIds = new ArrayList<>();
		}
		appInfoIds_out.addAll(appInfoIds);
		
		if (ListTools.isNotEmpty(inAppInfoIds)) {
			appInfoIds_out.retainAll(inAppInfoIds);
		}
		
		if (ListTools.isNotEmpty(excludAppInfoIds)) {
			appInfoIds_out = excludListContent(appInfoIds_out, excludAppInfoIds);
		}
		
		return appInfoIds_out;
	}

	/**
	 * 查询指定用户，组织，群组可以访问到的所有分类ID列表（包含全员可以访问的分类） 所获得到的分类列表可能会大于可访问的栏目列表
	 * @param emc
	 * @param personName
	 * @param isAnonymous
	 * @param unitNames
	 * @param groupNames
	 * @param inAppInfoIds
	 * @param inCategoryInfoIds
	 * @param excludCategoryInfoIds
	 * @param documentType
	 * @param appType
	 * @param maxCount
	 * @param manager
	 * @return
	 * @throws Exception
	 */
	public List<String> listViewableCategoryIdByPerson(EntityManagerContainer emc, String personName,
			Boolean isAnonymous, List<String> unitNames, List<String> groupNames, List<String> inAppInfoIds,
			List<String> inCategoryInfoIds, List<String> excludCategoryInfoIds, String documentType, String appType, Integer maxCount,
			Boolean manager) throws Exception {
		List<String> viewableCategoryInfoIds = new ArrayList<>();
		List<String> allViewableAppIds = null;
		// 查询我可以访问到的所有栏目ID列表
		if (manager) {
			allViewableAppIds = this.listAllAppIds(emc, inAppInfoIds, null, documentType, maxCount);
		} else {
			allViewableAppIds = this.listViewableAppIdByPerson(emc, personName, isAnonymous, unitNames, groupNames,
					inAppInfoIds, null, documentType, appType, maxCount);
			if (ListTools.isNotEmpty(inAppInfoIds) && ListTools.isEmpty(allViewableAppIds)) {
				allViewableAppIds = new ArrayList<>();
				allViewableAppIds.add("无可见栏目");
			}
		}

		// 在所有我能访问到的栏目范围内，查询所有的全员可见分类ID列表
		viewableCategoryInfoIds = addResultToSourceList(this.listAllPeopleViewCategoryIds(emc, allViewableAppIds,
				inCategoryInfoIds, excludCategoryInfoIds, documentType, maxCount), viewableCategoryInfoIds);

		if (!isAnonymous) {
			// 在所有我能访问到的栏目范围内，查询所有我可见的分类ID列表（包含有发布权限的分类，不检测全员可见标识）
			viewableCategoryInfoIds = addResultToSourceList(
					this.listViewableCategoryIdsInPermission(emc, personName, unitNames, groupNames, inAppInfoIds,
							inCategoryInfoIds, excludCategoryInfoIds, documentType, maxCount, manager),
					viewableCategoryInfoIds);

			// 查询所有我有权限发布的分类（可以不在可以访问到的所有栏目ID列表中）
			viewableCategoryInfoIds = addResultToSourceList(
					this.listPublishableCategoryIdsInPermission(emc, personName, unitNames, groupNames, inAppInfoIds,
							inCategoryInfoIds, excludCategoryInfoIds, documentType, maxCount),
					viewableCategoryInfoIds);

			// 查询所有我有权限管理的分类（可以不在可以访问到的所有栏目ID列表中）
			viewableCategoryInfoIds = addResultToSourceList(this.listManageableCategoryIdsByPerson(emc, personName,
					unitNames, groupNames, inAppInfoIds, documentType, maxCount, manager), viewableCategoryInfoIds);
		}

		if (ListTools.isEmpty(viewableCategoryInfoIds)) {
			if (!manager) {
				viewableCategoryInfoIds.add("没有可用分类");
			}
		} else {
			if (ListTools.isNotEmpty(inCategoryInfoIds)) {
				viewableCategoryInfoIds.retainAll(inCategoryInfoIds);
			}
		}
		viewableCategoryInfoIds = excludListContent(viewableCategoryInfoIds, excludCategoryInfoIds);

		return viewableCategoryInfoIds;
	}

	/**
	 * 查询指定用户，组织，群组可以发布文档的所有分类ID列表（包含全员可以发布文档的分类） 所获得到的分类列表可能会大于可访问的栏目列表
	 * 
	 * @param personName
	 * @param isAnonymous
	 * @param unitNames
	 * @param groupNames
	 * @param inAppInfoIds          - 过滤栏目ID
	 * @param inCategoryInfoIds     - 过滤分类ID
	 * @param excludCategoryInfoIds - 排队分类ID
	 * @return
	 * @throws Exception
	 */
	public List<String> listPublishableCategoryIdByPerson(EntityManagerContainer emc, String personName,
			Boolean isAnonymous, List<String> unitNames, List<String> groupNames, List<String> inAppInfoIds,
			List<String> inCategoryInfoIds, List<String> excludCategoryInfoIds, String documentType, String appType,
			Integer maxCount, Boolean manager) throws Exception {
		List<String> publishableCategoryInfoIds = new ArrayList<>();
		List<String> allPublishableAppIds = null;

		// 查询我可以发布文档的所有栏目ID列表（单从栏目信息层面判断，不涉及分类，未设置发布者，或者有发布者权限）
		allPublishableAppIds = this.listPublishableAppIdByPerson(emc, personName, isAnonymous, unitNames, groupNames,
				inAppInfoIds, null, documentType, appType, maxCount);
		if (ListTools.isEmpty(allPublishableAppIds)) {
			allPublishableAppIds.add("无可发布栏目ID");
		}
		// 在所有我能直接发布文档到的栏目范围内，查询所有的全员可见分类ID列表
		publishableCategoryInfoIds = addResultToSourceList(this.listAllPeoplePublishCategoryIds(emc,
				allPublishableAppIds, inCategoryInfoIds, excludCategoryInfoIds, documentType, maxCount),
				publishableCategoryInfoIds);

		if (!isAnonymous) {
			// 在指定的栏目范围内（inAppInfoIds），查询所有我可以发布文档的分类ID列表（包含有发布权限的分类，不检测全员可发布标识）
			publishableCategoryInfoIds = addResultToSourceList(
					this.listPublishableCategoryIdsInPermission(emc, personName, unitNames, groupNames, inAppInfoIds,
							inCategoryInfoIds, excludCategoryInfoIds, documentType, maxCount),
					publishableCategoryInfoIds);

			// 在指定的栏目范围内（inAppInfoIds），查询我可以管理的分类ID列表
			publishableCategoryInfoIds = addResultToSourceList(this.listManageableCategoryIdsByPerson(emc, personName,
					unitNames, groupNames, inAppInfoIds, documentType, maxCount, manager), publishableCategoryInfoIds);
		}

		if (ListTools.isEmpty(publishableCategoryInfoIds)) {
			if (!manager) {
				publishableCategoryInfoIds.add("没有可用分类");
			}
		}
		publishableCategoryInfoIds = excludListContent(publishableCategoryInfoIds, excludCategoryInfoIds);
		return publishableCategoryInfoIds;
	}

	/**
	 * 查询指定用户可以管理的所有分类ID列表( with List copy )
	 * 
	 * @param emc
	 * @param personName
	 * @return
	 * @throws Exception
	 */
	public List<String> listManageableCategoryIdsByPerson(EntityManagerContainer emc, String personName,
			List<String> unitNames, List<String> groupNames, List<String> inAppInfoIds, String documentType,
			Integer maxCount, Boolean manager) throws Exception {
		if (StringUtils.isEmpty(personName)) {
			throw new Exception("personName is empty!");
		}
		List<String> categoryInfoIds = null;
		List<String> categoryInfoIds_out = new ArrayList<>();
		EntityManager em = emc.get(CategoryInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<CategoryInfo> root = cq.from(CategoryInfo.class);

		Predicate p = null;
		if (ListTools.isNotEmpty(inAppInfoIds)) {
			p = CriteriaBuilderTools.predicate_and(cb, p, root.get(CategoryInfo.appId_FIELDNAME).in(inAppInfoIds));
		}
		if (StringUtils.isNotEmpty(documentType) && !"全部".equals(documentType)
				&& !"all".equalsIgnoreCase(documentType)) {
			p = CriteriaBuilderTools.predicate_and(cb, p,
					cb.equal(root.get(CategoryInfo.documentType_FIELDNAME), documentType));
		}

		Predicate permission = cb.isMember(personName, root.get(CategoryInfo.manageablePersonList_FIELDNAME));
		if (ListTools.isNotEmpty(unitNames)) {
			permission = CriteriaBuilderTools.predicate_or(cb, permission,
					root.get(CategoryInfo.manageableUnitList_FIELDNAME).in(unitNames));
		}
		if (ListTools.isNotEmpty(groupNames)) {
			permission = CriteriaBuilderTools.predicate_or(cb, permission,
					root.get(CategoryInfo.manageableGroupList_FIELDNAME).in(groupNames));
		}

		p = CriteriaBuilderTools.predicate_and(cb, p, permission);

		cq.select(root.get(CategoryInfo.id_FIELDNAME));
		categoryInfoIds = em.createQuery(cq.where(p)).setMaxResults(maxCount).getResultList();
		if (categoryInfoIds == null) {
			categoryInfoIds = new ArrayList<>();
		}
		categoryInfoIds_out.addAll(categoryInfoIds);
		if (ListTools.isEmpty(categoryInfoIds_out)) {
			if (!manager) {
				categoryInfoIds_out.add("没有可用分类");
			}
		}
		return categoryInfoIds_out;
	}

	/**
	 * 查询用户有权限访问的所有分类ID列表（ 不检测allPeopleView和allPeoplePublish, with List copy ）
	 * 
	 * @param personName
	 * @param unitNames
	 * @param groupNames
	 * @param inAppInfoIds
	 * @param inCategoryInfoIds
	 * @param excludCategoryInfoIds
	 * @return
	 * @throws Exception
	 */
	private List<String> listPublishableCategoryIdsInPermission(EntityManagerContainer emc, String personName,
			List<String> unitNames, List<String> groupNames, List<String> inAppInfoIds, List<String> inCategoryInfoIds,
			List<String> excludCategoryInfoIds, String documentType, Integer maxCount) throws Exception {
		List<String> categoryInfoIds = null;
		List<String> categoryInfoIds_out = new ArrayList<>();
		EntityManager em = emc.get(CategoryInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<CategoryInfo> root = cq.from(CategoryInfo.class);

		Predicate p = null;
		// 限定栏目范围
		if (ListTools.isNotEmpty(inAppInfoIds)) {
			p = CriteriaBuilderTools.predicate_and(cb, p, root.get(CategoryInfo.appId_FIELDNAME).in(inAppInfoIds));
		}
		// 限定范围
		if (ListTools.isNotEmpty(inCategoryInfoIds)) {
			p = CriteriaBuilderTools.predicate_and(cb, p, root.get(CategoryInfo.id_FIELDNAME).in(inCategoryInfoIds));
		}
		// 排除指定的ID列表
		if (ListTools.isNotEmpty(excludCategoryInfoIds)) {
			p = CriteriaBuilderTools.predicate_and(cb, p,
					cb.not(root.get(CategoryInfo.id_FIELDNAME).in(excludCategoryInfoIds)));
		}
		if (StringUtils.isNotEmpty(documentType) && !"全部".equals(documentType)
				&& !"all".equalsIgnoreCase(documentType)) {
			p = CriteriaBuilderTools.predicate_and(cb, p,
					cb.equal(root.get(CategoryInfo.documentType_FIELDNAME), documentType));
		}

		Predicate p_permission = null;
		if (StringUtils.isNotEmpty(personName)) {
			// 可以管理的栏目，肯定可以发布信息
			p_permission = CriteriaBuilderTools.predicate_or(cb, p_permission,
					cb.isMember(personName, root.get(CategoryInfo.manageablePersonList_FIELDNAME)));
			p_permission = CriteriaBuilderTools.predicate_or(cb, p_permission,
					cb.isMember(personName, root.get(CategoryInfo.publishablePersonList_FIELDNAME)));
		}
		if (ListTools.isNotEmpty(unitNames)) {
			p_permission = CriteriaBuilderTools.predicate_or(cb, p_permission,
					root.get(CategoryInfo.publishableUnitList_FIELDNAME).in(unitNames));
		}
		if (ListTools.isNotEmpty(groupNames)) {
			p_permission = CriteriaBuilderTools.predicate_or(cb, p_permission,
					root.get(CategoryInfo.publishableGroupList_FIELDNAME).in(groupNames));
		}

		p = CriteriaBuilderTools.predicate_and(cb, p, p_permission);
		cq.select(root.get(CategoryInfo.id_FIELDNAME));
		categoryInfoIds = em.createQuery(cq.where(p)).setMaxResults(maxCount).getResultList();
		if (categoryInfoIds == null) {
			categoryInfoIds = new ArrayList<>();
		}
		categoryInfoIds_out.addAll(categoryInfoIds);
		categoryInfoIds_out = excludListContent(categoryInfoIds_out, excludCategoryInfoIds);
		return categoryInfoIds_out;
	}

	/**
	 * 查询用户有权限访问的所有分类ID列表（ 不检测allPeopleView和allPeoplePublish, with List copy ）
	 * 
	 * @param personName
	 * @param unitNames
	 * @param groupNames
	 * @param inAppInfoIds
	 * @param inCategoryInfoIds
	 * @param excludCategoryInfoIds
	 * @return
	 * @throws Exception
	 */
	private List<String> listViewableCategoryIdsInPermission(EntityManagerContainer emc, String personName,
			List<String> unitNames, List<String> groupNames, List<String> inAppInfoIds, List<String> inCategoryInfoIds,
			List<String> excludCategoryInfoIds, String documentType, Integer maxCount, Boolean manager)
			throws Exception {
		List<String> categoryInfoIds = null;
		List<String> categoryInfoIds_out = new ArrayList<>();
		EntityManager em = emc.get(CategoryInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<CategoryInfo> root = cq.from(CategoryInfo.class);

		Predicate p = null;
		// 限定栏目范围
		if (ListTools.isNotEmpty(inAppInfoIds)) {
			p = CriteriaBuilderTools.predicate_and(cb, p, root.get(CategoryInfo.appId_FIELDNAME).in(inAppInfoIds));
		}
		// 限定范围
		if (ListTools.isNotEmpty(inCategoryInfoIds)) {
			p = CriteriaBuilderTools.predicate_and(cb, p, root.get(CategoryInfo.id_FIELDNAME).in(inCategoryInfoIds));
		}
		// 排除指定的ID列表
		if (ListTools.isNotEmpty(excludCategoryInfoIds)) {
			p = CriteriaBuilderTools.predicate_and(cb, p,
					cb.not(root.get(CategoryInfo.id_FIELDNAME).in(excludCategoryInfoIds)));
		}
		if (StringUtils.isNotEmpty(documentType) && !"全部".equals(documentType)
				&& !"all".equalsIgnoreCase(documentType)) {
			p = CriteriaBuilderTools.predicate_and(cb, p,
					cb.equal(root.get(CategoryInfo.documentType_FIELDNAME), documentType));
		}

		Predicate p_permission = null;
		if (StringUtils.isNotEmpty(personName)) {
			// 可以管理的栏目，肯定可以发布信息
			p_permission = CriteriaBuilderTools.predicate_or(cb, p_permission,
					cb.isMember(personName, root.get(CategoryInfo.viewablePersonList_FIELDNAME)));
			p_permission = CriteriaBuilderTools.predicate_or(cb, p_permission,
					cb.isMember(personName, root.get(CategoryInfo.manageablePersonList_FIELDNAME)));
			p_permission = CriteriaBuilderTools.predicate_or(cb, p_permission,
					cb.isMember(personName, root.get(CategoryInfo.publishablePersonList_FIELDNAME)));
		}
		if (ListTools.isNotEmpty(unitNames)) {
			p_permission = CriteriaBuilderTools.predicate_or(cb, p_permission,
					root.get(CategoryInfo.publishableUnitList_FIELDNAME).in(unitNames));
			p_permission = CriteriaBuilderTools.predicate_or(cb, p_permission,
					root.get(CategoryInfo.viewableUnitList_FIELDNAME).in(unitNames));
		}
		if (ListTools.isNotEmpty(groupNames)) {
			p_permission = CriteriaBuilderTools.predicate_or(cb, p_permission,
					root.get(CategoryInfo.publishableGroupList_FIELDNAME).in(groupNames));
			p_permission = CriteriaBuilderTools.predicate_or(cb, p_permission,
					root.get(CategoryInfo.viewableGroupList_FIELDNAME).in(groupNames));
		}

		p = CriteriaBuilderTools.predicate_and(cb, p, p_permission);
		cq.select(root.get(CategoryInfo.id_FIELDNAME));
		categoryInfoIds = em.createQuery(cq.where(p)).setMaxResults(maxCount).getResultList();
		if (categoryInfoIds == null) {
			categoryInfoIds = new ArrayList<>();
		}
		categoryInfoIds_out.addAll(categoryInfoIds);
		if (ListTools.isEmpty(categoryInfoIds_out)) {
			if (!manager) {
				categoryInfoIds_out.add("没有可用分类");
			}
		}
		categoryInfoIds_out = excludListContent(categoryInfoIds_out, excludCategoryInfoIds);
		return categoryInfoIds_out;
	}

	/**
	 * 查询用户有权限访问的所有栏目ID列表（ 不检测allPeopleView, with List copy ）
	 * 
	 * @param personName
	 * @param unitNames
	 * @param groupNames
	 * @return
	 * @throws Exception
	 */
	private List<String> listViewableAppIdsInPermission(EntityManagerContainer emc, String personName,
			List<String> unitNames, List<String> groupNames, String documentType, Integer maxCount) throws Exception {
		return listViewableAppIdsInPermission(emc, personName, unitNames, groupNames, null, null, documentType,
				maxCount);
	}

	/**
	 * 查询用户有权限访问的所有栏目ID列表（ 不检测allPeopleView, with List copy ）
	 * 
	 * @param personName
	 * @param unitNames
	 * @param groupNames
	 * @param inAppInfoIds
	 * @param excludAppInfoIds
	 * @return
	 * @throws Exception
	 */
	private List<String> listViewableAppIdsInPermission(EntityManagerContainer emc, String personName,
			List<String> unitNames, List<String> groupNames, List<String> inAppInfoIds, List<String> excludAppInfoIds,
			String documentType, Integer maxCount) throws Exception {
		List<String> appInfoIds = null;
		List<String> appInfoIds_out = new ArrayList<>();
		EntityManager em = emc.get(AppInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<AppInfo> root = cq.from(AppInfo.class);

		Predicate p = null;
		// 限定范围
		if (ListTools.isNotEmpty(inAppInfoIds)) {
			p = CriteriaBuilderTools.predicate_and(cb, p, root.get(AppInfo.id_FIELDNAME).in(inAppInfoIds));
		}
		// 排队指定的ID列表
		if (ListTools.isNotEmpty(excludAppInfoIds)) {
			p = CriteriaBuilderTools.predicate_and(cb, p, cb.not(root.get(AppInfo.id_FIELDNAME).in(excludAppInfoIds)));
		}
		if (StringUtils.isNotEmpty(documentType) && !"全部".equals(documentType)
				&& !"all".equalsIgnoreCase(documentType)) {
			p = CriteriaBuilderTools.predicate_and(cb, p,
					cb.equal(root.get(AppInfo.documentType_FIELDNAME), documentType));
		}

		Predicate p_permission = null;
		if (StringUtils.isNotEmpty(personName)) {
			// 可以管理的栏目，肯定可以看到
			p_permission = CriteriaBuilderTools.predicate_or(cb, p_permission,
					cb.isMember(personName, root.get(AppInfo.manageablePersonList_FIELDNAME)));
			// 可发布的栏目，肯定可以看到
			p_permission = CriteriaBuilderTools.predicate_or(cb, p_permission,
					cb.isMember(personName, root.get(AppInfo.publishablePersonList_FIELDNAME)));
			p_permission = CriteriaBuilderTools.predicate_or(cb, p_permission,
					cb.isMember(personName, root.get(AppInfo.viewablePersonList_FIELDNAME)));
		}
		if (ListTools.isNotEmpty(unitNames)) {
			p_permission = CriteriaBuilderTools.predicate_or(cb, p_permission,
					root.get(AppInfo.publishableUnitList_FIELDNAME).in(unitNames));
			p_permission = CriteriaBuilderTools.predicate_or(cb, p_permission,
					root.get(AppInfo.viewableUnitList_FIELDNAME).in(unitNames));
		}
		if (ListTools.isNotEmpty(groupNames)) {
			p_permission = CriteriaBuilderTools.predicate_or(cb, p_permission,
					root.get(AppInfo.publishableGroupList_FIELDNAME).in(groupNames));
			p_permission = CriteriaBuilderTools.predicate_or(cb, p_permission,
					root.get(AppInfo.viewableGroupList_FIELDNAME).in(groupNames));
		}

		p = CriteriaBuilderTools.predicate_and(cb, p, p_permission);
		cq.select(root.get(AppInfo.id_FIELDNAME));
		appInfoIds = em.createQuery(cq.where(p)).setMaxResults(maxCount).getResultList();
		if (appInfoIds == null) {
			appInfoIds = new ArrayList<>();
		}
		appInfoIds_out.addAll(appInfoIds);
		appInfoIds_out = excludListContent(appInfoIds_out, excludAppInfoIds);
		return appInfoIds_out;
	}

	/**
	 * 查询用户有权限访问的所有栏目ID列表（ 不检测allPeopleView, with List copy ）
	 * @param emc
	 * @param inAppInfoIds
	 * @param excludAppInfoIds
	 * @param documentType
	 * @param maxCount
	 * @return
	 * @throws Exception
	 */
	private List<String> listAllAppIds(EntityManagerContainer emc, List<String> inAppInfoIds,
			List<String> excludAppInfoIds, String documentType, Integer maxCount) throws Exception {
		List<String> appInfoIds = null;
		List<String> appInfoIds_out = new ArrayList<>();
		EntityManager em = emc.get(AppInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<AppInfo> root = cq.from(AppInfo.class);

		Predicate p = null;
		// 限定范围
		if (ListTools.isNotEmpty(inAppInfoIds)) {
			p = CriteriaBuilderTools.predicate_and(cb, p, root.get(AppInfo.id_FIELDNAME).in(inAppInfoIds));
		}
		// 排队指定的ID列表
		if (ListTools.isNotEmpty(excludAppInfoIds)) {
			p = CriteriaBuilderTools.predicate_and(cb, p, cb.not(root.get(AppInfo.id_FIELDNAME).in(excludAppInfoIds)));
		}
		if (StringUtils.isNotEmpty(documentType) && !"全部".equals(documentType)
				&& !"all".equalsIgnoreCase(documentType)) {
			p = CriteriaBuilderTools.predicate_and(cb, p,
					cb.equal(root.get(AppInfo.documentType_FIELDNAME), documentType));
		}
		cq.select(root.get(AppInfo.id_FIELDNAME));

		appInfoIds = em.createQuery(cq.where(p)).setMaxResults(maxCount).getResultList();
		if (appInfoIds == null) {
			appInfoIds = new ArrayList<>();
		}
		appInfoIds_out.addAll(appInfoIds);
		appInfoIds_out = excludListContent(appInfoIds_out, excludAppInfoIds);
		return appInfoIds_out;
	}

	/**
	 * 查询用户有权限发布文档的所有栏目ID列表（ 不检测allPeoplePublish, with List copy ）
	 * 
	 * @param personName
	 * @param unitNames
	 * @param groupNames
	 * @return
	 * @throws Exception
	 */
	private List<String> listPublishableAppIdsInPermission(EntityManagerContainer emc, String personName,
			List<String> unitNames, List<String> groupNames, String documentType, Integer maxCount) throws Exception {
		return listPublishableAppIdsInPermission(emc, personName, unitNames, groupNames, null, null, documentType,
				maxCount);
	}

	/**
	 * 查询用户有权限发布文档的所有栏目ID列表（ 不检测allPeopleView, with List copy ）
	 * 
	 * @param personName
	 * @param unitNames
	 * @param groupNames
	 * @param inAppInfoIds
	 * @param excludAppInfoIds
	 * @return
	 * @throws Exception
	 */
	private List<String> listPublishableAppIdsInPermission(EntityManagerContainer emc, String personName,
			List<String> unitNames, List<String> groupNames, List<String> inAppInfoIds, List<String> excludAppInfoIds,
			String documentType, Integer maxCount) throws Exception {
		List<String> appInfoIds = null;
		List<String> appInfoIds_out = new ArrayList<>();
		EntityManager em = emc.get(AppInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<AppInfo> root = cq.from(AppInfo.class);

		Predicate p = null;
		// 限定范围
		if (ListTools.isNotEmpty(inAppInfoIds)) {
			p = CriteriaBuilderTools.predicate_and(cb, p, cb.and(p, root.get(AppInfo.id_FIELDNAME).in(inAppInfoIds)));
		}
		// 排除指定的ID列表
		if (ListTools.isNotEmpty(excludAppInfoIds)) {
			p = CriteriaBuilderTools.predicate_and(cb, p, cb.not(root.get(AppInfo.id_FIELDNAME).in(excludAppInfoIds)));
		}
		if (StringUtils.isNotEmpty(documentType) && !"全部".equals(documentType)
				&& !"all".equalsIgnoreCase(documentType)) {
			p = CriteriaBuilderTools.predicate_and(cb, p,
					cb.equal(root.get(AppInfo.documentType_FIELDNAME), documentType));
		}

		Predicate p_permission = null;
		if (StringUtils.isNotEmpty(personName)) {
			// 可以管理的栏目，肯定可以发布信息
			p_permission = cb.isMember(personName, root.get(AppInfo.manageablePersonList_FIELDNAME));
			p_permission = cb.or(p_permission,
					cb.isMember(personName, root.get(AppInfo.publishablePersonList_FIELDNAME)));
		}
		if (ListTools.isNotEmpty(unitNames)) {
			p_permission = cb.or(p_permission, root.get(AppInfo.publishableUnitList_FIELDNAME).in(unitNames));
		}
		if (ListTools.isNotEmpty(groupNames)) {
			p_permission = cb.or(p_permission, root.get(AppInfo.publishableGroupList_FIELDNAME).in(groupNames));
		}

		p = CriteriaBuilderTools.predicate_and(cb, p, p_permission);
		cq.select(root.get(AppInfo.id_FIELDNAME));
		appInfoIds = em.createQuery(cq.where(p)).setMaxResults(maxCount).getResultList();
		if (appInfoIds == null) {
			appInfoIds = new ArrayList<>();
		}
		appInfoIds_out.addAll(appInfoIds);
		appInfoIds_out = excludListContent(appInfoIds_out, excludAppInfoIds);
		return appInfoIds_out;
	}

	/**
	 * 将一个List追加到SourceList里
	 * 
	 * @param result
	 * @param sourceList
	 */
	private List<String> addResultToSourceList(List<String> result, List<String> sourceList) {
		if (ListTools.isNotEmpty(result)) {
			for (String id : result) {
				if (!sourceList.contains(id)) {
					sourceList.add(id);
				}
			}
		}
		return sourceList;
	}

	/**
	 * 查询所有未设置可见权限的AppInfo的ID列表( with List copy ) 全员可发布的栏目也包含在内：判断allPeopleView or
	 * allPeoplePublish
	 * 
	 * @return
	 * @throws Exception
	 */
	private List<String> listAllPeopleViewAppIds(EntityManagerContainer emc, String documentType, String appType ) throws Exception {
		List<String> appInfoIds = null;
		List<String> appInfoIds_out = new ArrayList<>();
		EntityManager em = emc.get(AppInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<AppInfo> root = cq.from(AppInfo.class);
		cq.select(root.get(AppInfo.id_FIELDNAME));

		Predicate p = cb.isTrue(root.get(AppInfo.allPeopleView_FIELDNAME));
		if( StringUtils.isNotEmpty( appType ) && !"all".equalsIgnoreCase( appType ) && !"全部".equalsIgnoreCase( appType )   ){
			p = CriteriaBuilderTools.predicate_and(cb, p, cb.equal(root.get(AppInfo.appType_FIELDNAME), appType));
		}
		if (StringUtils.isNotEmpty(documentType) && !"全部".equals(documentType) && !"all".equalsIgnoreCase(documentType)) {
			p = CriteriaBuilderTools.predicate_and(cb, p, cb.equal(root.get(AppInfo.documentType_FIELDNAME), documentType));
		}
		appInfoIds = em.createQuery(cq.where(p)).getResultList();
		if (appInfoIds == null) {
			appInfoIds = new ArrayList<>();
		}
		appInfoIds_out.addAll(appInfoIds);
		return appInfoIds_out;
	}

	/**
	 * 查询所有未设置发布权限的AppInfo的ID列表( with List copy ) 判断 allPeoplePublish
	 * 
	 * @param emc
	 * @return
	 * @throws Exception
	 */
	private List<String> listAllPeoplePublishAppIds(EntityManagerContainer emc, String documentType, String appType) throws Exception {
		List<String> appInfoIds = null;
		List<String> appInfoIds_out = new ArrayList<>();
		EntityManager em = emc.get(AppInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<AppInfo> root = cq.from(AppInfo.class);
		cq.select(root.get(AppInfo.id_FIELDNAME));
		Predicate p = cb.isTrue(root.get(AppInfo.allPeoplePublish_FIELDNAME));
		if( StringUtils.isNotEmpty( appType ) && !"all".equalsIgnoreCase( appType ) && !"全部".equalsIgnoreCase( appType )  ){
			p = CriteriaBuilderTools.predicate_and(cb, p, cb.equal(root.get(AppInfo.appType_FIELDNAME), appType));
		}
		if (StringUtils.isNotEmpty(documentType) && !"全部".equals(documentType) && !"all".equalsIgnoreCase(documentType)) {
			p = cb.and(p, cb.equal(root.get(AppInfo.documentType_FIELDNAME), documentType));
		}
		appInfoIds = em.createQuery(cq.where(p)).getResultList();
		if (appInfoIds == null) {
			appInfoIds = new ArrayList<>();
		}
		appInfoIds_out.addAll(appInfoIds);
		return appInfoIds_out;
	}

	/**
	 * 查询所有用户都可以访问的分类ID列表(检测allPeopleView和allPeoplePublish)
	 * 
	 * @param inAppInfoIds      - 过滤栏目ID列表
	 * @param inCategoryIds     - 过滤分类ID列表
	 * @param excludCategoryIds - 排队分类ID列表
	 * @return
	 * @throws Exception
	 */
	private List<String> listAllPeopleViewCategoryIds(EntityManagerContainer emc, List<String> inAppInfoIds,
			List<String> inCategoryIds, List<String> excludCategoryIds, String documentType, Integer maxCount)
			throws Exception {
		List<String> categoryInfoIds = null;
		List<String> categoryInfoIds_out = new ArrayList<>();
		EntityManager em = emc.get(CategoryInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<CategoryInfo> root = cq.from(CategoryInfo.class);
		cq.select(root.get(CategoryInfo.id_FIELDNAME));

		Predicate p_all = cb.isTrue(root.get(CategoryInfo.allPeopleView_FIELDNAME));
		p_all = cb.or(p_all, cb.isTrue(root.get(CategoryInfo.allPeoplePublish_FIELDNAME)));

		Predicate p = null;
		if (ListTools.isNotEmpty(inAppInfoIds)) {
			p = CriteriaBuilderTools.predicate_and(cb, p, root.get(CategoryInfo.appId_FIELDNAME).in(inAppInfoIds));
		}
		if (ListTools.isNotEmpty(inCategoryIds)) {
			p = CriteriaBuilderTools.predicate_and(cb, p, root.get(CategoryInfo.id_FIELDNAME).in(inCategoryIds));
		}
		if (ListTools.isNotEmpty(excludCategoryIds)) {
			p = CriteriaBuilderTools.predicate_and(cb, p,
					cb.not(root.get(CategoryInfo.id_FIELDNAME).in(excludCategoryIds)));
		}
		if (StringUtils.isNotEmpty(documentType) && !"全部".equals(documentType)
				&& !"all".equalsIgnoreCase(documentType)) {
			p = CriteriaBuilderTools.predicate_and(cb, p,
					cb.equal(root.get(CategoryInfo.documentType_FIELDNAME), documentType));
		}

		p = CriteriaBuilderTools.predicate_and(cb, p, p_all);

		categoryInfoIds = em.createQuery(cq.where(p)).setMaxResults(maxCount).getResultList();
		if (categoryInfoIds == null) {
			categoryInfoIds = new ArrayList<>();
		}
		categoryInfoIds_out.addAll(categoryInfoIds);
		categoryInfoIds_out = excludListContent(categoryInfoIds_out, excludCategoryIds);
		return categoryInfoIds_out;
	}

	/**
	 * 查询所有用户都可以发布的分类ID列表(检测allPeoplePublish)
	 * 
	 * @param inAppInfoIds      - 过滤栏目ID列表
	 * @param inCategoryIds     - 过滤分类ID列表
	 * @param excludCategoryIds - 排队分类ID列表
	 * @return
	 * @throws Exception
	 */
	private List<String> listAllPeoplePublishCategoryIds(EntityManagerContainer emc, List<String> inAppInfoIds,
			List<String> inCategoryIds, List<String> excludCategoryIds, String documentType, Integer maxCount)
			throws Exception {
		List<String> categoryInfoIds = null;
		List<String> categoryInfoIds_out = new ArrayList<>();

		EntityManager em = emc.get(CategoryInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<CategoryInfo> root = cq.from(CategoryInfo.class);
		cq.select(root.get(CategoryInfo.id_FIELDNAME));

		Predicate p = cb.isTrue(root.get(CategoryInfo.allPeoplePublish_FIELDNAME));
		if (ListTools.isNotEmpty(inAppInfoIds)) {
			p = cb.and(p, root.get(CategoryInfo.appId_FIELDNAME).in(inAppInfoIds));
		}
		if (ListTools.isNotEmpty(inCategoryIds)) {
			p = cb.and(p, root.get(CategoryInfo.id_FIELDNAME).in(inCategoryIds));
		}
		if (ListTools.isNotEmpty(excludCategoryIds)) {
			p = cb.and(p, cb.not(root.get(CategoryInfo.id_FIELDNAME).in(excludCategoryIds)));
		}
		if (StringUtils.isNotEmpty(documentType) && !"全部".equals(documentType)
				&& !"all".equalsIgnoreCase(documentType)) {
			p = cb.and(p, cb.equal(root.get(CategoryInfo.documentType_FIELDNAME), documentType));
		}
		categoryInfoIds = em.createQuery(cq.where(p)).setMaxResults(maxCount).getResultList();
		if (categoryInfoIds == null) {
			categoryInfoIds = new ArrayList<>();
		}
		categoryInfoIds_out.addAll(categoryInfoIds);
		categoryInfoIds_out = excludListContent(categoryInfoIds_out, excludCategoryIds);
		return categoryInfoIds_out;
	}
}
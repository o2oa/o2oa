package com.x.cms.assemble.control.service;

import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.cms.core.entity.CmsPermissionService;

public class PermissionQueryService {
	
	private CmsPermissionService cmsPermissionService = new CmsPermissionService();
	/**
	 * 查询指定用户，组织，群组可以访问到的所有栏目ID列表(包含全员可以访问的栏目)
	 * @param personName
	 * @param isAnonymous
	 * @param unitNames
	 * @param groupNames
	 * @param inAppInfoIds
	 * @param excludAppInfoIds
	 * @return
	 * @throws Exception
	 */
	public List<String>listViewableAppIdByPerson( String personName, Boolean isAnonymous, List<String> unitNames,
			List<String> groupNames, List<String> inAppInfoIds, List<String> excludAppInfoIds, String documentType, Integer maxCount ) throws Exception{
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return cmsPermissionService.listViewableAppIdByPerson(
					emc, personName, isAnonymous, unitNames, groupNames, inAppInfoIds, excludAppInfoIds, documentType, maxCount);
		} catch (Exception e) {
			throw e;
		}
	}
	
	/**
	 * 查询指定用户，组织，群组可以发布文档的所有栏目ID列表
	 * @param personName
	 * @param isAnonymous
	 * @param unitNames
	 * @param groupNames
	 * @param inAppInfoIds
	 * @param excludAppInfoIds
	 * @return
	 * @throws Exception
	 */
	public List<String>listPublishableAppIdByPerson( String personName, Boolean isAnonymous, List<String> unitNames,
			List<String> groupNames, List<String> inAppInfoIds, List<String> excludAppInfoIds, String documentType, 
			Integer maxCount) throws Exception{
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return cmsPermissionService.listPublishableAppIdByPerson(
					emc, personName, isAnonymous, unitNames, groupNames, inAppInfoIds, excludAppInfoIds, documentType, maxCount);
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 查询指定用户可以管理的所有栏目ID列表( with List copy )
	 * @param emc
	 * @param personName
	 * @return
	 * @throws Exception
	 */
	public List<String> listManageableAppIdsByPerson( String personName, List<String> unitNames, List<String> groupNames,
			String documentType, Integer maxCount) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return cmsPermissionService.listManageableAppIdsByPerson(
					emc, personName, unitNames, groupNames, documentType, maxCount);
		} catch (Exception e) {
			throw e;
		}
	}
		
	/**
	 * 查询指定用户，组织，群组可以访问到的所有分类ID列表（包含全员可以访问的分类）
	 * 所获得到的分类列表可能会大于可访问的栏目列表
	 * 
	 * @param personName
	 * @param isAnonymous
	 * @param unitNames
	 * @param groupNames
	 * @param inAppInfoIds - 过滤栏目ID
	 * @param inCategoryInfoIds - 过滤分类ID
	 * @param excludCategoryInfoIds - 排队分类ID
	 * @return
	 * @throws Exception 
	 */
	public List<String>listViewableCategoryIdByPerson( String personName, Boolean isAnonymous, List<String> unitNames,
			List<String> groupNames, List<String> inAppInfoIds, List<String> inCategoryInfoIds, List<String> excludCategoryInfoIds, 
			String documentType, Integer maxCount, Boolean manager) throws Exception{
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return cmsPermissionService.listViewableCategoryIdByPerson(
					emc, personName, isAnonymous, unitNames, groupNames, inAppInfoIds, inCategoryInfoIds, excludCategoryInfoIds, documentType, maxCount, manager);
		} catch (Exception e) {
			throw e;
		}
	}
	
	/**
	 * 查询指定用户，组织，群组可以发布文档的所有分类ID列表（包含全员可以发布文档的分类）
	 * 所获得到的分类列表可能会大于可访问的栏目列表
	 * 
	 * @param personName
	 * @param isAnonymous
	 * @param unitNames
	 * @param groupNames
	 * @param inAppInfoIds - 过滤栏目ID
	 * @param inCategoryInfoIds - 过滤分类ID
	 * @param excludCategoryInfoIds - 排队分类ID
	 * @return
	 * @throws Exception
	 */
	public List<String>listPublishableCategoryIdByPerson( String personName, Boolean isAnonymous, List<String> unitNames,
			List<String> groupNames, List<String> inAppInfoIds, List<String> inCategoryInfoIds, List<String> excludCategoryInfoIds, 
			String documentType, Integer maxCount, Boolean manager) throws Exception{
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return cmsPermissionService.listPublishableCategoryIdByPerson(
					emc, personName, isAnonymous, unitNames, groupNames, inAppInfoIds, inCategoryInfoIds, excludCategoryInfoIds, documentType, maxCount, manager);
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 查询指定用户可以管理的所有分类ID列表( with List copy )
	 * @param emc
	 * @param personName
	 * @return
	 * @throws Exception
	 */
	public List<String> listManageableCategoryIdsByPerson( String personName, List<String> unitNames,
			List<String> groupNames, List<String> inAppInfoIds,
			String documentType, Integer maxCount, Boolean manager ) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return cmsPermissionService.listManageableCategoryIdsByPerson(
					emc, personName, unitNames, groupNames, inAppInfoIds, documentType, maxCount, manager);
		} catch (Exception e) {
			throw e;
		}
	}
}

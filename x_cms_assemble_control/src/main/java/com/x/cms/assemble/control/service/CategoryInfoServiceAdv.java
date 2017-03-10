package com.x.cms.assemble.control.service;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.http.EffectivePerson;
import com.x.cms.assemble.control.Business;
import com.x.cms.assemble.control.jaxrs.categoryinfo.WrapInCategoryInfo;
import com.x.cms.core.entity.CategoryInfo;

public class CategoryInfoServiceAdv {

	private UserManagerService userManagerService = new UserManagerService();
	private CategoryInfoService categoryInfoService = new CategoryInfoService();
	private AppCategoryAdminService appCategoryAdminService = new AppCategoryAdminService();
	private AppCategoryPermissionService appCategoryPermissionService = new AppCategoryPermissionService();
	
	public List<String> listByAppId( String appId ) throws Exception {
		if( appId == null || appId.isEmpty() ){
			throw new Exception("appId is null!");
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return categoryInfoService.listByAppId( emc, appId );
		} catch ( Exception e ) {
			throw e;
		}
	}

	/**
	 * 根据栏目ID以及用户的权限获取用户可访问所的所有分类ID列表
	 * 1、栏目下所有未设置访问权限的（全员可以访问的Category）
	 * 2、该用户自己为管理员的所有Category
	 * 3、该用户以及用户所在的部门，公司有权限访问的所有Category
	 * @param appId
	 * @param name
	 * @return
	 * @throws Exception 
	 */
	public List<String> listViewableByAppIdAndUserPermission( String appId, String person, String permission ) throws Exception {
		if( appId == null || appId.isEmpty() ){
			throw new Exception("appId is null!");
		}
		if( person == null || person.isEmpty() ){
			throw new Exception("name is null!");
		}
		List<String> viewAbleCategoryIds = new ArrayList<>();
		List<String> categoryIds = null;
		List<String> departmentNames = null;
		List<String> companyNames = null;
		
		//1、在指定APPID下所有未设置访问权限的（全员可以访问的Category）
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			categoryIds = categoryInfoService.listNoViewPermissionCategoryIds( emc, appId, permission );
			if (categoryIds != null && !categoryIds.isEmpty()) {
				for (String id : categoryIds) {
					if (!viewAbleCategoryIds.contains(id)) {
						viewAbleCategoryIds.add(id);
					}
				}
			}
		} catch (Exception e) {
			throw e;
		}
		
		// 2、该用户自己为管理员的所有Category
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			categoryIds = appCategoryAdminService.listCategoryInfoIdsByAdminName( emc, person, appId );
			if (categoryIds != null && !categoryIds.isEmpty()) {
				for (String id : categoryIds) {
					if (!viewAbleCategoryIds.contains(id)) {
						viewAbleCategoryIds.add(id);
					}
				}
			}
		} catch (Exception e) {
			throw e;
		}
		
		// 3、该用户以及用户所在的部门，公司有权限访问的所有Category
		departmentNames = userManagerService.listDepartmentNameByEmployeeName( person );
		companyNames = userManagerService.listCompanyNameByEmployeeName( person );
		
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			categoryIds = appCategoryPermissionService.listCategoryIdsByPermission(emc, person, departmentNames, companyNames, appId, permission );
			if ( categoryIds != null && !categoryIds.isEmpty()) {
				for ( String id : categoryIds ) {
					if (!viewAbleCategoryIds.contains(id)) {
						viewAbleCategoryIds.add(id);
					}
				}
			}
		} catch (Exception e) {
			throw e;
		}
		return viewAbleCategoryIds;
	}

	public List<CategoryInfo> list(List<String> ids) throws Exception {
		if( ids == null || ids.isEmpty() ){
			return null;
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return categoryInfoService.list( emc, ids );
		} catch ( Exception e ) {
			throw e;
		}
	}

	public CategoryInfo get( String id ) throws Exception {
		if( id == null || id.isEmpty() ){
			throw new Exception("id is null!");
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return categoryInfoService.get( emc, id );
		} catch ( Exception e ) {
			throw e;
		}
	}

	public List<CategoryInfo> listAll() throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return categoryInfoService.listAll( emc );
		} catch ( Exception e ) {
			throw e;
		}
	}
	
	public CategoryInfo saveBaseInfo( CategoryInfo categoryInfo, EffectivePerson currentPerson) throws Exception {
		if( categoryInfo == null ){
			throw new Exception( "categoryInfo is null." );
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			categoryInfo = categoryInfoService.saveBaseInfo( emc, categoryInfo );
		} catch ( Exception e ) {
			throw e;
		}
		return categoryInfo;
	}
	
	public CategoryInfo save( WrapInCategoryInfo wrapIn, EffectivePerson currentPerson ) throws Exception {
		if( wrapIn == null ){
			throw new Exception( "wrapIn is null." );
		}
		List<String> ids = null;
		CategoryInfo categoryInfo = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			categoryInfo = categoryInfoService.save( emc, wrapIn );
			//检查一下该应用栏目是否存在管理者，如果不存在，则将当前登录者作为应用栏目的管理者
			ids = appCategoryAdminService.listAppCategoryIdByCondition( emc, "CATEGORY", categoryInfo.getId(), currentPerson.getName() );
			if( ids == null || ids.isEmpty()  ){
				appCategoryAdminService.addNewAdminForCategoryInfo( emc, categoryInfo, currentPerson.getName() );
			}
		} catch ( Exception e ) {
			throw e;
		}
		return categoryInfo;
	}

	public Boolean categoryInfoEditAvailable( EffectivePerson currentPerson, String id) throws Exception {
		if( id == null || id.isEmpty() ){
			throw new Exception("id is null.");
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			return business.categoryInfoEditAvailable( currentPerson, id );
		} catch ( Exception e ) {
			throw e;
		}
	}

	public void delete( String id, EffectivePerson currentPerson) throws Exception {
		if( id == null || id.isEmpty() ){
			throw new Exception("id is null.");
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			categoryInfoService.delete( emc, id );
		} catch ( Exception e ) {
			throw e;
		}
	}
}

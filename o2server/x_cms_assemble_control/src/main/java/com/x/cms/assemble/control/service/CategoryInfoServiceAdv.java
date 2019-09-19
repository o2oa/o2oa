package com.x.cms.assemble.control.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.tools.ListTools;
import com.x.cms.core.entity.CategoryExt;
import com.x.cms.core.entity.CategoryInfo;
import com.x.query.core.entity.View;

/**
 * 对栏目分类信息进行管理的服务类（高级）
 * 高级服务器可以利用Service完成事务控制
 * 
 * @author O2LEE
 */
public class CategoryInfoServiceAdv {
	
	private CategoryInfoService categoryInfoService = new CategoryInfoService();

	public List<String> listIdsByAppId( String appId ) throws Exception {
		if (StringUtils.isEmpty( appId )) {
			throw new Exception("appId is null!");
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return categoryInfoService.listByAppId(emc, appId);
		} catch (Exception e) {
			throw e;
		}
	}

    public List<CategoryInfo> listByAppId( String appId ) throws Exception {
        if ( StringUtils.isEmpty(appId )) {
            throw new Exception("appId is null!");
        }
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            List<String> ids = categoryInfoService.listByAppId(emc, appId);
            if(ListTools.isNotEmpty(ids) ){
            	return emc.list( CategoryInfo.class , ids );
//                return categoryInfoService.list( emc, ids );
            }
            return null;
        } catch (Exception e) {
            throw e;
        }
    }
    
    public List<String> listCategoryIdsWithAppIds(List<String> viewAbleAppIds, String documentType, Boolean manager, Integer maxCount ) throws Exception {
    	if (ListTools.isEmpty( viewAbleAppIds )) {
//    		System.out.println(">>>>>>>>listCategoryIdsWithAppIds viewAbleAppIds is empty!");
           return new ArrayList<>();
        }
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
           return categoryInfoService.listByAppIds( emc, viewAbleAppIds, documentType, manager, maxCount );
        } catch (Exception e) {
            throw e;
        }
	}	

	public List<CategoryInfo> list(List<String> ids) throws Exception {
		if ( ListTools.isEmpty( ids )) {
			return null;
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return emc.list( CategoryInfo.class , ids );
//			return categoryInfoService.list(emc, ids);
		} catch (Exception e) {
			throw e;
		}
	}

	public CategoryInfo get( String id ) throws Exception {
		if ( StringUtils.isEmpty( id )) {
			throw new Exception("id is null!");
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return categoryInfoService.get(emc, id);
		} catch (Exception e) {
			throw e;
		}
	}
	
	public CategoryInfo getWithFlag( String flag ) throws Exception {
		if ( StringUtils.isEmpty( flag )) {
			throw new Exception("flag is empty!");
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return categoryInfoService.getWithFlag(emc, flag);
		} catch (Exception e) {
			throw e;
		}
	}
	
	public List<CategoryInfo> listAll() throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return categoryInfoService.listAll(emc);
		} catch (Exception e) {
			throw e;
		}
	}
	
	public List<String> listAllIds() throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return categoryInfoService.listAllIds(emc);
		} catch (Exception e) {
			throw e;
		}
	}
	
	public void inReview(String categoryId) throws Exception {
		if (StringUtils.isEmpty( categoryId )) {
			throw new Exception("categoryId is empty.");
		}
		CategoryInfo categoryInfo = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			categoryInfo = emc.find( categoryId, CategoryInfo.class );
			emc.beginTransaction( CategoryInfo.class );
			emc.persist( categoryInfo, CheckPersistType.all );
			emc.commit();
		} catch (Exception e) {
			throw e;
		}
	}

	public CategoryInfo saveBaseInfo(CategoryInfo categoryInfo, EffectivePerson currentPerson) throws Exception {
		if (categoryInfo == null) {
			throw new Exception("categoryInfo is null.");
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			categoryInfo = categoryInfoService.saveBaseInfo(emc, categoryInfo);
		} catch (Exception e) {
			throw e;
		}
		return categoryInfo;
	}

	public CategoryInfo save( CategoryInfo categoryInfo, String extContent, EffectivePerson currentPerson ) throws Exception {
		if ( categoryInfo == null ) {
			throw new Exception("wrapIn is null.");
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			// 检查一下该应用栏目是否存在管理者，如果不存在，则将当前登录者作为应用栏目的管理者
			if( ListTools.isEmpty( categoryInfo.getManageablePersonList())  && ListTools.isEmpty( categoryInfo.getManageableUnitList())  &&ListTools.isEmpty( categoryInfo.getManageableGroupList())) {
				categoryInfo.addManageablePerson( currentPerson.getDistinguishedName() );
			}
			categoryInfo = categoryInfoService.save( emc, categoryInfo, extContent );
		} catch (Exception e) {
			throw e;
		}
		return categoryInfo;
	}
	
	public CategoryExt  saveExtContent( String categoryId, String extContent, EffectivePerson currentPerson ) throws Exception {
		if ( categoryId == null ) {
			throw new Exception("categoryId is null.");
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return categoryInfoService.saveExtContent(emc, categoryId, extContent);
		} catch (Exception e) {
			throw e;
		}
	}

	public void delete(String id, EffectivePerson currentPerson) throws Exception {
		if ( StringUtils.isEmpty( id )) {
			throw new Exception("id is null.");
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			categoryInfoService.delete(emc, id);
		} catch (Exception e) {
			throw e;
		}
	}

	public List<String> listByAlias(String cataggoryAlias) throws Exception {
		if ( StringUtils.isEmpty( cataggoryAlias )) {
			throw new Exception("cataggoryAlias is null.");
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return categoryInfoService.listByAlias(emc, cataggoryAlias);
		} catch (Exception e) {
			throw e;
		}
	}


	public List<CategoryInfo> listCategoryInfoWithAliases(List<String> wrapIn_categoryAliasList) throws Exception {
		if (ListTools.isEmpty( wrapIn_categoryAliasList )) {
			return null;
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return categoryInfoService.listByAliases(emc, wrapIn_categoryAliasList);
		} catch (Exception e) {
			throw e;
		}
	}
	
	public String getExtContentWithId(String id) throws Exception {
		if ( StringUtils.isEmpty( id )) {
			return "{}";
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return categoryInfoService.getExtContentWithId( emc, id);
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 更新分类管理员权限信息
	 * @param categoryId
	 * @param personList
	 * @param unitList
	 * @param groupList
	 * @throws Exception 
	 */
	public void updateManagerPermission(String categoryId, List<String> personList, List<String> unitList, List<String> groupList) throws Exception {
		if ( StringUtils.isEmpty( categoryId )) {
			throw new Exception("categoryId is empty.");
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			CategoryInfo categoryInfo = emc.find( categoryId, CategoryInfo.class );
			emc.beginTransaction( CategoryInfo.class );
			categoryInfo.setManageablePersonList(personList);
			categoryInfo.setManageableUnitList(unitList);
			categoryInfo.setManageableGroupList(groupList);
			emc.check(categoryInfo , CheckPersistType.all );
			emc.commit();
		} catch (Exception e) {
			throw e;
		}
	}
	
	/**
	 * 更新分类发布权限信息
	 * @param categoryId
	 * @param personList
	 * @param unitList
	 * @param groupList
	 * @throws Exception 
	 */
	public void updatePublisherPermission(String categoryId, List<String> personList, List<String> unitList, List<String> groupList) throws Exception {
		if ( StringUtils.isEmpty( categoryId )) {
			throw new Exception("categoryId is empty.");
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			CategoryInfo categoryInfo = emc.find( categoryId, CategoryInfo.class );
			emc.beginTransaction( CategoryInfo.class );
			categoryInfo.setPublishablePersonList(personList);
			categoryInfo.setPublishableUnitList(unitList);
			categoryInfo.setPublishableGroupList(groupList);
			emc.check(categoryInfo , CheckPersistType.all );
			emc.commit();
		} catch (Exception e) {
			throw e;
		}
	}
	
	/**
	 * 更新分类可见权限信息
	 * @param categoryId
	 * @param personList
	 * @param unitList
	 * @param groupList
	 * @throws Exception 
	 */
	public void updateViewerPermission(String categoryId, List<String> personList, List<String> unitList, List<String> groupList) throws Exception {
		if ( StringUtils.isEmpty( categoryId )) {
			throw new Exception("categoryId is empty.");
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			CategoryInfo categoryInfo = emc.find( categoryId, CategoryInfo.class );
			emc.beginTransaction( CategoryInfo.class );
			categoryInfo.setViewablePersonList(personList);
			categoryInfo.setViewableUnitList(unitList);
			categoryInfo.setViewableGroupList(groupList);
			emc.check(categoryInfo , CheckPersistType.all );
			emc.commit();
		} catch (Exception e) {
			throw e;
		}
	}

	public void updateAllPermission( CategoryInfo categoryInfo ) throws Exception {
		if ( categoryInfo == null ) {
			throw new Exception("categoryInfo is null.");
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			CategoryInfo categoryInfo_entity = emc.find( categoryInfo.getId(), CategoryInfo.class );
			if( categoryInfo_entity != null ) {
				emc.beginTransaction( CategoryInfo.class );
				categoryInfo_entity.setManageablePersonList( categoryInfo.getManageablePersonList());
				categoryInfo_entity.setManageableUnitList( categoryInfo.getManageableUnitList());
				categoryInfo_entity.setManageableGroupList( categoryInfo.getManageableGroupList() );
				categoryInfo_entity.setPublishablePersonList( categoryInfo.getPublishablePersonList() );
				categoryInfo_entity.setPublishableUnitList( categoryInfo.getPublishableUnitList() );
				categoryInfo_entity.setPublishableGroupList( categoryInfo.getPublishableGroupList() );
				categoryInfo_entity.setViewablePersonList( categoryInfo.getViewablePersonList() );
				categoryInfo_entity.setViewableUnitList( categoryInfo.getViewableUnitList() );
				categoryInfo_entity.setViewableGroupList( categoryInfo.getViewableGroupList() );
				if( StringUtils.isEmpty( categoryInfo_entity.getDocumentType() )) {
					categoryInfo_entity.setDocumentType( "信息" );
				}
				emc.check(categoryInfo_entity , CheckPersistType.all );
				emc.commit();
			}
		} catch (Exception e) {
			throw e;
		}
	}	

	/**
	 * 判断用户是否为指定分类的管理员
	 * @param categoryId
	 * @param personName
	 * @param units
	 * @param groups
	 * @return
	 * @throws Exception
	 */
	public Boolean isCategoryInfoManager(String categoryId, String personName,  List<String> unitNames, List<String> groupNames ) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			CategoryInfo categoryInfo = emc.find( categoryId, CategoryInfo.class );
			return isCategoryInfoManager(categoryInfo, personName, unitNames, groupNames );
		} catch (Exception e) {
			throw e;
		}
	}
	
	/**
	 * 判断用户是否拥有指定分类的发布者权限
	 * @param categoryId
	 * @param personName
	 * @param unitNames
	 * @param groupNames
	 * @return
	 * @throws Exception
	 */
	public Boolean isCategoryInfoPublisher(String categoryId, String personName, List<String> unitNames, List<String> groupNames ) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			CategoryInfo categoryInfo = emc.find( categoryId, CategoryInfo.class );
			return  isCategoryInfoPublisher(categoryInfo, personName, unitNames, groupNames);
		} catch (Exception e) {
			throw e;
		}
	}
	
	/**
	 *  判断用户是否拥有指定分类的访问权限
	 * @param categoryId
	 * @param personName
	 * @param unitNames
	 * @param groupNames
	 * @return
	 * @throws Exception
	 */
	public Boolean isCategoryInfoViewer(String categoryId, String personName, List<String> unitNames, List<String> groupNames ) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			CategoryInfo categoryInfo = emc.find( categoryId, CategoryInfo.class );
			return isCategoryInfoViewer(categoryInfo, personName, unitNames, groupNames);
		} catch (Exception e) {
			throw e;
		}
	}
	
	/**
	 * 判断用户是否为指定分类的管理员
	 * @param categoryInfo
	 * @param personName
	 * @param units
	 * @param groups
	 * @return
	 * @throws Exception
	 */
	public Boolean isCategoryInfoManager( CategoryInfo categoryInfo, String personName,  List<String> unitNames, List<String> groupNames) throws Exception {
		if( categoryInfo != null ){
			if( ListTools.isNotEmpty( categoryInfo.getManageablePersonList() )){
				if( categoryInfo.getManageablePersonList().contains( personName )) {
					return true;
				}
			}
			if( ListTools.isNotEmpty( categoryInfo.getManageableUnitList() )){
				if( ListTools.containsAny( unitNames, categoryInfo.getManageableUnitList()) ) {
					return true;
				}
			}
			if( ListTools.isNotEmpty( categoryInfo.getManageableGroupList() )){
				if( ListTools.containsAny( groupNames, categoryInfo.getManageableGroupList()) ) {
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * 判断用户是否拥有指定分类的发布者权限
	 * @param categoryInfo
	 * @param personName
	 * @param unitNames
	 * @param groupNames
	 * @return
	 * @throws Exception
	 */
	public Boolean isCategoryInfoPublisher( CategoryInfo categoryInfo, String personName, List<String> unitNames, List<String> groupNames ) throws Exception {
		if( categoryInfo != null ) {
			if( ListTools.isNotEmpty( categoryInfo.getPublishablePersonList() )){
				if( categoryInfo.getPublishablePersonList().contains( personName )) {
					return true;
				}
			}
			if( ListTools.isNotEmpty( categoryInfo.getManageablePersonList() )){
				if( categoryInfo.getManageablePersonList().contains( personName )) {
					return true;
				}
			}
			if( ListTools.isNotEmpty( categoryInfo.getPublishableUnitList() )){
				if( ListTools.containsAny( unitNames, categoryInfo.getPublishableUnitList() )) {
					return true;
				}
			}
			if( ListTools.isNotEmpty( categoryInfo.getManageableUnitList() )){
				if( ListTools.containsAny( unitNames, categoryInfo.getManageableUnitList() )) {
					return true;
				}
			}
			if( ListTools.isNotEmpty( categoryInfo.getPublishableGroupList() )){
				if( ListTools.containsAny( groupNames, categoryInfo.getPublishableGroupList() )) {
					return true;
				}
			}
			if( ListTools.isNotEmpty( categoryInfo.getManageableGroupList() )){
				if( ListTools.containsAny( groupNames, categoryInfo.getManageableGroupList() )) {
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 *  判断用户是否拥有指定分类的访问权限
	 * @param categoryInfo
	 * @param personName
	 * @param unitNames
	 * @param groupNames
	 * @return
	 * @throws Exception
	 */
	public Boolean isCategoryInfoViewer( CategoryInfo categoryInfo, String personName, List<String> unitNames, List<String> groupNames ) throws Exception {
		if( unitNames == null ) { unitNames = new ArrayList<>(); }
		if( groupNames == null ) { groupNames = new ArrayList<>(); }
		
		if( categoryInfo != null ) {
			if( ListTools.isNotEmpty( categoryInfo.getViewablePersonList() )){
				if( categoryInfo.getViewablePersonList().contains( personName )) {
					return true;
				}				
			}
			if( ListTools.isNotEmpty( categoryInfo.getPublishablePersonList() )){
				if( categoryInfo.getPublishablePersonList().contains( personName )) {
					return true;
				}
			}			
			if( ListTools.isNotEmpty( categoryInfo.getManageablePersonList() )){
				if( categoryInfo.getManageablePersonList().contains( personName )) {
					return true;
				}
			}
			if( ListTools.isNotEmpty( categoryInfo.getViewableUnitList() )){
				if( ListTools.containsAny( unitNames, categoryInfo.getViewableUnitList() )) {
					return true;
				}
			}
			if( ListTools.isNotEmpty( categoryInfo.getPublishableUnitList() )){
				if( ListTools.containsAny( unitNames, categoryInfo.getPublishableUnitList() )) {
					return true;
				}
			}
			if( ListTools.isNotEmpty( categoryInfo.getManageableUnitList() )){
				if( ListTools.containsAny( unitNames, categoryInfo.getManageableUnitList() )) {
					return true;
				}
			}
			
			if( ListTools.isNotEmpty( categoryInfo.getViewableGroupList() )){
				if( ListTools.containsAny( groupNames, categoryInfo.getViewableGroupList() )) {
					return true;
				}
			}
			if( ListTools.isNotEmpty( categoryInfo.getPublishableGroupList() )){
				if( ListTools.containsAny( groupNames, categoryInfo.getPublishableGroupList() )) {
					return true;
				}
			}
			if( ListTools.isNotEmpty( categoryInfo.getManageableGroupList() )){
				if( ListTools.containsAny( groupNames, categoryInfo.getManageableGroupList() )) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 为分类绑定导入的列表信息ID
	 * @param categoryInfo
	 * @param view
	 * @throws Exception
	 */
	public void bindImportViewId(CategoryInfo categoryInfo, View view, String viewAppId) throws Exception {
		if ( categoryInfo == null ) {
			throw new Exception("categoryId is empty!");
		}		
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			categoryInfo = emc.find( categoryInfo.getId(), CategoryInfo.class );
			emc.beginTransaction( CategoryInfo.class );
			if ( view == null ) {
				categoryInfo.setImportViewAppId( null );
				categoryInfo.setImportViewName( null );
				categoryInfo.setImportViewId( null );
			}else {
				categoryInfo.setImportViewId( view.getId() );
				categoryInfo.setImportViewName(view.getName());
				categoryInfo.setImportViewAppId( viewAppId );
			}
			emc.check(categoryInfo , CheckPersistType.all );
			emc.commit();
		} catch (Exception e) {
			throw e;
		}
	}

	public void reviewed(String categoryId) throws Exception {
		if ( StringUtils.isEmpty( categoryId )) {
			return;
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			CategoryInfo categoryInfo = emc.find( categoryId, CategoryInfo.class );
			emc.beginTransaction( CategoryInfo.class );
			emc.persist( categoryInfo, CheckPersistType.all );
			emc.commit();
		} catch (Exception e) {
			throw e;
		}
	}	
}

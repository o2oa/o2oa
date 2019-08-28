package com.x.cms.assemble.control.factory;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.tools.ListTools;
import com.x.cms.assemble.control.AbstractFactory;
import com.x.cms.assemble.control.Business;
import com.x.cms.core.entity.CategoryInfo;
import com.x.cms.core.entity.CategoryInfo_;

/**
 * 分类信息基础功能服务类
 * 
 * @author O2LEE
 */
public class CategoryInfoFactory extends AbstractFactory {

	public CategoryInfoFactory(Business business) throws Exception {
		super(business);
	}

	//@MethodDescribe("获取指定Id的CategoryInfo分类信息对象")
	public CategoryInfo get( String id ) throws Exception {
		return this.entityManagerContainer().find( id, CategoryInfo.class );
	}
	
	//@MethodDescribe("列示全部的CategoryInfo分类信息列表")
	@SuppressWarnings("unused")
	public List<CategoryInfo> listAll() throws Exception {
		EntityManager em = this.entityManagerContainer().get( CategoryInfo.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<CategoryInfo> cq = cb.createQuery( CategoryInfo.class );
		Root<CategoryInfo> root = cq.from( CategoryInfo.class );
		return em.createQuery(cq).getResultList();
	}
	
	//@MethodDescribe("列示全部的CategoryInfo分类信息列表")
	public List<String> listAllIds() throws Exception {
		EntityManager em = this.entityManagerContainer().get( CategoryInfo.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery( String.class );
		Root<CategoryInfo> root = cq.from( CategoryInfo.class );
		cq.select(root.get(CategoryInfo_.id));
		return em.createQuery(cq).getResultList();
	}
	
	//@MethodDescribe("列示指定Id的CategoryInfo分类信息列表")
//	public List<CategoryInfo> list(List<String> ids) throws Exception {
//		EntityManager em = this.entityManagerContainer().get( CategoryInfo.class );
//		CriteriaBuilder cb = em.getCriteriaBuilder();
//		CriteriaQuery<CategoryInfo> cq = cb.createQuery( CategoryInfo.class );
//		Root<CategoryInfo> root = cq.from( CategoryInfo.class );
//		Predicate p = root.get( CategoryInfo_.id).in(ids);
//		return em.createQuery(cq.where(p)).getResultList();
//	}
	
	//@MethodDescribe("根据应用ID列示所有的CategoryInfo分类信息列表")
	public List<String> listByAppId( String appId ) throws Exception {
		EntityManager em = this.entityManagerContainer().get( CategoryInfo.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery( String.class );
		Root<CategoryInfo> root = cq.from( CategoryInfo.class );
		Predicate p = cb.equal(root.get( CategoryInfo_.appId ), appId );
		cq.select(root.get( CategoryInfo_.id));
		return em.createQuery( cq.where(p) ).setMaxResults(1000).getResultList();
	}

	public List<String> listByAppIds( List<String> appIds, String documentType, Boolean manager, Integer maxCount ) throws Exception {
		EntityManager em = this.entityManagerContainer().get( CategoryInfo.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery( String.class );
		Root<CategoryInfo> root = cq.from( CategoryInfo.class );
		cq.select(root.get( CategoryInfo_.id));		
		TypedQuery<String> query = null;
		if( manager ) {
			if( ListTools.isEmpty( appIds )) {
				if( StringUtils.isNotEmpty( documentType ) && !"全部".equals(documentType)&& !"all".equalsIgnoreCase(documentType)) {
					Predicate p = cb.equal( root.get( CategoryInfo_.documentType ), documentType );					
					query = em.createQuery( cq.where( p ) ).setMaxResults(maxCount);
				}else {
					query =  em.createQuery( cq ).setMaxResults(maxCount);
				}
			}else {
				Predicate p = root.get( CategoryInfo_.appId ).in(appIds);
				if( StringUtils.isNotEmpty( documentType ) && !"全部".equals(documentType)&& !"all".equalsIgnoreCase(documentType) ) {
					p = cb.and( p, cb.equal( root.get( CategoryInfo_.documentType ), documentType ));
				}
				query = em.createQuery( cq.where(p) ).setMaxResults(maxCount);
			}
		}else {
			if( ListTools.isEmpty( appIds )) {
				return new ArrayList<>();
			}else {
				Predicate p = root.get( CategoryInfo_.appId ).in(appIds);
				if( StringUtils.isNotEmpty( documentType ) && !"全部".equals(documentType)&& !"all".equalsIgnoreCase(documentType)) {
					p = cb.and( p, cb.equal( root.get( CategoryInfo_.documentType ), documentType ));
				}
				query = em.createQuery( cq.where(p) ).setMaxResults(maxCount);
			}
		}
		
		//System.out.println(">>>>>>>>>>>listByAppIds SQL:" +query.toString() );
		return query.getResultList();
	}
	
	public List<CategoryInfo> listCategoryByAppId( String appId, String documentType, Integer maxCount ) throws Exception {
		EntityManager em = this.entityManagerContainer().get( CategoryInfo.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<CategoryInfo> cq = cb.createQuery( CategoryInfo.class );
		Root<CategoryInfo> root = cq.from( CategoryInfo.class );
		Predicate p = cb.equal(root.get( CategoryInfo_.appId ), appId );
		if( StringUtils.isNotEmpty( documentType ) && !"全部".equals(documentType)&& !"all".equalsIgnoreCase(documentType)) {
			p = cb.and( p, cb.equal( root.get( CategoryInfo_.documentType ), documentType ));
		}
		return em.createQuery( cq.where(p) ).setMaxResults(maxCount).getResultList();
	}
	
	//@MethodDescribe("根据应用ID列示所有的CategoryInfo分类信息数量")
	public Long countByAppId( String appId, String documentType ) throws Exception {
		EntityManager em = this.entityManagerContainer().get(CategoryInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<CategoryInfo> root = cq.from(CategoryInfo.class);
		Predicate p = cb.equal( root.get(CategoryInfo_.appId), appId );
		if( StringUtils.isNotEmpty( documentType ) && !"全部".equals(documentType)&& !"all".equalsIgnoreCase(documentType)) {
			p = cb.and( p, cb.equal( root.get( CategoryInfo_.documentType ), documentType ));
		}
		cq.select(cb.count(root));
		return em.createQuery(cq.where(p)).getSingleResult();
	}
	
	//@MethodDescribe("根据分类ID列示所有下级CategoryInfo分类信息列表")
	public List<String> listByParentId( String categoryId ) throws Exception {
		EntityManager em = this.entityManagerContainer().get( CategoryInfo.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery( String.class );
		Root<CategoryInfo> root = cq.from( CategoryInfo.class );
		Predicate p = cb.equal(root.get( CategoryInfo_.parentId ), categoryId );
		cq.select(root.get( CategoryInfo_.id));
		return em.createQuery( cq.where(p) ).setMaxResults(100).getResultList();
	}

	//@MethodDescribe("对分类信息进行模糊查询，并且返回信息列表.")
	public List<String> listLike(String keyStr, String documentType ) throws Exception {
		String str = keyStr.replaceAll("_", "\\\\_");
		str = str.replaceAll("%", "\\\\%");
		str = str.toLowerCase();
		EntityManager em = this.entityManagerContainer().get( CategoryInfo.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<CategoryInfo> root = cq.from( CategoryInfo.class);
		Predicate p = cb.like(root.get( CategoryInfo_.categoryName ), "%" + str + "%", '\\');
		p = cb.or(p, cb.like(root.get( CategoryInfo_.categoryAlias ), str + "%", '\\'));
		if( StringUtils.isNotEmpty( documentType ) && !"全部".equals(documentType)&& !"all".equalsIgnoreCase(documentType)) {
			p = cb.and( p, cb.equal( root.get( CategoryInfo_.documentType ), documentType ));
		}
		cq.select(root.get( CategoryInfo_.id));
		return em.createQuery(cq.where(p)).setMaxResults(200).getResultList();
	}
	
	public List<String> listMyCategoryWithAppId( List<String> myCategoryIds, String documentType, String appId ) throws Exception {
		EntityManager em = this.entityManagerContainer().get( CategoryInfo.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<CategoryInfo> root = cq.from(CategoryInfo.class);
		cq.select(root.get(CategoryInfo_.id));
		Predicate p = cb.equal( root.get( CategoryInfo_.appId ), appId );
		if( myCategoryIds != null && !myCategoryIds.isEmpty() ){
			p = cb.and( p, root.get( CategoryInfo_.id ).in( myCategoryIds ) );
		}
		if( StringUtils.isNotEmpty( documentType) && !"全部".equals(documentType) && !"all".equalsIgnoreCase(documentType)) {
			p = cb.and( p, cb.equal( root.get( CategoryInfo_.documentType), documentType));
		}
		return em.createQuery(cq.where( p )).getResultList();
	}

	public List<String> listByAlias( List<String> categoryAlias) throws Exception {
		if(ListTools.isEmpty( categoryAlias )) {
			return null;
		}
		EntityManager em = this.entityManagerContainer().get( CategoryInfo.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<CategoryInfo> root = cq.from(CategoryInfo.class);
		cq.select(root.get(CategoryInfo_.id));
		Predicate p = root.get( CategoryInfo_.categoryAlias ).in( categoryAlias );
		return em.createQuery(cq.where( p )).getResultList();
	}
	
	public List<CategoryInfo> listByAliases(List<String> categoryAlias) throws Exception {
		if(ListTools.isEmpty( categoryAlias )) {
			return null;
		}
		EntityManager em = this.entityManagerContainer().get( CategoryInfo.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<CategoryInfo> cq = cb.createQuery(CategoryInfo.class);
		Root<CategoryInfo> root = cq.from(CategoryInfo.class);
		Predicate p = root.get( CategoryInfo_.categoryAlias ).in( categoryAlias );
		return em.createQuery(cq.where( p )).getResultList();
	}

	/**
	 * 根据权限查询用户可以发布文档的分类ID列表(根据权限, 不检测allPeopleView和allPeoplePublish)
	 * 可管理的分类也属于可发布的分类
	 * @param personName
	 * @param unitNames
	 * @param groupNames
	 * @param inAppInfoIds  - 需要限定的栏目ID列表
	 * @param inCategoryIds  - 栏目ID的最大范围
	 * @param excludCategoryIds - 需要排除的栏目ID
	 * @return
	 * @throws Exception 
	 */
	public List<String> listPublishableCategoryInfoIdsWithPermission(String personName, List<String> unitNames,
			List<String> groupNames, List<String> inAppInfoIds, List<String> inCategoryIds,
			List<String> excludCategoryIds, String documentType, Integer maxCount ) throws Exception {
		EntityManager em = this.entityManagerContainer().get(CategoryInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<CategoryInfo> root = cq.from(CategoryInfo.class);
		
		Predicate p = null;
		Predicate p_filter = null;
		//限定栏目范围
		if( ListTools.isNotEmpty( inAppInfoIds )) {
			p_filter = root.get( CategoryInfo_.appId ).in( inAppInfoIds );
		}
		//限定范围
		if( ListTools.isNotEmpty( inCategoryIds )) {
			if( p_filter == null ) {
				p_filter = root.get( CategoryInfo_.id ).in( inCategoryIds );
			}else {
				p_filter = cb.and( p_filter, root.get( CategoryInfo_.id ).in( inCategoryIds ));
			}
		}
		//排除指定的ID列表
		if( ListTools.isNotEmpty( excludCategoryIds )) {
			if( p_filter == null ) {
				p_filter = cb.not( root.get( CategoryInfo_.id ).in( excludCategoryIds ));
			}else {
				p_filter = cb.and( p_filter, cb.not( root.get( CategoryInfo_.id ).in( excludCategoryIds )));
			}
		}		
		Predicate p_permission = null;
		if( StringUtils.isNotEmpty( personName )) {
			//可以管理的栏目，肯定可以发布信息
			p_permission = cb.isMember( personName, root.get( CategoryInfo_.manageablePersonList ));	
			p_permission = cb.or( p_permission, cb.isMember( personName, root.get( CategoryInfo_.publishablePersonList )));			
		}
		if( ListTools.isNotEmpty( unitNames )) {
			if( p_permission == null  ) {
				p_permission = root.get( CategoryInfo_.publishableUnitList).in(unitNames);
			}else {
				p_permission = cb.or( p_permission,  root.get( CategoryInfo_.publishableUnitList).in(unitNames));
			}
		}
		if( ListTools.isNotEmpty( groupNames )) {
			if( p_permission == null  ) {
				p_permission = root.get( CategoryInfo_.publishableGroupList).in(groupNames);
			}else {
				p_permission = cb.or( p_permission,  root.get( CategoryInfo_.publishableGroupList).in(groupNames));
			}
		}
		
		//使用新的条件将两个条件组合起来
		if( p_filter != null ) {
			p = p_filter;
		}
		if( p != null ) {
			if( p_permission != null ) {
				p = cb.and(p, p_permission);
			}
		}else {
			if( p_permission != null ) {
				p = p_permission;
			}
		}
		if( StringUtils.isNotEmpty( documentType) && !"全部".equals(documentType)&& !"all".equalsIgnoreCase(documentType)) {
			p = cb.and( p, cb.equal( root.get( CategoryInfo_.documentType), documentType));
		}
		cq.select(root.get( CategoryInfo_.id ));
		return em.createQuery( cq.where( p ) ).setMaxResults(maxCount).getResultList();
	}

	/**
	 * 根据权限查询用户可以访问文档的分类ID列表(根据权限, 不检测allPeopleView和allPeoplePublish)
	 * 可发布、可管理的分类也属于可见分类
	 * @param personName
	 * @param unitNames
	 * @param groupNames
	 * @param inAppInfoIds  - 需要限定的栏目ID列表
	 * @param inCategoryIds  - 栏目ID的最大范围
	 * @param excludCategoryIds - 需要排除的栏目ID
	 * @return
	 * @throws Exception 
	 */
	public List<String> listViewableCategoryInfoIdsWithPermission(String personName, List<String> unitNames, List<String> groupNames,
			List<String> inAppInfoIds, List<String> inCategoryIds, List<String> excludCategoryIds,
			String documentType, Integer maxCount ) throws Exception {
		EntityManager em = this.entityManagerContainer().get(CategoryInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<CategoryInfo> root = cq.from(CategoryInfo.class);
		
		Predicate p = null;
		Predicate p_filter = null;
		//限定栏目范围
		if( ListTools.isNotEmpty( inAppInfoIds )) {
			p_filter = root.get( CategoryInfo_.appId ).in( inAppInfoIds );
		}
		//限定范围
		if( ListTools.isNotEmpty( inCategoryIds )) {
			if( p_filter == null ) {
				p_filter = root.get( CategoryInfo_.id ).in( inCategoryIds );
			}else {
				p_filter = cb.and( p_filter, root.get( CategoryInfo_.id ).in( inCategoryIds ));
			}
		}		
		//排除指定的ID列表
		if( ListTools.isNotEmpty( excludCategoryIds )) {
			if( p_filter == null ) {
				p_filter = cb.not( root.get( CategoryInfo_.id ).in( excludCategoryIds ));
			}else {
				p_filter = cb.and( p_filter, cb.not( root.get( CategoryInfo_.id ).in( excludCategoryIds )));
			}
		}
		
		Predicate p_permission = null;
		if( StringUtils.isNotEmpty( personName )) {
			//可以管理的栏目，肯定可以发布信息
			p_permission = cb.isMember( personName, root.get( CategoryInfo_.manageablePersonList ));	
			p_permission = cb.or( p_permission, cb.isMember( personName, root.get( CategoryInfo_.publishablePersonList )));
		}
		if( ListTools.isNotEmpty( unitNames )) {
			if( p_permission == null ){
				p_permission =  root.get( CategoryInfo_.publishableUnitList).in(unitNames);
				p_permission = cb.or( p_permission,  root.get( CategoryInfo_.viewableUnitList).in(unitNames));
			}else {
				p_permission = cb.or( p_permission,  root.get( CategoryInfo_.publishableUnitList).in(unitNames));
				p_permission = cb.or( p_permission,  root.get( CategoryInfo_.viewableUnitList).in(unitNames));
			}
		}
		if( ListTools.isNotEmpty( groupNames )) {
			if( p_permission == null ){
				p_permission = root.get( CategoryInfo_.publishableGroupList).in(groupNames);
				p_permission = cb.or( p_permission,  root.get( CategoryInfo_.viewableGroupList).in(groupNames));
			}else {
				p_permission = cb.or( p_permission,  root.get( CategoryInfo_.publishableGroupList).in(groupNames));
				p_permission = cb.or( p_permission,  root.get( CategoryInfo_.viewableGroupList).in(groupNames));
			}
		}
		
		//使用新的条件将两个条件组合起来
		if( p_filter != null ) {
			p = p_filter;
		}
		if( p != null ) {
			if( p_permission != null ) {
				p = cb.and(p, p_permission);
			}
		}else {
			if( p_permission != null ) {
				p = p_permission;
			}
		}
		if( StringUtils.isNotEmpty( documentType) && !"全部".equals(documentType) && !"all".equalsIgnoreCase(documentType)) {
			p = cb.and( p, cb.equal( root.get( CategoryInfo_.documentType), documentType));
		}
		cq.select(root.get( CategoryInfo_.id ));
		return em.createQuery( cq.where( p ) ).setMaxResults(maxCount).getResultList();
	}

	/**
	 * 查询所有用户都可以发布的分类ID列表
	 * @param inAppInfoIds
	 * @param inCategoryIds
	 * @param excludCategoryIds
	 * @return
	 * @throws Exception 
	 */
	public List<String> listAllPeoplePublishableCategoryInfoIds(List<String> inAppInfoIds, List<String> inCategoryIds,
			List<String> excludCategoryIds, String documentType, Integer maxCount ) throws Exception {
		EntityManager em = this.entityManagerContainer().get( CategoryInfo.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<CategoryInfo> root = cq.from(CategoryInfo.class);
		cq.select(root.get(CategoryInfo_.id));
		Predicate p = cb.isTrue( root.get( CategoryInfo_.allPeoplePublish ) );
		if( ListTools.isNotEmpty( inAppInfoIds )) {
			p = cb.and( p,  root.get( CategoryInfo_.appId ).in( inAppInfoIds ) );
		}
		if( ListTools.isNotEmpty( inCategoryIds )) {
			p = cb.and( p, root.get( CategoryInfo_.id ).in( inCategoryIds ));
		}
		if( ListTools.isNotEmpty( excludCategoryIds )) {
			p = cb.and( p, cb.not( root.get( CategoryInfo_.id ).in( excludCategoryIds )));
		}
		if( StringUtils.isNotEmpty( documentType) && !"全部".equals(documentType) && !"all".equalsIgnoreCase(documentType)) {
			p = cb.and( p, cb.equal( root.get( CategoryInfo_.documentType), documentType));
		}
		return em.createQuery(cq.where( p )).setMaxResults(maxCount).getResultList();
	}
	
	/**
	 * 查询所有用户都可以发布的分类ID列表(检测allPeopleView和allPeoplePublish)
	 * @param inAppInfoIds
	 * @param inCategoryIds
	 * @param excludCategoryIds
	 * @return
	 * @throws Exception 
	 */
	public List<String> listAllPeopleViewableCategoryInfoIds(List<String> inAppInfoIds, List<String> inCategoryIds, List<String> excludCategoryIds,
			String documentType, Integer maxCount ) throws Exception {
		EntityManager em = this.entityManagerContainer().get( CategoryInfo.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<CategoryInfo> root = cq.from(CategoryInfo.class);
		cq.select(root.get(CategoryInfo_.id));
		
		Predicate p_all = cb.isTrue( root.get( CategoryInfo_.allPeopleView ) );
		p_all = cb.or( p_all,  cb.isTrue( root.get( CategoryInfo_.allPeoplePublish )));
		
		Predicate p = root.get( CategoryInfo_.id ).isNotNull();
		if( ListTools.isNotEmpty( inAppInfoIds )) {
			p = cb.and( p,  root.get( CategoryInfo_.appId ).in( inAppInfoIds ) );
		}
		if( ListTools.isNotEmpty( inCategoryIds )) {
			p = cb.and( p, root.get( CategoryInfo_.id ).in( inCategoryIds ));
		}
		if( ListTools.isNotEmpty( excludCategoryIds )) {
			p = cb.and( p, cb.not( root.get( CategoryInfo_.id ).in( excludCategoryIds )));
		}
		p = cb.and( p, p_all );
		if( StringUtils.isNotEmpty( documentType) && !"全部".equals(documentType) && !"all".equalsIgnoreCase(documentType)) {
			p = cb.and( p, cb.equal( root.get( CategoryInfo_.documentType), documentType));
		}
		return em.createQuery(cq.where( p )).setMaxResults(maxCount).getResultList();
	}

	/**
	 * 查询指定用户，组织，群组可以管理的分类列表
	 * @param personName
	 * @param unitNames
	 * @param groupNames
	 * @param inAppInfoIds
	 * @return
	 * @throws Exception
	 */
	public List<String> listManageableCategoryIds( String personName, List<String> unitNames, List<String> groupNames, List<String> inAppInfoIds,
			String documentType, Integer maxCount ) throws Exception {
		EntityManager em = this.entityManagerContainer().get(CategoryInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<CategoryInfo> root = cq.from(CategoryInfo.class);
		
		Predicate p = cb.isMember( personName, root.get( CategoryInfo_.manageablePersonList ));
		if( ListTools.isNotEmpty( inAppInfoIds )) {
			p = cb.and( p, root.get( CategoryInfo_.appId ).in( inAppInfoIds ) );
		}
		if( ListTools.isNotEmpty( unitNames )) {
			p = cb.or( p,  root.get( CategoryInfo_.manageableUnitList).in(unitNames));
		}
		if( ListTools.isNotEmpty( groupNames )) {
			p = cb.or( p,  root.get( CategoryInfo_.manageableGroupList).in(groupNames));
		}
		cq.select(root.get( CategoryInfo_.id ));
		if( StringUtils.isNotEmpty( documentType) && !"全部".equals(documentType) && !"all".equalsIgnoreCase(documentType)) {
			p = cb.and( p, cb.equal( root.get( CategoryInfo_.documentType), documentType));
		}
		return em.createQuery( cq.where( p ) ).setMaxResults(maxCount).getResultList();
	}
}
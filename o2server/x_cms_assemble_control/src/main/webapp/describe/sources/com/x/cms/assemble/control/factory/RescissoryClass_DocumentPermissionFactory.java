package com.x.cms.assemble.control.factory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.cms.assemble.control.AbstractFactory;
import com.x.cms.assemble.control.Business;
import com.x.cms.core.entity.DocumentPermission;
import com.x.cms.core.entity.DocumentPermission_;
import com.x.cms.core.entity.tools.DateOperation;

/**
 * 文档权限基础功能服务类
 * 
 * @author O2LEE
 */
public class RescissoryClass_DocumentPermissionFactory extends AbstractFactory {

	public RescissoryClass_DocumentPermissionFactory(Business business) throws Exception {
		super(business);
	}
	
	//@MethodDescribe("列示指定Id的DocumentPermission信息列表")
	public List<DocumentPermission> list( List<String> ids ) throws Exception {
		EntityManager em = this.entityManagerContainer().get( DocumentPermission.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<DocumentPermission> cq = cb.createQuery( DocumentPermission.class );
		Root<DocumentPermission> root = cq.from( DocumentPermission.class );
		Predicate p = root.get( DocumentPermission_.id).in( ids );
		return em.createQuery(cq.where(p)).getResultList();
	}
	public List<String> listIds(String docId, List<String> permissionCodes, String permission) throws Exception {
		EntityManager em = this.entityManagerContainer().get( DocumentPermission.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery( String.class );
		Root<DocumentPermission> root = cq.from( DocumentPermission.class );
		Predicate p = cb.equal( root.get( DocumentPermission_.documentId ), docId );
		p = cb.and( p, cb.equal( root.get( DocumentPermission_.permission ), permission ) );
		p = cb.and( p, root.get( DocumentPermission_.permissionObjectCode ).in( permissionCodes ) );
		cq.select( root.get( DocumentPermission_.id ));
		return em.createQuery(cq.where(p)).getResultList();
	}
	//@MethodDescribe("根据指定的信息查询文档权限记录ID列表")
	public List<String> listIds( String docId, String permission, String permissionObjectType, String permissionObjectCode ) throws Exception {
		EntityManager em = this.entityManagerContainer().get( DocumentPermission.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery( String.class );
		Root<DocumentPermission> root = cq.from( DocumentPermission.class );
		Predicate p = cb.equal( root.get( DocumentPermission_.documentId ), docId );
		p = cb.and( p, cb.equal( root.get( DocumentPermission_.permission ), permission ) );
		p = cb.and( p, cb.equal( root.get( DocumentPermission_.permissionObjectType ), permissionObjectType ) );
		p = cb.and( p, cb.equal( root.get( DocumentPermission_.permissionObjectCode ), permissionObjectCode ) );
		cq.select( root.get( DocumentPermission_.id ));
		return em.createQuery(cq.where(p)).getResultList();
	}

	//@MethodDescribe("根据文档ID查询文档所有的权限信息ID列表")
	public List<String> listIdsByDocumentId( String docId ) throws Exception {
		EntityManager em = this.entityManagerContainer().get( DocumentPermission.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery( String.class );
		Root<DocumentPermission> root = cq.from( DocumentPermission.class );
		Predicate p = cb.equal( root.get( DocumentPermission_.documentId ), docId );
		cq.select( root.get( DocumentPermission_.id ));
		return em.createQuery(cq.where(p)).getResultList();
	}

	//@MethodDescribe("查询未被更新过的文档权限信息ID列表")
	public List<String> listNoModifyIds(String docId, String updateFlag) throws Exception {
		EntityManager em = this.entityManagerContainer().get( DocumentPermission.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery( String.class );
		Root<DocumentPermission> root = cq.from( DocumentPermission.class );
		Predicate p = cb.equal( root.get( DocumentPermission_.documentId ), docId );
		p = cb.and( p, cb.notEqual( root.get( DocumentPermission_.updateFlag ), updateFlag ) );
		cq.select( root.get( DocumentPermission_.id ));
		return em.createQuery(cq.where(p)).getResultList();
	}

	//@MethodDescribe("根据条件查询出用户可访问的文档ID列表，按发布时间倒排序，取前500条")
	public List<String> lisViewableDocIdsWithFilter1( List<String> appIdList,
			List<String> categoryIdList, List<String> publisherList, String title, List<String> createDateList,
			List<String> publishDateList, List<String> statusList, List<String> permissionObjectCodeList, 
			List<String> viewableCategoryIds,
			Integer maxResultCount ) throws Exception {
		Date startDate = null;
		Date endDate = null;
		List<String> ids = new ArrayList<>();
		List<DocumentPermission> permissions = null;
		DateOperation dateOperation = new DateOperation();
		EntityManager em = this.entityManagerContainer().get( DocumentPermission.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<DocumentPermission> cq = cb.createQuery( DocumentPermission.class );
		Root<DocumentPermission> root = cq.from( DocumentPermission.class );
	
		//先圈定用户能访问的文档
		Predicate permissionWhere = root.get( DocumentPermission_.permissionObjectCode ).in( permissionObjectCodeList );
		
		//所有人可访问的文档
		permissionWhere = cb.or( permissionWhere, cb.equal( root.get( DocumentPermission_.permissionObjectCode ), "所有人") );
		
		//或者用户可管理的分类中所有的文档
		permissionWhere = cb.or( permissionWhere, root.get( DocumentPermission_.categoryId ).in( viewableCategoryIds ) );
		
		Predicate p = cb.isNotNull( root.get( DocumentPermission_.id ) );
		if( appIdList != null && !appIdList.isEmpty() ){
			p = cb.and( p, root.get( DocumentPermission_.appId ).in( appIdList ));
		}
		if( categoryIdList != null && !categoryIdList.isEmpty() ){
			p = cb.and( p, root.get( DocumentPermission_.categoryId ).in( categoryIdList ));
		}
		if( publisherList != null && !publisherList.isEmpty() ){
			p = cb.and( p, root.get( DocumentPermission_.publisher ).in( publisherList ));
		}
		if( title != null && !title.isEmpty() ){
			p = cb.and( p, cb.like( root.get( DocumentPermission_.title ), "%" + title + "%" ));
		}
		if( statusList == null || statusList.isEmpty() ){
			p = cb.and( p, cb.equal(root.get( DocumentPermission_.documentStatus ), "published"));
		}else{
			p = cb.and( p, root.get( DocumentPermission_.documentStatus ).in( statusList ));
		}
		
		if( createDateList != null && !createDateList.isEmpty() ){
			if ( createDateList.size() == 1 ) {// 从开始时间（yyyy-MM-DD），到现在				
				startDate = dateOperation.getDateFromString( createDateList.get(0).toString() );
				endDate = new Date();
			}else if( createDateList.size() == 2 ){// 从开始时间到结束时间（yyyy-MM-DD）				
				startDate = dateOperation.getDateFromString( createDateList.get(0).toString());
				endDate = dateOperation.getDateFromString( createDateList.get(1).toString());
			}
			p = cb.and( p, cb.between( root.get( DocumentPermission_.docCreateDate ), startDate, endDate ) );
		}
		
		if( publishDateList != null && !publishDateList.isEmpty() ){
			if ( publishDateList.size() == 1 ) {
				// 从开始时间（yyyy-MM-DD），到现在
				startDate = dateOperation.getDateFromString( publishDateList.get(0).toString() );
				endDate = new Date();
			}else if( publishDateList.size() == 2 ){
				// 从开始时间到结束时间（yyyy-MM-DD）
				startDate = dateOperation.getDateFromString( publishDateList.get(0).toString());
				endDate = dateOperation.getDateFromString( publishDateList.get(1).toString());
			}
			p = cb.and( p, cb.between( root.get( DocumentPermission_.publishDate ), startDate, endDate ) );
		}
		
		cq.orderBy( cb.desc( root.get( DocumentPermission_.publishDate ) ) );
		
		if( maxResultCount == null || maxResultCount == 0 ){
			maxResultCount = 500;
		}
		
		permissions = em.createQuery(cq.where( permissionWhere, p )).setMaxResults( maxResultCount ).getResultList();
		if( permissions != null && !permissions.isEmpty() ){
			for( DocumentPermission permission : permissions ){
				if( !ids.contains( permission.getDocumentId() )){
					ids.add( permission.getDocumentId() );
				}
			}
		}
		return ids;
	}

	public List<String> getWithNullScartchString( int maxCount ) throws Exception {
		EntityManager em = this.entityManagerContainer().get( DocumentPermission.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery( String.class );
		Root<DocumentPermission> root = cq.from( DocumentPermission.class );
		Predicate p = cb.isNull( root.get( DocumentPermission_.scratchString ) );
		cq.select( root.get( DocumentPermission_.id ));
		return em.createQuery( cq.where(p) ).setMaxResults( maxCount ).getResultList();
	}
}
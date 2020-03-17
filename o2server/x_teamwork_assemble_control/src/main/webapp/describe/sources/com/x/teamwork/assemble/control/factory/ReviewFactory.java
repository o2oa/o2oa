package com.x.teamwork.assemble.control.factory;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.tools.ListTools;
import com.x.teamwork.assemble.control.AbstractFactory;
import com.x.teamwork.assemble.control.Business;
import com.x.teamwork.core.entity.Review;
import com.x.teamwork.core.entity.Review_;
import com.x.teamwork.core.entity.tools.CriteriaBuilderTools;
import com.x.teamwork.core.entity.tools.filter.QueryFilter;
/**
 * 工作任务权限控制信息服务类
 */
public class ReviewFactory extends AbstractFactory {
	
	public ReviewFactory( Business business) throws Exception {
		super(business);
	}
	
	public List<String> listReviewByTask( String taskId,  Integer maxCount ) throws Exception {
		if( maxCount == null ) {
			maxCount = 1000;
		}
		EntityManager em = this.entityManagerContainer().get( Review.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery( String.class );
		Root<Review> root = cq.from( Review.class );
		Predicate p = cb.equal(root.get( Review_.taskId ), taskId );
		cq.select( root.get( Review_.id) ).where(p);
		return em.createQuery( cq ).setMaxResults(maxCount).getResultList();
	}
	
	public List<String> listTaskIdsWithPersonAndProject(String person, String project ) throws Exception {
		if( StringUtils.isEmpty( person ) ) {
			throw new Exception("person can not be empty!");
		}
		EntityManager em = this.entityManagerContainer().get( Review.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery( String.class );
		Root<Review> root = cq.from( Review.class );
		Predicate p = cb.equal( root.get( Review_.permissionObj ), person );
		p = cb.and( p, cb.equal( root.get( Review_.project ), project ));
		cq.select(root.get( Review_.taskId)).where(p);
		return em.createQuery( cq ).getResultList();
	}
	
	public List<String> listTaskIdsWithPersonAndProject( String project, String person,  Integer maxCount ) throws Exception {
		if( maxCount == null ) {
			maxCount = 1000;
		}
		EntityManager em = this.entityManagerContainer().get( Review.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery( String.class );
		Root<Review> root = cq.from( Review.class );
		Predicate p = cb.equal(root.get( Review_.project ), project );
		p = cb.and( p, cb.equal(root.get( Review_.permissionObj ), person ));
		cq.select( root.get( Review_.taskId) ).where(p);
		return em.createQuery( cq ).setMaxResults(maxCount).getResultList();
	}
	
	public List<String> listPermissionByTask( String taskId, Integer maxCount ) throws Exception {
		EntityManager em = this.entityManagerContainer().get( Review.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery( String.class );
		Root<Review> root = cq.from( Review.class );
		Predicate p = cb.equal(root.get( Review_.taskId ), taskId );
		cq.select(root.get( Review_.permissionObj )).where(p);
		return em.createQuery( cq ).setMaxResults(maxCount).getResultList();
	}	
	
	public List<String> listIdsByTaskAndPerson(String taskId, String person) throws Exception {
		if( StringUtils.isEmpty( taskId ) ) {
			throw new Exception("task id can not be empty!");
		}
		if( StringUtils.isEmpty( person ) ) {
			throw new Exception("person can not be empty!");
		}
		EntityManager em = this.entityManagerContainer().get( Review.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery( String.class );
		Root<Review> root = cq.from( Review.class );
		Predicate p = cb.equal(root.get( Review_.taskId ), taskId );
		p = cb.and( p, cb.equal( root.get( Review_.permissionObj ), person));
		cq.select(root.get( Review_.id)).where(p);
		return em.createQuery( cq ).getResultList();
	}
	
	public List<Review> listByTaskAndPerson(String taskId, String person) throws Exception {
		if( StringUtils.isEmpty( taskId ) ) {
			throw new Exception("task id can not be empty!");
		}
		if( StringUtils.isEmpty( person ) ) {
			throw new Exception("person can not be empty!");
		}
		EntityManager em = this.entityManagerContainer().get( Review.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Review> cq = cb.createQuery( Review.class );
		Root<Review> root = cq.from( Review.class );
		Predicate p = cb.equal(root.get( Review_.taskId ), taskId );
		p = cb.and( p, cb.equal( root.get( Review_.permissionObj ), person));
		return em.createQuery( cq.where(p) ).getResultList();
	}

	public Long countByTask(String taskId) throws Exception {
		if( StringUtils.isEmpty( taskId ) ) {
			throw new Exception("task id can not be empty!");
		}
		EntityManager em = this.entityManagerContainer().get( Review.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Review> root = cq.from(Review.class);
		Predicate p = cb.equal( root.get(Review_.taskId), taskId );
		cq.select(cb.count(root)).where(p);
		return em.createQuery(cq).getSingleResult();
	}
	
	public Long countWithFilter(String personName, QueryFilter queryFilter) throws Exception {
		if( StringUtils.isEmpty( personName ) ) {
			return 0L;
		}
		EntityManager em = this.entityManagerContainer().get( Review.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Review> root = cq.from(Review.class);
		Predicate p_permission = cb.equal( root.get(Review_.permissionObj), personName );
		Predicate p = CriteriaBuilderTools.composePredicateWithQueryFilter( Review_.class, cb, p_permission, root, queryFilter );
		cq.select(cb.count(root)).where(p);
		return em.createQuery(cq).getSingleResult();
	}

	public List<Review> listWithFilter( Integer maxCount, String orderField, String orderType, String personName, QueryFilter queryFilter) throws Exception {
		EntityManager em = this.entityManagerContainer().get( Review.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Review> cq = cb.createQuery(Review.class);
		Root<Review> root = cq.from(Review.class);
		Predicate p_permission = null;		
		if( StringUtils.isNotEmpty( personName )) {
			p_permission = CriteriaBuilderTools.predicate_or( cb, p_permission, cb.equal( root.get( Review_.permissionObj ), personName ));
		}
		
		Predicate p = CriteriaBuilderTools.composePredicateWithQueryFilter( Review_.class, cb, p_permission, root, queryFilter );
		
		//排序，添加排序列，默认使用sequence
		List<Order> orders = new ArrayList<>();
		Order orderWithField = CriteriaBuilderTools.getOrder( cb, root, Review_.class, orderField, orderType );
		if( orderWithField != null ){
			orders.add( orderWithField );
		}
		
		if( !Review.taskSequence_FIELDNAME.equalsIgnoreCase( orderField )) {
			//如果是其他的列，很可能排序值不唯一，所以使用多一列排序列来确定每次查询的顺序
			orderWithField = CriteriaBuilderTools.getOrder( cb, root, Review_.class, Review.id_FIELDNAME, orderType );
			if( orderWithField != null ){
				orders.add( orderWithField );
			}
		}		
		if( ListTools.isNotEmpty(  orders )){
			cq.orderBy( orders );
		}
		return em.createQuery(cq.where(p)).setMaxResults( maxCount).getResultList();
	}

	public List<String> checkTaskIdsWithPermission(List<String> taskIds, String personName) throws Exception {
		if( ListTools.isEmpty( taskIds ) ) {
			throw new Exception("taskIds can not be empty!");
		}
		if( StringUtils.isEmpty( personName ) ) {
			throw new Exception("personName can not be empty!");
		}
		EntityManager em = this.entityManagerContainer().get( Review.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery( String.class );
		Root<Review> root = cq.from( Review.class );
		Predicate p = cb.equal( root.get( Review_.permissionObj ), personName );
		p = cb.and( p, root.get( Review_.taskId ).in( taskIds ));
		cq.select(root.get( Review_.taskId)).where(p);
		return em.createQuery( cq ).getResultList();
	}

	public List<Review> listTaskWithPersonAndParentId(String person, String taskId) throws Exception {
		if( StringUtils.isEmpty( person ) ) {
			throw new Exception("person can not be empty!");
		}
		if( StringUtils.isEmpty( taskId ) ) {
			throw new Exception("taskId can not be empty!");
		}
		EntityManager em = this.entityManagerContainer().get( Review.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Review> cq = cb.createQuery( Review.class );
		Root<Review> root = cq.from( Review.class );
		Predicate p = cb.equal( root.get( Review_.permissionObj ), person );
		p = cb.and( p, cb.equal( root.get( Review_.parent ), taskId ));
		System.out.println(">>>SQL:" + em.createQuery( cq.where(p) ) );
		return em.createQuery( cq.where(p) ).getResultList();
	}
}
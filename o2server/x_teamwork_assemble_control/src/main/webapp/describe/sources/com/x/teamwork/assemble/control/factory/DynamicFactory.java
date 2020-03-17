package com.x.teamwork.assemble.control.factory;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import com.x.base.core.project.exception.ExceptionWhen;
import com.x.teamwork.assemble.control.AbstractFactory;
import com.x.teamwork.assemble.control.Business;
import com.x.teamwork.core.entity.Dynamic;
import com.x.teamwork.core.entity.DynamicDetail;
import com.x.teamwork.core.entity.Dynamic_;
import com.x.teamwork.core.entity.tools.CriteriaBuilderTools;
import com.x.teamwork.core.entity.tools.filter.QueryFilter;


public class DynamicFactory extends AbstractFactory {

	public DynamicFactory( Business business ) throws Exception {
		super(business);
	}

	/**
	 * 获取指定Id的Dynamic实体信息对象
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public Dynamic get( String id ) throws Exception {
		return this.entityManagerContainer().find( id, Dynamic.class, ExceptionWhen.none );
	}

	public DynamicDetail getDetail(String flag) throws Exception {
		return this.entityManagerContainer().find( flag, DynamicDetail.class, ExceptionWhen.none );
	}
	
	/**
	 * 根据过滤条件查询符合要求的项目信息数量
	 * @param queryFilter
	 * @return
	 * @throws Exception
	 */
	public Long countWithFilter( QueryFilter queryFilter) throws Exception {
		EntityManager em = this.entityManagerContainer().get( Dynamic.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Dynamic> root = cq.from(Dynamic.class);
		Predicate p = CriteriaBuilderTools.composePredicateWithQueryFilter( Dynamic_.class, cb, null, root, queryFilter );
		cq.select(cb.count(root));
		return em.createQuery(cq.where(p)).getSingleResult();
	}
	
	/**
	 * 根据条件查询符合条件的项目信息ID(查询前N条内存分页支持)
	 * @param maxCount
	 * @param orderField
	 * @param orderType
	 * @param queryFilter
	 * @return
	 * @throws Exception
	 */
	public List<Dynamic> listWithFilter( Integer maxCount, String orderField, String orderType, QueryFilter queryFilter ) throws Exception {
		EntityManager em = this.entityManagerContainer().get( Dynamic.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Dynamic> cq = cb.createQuery(Dynamic.class);
		Root<Dynamic> root = cq.from(Dynamic.class);
		Predicate p = CriteriaBuilderTools.composePredicateWithQueryFilter( Dynamic_.class, cb, null, root, queryFilter );		
		
		Order orderWithField = CriteriaBuilderTools.getOrder(cb, root, Dynamic_.class, orderField, orderType);
		if( orderWithField != null ){
			cq.orderBy( orderWithField );
		}
		return em.createQuery(cq.where(p)).setMaxResults( maxCount).getResultList();
	}
	
	/**
	 * 根据条件查询符合条件的项目信息ID(根据sequnce分页支持)
	 * @param maxCount
	 * @param sequenceFieldValue
	 * @param orderField
	 * @param orderType
	 * @param queryFilter
	 * @return
	 * @throws Exception
	 */
	public List<Dynamic> listWithFilter( Integer maxCount, Object sequenceFieldValue, String orderField, String orderType, QueryFilter queryFilter ) throws Exception {
		EntityManager em = this.entityManagerContainer().get( Dynamic.class );
		return CriteriaBuilderTools.listNextWithCondition( em, Dynamic.class, Dynamic_.class, maxCount, queryFilter, sequenceFieldValue, orderField, orderType );
	}
}

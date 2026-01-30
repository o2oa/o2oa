package com.x.teamwork.assemble.control.factory;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.*;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.exception.ExceptionWhen;
import com.x.base.core.project.tools.ListTools;
import com.x.teamwork.assemble.control.AbstractFactory;
import com.x.teamwork.assemble.control.Business;
import com.x.teamwork.core.entity.*;
import com.x.teamwork.core.entity.tools.CriteriaBuilderTools;
import com.x.teamwork.core.entity.tools.filter.QueryFilter;
import org.apache.commons.lang3.StringUtils;


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

	/**
	 * 根据条件分页查询符合条件的文档信息列表
	 * @param personName
	 * @param orderField
	 * @param orderType
	 * @param queryFilter
	 * @param adjustPage
	 * @param adjustPageSize
	 * @return
	 * @throws Exception
	 */
	public List<Dynamic> listPagingWithCondition(String personName, String orderField, String orderType, QueryFilter queryFilter, Integer adjustPage,
												 Integer adjustPageSize) throws Exception {
		EntityManager em = this.entityManagerContainer().get( Dynamic.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		EntityManager em1 = this.entityManagerContainer().get( ProjectPermission.class );
		CriteriaBuilder cb1 = em1.getCriteriaBuilder();
		CriteriaQuery<Dynamic> cq = cb.createQuery(Dynamic.class);
		Root<Dynamic> root = cq.from(Dynamic.class);
		Predicate p = CriteriaBuilderTools.composePredicateWithQueryFilter( Dynamic_.class, cb, null, root, queryFilter );

		if(StringUtils.isNotBlank(personName)){
			Subquery<ProjectPermission> subQuery = cq.subquery(ProjectPermission.class);
			Root<ProjectPermission> root2 = subQuery.from(em1.getMetamodel().entity(ProjectPermission.class));
			subQuery.select(root2);
			Predicate p_permission = cb1.equal(root2.get(ProjectPermission_.name), personName);
			p_permission = cb1.and(p_permission, cb1.equal(root2.get(ProjectPermission_.projectId), root.get(Dynamic_.projectId)));
			subQuery.where(p_permission);
			p = cb.and(p, cb.exists(subQuery));
		}
		List<String> fields = DynamicWo.copier.getCopyFields();
		cq.select(root).where(p);

		List<Order> orders = new ArrayList<>();

		if(StringUtils.isNotBlank(orderField) && fields.contains(orderField)) {
			String defaultOrderBy = "asc";
			if( defaultOrderBy.equalsIgnoreCase( orderType )) {
				orders.add(cb.asc( root.get( orderField )));
			}else {
				orders.add(cb.desc( root.get( orderField )));
			}
		}
		if(orders.isEmpty() || (!JpaObject.sequence_FIELDNAME.equals(orderField) && !JpaObject.createTime_FIELDNAME.equals(orderField))) {
			orders.add(cb.desc(root.get(JpaObject.sequence_FIELDNAME)));
		}
		cq.orderBy( orders );

		return em.createQuery(cq).setFirstResult((adjustPage - 1) * adjustPageSize).setMaxResults(adjustPageSize)
				.getResultList();
	}

	/**
	 * 根据条件统计文档数目
	 * @param personName
	 * @param queryFilter
	 * @return
	 * @throws Exception
	 */
	public Long countWithCondition( String personName, QueryFilter queryFilter) throws Exception {
		EntityManager em = this.entityManagerContainer().get( Dynamic.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		EntityManager em1 = this.entityManagerContainer().get( ProjectPermission.class );
		CriteriaBuilder cb1 = em1.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Dynamic> root = cq.from(Dynamic.class);
		Predicate p = CriteriaBuilderTools.composePredicateWithQueryFilter( Dynamic_.class, cb, null, root, queryFilter );

		if(StringUtils.isNotBlank(personName)){
			Subquery<ProjectPermission> subQuery = cq.subquery(ProjectPermission.class);
			Root<ProjectPermission> root2 = subQuery.from(em1.getMetamodel().entity(ProjectPermission.class));
			subQuery.select(root2);
			Predicate p_permission = cb1.equal(root2.get(ProjectPermission_.name), personName);
			p_permission = cb1.and(p_permission, cb1.equal(root2.get(ProjectPermission_.projectId), root.get(Dynamic_.projectId)));
			subQuery.where(p_permission);
			p = cb.and(p, cb.exists(subQuery));
		}

		return em.createQuery(cq.select(cb.count(root)).where(p)).getSingleResult();
	}

	public static class DynamicWo extends Dynamic{

		static WrapCopier<Dynamic, DynamicWo> copier = WrapCopierFactory.wo(Dynamic.class, DynamicWo.class,
				JpaObject.singularAttributeField(Dynamic.class, true, true), null);
	}

}

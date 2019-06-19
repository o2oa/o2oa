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
import com.x.teamwork.core.entity.Chat;
import com.x.teamwork.core.entity.ChatContent;
import com.x.teamwork.core.entity.Chat_;
import com.x.teamwork.core.entity.tools.CriteriaBuilderTools;
import com.x.teamwork.core.entity.tools.filter.QueryFilter;


public class ChatFactory extends AbstractFactory {

	public ChatFactory( Business business ) throws Exception {
		super(business);
	}

	/**
	 * 获取指定Id的Chat实体信息对象
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public Chat get( String id ) throws Exception {
		return this.entityManagerContainer().find( id, Chat.class, ExceptionWhen.none );
	}

	/**
	 * 根据ChatId获取LOB内容
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public ChatContent getContent(String id) throws Exception {
		return this.entityManagerContainer().find( id, ChatContent.class, ExceptionWhen.none );
	}
	
	/**
	 * 根据ChatId获取LOB内容
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public String getContentString(String id) throws Exception {
		ChatContent  content = this.entityManagerContainer().find( id, ChatContent.class, ExceptionWhen.none );
		if( content != null ) {
			return content.getContent();
		}
		return null;
	}
	
	/**
	 * 根据条件查询符合条件的Chat数量
	 * @param personName
	 * @param unitNames
	 * @param groupNames
	 * @param queryFilter
	 * @return
	 * @throws Exception
	 */
	public Long countWithFilter( QueryFilter queryFilter) throws Exception {
		EntityManager em = this.entityManagerContainer().get( Chat.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Chat> root = cq.from(Chat.class);
		Predicate p = CriteriaBuilderTools.composePredicateWithQueryFilter( Chat_.class, cb, null, root, queryFilter );
		cq.select(cb.count(root)).where(p);
		return em.createQuery(cq).getSingleResult();
	}
	
	/**
	 * 根据条件查询符合条件的项目信息ID(查询前N条内存分页支持)
	 * @param personName
	 * @param unitNames
	 * @param groupNames
	 * @param queryFilter
	 * @return
	 * @throws Exception
	 */
	public List<Chat> listWithFilter( Integer maxCount, String orderField, String orderType, QueryFilter queryFilter ) throws Exception {
		EntityManager em = this.entityManagerContainer().get( Chat.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Chat> cq = cb.createQuery(Chat.class);
		Root<Chat> root = cq.from(Chat.class);
		Predicate p = CriteriaBuilderTools.composePredicateWithQueryFilter( Chat_.class, cb, null, root, queryFilter );		
		
		Order orderWithField = CriteriaBuilderTools.getOrder(cb, root, Chat_.class, orderField, orderType);
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
	public List<Chat> listWithFilter( Integer maxCount, Object sequenceFieldValue, String orderField, String orderType, QueryFilter queryFilter ) throws Exception {
		EntityManager em = this.entityManagerContainer().get( Chat.class );
		return CriteriaBuilderTools.listNextWithCondition( em, Chat.class, Chat_.class, maxCount, queryFilter, sequenceFieldValue, orderField, orderType );
	}
}

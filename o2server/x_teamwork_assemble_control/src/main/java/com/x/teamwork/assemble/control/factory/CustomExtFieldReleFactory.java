package com.x.teamwork.assemble.control.factory;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.exception.ExceptionWhen;
import com.x.teamwork.assemble.control.AbstractFactory;
import com.x.teamwork.assemble.control.Business;
import com.x.teamwork.core.entity.CustomExtFieldRele;
import com.x.teamwork.core.entity.CustomExtFieldRele_;


public class CustomExtFieldReleFactory extends AbstractFactory {

	public CustomExtFieldReleFactory( Business business ) throws Exception {
		super(business);
	}

	/**
	 * 获取指定Id的CustomExtFieldRele实体信息对象
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public CustomExtFieldRele get( String id ) throws Exception {
		return this.entityManagerContainer().find( id, CustomExtFieldRele.class, ExceptionWhen.none );
	}
	
	/**
	 * 列示指定Id的CustomExtFieldRele实体信息列表
	 * @param ids
	 * @return
	 * @throws Exception
	 */
	public List<CustomExtFieldRele> list( List<String> ids ) throws Exception {
		if( ids == null || ids.size() == 0 ){
			return new ArrayList<CustomExtFieldRele>();
		}
		EntityManager em = this.entityManagerContainer().get(CustomExtFieldRele.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<CustomExtFieldRele> cq = cb.createQuery(CustomExtFieldRele.class);
		Root<CustomExtFieldRele> root = cq.from(CustomExtFieldRele.class);
		Predicate p = root.get(CustomExtFieldRele_.id).in(ids);
		cq.orderBy( cb.desc( root.get( CustomExtFieldRele_.updateTime ) ) );
		return em.createQuery(cq.where(p)).getResultList();
	}
	
	/**
	 * 列示所有扩展属性信息列表
	 * @return
	 * @throws Exception
	 */
	public List<CustomExtFieldRele> listAllFieldReleObj() throws Exception {
		
		EntityManager em = this.entityManagerContainer().get(CustomExtFieldRele.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<CustomExtFieldRele> cq = cb.createQuery(CustomExtFieldRele.class);
		Root<CustomExtFieldRele> root = cq.from(CustomExtFieldRele.class);
		cq.orderBy( cb.asc( root.get( CustomExtFieldRele_.order ) ) );
		return em.createQuery(cq).getResultList();
	}

	/**
	 * 根据关联ID列示扩展属性信息列表
	 * @param correlationId
	 * @return
	 * @throws Exception
	 */
	public List<CustomExtFieldRele> listFieldReleObjByCorrelation( String correlationId ) throws Exception {
		if( StringUtils.isEmpty( correlationId ) ){
			throw new Exception("correlationId can not be empty!");
		}
		EntityManager em = this.entityManagerContainer().get(CustomExtFieldRele.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<CustomExtFieldRele> cq = cb.createQuery(CustomExtFieldRele.class);
		Root<CustomExtFieldRele> root = cq.from(CustomExtFieldRele.class);
		Predicate p = cb.equal( root.get(CustomExtFieldRele_.correlationId), correlationId );
		return em.createQuery(cq.where(p)).getResultList();
	}
	
	/**
	 * 根据扩展属性类型列示扩展属性信息列表
	 * @param type
	 * @return
	 * @throws Exception
	 */
	public List<CustomExtFieldRele> listFieldReleObjByType( String type ) throws Exception {
		if( StringUtils.isEmpty( type ) ){
			throw new Exception("type can not be empty!");
		}
		EntityManager em = this.entityManagerContainer().get(CustomExtFieldRele.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<CustomExtFieldRele> cq = cb.createQuery(CustomExtFieldRele.class);
		Root<CustomExtFieldRele> root = cq.from(CustomExtFieldRele.class);
		Predicate p = cb.equal( root.get(CustomExtFieldRele_.type), type );
		return em.createQuery(cq.where(p)).getResultList();
	}
	
	/**
	 * 根据关联ID列示扩展属性ID信息列表
	 * @param correlationId
	 * @return
	 * @throws Exception
	 */
	public List<String> listFieldReleIdsByCorrelation( String correlationId ) throws Exception {
		if( StringUtils.isEmpty( correlationId ) ){
			throw new Exception("correlationId can not be empty!");
		}
		EntityManager em = this.entityManagerContainer().get(CustomExtFieldRele.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<CustomExtFieldRele> root = cq.from(CustomExtFieldRele.class);
		Predicate p = cb.equal( root.get(CustomExtFieldRele_.correlationId), correlationId );
		cq.select( root.get(CustomExtFieldRele_.correlationId) );
		return em.createQuery(cq.where(p)).getResultList();
	}
	
	/**
	 * 根据扩展属性名以及关联ID获取一组关联信息
	 * @param fieldName
	 * @param correlationId
	 * @return
	 * @throws Exception
	 */
	public List<CustomExtFieldRele> listWithFieldNameAndCorrelation( String fieldName, String correlationId ) throws Exception {
		if( StringUtils.isEmpty( fieldName ) ){
			throw new Exception("fieldName can not be empty!");
		}
		/*if( StringUtils.isEmpty( correlationId ) ){
			throw new Exception("correlationId can not be empty!");
		}*/
		EntityManager em = this.entityManagerContainer().get(CustomExtFieldRele.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<CustomExtFieldRele> cq = cb.createQuery(CustomExtFieldRele.class);
		Root<CustomExtFieldRele> root = cq.from(CustomExtFieldRele.class);
		Predicate p = cb.equal( root.get(CustomExtFieldRele_.extFieldName), fieldName );
		if( StringUtils.isNotEmpty( correlationId ) ){
			p = cb.and( p,  cb.equal( root.get(CustomExtFieldRele_.correlationId), correlationId ));
		}
		return em.createQuery(cq.where(p)).getResultList();
	}
}

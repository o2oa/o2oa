package com.x.okr.assemble.control.factory;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.exception.ExceptionWhen;
import com.x.base.core.utils.annotation.MethodDescribe;
import com.x.okr.assemble.control.AbstractFactory;
import com.x.okr.assemble.control.Business;
import com.x.okr.assemble.control.jaxrs.okrworkdynamics.WrapInFilter;
import com.x.okr.entity.OkrWorkDynamics;
import com.x.okr.entity.OkrWorkDynamics_;

/**
 * 类   名：OkrWorkDynamicsFactory<br/>
 * 实体类：OkrWorkDynamics<br/>
 * 作   者：Liyi<br/>
 * 单   位：O2 Team<br/>
 * 日   期：2016-05-20 17:17:27
**/
public class OkrWorkDynamicsFactory extends AbstractFactory {

	public OkrWorkDynamicsFactory(Business business) throws Exception {
		super(business);
	}
	
	@MethodDescribe( "获取指定Id的OkrWorkDynamics实体信息对象" )
	public OkrWorkDynamics get( String id ) throws Exception {
		return this.entityManagerContainer().find(id, OkrWorkDynamics.class, ExceptionWhen.none);
	}
	
	@MethodDescribe( "列示全部的OkrWorkDynamics实体信息列表" )
	public List<String> listAll() throws Exception {
		EntityManager em = this.entityManagerContainer().get(OkrWorkDynamics.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<OkrWorkDynamics> root = cq.from( OkrWorkDynamics.class);
		cq.select(root.get(OkrWorkDynamics_.id));
		return em.createQuery(cq).getResultList();
	}
	
	@MethodDescribe( "列示指定Id的OkrWorkDynamics实体信息列表" )
	public List<OkrWorkDynamics> list(List<String> ids) throws Exception {
		if( ids == null || ids.size() == 0 ){
			return new ArrayList<OkrWorkDynamics>();
		}
		EntityManager em = this.entityManagerContainer().get(OkrWorkDynamics.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<OkrWorkDynamics> cq = cb.createQuery(OkrWorkDynamics.class);
		Root<OkrWorkDynamics> root = cq.from(OkrWorkDynamics.class);
		Predicate p = root.get(OkrWorkDynamics_.id).in(ids);
		return em.createQuery(cq.where(p)).getResultList();
	}

	/**
	 * 根据中心工作ID，列示所有的数据信息
	 * @param centerId 中心工作
	 * @return
	 * @throws Exception
	 */
	@MethodDescribe( "根据中心工作ID，列示所有的信息" )
	public List<String> listByCenterWorkId(String centerId) throws Exception {
		if( centerId == null || centerId.isEmpty() ){
			throw new Exception( " centerId is null!" );
		}
		EntityManager em = this.entityManagerContainer().get( OkrWorkDynamics.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery< String > cq = cb.createQuery( String.class );
		Root< OkrWorkDynamics > root = cq.from( OkrWorkDynamics.class );
		Predicate p = cb.equal( root.get( OkrWorkDynamics_.centerId ), centerId );
		cq.select( root.get( OkrWorkDynamics_.id ) );
		return em.createQuery( cq.where(p) ).getResultList();
	}

	/**
	 * 根据工作信息ID，列示所有的数据信息
	 * @param workId
	 * @return
	 * @throws Exception 
	 */
	@MethodDescribe( "根据工作信息ID，列示所有的数据信息" )
	public List<String> listByWorkId(String workId) throws Exception {
		EntityManager em = this.entityManagerContainer().get(OkrWorkDynamics.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<OkrWorkDynamics> root = cq.from( OkrWorkDynamics.class);
		Predicate p = cb.equal( root.get(OkrWorkDynamics_.workId), workId );
		cq.select(root.get( OkrWorkDynamics_.id) );
		return em.createQuery(cq.where(p)).getResultList();
	}

	/**
	 * 查询下一页的信息数据
	 * @param id
	 * @param count
	 * @param sequence
	 * @param wrapIn
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings( "unchecked" )
	public List< OkrWorkDynamics > listNextWithFilter( String id, Integer count, Object sequence, WrapInFilter wrapIn ) throws Exception {
		//先获取上一页最后一条的sequence值，如果有值的话，以此sequence值作为依据取后续的count条数据
		EntityManager em = this.entityManagerContainer().get( OkrWorkDynamics.class );
		String order = wrapIn.getOrder();//排序方式
		List<Object> vs = new ArrayList<>();
		StringBuffer sql_stringBuffer = new StringBuffer();
		
		if( order == null || order.isEmpty() ){
			order = "DESC";
		}
		
		Integer index = 1;
		sql_stringBuffer.append( "SELECT o FROM "+OkrWorkDynamics.class.getCanonicalName()+" o where 1=1" );

		if ((null != sequence) ) {
			sql_stringBuffer.append( " and o."+wrapIn.getSequenceField()+" " + (StringUtils.equalsIgnoreCase(order, "DESC" ) ? "<" : ">" ) + ( " ?" + (index)));
			vs.add(sequence);
			index++;
		}
		
		if( ( null != wrapIn.getCenterIds() && wrapIn.getCenterIds().size() > 0 ) ||
			( null != wrapIn.getWorkIds() && wrapIn.getWorkIds().size() > 0 )
		){
			sql_stringBuffer.append(" and ( ");			
			//中心工作IDS
			if (null != wrapIn.getCenterIds() && wrapIn.getCenterIds().size() > 0) {
				sql_stringBuffer.append(" o.centerId in ( ?" + (index) + " )");
				vs.add(wrapIn.getCenterIds());
				index++;
			}
			
			if( null != wrapIn.getCenterIds() && wrapIn.getCenterIds().size() > 0 &&
				null != wrapIn.getWorkIds() && wrapIn.getWorkIds().size() > 0
			){
				sql_stringBuffer.append(" or ");
			}
			
			//工作IDS
			if (null != wrapIn.getWorkIds() && wrapIn.getWorkIds().size() > 0) {
				sql_stringBuffer.append(" o.workId in ( ?" + (index) + " )");
				vs.add(wrapIn.getWorkIds());
				index++;
			}
			
			sql_stringBuffer.append(" ) ");
		}else{
			if( !wrapIn.isOkrSystemAdmin() ){
				return null;
			}
		}

		sql_stringBuffer.append( " order by o." + wrapIn.getSequenceField() + " " + ( StringUtils.equalsIgnoreCase( order, "DESC" ) ? "DESC" : "ASC" ) );
		
		Query query = em.createQuery( sql_stringBuffer.toString(), OkrWorkDynamics.class );

		for (int i = 0; i < vs.size(); i++) {
			query.setParameter(i + 1, vs.get(i));
			//logger.debug( ">>>>>>>>>>>PARAM("+i+"):" + vs.get(i).toString() );
		}
		//logger.debug( ">>>>>>>>>>>SQL:" + query.setMaxResults(count).toString() );
		return query.setMaxResults(count).getResultList();
	}	
	
	/**
	 * 查询上一页的信息数据
	 * @param id
	 * @param count
	 * @param sequence
	 * @param wrapIn
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings( "unchecked" )
	public List<OkrWorkDynamics> listPrevWithFilter( String id, Integer count, Object sequence, WrapInFilter wrapIn ) throws Exception {
		//先获取上一页最后一条的sequence值，如果有值的话，以此sequence值作为依据取后续的count条数据
		EntityManager em = this.entityManagerContainer().get( OkrWorkDynamics.class );
		String order = wrapIn.getOrder();//排序方式
		List<Object> vs = new ArrayList<>();
		StringBuffer sql_stringBuffer = new StringBuffer();
		Integer index = 1;
		
		if( order == null || order.isEmpty() ){
			order = "DESC";
		}
		
		sql_stringBuffer.append( "SELECT o FROM "+OkrWorkDynamics.class.getCanonicalName()+" o where 1=1" );

		if ((null != sequence) ) {
			sql_stringBuffer.append( " and o."+wrapIn.getSequenceField()+" " + (StringUtils.equalsIgnoreCase(order, "DESC" ) ? "<" : ">" ) + ( " ?" + (index)));
			vs.add(sequence);
			index++;
		}
		
		if( ( null != wrapIn.getCenterIds() && wrapIn.getCenterIds().size() > 0 ) ||
				( null != wrapIn.getWorkIds() && wrapIn.getWorkIds().size() > 0 )
			){
				sql_stringBuffer.append(" and ( ");			
				//中心工作IDS
				if (null != wrapIn.getCenterIds() && wrapIn.getCenterIds().size() > 0) {
					sql_stringBuffer.append(" o.centerId in ( ?" + (index) + " )");
					vs.add(wrapIn.getCenterIds());
					index++;
				}
				
				if( null != wrapIn.getCenterIds() && wrapIn.getCenterIds().size() > 0 &&
					null != wrapIn.getWorkIds() && wrapIn.getWorkIds().size() > 0
				){
					sql_stringBuffer.append(" or ");
				}
				
				//工作IDS
				if (null != wrapIn.getWorkIds() && wrapIn.getWorkIds().size() > 0) {
					sql_stringBuffer.append(" o.workId in ( ?" + (index) + " )");
					vs.add(wrapIn.getWorkIds());
					index++;
				}
				
				sql_stringBuffer.append(" ) ");
			}else{
				if( !wrapIn.isOkrSystemAdmin() ){
					return null;
				}
			}

		sql_stringBuffer.append( " order by o." + wrapIn.getSequenceField() + " " + ( StringUtils.equalsIgnoreCase( order, "DESC" ) ? "DESC" : "ASC" ) );
		
		Query query = em.createQuery( sql_stringBuffer.toString(), OkrWorkDynamics.class );
		//为查询设置所有的参数值
		for (int i = 0; i < vs.size(); i++) {
			query.setParameter(i + 1, vs.get(i));
		}
		
		return query.setMaxResults(count).getResultList();
	}
	
	/**
	 * 查询符合的信息总数
	 * @param id
	 * @param count
	 * @param sequence
	 * @param wrapIn
	 * @return
	 * @throws Exception
	 */
	public long getCountWithFilter( WrapInFilter wrapIn ) throws Exception {
		//先获取上一页最后一条的sequence值，如果有值的话，以此sequence值作为依据取后续的count条数据
		EntityManager em = this.entityManagerContainer().get( OkrWorkDynamics.class );
		List<Object> vs = new ArrayList<>();
		StringBuffer sql_stringBuffer = new StringBuffer();
		Integer index = 1;
		
		sql_stringBuffer.append( "SELECT count(o.id) FROM "+OkrWorkDynamics.class.getCanonicalName()+" o where 1=1" );
		
		if( ( null != wrapIn.getCenterIds() && wrapIn.getCenterIds().size() > 0 ) ||
				( null != wrapIn.getWorkIds() && wrapIn.getWorkIds().size() > 0 )
			){
				sql_stringBuffer.append(" and ( ");			
				//中心工作IDS
				if (null != wrapIn.getCenterIds() && wrapIn.getCenterIds().size() > 0) {
					sql_stringBuffer.append(" o.centerId in ( ?" + (index) + " )");
					vs.add(wrapIn.getCenterIds());
					index++;
				}
				
				if( null != wrapIn.getCenterIds() && wrapIn.getCenterIds().size() > 0 &&
					null != wrapIn.getWorkIds() && wrapIn.getWorkIds().size() > 0
				){
					sql_stringBuffer.append(" or ");
				}
				
				//工作IDS
				if (null != wrapIn.getWorkIds() && wrapIn.getWorkIds().size() > 0) {
					sql_stringBuffer.append(" o.workId in ( ?" + (index) + " )");
					vs.add(wrapIn.getWorkIds());
					index++;
				}
				
				sql_stringBuffer.append(" ) ");
			}else{
				if( !wrapIn.isOkrSystemAdmin() ){
					return 0;
				}
			}
		
		Query query = em.createQuery( sql_stringBuffer.toString(), OkrWorkDynamics.class );

		for (int i = 0; i < vs.size(); i++) {
			query.setParameter(i + 1, vs.get(i));
		}		
		return (Long) query.getSingleResult();
	}
}

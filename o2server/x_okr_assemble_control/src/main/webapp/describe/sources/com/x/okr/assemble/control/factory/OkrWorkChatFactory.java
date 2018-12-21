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

import com.x.base.core.project.exception.ExceptionWhen;
import com.x.okr.assemble.control.AbstractFactory;
import com.x.okr.assemble.control.Business;
import com.x.okr.entity.OkrWorkChat;
import com.x.okr.entity.OkrWorkChat_;

/**
 * 类   名：OkrWorkChatFactory<br/>
 * 实体类：OkrWorkChat<br/>
 * 作   者：Liyi<br/>
 * 单   位：O2 Team<br/>
 * 日   期：2016-05-20 17:17:27
**/
public class OkrWorkChatFactory extends AbstractFactory {

	public OkrWorkChatFactory( Business business ) throws Exception {
		super(business);
	}
	
	//@MethodDescribe( "获取指定Id的OkrWorkChat实体信息对象" )
	public OkrWorkChat get( String id ) throws Exception {
		return this.entityManagerContainer().find(id, OkrWorkChat.class, ExceptionWhen.none);
	}
	
	//@MethodDescribe( "列示全部的OkrWorkChat实体信息列表" )
	public List<String> listAll() throws Exception {
		EntityManager em = this.entityManagerContainer().get(OkrWorkChat.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<OkrWorkChat> root = cq.from( OkrWorkChat.class);
		cq.select(root.get(OkrWorkChat_.id));
		return em.createQuery(cq).getResultList();
	}
	
	//@MethodDescribe( "列示指定Id的OkrWorkChat实体信息列表" )
	public List<OkrWorkChat> list(List<String> ids) throws Exception {
		if( ids == null || ids.size() == 0 ){
			return new ArrayList<OkrWorkChat>();
		}
		EntityManager em = this.entityManagerContainer().get(OkrWorkChat.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<OkrWorkChat> cq = cb.createQuery(OkrWorkChat.class);
		Root<OkrWorkChat> root = cq.from(OkrWorkChat.class);
		Predicate p = root.get(OkrWorkChat_.id).in(ids);
		return em.createQuery(cq.where(p)).getResultList();
	}

	/**
	 * 根据中心工作ID，列示所有的数据信息
	 * @param centerId 中心工作
	 * @return
	 * @throws Exception
	 */
	//@MethodDescribe( "根据中心工作ID，列示所有的信息" )
	public List<String> listByCenterWorkId(String centerId) throws Exception {
		if( centerId == null || centerId.isEmpty() ){
			throw new Exception( " centerId is null!" );
		}
		EntityManager em = this.entityManagerContainer().get( OkrWorkChat.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery< String > cq = cb.createQuery( String.class );
		Root< OkrWorkChat > root = cq.from( OkrWorkChat.class );
		Predicate p = cb.equal( root.get( OkrWorkChat_.centerId ), centerId );
		cq.select( root.get( OkrWorkChat_.id ) );
		return em.createQuery( cq.where(p) ).getResultList();
	}

	/**
	 * 根据工作信息ID，列示所有的数据信息
	 * @param workId
	 * @return
	 * @throws Exception 
	 */
	//@MethodDescribe( "根据工作信息ID，列示所有的数据信息" )
	public List<String> listByWorkId(String workId) throws Exception {
		EntityManager em = this.entityManagerContainer().get( OkrWorkChat.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<OkrWorkChat> root = cq.from( OkrWorkChat.class);
		Predicate p = cb.equal( root.get( OkrWorkChat_.workId ), workId );
		cq.select(root.get( OkrWorkChat_.id) );
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
	public List<OkrWorkChat> listNextWithFilter( String id, Integer count, Object sequence, String workId, String sequenceField, String order ) throws Exception {
		//先获取上一页最后一条的sequence值，如果有值的话，以此sequence值作为依据取后续的count条数据
		EntityManager em = this.entityManagerContainer().get( OkrWorkChat.class );
		List<Object> vs = new ArrayList<>();
		StringBuffer sql_stringBuffer = new StringBuffer();
		
		if( order == null || order.isEmpty() ){
			order = "DESC";
		}
		
		Integer index = 1;
		sql_stringBuffer.append( "SELECT o FROM "+OkrWorkChat.class.getCanonicalName()+" o where 1=1" );

		if ((null != sequence) ) {
			sql_stringBuffer.append( " and o."+ sequenceField +" " + (StringUtils.equalsIgnoreCase( order, "DESC" ) ? "<" : ">" ) + ( " ?" + (index)));
			vs.add(sequence);
			index++;
		}
		if ( null != workId && !workId.isEmpty() ) {
			sql_stringBuffer.append( " and o.workId =  ?" + (index) );
			vs.add( workId );
			index++;
		}
		
		sql_stringBuffer.append( " order by o." + sequenceField + " " + ( StringUtils.equalsIgnoreCase( order, "DESC" ) ? "DESC" : "ASC" ) );
		
		Query query = em.createQuery( sql_stringBuffer.toString(), OkrWorkChat.class );

		for (int i = 0; i < vs.size(); i++) {
			query.setParameter(i + 1, vs.get(i));
		}
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
	public List<OkrWorkChat> listPrevWithFilter( String id, Integer count, Object sequence, String workId, String sequenceField, String order ) throws Exception {
		//先获取上一页最后一条的sequence值，如果有值的话，以此sequence值作为依据取后续的count条数据
		EntityManager em = this.entityManagerContainer().get( OkrWorkChat.class );
		List<Object> vs = new ArrayList<>();
		StringBuffer sql_stringBuffer = new StringBuffer();
		Integer index = 1;
		
		if( order == null || order.isEmpty() ){
			order = "DESC";
		}
		
		sql_stringBuffer.append( "SELECT o FROM "+ OkrWorkChat.class.getCanonicalName() +" o where 1=1" );

		if ((null != sequence) ) {
			sql_stringBuffer.append( " and o."+ sequenceField +" " + (StringUtils.equalsIgnoreCase(order, "DESC" ) ? ">" : "<" ) + ( " ?" + (index)));
			vs.add(sequence);
			index++;
		}
		if ( null != workId && !workId.isEmpty() ) {
			sql_stringBuffer.append( " and o.workId =  ?" + (index) );
			vs.add( workId );
			index++;
		}
		
		sql_stringBuffer.append( " order by o." + sequenceField + " " + ( StringUtils.equalsIgnoreCase( order, "DESC" ) ? "DESC" : "ASC" ) );
		Query query = em.createQuery( sql_stringBuffer.toString(), OkrWorkChat.class );
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
	public long getCountWithFilter( String workId ) throws Exception {
		//先获取上一页最后一条的sequence值，如果有值的话，以此sequence值作为依据取后续的count条数据
		EntityManager em = this.entityManagerContainer().get( OkrWorkChat.class );
		List<Object> vs = new ArrayList<>();
		StringBuffer sql_stringBuffer = new StringBuffer();
		Integer index = 1;
		
		sql_stringBuffer.append( "SELECT count(o.id) FROM "+OkrWorkChat.class.getCanonicalName()+" o where 1=1" );

		if ( null != workId && !workId.isEmpty() ) {
			sql_stringBuffer.append( " and o.workId =  ?" + (index) );
			vs.add( workId );
			index++;
		}
		
		Query query = em.createQuery( sql_stringBuffer.toString(), OkrWorkChat.class );

		for (int i = 0; i < vs.size(); i++) {
			query.setParameter(i + 1, vs.get(i));
		}		
		return (Long) query.getSingleResult();
	}
	/**
	 * 查询工作交流信息发送者身份列表（去重复）
	 * @param identities_ok 排除身份
	 * @param identities_error 排除身份
	 * @return
	 * @throws Exception 
	 */
	public List<String> listAllDistinctSenderIdentity(List<String> identities_ok, List<String> identities_error) throws Exception {
		EntityManager em = this.entityManagerContainer().get(OkrWorkChat.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery( String.class );
		Root<OkrWorkChat> root = cq.from(OkrWorkChat.class);
		
		Predicate p = cb.isNotNull( root.get( OkrWorkChat_.id ) );
		if( identities_ok != null && identities_ok.size() > 0 ){
			p = cb.and( p, cb.not(root.get( OkrWorkChat_.senderIdentity ).in( identities_ok )) );
		}
		if( identities_error != null && identities_error.size() > 0 ){
			p = cb.and( p, cb.not(root.get( OkrWorkChat_.senderIdentity ).in( identities_error )) );
		}
		cq.distinct(true).select(root.get( OkrWorkChat_.senderIdentity ));
		return em.createQuery(cq.where(p)).getResultList();
	}
	/**
	 * 查询工作交流信息接收者身份列表（去重复）
	 * @param identities_ok 排除身份
	 * @param identities_error 排除身份
	 * @return
	 * @throws Exception 
	 */
	public List<String> listAllDistinctTargetIdentity(List<String> identities_ok, List<String> identities_error) throws Exception {
		EntityManager em = this.entityManagerContainer().get(OkrWorkChat.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery( String.class );
		Root<OkrWorkChat> root = cq.from(OkrWorkChat.class);
		
		Predicate p = cb.isNotNull( root.get( OkrWorkChat_.id ) );
		if( identities_ok != null && identities_ok.size() > 0 ){
			p = cb.and( p, cb.not(root.get( OkrWorkChat_.targetIdentity ).in( identities_ok )) );
		}
		if( identities_error != null && identities_error.size() > 0 ){
			p = cb.and( p, cb.not(root.get( OkrWorkChat_.targetIdentity ).in( identities_error )) );
		}
		cq.distinct(true).select(root.get( OkrWorkChat_.targetIdentity ));
		return em.createQuery(cq.where(p)).getResultList();
	}
	/**
	 * 根据身份名称，从具体工作交流信息中查询与该身份有关的所有信息列表
	 * @param identity
	 * @param recordId 
	 * @return
	 * @throws Exception 
	 */
	public List<OkrWorkChat> listErrorIdentitiesInWorkChat(String identity, String recordId) throws Exception {
		EntityManager em = this.entityManagerContainer().get(OkrWorkChat.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<OkrWorkChat> cq = cb.createQuery( OkrWorkChat.class );
		Root<OkrWorkChat> root = cq.from( OkrWorkChat.class );
		Predicate p = cb.isNotNull(root.get( OkrWorkChat_.id ));
		
		if( recordId != null && !recordId.isEmpty() && !"all".equals( recordId ) ){
			p = cb.and( p, cb.equal( root.get( OkrWorkChat_.id ), recordId ) );
		}
		
		Predicate p_targetIdentity = cb.isNotNull(root.get( OkrWorkChat_.targetIdentity ));
		p_targetIdentity = cb.and( p_targetIdentity, cb.equal( root.get( OkrWorkChat_.targetIdentity ), identity ) );
		
		Predicate p_senderIdentity = cb.isNotNull(root.get( OkrWorkChat_.senderIdentity ));
		p_senderIdentity = cb.and( p_senderIdentity, cb.equal( root.get( OkrWorkChat_.senderIdentity ), identity ) );
		
		Predicate p_identity = cb.or( p_targetIdentity, p_senderIdentity );
		
		p = cb.and( p, p_identity );
		
		return em.createQuery(cq.where(p)).getResultList();
	}
}

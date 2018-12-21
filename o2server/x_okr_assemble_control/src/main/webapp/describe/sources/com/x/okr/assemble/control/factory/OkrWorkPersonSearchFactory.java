package com.x.okr.assemble.control.factory;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.commons.lang3.StringUtils;

import com.x.okr.assemble.control.AbstractFactory;
import com.x.okr.assemble.control.Business;
import com.x.okr.entity.OkrWorkPerson;

public class OkrWorkPersonSearchFactory extends AbstractFactory {

	public OkrWorkPersonSearchFactory(Business business) throws Exception {
		super(business);
	}

	/**
	 * 根据条件搜索中心工作ID
	 * @param id
	 * @param count
	 * @param sequence
	 * @param com.x.okr.assemble.control.jaxrs.okrcenterworkinfo.WrapInFilter wrapIn
	 * @return
	 * @throws Exception 
	 */
	@SuppressWarnings("unchecked")
	public List<OkrWorkPerson> listCenterWorkPersonNextWithFilter(String id, Integer count, Object sequence,
			com.x.okr.assemble.control.jaxrs.WorkCommonQueryFilter wrapIn) throws Exception {
		// 先获取上一页最后一条的sequence值，如果有值的话，以此sequence值作为依据取后续的count条数据
		EntityManager em = this.entityManagerContainer().get( OkrWorkPerson.class );
		String order = wrapIn.getOrder();// 排序方式
		List<Object> vs = new ArrayList<>();
		StringBuffer sql_stringBuffer = new StringBuffer();

		if (order == null || order.isEmpty()) {
			order = "DESC";
		}

		Integer index = 1;
		sql_stringBuffer.append("SELECT o FROM " + OkrWorkPerson.class.getCanonicalName() + " o where o.workId is null and o.processIdentity = '观察者' ");

		if ((null != sequence)) {
			sql_stringBuffer.append(" and o." + wrapIn.getSequenceField() + " " + (StringUtils.equalsIgnoreCase(order, "DESC") ? "<" : ">") + (" ?" + (index)));
			vs.add(sequence);
			index++;
		}
		
		//根据标题模糊查询
		if (null != wrapIn.getTitle() && !wrapIn.getTitle().isEmpty()) {
			sql_stringBuffer.append(" and o.centerTitle like ?" + (index));
			vs.add("%" + wrapIn.getTitle() + "%");
			index++;
		}
		
		//根据信息状态查询，比如：正常，已删除
		if (null != wrapIn.getQ_statuses() && wrapIn.getQ_statuses().size() > 0) {
			sql_stringBuffer.append(" and o.status in ( ?" + (index) + " )");
			vs.add(wrapIn.getQ_statuses());
			index++;
		}
		
		//根据默认的工作类别查询
		if (null != wrapIn.getDefaultWorkTypes() && wrapIn.getDefaultWorkTypes().size() > 0) {
			sql_stringBuffer.append(" and o.workType in ( ?" + (index) + " )");
			vs.add(wrapIn.getDefaultWorkTypes());
			index++;
		}
		
		//根据用户身份查询查询
		if (null != wrapIn.getIdentity() && !wrapIn.getIdentity().isEmpty() ) {
			sql_stringBuffer.append(" and o.employeeIdentity = ?" + (index) );
			vs.add(wrapIn.getIdentity());
			index++;
		}
		
		sql_stringBuffer.append(" order by o." + wrapIn.getSequenceField() + " " + (StringUtils.equalsIgnoreCase(order, "DESC") ? "DESC" : "ASC"));

		Query query = em.createQuery( sql_stringBuffer.toString(), OkrWorkPerson.class );

		for (int i = 0; i < vs.size(); i++) {
			query.setParameter(i + 1, vs.get(i));
		}
		return query.setMaxResults(count).getResultList();
	}
	
	/**
	 * 根据条件搜索中心工作ID
	 * @param id
	 * @param count
	 * @param sequence
	 * @param com.x.okr.assemble.control.jaxrs.okrcenterworkinfo.WrapInFilter wrapIn
	 * @return
	 * @throws Exception 
	 */
	@SuppressWarnings("unchecked")
	public List<OkrWorkPerson> listCenterWorkPersonPrevWithFilter(String id, Integer count, Object sequence,
			com.x.okr.assemble.control.jaxrs.WorkCommonQueryFilter wrapIn) throws Exception {
		// 先获取上一页最后一条的sequence值，如果有值的话，以此sequence值作为依据取后续的count条数据
		EntityManager em = this.entityManagerContainer().get( OkrWorkPerson.class );
		String order = wrapIn.getOrder();// 排序方式
		List<Object> vs = new ArrayList<>();
		StringBuffer sql_stringBuffer = new StringBuffer();

		if (order == null || order.isEmpty()) {
			order = "DESC";
		}

		Integer index = 1;
		sql_stringBuffer.append("SELECT o FROM " + OkrWorkPerson.class.getCanonicalName() + " o where o.workId is null and o.processIdentity = '观察者' ");

		if ((null != sequence)) {
			sql_stringBuffer.append(" and o." + wrapIn.getSequenceField() + " " + (StringUtils.equalsIgnoreCase(order, "DESC") ? ">" : "<") + (" ?" + (index)));
			vs.add(sequence);
			index++;
		}
		
		//根据标题模糊查询
		if (null != wrapIn.getTitle() && !wrapIn.getTitle().isEmpty()) {
			sql_stringBuffer.append(" and o.centerTitle like ?" + (index));
			vs.add("%" + wrapIn.getTitle() + "%");
			index++;
		}
		
		//根据信息状态查询，比如：正常，已删除
		if (null != wrapIn.getQ_statuses() && wrapIn.getQ_statuses().size() > 0) {
			sql_stringBuffer.append(" and o.status in ( ?" + (index) + " )");
			vs.add(wrapIn.getQ_statuses());
			index++;
		}
		
		//根据默认的工作类别查询
		if (null != wrapIn.getDefaultWorkTypes() && wrapIn.getDefaultWorkTypes().size() > 0) {
			sql_stringBuffer.append(" and o.workType in ( ?" + (index) + " )");
			vs.add(wrapIn.getDefaultWorkTypes());
			index++;
		}
		
		//根据用户身份查询查询
		if (null != wrapIn.getIdentity() && !wrapIn.getIdentity().isEmpty() ) {
			sql_stringBuffer.append(" and o.employeeIdentity = ?" + (index) );
			vs.add(wrapIn.getIdentity());
			index++;
		}
		
		sql_stringBuffer.append(" order by o." + wrapIn.getSequenceField() + " " + (StringUtils.equalsIgnoreCase(order, "DESC") ? "DESC" : "ASC"));

		Query query = em.createQuery( sql_stringBuffer.toString(), OkrWorkPerson.class );

		for (int i = 0; i < vs.size(); i++) {
			query.setParameter(i + 1, vs.get(i));
		}
		return query.setMaxResults(count).getResultList();
	}
	
	/**
	 * 查询符合的中心工作信息总数
	 * @param id
	 * @param count
	 * @param sequence
	 * @param com.x.okr.assemble.control.jaxrs.okrcenterworkinfo.WrapInFilter wrapIn
	 * @return
	 * @throws Exception
	 */
	public long getCountForCenterInfoWithFilter( com.x.okr.assemble.control.jaxrs.WorkCommonQueryFilter wrapIn ) throws Exception {
		//先获取上一页最后一条的sequence值，如果有值的话，以此sequence值作为依据取后续的count条数据
		EntityManager em = this.entityManagerContainer().get( OkrWorkPerson.class );
		List<Object> vs = new ArrayList<>();
		StringBuffer sql_stringBuffer = new StringBuffer();
		Integer index = 1;
		
		sql_stringBuffer.append( "SELECT count( o.id ) FROM "+OkrWorkPerson.class.getCanonicalName()+" o where o.workId is null and o.processIdentity = '观察者'  " );
		
		// 根据标题模糊查询
		if (null != wrapIn.getTitle() && !wrapIn.getTitle().isEmpty()) {
			sql_stringBuffer.append(" and o.centerTitle like ?" + (index));
			vs.add("%" + wrapIn.getTitle() + "%");
			index++;
		}

		// 根据信息状态查询，比如：正常，已删除
		if (null != wrapIn.getQ_statuses() && wrapIn.getQ_statuses().size() > 0) {
			sql_stringBuffer.append(" and o.status in ( ?" + (index) + " )");
			vs.add(wrapIn.getQ_statuses());
			index++;
		}

		// 根据默认的工作类别查询
		if (null != wrapIn.getDefaultWorkTypes() && wrapIn.getDefaultWorkTypes().size() > 0) {
			sql_stringBuffer.append(" and o.workType in ( ?" + (index) + " )");
			vs.add(wrapIn.getDefaultWorkTypes());
			index++;
		}
		
		//根据用户身份查询查询
		if (null != wrapIn.getIdentity() && !wrapIn.getIdentity().isEmpty()) {
			sql_stringBuffer.append(" and o.employeeIdentity = ?" + (index));
			vs.add(wrapIn.getIdentity());
			index++;
		}

		Query query = em.createQuery( sql_stringBuffer.toString(), OkrWorkPerson.class );
		for (int i = 0; i < vs.size(); i++) {
			query.setParameter(i + 1, vs.get(i));
		}
		return (Long) query.getSingleResult();
	}
}
package com.x.crm.assemble.control.factory;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.x.crm.assemble.control.AbstractFactory;
import com.x.crm.assemble.control.Business;
import com.x.crm.assemble.control.wrapin.WrapInFilterCustomerBaseInfo;
import com.x.crm.core.entity.CustomerBaseInfo;
import com.x.crm.core.entity.CustomerBaseInfo_;

public class CustomerBaseInfoFactory extends AbstractFactory  {
	private Logger logger = LoggerFactory.getLogger(CustomerBaseInfoFactory.class);

	private static String joinmark = "_";

	public CustomerBaseInfoFactory(Business business) throws Exception {
		super(business);
		// TODO Auto-generated constructor stub
	}

	public long count() throws Exception {
		EntityManager em = this.entityManagerContainer().get(CustomerBaseInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<CustomerBaseInfo> root = cq.from(CustomerBaseInfo.class);
		Predicate p = cb.isNotNull(root.get(CustomerBaseInfo_.id));
		//cq.select(cb.count(root));
		//return em.createQuery(cq).getResultList();
		cq.select(cb.count(root)).where(p);
		return em.createQuery(cq).getSingleResult();
	}

	//列出所有客户名称
	public List<String> listAll() throws Exception {
		EntityManager em = this.entityManagerContainer().get(CustomerBaseInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<CustomerBaseInfo> root = cq.from(CustomerBaseInfo.class);
		cq.select(root.get(CustomerBaseInfo_.id));
		return em.createQuery(cq).getResultList();
	}

	//根据id查找客户是否存在
	public Boolean findCustomerById(String _id) throws Exception {
		EntityManager em = this.entityManagerContainer().get(CustomerBaseInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<CustomerBaseInfo> root = cq.from(CustomerBaseInfo.class);
		Predicate p = cb.equal(root.get(CustomerBaseInfo_.id), _id);
		cq.select(root.get(CustomerBaseInfo_.id)).where(p);

		List<String> _tmpList = new ArrayList<>();
		_tmpList = em.createQuery(cq).getResultList();
		int _tmpSize = _tmpList.size();
		if (_tmpSize > 0) {
			return true;
		} else {
			return false;
		}
	}

	//根据用户姓名、客户名称，查找客户是否存在（一个用户下的客户名称是不能重复的。）
	public boolean checkCustomerByCustomerName(String _CustomerName, String _LoginName) throws Exception {
		EntityManager em = this.entityManagerContainer().get(CustomerBaseInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<CustomerBaseInfo> root = cq.from(CustomerBaseInfo.class);
		//Predicate p = cb.like(root.get(CustomerBaseInfo_.id), "%" + _name + "%", '\\');
		Predicate p = cb.equal(root.get(CustomerBaseInfo_.customername), _CustomerName);
		cb.and(p, cb.equal(root.get(CustomerBaseInfo_.creatorname), _LoginName));
		cq.select(root.get(CustomerBaseInfo_.id)).where(p);
		List<String> _tmpList = new ArrayList<>();
		_tmpList = em.createQuery(cq).getResultList();
		if (_tmpList.isEmpty()) {
			return false;
		} else {
			return true;
		}
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
	@SuppressWarnings("unchecked")
	public List<CustomerBaseInfo> listNextWithFilter(String id, Integer count, Object sequence, WrapInFilterCustomerBaseInfo wrapIn) throws Exception {
		//先获取上一页最后一条的sequence值，如果有值的话，以此sequence值作为依据取后续的count条数据
		EntityManager em = this.entityManagerContainer().get(CustomerBaseInfo.class);
		String order = "DESC";
		if (null != wrapIn) {
			order = wrapIn.getOrder();//排序方式
		}

		List<Object> vs = new ArrayList<>();
		StringBuffer sql_stringBuffer = new StringBuffer();

		if (order == null || order.isEmpty()) {
			order = "DESC";
		}

		Integer index = 1;
		sql_stringBuffer.append("SELECT o FROM " + CustomerBaseInfo.class.getCanonicalName() + " o where 1=1");

		if ((null != sequence)) {
			sql_stringBuffer.append(" and o." + wrapIn.getSequenceField() + " " + (StringUtils.equalsIgnoreCase(order, "DESC") ? "<" : ">") + (" ?" + (index)));
			vs.add(sequence);
			index++;
		}

		//		if (null != wrapIn && null != wrapIn.getWorkId() && !wrapIn.getWorkId().isEmpty()) {
		//			sql_stringBuffer.append(" and o.workId =  ?" + (index));
		//			vs.add(wrapIn.getWorkId());
		//			index++;
		//		}

		sql_stringBuffer.append(" order by o." + wrapIn.getSequenceField() + " " + (StringUtils.equalsIgnoreCase(order, "DESC") ? "DESC" : "ASC"));

		logger.error(sql_stringBuffer.toString());
		Query query = em.createQuery(sql_stringBuffer.toString(), CustomerBaseInfo.class);

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
	@SuppressWarnings("unchecked")
	public List<CustomerBaseInfo> listPrevWithFilter(String id, Integer count, Object sequence, WrapInFilterCustomerBaseInfo wrapIn) throws Exception {
		//先获取上一页最后一条的sequence值，如果有值的话，以此sequence值作为依据取后续的count条数据
		EntityManager em = this.entityManagerContainer().get(CustomerBaseInfo.class);
		String order = "DESC";
		if (null != wrapIn) {
			order = wrapIn.getOrder();//排序方式
		}
		List<Object> vs = new ArrayList<>();
		StringBuffer sql_stringBuffer = new StringBuffer();
		Integer index = 1;

		if (order == null || order.isEmpty()) {
			order = "DESC";
		}

		sql_stringBuffer.append("SELECT o FROM " + CustomerBaseInfo.class.getCanonicalName() + " o where 1=1");

		if ((null != sequence)) {
			sql_stringBuffer.append(" and o." + wrapIn.getSequenceField() + " " + (StringUtils.equalsIgnoreCase(order, "DESC") ? ">" : "<") + (" ?" + (index)));
			vs.add(sequence);
			index++;
		}
		if (null != wrapIn && null != wrapIn.getWorkId() && !wrapIn.getWorkId().isEmpty()) {
			sql_stringBuffer.append(" and o.workId =  ?" + (index));
			vs.add(wrapIn.getWorkId());
			index++;
		}

		sql_stringBuffer.append(" order by o." + wrapIn.getSequenceField() + " " + (StringUtils.equalsIgnoreCase(order, "DESC") ? "DESC" : "ASC"));
		Query query = em.createQuery(sql_stringBuffer.toString(), CustomerBaseInfo.class);
		//为查询设置所有的参数值
		for (int i = 0; i < vs.size(); i++) {
			query.setParameter(i + 1, vs.get(i));
		}

		return query.setMaxResults(count).getResultList();
	}

	/*
	 * 默认序号:类似20170405_000018
	 *
	 * */
	public String defaultSequence() throws Exception {
		Long _count = this.count();
		LocalDate today = LocalDate.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
		String _tmptoday = today.format(formatter);
		String sequenceNumber = String.format("%06d", _count);
		return _tmptoday + joinmark + sequenceNumber;
	}
}

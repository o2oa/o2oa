package com.x.crm.assemble.control.factory;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.exception.ExceptionWhen;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.jaxrs.EqualsTerms;
import com.x.base.core.project.jaxrs.InTerms;
import com.x.base.core.project.jaxrs.LikeTerms;
import com.x.base.core.project.jaxrs.MemberTerms;
import com.x.base.core.project.jaxrs.NotEqualsTerms;
import com.x.base.core.project.jaxrs.NotInTerms;
import com.x.base.core.project.jaxrs.NotMemberTerms;
import com.x.crm.assemble.control.AbstractFactory;
import com.x.crm.assemble.control.Business;
import com.x.crm.assemble.control.jaxrs.customer.CustomerPageCountException;
import com.x.crm.assemble.control.wrapin.WrapInFilterCustomerBaseInfo;
import com.x.crm.assemble.control.wrapout.WrapOutCustomerBaseInfo;
import com.x.crm.core.entity.CustomerBaseInfo;
import com.x.crm.core.entity.CustomerBaseInfo_;

public class CustomerBaseInfoFactory extends AbstractFactory {
	private Logger logger = LoggerFactory.getLogger(CustomerBaseInfoFactory.class);

	private static String joinmark = "_";

	protected static Integer list_max = 1000;
	protected static Integer list_min = 1;

	protected static final String DESC = "desc";
	protected static final String ASC = "asc";

	public final static String EMPTY_SYMBOL = "(0)";

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
	public Boolean IsExistById(String _id) throws Exception {
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

	//	//根据id查找客户。
	//	public CustomerBaseInfo findCustomerById(String _id) throws Exception {
	//		EntityManager em = this.entityManagerContainer().get(CustomerBaseInfo.class);
	//		CriteriaBuilder cb = em.getCriteriaBuilder();
	//		CriteriaQuery<CustomerBaseInfo> cq = cb.createQuery(CustomerBaseInfo.class);
	//		Root<CustomerBaseInfo> root = cq.from(CustomerBaseInfo.class);
	//		Predicate p = cb.equal(root.get(CustomerBaseInfo_.id), _id);
	//		em.createQuery(cq.select(root).where(p));
	//		return em.createQuery(cq).getResultList().get(0);
	//	}

	//根据用户姓名、客户名称，查找客户是否存在（一个用户下的客户名称是不能重复的。）
	public boolean checkCustomerByCustomerName(String _CustomerName, String _LoginName) throws Exception {
		EntityManager em = this.entityManagerContainer().get(CustomerBaseInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<CustomerBaseInfo> root = cq.from(CustomerBaseInfo.class);
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
	 * 
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

		if (null != wrapIn && null != wrapIn.getWorkId() && !wrapIn.getWorkId().isEmpty()) {
			sql_stringBuffer.append(" and o.workId =  ?" + (index));
			vs.add(wrapIn.getWorkId());
			index++;
		}

		sql_stringBuffer.append(" order by o." + wrapIn.getSequenceField() + " " + (StringUtils.equalsIgnoreCase(order, "DESC") ? "DESC" : "ASC"));

		logger.error(new Exception(sql_stringBuffer.toString()));
		Query query = em.createQuery(sql_stringBuffer.toString(), CustomerBaseInfo.class);

		for (int i = 0; i < vs.size(); i++) {
			query.setParameter(i + 1, vs.get(i));
		}
		return query.setMaxResults(count).getResultList();
	}

	/**
	 * 查询上一页的信息数据
	 * 
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
	 */
	public String defaultSequence() throws Exception {
		Long _count = this.count();
		LocalDate today = LocalDate.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
		String _tmptoday = today.format(formatter);
		String sequenceNumber = String.format("%06d", _count);
		return _tmptoday + joinmark + sequenceNumber;
	}

	//根据页码，每页数量获得一个结果列表 /page/{page}/count/{count}
	public ActionResult<List<WrapOutCustomerBaseInfo>> listbyPageAndCount(WrapCopier<CustomerBaseInfo, WrapOutCustomerBaseInfo> wrapout_copier, String id, Integer page, Integer count, String sequenceField, EqualsTerms equals, NotEqualsTerms notEquals, LikeTerms likes, InTerms ins, NotInTerms notIns,
			MemberTerms members, NotMemberTerms notMembers, boolean andJoin, String order) throws Exception {

		Class<CustomerBaseInfo> tClass = (Class<CustomerBaseInfo>) wrapout_copier.getOrigClass();
		Class<WrapOutCustomerBaseInfo> wClass = (Class<WrapOutCustomerBaseInfo>) wrapout_copier.getDestClass();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			// 先获取上一页最后一条的sequence值，如果有值的话，以此sequence值作为依据取后续的count条数据
			Object sequence = null;
			if (!StringUtils.equalsIgnoreCase(id, EMPTY_SYMBOL)) {
				sequence = PropertyUtils.getProperty(emc.find(id, tClass, ExceptionWhen.not_found), sequenceField);
			}
			EntityManager em = emc.get(tClass);
			String str = "SELECT o FROM " + tClass.getCanonicalName() + " o";
			/* 预编译的SQL语句的参数序号，必须由1开始 */
			Integer index = 1;
			List<String> ps = new ArrayList<>();
			List<Object> vs = new ArrayList<>();
			// 如果sequence值不为空，那么从sequence开始，如果为空则从头开始
			logger.error(new Exception("sequence:" + sequence));
			if (null != sequence) {
				//ps.add("o." + sequenceField + " " + (StringUtils.equalsIgnoreCase(order, DESC) ? "<" : ">") + (" ?" + (index)));
				//vs.add(sequence);
				index++;
			}
			//			if (null != sequence) {
			//				ps.add("o." + sequenceField + " " + ("IS NOT NULL"));
			//				vs.add(sequence);
			//				index++;
			//			}

			// 以下组织各种条件子句，放到List<String> ps里，相应的值放到List<Object> vs里
			if (null != equals && (!equals.isEmpty())) {
				for (Entry<String, Object> en : equals.entrySet()) {
					ps.add("o." + en.getKey() + (" = ?" + index));
					vs.add(en.getValue());
					index++;
				}
			}
			if (null != notEquals && (!notEquals.isEmpty())) {
				for (Entry<String, Object> en : notEquals.entrySet()) {
					ps.add("o." + en.getKey() + (" != ?" + index));
					vs.add(en.getValue());
					index++;
				}
			}
			// like有点特殊，多个like一般用or相联
			if (null != likes && (!likes.isEmpty())) {
				List<String> ors = new ArrayList<>();
				for (Entry<String, Object> en : likes.entrySet()) {
					for (String s : StringUtils.split(en.getValue().toString(), " ")) {
						ors.add("o." + en.getKey() + (" Like ?" + index));
						vs.add("%" + s + "%");
						index++;
					}
				}
				ps.add("(" + StringUtils.join(ors, " or ") + ")");
			}
			if (null != ins && (!ins.isEmpty())) {
				for (Entry<String, Collection<?>> en : ins.entrySet()) {
					ps.add("o." + en.getKey() + (" in ?" + index));
					vs.add(en.getValue());
					index++;
				}
			}
			if (null != notIns && (!notIns.isEmpty())) {
				for (Entry<String, Collection<?>> en : notIns.entrySet()) {
					ps.add("o." + en.getKey() + (" not in ?" + index));
					vs.add(en.getValue());
					index++;
				}
			}
			if (null != members && (!members.isEmpty())) {
				for (Entry<String, Object> en : members.entrySet()) {
					ps.add(("?" + index) + (" member of o." + en.getKey()));
					vs.add(en.getValue());
					index++;
				}
			}
			if (null != notMembers && (!notMembers.isEmpty())) {
				for (Entry<String, Object> en : notMembers.entrySet()) {
					ps.add(("?" + index) + (" not member of o." + en.getKey()));
					vs.add(en.getValue());
					index++;
				}
			}
			// 使用指定的方式将条件子句组合起来
			if (!ps.isEmpty()) {
				str += " where " + StringUtils.join(ps, (andJoin ? " and " : " or "));
			}
			// 排序
			str += " order by o." + sequenceField + " " + (StringUtils.equalsIgnoreCase(order, DESC) ? DESC : ASC);
			logger.error(new Exception("JPQL str :" + str));
			Query query = em.createQuery(str, tClass);
			// 为查询设置所有的参数值
			for (int i = 0; i < vs.size(); i++) {
				query.setParameter(i + 1, vs.get(i));
			}
			List<WrapOutCustomerBaseInfo> wraps = new ArrayList<WrapOutCustomerBaseInfo>();
			// 限制查询的条数，一般就取一页的条目数，最多为超过list_max
			@SuppressWarnings("unchecked")
			List<CustomerBaseInfo> tmplist = query.setMaxResults(Math.max(Math.min(page * count, list_max), list_min)).getResultList();
			logger.error(new Exception("结果一共：" + tmplist.size() + "条"));

			ActionResult<List<WrapOutCustomerBaseInfo>> result = new ActionResult<>();

			if ((page * count) > tmplist.size()) {
				Exception exception = new CustomerPageCountException();
				result.error(exception);
			} else {
				List<CustomerBaseInfo> list = tmplist.subList((page - 1) * count, page * count);
				if (!list.isEmpty()) {
					// 查询初始的编号
					//Long rank = this.rank(emc, tClass, sequenceField, PropertyUtils.getProperty(list.get(0), sequenceField), equals, notEquals, likes, ins, notIns, members, notMembers, andJoin, order);
					Long rank = 0l;
					// 为输出的结果进行编号
					for (CustomerBaseInfo t : list) {
						WrapOutCustomerBaseInfo w = wClass.newInstance();
						wrapout_copier.copy(t, w);
						PropertyUtils.setProperty(w, "rank", rank++);
						wraps.add(w);
					}
				}
				result.setData(wraps);
				result.setCount((long) tmplist.size());
				// 设置查询结果的总条目数				
			}

			return result;
		}

	}
}

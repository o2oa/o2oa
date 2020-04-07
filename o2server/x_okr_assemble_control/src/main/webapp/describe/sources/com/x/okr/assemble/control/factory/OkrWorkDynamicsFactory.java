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
import com.x.okr.entity.OkrWorkDynamics;
import com.x.okr.entity.OkrWorkDynamics_;

/**
 * 类 名：OkrWorkDynamicsFactory<br/>
 * 实体类：OkrWorkDynamics<br/>
 * 作 者：Liyi<br/>
 * 单 位：O2 Team<br/>
 * 日 期：2016-05-20 17:17:27
 **/
public class OkrWorkDynamicsFactory extends AbstractFactory {

	public OkrWorkDynamicsFactory(Business business) throws Exception {
		super(business);
	}

	// @MethodDescribe( "获取指定Id的OkrWorkDynamics实体信息对象" )
	public OkrWorkDynamics get(String id) throws Exception {
		return this.entityManagerContainer().find(id, OkrWorkDynamics.class, ExceptionWhen.none);
	}

	// @MethodDescribe( "列示全部的OkrWorkDynamics实体信息列表" )
	public List<String> listAll() throws Exception {
		EntityManager em = this.entityManagerContainer().get(OkrWorkDynamics.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<OkrWorkDynamics> root = cq.from(OkrWorkDynamics.class);
		cq.select(root.get(OkrWorkDynamics_.id));
		return em.createQuery(cq).getResultList();
	}

	// @MethodDescribe( "列示指定Id的OkrWorkDynamics实体信息列表" )
	public List<OkrWorkDynamics> list(List<String> ids) throws Exception {
		if (ids == null || ids.size() == 0) {
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
	 * 
	 * @param centerId
	 *            中心工作
	 * @return
	 * @throws Exception
	 */
	// @MethodDescribe( "根据中心工作ID，列示所有的信息" )
	public List<String> listByCenterWorkId(String centerId) throws Exception {
		if (centerId == null || centerId.isEmpty()) {
			throw new Exception(" centerId is null!");
		}
		EntityManager em = this.entityManagerContainer().get(OkrWorkDynamics.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<OkrWorkDynamics> root = cq.from(OkrWorkDynamics.class);
		Predicate p = cb.equal(root.get(OkrWorkDynamics_.centerId), centerId);
		cq.select(root.get(OkrWorkDynamics_.id));
		return em.createQuery(cq.where(p)).getResultList();
	}

	/**
	 * 根据工作信息ID，列示所有的数据信息
	 * 
	 * @param workId
	 * @return
	 * @throws Exception
	 */
	//@MethodDescribe("根据工作信息ID，列示所有的数据信息")
	public List<String> listByWorkId(String workId) throws Exception {
		EntityManager em = this.entityManagerContainer().get(OkrWorkDynamics.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<OkrWorkDynamics> root = cq.from(OkrWorkDynamics.class);
		Predicate p = cb.equal(root.get(OkrWorkDynamics_.workId), workId);
		cq.select(root.get(OkrWorkDynamics_.id));
		return em.createQuery(cq.where(p)).getResultList();
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
	public List<OkrWorkDynamics> listNextWithFilter(String id, Integer count, Object sequence, List<String> centerIds,
			List<String> workIds, String sequenceField, String order, Boolean isOkrManager) throws Exception {
		// 先获取上一页最后一条的sequence值，如果有值的话，以此sequence值作为依据取后续的count条数据
		EntityManager em = this.entityManagerContainer().get(OkrWorkDynamics.class);
		List<Object> vs = new ArrayList<>();
		StringBuffer sql_stringBuffer = new StringBuffer();

		if (order == null || order.isEmpty()) {
			order = "DESC";
		}

		Integer index = 1;
		sql_stringBuffer.append("SELECT o FROM " + OkrWorkDynamics.class.getCanonicalName() + " o where 1=1");

		if ((null != sequence)) {
			sql_stringBuffer.append(" and o." + sequenceField + " "
					+ (StringUtils.equalsIgnoreCase(order, "DESC") ? "<" : ">") + (" ?" + (index)));
			vs.add(sequence);
			index++;
		}

		if ((null != centerIds && centerIds.size() > 0) || (null != workIds && workIds.size() > 0)) {
			sql_stringBuffer.append(" and ( ");
			// 中心工作IDS
			if (null != centerIds && centerIds.size() > 0) {
				sql_stringBuffer.append(" o.centerId in ( ?" + (index) + " )");
				vs.add(centerIds);
				index++;
			}

			if (null != centerIds && centerIds.size() > 0 && null != workIds && workIds.size() > 0) {
				sql_stringBuffer.append(" or ");
			}

			// 工作IDS
			if (null != workIds && workIds.size() > 0) {
				sql_stringBuffer.append(" o.workId in ( ?" + (index) + " )");
				vs.add(workIds);
				index++;
			}

			sql_stringBuffer.append(" ) ");
		} else {
			if (!isOkrManager) {
				return null;
			}
		}

		sql_stringBuffer.append(
				" order by o." + sequenceField + " " + (StringUtils.equalsIgnoreCase(order, "DESC") ? "DESC" : "ASC"));

		Query query = em.createQuery(sql_stringBuffer.toString(), OkrWorkDynamics.class);

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
	public List<OkrWorkDynamics> listPrevWithFilter(String id, Integer count, Object sequence, List<String> centerIds,
			List<String> workIds, String sequenceField, String order, Boolean isOkrSystemAdmin) throws Exception {
		// 先获取上一页最后一条的sequence值，如果有值的话，以此sequence值作为依据取后续的count条数据
		EntityManager em = this.entityManagerContainer().get(OkrWorkDynamics.class);
		List<Object> vs = new ArrayList<>();
		StringBuffer sql_stringBuffer = new StringBuffer();
		Integer index = 1;

		if (order == null || order.isEmpty()) {
			order = "DESC";
		}

		sql_stringBuffer.append("SELECT o FROM " + OkrWorkDynamics.class.getCanonicalName() + " o where 1=1");

		if ((null != sequence)) {
			sql_stringBuffer.append(" and o." + sequenceField + " "
					+ (StringUtils.equalsIgnoreCase(order, "DESC") ? "<" : ">") + (" ?" + (index)));
			vs.add(sequence);
			index++;
		}

		if ((null != centerIds && centerIds.size() > 0) || (null != workIds && workIds.size() > 0)) {
			sql_stringBuffer.append(" and ( ");
			// 中心工作IDS
			if (null != centerIds && centerIds.size() > 0) {
				sql_stringBuffer.append(" o.centerId in ( ?" + (index) + " )");
				vs.add(centerIds);
				index++;
			}

			if (null != centerIds && centerIds.size() > 0 && null != workIds && workIds.size() > 0) {
				sql_stringBuffer.append(" or ");
			}

			// 工作IDS
			if (null != workIds && workIds.size() > 0) {
				sql_stringBuffer.append(" o.workId in ( ?" + (index) + " )");
				vs.add(workIds);
				index++;
			}

			sql_stringBuffer.append(" ) ");
		} else {
			if (!isOkrSystemAdmin) {
				return null;
			}
		}

		sql_stringBuffer.append(
				" order by o." + sequenceField + " " + (StringUtils.equalsIgnoreCase(order, "DESC") ? "DESC" : "ASC"));

		Query query = em.createQuery(sql_stringBuffer.toString(), OkrWorkDynamics.class);
		// 为查询设置所有的参数值
		for (int i = 0; i < vs.size(); i++) {
			query.setParameter(i + 1, vs.get(i));
		}

		return query.setMaxResults(count).getResultList();
	}

	/**
	 * 查询符合的信息总数
	 * 
	 * @param id
	 * @param count
	 * @param sequence
	 * @param wrapIn
	 * @return
	 * @throws Exception
	 */
	public long getCountWithFilter(List<String> centerIds, List<String> workIds, String sequenceField, String order,
			Boolean isOkrSystemAdmin) throws Exception {
		// 先获取上一页最后一条的sequence值，如果有值的话，以此sequence值作为依据取后续的count条数据
		EntityManager em = this.entityManagerContainer().get(OkrWorkDynamics.class);
		List<Object> vs = new ArrayList<>();
		StringBuffer sql_stringBuffer = new StringBuffer();
		Integer index = 1;

		sql_stringBuffer.append("SELECT count(o.id) FROM " + OkrWorkDynamics.class.getCanonicalName() + " o where 1=1");

		if ((null != centerIds && centerIds.size() > 0) || (null != workIds && workIds.size() > 0)) {
			sql_stringBuffer.append(" and ( ");
			// 中心工作IDS
			if (null != centerIds && centerIds.size() > 0) {
				sql_stringBuffer.append(" o.centerId in ( ?" + (index) + " )");
				vs.add(centerIds);
				index++;
			}

			if (null != centerIds && centerIds.size() > 0 && null != workIds && workIds.size() > 0) {
				sql_stringBuffer.append(" or ");
			}

			// 工作IDS
			if (null != workIds && workIds.size() > 0) {
				sql_stringBuffer.append(" o.workId in ( ?" + (index) + " )");
				vs.add(workIds);
				index++;
			}

			sql_stringBuffer.append(" ) ");
		} else {
			if (!isOkrSystemAdmin) {
				return 0;
			}
		}

		Query query = em.createQuery(sql_stringBuffer.toString(), OkrWorkDynamics.class);

		for (int i = 0; i < vs.size(); i++) {
			query.setParameter(i + 1, vs.get(i));
		}
		return (Long) query.getSingleResult();
	}

	/**
	 * 查询工作动态信息操作者身份列表（去重复）
	 * 
	 * @param identities_ok
	 *            排除身份
	 * @param identities_error
	 *            排除身份
	 * @return
	 * @throws Exception
	 */
	public List<String> listAllDistinctTargetIdentity(List<String> identities_ok, List<String> identities_error)
			throws Exception {
		EntityManager em = this.entityManagerContainer().get(OkrWorkDynamics.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<OkrWorkDynamics> root = cq.from(OkrWorkDynamics.class);

		Predicate p = cb.isNotNull(root.get(OkrWorkDynamics_.id));
		if (identities_ok != null && identities_ok.size() > 0) {
			p = cb.and(p, cb.not(root.get(OkrWorkDynamics_.targetIdentity).in(identities_ok)));
		}
		if (identities_error != null && identities_error.size() > 0) {
			p = cb.and(p, cb.not(root.get(OkrWorkDynamics_.targetIdentity).in(identities_error)));
		}
		cq.distinct(true).select(root.get(OkrWorkDynamics_.targetIdentity));
		return em.createQuery(cq.where(p)).getResultList();
	}

	/**
	 * 根据身份名称，从具体工作操作动态信息中查询与该身份有关的所有信息列表
	 * 
	 * @param identity
	 * @return
	 * @throws Exception
	 */
	public List<OkrWorkDynamics> listErrorIdentitiesInDynamics(String identity) throws Exception {
		EntityManager em = this.entityManagerContainer().get(OkrWorkDynamics.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<OkrWorkDynamics> cq = cb.createQuery(OkrWorkDynamics.class);
		Root<OkrWorkDynamics> root = cq.from(OkrWorkDynamics.class);
		Predicate p = cb.isNotNull(root.get(OkrWorkDynamics_.id));

		Predicate p_targetIdentity = cb.isNotNull(root.get(OkrWorkDynamics_.targetIdentity));
		p_targetIdentity = cb.and(p_targetIdentity, cb.equal(root.get(OkrWorkDynamics_.targetIdentity), identity));

		p = cb.and(p, p_targetIdentity);

		return em.createQuery(cq.where(p)).getResultList();
	}
}

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
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.okr.assemble.control.AbstractFactory;
import com.x.okr.assemble.control.Business;
import com.x.okr.assemble.control.jaxrs.WorkCommonSearchFilter;
import com.x.okr.entity.OkrWorkPerson;
import com.x.okr.entity.OkrWorkPerson_;

public class OkrWorkPersonFactory extends AbstractFactory {

	private static  Logger logger = LoggerFactory.getLogger(OkrWorkPersonFactory.class);

	public OkrWorkPersonFactory(Business business) throws Exception {
		super(business);
	}

	// @MethodDescribe( "获取指定Id的OkrWorkPerson对象" )
	public OkrWorkPerson get(String id) throws Exception {
		return this.entityManagerContainer().find(id, OkrWorkPerson.class, ExceptionWhen.none);
	}

	// @MethodDescribe( "列示全部的OkrWorkPerson列表" )
	public List<String> listAll() throws Exception {
		EntityManager em = this.entityManagerContainer().get(OkrWorkPerson.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<OkrWorkPerson> root = cq.from(OkrWorkPerson.class);
		cq.select(root.get(OkrWorkPerson_.id));
		return em.createQuery(cq).getResultList();
	}

	// @MethodDescribe( "列示指定Id的OkrWorkPerson列表" )
	public List<OkrWorkPerson> list(List<String> ids) throws Exception {
		if (ids == null || ids.size() == 0) {
			return new ArrayList<OkrWorkPerson>();
		}
		EntityManager em = this.entityManagerContainer().get(OkrWorkPerson.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<OkrWorkPerson> cq = cb.createQuery(OkrWorkPerson.class);
		Root<OkrWorkPerson> root = cq.from(OkrWorkPerson.class);
		Predicate p = root.get(OkrWorkPerson_.id).in(ids);
		return em.createQuery(cq.where(p)).getResultList();
	}

	/**
	 * 根据用户姓名，列示有权限访问的所有具体工作Id列表
	 * 
	 * @param name
	 * @param statuses
	 *            需要显示的信息状态: 正常|已删除
	 * @return
	 * @throws Exception
	 */
	// @MethodDescribe( "根据用户姓名，列示有权限访问的所有具体工作Id列表" )
	public List<String> listDistinctWorkIdsByIdentity(String userIdentity, String centerId, List<String> statuses)
			throws Exception {
		if (userIdentity == null || userIdentity.isEmpty()) {
			logger.warn("userIdentity is null!");
			return null;
		}

		EntityManager em = this.entityManagerContainer().get(OkrWorkPerson.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<OkrWorkPerson> root = cq.from(OkrWorkPerson.class);
		cq.distinct(true).select(root.get(OkrWorkPerson_.workId));

		/**
		 * 获取的时候过滤条件： 1、如果当前身份是创建者或者部署者的，那么，草稿也要取，如果当前身份不是创建者或者部署者，那么草稿不要去
		 * 2、信息状态是正常的才取出来 3、观察者是自己的取出来 状态正常 and ( ( 姓名 = name and 身份 in ( 部署者,
		 * 创建者 ) ) or ( 姓名 = name and 身份 in ( 协助者，阅知者 ) and 处理状态 != 草稿 ) )
		 */
		Predicate p = cb.isNotNull(root.get(OkrWorkPerson_.workId)); // 工作ID不为空的就是具体工作的权限信息
		if (centerId != null && !centerId.isEmpty()) {
			p = cb.and(p, cb.equal(root.get(OkrWorkPerson_.centerId), centerId));
		}
		if (statuses != null && statuses.size() > 0) {
			p = cb.and(p, root.get(OkrWorkPerson_.status).in(statuses));
		}

		List<String> identityList_1 = new ArrayList<String>();
		List<String> identityList_2 = new ArrayList<String>();
		identityList_1.add("部署者");
		identityList_1.add("创建者");
		// ( 姓名 = name and 身份 in ( 部署者, 创建者 ) )
		Predicate p_creator_or_depoloyer = cb.equal(root.get(OkrWorkPerson_.employeeIdentity), userIdentity);
		p_creator_or_depoloyer = cb.and(p_creator_or_depoloyer,
				root.get(OkrWorkPerson_.processIdentity).in(identityList_1));

		identityList_2.add("观察者");
		identityList_2.add("协助者");
		identityList_2.add("责任者");
		identityList_2.add("阅知者");
		identityList_2.add("授权者");
		// ( 姓名 = name and 身份 in ( 责任者，协助者，阅知者 ) and 处理状态 != 草稿 )
		Predicate p_watcher = cb.equal(root.get(OkrWorkPerson_.employeeIdentity), userIdentity);
		p_watcher = cb.and(p_watcher, root.get(OkrWorkPerson_.processIdentity).in(identityList_2));
		p_watcher = cb.and(p_watcher, cb.notEqual(root.get(OkrWorkPerson_.workProcessStatus), "草稿"));

		p = cb.and(p, cb.or(p_creator_or_depoloyer, p_watcher));

		return em.createQuery(cq.where(p)).setMaxResults(1000).getResultList();
	}

	/**
	 * 根据用户姓名，列示有权限访问的所有具体工作Id列表
	 * 
	 * @param name
	 * @param statuses
	 *            需要显示的信息状态: 正常|已删除
	 * @return
	 * @throws Exception
	 */
	// @MethodDescribe( "根据用户姓名，列示有权限访问的所有具体工作Id列表" )
	public List<String> listDistinctWorkIdsWithMe(String userIdentity, String centerId) throws Exception {
		if (userIdentity == null || userIdentity.isEmpty()) {
			logger.warn("userIdentity is null!");
			return null;
		}
		EntityManager em = this.entityManagerContainer().get(OkrWorkPerson.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<OkrWorkPerson> root = cq.from(OkrWorkPerson.class);
		cq.distinct(true).select(root.get(OkrWorkPerson_.workId));
		Predicate p = cb.equal(root.get(OkrWorkPerson_.employeeIdentity), userIdentity);
		if (centerId != null && !centerId.isEmpty()) {
			p = cb.and(p, cb.equal(root.get(OkrWorkPerson_.centerId), centerId));
		}
		return em.createQuery(cq.where(p)).setMaxResults(1000).getResultList();
	}

	// @MethodDescribe( "根据用户姓名和处理者身份，列示有权限访问的所有具体工作Id列表" )
	public List<String> listDistinctWorkIdsByPerson(String name, String processIdentity) throws Exception {
		if (name == null || name.isEmpty()) {
			logger.warn("name is null!");
			return null;
		}
		if (processIdentity == null || processIdentity.isEmpty()) {
			logger.warn("processIdentity is null!");
			return null;
		}
		EntityManager em = this.entityManagerContainer().get(OkrWorkPerson.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<OkrWorkPerson> root = cq.from(OkrWorkPerson.class);
		cq.distinct(true).select(root.get(OkrWorkPerson_.workId));
		Predicate p = cb.isNotNull(root.get(OkrWorkPerson_.workId));
		p = cb.and(p, cb.equal(root.get(OkrWorkPerson_.employeeName), name));
		p = cb.and(p, cb.equal(root.get(OkrWorkPerson_.processIdentity), processIdentity));
		return em.createQuery(cq.where(p)).setMaxResults(1000).getResultList();
	}

	// @MethodDescribe( "根据用户姓名和处理者身份，列示有权限访问的所有具体工作Id列表" )
	public List<String> listDistinctWorkIdsByPersonIndentity(String centerId, String identity, String processIdentity,
			List<String> notInCenterIds) throws Exception {
		if (identity == null || identity.isEmpty()) {
			logger.warn("identity is null!");
			return null;
		}
		if (processIdentity == null || processIdentity.isEmpty()) {
			logger.warn("processIdentity is null!");
			return null;
		}
		EntityManager em = this.entityManagerContainer().get(OkrWorkPerson.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<OkrWorkPerson> root = cq.from(OkrWorkPerson.class);
		cq.distinct(true).select(root.get(OkrWorkPerson_.workId));

		Predicate p = cb.isNotNull(root.get(OkrWorkPerson_.workId));

		if (notInCenterIds != null && !notInCenterIds.isEmpty()) {
			p = cb.and(p, cb.not(root.get(OkrWorkPerson_.centerId).in(notInCenterIds)));
		}
		if (centerId != null && !centerId.isEmpty()) {
			p = cb.and(p, cb.equal(root.get(OkrWorkPerson_.centerId), centerId));
		}
		p = cb.and(p, cb.equal(root.get(OkrWorkPerson_.employeeIdentity), identity));
		p = cb.and(p, cb.equal(root.get(OkrWorkPerson_.processIdentity), processIdentity));
		p = cb.and(p, cb.equal(root.get(OkrWorkPerson_.status), "正常"));

		return em.createQuery(cq.where(p)).setMaxResults(200).getResultList();
	}

	// @MethodDescribe( "根据用户姓名，列示有权限访问的所有中心工作Id列表" )
	public List<String> listDistinctCenterIdsByPerson(String name, List<String> statuses) throws Exception {
		if (name == null || name.isEmpty()) {
			logger.warn("name is null!");
			return null;
		}
		EntityManager em = this.entityManagerContainer().get(OkrWorkPerson.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<OkrWorkPerson> root = cq.from(OkrWorkPerson.class);
		cq.distinct(true).select(root.get(OkrWorkPerson_.centerId));
		Predicate p = cb.equal(root.get(OkrWorkPerson_.employeeName), name);
		if (statuses != null && statuses.size() > 0) {
			p = cb.and(p, root.get(OkrWorkPerson_.status).in(statuses));
		}
		return em.createQuery(cq.where(p)).setMaxResults(1000).getResultList();
	}

	// @MethodDescribe( "根据用户姓名和处理者身份，列示有权限访问的所有中心工作Id列表" )
	public List<String> listDistinctCenterIdsByPerson(String name, String processIdentity, List<String> statuses)
			throws Exception {
		if (name == null || name.isEmpty()) {
			logger.warn("name is null!");
			return null;
		}
		if (processIdentity == null || processIdentity.isEmpty()) {
			logger.warn("processIdentity is null!");
			return null;
		}
		EntityManager em = this.entityManagerContainer().get(OkrWorkPerson.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<OkrWorkPerson> root = cq.from(OkrWorkPerson.class);
		cq.distinct(true).select(root.get(OkrWorkPerson_.centerId));
		Predicate p = cb.equal(root.get(OkrWorkPerson_.employeeName), name);
		p = cb.and(p, cb.equal(root.get(OkrWorkPerson_.processIdentity), processIdentity));
		if (statuses != null && statuses.size() > 0) {
			p = cb.and(p, root.get(OkrWorkPerson_.status).in(statuses));
		}
		return em.createQuery(cq.where(p)).setMaxResults(1000).getResultList();
	}

	// @MethodDescribe( "根据用户身份和处理者身份，列示有权限访问的所有中心工作Id列表" )
	public List<String> listDistinctCenterIdsByPersonIdentity(String identity, String processIdentity,
			List<String> statuses) throws Exception {
		if (identity == null || identity.isEmpty()) {
			logger.warn("identity is null!");
			return null;
		}
		if (processIdentity == null || processIdentity.isEmpty()) {
			logger.warn("processIdentity is null!");
			return null;
		}
		EntityManager em = this.entityManagerContainer().get(OkrWorkPerson.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<OkrWorkPerson> root = cq.from(OkrWorkPerson.class);
		cq.distinct(true).select(root.get(OkrWorkPerson_.centerId));
		Predicate p = cb.equal(root.get(OkrWorkPerson_.employeeIdentity), identity);
		p = cb.and(p, cb.equal(root.get(OkrWorkPerson_.processIdentity), processIdentity));
		if (statuses != null && statuses.size() > 0) {
			p = cb.and(p, root.get(OkrWorkPerson_.status).in(statuses));
		}
		return em.createQuery(cq.where(p)).setMaxResults(1000).getResultList();
	}

	// @MethodDescribe( "根据组织名称，列示有权限访问的所有中心工作Id列表" )
	public List<String> listDistinctCenterIdsByUnitName(String name, List<String> statuses) throws Exception {
		if (name == null || name.isEmpty()) {
			logger.warn("name is null!");
			return null;
		}
		EntityManager em = this.entityManagerContainer().get(OkrWorkPerson.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<OkrWorkPerson> root = cq.from(OkrWorkPerson.class);
		cq.distinct(true).select(root.get(OkrWorkPerson_.centerId));
		Predicate p = cb.equal(root.get(OkrWorkPerson_.unitName), name);
		if (statuses != null && statuses.size() > 0) {
			p = cb.and(p, root.get(OkrWorkPerson_.status).in(statuses));
		}
		return em.createQuery(cq.where(p)).setMaxResults(1000).getResultList();
	}

	// @MethodDescribe( "根据组织列表，列示有权限访问的所有中心工作Id列表" )
	public List<String> listDistinctCenterIdsByUnitNames(List<String> names, List<String> statuses) throws Exception {
		if (names == null || names.size() == 0) {
			logger.warn("names is null!");
			return null;
		}
		EntityManager em = this.entityManagerContainer().get(OkrWorkPerson.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<OkrWorkPerson> root = cq.from(OkrWorkPerson.class);
		cq.distinct(true).select(root.get(OkrWorkPerson_.centerId));
		Predicate p = root.get(OkrWorkPerson_.unitName).in(names);
		if (statuses != null && statuses.size() > 0) {
			p = cb.and(p, root.get(OkrWorkPerson_.status).in(statuses));
		}
		return em.createQuery(cq.where(p)).setMaxResults(1000).getResultList();
	}

	// @MethodDescribe( "根据顶层组织名称，列示有权限访问的所有中心工作Id列表" )
	public List<String> listDistinctCenterIdsByTopUnitName(String name, List<String> statuses) throws Exception {
		if (name == null || name.isEmpty()) {
			logger.warn("name is null!");
			return null;
		}
		EntityManager em = this.entityManagerContainer().get(OkrWorkPerson.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<OkrWorkPerson> root = cq.from(OkrWorkPerson.class);
		cq.distinct(true).select(root.get(OkrWorkPerson_.centerId));
		Predicate p = cb.equal(root.get(OkrWorkPerson_.topUnitName), name);
		if (statuses != null && statuses.size() > 0) {
			p = cb.and(p, root.get(OkrWorkPerson_.status).in(statuses));
		}
		return em.createQuery(cq.where(p)).setMaxResults(1000).getResultList();
	}

	/**
	 * 根据中心工作ID，查询所有的干系人姓名
	 * 
	 * @param centerId
	 * @param identity
	 *            干系人身份
	 * @return
	 * @throws Exception
	 */
	// @MethodDescribe( "根据中心工作ID，查询所有的干系人身份" )
	public List<String> listDistinctIdentityNameByCenterId(String centerId, String identity) throws Exception {
		if (centerId == null || centerId.isEmpty()) {
			logger.warn("centerId is null!");
			return null;
		}
		EntityManager em = this.entityManagerContainer().get(OkrWorkPerson.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<OkrWorkPerson> root = cq.from(OkrWorkPerson.class);
		cq.distinct(true).select(root.get(OkrWorkPerson_.employeeIdentity));
		Predicate p = cb.equal(root.get(OkrWorkPerson_.centerId), centerId);
		if (identity != null && !identity.isEmpty()) {
			p = cb.and(p, cb.equal(root.get(OkrWorkPerson_.processIdentity), identity));
		}
		return em.createQuery(cq.where(p)).setMaxResults(2000).getResultList();
	}

	// @MethodDescribe( "根据顶层组织列表，列示有权限访问的所有中心工作Id列表" )
	public List<String> listDistinctCenterIdsByTopUnitNames(List<String> names, List<String> statuses)
			throws Exception {
		if (names == null || names.size() == 0) {
			logger.warn("names is null!");
			return null;
		}
		EntityManager em = this.entityManagerContainer().get(OkrWorkPerson.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<OkrWorkPerson> root = cq.from(OkrWorkPerson.class);
		cq.distinct(true).select(root.get(OkrWorkPerson_.centerId));
		Predicate p = root.get(OkrWorkPerson_.topUnitName).in(names);
		if (statuses != null && statuses.size() > 0) {
			p = cb.and(p, root.get(OkrWorkPerson_.status).in(statuses));
		}
		return em.createQuery(cq.where(p)).setMaxResults(1000).getResultList();
	}

	/**
	 * 根据工作ID，获取工作的指定干系人
	 * 
	 * @param id
	 * @param identity
	 * @return
	 * @throws Exception
	 */
	public List<String> getWorkPerson(String workId, String identity, List<String> statuses) throws Exception {
		if (workId == null || workId.isEmpty()) {
			logger.warn("id is null!");
			return null;
		}
		EntityManager em = this.entityManagerContainer().get(OkrWorkPerson.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<OkrWorkPerson> root = cq.from(OkrWorkPerson.class);
		cq.distinct(true).select(root.get(OkrWorkPerson_.employeeName));
		Predicate p = cb.equal(root.get(OkrWorkPerson_.workId), workId);
		if (identity != null && !identity.isEmpty()) {
			p = cb.and(p, cb.equal(root.get(OkrWorkPerson_.processIdentity), identity));
		}
		if (statuses != null && statuses.size() > 0) {
			p = cb.and(p, root.get(OkrWorkPerson_.status).in(statuses));
		}
		return em.createQuery(cq.where(p)).setMaxResults(100).getResultList();
	}

	/**
	 * 根据中心工作ID，工作ID和员工姓名来查询已经存在的工作干系人信息
	 * 
	 * @param centerId
	 * @param workId
	 * @param employeeName
	 * @return
	 * @throws Exception
	 */
	public List<String> listWorkByCenterAndIdentity(String centerId, String employeeIdentity, String identity,
			List<String> statuses) throws Exception {
		if (employeeIdentity == null || employeeIdentity.isEmpty()) {
			logger.warn("employeeIdentity is null!");
			return null;
		}
		EntityManager em = this.entityManagerContainer().get(OkrWorkPerson.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<OkrWorkPerson> root = cq.from(OkrWorkPerson.class);
		cq.select(root.get(OkrWorkPerson_.id));
		Predicate p = cb.equal(root.get(OkrWorkPerson_.employeeIdentity), employeeIdentity);
		if (centerId != null && !centerId.isEmpty()) {
			p = cb.and(p, cb.equal(root.get(OkrWorkPerson_.centerId), centerId));
		}
		p = cb.and(p, cb.isNotNull(root.get(OkrWorkPerson_.workId)));
		if (identity != null && !identity.isEmpty()) {
			p = cb.and(p, cb.equal(root.get(OkrWorkPerson_.processIdentity), identity));
		}
		if (statuses != null && statuses.size() > 0) {
			p = cb.and(p, root.get(OkrWorkPerson_.status).in(statuses));
		}
		return em.createQuery(cq.where(p)).getResultList();
	}

	/**
	 * 根据中心工作ID，工作ID和员工姓名来查询已经存在的工作干系人信息
	 * 
	 * @param centerId
	 * @param workId
	 * @param employeeName
	 * @return
	 * @throws Exception
	 */
	public List<String> listByWorkAndIdentity(String centerId, String workId, String employeeIdentity, String identity,
			List<String> statuses) throws Exception {
		if (employeeIdentity == null || employeeIdentity.isEmpty()) {
			logger.warn("employeeIdentity is null!");
			return null;
		}
		EntityManager em = this.entityManagerContainer().get(OkrWorkPerson.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<OkrWorkPerson> root = cq.from(OkrWorkPerson.class);
		cq.select(root.get(OkrWorkPerson_.id));
		Predicate p = cb.equal(root.get(OkrWorkPerson_.employeeIdentity), employeeIdentity);
		if (centerId != null && !centerId.isEmpty()) {
			p = cb.and(p, cb.equal(root.get(OkrWorkPerson_.centerId), centerId));
		}
		if (workId != null && !workId.isEmpty()) {
			p = cb.and(p, cb.equal(root.get(OkrWorkPerson_.workId), workId));
		}
		if (identity != null && !identity.isEmpty()) {
			p = cb.and(p, cb.equal(root.get(OkrWorkPerson_.processIdentity), identity));
		}
		if (statuses != null && statuses.size() > 0) {
			p = cb.and(p, root.get(OkrWorkPerson_.status).in(statuses));
		}
		return em.createQuery(cq.where(p)).getResultList();
	}

	/**
	 * 根据中心工作ID，工作ID和员工姓名来查询已经存在的工作干系人信息
	 * 
	 * @param centerId
	 * @param workId
	 * @param employeeName
	 * @return
	 * @throws Exception
	 */
	public List<String> listDistinctWorkIdsByWorkAndIdentity(String centerId, String workId, String employeeIdentity,
			String identity, List<String> statuses) throws Exception {
		if (employeeIdentity == null || employeeIdentity.isEmpty()) {
			logger.warn("employeeIdentity is null!");
			return null;
		}
		EntityManager em = this.entityManagerContainer().get(OkrWorkPerson.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<OkrWorkPerson> root = cq.from(OkrWorkPerson.class);
		cq.distinct(true).select(root.get(OkrWorkPerson_.workId));
		Predicate p = cb.equal(root.get(OkrWorkPerson_.employeeIdentity), employeeIdentity);
		p = cb.and(p, cb.isNotNull(root.get(OkrWorkPerson_.workId)));
		if (centerId != null && !centerId.isEmpty()) {
			p = cb.and(p, cb.equal(root.get(OkrWorkPerson_.centerId), centerId));
		}
		if (workId != null && !workId.isEmpty()) {
			p = cb.and(p, cb.equal(root.get(OkrWorkPerson_.workId), workId));
		}
		if (identity != null && !identity.isEmpty()) {
			p = cb.and(p, cb.equal(root.get(OkrWorkPerson_.processIdentity), identity));
		}
		if (statuses != null && statuses.size() > 0) {
			p = cb.and(p, root.get(OkrWorkPerson_.status).in(statuses));
		}
		return em.createQuery(cq.where(p)).getResultList();
	}

	/**
	 * 根据中心工作ID，工作ID来查询已经存在的工作干系人身份
	 * 
	 * @param centerId
	 * @param workId
	 * @param employeeName
	 * @return
	 * @throws Exception
	 */
	public List<String> listUserIndentityByWorkId(String centerId, String workId, String processIdentity,
			List<String> statuses) throws Exception {
		if (centerId == null || centerId.isEmpty()) {
			logger.warn("centerId is null!");
			return null;
		}
		if (workId == null || workId.isEmpty()) {
			logger.warn("workId is null!");
			return null;
		}
		EntityManager em = this.entityManagerContainer().get(OkrWorkPerson.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<OkrWorkPerson> root = cq.from(OkrWorkPerson.class);
		cq.select(root.get(OkrWorkPerson_.employeeIdentity));
		Predicate p = cb.isNotNull(root.get(OkrWorkPerson_.employeeIdentity));
		if (centerId != null && !centerId.isEmpty()) {
			p = cb.and(p, cb.equal(root.get(OkrWorkPerson_.centerId), centerId));
		}
		if (workId != null && !workId.isEmpty()) {
			p = cb.and(p, cb.equal(root.get(OkrWorkPerson_.workId), workId));
		}
		if (processIdentity != null && !processIdentity.isEmpty()) {
			p = cb.and(p, cb.equal(root.get(OkrWorkPerson_.processIdentity), processIdentity));
		}
		if (statuses != null && statuses.size() > 0) {
			p = cb.and(p, root.get(OkrWorkPerson_.status).in(statuses));
		}
		return em.createQuery(cq.where(p)).getResultList();
	}

	/**
	 * 根据工作信息ID，获取工作干系人信息ID列表
	 * 
	 * @param workId
	 * @return
	 * @throws Exception
	 */
	// @MethodDescribe( "根据工作信息ID，获取工作干系人信息ID列表" )
	public List<String> listByWorkId(String workId, List<String> statuses) throws Exception {
		EntityManager em = this.entityManagerContainer().get(OkrWorkPerson.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<OkrWorkPerson> root = cq.from(OkrWorkPerson.class);
		Predicate p = cb.equal(root.get(OkrWorkPerson_.workId), workId);
		if (statuses != null && statuses.size() > 0) {
			p = cb.and(p, root.get(OkrWorkPerson_.status).in(statuses));
		}
		cq.select(root.get(OkrWorkPerson_.id));
		return em.createQuery(cq.where(p)).getResultList();
	}

	/**
	 * 根据工作信息ID，和处理身份, 获取工作干系人信息ID列表
	 * 
	 * @param workId
	 * @return
	 * @throws Exception
	 */
	// @MethodDescribe( "根据工作信息ID，和处理身份, 获取工作干系人信息ID列表" )
	public List<String> listByWorkIdAndProcessIdentity(String workId, String processIdentity, List<String> statuses)
			throws Exception {
		EntityManager em = this.entityManagerContainer().get(OkrWorkPerson.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<OkrWorkPerson> root = cq.from(OkrWorkPerson.class);
		Predicate p = cb.equal(root.get(OkrWorkPerson_.workId), workId);
		if (processIdentity != null && !processIdentity.isEmpty()) {
			p = cb.and(p, cb.equal(root.get(OkrWorkPerson_.processIdentity), processIdentity));
		}
		if (statuses != null && statuses.size() > 0) {
			p = cb.and(p, root.get(OkrWorkPerson_.status).in(statuses));
		}
		cq.select(root.get(OkrWorkPerson_.id));
		return em.createQuery(cq.where(p)).getResultList();
	}

	/**
	 * 根据工作信息ID，获取工作干系人信息ID列表
	 * 
	 * @param workId
	 * @return
	 * @throws Exception
	 */
	// @MethodDescribe( "根据工作信息ID，用户身份，获取工作干系人信息ID列表" )
	public List<String> listByWorkIdAndUserIdentity(String workId, String userIdentity, List<String> statuses)
			throws Exception {
		EntityManager em = this.entityManagerContainer().get(OkrWorkPerson.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<OkrWorkPerson> root = cq.from(OkrWorkPerson.class);
		Predicate p = cb.equal(root.get(OkrWorkPerson_.workId), workId);
		if (userIdentity != null && !userIdentity.isEmpty()) {
			p = cb.and(p, cb.equal(root.get(OkrWorkPerson_.employeeIdentity), userIdentity));
		}
		if (statuses != null && statuses.size() > 0) {
			p = cb.and(p, root.get(OkrWorkPerson_.status).in(statuses));
		}
		cq.select(root.get(OkrWorkPerson_.id));
		return em.createQuery(cq.where(p)).getResultList();
	}

	// @MethodDescribe( "查询中心工作ID和处理人查询与其有关的所有工作ID" )
	public List<String> listByCenterAndPerson(String centerId, String userIdentity, String processIdentity,
			List<String> statuses) throws Exception {
		if (centerId == null || centerId.isEmpty()) {
			throw new Exception("center is null");
		}
		if (userIdentity == null || userIdentity.isEmpty()) {
			throw new Exception("userIdentity is null");
		}
		EntityManager em = this.entityManagerContainer().get(OkrWorkPerson.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<OkrWorkPerson> root = cq.from(OkrWorkPerson.class);
		Predicate p = cb.equal(root.get(OkrWorkPerson_.centerId), centerId);
		p = cb.and(p, cb.equal(root.get(OkrWorkPerson_.employeeIdentity), userIdentity));
		if (processIdentity != null && !processIdentity.isEmpty()) {
			p = cb.and(p, cb.equal(root.get(OkrWorkPerson_.processIdentity), processIdentity));
		}
		if (statuses != null && statuses.size() > 0) {
			p = cb.and(p, root.get(OkrWorkPerson_.status).in(statuses));
		}
		cq.select(root.get(OkrWorkPerson_.id));
		return em.createQuery(cq.where(p)).getResultList();
	}

	/**
	 * 根据中心工作ID，获取该中心工作信息（不包括下级工作信息）所有的干系人信息
	 * 
	 * @param centerId
	 *            中心工作ID
	 * @param processIdentity
	 *            干系人身份，可以为空
	 * @return
	 * @throws Exception
	 */
	// @MethodDescribe( "根据中心工作ID，获取该中心工作信息（不包括下级工作信息）所有的干系人信息" )
	public List<String> listIdsForCenterWorkByCenterId(String centerId, String employeeIdentity, String processIdentity,
			List<String> statuses) throws Exception {
		if (centerId == null || centerId.isEmpty()) {
			throw new Exception("center is null");
		}
		EntityManager em = this.entityManagerContainer().get(OkrWorkPerson.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<OkrWorkPerson> root = cq.from(OkrWorkPerson.class);
		Predicate p = cb.equal(root.get(OkrWorkPerson_.centerId), centerId);
		p = cb.and(p, cb.or(cb.isNull(root.get(OkrWorkPerson_.workId)), cb.equal(root.get(OkrWorkPerson_.workId), "")));
		if (employeeIdentity != null && !employeeIdentity.isEmpty()) {
			p = cb.and(p, cb.equal(root.get(OkrWorkPerson_.employeeIdentity), employeeIdentity));
		}
		if (processIdentity != null && !processIdentity.isEmpty()) {
			p = cb.and(p, cb.equal(root.get(OkrWorkPerson_.processIdentity), processIdentity));
		}
		if (statuses != null && statuses.size() > 0) {
			p = cb.and(p, root.get(OkrWorkPerson_.status).in(statuses));
		}
		cq.select(root.get(OkrWorkPerson_.id));
		return em.createQuery(cq.where(p)).getResultList();
	}

	/**
	 * 根据中心工作ID，获取该中心工作信息（不包括下级工作信息）所有的干系人身份
	 * 
	 * @param centerId
	 *            中心工作ID
	 * @param processIdentity
	 *            干系人身份，可以为空
	 * @return
	 * @throws Exception
	 */
	// @MethodDescribe( "根据中心工作ID，获取该中心工作信息（不包括下级工作信息）所有的干系人信息" )
	public List<String> listUserIdentityForCenterWork(String centerId, String processIdentity, List<String> statuses)
			throws Exception {
		if (centerId == null || centerId.isEmpty()) {
			throw new Exception("center is null");
		}
		EntityManager em = this.entityManagerContainer().get(OkrWorkPerson.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<OkrWorkPerson> root = cq.from(OkrWorkPerson.class);
		Predicate p = cb.equal(root.get(OkrWorkPerson_.centerId), centerId);
		p = cb.and(p, cb.or(cb.isNull(root.get(OkrWorkPerson_.workId)), cb.equal(root.get(OkrWorkPerson_.workId), "")));
		if (processIdentity != null && !processIdentity.isEmpty()) {
			p = cb.and(p, cb.equal(root.get(OkrWorkPerson_.processIdentity), processIdentity));
		}
		if (statuses != null && statuses.size() > 0) {
			p = cb.and(p, root.get(OkrWorkPerson_.status).in(statuses));
		}
		cq.select(root.get(OkrWorkPerson_.employeeIdentity));
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
	public List<String> listByCenterWorkId(String centerId, List<String> statuses) throws Exception {
		if (centerId == null || centerId.isEmpty()) {
			throw new Exception(" centerId is null!");
		}
		EntityManager em = this.entityManagerContainer().get(OkrWorkPerson.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<OkrWorkPerson> root = cq.from(OkrWorkPerson.class);
		Predicate p = cb.equal(root.get(OkrWorkPerson_.centerId), centerId);
		if (statuses != null && statuses.size() > 0) {
			p = cb.and(p, root.get(OkrWorkPerson_.status).in(statuses));
		}
		cq.select(root.get(OkrWorkPerson_.id));
		return em.createQuery(cq.where(p)).getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<OkrWorkPerson> listNextWithFilter(String id, Integer count, Object sequence,
			WorkCommonSearchFilter wrapIn) throws Exception {
		// 先获取上一页最后一条的sequence值，如果有值的话，以此sequence值作为依据取后续的count条数据
		EntityManager em = this.entityManagerContainer().get(OkrWorkPerson.class);
		String order = wrapIn.getOrder(); // 排序方式
		List<Object> vs = new ArrayList<>();
		StringBuffer sql_stringBuffer = new StringBuffer();
		Integer index = 1;
		if (order == null || order.isEmpty()) {
			order = "DESC";
		}
		sql_stringBuffer.append("SELECT o FROM " + OkrWorkPerson.class.getCanonicalName() + " o where 1=1");
		if ((null != sequence)) {
			sql_stringBuffer.append(" and o." + wrapIn.getSequenceField() + " "
					+ (StringUtils.equalsIgnoreCase(order, "DESC") ? "<" : ">") + (" ?" + (index)));
			vs.add(sequence);
			index++;
		}
		// 对象类别，是中心工作，还是普通工作，如果是中心工作，那么普通的工作ID应该是为空的
		if (null != wrapIn.getInfoType() && "CENTERWORK".equals(wrapIn.getInfoType())) {
			sql_stringBuffer.append(" and o.recordType = '中心工作' ");
			if (null != wrapIn.getWorkTitle() && !wrapIn.getWorkTitle().isEmpty()) {
				sql_stringBuffer.append(" and o.centerTitle like  ?" + (index));
				vs.add("%" + wrapIn.getWorkTitle() + "%");
				index++;
			}
		} else {
			sql_stringBuffer.append(" and o.recordType = '具体工作' ");
			if (null != wrapIn.getWorkTitle() && !wrapIn.getWorkTitle().isEmpty()) {
				sql_stringBuffer.append(" and o.workTitle like  ?" + (index));
				vs.add("%" + wrapIn.getWorkTitle() + "%");
				index++;
			}
		}
		// 干系人姓名列表
		if ((null != wrapIn.getEmployeeNames()) && wrapIn.getEmployeeNames().size() > 0) {
			sql_stringBuffer.append(" and o.employeeName in ( ?" + (index) + " )");
			vs.add(wrapIn.getEmployeeNames());
			index++;
		}
		// 干系人姓名列表
		if ((null != wrapIn.getEmployeeIdentities()) && wrapIn.getEmployeeIdentities().size() > 0) {
			sql_stringBuffer.append(" and o.employeeIdentity in ( ?" + (index) + " )");
			vs.add(wrapIn.getEmployeeIdentities());
			index++;
		}
		// 处理身份
		if (null != wrapIn.getProcessIdentities() && wrapIn.getProcessIdentities().size() > 0) {
			sql_stringBuffer.append(" and o.processIdentity in ( ?" + (index) + " )");
			vs.add(wrapIn.getProcessIdentities());
			index++;
		}
		// 干系人所属组织名称列表
		if (null != wrapIn.getUnitNames() && wrapIn.getUnitNames().size() > 0) {
			sql_stringBuffer.append(" and o.unitName in ( ?" + (index) + " )");
			vs.add(wrapIn.getUnitNames());
			index++;
		}
		// 干系人所属顶层组织名称列表
		if ((null != wrapIn.getTopUnitNames()) && wrapIn.getTopUnitNames().size() > 0) {
			sql_stringBuffer.append(" and o.topUnitName in ( ?" + (index) + " )");
			vs.add(wrapIn.getTopUnitNames());
			index++;
		}
		// 工作类别
		if (null != wrapIn.getWorkTypes() && wrapIn.getWorkTypes().size() > 0) {
			sql_stringBuffer.append(" and o.workType in ( ?" + (index) + " )");
			vs.add(wrapIn.getWorkTypes());
			index++;
		}
		// 部署年份
		if (null != wrapIn.getDeployYear() && !wrapIn.getDeployYear().isEmpty()) {
			sql_stringBuffer.append(" and o.deployYear = ?" + (index));
			vs.add(wrapIn.getDeployYear());
			index++;
		}
		// 部署月份
		if (null != wrapIn.getDeployMonth() && !wrapIn.getDeployMonth().isEmpty()) {
			sql_stringBuffer.append(" and o.deployMonth = ?" + (index));
			vs.add(wrapIn.getDeployMonth());
			index++;
		}
		// 工作时长类型：短期工作|长期工作
		if (null != wrapIn.getWorkDateTimeType() && wrapIn.getWorkDateTimeType().isEmpty()) {
			sql_stringBuffer.append(" and o.workDateTimeType = ?" + (index));
			vs.add(wrapIn.getWorkDateTimeType());
			index++;
		}
		// 工作处理状态
		if (null != wrapIn.getWorkProcessStatuses() && wrapIn.getWorkProcessStatuses().size() > 0) {
			sql_stringBuffer.append(" and o.workProcessStatus in ( ?" + (index) + " )");
			vs.add(wrapIn.getWorkProcessStatuses());
			index++;
		}
		if (null != wrapIn.getInfoStatuses() && wrapIn.getInfoStatuses().size() > 0) {
			sql_stringBuffer.append(" and o.status in ( ?" + (index) + " )");
			vs.add(wrapIn.getInfoStatuses());
			index++;
		}
		sql_stringBuffer.append(" order by o." + wrapIn.getSequenceField() + " "
				+ (StringUtils.equalsIgnoreCase(order, "DESC") ? "DESC" : "ASC"));

		Query query = em.createQuery(sql_stringBuffer.toString(), OkrWorkPerson.class);
		// 为查询设置所有的参数值
		for (int i = 0; i < vs.size(); i++) {
			query.setParameter(i + 1, vs.get(i));
		}
		return query.setMaxResults(count).getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<OkrWorkPerson> listPrevWithFilter(String id, Integer count, Object sequence,
			WorkCommonSearchFilter wrapIn) throws Exception {
		// 先获取上一页最后一条的sequence值，如果有值的话，以此sequence值作为依据取后续的count条数据
		EntityManager em = this.entityManagerContainer().get(OkrWorkPerson.class);
		String order = wrapIn.getOrder(); // 排序方式
		List<Object> vs = new ArrayList<>();
		StringBuffer sql_stringBuffer = new StringBuffer();
		Integer index = 1;
		if (order == null || order.isEmpty()) {
			order = "DESC";
		}
		sql_stringBuffer.append("SELECT o FROM " + OkrWorkPerson.class.getCanonicalName() + " o where 1=1");
		if ((null != sequence)) {
			sql_stringBuffer.append(" and o." + wrapIn.getSequenceField() + " "
					+ (StringUtils.equalsIgnoreCase(order, "DESC") ? ">" : "<") + (" ?" + (index)));
			vs.add(sequence);
			index++;
		}
		// 对象类别，是中心工作，还是普通工作，如果是中心工作，那么普通的工作ID应该是为空的
		if (null != wrapIn.getInfoType() && "CENTERWORK".equals(wrapIn.getInfoType())) {
			sql_stringBuffer.append(" and o.recordType = '中心工作' ");
			if (null != wrapIn.getWorkTitle() && !wrapIn.getWorkTitle().isEmpty()) {
				sql_stringBuffer.append(" and o.centerTitle like  ?" + (index));
				vs.add("%" + wrapIn.getWorkTitle() + "%");
				index++;
			}
		} else {
			sql_stringBuffer.append(" and o.recordType = '具体工作' ");
			if (null != wrapIn.getWorkTitle() && !wrapIn.getWorkTitle().isEmpty()) {
				sql_stringBuffer.append(" and o.workTitle like  ?" + (index));
				vs.add("%" + wrapIn.getWorkTitle() + "%");
				index++;
			}
		}
		// 干系人姓名列表
		if ((null != wrapIn.getEmployeeNames()) && wrapIn.getEmployeeNames().size() > 0) {
			sql_stringBuffer.append(" and o.employeeName in ( ?" + (index) + " )");
			vs.add(wrapIn.getEmployeeNames());
			index++;
		}
		// 干系人姓名列表
		if ((null != wrapIn.getEmployeeIdentities()) && wrapIn.getEmployeeIdentities().size() > 0) {
			sql_stringBuffer.append(" and o.employeeIdentity in ( ?" + (index) + " )");
			vs.add(wrapIn.getEmployeeIdentities());
			index++;
		}
		// 处理身份
		if (null != wrapIn.getProcessIdentities() && wrapIn.getProcessIdentities().size() > 0) {
			sql_stringBuffer.append(" and o.processIdentity in ( ?" + (index) + " )");
			vs.add(wrapIn.getProcessIdentities());
			index++;
		}
		// 干系人所属组织名称列表
		if (null != wrapIn.getUnitNames() && wrapIn.getUnitNames().size() > 0) {
			sql_stringBuffer.append(" and o.unitName in ( ?" + (index) + " )");
			vs.add(wrapIn.getUnitNames());
			index++;
		}
		// 干系人所属顶层组织名称列表
		if ((null != wrapIn.getTopUnitNames()) && wrapIn.getTopUnitNames().size() > 0) {
			sql_stringBuffer.append(" and o.topUnitName in ( ?" + (index) + " )");
			vs.add(wrapIn.getTopUnitNames());
			index++;
		}
		// 工作类别
		if (null != wrapIn.getWorkTypes() && wrapIn.getWorkTypes().size() > 0) {
			sql_stringBuffer.append(" and o.workType in ( ?" + (index) + " )");
			vs.add(wrapIn.getWorkTypes());
			index++;
		}
		// 部署年份
		if (null != wrapIn.getDeployYear() && !wrapIn.getDeployYear().isEmpty()) {
			sql_stringBuffer.append(" and o.deployYear = ?" + (index));
			vs.add(wrapIn.getDeployYear());
			index++;
		}
		// 部署月份
		if (null != wrapIn.getDeployMonth() && !wrapIn.getDeployMonth().isEmpty()) {
			sql_stringBuffer.append(" and o.deployMonth = ?" + (index));
			vs.add(wrapIn.getDeployMonth());
			index++;
		}
		// 工作时长类型：短期工作|长期工作
		if (null != wrapIn.getWorkDateTimeType() && wrapIn.getWorkDateTimeType().isEmpty()) {
			sql_stringBuffer.append(" and o.workDateTimeType = ?" + (index));
			vs.add(wrapIn.getWorkDateTimeType());
			index++;
		}
		// 工作处理状态
		if (null != wrapIn.getWorkProcessStatuses() && wrapIn.getWorkProcessStatuses().size() > 0) {
			sql_stringBuffer.append(" and o.workProcessStatus in ( ?" + (index) + " )");
			vs.add(wrapIn.getWorkProcessStatuses());
			index++;
		}
		if (null != wrapIn.getInfoStatuses() && wrapIn.getInfoStatuses().size() > 0) {
			sql_stringBuffer.append(" and o.status in ( ?" + (index) + " )");
			vs.add(wrapIn.getInfoStatuses());
			index++;
		}
		sql_stringBuffer.append(" order by o." + wrapIn.getSequenceField() + " "
				+ (StringUtils.equalsIgnoreCase(order, "DESC") ? "DESC" : "ASC"));
		Query query = em.createQuery(sql_stringBuffer.toString(), OkrWorkPerson.class);
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
	public long getCountWithFilter(WorkCommonSearchFilter wrapIn) throws Exception {
		// 先获取上一页最后一条的sequence值，如果有值的话，以此sequence值作为依据取后续的count条数据
		EntityManager em = this.entityManagerContainer().get(OkrWorkPerson.class);
		List<Object> vs = new ArrayList<>();
		StringBuffer sql_stringBuffer = new StringBuffer();
		Integer index = 1;

		sql_stringBuffer.append("SELECT count(o.id) FROM " + OkrWorkPerson.class.getCanonicalName() + " o where 1=1");

		// 对象类别，是中心工作，还是普通工作，如果是中心工作，那么普通的工作ID应该是为空的
		if (null != wrapIn.getInfoType() && "CENTERWORK".equals(wrapIn.getInfoType())) {
			sql_stringBuffer.append(" and o.recordType = '中心工作' ");
			if (null != wrapIn.getWorkTitle() && !wrapIn.getWorkTitle().isEmpty()) {
				sql_stringBuffer.append(" and o.centerTitle like  ?" + (index));
				vs.add("%" + wrapIn.getWorkTitle() + "%");
				index++;
			}
		} else {
			sql_stringBuffer.append(" and o.recordType = '具体工作' ");
			if (null != wrapIn.getWorkTitle() && !wrapIn.getWorkTitle().isEmpty()) {
				sql_stringBuffer.append(" and o.workTitle like  ?" + (index));
				vs.add("%" + wrapIn.getWorkTitle() + "%");
				index++;
			}
		}
		// 干系人姓名列表
		if ((null != wrapIn.getEmployeeNames()) && wrapIn.getEmployeeNames().size() > 0) {
			sql_stringBuffer.append(" and o.employeeName in ( ?" + (index) + " )");
			vs.add(wrapIn.getEmployeeNames());
			index++;
		}
		// 干系人姓名列表
		if ((null != wrapIn.getEmployeeIdentities()) && wrapIn.getEmployeeIdentities().size() > 0) {
			sql_stringBuffer.append(" and o.employeeIdentity in ( ?" + (index) + " )");
			vs.add(wrapIn.getEmployeeIdentities());
			index++;
		}
		// 处理身份
		if (null != wrapIn.getProcessIdentities() && wrapIn.getProcessIdentities().size() > 0) {
			sql_stringBuffer.append(" and o.processIdentity in ( ?" + (index) + " )");
			vs.add(wrapIn.getProcessIdentities());
			index++;
		}
		// 干系人所属组织名称列表
		if (null != wrapIn.getUnitNames() && wrapIn.getUnitNames().size() > 0) {
			sql_stringBuffer.append(" and o.unitName in ( ?" + (index) + " )");
			vs.add(wrapIn.getUnitNames());
			index++;
		}
		// 干系人所属顶层组织名称列表
		if ((null != wrapIn.getTopUnitNames()) && wrapIn.getTopUnitNames().size() > 0) {
			sql_stringBuffer.append(" and o.topUnitName in ( ?" + (index) + " )");
			vs.add(wrapIn.getTopUnitNames());
			index++;
		}
		// 工作类别
		if (null != wrapIn.getWorkTypes() && wrapIn.getWorkTypes().size() > 0) {
			sql_stringBuffer.append(" and o.workType in ( ?" + (index) + " )");
			vs.add(wrapIn.getWorkTypes());
			index++;
		}
		// 部署年份
		if (null != wrapIn.getDeployYear() && !wrapIn.getDeployYear().isEmpty()) {
			sql_stringBuffer.append(" and o.deployYear = ?" + (index));
			vs.add(wrapIn.getDeployYear());
			index++;
		}
		// 部署月份
		if (null != wrapIn.getDeployMonth() && !wrapIn.getDeployMonth().isEmpty()) {
			sql_stringBuffer.append(" and o.deployMonth = ?" + (index));
			vs.add(wrapIn.getDeployMonth());
			index++;
		}
		// 工作时长类型：短期工作|长期工作
		if (null != wrapIn.getWorkDateTimeType() && wrapIn.getWorkDateTimeType().isEmpty()) {
			sql_stringBuffer.append(" and o.workDateTimeType = ?" + (index));
			vs.add(wrapIn.getWorkDateTimeType());
			index++;
		}
		// 工作处理状态
		if (null != wrapIn.getWorkProcessStatuses() && wrapIn.getWorkProcessStatuses().size() > 0) {
			sql_stringBuffer.append(" and o.workProcessStatus in ( ?" + (index) + " )");
			vs.add(wrapIn.getWorkProcessStatuses());
			index++;
		}
		if (null != wrapIn.getInfoStatuses() && wrapIn.getInfoStatuses().size() > 0) {
			sql_stringBuffer.append(" and o.status in ( ?" + (index) + " )");
			vs.add(wrapIn.getInfoStatuses());
			index++;
		}
		Query query = em.createQuery(sql_stringBuffer.toString(), OkrWorkPerson.class);
		for (int i = 0; i < vs.size(); i++) {
			query.setParameter(i + 1, vs.get(i));
		}
		return (Long) query.getSingleResult();
	}

	public List<String> listDistinctIdentity() throws Exception {
		EntityManager em = this.entityManagerContainer().get(OkrWorkPerson.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<OkrWorkPerson> root = cq.from(OkrWorkPerson.class);
		cq.distinct(true).select(root.get(OkrWorkPerson_.employeeIdentity));
		return em.createQuery(cq).setMaxResults(10000).getResultList();
	}

	/**
	 * 根据工作类别和登录身份来查询用户可以访问到的所有中心工作数量
	 * 
	 * @param workTypeName
	 * @param loginIdentity
	 * @return
	 * @throws Exception
	 */
	public List<String> listCenterWorkIdsByWorkType(List<String> workTypeNames, String loginIdentity,
			String processIdentity) throws Exception {
		EntityManager em = this.entityManagerContainer().get(OkrWorkPerson.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<OkrWorkPerson> root = cq.from(OkrWorkPerson.class);
		Predicate p = cb.isNotNull(root.get(OkrWorkPerson_.id));
		if (workTypeNames != null && !workTypeNames.isEmpty()) {
			p = cb.and(p, root.get(OkrWorkPerson_.workType).in(workTypeNames));
		}
		if (loginIdentity != null && !loginIdentity.isEmpty()) {
			p = cb.and(p, cb.equal(root.get(OkrWorkPerson_.employeeIdentity), loginIdentity));
		}
		if (processIdentity != null && !processIdentity.isEmpty()) {
			p = cb.and(p, cb.equal(root.get(OkrWorkPerson_.processIdentity), processIdentity));
		}
		cq.distinct(true).select(root.get(OkrWorkPerson_.centerId));
		return em.createQuery(cq.where(p)).getResultList();
	}

	public List<String> listByAuthorizeRecordIds(List<String> authorizeRecordIds, List<String> statuses)
			throws Exception {
		if (authorizeRecordIds == null || authorizeRecordIds.isEmpty()) {
			logger.warn("authorizeRecordIds is null!");
			return null;
		}
		EntityManager em = this.entityManagerContainer().get(OkrWorkPerson.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<OkrWorkPerson> root = cq.from(OkrWorkPerson.class);
		cq.select(root.get(OkrWorkPerson_.id));

		Predicate p = root.get(OkrWorkPerson_.authorizeRecordId).in(authorizeRecordIds);
		if (statuses != null && statuses.size() > 0) {
			p = cb.and(p, root.get(OkrWorkPerson_.status).in(statuses));
		}
		return em.createQuery(cq.where(p)).setMaxResults(1000).getResultList();
	}

	public Long getProcessingWorkCountByCenterId(String identity, List<String> status, String processIdentity)
			throws Exception {
		if (identity == null || identity.isEmpty()) {
			throw new Exception("identity is null.");
		}
		if (status == null || status.isEmpty()) {
			throw new Exception("status is null.");
		}
		EntityManager em = this.entityManagerContainer().get(OkrWorkPerson.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<OkrWorkPerson> root = cq.from(OkrWorkPerson.class);
		Predicate p = root.get(OkrWorkPerson_.status).in(status);
		p = cb.and(p, cb.equal(root.get(OkrWorkPerson_.employeeIdentity), identity));
		p = cb.and(p, cb.equal(root.get(OkrWorkPerson_.workProcessStatus), "执行中"));
		if (processIdentity != null && !processIdentity.isEmpty()) {
			p = cb.and(p, cb.equal(root.get(OkrWorkPerson_.processIdentity), processIdentity));
		}
		// 查询总数
		cq.select(cb.count(root));
		// logger.info( ">>>>getProcessingWorkCountByCenterId-SQL:" +
		// em.createQuery(cq.where(p)).toString() );
		return em.createQuery(cq.where(p)).getSingleResult();
	}

	public Long getWorkTotalByCenterId(String identity, List<String> status, String processIdentity) throws Exception {
		if (identity == null || identity.isEmpty()) {
			throw new Exception("identity is null.");
		}
		if (status == null || status.isEmpty()) {
			throw new Exception("status is null.");
		}
		EntityManager em = this.entityManagerContainer().get(OkrWorkPerson.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<OkrWorkPerson> root = cq.from(OkrWorkPerson.class);
		Predicate p = root.get(OkrWorkPerson_.status).in(status);
		p = cb.and(p, cb.equal(root.get(OkrWorkPerson_.employeeIdentity), identity));
		if (processIdentity != null && !processIdentity.isEmpty()) {
			p = cb.and(p, cb.equal(root.get(OkrWorkPerson_.processIdentity), processIdentity));
		}
		// 查询总数
		cq.select(cb.count(root));
		return em.createQuery(cq.where(p)).getSingleResult();
	}

	public Long getCompletedWorkCountByCenterId(String identity, List<String> status, String processIdentity)
			throws Exception {
		if (identity == null || identity.isEmpty()) {
			throw new Exception("identity is null.");
		}
		if (status == null || status.isEmpty()) {
			throw new Exception("status is null.");
		}
		EntityManager em = this.entityManagerContainer().get(OkrWorkPerson.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<OkrWorkPerson> root = cq.from(OkrWorkPerson.class);
		Predicate p = root.get(OkrWorkPerson_.status).in(status);
		p = cb.and(p, cb.equal(root.get(OkrWorkPerson_.employeeIdentity), identity));
		p = cb.and(p, cb.equal(root.get(OkrWorkPerson_.workProcessStatus), "已完成"));
		if (processIdentity != null && !processIdentity.isEmpty()) {
			p = cb.and(p, cb.equal(root.get(OkrWorkPerson_.processIdentity), processIdentity));
		}
		// 查询总数
		cq.select(cb.count(root));
		// logger.info( ">>>>getCompletedWorkCountByCenterId-SQL:" +
		// em.createQuery(cq.where(p)).toString() );
		return em.createQuery(cq.where(p)).getSingleResult();
	}

	public Long getOvertimeWorkCountByCenterId(String identity, List<String> status, String processIdentity)
			throws Exception {
		if (identity == null || identity.isEmpty()) {
			throw new Exception("identity is null.");
		}
		if (status == null || status.isEmpty()) {
			throw new Exception("status is null.");
		}
		EntityManager em = this.entityManagerContainer().get(OkrWorkPerson.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<OkrWorkPerson> root = cq.from(OkrWorkPerson.class);
		Predicate p = root.get(OkrWorkPerson_.status).in(status);
		p = cb.and(p, cb.equal(root.get(OkrWorkPerson_.employeeIdentity), identity));
		p = cb.and(p, cb.isTrue(root.get(OkrWorkPerson_.isOverTime)));
		if (processIdentity != null && !processIdentity.isEmpty()) {
			p = cb.and(p, cb.equal(root.get(OkrWorkPerson_.processIdentity), processIdentity));
		}
		// 查询总数
		cq.select(cb.count(root));
		return em.createQuery(cq.where(p)).getSingleResult();
	}

	public Long getOvertimenessWorkCountByCenterId(String identity, List<String> status, String processIdentity)
			throws Exception {
		if (identity == null || identity.isEmpty()) {
			throw new Exception("identity is null.");
		}
		if (status == null || status.isEmpty()) {
			throw new Exception("status is null.");
		}
		EntityManager em = this.entityManagerContainer().get(OkrWorkPerson.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<OkrWorkPerson> root = cq.from(OkrWorkPerson.class);
		Predicate p = root.get(OkrWorkPerson_.status).in(status);
		p = cb.and(p, cb.equal(root.get(OkrWorkPerson_.employeeIdentity), identity));
		p = cb.and(p, cb.isFalse(root.get(OkrWorkPerson_.isOverTime)));
		if (processIdentity != null && !processIdentity.isEmpty()) {
			p = cb.and(p, cb.equal(root.get(OkrWorkPerson_.processIdentity), processIdentity));
		}
		// 查询总数
		cq.select(cb.count(root));
		return em.createQuery(cq.where(p)).getSingleResult();
	}

	public Long getDraftWorkCountByCenterId(String identity, List<String> status, String processIdentity)
			throws Exception {
		if (identity == null || identity.isEmpty()) {
			throw new Exception("identity is null.");
		}
		if (status == null || status.isEmpty()) {
			throw new Exception("status is null.");
		}
		EntityManager em = this.entityManagerContainer().get(OkrWorkPerson.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<OkrWorkPerson> root = cq.from(OkrWorkPerson.class);
		Predicate p = root.get(OkrWorkPerson_.status).in(status);
		p = cb.and(p, cb.equal(root.get(OkrWorkPerson_.employeeIdentity), identity));
		p = cb.and(p, cb.equal(root.get(OkrWorkPerson_.workProcessStatus), "草稿"));
		if (processIdentity != null && !processIdentity.isEmpty()) {
			p = cb.and(p, cb.equal(root.get(OkrWorkPerson_.processIdentity), processIdentity));
		}
		// 查询总数
		cq.select(cb.count(root));
		return em.createQuery(cq.where(p)).getSingleResult();
	}

	public List<OkrWorkPerson> listCenterWorkPerson(String id, WorkCommonSearchFilter wrapIn) throws Exception {
		if (id == null || id.isEmpty()) {
			throw new Exception("id is null!");
		}
		EntityManager em = this.entityManagerContainer().get(OkrWorkPerson.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<OkrWorkPerson> cq = cb.createQuery(OkrWorkPerson.class);
		Root<OkrWorkPerson> root = cq.from(OkrWorkPerson.class);
		Predicate p = cb.equal(root.get(OkrWorkPerson_.recordType), "中心工作");
		p = cb.and(p, cb.equal(root.get(OkrWorkPerson_.centerId), id));
		// 干系人姓名列表
		if (null != wrapIn.getEmployeeNames() && wrapIn.getEmployeeNames().size() > 0) {
			p = cb.and(p, root.get(OkrWorkPerson_.employeeName).in(wrapIn.getEmployeeNames()));
		}
		// 干系人姓名列表
		if (null != wrapIn.getEmployeeIdentities() && wrapIn.getEmployeeIdentities().size() > 0) {
			p = cb.and(p, root.get(OkrWorkPerson_.employeeIdentity).in(wrapIn.getEmployeeIdentities()));
		}
		// 处理身份
		if (null != wrapIn.getProcessIdentities() && wrapIn.getProcessIdentities().size() > 0) {
			p = cb.and(p, root.get(OkrWorkPerson_.processIdentity).in(wrapIn.getProcessIdentities()));
		}
		// 干系人所属组织名称列表
		if (null != wrapIn.getUnitNames() && wrapIn.getUnitNames().size() > 0) {
			p = cb.and(p, root.get(OkrWorkPerson_.unitName).in(wrapIn.getUnitNames()));
		}
		// 干系人所属顶层组织名称列表
		if (null != wrapIn.getTopUnitNames() && wrapIn.getTopUnitNames().size() > 0) {
			p = cb.and(p, root.get(OkrWorkPerson_.topUnitName).in(wrapIn.getTopUnitNames()));
		}
		// 工作类别
		if (null != wrapIn.getWorkTypes() && wrapIn.getWorkTypes().size() > 0) {
			p = cb.and(p, root.get(OkrWorkPerson_.workType).in(wrapIn.getWorkTypes()));
		}
		// 工作时长类型：短期工作|长期工作
		if (null != wrapIn.getWorkDateTimeType() && wrapIn.getWorkDateTimeType().isEmpty()) {
			p = cb.and(p, cb.equal(root.get(OkrWorkPerson_.workDateTimeType), wrapIn.getWorkDateTimeType()));
		}
		// 工作处理状态
		if (null != wrapIn.getWorkProcessStatuses() && wrapIn.getWorkProcessStatuses().size() > 0) {
			p = cb.and(p, root.get(OkrWorkPerson_.workProcessStatus).in(wrapIn.getWorkProcessStatuses()));
		}
		if (null != wrapIn.getInfoStatuses() && wrapIn.getInfoStatuses().size() > 0) {
			p = cb.and(p, root.get(OkrWorkPerson_.status).in(wrapIn.getInfoStatuses()));
		}
		// 部署年份
		if (null != wrapIn.getDeployYear() && !wrapIn.getDeployYear().isEmpty()) {
			p = cb.and(p, cb.equal(root.get(OkrWorkPerson_.deployYear), wrapIn.getDeployYear()));
		}
		// 部署月份
		if (null != wrapIn.getDeployMonth() && !wrapIn.getDeployMonth().isEmpty()) {
			p = cb.and(p, cb.equal(root.get(OkrWorkPerson_.deployMonth), wrapIn.getDeployMonth()));
		}

		return em.createQuery(cq.where(p)).getResultList();
	}

	public List<OkrWorkPerson> listDetailWorkPerson(String id, WorkCommonSearchFilter wrapIn) throws Exception {
		if (id == null || id.isEmpty()) {
			throw new Exception("id is null!");
		}
		EntityManager em = this.entityManagerContainer().get(OkrWorkPerson.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<OkrWorkPerson> cq = cb.createQuery(OkrWorkPerson.class);
		Root<OkrWorkPerson> root = cq.from(OkrWorkPerson.class);
		Predicate p = cb.equal(root.get(OkrWorkPerson_.recordType), "具体工作");
		p = cb.and(p, cb.equal(root.get(OkrWorkPerson_.workId), id));
		// 干系人姓名列表
		if (null != wrapIn.getEmployeeNames() && wrapIn.getEmployeeNames().size() > 0) {
			p = cb.and(p, root.get(OkrWorkPerson_.employeeName).in(wrapIn.getEmployeeNames()));
		}
		// 干系人姓名列表
		if (null != wrapIn.getEmployeeIdentities() && wrapIn.getEmployeeIdentities().size() > 0) {
			p = cb.and(p, root.get(OkrWorkPerson_.employeeIdentity).in(wrapIn.getEmployeeIdentities()));
		}
		// 处理身份
		if (null != wrapIn.getProcessIdentities() && wrapIn.getProcessIdentities().size() > 0) {
			p = cb.and(p, root.get(OkrWorkPerson_.processIdentity).in(wrapIn.getProcessIdentities()));
		}
		// 干系人所属组织名称列表
		if (null != wrapIn.getUnitNames() && wrapIn.getUnitNames().size() > 0) {
			p = cb.and(p, root.get(OkrWorkPerson_.unitName).in(wrapIn.getUnitNames()));
		}
		// 干系人所属顶层组织名称列表
		if (null != wrapIn.getTopUnitNames() && wrapIn.getTopUnitNames().size() > 0) {
			p = cb.and(p, root.get(OkrWorkPerson_.topUnitName).in(wrapIn.getTopUnitNames()));
		}
		// 工作类别
		if (null != wrapIn.getWorkTypes() && wrapIn.getWorkTypes().size() > 0) {
			p = cb.and(p, root.get(OkrWorkPerson_.workType).in(wrapIn.getWorkTypes()));
		}
		// 工作时长类型：短期工作|长期工作
		if (null != wrapIn.getWorkDateTimeType() && wrapIn.getWorkDateTimeType().isEmpty()) {
			p = cb.and(p, cb.equal(root.get(OkrWorkPerson_.workDateTimeType), wrapIn.getWorkDateTimeType()));
		}
		// 工作处理状态
		if (null != wrapIn.getWorkProcessStatuses() && wrapIn.getWorkProcessStatuses().size() > 0) {
			p = cb.and(p, root.get(OkrWorkPerson_.workProcessStatus).in(wrapIn.getWorkProcessStatuses()));
		}
		if (null != wrapIn.getInfoStatuses() && wrapIn.getInfoStatuses().size() > 0) {
			p = cb.and(p, root.get(OkrWorkPerson_.status).in(wrapIn.getInfoStatuses()));
		}
		// 部署年份
		if (null != wrapIn.getDeployYear() && !wrapIn.getDeployYear().isEmpty()) {
			p = cb.and(p, cb.equal(root.get(OkrWorkPerson_.deployYear), wrapIn.getDeployYear()));
		}
		// 部署月份
		if (null != wrapIn.getDeployMonth() && !wrapIn.getDeployMonth().isEmpty()) {
			p = cb.and(p, cb.equal(root.get(OkrWorkPerson_.deployMonth), wrapIn.getDeployMonth()));
		}

		return em.createQuery(cq.where(p)).getResultList();
	}

	/**
	 * 查询工作干系人身份列表（去重复）
	 * 
	 * @param identities_ok
	 *            排除身份
	 * @param identities_error
	 *            排除身份
	 * @return
	 * @throws Exception
	 */
	public List<String> listAllDistinctEmployeeIdentity(List<String> identities_ok, List<String> identities_error)
			throws Exception {
		EntityManager em = this.entityManagerContainer().get(OkrWorkPerson.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<OkrWorkPerson> root = cq.from(OkrWorkPerson.class);

		Predicate p = cb.isNotNull(root.get(OkrWorkPerson_.id));
		if (identities_ok != null && identities_ok.size() > 0) {
			p = cb.and(p, cb.not(root.get(OkrWorkPerson_.employeeIdentity).in(identities_ok)));
		}
		if (identities_error != null && identities_error.size() > 0) {
			p = cb.and(p, cb.not(root.get(OkrWorkPerson_.employeeIdentity).in(identities_error)));
		}
		cq.distinct(true).select(root.get(OkrWorkPerson_.employeeIdentity));
		return em.createQuery(cq.where(p)).getResultList();
	}

	/**
	 * 根据身份名称，从工作干系人信息中查询与该身份有关的所有信息列表
	 * 
	 * @param identity
	 * @param recordId
	 * @return
	 * @throws Exception
	 */
	public List<OkrWorkPerson> listErrorIdentitiesInWorkPerson(String identity, String recordId) throws Exception {
		EntityManager em = this.entityManagerContainer().get(OkrWorkPerson.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<OkrWorkPerson> cq = cb.createQuery(OkrWorkPerson.class);
		Root<OkrWorkPerson> root = cq.from(OkrWorkPerson.class);
		Predicate p = cb.isNotNull(root.get(OkrWorkPerson_.id));

		if (recordId != null && !recordId.isEmpty() && !"all".equals(recordId)) {
			p = cb.and(p, cb.equal(root.get(OkrWorkPerson_.id), recordId));
		}

		Predicate p_employeeIdentity = cb.isNotNull(root.get(OkrWorkPerson_.employeeIdentity));
		p_employeeIdentity = cb.and(p_employeeIdentity, cb.equal(root.get(OkrWorkPerson_.employeeIdentity), identity));

		p = cb.and(p, p_employeeIdentity);

		return em.createQuery(cq.where(p)).getResultList();
	}

	public List<String> listAllDistinctIdentityWithWorkId(String workId) throws Exception {
		EntityManager em = this.entityManagerContainer().get(OkrWorkPerson.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<OkrWorkPerson> root = cq.from(OkrWorkPerson.class);
		Predicate p = cb.equal( root.get(OkrWorkPerson_.workId), workId);
		cq.distinct(true).select(root.get(OkrWorkPerson_.employeeIdentity));
		return em.createQuery(cq.where(p)).getResultList();
	}
}
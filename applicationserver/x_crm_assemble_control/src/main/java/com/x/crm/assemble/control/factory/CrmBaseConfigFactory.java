package com.x.crm.assemble.control.factory;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.collections4.set.ListOrderedSet;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.crm.assemble.control.AbstractFactory;
import com.x.crm.assemble.control.Business;
import com.x.crm.assemble.control.WrapCrmTools;
import com.x.crm.assemble.control.wrapout.WrapOutCrmBaseConfig;
import com.x.crm.core.entity.CrmBaseConfig;
import com.x.crm.core.entity.CrmBaseConfig_;

public class CrmBaseConfigFactory extends AbstractFactory {
	private Logger logger = LoggerFactory.getLogger(CrmBaseConfigFactory.class);

	public CrmBaseConfigFactory(Business business) throws Exception {
		super(business);
		// TODO Auto-generated constructor stub
	}

	// 1：_configtype类型，查询顶层配置
	public List<String> getIdListByBaseconfigtype(String _configtype) throws Exception {
		logger.error(new Exception(_configtype));
		EntityManager em = this.entityManagerContainer().get(CrmBaseConfig.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<CrmBaseConfig> root = cq.from(CrmBaseConfig.class);
		// Predicate p =
		// cb.and(cb.equal(root.get(CrmBaseConfig_.baseconfigtype),
		// _configtype), cb.isNull(root.get(CrmBaseConfig_.parentconfigid)));
		Predicate p = cb.or(cb.equal(root.get(CrmBaseConfig_.parentconfigid), ""),
				cb.isNull(root.get(CrmBaseConfig_.parentconfigid)));
		p = cb.and(p, cb.equal(root.get(CrmBaseConfig_.baseconfigtype), _configtype));
		cq.select(root.get(CrmBaseConfig_.id)).where(p);
		List<String> _tmpList = new ArrayList<>();

		_tmpList = em.createQuery(cq).getResultList();
		if (null != _tmpList) {
			logger.error(new Exception("" + _tmpList.size()));
		}
		return _tmpList;
	}

	// 查询parentconfigid为指定值得ids
	public List<String> getIdListByBaseconfigtypeByParentId(String _configtype, String _parentconfigid)
			throws Exception {
		EntityManager em = this.entityManagerContainer().get(CrmBaseConfig.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<CrmBaseConfig> root = cq.from(CrmBaseConfig.class);
		Predicate p = cb.and(cb.equal(root.get(CrmBaseConfig_.baseconfigtype), _configtype),
				cb.equal(root.get(CrmBaseConfig_.parentconfigid), _parentconfigid));
		cq.select(root.get(CrmBaseConfig_.id)).where(p);
		List<String> _tmpList = new ArrayList<>();

		_tmpList = em.createQuery(cq).getResultList();

		return _tmpList;
	}

	/**
	 * _configtype:类型 _parentconfigid：上一级id
	 */
	public List<CrmBaseConfig> getConfigListByTypByParentIdOrderByOrdernumber(String _configtype,
			String _parentconfigid) throws Exception {
		EntityManager em = this.entityManagerContainer().get(CrmBaseConfig.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<CrmBaseConfig> cq = cb.createQuery(CrmBaseConfig.class);
		Root<CrmBaseConfig> root = cq.from(CrmBaseConfig.class);
		Predicate p = cb.and(cb.equal(root.get(CrmBaseConfig_.baseconfigtype), _configtype),
				cb.equal(root.get(CrmBaseConfig_.parentconfigid), _parentconfigid));
		em.createQuery(cq.select(root).where(p).orderBy(cb.asc(root.get(CrmBaseConfig_.ordernumber))));
		return em.createQuery(cq).getResultList();
	}

	public List<WrapOutCrmBaseConfig> rootNodeListByType(String _configtype) throws Exception {
		logger.error(new Exception("rootNodeListByType:run!!!!:" + _configtype));
		EntityManager em = this.entityManagerContainer().get(CrmBaseConfig.class);
		EntityManagerContainer emc = EntityManagerContainerFactory.instance().create();
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<CrmBaseConfig> root = cq.from(CrmBaseConfig.class);
		Predicate p = cb.and(cb.equal(root.get(CrmBaseConfig_.baseconfigtype), _configtype),
				cb.isNull(root.get(CrmBaseConfig_.parentconfigid)));
		cq.select(root.get(CrmBaseConfig_.id)).where(p);
		List<String> _tmpList = new ArrayList<>();
		_tmpList = em.createQuery(cq).getResultList();
		for (String string : _tmpList) {
			logger.error(new Exception(string));
		}
		List<WrapOutCrmBaseConfig> rootNodeList = WrapCrmTools.CrmBaseConfigOutCopier
				.copy(emc.list(CrmBaseConfig.class, _tmpList));
		return rootNodeList;
	}

	public WrapOutCrmBaseConfig recursiveTree(String _configtype, String _parentconfigid, WrapOutCrmBaseConfig wrapnode)
			throws Exception {
		EntityManager em = this.entityManagerContainer().get(CrmBaseConfig.class);
		// 当前节点
		// WrapOutCrmBaseConfig parentNode =
		// WrapCrmTools.CrmBaseConfigOutCopier.copy(this.entityManagerContainer().find(_parentconfigid,
		// CrmBaseConfig.class));
		WrapOutCrmBaseConfig parentNode = wrapnode;

		// 查找子节点对象
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<CrmBaseConfig> cq = cb.createQuery(CrmBaseConfig.class);
		Root<CrmBaseConfig> root = cq.from(CrmBaseConfig.class);
		Predicate p = cb.and(cb.equal(root.get(CrmBaseConfig_.baseconfigtype), _configtype),
				cb.equal(root.get(CrmBaseConfig_.parentconfigid), _parentconfigid));
		TypedQuery<CrmBaseConfig> typedQuery = em.createQuery(cq.select(root).where(p));
		logger.error(new Exception("typedQuery.getResultList:" + typedQuery.getResultList().size()));
		List<WrapOutCrmBaseConfig> childNodes = WrapCrmTools.CrmBaseConfigOutCopier.copy(typedQuery.getResultList());

		// parentNode.setChildNodes(childNodes);

		for (WrapOutCrmBaseConfig childnode : childNodes) {
			WrapOutCrmBaseConfig node = recursiveTree(_configtype, childnode.getId(), wrapnode);
			// WrapOutCrmBaseConfig node = recursiveTree(_configtype,
			// childnode.getId(), wrapnode); //递归
			parentNode.getChildNodes().add(node);
		}

		if (childNodes.isEmpty()) {
			return null;
		}
		return parentNode;
	}

	// @MethodDescribe("获取指定id的全部下级配置.包括嵌套的配置,仅返回Id.")
	public List<String> listSubNested(String id) throws Exception {
		logger.error(new Exception("listSubNested:" + id));
		ListOrderedSet<String> set = new ListOrderedSet<>();
		this.subNested(id, set);
		return set.asList();
	}

	// @MethodDescribe("递归循环调用查找,仅返回Id.")
	private void subNested(String id, ListOrderedSet<String> set) throws Exception {
		logger.error(new Exception("subNested:" + id));
		List<String> list = new ArrayList<>();
		for (String o : this.listSubDirect(id)) {
			if (!set.contains(o)) {
				list.add(o);
			}
		}
		if (!list.isEmpty()) {
			set.addAll(list);
			for (String o : list) {
				this.subNested(o, set);
			}
		}
	}

	// @MethodDescribe("获取指定id的直接下级配置,仅返回Id.")
	public List<String> listSubDirect(String id) throws Exception {
		logger.error(new Exception("listSubDirect:" + id));
		EntityManager em = this.entityManagerContainer().get(CrmBaseConfig.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<CrmBaseConfig> root = cq.from(CrmBaseConfig.class);
		Predicate p = cb.equal(root.get(CrmBaseConfig_.parentconfigid), id);
		cq.select(root.get(CrmBaseConfig_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}
}

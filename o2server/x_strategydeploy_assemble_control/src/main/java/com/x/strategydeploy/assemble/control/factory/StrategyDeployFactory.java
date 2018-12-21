package com.x.strategydeploy.assemble.control.factory;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.base.core.project.exception.ExceptionWhen;
import com.x.strategydeploy.assemble.control.AbstractFactory;
import com.x.strategydeploy.assemble.control.Business;
import com.x.strategydeploy.core.entity.StrategyDeploy;
import com.x.strategydeploy.core.entity.StrategyDeploy_;

public class StrategyDeployFactory extends AbstractFactory {

	protected static final String DESC = "desc";
	protected static final String ASC = "asc";

	public final static String EMPTY_SYMBOL = "(0)";

	public StrategyDeployFactory(Business business) throws Exception {
		super(business);
		// TODO Auto-generated constructor stub
	}

	//根据id 判断 战略部署是否存在。返回 true or false
	public Boolean IsExistById(String _id) throws Exception {
		EntityManager em = this.entityManagerContainer().get(StrategyDeploy.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<StrategyDeploy> root = cq.from(StrategyDeploy.class);
		Predicate p = cb.equal(root.get(StrategyDeploy_.id), _id);
		cq.select(root.get(StrategyDeploy_.id)).where(p);

		List<String> _tmpList = new ArrayList<>();
		_tmpList = em.createQuery(cq).getResultList();
		int _tmpSize = _tmpList.size();
		if (_tmpSize > 0) {
			return true;
		} else {
			return false;
		}
	}

	//根据id查找  战略部署对象。返回战略部署对象。
	public StrategyDeploy getById(String _id) throws Exception {
		return this.entityManagerContainer().find(_id, StrategyDeploy.class, ExceptionWhen.none);
	}

	//根据“年份”，列出战略部署对象列表。
	public List<StrategyDeploy> getListByYear(String _year) throws Exception {
		EntityManager em = this.entityManagerContainer().get(StrategyDeploy.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<StrategyDeploy> cq = cb.createQuery(StrategyDeploy.class);
		Root<StrategyDeploy> root = cq.from(StrategyDeploy.class);
		Predicate p = cb.equal(root.get(StrategyDeploy_.strategydeployyear), _year);
		cq.select(root).where(p).orderBy(cb.asc(root.get(StrategyDeploy_.sequencenumber)));
		return em.createQuery(cq).getResultList();
	}

	//根据“年份”，“部门”，列出战略部署对象列表。
	public List<StrategyDeploy> getListByYearAndDept(String _year, String _dept) throws Exception {
		//		List<String> units = new ArrayList<>();
		//		String unit = "";
		EntityManager em = this.entityManagerContainer().get(StrategyDeploy.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<StrategyDeploy> cq = cb.createQuery(StrategyDeploy.class);
		Root<StrategyDeploy> root = cq.from(StrategyDeploy.class);
		Predicate p = cb.equal(root.get(StrategyDeploy_.strategydeployyear), _year);
		p = cb.and(p, cb.isMember(_dept, root.get(StrategyDeploy_.deptlist)));
		cq.select(root).where(p).orderBy(cb.asc(root.get(StrategyDeploy_.sequencenumber)));
		return em.createQuery(cq).getResultList();
	}

	//@MethodDescribe("列示指定Id的实体信息列表")
	public List<StrategyDeploy> getListByIds(List<String> ids) throws Exception {
		if (ids == null || ids.size() == 0) {
			return new ArrayList<StrategyDeploy>();
		}
		EntityManager em = this.entityManagerContainer().get(StrategyDeploy.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<StrategyDeploy> cq = cb.createQuery(StrategyDeploy.class);
		Root<StrategyDeploy> root = cq.from(StrategyDeploy.class);
		Predicate p = root.get(StrategyDeploy_.id).in(ids);
		List<StrategyDeploy> _list = em.createQuery(cq.where(p)).getResultList();
		return _list;
	}
	
	//@MethodDescribe("列示指定Id的排序号列表")
	public List<Integer> getListSequencenumberByIds(List<String> ids) throws Exception {
		if (ids == null || ids.size() == 0) {
			return new ArrayList<Integer>();
		}
		EntityManager em = this.entityManagerContainer().get(StrategyDeploy.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Integer> cq = cb.createQuery(Integer.class);
		Root<StrategyDeploy> root = cq.from(StrategyDeploy.class);
		Predicate p = root.get(StrategyDeploy_.id).in(ids);
		cq.select(root.get(StrategyDeploy_.sequencenumber));
		List<Integer> _list = em.createQuery(cq.where(p)).getResultList();
		return _list;
	}
}

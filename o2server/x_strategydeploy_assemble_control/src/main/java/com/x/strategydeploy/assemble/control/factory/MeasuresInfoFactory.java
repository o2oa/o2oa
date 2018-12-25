package com.x.strategydeploy.assemble.control.factory;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.base.core.project.exception.ExceptionWhen;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.strategydeploy.assemble.control.AbstractFactory;
import com.x.strategydeploy.assemble.control.Business;
import com.x.strategydeploy.core.entity.MeasuresInfo;
import com.x.strategydeploy.core.entity.MeasuresInfo_;

public class MeasuresInfoFactory extends AbstractFactory {

	private static  Logger logger = LoggerFactory.getLogger(MeasuresInfoFactory.class);

	protected static final String DESC = "desc";
	protected static final String ASC = "asc";

	public final static String EMPTY_SYMBOL = "(0)";

	public MeasuresInfoFactory(Business business) throws Exception {
		super(business);
		// TODO Auto-generated constructor stub
	}

	// 根据id 判断举措 是否存在。返回 true or false
	public Boolean IsExistById(String _id) throws Exception {
		EntityManager em = this.entityManagerContainer().get(MeasuresInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<MeasuresInfo> root = cq.from(MeasuresInfo.class);
		Predicate p = cb.equal(root.get(MeasuresInfo_.id), _id);
		cq.select(root.get(MeasuresInfo_.id)).where(p);

		List<String> _tmpList = new ArrayList<>();
		_tmpList = em.createQuery(cq).getResultList();
		int _tmpSize = _tmpList.size();
		if (_tmpSize > 0) {
			return true;
		} else {
			return false;
		}
	}

	// 根据id查找 举措对象。返回战略部署对象。
	public MeasuresInfo getById(String _id) throws Exception {
		return this.entityManagerContainer().find(_id, MeasuresInfo.class, ExceptionWhen.none);
	}

	// 根据ids查找举措标题，返回标题列表。
	public List<String> getTitleListByIds(List<String> ids) throws Exception {
		List<String> titleList = new ArrayList<String>();
		for (String _id : ids) {
			MeasuresInfo measuresinfo = new MeasuresInfo();
			measuresinfo = this.entityManagerContainer().find(_id, MeasuresInfo.class, ExceptionWhen.none);
			String _title = measuresinfo.getMeasuresinfotitle();
			titleList.add(_title);
		}
		return titleList;
	}

	// @MethodDescribe("根据ids查找举措对象列表。")
	public List<MeasuresInfo> getListByIds(List<String> ids) throws Exception {
		if (ids == null || ids.size() == 0) {
			return new ArrayList<MeasuresInfo>();
		}
		EntityManager em = this.entityManagerContainer().get(MeasuresInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<MeasuresInfo> cq = cb.createQuery(MeasuresInfo.class);
		Root<MeasuresInfo> root = cq.from(MeasuresInfo.class);
		Predicate p = root.get(MeasuresInfo_.id).in(ids);
		List<MeasuresInfo> _list = em.createQuery(cq.where(p)).getResultList();
		return _list;
	}

	public List<MeasuresInfo> getListByIdsAscFormatsequencenumber(List<String> ids) throws Exception {
		if (ids == null || ids.size() == 0) {
			return new ArrayList<MeasuresInfo>();
		}
		EntityManager em = this.entityManagerContainer().get(MeasuresInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<MeasuresInfo> cq = cb.createQuery(MeasuresInfo.class);
		Root<MeasuresInfo> root = cq.from(MeasuresInfo.class);
		Predicate p = root.get(MeasuresInfo_.id).in(ids);
		cq.select(root).where(p).orderBy(cb.asc(root.get(MeasuresInfo_.formatsequencenumber)));
		List<MeasuresInfo> _list = em.createQuery(cq).getResultList();
		return _list;
	}
	
	
	// 根据“年份”，列出举措对象列表。
	public List<MeasuresInfo> getListByYear(String _year) throws Exception {
		EntityManager em = this.entityManagerContainer().get(MeasuresInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<MeasuresInfo> cq = cb.createQuery(MeasuresInfo.class);
		Root<MeasuresInfo> root = cq.from(MeasuresInfo.class);
		Predicate p = cb.equal(root.get(MeasuresInfo_.measuresinfoyear), _year);
		//cq.select(root).where(p).orderBy(cb.asc(root.get(MeasuresInfo_.sequencenumber)));
		cq.select(root).where(p).orderBy(cb.asc(root.get(MeasuresInfo_.formatsequencenumber)));
		return em.createQuery(cq).getResultList();
	}

	// 根据“年份”、“parentId”，列出举措对象列表。
	// @MethodDescribe("根据“parentId”，列出举措对象列表")
	public List<MeasuresInfo> getListByParentId(String _parentId) throws Exception {
		EntityManager em = this.entityManagerContainer().get(MeasuresInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<MeasuresInfo> cq = cb.createQuery(MeasuresInfo.class);
		Root<MeasuresInfo> root = cq.from(MeasuresInfo.class);
		Predicate p = cb.isNotNull(root.get(MeasuresInfo_.measuresinfoyear));
		p = cb.and(p, cb.equal(root.get(MeasuresInfo_.measuresinfoparentid), _parentId));
		//cq.select(root).where(p).orderBy(cb.asc(root.get(MeasuresInfo_.sequencenumber)));
		cq.select(root).where(p).orderBy(cb.asc(root.get(MeasuresInfo_.formatsequencenumber)));
		return em.createQuery(cq).getResultList();
	}

	// 根据“年份”、“parentId”，列出举措对象列表。
	public List<MeasuresInfo> getListByYearAndParentId(String _year, String _parentId) throws Exception {
		EntityManager em = this.entityManagerContainer().get(MeasuresInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<MeasuresInfo> cq = cb.createQuery(MeasuresInfo.class);
		Root<MeasuresInfo> root = cq.from(MeasuresInfo.class);
		Predicate p = cb.equal(root.get(MeasuresInfo_.measuresinfoyear), _year);
		p = cb.and(p, cb.equal(root.get(MeasuresInfo_.measuresinfoparentid), _parentId));
		//cq.select(root).where(p).orderBy(cb.asc(root.get(MeasuresInfo_.sequencenumber)));
		cq.select(root).where(p).orderBy(cb.asc(root.get(MeasuresInfo_.formatsequencenumber)));
		return em.createQuery(cq).getResultList();
	}

	// 根据“年份”，“部门”，列出举措对象列表。
	public List<MeasuresInfo> getListByYearAndDept(String _year, String _dept) throws Exception {
		EntityManager em = this.entityManagerContainer().get(MeasuresInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<MeasuresInfo> cq = cb.createQuery(MeasuresInfo.class);
		Root<MeasuresInfo> root = cq.from(MeasuresInfo.class);
		Predicate p = cb.equal(root.get(MeasuresInfo_.measuresinfoyear), _year);
		p = cb.and(p, cb.isMember(_dept, root.get(MeasuresInfo_.deptlist)));
		// logger.info("_year：" + _year + " _dept:" + _dept);
		//cq.select(root).where(p).orderBy(cb.asc(root.get(MeasuresInfo_.sequencenumber)));
		cq.select(root).where(p).orderBy(cb.asc(root.get(MeasuresInfo_.formatsequencenumber)));
		return em.createQuery(cq).getResultList();
	}

}

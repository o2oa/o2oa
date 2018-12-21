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
import com.x.strategydeploy.core.entity.KeyworkInfo;
import com.x.strategydeploy.core.entity.KeyworkInfo_;

public class KeyWorkInfoFactory extends AbstractFactory {

	protected static final String DESC = "desc";
	protected static final String ASC = "asc";

	public final static String EMPTY_SYMBOL = "(0)";

	public KeyWorkInfoFactory(Business business) throws Exception {
		super(business);
		// TODO Auto-generated constructor stub
	}

	//根据id 判断五项重点工作 是否存在。返回 true or false
	public Boolean IsExistById(String _id) throws Exception {
		EntityManager em = this.entityManagerContainer().get(KeyworkInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<KeyworkInfo> root = cq.from(KeyworkInfo.class);
		Predicate p = cb.equal(root.get(KeyworkInfo_.id), _id);
		cq.select(root.get(KeyworkInfo_.id)).where(p);

		List<String> _tmpList = new ArrayList<>();
		_tmpList = em.createQuery(cq).getResultList();
		int _tmpSize = _tmpList.size();
		if (_tmpSize > 0) {
			return true;
		} else {
			return false;
		}
	}

	//根据id查找  五项重点工作对象。返回五项重点工作对象。
	public KeyworkInfo getById(String _id) throws Exception {
		return this.entityManagerContainer().find(_id, KeyworkInfo.class, ExceptionWhen.none);
	}

	//根据“年份”，列出五项重点工作对象列表。
	public List<KeyworkInfo> getListByYear(String _year) throws Exception {
		EntityManager em = this.entityManagerContainer().get(KeyworkInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<KeyworkInfo> cq = cb.createQuery(KeyworkInfo.class);
		Root<KeyworkInfo> root = cq.from(KeyworkInfo.class);
		Predicate p = cb.equal(root.get(KeyworkInfo_.keyworkyear), _year);
		cq.select(root).where(p).orderBy(cb.asc(root.get(KeyworkInfo_.sequencenumber)));
		return em.createQuery(cq).getResultList();
	}

	//根据“年份”，“部门”，列出五项重点工作对象列表。
	public List<KeyworkInfo> getListByYearAndDept(String _year, String _dept) throws Exception {
		EntityManager em = this.entityManagerContainer().get(KeyworkInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<KeyworkInfo> cq = cb.createQuery(KeyworkInfo.class);
		Root<KeyworkInfo> root = cq.from(KeyworkInfo.class);
		Predicate p = cb.equal(root.get(KeyworkInfo_.keyworkyear), _year);
		p = cb.and(p, cb.equal(root.get(KeyworkInfo_.keyworkunit), _dept));
		cq.select(root).where(p).orderBy(cb.asc(root.get(KeyworkInfo_.sequencenumber)));
		return em.createQuery(cq).getResultList();
	}
}

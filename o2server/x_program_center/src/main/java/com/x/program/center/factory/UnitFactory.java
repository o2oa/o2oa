package com.x.program.center.factory;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.tools.ListTools;
import com.x.organization.core.entity.PersistenceProperties;
import com.x.organization.core.entity.Unit;
import com.x.organization.core.entity.Unit_;
import com.x.program.center.AbstractFactory;
import com.x.program.center.Business;

public class UnitFactory extends AbstractFactory {

	public UnitFactory(Business business) throws Exception {
		super(business);
	}

	public Unit getWithAndFxIdObject(String id) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Unit.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Unit> cq = cb.createQuery(Unit.class);
		Root<Unit> root = cq.from(Unit.class);
		Predicate p = cb.equal(root.get(Unit_.andFxId), id);
		List<Unit> os = em.createQuery(cq.select(root).where(p)).setMaxResults(1).getResultList();
		if (os.isEmpty()) {
			return null;
		} else {
			return os.get(0);
		}
	}

	public Unit getWithDingdingIdObject(String dingdingId) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Unit.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Unit> cq = cb.createQuery(Unit.class);
		Root<Unit> root = cq.from(Unit.class);
		Predicate p = cb.equal(root.get(Unit_.dingdingId), dingdingId);
		List<Unit> os = em.createQuery(cq.select(root).where(p)).setMaxResults(1).getResultList();
		if (os.isEmpty()) {
			return null;
		} else {
			return os.get(0);
		}
	}

	public Unit getWithWeLinkDeptCodeObject(String weLinkDeptCode) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Unit.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Unit> cq = cb.createQuery(Unit.class);
		Root<Unit> root = cq.from(Unit.class);
		Predicate p = cb.equal(root.get(Unit_.weLinkId), weLinkDeptCode);
		List<Unit> os = em.createQuery(cq.select(root).where(p)).setMaxResults(1).getResultList();
		if (os.isEmpty()) {
			return null;
		} else {
			return os.get(0);
		}
	}

	public Unit getWithZhengwuDingdingIdObject(String zhengwuDingdingId) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Unit.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Unit> cq = cb.createQuery(Unit.class);
		Root<Unit> root = cq.from(Unit.class);
		Predicate p = cb.equal(root.get(Unit_.zhengwuDingdingId), zhengwuDingdingId);
		List<Unit> os = em.createQuery(cq.select(root).where(p)).setMaxResults(1).getResultList();
		if (os.isEmpty()) {
			return null;
		} else {
			return os.get(0);
		}
	}

	public Unit getWithQiyeweixinIdObject(String qiyeweixinId) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Unit.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Unit> cq = cb.createQuery(Unit.class);
		Root<Unit> root = cq.from(Unit.class);
		Predicate p = cb.equal(root.get(Unit_.qiyeweixinId), qiyeweixinId);
		List<Unit> os = em.createQuery(cq.select(root).where(p)).setMaxResults(1).getResultList();
		if (os.isEmpty()) {
			return null;
		} else {
			return os.get(0);
		}
	}

	public void adjustInherit(Unit unit) throws Exception {
		List<Unit> os = new ArrayList<>();
		os.add(unit);
		os.addAll(this.listSubNestedObject(unit));
		for (Unit o : os) {
			List<Unit> list = this.listSupNestedObject(o);
			/** 级别从1开始 */
			o.setLevel(list.size() + 1);
			List<String> names = ListTools.extractProperty(list, Unit.name_FIELDNAME, String.class, false, false);
			// Collections.reverse(names);
			names.add(o.getName());
			o.setLevelName(StringUtils.join(names, PersistenceProperties.Unit.levelNameSplit));
//			List<String> inheritControllerList = new ArrayList<>();
//			for (Unit u : list) {
//				if (ListTools.isNotEmpty(u.getControllerList())) {
//					inheritControllerList.addAll(u.getControllerList());
//				}
//			}
//			o.setInheritedControllerList(ListTools.trim(inheritControllerList, true, true));
		}
	}

	/** 递归的上级组织,从底层到顶层 */
	public List<Unit> listSupNestedObject(Unit unit) throws Exception {
		List<Unit> list = new ArrayList<>();
		if (unit == null) {
			return list;
		}
		if (StringUtils.isEmpty(unit.getSuperior())) {
			return list;
		}
		this.supNestedObject(unit, list);
		list = list.stream().sorted(Comparator.comparing(Unit::getLevel, Comparator.nullsLast(Integer::compareTo)))
				.collect(Collectors.toList());
		return list;
	}

	private void supNestedObject(Unit unit, List<Unit> list) throws Exception {
		Unit superior = this.getSupDirectObject(unit);
		if ((null != superior) && (!list.contains(superior))) {
			list.add(superior);
			this.supNestedObject(superior, list);
		}
	}

	public Unit getSupDirectObject(Unit unit) throws Exception {
		if (null == unit) {
			return null;
		}
		if (StringUtils.isEmpty(unit.getSuperior())) {
			return null;
		}
		EntityManager em = this.entityManagerContainer().get(Unit.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Unit> cq = cb.createQuery(Unit.class);
		Root<Unit> root = cq.from(Unit.class);
		Predicate p = cb.equal(root.get(Unit_.id), unit.getSuperior());
		List<Unit> list = em.createQuery(cq.select(root).where(p)).setMaxResults(1).getResultList();
		if (!list.isEmpty()) {
			return list.get(0);
		} else {
			return null;
		}
	}

	public List<Unit> listSubNestedObject(Unit unit) throws Exception {
		List<Unit> list = new ArrayList<>();
		this.subNestedObject(unit, list);
		return list;
	}

	private void subNestedObject(Unit unit, List<Unit> list) throws Exception {
		List<Unit> os = new ArrayList<>();
		for (Unit o : this.listSubDirectObject(unit)) {
			if (!list.contains(o)) {
				os.add(o);
			}
		}
		if (!os.isEmpty()) {
			list.addAll(os);
			for (Unit o : os) {
				this.subNestedObject(o, list);
			}
		}
	}

	public List<Unit> listSubDirectObject(Unit unit) throws Exception {
		if (null == unit) {
			return new ArrayList<Unit>();
		}
		EntityManager em = this.entityManagerContainer().get(Unit.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Unit> cq = cb.createQuery(Unit.class);
		Root<Unit> root = cq.from(Unit.class);
		Predicate p = cb.equal(root.get(Unit_.superior), unit.getId());
		return em.createQuery(cq.select(root).where(p)).getResultList();
	}

}

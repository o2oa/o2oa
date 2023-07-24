package com.x.organization.assemble.personal.factory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.cache.Cache.CacheKey;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.tools.ListTools;
import com.x.organization.assemble.personal.AbstractFactory;
import com.x.organization.assemble.personal.Business;
import com.x.organization.core.entity.PersistenceProperties;
import com.x.organization.core.entity.Unit;
import com.x.organization.core.entity.Unit_;

public class UnitFactory extends AbstractFactory {

	public UnitFactory(Business business) throws Exception {
		super(business);
	}

	public Unit pick(String flag) throws Exception {
		if (StringUtils.isEmpty(flag)) {
			return null;
		}
		Unit o = null;
		CacheKey cacheKey = new CacheKey(this.getClass(), flag);
		Optional<?> optional = CacheManager.get(business.cache(), cacheKey);
		if (optional.isPresent()) {
			o = (Unit) optional.get();
		} else {
			o = this.pickObject(flag);
			CacheManager.put(business.cache(), cacheKey, o);
		}
		return o;
	}

	private Unit pickObject(String flag) throws Exception {
		Unit o = this.entityManagerContainer().flag(flag, Unit.class);
		if (o != null) {
			this.entityManagerContainer().get(Unit.class).detach(o);
		} else {
			String name = flag;
			Matcher matcher = PersistenceProperties.Unit.distinguishedName_pattern.matcher(flag);
			if (matcher.find()) {
				name = matcher.group(1);
				String unique = matcher.group(2);
				o = this.entityManagerContainer().flag(unique, Unit.class);
				if (null != o) {
					this.entityManagerContainer().get(Unit.class).detach(o);
				}
			}
			if (null == o) {
				EntityManager em = this.entityManagerContainer().get(Unit.class);
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<Unit> cq = cb.createQuery(Unit.class);
				Root<Unit> root = cq.from(Unit.class);
				Predicate p = cb.equal(root.get(Unit_.name), name);
				List<Unit> os = em.createQuery(cq.select(root).where(p)).getResultList();
				if (os.size() == 1) {
					o = os.get(0);
					em.detach(o);
				}
			}
			if (null == o && StringUtils.contains(flag, PersistenceProperties.Unit.levelNameSplit)) {
				List<String> names = Arrays.asList(StringUtils.split(flag, PersistenceProperties.Unit.levelNameSplit));
				EntityManager em = this.entityManagerContainer().get(Unit.class);
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<Unit> cq = cb.createQuery(Unit.class);
				Root<Unit> root = cq.from(Unit.class);
				Predicate p = root.get(Unit_.name).in(names);
				List<Unit> os = em.createQuery(cq.select(root).where(p)).getResultList();
				os = os.stream().sorted(Comparator.comparing(Unit::getLevel, Comparator.nullsLast(Integer::compareTo)))
						.collect(Collectors.toList());
				List<String> values = ListTools.extractProperty(os, "name", String.class, false, false);
				if (StringUtils.equals(flag, StringUtils.join(values, PersistenceProperties.Unit.levelNameSplit))) {
					o = os.get(os.size() - 1);
					em.detach(o);
				}
			}
		}
		return o;
	}

	public List<Unit> pick(List<String> flags) throws Exception {
		List<Unit> list = new ArrayList<>();
		for (String str : flags) {
			CacheKey cacheKey = new CacheKey(str);
			Optional<?> optional = CacheManager.get(business.cache(), cacheKey);
			if (optional.isPresent()) {
				list.add((Unit) optional.get());
			} else {
				Unit o = this.pickObject(str);
				CacheManager.put(business.cache(), cacheKey, o);
				if (null != o) {
					list.add(o);
				}
			}
		}
		return list;
	}

	public <T extends Unit> List<T> sort(List<T> list) {
		list = list.stream()
				.sorted(Comparator.comparing(Unit::getLevel, Comparator.nullsLast(Integer::compareTo))
						.thenComparing(
								Comparator.comparing(Unit::getOrderNumber, Comparator.nullsLast(Integer::compareTo)))
						.thenComparing(Comparator.comparing(Unit::getName, Comparator.nullsLast(String::compareTo))))
				.collect(Collectors.toList());
		return list;
	}

	public String getSupDirect(String id) throws Exception {
		if (StringUtils.isEmpty(id)) {
			return null;
		}
		Unit unit = this.pick(id);
		if (null == unit) {
			return null;
		}
		if (StringUtils.isEmpty(unit.getSuperior())) {
			return null;
		}
		Unit superior = this.pick(unit.getSuperior());
		if (null == superior) {
			return null;
		}
		return superior.getId();
	}

	/** 递归的上级组织,从底层到顶层 */
	public List<String> listSupNested(String id) throws Exception {
		List<String> list = new ArrayList<>();
		this.supNested(id, list);
		return list;
	}

	private void supNested(String id, List<String> list) throws Exception {
		String superiorId = this.getSupDirect(id);
		if (StringUtils.isNotEmpty(superiorId) && (!list.contains(superiorId))) {
			list.add(superiorId);
			this.supNested(superiorId, list);
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

	public void adjustInherit(Unit unit) throws Exception {
		List<Unit> os = new ArrayList<>();
		os.add(unit);
		os.addAll(this.listSubNestedObject(unit));
		for (Unit o : os) {
			List<Unit> list = this.listSupNestedObject(o);
			/** 级别从1开始 */
			o.setLevel(list.size() + 1);
			List<String> names = ListTools.extractProperty(list, "name", String.class, false, false);
			names.add(o.getName());
			o.setLevelName(StringUtils.join(names, PersistenceProperties.Unit.levelNameSplit));
		}
	}

	public List<String> listSubNested(String id) throws Exception {
		List<String> list = new ArrayList<>();
		this.subNested(id, list);
		return list;
	}

	private void subNested(String id, List<String> list) throws Exception {
		List<String> os = new ArrayList<>();
		for (String o : this.listSubDirect(id)) {
			if (!list.contains(o)) {
				os.add(o);
			}
		}
		if (!os.isEmpty()) {
			list.addAll(os);
			for (String o : os) {
				this.subNested(o, list);
			}
		}
	}

	public List<String> listSubDirect(String id) throws Exception {
		if (StringUtils.isEmpty(id)) {
			return new ArrayList<String>();
		}
		EntityManager em = this.entityManagerContainer().get(Unit.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Unit> root = cq.from(Unit.class);
		Predicate p = cb.equal(root.get(Unit_.superior), id);
		return em.createQuery(cq.select(root.get(Unit_.id)).where(p)).getResultList();
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
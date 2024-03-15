package com.x.organization.assemble.express.factory;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.cache.Cache.CacheCategory;
import com.x.base.core.project.cache.Cache.CacheKey;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.tools.ListTools;
import com.x.organization.assemble.express.AbstractFactory;
import com.x.organization.assemble.express.Business;
import com.x.organization.core.entity.*;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.*;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

public class UnitFactory extends AbstractFactory {

	private CacheCategory cacheCategory = new CacheCategory(Unit.class);

	public UnitFactory(Business business) throws Exception {
		super(business);
	}

	public Unit pick(String flag) throws Exception {
		if (StringUtils.isEmpty(flag)) {
			return null;
		}
		Unit o = null;
		CacheKey cacheKey = new CacheKey(Unit.class.getName(), flag);
		Optional<?> optional = CacheManager.get(cacheCategory, cacheKey);
		if (optional.isPresent()) {
			o = (Unit) optional.get();
		} else {
			o = this.pickObject(flag);
			if (null != o) {
				CacheManager.put(cacheCategory, cacheKey, o);
			}
		}
		return o;
	}

	private Unit pickObject(String flag) throws Exception {
		Unit o = this.entityManagerContainer().flag(flag, Unit.class);
		if (o != null) {
			this.entityManagerContainer().get(Unit.class).detach(o);
		} else {
			//String name = flag;
			Matcher matcher = PersistenceProperties.Unit.distinguishedName_pattern.matcher(flag);
			if (matcher.find()) {
				//name = matcher.group(1);
				String unique = matcher.group(2);
				o = this.entityManagerContainer().flag(unique, Unit.class);
				if (null != o) {
					this.entityManagerContainer().get(Unit.class).detach(o);
				}
			}
//			if (null == o) {
//				EntityManager em = this.entityManagerContainer().get(Unit.class);
//				CriteriaBuilder cb = em.getCriteriaBuilder();
//				CriteriaQuery<Unit> cq = cb.createQuery(Unit.class);
//				Root<Unit> root = cq.from(Unit.class);
//				Predicate p = cb.equal(root.get(Unit_.name), name);
//				List<Unit> os = em.createQuery(cq.select(root).where(p)).getResultList();
//				if (os.size() == 1) {
//					o = os.get(0);
//					em.detach(o);
//				}
//			}
//			if (null == o && StringUtils.contains(flag, PersistenceProperties.Unit.levelNameSplit)) {
//				List<String> list = Arrays.asList(StringUtils.split(flag, PersistenceProperties.Unit.levelNameSplit));
//				EntityManager em = this.entityManagerContainer().get(Unit.class);
//				CriteriaBuilder cb = em.getCriteriaBuilder();
//				CriteriaQuery<Unit> cq = cb.createQuery(Unit.class);
//				Root<Unit> root = cq.from(Unit.class);
//				Predicate p = cb.and(cb.equal(root.get(Unit_.name), Objects.toString(ListTools.last(list), "")),
//						cb.equal(root.get(Unit_.level), list.size()));
//				List<Unit> os = em.createQuery(cq.select(root).where(p)).getResultList();
//				for (Unit unit : os) {
//					if (StringUtils.equalsIgnoreCase(unit.getLevelName(), flag)) {
//						o = unit;
//						em.detach(o);
//						break;
//					}
//				}
//			}
		}
		return o;
	}

	public List<Unit> pick(List<String> flags) throws Exception {
		List<Unit> list = new ArrayList<>();
		for (String str : flags) {
			CacheKey cacheKey = new CacheKey(Unit.class.getName(), str);
			Optional<?> optional = CacheManager.get(cacheCategory, cacheKey);
			if (optional.isPresent()) {
				list.add((Unit) optional.get());
			} else {
				Unit o = this.pickObject(str);
				if (null != o) {
					CacheManager.put(cacheCategory, cacheKey, o);
					list.add(o);
				}
			}
		}
		return list;
	}

	public List<Unit> pick(List<String> flags, Boolean useNameFind) throws Exception {
		List<Unit> list = new ArrayList<>();
		for (String str : flags) {
			CacheKey cacheKey = new CacheKey(Unit.class.getName(), str);
			Optional<?> optional = CacheManager.get(cacheCategory, cacheKey);
			if (optional.isPresent()) {
				list.add((Unit) optional.get());
			} else {
				Unit o = this.pickObject(str);
				if (null != o) {
					CacheManager.put(cacheCategory, cacheKey, o);
					list.add(o);
				}
			}
		}
		if(list.isEmpty() && BooleanUtils.isTrue(useNameFind)){
			list = this.listWithName(flags);
		}
		return list;
	}

	public List<Unit> listWithName(List<String> names) throws Exception {
		if(ListTools.isEmpty(names)){
			return Collections.EMPTY_LIST;
		}
		EntityManager em = this.entityManagerContainer().get(Unit.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Unit> cq = cb.createQuery(Unit.class);
		Root<Unit> root = cq.from(Unit.class);
		Predicate p;
		if(names.size() > 1){
			p = root.get(Unit_.name).in(names);
		}else{
			p = cb.equal(root.get(Unit_.name), names.get(0));
		}
		return em.createQuery(cq.select(root).where(p)).getResultList().stream().distinct().collect(Collectors.toList());
	}

	public <T extends Unit> List<T> sort(List<T> list) throws Exception {
		if (Config.person().getPersonUnitOrderByAsc()) {
			list = list.stream().sorted(Comparator.comparing(Unit::getLevel, Comparator.nullsLast(Integer::compareTo))
					.thenComparing(Comparator.comparing(Unit::getOrderNumber, Comparator.nullsLast(Integer::compareTo)))
					.thenComparing(
							Comparator.comparing(Unit::getName, Comparator.nullsFirst(String::compareTo)).reversed()))
					.collect(Collectors.toList());
		} else {
			list = list.stream().sorted(Comparator.comparing(Unit::getLevel, Comparator.nullsLast(Integer::compareTo))
					.thenComparing(Comparator.comparing(Unit::getOrderNumber, Comparator.nullsLast(Integer::compareTo))
							.reversed())
					.thenComparing(
							Comparator.comparing(Unit::getName, Comparator.nullsFirst(String::compareTo)).reversed()))
					.collect(Collectors.toList());
		}
		return list;
	}

	public Unit getSupDirectObject(Unit unit) throws Exception {
		if (null == unit) {
			return null;
		}
		if (StringUtils.isEmpty(unit.getSuperior())) {
			return null;
		}
		Unit superior = this.pick(unit.getSuperior());
		return superior;
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
		if (null != superior) {
			return superior.getId();
		}
		return null;
	}

	/** 递归的上级组织,从底层到顶层 */
	public List<String> listSupNested(String id) throws Exception {
		List<String> list = new ArrayList<>();
		if (StringUtils.isEmpty(id)) {
			return list;
		}
		this.supNested(id, list);
		return list;
	}

	private void supNested(String id, List<String> list) throws Exception {
		String superior = this.getSupDirect(id);
		if (StringUtils.isNotEmpty(superior) && (!list.contains(superior))) {
			list.add(superior);
			this.supNested(superior, list);
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
			names = names.stream().sorted(Comparator.reverseOrder()).collect(Collectors.toList());
			names.add(o.getName());
			o.setLevelName(StringUtils.join(names, "/"));
//			List<String> inheritControllerList = new ArrayList<>();
//			for (Unit u : list) {
//				inheritControllerList.addAll(u.getControllerList());
//			}
//			o.setInheritedControllerList(ListTools.trim(inheritControllerList, true, true));
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

	public List<String> listSubNested(String id) throws Exception {
		List<String> list = new ArrayList<>();
		this.subNested(id, list);
		return list;
	}

	private void subNested(String id, List<String> list) throws Exception {
		List<String> ids = new ArrayList<>();
		for (String o : this.listSubDirect(id)) {
			if (!list.contains(o)) {
				ids.add(o);
			}
		}
		if (!ids.isEmpty()) {
			list.addAll(ids);
			for (String o : ids) {
				this.subNested(o, list);
			}
		}
	}

	public List<String> listSubDirect(String id) throws Exception {
		List<String> list = new ArrayList<>();
		if (StringUtils.isEmpty(id)) {
			return list;
		}
		Unit unit = this.pick(id);
		if (null == unit) {
			return list;
		}
		EntityManager em = this.entityManagerContainer().get(Unit.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Unit> root = cq.from(Unit.class);
		Predicate p = cb.equal(root.get(Unit_.superior), unit.getId());
		return em.createQuery(cq.select(root.get(Unit_.id)).where(p).orderBy(cb.asc(root.get(Unit_.orderNumber))))
				.getResultList();
	}

	public List<String> listUnitDistinguishedNameSorted(List<String> unitIds) throws Exception {
		List<Unit> list = this.entityManagerContainer().list(Unit.class, unitIds);
		list = this.sort(list);
		List<String> values = ListTools.extractProperty(list, JpaObject.DISTINGUISHEDNAME, String.class, true, true);
		return values;
	}

	public Long countBySuper(String superId) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Unit.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Unit> root = cq.from(Unit.class);
		Predicate p = cb.equal(root.get(Unit_.superior), superId);
		return em.createQuery(cq.select(cb.count(root)).where(p)).getSingleResult();
	}
}

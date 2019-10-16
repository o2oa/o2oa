package com.x.base.core.container;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;
import java.util.Objects;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.Tuple;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections4.list.TreeList;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;

import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.annotation.CheckRemove;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.entity.annotation.RestrictFlag;
import com.x.base.core.entity.tools.JpaObjectTools;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.exception.ExceptionWhen;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.tools.ListTools;
import com.x.base.core.project.tools.StringTools;

public class EntityManagerContainer extends EntityManagerContainerBasic {

	public static final Integer DEFAULT_PAGESIZE = 20;
	public static final Integer MAX_PAGESIZE = 1000;

	public EntityManagerContainer(EntityManagerContainerFactory entityManagerContainerFactory) {
		super(entityManagerContainerFactory);
	}

	public void persist(JpaObject o) throws Exception {
		// o.onPersist();
		this.get(o.getClass()).persist(o);
	}

	public void persist(JpaObject o, CheckPersistType type) throws Exception {
		if (!type.equals(CheckPersistType.none)) {
			check(o, type);
		}
		this.get(o.getClass()).persist(o);
	}

	public void remove(JpaObject o) throws Exception {
		this.get(o.getClass()).remove(o);
	}

	public void remove(JpaObject o, CheckRemoveType type) throws Exception {
		if (!type.equals(CheckRemoveType.none)) {
			check(o, type);
		}
		this.get(o.getClass()).remove(o);
	}

	@SuppressWarnings("unchecked")
	public void check(JpaObject jpa, CheckPersistType checkPersistType) throws Exception {
		jpa.onPersist();
		for (Entry<Field, CheckPersist> entry : entityManagerContainerFactory.getCheckPersistFields(jpa.getClass())
				.entrySet()) {
			Field field = entry.getKey();
			CheckPersist checkPersist = entry.getValue();
			FieldType fieldType = this.getFieldType(entry.getKey());
			// Object object = jpa.get(field.getName());
			Object object = FieldUtils.readField(field, jpa, true);
			switch (fieldType) {
			case stringValue:
				this.persistChecker.stringValue.check(field, null == object ? null : Objects.toString(object), jpa,
						checkPersist, checkPersistType);
				break;
			case stringValueList:
				this.persistChecker.stringValueList.check(field, null == object ? null : (List<String>) object, jpa,
						checkPersist, checkPersistType);
				break;
			case dateValue:
				this.persistChecker.dateValue.check(field, (null == object ? null : (Date) object), jpa, checkPersist,
						checkPersistType);
				break;
			case dateValueList:
				this.persistChecker.dateValueList.check(field, (null == object ? null : (List<Date>) object), jpa,
						checkPersist, checkPersistType);
				break;
			case booleanValue:
				this.persistChecker.booleanValue.check(field, (null == object ? null : (Boolean) object), jpa,
						checkPersist, checkPersistType);
				break;
			case booleanValueList:
				this.persistChecker.booleanValueList.check(field, (null == object ? null : (List<Boolean>) object), jpa,
						checkPersist, checkPersistType);
				break;
			case integerValue:
				this.persistChecker.integerValue.check(field, (null == object ? null : (Integer) object), jpa,
						checkPersist, checkPersistType);
				break;
			case integerValueList:
				this.persistChecker.integerValueList.check(field, (null == object ? null : (List<Integer>) object), jpa,
						checkPersist, checkPersistType);
				break;
			case doubleValue:
				this.persistChecker.doubleValue.check(field, (null == object ? null : (Double) object), jpa,
						checkPersist, checkPersistType);
				break;
			case doubleValueList:
				this.persistChecker.doubleValueList.check(field, (null == object ? null : (List<Double>) object), jpa,
						checkPersist, checkPersistType);
				break;
			case longValue:
				this.persistChecker.longValue.check(field, (null == object ? null : (Long) object), jpa, checkPersist,
						checkPersistType);
				break;
			case longValueList:
				this.persistChecker.longValueList.check(field, (null == object ? null : (List<Long>) object), jpa,
						checkPersist, checkPersistType);
				break;
			case floatValue:
				this.persistChecker.floatValue.check(field, (null == object ? null : (Float) object), jpa, checkPersist,
						checkPersistType);
				break;
			case floatValueList:
				this.persistChecker.floatValueList.check(field, (null == object ? null : (List<Float>) object), jpa,
						checkPersist, checkPersistType);
				break;
			case byteValueArray:
				this.persistChecker.byteValueArray.check(field, (null == object ? null : (byte[]) object), jpa,
						checkPersist, checkPersistType);
				break;
			case enumValue:
				break;
			default:
				break;
			}
		}
	}

	@SuppressWarnings("unchecked")
	public void check(JpaObject jpa, CheckRemoveType checkRemoveType) throws Exception {
		for (Entry<Field, CheckRemove> entry : entityManagerContainerFactory.getCheckRemoveFields(jpa.getClass())
				.entrySet()) {
			Field field = entry.getKey();
			CheckRemove checkRemove = entry.getValue();
			FieldType fieldType = this.getFieldType(entry.getKey());
			// Object object = jpa.get(field.getName());
			Object object = FieldUtils.readField(field, jpa, true);
			switch (fieldType) {
			case stringValue:
				this.removeChecker.stringValue.check(field, null == object ? null : Objects.toString(object), jpa,
						checkRemove, checkRemoveType);
				break;
			case stringValueList:
				this.removeChecker.stringValueList.check(field, null == object ? null : (List<String>) object, jpa,
						checkRemove, checkRemoveType);
				break;
			default:
				break;
			}
		}
	}

	/**
	 * 判断id在实体类中是否可用
	 * 
	 * @param id
	 * @param cls
	 * @return
	 * @throws Exception
	 */
	public <T extends JpaObject> boolean idle(String id, Class<T> cls) throws Exception {
		T t = this.fetch(id, cls, new ArrayList<String>());
		if (t == null) {
			return true;
		}
		return false;
	}

	public <T extends JpaObject> T find(String id, Class<T> cls) throws Exception {
		return this.find(id, cls, ExceptionWhen.none, false);
	}

	public <T extends JpaObject> T find(String id, Class<T> cls, ExceptionWhen exceptionWhen) throws Exception {
		return this.find(id, cls, exceptionWhen, false);
	}

	public <T extends JpaObject> T find(String id, Class<T> cls, ExceptionWhen exceptionWhen, boolean startTransaction)
			throws Exception {
		EntityManager em = startTransaction ? this.beginTransaction(cls) : this.get(cls);
		T t = null;
		/* 判断字段长度在定义范围之内否则db2会报错 */
		if (StringUtils.isNotEmpty(id) && JpaObjectTools.withinDefinedLength(id, cls, JpaObject.id_FIELDNAME)) {
			t = em.find(cls, id);
		}
		switch (exceptionWhen) {
		case not_found:
			if (null == t) {
				throw new Exception("can not find entity id: " + id + ", class: " + cls.getCanonicalName() + ".");
			}
			break;
		case found:
			if (null != t) {
				throw new Exception("entity already existed, id: " + id + ", class: " + cls.getCanonicalName() + ".");
			}
			break;
		default:
			break;
		}
		return t;
	}

	public <T extends JpaObject> T flag(String flag, Class<T> cls) throws Exception {
		return this.flag(flag, cls, this.entityManagerContainerFactory.getFlagFields(cls));
	}

	private <T extends JpaObject> T flag(String flag, Class<T> cls, List<Field> fields) throws Exception {
		EntityManager em = this.get(cls);
		if (StringUtils.isEmpty(flag) || ListTools.isEmpty(fields)) {
			return null;
		}
		T t = null;
		out: for (Field field : fields) {
			if (!JpaObjectTools.withinDefinedLength(flag, cls, field)) {
				continue;
			}
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<T> cq = cb.createQuery(cls);
			Root<T> root = cq.from(cls);
			Predicate p = cb.equal(root.get(field.getName()), flag);
			List<T> list = em.createQuery(cq.select(root).where(p).distinct(true)).setMaxResults(2).getResultList();
			switch (list.size()) {
			case 0:
				break;
			case 1:
				t = list.get(0);
				break out;
			case 2:
				throw new Exception("flag get multiple entity flag:" + flag + ", class:" + cls.getName()
						+ ", attribute:" + field.getName() + ".");
			}
		}
		return t;
	}

	public <T extends JpaObject> List<T> flag(List<String> FLAGS, Class<T> cls) throws Exception {
		return this.flag(FLAGS, cls, this.entityManagerContainerFactory.getFlagFields(cls));
	}

	private <T extends JpaObject> List<T> flag(List<String> FLAGS, Class<T> cls, List<Field> fields) throws Exception {
		if (ListTools.isEmpty(fields)) {
			throw new Exception("attributes can not be empty.");
		}
		List<T> list = new ArrayList<>();
		if (ListTools.isEmpty(FLAGS)) {
			return list;
		}
		EntityManager em = this.get(cls);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<T> cq = cb.createQuery(cls);
		Root<T> root = cq.from(cls);
		Predicate[] ps = new Predicate[fields.size()];
		for (int i = 0; i < fields.size(); i++) {
			String att = fields.get(i).getName();
			Predicate p = root.get(att)
					.in(StringTools.filterLessThanOrEqualToUtf8Length(FLAGS, JpaObjectTools.definedLength(cls, att)));
			ps[i] = p;
		}
		list.addAll(em.createQuery(cq.select(root).where(cb.or(ps))).getResultList());
		return list;
	}

	public <T extends JpaObject> T restrictFlag(String flag, Class<T> cls, String singularAttribute,
			Object restrictValue) throws Exception {
		return this.restrictFlag(flag, cls, singularAttribute, restrictValue,
				this.entityManagerContainerFactory.getRestrictFlagFields(cls));
	}

	public <T extends JpaObject> T restrictFlag(String flag, Class<T> cls, String singularAttribute,
			Object restrictValue, List<Field> fields) throws Exception {
		EntityManager em = this.get(cls);
		if (StringUtils.isEmpty(flag)) {
			return null;
		}
		T t = null;
		out: for (Field field : fields) {
			if (!JpaObjectTools.withinDefinedLength(flag, cls, field)) {
				continue;
			}
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<T> cq = cb.createQuery(cls);
			Root<T> root = cq.from(cls);
			Predicate p = cb.equal(root.get(field.getName()), flag);
			p = cb.and(p, cb.equal(root.get(singularAttribute), restrictValue));
			cq.select(root).where(p);
			List<T> list = em.createQuery(cq).setMaxResults(2).getResultList();
			switch (list.size()) {
			case 0:
				break;
			case 1:
				t = list.get(0);
				break out;
			case 2:
				throw new Exception("flag get multiple entity flag:" + flag + ", class:" + cls.getName()
						+ ", attribute:" + field.getName() + ", restrict attrubte:" + singularAttribute
						+ ", restrict value:" + restrictValue + ".");
			}
		}
		return t;
	}

	public <T extends JpaObject> List<T> listAll(Class<T> cls) throws Exception {
		EntityManager em = this.get(cls);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<T> cq = cb.createQuery(cls);
		Root<T> root = cq.from(cls);
		List<T> os = em.createQuery(cq.select(root)).getResultList();
		List<T> list = new ArrayList<>(os);
		return list;
	}

	public <T extends JpaObject> List<T> list(Class<T> cls, String... ids) throws Exception {
		return this.list(cls, false, ListTools.toList(ids));
	}

	public <T extends JpaObject> List<T> list(Class<T> cls, Collection<String> ids) throws Exception {
		return this.list(cls, false, ids);
	}

	public <T extends JpaObject> List<T> list(Class<T> cls, boolean ordered, String... ids) throws Exception {
		List<String> list = new ArrayList<>();
		for (String str : ids) {
			list.add(str);
		}
		return this.list(cls, ordered, list);
	}

	public <T extends JpaObject> List<T> list(Class<T> cls, boolean ordered, Collection<String> ids) throws Exception {
		EntityManager em = this.get(cls);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<T> cq = cb.createQuery(cls);
		Root<T> root = cq.from(cls);
		final List<String> list = ListTools.trim(new ArrayList<>(ids), true, true);
		cq.select(root).where(cb.isMember(root.get(JpaObject.id_FIELDNAME), cb.literal(list)));
		List<T> os = em.createQuery(cq).getResultList();
		if (!ordered) {
			return new ArrayList<T>(os);
		}
		List<T> ordering = new ArrayList<>(os);
		Collections.sort(ordering, new Comparator<T>() {
			public int compare(T t1, T t2) {
				return Integer.compare(list.indexOf(t1.getId()), list.indexOf(t2.getId()));
			}
		});
		return ordering;
	}

	public <T extends JpaObject> List<T> listEqual(Class<T> cls, String attribute, Object value) throws Exception {
		EntityManager em = this.get(cls);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<T> cq = cb.createQuery(cls);
		Root<T> root = cq.from(cls);
		cq.select(root).where(cb.equal(root.get(attribute), value));
		List<T> os = em.createQuery(cq).getResultList();
		List<T> list = new ArrayList<>(os);
		return list;
	}

	public <T extends JpaObject> List<T> listEqualAndEqual(Class<T> cls, String attribute, Object value,
			String otherAttribute, Object otherValue) throws Exception {
		EntityManager em = this.get(cls);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<T> cq = cb.createQuery(cls);
		Root<T> root = cq.from(cls);
		cq.select(root)
				.where(cb.and(cb.equal(root.get(attribute), value), cb.equal(root.get(otherAttribute), otherValue)));
		List<T> os = em.createQuery(cq).getResultList();
		return new TreeList<T>(os);
	}

	public <T extends JpaObject> List<T> listEqualAndEqualAndEqual(Class<T> cls, String attribute, Object value,
			String otherAttribute, Object otherValue, String thirdAttribute, Object thirdValue) throws Exception {
		EntityManager em = this.get(cls);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<T> cq = cb.createQuery(cls);
		Root<T> root = cq.from(cls);
		cq.select(root).where(cb.and(cb.equal(root.get(attribute), value),
				cb.equal(root.get(otherAttribute), otherValue), cb.equal(root.get(thirdAttribute), thirdValue)));
		List<T> os = em.createQuery(cq).getResultList();
		return new TreeList<T>(os);
	}

	public <T extends JpaObject> List<T> listEqualAndEqualAndEqualAndNotEqual(Class<T> cls, String firstAttribute,
			Object firstValue, String secondAttribute, Object secondValue, String thirdAttribute, Object thirdValue,
			String fourthAttribute, Object fourthValue) throws Exception {
		EntityManager em = this.get(cls);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<T> cq = cb.createQuery(cls);
		Root<T> root = cq.from(cls);
		cq.select(root).where(cb.and(cb.equal(root.get(firstAttribute), firstValue),
				cb.equal(root.get(secondAttribute), secondValue), cb.equal(root.get(thirdAttribute), thirdValue),
				cb.notEqual(root.get(fourthAttribute), fourthValue)));
		List<T> os = em.createQuery(cq).getResultList();
		return new TreeList<T>(os);
	}

	public <T extends JpaObject> List<T> listEqualAndNotEqual(Class<T> cls, String equalAttribute, Object equalValue,
			String notEqualAttribute, Object notEqualValue) throws Exception {
		EntityManager em = this.get(cls);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<T> cq = cb.createQuery(cls);
		Root<T> root = cq.from(cls);
		cq.select(root).where(cb.and(cb.equal(root.get(equalAttribute), equalValue),
				cb.notEqual(root.get(notEqualAttribute), notEqualValue)));
		List<T> os = em.createQuery(cq).getResultList();
		return new TreeList<T>(os);
	}

	public <T extends JpaObject> List<T> listEqualAndEqualAndNotEqual(Class<T> cls, String equalAttribute,
			Object equalValue, String otherEqualAttribute, Object otherEqualValue, String notEqualAttribute,
			Object notEqualValue) throws Exception {
		EntityManager em = this.get(cls);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<T> cq = cb.createQuery(cls);
		Root<T> root = cq.from(cls);
		cq.select(root)
				.where(cb.and(cb.equal(root.get(equalAttribute), equalValue),
						cb.equal(root.get(otherEqualAttribute), otherEqualValue),
						cb.notEqual(root.get(notEqualAttribute), notEqualValue)));
		List<T> os = em.createQuery(cq).getResultList();
		return new TreeList<T>(os);
	}

	public <T extends JpaObject, W extends Object> List<T> listEqualAndIn(Class<T> cls, String attribute, Object value,
			String otherAttribute, Collection<W> otherValues) throws Exception {
		EntityManager em = this.get(cls);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<T> cq = cb.createQuery(cls);
		Root<T> root = cq.from(cls);
		Predicate p = cb.equal(root.get(attribute), value);
		p = cb.and(p, cb.isMember(root.get(otherAttribute), cb.literal(otherValues)));
		List<T> os = em.createQuery(cq.select(root).where(p)).getResultList();
		List<T> list = new ArrayList<>(os);
		return list;
	}

	public <T extends JpaObject, W extends Object> List<T> listEqualAndGreaterThanOrEqualTo(Class<T> cls,
			String attribute, Object value, String otherAttribute, Object otherValue) throws Exception {
		EntityManager em = this.get(cls);
		Query query = em.createQuery("select o from " + cls.getName() + " o where ((o." + attribute + " = ?1) and (o."
				+ otherAttribute + " >= ?2))");
		query.setParameter(1, value);
		query.setParameter(2, otherValue);
		return new ArrayList<T>(query.getResultList());
	}

	public <T extends JpaObject, W extends Object> List<T> listBetweenAndEqual(Class<T> cls, String attribute,
			Object start, Object end, String equalAttribute, Object equalValue) throws Exception {
		EntityManager em = this.get(cls);
		Query query = em.createQuery("select o from " + cls.getName() + " o where ((o." + attribute
				+ " between ?1 and ?2) and (o." + equalAttribute + " = ?3))");
		query.setParameter(1, start);
		query.setParameter(2, end);
		query.setParameter(3, equalValue);
		return new ArrayList<T>(query.getResultList());
	}

	public <T extends JpaObject> Long count(Class<T> cls) throws Exception {
		EntityManager em = this.get(cls);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<T> root = cq.from(cls);
		return em.createQuery(cq.select(cb.count(root))).getSingleResult();
	}

	public <T extends JpaObject> Long countGreaterThan(Class<T> cls, String attribute, Object value) throws Exception {
		EntityManager em = this.get(cls);
		Query query = em
				.createQuery("select count(o) from " + cls.getName() + " o where (o." + attribute + " > " + "?1)");
		query.setParameter(1, value);
		return (Long) query.getSingleResult();
	}

	public <T extends JpaObject> Long countLessThan(Class<T> cls, String attribute, Object value) throws Exception {
		EntityManager em = this.get(cls);
		Query query = em
				.createQuery("select count(o) from " + cls.getName() + " o where (o." + attribute + " < " + "?1)");
		query.setParameter(1, value);
		return (Long) query.getSingleResult();
	}

	public <T extends JpaObject> Long countGreaterThanOrEqualTo(Class<T> cls, String attribute, Object value)
			throws Exception {
		EntityManager em = this.get(cls);
		Query query = em
				.createQuery("select count(o) from " + cls.getName() + " o where (o." + attribute + " >= " + "?1)");
		query.setParameter(1, value);
		return (Long) query.getSingleResult();
	}

	public <T extends JpaObject> Long countLessThanOrEqualTo(Class<T> cls, String attribute, Object value)
			throws Exception {
		EntityManager em = this.get(cls);
		Query query = em
				.createQuery("select count(o) from " + cls.getName() + " o where (o." + attribute + " <= " + "?1)");
		query.setParameter(1, value);
		return (Long) query.getSingleResult();
	}

	public <T extends JpaObject> Long countEqual(Class<T> cls, String attribute, Object value) throws Exception {
		EntityManager em = this.get(cls);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<T> root = cq.from(cls);
		cq.select(cb.count(root)).where(cb.equal(root.get(attribute), value));
		return em.createQuery(cq).getSingleResult();
	}

	public <T extends JpaObject> Long countNotEqual(Class<T> cls, String attribute, Object value) throws Exception {
		EntityManager em = this.get(cls);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<T> root = cq.from(cls);
		cq.select(cb.count(root)).where(cb.notEqual(root.get(attribute), value));
		return em.createQuery(cq).getSingleResult();
	}

	public <T extends JpaObject> Long countIsNull(Class<T> cls, String attribute) throws Exception {
		EntityManager em = this.get(cls);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<T> root = cq.from(cls);
		cq.select(cb.count(root)).where(cb.isNull(root.get(attribute)));
		return em.createQuery(cq).getSingleResult();
	}

	public <T extends JpaObject> Long countEqualAndEqual(Class<T> cls, String euqalAttribute, Object equalValue,
			String otherEqualAttribute, Object otherEqualValue) throws Exception {
		EntityManager em = this.get(cls);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<T> root = cq.from(cls);
		cq.select(cb.count(root)).where(cb.and(cb.equal(root.get(euqalAttribute), equalValue),
				cb.equal(root.get(otherEqualAttribute), otherEqualValue)));
		return em.createQuery(cq).getSingleResult();
	}

	public <T extends JpaObject> Long countEqualAndEqualAndEqual(Class<T> cls, String oneEuqalAttribute,
			Object oneEqualValue, String twoEqualAttribute, Object twoEqualValue, String threeEqualAttribute,
			Object threeEqualValue) throws Exception {
		EntityManager em = this.get(cls);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<T> root = cq.from(cls);
		cq.select(cb.count(root))
				.where(cb.and(cb.equal(root.get(oneEuqalAttribute), oneEqualValue),
						cb.equal(root.get(twoEqualAttribute), twoEqualValue),
						cb.equal(root.get(threeEqualAttribute), threeEqualValue)));
		return em.createQuery(cq).getSingleResult();
	}

	public <T extends JpaObject> Long countEqualAndEqualAndNotEqual(Class<T> cls, String euqalAttribute,
			Object equalValue, String otherEqualAttribute, Object otherEqualValue, String notEqualAttribute,
			Object notEqualValue) throws Exception {
		EntityManager em = this.get(cls);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<T> root = cq.from(cls);
		cq.select(cb.count(root))
				.where(cb.and(cb.equal(root.get(euqalAttribute), equalValue),
						cb.equal(root.get(otherEqualAttribute), otherEqualValue),
						cb.notEqual(root.get(notEqualAttribute), notEqualValue)));
		return em.createQuery(cq).getSingleResult();
	}

	public <T extends JpaObject> Long countEqualAndEqualAndEqualAndNotEqual(Class<T> cls, String firstEuqalAttribute,
			Object firstEqualValue, String secondEqualAttribute, Object secondEqualValue, String thirdEqualAttribute,
			Object thirdEqualValue, String notEqualAttribute, Object notEqualValue) throws Exception {
		EntityManager em = this.get(cls);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<T> root = cq.from(cls);
		cq.select(cb.count(root))
				.where(cb.and(cb.equal(root.get(firstEuqalAttribute), firstEqualValue),
						cb.equal(root.get(secondEqualAttribute), secondEqualValue),
						cb.equal(root.get(thirdEqualAttribute), thirdEqualValue),
						cb.notEqual(root.get(notEqualAttribute), notEqualValue)));
		return em.createQuery(cq).getSingleResult();
	}

	public <T extends JpaObject> Long countEqualAndNotEqual(Class<T> cls, String attribute, Object value,
			String otherAttribute, Object otherValue) throws Exception {
		EntityManager em = this.get(cls);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<T> root = cq.from(cls);
		cq.select(cb.count(root)).where(cb.and(cb.equal(root.get(attribute), value),
				cb.or(cb.isNull(root.get(otherAttribute)), cb.notEqual(root.get(otherAttribute), otherValue))));
		return em.createQuery(cq).getSingleResult();
	}

	public <T extends JpaObject> List<T> listNotEqual(Class<T> cls, String attribute, Object value) throws Exception {
		EntityManager em = this.get(cls);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<T> cq = cb.createQuery(cls);
		Root<T> root = cq.from(cls);
		cq.select(root).where(cb.or(cb.isNull(root.get(attribute)), cb.notEqual(root.get(attribute), value)));
		List<T> os = em.createQuery(cq).getResultList();
		List<T> list = new ArrayList<>(os);
		return list;
	}

	public <T extends JpaObject, W extends Object> List<T> listIn(Class<T> cls, String attribute, Collection<W> values)
			throws Exception {
		EntityManager em = this.get(cls);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<T> cq = cb.createQuery(cls);
		Root<T> root = cq.from(cls);
		cq.select(root).where(cb.isMember(root.get(attribute), cb.literal(values)));
		List<T> os = em.createQuery(cq.distinct(true)).getResultList();
		List<T> list = new ArrayList<>(os);
		return list;
	}

	public <T extends JpaObject> List<T> listIsMember(Class<T> cls, String attribute, Object value) throws Exception {
		EntityManager em = this.get(cls);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<T> cq = cb.createQuery(cls);
		Root<T> root = cq.from(cls);
		cq.select(root).where(cb.isMember(value, root.get(attribute)));
		List<T> os = em.createQuery(cq).getResultList();
		List<T> list = new ArrayList<>(os);
		return list;
	}

	public <T extends JpaObject> List<String> ids(Class<T> cls) throws Exception {
		EntityManager em = this.get(cls);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<T> root = cq.from(cls);
		cq.select(root.get(JpaObject.id_FIELDNAME));
		List<String> os = em.createQuery(cq).getResultList();
		List<String> list = new ArrayList<>(os);
		return list;
	}

	public <T extends JpaObject> List<String> idsEqual(Class<T> cls, String attribute, Object value) throws Exception {
		EntityManager em = this.get(cls);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<T> root = cq.from(cls);
		cq.select(root.get(JpaObject.id_FIELDNAME)).where(cb.equal(root.get(attribute), value));
		List<String> os = em.createQuery(cq).getResultList();
		List<String> list = new ArrayList<>(os);
		return list;
	}

	public <T extends JpaObject> List<String> idsEqualAndEqual(Class<T> cls, String attribute, Object value,
			String otherAttribute, Object otherValue) throws Exception {
		EntityManager em = this.get(cls);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<T> root = cq.from(cls);
		Predicate p = cb.equal(root.get(attribute), value);
		p = cb.and(p, cb.equal(root.get(otherAttribute), otherValue));
		cq.select(root.get(JpaObject.id_FIELDNAME)).where(p);
		List<String> os = em.createQuery(cq).getResultList();
		List<String> list = new ArrayList<>(os);
		return list;
	}

	public <T extends JpaObject> List<String> idsEqualAndEqualAndEqual(Class<T> cls, String attribute, Object value,
			String otherAttribute, Object otherValue, String thirdAttribute, Object thirdValue) throws Exception {
		EntityManager em = this.get(cls);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<T> root = cq.from(cls);
		Predicate p = cb.equal(root.get(attribute), value);
		p = cb.and(p, cb.equal(root.get(otherAttribute), otherValue));
		p = cb.and(p, cb.equal(root.get(thirdAttribute), thirdValue));
		cq.select(root.get(JpaObject.id_FIELDNAME)).where(p);
		List<String> os = em.createQuery(cq).getResultList();
		List<String> list = new ArrayList<>(os);
		return list;
	}

	public <T extends JpaObject> List<String> idsEqualAndEqualAndEqualAndNotEqual(Class<T> cls, String firstAttribute,
			Object firstValue, String secondAttribute, Object secondValue, String thirdAttribute, Object thirdValue,
			String fourthAttribute, Object fourthValue) throws Exception {
		EntityManager em = this.get(cls);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<T> root = cq.from(cls);
		Predicate p = cb.equal(root.get(firstAttribute), firstValue);
		p = cb.and(p, cb.equal(root.get(secondAttribute), secondValue));
		p = cb.and(p, cb.equal(root.get(thirdAttribute), thirdValue));
		p = cb.and(p, cb.notEqual(root.get(fourthAttribute), fourthValue));
		cq.select(root.get(JpaObject.id_FIELDNAME)).where(p);
		List<String> os = em.createQuery(cq).getResultList();
		List<String> list = new ArrayList<>(os);
		return list;
	}

	public <T extends JpaObject> List<String> idsEqualAndNotEqual(Class<T> cls, String equalAttribute,
			Object equalValue, String notEqualAttribute, Object notEqualValue) throws Exception {
		EntityManager em = this.get(cls);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<T> root = cq.from(cls);
		Predicate p = cb.equal(root.get(equalAttribute), equalValue);
		p = cb.and(p, cb.notEqual(root.get(notEqualAttribute), notEqualValue));
		cq.select(root.get(JpaObject.id_FIELDNAME)).where(p);
		List<String> os = em.createQuery(cq).getResultList();
		List<String> list = new ArrayList<>(os);
		return list;
	}

	public <T extends JpaObject> List<String> idsNotEqual(Class<T> cls, String attribute, Object value)
			throws Exception {
		EntityManager em = this.get(cls);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<T> root = cq.from(cls);
		cq.select(root.get(JpaObject.id_FIELDNAME)).where(cb.notEqual(root.get(attribute), value));
		List<String> os = em.createQuery(cq).getResultList();
		List<String> list = new ArrayList<>(os);
		return list;
	}

	public <T extends JpaObject, W extends Object> List<String> idsIn(Class<T> cls, String attribute,
			Collection<W> values) throws Exception {
		EntityManager em = this.get(cls);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<T> root = cq.from(cls);
		cq.select(root.get(JpaObject.id_FIELDNAME)).where(cb.isMember(root.get(attribute), cb.literal(values)));
		List<String> os = em.createQuery(cq.distinct(true)).getResultList();
		List<String> list = new ArrayList<>(os);
		return list;
	}

	public <T extends JpaObject, W extends Object> List<String> idsNotIn(Class<T> cls, String attribute,
			Collection<W> values) throws Exception {
		EntityManager em = this.get(cls);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<T> root = cq.from(cls);
		cq.select(root.get(JpaObject.id_FIELDNAME)).where(cb.not(root.get(attribute).in(values)));
		List<String> os = em.createQuery(cq.distinct(true)).getResultList();
		List<String> list = new ArrayList<>(os);
		return list;
	}

	public <T extends JpaObject, W extends Object> List<String> idsEqualAndNotIn(Class<T> cls, String attribute,
			Collection<W> values, String otherAttribute, Object otherValue) throws Exception {
		EntityManager em = this.get(cls);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<T> root = cq.from(cls);
		Predicate p = cb.not(root.get(attribute).in(values));
		p = cb.and(p, cb.equal(root.get(otherAttribute), otherValue));
		cq.select(root.get(JpaObject.id_FIELDNAME)).where(p);
		List<String> os = em.createQuery(cq.distinct(true)).getResultList();
		List<String> list = new ArrayList<>(os);
		return list;
	}

	public <T extends JpaObject> List<String> idsIsMember(Class<T> cls, String attribute, Object value)
			throws Exception {
		EntityManager em = this.get(cls);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<T> root = cq.from(cls);
		cq.select(root.get(JpaObject.id_FIELDNAME)).where(cb.isMember(value, root.get(attribute)));
		List<String> os = em.createQuery(cq).getResultList();
		List<String> list = new ArrayList<>(os);
		return list;
	}

	public <T extends JpaObject> List<String> idsLessThan(Class<T> cls, String attribute, Object value)
			throws Exception {
		EntityManager em = this.get(cls);
		String str = "SELECT o.id FROM " + cls.getCanonicalName() + " o where o." + attribute + " < ?1";
		TypedQuery<String> query = em.createQuery(str, String.class);
		query.setParameter(1, value);
		List<String> os = query.getResultList();
		return new ArrayList<>(os);
	}

	public <T extends JpaObject> List<String> idsGreaterThan(Class<T> cls, String attribute, Object value)
			throws Exception {
		EntityManager em = this.get(cls);
		String str = "SELECT o.id FROM " + cls.getCanonicalName() + " o where o." + attribute + " > ?1";
		TypedQuery<String> query = em.createQuery(str, String.class);
		query.setParameter(1, value);
		List<String> os = query.getResultList();
		return new ArrayList<>(os);
	}

	public <T extends JpaObject, W> List<String> idsEqualAndIn(Class<T> cls, String attribute, Object value,
			String otherAttribute, Collection<W> otherValues) throws Exception {
		EntityManager em = this.get(cls);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<T> root = cq.from(cls);
		Predicate p = cb.equal(root.get(attribute), value);
		p = cb.and(p, cb.isMember(root.get(otherAttribute), cb.literal(otherValues)));
		List<String> os = em.createQuery(cq.select(root.get(JpaObject.id_FIELDNAME)).where(p)).getResultList();
		List<String> list = new ArrayList<>(os);
		return list;
	}

	public void commit() throws Exception {
		try {
			for (EntityManager em : entityManagerMap.values()) {
				if ((null != em) && em.getTransaction().isActive()) {
					em.getTransaction().commit();
				}
			}
		} catch (Exception e) {
			throw new Exception("commit error", e);
		}
	}

	public void flush() throws Exception {
		try {
			for (EntityManager em : entityManagerMap.values()) {
				if ((null != em) && em.getTransaction().isActive()) {
					em.flush();
				}
			}
		} catch (Exception e) {
			throw new Exception("flush error", e);
		}
	}

	public <T extends JpaObject> List<T> fetchAll(Class<T> clz) throws Exception {
		return this.fetchAll(clz, JpaObject.singularAttributeField(clz, true, true));
	}

	public <T extends JpaObject, W extends GsonPropertyObject> List<W> fetchAll(Class<T> clz, WrapCopier<T, W> copier)
			throws Exception {
		return copier.copy(this.fetchAll(clz, copier.getCopyFields()));
	}

	public <T extends JpaObject> List<T> fetchAll(Class<T> clz, List<String> attributes) throws Exception {
		List<T> list = new ArrayList<>();
		List<String> fields = ListTools.trim(attributes, true, true, JpaObject.id_FIELDNAME);
		EntityManager em = this.get(clz);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Tuple> cq = cb.createQuery(Tuple.class);
		Root<T> root = cq.from(clz);
		List<Selection<?>> selections = new ArrayList<>();
		for (String str : fields) {
			selections.add(root.get(str));
		}
		for (Tuple o : em.createQuery(cq.multiselect(selections)).getResultList()) {
			T t = clz.newInstance();
			for (int i = 0; i < fields.size(); i++) {
				PropertyUtils.setProperty(t, attributes.get(i), o.get(selections.get(i)));
			}
			list.add(t);
		}
		return list;
	}

	public <T extends JpaObject> T fetch(String id, Class<T> clz) throws Exception {
		return this.fetch(id, clz, JpaObject.singularAttributeField(clz, true, true));
	}

	public <T extends JpaObject> T fetch(String id, Class<T> clz, List<String> attributes) throws Exception {
		T t = null;
		if (StringUtils.isEmpty(id)) {
			return null;
		}
		if (!attributes.contains(JpaObject.id_FIELDNAME)) {
			attributes.add(JpaObject.id_FIELDNAME);
		}
		EntityManager em = this.get(clz);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Tuple> cq = cb.createQuery(Tuple.class);
		Root<T> root = cq.from(clz);
		List<Selection<?>> selections = new ArrayList<>();
		for (String str : attributes) {
			selections.add(root.get(str));
		}
		cq.multiselect(selections).where(cb.equal(root.get(JpaObject.id_FIELDNAME), id));
		List<Tuple> list = em.createQuery(cq).setMaxResults(1).getResultList();
		if (!list.isEmpty()) {
			Tuple tuple = list.get(0);
			t = clz.newInstance();
			for (int i = 0; i < selections.size(); i++) {
				PropertyUtils.setProperty(t, attributes.get(i), tuple.get(selections.get(i)));
			}
		}
		return t;
	}

	public <T extends JpaObject, W extends GsonPropertyObject> T fetch(String id, Class<T> clz, Class<W> wrapClass)
			throws Exception {
		List<String> list = new ArrayList<>();
		for (Field field : FieldUtils.getAllFields(wrapClass)) {
			Field jpaField = FieldUtils.getField(clz, field.getName(), true);
			if ((null != jpaField) && (!Collection.class.isAssignableFrom(jpaField.getType()))) {
				list.add(field.getName());
			}
		}
		return this.fetch(id, clz, list);
	}

	public <T extends JpaObject, W extends GsonPropertyObject> W fetch(String id, WrapCopier<T, W> copier)
			throws Exception {
		T t = this.fetch(id, copier.getOrigClass(), copier.getCopyFields());
		return copier.copy(t);
	}

	public <T extends JpaObject> List<T> fetch(Collection<String> ids, Class<T> clz) throws Exception {
		return this.fetch(ids, clz, JpaObject.singularAttributeField(clz, true, true));
	}

	public <T extends JpaObject> List<T> fetch(Collection<String> ids, Class<T> clz, List<String> attributes)
			throws Exception {
		List<T> list = new ArrayList<>();
		if (ids.isEmpty()) {
			return list;
		}
		List<String> idList = new ArrayList<>(ids);
		if (!attributes.contains(JpaObject.id_FIELDNAME)) {
			attributes.add(JpaObject.id_FIELDNAME);
		}
		EntityManager em = this.get(clz);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Tuple> cq = cb.createQuery(Tuple.class);
		Root<T> root = cq.from(clz);
		List<Selection<?>> selections = new ArrayList<>();
		for (String str : attributes) {
			selections.add(root.get(str));
		}
		cq.multiselect(selections).where(root.get(JpaObject.id_FIELDNAME).in(idList));
		for (Tuple o : em.createQuery(cq).getResultList()) {
			T t = clz.newInstance();
			for (int i = 0; i < attributes.size(); i++) {
				PropertyUtils.setProperty(t, attributes.get(i), o.get(selections.get(i)));
			}
			list.add(t);
		}
		List<T> ordering = new ArrayList<>(list);
		Collections.sort(ordering, new Comparator<T>() {
			public int compare(T t1, T t2) {
				return Integer.compare(idList.indexOf(t1.getId()), idList.indexOf(t2.getId()));
			}
		});
		return ordering;
	}

	public <T extends JpaObject, W extends GsonPropertyObject> List<T> fetch(Collection<String> ids, Class<T> clz,
			Class<W> wrapClass) throws Exception {
		List<String> list = new ArrayList<>();
		for (Field field : FieldUtils.getAllFields(wrapClass)) {
			Field jpaField = FieldUtils.getField(clz, field.getName(), true);
			if ((null != jpaField) && (!Collection.class.isAssignableFrom(jpaField.getType()))) {
				list.add(field.getName());
			}
		}
		return this.fetch(ids, clz, list);
	}

	public <T extends JpaObject, W extends GsonPropertyObject> List<W> fetch(Collection<String> ids,
			WrapCopier<T, W> copier) throws Exception {
		List<T> os = this.fetch(ids, copier.getOrigClass(), copier.getCopyFields());
		return copier.copy(os);
	}

	public <T extends JpaObject> List<T> fetchEqual(Class<T> clz, String attribute, Object value) throws Exception {
		List<T> os = this.fetchEqual(clz, JpaObject.singularAttributeField(clz, true, true), attribute, value);
		return os;
	}

	public <T extends JpaObject, W extends GsonPropertyObject> List<W> fetchEqual(Class<T> clz, WrapCopier<T, W> copier,
			String attribute, Object value) throws Exception {
		List<T> os = this.fetchEqual(clz, copier.getCopyFields(), attribute, value);
		return copier.copy(os);
	}

	public <T extends JpaObject> List<T> fetchEqual(Class<T> clz, List<String> attributes, String attribute,
			Object value) throws Exception {
		List<T> list = new ArrayList<>();
		List<String> fields = ListTools.trim(attributes, true, true, JpaObject.id_FIELDNAME);
		EntityManager em = this.get(clz);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Tuple> cq = cb.createQuery(Tuple.class);
		Root<T> root = cq.from(clz);
		List<Selection<?>> selections = new ArrayList<>();
		for (String str : fields) {
			selections.add(root.get(str));
		}
		Predicate p = cb.equal(root.get(attribute), value);
		cq.multiselect(selections).where(p);
		for (Tuple o : em.createQuery(cq).getResultList()) {
			T t = clz.newInstance();
			for (int i = 0; i < fields.size(); i++) {
				PropertyUtils.setProperty(t, fields.get(i), o.get(selections.get(i)));
			}
			list.add(t);
		}
		return list;
	}

	public <T extends JpaObject> List<T> fetchEqualAndEqual(Class<T> clz, String attribute, Object value,
			String otherAttribute, Object otherValue) throws Exception {
		List<T> os = this.fetchEqualAndEqual(clz, JpaObject.singularAttributeField(clz, true, true), attribute, value,
				otherAttribute, otherValue);
		return os;
	}

	public <T extends JpaObject, W extends GsonPropertyObject> List<W> fetchEqualAndEqual(Class<T> clz,
			WrapCopier<T, W> copier, String attribute, Object value, String otherAttribute, Object otherValue)
			throws Exception {
		List<T> os = this.fetchEqualAndEqual(clz, copier.getCopyFields(), attribute, value, otherAttribute, otherValue);
		return copier.copy(os);
	}

	public <T extends JpaObject> List<T> fetchEqualAndEqual(Class<T> clz, List<String> fetchAttributes,
			String attribute, Object value, String otherAttribute, Object otherValue) throws Exception {
		List<T> list = new ArrayList<>();
		List<String> fields = ListTools.trim(fetchAttributes, true, true, JpaObject.id_FIELDNAME);
		EntityManager em = this.get(clz);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Tuple> cq = cb.createQuery(Tuple.class);
		Root<T> root = cq.from(clz);
		List<Selection<?>> selections = new ArrayList<>();
		for (String str : fields) {
			selections.add(root.get(str));
		}
		Predicate p = cb.and(cb.equal(root.get(attribute), value), cb.equal(root.get(otherAttribute), otherValue));
		cq.multiselect(selections).where(p);
		for (Tuple o : em.createQuery(cq).getResultList()) {
			T t = clz.newInstance();
			for (int i = 0; i < fields.size(); i++) {
				PropertyUtils.setProperty(t, fields.get(i), o.get(selections.get(i)));
			}
			list.add(t);
		}
		return list;
	}

	public <T extends JpaObject> List<T> fetchNotEqual(Class<T> clz, String attribute, Object value) throws Exception {
		List<T> os = this.fetchNotEqual(clz, JpaObject.singularAttributeField(clz, true, true), attribute, value);
		return os;
	}

	public <T extends JpaObject, W extends GsonPropertyObject> List<W> fetchNotEqual(Class<T> clz,
			WrapCopier<T, W> copier, String attribute, Object value) throws Exception {
		List<T> os = this.fetchNotEqual(clz, copier.getCopyFields(), attribute, value);
		return copier.copy(os);
	}

	public <T extends JpaObject> List<T> fetchNotEqual(Class<T> clz, List<String> attributes, String attribute,
			Object value) throws Exception {
		List<T> list = new ArrayList<>();
		List<String> fields = ListTools.trim(attributes, true, true, JpaObject.id_FIELDNAME);
		EntityManager em = this.get(clz);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Tuple> cq = cb.createQuery(Tuple.class);
		Root<T> root = cq.from(clz);
		List<Selection<?>> selections = new ArrayList<>();
		for (String str : fields) {
			selections.add(root.get(str));
		}
		Predicate p = cb.or(cb.isNull(root.get(attribute)), cb.notEqual(root.get(attribute), value));
		cq.multiselect(selections).where(p);
		for (Tuple o : em.createQuery(cq).getResultList()) {
			T t = clz.newInstance();
			for (int i = 0; i < fields.size(); i++) {
				PropertyUtils.setProperty(t, fields.get(i), o.get(selections.get(i)));
			}
			list.add(t);
		}
		return list;
	}

	public <T extends JpaObject, V extends Object> List<T> fetchIn(Class<T> clz, String attribute, Collection<V> values)
			throws Exception {
		List<T> os = this.fetchIn(clz, JpaObject.singularAttributeField(clz, true, true), attribute, values);
		return os;
	}

	public <T extends JpaObject, W extends GsonPropertyObject, V extends Object> List<W> fetchIn(Class<T> clz,
			WrapCopier<T, W> copier, String attribute, Collection<V> values) throws Exception {
		List<T> os = this.fetchIn(clz, copier.getCopyFields(), attribute, values);
		return copier.copy(os);
	}

	public <T extends JpaObject, W extends Object> List<T> fetchIn(Class<T> clz, List<String> attributes,
			String attribute, Collection<W> values) throws Exception {
		List<T> list = new ArrayList<>();
		List<String> fields = ListTools.trim(attributes, true, true, JpaObject.id_FIELDNAME);
		EntityManager em = this.get(clz);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Tuple> cq = cb.createQuery(Tuple.class);
		Root<T> root = cq.from(clz);
		List<Selection<?>> selections = new ArrayList<>();
		for (String str : fields) {
			selections.add(root.get(str));
		}
		Predicate p = cb.isMember(root.get(attribute), cb.literal(values));
		cq.multiselect(selections).where(p);
		for (Tuple o : em.createQuery(cq).getResultList()) {
			T t = clz.newInstance();
			for (int i = 0; i < fields.size(); i++) {
				PropertyUtils.setProperty(t, fields.get(i), o.get(selections.get(i)));
			}
			list.add(t);
		}
		return list;
	}

	public <T extends JpaObject, V extends Object> List<T> fetchEqualAndIn(Class<T> clz, String attribute, Object value,
			String otherAttribute, Collection<V> otherValues) throws Exception {
		List<T> os = this.fetchEqualAndIn(clz, JpaObject.singularAttributeField(clz, true, true), attribute, value,
				otherAttribute, otherValues);
		return os;
	}

	public <T extends JpaObject, W extends GsonPropertyObject, V extends Object> List<W> fetchEqualAndIn(Class<T> clz,
			WrapCopier<T, W> copier, String attribute, Object value, String otherAttribute, Collection<V> otherValues)
			throws Exception {
		List<T> os = this.fetchEqualAndIn(clz, copier.getCopyFields(), attribute, value, otherAttribute, otherValues);
		return copier.copy(os);
	}

	public <T extends JpaObject, V extends Object> List<T> fetchEqualAndIn(Class<T> clz, List<String> fetchAttributes,
			String attribute, Object value, String otherAttribute, Collection<V> otherValues) throws Exception {
		List<T> list = new ArrayList<>();
		List<String> fields = ListTools.trim(fetchAttributes, true, true, JpaObject.id_FIELDNAME);
		EntityManager em = this.get(clz);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Tuple> cq = cb.createQuery(Tuple.class);
		Root<T> root = cq.from(clz);
		List<Selection<?>> selections = new ArrayList<>();
		for (String str : fields) {
			selections.add(root.get(str));
		}
		Predicate p = cb.and(cb.equal(root.get(attribute), value),
				cb.isMember(root.get(otherAttribute), cb.literal(otherValues)));
		cq.multiselect(selections).where(p);
		for (Tuple o : em.createQuery(cq).getResultList()) {
			T t = clz.newInstance();
			for (int i = 0; i < fields.size(); i++) {
				PropertyUtils.setProperty(t, fields.get(i), o.get(selections.get(i)));
			}
			list.add(t);
		}
		return list;
	}

	public <T extends JpaObject> List<T> fetchIsMember(Class<T> clz, String attribute, Object value) throws Exception {
		List<T> os = this.fetchIsMember(clz, JpaObject.singularAttributeField(clz, true, true), attribute, value);
		return os;
	}

	public <T extends JpaObject, W extends GsonPropertyObject> List<W> fetchIsMember(Class<T> clz,
			WrapCopier<T, W> copier, String attribute, Object value) throws Exception {
		List<T> os = this.fetchIsMember(clz, copier.getCopyFields(), attribute, value);
		return copier.copy(os);
	}

	public <T extends JpaObject> List<T> fetchIsMember(Class<T> clz, List<String> attributes, String attribute,
			Object value) throws Exception {
		List<T> list = new ArrayList<>();
		List<String> fields = ListTools.trim(attributes, true, true, JpaObject.id_FIELDNAME);
		EntityManager em = this.get(clz);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Tuple> cq = cb.createQuery(Tuple.class);
		Root<T> root = cq.from(clz);
		List<Selection<?>> selections = new ArrayList<>();
		for (String str : fields) {
			selections.add(root.get(str));
		}
		Predicate p = cb.isMember(value, root.get(attribute));
		cq.multiselect(selections).where(p);
		for (Tuple o : em.createQuery(cq).getResultList()) {
			T t = clz.newInstance();
			for (int i = 0; i < fields.size(); i++) {
				PropertyUtils.setProperty(t, fields.get(i), o.get(selections.get(i)));
			}
			list.add(t);
		}
		return list;
	}

	public <T extends JpaObject> List<T> fetchEuqalOrIsMember(Class<T> clz, String equalAttribute, Object equalValue,
			String isMemberAttribute, Object isMemberValue) throws Exception {
		List<T> os = this.fetchEuqalOrIsMember(clz, JpaObject.singularAttributeField(clz, true, true), equalAttribute,
				equalValue, isMemberAttribute, isMemberValue);
		return os;
	}

	public <T extends JpaObject, W extends GsonPropertyObject> List<W> fetchEuqalOrIsMember(Class<T> clz,
			WrapCopier<T, W> copier, String equalAttribute, Object equalValue, String isMemberAttribute,
			Object isMemberValue) throws Exception {
		List<T> os = this.fetchEuqalOrIsMember(clz, copier.getCopyFields(), equalAttribute, equalValue,
				isMemberAttribute, isMemberValue);
		return copier.copy(os);
	}

	public <T extends JpaObject> List<T> fetchEuqalOrIsMember(Class<T> clz, List<String> attributes,
			String equalAttribute, Object equalValue, String isMemberAttribute, Object isMemberValue) throws Exception {
		List<T> list = new ArrayList<>();
		List<String> fields = ListTools.trim(attributes, true, true, JpaObject.id_FIELDNAME);
		EntityManager em = this.get(clz);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Tuple> cq = cb.createQuery(Tuple.class);
		Root<T> root = cq.from(clz);
		List<Selection<?>> selections = new ArrayList<>();
		for (String str : fields) {
			selections.add(root.get(str));
		}
		Predicate p = cb.equal(root.get(equalAttribute), equalValue);
		p = cb.or(p, cb.isMember(isMemberValue, root.get(isMemberAttribute)));
		cq.multiselect(selections).where(p);
		for (Tuple o : em.createQuery(cq).getResultList()) {
			T t = clz.newInstance();
			for (int i = 0; i < fields.size(); i++) {
				PropertyUtils.setProperty(t, fields.get(i), o.get(selections.get(i)));
			}
			list.add(t);
		}
		return list;
	}

	public <T extends JpaObject> Integer delete(Class<T> clz, Collection<String> ids) throws Exception {
		int i = 0;
		if (!ids.isEmpty()) {
			EntityManager em = this.get(clz);
			for (String id : ids) {
				if (StringUtils.isNotEmpty(id)) {
					T t = em.find(clz, id);
					if (null != t) {
						em.remove(t);
						i++;
					}
				}
			}
		}
		return i;
	}

	public <T extends JpaObject> Integer delete(Class<T> clz, String... ids) throws Exception {
		List<String> list = new ArrayList<>();
		for (String o : ids) {
			list.add(o);
		}
		return this.delete(clz, list);
	}

	public <T extends JpaObject> List<T> deleteEqual(Class<T> cls, String attribute, Object value) throws Exception {
		List<T> os = this.listEqual(cls, attribute, value);
		os.stream().forEach(o -> {
			try {
				this.remove(o);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		return os;
	}

	public <T extends JpaObject> List<T> deleteEqualAndEqual(Class<T> cls, String attribute, Object value,
			String otherAttribute, Object otherValue) throws Exception {
		List<T> os = this.listEqualAndEqual(cls, attribute, value, otherAttribute, otherValue);
		os.stream().forEach(o -> {
			try {
				this.remove(o);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		return os;
	}

	public <T extends JpaObject> boolean duplicateWithFlags(Class<T> clz, String... value) throws Exception {
		List<String> list = Arrays.asList(value);
		EntityManager em = this.get(clz);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<T> root = cq.from(clz);
		Predicate p = cb.disjunction();
		for (Field field : this.entityManagerContainerFactory.getFlagFields(clz)) {
			p = cb.or(p, root.get(field.getName())
					.in(StringTools.filterLessThanOrEqualToUtf8Length(list, JpaObjectTools.definedLength(clz, field))));
		}
		return em.createQuery(cq.select(cb.count(root)).where(p)).getSingleResult() > 0;
	}

	public <T extends JpaObject> boolean duplicateWithFlags(String excludeId, Class<T> clz, String... value)
			throws Exception {
		List<String> list = Arrays.asList(value);
		EntityManager em = this.get(clz);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<T> root = cq.from(clz);
		Predicate p = cb.disjunction();
		for (Field field : this.entityManagerContainerFactory.getFlagFields(clz)) {
			p = cb.or(p, root.get(field.getName())
					.in(StringTools.filterLessThanOrEqualToUtf8Length(list, JpaObjectTools.definedLength(clz, field))));
		}
		p = cb.and(p, cb.notEqual(root.get(JpaObject.id_FIELDNAME), excludeId));
		return em.createQuery(cq.select(cb.count(root)).where(p)).getSingleResult() > 0;
	}

	public <T extends JpaObject> boolean duplicateWithFlags(List<String> excludeIds, Class<T> clz, String... value)
			throws Exception {
		List<String> list = Arrays.asList(value);
		EntityManager em = this.get(clz);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<T> root = cq.from(clz);
		Predicate p = cb.disjunction();
		for (Field field : this.entityManagerContainerFactory.getFlagFields(clz)) {
			p = cb.or(p, root.get(field.getName())
					.in(StringTools.filterLessThanOrEqualToUtf8Length(list, JpaObjectTools.definedLength(clz, field))));
		}
		p = cb.and(p, cb.not(root.get(JpaObject.id_FIELDNAME).in(excludeIds)));
		return em.createQuery(cq.select(cb.count(root)).where(p)).getSingleResult() > 0;
	}

	public <T extends JpaObject> boolean duplicateWithRestrictFlags(Class<T> clz, String restrictSingularAttribute,
			Object restrictValue, String excludeId, List<String> values) throws Exception {
		values = ListTools.trim(values, true, true);
		EntityManager em = this.get(clz);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<T> root = cq.from(clz);
		Predicate p = cb.disjunction();
		for (Field field : this.entityManagerContainerFactory.getFlagFields(clz)) {
			p = cb.or(p, root.get(field.getName()).in(StringTools.filterLessThanOrEqualToUtf8Length(values,
					JpaObjectTools.definedLength(clz, field.getName()))));
		}
		if (StringUtils.isNotEmpty(excludeId)) {
			p = cb.and(p, cb.notEqual(root.get(JpaObject.id_FIELDNAME), excludeId));
		}
		return em.createQuery(cq.select(cb.count(root)).where(p)).getSingleResult() > 0;
	}

	public <T extends JpaObject, W> List<W> select(Class<T> cls, String attribute, Class<W> attributeClass)
			throws Exception {
		EntityManager em = this.get(cls);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<W> cq = cb.createQuery(attributeClass);
		Root<T> root = cq.from(cls);
		List<W> os = em.createQuery(cq.select(root.get(attribute))).getResultList();
		List<W> list = new ArrayList<>(os);
		return list;
	}

	public <T extends JpaObject> String conflict(Class<T> clz, T t) throws Exception {
		EntityManager em = this.get(clz);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		for (Field field : this.entityManagerContainerFactory.getFlagFields(clz)) {
			Object value = t.get(field.getName());
			if ((null != value) && StringUtils.isNotEmpty(Objects.toString(value))) {
				CriteriaQuery<Long> cq = cb.createQuery(Long.class);
				Root<T> root = cq.from(clz);
				Predicate p = cb.disjunction();
				for (Field f : this.entityManagerContainerFactory.getFlagFields(clz)) {
					p = cb.or(p, cb.equal(root.get(f.getName()), value));
				}
				p = cb.and(p, cb.notEqual(root.get(JpaObject.id_FIELDNAME), t.getId()));
				if (em.createQuery(cq.select(cb.count(root)).where(p)).getSingleResult() > 0) {
					return field.getName() + ":" + Objects.toString(value);
				}
			}
		}
		for (Field field : this.entityManagerContainerFactory.getRestrictFlagFields(clz)) {
			Object value = t.get(field.getName());
			if ((null != value) && StringUtils.isNotEmpty(Objects.toString(value))) {
				RestrictFlag restrictFlag = field.getAnnotation(RestrictFlag.class);
				if ((null != restrictFlag) && restrictFlag.fields().length > 0) {
					CriteriaQuery<Long> cq = cb.createQuery(Long.class);
					Root<T> root = cq.from(clz);
					Predicate p = cb.disjunction();
					for (Field f : this.entityManagerContainerFactory.getFlagFields(clz)) {
						Object v = t.get(f.getName());
						if ((null != v) && StringUtils.isNotEmpty(Objects.toString(v))) {
							p = cb.or(cb.equal(root.get(f.getName()), v));
						}
					}
					p = cb.and(p, cb.equal(root.get(field.getName()), value));
					p = cb.and(p, cb.notEqual(root.get(JpaObject.id_FIELDNAME), t.getId()));
					if (em.createQuery(cq.select(cb.count(root)).where(p)).getSingleResult() > 0) {
						return field.getName() + ":" + Objects.toString(value);
					}
				}
			}
		}
		return null;
	}

	public <T extends JpaObject> List<T> listEqualAndSequenceAfter(Class<T> clz, String equalAttribute,
			Object equalValue, Integer count, String sequence) throws Exception {
		EntityManager em = this.get(clz);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<T> cq = cb.createQuery(clz);
		Root<T> root = cq.from(clz);
		Predicate p = cb.equal(root.get(equalAttribute), equalValue);
		if (StringUtils.isNotEmpty(sequence)) {
			p = cb.and(p, cb.greaterThan(root.get(JpaObject.sequence_FIELDNAME), sequence));
		}
		cq.select(root).where(p).orderBy(cb.asc(root.get(JpaObject.sequence_FIELDNAME)));
		List<T> os = em.createQuery(cq).setMaxResults((count != null && count > 0) ? count : 100).getResultList();
		List<T> list = new ArrayList<>(os);
		return list;
	}

	public <T extends JpaObject> List<T> listEqualAndEqualAndSequenceAfter(Class<T> clz, String oneEqualAttribute,
			Object oneEqualValue, String twoEqualAttribute, Object twoEqualValue, Integer count, String sequence)
			throws Exception {
		EntityManager em = this.get(clz);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<T> cq = cb.createQuery(clz);
		Root<T> root = cq.from(clz);
		Predicate p = cb.equal(root.get(oneEqualAttribute), oneEqualValue);
		p = cb.and(p, cb.equal(root.get(twoEqualAttribute), twoEqualValue));
		if (StringUtils.isNotEmpty(sequence)) {
			p = cb.and(p, cb.greaterThan(root.get(JpaObject.sequence_FIELDNAME), sequence));
		}
		cq.select(root).where(p).orderBy(cb.asc(root.get(JpaObject.sequence_FIELDNAME)));
		List<T> os = em.createQuery(cq).setMaxResults((count != null && count > 0) ? count : 100).getResultList();
		List<T> list = new ArrayList<>(os);
		return list;
	}

	/* 仅在单一数据库可用 */
	public <T extends JpaObject, W extends GsonPropertyObject> List<W> fetchEqualDescPaging(Class<T> clz,
			WrapCopier<T, W> copier, String equalAttribute, Object equalValue, Integer page, Integer count,
			String orderAttribute) throws Exception {
		List<T> os = fetchEqualDescPaging(clz, copier.getCopyFields(), equalAttribute, equalValue, page, count,
				orderAttribute);
		return copier.copy(os);
	}

	/* 仅在单一数据库可用 */
	public <T extends JpaObject> List<T> fetchEqualDescPaging(Class<T> clz, String equalAttribute, Object equalValue,
			Integer page, Integer count, String orderAttribute) throws Exception {
		return fetchEqualDescPaging(clz, JpaObject.singularAttributeField(clz, true, true), equalAttribute, equalValue,
				page, count, orderAttribute);
	}

	/* 仅在单一数据库可用 */
	public <T extends JpaObject, W extends GsonPropertyObject> List<T> fetchEqualDescPaging(Class<T> clz,
			List<String> fetchAttributes, String equalAttribute, Object equalValue, Integer page, Integer pageSize,
			String orderAttribute) throws Exception {
		List<T> list = new ArrayList<>();
		int max = (pageSize == null || pageSize < 1 || pageSize > MAX_PAGESIZE) ? DEFAULT_PAGESIZE : pageSize;
		int startPosition = (page == null || page < 1) ? 0 : (page - 1) * max;
		List<String> fields = ListTools.trim(fetchAttributes, true, true, JpaObject.id_FIELDNAME);
		EntityManager em = this.get(clz);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Tuple> cq = cb.createQuery(Tuple.class);
		Root<T> root = cq.from(clz);
		List<Selection<?>> selections = new ArrayList<>();
		for (String str : fields) {
			selections.add(root.get(str));
		}
		Predicate p = cb.equal(root.get(equalAttribute), equalValue);
		cq.multiselect(selections).where(p).orderBy(cb.desc(root.get(orderAttribute)));
		for (Tuple o : em.createQuery(cq).setFirstResult(startPosition).setMaxResults(max).getResultList()) {
			T t = clz.newInstance();
			for (int i = 0; i < fields.size(); i++) {
				PropertyUtils.setProperty(t, fields.get(i), o.get(selections.get(i)));
			}
			list.add(t);
		}
		return list;
	}

	/* 仅在单一数据库可用 */
	public <T extends JpaObject, W extends GsonPropertyObject> List<W> fetchEqualAndEqualDescPaging(Class<T> clz,
			WrapCopier<T, W> copier, String equalAttribute, Object equalValue, String otherEqualAttribute,
			Object otherEqualValue, Integer page, Integer count, String orderAttribute) throws Exception {
		List<T> os = fetchEqualAndEqualDescPaging(clz, copier.getCopyFields(), equalAttribute, equalValue,
				otherEqualAttribute, otherEqualValue, page, count, orderAttribute);
		return copier.copy(os);
	}

	/* 仅在单一数据库可用 */
	public <T extends JpaObject> List<T> fetchEqualAndEqualDescPaging(Class<T> clz, String equalAttribute,
			Object equalValue, String otherEqualAttribute, Object otherEqualValue, Integer page, Integer count,
			String orderAttribute) throws Exception {
		return fetchEqualAndEqualDescPaging(clz, JpaObject.singularAttributeField(clz, true, true), equalAttribute,
				equalValue, otherEqualAttribute, otherEqualValue, page, count, orderAttribute);
	}

	/* 仅在单一数据库可用 */
	public <T extends JpaObject, W extends GsonPropertyObject> List<T> fetchEqualAndEqualDescPaging(Class<T> clz,
			List<String> fetchAttributes, String equalAttribute, Object equalValue, String otherEqualAttribute,
			Object otherEqualValue, Integer page, Integer pageSize, String orderAttribute) throws Exception {
		List<T> list = new ArrayList<>();
		int max = (pageSize == null || pageSize < 1 || pageSize > MAX_PAGESIZE) ? DEFAULT_PAGESIZE : pageSize;
		int startPosition = (page == null || page < 1) ? 0 : (page - 1) * max;
		List<String> fields = ListTools.trim(fetchAttributes, true, true, JpaObject.id_FIELDNAME);
		EntityManager em = this.get(clz);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Tuple> cq = cb.createQuery(Tuple.class);
		Root<T> root = cq.from(clz);
		List<Selection<?>> selections = new ArrayList<>();
		for (String str : fields) {
			selections.add(root.get(str));
		}
		Predicate p = cb.equal(root.get(equalAttribute), equalValue);
		p = cb.and(p, cb.equal(root.get(otherEqualAttribute), otherEqualValue));
		cq.multiselect(selections).where(p).orderBy(cb.desc(root.get(orderAttribute)));
		for (Tuple o : em.createQuery(cq).setFirstResult(startPosition).setMaxResults(max).getResultList()) {
			T t = clz.newInstance();
			for (int i = 0; i < fields.size(); i++) {
				PropertyUtils.setProperty(t, fields.get(i), o.get(selections.get(i)));
			}
			list.add(t);
		}
		return list;
	}

	/* 仅在单一数据库可用 */
	public <T extends JpaObject, W extends GsonPropertyObject> List<W> fetchEqualAndNotEqualDescPaging(Class<T> clz,
			WrapCopier<T, W> copier, String equalAttribute, Object equalValue, String otherNotEqualAttribute,
			Object otherNotEqualValue, Integer page, Integer count, String orderAttribute) throws Exception {
		List<T> os = fetchEqualAndNotEqualDescPaging(clz, copier.getCopyFields(), equalAttribute, equalValue,
				otherNotEqualAttribute, otherNotEqualValue, page, count, orderAttribute);
		return copier.copy(os);
	}

	/* 仅在单一数据库可用 */
	public <T extends JpaObject> List<T> fetchEqualAndNotEqualDescPaging(Class<T> clz, String equalAttribute,
			Object equalValue, String otherNotEqualAttribute, Object otherNotEqualValue, Integer page, Integer count,
			String orderAttribute) throws Exception {
		return fetchEqualAndNotEqualDescPaging(clz, JpaObject.singularAttributeField(clz, true, true), equalAttribute,
				equalValue, otherNotEqualAttribute, otherNotEqualValue, page, count, orderAttribute);
	}

	/* 仅在单一数据库可用 */
	public <T extends JpaObject, W extends GsonPropertyObject> List<T> fetchEqualAndNotEqualDescPaging(Class<T> clz,
			List<String> fetchAttributes, String equalAttribute, Object equalValue, String otherNotEqualAttribute,
			Object otherNotEqualValue, Integer page, Integer pageSize, String orderAttribute) throws Exception {
		List<T> list = new ArrayList<>();
		int max = (pageSize == null || pageSize < 1 || pageSize > MAX_PAGESIZE) ? DEFAULT_PAGESIZE : pageSize;
		int startPosition = (page == null || page < 1) ? 0 : (page - 1) * max;
		List<String> fields = ListTools.trim(fetchAttributes, true, true, JpaObject.id_FIELDNAME);
		EntityManager em = this.get(clz);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Tuple> cq = cb.createQuery(Tuple.class);
		Root<T> root = cq.from(clz);
		List<Selection<?>> selections = new ArrayList<>();
		for (String str : fields) {
			selections.add(root.get(str));
		}
		Predicate p = cb.equal(root.get(equalAttribute), equalValue);
		p = cb.and(p, cb.notEqual(root.get(otherNotEqualAttribute), otherNotEqualValue));
		cq.multiselect(selections).where(p).orderBy(cb.desc(root.get(orderAttribute)));
		for (Tuple o : em.createQuery(cq).setFirstResult(startPosition).setMaxResults(max).getResultList()) {
			T t = clz.newInstance();
			for (int i = 0; i < fields.size(); i++) {
				PropertyUtils.setProperty(t, fields.get(i), o.get(selections.get(i)));
			}
			list.add(t);
		}
		return list;
	}

}
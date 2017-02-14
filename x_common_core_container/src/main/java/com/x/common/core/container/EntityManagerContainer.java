package com.x.common.core.container;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;
import java.util.Objects;

import javax.persistence.EntityManager;
import javax.persistence.Tuple;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.annotation.CheckRemove;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.entity.tools.JpaObjectTools;
import com.x.base.core.exception.ExceptionWhen;
import com.x.base.core.utils.collection.ListTools;
import com.x.common.core.container.factory.EntityManagerContainerFactory;

public class EntityManagerContainer extends EntityManagerContainerBasic {

	public EntityManagerContainer(EntityManagerContainerFactory entityManagerContainerFactory) {
		super(entityManagerContainerFactory);
	}

	public void persist(JpaObject o) throws Exception {
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
		for (Entry<Field, CheckPersist> entry : entityManagerContainerFactory.getCheckPersistFields(jpa.getClass())
				.entrySet()) {
			Field field = entry.getKey();
			CheckPersist checkPersist = entry.getValue();
			FieldType fieldType = this.getFieldType(entry.getKey());
			Object object = jpa.get(field.getName());
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
			Object object = jpa.get(field.getName());
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

	public <T extends JpaObject> T find(String id, Class<T> cls) throws Exception {
		return this.find(id, cls, ExceptionWhen.none, false);
	}

	public <T extends JpaObject> T find(String id, Class<T> cls, ExceptionWhen exceptionWhen) throws Exception {
		return this.find(id, cls, exceptionWhen, false);
	}

	public <T extends JpaObject> T find(String id, Class<T> cls, ExceptionWhen exceptionWhen, boolean startTransaction)
			throws Exception {
		EntityManager em = startTransaction ? this.beginTransaction(cls) : this.get(cls);
		T t = em.find(cls, id);
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

	public <T extends JpaObject> T flag(String flag, Class<T> cls, ExceptionWhen exceptionWhen,
			boolean startTransaction, String... attributes) throws Exception {
		List<String> list = ListTools.concreteArrayList(null, true, false, attributes);
		return this.flag(flag, cls, exceptionWhen, startTransaction, list);
	}

	public <T extends JpaObject> T flag(String flag, Class<T> cls, ExceptionWhen exceptionWhen,
			boolean startTransaction, List<String> attributes) throws Exception {
		EntityManager em = startTransaction ? this.beginTransaction(cls) : this.get(cls);
		if (StringUtils.isEmpty(flag)) {
			return null;
		}
		T t = null;
		out: for (String str : attributes) {
			if (!JpaObjectTools.withinDefinedLength(flag, cls, str)) {
				continue;
			}
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<T> cq = cb.createQuery(cls);
			Root<T> root = cq.from(cls);
			Predicate p = cb.equal(root.get(str), flag);
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
						+ ", attribute:" + str + ".");
			}
		}
		switch (exceptionWhen) {
		case not_found:
			if (null == t) {
				throw new Exception("can not find entity flag: " + flag + ", class: " + cls.getCanonicalName()
						+ ", attribute:" + StringUtils.join(attributes, ",") + ".");
			}
			break;
		case found:
			if (null != t) {
				throw new Exception("entity already existed, flag: " + flag + ", class: " + cls.getCanonicalName()
						+ ", attribute:" + StringUtils.join(attributes, ",") + ".");
			}
			break;
		default:
			break;
		}
		return t;
	}

	public <T extends JpaObject> List<T> list(Class<T> cls, boolean startTransaction, String... ids) throws Exception {
		List<String> list = new ArrayList<>();
		for (String str : ids) {
			list.add(str);
		}
		return this.list(cls, startTransaction, false, list);
	}

	public <T extends JpaObject> List<T> list(Class<T> cls, boolean startTransaction, Collection<String> ids)
			throws Exception {
		return this.list(cls, startTransaction, false, ids);
	}

	public <T extends JpaObject> List<T> list(Class<T> cls, String... ids) throws Exception {
		List<String> list = new ArrayList<>();
		for (String str : ids) {
			list.add(str);
		}
		return this.list(cls, false, false, list);
	}

	public <T extends JpaObject> List<T> list(Class<T> cls, Collection<String> ids) throws Exception {
		return this.list(cls, false, false, ids);
	}

	public <T extends JpaObject> List<T> list(Class<T> cls, boolean startTransaction, boolean ordered, String... ids)
			throws Exception {
		List<String> list = new ArrayList<>();
		for (String str : ids) {
			list.add(str);
		}
		return this.list(cls, startTransaction, ordered, list);
	}

	public <T extends JpaObject> List<T> list(Class<T> cls, boolean startTransaction, boolean ordered,
			Collection<String> ids) throws Exception {
		EntityManager em = null;
		if (startTransaction) {
			em = this.beginTransaction(cls);
		} else {
			em = this.get(cls);
		}
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<T> cq = cb.createQuery(cls);
		Root<T> root = cq.from(cls);
		cq.select(root).where(root.get(JpaObject.ID).in(ids));
		List<T> list = em.createQuery(cq).getResultList();
		if (!ordered) {
			return list;
		}
		List<T> ordering = new ArrayList<>(list);
		final List<String> idsl = new ArrayList<>(ids);
		Collections.sort(ordering, new Comparator<T>() {
			public int compare(T t1, T t2) {
				return Integer.compare(idsl.indexOf(t1.getId()), idsl.indexOf(t2.getId()));
			}
		});
		return ordering;
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

	public <T extends JpaObject> T fetchAttribute(String id, Class<T> clz, List<String> attributes) throws Exception {
		T t = null;
		if (StringUtils.isEmpty(id)) {
			return null;
		}
		if (!attributes.contains(JpaObject.ID)) {
			attributes.add(JpaObject.ID);
		}
		EntityManager em = this.get(clz);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Tuple> cq = cb.createQuery(Tuple.class);
		Root<T> root = cq.from(clz);
		List<Selection<?>> selections = new ArrayList<>();
		for (String str : attributes) {
			selections.add(root.get(str));
		}
		cq.multiselect(selections).where(cb.equal(root.get(JpaObject.ID), id));
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

	public <T extends JpaObject> T fetchAttribute(String id, Class<T> clz, String... attributes) throws Exception {
		List<String> list = new ArrayList<>();
		for (String str : attributes) {
			list.add(str);
		}
		return this.fetchAttribute(id, clz, list);
	}

	public <T extends JpaObject, W extends WrapJpaObject> T fetchAttribute(String id, Class<T> clz, Class<W> wrapClass)
			throws Exception {
		List<String> list = new ArrayList<>();
		for (Field field : FieldUtils.getAllFields(wrapClass)) {
			Field jpaField = FieldUtils.getField(clz, field.getName(), true);
			if ((null != jpaField) && (!Collection.class.isAssignableFrom(jpaField.getType()))) {
				list.add(field.getName());
			}
		}
		return this.fetchAttribute(id, clz, list);
	}

	public <T extends JpaObject> List<T> fetchAttribute(Collection<String> ids, Class<T> clz, List<String> attributes)
			throws Exception {
		List<T> list = new ArrayList<>();
		if (ids.isEmpty()) {
			return list;
		}
		if (!attributes.contains(JpaObject.ID)) {
			attributes.add(JpaObject.ID);
		}
		EntityManager em = this.get(clz);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Tuple> cq = cb.createQuery(Tuple.class);
		Root<T> root = cq.from(clz);
		List<Selection<?>> selections = new ArrayList<>();
		for (String str : attributes) {
			selections.add(root.get(str));
		}
		cq.multiselect(selections).where(root.get(JpaObject.ID).in(ids));
		for (Tuple o : em.createQuery(cq).getResultList()) {
			T t = clz.newInstance();
			// t.setId(o.get(selections.get(0)).toString());
			for (int i = 0; i < attributes.size(); i++) {
				PropertyUtils.setProperty(t, attributes.get(i), o.get(selections.get(i)));
			}
			list.add(t);
		}
		return list;
	}

	public <T extends JpaObject> List<T> fetchAttribute(Collection<String> ids, Class<T> clz, String... attributes)
			throws Exception {
		List<String> list = new ArrayList<>();
		for (String str : attributes) {
			list.add(str);
		}
		return this.fetchAttribute(ids, clz, list);
	}

	public <T extends JpaObject, W extends WrapJpaObject> List<T> fetchAttribute(Collection<String> ids, Class<T> clz,
			Class<W> wrapClass) throws Exception {
		List<String> list = new ArrayList<>();
		for (Field field : FieldUtils.getAllFields(wrapClass)) {
			Field jpaField = FieldUtils.getField(clz, field.getName(), true);
			if ((null != jpaField) && (!Collection.class.isAssignableFrom(jpaField.getType()))) {
				list.add(field.getName());
			}
		}

		return this.fetchAttribute(ids, clz, list);
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

	// @Deprecated
	// public <T extends JpaObject> Integer
	// deleteNoCollectionElementObject(Class<T> clz, Collection<String> ids)
	// throws Exception {
	// int i = 0;
	// if (!ids.isEmpty()) {
	// EntityManager em = this.get(clz);
	// String str = "delete from " + clz.getCanonicalName() + " o where o.id in
	// :ids";
	// Query query = em.createQuery(str);
	// query.setParameter("ids", ids);
	// i = query.executeUpdate();
	// }
	// return i;
	// }
	//
	// @Deprecated
	// public <T extends JpaObject> Integer
	// deleteNoCollectionElementObject(Class<T> clz, String... ids) throws
	// Exception {
	// List<String> list = new ArrayList<>();
	// for (String o : ids) {
	// list.add(o);
	// }
	// return this.deleteNoCollectionElementObject(clz, list);
	// }
}
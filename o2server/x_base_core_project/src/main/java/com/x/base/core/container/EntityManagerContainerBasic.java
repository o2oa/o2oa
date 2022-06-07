package com.x.base.core.container;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;

import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.JsonProperties;

public abstract class EntityManagerContainerBasic implements AutoCloseable {

	protected EntityManagerContainerBasic(EntityManagerContainerFactory entityManagerContainerFactory) {
		this.entityManagerContainerFactory = entityManagerContainerFactory;
		this.persistChecker = new PersistChecker(this);
		this.removeChecker = new RemoveChecker(this);
	}

	protected PersistChecker persistChecker;
	protected RemoveChecker removeChecker;

	protected EntityManagerContainerFactory entityManagerContainerFactory;

	protected Map<Class<? extends JpaObject>, EntityManager> entityManagerMap = new ConcurrentHashMap<Class<? extends JpaObject>, EntityManager>();

	public <T extends JpaObject> EntityManager get(Class<T> cls) throws Exception {
		Class<T> clazz = (Class<T>) entityManagerContainerFactory.assignableFrom(cls);
		EntityManager em = fromEntityManagers(clazz);
		if (null == em) {
			em = entityManagerContainerFactory.createEntityManager(clazz);
			em.setFlushMode(FlushModeType.COMMIT);
			entityManagerMap.put(cls, em);
		}
		return em;
	}

	public <T extends JpaObject> EntityManager beginTransaction(Class<T> cls) throws Exception {
		EntityManager em = get(cls);
		if (!em.getTransaction().isActive()) {
			em.getTransaction().begin();
		}
		return em;
	}

	private EntityManager fromEntityManagers(Class<?> cls) {
		EntityManager em = entityManagerMap.get(cls);
		if ((null != em) && em.isOpen()) {
			return em;
		}
		return null;
	}

//	public void rollback() {
//		for (EntityManager em : entityManagerMap.values()) {
//			if ((null != em) && em.getTransaction().isActive()) {
//				em.getTransaction().rollback();
//			}
//		}
//	}

	public void close() {
		for (EntityManager em : entityManagerMap.values()) {
			if (null != em && em.isOpen()) {
				if (em.getTransaction().isActive()) {
					em.getTransaction().rollback();
				}
				em.close();
			}
		}
	}

	protected FieldType getFieldType(Field field) throws Exception {
		if (field.getType().isAssignableFrom(String.class)) {
			return FieldType.stringValue;
		}
		if (field.getType().isAssignableFrom(Integer.class)) {
			return FieldType.integerValue;
		}
		if (field.getType().isAssignableFrom(Double.class)) {
			return FieldType.doubleValue;
		}
		if (field.getType().isAssignableFrom(Long.class)) {
			return FieldType.longValue;
		}
		if (field.getType().isAssignableFrom(Float.class)) {
			return FieldType.floatValue;
		}
		if (field.getType().isAssignableFrom(Date.class)) {
			return FieldType.dateValue;
		}
		if (field.getType().isAssignableFrom(Boolean.class)) {
			return FieldType.booleanValue;
		}
		if (field.getType().isAssignableFrom((new byte[] {}).getClass())) {
			return FieldType.byteValueArray;
		}
		if (List.class.isAssignableFrom(field.getType())) {
			ParameterizedType parameterized = (ParameterizedType) field.getGenericType();
			Class<?> actualClass = (Class<?>) parameterized.getActualTypeArguments()[0];
			if (String.class.isAssignableFrom(actualClass)) {
				return FieldType.stringValueList;
			}
			if (Integer.class.isAssignableFrom(actualClass)) {
				return FieldType.integerValueList;
			}
			if (Double.class.isAssignableFrom(actualClass)) {
				return FieldType.doubleValueList;
			}
			if (Long.class.isAssignableFrom(actualClass)) {
				return FieldType.longValueList;
			}
			if (Float.class.isAssignableFrom(actualClass)) {
				return FieldType.floatValueList;
			}
			if (Date.class.isAssignableFrom(actualClass)) {
				return FieldType.dateValueList;
			}
			if (Boolean.class.isAssignableFrom(actualClass)) {
				return FieldType.booleanValueList;
			}
		}
		if (Map.class.isAssignableFrom(field.getType())) {
			ParameterizedType parameterized = (ParameterizedType) field.getGenericType();
			Class<?> actualClass = (Class<?>) parameterized.getActualTypeArguments()[1];
			if (String.class.isAssignableFrom(actualClass)) {
				return FieldType.stringValueMap;
			}
			if (Integer.class.isAssignableFrom(actualClass)) {
				return FieldType.integerValueMap;
			}
			if (Double.class.isAssignableFrom(actualClass)) {
				return FieldType.doubleValueMap;
			}
			if (Long.class.isAssignableFrom(actualClass)) {
				return FieldType.longValueMap;
			}
			if (Float.class.isAssignableFrom(actualClass)) {
				return FieldType.floatValueMap;
			}
			if (Date.class.isAssignableFrom(actualClass)) {
				return FieldType.dateValueMap;
			}
			if (Boolean.class.isAssignableFrom(actualClass)) {
				return FieldType.booleanValueMap;
			}
		}
		if (field.getType().isEnum()) {
			return FieldType.enumValue;
		}
		if (JsonProperties.class.isAssignableFrom(field.getType())) {
			return FieldType.JsonPropertiesValue;
		}
		throw new Exception("unknow filed type{name:" + field.getName() + "}.");
	}
}
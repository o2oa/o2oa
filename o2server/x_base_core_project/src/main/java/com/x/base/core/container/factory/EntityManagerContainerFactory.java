package com.x.base.core.container.factory;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.CheckRemove;

public class EntityManagerContainerFactory extends SliceEntityManagerContainerFactory {

	private static EntityManagerContainerFactory instance;

	public static void init(String webApplicationDirectory, List<String> entities) throws Exception {
		synchronized (EntityManagerContainerFactory.class) {
			if (instance != null) {
				EntityManagerContainerFactory.close();
			}
			instance = new EntityManagerContainerFactory(webApplicationDirectory, entities, false, null);
		}
	}

	public static void init(String webApplicationDirectory, List<String> entities, boolean loadDynamicEntityClass,
			ClassLoader classLoader) throws Exception {
		synchronized (EntityManagerContainerFactory.class) {
			if (instance != null) {
				EntityManagerContainerFactory.close();
			}
			instance = new EntityManagerContainerFactory(webApplicationDirectory, entities, loadDynamicEntityClass,
					classLoader);
		}
	}

	public static void init(String source) {
		synchronized (EntityManagerContainerFactory.class) {
			if (instance != null) {
				EntityManagerContainerFactory.close();
			}
			instance = new EntityManagerContainerFactory(source);
		}
	}

	public static void init() {
		synchronized (EntityManagerContainerFactory.class) {
			if (instance != null) {
				EntityManagerContainerFactory.close();
			}
			instance = new EntityManagerContainerFactory("");
		}
	}

	public static EntityManagerContainerFactory instance() {
		if (instance == null) {
			throw new IllegalStateException("get EntityManagerContainerFactory instance error, not initial.");
		}
		return instance;
	}

	private EntityManagerContainerFactory(String webApplicationDirectory, List<String> entities,
			boolean loadDynamicEntityClass, ClassLoader classLoader) throws Exception {
		super(webApplicationDirectory, entities, false, loadDynamicEntityClass, classLoader);
	}

	private EntityManagerContainerFactory(String source) {
		super(source);
	}

	public static void close() {
		try {
			if (instance != null) {
				for (EntityManagerFactory emf : instance.entityManagerFactoryMap.values()) {
					if (emf.isOpen()) {
						emf.close();
					}
				}
				instance.entityManagerFactoryMap.clear();
				instance.checkPersistFieldMap.clear();
				instance.checkRemoveFieldMap.clear();
			}
			/* 注销驱动程序 */
//			Enumeration<Driver> drivers = DriverManager.getDrivers();
//			while (drivers.hasMoreElements()) {
//				Driver driver = drivers.nextElement();
//				DriverManager.deregisterDriver(driver);
//			}
			/* 由于可能重新载入 */
			instance = null;
		} catch (Exception e) {
			throw new IllegalStateException("close error.", e);
		}
	}

	public EntityManagerContainer create() {
		return new EntityManagerContainer(this);
	}

	public <T extends JpaObject> EntityManager createEntityManager(Class<T> cls) {
		try {
			for (Map.Entry<Class<? extends JpaObject>, EntityManagerFactory> en : entityManagerFactoryMap.entrySet()) {
				if (en.getKey().isAssignableFrom(cls)) {
					return entityManagerFactoryMap.get(en.getKey()).createEntityManager();
				}
			}
			throw new IllegalStateException("can not createEntityManager for class " + cls.getName()
					+ ", not registed in EntityManagerContainerFactory.");
		} catch (Exception e) {
			throw new IllegalStateException("get entityManager for " + cls + " error.", e);
		}
	}

	public Map<Field, CheckPersist> getCheckPersistFields(Class<?> clazz) {
		return checkPersistFieldMap.get(assignableFrom(clazz));
	}

	public Map<Field, CheckRemove> getCheckRemoveFields(Class<?> clazz) {
		return checkRemoveFieldMap.get(assignableFrom(clazz));
	}

	public List<Field> getFlagFields(Class<?> clazz) {
		return flagMap.get(assignableFrom(clazz));
	}

	public List<Field> getRestrictFlagFields(Class<?> clazz) {
		return restrictFlagMap.get(assignableFrom(clazz));
	}

}

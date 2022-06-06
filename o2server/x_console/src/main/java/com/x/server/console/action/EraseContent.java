package com.x.server.console.action;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.collections4.ListUtils;
import org.apache.openjpa.persistence.OpenJPAPersistence;

import com.x.base.core.container.factory.PersistenceXmlHelper;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.StorageObject;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.entity.dataitem.DataItem;
import com.x.base.core.entity.dataitem.ItemCategory;
import com.x.base.core.entity.tools.JpaObjectTools;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.config.StorageMapping;
import com.x.base.core.project.config.StorageMappings;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.DateTools;
import com.x.base.core.project.tools.ListTools;

public abstract class EraseContent {

	private static final Logger LOGGER = LoggerFactory.getLogger(EraseContent.class);

	private Date start;

	private String name;

	private List<String> classNames = new ArrayList<>();

	private ItemCategory itemCategory;

	private ClassLoader classLoader;

	public abstract boolean execute();

	protected void addClass(Class<?> cls) {
		this.classNames.add(cls.getName());
	}

	protected void init(String name, ItemCategory itemCategory, ClassLoader classLoader) {
		this.name = name;
		this.itemCategory = itemCategory;
		this.start = new Date();
		this.classLoader = classLoader;
	}

	@SuppressWarnings("unchecked")
	protected void run() {
		try {
			LOGGER.print("erase {} content data: start at {}.", name, DateTools.format(start));
			this.classNames = ListUtils.intersection(this.classNames,
					new ArrayList<>(JpaObjectTools.scanContainerEntityNames(classLoader)));
			StorageMappings storageMappings = Config.storageMappings();
			File persistence = new File(Config.dir_local_temp_classes(),
					DateTools.compact(this.start) + "_eraseContent.xml");
			PersistenceXmlHelper.write(persistence.getAbsolutePath(), classNames, true, classLoader);
			for (int i = 0; i < classNames.size(); i++) {
				Class<? extends JpaObject> cls = (Class<? extends JpaObject>) Thread.currentThread()
						.getContextClassLoader().loadClass(classNames.get(i));
//				EntityManagerFactory emf = OpenJPAPersistence.createEntityManagerFactory(cls.getName(),
//						persistence.getName(),
//						PersistenceXmlHelper.properties(cls.getName(), Config.slice().getEnable()));
				EntityManagerFactory emf = OpenJPAPersistence.createEntityManagerFactory(cls.getName(),
						persistence.getName(), PersistenceXmlHelper.properties(cls.getName(), false));
				EntityManager em = emf.createEntityManager();
				if (DataItem.class.isAssignableFrom(cls)) {
					Long total = this.estimateItemCount(em, cls);
					LOGGER.print("erase {} content data:{}, total {}.", name, cls.getName(), total);
					this.eraseItem(cls, em, total);
				} else {
					Long total = this.estimateCount(em, cls);
					LOGGER.print("erase {} content data:{}, total {}.", name, cls.getName(), total);
					this.erase(cls, em, storageMappings, total);
				}
				em.close();
				emf.close();
			}
			Date end = new Date();
			LOGGER.print("erase {} content data: completed at {}, elapsed {} ms.", name, DateTools.format(end),
					(end.getTime() - start.getTime()));
		} catch (Exception e) {
			LOGGER.error(e);
		}
	}

	private <T extends JpaObject> long estimateCount(EntityManager em, Class<T> cls) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<T> root = cq.from(cls);
		cq.select(cb.count(root));
		return em.createQuery(cq).getSingleResult();
	}

	private <T extends JpaObject> long estimateItemCount(EntityManager em, Class<T> cls) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<T> root = cq.from(cls);
		Predicate p = cb.equal(root.get(DataItem.itemCategory_FIELDNAME), this.itemCategory);
		cq.select(cb.count(root)).where(p);
		return em.createQuery(cq).getSingleResult();
	}

	private <T> Long erase(Class<T> cls, EntityManager em, StorageMappings storageMappings, Long total)
			throws Exception {
		Long count = 0L;
		List<T> list = null;
		ContainerEntity containerEntity = cls.getAnnotation(ContainerEntity.class);
		do {
			if (ListTools.isNotEmpty(list)) {
				delete(em, list);
				if (StorageObject.class.isAssignableFrom(cls)) {
					deleteStorage(storageMappings, list);
				}
				if (null != list) {
					count += list.size();
				}
				em.clear();
				LOGGER.print("erase {} content data:{}, {}/{}.", name, cls.getName(), count, total);
			}
			list = list(cls, em, containerEntity);
		} while (ListTools.isNotEmpty(list));
		return count;
	}

	private <T> void deleteStorage(StorageMappings storageMappings, List<T> list) throws Exception {
		for (T t : list) {
			StorageObject storageObject = (StorageObject) t;
			String storageName = storageObject.getStorage();
			StorageMapping mapping = storageMappings.get(storageObject.getClass(), storageName);
			if (null != mapping) {
				storageObject.deleteContent(mapping);
			} else {
				LOGGER.print("can not find storage mapping {}.", storageName);
			}
		}
	}

	private <T> void delete(EntityManager em, List<T> list) {
		em.getTransaction().begin();
		for (T t : list) {
			em.remove(t);
		}
		em.getTransaction().commit();
	}

	private <T> List<T> list(Class<T> cls, EntityManager em, ContainerEntity containerEntity) {
		List<T> list;
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<T> cq = cb.createQuery(cls);
		Root<T> root = cq.from(cls);
		cq.select(root);
		list = em.createQuery(cq).setMaxResults(containerEntity.dumpSize()).getResultList();
		return list;
	}

	private <T> Long eraseItem(Class<T> cls, EntityManager em, Long total) {
		Long count = 0L;
		List<T> list = null;
		ContainerEntity containerEntity = cls.getAnnotation(ContainerEntity.class);
		do {
			if (ListTools.isNotEmpty(list)) {
				em.getTransaction().begin();
				for (T t : list) {
					em.remove(t);
				}
				em.getTransaction().commit();
				if (null != list) {
					count += list.size();
				}
				em.clear();
				LOGGER.print("erase {} content data:{}, {}/{}.", name, cls.getName(), count, total);
			}
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<T> cq = cb.createQuery(cls);
			Root<T> root = cq.from(cls);
			Predicate p = cb.equal(root.get(DataItem.itemCategory_FIELDNAME), itemCategory);
			cq.select(root).where(p);
			list = em.createQuery(cq).setMaxResults(containerEntity.dumpSize()).getResultList();
		} while (ListTools.isNotEmpty(list));
		return count;
	}

}
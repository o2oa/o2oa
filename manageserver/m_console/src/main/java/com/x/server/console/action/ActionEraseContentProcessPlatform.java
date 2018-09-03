package com.x.server.console.action;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.StorageObject;
import com.x.base.core.entity.dataitem.DataItem;
import com.x.base.core.entity.dataitem.ItemCategory;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.config.DataMappings;
import com.x.base.core.project.config.StorageMapping;
import com.x.base.core.project.config.StorageMappings;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.DateTools;
import com.x.base.core.project.tools.ListTools;
import com.x.base.core.project.tools.StringTools;
import com.x.processplatform.core.entity.content.Attachment;
import com.x.processplatform.core.entity.content.Hint;
import com.x.processplatform.core.entity.content.Read;
import com.x.processplatform.core.entity.content.ReadCompleted;
import com.x.processplatform.core.entity.content.Review;
import com.x.processplatform.core.entity.content.SerialNumber;
import com.x.processplatform.core.entity.content.Task;
import com.x.processplatform.core.entity.content.TaskCompleted;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.WorkCompleted;
import com.x.processplatform.core.entity.content.WorkLog;
import com.x.query.core.entity.Item;

public class ActionEraseContentProcessPlatform {

	private static Logger logger = LoggerFactory.getLogger(ActionEraseContentProcessPlatform.class);

	private Date start;

	private String name;

	private List<Class<?>> classes = new ArrayList<>();

	private ItemCategory itemCategory;

	public boolean execute(String password) throws Exception {
		if (!StringUtils.equals(Config.token().getPassword(), password)) {
			logger.print("password not match.");
			return false;
		}
		this.init("processPlatform", ItemCategory.pp);
		addClass(Attachment.class);
		addClass(Hint.class);
		addClass(Read.class);
		addClass(ReadCompleted.class);
		addClass(Review.class);
		addClass(SerialNumber.class);
		addClass(Task.class);
		addClass(TaskCompleted.class);
		addClass(Work.class);
		addClass(WorkCompleted.class);
		addClass(WorkLog.class);
		addClass(Item.class);
		this.run();
		return true;
	}

	protected void addClass(Class<?> cls) throws Exception {
		this.classes.add(cls);
	}

	protected void init(String name, ItemCategory itemCategory) throws Exception {
		this.name = name;
		this.itemCategory = itemCategory;
		this.start = new Date();
	}

	protected void run() throws Exception {
		logger.print("clean {} content data, start at {}.", name, DateTools.format(start));
		DataMappings mappings = Config.dataMappings();
		StorageMappings storageMappings = Config.storageMappings();
		File orm = this.createPersistenceXml(classes, mappings);
		EntityManagerContainerFactory.init(orm.getName());
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			for (int i = 0; i < classes.size(); i++) {
				Class<JpaObject> cls = (Class<JpaObject>) classes.get(i);
				EntityManager em = emc.get(cls);
				try {
					if (DataItem.class.isAssignableFrom(cls)) {
						logger.print("erase {} content data:{}, total:{}.", name, cls.getName(),
								this.estimateItemCount(em, cls));
						this.eraseItem(cls, em);
					} else {
						logger.print("erase {} content data:{}, total:{}.", name, cls.getName(),
								this.estimateCount(em, cls));
						this.erase(cls, em, storageMappings);
					}
				} finally {
					System.gc();
				}
			}
		}
		Date end = new Date();
		logger.print("erase {} completed at {}, elapsed:{}ms.", name, DateTools.format(end),
				(end.getTime() - start.getTime()));
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
		Predicate p = cb.equal(root.get(Item.itemCategory_FIELDNAME), this.itemCategory);
		cq.select(cb.count(root)).where(p);
		return em.createQuery(cq).getSingleResult();
	}

	private <T> Long erase(Class<T> cls, EntityManager em, StorageMappings storageMappings) throws Exception {
		Long count = 0L;
		List<T> list = null;
		do {
			if (ListTools.isNotEmpty(list)) {
				em.getTransaction().begin();
				for (T t : list) {
					em.remove(t);
				}
				em.getTransaction().commit();
				if (StorageObject.class.isAssignableFrom(cls)) {
					for (T t : list) {
						StorageObject storageObject = (StorageObject) t;
						String name = storageObject.getStorage();
						StorageMapping mapping = storageMappings.get(storageObject.getClass(), name);
						if (null != mapping) {
							storageObject.deleteContent(mapping);
						} else {
							System.out.println("can not find storage mapping:" + name + ".");
						}
					}
				}
				count += list.size();
			}
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<T> cq = cb.createQuery(cls);
			Root<T> root = cq.from(cls);
			cq.select(root);
			list = em.createQuery(cq).setMaxResults(1000).getResultList();
		} while (ListTools.isNotEmpty(list));
		return count;
	}

	private <T> Long eraseItem(Class<T> cls, EntityManager em) throws Exception {
		Long count = 0L;
		List<T> list = null;
		do {
			if (ListTools.isNotEmpty(list)) {
				em.getTransaction().begin();
				for (T t : list) {
					em.remove(t);
				}
				em.getTransaction().commit();
				count += list.size();
			}
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<T> cq = cb.createQuery(cls);
			Root<T> root = cq.from(cls);
			Predicate p = cb.equal(root.get(Item.itemCategory_FIELDNAME), itemCategory);
			cq.select(root).where(p);
			list = em.createQuery(cq).setMaxResults(1000).getResultList();
		} while (ListTools.isNotEmpty(list));
		return count;
	}

	/** 创建临时使用的persistence.xml 并复制到class目录下 */
	private File createPersistenceXml(List<Class<?>> clsList, DataMappings mappings) throws Exception {
		File dir = new File(Config.base(), "local/temp/classes");
		File xml = new File(dir, StringTools.uniqueToken() + "_eraseContent.xml");
		PersistenceXmlHelper.createPersistenceXml(clsList, mappings, xml);
		return xml;
	}
}
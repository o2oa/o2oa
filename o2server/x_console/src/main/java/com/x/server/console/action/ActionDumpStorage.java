package com.x.server.console.action;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.openjpa.persistence.OpenJPAPersistence;

import com.google.gson.Gson;
import com.x.base.core.container.factory.PersistenceXmlHelper;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.StorageObject;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.config.StorageMapping;
import com.x.base.core.project.config.StorageMappings;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.DateTools;
import com.x.base.core.project.tools.DefaultCharset;
import com.x.base.core.project.tools.ListTools;

public class ActionDumpStorage {

	private static Logger logger = LoggerFactory.getLogger(ActionDumpStorage.class);

	private Date start;

	private File dir;

	private DumpRestoreStorageCatalog catalog;

	private Gson pureGsonDateFormated;

	public boolean execute(String path, String password) throws Exception {
		this.start = new Date();
		if (!StringUtils.equals(Config.token().getPassword(), password)) {
			logger.print("password not match.");
			return false;
		}
		if (StringUtils.isEmpty(path)) {
			this.dir = new File(Config.base(), "local/dump/dumpStorage_" + DateTools.compact(this.start));
		} else {
			this.dir = new File(path);
			if (dir.getAbsolutePath().startsWith(Config.base())) {
				logger.print("path can not in base directory.");
				return false;
			}
		}
		FileUtils.forceMkdir(this.dir);
		FileUtils.cleanDirectory(this.dir);
		this.pureGsonDateFormated = XGsonBuilder.instance();
		this.catalog = new DumpRestoreStorageCatalog();

		List<String> storageContainerEntityNames = new ArrayList<>();
		storageContainerEntityNames.addAll((List<String>) Config.resource(Config.RESOURCE_STORAGECONTAINERENTITYNAMES));
		List<String> classNames = ListTools.includesExcludesWildcard(storageContainerEntityNames,
				Config.dumpRestoreStorage().getIncludes(), Config.dumpRestoreStorage().getExcludes());
		logger.print("dump storage find {} data to dump, start at {}.", classNames.size(), DateTools.format(start));
		StorageMappings storageMappings = Config.storageMappings();
		File persistence = new File(Config.dir_local_temp_classes(), DateTools.compact(this.start) + "_dump.xml");
		PersistenceXmlHelper.write(persistence.getAbsolutePath(), classNames);
		for (int i = 0; i < classNames.size(); i++) {
			Class<StorageObject> cls = (Class<StorageObject>) Class.forName(classNames.get(i));
			EntityManagerFactory emf = OpenJPAPersistence.createEntityManagerFactory(cls.getName(),
					persistence.getName(), PersistenceXmlHelper.properties(cls.getName(), Config.slice().getEnable()));
			EntityManager em = emf.createEntityManager();
			try {
				logger.print("dump storage({}/{}): {}, estimate count: {}, estimate size: {}M.", (i + 1),
						classNames.size(), cls.getName(), this.estimateCount(em, cls),
						(this.estimateSize(em, cls) / 1024 / 1024));
				this.dump(cls, em, storageMappings);
			} finally {
				em.close();
				emf.close();
			}
		}
		FileUtils.write(new File(dir, "catalog.json"), XGsonBuilder.instance().toJson(this.catalog),
				DefaultCharset.charset);
		logger.print(
				"dump storage completed, directory: {}, count: {}, normal: {}, empty: {}, invalidStorage: {}, size: {}M, elapsed: {} minutes.",
				dir.getAbsolutePath(), this.count(), this.normal(), this.empty(), this.invalidStorage(),
				(this.size() / 1024 / 1024), (new Date().getTime() - start.getTime()) / 1000 / 60);
		return true;
	}

	private Integer count() {
		return this.catalog.values().stream().mapToInt(DumpRestoreStorageCatalogItem::getCount).sum();
	}

	private Long size() {
		return this.catalog.values().stream().mapToLong(DumpRestoreStorageCatalogItem::getSize).sum();
	}

	private Long normal() {
		return this.catalog.values().stream().mapToLong(DumpRestoreStorageCatalogItem::getNormal).sum();
	}

	private Long empty() {
		return this.catalog.values().stream().mapToLong(DumpRestoreStorageCatalogItem::getEmpty).sum();
	}

	private Long invalidStorage() {
		return this.catalog.values().stream().mapToLong(DumpRestoreStorageCatalogItem::getInvalidStorage).sum();
	}

	private <T> long estimateCount(EntityManager em, Class<T> cls) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<T> root = cq.from(cls);
		cq.select(cb.count(root));
		return em.createQuery(cq).getSingleResult();
	}

	private <T> long estimateSize(EntityManager em, Class<T> cls) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<T> root = cq.from(cls);
		cq.select(root.get("length"));
		List<Long> list = em.createQuery(cq).getResultList();
		if (!list.isEmpty()) {
			/** 上面的语句有可能返回的是null值,所以要先过滤 */
			return list.stream().filter(o -> null != o).mapToLong(Long::longValue).sum();
		} else {
			return 0L;
		}
	}

	private <T extends StorageObject> void dump(Class<T> cls, EntityManager em, StorageMappings storageMappings)
			throws Exception {
		/** 创建最终存储文件的目录 */
		File classDirectory = new File(dir, cls.getName());
		FileUtils.forceMkdir(classDirectory);
		FileUtils.cleanDirectory(classDirectory);
		int count = 0;
		long size = 0L;
		int normal = 0;
		int invalidStorage = 0;
		int empty = 0;
		String id = "";
		String name = "";
		List<T> list = null;
		File directory = null;
		StorageMapping mapping = null;
		List<T> normalList = null;
		List<T> emptyList = null;
		List<T> invalidStorageList = null;
		do {
			list = this.list(em, cls, id, Config.dumpRestoreStorage().getBatchSize());
			if (ListTools.isNotEmpty(list)) {
				count += list.size();
				directory = new File(classDirectory, Integer.toString(count));
				FileUtils.forceMkdir(directory);
				FileUtils.cleanDirectory(directory);
				normalList = new ArrayList<T>();
				emptyList = new ArrayList<T>();
				invalidStorageList = new ArrayList<T>();
				for (T t : list) {
					name = t.getStorage();
					mapping = storageMappings.get(cls, name);
					if (null == mapping && Config.dumpRestoreStorage().getExceptionInvalidStorage()) {
						throw new Exception("can not find storageMapping class: " + cls.getName() + ", storage: " + name
								+ ", id: " + t.getId() + ", name: " + t.getName()
								+ ", set exceptionInvalidStorage to false will ignore item.");
					}
					if (null != mapping) {
						File file = new File(directory, FilenameUtils.getName(t.path()));
						if (t.existContent(mapping)) {
							try (FileOutputStream fos = new FileOutputStream(file)) {
								size += t.readContent(mapping, fos);
								normalList.add(t);
								normal++;
							}
						} else {
							emptyList.add(t);
							empty++;
						}
					} else {
						invalidStorageList.add(t);
						invalidStorage++;
					}
				}
				id = BeanUtils.getProperty(list.get(list.size() - 1), JpaObject.id_FIELDNAME);
				File file = new File(classDirectory, count + ".json");
				this.dumpWrite(file, normalList, emptyList, invalidStorageList);
			}
			em.clear();
		} while (ListTools.isNotEmpty(list));
		DumpRestoreStorageCatalogItem item = new DumpRestoreStorageCatalogItem();
		item.setCount(count);
		item.setNormal(normal);
		item.setEmpty(empty);
		item.setSize(size);
		item.setInvalidStorage(invalidStorage);
		this.catalog.put(cls.getName(), item);
		logger.print(
				"dumped storage: " + cls.getName() + ", count: " + count + ", normal: " + normal + ", invalidStorage: "
						+ invalidStorage + ", empty: " + empty + ", size: " + (size / 1024 / 1024) + "M.");
	}

	private <T extends StorageObject> void dumpWrite(File file, List<T> normalList, List<T> emptyList,
			List<T> invalidStorageList) throws Exception {
		LinkedHashMap<String, List<T>> o = new LinkedHashMap<>();
		o.put("normals", normalList);
		o.put("emptys", emptyList);
		o.put("invalidStorages", invalidStorageList);
		FileUtils.write(file, pureGsonDateFormated.toJson(o), DefaultCharset.charset);
	}

	private <T extends StorageObject> List<T> list(EntityManager em, Class<T> cls, String id, Integer size) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<T> cq = cb.createQuery(cls);
		Root<T> root = cq.from(cls);
		Predicate p = cb.conjunction();
		if (StringUtils.isNotEmpty(id)) {
			p = cb.greaterThan(root.get("id"), id);
		}
		cq.select(root).where(p).orderBy(cb.asc(root.get("id")));
		return em.createQuery(cq).setMaxResults(size).getResultList();
	}
}
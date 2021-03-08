package com.x.server.console.action;

import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.file.PathUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.openjpa.persistence.OpenJPAPersistence;

import com.google.gson.Gson;
import com.x.base.core.container.factory.PersistenceXmlHelper;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.StorageObject;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.entity.annotation.ContainerEntity.Reference;
import com.x.base.core.entity.dataitem.DataItem;
import com.x.base.core.entity.dataitem.ItemCategory;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.config.DumpRestoreData;
import com.x.base.core.project.config.StorageMapping;
import com.x.base.core.project.config.StorageMappings;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ClassLoaderTools;
import com.x.base.core.project.tools.DateTools;
import com.x.base.core.project.tools.ListTools;
import com.x.query.core.entity.Item;

public class DumpData {

	private static Logger logger = LoggerFactory.getLogger(DumpData.class);

	public boolean execute(String path) throws Exception {
		Path dir = null;
		Date start = new Date();
		if (StringUtils.isEmpty(path)) {
			dir = Paths.get(Config.base(), "local", "dump", "dumpData_" + DateTools.compact(start));
		} else {
			dir = Paths.get(path);
			if (dir.startsWith(Paths.get(Config.base()))) {
				logger.warn("path can not in base directory.");
				return false;
			}
		}
		Files.createDirectories(dir);
		Thread thread = new Thread(new RunnableImpl(dir, start));
		thread.start();
		return true;
	}

	public class RunnableImpl implements Runnable {

		private Path dir;
		private Date start;
		private DumpRestoreDataCatalog catalog;
		private Gson pureGsonDateFormated;

		public RunnableImpl(Path dir, Date start) {
			this.dir = dir;
			this.start = start;
			this.catalog = new DumpRestoreDataCatalog();
			this.pureGsonDateFormated = XGsonBuilder.instance();
		}

		private Thread dumpDataThread = new Thread(() -> {
			try {
				List<String> classNames = entities();
				logger.print("find {} data to dump, start at {}.", classNames.size(), DateTools.format(start));
				Path xml = Paths.get(Config.dir_local_temp_classes().getAbsolutePath(),
						DateTools.compact(start) + "_dump.xml");
				PersistenceXmlHelper.write(xml.toString(), classNames, false);
				StorageMappings storageMappings = Config.storageMappings();
				Stream<String> stream = BooleanUtils.isTrue(Config.dumpRestoreData().getParallel())
						? classNames.parallelStream()
						: classNames.stream();
				AtomicInteger idx = new AtomicInteger(1);
				stream.forEach(className -> {
					String nameOfThread = Thread.currentThread().getName();
					EntityManagerFactory emf = null;
					EntityManager em = null;
					try {
						Thread.currentThread().setContextClassLoader(ClassLoaderTools.urlClassLoader(false, false,
								false, false, false, Config.dir_local_temp_classes().toPath()));
						Thread.currentThread().setName(DumpData.class.getName() + ":" + className);
						@SuppressWarnings("unchecked")
						Class<JpaObject> cls = (Class<JpaObject>) Thread.currentThread().getContextClassLoader()
								.loadClass(className);
						emf = OpenJPAPersistence.createEntityManagerFactory(cls.getName(), xml.getFileName().toString(),
								PersistenceXmlHelper.properties(cls.getName(), Config.slice().getEnable()));
						em = emf.createEntityManager();
						long estimateCount = estimateCount(em, cls);
						logger.print("dump data({}/{}): {}, count: {}.", idx.getAndAdd(1), classNames.size(),
								cls.getName(), estimateCount);
						dump(cls, em, storageMappings, estimateCount);
					} catch (Exception e) {
						logger.error(new Exception(String.format("dump:%s error.", className), e));
					} finally {
						Thread.currentThread().setName(nameOfThread);
						em.close();
						emf.close();
					}
				});
				Files.write(dir.resolve("catalog.json"),
						pureGsonDateFormated.toJson(catalog).getBytes(StandardCharsets.UTF_8));
				logger.print("dump data completed, directory: {}, count: {}, elapsed: {} minutes.", dir.toString(),
						count(), (System.currentTimeMillis() - start.getTime()) / 1000 / 60);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}, "dumpDataThread");

		public void run() {
			dumpDataThread.start();
		}

		@SuppressWarnings("unchecked")
		private List<String> entities() throws Exception {
			List<String> list = new ArrayList<>();
			if (StringUtils.equals(Config.dumpRestoreData().getMode(), DumpRestoreData.TYPE_FULL)) {
				list.addAll((List<String>) Config.resource(Config.RESOURCE_CONTAINERENTITYNAMES));
				//return list;
			}else {
				for (String str : (List<String>) Config.resource(Config.RESOURCE_CONTAINERENTITYNAMES)) {
					Class<?> cls = Thread.currentThread().getContextClassLoader().loadClass(str);
					ContainerEntity containerEntity = cls.getAnnotation(ContainerEntity.class);
					if (Objects.equals(containerEntity.reference(), Reference.strong)) {
						list.add(str);
					}
				}
			}
			return ListTools.includesExcludesWildcard(list, Config.dumpRestoreData().getIncludes(),
					Config.dumpRestoreData().getExcludes());
		}

		private <T extends JpaObject> long estimateCount(EntityManager em, Class<T> cls) {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Long> cq = cb.createQuery(Long.class);
			Root<T> root = cq.from(cls);
			cq.select(cb.count(root));
			return em.createQuery(cq).getSingleResult();
		}

		private Integer count() {
			return catalog.values().stream().mapToInt(Integer::intValue).sum();
		}

		private <T> List<T> list(EntityManager em, Class<T> cls, String id, Integer size) throws Exception {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<T> cq = cb.createQuery(cls);
			Root<T> root = cq.from(cls);
			Predicate p = cb.conjunction();
			if (StringUtils.isNotEmpty(id)) {
				p = cb.greaterThan(root.get(JpaObject.id_FIELDNAME), id);
			}
			if ((Item.class == cls) && (StringUtils.isNotBlank(Config.dumpRestoreData().getItemCategory()))) {
				p = cb.and(p, cb.equal(root.get(DataItem.itemCategory_FIELDNAME),
						ItemCategory.valueOf(Config.dumpRestoreData().getItemCategory())));
			}
			cq.select(root).where(p).orderBy(cb.asc(root.get(JpaObject.id_FIELDNAME)));
			return em.createQuery(cq).setMaxResults(size).getResultList();
		}

		private <T> void dump(Class<T> cls, EntityManager em, StorageMappings storageMappings, long total)
				throws Exception {
			// 创建最终存储文件的目录
			Path directory = dir.resolve(cls.getName());
			Files.createDirectories(directory);
			PathUtils.cleanDirectory(directory);
			ContainerEntity containerEntity = cls.getAnnotation(ContainerEntity.class);
			String id = "";
			List<T> list = null;
			int count = 0;
			int loop = 1;
			int btach = (int) Math.ceil((total + 0.0) / containerEntity.dumpSize());
			do {
				list = list(em, cls, id, containerEntity.dumpSize());
				if (ListTools.isNotEmpty(list)) {
					count = count + list.size();
					id = BeanUtils.getProperty(list.get(list.size() - 1), JpaObject.id_FIELDNAME);
					if (StorageObject.class.isAssignableFrom(cls)) {
						Path sub = directory.resolve(count + "");
						Files.createDirectories(sub);
						binary(list, sub, storageMappings);
					}
					Files.write(directory.resolve(count + ".json"),
							pureGsonDateFormated.toJson(list).getBytes(StandardCharsets.UTF_8));
					logger.print("dump data {}/{} part of data:{}.", loop++, btach, cls.getName());
				}
				em.clear();
			} while (ListTools.isNotEmpty(list));
			catalog.put(cls.getName(), count);
		}

		private <T> void binary(List<T> list, Path sub, StorageMappings storageMappings) throws Exception {
			for (T t : list) {
				StorageObject s = (StorageObject) t;
				String name = s.getStorage();
				StorageMapping mapping = storageMappings.get(s.getClass(), name);
				if (null == mapping && Config.dumpRestoreData().getExceptionInvalidStorage()) {
					throw new ExceptionInvalidStorage(s);
				}
				if (null != mapping) {
					Path p = sub.resolve(FilenameUtils.getName(s.path()));
					if (s.existContent(mapping)) {
						try (OutputStream out = Files.newOutputStream(p)) {
							out.write(s.readContent(mapping));
						}
					}
				}
			}
		}
	}
}

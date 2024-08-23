package com.x.server.console.action;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.FlushModeType;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.openjpa.persistence.OpenJPAPersistence;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.x.base.core.container.factory.PersistenceXmlHelper;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.StorageObject;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.entity.dataitem.DataItem;
import com.x.base.core.entity.dataitem.ItemCategory;
import com.x.base.core.entity.tools.JpaObjectTools;
import com.x.base.core.project.bean.tuple.Pair;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.config.DumpRestoreData;
import com.x.base.core.project.config.StorageMapping;
import com.x.base.core.project.config.StorageMappings;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.DateTools;
import com.x.base.core.project.tools.ListTools;
import com.x.server.console.command.Commands;

public class RestoreData {

	private static final Logger LOGGER = LoggerFactory.getLogger(RestoreData.class);

	public boolean execute(String path) throws Exception {
		Config.resource_commandTerminatedSignal_ctl_rd().clear();

		Date start = new Date();
		if (StringUtils.isEmpty(path)) {
			LOGGER.warn("{}.", () -> "path is empty.");
		}

		Path dir = dir(path);
		Thread thread = new Thread(new RunnableImpl(dir, start));
		thread.start();
		return true;
	}

	private Path dir(String path) throws IOException, URISyntaxException {
		Path dir;
		if (BooleanUtils.isTrue(DateTools.isCompactDateTime(path))) {
			dir = Paths.get(Config.base(), "local", "dump", "dumpData_" + path);
		} else {
			dir = Paths.get(path);
			if ((!Files.exists(dir)) || (!Files.isDirectory(dir))) {
				throw new IllegalArgumentException("directory not exist: " + path + ".");
			} else if (dir.startsWith(Paths.get(Config.base())) && (!dir.startsWith(Config.path_local_temp(false)))) {
				// 如果通过路径来恢复,那么只能在 local/temp 目录下,避免程序文件被删除.
				throw new IllegalArgumentException("path can not in base directory.");
			}
		}
		return dir;
	}

	public class RunnableImpl implements Runnable {

		private Path dir;
		private Date start;
		private DumpRestoreDataCatalog catalog;
		private Gson gson;

		public RunnableImpl(Path dir, Date start) throws IOException {
			this.dir = dir;
			this.start = start;
			this.catalog = new DumpRestoreDataCatalog();
			this.gson = XGsonBuilder.instance();
			Path path = dir.resolve("catalog.json");
			this.catalog = XGsonBuilder.instance().fromJson(
					new String(Files.readAllBytes(path), StandardCharsets.UTF_8), DumpRestoreDataCatalog.class);
		}

		@Override
		public void run() {
			ClassLoader originClassLoader = Thread.currentThread().getContextClassLoader();
			try (URLClassLoader classLoader = EntityClassLoaderTools.concreteClassLoader()) {
				Thread.currentThread().setContextClassLoader(classLoader);
				Pair<List<String>, List<String>> pair = entities(catalog, classLoader);
				List<String> classNames = pair.first();
				if (pair.second().isEmpty()) {
					LOGGER.print("find: {} data to restore from path: {}.", classNames.size(), this.dir.toString());
				} else {
					LOGGER.print("find: {} data to restore from path: {}, can not find entity classes:{}.",
							classNames.size(), this.dir.toString(), gson.toJson(pair.second()));
				}
				Path xml = Paths.get(Config.dir_local_temp_classes().getAbsolutePath(),
						DateTools.compact(start) + "_restore.xml");
				PersistenceXmlHelper.write(xml.toString(), classNames, true, classLoader);
				AtomicInteger idx = new AtomicInteger(1);
				AtomicLong total = new AtomicLong(0);
//				Stream<String> stream = BooleanUtils.isTrue(Config.dumpRestoreData().getParallel())
//						? classNames.parallelStream()
//						: classNames.stream();
				classNames.forEach(className -> {
					try {
						@SuppressWarnings("unchecked")
						Class<JpaObject> cls = (Class<JpaObject>) classLoader.loadClass(className);
						LOGGER.print("restore data({}/{}): {}.", idx.getAndAdd(1), classNames.size(), cls.getName());
						long size = restore(cls, Config.dumpRestoreData().getAttachStorage(), xml);
						total.getAndAdd(size);
						cls = null;
					} catch (Exception e) {
						LOGGER.error(new Exception(String.format("restore:%s error.", className), e));
					}
				});
				LOGGER.print("restore data completed, directory: {}, count: {}, total: {}, elapsed: {} minutes.",
						dir.toString(), idx.get(), total.longValue(),
						(System.currentTimeMillis() - start.getTime()) / 1000 / 60);
				Config.resource_commandTerminatedSignal_ctl_rd().put(Commands.COMMANDTERMINATEDSIGNAL_SUCCESS);
			} catch (InterruptedException ie) {
				Thread.currentThread().interrupt();
				LOGGER.error(ie);
			} catch (Exception e) {
				try {
					Config.resource_commandTerminatedSignal_ctl_rd().put(e.getMessage());
				} catch (InterruptedException interruptedException) {
					Thread.currentThread().interrupt();
					LOGGER.error(interruptedException);
				} catch (Exception exception) {
					exception.printStackTrace();
				}
				LOGGER.error(e);
			} finally {
				Thread.currentThread().setContextClassLoader(originClassLoader);
			}
		}

		private long restore(Class<? extends JpaObject> cls, boolean attachStorage, Path xml) throws Exception {
			EntityManagerFactory emf = OpenJPAPersistence.createEntityManagerFactory(cls.getName(),
					xml.getFileName().toString(), PersistenceXmlHelper.properties(cls.getName(), false));
			EntityManager em = emf.createEntityManager();
			em.setFlushMode(FlushModeType.COMMIT);
			AtomicLong count = new AtomicLong(0);
			AtomicInteger batch = new AtomicInteger(1);
			try {
				Path directory = dir.resolve(cls.getName());
				if ((!Files.exists(directory)) || (!Files.isDirectory(directory))) {
					throw new ExceptionDirectoryNotExist(directory);
				}
				StorageMappings storageMappings = Config.storageMappings();
				if (!Objects.equals(Config.dumpRestoreData().getRestoreOverride(),
						DumpRestoreData.RESTOREOVERRIDE_SKIPEXISTED)) {
					this.clean(cls, em, storageMappings, cls.getAnnotation(ContainerEntity.class));
					em.clear();
				}
				List<Path> paths = this.list(directory);
				paths.stream().forEach(o -> {
					LOGGER.print("restore {}/{} part of data:{}.", batch.getAndAdd(1), paths.size(), cls.getName());
					try {
						restore(cls, o, em, attachStorage, storageMappings, count);
					} catch (Exception e) {
						LOGGER.error(new Exception(String.format("restore error with file:%s.", o.toString()), e));
					}
				});
				LOGGER.print("restore data: {} completed, count: {}.", cls.getName(), count.intValue());
			} catch (Exception e) {
				LOGGER.error(e);
			} finally {
				em.close();
				emf.close();
			}
			return count.longValue();
		}

		private void restore(Class<?> cls, Path o, EntityManager em, boolean attachStorage,
				StorageMappings storageMappings, AtomicLong count) throws Exception {
			em.getTransaction().begin();
			JsonArray raws = this.convert(o);
			for (JsonElement json : raws) {
				Object t = gson.fromJson(json, cls);
				if (Objects.equals(Config.dumpRestoreData().getRestoreOverride(),
						DumpRestoreData.RESTOREOVERRIDE_SKIPEXISTED)
						&& (null != em.find(cls, BeanUtils.getProperty(t, JpaObject.id_FIELDNAME)))) {
					continue;
				}
				if (StorageObject.class.isAssignableFrom(cls) && attachStorage) {
					Path sub = o.resolveSibling(FilenameUtils.getBaseName(o.getFileName().toString()));
					this.binary(t, cls, sub, storageMappings);
				}
				em.persist(t);
				count.getAndAdd(1);
			}
			em.getTransaction().commit();
			em.clear();
		}

		private List<Path> list(Path directory) throws IOException {
			List<Path> list = new ArrayList<>();
			try (Stream<Path> stream = Files.list(directory)) {
				stream.filter(p -> StringUtils.endsWithIgnoreCase(p.getFileName().toString(), ".json"))
						.sorted((Path p1, Path p2) -> {
							Integer i1 = Integer.parseInt(FilenameUtils.getBaseName(p1.getFileName().toString()));
							Integer i2 = Integer.parseInt(FilenameUtils.getBaseName(p2.getFileName().toString()));
							return i1.compareTo(i2);
						}).forEach(list::add);
			}
			return list;
		}

		private Pair<List<String>, List<String>> entities(DumpRestoreDataCatalog catalog, ClassLoader classLoader)
				throws ClassNotFoundException {
			List<String> containerEntityNames = new ArrayList<>(JpaObjectTools.scanContainerEntityNames(classLoader));
			if (StringUtils.equalsIgnoreCase(DumpRestoreData.MODE_LITE, Config.dumpRestoreData().getMode())) {
				containerEntityNames = containerEntityNames.stream().filter(o -> {
					try {
						ContainerEntity containerEntity = classLoader.loadClass(o).getAnnotation(ContainerEntity.class);
						return Objects.equals(containerEntity.reference(), ContainerEntity.Reference.strong);
					} catch (Exception e) {
						LOGGER.error(e);
					}
					return false;
				}).collect(Collectors.toList());
			}
			List<String> classNames = new ArrayList<>(catalog.keySet());
			classNames = ListTools.includesExcludesWildcard(classNames, Config.dumpRestoreData().getIncludes(),
					Config.dumpRestoreData().getExcludes());
			return Pair.of(ListUtils.intersection(classNames, containerEntityNames),
					ListUtils.subtract(classNames, containerEntityNames));

		}

		@SuppressWarnings("unchecked")
		private void binary(Object o, Class<?> cls, Path sub, StorageMappings storageMappings) throws Exception {
			StorageObject so = (StorageObject) o;
			Path path = sub.resolve(Paths.get(so.path()).getFileName());
			if (!Files.exists(path)) {
				LOGGER.warn("storage file not exist, path:{}, storageObject:{}.", path, so);
				return;
			}
			StorageMapping mapping = null;
			if (BooleanUtils.isTrue(Config.dumpRestoreData().getRedistributeStorage())) {
				mapping = storageMappings.random((Class<StorageObject>) cls);
			} else {
				mapping = storageMappings.get((Class<StorageObject>) cls, so.getStorage());
			}
			if (null == mapping) {
				if (BooleanUtils.isTrue(Config.dumpRestoreData().getExceptionInvalidStorage())) {
					throw new ExceptionInvalidStorage(so);
				} else {
					LOGGER.warn("can not access storage:{}, storageObject:{}.", so.getStorage(), so);
				}
			} else {
				try (InputStream input = Files.newInputStream(path)) {
					so.saveContent(mapping, input, so.getName());
				} catch (Exception e) {
					LOGGER.error(e);
				}
			}
		}

		private JsonArray convert(Path path) throws IOException {
			// 必须先转换成 jsonElement 不能直接转成泛型T,如果直接转会有类型不匹配比如Integer变成了Double
			String json = new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
			JsonElement jsonElement = gson.fromJson(json, JsonElement.class);
			return jsonElement.getAsJsonArray();
		}

		private <T extends JpaObject> void clean(Class<T> cls, EntityManager em, StorageMappings storageMappings,
				ContainerEntity containerEntity) throws Exception {
			duplicateIdEntityToSingleBeforeClean(cls, em, containerEntity);
			List<T> list = null;
			do {
				list = batchListExistEntity(cls, em, containerEntity);
				if (ListTools.isNotEmpty(list)) {
					em.getTransaction().begin();
					remove(cls, em, storageMappings, list);
					em.getTransaction().commit();
					em.clear();
				}
			} while (ListTools.isNotEmpty(list));
		}

		private <T extends JpaObject> void duplicateIdEntityToSingleBeforeClean(Class<T> cls, EntityManager em,
				ContainerEntity containerEntity) throws Exception {
			String latestId = null;
			Set<String> idCheckSet = new HashSet<>();
			Set<String> duplicateSet = new HashSet<>();
			List<String> list = null;
			do {
				list = loopIds(cls, em, containerEntity, latestId);
				if (ListTools.isNotEmpty(list)) {
					for (String id : list) {
						if (!idCheckSet.add(id)) {
							duplicateSet.add(id);
						}
					}
					latestId = list.get(list.size() - 1);
				}
			} while (ListTools.isNotEmpty(list));
			if (!duplicateSet.isEmpty()) {
				cleanDuplicateIdEntity(cls, em, duplicateSet);
			}
		}

		private <T extends JpaObject> void cleanDuplicateIdEntity(Class<T> cls, EntityManager em,
				Collection<String> ids) {
			for (String id : ids) {
				LOGGER.info("clear duplicate id entity, class:{}, id:{}.", cls.getName(), id);
				cleanDuplicateIdEntity(cls, em, id);
			}
		}

		private <T extends JpaObject> void cleanDuplicateIdEntity(Class<T> cls, EntityManager em, String id) {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<T> cq = cb.createQuery(cls);
			Root<T> root = cq.from(cls);
			Predicate p = cb.equal(root.get(JpaObject.id_FIELDNAME), id);
			cq.select(root).where(p).orderBy(cb.asc(root.get(JpaObject.createTime_FIELDNAME)));
			List<T> os = em.createQuery(cq).getResultList();
			if (!os.isEmpty()) {
				T t = os.get(os.size() - 1);
				em.detach(t);
				em.getTransaction().begin();
				Query query = em.createQuery("DELETE FROM " + cls.getName() + " o WHERE o.id = :id");
				query.setParameter(JpaObject.id_FIELDNAME, id);
				query.executeUpdate();
				em.getTransaction().commit();
				em.clear();
				em.getTransaction().begin();
				em.persist(t);
				em.getTransaction().commit();
				em.clear();
			}
		}

		private <T> void remove(Class<T> cls, EntityManager em, StorageMappings storageMappings, List<T> list)
				throws Exception {
			for (T t : list) {
				if (BooleanUtils.isTrue(Config.dumpRestoreData().getAttachStorage())
						&& StorageObject.class.isAssignableFrom(cls)) {
					StorageObject so = (StorageObject) t;
					@SuppressWarnings("unchecked")
					StorageMapping mapping = storageMappings.get((Class<StorageObject>) cls, so.getStorage());
					if (null != mapping) {
						so.deleteContent(mapping);
					}
				}
				em.remove(t);
			}
		}

		private <T> List<T> batchListExistEntity(Class<T> cls, EntityManager em, ContainerEntity containerEntity) {
			List<T> list;
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<T> cq = cb.createQuery(cls);
			Root<T> root = cq.from(cls);
			Predicate p = cb.conjunction();
			if (StringUtils.equals(cls.getName(), "com.x.query.core.entity.Item")
					&& (StringUtils.isNotBlank(Config.dumpRestoreData().getItemCategory()))) {
				p = cb.and(p, cb.equal(root.get(DataItem.itemCategory_FIELDNAME),
						ItemCategory.valueOf(Config.dumpRestoreData().getItemCategory())));
			}
			list = em.createQuery(cq.select(root).where(p)).setMaxResults(containerEntity.dumpSize()).getResultList();
			return list;
		}

		private <T extends JpaObject> List<String> loopIds(Class<T> cls, EntityManager em,
				ContainerEntity containerEntity, String latestId) {
			List<String> list;
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<String> cq = cb.createQuery(String.class);
			Root<T> root = cq.from(cls);
			javax.persistence.criteria.Path<String> idPath = root.get(JpaObject.id_FIELDNAME);
			Predicate p = cb.conjunction();
			if (null != latestId) {
				p = cb.greaterThan(idPath, latestId);
			}
			if (StringUtils.equals(cls.getName(), "com.x.query.core.entity.Item")
					&& (StringUtils.isNotBlank(Config.dumpRestoreData().getItemCategory()))) {
				p = cb.and(p, cb.equal(root.get(DataItem.itemCategory_FIELDNAME),
						ItemCategory.valueOf(Config.dumpRestoreData().getItemCategory())));
			}
			list = em.createQuery(cq.select(idPath).where(p).orderBy(cb.asc(idPath)))
					.setMaxResults(containerEntity.dumpSize()).getResultList();
			return list;
		}
	}
}
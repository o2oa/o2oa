package com.x.server.console.action;

import java.io.File;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.openjpa.persistence.OpenJPAPersistence;

import com.google.gson.Gson;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.config.DataMapping;
import com.x.base.core.project.config.DataMappings;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.DateTools;
import com.x.base.core.project.tools.DefaultCharset;
import com.x.base.core.project.tools.ListTools;

public class ActionDumpData {

	private static Logger logger = LoggerFactory.getLogger(ActionDumpData.class);

	private Date start;

	private File dir;

	private DumpRestoreDataCatalog catalog;

	private Gson pureGsonDateFormated;

	private void init() throws Exception {
		this.start = new Date();
		this.dir = new File(Config.base(), "local/dump/dumpData_" + DateTools.compact(this.start));
		this.catalog = new DumpRestoreDataCatalog();
		pureGsonDateFormated = XGsonBuilder.instance();
		FileUtils.forceMkdir(this.dir);
		FileUtils.cleanDirectory(this.dir);
	}

	public boolean execute(String password) throws Exception {
		this.init();
		if (!StringUtils.equals(Config.token().getPassword(), password)) {
			logger.print("password not match.");
			return false;
		}
		List<Class<?>> classes = PersistenceXmlHelper.listClassWithIncludesExcludes(
				PersistenceXmlHelper.listDataClassName(), Config.dumpRestoreData().getIncludes(),
				Config.dumpRestoreData().getExcludes());
		logger.print("dump data find {} data to dump, start at {}.", classes.size(), DateTools.format(start));
		DataMappings mappings = Config.dataMappings();
		for (int i = 0; i < classes.size(); i++) {
			Class<JpaObject> cls = (Class<JpaObject>) classes.get(i);
			List<DataMapping> sources = mappings.get(cls.getName());
			if (ListTools.isEmpty(sources)) {
				throw new Exception("can not get datamapping of class:" + cls.getName() + ".");
			}
			EntityManagerFactory emf = OpenJPAPersistence.createEntityManagerFactory(cls.getName(),
					this.createPersistenceXml(cls, sources).getName());
			EntityManager em = emf.createEntityManager();
			try {
				logger.print("dump data({}/{}): {}, count: {}.", (i + 1), classes.size(), cls.getName(),
						this.estimateCount(em, cls));
				// System.out.println("dump data(" + (i + 1) + "/" + classes.size() + "): " +
				// cls.getName() + ", count: "
				// + this.estimateCount(em, cls) + ".");
				this.dump(cls, em);
			} finally {
				em.close();
				emf.close();
			}
			System.gc();
		}
		FileUtils.write(new File(dir, "catalog.json"), pureGsonDateFormated.toJson(this.catalog),
				DefaultCharset.charset);
		logger.print("dump data completed, directory: {}, count: {}, elapsed: {} minutes.", dir.getAbsolutePath(),
				this.count(), (new Date().getTime() - start.getTime()) / 1000 / 60);
		// System.out.println("dump data completed, directory: " + dir.getAbsolutePath()
		// + ", count: " + this.count()
		// + ", elapsed: " + (new Date().getTime() - start.getTime()) / 1000 / 60 + "
		// minutes.");
		return true;
	}

	private <T extends JpaObject> long estimateCount(EntityManager em, Class<T> cls) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<T> root = cq.from(cls);
		cq.select(cb.count(root));
		return em.createQuery(cq).getSingleResult();
	}

	private Integer count() {
		return this.catalog.values().stream().mapToInt(Integer::intValue).sum();
	}

	/** 创建临时使用的persistence.xml 并复制到class目录下 */
	private <T> File createPersistenceXml(Class<T> cls, List<DataMapping> sources) throws Exception {
		File xml = new File(dir, cls.getName() + "_dump.xml");
		PersistenceXmlHelper.createPersistenceXml(cls, sources, xml);
		File tempFile = File.createTempFile(DateTools.compact(this.start), ".xml",
				new File(Config.base(), "local/temp/classes"));
		FileUtils.copyFile(xml, tempFile);
		return tempFile;
	}

	private <T> void dump(Class<T> cls, EntityManager em) throws Exception {
		/** 创建最终存储文件的目录 */
		File directory = new File(dir, cls.getName());
		FileUtils.forceMkdir(directory);
		FileUtils.cleanDirectory(directory);
		int count = 0;
		int size = Config.dumpRestoreData().getBatchSize();
		String id = "";
		List<T> list = null;
		do {
			list = this.list(em, cls, id, size);
			if (ListTools.isNotEmpty(list)) {
				count = count + list.size();
				id = BeanUtils.getProperty(list.get(list.size() - 1), JpaObject.id_FIELDNAME);
				File file = new File(directory, count + ".json");
				FileUtils.write(file, pureGsonDateFormated.toJson(list), DefaultCharset.charset);
			}
			em.clear();
		} while (ListTools.isNotEmpty(list));
		this.catalog.put(cls.getName(), count);
	}

	private <T> List<T> list(EntityManager em, Class<T> cls, String id, Integer size) {
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
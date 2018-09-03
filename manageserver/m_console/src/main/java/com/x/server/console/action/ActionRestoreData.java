package com.x.server.console.action;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.FlushModeType;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.openjpa.persistence.OpenJPAPersistence;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.ibm.db2.jcc.am.SqlException;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.config.DataMapping;
import com.x.base.core.project.config.DataMappings;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.BaseTools;
import com.x.base.core.project.tools.DateTools;
import com.x.base.core.project.tools.DefaultCharset;
import com.x.base.core.project.tools.ListTools;

public class ActionRestoreData {

	private static Logger logger = LoggerFactory.getLogger(ActionRestoreData.class);

	private Date start;

	private File dir;

	private DumpRestoreDataCatalog catalog;

	private Gson pureGsonDateFormated;

	private void init(Date date) throws Exception {
		this.start = new Date();
		this.dir = new File(Config.base(), "local/dump/dumpData_" + DateTools.compact(date));
		this.catalog = BaseTools.readObject("local/dump/dumpData_" + DateTools.compact(date) + "/catalog.json",
				DumpRestoreDataCatalog.class);
		pureGsonDateFormated = XGsonBuilder.instance();
	}

	public boolean execute(Date date, String password) throws Exception {
		this.init(date);
		if (!StringUtils.equals(Config.token().getPassword(), password)) {
			logger.print("password not mactch.");
			return false;
		}
		List<Class<?>> classes = PersistenceXmlHelper.listClassWithIncludesExcludes(
				new ArrayList<String>(this.catalog.keySet()), Config.dumpRestoreData().getIncludes(),
				Config.dumpRestoreData().getExcludes());
		logger.print("find: {} data to restore, dump time: {}.", classes.size(), DateTools.format(date));
		DataMappings mappings = Config.dataMappings();
		long count = 0;
		for (int i = 0; i < classes.size(); i++) {
			Class<JpaObject> cls = (Class<JpaObject>) classes.get(i);
			List<DataMapping> sources = mappings.get(cls.getName());
			if (ListTools.isEmpty(sources)) {
				throw new Exception("can not get datamapping of class:" + cls.getName() + ".");
			}
			EntityManagerFactory emf = OpenJPAPersistence.createEntityManagerFactory(cls.getName(),
					this.createPersistenceXml(cls, sources).getName());
			EntityManager em = emf.createEntityManager();
			em.setFlushMode(FlushModeType.COMMIT);
			try {
				logger.print("restore data({}/{}): {}, count: {}.", (i + 1), classes.size(), cls.getName(),
						catalog.get(cls.getName()));
				// System.out.println("restore data(" + (i + 1) + "/" + classes.size() + "): " +
				// cls.getName()
				// + ", count: " + catalog.get(cls.getName()) + ".");
				count = count + this.store(cls, em);
			} finally {
				em.close();
				emf.close();
			}
		}
		logger.print("restore data completed, total count: {}, elapsed: {} minutes.", count,
				(new Date().getTime() - start.getTime()) / 1000 / 60);
		// System.out.println("restore data completed, total count: " + count + ",
		// elapsed: "
		// + (new Date().getTime() - start.getTime()) / 1000 / 60 + " minutes.");
		return true;
	}

	/** 创建临时使用的persistence.xml 并复制到class目录下 */
	private <T> File createPersistenceXml(Class<T> cls, List<DataMapping> sources) throws Exception {
		File xml = new File(dir, cls.getName() + "_restore.xml");
		PersistenceXmlHelper.createPersistenceXml(cls, sources, xml);
		File tempFile = File.createTempFile(DateTools.compact(this.start), ".xml",
				new File(Config.base(), "local/temp/classes"));
		FileUtils.copyFile(xml, tempFile);
		return tempFile;
	}

	private <T> long store(Class<T> cls, EntityManager em) throws Exception {
		File directory = new File(this.dir, cls.getName());
		if ((!directory.exists()) || (!directory.isDirectory())) {
			throw new Exception("can not find directory: " + directory.getAbsolutePath() + ".");
		}
		long count = 0;
		List<File> files = new ArrayList<>(FileUtils.listFiles(directory, new String[] { "json" }, false));
		/** 对文件进行排序,和dump的时候的顺序保持一直 */
		Collections.sort(files, new Comparator<File>() {
			public int compare(File o1, File o2) {
				String n1 = FilenameUtils.getBaseName(o1.getName());
				String n2 = FilenameUtils.getBaseName(o2.getName());
				Integer i1 = Integer.parseInt(n1);
				Integer i2 = Integer.parseInt(n2);
				return i1.compareTo(i2);
			}
		});
		/** 尽量在最后进行清空操作 */
		this.clean(cls, em);
		File file = null;
		for (int i = 0; i < files.size(); i++) {
			file = files.get(i);
			System.out.println("restoring " + (i + 1) + "/" + files.size() + " part of data: " + cls.getName() + ".");
			JsonArray raws = this.convert(file);
			em.getTransaction().begin();
			for (JsonElement o : raws) {
				T t = pureGsonDateFormated.fromJson(o, cls);
				em.persist(t);
				count++;
			}
			em.getTransaction().commit();
			em.clear();
		}
		// JsonArray raws = this.convert(new File("d://2000.json"));
		// for (JsonElement o : raws) {
		// em.getTransaction().begin();
		// T t = pureGsonDateFormated.fromJson(o, cls);
		// System.err.println(t);
		// em.persist(t);
		//
		// em.getTransaction().commit();
		// count++;
		// }
		System.out.println("restore data: " + cls.getName() + " completed, count: " + count + ".");
		return count;

	}

	private JsonArray convert(File file) throws Exception {
		/** 必须先转换成 jsonElement 不能直接转成泛型T,如果直接转会有类型不匹配比如Integer变成了Double */
		String json = FileUtils.readFileToString(file, DefaultCharset.charset);
		JsonElement jsonElement = pureGsonDateFormated.fromJson(json, JsonElement.class);
		return jsonElement.getAsJsonArray();
	}

	private <T> void clean(Class<T> cls, EntityManager em) throws Exception {
		List<T> list = null;
		do {
			if (ListTools.isNotEmpty(list)) {
				em.getTransaction().begin();
				for (T t : list) {
					em.remove(t);
				}
				em.getTransaction().commit();
			}
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<T> cq = cb.createQuery(cls);
			Root<T> root = cq.from(cls);
			cq.select(root);
			list = em.createQuery(cq).setMaxResults(Config.dumpRestoreData().getBatchSize()).getResultList();
		} while (ListTools.isNotEmpty(list));
	}
}
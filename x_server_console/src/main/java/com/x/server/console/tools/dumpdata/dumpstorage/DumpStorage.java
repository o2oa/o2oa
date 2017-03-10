package com.x.server.console.tools.dumpdata.dumpstorage;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.apache.openjpa.persistence.OpenJPAPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.x.base.core.BaseTools;
import com.x.base.core.DefaultCharset;
import com.x.base.core.Packages;
import com.x.base.core.entity.Storage;
import com.x.base.core.entity.StorageObject;
import com.x.base.core.gson.XGsonBuilder;
import com.x.base.core.project.server.Config;
import com.x.base.core.project.server.DataMapping;
import com.x.base.core.project.server.DataMappings;
import com.x.base.core.project.server.StorageMapping;
import com.x.base.core.project.server.StorageMappings;
import com.x.base.core.utils.DateTools;
import com.x.base.core.utils.ListTools;
import com.x.server.console.tools.dumpdata.DumpRestoreHelper;

import io.github.lukehutch.fastclasspathscanner.FastClasspathScanner;
import io.github.lukehutch.fastclasspathscanner.scanner.ScanResult;

public class DumpStorage {

	private static Logger logger = LoggerFactory.getLogger(DumpStorage.class);

	private Date start;

	private String base;

	private File dir;

	private DumpStorageCatalog catalog;

	public void execute() throws Exception {
		this.base = BaseTools.getBasePath();
		this.start = new Date();
		this.dir = new File(base, "local/dump/dumpStorage_" + DateTools.compact(this.start));
		this.catalog = new DumpStorageCatalog();
		FileUtils.forceMkdir(this.dir);
		List<Class<? extends StorageObject>> classes = this.listEntityToDump();
		DataMappings dataMappings = Config.dataMappings();
		StorageMappings storageMappings = Config.storageMappings();
		logger.info("dump storage find {} to dump.", classes.size());
		for (int i = 0; i < classes.size(); i++) {
			Class<? extends StorageObject> cls = classes.get(i);
			logger.info("dump storage({}/{}):{}.", (i + 1), classes.size(), cls.getName());
			List<DataMapping> sources = dataMappings.get(cls.getName());
			if (ListTools.isEmpty(sources)) {
				throw new Exception("can not get datamapping of class:" + cls.getName() + ".");
			}
			this.dump(cls, sources, storageMappings);
		}
		FileUtils.write(new File(dir, "catalog.json"), XGsonBuilder.pureGsonDateFormated().toJson(this.catalog),
				DefaultCharset.charset);
		System.out.println("dump storage completed, directory:" + dir.getAbsolutePath());
	}

	private List<Class<? extends StorageObject>> listEntityToDump() throws Exception {
		ScanResult scanResult = new FastClasspathScanner(Packages.PREFIX).scan();
		List<String> all = scanResult.getNamesOfClassesWithAnnotation(Storage.class);
		List<Class<? extends StorageObject>> classes = new ArrayList<>();
		if (ListTools.isNotEmpty(Config.dumpRestoreStorage().getIncludes())) {
			for (String str : all) {
				Class<?> cls = Class.forName(str);
				if (Config.dumpRestoreStorage().getIncludes().contains(cls.getAnnotation(Storage.class).type())) {
					classes.add((Class<? extends StorageObject>) cls);
				}
			}
		} else {
			for (String str : all) {
				Class<?> cls = Class.forName(str);
				classes.add((Class<? extends StorageObject>) cls);
			}
		}
		if (ListTools.isNotEmpty(Config.dumpRestoreStorage().getExcludes())) {
			List<Class<? extends StorageObject>> retains = new ArrayList<>();
			for (Class<? extends StorageObject> cls : classes) {
				if (!Config.dumpRestoreStorage().getExcludes().contains(cls.getAnnotation(Storage.class).type())) {
					retains.add((Class<? extends StorageObject>) cls);
				}
			}
			classes = retains;
		}
		return classes;
	}

	private <T extends StorageObject> void dump(Class<T> cls, List<DataMapping> sources,
			StorageMappings storageMappings) throws Exception {
		File folder = new File(dir, cls.getName());
		FileUtils.forceMkdir(folder);
		FileUtils.cleanDirectory(folder);
		File xml = new File(folder, cls.getName() + "_dump.xml");
		DumpRestoreHelper.createPersistenceXml(cls, sources, xml);
		File tempFile = File.createTempFile(DateTools.compact(this.start), ".xml",
				new File(base, "local/temp/classes"));
		FileUtils.copyFile(xml, tempFile);
		EntityManagerFactory emf = OpenJPAPersistence.createEntityManagerFactory(cls.getName(), tempFile.getName());
		EntityManager em = emf.createEntityManager();
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<T> cq = cb.createQuery(cls);
		Root<T> root = cq.from(cls);
		cq.select(root);
		List<T> list = em.createQuery(cq).getResultList();
		em.close();
		emf.close();
		Gson gson = XGsonBuilder.pureGsonDateFormated();
		for (T t : list) {
			String storage = Objects.toString(PropertyUtils.getProperty(t, "storage"));
			StorageMapping mapping = storageMappings.get(t.getClass(), storage);
			MethodUtils.invokeMethod(t, "dumpContent", mapping);
			File file = new File(folder, PropertyUtils.getProperty(t, "id") + ".json");
			FileUtils.write(file, gson.toJson(t), DefaultCharset.charset);
		}
		this.catalog.put(cls.getName(), list.size());
	}

}

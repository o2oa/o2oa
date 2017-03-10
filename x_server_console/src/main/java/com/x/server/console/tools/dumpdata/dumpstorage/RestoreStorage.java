package com.x.server.console.tools.dumpdata.dumpstorage;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.FlushModeType;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
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

public class RestoreStorage {

	private static Logger logger = LoggerFactory.getLogger(RestoreStorage.class);

	private Date start;

	private String base;

	private File dir;

	private DumpStorageCatalog catalog;

	public void execute(Date date) throws Exception {
		this.base = BaseTools.getBasePath();
		this.start = new Date();
		this.dir = new File(this.base, "local/dump/dumpStorage_" + DateTools.compact(date));
		this.catalog = BaseTools.readObject("local/dump/dumpStorage_" + DateTools.compact(date) + "/catalog.json",
				DumpStorageCatalog.class);
		List<Class<? extends StorageObject>> classes = this.listEntityToRestore();
		logger.info("restore storage find {} to store.", classes.size());
		DataMappings mappings = Config.dataMappings();
		StorageMappings storageMappings = Config.storageMappings();
		for (int i = 0; i < classes.size(); i++) {
			Class<? extends StorageObject> cls = classes.get(i);
			logger.info("restore storage({}/{}):{}.", (i + 1), classes.size(), cls.getName());
			List<DataMapping> sources = mappings.get(cls.getName());
			if (ListTools.isEmpty(sources)) {
				throw new Exception("can not get datamapping of class:" + cls.getName() + ".");
			}
			this.store(cls, sources, storageMappings);
		}
		logger.info("restore storage completed.");
	}

	private List<Class<? extends StorageObject>> listEntityToRestore() throws Exception {
		ScanResult scanResult = new FastClasspathScanner(Packages.PREFIX).scan();
		List<Class<? extends StorageObject>> list = new ArrayList<>();
		List<String> all = scanResult.getNamesOfClassesWithAnnotation(Storage.class);
		for (String str : this.catalog.keySet()) {
			if (all.contains(str)) {
				list.add((Class<? extends StorageObject>) Class.forName(str));
			}
		}
		return list;
	}

	private <T extends StorageObject> void store(Class<T> cls, List<DataMapping> sources,
			StorageMappings storageMappings) throws Exception {
		Gson gson = XGsonBuilder.pureGsonDateFormated();
		File folder = new File(dir, cls.getName());
		File xml = new File(folder, cls.getName() + "_restore.xml");
		DumpRestoreHelper.createPersistenceXml(cls, sources, xml);
		File tempFile = File.createTempFile(DateTools.compact(this.start), ".xml",
				new File(base, "local/temp/classes"));
		FileUtils.copyFile(xml, tempFile);
		EntityManagerFactory emf = OpenJPAPersistence.createEntityManagerFactory(cls.getName(), tempFile.getName());
		EntityManager em = emf.createEntityManager();
		em.setFlushMode(FlushModeType.COMMIT);
		this.removeExists(cls, em);
		for (File file : this.listFile(folder)) {
			String json = FileUtils.readFileToString(file, DefaultCharset.charset);
			T t = gson.fromJson(json, cls);
			byte[] bytes = (byte[]) PropertyUtils.getProperty(t, "bytes");
			String name = Objects.toString(PropertyUtils.getProperty(t, "name"));
			String storage = Objects.toString(PropertyUtils.getProperty(t, "storage"));
			StorageMapping mapping = null;
			if (BooleanUtils.isNotTrue(Config.dumpRestoreStorage().getRedistribute())) {
				mapping = storageMappings.random(t.getClass());
			} else {
				mapping = storageMappings.get(t.getClass(), storage);
			}
			ByteArrayInputStream input = new ByteArrayInputStream(bytes);
			MethodUtils.invokeMethod(t, "saveContent", mapping, input, name);
			em.getTransaction().begin();
			em.persist(t);
			em.getTransaction().commit();
		}
		em.close();
		emf.close();
	}

	private <T> void removeExists(Class<T> cls, EntityManager em) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<T> cq = cb.createQuery(cls);
		Root<T> root = cq.from(cls);
		cq.select(root);
		List<T> list = em.createQuery(cq).getResultList();
		em.getTransaction().begin();
		for (T t : list) {
			em.remove(t);
		}
		em.getTransaction().commit();
	}

	private File[] listFile(File folder) throws Exception {
		File[] files = folder.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				if (StringUtils.equals(FilenameUtils.getExtension(name), "json")) {
					return true;
				}
				return false;
			}
		});
		return files;
	}
}
package com.x.server.console.tools.dumpdata;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.apache.commons.collections4.ListUtils;
import org.apache.commons.io.FileUtils;
import org.apache.openjpa.persistence.OpenJPAPersistence;

import com.x.base.core.BaseTools;
import com.x.base.core.DefaultCharset;
import com.x.base.core.Packages;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.gson.XGsonBuilder;
import com.x.base.core.project.server.Config;
import com.x.base.core.project.server.DataMapping;
import com.x.base.core.project.server.DataMappings;
import com.x.base.core.utils.DateTools;
import com.x.base.core.utils.ListTools;

import io.github.lukehutch.fastclasspathscanner.FastClasspathScanner;
import io.github.lukehutch.fastclasspathscanner.scanner.ScanResult;

public class DumpData {

	private Date start;

	private String base;

	private File dir;

	private DumpDataCatalog catalog;

	public void execute() throws Exception {
		this.base = BaseTools.getBasePath();
		this.start = new Date();
		this.dir = new File(base, "local/dump/dumpData_" + DateTools.compact(this.start));
		this.catalog = new DumpDataCatalog();
		FileUtils.forceMkdir(this.dir);
		List<Class<?>> classes = this.listEntityToDump();
		DataMappings mappings = Config.dataMappings();
		for (int i = 0; i < classes.size(); i++) {
			Class<?> clz = classes.get(i);
			List<DataMapping> sources = mappings.get(clz.getName());
			if (ListTools.isEmpty(sources)) {
				throw new Exception("can not get datamapping of class:" + clz.getName() + ".");
			}
			System.out.println("dump data(" + (i + 1) + "/" + classes.size() + "):" + clz.getName() + ".");
			this.dump(clz, sources);
		}
		FileUtils.write(new File(dir, "catalog.json"), XGsonBuilder.pureGsonDateFormated().toJson(this.catalog),
				DefaultCharset.charset);
		System.out.println("dump data completed, directory:" + dir.getAbsolutePath());
	}

	private List<Class<?>> listEntityToDump() throws Exception {
		ScanResult scanResult = new FastClasspathScanner(Packages.PREFIX).scan();
		List<String> all = scanResult.getNamesOfClassesWithAnnotation(ContainerEntity.class);
		if (ListTools.isNotEmpty(Config.dumpRestoreData().getIncludes())) {
			all = ListUtils.intersection(all, Config.dumpRestoreData().getIncludes());
		}
		if (ListTools.isNotEmpty(Config.dumpRestoreData().getExcludes())) {
			all = ListUtils.subtract(all, Config.dumpRestoreData().getExcludes());
		}
		List<Class<?>> list = new ArrayList<>();
		for (String str : all) {
			list.add(Class.forName(str));
		}
		return list;
	}

	private <T> void dump(Class<T> cls, List<DataMapping> sources) throws Exception {
		File xml = new File(dir, cls.getName() + "_dump.xml");
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
		File file = new File(dir, cls.getName() + ".json");
		FileUtils.write(file, XGsonBuilder.pureGsonDateFormated().toJson(list), DefaultCharset.charset);
		em.close();
		emf.close();
		this.catalog.put(cls.getName(), list.size());
	}

}

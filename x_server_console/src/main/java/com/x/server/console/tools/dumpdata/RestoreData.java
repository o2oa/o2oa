package com.x.server.console.tools.dumpdata;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.FlushModeType;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.apache.commons.io.FileUtils;
import org.apache.openjpa.persistence.OpenJPAPersistence;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
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

public class RestoreData {

	private Date start;

	private String base;

	private File dir;

	private DumpDataCatalog catalog;

	public void execute(Date date) throws Exception {
		this.base = BaseTools.getBasePath();
		this.start = new Date();
		this.dir = new File(this.base, "local/dump/dumpData_" + DateTools.compact(date));
		this.catalog = BaseTools.readObject("local/dump/dumpData_" + DateTools.compact(date) + "/catalog.json",
				DumpDataCatalog.class);
		List<Class<?>> classes = this.listEntityToRestore();
		DataMappings mappings = Config.dataMappings();
		for (int i = 0; i < classes.size(); i++) {
			Class<?> clz = classes.get(i);
			List<DataMapping> sources = mappings.get(clz.getName());
			if (ListTools.isEmpty(sources)) {
				throw new Exception("can not get datamapping of class:" + clz.getName() + ".");
			}
			System.out.println("restore data(" + (i + 1) + "/" + classes.size() + "):" + clz.getName() + ".");
			this.store(clz, sources);
		}
		System.out.println("restore data completed.");
	}

	private List<Class<?>> listEntityToRestore() throws Exception {
		ScanResult scanResult = new FastClasspathScanner(Packages.PREFIX).scan();
		List<Class<?>> list = new ArrayList<>();
		List<String> all = scanResult.getNamesOfClassesWithAnnotation(ContainerEntity.class);
		for (String str : this.catalog.keySet()) {
			if (all.contains(str)) {
				list.add(Class.forName(str));
			}
		}
		return list;
	}

	private <T> void store(Class<T> cls, List<DataMapping> sources) throws Exception {
		Gson gson = XGsonBuilder.pureGsonDateFormated();
		File xml = new File(dir, cls.getName() + "_restore.xml");
		DumpRestoreHelper.createPersistenceXml(cls, sources, xml);
		File tempFile = File.createTempFile(DateTools.compact(this.start), ".xml",
				new File(base, "local/temp/classes"));
		FileUtils.copyFile(xml, tempFile);
		EntityManagerFactory emf = OpenJPAPersistence.createEntityManagerFactory(cls.getName(), tempFile.getName());
		EntityManager em = emf.createEntityManager();
		em.setFlushMode(FlushModeType.COMMIT);
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
		Type type = new TypeToken<ArrayList<JsonElement>>() {
		}.getType();
		String json = FileUtils.readFileToString(new File(dir, cls.getName() + ".json"), DefaultCharset.charset);
		List<JsonElement> raws = gson.fromJson(json, type);
		em.getTransaction().begin();
		for (JsonElement o : raws) {
			T t = gson.fromJson(o, cls);
			em.persist(t);
		}
		em.getTransaction().commit();
		em.close();
		emf.close();
	}
}

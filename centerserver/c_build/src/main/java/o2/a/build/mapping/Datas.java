package o2.a.build.mapping;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ObjectUtils;

import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.project.Packages;
import com.x.base.core.project.gson.XGsonBuilder;

import io.github.lukehutch.fastclasspathscanner.FastClasspathScanner;
import io.github.lukehutch.fastclasspathscanner.scanner.ScanResult;

public class Datas extends ConcurrentHashMap<String, CopyOnWriteArrayList<Data>> {

	private static final long serialVersionUID = -5705119379284982454L;
	private static final String FILEPATH = "resources/datas.json";

	public void sort() throws Exception {
		for (CopyOnWriteArrayList<Data> list : this.values()) {
			list.sort(new Comparator<Data>() {
				public int compare(Data o1, Data o2) {
					return ObjectUtils.compare(o1.getOrder(), o2.getOrder(), true);
				}
			});
			Integer order = 0;
			for (Data o : list) {
				o.setOrder(order++);
			}
		}
		List<String> entities = this.scanContainerEntityClass();
		for (String str : entities) {
			CopyOnWriteArrayList<Data> list = this.get(str);
			if (null == list) {
				this.put(str, new CopyOnWriteArrayList<Data>());
			}
		}
		for (String str : this.keySet()) {
			if (!entities.contains(str)) {
				this.remove(str);
			}
		}
	}

//	public static Datas load() throws Exception {
//		File file = new File(ThisApplication.root, FILEPATH);
//		return load(file.getAbsolutePath());
//	}

	public static Datas load(String path) throws Exception {
		File file = new File(path);
		Datas o = null;
		synchronized (Datas.class) {
			if (file.exists()) {
				o = XGsonBuilder.instance().fromJson(FileUtils.readFileToString(file, "utf-8"), Datas.class);
			} else {
				o = new Datas();
			}
			/* 扫描类并生成空值 */
			o.sort();
		}
		return o;
	}
//
//	public static void store(Datas datas) throws Exception {
//		File file = new File(ThisApplication.root, FILEPATH);
//		store(datas, file.getAbsolutePath());
//	}

	public static void store(Datas datas, String path) throws Exception {
		synchronized (Datas.class) {
			File file = new File(path);
			FileUtils.write(file, XGsonBuilder.toJson(datas), "utf-8");
		}
	}

	private List<String> scanContainerEntityClass() throws Exception {
		ScanResult scanResult = new FastClasspathScanner(Packages.PREFIX).scan();
		List<String> sortedList = new ArrayList<>(scanResult.getNamesOfClassesWithAnnotation(ContainerEntity.class));
		Collections.sort(sortedList, new Comparator<String>() {
			public int compare(String s1, String s2) {
				return s1.compareTo(s2);
			}
		});
		return sortedList;
	}
}
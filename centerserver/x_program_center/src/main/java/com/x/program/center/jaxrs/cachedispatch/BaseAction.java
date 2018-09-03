package com.x.program.center.jaxrs.cachedispatch;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.reflect.FieldUtils;

import com.x.base.core.project.AssembleA;
import com.x.base.core.project.Packages;
import com.x.base.core.project.ServiceA;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;

import io.github.lukehutch.fastclasspathscanner.FastClasspathScanner;
import io.github.lukehutch.fastclasspathscanner.scanner.ScanResult;

abstract class BaseAction extends StandardJaxrsAction {
	private static ConcurrentHashMap<String, List<Class<?>>> entityInApplicationMap = null;

	static List<Class<?>> findApplicationWithEntity(String name) {
		if (null == entityInApplicationMap) {
			synchronized (BaseAction.class) {
				if (null == entityInApplicationMap) {
					entityInApplicationMap = new ConcurrentHashMap<String, List<Class<?>>>();
					List<String> classes = new ArrayList<>();
					ScanResult scanResult = new FastClasspathScanner(Packages.PREFIX).scan();
					classes.addAll(scanResult.getNamesOfSubclassesOf(AssembleA.class));
					classes.addAll(scanResult.getNamesOfSubclassesOf(ServiceA.class));
					classes.stream().forEach(s -> {
						Class<?> clz;
						try {
							clz = Class.forName(s);
							((List<String>) FieldUtils.readStaticField(clz, "containerEntities")).stream()
									.forEach(o -> {
										List<Class<?>> list = entityInApplicationMap.get(o);
										if (null == list) {
											list = new ArrayList<Class<?>>();
											entityInApplicationMap.put(o, list);
										}
										list.add(clz);
									});
						} catch (Exception e) {
							e.printStackTrace();
						}
					});
				}
			}
		}
		List<Class<?>> list = entityInApplicationMap.get(name);
		if (null == list) {
			list = new ArrayList<Class<?>>();
		}
		return list;
	}
}

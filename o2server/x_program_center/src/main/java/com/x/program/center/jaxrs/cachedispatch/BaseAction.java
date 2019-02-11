package com.x.program.center.jaxrs.cachedispatch;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

abstract class BaseAction extends StandardJaxrsAction {

	private static Logger logger = LoggerFactory.getLogger(BaseAction.class);

	private static ConcurrentHashMap<String, List<Class<?>>> entityInApplicationMap = null;


	

//	static List<Class<?>> findApplicationWithEntity(String name) {
//		
//		
//		
//		if (null == entityInApplicationMap) {
//			synchronized (BaseAction.class) {
//				if (null == entityInApplicationMap) {
//					entityInApplicationMap = new ConcurrentHashMap<String, List<Class<?>>>();
//					try (ScanResult scanResult = new ClassGraph().enableAllInfo().scan()) {
//						List<ClassInfo> list = new ArrayList<>();
//						list.addAll(scanResult.getSubclasses(AssembleA.class.getName()));
//						list.addAll(scanResult.getSubclasses(ServiceA.class.getName()));
//						for (ClassInfo info : list) {
//							Class<?> clz = Class.forName(info.getName());
//							Deployable deployable = (Deployable) clz.newInstance();
//							deployable.dependency().containerEntities.forEach(o -> {
//								List<Class<?>> os = entityInApplicationMap.get(o);
//								if (null == os) {
//									os = new ArrayList<Class<?>>();
//									entityInApplicationMap.put(o, os);
//								}
//								os.add(clz);
//							});
//						}
//					} catch (Exception e) {
//						logger.error(e);
//					}
////					ScanResult scanResult = new FastClasspathScanner(Packages.PREFIX).scan();
////					classes.addAll(scanResult.getNamesOfSubclassesOf(AssembleA.class));
////					classes.addAll(scanResult.getNamesOfSubclassesOf(ServiceA.class));
////					classes.stream().forEach(s -> {
////						Class<?> clz;
////						try {
////							clz = Class.forName(s);
////							Deployable deployable = (Deployable) clz.newInstance();
////							deployable.dependency().containerEntities.forEach(o -> {
////								List<Class<?>> list = entityInApplicationMap.get(o);
////								if (null == list) {
////									list = new ArrayList<Class<?>>();
////									entityInApplicationMap.put(o, list);
////								}
////								list.add(clz);
////							});
////						} catch (Exception e) {
////							e.printStackTrace();
////						}
////					});
//				}
//			}
//		}
//		List<Class<?>> list = entityInApplicationMap.get(name);
//		if (null == list) {
//			return new ArrayList<Class<?>>();
//		} else {
//			List<Class<?>> os = new ArrayList<>();
//			os.addAll(list);
//			return os;
//		}
//	}
}

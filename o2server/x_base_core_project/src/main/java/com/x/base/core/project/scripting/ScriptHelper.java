//package com.x.base.core.project.scripting;
//
//import java.util.ArrayList;
//import java.util.Collection;
//import java.util.List;
//import java.util.Map;
//import java.util.Map.Entry;
//import java.util.Objects;
//
//import javax.script.ScriptEngine;
//import javax.script.ScriptEngineManager;
//
//import org.apache.commons.beanutils.PropertyUtils;
//import org.apache.commons.lang3.StringUtils;
//import org.apache.commons.lang3.SystemUtils;
//
//import com.x.base.core.project.exception.ExceptionScriptEval;
//import com.x.base.core.project.logger.Logger;
//import com.x.base.core.project.logger.LoggerFactory;
//
//import jdk.nashorn.api.scripting.ScriptObjectMirror;
//
//public class ScriptHelper {
//
//	private static Logger logger = LoggerFactory.getLogger(ScriptHelper.class);
//
//	private static final String defaultLanguage = "JavaScript";
//
//	private static final String distinguishedName = "distinguishedName";
//
//	private ScriptEngineManager factory;
//	private ScriptEngine engine;
//
//	public ScriptHelper() throws Exception {
//		this(null, null);
//	}
//
//	public ScriptHelper(Map<String, Object> map) throws Exception {
//		this(map, null);
//	}
//
//	public ScriptHelper(String initialScriptText) throws Exception {
//		this(null, initialScriptText);
//	}
//
//	public ScriptHelper(Map<String, Object> map, String initialScriptText) throws Exception {
//		this.factory = new ScriptEngineManager();
//		this.engine = factory.getEngineByName(defaultLanguage);
//		if (null != map && (!map.isEmpty())) {
//			for (Entry<String, Object> entry : map.entrySet()) {
//				engine.put(entry.getKey(), entry.getValue());
//			}
//		}
//		if (StringUtils.isNotBlank(initialScriptText)) {
//			engine.eval(initialScriptText);
//		}
//	}
//
//	public void put(String key, Object value) {
//		this.engine.put(key, value);
//	}
//
//	public void put(Map<String, Object> map) {
//		for (Entry<String, Object> entry : map.entrySet()) {
//			engine.put(entry.getKey(), entry.getValue());
//		}
//	}
//
//	public Object eval(String scriptText) throws Exception {
//		StringBuffer sb = new StringBuffer();
//		try {
//			sb.append("(function(){").append(System.lineSeparator());
//			if (StringUtils.isNotEmpty(scriptText)) {
//				sb.append(scriptText).append(System.lineSeparator());
//			}
//			sb.append("})();");
//			Object obj = this.engine.eval(sb.toString());
//			return obj;
//		} catch (Exception e) {
//			throw new ExceptionScriptEval(e, sb.toString());
//		}
//	}
//
//	public List<String> evalAsStringList(String scriptText) throws Exception {
//		Object o = this.eval(scriptText);
//		return this.readAsStringList(o);
//	}
//
//	public String evalAsString(String scriptText) throws Exception {
//		Object o = this.eval(scriptText);
//		return Objects.toString(o);
//	}
//
//	public Boolean evalAsBoolean(String scriptText) throws Exception {
//		Object o = this.eval(scriptText);
//		return (Boolean) o;
//	}
//
//	public List<String> evalExtrectDistinguishedName(String scriptText) throws Exception {
//		List<String> list = new ArrayList<>();
//		Object o = this.eval(scriptText);
//		if (null != o) {
//			if (o instanceof CharSequence) {
//				list.add(Objects.toString(o, ""));
//			} else if (o instanceof Iterable) {
//				for (Object obj : (Iterable<?>) o) {
//					if (null != obj) {
//						if (obj instanceof CharSequence) {
//							list.add(Objects.toString(obj, ""));
//						} else {
//							Object d = PropertyUtils.getProperty(obj, distinguishedName);
//							if (null != d) {
//								list.add(Objects.toString(d, ""));
//							}
//						}
//					}
//				}
//			} else if (o instanceof ScriptObjectMirror) {
//				ScriptObjectMirror som = (ScriptObjectMirror) o;
//				if (som.isArray()) {
//					Object[] objs = (som.to(Object[].class));
//					for (Object obj : objs) {
//						if (null != obj) {
//							if (obj instanceof CharSequence) {
//								list.add(Objects.toString(obj, ""));
//							} else {
//								Object d = PropertyUtils.getProperty(obj, distinguishedName);
//								if (null != d) {
//									list.add(Objects.toString(d, ""));
//								}
//							}
//						}
//					}
//				} else {
//					Object d = PropertyUtils.getProperty(o, distinguishedName);
//					if (null != d) {
//						list.add(Objects.toString(d, ""));
//					}
//				}
//			}
//		}
//		return list;
//	}
//
//	private List<String> readAsStringList(Object obj) throws Exception {
//		List<String> list = new ArrayList<>();
//		for (Object o : this.iterator(obj)) {
//			list.add(Objects.toString(o));
//		}
//		return list;
//	}
//
//	private List<Object> iterator(Object obj) throws Exception {
//		List<Object> results = new ArrayList<>();
//		this.iterator(obj, results);
//		return results;
//	}
//
//	private void iterator(Object obj, List<Object> results) throws Exception {
//		if (null == obj) {
//			return;
//		}
//		List<Object> list = new ArrayList<>();
//		if (obj.getClass().isArray()) {
//			for (Object o : (Object[]) obj) {
//				list.add(o);
//			}
//		} else if (obj instanceof Collection) {
//			for (Object o : (Collection<?>) obj) {
//				list.add(o);
//			}
//		} else if (obj instanceof ScriptObjectMirror) {
//			ScriptObjectMirror som = (ScriptObjectMirror) obj;
//			if (som.isArray()) {
//				Object[] os = (som.to(Object[].class));
//				for (Object o : os) {
//					list.add(o);
//				}
//			} else {
//				results.add(som);
//			}
//		} else {
//			results.add(obj);
//		}
//		for (Object o : list) {
//			iterator(o, results);
//		}
//	}
//}
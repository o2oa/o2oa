package com.x.processplatform.service.processing;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;

import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.processplatform.core.entity.element.Script;
import com.x.processplatform.service.processing.processor.AeiObjects;

import jdk.nashorn.api.scripting.ScriptObjectMirror;

public class ScriptHelper {

	private static Logger logger = LoggerFactory.getLogger(ScriptHelper.class);

	private static final String defaultLanguage = "nashorn";

	private static final String distinguishedName = "distinguishedName";

	private ScriptEngineManager factory;
	private ScriptEngine engine;
	private Business business;
	private AeiObjects aeiObjects = null;

	public AeiObjects getAeiObjects() {
		return this.aeiObjects;
	}

	void setAeiObjects(AeiObjects aeiObjects) {
		this.aeiObjects = aeiObjects;
	}

	ScriptHelper(Business business, Map<String, Object> map, String initialScriptText) throws Exception {
		this.factory = new ScriptEngineManager();
		this.engine = factory.getEngineByName(defaultLanguage);
		this.business = business;
		// scriptContext = new SimpleScriptContext();
		// scriptContext.setWriter(new PrintWriter(System.out));
		// scriptContext.setErrorWriter(new PrintWriter(System.err));
		for (Entry<String, Object> entry : map.entrySet()) {
			// scriptContext.setAttribute(entry.getKey(), entry.getValue(),
			// ScriptContext.ENGINE_SCOPE);
			engine.put(entry.getKey(), entry.getValue());
		}
		// engine.eval(initialScriptText, scriptContext);
		engine.eval(initialScriptText);
		// List<String> list = new ArrayList<>();
		// list.add("'library' : this.library");
		// for (String str : map.keySet()) {
		// list.add("'" + str + "' : this." + str);
		// }
		// String str = "rt= {" + StringUtils.join(list.toArray(), ", ") + "};
		// ";
		// engine.eval(str, scriptContext);
		// engine.eval(str);
	}

	public Object eval(String application, String scriptName, String scriptText) throws Exception {
		StringBuffer sb = new StringBuffer();
		sb.append("(function(){").append(SystemUtils.LINE_SEPARATOR);
		if (StringUtils.isNotEmpty(scriptName)) {
			List<Script> list = business.element().listScriptNestedWithApplicationWithUniqueName(application,
					scriptName);
			for (Script o : list) {
				sb.append(o.getText()).append(SystemUtils.LINE_SEPARATOR);
			}
		}
		if (StringUtils.isNotEmpty(scriptText)) {
			sb.append(scriptText).append(SystemUtils.LINE_SEPARATOR);
		}
		sb.append("}).apply(bind);");
		// return this.engine.eval(sb.toString(), scriptContext);
		Object obj = this.engine.eval(sb.toString());
		logger.debug("eval return:{}.", obj);
		return obj;
	}

	public List<String> evalAsStringList(String application, String scriptName, String scriptText) throws Exception {
		Object o = this.eval(application, scriptName, scriptText);
		return this.readAsStringList(o);
	}

	public String evalAsString(String application, String scriptName, String scriptText) throws Exception {
		Object o = this.eval(application, scriptName, scriptText);
		return Objects.toString(o);
	}

	public Boolean evalAsBoolean(String application, String scriptName, String scriptText) throws Exception {
		Object o = this.eval(application, scriptName, scriptText);
		return (Boolean) o;
	}

	public List<String> evalExtrectDistinguishedName(String application, String scriptName, String scriptText)
			throws Exception {
		List<String> list = new ArrayList<>();
		Object o = this.eval(application, scriptName, scriptText);
		if (null != o) {
			if (o instanceof CharSequence) {
				list.add(Objects.toString(o, ""));
			} else if (o instanceof Iterable) {
				for (Object obj : (Iterable<?>) o) {
					if (null != obj) {
						if (obj instanceof CharSequence) {
							list.add(Objects.toString(obj, ""));
						} else {
							Object d = PropertyUtils.getProperty(obj, distinguishedName);
							if (null != d) {
								list.add(Objects.toString(d, ""));
							}
						}
					}
				}
			} else if (o instanceof ScriptObjectMirror) {
				ScriptObjectMirror som = (ScriptObjectMirror) o;
				if (som.isArray()) {
					Object[] objs = (som.to(Object[].class));
					for (Object obj : objs) {
						if (null != obj) {
							if (obj instanceof CharSequence) {
								list.add(Objects.toString(obj, ""));
							} else {
								Object d = PropertyUtils.getProperty(obj, distinguishedName);
								if (null != d) {
									list.add(Objects.toString(d, ""));
								}
							}
						}
					}
				} else {
					Object d = PropertyUtils.getProperty(o, distinguishedName);
					if (null != d) {
						list.add(Objects.toString(d, ""));
					}
				}
			}
		}
		return list;
	}

	private List<String> readAsStringList(Object obj) throws Exception {
		List<String> list = new ArrayList<>();
		for (Object o : this.iterator(obj)) {
			list.add(Objects.toString(o));
		}
		return list;
	}

	private List<Object> iterator(Object obj) throws Exception {
		List<Object> results = new ArrayList<>();
		this.iterator(obj, results);
		return results;
	}

	private void iterator(Object obj, List<Object> results) throws Exception {
		if (null == obj) {
			return;
		}
		List<Object> list = new ArrayList<>();
		if (obj.getClass().isArray()) {
			for (Object o : (Object[]) obj) {
				list.add(o);
			}
		} else if (obj instanceof Collection) {
			for (Object o : (Collection<?>) obj) {
				list.add(o);
			}
		} else if (obj instanceof ScriptObjectMirror) {
			ScriptObjectMirror som = (ScriptObjectMirror) obj;
			if (som.isArray()) {
				Object[] os = (som.to(Object[].class));
				for (Object o : os) {
					list.add(o);
				}
			} else {
				results.add(som);
			}
		} else {
			results.add(obj);
		}
		for (Object o : list) {
			iterator(o, results);
		}
	}
}
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
//
//import org.apache.commons.beanutils.PropertyUtils;
//import org.apache.commons.lang3.StringUtils;
//
//import com.x.base.core.project.exception.ExceptionScriptEval;
//import com.x.base.core.project.http.EffectivePerson;
//
//import jdk.nashorn.api.scripting.ScriptObjectMirror;
//
//public class ScriptingEngine {
//
//	public ScriptEngine scriptEngine;
//
//	private static final String distinguishedName = "distinguishedName";
//
//	public static final String BINDINGNAME_GSON = "gson";
//	public static final String BINDINGNAME_ORGANIZATION = "organization";
//	public static final String BINDINGNAME_WORKCONTEXT = "workContext";
//	public static final String BINDINGNAME_DATA = "data";
//	public static final String BINDINGNAME_WEBSERVICESCLIENT = "webservicesClient";
//	public static final String BINDINGNAME_DICTIONARY = "dictionary";
//	public static final String BINDINGNAME__LOOKUP = "lookup";
//	public static final String BINDINGNAME_APPLICATIONS = "applications";
//	public static final String BINDINGNAME_PARAMETER = "parameter";
//	public static final String BINDINGNAME_PARAMETERS = "parameters";
//	public static final String BINDINGNAME_EFFECTIVEPERSON = "effectivePerson";
//	public static final String BINDINGNAME_JAXRSRESPONSE = "jaxrsResponse";
//	public static final String BINDINGNAME_JAXWSRESPONSE = "jaxwsResponse";
//	public static final String BINDINGNAME_ROUTEDATA = "routeData";
//
//	public static final String BINDINGNAME_ROUTES = "routes";
//	public static final String BINDINGNAME_ROUTE = "route";
//
//	public static final String BINDINGNAME_RESOURCES = "resources";
//
//	public ScriptingEngine(ScriptEngine scriptEngine) {
//		this.scriptEngine = scriptEngine;
//	}
//
//	public ScriptingEngine binding(String key, Object value) {
//		this.scriptEngine.put(key, value);
//		return this;
//	}
//
//	public ScriptingEngine binding(Map<String, Object> map) {
//		for (Entry<String, Object> entry : map.entrySet()) {
//			scriptEngine.put(entry.getKey(), entry.getValue());
//		}
//		return this;
//	}
//
//	public ScriptingEngine bindingOrganization(Object o) {
//		this.scriptEngine.put(BINDINGNAME_ORGANIZATION, o);
//		return this;
//	}
//
//	public ScriptingEngine bindingWorkContext(Object o) {
//		this.scriptEngine.put(BINDINGNAME_WORKCONTEXT, o);
//		return this;
//	}
//
//	public ScriptingEngine bindingData(Object o) {
//		this.scriptEngine.put(BINDINGNAME_DATA, o);
//		return this;
//	}
//
//	public ScriptingEngine bindingWebservicesClient(Object o) {
//		this.scriptEngine.put(BINDINGNAME_WEBSERVICESCLIENT, o);
//		return this;
//	}
//
//	public ScriptingEngine bindingDictionary(Object o) {
//		this.scriptEngine.put(BINDINGNAME_DICTIONARY, o);
//		return this;
//	}
//
//	public ScriptingEngine bindingApplications(Object o) {
//		this.scriptEngine.put(BINDINGNAME_APPLICATIONS, o);
//		return this;
//	}
//
//	public ScriptingEngine bindingEffectivePerson(EffectivePerson effectivePerson) {
//		this.scriptEngine.put(BINDINGNAME_EFFECTIVEPERSON, effectivePerson);
//		return this;
//	}
//
//	public ScriptingEngine bindingJaxrsResponse(Object o) {
//		this.scriptEngine.put(BINDINGNAME_JAXRSRESPONSE, o);
//		return this;
//	}
//
//	public ScriptingEngine bindingJaxwsResponse(Object o) {
//		this.scriptEngine.put(BINDINGNAME_JAXWSRESPONSE, o);
//		return this;
//	}
//
//	public ScriptingEngine bindingParameter(Object o) {
//		this.scriptEngine.put(BINDINGNAME_PARAMETER, o);
//		return this;
//	}
//
//	public ScriptingEngine bindingParameters(Object o) {
//		this.scriptEngine.put(BINDINGNAME_PARAMETERS, o);
//		return this;
//	}
//
//	public ScriptingEngine bindingRouteData(String str) {
//		this.scriptEngine.put(BINDINGNAME_ROUTEDATA, str);
//		return this;
//	}
//
//	public ScriptingEngine bindingRoutes(Object o) {
//		this.scriptEngine.put(BINDINGNAME_ROUTES, o);
//		return this;
//	}
//
//	public ScriptingEngine bindingRoute(Object o) {
//		this.scriptEngine.put(BINDINGNAME_ROUTE, o);
//		return this;
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
//			Object obj = this.scriptEngine.eval(sb.toString());
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
//
//}
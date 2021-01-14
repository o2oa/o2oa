package com.x.base.core.project.script;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.apache.commons.lang3.BooleanUtils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.tools.PropertyTools;

import jdk.nashorn.api.scripting.ScriptObjectMirror;
import jdk.nashorn.internal.runtime.ScriptObject;

public class ScriptFactory {

	private ScriptFactory() {

	}

	public static final ScriptEngine scriptEngine = (new ScriptEngineManager())
			.getEngineByName(Config.SCRIPTING_ENGINE_NAME);

	private static CompiledScript COMPILEDSCRIPT_INITIALSERVICESCRIPTTEXT;
	private static CompiledScript COMPILEDSCRIPT_INITIALSCRIPTTEXT;

	public static final String BINDING_NAME_RESOURCES = "resources";
	public static final String BINDING_NAME_EFFECTIVEPERSON = "effectivePerson";
	public static final String BINDING_NAME_WORKCONTEXT = "workContext";
	public static final String BINDING_NAME_GSON = "gson";
	public static final String BINDING_NAME_DATA = "data";
	public static final String BINDING_NAME_ORGANIZATION = "organization";
	public static final String BINDING_NAME_WEBSERVICESCLIENT = "webservicesClient";
	public static final String BINDING_NAME_DICTIONARY = "dictionary";
	public static final String BINDING_NAME_ROUTES = "routes";
	public static final String BINDING_NAME_ROUTE = "routes";
	public static final String BINDING_NAME_APPLICATIONS = "applications";

	public static final String BINDING_NAME_ASSIGNDATA = "assignData";

	public static final String BINDING_NAME_IDENTITY = "identity";

	public static final String BINDING_NAME_PARAMETERS = "parameters";
	public static final String BINDING_NAME_JAXRSRESPONSE = "jaxrsResponse";
	public static final String BINDING_NAME_JAXWSRESPONSE = "jaxwsResponse";

	public static final String BINDING_NAME_JAXRSBODY = "jaxrsBody";
	public static final String BINDING_NAME_JAXRSHEAD = "jaxrsHead";

	public static final String BINDING_NAME_SERVICEVALUE = "serviceValue";
	public static final String BINDING_NAME_TASK = "task";
	public static final String BINDING_NAME_EXPIRE = "expire";
	public static final String BINDING_NAME_SERIAL = "serial";
	public static final String BINDING_NAME_PROCESS = "process";

	public static ScriptEngine newScriptEngine() {
		return (new ScriptEngineManager()).getEngineByName(Config.SCRIPTING_ENGINE_NAME);
	}

	public static CompiledScript initialServiceScriptText() throws Exception {
		if (COMPILEDSCRIPT_INITIALSERVICESCRIPTTEXT == null) {
			synchronized (ScriptFactory.class) {
				if (COMPILEDSCRIPT_INITIALSERVICESCRIPTTEXT == null) {
					String text = Config.initialServiceScriptText();
					COMPILEDSCRIPT_INITIALSERVICESCRIPTTEXT = ((Compilable) scriptEngine).compile(text);
				}
			}
		}
		return COMPILEDSCRIPT_INITIALSERVICESCRIPTTEXT;
	}

	public static CompiledScript compile(String text) throws Exception {
		return ((Compilable) scriptEngine).compile(text);
	}

	public static CompiledScript initialScriptText() throws Exception {
		if (COMPILEDSCRIPT_INITIALSCRIPTTEXT == null) {
			synchronized (ScriptFactory.class) {
				if (COMPILEDSCRIPT_INITIALSCRIPTTEXT == null) {
					String text = Config.initialScriptText();
					COMPILEDSCRIPT_INITIALSCRIPTTEXT = ((Compilable) scriptEngine).compile(text);
				}
			}
		}
		return COMPILEDSCRIPT_INITIALSCRIPTTEXT;
	}

	public static String functionalization(String text) {
		StringBuffer sb = new StringBuffer();
		sb.append("(function(){").append(System.lineSeparator());
		sb.append(Objects.toString(text, "")).append(System.lineSeparator());
		sb.append("})();");
		return sb.toString();
	}

	public static List<String> asStringList(Object o) throws Exception {
		return readAsStringList(o);
	}

	public static String asString(Object o) throws Exception {
		return Objects.toString(o);
	}

	public static Boolean asBoolean(Object o) throws Exception {
		if (null == o) {
			return false;
		}
		if (o instanceof Number) {
			return ((Number) o).intValue() != 0;
		}
		return BooleanUtils.toBooleanObject(Objects.toString(o, "false"));
	}

//	public static List<String> asDistinguishedName(Object o) throws Exception {
//		List<String> list = new ArrayList<>();
//		if (null != o) {
//			if (o instanceof CharSequence) {
//				list.add(Objects.toString(o));
//			} else if (o instanceof Iterable) {
//				for (Object obj : (Iterable<?>) o) {
//					if (null != obj) {
//						if (obj instanceof CharSequence) {
//							list.add(Objects.toString(obj));
//						} else {
//							Object d = PropertyUtils.getProperty(obj, JpaObject.DISTINGUISHEDNAME);
//							if (null != d) {
//								list.add(Objects.toString(d));
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
//								list.add(Objects.toString(obj));
//							} else {
//								Object d = PropertyUtils.getProperty(obj, JpaObject.DISTINGUISHEDNAME);
//								if (null != d) {
//									list.add(Objects.toString(d));
//								}
//							}
//						}
//					}
//				} else {
//					Object d = PropertyUtils.getProperty(o, JpaObject.DISTINGUISHEDNAME);
//					if (null != d) {
//						list.add(Objects.toString(d));
//					}
//				}
//			}
//		}
//		return list;
//	}

	private static List<String> readAsStringList(Object obj) throws Exception {
		List<String> list = new ArrayList<>();
		for (Object o : iterator(obj)) {
			list.add(Objects.toString(o));
		}
		return list;
	}

	private static List<Object> iterator(Object obj) throws Exception {
		List<Object> results = new ArrayList<>();
		iterator(obj, results);
		return results;
	}

	private static void iterator(Object obj, List<Object> results) throws Exception {
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

	public static List<String> asDistinguishedNameList(Object o) throws Exception {
		List<String> list = new ArrayList<>();
		if (null != o) {
			if (o instanceof CharSequence) {
				list.add(Objects.toString(o, ""));
			} else if (o instanceof JsonObject) {
				JsonObject jsonObject = (JsonObject) o;
				if (jsonObject.has(JpaObject.DISTINGUISHEDNAME)) {
					list.add(jsonObject.get(JpaObject.DISTINGUISHEDNAME).getAsString());
				}
			} else if (o instanceof JsonArray) {
				for (JsonElement jsonElement : (JsonArray) o) {
					if (jsonElement.isJsonObject()) {
						JsonObject jsonObject = jsonElement.getAsJsonObject();
						if (jsonObject.has(JpaObject.DISTINGUISHEDNAME)) {
							list.add(jsonObject.get(JpaObject.DISTINGUISHEDNAME).getAsString());
						}
					}
				}
			} else if (o instanceof ScriptObjectMirror) {
				ScriptObjectMirror som = (ScriptObjectMirror) o;
				if (som.isArray()) {
					for (Object obj : som.to(Object[].class)) {
						if (null != obj) {
							if (obj instanceof CharSequence) {
								list.add(Objects.toString(obj, ""));
							} else if (obj instanceof ScriptObject) {
								ScriptObject so = (ScriptObject) obj;
								if (so.containsKey(JpaObject.DISTINGUISHEDNAME)) {
									list.add(Objects.toString(so.get(JpaObject.DISTINGUISHEDNAME), ""));
								}
							} else {
								list.add(PropertyTools.getOrElse(obj, JpaObject.DISTINGUISHEDNAME, String.class, ""));
							}

						}
					}
				} else if (som.containsKey(JpaObject.DISTINGUISHEDNAME)) {
					list.add(Objects.toString(som.get(JpaObject.DISTINGUISHEDNAME), ""));
				} else {
					list.add(PropertyTools.getOrElse(o, JpaObject.DISTINGUISHEDNAME, String.class, ""));
				}
			// ScriptObject or JO
			} else if (o instanceof ScriptObject) {
				ScriptObject so = (ScriptObject) o;
				if (so.isArray()) {
					for (Object obj : so.getArray().asObjectArray()) {
						if (null != obj) {
							if (obj instanceof CharSequence) {
								list.add(Objects.toString(obj, ""));
							} else {
								if (obj instanceof ScriptObject) {
									ScriptObject scriptObject = (ScriptObject) obj;
									if (scriptObject.containsKey(JpaObject.DISTINGUISHEDNAME)) {
										list.add(Objects.toString(scriptObject.get(JpaObject.DISTINGUISHEDNAME), ""));
									}
								} else {
									list.add(PropertyTools.getOrElse(obj, JpaObject.DISTINGUISHEDNAME, String.class,
											""));
								}
							}
						}
					}
				} else if (so.containsKey(JpaObject.DISTINGUISHEDNAME)) {
					list.add(Objects.toString(so.get(JpaObject.DISTINGUISHEDNAME), ""));
				} else {
					list.add(PropertyTools.getOrElse(o, JpaObject.DISTINGUISHEDNAME, String.class, ""));
				}
			// Map类型也属于Iterable 所以必须在Iterable之前进行判断
			} else if (o instanceof Map) {
				Map map = (Map) o;
				if (map.containsKey(JpaObject.DISTINGUISHEDNAME)) {
					list.add(PropertyTools.getOrElse(o, JpaObject.DISTINGUISHEDNAME, String.class, ""));
				} else {
					for (Object mapValue : map.values()) {
						if (mapValue instanceof CharSequence) {
							list.add(Objects.toString(mapValue, ""));
						} else {
							list.add(PropertyTools.getOrElse(mapValue, JpaObject.DISTINGUISHEDNAME, String.class, ""));
						}
					}
				}
			} else if (o instanceof Iterable) {
				for (Object obj : (Iterable<?>) o) {
					if (null != obj) {
						if (obj instanceof CharSequence) {
							list.add(Objects.toString(obj, ""));
						} else {
							list.add(PropertyTools.getOrElse(obj, JpaObject.DISTINGUISHEDNAME, String.class, ""));
						}
					}
				}
			} else {
				list.add(Objects.toString(o, ""));
			}
		}
		return list;
	}

}
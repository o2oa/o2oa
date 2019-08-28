package com.x.processplatform.core.entity.element.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.TreeMap;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.math.NumberUtils;

import com.google.gson.reflect.TypeToken;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.dynamic.DynamicEntity;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.tools.DateTools;
import com.x.base.core.project.tools.ListTools;
import com.x.base.core.project.tools.StringTools;
import com.x.processplatform.core.entity.content.Data;
import com.x.processplatform.core.entity.content.WorkCompleted;
import com.x.processplatform.core.entity.element.Mapping;

public class MappingFactory {

//	public static void mappingWorkCompleted(Mapping mapping, Data data, WorkCompleted workCompleted) throws Exception {
//		mapping(mapping, data, workCompleted);
//	}

	public static void mapping(Mapping mapping, WorkCompleted workCompleted, Data data, JpaObject jpaObject)
			throws Exception {

//		if (BooleanUtils.isNotTrue(mapping.getEnable())) {
//			return;
//		}
//
//		try {
//			Class.forName(DynamicEntity.CLASS_PACKAGE + "." + mapping.getTableName());
//		} catch (Exception e) {
//			throw new ExceptionDynamicClassNotExist(mapping.getTableName());
//		}

		List<Mapping.Item> items = XGsonBuilder.instance().fromJson(mapping.getData(),
				new TypeToken<List<Mapping.Item>>() {
				}.getType());

		for (Mapping.Item item : items) {

			switch (item.getType()) {

			case JpaObject.TYPE_STRING:
				stringValue(data, item.getPath(), jpaObject, item.getColumn());
				break;

			case JpaObject.TYPE_INTEGER:
				integerValue(data, item.getPath(), jpaObject, item.getColumn());
				break;

			case JpaObject.TYPE_LONG:
				longValue(data, item.getPath(), jpaObject, item.getColumn());
				break;

			case JpaObject.TYPE_DOUBLE:
				doubleValue(data, item.getPath(), jpaObject, item.getColumn());
				break;

			case JpaObject.TYPE_BOOLEAN:
				booleanValue(data, item.getPath(), jpaObject, item.getColumn());
				break;

			case JpaObject.TYPE_DATE:
				dateValue(data, item.getPath(), jpaObject, item.getColumn());
				break;

			case JpaObject.TYPE_TIME:
				timeValue(data, item.getPath(), jpaObject, item.getColumn());
				break;

			case JpaObject.TYPE_DATETIME:
				dateTimeValue(data, item.getPath(), jpaObject, item.getColumn());
				break;

			case JpaObject.TYPE_STRINGLIST:
				stringListValue(data, item.getPath(), jpaObject, item.getColumn());
				break;

			case JpaObject.TYPE_INTEGERLIST:
				integerListValue(data, item.getPath(), jpaObject, item.getColumn());
				break;

			case JpaObject.TYPE_LONGLIST:
				longListValue(data, item.getPath(), jpaObject, item.getColumn());
				break;

			case JpaObject.TYPE_DOUBLELIST:
				doubleListValue(data, item.getPath(), jpaObject, item.getColumn());
				break;

			case JpaObject.TYPE_BOOLEANLIST:
				booleanListValue(data, item.getPath(), jpaObject, item.getColumn());
				break;

			case JpaObject.TYPE_STRINGLOB:
				stringLobValue(data, item.getPath(), jpaObject, item.getColumn());
				break;

			case JpaObject.TYPE_STRINGMAP:
				stringMapValue(data, item.getPath(), jpaObject, item.getColumn());
				break;

			default:
				break;
			}
		}
	}

	private static void stringValue(Data data, String path, JpaObject jpaObject, String property) throws Exception {
		Object obj = data.find(path);
		if (null == obj) {
			PropertyUtils.setProperty(jpaObject, property, null);
		} else {
			String value = ListTools.toStringJoin(obj);
			value = StringTools.utf8SubString(value, JpaObject.length_255B);
			PropertyUtils.setProperty(jpaObject, property, value);
		}
	}

	private static void integerValue(Data data, String path, JpaObject jpaObject, String property) throws Exception {
		Object obj = data.find(path);
		if (null == obj) {
			PropertyUtils.setProperty(jpaObject, property, null);
		} else {
			if (Number.class.isAssignableFrom(obj.getClass())) {
				PropertyUtils.setProperty(jpaObject, property, ((Number) obj).intValue());
			} else {
				String str = Objects.toString(obj);
				if (NumberUtils.isCreatable(str)) {
					PropertyUtils.setProperty(jpaObject, property, NumberUtils.createInteger(str));
				}
			}
		}
	}

	private static void longValue(Data data, String path, JpaObject jpaObject, String property) throws Exception {
		Object obj = data.find(path);
		if (null == obj) {
			PropertyUtils.setProperty(jpaObject, property, null);
		} else {
			if (Number.class.isAssignableFrom(obj.getClass())) {
				PropertyUtils.setProperty(jpaObject, property, ((Number) obj).longValue());
			} else {
				String str = Objects.toString(obj);
				if (NumberUtils.isCreatable(str)) {
					PropertyUtils.setProperty(jpaObject, property, NumberUtils.createLong(str));
				}
			}
		}
	}

	private static void doubleValue(Data data, String path, JpaObject jpaObject, String property) throws Exception {
		Object obj = data.find(path);
		if (null == obj) {
			PropertyUtils.setProperty(jpaObject, property, null);
		} else {
			if (Number.class.isAssignableFrom(obj.getClass())) {
				PropertyUtils.setProperty(jpaObject, property, ((Number) obj).doubleValue());
			} else {
				String str = Objects.toString(obj);
				if (NumberUtils.isCreatable(str)) {
					PropertyUtils.setProperty(jpaObject, property, NumberUtils.createDouble(str));
				}
			}
		}
	}

	private static void booleanValue(Data data, String path, JpaObject jpaObject, String property) throws Exception {
		Object obj = data.find(path);
		if (null == obj) {
			PropertyUtils.setProperty(jpaObject, property, null);
		} else {
			PropertyUtils.setProperty(jpaObject, property, BooleanUtils.toBoolean(obj.toString()));
		}
	}

	private static void dateValue(Data data, String path, JpaObject jpaObject, String property) throws Exception {
		Object obj = data.find(path);
		if ((null != obj) && DateTools.isDateTimeOrDate(obj.toString())) {
			PropertyUtils.setProperty(jpaObject, property, DateTools.parse(obj.toString()));
		} else {
			PropertyUtils.setProperty(jpaObject, property, null);
		}
	}

	private static void timeValue(Data data, String path, JpaObject jpaObject, String property) throws Exception {
		Object obj = data.find(path);
		if ((null != obj) && DateTools.isDateTimeOrTime(obj.toString())) {
			PropertyUtils.setProperty(jpaObject, property, DateTools.parse(obj.toString()));
		} else {
			PropertyUtils.setProperty(jpaObject, property, null);
		}
	}

	private static void dateTimeValue(Data data, String path, JpaObject jpaObject, String property) throws Exception {
		Object obj = data.find(path);
		if ((null != obj) && DateTools.isDateTime(obj.toString())) {
			PropertyUtils.setProperty(jpaObject, property, DateTools.parse(obj.toString()));
		} else {
			PropertyUtils.setProperty(jpaObject, property, null);
		}
	}

	private static void stringListValue(Data data, String path, JpaObject jpaObject, String property) throws Exception {
		List<String> os = new ArrayList<>();
		Object obj = data.find(path);
		if (null != obj) {
			if (ListTools.isList(obj)) {
				for (Object o : (List<?>) obj) {
					os.add(o.toString());
				}
			} else {
				os.add(obj.toString());
			}
		}
		PropertyUtils.setProperty(jpaObject, property, os);
	}

	private static void integerListValue(Data data, String path, JpaObject jpaObject, String property)
			throws Exception {
		List<Integer> os = new ArrayList<>();
		Object obj = data.find(path);
		if (null != obj) {
			if (ListTools.isList(obj)) {
				for (Object o : (List<?>) obj) {
					if (NumberUtils.isCreatable(o.toString())) {
						os.add(NumberUtils.createInteger(o.toString()));
					}
				}
			} else {
				if (NumberUtils.isCreatable(obj.toString())) {
					os.add(NumberUtils.createInteger(obj.toString()));
				}
			}
		}
		PropertyUtils.setProperty(jpaObject, property, os);
	}

	private static void longListValue(Data data, String path, JpaObject jpaObject, String property) throws Exception {
		List<Long> os = new ArrayList<>();
		Object obj = data.find(path);
		if (null != obj) {
			if (ListTools.isList(obj)) {
				for (Object o : (List<?>) obj) {
					if (NumberUtils.isCreatable(o.toString())) {
						os.add(NumberUtils.createLong(o.toString()));
					}
				}
			} else {
				if (NumberUtils.isCreatable(obj.toString())) {
					os.add(NumberUtils.createLong(obj.toString()));
				}
			}
		}
		PropertyUtils.setProperty(jpaObject, property, os);
	}

	private static void doubleListValue(Data data, String path, JpaObject jpaObject, String property) throws Exception {
		List<Double> os = new ArrayList<>();
		Object obj = data.find(path);
		if (null != obj) {
			if (ListTools.isList(obj)) {
				for (Object o : (List<?>) obj) {
					if (NumberUtils.isCreatable(o.toString())) {
						os.add(NumberUtils.createDouble(o.toString()));
					}
				}
			} else {
				if (NumberUtils.isCreatable(obj.toString())) {
					os.add(NumberUtils.createDouble(obj.toString()));
				}
			}
		}
		PropertyUtils.setProperty(jpaObject, property, os);
	}

	private static void booleanListValue(Data data, String path, JpaObject jpaObject, String property)
			throws Exception {
		List<Boolean> os = new ArrayList<>();
		Object obj = data.find(path);
		if (null != obj) {
			if (ListTools.isList(obj)) {
				for (Object o : (List<?>) obj) {
					os.add(BooleanUtils.toBoolean(Objects.toString(o)));
				}
			} else {
				os.add(BooleanUtils.toBoolean(Objects.toString(obj)));
			}
		}
		PropertyUtils.setProperty(jpaObject, property, os);
	}

	private static void stringLobValue(Data data, String path, JpaObject jpaObject, String property) throws Exception {
		Object obj = data.find(path);
		if (null == obj) {
			PropertyUtils.setProperty(jpaObject, property, null);
		} else {
			String value = ListTools.toStringJoin(obj);
			PropertyUtils.setProperty(jpaObject, property, value);
		}
	}

	private static void stringMapValue(Data data, String path, JpaObject jpaObject, String property) throws Exception {
		Object obj = data.find(path);
		Map<String, String> map = new TreeMap<String, String>();
		if ((null != obj) && (Map.class.isAssignableFrom(obj.getClass()))) {
			for (Entry<?, ?> en : ((Map<?, ?>) obj).entrySet()) {
				map.put(Objects.toString(en.getKey(), ""), Objects.toString(en.getValue(), ""));
			}
		}
		PropertyUtils.setProperty(jpaObject, property, map);
	}

}

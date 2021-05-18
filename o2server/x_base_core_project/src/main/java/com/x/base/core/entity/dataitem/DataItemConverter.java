package com.x.base.core.entity.dataitem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Objects;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.organization.OrganizationDefinition;
import com.x.base.core.project.tools.StringTools;

public class DataItemConverter<T extends DataItem> {

	public static final int STRING_VALUE_MAX_LENGTH = JpaObject.length_255B;

	private Class<T> clz;

	private static Logger logger = LoggerFactory.getLogger(DataItemConverter.class);

	public DataItemConverter(Class<T> clz) {
		this.clz = clz;
	}

	public List<T> disassemble(JsonElement root, String... prefixPaths) throws Exception {
		List<String> paths = new ArrayList<>();
		for (String str : prefixPaths) {
			paths.add(str);
		}
		List<T> list = this.disassemble(root, paths, new ArrayList<T>());
		/**
		 * 20170905 通过 javascripting 转换的Map将 array -> {0:"xxxxx"}
		 * 的格式,变成了一个对象而非array,所以这里需要进行单独的判断,把用数字下标的Map强制设置为List
		 */
		// fixme 使用 ListIterator 替代后面的for循环。 消除List::get的使用
		for (int i = 0; i < (list.size() - 1); i++) {
			/** 因为要取下一个,循环不用取最后一个数. */
			T t = list.get(i);
			if (t.getItemType() == ItemType.o) {
				T next = list.get(i + 1);
				/** 是一个数字的值,说明是数组中的一个 */
				if (StringUtils.isNumeric(next.paths().get(next.paths().size() - 1))) {
					/** 说明上一个T应该是一个Array */
					t.setItemType(ItemType.a);
				}
			}
		}
		return list;
	}

	private List<T> disassemble(JsonElement root, List<String> paths, List<T> list) throws Exception {
		T t = clz.newInstance();
		t.path(paths);
		list.add(t);
		if (root.isJsonPrimitive()) {
			t.setItemType(ItemType.p);
			JsonPrimitive jsonPrimitive = root.getAsJsonPrimitive();
			if (jsonPrimitive.isBoolean()) {
				t.setItemPrimitiveType(ItemPrimitiveType.b);
				t.setItemStringValueType(ItemStringValueType.u);
				t.value(jsonPrimitive.getAsBoolean());
			} else if (jsonPrimitive.isNumber()) {
				t.setItemPrimitiveType(ItemPrimitiveType.n);
				t.setItemStringValueType(ItemStringValueType.u);
				t.value(jsonPrimitive.getAsDouble());
			} else if (jsonPrimitive.isString()) {
				t.setItemPrimitiveType(ItemPrimitiveType.s);
				t.setItemStringValueType(ItemStringValueType.s);
				t.value(jsonPrimitive.getAsString());
			}
		} else if (root.isJsonArray()) {
			t.setItemType(ItemType.a);
			t.setItemPrimitiveType(ItemPrimitiveType.u);
			t.setItemStringValueType(ItemStringValueType.u);
			int i = 0;
			for (JsonElement o : root.getAsJsonArray()) {
				List<String> ps = new ArrayList<>(paths);
				ps.add(Integer.toString(i++));
				this.disassemble(o, ps, list);
			}
		} else if (root.isJsonNull()) {
			t.setItemType(ItemType.n);
			t.setItemPrimitiveType(ItemPrimitiveType.u);
			t.setItemStringValueType(ItemStringValueType.u);
		} else if (root.isJsonObject()) {
			t.setItemType(ItemType.o);
			t.setItemPrimitiveType(ItemPrimitiveType.u);
			t.setItemStringValueType(ItemStringValueType.u);
			for (Entry<String, JsonElement> entry : root.getAsJsonObject().entrySet()) {
				List<String> ps = new ArrayList<String>(paths);
				ps.add(entry.getKey());
				this.disassemble(entry.getValue(), ps, list);
			}
		}
		return list;
	}

	public JsonElement assemble(List<T> list) {
		return this.assemble(list, null);
	}

	public JsonElement assemble(List<T> list, Integer retract) {
		try {
			JsonElement root = null;
			List<T> sorted = new ArrayList<>(list);
			this.sort(sorted);
			for (T t : sorted) {
				root = this.assemble(sorted, t, retract, root);
			}
			return root;
		} catch (Exception e) {
			logger.error(e);
		}
		return null;
	}

	private JsonElement assemble(List<T> list, T t, Integer retract, JsonElement root) throws Exception {
		JsonElement jsonElement = null;
		if (t.getItemType().equals(ItemType.p)) {
			if (t.getItemPrimitiveType().equals(ItemPrimitiveType.s)) {
				jsonElement = new JsonPrimitive(Objects.toString(t.getStringValue(), ""));
			} else if (t.getItemPrimitiveType().equals(ItemPrimitiveType.n)) {
				jsonElement = new JsonPrimitive(t.getNumberValue());
			} else if (t.getItemPrimitiveType().equals(ItemPrimitiveType.b)) {
				jsonElement = new JsonPrimitive(t.getBooleanValue());
			}
		} else if (t.getItemType().equals(ItemType.o)) {
			jsonElement = new JsonObject();
		} else if (t.getItemType().equals(ItemType.a)) {
			jsonElement = new JsonArray();
		} else if (t.getItemType().equals(ItemType.n)) {
			jsonElement = JsonNull.INSTANCE;
		}
		if (root == null) {
			return jsonElement;
		}
		List<String> paths = t.paths();
		if (null != retract) {
			paths = paths.subList(retract, paths.size());
		}
		String name = paths.get(paths.size() - 1);
		JsonElement o = root;
		for (int i = 0; i < paths.size() - 1; i++) {
			String path = paths.get(i);
			if (!StringUtils.isNumeric(path)) {
				o = o.getAsJsonObject().get(path);
			} else {
				o = o.getAsJsonArray().get(Integer.parseInt(path));
			}
		}
		if (!StringUtils.isNumeric(name)) {
			o.getAsJsonObject().add(name, jsonElement);
		} else {
			try {
				o.getAsJsonArray().add(jsonElement);
			} catch (Exception e) {
				throw new Exception(e.getMessage() + ": name:" + name + ", jsonElement:" + jsonElement + ".", e);
			}
		}
		return root;
	}

	public void sort(List<T> list) {
		Collections.sort(list, new Comparator<T>() {
			public int compare(T o1, T o2) {
				int c = 0;
				c = comparePathLocation(o1.getPath0Location(), o2.getPath0Location());
				if (c == 0) {
					c = comparePath(o1.getPath0(), o2.getPath0());
					if (c == 0) {
						c = comparePathLocation(o1.getPath1Location(), o2.getPath1Location());
						if (c == 0) {
							c = comparePath(o1.getPath1(), o2.getPath1());
							if (c == 0) {
								c = comparePathLocation(o1.getPath2Location(), o2.getPath2Location());
								if (c == 0) {
									c = comparePath(o1.getPath2(), o2.getPath2());
									if (c == 0) {
										c = comparePathLocation(o1.getPath3Location(), o2.getPath3Location());
										if (c == 0) {
											c = comparePath(o1.getPath3(), o2.getPath3());
											if (c == 0) {
												c = comparePathLocation(o1.getPath4Location(), o2.getPath4Location());
												if (c == 0) {
													c = comparePath(o1.getPath4(), o2.getPath4());
													if (c == 0) {
														c = comparePathLocation(o1.getPath5Location(),
																o2.getPath5Location());
														if (c == 0) {
															c = comparePath(o1.getPath5(), o2.getPath5());
															if (c == 0) {
																c = comparePathLocation(o1.getPath6Location(),
																		o2.getPath6Location());
																if (c == 0) {
																	c = comparePath(o1.getPath6(), o2.getPath6());
																	if (c == 0) {
																		c = comparePathLocation(o1.getPath7Location(),
																				o2.getPath7Location());
																		if (c == 0) {
																			c = comparePath(o1.getPath7(),
																					o2.getPath7());
																		}
																	}
																}
															}
														}
													}
												}
											}
										}
									}
								}
							}
						}
					}
				}
				return c;
			}
		});
	}

	private int comparePath(String p1, String p2) {
		if (StringUtils.isEmpty(p1) && StringUtils.isEmpty(p2)) {
			return 0;
		} else if (StringUtils.isEmpty(p1)) {
			return -1;
		} else if (StringUtils.isEmpty(p2)) {
			return 1;
		}
		return ObjectUtils.compare(p1, p2);
	}

	private int comparePathLocation(Integer pl1, Integer pl2) {
		if ((null == pl1) && (null == pl2)) {
			return 0;
		} else if (null == pl1) {
			return -1;
		} else if (null == pl2) {
			return 1;
		}
		return ObjectUtils.compare(pl1, pl2);
	}

//	public List<T> subtract(List<T> l1, List<T> l2) throws Exception {
//		List<T> result = new ArrayList<>();
//		List<T> list2 = new ArrayList<>(l2);
//		T dummy = null;
//		next: for (T t1 : l1) {
//			if (null != dummy) {
//				list2.remove(dummy);
//			}
//			for (T t2 : list2) {
//				if (this.equate(t1, t2)) {
//					dummy = t2;
//					continue next;
//				}
//			}
//			result.add(t1);
//		}
//		return result;
//	}

	public boolean equate(T t1, T t2) {
		if (!Objects.equals(t1.getPath0(), t2.getPath0())) {
			return false;
		}
		if (!Objects.equals(t1.getPath1(), t2.getPath1())) {
			return false;
		}
		if (!Objects.equals(t1.getPath2(), t2.getPath2())) {
			return false;
		}
		if (!Objects.equals(t1.getPath3(), t2.getPath3())) {
			return false;
		}
		if (!Objects.equals(t1.getPath4(), t2.getPath4())) {
			return false;
		}
		if (!Objects.equals(t1.getPath5(), t2.getPath5())) {
			return false;
		}
		if (!Objects.equals(t1.getPath6(), t2.getPath6())) {
			return false;
		}
		if (!Objects.equals(t1.getPath7(), t2.getPath7())) {
			return false;
		}
		if (!Objects.equals(t1.getItemType(), t2.getItemType())) {
			return false;
		} else if (Objects.equals(t1.getItemType(), ItemType.p)) {
			if (!Objects.equals(t1.getItemPrimitiveType(), t2.getItemPrimitiveType())) {
				return false;
			} else {
				if (t1.getItemPrimitiveType().equals(ItemPrimitiveType.s)) {
					if (!Objects.equals(t1.getItemStringValueType(), t2.getItemStringValueType())) {
						return false;
					} else {
						return Objects.equals(t1.getStringValue(), t2.getStringValue());
					}
				} else if (t1.getItemPrimitiveType().equals(ItemPrimitiveType.n)) {
					return Objects.equals(t1.getNumberValue(), t2.getNumberValue());
				} else if (t1.getItemPrimitiveType().equals(ItemPrimitiveType.b)) {
					return Objects.equals(t1.getBooleanValue(), t2.getBooleanValue());
				}
			}
		}
		return true;
	}

	public String text(List<T> items, boolean escapeNumber, boolean escapeBoolean, boolean escapeId,
			boolean simplifyDistinguishedName, boolean htmlToText, String split) {
		StringBuilder builder = new StringBuilder();
		this.sort(items);
		for (T t : items) {
			if (Objects.equals(t.getItemType(), ItemType.p)) {
				if (Objects.equals(t.getItemPrimitiveType(), ItemPrimitiveType.s)
						&& StringUtils.isNotEmpty(t.getStringValue())) {
					if (escapeId && StringTools.isUUIDFormat(t.getStringValue())) {
						continue;
					}
					if (simplifyDistinguishedName && OrganizationDefinition.isDistinguishedName(t.getStringValue())) {
						builder.append(OrganizationDefinition.name(t.getStringValue()));
						builder.append(split);
						continue;
					}
					if (htmlToText) {
						builder.append(t.getStringValue().replaceAll("(?s)<[^>]*>(\\s*<[^>]*>)*", ""));
						builder.append(split);
						continue;
					}
					builder.append(t.getStringValue());
				}
				if (Objects.equals(t.getItemPrimitiveType(), ItemPrimitiveType.b) && (null != t.getBooleanValue())
						&& (!escapeBoolean)) {
					builder.append(Objects.toString(t.getBooleanValue()));
					builder.append(split);
				}
				if (Objects.equals(t.getItemPrimitiveType(), ItemPrimitiveType.n) && (null != t.getNumberValue())
						&& (!escapeNumber)) {
					builder.append(Objects.toString(t.getNumberValue()));
					builder.append(split);
				}
			}
		}
		return builder.toString();
	}

	/**
	 * 此方法在item数据较大(>20000) 时由于双重循环导致运行时间较长( > 5000ms) 改为新的使用hashMap实现.<br>
	 * Thanks 李舟<lizhou@mochasoft.com.cn>
	 **/
	public List<T> subtract(List<T> l1, List<T> l2) throws Exception {
		List<T> result = new ArrayList<>();
		HashMap<Wrap, T> map = new HashMap<>();
		for (T t2 : l2) {
			map.put(new Wrap(t2), t2);
		}
		for (T t1 : l1) {
			T t2 = map.get(new Wrap(t1));
			if (null == t2) {
				result.add(t1);
			}
		}
		return result;
	}

	private static class Wrap {

		private DataItem item;

		private Wrap(DataItem item) {
			this.item = item;
		}

		@Override
		public int hashCode() {
			return this.item.path().hashCode();
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			Wrap other = (Wrap) obj;
			if (!Objects.equals(this.item.getPath0(), other.item.getPath0())) {
				return false;
			}
			if (!Objects.equals(this.item.getPath1(), other.item.getPath1())) {
				return false;
			}
			if (!Objects.equals(this.item.getPath2(), other.item.getPath2())) {
				return false;
			}
			if (!Objects.equals(this.item.getPath3(), other.item.getPath3())) {
				return false;
			}
			if (!Objects.equals(this.item.getPath4(), other.item.getPath4())) {
				return false;
			}
			if (!Objects.equals(this.item.getPath5(), other.item.getPath5())) {
				return false;
			}
			if (!Objects.equals(this.item.getPath6(), other.item.getPath6())) {
				return false;
			}
			if (!Objects.equals(this.item.getPath7(), other.item.getPath7())) {
				return false;
			}
			if (!Objects.equals(this.item.getItemType(), other.item.getItemType())) {
				return false;
			} else if (Objects.equals(this.item.getItemType(), ItemType.p)) {
				if (!Objects.equals(this.item.getItemPrimitiveType(), other.item.getItemPrimitiveType())) {
					return false;
				} else {
					if (this.item.getItemPrimitiveType().equals(ItemPrimitiveType.s)) {
						if (!Objects.equals(this.item.getItemStringValueType(), other.item.getItemStringValueType())) {
							return false;
						} else {
							return Objects.equals(this.item.getStringValue(), other.item.getStringValue());
						}
					} else if (this.item.getItemPrimitiveType().equals(ItemPrimitiveType.n)) {
						return Objects.equals(this.item.getNumberValue(), other.item.getNumberValue());
					} else if (this.item.getItemPrimitiveType().equals(ItemPrimitiveType.b)) {
						return Objects.equals(this.item.getBooleanValue(), other.item.getBooleanValue());
					}
				}
			}
			return true;
		}
	}

//	private final List<Pair<BiFunction<Object, Object, Integer>, Function<T, Object>>> COMPARE_SEQ = Arrays.asList(
//			comparePathLocationOf(DataItem::getPath0Location), comparePathLocationOf(DataItem::getPath1Location),
//			comparePathOf(DataItem::getPath1), comparePathLocationOf(DataItem::getPath2Location),
//			comparePathOf(DataItem::getPath2), comparePathLocationOf(DataItem::getPath3Location),
//			comparePathOf(DataItem::getPath3), comparePathLocationOf(DataItem::getPath4Location),
//			comparePathOf(DataItem::getPath4), comparePathLocationOf(DataItem::getPath5Location),
//			comparePathOf(DataItem::getPath5), comparePathLocationOf(DataItem::getPath6Location),
//			comparePathOf(DataItem::getPath6), comparePathLocationOf(DataItem::getPath7Location),
//			comparePathOf(DataItem::getPath7));
//
//	private Pair<BiFunction<Object, Object, Integer>, Function<T, Object>> comparePathLocationOf(
//			final Function<T, Object> mapper) {
//		return Pair.of(DataItemConverter::comparePathLocation, mapper);
//	}
//
//	private Pair<BiFunction<Object, Object, Integer>, Function<T, Object>> comparePathOf(
//			final Function<T, Object> mapper) {
//		return Pair.of(DataItemConverter::comparePath, mapper);
//	}
//
//	private   final Comparator<T> comparator = new Comparator<T>() {
//			public int compare(final T o1, final T o2) {
//				return COMPARE_SEQ.stream()
//					.mapToInt(t -> {
//						final Function<T, Object> getter = Pair.getRight();
//						return Pair.getLeft().apply(getter.apply(o1), getter.apply(o2));
//					})
//					.filter(t -> t != 0)
//					.findAny()
//					.orElse(0);
//			}
//		}
//
//	public void sort(List<T> list) {
//		Collections.sort(list, comparator);
//	}
}
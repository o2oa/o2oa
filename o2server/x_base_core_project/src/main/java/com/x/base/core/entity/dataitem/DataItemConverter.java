package com.x.base.core.entity.dataitem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.exception.RunningException;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.organization.OrganizationDefinition;
import com.x.base.core.project.tools.StringTools;

public class DataItemConverter<T extends DataItem> {

    public static final int STRING_VALUE_MAX_LENGTH = JpaObject.length_255B;

    private static final Gson gson = XGsonBuilder.instance();

    private static final int MAX_PATH_DEPTH = 8;

    private Class<T> clz;

    private Comparator<T> dataItemComparator = Comparator
            .comparing(T::getPath0Location, (o1, o2) -> comparePathLocation(o1, o2))
            .thenComparing(T::getPath0, (o1, o2) -> comparePath(o1, o2))
            .thenComparing(T::getPath1Location, (o1, o2) -> comparePathLocation(o1, o2))
            .thenComparing(T::getPath1, (o1, o2) -> comparePath(o1, o2))
            .thenComparing(T::getPath2Location, (o1, o2) -> comparePathLocation(o1, o2))
            .thenComparing(T::getPath2, (o1, o2) -> comparePath(o1, o2))
            .thenComparing(T::getPath3Location, (o1, o2) -> comparePathLocation(o1, o2))
            .thenComparing(T::getPath3, (o1, o2) -> comparePath(o1, o2))
            .thenComparing(T::getPath4Location, (o1, o2) -> comparePathLocation(o1, o2))
            .thenComparing(T::getPath4, (o1, o2) -> comparePath(o1, o2))
            .thenComparing(T::getPath5Location, (o1, o2) -> comparePathLocation(o1, o2))
            .thenComparing(T::getPath5, (o1, o2) -> comparePath(o1, o2))
            .thenComparing(T::getPath6Location, (o1, o2) -> comparePathLocation(o1, o2))
            .thenComparing(T::getPath6, (o1, o2) -> comparePath(o1, o2))
            .thenComparing(T::getPath7Location, (o1, o2) -> comparePathLocation(o1, o2))
            .thenComparing(T::getPath7, (o1, o2) -> comparePath(o1, o2));

    public DataItemConverter(Class<T> clz) {
        this.clz = clz;
    }

    /**
     * 对root进行检查<br>
     * 1.检查是否使用了纯数字作为key
     * 
     * @param jsonElement
     * @throws RunningException
     */
    private void checkJsonElement(JsonElement jsonElement) throws RunningException {
        if ((null == jsonElement) || jsonElement.isJsonNull() || jsonElement.isJsonPrimitive()) {
            return;
        }
        if (jsonElement.isJsonObject()) {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
                if (StringUtils.isNumeric(entry.getKey())) {
                    throw new RunningException("key can not be numeric value:{}.", entry.getKey());
                }
                if (entry.getValue().isJsonArray() || entry.getValue().isJsonObject()) {
                    checkJsonElement(entry.getValue());
                }
            }
        } else if (jsonElement.isJsonArray()) {
            JsonArray jsonArray = jsonElement.getAsJsonArray();
            for (JsonElement o : jsonArray) {
                checkJsonElement(o);
            }
        }
    }

    public List<T> disassemble(JsonElement root, String... prefixPaths) throws Exception {
        checkJsonElement(root);
        List<String> paths = new ArrayList<>();
        paths.addAll(Arrays.asList(prefixPaths));

        List<T> list = this.disassemble(root, paths, new ArrayList<>());
        /**
         * 20170905 通过 javascripting 转换的Map将 array -> {0:"xxxxx"}
         * 的格式,变成了一个对象而非array,所以这里需要进行单独的判断,把用数字下标的Map强制设置为List
         */
        // fixme 使用 ListIterator 替代后面的for循环。 消除List::get的使用
//        for (int i = 0; i < (list.size() - 1); i++) {
//            /** 因为要取下一个,循环不用取最后一个数. */
//            T t = list.get(i);
//            if (t.getItemType() == ItemType.o) {
//                T next = list.get(i + 1);
//                // 是一个数字的值,说明是数组中的一个
//                if (StringUtils.isNumeric(next.paths().get(next.paths().size() - 1))) {
//                    // 说明上一个T应该是一个Array
//                    t.setItemType(ItemType.a);
//                }
//            }
//        }

        ListIterator<T> iterator = list.listIterator();
        while (iterator.hasNext()) {
            T t = iterator.next();
            if (t.getItemType() == ItemType.o && iterator.hasNext()) {
                T next = iterator.next();
                // 是一个数字的值,说明是数组中的一个
                if (StringUtils.isNumeric(next.paths().get(next.paths().size() - 1))) {
                    // 说明上一个T应该是一个Array
                    t.setItemType(ItemType.a);
                }
                // 回到上一个元素
                iterator.previous();
            }
        }

        return list;
    }

    private List<T> disassemble(JsonElement root, List<String> paths, List<T> list) throws Exception {
        T t = clz.getDeclaredConstructor().newInstance();
        t.path(paths);
        list.add(t);
        if (root.isJsonPrimitive()) {
            disassembleJsonPrimitive(root, t);
        } else if (root.isJsonArray()) {
            disassembleJsonArray(root, paths, list, t);
        } else if (root.isJsonNull()) {
            disassembleJsonNull(t);
        } else if (root.isJsonObject()) {
            disassembleJsonObject(root, paths, list, t);
        }
        return list;
    }

    private void disassembleJsonNull(T t) {
        t.setItemType(ItemType.n);
        t.setItemPrimitiveType(ItemPrimitiveType.u);
        t.setItemStringValueType(ItemStringValueType.u);
    }

    private void disassembleJsonObject(JsonElement jsonElement, List<String> paths, List<T> list, T t)
            throws Exception {
        if (paths.size() < MAX_PATH_DEPTH) {
            t.setItemType(ItemType.o);
            t.setItemPrimitiveType(ItemPrimitiveType.u);
            t.setItemStringValueType(ItemStringValueType.u);
            for (Entry<String, JsonElement> entry : jsonElement.getAsJsonObject().entrySet()) {
                List<String> ps = new ArrayList<>(paths);
                ps.add(entry.getKey());
                this.disassemble(entry.getValue(), ps, list);
            }
        } else {
            disassembleMaxPathDepthToJsonElement(t, jsonElement);
        }
    }

    private void disassembleJsonArray(JsonElement jsonElement, List<String> paths, List<T> list, T t) throws Exception {
        if (paths.size() < MAX_PATH_DEPTH) {
            t.setItemType(ItemType.a);
            t.setItemPrimitiveType(ItemPrimitiveType.u);
            t.setItemStringValueType(ItemStringValueType.u);
            int i = 0;
            for (JsonElement o : jsonElement.getAsJsonArray()) {
                List<String> ps = new ArrayList<>(paths);
                ps.add(Integer.toString(i++));
                this.disassemble(o, ps, list);
            }
        } else {
            disassembleMaxPathDepthToJsonElement(t, jsonElement);
        }
    }

    private void disassembleJsonPrimitive(JsonElement jsonElement, T t) throws Exception {
        t.setItemType(ItemType.p);
        JsonPrimitive jsonPrimitive = jsonElement.getAsJsonPrimitive();
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
    }

    /**
     * 将8层以上的对象或者Array直接以jsonElement的toString进行保存,不再进行递归解析
     * 
     * @param t
     * @param root
     */
    private void disassembleMaxPathDepthToJsonElement(T t, JsonElement root) {
        t.setItemType(ItemType.j);
        t.setItemPrimitiveType(ItemPrimitiveType.u);
        t.setItemStringValueType(ItemStringValueType.u);
        t.setStringValue(root.toString());
    }

    public JsonElement assemble(List<T> list) {
        return this.assemble(list, null);
    }

    public JsonElement assemble(List<T> list, Integer retract) {
        JsonElement root = null;
        List<T> sorted = new ArrayList<>(list);
        this.sort(sorted);
        for (T t : sorted) {
            root = this.assemble(t, retract, root);
        }
        return root;
    }

    private JsonElement assemble(T t, Integer retract, JsonElement root) {
        JsonElement jsonElement = assembleDataItemToJsonElement(t);
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
                if (o.isJsonObject()) {
                    o = o.getAsJsonObject().get(path);
                } else {
                    // 无法正确定位位置,结束本条内容组装,直接返回
                    return root;
                }
            } else {
                if (o.isJsonArray()) {
                    o = o.getAsJsonArray().get(Integer.parseInt(path));
                } else {
                    // 无法正确定位位置,结束本条内容组装,直接返回
                    return root;
                }
            }
        }
        attachToParent(o, name, jsonElement);
        return root;
    }

    /**
     * 将子元素加入到父元素中
     * 
     * @param parent
     * @param name
     * @param sub
     */
    private void attachToParent(JsonElement parent, String name, JsonElement sub) {
        // 如果字段名不幸设置为存数字这里需要进行特别的判断
        if (!StringUtils.isNumeric(name)) {
            if (parent.isJsonObject()) {
                parent.getAsJsonObject().add(name, sub);
            }
        } else if (parent.isJsonArray()) {
            parent.getAsJsonArray().add(sub);
        }
    }

    /**
     * 将T转换成一个独立的jsonElement
     * 
     * @param t
     * @return
     */
    private JsonElement assembleDataItemToJsonElement(T t) {
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
        } else if (t.getItemType().equals(ItemType.j)) {
            jsonElement = gson.fromJson(t.getStringValue(), JsonElement.class);
        }
        return jsonElement;
    }

    public void sort(List<T> list) {
        Collections.sort(list, dataItemComparator);
    }
//    public void sort(List<T> list) {
//        Collections.sort(list, new Comparator<T>() {
//            public int compare(T o1, T o2) {
//                int c = 0;
//                c = comparePathLocation(o1.getPath0Location(), o2.getPath0Location());
//                if (c == 0) {
//                    c = comparePath(o1.getPath0(), o2.getPath0());
//                    if (c == 0) {
//                        c = comparePathLocation(o1.getPath1Location(), o2.getPath1Location());
//                        if (c == 0) {
//                            c = comparePath(o1.getPath1(), o2.getPath1());
//                            if (c == 0) {
//                                c = comparePathLocation(o1.getPath2Location(), o2.getPath2Location());
//                                if (c == 0) {
//                                    c = comparePath(o1.getPath2(), o2.getPath2());
//                                    if (c == 0) {
//                                        c = comparePathLocation(o1.getPath3Location(), o2.getPath3Location());
//                                        if (c == 0) {
//                                            c = comparePath(o1.getPath3(), o2.getPath3());
//                                            if (c == 0) {
//                                                c = comparePathLocation(o1.getPath4Location(), o2.getPath4Location());
//                                                if (c == 0) {
//                                                    c = comparePath(o1.getPath4(), o2.getPath4());
//                                                    if (c == 0) {
//                                                        c = comparePathLocation(o1.getPath5Location(),
//                                                                o2.getPath5Location());
//                                                        if (c == 0) {
//                                                            c = comparePath(o1.getPath5(), o2.getPath5());
//                                                            if (c == 0) {
//                                                                c = comparePathLocation(o1.getPath6Location(),
//                                                                        o2.getPath6Location());
//                                                                if (c == 0) {
//                                                                    c = comparePath(o1.getPath6(), o2.getPath6());
//                                                                    if (c == 0) {
//                                                                        c = comparePathLocation(o1.getPath7Location(),
//                                                                                o2.getPath7Location());
//                                                                        if (c == 0) {
//                                                                            c = comparePath(o1.getPath7(),
//                                                                                    o2.getPath7());
//                                                                        }
//                                                                    }
//                                                                }
//                                                            }
//                                                        }
//                                                    }
//                                                }
//                                            }
//                                        }
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
//                return c;
//            }
//        });
//    }

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

//    public boolean equate(T t1, T t2) {
//        if ((!Objects.equals(t1.getPath0(), t2.getPath0())) || (!Objects.equals(t1.getPath1(), t2.getPath1()))
//                || (!Objects.equals(t1.getPath2(), t2.getPath2())) || (!Objects.equals(t1.getPath3(), t2.getPath3()))
//                || (!Objects.equals(t1.getPath4(), t2.getPath4())) || (!Objects.equals(t1.getPath5(), t2.getPath5()))
//                || (!Objects.equals(t1.getPath6(), t2.getPath6())) || (!Objects.equals(t1.getPath7(), t2.getPath7()))) {
//            return false;
//        }
//        if (!Objects.equals(t1.getItemType(), t2.getItemType())) {
//            return false;
//        } else if (Objects.equals(t1.getItemType(), ItemType.p)) {
//            if (!Objects.equals(t1.getItemPrimitiveType(), t2.getItemPrimitiveType())) {
//                return false;
//            } else {
//                if (t1.getItemPrimitiveType().equals(ItemPrimitiveType.s)) {
//                    if (!Objects.equals(t1.getItemStringValueType(), t2.getItemStringValueType())) {
//                        return false;
//                    } else {
//                        return Objects.equals(t1.getStringValue(), t2.getStringValue());
//                    }
//                } else if (t1.getItemPrimitiveType().equals(ItemPrimitiveType.n)) {
//                    return Objects.equals(t1.getNumberValue(), t2.getNumberValue());
//                } else if (t1.getItemPrimitiveType().equals(ItemPrimitiveType.b)) {
//                    return Objects.equals(t1.getBooleanValue(), t2.getBooleanValue());
//                }
//            }
//        } else if (Objects.equals(t1.getItemType(), ItemType.j)) {
//            return Objects.equals(t1.getStringValue(), t2.getStringValue());
//        }
//        return true;
//    }

    /**
     * 此方法在item数据较大(>20000) 时由于双重循环导致运行时间较长( > 5000ms) 改为新的使用hashMap实现.<br>
     * Thanks 李舟<lizhou@mochasoft.com.cn>
     **/
    public List<T> subtract(List<T> l1, List<T> l2) {
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
            if ((!Objects.equals(this.item.getPath0(), other.item.getPath0()))
                    || (!Objects.equals(this.item.getPath1(), other.item.getPath1()))
                    || (!Objects.equals(this.item.getPath2(), other.item.getPath2()))
                    || (!Objects.equals(this.item.getPath3(), other.item.getPath3()))
                    || (!Objects.equals(this.item.getPath4(), other.item.getPath4()))
                    || (!Objects.equals(this.item.getPath5(), other.item.getPath5()))
                    || (!Objects.equals(this.item.getPath6(), other.item.getPath6()))
                    || (!Objects.equals(this.item.getPath7(), other.item.getPath7()))) {
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
            } else if (Objects.equals(this.item.getItemType(), ItemType.j)) {
                return Objects.equals(this.item.getStringValue(), other.item.getStringValue());
            }
            return true;
        }
    }

    /**
     * 提取DataItem纯文本辅助类
     * 
     * @author ray
     *
     */
    public static class ItemText {

        private ItemText() {
            // nothing
        }

        private static final Predicate<DataItem> NUMBERPREDICATE = o -> Objects.equals(ItemType.p, o.getItemType())
                && Objects.equals(ItemPrimitiveType.n, o.getItemPrimitiveType());

        public static final Predicate<DataItem> BOOLEANPREDICATE = o -> Objects.equals(ItemType.p, o.getItemType())
                && Objects.equals(ItemPrimitiveType.b, o.getItemPrimitiveType());

        public static final Predicate<DataItem> STRINGPREDICATE = o -> Objects.equals(ItemType.p, o.getItemType())
                && Objects.equals(ItemPrimitiveType.s, o.getItemPrimitiveType());

        public static final UnaryOperator<String> ESCAPEIDFUNCTION = o -> (null == o
                || (StringTools.UUID_REGEX.matcher(o).matches())) ? "" : o;

        public static final UnaryOperator<String> SIMPLIFYDISTINGUISHEDNAMEFUNCTION = o -> OrganizationDefinition
                .isDistinguishedName(o) ? OrganizationDefinition.name(o) : o;

        public static final UnaryOperator<String> HTMLTOTEXTFUNCTION = o -> Jsoup.parse(o).text();

        public static final Function<DataItem, String> DATAITEMTOSTRINGFUNCTION = o -> {
            String value = "";
            switch (o.getItemPrimitiveType()) {
                case b:
                    if (null != o.getBooleanValue()) {
                        value = BooleanUtils.toStringTrueFalse(o.getBooleanValue());
                    }
                    break;
                case n:
                    if (null != o.getNumberValue()) {
                        value = Objects.toString(o.getNumberValue());
                    }
                    break;
                default:
                    value = o.getStringValue();
            }
            return value;
        };

        /**
         * 提取DataItem中的文本,拼接成String
         * 
         * @param items
         * @param escapeNumber
         * @param escapeBoolean
         * @param escapeId
         * @param simplifyDistinguishedName
         * @param htmlToText
         * @param split
         * @return
         */
        public static String text(List<? extends DataItem> items, boolean escapeNumber, boolean escapeBoolean,
                boolean escapeId, boolean simplifyDistinguishedName, boolean htmlToText, String split) {

            Predicate<DataItem> predicate = concretePredicate(escapeNumber, escapeBoolean);

            Function<String, String> function = concreteFunction(escapeId, simplifyDistinguishedName, htmlToText);

            return items.stream().filter(predicate).map(DATAITEMTOSTRINGFUNCTION).map(function)
                    .filter(StringUtils::isNotBlank).distinct()
                    .collect(Collectors.joining(StringUtils.isBlank(split) ? "," : split));

        }

        private static Predicate<DataItem> concretePredicate(boolean escapeNumber, boolean escapeBoolean) {

            Predicate<DataItem> p = STRINGPREDICATE;

            if (!escapeNumber) {
                p.or(NUMBERPREDICATE);
            }

            if (!escapeBoolean) {
                p.or(BOOLEANPREDICATE);
            }
            return p;

        }

        private static Function<String, String> concreteFunction(boolean escapeId, boolean simplifyDistinguishedName,
                boolean htmlToText) {
            Function<String, String> f = StringUtils::trim;
            if (escapeId) {
                f = f.andThen(ESCAPEIDFUNCTION);
            }
            if (simplifyDistinguishedName) {
                f = f.andThen(SIMPLIFYDISTINGUISHEDNAMEFUNCTION);
            }
            if (htmlToText) {
                f = f.andThen(HTMLTOTEXTFUNCTION);
            }
            return f;
        }

    }

}
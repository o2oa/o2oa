package com.x.base.core.project.gson;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.List;

/**
 * JSON数组操作工具类
 */
public class JsonArrayOperator {

    private static final Gson gson = new Gson();

    /**
     * 在指定位置插入JSON对象
     * @param jsonArray 原始JSON数组
     * @param index 插入位置索引
     * @param jsonObject 要插入的JSON对象
     * @return 操作后的新JSON数组
     * @throws IllegalArgumentException 如果索引越界
     */
    public static JsonArray insert(JsonArray jsonArray, int index, JsonObject jsonObject) {
        if (jsonArray == null) {
            jsonArray = new JsonArray();
        }

        if (index < 0 || index > jsonArray.size()) {
            throw new IllegalArgumentException("插入位置索引越界: " + index);
        }

        JsonArray result = new JsonArray();

        // 插入位置前的元素
        for (int i = 0; i < index; i++) {
            result.add(jsonArray.get(i));
        }

        // 插入新元素
        result.add(jsonObject);

        // 插入位置后的元素
        for (int i = index; i < jsonArray.size(); i++) {
            result.add(jsonArray.get(i));
        }

        return result;
    }

    /**
     * 在指定位置插入对象（自动转换为JSON）
     * @param jsonArray 原始JSON数组
     * @param index 插入位置索引
     * @param object 要插入的对象
     * @return 操作后的新JSON数组
     */
    public static JsonArray insert(JsonArray jsonArray, int index, Object object) {
        JsonObject jsonObject = gson.toJsonTree(object).getAsJsonObject();
        return insert(jsonArray, index, jsonObject);
    }

    /**
     * 删除指定位置的JSON对象
     * @param jsonArray 原始JSON数组
     * @param index 要删除的位置索引
     * @return 操作后的新JSON数组
     * @throws IllegalArgumentException 如果索引越界
     */
    public static JsonArray remove(JsonArray jsonArray, int index) {
        if (jsonArray == null || jsonArray.size() == 0) {
            return new JsonArray();
        }

        if (index < 0 || index >= jsonArray.size()) {
            throw new IllegalArgumentException("删除位置索引越界: " + index);
        }

        JsonArray result = new JsonArray();

        for (int i = 0; i < jsonArray.size(); i++) {
            if (i != index) {
                result.add(jsonArray.get(i));
            }
        }

        return result;
    }

    /**
     * 移动指定位置的对象到新位置
     * @param jsonArray 原始JSON数组
     * @param fromIndex 原位置索引
     * @param toIndex 目标位置索引
     * @return 操作后的新JSON数组
     * @throws IllegalArgumentException 如果索引越界
     */
    public static JsonArray move(JsonArray jsonArray, int fromIndex, int toIndex) {
        if (jsonArray == null || jsonArray.size() == 0) {
            return new JsonArray();
        }

        if (fromIndex < 0 || fromIndex >= jsonArray.size() ||
                toIndex < 0 || toIndex >= jsonArray.size()) {
            throw new IllegalArgumentException("移动位置索引越界: fromIndex=" + fromIndex + ", toIndex=" + toIndex);
        }

        if (fromIndex == toIndex) {
            // 位置相同，直接返回原数组的副本
            return deepCopy(jsonArray);
        }

        // 先移除原位置的元素
        JsonElement movedElement = jsonArray.get(fromIndex);
        JsonArray tempArray = remove(jsonArray, fromIndex);

        // 计算在新数组中的插入位置
        int insertIndex = (toIndex > fromIndex) ? toIndex - 1 : toIndex;

        // 插入到新位置
        return insert(tempArray, insertIndex, movedElement.getAsJsonObject());
    }

    /**
     * 深拷贝JSON数组
     * @param jsonArray 原始JSON数组
     * @return 深拷贝后的新JSON数组
     */
    public static JsonArray deepCopy(JsonArray jsonArray) {
        if (jsonArray == null) {
            return new JsonArray();
        }

        JsonArray copy = new JsonArray();
        for (JsonElement element : jsonArray) {
            copy.add(gson.fromJson(gson.toJson(element), JsonElement.class));
        }
        return copy;
    }

    /**
     * 将JSON数组转换为对象列表
     * @param jsonArray JSON数组
     * @param clazz 目标类型
     * @return 对象列表
     */
    public static <T> List<T> toList(JsonArray jsonArray, Class<T> clazz) {
        List<T> list = new ArrayList<>();
        if (jsonArray != null) {
            for (JsonElement element : jsonArray) {
                list.add(gson.fromJson(element, clazz));
            }
        }
        return list;
    }

    /**
     * 将对象列表转换为JSON数组
     * @param list 对象列表
     * @return JSON数组
     */
    public static <T> JsonArray fromList(List<T> list) {
        JsonArray jsonArray = new JsonArray();
        if (list != null) {
            for (T item : list) {
                jsonArray.add(gson.toJsonTree(item));
            }
        }
        return jsonArray;
    }
}

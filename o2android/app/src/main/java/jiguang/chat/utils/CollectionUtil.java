package jiguang.chat.utils;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * 集合处理工具类
 */
@SuppressWarnings("WeakerAccess")
public final class CollectionUtil {
    /**
     * List集合转换为数组
     *
     * @param items  List数据
     * @param tClass 数据的类型class
     * @param <T>    Class
     * @return 转换完成后的数组
     */
    @SuppressWarnings("unchecked")
    public static <T> T[] toArray(List<T> items, Class<T> tClass) {
        if (items == null || items.size() == 0)
            return null;
        int size = items.size();
        try {
            T[] array = (T[]) Array.newInstance(tClass, size);
            return items.toArray(array);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Set集合转换为数组
     *
     * @param items  List数据
     * @param tClass 数据的类型class
     * @param <T>    Class
     * @return 转换完成后的数组
     */
    @SuppressWarnings("unchecked")
    public static <T> T[] toArray(Set<T> items, Class<T> tClass) {
        if (items == null || items.size() == 0)
            return null;
        int size = items.size();
        try {
            T[] array = (T[]) Array.newInstance(tClass, size);
            return items.toArray(array);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 数组集合转换为HashSet集合
     *
     * @param items 数组集合
     * @param <T>   Class
     * @return 转换完成后的Hash集合
     */
    public static <T> HashSet<T> toHashSet(T[] items) {
        if (items == null || items.length == 0)
            return null;
        HashSet<T> set = new HashSet<>();
        Collections.addAll(set, items);
        return set;
    }

    /**
     * 数组集合转换为ArrayList集合
     *
     * @param items 数组集合
     * @param <T>   Class
     * @return 转换完成后的ArrayList集合
     */
    public static <T> ArrayList<T> toArrayList(T[] items) {
        if (items == null || items.length == 0)
            return null;
        ArrayList<T> list = new ArrayList<>();
        Collections.addAll(list, items);
        return list;
    }

    /**
     * 移动一个列表中的元素位置
     * <p>
     * A B C D 四个元素，移动2坐标移动到0坐标，
     * 结果： C A B D
     *
     * @param collection   列表
     * @param fromPosition 起始位置
     * @param toPosition   目标位置
     * @param <T>          元素
     * @return 列表
     */
    public static <T> Collection<T> move(List<T> collection, int fromPosition, int toPosition) {
        int maxPosition = collection.size() - 1;
        if (fromPosition == toPosition || fromPosition > maxPosition || toPosition > maxPosition)
            return collection;

        if (fromPosition < toPosition) {
            T fromModel = collection.get(fromPosition);
            T toModel = collection.get(toPosition);

            collection.remove(fromPosition);
            collection.add(collection.indexOf(toModel) + 1, fromModel);
        } else {
            T fromModel = collection.get(fromPosition);
            collection.remove(fromPosition);
            collection.add(toPosition, fromModel);
        }

        return collection;
    }

    /**
     * 筛选操作，通过该方法可以按照{@link Character} 中的返回进行筛选
     * 如果需要则返回 TRUE, 否则 FALSE
     *
     * @param source  源数据
     * @param checker 检查者 需要则返回 TRUE, 否则 FALSE
     * @param <T>     类型
     * @return 返回一个集合列表
     */
    public static <T> List<T> filter(List<T> source, Checker<T> checker) {
        Iterator<T> iterator = source.iterator();
        while (iterator.hasNext()) {
            if (!checker.check(iterator.next()))
                iterator.remove();
        }
        return source;
    }

    /**
     * 筛选操作，通过该方法可以按照{@link Character} 中的返回进行筛选
     * 如果需要则返回 TRUE, 否则 FALSE
     *
     * @param source  源数据数组
     * @param checker 检查者 需要则返回 TRUE, 否则 FALSE
     * @param <T>     类型
     * @return 返回一个集合列表
     */
    public static <T> List<T> filter(T[] source, Checker<T> checker) {
        ArrayList<T> list = toArrayList(source);
        return filter(list, checker);
    }

    /**
     * 检查者，筛选者
     *
     * @param <T> 类型
     */
    public interface Checker<T> {
        boolean check(T t);
    }
}

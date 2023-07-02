package com.x.base.core.project.jaxrs;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.Tuple;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections4.map.ListOrderedMap;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.exception.ExceptionWhen;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.CollectionTools;
import com.x.base.core.project.tools.ListTools;

public abstract class StandardJaxrsAction extends AbstractJaxrsAction {

	private static Logger logger = LoggerFactory.getLogger(StandardJaxrsAction.class);

	protected static Integer list_max = 200;
	protected static Integer list_min = 1;

	protected static final String DESC = "desc";
	protected static final String ASC = "asc";

	/**
	 * 查询数据的下一页对象信息
	 * 
	 * [2015年12月24日 李义添加了注释内容，代码没有改变]
	 * 
	 * @param cls           实体类
	 * @param wcls          wrap类
	 * @param id            上一页最后一条的ID
	 * @param count         每页条目数:pagesize
	 * @param sequenceField 作分页序列的属性名
	 * @param equals        等于的条件集合
	 * @param notEquals     不等于（例外）的条件集合
	 * @param likes         模糊查询条件集合
	 * @param ins           IN查询条件集合
	 * @param notIns        NOT IN查询条件集合
	 * @param members       隶属于
	 * @param notMembers    非隶属于
	 * @param andJoin       条件的连接方式
	 * @param order         排序方式ASC|DESC
	 * @return
	 * @throws Exception
	 */
	public <T extends JpaObject, W extends GsonPropertyObject> ActionResult<List<W>> standardListNext(Class<T> cls,
			Class<W> wcls, String id, Integer count, String sequenceField, EqualsTerms equals, NotEqualsTerms notEquals,
			LikeTerms likes, InTerms ins, NotInTerms notIns, MemberTerms members, NotMemberTerms notMembers,
			BetweenTerms betweens, boolean andJoin, String order) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			// 先获取上一页最后一条的sequence值，如果有值的话，以此sequence值作为依据取后续的count条数据
			Object sequence = null;
			if (!StringUtils.equalsIgnoreCase(id, EMPTY_SYMBOL)) {
				sequence = PropertyUtils.getProperty(emc.find(id, cls, ExceptionWhen.not_found), sequenceField);
			}
			EntityManager em = emc.get(cls);
			String str = "SELECT o FROM " + cls.getCanonicalName() + " o";
			/* 预编译的SQL语句的参数序号，必须由1开始 */
			Integer index = 1;
			List<String> ps = new ArrayList<>();
			List<Object> vs = new ArrayList<>();
			// 如果sequence值不为空，那么从sequence开始，如果为空则从头开始
			if (null != sequence) {
				ps.add("o." + sequenceField + " " + (StringUtils.equalsIgnoreCase(order, DESC) ? "<" : ">")
						+ (" ?" + (index)));
				vs.add(sequence);
				index++;
			}
			// 以下组织各种条件子句，放到List<String> ps里，相应的值放到List<Object> vs里
			if (null != equals && (!equals.isEmpty())) {
				for (Entry<String, Object> en : equals.entrySet()) {
					ps.add("o." + en.getKey() + (" = ?" + index));
					vs.add(en.getValue());
					index++;
				}
			}
			if (null != notEquals && (!notEquals.isEmpty())) {
				for (Entry<String, Object> en : notEquals.entrySet()) {
					ps.add("(o." + en.getKey() + (" <> ?" + index) + " or o." + en.getKey() + " is null)");
					vs.add(en.getValue());
					index++;
				}
			}
			// like有点特殊，多个like一般用or相联
			if (null != likes && (!likes.isEmpty())) {
				List<String> ors = new ArrayList<>();
				for (Entry<String, Object> en : likes.entrySet()) {
					for (String s : StringUtils.split(en.getValue().toString(), " ")) {
						ors.add("o." + en.getKey() + (" Like ?" + index));
						vs.add("%" + s + "%");
						index++;
					}
				}
				ps.add("(" + StringUtils.join(ors, " or ") + ")");
			}
			if (null != ins && (!ins.isEmpty())) {
				for (Entry<String, Collection<?>> en : ins.entrySet()) {
					ps.add("o." + en.getKey() + (" in ?" + index));
					vs.add(en.getValue());
					index++;
				}
			}
			if (null != notIns && (!notIns.isEmpty())) {
				for (Entry<String, Collection<?>> en : notIns.entrySet()) {
					ps.add("o." + en.getKey() + (" not in ?" + index));
					vs.add(en.getValue());
					index++;
				}
			}
			if (null != members && (!members.isEmpty())) {
				for (Entry<String, Object> en : members.entrySet()) {
					ps.add(("?" + index) + (" member of o." + en.getKey()));
					vs.add(en.getValue());
					index++;
				}
			}
			if (null != notMembers && (!notMembers.isEmpty())) {
				for (Entry<String, Object> en : notMembers.entrySet()) {
					ps.add(("?" + index) + (" not member of o." + en.getKey()));
					vs.add(en.getValue());
					index++;
				}
			}
			if (null != betweens && (!betweens.isEmpty())) {
				for (Entry<String, List<Object>> en : betweens.entrySet()) {
					ps.add("(o." + en.getKey() + (" between ?" + index) + " AND ?" + (index + 1) + ")");
					vs.add(en.getValue().get(0));
					vs.add(en.getValue().get(1));
					index++;
					index++;
				}
			}
			// 使用指定的方式将条件子句组合起来
			if (!ps.isEmpty()) {
				str += " where " + StringUtils.join(ps, (andJoin ? " and " : " or "));
			}
			// 排序
			str += " order by o." + sequenceField + " " + (StringUtils.equalsIgnoreCase(order, DESC) ? DESC : ASC);
			Query query = em.createQuery(str, cls);
			// 为查询设置所有的参数值
			for (int i = 0; i < vs.size(); i++) {
				query.setParameter(i + 1, vs.get(i));
			}

			List<W> wraps = new ArrayList<W>();
			// 限制查询的条数，一般就取一页的条目数，最多为超过list_max
			@SuppressWarnings("unchecked")
			List<T> list = query.setMaxResults(Math.max(Math.min(count, list_max), list_min)).getResultList();
			if (!list.isEmpty()) {
				// 查询初始的编号
				Long rank = this.rank(emc, cls, sequenceField, PropertyUtils.getProperty(list.get(0), sequenceField),
						equals, notEquals, likes, ins, notIns, members, notMembers, andJoin, order);
				Constructor<W> constructor = wcls.getConstructor(new Class[] { cls });
				for (T t : list) {
					W w = (W) constructor.newInstance(new Object[] { t });
					PropertyUtils.setProperty(w, "rank", rank++);
					wraps.add(w);
				}
			}
			ActionResult<List<W>> result = new ActionResult<>();
			result.setData(wraps);
			// 设置查询结果的总条目数
			result.setCount(this.count(emc, cls, equals, notEquals, likes, ins, notIns, members, notMembers, andJoin));
			return result;
		}
	}

	/**
	 * 查询数据的下一页对象信息
	 * 
	 * [2015年12月24日 李义添加了注释内容，代码没有改变]
	 * 
	 * @param copier        对象转换类
	 * @param id            上一页最后一条的ID
	 * @param count         每页条目数:pagesize
	 * @param sequenceField 作分页序列的属性名
	 * @param equals        等于的条件集合
	 * @param notEquals     不等于（例外）的条件集合
	 * @param likes         模糊查询条件集合
	 * @param ins           IN查询条件集合
	 * @param notIns        NOT IN查询条件集合
	 * @param members       隶属于
	 * @param notMembers    非隶属于
	 * @param andJoin       条件的连接方式
	 * @param order         排序方式ASC|DESC
	 * @return
	 * @throws Exception
	 */
	public <T extends JpaObject, W> ActionResult<List<W>> standardListNext(WrapCopier<T, W> copier, String id,
			Integer count, String sequenceField, EqualsTerms equals, NotEqualsTerms notEquals, LikeTerms likes,
			InTerms ins, NotInTerms notIns, MemberTerms members, NotMemberTerms notMembers, BetweenTerms betweens,
			boolean andJoin, String order) throws Exception {
		Class<T> tClass = (Class<T>) copier.getOrigClass();
		Class<W> wClass = (Class<W>) copier.getDestClass();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			// 先获取上一页最后一条的sequence值，如果有值的话，以此sequence值作为依据取后续的count条数据
			Object sequence = null;
			if (!StringUtils.equalsIgnoreCase(id, EMPTY_SYMBOL)) {
				sequence = PropertyUtils.getProperty(emc.find(id, tClass, ExceptionWhen.not_found), sequenceField);
			}
			EntityManager em = emc.get(tClass);
			String str = "SELECT o FROM " + tClass.getCanonicalName() + " o";
			/* 预编译的SQL语句的参数序号，必须由1开始 */
			Integer index = 1;
			List<String> ps = new ArrayList<>();
			List<Object> vs = new ArrayList<>();
			// 如果sequence值不为空，那么从sequence开始，如果为空则从头开始
			if (null != sequence) {
				ps.add("o." + sequenceField + " " + (StringUtils.equalsIgnoreCase(order, DESC) ? "<" : ">")
						+ (" ?" + (index)));
				vs.add(sequence);
				index++;
			}
			// 以下组织各种条件子句，放到List<String> ps里，相应的值放到List<Object> vs里
			if (null != equals && (!equals.isEmpty())) {
				for (Entry<String, Object> en : equals.entrySet()) {
					ps.add("o." + en.getKey() + (" = ?" + index));
					vs.add(en.getValue());
					index++;
				}
			}
			if (null != notEquals && (!notEquals.isEmpty())) {
				for (Entry<String, Object> en : notEquals.entrySet()) {
					ps.add("(o." + en.getKey() + (" <> ?" + index) + " or o." + en.getKey() + " is null)");
					vs.add(en.getValue());
					index++;
				}
			}
			// like有点特殊，多个like一般用or相联
			if (null != likes && (!likes.isEmpty())) {
				List<String> ors = new ArrayList<>();
				for (Entry<String, Object> en : likes.entrySet()) {
					for (String s : StringUtils.split(en.getValue().toString(), " ")) {
						ors.add("o." + en.getKey() + (" Like ?" + index));
						vs.add("%" + s + "%");
						index++;
					}
				}
				ps.add("(" + StringUtils.join(ors, " or ") + ")");
			}
			if (null != ins && (!ins.isEmpty())) {
				for (Entry<String, Collection<?>> en : ins.entrySet()) {
					ps.add("o." + en.getKey() + (" in ?" + index));
					vs.add(en.getValue());
					index++;
				}
			}
			if (null != notIns && (!notIns.isEmpty())) {
				for (Entry<String, Collection<?>> en : notIns.entrySet()) {
					ps.add("o." + en.getKey() + (" not in ?" + index));
					vs.add(en.getValue());
					index++;
				}
			}
			if (null != members && (!members.isEmpty())) {
				for (Entry<String, Object> en : members.entrySet()) {
					ps.add(("?" + index) + (" member of o." + en.getKey()));
					vs.add(en.getValue());
					index++;
				}
			}
			if (null != notMembers && (!notMembers.isEmpty())) {
				for (Entry<String, Object> en : notMembers.entrySet()) {
					ps.add(("?" + index) + (" not member of o." + en.getKey()));
					vs.add(en.getValue());
					index++;
				}
			}
			if (null != betweens && (!betweens.isEmpty())) {
				for (Entry<String, List<Object>> en : betweens.entrySet()) {
					ps.add("(o." + en.getKey() + (" between ?" + index) + " AND ?" + (index + 1) + ")");
					vs.add(en.getValue().get(0));
					vs.add(en.getValue().get(1));
					index++;
					index++;
				}
			}
			// 使用指定的方式将条件子句组合起来
			if (!ps.isEmpty()) {
				str += " where " + StringUtils.join(ps, (andJoin ? " and " : " or "));
			}
			// 排序
			str += " order by o." + sequenceField + " " + (StringUtils.equalsIgnoreCase(order, DESC) ? DESC : ASC);
			Query query = em.createQuery(str, tClass);
			// 为查询设置所有的参数值
			for (int i = 0; i < vs.size(); i++) {
				query.setParameter(i + 1, vs.get(i));
			}
			List<W> wraps = new ArrayList<W>();
			// 限制查询的条数，一般就取一页的条目数，最多为超过list_max
			@SuppressWarnings("unchecked")
			List<T> list = query.setMaxResults(Math.max(Math.min(count, list_max), list_min)).getResultList();
			if (!list.isEmpty()) {
				// 查询初始的编号
				Long rank = this.rank(emc, tClass, sequenceField, PropertyUtils.getProperty(list.get(0), sequenceField),
						equals, notEquals, likes, ins, notIns, members, notMembers, andJoin, order);
				// 为输出的结果进行编号
				for (T t : list) {
					W w = wClass.newInstance();
					copier.copy(t, w);
					PropertyUtils.setProperty(w, "rank", rank++);
					wraps.add(w);
				}
			}
			ActionResult<List<W>> result = new ActionResult<>();
			result.setData(wraps);
			// 设置查询结果的总条目数
			result.setCount(
					this.count(emc, tClass, equals, notEquals, likes, ins, notIns, members, notMembers, andJoin));
			return result;
		}
	}

	/**
	 * 查询数据的上一页对象信息
	 * 
	 * [2015年12月24日 李义添加了注释内容，代码没有改变]
	 * 
	 * @param cls           实体类
	 * @param wcls          wrap类
	 * @param id            上一页最后一条的ID
	 * @param count         每页条目数:pagesize
	 * @param sequenceField 作分页序列的属性名
	 * @param equals        等于的条件集合
	 * @param notEquals     不等于（例外）的条件集合
	 * @param likes         模糊查询条件集合
	 * @param ins           IN查询条件集合
	 * @param notIns        NOT IN查询条件集合
	 * @param members       隶属于
	 * @param notMembers    非隶属于
	 * @param andJoin       条件的连接方式
	 * @param order         排序方式ASC|DESC
	 * @return
	 * @throws Exception
	 */
	public <T extends JpaObject, W extends GsonPropertyObject> ActionResult<List<W>> standardListPrev(Class<T> cls,
			Class<W> wcls, String id, Integer count, String sequenceField, EqualsTerms equals, NotEqualsTerms notEquals,
			LikeTerms likes, InTerms ins, NotInTerms notIns, MemberTerms members, NotMemberTerms notMembers,
			BetweenTerms betweens, boolean andJoin, String order) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			// 先获取上一页最后一条的sequence值，如果有值的话，以此sequence值作为依据取后续的count条数据
			Object sequence = null;
			if (!StringUtils.equalsIgnoreCase(id, EMPTY_SYMBOL)) {
				sequence = PropertyUtils.getProperty(emc.find(id, cls, ExceptionWhen.not_found), sequenceField);
			}
			EntityManager em = emc.get(cls);
			String str = "SELECT o FROM " + cls.getCanonicalName() + " o";
			/* 预编译的SQL语句的参数序号，必须由1开始 */
			Integer index = 1;
			List<String> ps = new ArrayList<>();
			List<Object> vs = new ArrayList<>();
			// 如果sequence值不为空，那么从sequence开始，如果为空则从头开始
			if (null != sequence) {
				ps.add("o." + sequenceField + " " + (StringUtils.equalsIgnoreCase(order, DESC) ? ">" : "<")
						+ (" ?" + (index)));
				vs.add(sequence);
				index++;
			}
			// 以下组织各种条件子句，放到List<String> ps里，相应的值放到List<Object> vs里
			if (null != equals && (!equals.isEmpty())) {
				for (Entry<String, Object> en : equals.entrySet()) {
					ps.add("o." + en.getKey() + (" = ?" + index));
					vs.add(en.getValue());
					index++;
				}
			}
			if (null != notEquals && (!notEquals.isEmpty())) {
				for (Entry<String, Object> en : notEquals.entrySet()) {
					ps.add("(o." + en.getKey() + (" <> ?" + index) + " or o." + en.getKey() + " is null)");
					vs.add(en.getValue());
					index++;
				}
			}
			// like有点特殊，多个like一般用or相联
			if (null != likes && (!likes.isEmpty())) {
				List<String> ors = new ArrayList<>();
				for (Entry<String, Object> en : likes.entrySet()) {
					for (String s : StringUtils.split(en.getValue().toString(), " ")) {
						ors.add("o." + en.getKey() + (" Like ?" + index));
						vs.add("%" + s + "%");
						index++;
					}
				}
				ps.add("(" + StringUtils.join(ors, " or ") + ")");
			}
			if (null != ins && (!ins.isEmpty())) {
				for (Entry<String, Collection<?>> en : ins.entrySet()) {
					ps.add("o." + en.getKey() + (" in ?" + index));
					vs.add(en.getValue());
					index++;
				}
			}
			if (null != notIns && (!notIns.isEmpty())) {
				for (Entry<String, Collection<?>> en : notIns.entrySet()) {
					ps.add("o." + en.getKey() + (" not in ?" + index));
					vs.add(en.getValue());
					index++;
				}
			}
			if (null != members && (!members.isEmpty())) {
				for (Entry<String, Object> en : members.entrySet()) {
					ps.add(("?" + index) + (" member of o." + en.getKey()));
					vs.add(en.getValue());
					index++;
				}
			}
			if (null != notMembers && (!notMembers.isEmpty())) {
				for (Entry<String, Object> en : notMembers.entrySet()) {
					ps.add(("?" + index) + (" not member of o." + en.getKey()));
					vs.add(en.getValue());
					index++;
				}
			}
			if (null != betweens && (!betweens.isEmpty())) {
				for (Entry<String, List<Object>> en : betweens.entrySet()) {
					ps.add("(o." + en.getKey() + (" between ?" + index) + " AND ?" + (index + 1) + ")");
					vs.add(en.getValue().get(0));
					vs.add(en.getValue().get(1));
					index++;
					index++;
				}
			}
			// 使用指定的方式将条件子句组合起来
			if (!ps.isEmpty()) {
				str += " where " + StringUtils.join(ps, (andJoin ? " and " : " or "));
			}
			// 排序
			str += " order by o." + sequenceField + " " + (StringUtils.equalsIgnoreCase(order, DESC) ? ASC : DESC);
			Query query = em.createQuery(str, cls);
			// 为查询设置所有的参数值
			for (int i = 0; i < vs.size(); i++) {
				query.setParameter(i + 1, vs.get(i));
			}
			List<W> wraps = new ArrayList<W>();
			// 限制查询的条数，一般就取一页的条目数，最多为超过list_max
			@SuppressWarnings("unchecked")
			List<T> list = query.setMaxResults(Math.max(Math.min(count, list_max), list_min)).getResultList();
			if (!list.isEmpty()) {
				// 查询初始的编号
				Long rank = this.rank(emc, cls, sequenceField,
						PropertyUtils.getProperty(list.get(list.size() - 1), sequenceField), equals, notEquals, likes,
						ins, notIns, members, notMembers, andJoin, order);
				Constructor<W> constructor = wcls.getConstructor(new Class[] { cls });
				// 为输出的结果进行编号
				for (int i = list.size() - 1; i >= 0; i--) {
					W w = (W) constructor.newInstance(new Object[] { list.get(i) });
					PropertyUtils.setProperty(w, "rank", rank++);
					wraps.add(w);
				}
			}
			ActionResult<List<W>> result = new ActionResult<>();
			result.setData(wraps);
			// 设置查询结果的总条目数
			result.setCount(this.count(emc, cls, equals, notEquals, likes, ins, notIns, members, notMembers, andJoin));
			return result;
		}
	}

	/**
	 * 查询数据的上一页对象信息
	 * 
	 * [2015年12月24日 李义添加了注释内容，代码没有改变]
	 * 
	 * @param id            上一页最后一条的ID
	 * @param count         每页条目数:pagesize
	 * @param sequenceField 作分页序列的属性名
	 * @param equals        等于的条件集合
	 * @param notEquals     不等于（例外）的条件集合
	 * @param likes         模糊查询条件集合
	 * @param ins           IN查询条件集合
	 * @param notIns        NOT IN查询条件集合
	 * @param members       隶属于
	 * @param notMembers    非隶属于
	 * @param andJoin       条件的连接方式
	 * @param order         排序方式ASC|DESC
	 * @return
	 * @throws Exception
	 */
	public <T extends JpaObject, W> ActionResult<List<W>> standardListPrev(WrapCopier<T, W> copier, String id,
			Integer count, String sequenceField, EqualsTerms equals, NotEqualsTerms notEquals, LikeTerms likes,
			InTerms ins, NotInTerms notIns, MemberTerms members, NotMemberTerms notMembers, BetweenTerms betweens,
			boolean andJoin, String order) throws Exception {
		Class<T> tClass = (Class<T>) copier.getOrigClass();
		Class<W> wClass = (Class<W>) copier.getDestClass();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			// 先获取上一页最后一条的sequence值，如果有值的话，以此sequence值作为依据取后续的count条数据
			Object sequence = null;
			if (!StringUtils.equalsIgnoreCase(id, EMPTY_SYMBOL)) {
				sequence = PropertyUtils.getProperty(emc.find(id, tClass, ExceptionWhen.not_found), sequenceField);
			}
			EntityManager em = emc.get(tClass);
			String str = "SELECT o FROM " + tClass.getCanonicalName() + " o";
			/* 预编译的SQL语句的参数序号，必须由1开始 */
			Integer index = 1;
			List<String> ps = new ArrayList<>();
			List<Object> vs = new ArrayList<>();
			// 如果sequence值不为空，那么从sequence开始，如果为空则从头开始
			if (null != sequence) {
				ps.add("o." + sequenceField + " " + (StringUtils.equalsIgnoreCase(order, DESC) ? ">" : "<")
						+ (" ?" + (index)));
				vs.add(sequence);
				index++;
			}
			// 以下组织各种条件子句，放到List<String> ps里，相应的值放到List<Object> vs里
			if (null != equals && (!equals.isEmpty())) {
				for (Entry<String, Object> en : equals.entrySet()) {
					ps.add("o." + en.getKey() + (" = ?" + index));
					vs.add(en.getValue());
					index++;
				}
			}
			if (null != notEquals && (!notEquals.isEmpty())) {
				for (Entry<String, Object> en : notEquals.entrySet()) {
					ps.add("(o." + en.getKey() + (" <> ?" + index) + " or o." + en.getKey() + " is null)");
					vs.add(en.getValue());
					index++;
				}
			}
			// like有点特殊，多个like一般用or相联
			if (null != likes && (!likes.isEmpty())) {
				List<String> ors = new ArrayList<>();
				for (Entry<String, Object> en : likes.entrySet()) {
					for (String s : StringUtils.split(en.getValue().toString(), " ")) {
						ors.add("o." + en.getKey() + (" Like ?" + index));
						vs.add("%" + s + "%");
						index++;
					}
				}
				ps.add("(" + StringUtils.join(ors, " or ") + ")");
			}
			if (null != ins && (!ins.isEmpty())) {
				for (Entry<String, Collection<?>> en : ins.entrySet()) {
					ps.add("o." + en.getKey() + (" in ?" + index));
					vs.add(en.getValue());
					index++;
				}
			}
			if (null != notIns && (!notIns.isEmpty())) {
				for (Entry<String, Collection<?>> en : notIns.entrySet()) {
					ps.add("o." + en.getKey() + (" not in ?" + index));
					vs.add(en.getValue());
					index++;
				}
			}
			if (null != members && (!members.isEmpty())) {
				for (Entry<String, Object> en : members.entrySet()) {
					ps.add(("?" + index) + (" member of o." + en.getKey()));
					vs.add(en.getValue());
					index++;
				}
			}
			if (null != notMembers && (!notMembers.isEmpty())) {
				for (Entry<String, Object> en : notMembers.entrySet()) {
					ps.add(("?" + index) + (" not member of o." + en.getKey()));
					vs.add(en.getValue());
					index++;
				}
			}
			if (null != betweens && (!betweens.isEmpty())) {
				for (Entry<String, List<Object>> en : betweens.entrySet()) {
					ps.add("(o." + en.getKey() + (" between ?" + index) + " AND ?" + (index + 1) + ")");
					vs.add(en.getValue().get(0));
					vs.add(en.getValue().get(1));
					index++;
					index++;
				}
			}
			// 使用指定的方式将条件子句组合起来
			if (!ps.isEmpty()) {
				str += " where " + StringUtils.join(ps, (andJoin ? " and " : " or "));
			}
			// 排序
			str += " order by o." + sequenceField + " " + (StringUtils.equalsIgnoreCase(order, DESC) ? ASC : DESC);
			Query query = em.createQuery(str, tClass);
			// 为查询设置所有的参数值
			for (int i = 0; i < vs.size(); i++) {
				query.setParameter(i + 1, vs.get(i));
			}
			List<W> wraps = new ArrayList<W>();
			// 限制查询的条数，一般就取一页的条目数，最多为超过list_max
			@SuppressWarnings("unchecked")
			List<T> list = query.setMaxResults(Math.max(Math.min(count, list_max), list_min)).getResultList();
			if (!list.isEmpty()) {
				// 查询初始的编号
				Long rank = this.rank(emc, tClass, sequenceField,
						PropertyUtils.getProperty(list.get(list.size() - 1), sequenceField), equals, notEquals, likes,
						ins, notIns, members, notMembers, andJoin, order);
				// 为输出的结果进行编号
				for (int i = list.size() - 1; i >= 0; i--) {
					W w = wClass.newInstance();
					copier.copy(list.get(i), w);
					PropertyUtils.setProperty(w, "rank", rank++);
					wraps.add(w);
				}
			}
			ActionResult<List<W>> result = new ActionResult<>();
			result.setData(wraps);
			// 设置查询结果的总条目数
			result.setCount(
					this.count(emc, tClass, equals, notEquals, likes, ins, notIns, members, notMembers, andJoin));
			return result;
		}
	}

	private <T extends JpaObject> Long rank(EntityManagerContainer emc, Class<T> cls, String sequenceField,
			Object sequence, ListOrderedMap<String, Object> equals, ListOrderedMap<String, Object> notEquals,
			ListOrderedMap<String, Object> likes, ListOrderedMap<String, Collection<?>> ins,
			ListOrderedMap<String, Collection<?>> notIns, ListOrderedMap<String, Object> members,
			ListOrderedMap<String, Object> notMembers, boolean andJoin, String order) throws Exception {
		EntityManager em = emc.get(cls);
		String str = "SELECT count(o) FROM " + cls.getCanonicalName() + " o";
		/* 预编译的SQL语句的参数序号，必须由1开始 */
		Integer index = 1;
		List<String> ps = new ArrayList<>();
		List<Object> vs = new ArrayList<>();
		if (null != sequence) {
			ps.add("o." + sequenceField + " " + (StringUtils.equalsIgnoreCase(order, DESC) ? ">" : "<")
					+ (" ?" + (index)));
			vs.add(sequence);
			index++;
		}
		if (null != equals && (!equals.isEmpty())) {
			for (Entry<String, Object> en : equals.entrySet()) {
				ps.add("o." + en.getKey() + (" = ?" + index));
				vs.add(en.getValue());
				index++;
			}
		}
		if (null != notEquals && (!notEquals.isEmpty())) {
			for (Entry<String, Object> en : notEquals.entrySet()) {
				ps.add("(o." + en.getKey() + (" <> ?" + index) + " or o." + en.getKey() + " is null)");
				vs.add(en.getValue());
				index++;
			}
		}
		if (null != likes && (!likes.isEmpty())) {
			List<String> ors = new ArrayList<>();
			for (Entry<String, Object> en : likes.entrySet()) {
				ors.add("o." + en.getKey() + (" Like ?" + index));
				vs.add("%" + en.getValue() + "%");
				index++;
			}
			ps.add("(" + StringUtils.join(ors, " or ") + ")");
		}
		if (null != ins && (!ins.isEmpty())) {
			for (Entry<String, Collection<?>> en : ins.entrySet()) {
				ps.add("o." + en.getKey() + (" in ?" + index));
				vs.add(en.getValue());
				index++;
			}
		}
		if (null != notIns && (!notIns.isEmpty())) {
			for (Entry<String, Collection<?>> en : notIns.entrySet()) {
				ps.add("o." + en.getKey() + (" not in ?" + index));
				vs.add(en.getValue());
				index++;
			}
		}
		if (null != members && (!members.isEmpty())) {
			for (Entry<String, Object> en : members.entrySet()) {
				ps.add(("?" + index) + (" member of o." + en.getKey()));
				vs.add(en.getValue());
				index++;
			}
		}
		if (null != notMembers && (!notMembers.isEmpty())) {
			for (Entry<String, Object> en : notMembers.entrySet()) {
				ps.add(("?" + index) + (" not member of o." + en.getKey()));
				vs.add(en.getValue());
				index++;
			}
		}
		if (!ps.isEmpty()) {
			str += " where " + StringUtils.join(ps, (andJoin ? " and " : " or "));
		}
		Query query = em.createQuery(str, cls);
		for (int i = 0; i < vs.size(); i++) {
			query.setParameter(i + 1, vs.get(i));
		}
		return (Long) query.getSingleResult() + 1;
	}

	private <T extends JpaObject> Long count(EntityManagerContainer emc, Class<T> cls, EqualsTerms equals,
			NotEqualsTerms notEquals, LikeTerms likes, InTerms ins, NotInTerms notIns, MemberTerms members,
			NotMemberTerms notMembers, boolean andJoin) throws Exception {
		EntityManager em = emc.get(cls);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<T> root = cq.from(cls);
		List<Predicate> ps = new ArrayList<>();
		if (null != equals && (!equals.isEmpty())) {
			for (Entry<String, Object> en : equals.entrySet()) {
				ps.add(cb.equal(root.get(en.getKey()), en.getValue()));
			}
		}
		if (null != notEquals && (!notEquals.isEmpty())) {
			for (Entry<String, Object> en : notEquals.entrySet()) {
				ps.add(cb.or(cb.isNull(root.get(en.getKey())), cb.notEqual(root.get(en.getKey()), en.getValue())));
			}
		}
		if (null != likes && (!likes.isEmpty())) {
			List<Predicate> ors = new ArrayList<>();
			for (Entry<String, Object> en : likes.entrySet()) {
				ors.add(cb.like(root.get(en.getKey()), "%" + en.getValue() + "%"));
			}
			ps.add(cb.or(CollectionTools.toArray(ors, Predicate.class)));
		}
		if (null != ins && (!ins.isEmpty())) {
			for (Entry<String, Collection<?>> en : ins.entrySet()) {
				ps.add(root.get(en.getKey()).in(en.getValue()));
			}
		}
		if (null != notIns && (!notIns.isEmpty())) {
			for (Entry<String, Collection<?>> en : notIns.entrySet()) {
				ps.add(cb.not(root.get(en.getKey()).in(en.getValue())));
			}
		}
		if (null != members && (!members.isEmpty())) {
			for (Entry<String, Object> en : members.entrySet()) {
				ps.add(cb.isMember(en.getValue(), root.get(en.getKey())));
			}
		}
		if (null != notMembers && (!notMembers.isEmpty())) {
			for (Entry<String, Object> en : notMembers.entrySet()) {
				ps.add(cb.isNotMember(en.getValue(), root.get(en.getKey())));
			}
		}
		Predicate p = null;
		if (BooleanUtils.isTrue(andJoin)) {
			p = cb.and(CollectionTools.toArray(ps, Predicate.class));
		} else {
			p = cb.or(CollectionTools.toArray(ps, Predicate.class));
		}
		return em.createQuery(cq.select(cb.countDistinct(root)).where(p)).getSingleResult();
	}

	/**
	 * 查询数据的下一页对象信息
	 * 
	 * [2015年12月24日 李义添加了注释内容，代码没有改变]
	 * 
	 * @param copier        对象转换类
	 * @param id            上一页最后一条的ID
	 * @param count         每页条目数:pagesize
	 * @param sequenceField 作分页序列的属性名
	 * @param equals        等于的条件集合
	 * @param notEquals     不等于（例外）的条件集合
	 * @param likes         模糊查询条件集合
	 * @param ins           IN查询条件集合
	 * @param notIns        NOT IN查询条件集合
	 * @param members       隶属于
	 * @param notMembers    非隶属于
	 * @param andJoin       条件的连接方式
	 * @param order         排序方式ASC|DESC
	 * @return
	 * @throws Exception
	 */
	public <T extends JpaObject, W extends GsonPropertyObject> ActionResult<List<W>> standardFetchAttributeListNext(
			EntityManagerContainer emc, WrapCopier<T, W> copier, String id, Integer count, String sequenceField,
			EqualsTerms equals, NotEqualsTerms notEquals, LikeTerms likes, InTerms ins, NotInTerms notIns,
			MemberTerms members, NotMemberTerms notMembers, boolean andJoin, String order) throws Exception {
		Class<T> tClass = (Class<T>) copier.getOrigClass();
		/** 先获取上一页最后一条的sequence值，如果有值的话，以此sequence值作为依据取后续的count条数据 */
		Object sequence = null;
		if (!StringUtils.equalsIgnoreCase(id, EMPTY_SYMBOL)) {
			Object o = emc.find(id, tClass);
			if (null != o) {
				sequence = PropertyUtils.getProperty(o, sequenceField);
			}
		}
		EntityManager em = emc.get(tClass);
		String str = "SELECT o.id FROM " + tClass.getCanonicalName() + " o";
		/** 预编译的SQL语句的参数序号，必须由1开始 */
		Integer index = 1;
		List<String> ps = new ArrayList<>();
		List<Object> vs = new ArrayList<>();
		/** 如果sequence值不为空，那么从sequence开始，如果为空则从头开始 */
		if (null != sequence) {
			ps.add("o." + sequenceField + " " + (StringUtils.equalsIgnoreCase(order, DESC) ? "<" : ">")
					+ (" ?" + (index)));
			vs.add(sequence);
			index++;
		}
		/** 以下组织各种条件子句，放到List<String> ps里，相应的值放到List<Object> vs里 */
		if (null != equals && (!equals.isEmpty())) {
			for (Entry<String, Object> en : equals.entrySet()) {
				ps.add("o." + en.getKey() + (" = ?" + index));
				vs.add(en.getValue());
				index++;
			}
		}
		if (null != notEquals && (!notEquals.isEmpty())) {
			for (Entry<String, Object> en : notEquals.entrySet()) {
				ps.add("(o." + en.getKey() + (" <> ?" + index) + " or o." + en.getKey() + " is null)");
				vs.add(en.getValue());
				index++;
			}
		}
		/** like有点特殊，多个like一般用or相联 */
		if (null != likes && (!likes.isEmpty())) {
			List<String> ors = new ArrayList<>();
			for (Entry<String, Object> en : likes.entrySet()) {
				for (String s : StringUtils.split(en.getValue().toString(), " ")) {
					ors.add("o." + en.getKey() + (" Like ?" + index));
					vs.add("%" + s + "%");
					index++;
				}
			}
			ps.add("(" + StringUtils.join(ors, " or ") + ")");
		}
		if (null != ins && (!ins.isEmpty())) {
			for (Entry<String, Collection<?>> en : ins.entrySet()) {
				ps.add("o." + en.getKey() + (" in ?" + index));
				vs.add(en.getValue());
				index++;
			}
		}
		if (null != notIns && (!notIns.isEmpty())) {
			for (Entry<String, Collection<?>> en : notIns.entrySet()) {
				ps.add("o." + en.getKey() + (" not in ?" + index));
				vs.add(en.getValue());
				index++;
			}
		}
		if (null != members && (!members.isEmpty())) {
			for (Entry<String, Object> en : members.entrySet()) {
				ps.add(("?" + index) + (" member of o." + en.getKey()));
				vs.add(en.getValue());
				index++;
			}
		}
		if (null != notMembers && (!notMembers.isEmpty())) {
			for (Entry<String, Object> en : notMembers.entrySet()) {
				ps.add(("?" + index) + (" not member of o." + en.getKey()));
				vs.add(en.getValue());
				index++;
			}
		}
		/** 使用指定的方式将条件子句组合起来 */
		if (!ps.isEmpty()) {
			str += " where " + StringUtils.join(ps, (andJoin ? " and " : " or "));
		}
		/** 排序 */
		str += " order by o." + sequenceField + " " + (StringUtils.equalsIgnoreCase(order, DESC) ? DESC : ASC);
		Query query = em.createQuery(str, tClass);
		/** 为查询设置所有的参数值 */
		for (int i = 0; i < vs.size(); i++) {
			query.setParameter(i + 1, vs.get(i));
		}
		/** 限制查询的条数，一般就取一页的条目数，最多为超过list_max */
		@SuppressWarnings("unchecked")
		List<String> ids = query.setMaxResults(Math.max(Math.min(count, list_max), list_min)).getResultList();
		/** 获取所有符合条件的id */
		List<W> wos = emc.fetch(ids, copier);
		if (!wos.isEmpty()) {
			/** 查询初始的编号 */
			Long rank = this.rank(emc, tClass, sequenceField, PropertyUtils.getProperty(wos.get(0), sequenceField),
					equals, notEquals, likes, ins, notIns, members, notMembers, andJoin, order);
			/** 为输出的结果进行编号 */
			for (W w : wos) {
				PropertyUtils.setProperty(w, "rank", rank++);
			}
		}
		ActionResult<List<W>> result = new ActionResult<>();
		result.setData(wos);
		/** 设置查询结果的总条目数 */
		result.setCount(this.count(emc, tClass, equals, notEquals, likes, ins, notIns, members, notMembers, andJoin));
		return result;
	}

	public <T extends JpaObject, W extends GsonPropertyObject> ActionResult<List<W>> standardFetchAttributeListPrev(
			EntityManagerContainer emc, WrapCopier<T, W> copier, String id, Integer count, String sequenceField,
			EqualsTerms equals, NotEqualsTerms notEquals, LikeTerms likes, InTerms ins, NotInTerms notIns,
			MemberTerms members, NotMemberTerms notMembers, boolean andJoin, String order) throws Exception {
		Class<T> tClass = (Class<T>) copier.getOrigClass();
		/** 先获取上一页最后一条的sequence值，如果有值的话，以此sequence值作为依据取后续的count条数据 */
		Object sequence = null;
		if (!StringUtils.equalsIgnoreCase(id, EMPTY_SYMBOL)) {
			Object o = emc.find(id, tClass);
			if (null != o) {
				sequence = PropertyUtils.getProperty(o, sequenceField);
			}
		}
		EntityManager em = emc.get(tClass);
		String str = "SELECT o.id FROM " + tClass.getCanonicalName() + " o";
		/** 预编译的SQL语句的参数序号，必须由1开始 */
		Integer index = 1;
		List<String> ps = new ArrayList<>();
		List<Object> vs = new ArrayList<>();
		/** 如果sequence值不为空，那么从sequence开始，如果为空则从头开始 */
		if (null != sequence) {
			ps.add("o." + sequenceField + " " + (StringUtils.equalsIgnoreCase(order, DESC) ? ">" : "<")
					+ (" ?" + (index)));
			vs.add(sequence);
			index++;
		}
		/** 以下组织各种条件子句，放到List<String> ps里，相应的值放到List<Object> vs里 */
		if (null != equals && (!equals.isEmpty())) {
			for (Entry<String, Object> en : equals.entrySet()) {
				ps.add("o." + en.getKey() + (" = ?" + index));
				vs.add(en.getValue());
				index++;
			}
		}
		if (null != notEquals && (!notEquals.isEmpty())) {
			for (Entry<String, Object> en : notEquals.entrySet()) {
				ps.add("(o." + en.getKey() + (" <> ?" + index) + " or o." + en.getKey() + " is null)");
				vs.add(en.getValue());
				index++;
			}
		}
		/** like有点特殊，多个like一般用or相联 */
		if (null != likes && (!likes.isEmpty())) {
			List<String> ors = new ArrayList<>();
			for (Entry<String, Object> en : likes.entrySet()) {
				for (String s : StringUtils.split(en.getValue().toString(), " ")) {
					ors.add("o." + en.getKey() + (" Like ?" + index));
					vs.add("%" + s + "%");
					index++;
				}
			}
			ps.add("(" + StringUtils.join(ors, " or ") + ")");
		}
		if (null != ins && (!ins.isEmpty())) {
			for (Entry<String, Collection<?>> en : ins.entrySet()) {
				ps.add("o." + en.getKey() + (" in ?" + index));
				vs.add(en.getValue());
				index++;
			}
		}
		if (null != notIns && (!notIns.isEmpty())) {
			for (Entry<String, Collection<?>> en : notIns.entrySet()) {
				ps.add("o." + en.getKey() + (" not in ?" + index));
				vs.add(en.getValue());
				index++;
			}
		}
		if (null != members && (!members.isEmpty())) {
			for (Entry<String, Object> en : members.entrySet()) {
				ps.add(("?" + index) + (" member of o." + en.getKey()));
				vs.add(en.getValue());
				index++;
			}
		}
		if (null != notMembers && (!notMembers.isEmpty())) {
			for (Entry<String, Object> en : notMembers.entrySet()) {
				ps.add(("?" + index) + (" not member of o." + en.getKey()));
				vs.add(en.getValue());
				index++;
			}
		}
		/** 使用指定的方式将条件子句组合起来 */
		if (!ps.isEmpty()) {
			str += " where " + StringUtils.join(ps, (andJoin ? " and " : " or "));
		}
		/** 排序 */
		str += " order by o." + sequenceField + " " + (StringUtils.equalsIgnoreCase(order, DESC) ? ASC : DESC);
		Query query = em.createQuery(str, tClass);
		/** 为查询设置所有的参数值 */
		for (int i = 0; i < vs.size(); i++) {
			query.setParameter(i + 1, vs.get(i));
		}
		/** 限制查询的条数，一般就取一页的条目数，最多为超过list_max */
		@SuppressWarnings("unchecked")
		List<String> ids = query.setMaxResults(Math.max(Math.min(count, list_max), list_min)).getResultList();
		List<W> wos = emc.fetch(ids, copier);
		if (!wos.isEmpty()) {
			/** 查询初始的编号 */
			Long rank = this.rank(emc, tClass, sequenceField,
					PropertyUtils.getProperty(wos.get(wos.size() - 1), sequenceField), equals, notEquals, likes, ins,
					notIns, members, notMembers, andJoin, order);
			/** 为输出的结果进行编号 */
			for (int i = wos.size() - 1; i >= 0; i--) {
				PropertyUtils.setProperty(wos.get(i), "rank", rank++);
			}
		}
		ActionResult<List<W>> result = new ActionResult<>();
		result.setData(wos);
		/** 设置查询结果的总条目数 */
		result.setCount(this.count(emc, tClass, equals, notEquals, likes, ins, notIns, members, notMembers, andJoin));
		return result;
	}

	public Integer adjustPage(Integer page) {
		return (page == null || page < 1) ? 1 : page;
	}

	public Integer adjustSize(Integer pageSize) {
		return (pageSize == null || pageSize < 1 || pageSize > EntityManagerContainer.MAX_PAGESIZE)
				? EntityManagerContainer.DEFAULT_PAGESIZE.intValue()
				: pageSize;
	}

	public <T extends JpaObject, W> ActionResult<List<W>> standardListNext(WrapCopier<T, W> copier, String id,
			Integer count, String sequenceField, String order, Predicate predicate) throws Exception {
		Class<T> tClass = (Class<T>) copier.getOrigClass();
		Class<W> wClass = (Class<W>) copier.getDestClass();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			// 先获取上一页最后一条的sequence值，如果有值的话，以此sequence值作为依据取后续的count条数据
			Object sequence = null;
			if (!StringUtils.equalsIgnoreCase(id, EMPTY_SYMBOL)) {
				T t = emc.fetch(id, tClass, ListTools.toList(sequenceField));
				if (null != t) {
					sequence = PropertyUtils.getProperty(t, sequenceField);
				}
			}
			EntityManager em = emc.get(tClass);
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Tuple> cq = cb.createQuery(Tuple.class);
			Root<T> root = cq.from(tClass);

			if (null == predicate) {
				predicate = cb.conjunction();
			}

			if (StringUtils.equalsIgnoreCase(order, DESC)) {
				cq.where(null == sequence ? predicate
						: cb.and(predicate, cb.lessThan(root.get(sequenceField), (Comparable) sequence)));
				cq.orderBy(cb.desc(root.get(sequenceField)));
			} else {
				cq.where(null == sequence ? predicate
						: cb.and(predicate, cb.greaterThan(root.get(sequenceField), (Comparable) sequence)));
				cq.orderBy(cb.asc(root.get(sequenceField)));
			}

			List<Selection<?>> selections = new ArrayList<>();

			List<String> fields = copier.getCopyFields();

			for (String field : fields) {
				selections.add(root.get(field));
			}

			List<Tuple> os = em.createQuery(cq.multiselect(selections))
					.setMaxResults(Math.max(Math.min(count, list_max), list_min)).getResultList();

			List<W> ws = new ArrayList<W>();

			for (Tuple tuple : os) {
				W w = wClass.newInstance();
				for (int i = 0; i < selections.size(); i++) {
					PropertyUtils.setProperty(w, fields.get(i), tuple.get(selections.get(i)));
				}
				ws.add(w);
			}
			ActionResult<List<W>> result = new ActionResult<>();
			result.setData(ws);
			// 设置查询结果的总条目数
			result.setCount(this.count(emc, tClass, predicate));
			return result;
		}
	}

	public <T extends JpaObject, W> ActionResult<List<W>> standardListPrev(WrapCopier<T, W> copier, String id,
			Integer count, String sequenceField, String order, Predicate predicate) throws Exception {
		Class<T> tClass = (Class<T>) copier.getOrigClass();
		Class<W> wClass = (Class<W>) copier.getDestClass();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			// 先获取上一页最后一条的sequence值，如果有值的话，以此sequence值作为依据取后续的count条数据
			Object sequence = null;
			if (!StringUtils.equalsIgnoreCase(id, EMPTY_SYMBOL)) {
				T t = emc.fetch(id, tClass, ListTools.toList(sequenceField));
				if (null != t) {
					sequence = PropertyUtils.getProperty(t, sequenceField);
				}
			}
			EntityManager em = emc.get(tClass);
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Tuple> cq = cb.createQuery(Tuple.class);
			Root<T> root = cq.from(tClass);

			if (null == predicate) {
				predicate = cb.conjunction();
			}

			if (StringUtils.equalsIgnoreCase(order, DESC)) {
				cq.where(null == sequence ? predicate
						: cb.and(predicate, cb.greaterThan(root.get(sequenceField), (Comparable) sequence)));
				cq.orderBy(cb.asc(root.get(sequenceField)));
			} else {
				cq.where(null == sequence ? predicate
						: cb.and(predicate, cb.lessThan(root.get(sequenceField), (Comparable) sequence)));
				cq.orderBy(cb.desc(root.get(sequenceField)));
			}

			List<Selection<?>> selections = new ArrayList<>();

			List<String> fields = copier.getCopyFields();

			for (String field : fields) {
				selections.add(root.get(field));
			}

			List<Tuple> os = em.createQuery(cq.multiselect(selections))
					.setMaxResults(Math.max(Math.min(count, list_max), list_min)).getResultList();

			List<W> ws = new ArrayList<W>();

			Tuple tuple = null;

			for (int i = os.size() - 1; i >= 0; i--) {
				tuple = os.get(i);
				W w = wClass.newInstance();
				for (int j = 0; j < selections.size(); j++) {
					PropertyUtils.setProperty(w, fields.get(j), tuple.get(selections.get(j)));
				}
				ws.add(w);
			}

			ActionResult<List<W>> result = new ActionResult<>();
			result.setData(ws);
			// 设置查询结果的总条目数
			result.setCount(this.count(emc, tClass, predicate));
			return result;
		}
	}

	public <T extends JpaObject> ActionResult<List<T>> standardListNext(Class<T> cls, String id, Integer count,
			String sequenceField, String order, Predicate predicate) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			// 先获取上一页最后一条的sequence值，如果有值的话，以此sequence值作为依据取后续的count条数据
			Object sequence = null;
			if (!StringUtils.equalsIgnoreCase(id, EMPTY_SYMBOL)) {
				T t = emc.fetch(id, cls, ListTools.toList(sequenceField));
				if (null != t) {
					sequence = PropertyUtils.getProperty(t, sequenceField);
				}
			}

			EntityManager em = emc.get(cls);
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<T> cq = cb.createQuery(cls);
			Root<T> root = cq.from(cls);

			if (null == predicate) {
				predicate = cb.conjunction();
			}

			if (StringUtils.equalsIgnoreCase(order, DESC)) {
				cq.where(null == sequence ? predicate
						: cb.and(predicate, cb.lessThan(root.get(sequenceField), (Comparable) sequence)));
				cq.orderBy(cb.desc(root.get(sequenceField)));
			} else {
				cq.where(null == sequence ? predicate
						: cb.and(predicate, cb.greaterThan(root.get(sequenceField), (Comparable) sequence)));
				cq.orderBy(cb.asc(root.get(sequenceField)));
			}

			List<T> os = em.createQuery(cq.select(root)).setMaxResults(Math.max(Math.min(count, list_max), list_min))
					.getResultList();

			ActionResult<List<T>> result = new ActionResult<>();
			result.setData(new ArrayList<T>(os));
			// 设置查询结果的总条目数
			result.setCount(this.count(emc, cls, predicate));
			return result;
		}
	}

	public <T extends JpaObject> ActionResult<List<T>> standardListPrev(Class<T> cls, String id, Integer count,
			String sequenceField, String order, Predicate predicate) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			// 先获取上一页最后一条的sequence值，如果有值的话，以此sequence值作为依据取后续的count条数据
			Object sequence = null;
			if (!StringUtils.equalsIgnoreCase(id, EMPTY_SYMBOL)) {
				T t = emc.fetch(id, cls, ListTools.toList(sequenceField));
				if (null != t) {
					sequence = PropertyUtils.getProperty(t, sequenceField);
				}
			}
			EntityManager em = emc.get(cls);
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<T> cq = cb.createQuery(cls);
			Root<T> root = cq.from(cls);

			if (null == predicate) {
				predicate = cb.conjunction();
			}

			if (StringUtils.equalsIgnoreCase(order, DESC)) {
				cq.where(null == sequence ? predicate
						: cb.and(predicate, cb.greaterThan(root.get(sequenceField), (Comparable) sequence)));
				cq.orderBy(cb.asc(root.get(sequenceField)));
			} else {
				cq.where(null == sequence ? predicate
						: cb.and(predicate, cb.lessThan(root.get(sequenceField), (Comparable) sequence)));
				cq.orderBy(cb.desc(root.get(sequenceField)));
			}

			List<T> os = em.createQuery(cq.select(root)).setMaxResults(Math.max(Math.min(count, list_max), list_min))
					.getResultList();

			List<T> wos = new ArrayList<>();

			for (int i = os.size() - 1; i >= 0; i--) {
				wos.add(os.get(i));
			}

			ActionResult<List<T>> result = new ActionResult<>();
			result.setData(wos);
			// 设置查询结果的总条目数
			result.setCount(this.count(emc, cls, predicate));
			return result;
		}
	}

	private <T extends JpaObject> Long count(EntityManagerContainer emc, Class<T> cls, Predicate predicate)
			throws Exception {
		EntityManager em = emc.get(cls);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<T> root = cq.from(cls);
		return em.createQuery(cq.select(cb.countDistinct(root)).where(predicate)).getSingleResult();
	}

	/**
	 * 将request参数值转为json
	 */
	public String request2Json(HttpServletRequest request) {
		Map<String, String> map = new HashMap<>();
		Enumeration paramNames = request.getParameterNames();
		while (paramNames.hasMoreElements()) {
			String paramName = (String) paramNames.nextElement();
			String[] pv = request.getParameterValues(paramName);
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < pv.length; i++) {
				if (pv[i].length() > 0) {
					if (i > 0) {
						sb.append(",");
					}
					sb.append(pv[i]);
				}
			}
			map.put(paramName, sb.toString());
		}
		return gson.toJson(map);
	}

}
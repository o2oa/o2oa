package com.x.organization.core.express.unit;

import java.util.Arrays;
import java.util.List;

import com.x.base.core.project.AbstractContext;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.organization.Unit;
import com.x.base.core.project.tools.ListTools;

public class UnitFactory {

	public UnitFactory(AbstractContext context) {
		this.context = context;
	}

	private AbstractContext context;

	/** 获取单个组织的distinguishedName */
	public String get(String value) throws Exception {
		List<String> os = ActionList.execute(context, Arrays.asList(value), false);
		if (ListTools.isEmpty(os)) {
			return "";
		} else {
			return os.get(0);
		}
	}

	public String get(String value, Boolean useNameFind) throws Exception {
		List<String> os = ActionList.execute(context, Arrays.asList(value), useNameFind);
		if (ListTools.isEmpty(os)) {
			return "";
		} else {
			return os.get(0);
		}
	}

	/** 校验用户是否在指定组织中注册身份. */
	public Boolean checkHasPerson(String person, String unit, Boolean recursive) throws Exception {
		return ActionHasPerson.execute(context, person, unit, recursive);
	}

	/** 根据身份和组织等级,获取组织的distinguishedName */
	public String getWithIdentityWithLevel(String identity, Integer level) throws Exception {
		return ActionGetWithIdentityWithLevel.execute(context, identity, level);
	}

	/** 根据身份和组织属性,获取组织的distinguishedName */
	public String getWithIdentityWithType(String identity, String type) throws Exception {
		return ActionGetWithIdentityWithType.execute(context, identity, type);
	}

	/** 批量获取组织的distinguishedName */
	public List<String> list(List<String> values) throws Exception {
		return ActionList.execute(context, values, false);
	}

	public List<String> list(List<String> values, Boolean useNameFind) throws Exception {
		return ActionList.execute(context, values, useNameFind);
	}

	/** 批量获取组织的distinguishedName */
	public List<String> list(String... values) throws Exception {
		return ActionList.execute(context, Arrays.asList(values), false);
	}

	/** 查询所有组织 */
	public List<String> listAll() throws Exception {
		return ActionListAll.execute(context);
	}

	/** 查询所有组织对象 */
	public List<Unit> listAllObject() throws Exception {
		List<? extends Unit> os = ActionListAllObject.execute(context);
		return (List<Unit>) os;
	}

	/** 获取组织对象 */
	public Unit getObject(String value) throws Exception {
		List<? extends Unit> os = ActionListObject.execute(context, Arrays.asList(value), false);
		if (ListTools.isEmpty(os)) {
			return null;
		} else {
			return os.get(0);
		}
	}

	public Unit getObject(String value, Boolean useNameFind) throws Exception {
		List<? extends Unit> os = ActionListObject.execute(context, Arrays.asList(value), useNameFind);
		if (ListTools.isEmpty(os)) {
			return null;
		} else {
			return os.get(0);
		}
	}

	/** 批量获取组织对象 */
	public List<Unit> listObject(List<String> values) throws Exception {
		List<? extends Unit> os = ActionListObject.execute(context, values, false);
		return (List<Unit>) os;
	}

	public List<Unit> listObject(List<String> values, Boolean useNameFind) throws Exception {
		List<? extends Unit> os = ActionListObject.execute(context, values, useNameFind);
		return (List<Unit>) os;
	}

	/** 批量获取组织对象 */
	public List<Unit> listObject(String... values) throws Exception {
		List<? extends Unit> os = ActionListObject.execute(context, Arrays.asList(values), false);
		return (List<Unit>) os;
	}

	/** 根据身份获取所在组织 */
	public String getWithIdentity(String value) throws Exception {
		List<String> os = ActionListWithIdentity.execute(context, Arrays.asList(value));
		if (ListTools.isEmpty(os)) {
			return "";
		} else {
			return os.get(0);
		}
	}

	/** 批量根据身份获取组织 */
	public List<String> listWithIdentity(List<String> values) throws Exception {
		return ActionListWithIdentity.execute(context, values);
	}

	/** 批量根据身份获取组织 */
	public List<String> listWithIdentity(String... values) throws Exception {
		return ActionListWithIdentity.execute(context, Arrays.asList(values));
	}

	/** 批量根据身份获取组织,并递归查找其上层组织 */
	public List<String> listWithIdentitySupNested(List<String> values) throws Exception {
		return ActionListWithIdentitySupNested.execute(context, values);
	}

	/** 批量根据身份获取组织,并递归查找其上层组织 */
	public List<String> listWithIdentitySupNested(String... values) throws Exception {
		return ActionListWithIdentitySupNested.execute(context, Arrays.asList(values));
	}

	/** 批量查询指定组织层级的组织 */
	public List<String> listWithLevel(List<Integer> values) throws Exception {
		return ActionListWithLevel.execute(context, values);
	}

	/** 批量查询指定组织层级的组织 */
	public List<String> listWithLevel(Integer... values) throws Exception {
		return ActionListWithLevel.execute(context, Arrays.asList(values));
	}

	/** 批量查询指定组织层级的组织对象 */
	public List<Unit> listWithLevelObject(List<Integer> values) throws Exception {
		List<? extends Unit> os = ActionListWithLevelObject.execute(context, values);
		return (List<Unit>) os;
	}

	/** 批量查询指定组织层级的组织对象 */
	public List<Unit> listWithLevelObject(Integer... values) throws Exception {
		List<? extends Unit> os = ActionListWithLevelObject.execute(context, Arrays.asList(values));
		return (List<Unit>) os;
	}

	/** 批量个人获取所在的组织 */
	public List<String> listWithPerson(List<String> values) throws Exception {
		return ActionListWithPerson.execute(context, values);
	}

	/** 批量个人获取所在的组织 */
	public List<String> listWithPerson(String... values) throws Exception {
		return ActionListWithPerson.execute(context, Arrays.asList(values));
	}

	/** 批量个人获取所在的组织 */
	public List<String> listWithPerson(EffectivePerson effectivePerson) throws Exception {
		return ActionListWithPerson.execute(context, ListTools.toList(effectivePerson.getDistinguishedName()));
	}

	/** 批量个人获取所在的组织,并递归其上级组织 */
	public List<String> listWithPersonSupNested(EffectivePerson effectivePerson) throws Exception {
		return ActionListWithPersonSupNested.execute(context, ListTools.toList(effectivePerson.getDistinguishedName()));
	}

	/** 批量个人获取所在的组织,并递归其上级组织 */
	public List<String> listWithPersonSupNested(List<String> values) throws Exception {
		return ActionListWithPersonSupNested.execute(context, values);
	}

	/** 批量个人获取所在的组织,并递归其上级组织 */
	public List<String> listWithPersonSupNested(String... values) throws Exception {
		return ActionListWithPersonSupNested.execute(context, Arrays.asList(values));
	}

	/** 查找拥有组织属性的组织 */
	public List<String> listWithUnitAttribute(String name, String attribute) throws Exception {
		return ActionListWithUnitAttribute.execute(context, name, attribute);
	}

	/** 查找拥有组织职务的组织 */
	public List<String> listWithUnitDuty(String name, String attribute) throws Exception {
		return ActionListWithUnitDuty.execute(context, name, attribute);
	}

	/** 根据组织获取组织的直接下级组织 */
	public List<String> listWithUnitSubDirect(List<String> values) throws Exception {
		return ActionListWithUnitSubDirect.execute(context, values);
	}

	/** 根据组织获取组织的直接下级组织 */
	public List<String> listWithUnitSubDirect(String... values) throws Exception {
		return ActionListWithUnitSubDirect.execute(context, Arrays.asList(values));
	}

	/** 根据组织获取组织的嵌套下级组织 */
	public List<String> listWithUnitSubNested(List<String> values) throws Exception {
		return ActionListWithUnitSubNested.execute(context, values);
	}

	/** 根据组织获取组织的嵌套下级组织 */
	public List<String> listWithUnitSubNested(String... values) throws Exception {
		return ActionListWithUnitSubNested.execute(context, Arrays.asList(values));
	}

	/** 根据组织获取组织的直接上级组织 */
	public List<String> listWithUnitSupDirect(List<String> values) throws Exception {
		return ActionListWithUnitSupDirect.execute(context, values);
	}

	/** 根据组织获取组织的直接上级组织 */
	public List<String> listWithUnitSupDirect(String... values) throws Exception {
		return ActionListWithUnitSupDirect.execute(context, Arrays.asList(values));
	}

	/** 根据组织获取组织的嵌套上级组织 */
	public List<String> listWithUnitSupNested(List<String> values) throws Exception {
		return ActionListWithUnitSupNested.execute(context, values);
	}

	/** 根据组织获取组织的嵌套上级组织 */
	public List<String> listWithUnitSupNested(String... values) throws Exception {
		return ActionListWithUnitSupNested.execute(context, Arrays.asList(values));
	}

	/** 根据组织获取排序号 */
	public Integer getOrderNumber(String value, Integer defaultValue) throws Exception {
		List<? extends Unit> os = ActionListObject.execute(context, Arrays.asList(value), false);
		if (os.isEmpty()) {
			return defaultValue;
		} else {
			return (os.get(0).getOrderNumber() == null) ? defaultValue : os.get(0).getOrderNumber();
		}
	}

	/** 根据组织获取层级排序号 */
	public String getLevelOrderNumber(String value, String defaultValue) throws Exception {
		List<? extends Unit> os = ActionListObject.execute(context, Arrays.asList(value), false);
		if (os.isEmpty()) {
			return defaultValue;
		} else {
			return (os.get(0).getLevelOrderNumber() == null) ? defaultValue : os.get(0).getLevelOrderNumber();
		}
	}

	/** 根据组织获取层级排序号 */
	public String getLevelName(String value, String defaultValue) throws Exception {
		List<? extends Unit> os = ActionListObject.execute(context, Arrays.asList(value), false);
		if (os.isEmpty()) {
			return defaultValue;
		} else {
			return (os.get(0).getLevelName() == null) ? defaultValue : os.get(0).getLevelName();
		}
	}
}

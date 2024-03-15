package com.x.organization.core.express.identity;

import java.util.Arrays;
import java.util.List;

import com.x.base.core.project.AbstractContext;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.organization.Identity;
import com.x.base.core.project.organization.WoIdentity;
import com.x.base.core.project.tools.ListTools;

public class IdentityFactory {

	public IdentityFactory(AbstractContext context) {
		this.context = context;
	}

	private AbstractContext context;

	/** 获取单个身份的distinguishedName */
	public String get(String value) throws Exception {
		List<String> os = ActionList.execute(context, Arrays.asList(value));
		if (ListTools.isEmpty(os)) {
			return "";
		} else {
			return os.get(0);
		}
	}

	/** 批量获取身份的distinguishedName */
	public List<String> list(List<String> values) throws Exception {
		return ActionList.execute(context, values);
	}

	/** 批量获取群组的distinguishedName */
	public List<String> list(String... values) throws Exception {
		return ActionList.execute(context, Arrays.asList(values));
	}

	/** 获取身份对象 */
	public Identity getObject(String value) throws Exception {
		List<? extends Identity> os = ActionListObject.execute(context, Arrays.asList(value), false);
		if (ListTools.isEmpty(os)) {
			return null;
		} else {
			return os.get(0);
		}
	}

	/** 批量获取身份对象 */
	public List<Identity> listObject(List<String> values) throws Exception {
		List<? extends Identity> os = ActionListObject.execute(context, values, false);
		return (List<Identity>) os;
	}

	/** 批量获取身份对象 */
	public List<WoIdentity> listWoObject(List<String> values) throws Exception {
		List<? extends Identity> os = ActionListObject.execute(context, values, true);
		return (List<WoIdentity>) os;
	}

	/** 批量获取身份对象 */
	public List<Identity> listObject(String... values) throws Exception {
		List<? extends Identity> os = ActionListObject.execute(context, Arrays.asList(values), false);
		return (List<Identity>) os;
	}

	/** 查询人员的身份 */
	public List<String> listWithPerson(List<String> values) throws Exception {
		return ActionListWithPerson.execute(context, values, false);
	}

	public List<String> listWithPerson(List<String> values, Boolean useNameFind) throws Exception {
		return ActionListWithPerson.execute(context, values, useNameFind);
	}

	/** 查询人员的身份 */
	public List<String> listWithPerson(String... values) throws Exception {
		return ActionListWithPerson.execute(context, Arrays.asList(values), false);
	}

	/** 查询人员的身份 */
	public List<String> listWithPerson(EffectivePerson effectivePerson) throws Exception {
		return ActionListWithPerson.execute(context, ListTools.toList(effectivePerson.getDistinguishedName()), false);
	}

	/** 查询群组的身份 */
	public List<String> listWithGroup(List<String> values) throws Exception {
		return ActionListWithGroup.execute(context, values);
	}

	/** 查询群组的身份 */
	public List<String> listWithGroup(String... values) throws Exception {
		return ActionListWithGroup.execute(context, Arrays.asList(values));
	}

	/** 根据组织获取直接成员身份 */
	public List<String> listWithUnitSubDirect(List<String> values) throws Exception {
		return ActionListWithUnitSubDirect.execute(context, values);
	}

	/** 根据组织获取直接成员身份 */
	public List<String> listWithUnitSubDirect(String... values) throws Exception {
		return ActionListWithUnitSubDirect.execute(context, Arrays.asList(values));
	}

	/** 根据组织获取嵌套成员身份 */
	public List<String> listWithUnitSubNested(List<String> values) throws Exception {
		return ActionListWithUnitSubNested.execute(context, values);
	}

	/** 根据组织获取嵌套成员身份 */
	public List<String> listWithUnitSubNested(String... values) throws Exception {
		return ActionListWithUnitSubNested.execute(context, Arrays.asList(values));
	}

	/** 根据个人和组织获取身份 */
	public String getWithPersonUnit(String person, String unit) throws Exception {
		return ActionGetWithPersonWithUnit.execute(context, person, unit);
	}

	/** 根据个人和组织获取身份对象 */
	public Identity getWithPersonUnitObject(String person, String unit) throws Exception {
		return ActionGetWithPersonWithUnitObject.execute(context, person, unit);
	}

	/** 根据个人获取主身份 */
	public String getMajorWithPerson(String person) throws Exception {
		List<String> os = ActionListMajorWithPerson.execute(context, ListTools.toList(person));
		if (ListTools.isEmpty(os)) {
			return "";
		} else {
			return os.get(0);
		}
	}

	/** 根据个人获取主身份 */
	public Identity getMajorWithPersonObject(String person) throws Exception {
		List<? extends Identity> os = ActionListMajorWithPersonObject.execute(context, ListTools.toList(person));
		if (ListTools.isEmpty(os)) {
			return null;
		} else {
			return os.get(0);
		}
	}

	/** 根据个人和组织获取身份 */
	public List<String> listWithPersonUnit(List<String> units, List<String> people) throws Exception {
		return ActionListWithPersonUnit.execute(context, units, people);
	}

	/** 根据个人和组织获取身份对象 */
	public List<Identity> listWithPersonUnitObject(List<String> units, List<String> people)
			throws Exception {
		List<? extends Identity> os = ActionListWithPersonUnitObject.execute(context, units, people);
		return (List<Identity>) os;
	}

	/** 根据个人获取排序号 */
	public Integer getOrderNumber(String value, Integer defaultValue) throws Exception {
		List<? extends Identity> os = ActionListObject.execute(context, Arrays.asList(value), false);
		if (os.isEmpty()) {
			return defaultValue;
		} else {
			return (os.get(0).getOrderNumber() == null) ? defaultValue : os.get(0).getOrderNumber();
		}
	}
}

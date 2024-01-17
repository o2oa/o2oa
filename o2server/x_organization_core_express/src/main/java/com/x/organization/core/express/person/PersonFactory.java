package com.x.organization.core.express.person;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.x.base.core.project.AbstractContext;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.organization.IdentityPersonPair;
import com.x.base.core.project.organization.Person;
import com.x.base.core.project.organization.PersonDetail;
import com.x.base.core.project.tools.ListTools;

public class PersonFactory {

	public PersonFactory(AbstractContext context) {
		this.context = context;
	}

	private AbstractContext context;

	/** 判断个人是否拥有指定角色中的一个或者多个 */
	public Boolean hasRole(EffectivePerson effectivePerson, List<String> values) throws Exception {
		return ActionHasRole.execute(context, effectivePerson.getDistinguishedName(), values);
	}

	/** 判断个人是否拥有指定角色中的一个或者多个 */
	public Boolean hasRole(EffectivePerson effectivePerson, String... values) throws Exception {
		return ActionHasRole.execute(context, effectivePerson.getDistinguishedName(), Arrays.asList(values));
	}

	/** 判断个人是否拥有指定角色中的一个或者多个 */
	public Boolean hasRole(String person, List<String> values) throws Exception {
		return ActionHasRole.execute(context, person, values);
	}

	/** 判断个人是否拥有指定角色中的一个或者多个 */
	public Boolean hasRole(String person, String... values) throws Exception {
		return ActionHasRole.execute(context, person, Arrays.asList(values));
	}

	/** 获取单个个人的distinguishedName */
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

	/** 获取个人的昵称 */
	public String getNickName(String value) throws Exception {
		return ActionGetNickName.execute(context, value);
	}

	/** 批量获取个人的distinguishedName */
	public List<String> list(List<String> values) throws Exception {
		return ActionList.execute(context, values, false);
	}

	public List<String> list(List<String> values, Boolean useNameFind) throws Exception {
		return ActionList.execute(context, values, useNameFind);
	}

	/** 批量获取个人的distinguishedName */
	public List<String> list(String... values) throws Exception {
		return ActionList.execute(context, Arrays.asList(values), false);
	}

	/** 查询所有个人 */
	public List<String> listAll() throws Exception {
		return ActionListAll.execute(context);
	}

	/** 查询所有个人对象 */
	public List<Person> listAllObject() throws Exception {
		List<? extends Person> os = ActionListAllObject.execute(context);
		return (List<Person>) os;
	}

	/** 获取个人对象 */
	public Person getObject(String value) throws Exception {
		List<? extends Person> os = ActionListObject.execute(context, Arrays.asList(value), false);
		if (ListTools.isEmpty(os)) {
			return null;
		} else {
			return os.get(0);
		}
	}

	public Person getObject(String value, Boolean useNameFind) throws Exception {
		List<? extends Person> os = ActionListObject.execute(context, Arrays.asList(value), useNameFind);
		if (ListTools.isEmpty(os)) {
			return null;
		} else {
			return os.get(0);
		}
	}

	/** 获取指定日期后登录的个人 */
	public List<String> listLoginAfter(Date date) throws Exception {
		return ActionListLoginAfter.execute(context, date);
	}

	/** 获取指定日期后登录的个人对象 */
	public List<Person> listLoginAfterObject(Date date) throws Exception {
		List<? extends Person> os = ActionListLoginAfterObject.execute(context, date);
		return (List<Person>) os;
	}

	/** 获取指定数量最近登录的个人 */
	public List<String> listLoginRecent(Integer count) throws Exception {
		return ActionListLoginRecent.execute(context, count);
	}

	/** 获取指定数量最近登录的个人对象 */
	public List<Person> listLoginRecentObject(Integer count) throws Exception {
		List<? extends Person> os = ActionListLoginRecentObject.execute(context, count);
		return (List<Person>) os;
	}

	/** 批量获取个人对象 */
	public List<Person> listObject(List<String> values) throws Exception {
		List<? extends Person> os = ActionListObject.execute(context, values, false);
		return (List<Person>) os;
	}

	public List<Person> listObject(List<String> values, Boolean useNameFind) throws Exception {
		List<? extends Person> os = ActionListObject.execute(context, values, useNameFind);
		return (List<Person>) os;
	}

	/** 批量获取个人对象 */
	public List<Person> listObject(String... values) throws Exception {
		List<? extends Person> os = ActionListObject.execute(context, Arrays.asList(values), false);
		return (List<Person>) os;
	}

	/** 根据群组获取组织的递归下级成员个人 */
	public List<String> listWithGroup(List<String> values) throws Exception {
		return ActionListWithGroup.execute(context, values);
	}

	/** 根据群组获取组织的递归下级成员个人 */
	public List<String> listWithGroup(String... values) throws Exception {
		return ActionListWithGroup.execute(context, Arrays.asList(values));
	}

	/** 根据身份获取个人 */
	public String getWithIdentity(String value) throws Exception {
		List<String> os = ActionListWithIdentity.execute(context, Arrays.asList(value));
		if (ListTools.isEmpty(os)) {
			return "";
		} else {
			return os.get(0);
		}
	}

	/** 批量根据身份获取个人 */
	public List<String> listWithIdentity(List<String> values) throws Exception {
		return ActionListWithIdentity.execute(context, values);
	}

	/** 批量根据身份获取个人 */
	public List<String> listWithIdentity(String... values) throws Exception {
		return ActionListWithIdentity.execute(context, Arrays.asList(values));
	}

	/** 查找拥有个人属性的个人 */
	public List<String> listWithPersonAttribute(String name, String value) throws Exception {
		return ActionListWithPersonAttribute.execute(context, name, value);
	}

	/** 根据个人获取个人直接分管的个人 */
	public List<String> listWithPersonSubDirect(List<String> values) throws Exception {
		return ActionListWithPersonSubDirect.execute(context, values);
	}

	public List<String> listWithPersonSubDirect(String... values) throws Exception {
		return ActionListWithPersonSubDirect.execute(context, Arrays.asList(values));
	}

	/** 根据个人获取个人嵌套分管的个人 */
	public List<String> listWithPersonSubNested(List<String> values) throws Exception {
		return ActionListWithPersonSubNested.execute(context, values);
	}

	public List<String> listWithPersonSubNested(String... values) throws Exception {
		return ActionListWithPersonSubNested.execute(context, Arrays.asList(values));
	}

	/** 查找个人的直接汇报对象 */
	public String getWithPersonSupDirect(String value) throws Exception {
		List<String> os = ActionListWithPersonSupDirect.execute(context, Arrays.asList(value));
		if (ListTools.isEmpty(os)) {
			return "";
		} else {
			return os.get(0);
		}
	}

	/** 根据个人获取个人直接主管 */
	public List<String> listWithPersonSupDirect(List<String> values) throws Exception {
		return ActionListWithPersonSupDirect.execute(context, values);
	}

	public List<String> listWithPersonSupDirect(String... values) throws Exception {
		return ActionListWithPersonSupDirect.execute(context, Arrays.asList(values));
	}

	/** 根据个人获取个人嵌套主管 */
	public List<String> listWithPersonSupNested(List<String> values) throws Exception {
		return ActionListWithPersonSupNested.execute(context, values);
	}

	public List<String> listWithPersonSupNested(String... values) throws Exception {
		return ActionListWithPersonSupNested.execute(context, Arrays.asList(values));
	}

	/** 根据角色获取组织的递归下级成员个人 */
	public List<String> listWithRole(List<String> values) throws Exception {
		return ActionListWithRole.execute(context, values);
	}

	/** 根据角色获取组织的递归下级成员个人 */
	public List<String> listWithRole(String... values) throws Exception {
		return ActionListWithRole.execute(context, Arrays.asList(values));
	}

	/** 根据组织获取组织的直接下级成员对应的个人 */
	public List<String> listWithUnitSubDirect(List<String> values) throws Exception {
		return ActionListWithUnitSubDirect.execute(context, values);
	}

	/** 根据组织获取组织的直接下级成员对应的个人 */
	public List<String> listWithUnitSubDirect(String... values) throws Exception {
		return ActionListWithUnitSubDirect.execute(context, Arrays.asList(values));
	}

	/** 根据组织获取组织的递归下级成员对应的个人 */
	public List<String> listWithUnitSubNested(List<String> values) throws Exception {
		return ActionListWithUnitSubNested.execute(context, values);
	}

	/** 根据组织获取组织的递归下级成员对应的个人 */
	public List<String> listWithUnitSubNested(String... values) throws Exception {
		return ActionListWithUnitSubNested.execute(context, Arrays.asList(values));
	}

	// 根据身份获取个人,返回身份人员匹配对
	public IdentityPersonPair getPairIdentity(String value) throws Exception {
		List<IdentityPersonPair> os = ActionListPairIdentity.execute(context, Arrays.asList(value));
		if (ListTools.isEmpty(os)) {
			return null;
		} else {
			return os.get(0);
		}
	}

	// 批量根据身份获取个人,返回身份人员匹配对
	public List<IdentityPersonPair> listPairIdentity(List<String> values) throws Exception {
		return ActionListPairIdentity.execute(context, values);
	}

	// 批量根据身份获取个人,返回身份人员匹配对
	public List<IdentityPersonPair> listPairIdentity(String... values) throws Exception {
		return ActionListPairIdentity.execute(context, Arrays.asList(values));
	}

	/**
	 * 获取扩充的个人对象,返回个人身份,身份所在组织等信息
	 * 
	 * @param value
	 * @return
	 * @throws Exception
	 */
	public Person getExt(String value) throws Exception {
		return ActionExt.execute(context, value);
	}

	/**
	 * 获取个人身份,组织,组织职务,群组,角色,个人属性
	 * 
	 * @param value                 个人标识
	 * @param fectchIdentity        是否获取身份
	 * @param fectchUnit            是否获取组织
	 * @param fectchUnitDuty        是否获取组织职务
	 * @param fectchGroup           是否获取群组
	 * @param fectchRole            是否获取角色
	 * @param fectchPersonAttribute 是否获取个人属性
	 * @return
	 * @throws Exception
	 */
	public PersonDetail detail(String value, Boolean fectchIdentity, Boolean fectchUnit, Boolean fectchUnitDuty,
			Boolean fectchGroup, Boolean fectchRole, Boolean fectchPersonAttribute) throws Exception {
		return ActionDetail.execute(context, value, fectchIdentity, fectchUnit, fectchUnitDuty, fectchGroup, fectchRole,
				fectchPersonAttribute);
	}

}

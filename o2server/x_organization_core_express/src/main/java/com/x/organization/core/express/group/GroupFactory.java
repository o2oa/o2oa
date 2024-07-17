package com.x.organization.core.express.group;

import java.util.Arrays;
import java.util.List;

import com.x.base.core.project.AbstractContext;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.organization.Group;
import com.x.base.core.project.tools.ListTools;

public class GroupFactory {

	public GroupFactory(AbstractContext context) {
		this.context = context;
	}

	private AbstractContext context;

	/** 判断群组是否拥有指定角色中的一个或者多个 */
	public Boolean hasRole(String group, List<String> values) throws Exception {
		return ActionHasRole.execute(context, group, values);
	}

	/** 判断群组是否拥有指定角色中的一个或者多个 */
	public Boolean hasRole(String group, String... values) throws Exception {
		return ActionHasRole.execute(context, group, Arrays.asList(values));
	}

	/** 获取单个群组的distinguishedName */
	public String get(String value) throws Exception {
		List<String> os = ActionList.execute(context, Arrays.asList(value));
		if (ListTools.isEmpty(os)) {
			return "";
		} else {
			return os.get(0);
		}
	}

	/** 批量获取群组的distinguishedName */
	public List<String> list(List<String> values) throws Exception {
		return ActionList.execute(context, values);
	}

	/** 批量获取群组的distinguishedName */
	public List<String> list(String... values) throws Exception {
		return ActionList.execute(context, Arrays.asList(values));
	}

	/** 获取群组对象 */
	public Group getObject(String value) throws Exception {
		List<? extends Group> os = ActionListObject.execute(context, Arrays.asList(value));
		if (ListTools.isEmpty(os)) {
			return null;
		} else {
			return os.get(0);
		}
	}

	/** 批量获取群组对象 */
	public List<Group> listObject(List<String> values) throws Exception {
		List<? extends Group> os = ActionListObject.execute(context, values);
		return (List<Group>) os;
	}

	/** 批量获取群组对象 */
	public List<Group> listObject(String... values) throws Exception {
		List<? extends Group> os = ActionListObject.execute(context, Arrays.asList(values));
		return (List<Group>) os;
	}

	/** 根据群组获取群组的直接下级群组 */
	public List<String> listWithGroupSubDirect(List<String> values) throws Exception {
		return ActionListWithGroupSubDirect.execute(context, values);
	}

	/** 根据群组获取群组的直接下级群组 */
	public List<String> listWithGroupSubDirect(String... values) throws Exception {
		return ActionListWithGroupSubDirect.execute(context, Arrays.asList(values));
	}

	/** 根据群组获取群组的递归下级群组 */
	public List<String> listWithGroupSubNested(List<String> values) throws Exception {
		return ActionListWithGroupSubNested.execute(context, values);
	}

	/** 根据群组获取群组的递归下级群组 */
	public List<String> listWithGroupSubNested(String... values) throws Exception {
		return ActionListWithGroupSubNested.execute(context, Arrays.asList(values));
	}

	/** 根据群组获取群组的直接上级群组 */
	public List<String> listWithGroupSupDirect(List<String> values) throws Exception {
		return ActionListWithGroupSupDirect.execute(context, values);
	}

	/** 根据群组获取群组的直接上级群组 */
	public List<String> listWithGroupSupDirect(String... values) throws Exception {
		return ActionListWithGroupSupDirect.execute(context, Arrays.asList(values));
	}

	/** 根据群组获取群组的递归上级群组 */
	public List<String> listWithGroupSupNested(List<String> values) throws Exception {
		return ActionListWithGroupSupNested.execute(context, values);
	}

	/** 根据群组获取群组的递归上级群组 */
	public List<String> listWithGroupSupNested(String... values) throws Exception {
		return ActionListWithGroupSupNested.execute(context, Arrays.asList(values));
	}

	/** 查询人员所在的群组 */
	public List<String> listWithPerson(List<String> values) throws Exception {
		return listWithPersonReference(values, true, false, false);
	}

	/** 查询人员所在的群组 */
	public List<String> listWithPerson(String... values) throws Exception {
		return listWithPersonReference(Arrays.asList(values), true, true, false);
	}

	/** 查询人员所在的群组 */
	public List<String> listWithPerson(EffectivePerson effectivePerson) throws Exception {
		return listWithPersonReference(ListTools.toList(effectivePerson.getDistinguishedName()), true, false, false);
	}

	/**
	 * 查询人员及关联身份、组织所在的群组
	 *
	 * @param values             用户
	 * @param recursiveGroupFlag 是否递归查询上级群组
	 * @param referenceFlag      是否包含查找人员身份成员、人员归属组织成员的所属群组
	 * @param recursiveOrgFlag   是否递归人员归属组织的上级组织所属群组，前提referenceFlag为true
	 * @return
	 * @throws Exception
	 */
	public List<String> listWithPersonReference(List<String> values, boolean recursiveGroupFlag, boolean referenceFlag,
			boolean recursiveOrgFlag) throws Exception {
		return ActionListWithPerson.execute(context, values, recursiveGroupFlag, referenceFlag, recursiveOrgFlag);
	}

	/** 查询身份所在的群组 */
	public List<String> listWithIdentity(List<String> values) throws Exception {
		return listWithIdentityReference(values, true, true, true);
	}

	/** 查询身份所在的群组 */
	public List<String> listWithIdentity(String... values) throws Exception {
		return listWithIdentityReference(Arrays.asList(values), true, true, true);
	}

	/**
	 * 查询身份及关联组织所在的群组
	 *
	 * @param values             身份
	 * @param recursiveGroupFlag 是否递归查询上级群组
	 * @param referenceFlag      是否包含查找人员身份成员、人员归属组织成员的所属群组
	 * @param recursiveOrgFlag   是否递归人员归属组织的上级组织所属群组，前提referenceFlag为true
	 * @return
	 * @throws Exception
	 */
	public List<String> listWithIdentityReference(List<String> values, boolean recursiveGroupFlag,
			boolean referenceFlag, boolean recursiveOrgFlag) throws Exception {
		return ActionListWithIdentity.execute(context, values, recursiveGroupFlag, referenceFlag, recursiveOrgFlag);
	}

}

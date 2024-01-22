package com.x.organization.core.express.role;

import java.util.Arrays;
import java.util.List;

import com.x.base.core.project.AbstractContext;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.organization.Role;
import com.x.base.core.project.tools.ListTools;

public class RoleFactory {

	public RoleFactory(AbstractContext context) {
		this.context = context;
	}

	private AbstractContext context;

	/** 获取单个角色的distinguishedName */
	public String get(String value) throws Exception {
		List<String> os = ActionList.execute(context, Arrays.asList(value));
		if (ListTools.isEmpty(os)) {
			return "";
		} else {
			return os.get(0);
		}
	}

	/** 批量获取角色的distinguishedName */
	public List<String> list(List<String> values) throws Exception {
		return ActionList.execute(context, values);
	}

	/** 批量获取角色的distinguishedName */
	public List<String> list(String... values) throws Exception {
		return ActionList.execute(context, Arrays.asList(values));
	}

	/** 获取角色对象 */
	public Role getObject(String value) throws Exception {
		List<? extends Role> os = ActionListObject.execute(context, Arrays.asList(value));
		if (ListTools.isEmpty(os)) {
			return null;
		} else {
			return os.get(0);
		}
	}

	/** 批量获取角色对象 */
	public List<Role> listObject(List<String> values) throws Exception {
		List<? extends Role> os = ActionListObject.execute(context, values);
		return (List<Role>) os;
	}

	/** 批量获取角色对象 */
	public List<Role> listObject(String... values) throws Exception {
		List<? extends Role> os = ActionListObject.execute(context, Arrays.asList(values));
		return (List<Role>) os;
	}

	/** 查询人员所拥有的角色 */
	public List<String> listWithPerson(EffectivePerson effectivePerson) throws Exception {
		return ActionListWithPerson.execute(context, ListTools.toList(effectivePerson.getDistinguishedName()));
	}

	/** 查询人员所拥有的角色 */
	public List<String> listWithPerson(List<String> values) throws Exception {
		return ActionListWithPerson.execute(context, values);
	}

	/** 查询人员所拥有的角色 */
	public List<String> listWithPerson(String... values) throws Exception {
		return ActionListWithPerson.execute(context, Arrays.asList(values));
	}

	/** 查询人员所拥有的角色对象 */
	public List<Role> listWithPersonObject(EffectivePerson effectivePerson) throws Exception {
		List<? extends Role> os = ActionListWithPersonObject.execute(context,
				ListTools.toList(effectivePerson.getDistinguishedName()));
		return (List<Role>) os;
	}

	/** 查询人员所拥有的角色对象 */
	public List<Role> listWithPersonObject(List<String> values) throws Exception {
		List<? extends Role> os = ActionListWithPersonObject.execute(context, values);
		return (List<Role>) os;
	}

	/** 查询人员所拥有的角色对象 */
	public List<Role> listWithPersonObject(String... values) throws Exception {
		List<? extends Role> os = ActionListWithPersonObject.execute(context, Arrays.asList(values));
		return (List<Role>) os;
	}

}
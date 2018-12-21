package com.x.organization.core.express.unitduty;

import java.util.Arrays;
import java.util.List;

import com.x.base.core.project.AbstractContext;

public class UnitDutyFactory {

	public UnitDutyFactory(AbstractContext context) {
		this.context = context;
	}

	private AbstractContext context;

	/** 获取组织属性值 */
	public List<String> listIdentityWithUnitWithName(String unit, String name) throws Exception {
		return ActionListIdenityWithUnitWithName.execute(context, unit, name);
	}

	/** 获取组织的组织职务名称 */
	public List<String> listNameWithUnit(String value) throws Exception {
		return ActionListNameWithUnit.execute(context, Arrays.asList(value));
	}

	/** 获取身份的组织职务名称 */
	public List<String> listNameWithIdentity(String value) throws Exception {
		return ActionListNameWithIdentity.execute(context, Arrays.asList(value));
	}

}
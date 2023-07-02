package com.x.organization.core.express.unitattribute;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.x.base.core.project.AbstractContext;

public class UnitAttributeFactory {

	public UnitAttributeFactory(AbstractContext context) {
		this.context = context;
	}

	private AbstractContext context;

	/** 添加组织属性值 */
	public boolean appendWithUnitWithName(String unit, String name, Collection<String> values) throws Exception {
		return ActionAppendWithUnitWithName.execute(context, unit, name, values);
	}

	/** 获取组织属性值 */
	public List<String> listAttributeWithUnitWithName(String unit, String name) throws Exception {
		return ActionListAttributeWithUnitWithName.execute(context, unit, name);
	}

	/** 获取组织属性名称 */
	public List<String> listNameWithUnit(String value) throws Exception {
		return ActionListNameWithUnit.execute(context, Arrays.asList(value));
	}

	/** 设置组织属性值 */
	public boolean setWithUnitWithName(String unit, String name, Collection<String> values) throws Exception {
		return ActionSetWithUnitWithName.execute(context, unit, name, values);
	}

}
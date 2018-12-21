package com.x.organization.core.express.unitattribute;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.AbstractContext;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.jaxrs.WrapBoolean;

class ActionAppendWithUnitWithName extends BaseAction {

	public static boolean execute(AbstractContext context, String unit, String name, Collection<String> attributeList)
			throws Exception {
		if (StringUtils.isEmpty(unit) || StringUtils.isEmpty(name)) {
			return false;
		}
		Wi wi = new Wi();
		wi.setUnit(unit);
		wi.setName(name);
		wi.getAttributeList().addAll(attributeList);
		Wo wo = context.applications().postQuery(applicationClass, "unitattribute/append/unit/name", wi)
				.getData(Wo.class);
		return wo.getValue();
	}

	public static class Wi extends GsonPropertyObject {

		@FieldDescribe("属性值")
		List<String> attributeList = new ArrayList<>();

		@FieldDescribe("属性名称")
		private String name;

		@FieldDescribe("组织")
		private String unit;

		public List<String> getAttributeList() {
			return attributeList;
		}

		public void setAttributeList(List<String> attributeList) {
			this.attributeList = attributeList;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getUnit() {
			return unit;
		}

		public void setUnit(String unit) {
			this.unit = unit;
		}

	}

	public static class Wo extends WrapBoolean {
	}
}

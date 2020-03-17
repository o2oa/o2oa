package com.x.teamwork.assemble.control.jaxrs.extfield;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.teamwork.core.entity.tools.FieldInfo;
import com.x.teamwork.core.entity.tools.ProjectExtField;

public class ActionListAllExtFields extends BaseAction {

	protected ActionResult<Wo> execute(HttpServletRequest request, EffectivePerson effectivePerson ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = new Wo();
		result.setData(wo);	
		return result;
	}

	public static class Wo {
 
		private List<FieldInfo> fieldInfos = ProjectExtField.listAllExtField();
		
		public List<FieldInfo> getFieldInfos() {
			return fieldInfos;
		}

		public void setFieldInfos(List<FieldInfo> fieldInfos) {
			this.fieldInfos = fieldInfos;
		}
	}
}
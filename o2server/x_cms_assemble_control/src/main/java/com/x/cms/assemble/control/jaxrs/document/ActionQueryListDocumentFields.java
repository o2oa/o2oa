package com.x.cms.assemble.control.jaxrs.document;

import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.cms.core.entity.Document;

public class ActionQueryListDocumentFields extends BaseAction {

	protected ActionResult<Wo> execute(HttpServletRequest request ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = new Wo();
		wo.setFieldNames( Arrays.asList( Document.documentFieldNames ) );
		result.setData( wo );
		return result;
	}

	public static class Wi {
		
		@FieldDescribe( "需要查询是否已阅读过的文档ID列表." )
		private List<String> ids = null;

		public List<String> getIds() {
			return ids;
		}

		public void setIds(List<String> ids) {
			this.ids = ids;
		}
	}

	public static class Wo extends GsonPropertyObject {

		@FieldDescribe( "文档对象可供列表使用的列名." )
		private List<String> fieldNames = null;

		public List<String> getFieldNames() {
			return fieldNames;
		}

		public void setFieldNames(List<String> fieldNames) {
			this.fieldNames = fieldNames;
		}		
	}
}
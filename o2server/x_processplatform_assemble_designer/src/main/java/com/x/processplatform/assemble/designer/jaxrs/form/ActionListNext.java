package com.x.processplatform.assemble.designer.jaxrs.form;

import java.util.List;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.core.entity.element.Form;

class ActionListNext extends BaseAction {
	ActionResult<List<Wo>> execute(String id, Integer count) throws Exception {
		return this.standardListNext(Wo.copier, id, count, JpaObject.sequence_FIELDNAME, null, null, null, null, null,
				null, null, null, true, DESC);
	}

	public static class Wo extends Form {

		private static final long serialVersionUID = -7495725325510376323L;

		public static final WrapCopier<Form, Wo> copier = WrapCopierFactory.wo(Form.class, Wo.class,
				JpaObject.singularAttributeField(Form.class, true, true),
				ListTools.toList(JpaObject.FieldsInvisible, Form.data_FIELDNAME, Form.mobileData_FIELDNAME));

		@FieldDescribe("排序号")
		private Long rank;

		public Long getRank() {
			return rank;
		}

		public void setRank(Long rank) {
			this.rank = rank;
		}

	}
}

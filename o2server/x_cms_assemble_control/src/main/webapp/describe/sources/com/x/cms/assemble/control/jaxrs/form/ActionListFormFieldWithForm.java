package com.x.cms.assemble.control.jaxrs.form;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.WrapOutMap;
import com.x.cms.assemble.control.Business;
import com.x.cms.core.entity.element.Form;
import com.x.cms.core.entity.element.FormField;

class ActionListFormFieldWithForm extends BaseAction {
	ActionResult<WrapOutMap> execute(String formId) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<WrapOutMap> result = new ActionResult<>();
			WrapOutMap wrap = new WrapOutMap();
			List<FormField> list = new ArrayList<>();
			Business business = new Business(emc);
			Form form = emc.find(formId, Form.class);
			if (null == form) {
				throw new ExceptionFormNotExist(formId);
			}
			List<String> ids = business.formFieldFactory().listWithForm(form.getId());
			list = new ArrayList<FormField>(emc.list(FormField.class, ids));
			Map<String, List<FormField>> map = list.stream()
					.sorted(Comparator.comparing(FormField::getDataType)
							.thenComparing(Comparator.comparing(FormField::getName)))
					.collect(Collectors.groupingBy(FormField::getDataType));
			wrap.putAll(map);
			result.setData(wrap);
			return result;
		}
	}
}

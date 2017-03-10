package com.x.processplatform.assemble.designer.jaxrs.form;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.exception.ExceptionWhen;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.WrapOutMap;
import com.x.processplatform.assemble.designer.Business;
import com.x.processplatform.core.entity.element.Form;
import com.x.processplatform.core.entity.element.FormField;

class ActionListFormFieldWithForm extends ActionBase {
	ActionResult<WrapOutMap> execute(String formId) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<WrapOutMap> result = new ActionResult<>();
			WrapOutMap wrap = new WrapOutMap();
			List<FormField> list = new ArrayList<>();
			Business business = new Business(emc);
			Form form = emc.find(formId, Form.class, ExceptionWhen.not_found);
			List<String> ids = business.formField().listWithForm(form.getId());
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

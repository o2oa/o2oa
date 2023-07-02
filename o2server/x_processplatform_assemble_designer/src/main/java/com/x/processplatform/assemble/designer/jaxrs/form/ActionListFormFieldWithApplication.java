package com.x.processplatform.assemble.designer.jaxrs.form;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.WrapOutMap;
import com.x.processplatform.assemble.designer.Business;
import com.x.processplatform.core.entity.element.Application;
import com.x.processplatform.core.entity.element.FormField;

class ActionListFormFieldWithApplication extends BaseAction {
	ActionResult<WrapOutMap> execute(String applicationId) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<WrapOutMap> result = new ActionResult<>();
			WrapOutMap wrap = new WrapOutMap();
			List<FormField> list = new ArrayList<>();
			Business business = new Business(emc);
			Application application = emc.find(applicationId, Application.class);
			if (null == application) {
				throw new ExceptionApplicationNotExist(applicationId);
			}
			List<String> ids = business.formField().listWithApplication(application.getId());
			for (FormField o : emc.list(FormField.class, ids)) {
				if (!contains(list, o)) {
					list.add(o);
				}
			}
			Map<String, List<FormField>> map = list.stream()
					.sorted(Comparator.comparing(FormField::getDataType)
							.thenComparing(Comparator.comparing(FormField::getName)))
					.collect(Collectors.groupingBy(FormField::getDataType));
			wrap.putAll(map);
			result.setData(wrap);
			return result;
		}
	}

	private boolean contains(List<FormField> list, FormField formField) {
		for (FormField o : list) {
			if (StringUtils.equals(o.getName(), formField.getName())) {
				if (StringUtils.equals(o.getDataType(), formField.getDataType())) {
					if (StringUtils.equals(o.getApplication(), formField.getApplication())) {
						return true;
					}
				}
			}
		}
		return false;
	}
}

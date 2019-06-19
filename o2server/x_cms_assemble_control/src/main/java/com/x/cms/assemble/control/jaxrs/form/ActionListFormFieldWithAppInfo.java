package com.x.cms.assemble.control.jaxrs.form;

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
import com.x.cms.assemble.control.Business;
import com.x.cms.core.entity.AppInfo;
import com.x.cms.core.entity.element.FormField;

class ActionListFormFieldWithAppInfo extends BaseAction {
	ActionResult<WrapOutMap> execute(String appId) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<WrapOutMap> result = new ActionResult<>();
			WrapOutMap wrap = new WrapOutMap();
			List<FormField> list = new ArrayList<>();
			Business business = new Business(emc);
			AppInfo appInfo = emc.find(appId, AppInfo.class);
			if (null == appInfo) {
				throw new ExceptionAppInfoNotExist(appId);
			}
			List<String> ids = business.formFieldFactory().listWithAppInfo(appInfo.getId());
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
					if (StringUtils.equals(o.getAppId(), formField.getAppId())) {
						return true;
					}
				}
			}
		}
		return false;
	}
}

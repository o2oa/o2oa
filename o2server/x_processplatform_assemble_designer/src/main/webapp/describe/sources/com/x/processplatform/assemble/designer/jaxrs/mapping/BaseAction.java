package com.x.processplatform.assemble.designer.jaxrs.mapping;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.exception.ExceptionEntityFieldEmpty;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.processplatform.assemble.designer.Business;
import com.x.processplatform.core.entity.element.Mapping;

abstract class BaseAction extends StandardJaxrsAction {

	protected void empty(Mapping mapping) throws Exception {
		if (StringUtils.isEmpty(mapping.getName())) {
			throw new ExceptionEntityFieldEmpty(Mapping.class, Mapping.name_FIELDNAME);
		}
		if (StringUtils.isEmpty(mapping.getData())) {
			throw new ExceptionEntityFieldEmpty(Mapping.class, Mapping.data_FIELDNAME);
		}
	}

	protected void duplicate(Business business, Mapping mapping) throws Exception {
		if (this.duplicateName(business, mapping)) {
			throw new ExceptionDuplicateName();
		}

		if (this.duplicateTable(business, mapping)) {
			throw new ExceptionDuplicateTable();
		}
	}

	private boolean duplicateName(Business business, Mapping mapping) throws Exception {
		Long count = business.entityManagerContainer().countEqualAndEqualAndNotEqual(Mapping.class,
				Mapping.application_FIELDNAME, mapping.getApplication(), Mapping.name_FIELDNAME, mapping.getName(),
				Mapping.id_FIELDNAME, mapping.getId());
		return count != 0;
	}

	private boolean duplicateTable(Business business, Mapping mapping) throws Exception {
		Long count = business.entityManagerContainer().countEqualAndEqualAndEqualAndNotEqual(Mapping.class,
				Mapping.application_FIELDNAME, mapping.getApplication(), Mapping.process_FIELDNAME,
				mapping.getProcess(), Mapping.tableName_FIELDNAME, mapping.getTableName(), Mapping.id_FIELDNAME,
				mapping.getId());
		return count != 0;
	}

}

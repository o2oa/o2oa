package com.x.processplatform.assemble.designer.jaxrs.projection;

import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.processplatform.assemble.designer.Business;
import com.x.processplatform.core.entity.element.Projection;

abstract class BaseAction extends StandardJaxrsAction {

	protected boolean duplicateWorkCompleted(Business business, Projection projection) throws Exception {
		Long count = business.entityManagerContainer().countEqualAndEqualAndNotEqual(Projection.class,
				Projection.process_FIELDNAME, projection.getProcess(), Projection.type_FIELDNAME,
				Projection.TYPE_WORKCOMPLETED, Projection.id_FIELDNAME, projection.getId());
		return count != 0;
	}

	protected boolean duplicateTaskCompleted(Business business, Projection projection) throws Exception {
		Long count = business.entityManagerContainer().countEqualAndEqualAndNotEqual(Projection.class,
				Projection.process_FIELDNAME, projection.getProcess(), Projection.type_FIELDNAME,
				Projection.TYPE_TASKCOMPLETED, Projection.id_FIELDNAME, projection.getId());
		return count != 0;
	}

	protected boolean duplicateReadCompleted(Business business, Projection projection) throws Exception {
		Long count = business.entityManagerContainer().countEqualAndEqualAndNotEqual(Projection.class,
				Projection.process_FIELDNAME, projection.getProcess(), Projection.type_FIELDNAME,
				Projection.TYPE_READCOMPLETED, Projection.id_FIELDNAME, projection.getId());
		return count != 0;
	}

	protected boolean duplicateRead(Business business, Projection projection) throws Exception {
		Long count = business.entityManagerContainer().countEqualAndEqualAndNotEqual(Projection.class,
				Projection.process_FIELDNAME, projection.getProcess(), Projection.type_FIELDNAME, Projection.TYPE_READ,
				Projection.id_FIELDNAME, projection.getId());
		return count != 0;
	}

	protected boolean duplicateReview(Business business, Projection projection) throws Exception {
		Long count = business.entityManagerContainer().countEqualAndEqualAndNotEqual(Projection.class,
				Projection.process_FIELDNAME, projection.getProcess(), Projection.type_FIELDNAME,
				Projection.TYPE_REVIEW, Projection.id_FIELDNAME, projection.getId());
		return count != 0;
	}

	protected boolean duplicateTable(Business business, Projection projection) throws Exception {
		Long count = business.entityManagerContainer().countEqualAndEqualAndEqualAndNotEqual(Projection.class,
				Projection.process_FIELDNAME, projection.getProcess(), Projection.type_FIELDNAME, Projection.TYPE_TABLE,
				Projection.dynamicName_FIELDNAME, projection.getDynamicName(), Projection.id_FIELDNAME,
				projection.getId());
		return count != 0;
	}

}

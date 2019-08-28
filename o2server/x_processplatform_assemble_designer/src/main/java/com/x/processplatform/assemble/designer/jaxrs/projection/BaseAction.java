package com.x.processplatform.assemble.designer.jaxrs.projection;

import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.exception.ExceptionEntityFieldEmpty;
import com.x.base.core.project.exception.ExceptionUnknowValue;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.processplatform.assemble.designer.Business;
import com.x.processplatform.core.entity.element.Projection;

abstract class BaseAction extends StandardJaxrsAction {

	protected void empty(Projection projection) throws Exception {

		if (StringUtils.isEmpty(projection.getName())) {
			throw new ExceptionEntityFieldEmpty(Projection.class, Projection.name_FIELDNAME);
		}

		if (StringUtils.isEmpty(projection.getType())) {
			throw new ExceptionEntityFieldEmpty(Projection.class, Projection.type_FIELDNAME);
		}

		if (StringUtils.isEmpty(projection.getData())) {
			throw new ExceptionEntityFieldEmpty(Projection.class, Projection.data_FIELDNAME);
		}
	}

	protected void duplicate(Business business, Projection projection) throws Exception {
		if (this.duplicateName(business, projection)) {
			throw new ExceptionDuplicateName();
		}
		switch (Objects.toString(projection.getType())) {
		case Projection.TYPE_WORK:
			if (this.duplicateWork(business, projection)) {
				throw new ExceptionDuplicateWork();
			}
			break;
		case Projection.TYPE_WORKCOMPLETED:
			if (this.duplicateWorkCompleted(business, projection)) {
				throw new ExceptionDuplicateWorkCompleted();
			}
			break;
		case Projection.TYPE_TASK:
			if (this.duplicateTask(business, projection)) {
				throw new ExceptionDuplicateTask();
			}
			break;
		case Projection.TYPE_TASKCOMPLETED:
			if (this.duplicateTaskCompleted(business, projection)) {
				throw new ExceptionDuplicateTaskCompleted();
			}
			break;
		case Projection.TYPE_READ:
			if (this.duplicateRead(business, projection)) {
				throw new ExceptionDuplicateRead();
			}
			break;
		case Projection.TYPE_READCOMPLETED:
			if (this.duplicateReadCompleted(business, projection)) {
				throw new ExceptionDuplicateReadCompleted();
			}
			break;
		case Projection.TYPE_REVIEW:
			if (this.duplicateReview(business, projection)) {
				throw new ExceptionDuplicateReview();
			}
			break;
		default:
			throw new ExceptionUnknowValue(projection.getType());
		}
	}

	private boolean duplicateName(Business business, Projection projection) throws Exception {
		Long count = business.entityManagerContainer().countEqualAndEqualAndNotEqual(Projection.class,
				Projection.application_FIELDNAME, projection.getApplication(), Projection.name_FIELDNAME,
				projection.getName(), Projection.id_FIELDNAME, projection.getId());
		return count != 0;
	}

	private boolean duplicateWork(Business business, Projection projection) throws Exception {
		Long count = business.entityManagerContainer().countEqualAndEqualAndEqualAndNotEqual(Projection.class,
				Projection.application_FIELDNAME, projection.getApplication(), Projection.process_FIELDNAME,
				projection.getProcess(), Projection.type_FIELDNAME, Projection.TYPE_WORK, Projection.id_FIELDNAME,
				projection.getId());
		return count != 0;
	}

	private boolean duplicateWorkCompleted(Business business, Projection projection) throws Exception {
		Long count = business.entityManagerContainer().countEqualAndEqualAndEqualAndNotEqual(Projection.class,
				Projection.application_FIELDNAME, projection.getApplication(), Projection.process_FIELDNAME,
				projection.getProcess(), Projection.type_FIELDNAME, Projection.TYPE_WORKCOMPLETED,
				Projection.id_FIELDNAME, projection.getId());
		return count != 0;
	}

	private boolean duplicateTask(Business business, Projection projection) throws Exception {
		Long count = business.entityManagerContainer().countEqualAndEqualAndEqualAndNotEqual(Projection.class,
				Projection.application_FIELDNAME, projection.getApplication(), Projection.process_FIELDNAME,
				projection.getProcess(), Projection.type_FIELDNAME, Projection.TYPE_TASKCOMPLETED,
				Projection.id_FIELDNAME, projection.getId());
		return count != 0;
	}

	private boolean duplicateTaskCompleted(Business business, Projection projection) throws Exception {
		Long count = business.entityManagerContainer().countEqualAndEqualAndEqualAndNotEqual(Projection.class,
				Projection.application_FIELDNAME, projection.getApplication(), Projection.process_FIELDNAME,
				projection.getProcess(), Projection.type_FIELDNAME, Projection.TYPE_TASKCOMPLETED,
				Projection.id_FIELDNAME, projection.getId());
		return count != 0;
	}

	private boolean duplicateRead(Business business, Projection projection) throws Exception {
		Long count = business.entityManagerContainer().countEqualAndEqualAndEqualAndNotEqual(Projection.class,
				Projection.application_FIELDNAME, projection.getApplication(), Projection.process_FIELDNAME,
				projection.getProcess(), Projection.type_FIELDNAME, Projection.TYPE_READ, Projection.id_FIELDNAME,
				projection.getId());
		return count != 0;
	}

	private boolean duplicateReadCompleted(Business business, Projection projection) throws Exception {
		Long count = business.entityManagerContainer().countEqualAndEqualAndEqualAndNotEqual(Projection.class,
				Projection.application_FIELDNAME, projection.getApplication(), Projection.process_FIELDNAME,
				projection.getProcess(), Projection.type_FIELDNAME, Projection.TYPE_READCOMPLETED,
				Projection.id_FIELDNAME, projection.getId());
		return count != 0;
	}

	private boolean duplicateReview(Business business, Projection projection) throws Exception {
		Long count = business.entityManagerContainer().countEqualAndEqualAndEqualAndNotEqual(Projection.class,
				Projection.application_FIELDNAME, projection.getApplication(), Projection.process_FIELDNAME,
				projection.getProcess(), Projection.type_FIELDNAME, Projection.TYPE_REVIEW, Projection.id_FIELDNAME,
				projection.getId());
		return count != 0;
	}

}

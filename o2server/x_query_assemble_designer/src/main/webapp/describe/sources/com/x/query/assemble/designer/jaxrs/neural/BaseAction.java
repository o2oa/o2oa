package com.x.query.assemble.designer.jaxrs.neural;

import java.util.List;

import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.query.assemble.designer.Business;
import com.x.query.core.entity.neural.Entry;
import com.x.query.core.entity.neural.InValue;
import com.x.query.core.entity.neural.OutValue;
import com.x.query.core.entity.neural.Project;

abstract class BaseAction extends StandardJaxrsAction {

	private static Logger logger = LoggerFactory.getLogger(BaseAction.class);

	protected Long cleanOutValue(Business business, Project project) throws Exception {
		List<String> ids = business.entityManagerContainer().idsEqual(OutValue.class, OutValue.project_FIELDNAME,
				project.getId());
		Long count = 0L;
		for (List<String> os : ListTools.batch(ids, 2000)) {
			business.entityManagerContainer().beginTransaction(OutValue.class);
			count = count + business.entityManagerContainer().delete(OutValue.class, os);
			business.entityManagerContainer().commit();
		}
		return count;
	}

	protected Long cleanInValue(Business business, Project project) throws Exception {
		List<String> ids = business.entityManagerContainer().idsEqual(InValue.class, InValue.project_FIELDNAME,
				project.getId());
		Long count = 0L;
		for (List<String> os : ListTools.batch(ids, 2000)) {
			business.entityManagerContainer().beginTransaction(InValue.class);
			count = count + business.entityManagerContainer().delete(InValue.class, os);
			business.entityManagerContainer().commit();
		}
		return count;
	}

	protected Long cleanEntry(Business business, Project project) throws Exception {
		List<String> ids = business.entityManagerContainer().idsEqual(Entry.class, Entry.project_FIELDNAME,
				project.getId());
		Long count = 0L;
		for (List<String> os : ListTools.batch(ids, 2000)) {
			business.entityManagerContainer().beginTransaction(Entry.class);
			count = count + business.entityManagerContainer().delete(Entry.class, os);
			business.entityManagerContainer().commit();
		}
		return count;
	}

}
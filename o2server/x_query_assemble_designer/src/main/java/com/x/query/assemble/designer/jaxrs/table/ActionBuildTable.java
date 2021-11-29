package com.x.query.assemble.designer.jaxrs.table;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.dynamic.DynamicEntity;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.query.assemble.designer.Business;
import com.x.query.core.entity.Query;
import com.x.query.core.entity.schema.Table;

import java.io.File;
import java.util.List;

class ActionBuildTable extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionBuildTable.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String queryId) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Wo wo = new Wo();
			Business business = new Business(emc);
			if (!business.controllable(effectivePerson)) {
				throw new ExceptionAccessDenied(effectivePerson);
			}
			Query curQuery = emc.fetch(queryId, Query.class);
			if (null == curQuery) {
				throw new ExceptionEntityNotExist(queryId, Query.class);
			}
			File jar = new File(Config.dir_dynamic_jars(true), DynamicEntity.JAR_NAME + Business.DOT_JAR);
			if(jar.exists()){
				List<Query> queryList = emc.fetchAll(Query.class);
				for(Query query : queryList){
					business.buildAllTable(query.getId());
				}
				jar.delete();
				wo.setValue(true);
			}else {
				wo.setValue(business.buildAllTable(queryId));
			}
			logger.info("build query {} table complete!", queryId);
			result.setData(wo);

			return result;
		}
	}

	public static class Wo extends WrapBoolean {

	}

}

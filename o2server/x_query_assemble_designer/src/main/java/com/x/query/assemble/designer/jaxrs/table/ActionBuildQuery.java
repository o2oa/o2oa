package com.x.query.assemble.designer.jaxrs.table;

import java.io.File;
import java.nio.file.Files;
import java.util.List;

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

class ActionBuildQuery extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionBuildQuery.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String queryId) throws Exception {
		LOGGER.debug("execute:{}.", effectivePerson::getDistinguishedName);
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Wo wo = new Wo();
			Business business = new Business(emc);
			if (!business.controllable(effectivePerson)) {
				throw new ExceptionAccessDenied(effectivePerson);
			}
			if(!EMPTY_SYMBOL.equals(queryId)) {
				Query curQuery = emc.fetch(queryId, Query.class);
				if (null == curQuery) {
					throw new ExceptionEntityNotExist(queryId, Query.class);
				}
			}
			// 兼容老版本的jar,删除统一的一个jar
			File jar = new File(Config.dir_dynamic_jars(true), DynamicEntity.JAR_NAME + Business.DOT_JAR);
			if (jar.exists()) {
				List<Query> queryList = emc.fetchAll(Query.class);
				for (Query query : queryList) {
					business.buildQuery(query.getId());
				}
				Files.delete(jar.toPath());
				wo.setValue(true);
			} else {
				if(!EMPTY_SYMBOL.equals(queryId)) {
					wo.setValue(business.buildQuery(queryId));
				}else{
					List<Query> queryList = emc.fetchAll(Query.class);
					for (Query query : queryList) {
						business.buildQuery(query.getId());
					}
					wo.setValue(true);
				}
			}
			LOGGER.info("build query {} table complete!", queryId);
			result.setData(wo);

			return result;
		}
	}

	public static class Wo extends WrapBoolean {

		private static final long serialVersionUID = 4060403776149004018L;

	}

}

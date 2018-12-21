package com.x.strategydeploy.assemble.control.service;

import java.util.ArrayList;
import java.util.List;

//import com.x.base.core.bean.WrapCopier;
//import com.x.base.core.bean.WrapCopierFactory;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.strategydeploy.assemble.control.Business;
import com.x.strategydeploy.core.entity.StrategyDeploy;

public class StrategyDeployExcuteSave {
	private static  Logger logger = LoggerFactory.getLogger(StrategyDeployExcuteSave.class);

	public StrategyDeploy save(EntityManagerContainer emc, StrategyDeploy strategydeploy) throws Exception {
		Business business = new Business(emc);
		emc.beginTransaction(StrategyDeploy.class);
		boolean IsExist = false;
		String _id = strategydeploy.getId();
		logger.info("save _id:" + _id);

		// IsExistById
		IsExist = business.strategyDeployFactory().IsExistById(_id);
		if (IsExist) {
			// IsExist true
			StrategyDeploy origin_strategydeploy = business.strategyDeployFactory().getById(_id);
			List<String> excludes = new ArrayList<String>();
			excludes.add("id");
			excludes.add("strategydeployname");
			WrapCopier<StrategyDeploy, StrategyDeploy> beanCopyTools = WrapCopierFactory.wi(StrategyDeploy.class,
					StrategyDeploy.class, null, excludes);
			beanCopyTools.copy(origin_strategydeploy);
			logger.info("StrategyDeploy update !!!");
			emc.persist(origin_strategydeploy, CheckPersistType.all);
			emc.commit();
			return origin_strategydeploy;
		} else {
			// IsExist false
			logger.info("StrategyDeploy new !!!");
			emc.persist(strategydeploy, CheckPersistType.all);
			emc.commit();
			return strategydeploy;
		}
	}
}

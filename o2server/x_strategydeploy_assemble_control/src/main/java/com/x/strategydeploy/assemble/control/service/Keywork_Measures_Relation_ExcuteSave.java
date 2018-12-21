package com.x.strategydeploy.assemble.control.service;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.strategydeploy.core.entity.KeyworkInfo;
import com.x.strategydeploy.core.entity.Keywork_Measures_Relation;

public class Keywork_Measures_Relation_ExcuteSave {
	private static  Logger logger = LoggerFactory.getLogger(Keywork_Measures_Relation_ExcuteSave.class);

	public Keywork_Measures_Relation save(EntityManagerContainer emc, Keywork_Measures_Relation keywork_measures_relation) throws Exception {
		emc.beginTransaction(KeyworkInfo.class);
		emc.persist(keywork_measures_relation);
		emc.commit();
		return keywork_measures_relation;
	}
}

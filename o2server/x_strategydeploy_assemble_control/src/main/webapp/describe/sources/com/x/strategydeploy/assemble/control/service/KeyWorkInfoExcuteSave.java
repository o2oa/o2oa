package com.x.strategydeploy.assemble.control.service;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.strategydeploy.core.entity.KeyworkInfo;

public class KeyWorkInfoExcuteSave {
	private static  Logger logger = LoggerFactory.getLogger(KeyWorkInfoExcuteSave.class);

	public KeyworkInfo save(EntityManagerContainer emc, KeyworkInfo keyworkinfo) throws Exception {
		emc.beginTransaction(KeyworkInfo.class);
		emc.persist(keyworkinfo);
		emc.commit();
		return keyworkinfo;
	}
}

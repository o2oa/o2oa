package com.x.pan.assemble.control.jaxrs.attachment3;

import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

class ActionSaveByWopi extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionSaveByWopi.class);

	String execute(EffectivePerson effectivePerson, String id, byte[] bytes) throws Exception {
		logger.info("{}用户通过officeOnline保存文档:{}", effectivePerson.getDistinguishedName(), id);
		Map<String, Object> map = new HashMap<>(2);
		this.saveFile(id, bytes, null, null, effectivePerson, map);
		return gson.toJson(map);
	}

}

package com.x.organization.assemble.personal.jaxrs.regist;

import org.apache.commons.lang3.StringUtils;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.WrapOutBoolean;
import com.x.base.core.project.tools.StringTools;

class ActionCheckName extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionCheckName.class);

	ActionResult<WrapOutBoolean> execute(String name) throws Exception {
		ActionResult<WrapOutBoolean> result = new ActionResult<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			if (StringUtils.isEmpty(name) || (!StringTools.isSimply(name))) {
				throw new ExceptionInvalidName(name);
			}
			if (this.nameExisted(emc, name)) {
				throw new ExceptionNameExist(name);
			}
			WrapOutBoolean wrap = new WrapOutBoolean();
			wrap.setValue(true);
			result.setData(wrap);
			return result;
		}
	}

}

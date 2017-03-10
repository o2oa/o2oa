package com.x.organization.assemble.personal.jaxrs.regist;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.WrapOutBoolean;
import com.x.base.core.utils.StringTools;

class ActionCheckName extends ActionBase {

	private static Logger logger = LoggerFactory.getLogger(ActionCheckName.class);

	ActionResult<WrapOutBoolean> execute(String name) throws Exception {
		ActionResult<WrapOutBoolean> result = new ActionResult<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			if (StringUtils.isEmpty(name) || (!StringTools.isSimply(name))) {
				throw new InvalidNameException(name);
			}
			if (this.nameExisted(emc, name)) {
				throw new NameExistedException(name);
			}
			WrapOutBoolean wrap = new WrapOutBoolean();
			wrap.setValue(true);
			result.setData(wrap);
			return result;
		}
	}

}

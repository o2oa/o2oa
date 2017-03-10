package com.x.program.center.jaxrs.collect;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.WrapOutBoolean;
import com.x.base.core.utils.StringTools;

class ActionCode extends ActionBase {

	ActionResult<WrapOutBoolean> execute(EffectivePerson effectivePerson, String mobile) throws Exception {
		ActionResult<WrapOutBoolean> result = new ActionResult<>();
		WrapOutBoolean wrap = WrapOutBoolean.falseInstance();
		if (!this.connect()) {
			throw new UnableConnectException();
		}
		if (StringUtils.isEmpty(mobile)) {
			throw new MobileEmptyException();
		}
		if (!StringTools.isMobile(mobile)) {
			throw new InvalidMobileException(mobile);
		}
		wrap.setValue(this.code(mobile));
		result.setData(wrap);
		return result;
	}

}

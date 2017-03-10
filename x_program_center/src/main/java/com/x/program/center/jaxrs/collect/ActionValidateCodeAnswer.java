package com.x.program.center.jaxrs.collect;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.WrapOutBoolean;
import com.x.base.core.utils.StringTools;
import com.x.program.center.jaxrs.collect.wrapin.WrapInCollect;

class ActionValidateCodeAnswer extends ActionBase {

	ActionResult<WrapOutBoolean> execute(JsonElement jsonElement) throws Exception {
		ActionResult<WrapOutBoolean> result = new ActionResult<>();
		WrapInCollect wrapIn = this.convertToWrapIn(jsonElement, WrapInCollect.class);
		WrapOutBoolean wrap = new WrapOutBoolean();
		String mobile = wrapIn.getMobile();
		String codeAnswer = wrapIn.getCodeAnswer();
		if (StringUtils.isEmpty(mobile)) {
			throw new MobileEmptyException();
		}
		if (!StringTools.isMobile(mobile)) {
			throw new InvalidMobileException(mobile);
		}
		if (StringUtils.isEmpty(codeAnswer)) {
			throw new CodeAnswerEmptyException();
		}
		if (!this.connect()) {
			throw new UnableConnectException();
		}
		if (!this.validateCodeAnswer(mobile, codeAnswer)) {
			throw new InvalidCodeAnswerException();
		}
		wrap.setValue(true);
		result.setData(wrap);
		return result;
	}

}

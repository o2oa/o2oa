package com.x.program.center.jaxrs.collect;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.wx.pwd.CheckStrength;
import com.x.base.core.DefaultCharset;
import com.x.base.core.gson.XGsonBuilder;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.WrapOutBoolean;
import com.x.base.core.project.server.Collect;
import com.x.base.core.project.server.Config;
import com.x.base.core.utils.StringTools;
import com.x.program.center.jaxrs.collect.wrapin.WrapInCollect;

class ActionRegist extends ActionBase {

	ActionResult<WrapOutBoolean> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		ActionResult<WrapOutBoolean> result = new ActionResult<>();
		WrapInCollect wrapIn = this.convertToWrapIn(jsonElement, WrapInCollect.class);
		String name = wrapIn.getName();
		String password = wrapIn.getPassword();
		String mobile = wrapIn.getMobile();
		String codeAnswer = wrapIn.getCodeAnswer();
		if (!this.connect()) {
			throw new UnableConnectException();
		}
		if (this.exist(name)) {
			throw new NameExistException(name);
		}
		if (CheckStrength.checkPasswordStrength(password) < 4) {
			throw new InvalidPasswordException();
		}
		if (StringUtils.isEmpty(mobile)) {
			throw new MobileEmptyException();
		}
		if (!StringTools.isMobile(mobile)) {
			throw new InvalidMobileException(mobile);
		}
		if (StringUtils.isEmpty(codeAnswer)) {
			throw new CodeAnswerEmptyException();
		}
		WrapOutBoolean wrap = new WrapOutBoolean();
		wrap.setValue(this.regist(name, password, mobile, codeAnswer));
		if (BooleanUtils.isTrue(wrap.getValue())) {
			Collect collect = Config.collect();
			collect.setEnable(true);
			collect.setName(name);
			collect.setPassword(password);
			File file = new File(Config.base(), Config.PATH_CONFIG_COLLECT);
			FileUtils.write(file, XGsonBuilder.toJson(collect), DefaultCharset.name);
			Config.flushCollect();
			/* 直接提交人员 */
			// this.collectTransmit();
		}
		result.setData(wrap);
		return result;
	}
}

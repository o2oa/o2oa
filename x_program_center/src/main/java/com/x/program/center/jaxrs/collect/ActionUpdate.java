package com.x.program.center.jaxrs.collect;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.DefaultCharset;
import com.x.base.core.gson.XGsonBuilder;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.WrapOutBoolean;
import com.x.base.core.project.server.Collect;
import com.x.base.core.project.server.Config;
import com.x.program.center.jaxrs.collect.wrapin.WrapInCollect;

class ActionUpdate extends ActionBase {

	ActionResult<WrapOutBoolean> execute(EffectivePerson effectivePerson, WrapInCollect wrapIn) throws Exception {
		ActionResult<WrapOutBoolean> result = new ActionResult<>();
		Collect collect = Config.collect();
		if (BooleanUtils.isTrue(wrapIn.getEnable())) {
			if (!this.connect()) {
				throw new UnableConnectException();
			}
			if (StringUtils.isEmpty(wrapIn.getName())) {
				throw new NameEmptyException();
			}
			if (!this.validate(wrapIn.getName(), wrapIn.getPassword())) {
				throw new InvalidCredentialException();
			}
			collect.setEnable(true);
		} else {
			collect.setEnable(false);
		}
		collect.setName(wrapIn.getName());
		collect.setPassword(wrapIn.getPassword());
		File file = new File(Config.base(), "config/collect.json");
		FileUtils.write(file, XGsonBuilder.toJson(collect), DefaultCharset.name);
		Config.flushCollect();
		result.setData(WrapOutBoolean.trueInstance());
		return result;
	}

}

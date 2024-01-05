package com.x.program.center.jaxrs.module;

import java.io.File;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;

import com.x.base.core.project.config.Config;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapStringList;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.StringTools;
import com.x.program.center.Business;

import io.swagger.v3.oas.annotations.media.Schema;

class ActionDispatchResource extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionDispatchResource.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, boolean asNew, String fileName, String filePath,
			byte[] bytes, FormDataContentDisposition disposition) throws Exception {

		if (BooleanUtils.isNotTrue(Config.general().getDeployResourceEnable())) {
			throw new ExceptionDeployDisable();
		}
		LOGGER.info("{}操作部署资源:{}到web目录{}.", effectivePerson::getDistinguishedName,
				() -> fileName, () -> filePath);
		String file = fileName;
		ActionResult<Wo> result = new ActionResult<>();
		if (StringUtils.isEmpty(file)) {
			file = this.fileName(disposition);
		}
		boolean flag = (!StringTools.isFileName(file)) || (!file.toLowerCase().endsWith(".zip") && StringUtils.isEmpty(filePath))
				|| (bytes == null || bytes.length == 0);
		if (flag) {
			throw new ExceptionIllegalFile(file);
		}
		if (StringUtils.isNotEmpty(filePath)) {
			File webServerDir = Config.dir_servers_webServer();
			File tempDie = new File(webServerDir, filePath);
			if ((!tempDie.getCanonicalPath().startsWith(webServerDir.getCanonicalPath()))
					|| (filePath.indexOf("../") > -1)) {
				throw new ExceptionIllegalFile(file);
			}
		}

		Wo wo = new Wo();
		wo.setValueList(Business.dispatch(asNew, file, filePath, bytes));
		result.setData(wo);
		return result;
	}

	@Schema(name = "com.x.program.center.jaxrs.module.ActionDispatchResource$Wo")
	public static class Wo extends WrapStringList {

		private static final long serialVersionUID = -344514217885570765L;

	}
}

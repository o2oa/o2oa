package com.x.program.center.jaxrs.deploy;

import com.x.base.core.project.config.Config;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.StringTools;
import com.x.program.center.Business;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.File;
import java.util.List;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;

class ActionDeployWebRes extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionDeployWebRes.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, final Wi wi,
			byte[] bytes, FormDataContentDisposition disposition) throws Exception {

		if (BooleanUtils.isNotTrue(Config.general().getDeployResourceEnable())) {
			throw new ExceptionDeployDisable();
		}
		LOGGER.info("{}操作部署资源:{}到web目录{}.", effectivePerson.getDistinguishedName(), wi.getName(), wi.getFilePath());
		String file = wi.getName();
		ActionResult<Wo> result = new ActionResult<>();
		if (StringUtils.isEmpty(file)) {
			file = this.fileName(disposition);
		}
		boolean flag = (!StringTools.isFileName(file)) || (!file.toLowerCase().endsWith(".zip") && StringUtils.isEmpty(wi.getFilePath()))
				|| (bytes == null || bytes.length == 0);
		if (flag) {
			throw new ExceptionIllegalFile(file);
		}
		wi.setName(file);
		if (StringUtils.isNotEmpty(wi.getFilePath())) {
			File webServerDir = Config.dir_servers_webServer();
			File tempDie = new File(webServerDir, wi.getFilePath());
			if ((!tempDie.getCanonicalPath().startsWith(webServerDir.getCanonicalPath()))
					|| (wi.getFilePath().contains("../"))) {
				throw new ExceptionIllegalFile(file);
			}
		}

		Wo wo = new Wo();
		List<String> resList = Business.dispatch(wi.getAsNew(), file, wi.getFilePath(), bytes);
		String message = StringUtils.join(resList);
		if(message.contains("success")){
			wi.setType("webRes");
			wo.setId(saveLog(wi, effectivePerson));
		}else{
			throw new ExceptionDeployFail();
		}
		result.setData(wo);
		return result;
	}



	@Schema(name = "com.x.program.center.jaxrs.module.ActionDispatchResource$Wo")
	public static class Wo extends WoId {

		private static final long serialVersionUID = -344514217885570765L;

	}
}

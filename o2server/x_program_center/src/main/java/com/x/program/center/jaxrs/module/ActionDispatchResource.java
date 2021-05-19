package com.x.program.center.jaxrs.module;

import com.x.base.core.project.config.Config;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapStringList;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.program.center.Business;
import org.apache.commons.lang3.StringUtils;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;

import java.io.File;


class ActionDispatchResource extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionDispatchResource.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, boolean asNew, String fileName, String filePath, byte[] bytes,
			FormDataContentDisposition disposition) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		if (StringUtils.isEmpty(fileName)) {
			fileName = this.fileName(disposition);
		}
		if(!fileName.toLowerCase().endsWith(".zip") && StringUtils.isEmpty(filePath)){
			throw new Exception("非zip文件的filePath属性不能为空");
		}
		if(StringUtils.isNotEmpty(filePath)){
			File webServerDir = Config.dir_servers_webServer();
			File tempDie = new File(webServerDir, filePath);
			if(!tempDie.getCanonicalPath().startsWith(webServerDir.getCanonicalPath())){
				throw new Exception("非法附件存放路径!");
			}
			if(filePath.indexOf("../") > -1){
				throw new Exception("附件存放路径不能包含'../'!");
			}
		}

		if(bytes==null || bytes.length==0){
			throw new Exception("file must be not empty!");
		}

		Wo wo = new Wo();
		wo.setValueList(Business.dispatch(asNew, fileName, filePath, bytes));
		result.setData(wo);
		return result;
	}

	public static class Wo extends WrapStringList {

	}
}

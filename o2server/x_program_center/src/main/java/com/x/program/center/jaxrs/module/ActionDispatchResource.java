package com.x.program.center.jaxrs.module;

import com.x.base.core.project.Application;
import com.x.base.core.project.config.CenterServer;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.config.Nodes;
import com.x.base.core.project.connection.ActionResponse;
import com.x.base.core.project.connection.CipherConnectionAction;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapStringList;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.Crypto;
import com.x.program.center.Business;
import com.x.program.center.ThisApplication;
import org.apache.commons.lang3.StringUtils;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
			if(filePath.startsWith("o2_") || filePath.startsWith("x_")){
				throw new Exception("filePath can not start with 'o2_' or 'x_'!");
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

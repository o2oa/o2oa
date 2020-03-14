package com.x.base.core.project.jaxrs.sysresource;

import com.x.base.core.project.config.Config;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.JarTools;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

class ActionUploadResource extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionUploadResource.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, Boolean asNew,String fileName,String filePath,byte[] bytes,
			FormDataContentDisposition disposition) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		if (StringUtils.isEmpty(fileName)) {
			fileName = this.fileName(disposition);
		}
		if(!fileName.toLowerCase().endsWith(".zip")){
			throw new Exception("file must be zip file!");
		}
		if(bytes==null || bytes.length==0){
			throw new Exception("file must be not empty zip file!");
		}

		File tempFile = new File(Config.base(), "local/temp/upload");
		FileUtils.forceMkdirParent(tempFile);
		FileUtils.forceMkdir(tempFile);
		FileUtils.cleanDirectory(tempFile);

		File zipFile = new File(tempFile.getAbsolutePath(), fileName);
		FileUtils.writeByteArrayToFile(zipFile, bytes);
		File dist = new File(Config.base(), Config.DIR_SERVERS_WEBSERVER);
		if(StringUtils.isNotEmpty(filePath)){
			dist = new File(dist, filePath);
			FileUtils.forceMkdirParent(dist);
			FileUtils.forceMkdir(dist);
		}
		List<String> subs = new ArrayList<>();
		subs.add("x_");
		subs.add("o2_");
		logger.print("{}上传静态资源:{},资源大小:{},到:{}", effectivePerson.getDistinguishedName(), fileName, bytes.length, dist.getAbsolutePath());
		JarTools.unjar(zipFile, subs, dist, asNew);

		FileUtils.cleanDirectory(tempFile);

		Wo wo = new Wo();
		wo.setValue(true);
		result.setData(wo);
		return result;
	}

	public static class Wo extends WrapBoolean {

	}
}

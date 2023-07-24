package com.x.base.core.project.jaxrs.sysresource;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.config.Config;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.FileTools;

import io.swagger.v3.oas.annotations.media.Schema;

class ActionListResource extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionListResource.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String filePath) throws IOException, URISyntaxException {
		LOGGER.debug("execute:{}, filePath:{}.", effectivePerson::getDistinguishedName, () -> filePath);
		ActionResult<Wo> result = new ActionResult<>();
		File dist = new File(Config.base(), Config.DIR_SERVERS_WEBSERVER);
		if (StringUtils.isNotEmpty(filePath) && !filePath.equals(EMPTY_SYMBOL)) {
			dist = new File(dist, filePath);
			if (!dist.exists()) {
				throw new IllegalStateException("filePath not exist!");
			}
		}

		Map<String, List<FileTools.FileInfo>> fileMap = FileTools.getFiles(dist.getAbsolutePath(), null,
				Config.DIR_SERVERS_WEBSERVER);

		Wo wo = new Wo();
		wo.setFiles(fileMap.get("files"));
		wo.setFolders(fileMap.get("folders"));
		result.setData(wo);
		return result;
	}

	@Schema(name = "com.x.base.core.project.jaxrs.sysresource.ActionListResource.Wo")
	public static class Wo extends GsonPropertyObject {

		private static final long serialVersionUID = -5820494266964710342L;

		private List<FileTools.FileInfo> files;

		private List<FileTools.FileInfo> folders;

		public List<FileTools.FileInfo> getFiles() {
			return files;
		}

		public void setFiles(List<FileTools.FileInfo> files) {
			this.files = files;
		}

		public List<FileTools.FileInfo> getFolders() {
			return folders;
		}

		public void setFolders(List<FileTools.FileInfo> folders) {
			this.folders = folders;
		}
	}
}

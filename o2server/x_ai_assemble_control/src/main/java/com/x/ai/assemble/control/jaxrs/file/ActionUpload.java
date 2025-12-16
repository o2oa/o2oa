package com.x.ai.assemble.control.jaxrs.file;

import com.x.ai.assemble.control.Business;
import com.x.ai.assemble.control.ThisApplication;
import com.x.ai.assemble.control.bean.AiConfig;
import com.x.ai.assemble.control.util.HttpUtil;
import com.x.ai.core.entity.File;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.bean.NameValuePair;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.config.StorageMapping;
import com.x.base.core.project.connection.ActionResponse;
import com.x.base.core.project.connection.FilePart;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.ActionResult.Type;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;

class ActionUpload extends BaseAction {
	private static final Logger logger = LoggerFactory.getLogger(ActionUpload.class);
	ActionResult<Wo> execute(EffectivePerson effectivePerson, byte[] bytes, FormDataContentDisposition disposition) throws Exception {
		String fileName;
		File file;
		Wo wo = new Wo();
		ActionResult<Wo> result = new ActionResult<>();

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create();
				ByteArrayInputStream in = new ByteArrayInputStream(bytes)) {
			StorageMapping mapping = ThisApplication.context().storageMappings().random(File.class);
			if (null == mapping) {
				throw new ExceptionAllocateStorageMapping();
			}
			fileName = this.fileName(disposition);
			fileName = FilenameUtils.getName(fileName);
			this.verifyConstraint(fileName);
			file = new File(mapping.getName(), fileName, effectivePerson.getDistinguishedName());
			emc.check(file, CheckPersistType.all);
			String fileId = this.uploadToO2Ai(file, bytes);
			file.saveContent(mapping, in, fileName);
			file.setFileId(fileId);
			emc.beginTransaction(File.class);
			emc.persist(file);
			emc.commit();
			wo.setId(StringUtils.isBlank(fileId) ? file.getId() : fileId);
		}

		result.setData(wo);
		return result;
	}

	private String uploadToO2Ai(File f, byte[] bytes) throws Exception{
		AiConfig aiConfig = Business.getConfig();
		if (BooleanUtils.isTrue(aiConfig.getO2AiEnable())
				&& StringUtils.isNotBlank(aiConfig.getO2AiBaseUrl())
				&& StringUtils.isNotBlank(aiConfig.getO2AiToken())) {
			List<NameValuePair> heads = List.of(
					new NameValuePair("Authorization", "Bearer " + aiConfig.getO2AiToken()));
			String url = aiConfig.getO2AiBaseUrl() + "/infra-gateway-material/create";
			List<FilePart> filePartList = new ArrayList<>();
			FilePart filePart = new FilePart(f.getName(), bytes, Config.mimeTypes(f.getExtension()), "file");
			filePartList.add(filePart);
			ActionResponse resp = HttpUtil.postMultiPartBinary(url, heads, null, filePartList);
			logger.info("ai document {} upload resp: {}", f.getName(), XGsonBuilder.toJson(resp));
			if (Type.success.equals(resp.getType())) {
				List<WoFile> woFileList = resp.getDataAsList(WoFile.class);
				if(ListTools.isNotEmpty(woFileList)){
					return woFileList.get(0).getId();
				}
			}
		}
		return "";
	}

	public static class Wo extends WoId {

		public Wo() {
		}

		public Wo(String id) throws Exception{
			super(id);
		}
	}

	public static class WoFile{
		private String id;
		private String fileName;

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getFileName() {
			return fileName;
		}

		public void setFileName(String fileName) {
			this.fileName = fileName;
		}
	}
}

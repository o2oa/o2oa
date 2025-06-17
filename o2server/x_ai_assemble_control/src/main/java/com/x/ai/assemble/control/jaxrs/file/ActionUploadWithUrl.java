package com.x.ai.assemble.control.jaxrs.file;

import com.google.gson.JsonElement;
import com.x.ai.assemble.control.Business;
import com.x.ai.assemble.control.ThisApplication;
import com.x.ai.assemble.control.bean.AiConfig;
import com.x.ai.assemble.control.util.AliUtil;
import com.x.ai.core.entity.File;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.config.StorageMapping;
import com.x.base.core.project.connection.CipherConnectionAction;
import com.x.base.core.project.exception.ExceptionEntityFieldEmpty;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import java.io.ByteArrayInputStream;
import org.apache.commons.lang3.StringUtils;

public class ActionUploadWithUrl extends BaseAction {

	private static final Logger logger = LoggerFactory.getLogger(ActionUploadWithUrl.class);

	protected ActionResult<Wo> execute(EffectivePerson effectivePerson,
										JsonElement jsonElement) throws Exception {
		logger.debug("ActionFileUploadWithUrl receive:{}.", jsonElement.toString());
		ActionResult<Wo> result = new ActionResult<>();

		Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
		if(StringUtils.isEmpty(wi.getReferenceId())){
			throw new ExceptionEntityFieldEmpty(File.class, File.referenceId_FIELDNAME);
		}
		if(StringUtils.isEmpty(wi.getFileName())){
			throw new ExceptionEntityFieldEmpty(File.class, "fileName");
		}
		if(StringUtils.isEmpty(wi.getFileUrl())){
			throw new ExceptionEntityFieldEmpty(File.class, "fileUrl");
		}
		String fileName = wi.getFileName();
		this.verifyConstraint(fileName);
		StorageMapping mapping = ThisApplication.context().storageMappings().random( File.class );
		if (null == mapping) {
			throw new ExceptionAllocateStorageMapping();
		}
		byte[] bytes = CipherConnectionAction.getBinary(false, wi.getFileUrl());
		if(bytes==null || bytes.length==0){
			throw new ExceptionEntityFieldEmpty(File.class, "bytes");
		}
		Wo wo = new Wo();
		File file;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create();
			 ByteArrayInputStream in = new ByteArrayInputStream(bytes)) {
			file = emc.firstEqual(File.class, File.referenceId_FIELDNAME, wi.getReferenceId());
			if(file == null) {
				file = new File(mapping.getName(), fileName,
						effectivePerson.getDistinguishedName());
				emc.check(file, CheckPersistType.all);
				file.saveContent(mapping, in, fileName);
				emc.beginTransaction(File.class);
				emc.persist(file);
				emc.commit();
			}else{
				AiConfig config = Business.getConfig();
				if(File.STATUS_INDEXED.equals(file.getStatus())){
					if(AliUtil.deleteIndexDoc(config, file.getFileId(), file.getName())){
						AliUtil.deleteFile(config, file.getFileId(), file.getName());
						file.setFileId("");
						file.setStatus(File.STATUS_NO_INDEX);
						emc.beginTransaction(File.class);
						emc.commit();
					}
				}else if(StringUtils.isNotBlank(file.getFileId())){
					AliUtil.deleteFile(config, file.getFileId(), file.getName());
					file.setFileId("");
					file.setStatus(File.STATUS_NO_INDEX);
					emc.beginTransaction(File.class);
					emc.commit();
				}

			}
			wo.setId(file.getId());
		}

		result.setData(wo);

		return result;
	}

	public static class Wi extends GsonPropertyObject {

		private static final long serialVersionUID = -3707551996175419386L;

		@FieldDescribe("*文件名称,带扩展名的文件名.")
		private String fileName;

		@FieldDescribe("*附件来源url地址.")
		private String fileUrl;

		@FieldDescribe("*关联id.")
		private String referenceId;

		public String getFileName() {
			return fileName;
		}

		public void setFileName(String fileName) {
			this.fileName = fileName;
		}

		public String getFileUrl() {
			return fileUrl;
		}

		public void setFileUrl(String fileUrl) {
			this.fileUrl = fileUrl;
		}

		public String getReferenceId() {
			return referenceId;
		}

		public void setReferenceId(String referenceId) {
			this.referenceId = referenceId;
		}
	}

	public static class Wo extends WoId {

		public Wo() {
		}

		public Wo(String id) throws Exception{
			super(id);
		}
	}
}

package com.x.general.assemble.control.jaxrs.invoice;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.annotation.AuditLog;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.config.StorageMapping;
import com.x.base.core.project.connection.CipherConnectionAction;
import com.x.base.core.project.exception.ExceptionFieldEmpty;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.general.assemble.control.ThisApplication;
import com.x.general.core.entity.Invoice;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

public class ActionUploadWithUrl extends BaseAction {

	private static final Logger logger = LoggerFactory.getLogger(ActionUploadWithUrl.class);

	@AuditLog(operation = "上传附件")
	protected ActionResult<Wo> execute(EffectivePerson effectivePerson,
										JsonElement jsonElement) throws Exception {
		logger.debug("ActionUploadWithUrl receive:{}.", jsonElement.toString());
		ActionResult<Wo> result = new ActionResult<>();

		Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
		if(StringUtils.isEmpty(wi.getFileName())){
			throw new ExceptionFieldEmpty("fileName");
		}
		if(StringUtils.isEmpty(wi.getFileUrl())){
			throw new ExceptionFieldEmpty("fileUrl");
		}
		String fileName = wi.getFileName();
		String extension = StringUtils.lowerCase(FilenameUtils.getExtension(fileName));
		if (StringUtils.isEmpty(extension)) {
			throw new ExceptionEmptyExtension(fileName);
		}
		if (!Invoice.EXT_PDF.equals(extension)) {
			throw new ExceptionErrorExtension(fileName);
		}

		Invoice file;
		StorageMapping mapping = ThisApplication.context().storageMappings().random(Invoice.class);
		if (null == mapping) {
			throw new ExceptionAllocateStorageMapping();
		}
		byte[] bytes = CipherConnectionAction.getBinary(false, wi.getFileUrl());
		if(bytes==null || bytes.length==0){
			throw new ExceptionEmptyExtension("bytes");
		}

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			file = new Invoice(mapping.getName(), fileName, effectivePerson.getDistinguishedName(), extension);
			extractInvoice(file, bytes);
			if(exists(emc, file.getNumber())){
				throw new ExceptionInvoiceExists(file.getNumber());
			}
			emc.check(file, CheckPersistType.all);
			file.saveContent(mapping, bytes, fileName);
			emc.beginTransaction(Invoice.class);
			emc.persist(file);
			emc.commit();
		}
		Wo wo = Wo.copier.copy(file);
		result.setData(wo);

		return result;
	}

	public static class Wi extends GsonPropertyObject {

		private static final long serialVersionUID = -3707551996175419386L;

		@FieldDescribe("文件名称,带扩展名的文件名.")
		private String fileName;

		@FieldDescribe("*附件来源url地址.")
		private String fileUrl;

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
	}

	public static class Wo extends Invoice {
		private static final long serialVersionUID = 100904116457932549L;

		static WrapCopier<Invoice, Wo> copier = WrapCopierFactory.wo(Invoice.class, Wo.class, null,
				JpaObject.FieldsInvisibleIncludeProperites);
	}
}

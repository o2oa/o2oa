package com.x.cms.assemble.control.jaxrs.fileinfo;

import com.x.base.core.project.annotation.AuditLog;
import com.x.base.core.project.config.StorageMapping;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoFile;
import com.x.cms.assemble.control.ThisApplication;
import com.x.cms.core.entity.FileInfo;

import javax.servlet.http.HttpServletRequest;

public class ActionFileDownload extends BaseAction {

	@AuditLog(operation = "下载附件")
	protected ActionResult<Wo> execute( HttpServletRequest request, EffectivePerson effectivePerson, String id ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		FileInfo attachment = fileInfoServiceAdv.get(id);
		
		if ( null == attachment ) {
			throw new Exception("附件不存在。id:" + id ) ;
		}else {
			StorageMapping mapping = ThisApplication.context().storageMappings().get(FileInfo.class, attachment.getStorage());
			Wo wo = new Wo(attachment.readContent(mapping), 
					this.contentType(false, attachment.getName()), 
					this.contentDisposition(false, attachment.getName()));
			result.setData(wo);
		}
		return result;
	}

	public static class Wo extends WoFile {
		public Wo(byte[] bytes, String contentType, String contentDisposition) {
			super(bytes, contentType, contentDisposition);
		}
	}

}

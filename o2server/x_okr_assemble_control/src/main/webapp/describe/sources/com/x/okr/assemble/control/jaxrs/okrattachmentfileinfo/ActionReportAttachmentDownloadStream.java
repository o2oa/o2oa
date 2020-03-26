package com.x.okr.assemble.control.jaxrs.okrattachmentfileinfo;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.project.config.StorageMapping;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoFile;
import com.x.okr.assemble.control.ThisApplication;
import com.x.okr.entity.OkrAttachmentFileInfo;

public class ActionReportAttachmentDownloadStream extends BaseAction {
	
	protected ActionResult<Wo> execute( HttpServletRequest request, EffectivePerson effectivePerson, String id, Boolean stream ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		OkrAttachmentFileInfo attachment = okrAttachmentFileInfoService.get(id);
		
		if ( null == attachment ) {
			throw new Exception("附件不存在。id:" + id ) ;
		}else {
			StorageMapping mapping = ThisApplication.context().storageMappings().get(OkrAttachmentFileInfo.class, attachment.getStorage());
			Wo wo = new Wo(attachment.readContent(mapping), 
					this.contentType(stream, attachment.getName()), 
					this.contentDisposition(stream, attachment.getName()));
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

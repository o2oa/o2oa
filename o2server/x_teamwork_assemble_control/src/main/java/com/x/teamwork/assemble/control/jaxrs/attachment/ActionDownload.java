package com.x.teamwork.assemble.control.jaxrs.attachment;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.project.config.StorageMapping;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoFile;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.teamwork.assemble.control.ThisApplication;
import com.x.teamwork.core.entity.Attachment;

public class ActionDownload extends BaseAction {
	
	private static  Logger logger = LoggerFactory.getLogger( ActionDownload.class );
	
	protected ActionResult<Wo> execute( HttpServletRequest request, EffectivePerson effectivePerson, String id ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Attachment attachment = null;
		Boolean check = true;
		
		try {
			attachment = attachmentQueryService.get( id );
			if( attachment == null ){
				check = false;
				Exception exception = new ExceptionAttachmentNotExists( id );
				result.error( exception );
			}
		} catch ( Exception e ) {
			check = false;
			Exception exception = new ExceptionAttachmentQueryById( e, id );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}
		
		if( check ) {
			StorageMapping mapping = ThisApplication.context().storageMappings().get(Attachment.class, attachment.getStorage());
			Wo wo = new Wo(attachment.readContent(mapping), 
					this.contentType(false, attachment.getName()), 
					this.contentDisposition(false, attachment.getName()));
			result.setData(wo);
		}
		
		if (check) {
			try {
				dynamicPersistService.downloadAttachmentDynamic(attachment, effectivePerson);
			} catch (Exception e) {
				logger.error(e, effectivePerson, request, null);
			}
		}
		return result;
	}

	public static class Wo extends WoFile {
		public Wo(byte[] bytes, String contentType, String contentDisposition) {
			super(bytes, contentType, contentDisposition);
		}
	}

}

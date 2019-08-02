package com.x.teamwork.assemble.control.jaxrs.attachment;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.config.StorageMapping;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoFile;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.teamwork.assemble.control.ThisApplication;
import com.x.teamwork.core.entity.Attachment;
import com.x.teamwork.core.entity.Dynamic;

public class ActionDownload extends BaseAction {
	
	private static  Logger logger = LoggerFactory.getLogger( ActionDownload.class );
	
	protected ActionResult<Wo> execute( HttpServletRequest request, EffectivePerson effectivePerson, String id ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Attachment attachment = null;
		Boolean check = true;
		Wo wo = null;
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
			wo = new Wo(attachment.readContent(mapping), 
					this.contentType(false, attachment.getName()), 
					this.contentDisposition(false, attachment.getName()));
		}
		
		if (check) {
			try {
				Dynamic dynamic = dynamicPersistService.downloadAttachmentDynamic(attachment, effectivePerson);
				if( dynamic != null ) {
					List<WoDynamic> dynamics = new ArrayList<>();
					dynamics.add( WoDynamic.copier.copy( dynamic ) );
					if( wo != null ) {
						wo.setDynamics(dynamics);
					}
				}
			} catch (Exception e) {
				logger.error(e, effectivePerson, request, null);
			}
		}
		result.setData(wo);
		return result;
	}

	public static class Wo extends WoFile {
		
		@FieldDescribe("操作引起的动态内容")
		List<WoDynamic> dynamics = new ArrayList<>();

		public List<WoDynamic> getDynamics() {
			return dynamics;
		}

		public void setDynamics(List<WoDynamic> dynamics) {
			this.dynamics = dynamics;
		}
		
		public Wo(byte[] bytes, String contentType, String contentDisposition) {
			super(bytes, contentType, contentDisposition);
		}
	}

	public static class WoDynamic extends Dynamic{

		private static final long serialVersionUID = -5076990764713538973L;

		public static WrapCopier<Dynamic, WoDynamic> copier = WrapCopierFactory.wo( Dynamic.class, WoDynamic.class, null, JpaObject.FieldsInvisible);
		
		private Long rank = 0L;

		public Long getRank() {
			return rank;
		}

		public void setRank(Long rank) {
			this.rank = rank;
		}		
	}
}

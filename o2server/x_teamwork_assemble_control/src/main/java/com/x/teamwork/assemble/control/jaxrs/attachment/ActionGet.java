package com.x.teamwork.assemble.control.jaxrs.attachment;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.teamwork.core.entity.Attachment;

public class ActionGet extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger( ActionGet.class );
	
	protected ActionResult<Wo> execute( HttpServletRequest request, EffectivePerson effectivePerson, String id ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wo wrap = null;
		Attachment attachment = null;
		if( StringUtils.isEmpty( id )){
			Exception exception = new ExceptionAttachmentIdEmpty();
			result.error( exception );
		}else{
			try {
				attachment = attachmentQueryService.get( id );
				if( attachment != null ){
					wrap = Wo.copier.copy( attachment );
					result.setData(wrap);
				}else{
					Exception exception = new ExceptionAttachmentNotExists( id );
					result.error( exception );
				}
			} catch ( Exception e ) {
				Exception exception = new ExceptionAttachmentQueryById( e, id );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		return result;
	}
	
	public static class Wo extends Attachment{

		private static final long serialVersionUID = -5076990764713538973L;

		public static WrapCopier<Attachment, Wo> copier = WrapCopierFactory.wo( Attachment.class, Wo.class, null, JpaObject.FieldsInvisible);
		
		private Long rank = 0L;

		public Long getRank() {
			return rank;
		}

		public void setRank(Long rank) {
			this.rank = rank;
		}
		
	}
}
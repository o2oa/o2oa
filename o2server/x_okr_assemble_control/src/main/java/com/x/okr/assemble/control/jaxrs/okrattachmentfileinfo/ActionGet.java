package com.x.okr.assemble.control.jaxrs.okrattachmentfileinfo;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.okr.assemble.control.jaxrs.okrattachmentfileinfo.exception.ExceptionAttachmentIdEmpty;
import com.x.okr.assemble.control.jaxrs.okrattachmentfileinfo.exception.ExceptionAttachmentNotExists;
import com.x.okr.assemble.control.jaxrs.okrattachmentfileinfo.exception.ExceptionAttachmentQueryById;
import com.x.okr.entity.OkrAttachmentFileInfo;

public class ActionGet extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger( ActionGet.class );
	
	protected ActionResult<Wo> execute( HttpServletRequest request, EffectivePerson effectivePerson, String id ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wo wrap = null;
		OkrAttachmentFileInfo okrAttachmentFileInfo = null;
		if( id == null || id.isEmpty() ){
			Exception exception = new ExceptionAttachmentIdEmpty();
			result.error( exception );
			//logger.error( e, effectivePerson, request, null);
		}else{
			try {
				okrAttachmentFileInfo = okrAttachmentFileInfoService.get( id );
				if( okrAttachmentFileInfo != null ){
					wrap = Wo.copier.copy( okrAttachmentFileInfo );
					result.setData(wrap);
				}else{
					Exception exception = new ExceptionAttachmentNotExists( id );
					result.error( exception );
					//logger.error( e, effectivePerson, request, null);
				}
			} catch ( Exception e ) {
				Exception exception = new ExceptionAttachmentQueryById( e, id );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		return result;
	}
	
	public static class Wo extends OkrAttachmentFileInfo{

		private static final long serialVersionUID = -5076990764713538973L;

		public static WrapCopier<OkrAttachmentFileInfo, Wo> copier = WrapCopierFactory.wo( OkrAttachmentFileInfo.class, Wo.class, null, JpaObject.FieldsInvisible);
		
		private Long rank = 0L;

		public Long getRank() {
			return rank;
		}

		public void setRank(Long rank) {
			this.rank = rank;
		}
		
	}
}
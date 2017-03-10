package com.x.okr.assemble.control.jaxrs.okrattachmentfileinfo;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.okr.entity.OkrAttachmentFileInfo;

public class ExcuteGet extends ExcuteBase {

	private Logger logger = LoggerFactory.getLogger( ExcuteGet.class );
	
	protected ActionResult<WrapOutOkrAttachmentFileInfo> execute( HttpServletRequest request, EffectivePerson effectivePerson, String id ) throws Exception {
		ActionResult<WrapOutOkrAttachmentFileInfo> result = new ActionResult<>();
		WrapOutOkrAttachmentFileInfo wrap = null;
		OkrAttachmentFileInfo okrAttachmentFileInfo = null;
		if( id == null || id.isEmpty() ){
			Exception exception = new AttachmentIdEmptyException();
			result.error( exception );
			logger.error( exception, effectivePerson, request, null);
		}else{
			try {
				okrAttachmentFileInfo = okrAttachmentFileInfoService.get( id );
				if( okrAttachmentFileInfo != null ){
					wrap = wrapout_copier.copy( okrAttachmentFileInfo );
					result.setData(wrap);
				}else{
					Exception exception = new AttachmentNotExistsException( id );
					result.error( exception );
					logger.error( exception, effectivePerson, request, null);
				}
			} catch ( Exception e ) {
				Exception exception = new AttachmentQueryByIdException( e, id );
				result.error( exception );
				logger.error( exception, effectivePerson, request, null);
			}
		}
		return result;
	}
	
}
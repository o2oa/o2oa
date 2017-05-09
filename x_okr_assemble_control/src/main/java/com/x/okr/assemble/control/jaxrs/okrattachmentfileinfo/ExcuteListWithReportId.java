package com.x.okr.assemble.control.jaxrs.okrattachmentfileinfo;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.okr.assemble.control.jaxrs.okrattachmentfileinfo.exception.WorkReportIdEmptyException;
import com.x.okr.assemble.control.jaxrs.okrattachmentfileinfo.exception.WorkReportQueryByIdException;
import com.x.okr.entity.OkrAttachmentFileInfo;
import com.x.okr.entity.OkrWorkReportBaseInfo;

public class ExcuteListWithReportId extends ExcuteBase {

	private Logger logger = LoggerFactory.getLogger( ExcuteListWithReportId.class );
	
	protected ActionResult<List<WrapOutOkrAttachmentFileInfo>> execute( HttpServletRequest request, EffectivePerson effectivePerson, String id ) throws Exception {
		ActionResult<List<WrapOutOkrAttachmentFileInfo>> result = new ActionResult<List<WrapOutOkrAttachmentFileInfo>>();
		List<WrapOutOkrAttachmentFileInfo> wrapOutOkrAttachmentFileInfoList = null;
		List<OkrAttachmentFileInfo> fileInfoList = null;
		OkrWorkReportBaseInfo workReportBaseInfo = null;
		if( id == null || id.isEmpty() ){
			Exception exception = new WorkReportIdEmptyException();
			result.error( exception );
			//logger.error( e, effectivePerson, request, null);
		}
		try {
			workReportBaseInfo = okrWorkReportQueryService.get( id );
			if( workReportBaseInfo != null ){
				if( workReportBaseInfo.getAttachmentList() != null && workReportBaseInfo.getAttachmentList().size() > 0 ){
					fileInfoList = okrAttachmentFileInfoService.list( workReportBaseInfo.getAttachmentList() );
				}else{
					fileInfoList = new ArrayList<OkrAttachmentFileInfo>();
				}
				wrapOutOkrAttachmentFileInfoList = wrapout_copier.copy( fileInfoList );
			}
//			else{
//				Exception exception = new WorkReportNotExistsException( id );
//				result.error( exception );
//				logger.error( e, effectivePerson, request, null);
//			}
		} catch (Exception e) {
			Exception exception = new WorkReportQueryByIdException( e, id );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}
		if( wrapOutOkrAttachmentFileInfoList == null ){
			wrapOutOkrAttachmentFileInfoList = new ArrayList<WrapOutOkrAttachmentFileInfo>();
		}
		result.setData( wrapOutOkrAttachmentFileInfoList );
		return result;
	}
	
}
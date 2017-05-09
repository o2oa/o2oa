package com.x.okr.assemble.control.jaxrs.okrattachmentfileinfo;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.okr.assemble.control.jaxrs.okrattachmentfileinfo.exception.AttachmentIdEmptyException;
import com.x.okr.assemble.control.jaxrs.okrattachmentfileinfo.exception.AttachmentListByIdsException;
import com.x.okr.assemble.control.jaxrs.okrattachmentfileinfo.exception.AttachmentWrapOutException;
import com.x.okr.assemble.control.jaxrs.okrattachmentfileinfo.exception.WorkQueryByIdException;
import com.x.okr.entity.OkrAttachmentFileInfo;
import com.x.okr.entity.OkrWorkBaseInfo;

public class ExcuteListWithWorkId extends ExcuteBase {

	private Logger logger = LoggerFactory.getLogger( ExcuteListWithWorkId.class );
	
	protected ActionResult<List<WrapOutOkrAttachmentFileInfo>> execute( HttpServletRequest request, EffectivePerson effectivePerson, String id ) throws Exception {
		ActionResult<List<WrapOutOkrAttachmentFileInfo>> result = new ActionResult<List<WrapOutOkrAttachmentFileInfo>>();
		List<WrapOutOkrAttachmentFileInfo> wrapOutOkrAttachmentFileInfoList = null;
		List<OkrAttachmentFileInfo> fileInfoList = null;
		OkrWorkBaseInfo okrWorkBaseInfo = null;
		if( id == null || id.isEmpty() ){
			Exception exception = new AttachmentIdEmptyException();
			result.error( exception );
			//logger.error( e, effectivePerson, request, null);
		}else{
			try {	
				okrWorkBaseInfo = okrWorkBaseInfoService.get( id );
			} catch ( Exception e ) {
				Exception exception = new WorkQueryByIdException( e, id );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
			if( okrWorkBaseInfo != null ){
				if( okrWorkBaseInfo.getAttachmentList() != null && okrWorkBaseInfo.getAttachmentList().size() > 0 ){
					try {
						fileInfoList = okrAttachmentFileInfoService.list( okrWorkBaseInfo.getAttachmentList() );
					} catch ( Exception e ) {
						Exception exception = new AttachmentListByIdsException( e );
						result.error( exception );
						logger.error( e, effectivePerson, request, null);
					}
				}else{
					fileInfoList = new ArrayList<OkrAttachmentFileInfo>();
				}
				if( fileInfoList != null && !fileInfoList.isEmpty() ){
					try {
						wrapOutOkrAttachmentFileInfoList = wrapout_copier.copy( fileInfoList );
					} catch ( Exception e ) {
						Exception exception = new AttachmentWrapOutException( e );
						result.error( exception );
						logger.error( e, effectivePerson, request, null);
					}
				}
			}
			if( wrapOutOkrAttachmentFileInfoList == null ){
				wrapOutOkrAttachmentFileInfoList = new ArrayList<WrapOutOkrAttachmentFileInfo>();
			}
			result.setData( wrapOutOkrAttachmentFileInfoList );
		}
		return result;
	}
	
}
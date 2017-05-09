package com.x.okr.assemble.control.jaxrs.okrattachmentfileinfo;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.okr.assemble.control.jaxrs.okrattachmentfileinfo.exception.CenterWorkIdEmptyException;
import com.x.okr.assemble.control.jaxrs.okrattachmentfileinfo.exception.CenterWorkQueryByIdException;
import com.x.okr.entity.OkrAttachmentFileInfo;
import com.x.okr.entity.OkrCenterWorkInfo;

public class ExcuteListWithCenterId extends ExcuteBase {

	private Logger logger = LoggerFactory.getLogger( ExcuteListWithCenterId.class );
	
	protected ActionResult<List<WrapOutOkrAttachmentFileInfo>> execute( HttpServletRequest request, EffectivePerson effectivePerson, String id ) throws Exception {
		ActionResult<List<WrapOutOkrAttachmentFileInfo>> result = new ActionResult<List<WrapOutOkrAttachmentFileInfo>>();
		List<WrapOutOkrAttachmentFileInfo> wrapOutOkrAttachmentFileInfoList = null;
		List<OkrAttachmentFileInfo> fileInfoList = null;
		OkrCenterWorkInfo okrCenterWorkInfo = null;
		if( id == null || id.isEmpty() ){
			Exception exception = new CenterWorkIdEmptyException();
			result.error( exception );
			//logger.error( e, effectivePerson, request, null);
		}else{
			try {	
				okrCenterWorkInfo = okrCenterWorkInfoService.get( id );
				if( okrCenterWorkInfo != null ){
					if( okrCenterWorkInfo.getAttachmentList() != null && okrCenterWorkInfo.getAttachmentList().size() > 0 ){
						fileInfoList = okrAttachmentFileInfoService.list( okrCenterWorkInfo.getAttachmentList() );
					}else{
						fileInfoList = new ArrayList<OkrAttachmentFileInfo>();
					}
					wrapOutOkrAttachmentFileInfoList = wrapout_copier.copy( fileInfoList );
				}
//				else{
//					Exception exception = new CenterWorkNotExistsException( id );
//					result.error( exception );
//					logger.error( e, effectivePerson, request, null);
//				}
			} catch (Exception e) {
				Exception exception = new CenterWorkQueryByIdException( e, id );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
			if( wrapOutOkrAttachmentFileInfoList == null ){
				wrapOutOkrAttachmentFileInfoList = new ArrayList<WrapOutOkrAttachmentFileInfo>();
			}
			result.setData( wrapOutOkrAttachmentFileInfoList );
		}
		return result;
	}
	
}
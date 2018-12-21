package com.x.okr.assemble.control.jaxrs.okrattachmentfileinfo;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.okr.assemble.control.jaxrs.okrattachmentfileinfo.exception.ExceptionCenterWorkIdEmpty;
import com.x.okr.assemble.control.jaxrs.okrattachmentfileinfo.exception.ExceptionCenterWorkQueryById;
import com.x.okr.entity.OkrAttachmentFileInfo;
import com.x.okr.entity.OkrCenterWorkInfo;

public class ActionListWithCenterId extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger( ActionListWithCenterId.class );
	
	protected ActionResult<List<Wo>> execute( HttpServletRequest request, EffectivePerson effectivePerson, String id ) throws Exception {
		ActionResult<List<Wo>> result = new ActionResult<List<Wo>>();
		List<Wo> wrapOutOkrAttachmentFileInfoList = null;
		List<OkrAttachmentFileInfo> fileInfoList = null;
		OkrCenterWorkInfo okrCenterWorkInfo = null;
		if( id == null || id.isEmpty() ){
			Exception exception = new ExceptionCenterWorkIdEmpty();
			result.error( exception );
		}else{
			try {	
				okrCenterWorkInfo = okrCenterWorkInfoService.get( id );
				if( okrCenterWorkInfo != null ){
					if( okrCenterWorkInfo.getAttachmentList() != null && okrCenterWorkInfo.getAttachmentList().size() > 0 ){
						fileInfoList = okrAttachmentFileInfoService.list( okrCenterWorkInfo.getAttachmentList() );
					}else{
						fileInfoList = new ArrayList<OkrAttachmentFileInfo>();
					}
					wrapOutOkrAttachmentFileInfoList = Wo.copier.copy( fileInfoList );
				}
			} catch (Exception e) {
				Exception exception = new ExceptionCenterWorkQueryById( e, id );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
			if( wrapOutOkrAttachmentFileInfoList == null ){
				wrapOutOkrAttachmentFileInfoList = new ArrayList<Wo>();
			}
			result.setData( wrapOutOkrAttachmentFileInfoList );
		}
		return result;
	}
	
	public static class Wo extends OkrAttachmentFileInfo{

		private static final long serialVersionUID = -5076990764713538973L;

		public static WrapCopier<OkrAttachmentFileInfo, Wo> copier = WrapCopierFactory.wo( OkrAttachmentFileInfo.class, Wo.class, null,JpaObject.FieldsInvisible);
		
		private Long rank = 0L;

		public Long getRank() {
			return rank;
		}

		public void setRank(Long rank) {
			this.rank = rank;
		}
		
	}
}
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
import com.x.okr.assemble.control.jaxrs.okrattachmentfileinfo.exception.ExceptionAttachmentIdEmpty;
import com.x.okr.assemble.control.jaxrs.okrattachmentfileinfo.exception.ExceptionAttachmentListByIds;
import com.x.okr.assemble.control.jaxrs.okrattachmentfileinfo.exception.ExceptionAttachmentWrapOut;
import com.x.okr.assemble.control.jaxrs.okrattachmentfileinfo.exception.ExceptionWorkQueryById;
import com.x.okr.entity.OkrAttachmentFileInfo;
import com.x.okr.entity.OkrWorkBaseInfo;

public class ActionListWithWorkId extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger( ActionListWithWorkId.class );
	
	protected ActionResult<List<Wo>> execute( HttpServletRequest request, EffectivePerson effectivePerson, String id ) throws Exception {
		ActionResult<List<Wo>> result = new ActionResult<List<Wo>>();
		List<Wo> wrapOutOkrAttachmentFileInfoList = null;
		List<OkrAttachmentFileInfo> fileInfoList = null;
		OkrWorkBaseInfo okrWorkBaseInfo = null;
		if( id == null || id.isEmpty() ){
			Exception exception = new ExceptionAttachmentIdEmpty();
			result.error( exception );
		}else{
			try {	
				okrWorkBaseInfo = okrWorkBaseInfoService.get( id );
			} catch ( Exception e ) {
				Exception exception = new ExceptionWorkQueryById( e, id );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
			if( okrWorkBaseInfo != null ){
				if( okrWorkBaseInfo.getAttachmentList() != null && okrWorkBaseInfo.getAttachmentList().size() > 0 ){
					try {
						fileInfoList = okrAttachmentFileInfoService.list( okrWorkBaseInfo.getAttachmentList() );
					} catch ( Exception e ) {
						Exception exception = new ExceptionAttachmentListByIds( e );
						result.error( exception );
						logger.error( e, effectivePerson, request, null);
					}
				}else{
					fileInfoList = new ArrayList<OkrAttachmentFileInfo>();
				}
				if( fileInfoList != null && !fileInfoList.isEmpty() ){
					try {
						wrapOutOkrAttachmentFileInfoList = Wo.copier.copy( fileInfoList );
					} catch ( Exception e ) {
						Exception exception = new ExceptionAttachmentWrapOut( e );
						result.error( exception );
						logger.error( e, effectivePerson, request, null);
					}
				}
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
package com.x.okr.assemble.control.jaxrs.okrworkreportdetailinfo;

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
import com.x.okr.assemble.control.jaxrs.okrworkreportdetailinfo.exception.ExceptionReportDetailIdEmpty;
import com.x.okr.assemble.control.jaxrs.okrworkreportdetailinfo.exception.ExceptionReportDetailNotExists;
import com.x.okr.assemble.control.jaxrs.okrworkreportdetailinfo.exception.ExceptionReportDetailQueryById;
import com.x.okr.entity.OkrWorkReportDetailInfo;

public class ActionGet extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger( ActionGet.class );
	
	protected ActionResult<Wo> execute( HttpServletRequest request,EffectivePerson effectivePerson, String id ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wo wrap = null;
		OkrWorkReportDetailInfo okrWorkReportDetailInfo = null;
		if( id == null || id.isEmpty() ){
			Exception exception = new ExceptionReportDetailIdEmpty();
			result.error( exception );
		}else{
			try {
				okrWorkReportDetailInfo = okrWorkReportDetailInfoService.get( id );
				if( okrWorkReportDetailInfo != null ){
					wrap = Wo.copier.copy( okrWorkReportDetailInfo );
					result.setData(wrap);
				}else{
					Exception exception = new ExceptionReportDetailNotExists( id );
					result.error( exception );
				}
			} catch (Exception e) {
				Exception exception = new ExceptionReportDetailQueryById( e, id );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		return result;
	}
	
	public static class Wo extends OkrWorkReportDetailInfo{

		private static final long serialVersionUID = -5076990764713538973L;

		public static List<String> Excludes = new ArrayList<String>();

		public static WrapCopier<OkrWorkReportDetailInfo, Wo> copier = WrapCopierFactory.wo( OkrWorkReportDetailInfo.class, Wo.class, null, JpaObject.FieldsInvisible);
		
		private Long rank = 0L;

		public Long getRank() {
			return rank;
		}

		public void setRank(Long rank) {
			this.rank = rank;
		}
		
	}
}
package com.x.okr.assemble.control.jaxrs.okrworkreportprocesslog;

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
import com.x.okr.assemble.control.jaxrs.okrworkreportprocesslog.exception.ExceptionReportProcessLogIdEmpty;
import com.x.okr.assemble.control.jaxrs.okrworkreportprocesslog.exception.ExceptionReportProcessLogNotExists;
import com.x.okr.assemble.control.jaxrs.okrworkreportprocesslog.exception.ExceptionReportProcessLogQueryById;
import com.x.okr.entity.OkrWorkReportProcessLog;

public class ActionGet extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger( ActionGet.class );
	
	protected ActionResult<Wo> execute( HttpServletRequest request,EffectivePerson effectivePerson, String id ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wo wrap = null;
		OkrWorkReportProcessLog okrWorkReportProcessLog = null;
		
		if( id == null || id.isEmpty() ){
			Exception exception = new ExceptionReportProcessLogIdEmpty();
			result.error( exception );
		}else{
			try {
				okrWorkReportProcessLog = okrWorkReportProcessLogService.get( id );
				if( okrWorkReportProcessLog != null ){
					wrap = Wo.copier.copy( okrWorkReportProcessLog );
					result.setData(wrap);
				}else{
					Exception exception = new ExceptionReportProcessLogNotExists( id );
					result.error( exception );
				}
			} catch (Exception e) {
				Exception exception = new ExceptionReportProcessLogQueryById( e, id );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		return result;
	}
	
	public static class Wo extends OkrWorkReportProcessLog{

		private static final long serialVersionUID = -5076990764713538973L;

		public static List<String> Excludes = new ArrayList<String>();

		public static WrapCopier<OkrWorkReportProcessLog, Wo> copier = WrapCopierFactory.wo( OkrWorkReportProcessLog.class, Wo.class, null, JpaObject.FieldsInvisible);
		
		private Long rank = 0L;

		public Long getRank() {
			return rank;
		}

		public void setRank(Long rank) {
			this.rank = rank;
		}
		
	}
}
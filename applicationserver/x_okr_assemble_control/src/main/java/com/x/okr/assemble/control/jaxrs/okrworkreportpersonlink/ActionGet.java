package com.x.okr.assemble.control.jaxrs.okrworkreportpersonlink;

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
import com.x.okr.assemble.control.jaxrs.okrworkreportpersonlink.exception.ExceptionReportPersonLinkIdEmpty;
import com.x.okr.assemble.control.jaxrs.okrworkreportpersonlink.exception.ExceptionReportPersonLinkNotExists;
import com.x.okr.assemble.control.jaxrs.okrworkreportpersonlink.exception.ExceptionReportPersonLinkQueryById;
import com.x.okr.entity.OkrWorkReportPersonLink;

public class ActionGet extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger( ActionGet.class );
	
	protected ActionResult<Wo> execute( HttpServletRequest request,EffectivePerson effectivePerson, String id ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wo wrap = null;
		OkrWorkReportPersonLink okrWorkReportPersonLink = null;
		
		if( id == null || id.isEmpty() ){
			Exception exception = new ExceptionReportPersonLinkIdEmpty();
			result.error( exception );
		}else{
			try {
				okrWorkReportPersonLink = okrWorkReportPersonLinkService.get( id );
				if( okrWorkReportPersonLink != null ){
					wrap = Wo.copier.copy( okrWorkReportPersonLink );
					result.setData(wrap);
				}else{
					Exception exception = new ExceptionReportPersonLinkNotExists( id );
					result.error( exception );
				}
			} catch (Exception e) {
				Exception exception = new ExceptionReportPersonLinkQueryById( e, id );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		return result;
	}
	
	public static class Wo extends OkrWorkReportPersonLink{

		private static final long serialVersionUID = -5076990764713538973L;

		public static List<String> Excludes = new ArrayList<String>();

		public static WrapCopier<OkrWorkReportPersonLink, Wo> copier = WrapCopierFactory.wo( OkrWorkReportPersonLink.class, Wo.class, null, JpaObject.FieldsInvisible);
		
		private Long rank = 0L;

		public Long getRank() {
			return rank;
		}

		public void setRank(Long rank) {
			this.rank = rank;
		}
		
	}
}
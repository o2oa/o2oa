package com.x.report.assemble.control.jaxrs.workinfo;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.report.assemble.control.jaxrs.workinfo.exception.ExceptionParameterInvalid;
import com.x.report.core.entity.Report_I_WorkInfo;

/**
 * 根据ID获取指定的工作信息
 * @author O2LEE
 *
 */
public class ActionGetWorkInfo extends BaseAction {
	
	protected ActionResult<Wo> execute( HttpServletRequest request, EffectivePerson effectivePerson, String id ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Report_I_WorkInfo report_I_WorkInfo = null;
		Wo wo = null;
		Boolean check = true;
		
		if( check ){
			if( id == null || id.isEmpty() ){
				check = false;
				Exception exception = new ExceptionParameterInvalid( "参数'id'不允许为空！" );
				result.error( exception );
			}
		}

		if( check ) {
			report_I_WorkInfo = report_I_WorkInfoServiceAdv.get( id );
		}

		if( check ) {
			if( report_I_WorkInfo != null ){
				wo = Wo.copier.copy( report_I_WorkInfo );
			}
		}
		result.setData( wo );
		return result;
	}

	public static class Wo extends Report_I_WorkInfo  {

		private static final long serialVersionUID = -5076990764713538973L;

		public static List<String> Excludes = new ArrayList<>();

		public static WrapCopier<Report_I_WorkInfo, Wo> copier = WrapCopierFactory.wo( Report_I_WorkInfo.class, Wo.class, null, Wo.Excludes);
	}
}
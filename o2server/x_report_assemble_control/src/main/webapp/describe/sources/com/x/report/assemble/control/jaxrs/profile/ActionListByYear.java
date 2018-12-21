package com.x.report.assemble.control.jaxrs.profile;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.report.assemble.control.jaxrs.report.exception.ExceptionReportInfoProcess;
import com.x.report.core.entity.Report_P_Profile;


/**
 * 根据年份查询汇报信息列表
 * @author O2LEE
 *
 */
public class ActionListByYear extends BaseAction {
	
	private Logger logger = LoggerFactory.getLogger( ActionListByYear.class );
	
	protected ActionResult<List<Wo>> execute( HttpServletRequest request, EffectivePerson effectivePerson, String year ) throws Exception {
		ActionResult<List<Wo>> result = new ActionResult<>();
		List<Wo> wraps = null;
		List<Report_P_Profile> profiles = null;
		List<String> ids = null;
		Boolean check = true;
		
		if( check ){
			try {
				ids = report_P_ProfileServiceAdv.listWithYear( year );
			}catch( Exception e ) {
				check = false;
				Exception exception = new ExceptionReportInfoProcess(e, "系统在根据根据年份查询汇报概要文件信息ID列表时发生异常。");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		
		if( check ){
			if( ListTools.isNotEmpty( ids ) ) {
				try {
					profiles = report_P_ProfileServiceAdv.list( ids );
				}catch( Exception e ) {
					check = false;
					Exception exception = new ExceptionReportInfoProcess(e, "系统在根据概要文件ID列表查询概要文件列表时发生异常。");
					result.error(exception);
					logger.error(e, effectivePerson, request, null);
				}
			}
		}
		
		if( check ){
			if( ListTools.isNotEmpty( profiles ) ){
				wraps = Wo.copier.copy( profiles );
				result.setData(wraps);
				result.setCount( Long.parseLong( wraps.size() + "" ) );
			}
		}
		
		return result;
	}
	
	public static class Wo extends Report_P_Profile  {
		
		private static final long serialVersionUID = -5076990764713538973L;
		
		public static List<String> Excludes = new ArrayList<String>();
		
		public static WrapCopier<Report_P_Profile, Wo> copier = WrapCopierFactory.wo( Report_P_Profile.class, Wo.class, null,Wo.Excludes);
		
	}
}
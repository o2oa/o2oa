package com.x.report.assemble.control.jaxrs.report;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.report.core.entity.Report_I_Base;


/**
 * 根据年份查询汇报信息列表
 * @author O2LEE
 *
 */
public class ActionListByYear extends BaseAction {
	
	//private Logger logger = LoggerFactory.getLogger( ActionListByYear.class );
	
	protected ActionResult<List<Wo>> execute( HttpServletRequest request, EffectivePerson effectivePerson, String year ) throws Exception {
		ActionResult<List<Wo>> result = new ActionResult<>();
		List<Wo> wraps = null;
		List<Report_I_Base> reports = null;
		Boolean check = true;
		
		if( check ){
			try {
				reports = report_I_ServiceAdv.list( null, null, null, null, year, null, null, null, null, false  );
			}catch( Exception e ) {
				e.printStackTrace();
			}
		}
		
		if( check ){
			if( reports != null && !reports.isEmpty() ) {
				wraps = Wo.copier.copy( reports );
				result.setData(wraps);
				result.setCount( Long.parseLong( wraps.size() + "" ) );
			}
		}
		
		return result;
	}
	
	public static class Wo extends Report_I_Base  {
		
		private static final long serialVersionUID = -5076990764713538973L;
		
		public static List<String> Excludes = new ArrayList<String>();
		
		public static WrapCopier<Report_I_Base, Wo> copier = WrapCopierFactory.wo( Report_I_Base.class, Wo.class, null,Wo.Excludes);
		
	}
}
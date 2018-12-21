package com.x.report.assemble.control.jaxrs.workinfo;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.google.gson.JsonElement;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.report.assemble.control.ExceptionWrapInConvert;
import com.x.report.assemble.control.jaxrs.workinfo.exception.ExceptionParameterInvalid;
import com.x.report.core.entity.Report_I_WorkTag;

/**
 * 根据汇报ID以及指定的工作ID获取所有的工作计划信息列表
 * @author O2LEE
 *
 */
public class ActionListWorkTagWithUnit extends BaseAction {
	
	private Logger logger = LoggerFactory.getLogger( ActionListWorkTagWithUnit.class);
	
	protected ActionResult<List<Wo>> execute( HttpServletRequest request, EffectivePerson effectivePerson, JsonElement jsonElement ) throws Exception {
		ActionResult<List<Wo>> result = new ActionResult<>();
		List<Report_I_WorkTag> workTagList = null;
		List<Wo> wos = null;
		List<String> ids = null;
		Wi wi = null;
		String unitName = null;
		Boolean check = true;
		
		try {
			wi = this.convertToWrapIn( jsonElement, Wi.class );
			unitName = wi.getUnitName();
		} catch (Exception e ) {
			check = false;
			Exception exception = new ExceptionWrapInConvert( e, jsonElement );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}
		
		if( check ){
			if( unitName == null || unitName.isEmpty() ){
				check = false;
				Exception exception = new ExceptionParameterInvalid( "参数'unitName'不允许为空！" );
				result.error( exception );
			}
		}

		if( check ) {
			ids = report_I_WorkInfoServiceAdv.listWorkTagIdsWithUnitName(unitName);
		}

		if( check ) {
			if(ListTools.isNotEmpty( ids )){
				workTagList = report_I_WorkInfoServiceAdv.listWorkTags( ids );
				if( ListTools.isNotEmpty( workTagList )){
					wos = Wo.copier.copy( workTagList );
					result.setData( wos );
				}
			}
		}
		return result;
	}

	public static class Wi {
		
		@FieldDescribe( "组织名称" )
		private String unitName = "";

		public String getUnitName() {
			return unitName;
		}

		public void setUnitName(String unitName) {
			this.unitName = unitName;
		}		
	}

	public static class Wo extends Report_I_WorkTag  {

		private static final long serialVersionUID = -5076990764713538973L;

		public static List<String> Excludes = new ArrayList<>();

		public static WrapCopier<Report_I_WorkTag, Wo> copier = WrapCopierFactory.wo( Report_I_WorkTag.class, Wo.class, null, Wo.Excludes);
	}
}
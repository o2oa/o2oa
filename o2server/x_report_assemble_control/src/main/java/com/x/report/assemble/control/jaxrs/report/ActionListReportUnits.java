package com.x.report.assemble.control.jaxrs.report;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapString;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.report.assemble.control.jaxrs.export.BaseAction;
import com.x.report.assemble.control.jaxrs.export.exception.ExceptionDataQuery;
import com.x.report.assemble.control.jaxrs.report.exception.ExceptionReportInfoProcess;

/**
 * 获取拥有汇报的组织列表
 * @author O2LEE
 */
public class ActionListReportUnits extends BaseAction {
	
	private static Logger logger = LoggerFactory.getLogger(ActionListReportUnits.class);
	
	protected ActionResult<List<Wo>> execute( HttpServletRequest request, EffectivePerson effectivePerson,  JsonElement jsonElement  ) {
		ActionResult<List<Wo>> result = new ActionResult<>();
		List<Wo> wraps = new ArrayList<>();
		Wi wi = null;
		Boolean check = true;

		try {
			wi = this.convertToWrapIn( jsonElement, Wi.class );
		} catch (Exception e ) {
			check = false;
			Exception exception = new ExceptionReportInfoProcess( e, "系统在将JSON信息转换为对象时发生异常。JSON:" + jsonElement.toString() );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}
		
		if( check ) {
			if( StringUtils.isEmpty( wi.getYear() )) {
				wi.setYear( dateOperation.getYear( new Date() ));
			}
			if( StringUtils.isEmpty( wi.getMonth() )) {
				wi.setMonth( dateOperation.getMonth( new Date() ));
			}
		}

		List<String>unitNames = null;
		if( check ) {
			try {
				unitNames = report_I_ServiceAdv.listUnitNamesWithConditions( wi.getYear(), wi.getMonth(), wi.getWfProcessStatus(), wi.getWfActivityNames(), false  );
				if( ListTools.isNotEmpty( unitNames )) {
					Wo wo = null;
					for( String unitName : unitNames ) {
						wo = new Wo();
						wo.setValue( unitName );
						wraps.add( wo );
					}
				}				
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionDataQuery( e, "year:" + wi.getYear() + ", month" + wi.getMonth() );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}		
		result.setData( wraps );
		return result;
	}
	
	
	public static class Wi {

		@FieldDescribe( "汇报年份." )
		private String year;
		
		@FieldDescribe( "汇报月份." )
		private String month;
		
		@FieldDescribe( "流程处理状态: 已完成、 流转中" )
		private List<String> wfProcessStatus;

		@FieldDescribe( "流程处理环节名称: 月度汇报员分派填写人、 战略负责人审核、月度汇报员汇总、汇报人" )
		private List<String> wfActivityNames;
		
		public String getYear() {
			return year;
		}

		public String getMonth() {
			return month;
		}

		public void setYear(String year) {
			this.year = year;
		}

		public void setMonth(String month) {
			this.month = month;
		}

		public List<String> getWfProcessStatus() {
			return wfProcessStatus;
		}

		public void setWfProcessStatus(List<String> wfProcessStatus) {
			this.wfProcessStatus = wfProcessStatus;
		}

		public List<String> getWfActivityNames() {
			return wfActivityNames;
		}

		public void setWfActivityNames(List<String> wfActivityNames) {
			this.wfActivityNames = wfActivityNames;
		}		
	}
	
	public static class Wo extends WrapString {
		
	}
}

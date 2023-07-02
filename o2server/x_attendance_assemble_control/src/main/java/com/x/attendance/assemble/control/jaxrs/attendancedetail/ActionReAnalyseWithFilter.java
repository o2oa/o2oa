package com.x.attendance.assemble.control.jaxrs.attendancedetail;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.attendance.assemble.control.ExceptionWrapInConvert;
import com.x.attendance.assemble.control.ThisApplication;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;

public class ActionReAnalyseWithFilter extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger(ActionReAnalyseWithFilter.class);

	protected ActionResult<Wo> execute(HttpServletRequest request, EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		EffectivePerson currentPerson = this.effectivePerson(request);
		String q_empName = null;
		String q_year = null;
		String q_month = null;
		List<String> ids = null;
		Wi wrapIn = null;
		Wo wo = new Wo();
		Boolean check = true;

		try {
			wrapIn = this.convertToWrapIn(jsonElement, Wi.class);
		} catch (Exception e) {
			check = false;
			Exception exception = new ExceptionWrapInConvert(e, jsonElement);
			result.error(exception);
			logger.error(e, currentPerson, request, null);
		}

		if (check) {
			if (wrapIn == null) {
				wrapIn = new Wi();
			}
			q_empName = wrapIn.getQ_empName();
			q_year = wrapIn.getQ_year();
			q_month = wrapIn.getQ_month();
		}

		if (check) {
			if(StringUtils.isEmpty( q_year )){
				check = false;
				Exception exception = new ExceptionQueryParameterEmpty("查询打卡信息年份不可以为空");
				result.error(exception);
			}
		}
		if (check) {
			if(StringUtils.isEmpty( q_month )){
				check = false;
				Exception exception = new ExceptionQueryParameterEmpty("查询打卡信息月份不可以为空");
				result.error(exception);
			}
		}

		if (check) {
			if(StringUtils.isEmpty( q_empName )){
				q_empName = effectivePerson.getDistinguishedName();
			}
		}

		if (check) {
			try {
				ids = attendanceDetailServiceAdv.listDetailByCycleYearAndMonthWithOutStatus( q_empName, q_year, q_month );
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionAttendanceDetailProcess(e,
						"系统在根据员工姓名，年份月份查询打卡详细信息ID列表时发生异常！"
						+ "Name:" + q_empName + ", Year:" + q_year + ", Month:" + q_month);
				result.error(exception);
				logger.error(e, currentPerson, request, null);
			}
		}

		if (check) {
			if (ListTools.isNotEmpty( ids )) {

				if( ids.size() > 100000 ){
					Exception exception = new ExceptionAttendanceDetailProcess("需要重新分析的打卡记录条目数量过多，请缩小重新分析的信息范围（一次不超过10万条）。需要分析的打卡信息数量：" + ids.size() );
					result.error(exception);
				}else{
					try {
						int seq = 0;
						for( String id : ids){
							seq++;
							ThisApplication.detailAnalyseQueue.send( id );
						}
					} catch (Exception e) {
						Exception exception = new ExceptionAttendanceDetailProcess(e,"将需要重新分析的打卡记录发送到分析队列时发生异常！");
						result.error(exception);
						logger.error(e, currentPerson, request, null);
					}
				}

				result.setCount(Long.parseLong(ids.size() + ""));
			}
		}
		result.setData(wo);
		return result;
	}

	public static class Wi extends GsonPropertyObject {

		@FieldDescribe( "用于查询的人员：DistinguishedName." )
		private String q_empName;

		@FieldDescribe( "查询的年份." )
		private String q_year;

		@FieldDescribe( "查询的月份." )
		private String q_month;

		public String getQ_empName() {
			return q_empName;
		}

		public String getQ_year() {
			return q_year;
		}

		public String getQ_month() {
			return q_month;
		}

		public void setQ_empName(String q_empName) {
			this.q_empName = q_empName;
		}

		public void setQ_year(String q_year) {
			this.q_year = q_year;
		}

		public void setQ_month(String q_month) {
			this.q_month = q_month;
		}
	}

	public static class Wo{
	}
}
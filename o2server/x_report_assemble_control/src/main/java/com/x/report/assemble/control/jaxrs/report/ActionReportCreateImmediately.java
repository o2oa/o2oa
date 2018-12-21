package com.x.report.assemble.control.jaxrs.report;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import com.google.gson.JsonElement;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.http.WrapOutBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.report.assemble.control.ExceptionWrapInConvert;
import com.x.report.assemble.control.service.Report_Sv_ReportCreator;
import com.x.report.common.date.DateOperation;

/**
 * 根据ID获取指定的汇报完整信息，包括当月计划， 完成情况 ，下月等内容，以及汇报的审批过程
 * 
 * @author O2LEE
 *
 */
public class ActionReportCreateImmediately extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionReportCreateImmediately.class);

	protected ActionResult<WrapOutBoolean> execute(HttpServletRequest request, EffectivePerson effectivePerson,
			JsonElement jsonElement) throws Exception {
		ActionResult<WrapOutBoolean> result = new ActionResult<>();
		WrapOutBoolean wrapOutBoolean = new WrapOutBoolean();
		logger.debug(effectivePerson, ">>>>>>>>>>>>系统正在尝试手动生成汇报......");
		Wi wrapIn = null;
		Date date = null;
		Boolean check = true;

		try {
			wrapIn = this.convertToWrapIn(jsonElement, Wi.class);
		} catch (Exception e) {
			check = false;
			Exception exception = new ExceptionWrapInConvert(e, jsonElement);
			result.error(exception);
			logger.error(e, effectivePerson, request, null);
		}

		if (check) {
			if (wrapIn.getDate() == null || wrapIn.getDate().isEmpty()) {
				date = new Date();
			} else {
				try {
					date = new DateOperation().getDateFromString(wrapIn.getDate());
				} catch (Exception e) {
					logger.debug(effectivePerson, "参数格式不正确，无法格式化为日期：" + wrapIn.getDate());
					e.printStackTrace();
					result.error(e);
				}
			}
		}

		if (check) {
			Boolean create = new Report_Sv_ReportCreator().create(effectivePerson, date);
			wrapOutBoolean.setValue(create);
			result.setData(wrapOutBoolean);
		}

		return result;
	}

	public static class Wi {

		@FieldDescribe("日期: 2017-10-01")
		private String date = null;

		public String getDate() {
			return date;
		}

		public void setDate(String date) {
			this.date = date;
		}
	}
}
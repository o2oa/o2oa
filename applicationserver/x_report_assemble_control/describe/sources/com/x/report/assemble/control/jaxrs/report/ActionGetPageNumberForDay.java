package com.x.report.assemble.control.jaxrs.report;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.SortTools;
import com.x.report.assemble.control.jaxrs.report.exception.ExceptionReportInfoProcess;
import com.x.report.common.date.DateOperation;

public class ActionGetPageNumberForDay extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionGetPageNumberForDay.class);
	private DateOperation dateOperation = new DateOperation();

	/**
	 * 根据指定的日期和每页多少条计算日期所在的页码数
	 * 
	 * @param request
	 * @param date
	 * @param count
	 * @param effectivePerson
	 * @return
	 */
	protected ActionResult<Wo> execute(HttpServletRequest request, String date, Integer count,
			EffectivePerson effectivePerson) {
		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = new Wo();
		List<String> permissionObjectCodes = null;
		List<String> dateStringList = null;
		List<String> dateList = new ArrayList<>();
		Boolean check = true;

		if (date == null || date.isEmpty()) {
			date = dateOperation.getNowDate();
		}

		if (count == null || count == 0) {
			count = 20;
		}

		if (check) {
			try {
				permissionObjectCodes = userManagerService
						.getPersonPermissionCodes(effectivePerson.getDistinguishedName());
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionReportInfoProcess(e, "系统在根据登录用户姓名获取用户拥有的所有组织，角色，群组信息列表时发生异常。");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}

		// 查询自己可以查看的汇报内容的所有日期列表，再将日期分页
		if (check) {
			try {
				dateStringList = report_I_PermissionServiceAdv.listAllCreateDateString(permissionObjectCodes);
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionReportInfoProcess(e, "系统在查询用户有权限查看汇报的所有日期列表时发生异常。");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}

		// 将dateStringList分页，只需要指定的一页信息，并且查询每一天用户可以看到的所有汇报列表
		if (check) {
			if (dateStringList != null && !dateStringList.isEmpty()) {
				for (String s_date : dateStringList) {
					if (!dateList.contains(s_date)) {
						dateList.add(s_date);
					}
				}
				// 如果查询的那个日期没有汇报，那么把这个日期加入到列表里一起排序，进行页码计算
				if (!dateList.contains(date)) {
					dateList.add(date);
				}
				// 日期排序
				SortTools.desc(dateList);

				// 再循环一次，确定需要查询的日期所在的位置，然后再计算页码
				int index = 0;
				for (String s_date : dateList) {
					index++;
					if (s_date.equalsIgnoreCase(date)) {
						break;
					}
				}

				// 计算总页码数
				int totalPage = 0;
				totalPage = dateList.size() / count;
				if (dateList.size() == 0) {
					totalPage = 0;
				} else {
					if (dateList.size() % count > 0) {
						totalPage++;
					}
				}

				// 计算页码数
				int currentPage = 0;
				currentPage = index / count;
				if (index == 0) {
					currentPage = 1;
				} else {
					if (index % count > 0) {
						currentPage++;
					}
				}

				wo.setCount(count);
				wo.setDate(date);
				wo.setCurrentPage(currentPage);
				wo.setTotalPage(totalPage);
			}
		}

		result.setCount(Long.parseLong(wo.getTotalPage().intValue() + ""));
		result.setData(wo);
		return result;
	}

	public static class Wo {

		@FieldDescribe("查询的日期")
		String date = null;

		@FieldDescribe("日期所在的页码")
		Integer currentPage = 0;

		@FieldDescribe("总页码数")
		Integer totalPage = 0;

		@FieldDescribe("每页显示的条目数")
		Integer count = 0;

		public String getDate() {
			return date;
		}

		public Integer getCount() {
			return count;
		}

		public void setDate(String date) {
			this.date = date;
		}

		public void setCount(Integer count) {
			this.count = count;
		}

		public Integer getCurrentPage() {
			return currentPage;
		}

		public Integer getTotalPage() {
			return totalPage;
		}

		public void setCurrentPage(Integer currentPage) {
			this.currentPage = currentPage;
		}

		public void setTotalPage(Integer totalPage) {
			this.totalPage = totalPage;
		}

	}

}
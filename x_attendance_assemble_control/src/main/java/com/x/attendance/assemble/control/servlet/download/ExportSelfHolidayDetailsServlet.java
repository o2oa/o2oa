package com.x.attendance.assemble.control.servlet.download;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.x.attendance.assemble.common.date.DateOperation;
import com.x.attendance.assemble.control.Business;
import com.x.attendance.assemble.control.jaxrs.attendanceimportfileinfo.WrapOutAttendanceImportFileInfo;
import com.x.attendance.entity.AttendanceSelfHoliday;
import com.x.base.core.application.servlet.FileUploadServletTools;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.annotation.HttpMethodDescribe;
import com.x.base.core.project.server.Config;

@WebServlet(urlPatterns = "/servlet/export/selfholiday/*")
public class ExportSelfHolidayDetailsServlet extends HttpServlet {

	private static final long serialVersionUID = -4314532091497625540L;
	private Logger logger = LoggerFactory.getLogger( ExportSelfHolidayDetailsServlet.class );

	@HttpMethodDescribe(value = "导出信息 servlet/export/selfholiday/{startdate}/{enddate}/stream", response = WrapOutAttendanceImportFileInfo.class)
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		boolean streamContentType = false;
		request.setCharacterEncoding("UTF-8");
		DateOperation dateOperation = new DateOperation();
		try {
			EffectivePerson effectivePerson = FileUploadServletTools.effectivePerson(request);
			logger.debug("[ExportSelfHolidayDetailsServlet]用户" + effectivePerson.getName() + "尝试下载数据文件.....");
			String part = FileUploadServletTools.getURIPart(request.getRequestURI(), "selfholiday");
			logger.debug("[ExportSelfHolidayDetailsServlet]截取URL, part=[" + part + "]");
			// 从URI里截取需要的信息
			String startdate = StringUtils.substringBefore(part, "/"); // year
			logger.debug("[ExportSelfHolidayDetailsServlet]从URL中获取year, startdate=[" + startdate + "]");
			part = StringUtils.substringAfter(part, "/"); // {month}/stream
			logger.debug("[ExportSelfHolidayDetailsServlet]从URL中获取part, part=[" + part + "]");
			String enddate = StringUtils.substringBefore(part, "/"); // year
			logger.debug("[ExportSelfHolidayDetailsServlet]从URL中获取month, enddate=[" + enddate + "]");
			part = StringUtils.substringAfter(part, "/"); // {month}/stream
			logger.debug("[ExportSelfHolidayDetailsServlet]从URL中获取part, part=[" + part + "]");
			streamContentType = StringUtils.endsWith(part, "stream");
			logger.info("[downloadFile]用application/octet-stream输出，streamContentType=" + streamContentType);

			List<String> ids = null;
			List<AttendanceSelfHoliday> detailList = null;
			// 先组织一个EXCEL
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				Business business = new Business(emc);

				Date startDate = dateOperation.getDateFromString(startdate + " 00:00:00");
				Date endDate = dateOperation.getDateFromString(enddate + " 23:59:59");

				ids = business.getAttendanceSelfHolidayFactory().listByStartDateAndEndDate(startDate, endDate);
				detailList = business.getAttendanceSelfHolidayFactory().list(ids);
			} catch (Exception e) {
				logger.error("系统在查询所有[" + startdate + "] 到 [" + enddate + "]的所有请假记录时发生异常。", e);
			}
			// 将结果组织成EXCEL
			String fileName = "" + startdate + "至" + enddate + "月请假记录.xls";
			Workbook wb = new HSSFWorkbook();
			AttendanceSelfHoliday attendanceSelfHoliday = null;
			if (detailList != null && detailList.size() > 0) {
				// 创建新的表格
				Sheet sheet = wb.createSheet("请假记录");
				Row row = null;

				// 先创建表头
				row = sheet.createRow(0);
				row.createCell(0).setCellValue("公司名称");
				row.createCell(1).setCellValue("部门名称");
				row.createCell(2).setCellValue("员工姓名");
				row.createCell(3).setCellValue("请假类型");
				row.createCell(4).setCellValue("开始时间");
				row.createCell(5).setCellValue("结束时间");
				row.createCell(6).setCellValue("请假天数");
				row.createCell(7).setCellValue("其他说明");

				for (int i = 0; i < detailList.size(); i++) {
					attendanceSelfHoliday = detailList.get(i);
					row = sheet.createRow(i + 1);
					row.createCell(0).setCellValue(attendanceSelfHoliday.getCompanyName());
					row.createCell(1).setCellValue(attendanceSelfHoliday.getOrganizationName());
					row.createCell(2).setCellValue(attendanceSelfHoliday.getEmployeeName());
					row.createCell(3).setCellValue(attendanceSelfHoliday.getLeaveType());
					row.createCell(4).setCellValue(dateOperation
							.getDateStringFromDate(attendanceSelfHoliday.getStartTime(), "yyyy-MM-dd HH:mm:ss"));
					row.createCell(5).setCellValue(dateOperation
							.getDateStringFromDate(attendanceSelfHoliday.getEndTime(), "yyyy-MM-dd HH:mm:ss"));
					row.createCell(6).setCellValue(attendanceSelfHoliday.getLeaveDayNumber());
					row.createCell(7).setCellValue(attendanceSelfHoliday.getDescription());
				}
			}
			// 向浏览器输出文件流
			OutputStream output = response.getOutputStream();
			this.setResponseHeader(response, streamContentType, fileName);
			wb.write(output);
			output.flush();
			output.close();
		} catch (Exception e) {
			logger.error("[ExportSelfHolidayDetailsServlet]用户下载请假记录文件发生未知异常！", e);
			ActionResult<Object> result = new ActionResult<>();
			result.error(e);
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			FileUploadServletTools.result(response, result);
		}
	}

	private void setResponseHeader(HttpServletResponse response, boolean streamContentType, String fileName)
			throws Exception {
		if (streamContentType) {
			response.setHeader("Content-Type", "application/octet-stream");
			response.setHeader("Content-Disposition", "fileInfo; filename=" + URLEncoder.encode(fileName, "utf-8"));
		} else {
			response.setHeader("Content-Type", Config.mimeTypes().getContentType(fileName));
			response.setHeader("Content-Disposition", "inline; filename=" + URLEncoder.encode(fileName, "utf-8"));
		}
	}
}
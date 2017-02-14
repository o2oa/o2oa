package com.x.attendance.assemble.control.servlet.download;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
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

import com.x.attendance.assemble.control.Business;
import com.x.attendance.assemble.control.jaxrs.attendanceimportfileinfo.WrapOutAttendanceImportFileInfo;
import com.x.attendance.entity.AttendanceAppealInfo;
import com.x.attendance.entity.AttendanceDetail;
import com.x.base.core.application.servlet.FileUploadServletTools;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.annotation.HttpMethodDescribe;
import com.x.base.core.project.server.Config;

@WebServlet(urlPatterns = "/servlet/export/abnormaldetails/*")
public class ExportAbnormalCaseDetailsServlet extends HttpServlet {

	private static final long serialVersionUID = -4314532091497625540L;
	private Logger logger = LoggerFactory.getLogger( ExportAbnormalCaseDetailsServlet.class );

	@HttpMethodDescribe(value = "导出信息 servlet/export/abnormaldetails/{year}/{month}/stream", response = WrapOutAttendanceImportFileInfo.class)
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		boolean streamContentType = false;
		request.setCharacterEncoding("UTF-8");
		try {
			EffectivePerson effectivePerson = FileUploadServletTools.effectivePerson(request);
			logger.debug("[ExportAbnormalCaseDetailsServlet]用户" + effectivePerson.getName() + "尝试下载数据文件.....");
			String part = FileUploadServletTools.getURIPart(request.getRequestURI(), "abnormaldetails");
			logger.debug("[ExportAbnormalCaseDetailsServlet]截取URL, part=[" + part + "]");
			// 从URI里截取需要的信息
			String year = StringUtils.substringBefore(part, "/"); // year
			logger.debug("[ExportAbnormalCaseDetailsServlet]从URL中获取year, year=[" + year + "]");
			part = StringUtils.substringAfter(part, "/"); // {month}/stream
			logger.debug("[ExportAbnormalCaseDetailsServlet]从URL中获取part, part=[" + part + "]");
			String month = StringUtils.substringBefore(part, "/"); // year
			logger.debug("[ExportAbnormalCaseDetailsServlet]从URL中获取month, month=[" + month + "]");
			part = StringUtils.substringAfter(part, "/"); // {month}/stream
			logger.debug("[ExportAbnormalCaseDetailsServlet]从URL中获取part, part=[" + part + "]");
			streamContentType = StringUtils.endsWith(part, "stream");
			logger.info("[downloadFile]用application/octet-stream输出，streamContentType=" + streamContentType);

			List<String> ids = null;
			List<AttendanceDetail> detailList = null;
			// 先组织一个EXCEL
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				Business business = new Business(emc);
				ids = business.getAttendanceDetailFactory().getDetailsWithAllAbnormalCase(year, month);
				detailList = business.getAttendanceDetailFactory().list(ids);
			} catch (Exception e) {
				logger.error("系统在查询所有[" + year + "年" + month + "月]非正常打卡记录时发生异常。", e);
			}
			// 将结果组织成EXCEL
			String fileName = "" + year + "年" + month + "月非正常打卡记录.xls";
			Workbook wb = new HSSFWorkbook();
			AttendanceDetail attendanceDetail = null;
			AttendanceAppealInfo attendanceAppealInfo = null;
			if (detailList != null && detailList.size() > 0) {
				// 创建新的表格
				Sheet sheet = wb.createSheet(year + "年" + month + "月");
				Row row = null;

				// 先创建表头
				row = sheet.createRow(0);
				row.createCell(0).setCellValue("公司名称");
				row.createCell(1).setCellValue("部门名称");
				row.createCell(2).setCellValue("员工姓名");
				row.createCell(3).setCellValue("打卡日期");
				row.createCell(4).setCellValue("异常原因");
				row.createCell(5).setCellValue("签到时间");
				row.createCell(6).setCellValue("签退时间");
				row.createCell(7).setCellValue("申诉原因");
				row.createCell(8).setCellValue("申诉具体原因");
				row.createCell(9).setCellValue("直接主管审批");

				for (int i = 0; i < detailList.size(); i++) {
					attendanceDetail = detailList.get(i);
					row = sheet.createRow(i + 1);
					row.createCell(0).setCellValue(attendanceDetail.getCompanyName());
					row.createCell(1).setCellValue(attendanceDetail.getDepartmentName());
					row.createCell(2).setCellValue(attendanceDetail.getEmpName());
					row.createCell(3).setCellValue(attendanceDetail.getRecordDateString());
					if (attendanceDetail.getIsAbsent()) {
						row.createCell(4).setCellValue("缺勤");
					} else if (attendanceDetail.getIsLackOfTime()) {
						row.createCell(4).setCellValue("工时不足");
					} else if (attendanceDetail.getIsAbnormalDuty()) {
						row.createCell(4).setCellValue("异常打卡");
					} else if (attendanceDetail.getIsLate()) {
						row.createCell(4).setCellValue("迟到");
					} else {
						row.createCell(4).setCellValue("未知原因");
					}
					row.createCell(5).setCellValue(attendanceDetail.getOnDutyTime());
					row.createCell(6).setCellValue(attendanceDetail.getOffDutyTime());
					row.createCell(7).setCellValue(attendanceDetail.getAppealReason());
					if (attendanceDetail.getAppealStatus() != 0) {
						// 查询该条打卡信息的申诉信息
						try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
							Business business = new Business(emc);
							attendanceAppealInfo = business.getAttendanceAppealInfoFactory()
									.get(attendanceDetail.getId());
						} catch (Exception e) {
							logger.error("系统在查询所有[" + year + "年" + month + "月]非正常打卡记录时发生异常。", e);
						}
						if (attendanceAppealInfo != null) {
							row.createCell(8).setCellValue(attendanceAppealInfo.getAppealDescription());
							if (attendanceAppealInfo.getStatus() == 0) {
								row.createCell(9).setCellValue("未审批");
							} else if (attendanceAppealInfo.getStatus() == -1) {
								row.createCell(9).setCellValue("已审批未通过");
							}
						}
					}
				}
			}
			// 向浏览器输出文件流
			OutputStream output = response.getOutputStream();
			this.setResponseHeader(response, streamContentType, fileName);
			wb.write(output);
			output.flush();
			output.close();
		} catch (Exception e) {
			logger.error("[ExportAbnormalCaseDetailsServlet]用户下载数据文件发生未知异常！", e);
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
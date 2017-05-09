package com.x.attendance.assemble.control.servlet.download;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import com.x.attendance.assemble.common.date.DateOperation;
import com.x.attendance.assemble.control.Business;
import com.x.attendance.assemble.control.jaxrs.attendanceimportfileinfo.WrapOutAttendanceImportFileInfo;
import com.x.attendance.entity.AttendanceSelfHoliday;
import com.x.base.core.application.servlet.AbstractServletAction;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.annotation.HttpMethodDescribe;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.base.core.project.server.Config;

@WebServlet(urlPatterns = "/servlet/export/selfholiday/*")
public class ExportSelfHolidayDetailsServlet extends AbstractServletAction {

	private static final long serialVersionUID = -4314532091497625540L;
	private Logger logger = LoggerFactory.getLogger(ExportSelfHolidayDetailsServlet.class);

	@SuppressWarnings("resource")
	@HttpMethodDescribe(value = "导出信息 servlet/export/selfholiday/{startdate}/{enddate}/stream", response = WrapOutAttendanceImportFileInfo.class)
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		ActionResult<Object> result = null;
		Boolean streamContentType = true;
		OutputStream output = null;
		Date startDate = null;
		Date endDate = null;
		request.setCharacterEncoding("UTF-8");
		DateOperation dateOperation = new DateOperation();
		try {
			EffectivePerson effectivePerson = this.effectivePerson(request);
			String startdate = this.getURIPart( request.getRequestURI(), "selfholiday" );
			String enddate = this.getURIPart( request.getRequestURI(), startdate );
			List<String> ids = null;
			List<AttendanceSelfHoliday> detailList = null;
			// 先组织一个EXCEL
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				Business business = new Business(emc);

				startDate = dateOperation.getDateFromString( startdate + " 00:00:00");
				endDate = dateOperation.getDateFromString( enddate + " 23:59:59");
				
				if( endDate == null ){
					endDate = dateOperation.getLastDateInMonth( new Date() );
					enddate = dateOperation.getDateStringFromDate( endDate, "yyyy-MM-dd");
					logger.info("[ExportSelfHolidayDetailsServlet]从URL中未获取到正确的endDate, endDate=[" + endDate + "]");
				}
				
				if( startDate == null ){
					startDate = dateOperation.getFirstDateInMonth( new Date() );
					startdate = dateOperation.getDateStringFromDate( startDate, "yyyy-MM-dd");
					logger.info("[ExportSelfHolidayDetailsServlet]从URL中未获取到正确的startDate, startDate=[" + startDate + "]");
				}
				
				if( startDate.after( endDate ) ){
					startDate = dateOperation.getFirstDateInMonth( new Date() );
					startdate = dateOperation.getDateStringFromDate( startDate, "yyyy-MM-dd");
					logger.info("[ExportSelfHolidayDetailsServlet]开始时间不可以晚于结束时间, startDate=[" + startDate + "]");
				}
				
				ids = business.getAttendanceSelfHolidayFactory().listByStartDateAndEndDate( startDate, endDate );
				
				if( ids != null && !ids.isEmpty() ){
					detailList = business.getAttendanceSelfHolidayFactory().list(ids);
				}
				
			} catch (Exception e) {
				logger.info("系统在查询所有[" + startdate + "] 到 [" + enddate + "]的所有请假记录时发生异常。");
				e.printStackTrace();
			}
			// 将结果组织成EXCEL
			String fileName = "" + startdate + "至" + enddate + "月请假记录.xls";
			
			AttendanceSelfHoliday attendanceSelfHoliday = null;
			Workbook wb = new HSSFWorkbook();
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
			
			if ( detailList != null && detailList.size() > 0 ) {
				
				logger.info("[" + startdate + "] 到 [" + enddate + "]一共有"+detailList.size()+"条请求记录可以输出。");
				
				for (int i = 0; i < detailList.size(); i++) {
					attendanceSelfHoliday = detailList.get(i);
					if( attendanceSelfHoliday != null ){
						row = sheet.createRow(i + 1);
						row.createCell(0).setCellValue(attendanceSelfHoliday.getCompanyName());
						row.createCell(1).setCellValue(attendanceSelfHoliday.getOrganizationName());
						row.createCell(2).setCellValue(attendanceSelfHoliday.getEmployeeName());
						row.createCell(3).setCellValue(attendanceSelfHoliday.getLeaveType());
						
						if( attendanceSelfHoliday.getStartTime() != null ){
							row.createCell(4).setCellValue( dateOperation.getDateStringFromDate( attendanceSelfHoliday.getStartTime(), "yyyy-MM-dd HH:mm:ss") );
						}else{
							row.createCell(4).setCellValue("");
						}
						if( attendanceSelfHoliday.getEndTime() != null ){
							row.createCell(5).setCellValue( dateOperation.getDateStringFromDate( attendanceSelfHoliday.getEndTime(), "yyyy-MM-dd HH:mm:ss") );
						}else{
							row.createCell(5).setCellValue("");
						}
						if( attendanceSelfHoliday.getLeaveDayNumber() != null ){
							row.createCell(6).setCellValue(attendanceSelfHoliday.getLeaveDayNumber());
						}else{
							row.createCell(6).setCellValue( "0" );
						}
						if( attendanceSelfHoliday.getDescription() != null ){
							row.createCell(7).setCellValue(attendanceSelfHoliday.getDescription());
						}
					}
				}
			}
			
			// 向浏览器输出文件流
			output = response.getOutputStream();
			setResponseHeader( response, streamContentType, fileName );
			wb.write( output );
			output.flush();
			
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("[ExportSelfHolidayDetailsServlet]用户下载请假记录文件发生未知异常！");
			result = new ActionResult<>();
			result.error(e);
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			result( response, result );
		} finally{
			if( output != null ){
				output.close();
			}
		}
	}

	private void setResponseHeader(HttpServletResponse response, Boolean streamContentType, String fileName)
			throws Exception {
		if (streamContentType) {
			response.setHeader("Content-Type", "application/octet-stream");
			response.setHeader("Content-Disposition", "fileInfo; filename=" + URLEncoder.encode(fileName, "utf-8"));
		} else {
			response.setHeader("Content-Type", Config.mimeTypes().getMimeByExtension(fileName));
			response.setHeader("Content-Disposition", "inline; filename=" + URLEncoder.encode(fileName, "utf-8"));
		}
	}
}
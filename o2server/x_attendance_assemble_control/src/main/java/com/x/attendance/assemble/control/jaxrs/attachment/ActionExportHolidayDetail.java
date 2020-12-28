package com.x.attendance.assemble.control.jaxrs.attachment;

import java.io.ByteArrayOutputStream;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import com.x.attendance.assemble.control.Business;
import com.x.attendance.entity.AttendanceSelfHoliday;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoFile;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;

/**
 * 导入的文件没有用到文件存储器，是直接放在数据库中的BLOB列
 *
 */
public class ActionExportHolidayDetail extends BaseAction {
	
	private static  Logger logger = LoggerFactory.getLogger(ActionExportHolidayDetail.class);
	
	protected ActionResult<Wo> execute( HttpServletRequest request, EffectivePerson effectivePerson, String startdate, String enddate, Boolean stream ) throws Exception {
			ActionResult<Wo> result = new ActionResult<>();
			List<String> ids = null;
			List<AttendanceSelfHoliday> holidayList = null;
			Workbook wb = null;
			Wo wo = null;
			String fileName = null;
			String sheetName = null;
			Date startDate = null;
			Date endDate = null;
			Boolean check = true;
			
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
			
			if( check ) {
				// 先获取需要导出的数据
				try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
					Business business = new Business(emc);
					ids = business.getAttendanceSelfHolidayFactory().listByStartDateAndEndDate( startDate, endDate );					
					if( ListTools.isNotEmpty( ids ) ){
						holidayList = business.getAttendanceSelfHolidayFactory().list(ids);
					}
				} catch (Exception e) {
					logger.warn("系统在查询所有[" + startdate + "] 到 [" + enddate + "]的所有请假记录时发生异常。");
					e.printStackTrace();
				}
			}
			
			// 将结果组织成EXCEL		
			if( check ) {
				fileName = "" + startdate + "至" + enddate + "月请假记录.xls";
				sheetName = "请假记录";
				wb = composeDetail( fileName, sheetName, holidayList );
			}
			
			//输出数据信息
			if( check ) {
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				try {
				    wb.write(bos);
				    wo = new Wo(bos.toByteArray(), 
							this.contentType(stream, fileName), 
							this.contentDisposition(stream, fileName));
				} finally {
				    bos.close();
				}
			}		
			result.setData(wo);
			return result;
		}

		private Workbook composeDetail(String fileName, String sheetName, List<AttendanceSelfHoliday> holidayList) throws Exception {
			AttendanceSelfHoliday attendanceSelfHoliday = null;
			
			Workbook wb = new HSSFWorkbook();
			Row row = null;
			if (holidayList != null && holidayList.size() > 0) {
				// 创建新的表格
				Sheet sheet = wb.createSheet(sheetName);
				
				// 先创建表头
				row = sheet.createRow(0);
				row.createCell(0).setCellValue("顶层组织名称");
				row.createCell(1).setCellValue("组织名称");
				row.createCell(2).setCellValue("员工姓名");
				row.createCell(3).setCellValue("请假类型");
				row.createCell(4).setCellValue("开始时间");
				row.createCell(5).setCellValue("结束时间");
				row.createCell(6).setCellValue("请假天数");
				row.createCell(7).setCellValue("其他说明");

				if ( holidayList != null && holidayList.size() > 0 ) {
					logger.info("一共有"+holidayList.size()+"条请求记录可以输出。");					
					for (int i = 0; i < holidayList.size(); i++) {
						attendanceSelfHoliday = holidayList.get(i);
						if( attendanceSelfHoliday != null ){
							row = sheet.createRow(i + 1);
							String empName = attendanceSelfHoliday.getEmployeeName();
							if(StringUtils.isNotEmpty(empName) && StringUtils.contains(empName,"@")){
								empName = empName.split("@")[0];
							}
							row.createCell(0).setCellValue(attendanceSelfHoliday.getTopUnitName());
							row.createCell(1).setCellValue(attendanceSelfHoliday.getUnitName());
							row.createCell(2).setCellValue(empName);
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
			}
			return wb;
		}

		public static class Wo extends WoFile {
			public Wo(byte[] bytes, String contentType, String contentDisposition) {
				super(bytes, contentType, contentDisposition);
			}
		}
	}

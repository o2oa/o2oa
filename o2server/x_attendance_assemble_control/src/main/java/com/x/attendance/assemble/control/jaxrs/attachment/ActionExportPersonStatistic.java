package com.x.attendance.assemble.control.jaxrs.attachment;

import java.io.ByteArrayOutputStream;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import com.x.attendance.entity.StatisticPersonForMonth;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoFile;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;

public class ActionExportPersonStatistic extends BaseAction {
	
	private static  Logger logger = LoggerFactory.getLogger(ActionExportPersonStatistic.class);
	
	protected ActionResult<Wo> execute( HttpServletRequest request, EffectivePerson effectivePerson, String name, String year, String month ,Boolean stream ) throws Exception {
			ActionResult<Wo> result = new ActionResult<>();
			List<String> ids = null;
			List<StatisticPersonForMonth> statisticPersonForMonth_list = null;
			Workbook wb = null;
			Wo wo = null;
			String fileName = null;
			String sheetName = null;
			Boolean check = true;

			if ("(0)".equals(year)) {
				year = null;
			}
			if ("(0)".equals(month)) {
				month = null;
			}
			if( check ){
				if( name == null || name.isEmpty() ){
					check = false;
					Exception exception = new ExceptionPersonNameEmpty();
					result.error( exception );
				}
			}

			if( check ){
				try {
					ids = attendanceStatisticServiceAdv.listPersonForMonthByUserYearAndMonth(name, year, month);
				} catch (Exception e) {
					check = false;
					Exception exception = new ExceptionAttendanceStatisticProcess( e,
							"系统根据人员姓名，年份和月份查询统计数据信息ID列表时发生异常.Name:"+name+", Year:"+year+", Month:" + month
					);
					result.error( exception );
					logger.error( e, effectivePerson, request, null);
				}
			}
			if( check ){
				if( ids != null && !ids.isEmpty() ){
					try {
						statisticPersonForMonth_list = attendanceStatisticServiceAdv.listPersonForMonth(ids);
					} catch (Exception e) {
						check = false;
						Exception exception = new ExceptionAttendanceStatisticProcess( e, "系统根据ID列表查询个人每月统计数据信息列表时发生异常." );
						result.error( exception );
						logger.error( e, effectivePerson, request, null);
					}
				}
			}
			
			// 将结果组织成EXCEL		
			if( check ) {
				if(StringUtils.isNotEmpty(name) && StringUtils.contains(name,"@")){
					fileName = "" + name.split("@")[0] + "的个人出勤率统计记录_"+year+"年"+month+"月.xls";
				}else{
					fileName = "" + name + "的个人出勤率统计记录_"+year+"年"+month+"月.xls";
				}
				sheetName = "个人出勤率统计记录";
				wb = composeDetail( fileName, sheetName, statisticPersonForMonth_list );
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

		private Workbook composeDetail(String fileName, String sheetName, List<StatisticPersonForMonth> statisticPersonForMonth_list) throws Exception {
			Workbook wb = new HSSFWorkbook();
			Row row = null;
			if (ListTools.isNotEmpty(statisticPersonForMonth_list)) {
				// 创建新的表格
				Sheet sheet = wb.createSheet(sheetName);
				// 先创建表头
				row = sheet.createRow(0);
				row.createCell(0).setCellValue("顶层组织名称");
				row.createCell(1).setCellValue("组织名称");
				row.createCell(2).setCellValue("姓名");
				row.createCell(3).setCellValue("月份");
				row.createCell(4).setCellValue("上班打卡次数");
				row.createCell(5).setCellValue("下班打卡次数");
				row.createCell(6).setCellValue("出勤人天数");
				row.createCell(7).setCellValue("请假或外出报备人天数");
				row.createCell(8).setCellValue("缺勤人天数");
				row.createCell(9).setCellValue("迟到次数");
				row.createCell(10).setCellValue("工时不足人次");
				row.createCell(11).setCellValue("异常打卡人次");

				logger.info("一共有"+statisticPersonForMonth_list.size()+"条请求记录可以输出。");
				for (int i = 0; i < statisticPersonForMonth_list.size(); i++) {
					StatisticPersonForMonth statisticPersonForMonth = null;
					statisticPersonForMonth = statisticPersonForMonth_list.get(i);
					if( statisticPersonForMonth != null ){
						row = sheet.createRow(i + 1);
						String topUnitName = statisticPersonForMonth.getTopUnitName();
						String unitName = statisticPersonForMonth.getUnitName();
						String empName = statisticPersonForMonth.getEmployeeName();
						if(StringUtils.isNotEmpty(topUnitName) && StringUtils.contains(topUnitName,"@")){
							topUnitName = topUnitName.split("@")[0];
						}
						if(StringUtils.isNotEmpty(unitName) && StringUtils.contains(unitName,"@")){
							unitName = unitName.split("@")[0];
						}
						if(StringUtils.isNotEmpty(empName) && StringUtils.contains(empName,"@")){
							empName = empName.split("@")[0];
						}
						row.createCell(0).setCellValue(topUnitName);
						row.createCell(1).setCellValue(unitName);
						row.createCell(2).setCellValue(empName);
						row.createCell(3).setCellValue(statisticPersonForMonth.getStatisticYear()+"-"+statisticPersonForMonth.getStatisticMonth());
						row.createCell(4).setCellValue(statisticPersonForMonth.getOnDutyTimes());
						row.createCell(5).setCellValue(statisticPersonForMonth.getOnDutyTimes());
						row.createCell(6).setCellValue(statisticPersonForMonth.getOnDutyDayCount());
						row.createCell(7).setCellValue(statisticPersonForMonth.getOnSelfHolidayCount());
						row.createCell(8).setCellValue(statisticPersonForMonth.getAbsenceDayCount());
						row.createCell(9).setCellValue(statisticPersonForMonth.getLateTimes());
						row.createCell(10).setCellValue(statisticPersonForMonth.getLackOfTimeCount());
						row.createCell(11).setCellValue(statisticPersonForMonth.getAbNormalDutyCount());
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

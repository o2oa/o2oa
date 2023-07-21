package com.x.attendance.assemble.control.jaxrs.attachment;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import com.x.attendance.entity.StatisticUnitForMonth;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoFile;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;

public class ActionExportTopUnitStatistic extends BaseAction {
	
	private static  Logger logger = LoggerFactory.getLogger(ActionExportTopUnitStatistic.class);
	
	protected ActionResult<Wo> execute( HttpServletRequest request, EffectivePerson effectivePerson, String name, String year, String month ,Boolean stream ) throws Exception {
			ActionResult<Wo> result = new ActionResult<>();
			List<String> ids = null;
			List<String> unitNames = new ArrayList<String>();
			List<StatisticUnitForMonth> statisticUnitForMonth_list = null;
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
					Exception exception = new ExceptionTopUnitNameEmpty();
					result.error( exception );
				}
			}
			if( check ){
				try {
					//根据顶层组织递归查询下级顶层组织经及顶层组织的顶级组织
					unitNames = getTopUnitNameList( name, unitNames, effectivePerson.getDebugger() );
				} catch (Exception e) {
					check = false;
					Exception exception = new ExceptionAttendanceStatisticProcess( e,
							"根据顶层组织递归查询下级顶层组织经及顶层组织的顶级组织时发生异常！"
									+ "Name:" + name
									+ ", Units:" + unitNames );
					result.error( exception );
					logger.error( e, effectivePerson, request, null);
				}
			}
			if( check ){
				if( unitNames == null ){
					unitNames = new ArrayList<>();
				}
				unitNames.add( name );
			}
			if( check ){
				try {
					ids = attendanceStatisticServiceAdv.listUnitForMonthByUnitYearAndMonth( unitNames, year, month);
				} catch (Exception e) {
					check = false;
					Exception exception = new ExceptionAttendanceStatisticProcess(e,
							"系统根据组织名称列表，年份和月份查询组织统计数据信息ID列表时发生异常.Name:"+unitNames+", Year:"+year+", Month:" + month
					);
					result.error( exception );
					logger.error( e, effectivePerson, request, null);
				}
			}
			if( check ){
				if( ids != null && !ids.isEmpty() ){
					try {
						statisticUnitForMonth_list = attendanceStatisticServiceAdv.listUnitForMonth( ids );
					} catch (Exception e) {
						check = false;
						Exception exception = new ExceptionAttendanceStatisticProcess( e, "系统根据ID列表查询组织每月统计数据信息列表时发生异常." );
						result.error( exception );
						logger.error( e, effectivePerson, request, null);
					}
				}
			}
			
			// 将结果组织成EXCEL		
			if( check ) {
				if(StringUtils.isNotEmpty(name) && StringUtils.contains(name,"@")){
					fileName = "" + name.split("@")[0] + "的出勤率统计记录_"+year+"年"+month+"月.xls";
				}else{
					fileName = "" + name + "的出勤率统计记录_"+year+"年"+month+"月.xls";
				}
				sheetName = "公司出勤率统计记录";
				wb = composeDetail( fileName, sheetName, statisticUnitForMonth_list );
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

		private Workbook composeDetail(String fileName, String sheetName, List<StatisticUnitForMonth> statisticUnitForMonth_list) throws Exception {
			Workbook wb = new HSSFWorkbook();
			Row row = null;
			if (ListTools.isNotEmpty(statisticUnitForMonth_list)) {
				// 创建新的表格
				Sheet sheet = wb.createSheet(sheetName);
				// 先创建表头
				row = sheet.createRow(0);
				row.createCell(0).setCellValue("公司");
				row.createCell(1).setCellValue("部门");
				row.createCell(2).setCellValue("月份");
				row.createCell(3).setCellValue("上班打卡次数");
				row.createCell(4).setCellValue("下班打卡次数");
				row.createCell(5).setCellValue("出勤人天数");
				row.createCell(6).setCellValue("请假或外出报备人天数");
				row.createCell(7).setCellValue("缺勤人天数");
				row.createCell(8).setCellValue("迟到次数");
				row.createCell(9).setCellValue("工时不足人次");
				row.createCell(10).setCellValue("异常打卡人次");

				logger.info("一共有"+statisticUnitForMonth_list.size()+"条请求记录可以输出。");
				for (int i = 0; i < statisticUnitForMonth_list.size(); i++) {
					StatisticUnitForMonth statisticUnitForMonth = null;
					statisticUnitForMonth = statisticUnitForMonth_list.get(i);
					if( statisticUnitForMonth != null ){
						row = sheet.createRow(i + 1);
						String topUnitName = statisticUnitForMonth.getTopUnitName();
						if(StringUtils.isNotEmpty(topUnitName) && StringUtils.contains(topUnitName,"@")){
							topUnitName = topUnitName.split("@")[0];
						}
						row.createCell(0).setCellValue(topUnitName);
						String unitName = statisticUnitForMonth.getUnitName();
						if(StringUtils.isNotEmpty(unitName) && StringUtils.contains(unitName,"@")){
							unitName = unitName.split("@")[0];
						}
						row.createCell(1).setCellValue(unitName);
						row.createCell(2).setCellValue(statisticUnitForMonth.getStatisticYear()+"-"+statisticUnitForMonth.getStatisticMonth());
						row.createCell(3).setCellValue(statisticUnitForMonth.getOnDutyCount());
						row.createCell(4).setCellValue(statisticUnitForMonth.getOffDutyCount());
						row.createCell(5).setCellValue(statisticUnitForMonth.getOnDutyEmployeeCount());
						row.createCell(6).setCellValue(statisticUnitForMonth.getOnSelfHolidayCount());
						row.createCell(7).setCellValue(statisticUnitForMonth.getAbsenceDayCount());
						row.createCell(8).setCellValue(statisticUnitForMonth.getLateCount());
						row.createCell(9).setCellValue(statisticUnitForMonth.getLackOfTimeCount());
						row.createCell(10).setCellValue(statisticUnitForMonth.getAbNormalDutyCount());
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

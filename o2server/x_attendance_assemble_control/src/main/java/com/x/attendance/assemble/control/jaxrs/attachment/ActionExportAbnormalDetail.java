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
import com.x.attendance.entity.AttendanceAppealInfo;
import com.x.attendance.entity.AttendanceDetail;
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
 */
public class ActionExportAbnormalDetail extends BaseAction {
	
	private static  Logger logger = LoggerFactory.getLogger(ActionExportAbnormalDetail.class);
	
	protected ActionResult<Wo> execute( HttpServletRequest request, EffectivePerson effectivePerson, String year, String month, Boolean stream ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		List<String> ids = null;
		List<AttendanceDetail> detailList = null;
		Workbook wb = null;
		Wo wo = null;
		String fileName = null;
		String sheetName = null;
		Boolean check = true;
		
		if( year == null || year.isEmpty() ){
			year = dateOperation.getYear( new Date() );
		}
		
		if( month == null || month.isEmpty() ){
			month = dateOperation.getMonth( new Date() );
		}
		
		if( check ) {
			// 先获取需要导出的数据
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				Business business = new Business(emc);
				ids = business.getAttendanceDetailFactory().getDetailsWithAllAbnormalCase(year, month);
				detailList = business.getAttendanceDetailFactory().list(ids);
			} catch (Exception e) {
				logger.info("系统在查询所有[" + year + "年" + month + "月]非正常打卡记录时发生异常。" );
				e.printStackTrace();
			}
		}
		
		// 将结果组织成EXCEL		
		if( check ) {
			fileName = "" + year + "年" + month + "月非正常打卡记录.xls";
			sheetName = year + "年" + month + "月";
			wb = composeDetail( fileName, sheetName, detailList );
		}
		
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

	private Workbook composeDetail(String fileName, String sheetName, List<AttendanceDetail> detailList) {
		AttendanceDetail attendanceDetail = null;
		AttendanceAppealInfo attendanceAppealInfo = null;
		
		Workbook wb = new HSSFWorkbook();
		Row row = null;
		if (ListTools.isNotEmpty(detailList) ) {
			// 创建新的表格
			Sheet sheet = wb.createSheet(sheetName);
			
			// 先创建表头
			row = sheet.createRow(0);
			row.createCell(0).setCellValue("顶层组织名称");
			row.createCell(1).setCellValue("组织名称");
			row.createCell(2).setCellValue("员工姓名");
			row.createCell(3).setCellValue("打卡日期");
			row.createCell(4).setCellValue("异常原因");
			row.createCell(5).setCellValue("上午上班打卡时间");
			row.createCell(6).setCellValue("上午下班打卡时间");
			row.createCell(7).setCellValue("下午上班打卡时间");
			row.createCell(8).setCellValue("下午下班打开时间");
			row.createCell(9).setCellValue("申诉原因");
			row.createCell(10).setCellValue("申诉具体原因");
			row.createCell(11).setCellValue("直接主管审批");

			for (int i = 0; i < detailList.size(); i++) {
				attendanceDetail = detailList.get(i);
				row = sheet.createRow(i + 1);
				String topUnitName = attendanceDetail.getTopUnitName();
				String unitName = attendanceDetail.getUnitName();
				String empName = attendanceDetail.getEmpName();
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
				row.createCell(3).setCellValue(attendanceDetail.getRecordDateString());
				if (attendanceDetail.getIsAbsent()) {
					row.createCell(4).setCellValue("缺勤");
				} else if (attendanceDetail.getIsLackOfTime()) {
					row.createCell(4).setCellValue("工时不足");
				} else if (attendanceDetail.getIsAbnormalDuty()) {
					row.createCell(4).setCellValue("异常打卡");
				}else if(attendanceDetail.getIsLeaveEarlier()){
					row.createCell(4).setCellValue("早退");
				} else if (attendanceDetail.getIsLate()) {
					row.createCell(4).setCellValue("迟到");
				} else {
					row.createCell(4).setCellValue("未知原因");
				}
				row.createCell(5).setCellValue(attendanceDetail.getOnDutyTime());
				row.createCell(6).setCellValue(attendanceDetail.getMorningOffDutyTime());
				row.createCell(7).setCellValue(attendanceDetail.getAfternoonOnDutyTime());
				row.createCell(8).setCellValue(attendanceDetail.getOffDutyTime());
				row.createCell(9).setCellValue(attendanceDetail.getAppealReason());
				if (attendanceDetail.getAppealStatus() != 0) {
					// 查询该条打卡信息的申诉信息
					try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
						Business business = new Business(emc);
						attendanceAppealInfo = business.getAttendanceAppealInfoFactory().get(attendanceDetail.getId());
					} catch (Exception e) {
						logger.info("系统在查询所有["+sheetName+"]非正常打卡记录时发生异常。" );
						e.printStackTrace();
					}
					if (attendanceAppealInfo != null) {
						row.createCell(10).setCellValue(attendanceAppealInfo.getAppealDescription());
						if (attendanceAppealInfo.getStatus() == 0) {
							row.createCell(11).setCellValue("未审批");
						} else if (attendanceAppealInfo.getStatus() == -1) {
							row.createCell(11).setCellValue("已审批未通过");
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

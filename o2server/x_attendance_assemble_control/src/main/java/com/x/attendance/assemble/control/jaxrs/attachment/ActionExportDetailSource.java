package com.x.attendance.assemble.control.jaxrs.attachment;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import com.x.attendance.assemble.control.Business;
import com.x.attendance.assemble.control.jaxrs.attendancedetail.WrapInFilter;
import com.x.attendance.assemble.control.service.AttendanceEmployeeConfigServiceAdv;
import com.x.attendance.assemble.control.service.UserManagerService;
import com.x.attendance.entity.AttendanceAppealInfo;
import com.x.attendance.entity.AttendanceDetail;
import com.x.attendance.entity.AttendanceEmployeeConfig;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoFile;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.DateTools;
import com.x.base.core.project.tools.ListTools;

/**
 * 导出原始打卡记录
 */
public class ActionExportDetailSource extends BaseAction {
	
	private static  Logger logger = LoggerFactory.getLogger(ActionExportDetailSource.class);
	private UserManagerService userManagerService = new UserManagerService();
	protected AttendanceEmployeeConfigServiceAdv attendanceEmployeeConfigServiceAdv = new AttendanceEmployeeConfigServiceAdv();
	
	protected ActionResult<Wo> execute( HttpServletRequest request, EffectivePerson effectivePerson,
										String cycleYear,
										String cycleMonth,
										Boolean stream) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		EffectivePerson currentPerson = this.effectivePerson(request);
		List<AttendanceDetail> detailList = null;
		List<AttendanceDetail> sourceList =  new ArrayList<>();
		Workbook wb = null;
		Wo wo = null;
		String fileName = null;
		String sheetName = null;
		Boolean check = true;
		WrapInFilter wrapIn = new WrapInFilter();

		List<String> unUnitNameList = new ArrayList<String>();
		List<String> personNameList = new ArrayList<String>();

		try {
			if(StringUtils.isNotEmpty(cycleYear)){
				wrapIn.setCycleYear(cycleYear);
			}
			if(StringUtils.isNotEmpty(cycleMonth)){
				wrapIn.setCycleMonth(cycleMonth);
			}
		} catch (Exception e) {
			check = false;
			logger.error(e, currentPerson, request, null);
		}
		if (check ) {
			unUnitNameList = getUnUnitNameList();
			personNameList = getUnPersonNameList();
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				// 从数据库中查询符合条件的一页数据对象
				Business business = new Business(emc);
				/*ids = business.getAttendanceDetailFactory().listIdsWithFilterUn(wrapIn,unUnitNameList,personNameList);
				detailList = business.getAttendanceDetailFactory().list(ids);*/
				detailList = business.getAttendanceDetailFactory().listIdsWithFilterUn(wrapIn,unUnitNameList,personNameList);
				logger.info("detailList======"+detailList.size() );
				for(AttendanceDetail source:detailList){
					if(!StringUtils.equals("系统补充",source.getBatchName())){
						sourceList.add(source);
					}
				}
				logger.info("sourceList======"+sourceList.size() );
			}catch (Exception e) {
				logger.info("系统在查询符合条件的打卡记录时发生异常。" );
				e.printStackTrace();
			}

		}
		// 将结果组织成EXCEL		
		if( check ) {
			fileName = "原始打卡记录_"+ DateTools.formatDate(new Date())+".xls";
			sheetName = "Sheet1";
			wb = composeDetail( fileName, sheetName, sourceList );
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
			row.createCell(0).setCellValue("姓名");
			row.createCell(1).setCellValue("员工号");
			row.createCell(2).setCellValue("日期");
			row.createCell(3).setCellValue("上午上班打卡时间");
			row.createCell(4).setCellValue("上午下班打卡时间");
			row.createCell(5).setCellValue("下午上班打卡时间");
			row.createCell(6).setCellValue("下午下班打开时间");

			for (int i = 0; i < detailList.size(); i++) {
				attendanceDetail = detailList.get(i);
				row = sheet.createRow(i + 1);
				String empName = attendanceDetail.getEmpName();
				if(StringUtils.isNotEmpty(empName) && StringUtils.contains(empName,"@")){
					empName = empName.split("@")[0];
				}
				String recordDate = attendanceDetail.getRecordDateString();
				String onDutyTime = attendanceDetail.getOnDutyTime();
				if(StringUtils.isNotEmpty(onDutyTime)){
					onDutyTime = onDutyTime.substring(0, onDutyTime.length() - 3);
				}
				String morningOffDutyTime = attendanceDetail.getMorningOffDutyTime();
				if(StringUtils.isNotEmpty(morningOffDutyTime)){
					morningOffDutyTime = morningOffDutyTime.substring(0, morningOffDutyTime.length() - 3);
				}
				String afternoonOnDutyTime = attendanceDetail.getAfternoonOnDutyTime();
				if(StringUtils.isNotEmpty(afternoonOnDutyTime)){
					afternoonOnDutyTime = afternoonOnDutyTime.substring(0, afternoonOnDutyTime.length() - 3);
				}
				String offDutyTime = attendanceDetail.getOffDutyTime();
				if(StringUtils.isNotEmpty(offDutyTime)){
					offDutyTime = offDutyTime.substring(0, offDutyTime.length() - 3);
				}

				row.createCell(0).setCellValue(attendanceDetail.getEmpName());
				row.createCell(1).setCellValue(attendanceDetail.getEmpNo());
				row.createCell(2).setCellValue(recordDate.replaceAll("-","/"));
				row.createCell(3).setCellValue(onDutyTime);
				row.createCell(4).setCellValue(morningOffDutyTime);
				row.createCell(5).setCellValue(afternoonOnDutyTime);
				row.createCell(6).setCellValue(offDutyTime);
			}
		}
		return wb;
	}

	/**
	 * 获取不需要考勤的组织
	 * @return
	 * @throws Exception
	 */
	protected  List<String> getUnUnitNameList() throws Exception {
		List<String> unUnitNameList = new ArrayList<String>();

		List<AttendanceEmployeeConfig> attendanceEmployeeConfigs = attendanceEmployeeConfigServiceAdv.listByConfigType("NOTREQUIRED");

		if(ListTools.isNotEmpty(attendanceEmployeeConfigs)){
			for (AttendanceEmployeeConfig attendanceEmployeeConfig : attendanceEmployeeConfigs) {
				String unitName = attendanceEmployeeConfig.getUnitName();
				String employeeName = attendanceEmployeeConfig.getEmployeeName();

				if(StringUtils.isEmpty(employeeName) && StringUtils.isNotEmpty(unitName)){
					unUnitNameList.add(unitName);
					List<String> tempUnitNameList = userManagerService.listSubUnitNameWithParent(unitName);
					if(ListTools.isNotEmpty(tempUnitNameList)){
						for(String tempUnit:tempUnitNameList){
							if(!ListTools.contains(unUnitNameList, tempUnit)){
								unUnitNameList.add(tempUnit);
							}
						}
					}
				}
			}
		}
		return unUnitNameList;
	}

	/**
	 * 获取不需要考勤的人员
	 * @return
	 * @throws Exception
	 */
	protected  List<String> getUnPersonNameList() throws Exception {
		List<String> personNameList = new ArrayList<String>();
		List<AttendanceEmployeeConfig> attendanceEmployeeConfigs = attendanceEmployeeConfigServiceAdv.listByConfigType("NOTREQUIRED");

		if(ListTools.isNotEmpty(attendanceEmployeeConfigs)){
			for (AttendanceEmployeeConfig attendanceEmployeeConfig : attendanceEmployeeConfigs) {
				String employeeName = attendanceEmployeeConfig.getEmployeeName();

				if(StringUtils.isNotEmpty(employeeName) && !ListTools.contains(personNameList, employeeName)){
					personNameList.add(employeeName);
				}
			}
		}
		return personNameList;
	}

	public static class Wo extends WoFile {
		public Wo(byte[] bytes, String contentType, String contentDisposition) {
			super(bytes, contentType, contentDisposition);
		}
	}

}

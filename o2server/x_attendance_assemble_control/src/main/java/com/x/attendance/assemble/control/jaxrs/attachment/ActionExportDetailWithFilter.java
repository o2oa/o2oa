package com.x.attendance.assemble.control.jaxrs.attachment;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.BooleanUtils;
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
import com.x.attendance.entity.AttendanceScheduleSetting;
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
 * 导出统计明细
 */
public class ActionExportDetailWithFilter extends BaseAction {
	
	private static  Logger logger = LoggerFactory.getLogger(ActionExportDetailWithFilter.class);
	private UserManagerService userManagerService = new UserManagerService();
	protected AttendanceEmployeeConfigServiceAdv attendanceEmployeeConfigServiceAdv = new AttendanceEmployeeConfigServiceAdv();
	
	protected ActionResult<Wo> execute( HttpServletRequest request, EffectivePerson effectivePerson,
										String q_topUnitName,
										String q_unitName,
										String q_empName,
										String cycleYear,
										String cycleMonth,
										String q_date,
										String isAbsent,
										String isLackOfTime,
										String isLate,
										Boolean stream) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		EffectivePerson currentPerson = this.effectivePerson(request);
		List<String> ids = null;
		List<AttendanceDetail> detailList = null;
		Workbook wb = null;
		Wo wo = null;
		String fileName = null;
		String sheetName = null;
		Boolean check = true;
		WrapInFilter wrapIn = new WrapInFilter();

		long total = 0;
		List<String> topUnitNames = new ArrayList<String>();
		List<String> unitNames = new ArrayList<String>();
		List<String> topUnitNames_tmp = null;
		List<String> unitNames_tmp = null;
		AttendanceScheduleSetting scheduleSetting_top = null;
		AttendanceScheduleSetting scheduleSetting = null;


		List<String> unUnitNameList = new ArrayList<String>();
		List<String> personNameList = new ArrayList<String>();

		try {
			if(StringUtils.isNotEmpty(q_topUnitName) && !StringUtils.equals(q_topUnitName,"0")){
				wrapIn.setQ_topUnitName(q_topUnitName);
			}
			if(StringUtils.isNotEmpty(q_unitName)&& !StringUtils.equals(q_unitName,"0")){
				wrapIn.setQ_unitName(q_unitName);
			}
			if(StringUtils.isNotEmpty(q_empName)&& !StringUtils.equals(q_empName,"0")){
				wrapIn.setQ_empName(q_empName);
				wrapIn.setKey("recordDateString");
			}
			if(StringUtils.isNotEmpty(cycleYear)&& !StringUtils.equals(cycleYear,"0")){
				wrapIn.setCycleYear(cycleYear);
			}
			if(StringUtils.isNotEmpty(cycleMonth)&& !StringUtils.equals(cycleMonth,"0")){
				wrapIn.setCycleMonth(cycleMonth);
			}
			if(StringUtils.isNotEmpty(q_date)&& !StringUtils.equals(q_date,"0")){
				wrapIn.setQ_date(q_date);
			}
			if(!StringUtils.equals(isAbsent,"0")){
				wrapIn.setIsAbsent(BooleanUtils.toBoolean(isAbsent));
			}
			if(!StringUtils.equals(isLackOfTime,"0")){
				wrapIn.setIsLackOfTime(BooleanUtils.toBoolean(isLackOfTime));
			}
			if(!StringUtils.equals(isLate,"0")){
				wrapIn.setIsLate(BooleanUtils.toBoolean(isLate));
			}
		} catch (Exception e) {
			check = false;
			logger.error(e, currentPerson, request, null);
		}
		if(check){
				// 处理一下顶层组织，查询下级顶层组织
				if ( StringUtils.isNotEmpty( wrapIn.getQ_topUnitName() )) {
					topUnitNames.add(wrapIn.getQ_topUnitName());
					scheduleSetting_top = attendanceScheduleSettingServiceAdv.getAttendanceScheduleSettingWithUnit(wrapIn.getQ_topUnitName(), effectivePerson.getDebugger() );
					try {
						topUnitNames_tmp = userManagerService.listSubUnitNameWithParent(wrapIn.getQ_topUnitName());
					} catch (Exception e) {
						Exception exception = new ExceptionAttendanceDetailProcess(e,
								"根据顶层组织顶层组织列示所有下级组织名称发生异常！TopUnit:" + wrapIn.getQ_topUnitName());
						result.error(exception);
						logger.error(e, currentPerson, request, null);
					}
					if (topUnitNames_tmp != null && topUnitNames_tmp.size() > 0) {
						for (String topUnitName : topUnitNames_tmp) {
							topUnitNames.add(topUnitName);
						}
					}
					wrapIn.setTopUnitNames(topUnitNames);
				}

				// 处理一下组织,查询下级组织
				if ( StringUtils.isNotEmpty( wrapIn.getQ_unitName() )) {
					unitNames.add(wrapIn.getQ_unitName());
					scheduleSetting = attendanceScheduleSettingServiceAdv.getAttendanceScheduleSettingWithUnit(wrapIn.getQ_unitName(), effectivePerson.getDebugger() );
					try {
						unitNames_tmp = userManagerService.listSubUnitNameWithParent(wrapIn.getQ_unitName());
					} catch (Exception e) {
						Exception exception = new ExceptionAttendanceDetailProcess(e,
								"根据组织名称列示所有下级组织名称发生异常！Unit:" + wrapIn.getQ_unitName());
						result.error(exception);
						logger.error(e, currentPerson, request, null);
					}
					if (unitNames_tmp != null && unitNames_tmp.size() > 0) {
						for (String unitName : unitNames_tmp) {
							unitNames.add(unitName);
						}
					}
					wrapIn.setUnitNames(unitNames);
				}

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
			}catch (Exception e) {
				logger.info("系统在查询符合条件的打卡记录时发生异常。" );
				e.printStackTrace();
			}

		}
		// 将结果组织成EXCEL		
		if( check ) {
			fileName = "统计打卡记录明细_"+ DateTools.formatDate(new Date())+".xls";
			sheetName = "Sheet1";
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
			row.createCell(2).setCellValue("姓名");
			row.createCell(3).setCellValue("日期");
			row.createCell(4).setCellValue("备注");
			row.createCell(5).setCellValue("上午上班打卡时间");
			row.createCell(6).setCellValue("上午下班打卡时间");
			row.createCell(7).setCellValue("下午上班打卡时间");
			row.createCell(8).setCellValue("下午下班打开时间");
			row.createCell(9).setCellValue("考勤状态");
			row.createCell(10).setCellValue("出勤时长(分钟)");
			row.createCell(11).setCellValue("申诉状态");

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
				String descrip = "";
				if(attendanceDetail.getIsHoliday()){
					descrip = "节假日";
				}else if(attendanceDetail.getIsWeekend()){
					descrip = "周末";
				}else if(attendanceDetail.getIsGetSelfHolidays()){
					descrip = "休假";
				}else{
					descrip = "工作日";
				}
				row.createCell(4).setCellValue(descrip);
				row.createCell(5).setCellValue(attendanceDetail.getOnDutyTime());
				row.createCell(6).setCellValue(attendanceDetail.getMorningOffDutyTime());
				row.createCell(7).setCellValue(attendanceDetail.getAfternoonOnDutyTime());
				row.createCell(8).setCellValue(attendanceDetail.getOffDutyTime());

				if(attendanceDetail.getIsGetSelfHolidays()){
					if(StringUtils.isNotEmpty(attendanceDetail.getLeaveType())){
						row.createCell(9).setCellValue(attendanceDetail.getLeaveType());
					}else{
						row.createCell(9).setCellValue("请假或外出报备");
					}
				}else if (attendanceDetail.getIsAbsent()) {
					row.createCell(9).setCellValue("缺勤");
				} else if (attendanceDetail.getIsLackOfTime()) {
					row.createCell(9).setCellValue("工时不足");
				} else if (attendanceDetail.getIsAbnormalDuty()) {
					row.createCell(9).setCellValue("异常打卡");
				}else if(attendanceDetail.getIsLeaveEarlier()){
					row.createCell(9).setCellValue("早退");
				} else if (attendanceDetail.getIsLate()) {
					row.createCell(9).setCellValue("迟到");
				} else {
					row.createCell(9).setCellValue("正常");
				}
				row.createCell(10).setCellValue(attendanceDetail.getWorkTimeDuration());
				switch(attendanceDetail.getAppealStatus()){
					case 1 :
						row.createCell(11).setCellValue("申诉中");
						break;
					case -1 :
						row.createCell(11).setCellValue("申诉未通过");
						break;
					case 9 :
						row.createCell(11).setCellValue("申诉通过");
						break;
					default :
						row.createCell(11).setCellValue("未申诉");
				}
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

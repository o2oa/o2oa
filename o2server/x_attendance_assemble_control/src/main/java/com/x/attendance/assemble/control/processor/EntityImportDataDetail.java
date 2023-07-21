package com.x.attendance.assemble.control.processor;

import java.util.Date;
import java.util.List;

public class EntityImportDataDetail extends AbStractDataForOperator {
	
	private String file_id = null;
	
	/**
	 * 第几个ExcelSheet
	 */
	private int sheetIndex = 0;
	/**
	 * 当前数据行数
	 */
	private int curRow = 0;
	/**
	 * 当前数据内容
	 */
	private List<String> colmlist = null;

	private String employeeNo = "";
	
	private String employeeName = "";
	
	private Date recordDate = null;	
	
	private String recordDateString = "";	
	
	private String recordYearString = "";
	
	private String recordMonthString = "";
	
	private String onDutyTime = "";
	
	private String morningOffDutyTime = "";
	
	private String afternoonOnDutyTime = "";
	
	private String offDutyTime = "";
	
	private String onDutyTimeFormated = "";
	
	private String morningOffDutyTimeFormated = "";
	
	private String afternoonOnDutyTimeFormated = "";
	
	private String offDutyTimeFormated = "";
	
	private String recordDateStringFormated = "";
	
	private String description = "";
	
	private String checkStatus = "success";

	public EntityImportDataDetail() {}
	
	public EntityImportDataDetail( String data_type, int sheetIndex, int curRow, String fileKey, List<String> colmlist) {
		this.data_type = data_type;
		this.sheetIndex = sheetIndex;
		this.curRow = curRow;
		this.file_id = fileKey;
		this.colmlist = colmlist;
	}
	
	public String getEmployeeNo() {
		return employeeNo;
	}

	public void setEmployeeNo(String employeeNo) {
		this.employeeNo = employeeNo;
	}

	public String getEmployeeName() {
		return employeeName;
	}

	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}

	public String getRecordDateString() {
		return recordDateString;
	}

	public void setRecordDateString(String recordDateString) {
		this.recordDateString = recordDateString;
	}

	public String getOnDutyTime() {
		return onDutyTime;
	}

	public void setOnDutyTime(String onDutyTime) {
		this.onDutyTime = onDutyTime;
	}
	
	public String getMorningOffDutyTime() {
		return morningOffDutyTime;
	}

	public void setMorningOffDutyTime(String morningOffDutyTime) {
		this.morningOffDutyTime = morningOffDutyTime;
	}
	
	public String getAfternoonOnDutyTime() {
		return afternoonOnDutyTime;
	}

	public void setAfternoonOnDutyTime(String afternoonOnDutyTime) {
		this.afternoonOnDutyTime = afternoonOnDutyTime;
	}

	public String getOffDutyTime() {
		return offDutyTime;
	}

	public void setOffDutyTime(String offDutyTime) {
		this.offDutyTime = offDutyTime;
	}

	public String getOnDutyTimeFormated() {
		return onDutyTimeFormated;
	}

	public void setOnDutyTimeFormated(String onDutyTimeFormated) {
		this.onDutyTimeFormated = onDutyTimeFormated;
	}
	
	public String getMorningOffDutyTimeFormated() {
		return morningOffDutyTimeFormated;
	}

	public void setMorningOffDutyTimeFormated(String morningOffDutyTimeFormated) {
		this.morningOffDutyTimeFormated = morningOffDutyTimeFormated;
	}
	
	public String getAfternoonOnDutyTimeFormated() {
		return afternoonOnDutyTimeFormated;
	}

	public void setAfternoonOnDutyTimeFormated(String afternoonOnDutyTimeFormated) {
		this.afternoonOnDutyTimeFormated = afternoonOnDutyTimeFormated;
	}

	public String getOffDutyTimeFormated() {
		return offDutyTimeFormated;
	}

	public void setOffDutyTimeFormated(String offDutyTimeFormated) {
		this.offDutyTimeFormated = offDutyTimeFormated;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getCheckStatus() {
		return checkStatus;
	}

	public void setCheckStatus(String checkStatus) {
		this.checkStatus = checkStatus;
	}

	public String getRecordDateStringFormated() {
		return recordDateStringFormated;
	}

	public void setRecordDateStringFormated(String recordDateStringFormated) {
		this.recordDateStringFormated = recordDateStringFormated;
	}

	public String getRecordYearString() {
		return recordYearString;
	}

	public void setRecordYearString(String recordYearString) {
		this.recordYearString = recordYearString;
	}

	public String getRecordMonthString() {
		return recordMonthString;
	}

	public void setRecordMonthString(String recordMonthString) {
		this.recordMonthString = recordMonthString;
	}

	public String getFile_id() {
		return file_id;
	}

	public void setFile_id(String file_id) {
		this.file_id = file_id;
	}

	public int getSheetIndex() {
		return sheetIndex;
	}

	public int getCurRow() {
		return curRow;
	}

	public List<String> getColmlist() {
		return colmlist;
	}

	public void setSheetIndex(int sheetIndex) {
		this.sheetIndex = sheetIndex;
	}

	public void setCurRow(int curRow) {
		this.curRow = curRow;
	}

	public void setColmlist(List<String> colmlist) {
		this.colmlist = colmlist;
	}
	
	public Date getRecordDate() {
		return recordDate;
	}

	public void setRecordDate(Date recordDate) {
		this.recordDate = recordDate;
	}	
}

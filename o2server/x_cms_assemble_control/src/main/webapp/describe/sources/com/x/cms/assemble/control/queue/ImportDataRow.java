package com.x.cms.assemble.control.queue;

import java.util.List;

import com.x.cms.common.excel.reader.ExcelReadRuntime;

public class ImportDataRow {
	
	private  int sheetIndex = 0;
	private int curRow = 0;
	private List<String> colmlist = null; 
	private ExcelReadRuntime excelReadRuntime = null;	
	
	public ImportDataRow( int sheetIndex, int curRow, List<String> colmlist, ExcelReadRuntime excelReadRuntime ) {
		super();
		this.sheetIndex = sheetIndex;
		this.curRow = curRow;
		this.colmlist = colmlist;
		this.excelReadRuntime = excelReadRuntime;
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
	public ExcelReadRuntime getExcelReadRuntime() {
		return excelReadRuntime;
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
	public void setExcelReadRuntime(ExcelReadRuntime excelReadRuntime) {
		this.excelReadRuntime = excelReadRuntime;
	}
	
}

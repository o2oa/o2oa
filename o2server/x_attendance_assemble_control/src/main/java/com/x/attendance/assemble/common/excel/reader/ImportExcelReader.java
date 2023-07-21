package com.x.attendance.assemble.common.excel.reader;

import java.util.List;

import com.x.attendance.assemble.control.processor.sender.SenderForValidateData;

public class ImportExcelReader implements IRowReader{
	
	/* 业务逻辑实现方法
	 * @see com.eprosun.util.excel.IRowReader#getRows(int, int, java.util.List)
	 */
	public void getRows( int sheetIndex, int curRow, List<String> colmlist, String fileKey, int startRow ) {
		if( curRow < startRow ){
			return;
		}

		if( colmlist != null && !colmlist.isEmpty() ) {
			if( !colmlist.get(0).isEmpty() && !colmlist.get(2).isEmpty()){
				new SenderForValidateData().execute( sheetIndex, curRow, colmlist, fileKey );
			}
		}
	}	
}

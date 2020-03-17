package com.x.cms.common.excel.reader;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.project.tools.ListTools;
import com.x.cms.assemble.control.ThisApplication;
import com.x.cms.assemble.control.queue.ImportDataRow;

public class DocumentExcelReader implements IRowReader{
	
	/* 业务逻辑实现方法
	 * @see com.eprosun.util.excel.IRowReader#getRows(int, int, java.util.List)
	 */
	public void getRows( int sheetIndex, int curRow, List<String> colmlist, ExcelReadRuntime excelReadRuntime ) {
		if( curRow < excelReadRuntime.startRow ){
			return;
		}
		System.out.println(">>>>>>>>>>>>>>>>>>>开始处理第" + curRow + "行数据：" + printData(colmlist) );
		if( ListTools.isNotEmpty( colmlist ) ) {
			List<String> _colmlist = new ArrayList<>();
			for( String col : colmlist ) {
				_colmlist.add( col );
			}
			excelReadRuntime.dataImportStatus.increaseDataTotal(1);
			try {
				ThisApplication.queueDataRowImport.send( new ImportDataRow( sheetIndex, curRow, _colmlist, excelReadRuntime ) );
			} catch (Exception e) {
				System.out.println(">>>>>>>>>>>>>>>>>>>第" + curRow + "行数据发送到处理队列时发生异常。" );
				e.printStackTrace();
			}
		}
	}
	
	private String printData(List<String> colmlist) {
		StringBuffer sb = new StringBuffer();
		for( String col : colmlist ){
			if( col == null ){
				col = "null";
			}
			if( sb.toString().isEmpty() ){
				sb.append( "["+ col + "]" );
			}else{
				sb.append( ", [" + col + "]" );
			}
		}
		return sb.toString();
	}
}

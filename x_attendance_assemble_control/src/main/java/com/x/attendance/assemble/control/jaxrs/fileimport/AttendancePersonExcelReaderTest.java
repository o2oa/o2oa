package com.x.attendance.assemble.control.jaxrs.fileimport;

import com.x.attendance.assemble.common.excel.reader.ExcelReaderUtil;
import com.x.attendance.assemble.common.excel.reader.IRowReader;

public class AttendancePersonExcelReaderTest {
	
	public static void main(String[] args) throws Exception {
		IRowReader reader = new AttendancePersonExcelReader();
		System.out.println( "开始读取数据......" );
		ExcelReaderUtil.readExcel(reader, "D:\\test33333.xls", "3333333", 1 );
		System.out.println( "读取数据完成！" );
	}
}

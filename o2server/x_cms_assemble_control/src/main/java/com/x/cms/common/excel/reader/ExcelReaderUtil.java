package com.x.cms.common.excel.reader;

import java.io.InputStream;

public class ExcelReaderUtil {
	
	//excel2003扩展名
	public static final String EXCEL03_EXTENSION = ".xls";
	//excel2007扩展名
	public static final String EXCEL07_EXTENSION = ".xlsx";
	
	/**
	 * 读取Excel文件，可能是03也可能是07版本
	 * @param excel03
	 * @param excel07
	 * @param fileName
	 * @throws Exception 
	 */
	public static void readExcel(IRowReader reader, String fileName, ExcelReadRuntime excelReadRuntime ) throws Exception{
		// 处理excel2003文件
		if (fileName.endsWith(EXCEL03_EXTENSION)){
			Excel2003Reader excel03 = new Excel2003Reader();
			excel03.setRowReader(reader, excelReadRuntime);
			excel03.process(fileName);
		// 处理excel2007文件
		} else if (fileName.endsWith(EXCEL07_EXTENSION)){
			Excel2007Reader excel07 = new Excel2007Reader();
			excel07.setRowReader(reader, excelReadRuntime);
			excel07.process(fileName);
		} else {
			throw new  Exception( "文件格式错误，fileName的扩展名只能是xls或xlsx。" );
		}
	}
	
	/**
	 * 读取Excel文件，可能是03也可能是07版本
	 * @param excel03
	 * @param excel07
	 * @param fileName
	 * @throws Exception 
	 */
	public static void readExcel(IRowReader reader,String fileName, InputStream inputStream, ExcelReadRuntime excelReadRuntime ) throws Exception{
		// 处理excel2003文件
		if (fileName.endsWith( EXCEL03_EXTENSION )){
			Excel2003Reader excel03 = new Excel2003Reader();
			excel03.setRowReader(reader, excelReadRuntime);
			excel03.process(inputStream);
		// 处理excel2007文件
		} else if (fileName.endsWith(EXCEL07_EXTENSION)){
			Excel2007Reader excel07 = new Excel2007Reader();
			excel07.setRowReader(reader, excelReadRuntime);
			excel07.process(inputStream);
		} else {
			throw new  Exception( "文件格式错误，fileName的扩展名只能是xls或xlsx。" );
		}
	}
}
//package com.x.calendar.common.excel.writer;
//
//import java.io.FileOutputStream;
//import java.io.IOException;
//
//import org.apache.poi.hssf.usermodel.HSSFWorkbook;
//import org.apache.poi.ss.usermodel.Cell;
//import org.apache.poi.ss.usermodel.Row;
//import org.apache.poi.ss.usermodel.Sheet;
//import org.apache.poi.ss.usermodel.Workbook;
//
//public class Excel2003Writer {
//
//	/**
//	 * @param args
//	 */
//	public static void main(String[] args) {
//		try{	
//			//System.out.println( "开始写入excel2003...." );
//			writeExcel( "tes2003.xls" );
//			//System.out.println( "写完xcel2003" );
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}
//	
//	
//	/**
//	 * 写入excel并填充内容,一个sheet只能写65536行以下，超出会报异常，写入时建议使用AbstractExcel2007Writer
//	 * @param fileName
//	 * @throws IOException
//	 */
//	@SuppressWarnings("resource")
//	public static void writeExcel(String fileName) throws IOException{
//			
//			// 创建excel2003对象
//			Workbook wb = new HSSFWorkbook();
//			
//			// 设置文件放置路径和文件名
//		    FileOutputStream fileOut = new FileOutputStream(fileName);
//		    // 创建新的表单
//		    Sheet sheet = wb.createSheet( "newsheet" );
//		    // 创建新行
//		    for(int i=0;i<20000;i++){
//			    Row row = sheet.createRow(i);
//			    // 创建单元格
//			    Cell cell = row.createCell(0);
//			    // 设置单元格值
//			    cell.setCellValue(1);
//			    row.createCell(1).setCellValue(1+i);
//			    row.createCell(2).setCellValue(true);
//			    row.createCell(3).setCellValue(0.43d);
//			    row.createCell(4).setCellValue('d');
//			    row.createCell(5).setCellValue( "" );
//			    row.createCell(6).setCellValue( "第七列"+i);
//			    row.createCell(7).setCellValue( "第八列"+i);
//		    }
//		    wb.write(fileOut);
//		    fileOut.close();
//	}
//
//
//}

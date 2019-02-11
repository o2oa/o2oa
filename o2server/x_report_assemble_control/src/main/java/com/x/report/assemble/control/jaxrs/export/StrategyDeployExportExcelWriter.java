package com.x.report.assemble.control.jaxrs.export;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;

import com.x.base.core.project.tools.ListTools;
import com.x.report.assemble.control.jaxrs.export.ActionExportForStrategyDeploy.ExpCompanyStrategy;
import com.x.report.assemble.control.jaxrs.export.ActionExportForStrategyDeploy.ExpMeasuresInfo;
import com.x.report.assemble.control.jaxrs.export.ActionExportForStrategyDeploy.ExpUnitInfo;
import com.x.report.assemble.control.jaxrs.export.ActionExportForStrategyDeploy.ExpWorkMonthProg;
import com.x.report.assemble.control.jaxrs.export.ActionExportForStrategyDeploy.ExpWorkProg;

public class StrategyDeployExportExcelWriter {

	private Integer currentRowNumber = 0;

	/**
	 * 写入excel并填充内容,一个sheet只能写65536行以下，超出会报异常，写入时建议大量数据时使用AbstractExcel2007Writer
	 * 
	 * @param fileOut
	 * @throws IOException
	 */
	public byte[] writeExcel(List<ExpCompanyStrategy> expCompanyStrateies) {
		String expCompanyStrategyTitle = null;
		String measureTitle = null;
		Integer expCompanyStrategyRowCount = 0;
		Integer measureRowCount = 0;
		byte[] buffer = null;
		Workbook wb = new HSSFWorkbook();// 创建excel2003对象
		Sheet sheet = wb.createSheet("公司战略汇报情况统计表");// 创建新的工作表
		Row row = null;
		Cell cell = null;

		if (ListTools.isNotEmpty(expCompanyStrateies)) {
			// 表头//////////////////////////////////////////////////////////////
			row = sheet.createRow(currentRowNumber);
			row.createCell(0).setCellValue("战略重点");
			row.createCell(1).setCellValue("战略举措");// 中心工作内容
			row.createCell(2).setCellValue("部门名称");
			row.createCell(3).setCellValue("1月");
			row.createCell(4).setCellValue("2月");
			row.createCell(5).setCellValue("3月");
			row.createCell(6).setCellValue("4月");
			row.createCell(7).setCellValue("5月");
			row.createCell(8).setCellValue("6月");
			row.createCell(9).setCellValue("7月");
			row.createCell(10).setCellValue("8月");
			row.createCell(11).setCellValue("9月");
			row.createCell(12).setCellValue("10月");
			row.createCell(13).setCellValue("11月");
			row.createCell(14).setCellValue("12月");

			currentRowNumber++;// 表头占一行

			for (ExpCompanyStrategy expCompanyStrategy : expCompanyStrateies) {
				expCompanyStrategyTitle = expCompanyStrategy.getTitle();
				expCompanyStrategyRowCount = expCompanyStrategy.getRowCount();
				if (expCompanyStrategyRowCount > 0) {
					sheet.addMergedRegion(new CellRangeAddress(currentRowNumber,
							(currentRowNumber + expCompanyStrategyRowCount - 1), 0, 0));
				}
				if (ListTools.isEmpty(expCompanyStrategy.getMeasures())) {
					row = sheet.createRow(currentRowNumber);
					row.createCell(0).setCellValue(expCompanyStrategyTitle);
					row.createCell(1).setCellValue("暂无");
					row.createCell(2).setCellValue("暂无");
					// 每一个战略重点加一行
					currentRowNumber++;
				} else {
					for (ExpMeasuresInfo expMeasuresInfo : expCompanyStrategy.getMeasures()) {
						measureTitle = expMeasuresInfo.getTitle();
						measureRowCount = expMeasuresInfo.getRowCount();
						if (measureRowCount > 0) {
							sheet.addMergedRegion(new CellRangeAddress(currentRowNumber,
									(currentRowNumber + measureRowCount - 1), 1, 1));
						}
						if (ListTools.isEmpty(expMeasuresInfo.getUnits())) {
							row = sheet.createRow(currentRowNumber);
							row.createCell(0).setCellValue(expCompanyStrategyTitle);
							row.createCell(1).setCellValue(measureTitle);
							row.createCell(2).setCellValue("暂无");
							// 每一个举措加一行
							currentRowNumber++;
						} else {
							for (ExpUnitInfo expUnitInfo : expMeasuresInfo.getUnits()) {
								row = sheet.createRow(currentRowNumber);
								row.createCell(0).setCellValue(expCompanyStrategyTitle);
								row.createCell(1).setCellValue(measureTitle);
								row.createCell(2).setCellValue(expUnitInfo.getTitle());
								if (ListTools.isNotEmpty(expUnitInfo.getWorkMonthProgs())) {
									StringBuffer sb = null;
									for (ExpWorkMonthProg monthProg : expUnitInfo.getWorkMonthProgs()) {
										if (ListTools.isNotEmpty(monthProg.getWorkProgs())) {
											sb = new StringBuffer();
											for (ExpWorkProg workProg : monthProg.getWorkProgs()) {
												sb.append(workProg.getPerson());
												sb.append(":");
												sb.append(workProg.getContent());
												sb.append("\n");
											}
										}
										if (StringUtils.isEmpty(sb.toString())) {
											row.createCell(monthProg.getMonth() + 2).setCellValue("暂无完成情况汇报");
										} else {
											row.createCell(monthProg.getMonth() + 2).setCellValue(sb.toString());
										}
									}
								}
								// 每一个部门加一行
								currentRowNumber++;
							}
						}
					}
				}
			}
			//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			//////////////// 单元格样式设计 //////////////////
			//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			sheet.setDefaultColumnWidth(20);
			sheet.setDefaultRowHeightInPoints(20);

			CellStyle cellStyle = wb.createCellStyle();
			cellStyle.setWrapText(true);
			cellStyle.setAlignment(HorizontalAlignment.CENTER); // 居中
			cellStyle.setVerticalAlignment(VerticalAlignment.CENTER); // 居中

			sheet.setColumnWidth(0, 50 * 256);
			sheet.setColumnWidth(1, 50 * 256);
			sheet.setColumnWidth(2, 30 * 256);
			sheet.setColumnWidth(3, 50 * 256);
			sheet.setColumnWidth(4, 50 * 256);
			sheet.setColumnWidth(5, 50 * 256);
			sheet.setColumnWidth(6, 50 * 256);
			sheet.setColumnWidth(7, 50 * 256);
			sheet.setColumnWidth(8, 50 * 256);
			sheet.setColumnWidth(9, 50 * 256);
			sheet.setColumnWidth(10, 50 * 256);
			sheet.setColumnWidth(11, 50 * 256);
			sheet.setColumnWidth(12, 50 * 256);
			sheet.setColumnWidth(13, 50 * 256);
			sheet.setColumnWidth(14, 50 * 256);

			Iterator<Row> rowIterator = sheet.rowIterator();
			Iterator<Cell> cellIterator = null;
			while (rowIterator.hasNext()) {
				row = rowIterator.next();
				row.setHeight((short) (40 * 20));
				cellIterator = row.cellIterator();
				while (cellIterator.hasNext()) {
					cell = cellIterator.next();
					cell.setCellStyle(cellStyle);
				}
			}

			ByteArrayOutputStream bos = null;
			try {
				bos = new ByteArrayOutputStream();
				wb.write(bos);
				wb.close();
				buffer = bos.toByteArray();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					bos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			try {
				return buffer;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return buffer;
	}
}

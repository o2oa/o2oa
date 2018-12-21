package com.x.report.assemble.control.jaxrs.export;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

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

import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.report.assemble.control.jaxrs.export.ActionExportForUnitStrategy.ExpMeasuresInfo;
import com.x.report.assemble.control.jaxrs.export.ActionExportForUnitStrategy.ExpUnitInfo;
import com.x.report.assemble.control.jaxrs.export.ActionExportForUnitStrategy.ExpWork;
import com.x.report.assemble.control.jaxrs.export.ActionExportForUnitStrategy.ExpWorkMonthProg;
import com.x.report.assemble.control.jaxrs.export.ActionExportForUnitStrategy.ExpWorkProg;

public class UnitStrategyExportExcelWriter {

	private static Logger logger = LoggerFactory.getLogger(UnitStrategyExportExcelWriter.class);
	private Integer currentRowNumber = 0;

	/**
	 * 写入excel并填充内容,一个sheet只能写65536行以下，超出会报异常，写入时建议大量数据时使用AbstractExcel2007Writer
	 * 
	 * @param fileOut
	 * @throws IOException
	 */
	public byte[] writeExcel(Map<String, ExpUnitInfo> expUnitInfoMap) {
		byte[] buffer = null;
		Workbook wb = new HSSFWorkbook();// 创建excel2003对象
		Sheet sheet = wb.createSheet("部门工作完成情况统计表");// 创建新的工作表
		Row row = null;
		Cell cell = null;

		Set<Entry<String, ExpUnitInfo>> expUnitInfo_set = expUnitInfoMap.entrySet();
		Iterator<Entry<String, ExpUnitInfo>> expUnitInfo_iterator = expUnitInfo_set.iterator();
		Map<String, ExpWork> expWorkMap = null;
		Set<Entry<String, ExpWork>> expWork_set = null;
		Iterator<Entry<String, ExpWork>> expWork_iterator = null;
		Map<String, ExpMeasuresInfo> expMeasuresInfoMap = null;
		Set<Entry<String, ExpMeasuresInfo>> expMeasuresInfo_set = null;
		Iterator<Entry<String, ExpMeasuresInfo>> expMeasuresInfo_iterator = null;
		Map<Integer, ExpWorkMonthProg> expWorkMonthProgMap = null;

		List<ExpWorkProg> expWorkProgList = null;
		ExpUnitInfo expUnitInfo = null;
		ExpWork expWork = null;
		ExpMeasuresInfo expMeasuresInfo = null;
		ExpWorkMonthProg expWorkMonthProg = null;
		StringBuffer sb = null;
		if (expUnitInfoMap != null) {
			// 表头//////////////////////////////////////////////////////////////
			row = sheet.createRow(currentRowNumber);
			row.createCell(0).setCellValue("部门名称");
			row.createCell(1).setCellValue("工作名称");
			row.createCell(2).setCellValue("战略举措");
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

			expUnitInfo_set = expUnitInfoMap.entrySet();
			expUnitInfo_iterator = expUnitInfo_set.iterator();
			while (expUnitInfo_iterator.hasNext()) {
				expUnitInfo = (ExpUnitInfo) expUnitInfo_iterator.next().getValue();
				// logger.info( "|===="+ currentRowNumber + " --> " + ( currentRowNumber +
				// expUnitInfo.getRowCount() -1 )+ ": 组织名称["+ expUnitInfo.getTitle() +"]" );
				logger.info(">>>>>>组织：" + expUnitInfo.getTitle() + " ， rowcount=" + expUnitInfo.getRowCount());
				logger.info(">>>>>>组织：currentRowNumber=" + currentRowNumber);
				if (expUnitInfo.getRowCount() == 0) {
					expUnitInfo.setRowCount(1);
				}
				sheet.addMergedRegion(new CellRangeAddress(currentRowNumber,
						(currentRowNumber + expUnitInfo.getRowCount() - 1), 0, 0));
				expWorkMap = expUnitInfo.getExpWorkMap();
				if (expWorkMap != null && !expWorkMap.isEmpty()) {
					expWork_set = expWorkMap.entrySet();
					expWork_iterator = expWork_set.iterator();
					while (expWork_iterator.hasNext()) {
						expWork = (ExpWork) expWork_iterator.next().getValue();
						// logger.info( "====|===="+ currentRowNumber + " --> " + ( currentRowNumber +
						// expWork.getRowCount() -1 )+ ": 工作标题["+ expWork.getTitle() +"]" );
						logger.info(">>>>>>>>>>>>工作：" + expWork.getTitle() + " , rowcount=" + expWork.getRowCount());
						logger.info(">>>>>>>>>>>>工作：currentRowNumber=" + currentRowNumber);
						if (expWork.getRowCount() == 0) {
							expWork.setRowCount(1);
						}
						sheet.addMergedRegion(new CellRangeAddress(currentRowNumber,
								(currentRowNumber + expWork.getRowCount() - 1), 1, 1));
						expMeasuresInfoMap = expWork.getExpMeasuresInfoMap();

						if (expMeasuresInfoMap != null && !expMeasuresInfoMap.isEmpty()) {
							expMeasuresInfo_set = expMeasuresInfoMap.entrySet();
							expMeasuresInfo_iterator = expMeasuresInfo_set.iterator();
							while (expMeasuresInfo_iterator.hasNext()) {
								expMeasuresInfo = (ExpMeasuresInfo) expMeasuresInfo_iterator.next().getValue();
								// logger.info( "========|===="+ currentRowNumber + " --> " + ( currentRowNumber
								// + expMeasuresInfo.getRowCount() -1 )+ ": 举措标题["+ expMeasuresInfo.getTitle()
								// +"]" );
								logger.info(">>>>>>>>>>>>>>>>>>举措：" + expMeasuresInfo.getTitle() + " , rowcount="
										+ expMeasuresInfo.getRowCount());
								logger.info(">>>>>>>>>>>>>>>>>>举措：currentRowNumber=" + currentRowNumber);
								row = sheet.createRow(currentRowNumber);
								row.createCell(0).setCellValue(expUnitInfo.getTitle());
								row.createCell(1).setCellValue(expWork.getTitle());
								row.createCell(2).setCellValue(expMeasuresInfo.getTitle());
								expWorkMonthProgMap = expMeasuresInfo.getExpWorkMonthProgMap();
								if (expWorkMonthProgMap == null) {
									expWorkMonthProgMap = new HashMap<>();
								}
								for (int i = 1; i <= 12; i++) {// 12个月的完成情况
									expWorkMonthProg = expWorkMonthProgMap.get(i);
									expWorkProgList = expWorkMonthProg.getWorkProgs();
									if (ListTools.isNotEmpty(expWorkProgList)) {
										sb = new StringBuffer();
										for (ExpWorkProg prog : expWorkProgList) {
											sb.append(prog.getPerson());
											sb.append(":");
											sb.append(prog.getContent());
											sb.append("\n");
										}
										if (StringUtils.isEmpty(sb.toString())) {
											row.createCell(i + 2).setCellValue("暂无完成情况汇报");
										} else {
											row.createCell(i + 2).setCellValue(sb.toString());
										}
									}
								}
								currentRowNumber++;
							}
						} else {
							logger.info(">>>>>>>>>>>>>>>>>>举措：expMeasuresInfoMap is null!");
							logger.info(">>>>>>>>>>>>>>>>>>举措：currentRowNumber=" + currentRowNumber);
							row = sheet.createRow(currentRowNumber);
							row.createCell(0).setCellValue(expUnitInfo.getTitle());
							row.createCell(1).setCellValue(expWork.getTitle());
							row.createCell(2).setCellValue("暂无");
							currentRowNumber++;
						}
					}
				} else {
					logger.info(">>>>>>>>>>>>工作：expWorkMap is null!");
					logger.info(">>>>>>>>>>>>工作：currentRowNumber=" + currentRowNumber);
					row = sheet.createRow(currentRowNumber);
					row.createCell(0).setCellValue(expUnitInfo.getTitle());
					row.createCell(1).setCellValue("暂无");
					row.createCell(2).setCellValue("暂无");
					currentRowNumber++;
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

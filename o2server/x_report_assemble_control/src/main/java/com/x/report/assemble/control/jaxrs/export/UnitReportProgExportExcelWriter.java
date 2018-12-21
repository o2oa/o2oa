package com.x.report.assemble.control.jaxrs.export;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFComment;
import org.apache.poi.xssf.usermodel.XSSFDrawing;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.x.base.core.project.tools.ListTools;
import com.x.base.core.project.tools.SortTools;
import com.x.report.assemble.control.jaxrs.export.ActionExportForUnitReport.ExpExtContentInfo;
import com.x.report.assemble.control.jaxrs.export.ActionExportForUnitReport.ExpMeasuresInfo;
import com.x.report.assemble.control.jaxrs.export.ActionExportForUnitReport.ExpUnitInfo;
import com.x.report.assemble.control.jaxrs.export.ActionExportForUnitReport.ExpWork;
import com.x.report.assemble.control.jaxrs.export.ActionExportForUnitReport.ExpWorkPlan;
import com.x.report.assemble.control.jaxrs.export.ActionExportForUnitReport.ExpWorkPlanNext;
import com.x.report.assemble.control.jaxrs.export.ActionExportForUnitReport.ExpWorkProg;

public class UnitReportProgExportExcelWriter {

	private Integer currentRowNumber = 0;

	/**
	 * 写入excel并填充内容,一个sheet只能写65536行以下，超出会报异常，写入时建议大量数据时使用AbstractExcel2007Writer
	 * 
	 * @param fileOut
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public byte[] writeExcel(List<ExpUnitInfo> expUnitInfoList) throws FileNotFoundException {
		byte[] buffer = null;
		StringBuffer stringBuffer = null;
		XSSFSheet sheet = null;
		// Row row = null;
		XSSFRow row = null;
		// Cell cell = null;
		XSSFCell cell = null;

		XSSFWorkbook wb = new XSSFWorkbook();// 创建excel2003对象

		if (ListTools.isNotEmpty(expUnitInfoList)) {
			for (ExpUnitInfo expUnitInfo : expUnitInfoList) {
				sheet = null;
				if (ListTools.isNotEmpty(expUnitInfo.getExpWorkList())) {
					try {
						sheet = wb.createSheet(expUnitInfo.getTitle());// 创建新的工作表
					} catch (Exception e) {
						for (int i = 1; i <= 10; i++) {
							try {
								sheet = wb.createSheet(expUnitInfo.getTitle() + "_" + i);// 创建新的工作表，名称重复，获取特殊名称
								break;
							} catch (Exception e1) {
							}
						}
					}
				}
				if (sheet != null) {
					currentRowNumber = 0;
					stringBuffer = new StringBuffer();
					stringBuffer.append(expUnitInfo.getTitle());
					stringBuffer.append(expUnitInfo.getThisYear());
					stringBuffer.append("年");
					stringBuffer.append(expUnitInfo.getThisMonth());
					stringBuffer.append("月工作总结和");
					stringBuffer.append(expUnitInfo.getNextYear());
					stringBuffer.append("年");
					stringBuffer.append(expUnitInfo.getNextMonth());
					stringBuffer.append("月份工作计划");

					row = sheet.createRow(currentRowNumber);
					row.createCell(0).setCellValue(stringBuffer.toString());
					sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 6));

					currentRowNumber++;
					row = sheet.createRow(currentRowNumber);
					row.createCell(0).setCellValue("部门");
					row.createCell(1).setCellValue("本月部门重点工作");
					row.createCell(2).setCellValue(
							"一、" + expUnitInfo.getThisYear() + "年" + expUnitInfo.getThisMonth() + "月份五项重点工作计划（要点）");
					row.createCell(3).setCellValue(
							"一、" + expUnitInfo.getThisYear() + "年" + expUnitInfo.getThisMonth() + "月份五项重点工作总结（要点）");
					row.createCell(4).setCellValue("下月部门重点工作");
					row.createCell(5).setCellValue(
							"二、" + expUnitInfo.getNextYear() + "年" + expUnitInfo.getNextMonth() + "月份五项重点工作计划（要点）");
					row.createCell(6).setCellValue("三、服务客户（如有）");
					row.createCell(7).setCellValue("四、关爱员工（如有）");
					row.createCell(8).setCellValue("五、意见建议（如有）");

					StringBuffer sb = null;
					// 得到一个POI的工具类
					CreationHelper factory = wb.getCreationHelper();
					// 得到一个换图的对象
					XSSFDrawing drawing = sheet.createDrawingPatriarch();
					// ClientAnchor是附属在WorkSheet上的一个对象， 其固定在一个单元格的左上角和右下角.
					XSSFComment comment = null;
					RichTextString comment_content = null;
					if (ListTools.isNotEmpty(expUnitInfo.getExpWorkList())) {

						try {
							SortTools.asc(expUnitInfo.getExpWorkList(), "orderNumber");
						} catch (Exception e) {
							e.printStackTrace();
						}

						for (ExpWork expWork : expUnitInfo.getExpWorkList()) {
							currentRowNumber++;
							row = sheet.createRow(currentRowNumber);

							row.createCell(0).setCellValue(expUnitInfo.getTitle());
							row.createCell(1).setCellValue(expWork.getTitle());
							// 设置备注
							if (ListTools.isNotEmpty(expWork.getExpMeasuresInfoList())) {
								sb = new StringBuffer();
								int idx = 0;
								for (ExpMeasuresInfo measuresInfo : expWork.getExpMeasuresInfoList()) {
									idx++;
									if (StringUtils.isNotEmpty(measuresInfo.getTitle())) {
										sb.append(idx + "、" + measuresInfo.getTitle() + "\n");
									}
								}
								// 获取批注对象( int dx1, int dy1, int dx2, int dy2, short col1, int row1, short col2,
								// int row2 )
								// 前四个参数是坐标点,后四个参数是编辑和显示批注时的大小.
								comment = drawing.createCellComment(
										new XSSFClientAnchor(0, 0, 0, 0, (short) 3, 3, (short) 4, 4));
								// comment = drawing.createCellComment(anchor);
								comment_content = factory.createRichTextString(sb.toString());
								comment.setString(comment_content);
								comment.setAuthor("战略管理系统");
								row.createCell(2).setCellComment(comment);
							}
							row.createCell(2).setCellValue(composeWorkPlanContent(expWork.getExpWorkPlans()));
							row.createCell(3).setCellValue(composeWorkProgContent(expWork.getExpWorkProgs()));
							row.createCell(4).setCellValue(expWork.getNextMontWorkTitle());
							row.createCell(5).setCellValue(composeWorkPlanNextContent(expWork.getExpWorkPlanNexts()));
							row.createCell(6).setCellValue(composeFWKHContent(expWork.getExpFWKH()));
							row.createCell(7).setCellValue(composeYGGAContent(expWork.getExpYGGAs()));
							row.createCell(8).setCellValue(composeYJJYContent(expWork.getExpYJJY()));

						}
					}
					//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
					//////////////// 单元格样式设计 //////////////////
					//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

					XSSFFont font = wb.createFont();
					font.setFontName("微软雅黑");
					font.setFontHeightInPoints((short) 9);// 设置字体大小

					// CellStyle cellStyle_sheet_title = wb.createCellStyle();
					XSSFCellStyle cellStyle_sheet_title = wb.createCellStyle();
					cellStyle_sheet_title.setWrapText(true);
					cellStyle_sheet_title.setAlignment(HorizontalAlignment.CENTER); // 居中
					cellStyle_sheet_title.setVerticalAlignment(VerticalAlignment.CENTER); // 居中

					cellStyle_sheet_title.setFont(font);
					cellStyle_sheet_title.setBorderTop(BorderStyle.THIN);
					cellStyle_sheet_title.setBorderBottom(BorderStyle.THIN);
					cellStyle_sheet_title.setBorderLeft(BorderStyle.THIN);
					cellStyle_sheet_title.setBorderRight(BorderStyle.THIN);

					// CellStyle cellStyle_top_title = wb.createCellStyle();
					XSSFCellStyle cellStyle_top_title = wb.createCellStyle();
					cellStyle_top_title.setWrapText(true);
					cellStyle_top_title.setAlignment(HorizontalAlignment.CENTER); // 居中
					cellStyle_top_title.setVerticalAlignment(VerticalAlignment.CENTER); // 居中

					cellStyle_top_title.setFont(font);
					cellStyle_top_title.setBorderTop(BorderStyle.THIN);
					cellStyle_top_title.setBorderBottom(BorderStyle.THIN);
					cellStyle_top_title.setBorderLeft(BorderStyle.THIN);
					cellStyle_top_title.setBorderRight(BorderStyle.THIN);

					// CellStyle cellStyle = wb.createCellStyle();
					XSSFCellStyle cellStyle = wb.createCellStyle();
					cellStyle.setWrapText(true);
					cellStyle.setAlignment(HorizontalAlignment.LEFT); // 侧左
					cellStyle.setVerticalAlignment(VerticalAlignment.CENTER); // 居中

					cellStyle.setFont(font);
					cellStyle.setBorderTop(BorderStyle.THIN);
					cellStyle.setBorderBottom(BorderStyle.THIN);
					cellStyle.setBorderLeft(BorderStyle.THIN);
					cellStyle.setBorderRight(BorderStyle.THIN);

					Iterator<Row> rowIterator = sheet.rowIterator();
					Iterator<Cell> cellIterator = null;

					int i = 0;
					while (rowIterator.hasNext()) {
						row = (XSSFRow) rowIterator.next();
						i++;
						if (i == 1) {
							row.setHeight((short) (40 * 20));
							cellIterator = row.cellIterator();
							while (cellIterator.hasNext()) {
								cell = (XSSFCell) cellIterator.next();
								cell.setCellStyle(cellStyle_sheet_title);
							}
						} else if (i == 2) {
							row.setHeight((short) (40 * 20));
							cellIterator = row.cellIterator();
							while (cellIterator.hasNext()) {
								cell = (XSSFCell) cellIterator.next();
								cell.setCellStyle(cellStyle_top_title);
							}
						} else {
							// row.setHeight((short) (40 * 20));
							row.setHeight((short) -1);
							cellIterator = row.cellIterator();
							while (cellIterator.hasNext()) {
								cell = (XSSFCell) cellIterator.next();
								cell.setCellStyle(cellStyle);
							}
						}
					}

					sheet.setDefaultColumnWidth(40 * 20);
					sheet.setDefaultRowHeightInPoints(40 * 20);
					sheet.setColumnWidth(0, 30 * 256);
					sheet.setColumnWidth(1, 20 * 256);
					sheet.setColumnWidth(2, 50 * 256);
					sheet.setColumnWidth(3, 50 * 256);
					sheet.setColumnWidth(4, 20 * 256);
					sheet.setColumnWidth(5, 50 * 256);
					sheet.setColumnWidth(6, 50 * 256);
					sheet.setColumnWidth(7, 50 * 256);
					sheet.setColumnWidth(8, 50 * 256);

				}
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
				bos.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
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
		return buffer;
	}

	private String composeYJJYContent(List<ExpExtContentInfo> expYJJY) {
		StringBuffer sb = new StringBuffer();
		if (ListTools.isNotEmpty(expYJJY)) {
			for (ExpExtContentInfo expExtContentInfo : expYJJY) {
				// sb.append( expExtContentInfo.getPerson() );
				// sb.append( "：" );
				sb.append(expExtContentInfo.getYJJY());
				sb.append("\n");
			}
		}
		return sb.toString();
	}

	private String composeYGGAContent(List<ExpExtContentInfo> expYGGAs) {
		StringBuffer sb = new StringBuffer();
		if (ListTools.isNotEmpty(expYGGAs)) {
			for (ExpExtContentInfo expExtContentInfo : expYGGAs) {
				// sb.append( expExtContentInfo.getPerson() );
				// sb.append( "：" );
				sb.append(expExtContentInfo.getYGGA());
				sb.append("\n");
			}
		}
		return sb.toString();
	}

	private String composeFWKHContent(List<ExpExtContentInfo> expFWKH) {
		StringBuffer sb = new StringBuffer();
		if (ListTools.isNotEmpty(expFWKH)) {
			for (ExpExtContentInfo expExtContentInfo : expFWKH) {
				// sb.append( expExtContentInfo.getPerson() );
				// sb.append( "：" );
				sb.append(expExtContentInfo.getFWKH());
				sb.append("\n");
			}
		}
		return sb.toString();
	}

	private String composeWorkProgContent(List<ExpWorkProg> expWorkProgs) {
		StringBuffer sb = new StringBuffer();
		if (ListTools.isNotEmpty(expWorkProgs)) {
			for (ExpWorkProg expWorkProg : expWorkProgs) {
				// sb.append( expWorkProg.getPerson() );
				// sb.append( "：" );
				sb.append(expWorkProg.getContent());
				sb.append("\n");
			}
		}
		return sb.toString();
	}

	private String composeWorkPlanNextContent(List<ExpWorkPlanNext> expWorkPlanNexts) {
		StringBuffer sb = new StringBuffer();
		if (ListTools.isNotEmpty(expWorkPlanNexts)) {
			for (ExpWorkPlanNext expWorkPlanNext : expWorkPlanNexts) {
				// sb.append( expWorkPlanNext.getPerson() );
				// sb.append( "：" );
				sb.append(expWorkPlanNext.getContent());
				sb.append("\n");
			}
		}
		return sb.toString();
	}

	private String composeWorkPlanContent(List<ExpWorkPlan> expWorkPlans) {
		StringBuffer sb = new StringBuffer();
		if (ListTools.isNotEmpty(expWorkPlans)) {
			for (ExpWorkPlan expWorkPlan : expWorkPlans) {
				sb.append(expWorkPlan.getContent());
				sb.append("\n");
			}
		}
		return sb.toString();
	}

}

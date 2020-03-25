package com.x.okr.assemble.common.excel.writer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.okr.assemble.common.date.DateOperation;
import com.x.okr.assemble.control.jaxrs.statistic.BaseAction.WoOkrStatisticReportContent;
import com.x.okr.assemble.control.jaxrs.statistic.BaseAction.WoOkrStatisticReportContentCenter;
import com.x.okr.assemble.control.schedule.entity.WorkReportProcessOpinionEntity;
import com.x.okr.assemble.control.service.OkrWorkDetailInfoService;
import com.x.okr.entity.OkrWorkDetailInfo;

public class WorkReportContentExportExcelWriter {

	private static Logger logger = LoggerFactory.getLogger(WorkReportContentExportExcelWriter.class);
	private OkrWorkDetailInfoService okrWorkDetailInfoService = new OkrWorkDetailInfoService();
	private Integer currentRowNumber = 0;

	/**
	 * 写入excel并填充内容,一个sheet只能写65536行以下，超出会报异常，写入时建议大量数据时使用AbstractExcel2007Writer
	 * 
	 * @param fileOut
	 * @throws IOException
	 */
	public String writeExcel(List<WoOkrStatisticReportContentCenter> exportDataList) {
		String centerTitle = null;
		Integer workCountInCenter = 0;

		Workbook wb = new HSSFWorkbook();// 创建excel2003对象
		Sheet sheet = wb.createSheet("工作汇报情况统计表");// 创建新的工作表
		Row row = null;
		Cell cell = null;
		// 表头//////////////////////////////////////////////////////////////
		row = sheet.createRow(currentRowNumber);
		row.createCell(0).setCellValue("工作类别");
		row.createCell(1).setCellValue("重点事项");// 中心工作内容
		row.createCell(2).setCellValue("责任组织");
		row.createCell(3).setCellValue("事项分解及描述");// 具体工作内容
		row.createCell(4).setCellValue("工作汇报情况");
		row.createCell(5).setCellValue("工作汇报形式");
		row.createCell(6).setCellValue("具体行动举措");
		row.createCell(7).setCellValue("预期里程碑/阶段性结果标志");
		row.createCell(8).setCellValue("截止目前完成情况");
		row.createCell(9).setCellValue("下一步工作要点及需求");
		row.createCell(10).setCellValue("督办评价");
		row.createCell(11).setCellValue("领导评价");

		currentRowNumber++;// 表头占一行

		if (exportDataList != null && !exportDataList.isEmpty()) {
			for (WoOkrStatisticReportContentCenter contentForCenter : exportDataList) {
				if (contentForCenter.getContents() != null && !contentForCenter.getContents().isEmpty()) {
					// 中心工作中所有工作的数量
					workCountInCenter = contentForCenter.getContents().size();
					centerTitle = contentForCenter.getTitle();

					logger.info(">>>>中心工作[" + centerTitle + "]:" + currentRowNumber + " --> "
							+ (currentRowNumber + workCountInCenter - 1));
					sheet.addMergedRegion(
							new CellRangeAddress(currentRowNumber, (currentRowNumber + workCountInCenter - 1), 0, 0));
					sheet.addMergedRegion(
							new CellRangeAddress(currentRowNumber, (currentRowNumber + workCountInCenter - 1), 1, 1));

					for (WoOkrStatisticReportContent wrapOutOkrStatisticReportContent : contentForCenter
							.getContents()) {
						row = sheet.createRow(currentRowNumber);
						row.createCell(0).setCellValue(wrapOutOkrStatisticReportContent.getWorkType());// "工作类别"
						row.createCell(1).setCellValue(centerTitle);// "中心工作事项"
						composeWorkToExcel(sheet, row, wrapOutOkrStatisticReportContent);
					}
				}
			}

			//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			//////////////// 单元格样式设计 //////////////////
			//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			sheet.setDefaultColumnWidth(20);
			sheet.setDefaultRowHeightInPoints(20);

			CellStyle cellStyle = wb.createCellStyle();
			cellStyle.setAlignment(HorizontalAlignment.CENTER); // 居中
			cellStyle.setVerticalAlignment(VerticalAlignment.CENTER); // 居中

			sheet.setColumnWidth(0, 10 * 256);
			sheet.setColumnWidth(1, 50 * 256);
			sheet.setColumnWidth(2, 20 * 256);
			sheet.setColumnWidth(3, 50 * 256);
			sheet.setColumnWidth(4, 10 * 256);
			sheet.setColumnWidth(5, 10 * 256);
			sheet.setColumnWidth(6, 50 * 256);
			sheet.setColumnWidth(7, 50 * 256);
			sheet.setColumnWidth(8, 50 * 256);
			sheet.setColumnWidth(9, 50 * 256);
			sheet.setColumnWidth(10, 50 * 256);
			sheet.setColumnWidth(11, 50 * 256);

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
		}
		try {
			String timeFlag = new DateOperation().getNowTimeChar();
			File dir = new File("download/temp");
			if (!dir.exists()) {
				dir.mkdirs();
			}
			FileOutputStream fos = new FileOutputStream("download/temp/export_" + timeFlag + ".xls");
			wb.write(fos);
			fos.close();
			wb.close();
			return timeFlag;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "none";
	}

	private void composeWorkToExcel(Sheet sheet, Row row,
			WoOkrStatisticReportContent wrapOutOkrStatisticReportContent) {
		if (wrapOutOkrStatisticReportContent == null) {
			return;
		}

		List<WorkReportProcessOpinionEntity> opinions = null;
		OkrWorkDetailInfo detail = null;
		String opinionContent = null;
		Gson gson = XGsonBuilder.instance();
		String opinion = null;

		row.createCell(2).setCellValue(wrapOutOkrStatisticReportContent.getResponsibilityUnitName());// "责任组织"
		row.createCell(5).setCellValue(wrapOutOkrStatisticReportContent.getCycleType());// "工作汇报形式"
		row.createCell(8).setCellValue(wrapOutOkrStatisticReportContent.getProgressDescription());// "截止目前完成情况"
		row.createCell(9).setCellValue(wrapOutOkrStatisticReportContent.getWorkPlan());// "下一步工作要点及需求"
		row.createCell(10).setCellValue(wrapOutOkrStatisticReportContent.getAdminSuperviseInfo());// "督办评价"

		try {
			detail = okrWorkDetailInfoService.get(wrapOutOkrStatisticReportContent.getWorkId());
		} catch (Exception e) {
			logger.warn("system get work detail with work id got an exception.");
			logger.error(e);
		}
		if (detail != null) {
			row.createCell(6).setCellValue(detail.getProgressAction());// "具体行动举措"
			row.createCell(7).setCellValue(detail.getLandmarkDescription());// "预期里程碑/阶段性结果标志"
			row.createCell(3).setCellValue(detail.getWorkDetail());// "事项分解及描述"
		}

		row.createCell(4).setCellValue(wrapOutOkrStatisticReportContent.getReportStatus());// "工作汇报情况"

		opinionContent = wrapOutOkrStatisticReportContent.getOpinion();
		if (opinionContent != null) {
			opinion = "";
			opinions = gson.fromJson(opinionContent, new TypeToken<List<WorkReportProcessOpinionEntity>>() {
			}.getType());
			if (opinions != null) {
				for (WorkReportProcessOpinionEntity workReportProcessOpinionEntity : opinions) {
					opinion = opinion + workReportProcessOpinionEntity.getProcessorName() + ":"
							+ workReportProcessOpinionEntity.getOpinion() + "\n";
				}
				row.createCell(11).setCellValue(opinion);// "领导评价"
			} else {
				row.createCell(11).setCellValue("");// "领导评价"
			}
		}
		currentRowNumber++;
		if (wrapOutOkrStatisticReportContent.getSubWork() != null
				&& !wrapOutOkrStatisticReportContent.getSubWork().isEmpty()) {
			for (WoOkrStatisticReportContent subWorks : wrapOutOkrStatisticReportContent.getSubWork()) {
				row = sheet.createRow(currentRowNumber);
				composeWorkToExcel(sheet, row, subWorks);
			}
		}
	}
}

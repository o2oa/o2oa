package com.x.strategydeploy.assemble.control.strategy;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoFile;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.strategydeploy.assemble.control.Business;
import com.x.strategydeploy.assemble.control.factory.MeasuresInfoFactory;
//import com.x.strategydeploy.assemble.control.service.StrategyDeployOperationService;
import com.x.strategydeploy.assemble.control.service.StrategyDeployQueryService;
import com.x.strategydeploy.core.entity.MeasuresInfo;
//import com.x.strategydeploy.assemble.control.strategy.BaseAction.Wo;
import com.x.strategydeploy.core.entity.StrategyDeploy;

public class ActionExportExcelStreamXLSX extends BaseAction {
	private static Logger logger = LoggerFactory.getLogger(ActionExportExcelStreamXLSX.class);
	private String filename = "StrategyAndMeasures";
	private String _suffix = ".xlsx";
	private String excelfilename = "";
	private List<StrategyDeploy> list;
	private List<MeasuresInfo> measures_list;

	ActionResult<WoExcel> execute(HttpServletRequest request, HttpServletResponse response,
			EffectivePerson effectivePerson, String year, Boolean stream) throws Exception {
		excelfilename = filename + "_" + year + _suffix;
		ActionResult<WoExcel> result = new ActionResult<>();
		logger.info("导出excel，公司工作重点和举措关系.");
		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet("公司工作重点");
		sheet.setDefaultColumnWidth(15);

		XSSFCellStyle style = workbook.createCellStyle();
		// 创建第一行（也可以称为表头）
		XSSFRow row = sheet.createRow(0);
		// 样式字体居中
		style.setAlignment(HorizontalAlignment.CENTER);
		// 给表头第一行一次创建单元格
		XSSFCell cell = row.createCell(0);
		cell.setCellValue("序号");
		cell.setCellStyle(style);

		cell = row.createCell(1);
		cell.setCellValue("公司工作重点标题");
		cell.setCellStyle(style);

		cell = row.createCell(2);
		cell.setCellValue("描述");
		cell.setCellStyle(style);

		cell = row.createCell(3);
		cell.setCellValue("举措标题");
		cell.setCellStyle(style);

		cell = row.createCell(4);
		cell.setCellValue("举措落实部门");
		cell.setCellStyle(style);

		list = ActionExportExcelStreamXLSX.listStrategyDeploysByYear(effectivePerson, year);

		// 向单元格里填充数据
		short StrategySize = (short) list.size();
		short RowCounter = (short) 0;
		int firstRow; // 合并单元格，开始行
		int lastRow; // 合并单元格，结束行
		for (short i = 0; i < StrategySize; i++) {
			measures_list = ActionExportExcelStreamXLSX.listMeasuresInfoByParentId(list.get(i).getId());
			short MeasuresSize = (short) measures_list.size();
			if (MeasuresSize == 0) {
				// 没有举措的行
				RowCounter++;
				row = sheet.createRow(RowCounter);
				row.createCell(0).setCellValue(list.get(i).getSequencenumber());
				row.createCell(1).setCellValue(list.get(i).getStrategydeploytitle());
				row.createCell(2).setCellValue(list.get(i).getStrategydeploydescribe());
				row.createCell(3).setCellValue("");
				row.createCell(4).setCellValue("");
			} else {
				firstRow = RowCounter + 1; // 先记录下合并的行
				for (int j = 0; j < MeasuresSize; j++) {
					RowCounter++;
					row = sheet.createRow(RowCounter);
					// logger.info("有举措：" + list.get(i).getSequencenumber() + ":" +
					// list.get(i).getStrategydeploytitle());
					row.createCell(0).setCellValue(list.get(i).getSequencenumber());
					row.createCell(1).setCellValue(list.get(i).getStrategydeploytitle());
					row.createCell(2).setCellValue(list.get(i).getStrategydeploydescribe());
					row.createCell(3).setCellValue(measures_list.get(j).getMeasuresinfotitle());
					row.createCell(4).setCellValue(measures_list.get(j).getDeptlist().toString());
				}
				lastRow = RowCounter;

				if (firstRow != lastRow) {// 如果开始行，和结束行不是同一行，那么才进行合并动作。
					CellRangeAddress cellRangeAddress0 = new CellRangeAddress(firstRow, lastRow, 0, 0); // 序号列合并
					CellRangeAddress cellRangeAddress1 = new CellRangeAddress(firstRow, lastRow, 1, 1); // 标题列合并
					CellRangeAddress cellRangeAddress2 = new CellRangeAddress(firstRow, lastRow, 2, 2); // 描述列合并
					sheet.addMergedRegion(cellRangeAddress0);
					sheet.addMergedRegion(cellRangeAddress1);
					sheet.addMergedRegion(cellRangeAddress2);
				}

			}
		}
		// logger.info("共有合并区域：" + sheet.getNumMergedRegions());
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		workbook.write(baos);
		byte[] bytes = baos.toByteArray();
		// Wo wo = new Wo(bytes, this.contentType(stream, workbook.getNameName(0)),
		// this.contentDisposition(stream, workbook.getNameName(0)));
		logger.info("excelfilename:" + excelfilename);
		WoExcel wo = new WoExcel(bytes, this.contentType(stream, excelfilename),
				this.contentDisposition(stream, excelfilename));
		result.setData(wo);
		workbook.close();
		return result;
	}

	public static class WoExcel extends WoFile {
		public WoExcel(byte[] bytes, String contentType, String contentDisposition) {
			super(bytes, contentType, contentDisposition);
		}
	}

	protected static List<StrategyDeploy> listStrategyDeploysByYear(EffectivePerson effectivePerson, String year)
			throws Exception {
		StrategyDeployQueryService strategyDeployQueryService = new StrategyDeployQueryService();
		// StrategyDeployOperationService strategyDeployOperationService = new
		// StrategyDeployOperationService();
		List<StrategyDeploy> strategydeployList = new ArrayList<StrategyDeploy>();
		// List<Wo> wrapOutList = new ArrayList<Wo>();
		strategydeployList = strategyDeployQueryService.getListByYear(year);
		// wrapOutList = Wo.copier.copy(strategydeployList);
		// wrapOutList =
		// strategyDeployOperationService.setActions(wrapOutList,effectivePerson);
		return strategydeployList;

	}

	protected static List<MeasuresInfo> listMeasuresInfoByParentId(String _parentId) throws Exception {
		List<MeasuresInfo> result = new ArrayList<MeasuresInfo>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			MeasuresInfoFactory measuresInfoFactory = business.measuresInfoFactory();
			result = measuresInfoFactory.getListByParentId(_parentId);
		}
		return result;
	}
}

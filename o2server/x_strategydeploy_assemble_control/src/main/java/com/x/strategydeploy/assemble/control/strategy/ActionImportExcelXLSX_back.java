package com.x.strategydeploy.assemble.control.strategy;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.StringTools;
import com.x.strategydeploy.assemble.control.Business;

public class ActionImportExcelXLSX_back extends BaseAction {
	private static  Logger logger = LoggerFactory.getLogger(ActionImportExcelXLSX_back.class);
	private int startLine = 1;

	ActionResult<Wo> execute(EffectivePerson effectivePerson, byte[] bytes, FormDataContentDisposition disposition, String year) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create(); InputStream is = new ByteArrayInputStream(bytes); XSSFWorkbook workbook = new XSSFWorkbook(is); ByteArrayOutputStream os = new ByteArrayOutputStream()) {
			Business business = new Business(emc);
			this.scan(business, workbook);
			String flag = StringTools.uniqueToken();
			Wo wo = new Wo();
			wo.setFlag(flag);
			result.setData(wo);
		}
		return result;

	}

	private void scan(Business business, XSSFWorkbook workbook) throws Exception {
		Sheet sheet = workbook.getSheetAt(0);
		int lastRowNum = sheet.getLastRowNum();
		Row row = null;
		for (int i = startLine; i <= lastRowNum; i++) {
			row = sheet.getRow(i);
			logger.info("---------------------------row number:" + i);
			//int LastCellNum = row.getLastCellNum();
			for (Cell c : row) {
				boolean isMerge = isMergedRegion(sheet, i, c.getColumnIndex());
				//判断是否具有合并单元格
				//logger.info("isMerge:" + isMerge);
				if (isMerge) {
					String rs = getMergedRegionValue(sheet, row.getRowNum(), c.getColumnIndex());
					//System.out.print(rs + "");
					logger.info(rs);
				} else {
					//System.out.print(c.getRichStringCellValue() + "");
					logger.info(getCellValue(c));
				}
			}
			logger.info("-------------------------------");
			logger.info("");
		}

		//		List<PersonItem> people = new ArrayList<>();
		//		PersonSheetConfigurator configurator = new PersonSheetConfigurator(workbook, sheet);
		//		this.scanPerson(configurator, sheet, people);
		//		this.concretePassword(people);
		//		this.persist(business, workbook, configurator, people);
	}

	/**
	 * 判断指定的单元格是否是合并单元格
	 * 
	 * @param sheet
	 * @param row
	 *            行下标
	 * @param column
	 *            列下标
	 * @return
	 */
	private boolean isMergedRegion(Sheet sheet, int row, int column) {
		logger.info("isMergedRegion:" + row + "::" + column);
		int sheetMergeCount = sheet.getNumMergedRegions();
		for (int i = 0; i < sheetMergeCount; i++) {
			CellRangeAddress range = sheet.getMergedRegion(i);
			int firstColumn = range.getFirstColumn();
			int lastColumn = range.getLastColumn();
			int firstRow = range.getFirstRow();
			int lastRow = range.getLastRow();
			if (row >= firstRow && row <= lastRow) {
				if (column >= firstColumn && column <= lastColumn) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 获取合并单元格的值
	 * 
	 * @param sheet
	 * @param row
	 * @param column
	 * @return
	 */
	public String getMergedRegionValue(Sheet sheet, int row, int column) {
		int sheetMergeCount = sheet.getNumMergedRegions();

		for (int i = 0; i < sheetMergeCount; i++) {
			CellRangeAddress ca = sheet.getMergedRegion(i);
			int firstColumn = ca.getFirstColumn();
			int lastColumn = ca.getLastColumn();
			int firstRow = ca.getFirstRow();
			int lastRow = ca.getLastRow();

			if (row >= firstRow && row <= lastRow) {

				if (column >= firstColumn && column <= lastColumn) {
					Row fRow = sheet.getRow(firstRow);
					Cell fCell = fRow.getCell(firstColumn);
					return getCellValue(fCell);
				}
			}
		}

		return null;
	}

	/**
	 * 获取单元格的值
	 * 
	 * @param cell
	 * @return
	 */
	public String getCellValue(Cell cell) {

		if (cell == null)
			return "";

		if (cell.getCellType() ==CellType.STRING) {

			return cell.getStringCellValue();

		} else if (cell.getCellType() == CellType.BOOLEAN) {

			return String.valueOf(cell.getBooleanCellValue());

		} else if (cell.getCellType() == CellType.FORMULA) {

			return cell.getCellFormula();

		} else if (cell.getCellType() == CellType.NUMERIC) {
			logger.info("CELL_TYPE_NUMERIC!!!");
			return String.valueOf(cell.getNumericCellValue());

		}
		return "";
	}

	public static class Wo extends GsonPropertyObject {
		@FieldDescribe("返回的结果标识")
		private String flag;

		public String getFlag() {
			return flag;
		}

		public void setFlag(String flag) {
			this.flag = flag;
		}
	}
}

package com.x.strategydeploy.assemble.control.strategy;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
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
import com.x.strategydeploy.core.entity.StrategyDeploy;

public class ActionImportExcelXLSX extends BaseAction {
	private static  Logger logger = LoggerFactory.getLogger(ActionImportExcelXLSX.class);
	private int startLine = 1;

	ActionResult<Wo> execute(EffectivePerson effectivePerson, byte[] bytes, FormDataContentDisposition disposition, String year) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create(); InputStream is = new ByteArrayInputStream(bytes); XSSFWorkbook workbook = new XSSFWorkbook(is); ByteArrayOutputStream os = new ByteArrayOutputStream()) {
			Business business = new Business(emc);
			this.scan(business, workbook, year);
			String flag = StringTools.uniqueToken();
			Wo wo = new Wo();
			wo.setFlag(flag);
			result.setData(wo);
		}
		return result;

	}

	private void scan(Business business, XSSFWorkbook workbook, String year) throws Exception {
		EntityManagerContainer emc = business.entityManagerContainer();

		Sheet sheet = workbook.getSheetAt(0);
		int lastRowNum = sheet.getLastRowNum();
		Row row = null;
		for (int i = startLine; i <= lastRowNum; i++) {
			row = sheet.getRow(i);
			logger.info("---------------------------row number:" + i);
			//int LastCellNum = row.getLastCellNum();
			StrategyDeploy strategydeploy = new StrategyDeploy();
			for (Cell cell : row) {
				int ColumnIndex = cell.getColumnIndex();
				if (ColumnIndex == 0) {
					double _double = Double.valueOf(this.getCellValue(cell));
					_double = Math.floor(_double);
					strategydeploy.setSequencenumber((int) _double);
				}
				if (ColumnIndex == 1) {
					strategydeploy.setStrategydeploytitle(this.getCellValue(cell));
				}
				if (ColumnIndex == 2) {
					strategydeploy.setStrategydeploydescribe(this.getCellValue(cell));
				}
				strategydeploy.setStrategydeployyear(year);
				emc.beginTransaction(StrategyDeploy.class);
				emc.persist(strategydeploy);
				emc.commit();
			}
			logger.info("-------------------------------");
			logger.info("");
		}
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
		} else if (cell.getCellType() ==CellType.BOOLEAN) {
			return String.valueOf(cell.getBooleanCellValue());
		} else if (cell.getCellType() == CellType.FORMULA) {
			return cell.getCellFormula();
		} else if (cell.getCellType() == CellType.NUMERIC) {
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

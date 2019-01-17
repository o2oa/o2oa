package com.x.organization.assemble.control.jaxrs.inputperson;

import java.io.ByteArrayOutputStream;
import java.util.stream.IntStream;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoFile;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

public class ActionTemplate extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionTemplate.class);

	private static String name = "input_person_template.xlsx";

	protected ActionResult<Wo> execute(EffectivePerson effectivePerson) throws Exception {
		try (XSSFWorkbook workbook = new XSSFWorkbook(); ByteArrayOutputStream os = new ByteArrayOutputStream()) {
			ActionResult<Wo> result = new ActionResult<>();
			this.template(workbook);
			workbook.write(os);
			Wo wo = new Wo(os.toByteArray(), this.contentType(true, name), this.contentDisposition(true, name));
			result.setData(wo);
			return result;
		}
	}

	private void template(XSSFWorkbook workbook) throws Exception {
		XSSFSheet sheet = workbook.createSheet("人员");
		Row row = sheet.createRow(0);
		Cell cell = row.createCell(0);
		cell.setCellValue("姓名");
		cell = row.createCell(1);
		cell.setCellValue("手机号");
		cell = row.createCell(2);
		cell.setCellValue("电子邮件");
		cell = row.createCell(3);
		cell.setCellValue("唯一编码");
		cell = row.createCell(4);
		cell.setCellValue("员工号");
		cell = row.createCell(5);
		cell.setCellValue("性别");
		cell = row.createCell(6);
		cell.setCellValue("(地址)");
		CellStyle cellStyle = workbook.createCellStyle();
		cellStyle.setWrapText(true);
		IntStream.rangeClosed(0, 6).forEach(i -> {
			sheet.setDefaultColumnStyle(i, cellStyle);
		});
	}

	public static class Wo extends WoFile {

		public Wo(byte[] bytes, String contentType, String contentDisposition) {
			super(bytes, contentType, contentDisposition);
		}

	}

}